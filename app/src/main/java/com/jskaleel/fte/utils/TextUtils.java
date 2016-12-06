package com.jskaleel.fte.utils;

import android.util.Base64;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class TextUtils {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().equals("");
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static String arrayToString(ArrayList<String> array, String delimiter) {
        StringBuilder builder = new StringBuilder();
        if (array.size() > 0) {
            builder.append(array.get(0));
            for (int i = 1; i < array.size(); i++) {
                builder.append(delimiter);
                builder.append(array.get(i));
            }
        }
        return builder.toString();
    }
}
