package com.jskaleel.fte;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.crashlytics.android.Crashlytics;
import com.jskaleel.fte.fragments.AboutUsFragment;
import com.jskaleel.fte.fragments.CommentsFragment;
import com.jskaleel.fte.fragments.ContributorsFragment;
import com.jskaleel.fte.fragments.HelpUsFragment;
import com.jskaleel.fte.fragments.SettingsFragment;
import com.jskaleel.fte.fragments.download.DownloadsFragment;
import com.jskaleel.fte.fragments.home.HomeFragment;
import com.jskaleel.fte.utils.DeviceUtils;
import com.jskaleel.fte.utils.DownloadService;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int mLastTabSelected = -1;
    public ArrayList<Long> downloadIdList;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Assent.setActivity(this, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.home);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        init();
    }

    private void init() {
        Crashlytics.setUserIdentifier(DeviceUtils.getUUID());

        downloadIdList = new ArrayList<>();
        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            Assent.requestPermissions(new AssentCallback() {
                @Override
                public void onPermissionResult(PermissionResultSet result) {

                }
            }, 69, Assent.WRITE_EXTERNAL_STORAGE);
        }
        onTabSelected(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_home) {
            onTabSelected(0);
        } else if (id == R.id.menu_about_us) {
            onTabSelected(1);
        } else if (id == R.id.menu_contributor) {
            onTabSelected(2);
        } else if (id == R.id.menu_help_us) {
            onTabSelected(3);
        } else if (id == R.id.menu_downloads) {
            onTabSelected(4);
        } else if (id == R.id.menu_comments) {
            onTabSelected(5);
        } else if (id == R.id.menu_settings) {
            onTabSelected(6);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onTabSelected(final int index) {
        if (mLastTabSelected == index) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switchScreen(index);
            }
        }, 300);
    }

    private void switchScreen(int index) {
        Fragment fragment = null;
        int title = R.string.home;

        switch (index) {
            case 0:
                fragment = new HomeFragment();
                title = R.string.home;
                break;
            case 1:
                fragment = new AboutUsFragment();
                title = R.string.aboutus;
                break;
            case 2:
                fragment = new ContributorsFragment();
                title = R.string.our_team;
                break;
            case 3:
                fragment = new HelpUsFragment();
                title = R.string.help_us;
                break;
            case 4:
                fragment = new DownloadsFragment();
                title = R.string.downloads;
                break;
            case 5:
                fragment = new CommentsFragment();
                title = R.string.comments;
                break;
            case 6:
                fragment = new SettingsFragment();
                title = R.string.action_settings;
                break;
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }

        if (fragment != null) {
            if (!this.isFinishing()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss();
            }
        }
        mLastTabSelected = index;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Assent.handleResult(permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Assent.setActivity(this, this);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(downloadReceiver, new IntentFilter(
                DownloadService.DOWNLOAD_COMPLETED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(downloadReceiver);
        if (isFinishing())
            Assent.setActivity(this, null);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), getString(R.string.download_completed), Toast.LENGTH_SHORT).show();
        }
    };
}
