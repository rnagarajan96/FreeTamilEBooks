package com.jskaleel.fte.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jskaleel.fte.HomeActivity;
import com.jskaleel.fte.R;
import com.jskaleel.fte.utils.AlertUtils;

public class AboutUsFragment extends Fragment {
    private WebView webView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, null);

        init(view);
        setupDefaults();

        return view;
    }

    private void init(View view) {
        webView = (WebView) view.findViewById(R.id.fragment_webview);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupDefaults() {

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.loading_));
        pd.setCancelable(true);
        pd.show();

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pd.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                AlertUtils.showShortSnackbar(view, description);
                pd.dismiss();
            }
        });

        webView.loadUrl("file:///android_asset/htmlfiles/about_us.html");
    }
}