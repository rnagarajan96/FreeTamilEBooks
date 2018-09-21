package com.jskaleel.fte.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.jskaleel.fte.BuildConfig;
import com.jskaleel.fte.R;

import java.io.File;
import java.util.UUID;

public class DeviceUtils {

    public static String getStorageLocation() {
        File path = new File(Environment.getExternalStorageDirectory(), "/Free_Tamil_Ebooks/");
        return path.getAbsolutePath();
    }

    public static File getAppDirectory(Context context) {
        return new File(context.getExternalFilesDir(null) + "/ebooks");
    }

    public static void hideSoftKeyboard(Context context, View paramView) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                paramView.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showSoftKeyboard(Context context, View paramView) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(paramView,
                InputMethodManager.SHOW_IMPLICIT);
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        boolean isConnected = false;

        if (info != null && info.isConnectedOrConnecting()) {
            isConnected = true;
        }

        return isConnected;
    }

    public static void setStatusBarColor(Context context, int colorId) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(context, colorId));
        }
    }

    public static void openAppReader(String filePath) {
        FolioReader folioReader = FolioReader.get();
        Config config = new Config()
                .setAllowedDirection(Config.AllowedDirection.ONLY_VERTICAL)
                .setNightMode(false)
                .setShowTts(false)
                .setThemeColorRes(R.color.primary)
                .setDirection(Config.Direction.VERTICAL);
        folioReader.setConfig(config, true);
        folioReader.openBook(filePath);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}

