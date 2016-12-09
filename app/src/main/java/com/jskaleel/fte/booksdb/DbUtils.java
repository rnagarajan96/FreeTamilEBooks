package com.jskaleel.fte.booksdb;

import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by khaleeljageer on 03-12-2016.
 */

public class DbUtils {
    public static final String BOOK_ID = "book_id";
    public static final String DOWNLOAD_ID = "download_id";
    public static final String STATUS = "status";

    public static List<DownloadedBooks> getAll(DownloadedBooks category) {
        return new Select()
                .from(DownloadedBooks.class)
                .where("book_id = ?", category.getId())
                .orderBy("Name ASC")
                .execute();
    }

    public static List<DownloadedBooks> getAllDownloadItems() {
        return new Select().from(DownloadedBooks.class).execute();
    }

    public static DownloadedBooks getSingleItem(String key, String value) {
        return new Select()
                .from(DownloadedBooks.class)
                .where(key + " = ?", value)
                .executeSingle();
    }

    public static DownloadedBooks getSingleItem(String key, long value) {
        return new Select()
                .from(DownloadedBooks.class)
                .where(key + " = ?", value)
                .executeSingle();
    }

    public static Boolean isExist(String bookId) {
        return new Select().from(DownloadedBooks.class).where("book_id = ?", bookId).exists();
    }
}
