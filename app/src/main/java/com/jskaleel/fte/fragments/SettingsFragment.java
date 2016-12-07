package com.jskaleel.fte.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.jskaleel.fte.R;
import com.jskaleel.fte.preferences.UserPreference;
import com.jskaleel.fte.utils.DownloadService;

import static com.jskaleel.fte.FTEApp.FCM_TOPIC;

/**
 * Created by khaleeljageer on 26-11-2016.
 */

public class SettingsFragment extends Fragment {

    private TextView txtPushStatus;
    private SwitchCompat swPushNotification;
    private UserPreference userPreference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        init(view);
        setupDefaults();
        setupEvents();
        return view;
    }

    private void init(View view) {
        userPreference = UserPreference.getInstance(getActivity().getApplicationContext());
        swPushNotification = (SwitchCompat) view.findViewById(R.id.sw_push);

        txtPushStatus = (TextView) view.findViewById(R.id.txt_push_status);
    }

    private void setupDefaults() {
        swPushNotification.setChecked(userPreference.getPushStatus());
        txtPushStatus.setText(userPreference.getPushStatus() ? getString(R.string.on) : getString(R.string.off));
    }

    private void setupEvents() {
        swPushNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userPreference.setPushNotificationStatus(isChecked);
                txtPushStatus.setText(isChecked ? getString(R.string.on) : getString(R.string.off));
                if(userPreference.getPushStatus()) {
                    FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC);
                }else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(FCM_TOPIC);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(downloadReceiver, new IntentFilter(
                DownloadService.DOWNLOAD_COMPLETED));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(downloadReceiver);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(), getString(R.string.download_completed), Toast.LENGTH_SHORT).show();
        }
    };
}
