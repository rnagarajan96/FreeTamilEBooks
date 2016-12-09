package com.jskaleel.fte.fragments.download;

import android.app.AlertDialog;
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
import com.folioreader.activity.FolioActivity;
import com.jskaleel.fte.R;
import com.jskaleel.fte.booksdb.DbUtils;
import com.jskaleel.fte.booksdb.DownloadedBooks;
import com.jskaleel.fte.utils.AlertUtils;
import com.jskaleel.fte.utils.DownloadService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadsFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback, DownloadedItemClicked {
    private ArrayList<String> item = null;
    private ArrayList<String> path = null;
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
        /*downloadsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openBook(path.get(position));
            }
        });*/
    }

    private void getDir(String dirPath) {

        item = new ArrayList<>();
        path = new ArrayList<>();
        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(dirPath)) {
            item.add(dirPath);
            path.add(dirPath);
            item.add("../");
            path.add(f.getParent());
        }

        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!file.isHidden() && file.canRead()) {
                    path.add(file.getPath());
                    if (!file.isDirectory()) {
                        item.add(file.getName());
                    }
                }
            }
        }

       /* ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, item);
        downloadsList.setAdapter(adapter);*/
    }

    public void openBook(String path) {
        Log.d("Khaleel", "File Path : " + path);
        final File file = new File(path);

        if (file.isDirectory()) {
            if (file.canRead()) {
                if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    Assent.requestPermissions(new AssentCallback() {
                        @Override
                        public void onPermissionResult(PermissionResultSet result) {
                            getDir(file.toString());
                        }
                    }, 69, Assent.WRITE_EXTERNAL_STORAGE);
                } else {
                    getDir(file.toString());
                }
            } else {
                new AlertDialog.Builder(getActivity()).setTitle("[" + file.getName() + "] folder can't be read!").setPositiveButton("Ok", null).show();
            }
        } else {
            String extension = (file.toString()).substring(((file.toString()).lastIndexOf(".") + 1), (file.toString()).length());

            if (extension.equals("epub")) {
                /*Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/epub");
                ComponentName cn = new ComponentName("org.geometerplus.zlibrary.ui.android", "org.geometerplus.android.fbreader.FBReader");
                intent.setComponent(cn);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    AlertUtils.showAlertWithYesNo(getActivity(), "", getString(R.string.down_epub), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadIt("org.geometerplus.zlibrary.ui.android");
                        }
                    }, false);
                }*/
                Log.d("Khaleel", "FilePath : " + file.getPath() + "-->AbsolutePath : " + file.getAbsolutePath());
                Intent intent = new Intent(getActivity(), FolioActivity.class);
                intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_TYPE, FolioActivity.EpubSourceType.SD_CARD);
                intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_PATH, file.getPath());
                startActivity(intent);
            } else {
                AlertUtils.showAlert(getActivity(), getString(R.string.wrong_format));
            }
        }
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
        Intent intent = new Intent(getActivity(), FolioActivity.class);
        intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_TYPE, FolioActivity.EpubSourceType.SD_CARD);
        intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_PATH, singleItem.getFilePath());
        startActivity(intent);
    }
}
