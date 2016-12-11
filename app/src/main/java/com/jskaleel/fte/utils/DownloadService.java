package com.jskaleel.fte.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.jskaleel.fte.booksdb.DbUtils;
import com.jskaleel.fte.booksdb.DownloadedBooks;

/**
 * Created by khaleeljageer on 06-12-2016.
 */

public class DownloadService extends BroadcastReceiver {
    public static final String DOWNLOAD_COMPLETED = "download_completed";
    private DownloadManager mDownloadManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {

            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor c = mDownloadManager.query(query);
            if (c.moveToFirst()) {
                int columnStatus = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                switch (c.getInt(columnStatus)) {
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        sendBroadcastMessage(context, downloadId, "FAILED");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        sendBroadcastMessage(context, downloadId, "SUCCESS");
                        break;
                    case DownloadManager.STATUS_FAILED:
                        sendBroadcastMessage(context, downloadId, "FAILED");
                        break;
                }
            }
        }
    }

    private void sendBroadcastMessage(Context context, long downloadId, String status) {
        FTELog.print("Khaleel : Status......" + status);
        DownloadedBooks downloadBooks = DbUtils.getSingleItem(DbUtils.DOWNLOAD_ID, downloadId);
        if (downloadBooks != null) {
            downloadBooks.setDownloadStatus(status);
            downloadBooks.save();

            Intent broadcastIntent = new Intent(DOWNLOAD_COMPLETED);
            broadcastIntent.putExtra(DbUtils.BOOK_ID, downloadBooks.getBookId());
            broadcastIntent.putExtra(DbUtils.DOWNLOAD_ID, downloadId);
            broadcastIntent.putExtra(DbUtils.STATUS, status);
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
        }
    }
}
