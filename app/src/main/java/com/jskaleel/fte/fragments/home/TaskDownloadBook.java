package com.jskaleel.fte.fragments.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.jskaleel.fte.R;
import com.jskaleel.fte.utils.DeviceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class TaskDownloadBook extends AsyncTask<Void, Integer, String> {
    private BookListParser.Books.Book bookItem;
    private BookDownloadListener bookDownloadListener;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog progressDialog;
    private Context context;
    final static String ERROR = "error", CANCELLED = "cancelled";

    TaskDownloadBook(Context context, BookListParser.Books.Book singleItem, BookDownloadListener bookDownloadListener) {
        this.context = context;
        this.bookItem = singleItem;
        this.bookDownloadListener = bookDownloadListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.app_name));
        progressDialog.setMessage(String.format(context.getString(R.string.downloading), bookItem.title));
        progressDialog.setCancelable(false);
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.hide), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancel(true);
            }
        });
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(bookItem.epub);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return ERROR;
            }
            int fileLength = connection.getContentLength();
            // download the file
            input = connection.getInputStream();

            File path = new File(DeviceUtils.getStorageLocation());
            if (!(path.exists()))
                path.mkdirs();

            String fileName = path + "/" + bookItem.title + ".epub";

            output = new FileOutputStream(fileName);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    File file = new File(fileName);
                    file.delete();
                    return CANCELLED;
                }
                total += count;
                if (fileLength > 0) {
                    publishProgress((int) (total * 100 / fileLength));
                }
                output.write(data, 0, count);
            }
            return fileName;
        } catch (Exception e) {
            return ERROR;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
            Log.d("DownloadTask", "doInBackground : Connection Closed");
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.d("DownloadTask", "onPostExecute : "+response);

        if (response != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            bookDownloadListener.booksDownloaded(bookItem, response);
        }
    }
}