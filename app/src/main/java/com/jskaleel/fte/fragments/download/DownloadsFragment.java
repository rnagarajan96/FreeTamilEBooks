package com.jskaleel.fte.fragments.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.jskaleel.fte.R;
import com.jskaleel.fte.booksdb.DbUtils;
import com.jskaleel.fte.booksdb.DownloadedBooks;
import com.jskaleel.fte.utils.DeviceUtils;
import com.jskaleel.fte.utils.DownloadService;

import java.util.ArrayList;
import java.util.List;

public class DownloadsFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback,
        DownloadedItemClicked {
    private List<DownloadedBooks> downloadedBookList;
    private RecyclerView downloadsList;
    private DownloadFragemntAdapter downloadFragemntAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Assent.setFragment(this, this);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(downloadReceiver, new IntentFilter(
                DownloadService.DOWNLOAD_COMPLETED));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity().isFinishing()) {
            Assent.setFragment(this, null);

            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(downloadReceiver);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloads, null);
        Assent.setFragment(this, this);

        init(view);
        setupDefaults();
        setupEvents();

        return view;
    }

    private void init(View view) {
        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            Assent.requestPermissions(new AssentCallback() {
                @Override
                public void onPermissionResult(PermissionResultSet result) {
                }
            }, 69, Assent.WRITE_EXTERNAL_STORAGE);
        }

        downloadsList = (RecyclerView) view.findViewById(R.id.download_list);
        downloadedBookList = new ArrayList<>();
    }

    private void setupDefaults() {
        downloadsList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        downloadsList.setLayoutManager(layoutManager);

        downloadedBookList = DbUtils.getAllDownloadItems();

        downloadFragemntAdapter = new DownloadFragemntAdapter(getActivity(), downloadedBookList);
        downloadFragemntAdapter.setListener(this);
        downloadsList.setAdapter(downloadFragemntAdapter);
    }

    private void setupEvents() {
        Log.e("supriya", "downloaded data" + DbUtils.getAllDownloadItems().size());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Assent.handleResult(permissions, grantResults);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(), getString(R.string.download_completed), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void openDownloaded(DownloadedBooks singleItem) {
        DeviceUtils.openBook(getActivity(), singleItem.getFilePath());
    }
}
