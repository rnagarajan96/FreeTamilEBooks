package com.jskaleel.fte.fragments.download;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.jskaleel.fte.R;
import com.jskaleel.fte.booksdb.DbUtils;
import com.jskaleel.fte.booksdb.DownloadedBooks;
import com.jskaleel.fte.preferences.UserPreference;
import com.jskaleel.fte.utils.DeviceUtils;
import com.jskaleel.fte.utils.FTELog;

import java.util.ArrayList;
import java.util.List;

public class DownloadsFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback,
        DownloadedItemClicked {
    private List<DownloadedBooks> downloadedBookList;
    private RecyclerView downloadsList;
    private DownloadFragemntAdapter downloadFragemntAdapter;
    private TextView txtEmptyView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Assent.setFragment(this, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity().isFinishing()) {
            Assent.setFragment(this, null);
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

        txtEmptyView = (TextView) view.findViewById(R.id.empty);
    }

    private void setupDefaults() {
        downloadsList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        downloadsList.setLayoutManager(layoutManager);

        downloadedBookList = DbUtils.getAllDownloadItems();
        FTELog.print("Size : " + downloadedBookList.size());
        if (downloadedBookList.size() > 0) {
            downloadsList.setVisibility(View.VISIBLE);
            txtEmptyView.setVisibility(View.GONE);
            downloadFragemntAdapter = new DownloadFragemntAdapter(getActivity(), downloadedBookList);
            downloadFragemntAdapter.setListener(this);
            downloadsList.setAdapter(downloadFragemntAdapter);
        } else {
            downloadsList.setVisibility(View.GONE);
            txtEmptyView.setVisibility(View.VISIBLE);
        }
    }

    private void setupEvents() {
        Log.e("supriya", "downloaded data" + DbUtils.getAllDownloadItems().size());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Assent.handleResult(permissions, grantResults);
    }

    @Override
    public void openDownloaded(DownloadedBooks singleItem) {
        if (DbUtils.isSuccess(singleItem.getBookId())) {
            DeviceUtils.openAppReader(getActivity(), singleItem.getFilePath());
        } else {
            Toast.makeText(getActivity(), getString(R.string.book_not_downloaded), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void deleteItem(DownloadedBooks singleItem, int position) {
        List<DownloadedBooks> downloadedBooks = DbUtils.removeDownloadedItem(singleItem.getBookId());
        downloadFragemntAdapter.removeDeleteItem(downloadedBooks, position);
    }
}
