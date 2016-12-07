package com.jskaleel.fte.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jskaleel.fte.R;
import com.jskaleel.fte.utils.AlertUtils;
import com.jskaleel.fte.utils.DownloadService;
import com.jskaleel.fte.utils.TextUtils;
import com.jskaleel.fte.webservice.WebServices;

public class CommentsFragment extends Fragment {

    private EditText edtFName, edtLName, edtMail, edtMessage;
    private static final int FROM_EMAIL_CLIENT = 121;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_contact, null);

        init(view);
        setupDefaults();
        setupEvents();

        return view;
    }

    private void init(View view) {
        edtFName = (EditText) view.findViewById(R.id.edt_fname);
        edtLName = (EditText) view.findViewById(R.id.edt_lname);
        edtMail = (EditText) view.findViewById(R.id.edt_mail);
        edtMessage = (EditText) view.findViewById(R.id.edt_message);
    }

    private void setupDefaults() {
        edtFName.setText("");
        edtLName.setText("");
        edtMail.setText("");
        edtMessage.setText("");
    }

    private void setupEvents() {

    }

    protected void validateFields() {
        String FName = edtFName.getText().toString();
        String LName = edtLName.getText().toString();
        String EMail = edtMail.getText().toString();
        String Message = edtMessage.getText().toString();

        if (TextUtils.isNullOrEmpty(FName)) {
            edtFName.setError(getString(R.string.firstname_alert));
            return;
        }

        if (TextUtils.isNullOrEmpty(LName)) {
            edtLName.setError(getString(R.string.lastname_alert));
            return;
        }

        if (TextUtils.isNullOrEmpty(EMail)) {
            edtMail.setError(getString(R.string.nomail_alert));
            return;
        }

        if (TextUtils.isValidEmail(EMail)) {
            edtMail.setError(getString(R.string.mail_alert));
        }

        if (TextUtils.isNullOrEmpty(Message)) {
            edtMessage.setError(getString(R.string.message_alert));
        }


        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"freetamilebooksteam@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Review from User[Android-App]");
        i.putExtra(Intent.EXTRA_TEXT, "\nName : " + FName + " " + LName + "\nEmail : " + EMail + "\nMessage : " + Message);
        getActivity().startActivityForResult(Intent.createChooser(i, "Choose an Email Client..."), FROM_EMAIL_CLIENT);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_done, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ic_submit) {
            validateFields();
        }

        return super.onOptionsItemSelected(item);
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
