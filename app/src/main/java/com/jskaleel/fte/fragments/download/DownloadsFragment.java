package com.jskaleel.fte.fragments.download;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.folioreader.activity.FolioActivity;
import com.jskaleel.fte.R;
import com.jskaleel.fte.utils.AlertUtils;
import com.jskaleel.fte.utils.DeviceUtils;
import com.jskaleel.fte.utils.FTELog;

import java.io.File;
import java.util.ArrayList;

public class DownloadsFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback {
    private ArrayList<String> item = null;
    private ArrayList<String> path = null;
    private RecyclerView downloadsList;
    private DownloadFragemntAdapter downloadFragemntAdapter ;

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
        setupEvents();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            Assent.requestPermissions(new AssentCallback() {
                @Override
                public void onPermissionResult(PermissionResultSet result) {
                    setupDefaults();
                }
            }, 69, Assent.WRITE_EXTERNAL_STORAGE);
        } else {
            setupDefaults();
        }
    }

    private void init(View view) {
        downloadsList = (RecyclerView) view.findViewById(R.id.download_list);
     //   downloadsList.setEmptyView(view.findViewById(R.id.empty));
    }

    private void setupDefaults() {

        downloadsList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        downloadsList.setLayoutManager(layoutManager);

        downloadFragemntAdapter = new DownloadFragemntAdapter(getActivity(),item);
downloadsList.setAdapter(downloadFragemntAdapter);
        getDir(DeviceUtils.getStorageLocation());
    }

    private void setupEvents() {
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
        Log.d("Khaleel", "File Path : "+path);
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
                Log.d("Khaleel", "FilePath : "+file.getPath() + "-->AbsolutePath : "+file.getAbsolutePath());
                Intent intent = new Intent(getActivity(), FolioActivity.class);
                intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_TYPE, FolioActivity.EpubSourceType.SD_CARD);
                intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_PATH, file.getPath());
                startActivity(intent);
            } else {
                AlertUtils.showAlert(getActivity(), getString(R.string.wrong_format));
            }
        }
    }

    private void downloadIt(String packageName) {
        Uri uri = Uri.parse("market://search?q=" + packageName + ".FBReader");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            String url = "https://play.google.com/store/apps/details?id=" + packageName + ".FBReader";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Assent.handleResult(permissions, grantResults);
    }
}
