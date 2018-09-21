package com.jskaleel.fte.fragments.download;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.runtimepermission.RuntimePermission;
import com.jskaleel.fte.R;
import com.jskaleel.fte.booksdb.DbUtils;
import com.jskaleel.fte.booksdb.DownloadedBooks;
import com.jskaleel.fte.preferences.UserPreference;
import com.jskaleel.fte.utils.DeviceUtils;
import com.jskaleel.fte.utils.FTELog;

import java.util.ArrayList;
import java.util.List;

public class DownloadsFragment extends Fragment implements DownloadedItemClicked {
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloads, null);

        init(view);
        setupDefaults();
        setupEvents();

        return view;
    }

    private void init(View view) {
        RuntimePermission.askPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE).ask();

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
    public void openDownloaded(DownloadedBooks singleItem) {
        if (DbUtils.isSuccess(singleItem.getBookId())) {
            DeviceUtils.openAppReader(singleItem.getFilePath());
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
