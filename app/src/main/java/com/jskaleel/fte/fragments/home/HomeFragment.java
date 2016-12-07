package com.jskaleel.fte.fragments.home;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jskaleel.fte.HomeActivity;
import com.jskaleel.fte.R;
import com.jskaleel.fte.booksdb.DownloadedBooks;
import com.jskaleel.fte.preferences.UserPreference;
import com.jskaleel.fte.utils.AlertUtils;
import com.jskaleel.fte.utils.DeviceUtils;
import com.jskaleel.fte.utils.DownloadService;
import com.jskaleel.fte.utils.FTELog;
import com.jskaleel.fte.utils.TextUtils;
import com.jskaleel.fte.webservice.WebServices;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;


public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ItemClickListener, EmptyViewListener {

    private UserPreference mPreference;
    private RecyclerView rvBookList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BookListAdapter bookListAdapter;
    private TextView txtTryAgain;
    private ProgressDialog pg;
    private DownloadManager mDownloadManager;
    private HomeActivity homeActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_home, null);

        init(rootView);
        setupDefaults();
        setupEvents();

        return rootView;
    }

    private void init(View view) {
        homeActivity = (HomeActivity) getActivity();
        mPreference = UserPreference.getInstance(getActivity().getApplicationContext());

        rvBookList = (RecyclerView) view.findViewById(R.id.rv_book_list);
        txtTryAgain = (TextView) view.findViewById(R.id.empty);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_dark, R.color.primary, R.color.primary_light);

        pg = new ProgressDialog(getActivity());
        pg.setMessage(getString(R.string.loading_));
        pg.setCancelable(false);
    }

    private void setupDefaults() {
        rvBookList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvBookList.setLayoutManager(layoutManager);

        bookListAdapter = new BookListAdapter(getActivity(), new ArrayList<BookListParser.Books.Book>());
        bookListAdapter.setEmptyViewListener(HomeFragment.this);
        bookListAdapter.setListItemListener(HomeFragment.this);
        rvBookList.setAdapter(bookListAdapter);

        String response = mPreference.getBookResponse();
        if (TextUtils.isNullOrEmpty(response)) {
            if (DeviceUtils.isInternetConnected(getActivity())) {
                getBookList(true);
            } else {
                AlertUtils.showAlert(getActivity(), getString(R.string.check_connection));
            }
        } else {
            showBookList(response);
        }

        mDownloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
    }

    private void setupEvents() {

    }

    private void showBookList(String bookResponse) {
        Gson gson = new Gson();
        BookListParser bookListParser = gson.fromJson(bookResponse, BookListParser.class);
        bookListAdapter.updateList(bookListParser.books.book);
    }

    private void getBookList(boolean isShowProgress) {
        if (isShowProgress) {
            if (pg != null) {
                pg.show();
            }
        }

        new TaskFetchBooks(new TaskCompleteListener() {

            @Override
            public void booksFetched(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = XML.toJSONObject(response);
                    if (jsonObj.toString() != null) {
                        mPreference.setBookResponse(jsonObj.toString());
                        showBookList(jsonObj.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (pg != null && pg.isShowing()) {
                    pg.dismiss();
                }

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }).execute();

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getBookList(false);
    }

    @Override
    public void downloadPressed(BookListParser.Books.Book bookItem) {
        File file = DeviceUtils.getAppDirectory(getActivity());
        if (!file.exists()) {
            file.mkdir();
        }

        DownloadManager.Request requestVideo = new DownloadManager.Request(Uri.parse(bookItem.getEpub()));
        String filePath = file + "/" + bookItem.getBookid();
        requestVideo.setDestinationUri(Uri.parse("file://" + filePath));
        requestVideo.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        requestVideo.setAllowedOverRoaming(true);
        requestVideo.setTitle(bookItem.getTitle());
        requestVideo.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        long downloadId = mDownloadManager.enqueue(requestVideo);
        homeActivity.downloadIdList.add(downloadId);

        FTELog.print("Storage Location : "+ getActivity().getExternalFilesDir(null));
        DownloadedBooks downloadedBooks = new DownloadedBooks(bookItem.getBookid(), bookItem.getTitle(),
                bookItem.getAuthor(), bookItem.getImage(),
                bookItem.getEpub(), bookItem.getCategory(), downloadId, filePath, "");
        downloadedBooks.save();
    }

    @Override
    public void openPressed(BookListParser.Books.Book singleItem) {

    }

    @Override
    public void setEmptyViewOnUi(int type) {
        rvBookList.setVisibility(type == 0 ? View.GONE : View.VISIBLE);
    }

    private class TaskFetchBooks extends AsyncTask<Void, Void, String> {
        private TaskCompleteListener taskCompleteListener;

        TaskFetchBooks(TaskCompleteListener taskCompleteListener) {
            this.taskCompleteListener = taskCompleteListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            InputStream is = null;

            try {
                URL feedUrl = new URL(WebServices.XML_URL);
                HttpURLConnection conn = (HttpURLConnection) feedUrl.openConnection();

                conn.setReadTimeout(60000);
                conn.setConnectTimeout(60000);
                conn.setRequestMethod("GET");

                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();
                return readIt(is);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (!TextUtils.isNullOrEmpty(response)) {
                taskCompleteListener.booksFetched(response);
            }
        }
    }

    public String readIt(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return total.toString();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_sync) {
            onRefresh();
            return true;
        } else if (id == R.id.menu_source) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebServices.SOURCE_CODE_URL));
            startActivity(browserIntent);
            return true;
        } else if (id == R.id.ic_search) {
            SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    bookListAdapter.filter(newText);
                    return true;
                }
            });
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
