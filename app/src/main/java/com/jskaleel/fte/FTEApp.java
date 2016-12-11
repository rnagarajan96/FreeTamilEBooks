package com.jskaleel.fte;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jskaleel.fte.preferences.UserPreference;
import io.fabric.sdk.android.Fabric;

public class FTEApp extends Application {

    public static final String FCM_TOPIC = "fte_books";

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        ActiveAndroid.initialize(this);

        UserPreference userPreference = UserPreference.getInstance(this);
        if(userPreference.getPushStatus()) {
            FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC);
        }else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(FCM_TOPIC);
        }
    }
}
