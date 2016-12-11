package com.jskaleel.fte.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreference {

    private static UserPreference ourInstance = null;
    private static final String PREFS_NAME = "user_preference";
    private static final String BOOK_RESPONSE = "book_response";
    private static final String PUSH_NOTIFICATION = "push_notification";
    private static final String EPUB_READER_TYPE = "epub_reader_type";
    private static final String STORAGE_STATUS = "storage_status";

    private SharedPreferences mPreference;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    public static UserPreference getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new UserPreference(context);
        }
        return ourInstance;
    }

    private UserPreference(Context context) {
        mContext = context;
        mPreference = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mEditor = mPreference.edit();
    }

    public void setBookResponse(String bookResponse){
        mEditor.putString(BOOK_RESPONSE, bookResponse);
        mEditor.apply();
    }

    public String getBookResponse() {
        return mPreference.getString(BOOK_RESPONSE, "");
    }

    public void setPushNotificationStatus(boolean pushStatus) {
        mEditor.putBoolean(PUSH_NOTIFICATION, pushStatus);
        mEditor.apply();
    }

    public Boolean getPushStatus() {
        return mPreference.getBoolean(PUSH_NOTIFICATION, true);
    }

    public void setReaderType(int readerType) {
        mEditor.putInt(EPUB_READER_TYPE, readerType);
        mEditor.apply();
    }

    public Integer getReaderType() {
        return mPreference.getInt(EPUB_READER_TYPE, 0);
    }
}
