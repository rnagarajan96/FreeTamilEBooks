package com.jskaleel.fte.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.jskaleel.fte.HomeActivity;
import com.jskaleel.fte.R;
import com.jskaleel.fte.utils.DeviceUtils;

import java.lang.ref.WeakReference;

public class SplashActivity extends Activity {
    private static final long SLEEP_DURATION = 3000;
    private boolean isDestroyed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtils.setStatusBarColor(this, R.color.primary_dark);
        setContentView(R.layout.activity_splash);


        FTEHandler mHandler = new FTEHandler(SplashActivity.this);
        mHandler.sendEmptyMessageDelayed(1, SLEEP_DURATION);
    }

    private static class FTEHandler extends Handler {
        WeakReference<SplashActivity> splash;

        FTEHandler(SplashActivity splashScreen) {
            splash = new WeakReference<>(splashScreen);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = splash.get();
            if (activity != null && msg.what == 1 && !activity.isDestroyed) {
                activity.launchNextScreen();
            }
        }
    }

    private void launchNextScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        super.onDestroy();
    }
}
