package com.jskaleel.fte.utils;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;

import com.jskaleel.fte.R;

public class AlertUtils {
    private static void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showShortSnackbar(View view, String message) {
        showSnackbar(view, message, Snackbar.LENGTH_SHORT);
    }

    public static void showLongSnackbar(View view, String message) {
        showSnackbar(view, message, Snackbar.LENGTH_LONG);
    }

    public static void showAlertWithYesNo(Context context, String title, String message,
                                          DialogInterface.OnClickListener onClick, boolean cancelable) {
        new AlertDialog.Builder(context).setMessage(message)
                .setTitle(TextUtils.isNullOrEmpty(title) ? context.getString(R.string.app_name) : title)
                .setCancelable(cancelable).setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, onClick).create().show();
    }

    public static void showAlert(Context context, String title, String message,
                                 DialogInterface.OnClickListener onClick, boolean cancelable) {
        new AlertDialog.Builder(context).setMessage(message)
                .setTitle(TextUtils.isNullOrEmpty(title) ? context.getString(R.string.app_name) : title)
                .setCancelable(cancelable).setNeutralButton(R.string.yes, onClick).create().show();
    }

    public static void showAlert(Context context, String message) {
        showAlert(context, null, message, null, true);
    }

    public static void showNotification(Context context, String messageBody) {

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
