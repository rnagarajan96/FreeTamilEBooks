package com.jskaleel.fte.booksdb;

import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by khaleeljageer on 03-12-2016.
 */

public class DbUtils {
    public static List<DownloadedBooks> getAll(DownloadedBooks category) {
        return new Select()
                .from(DownloadedBooks.class)
                .where("book_id = ?", category.getId())
                .orderBy("Name ASC")
                .execute();
    }

    public static DownloadedBooks getSingleItem(String bookId) {
        return new Select()
                .from(DownloadedBooks.class)
                .where("book_id = ?", bookId)
                .executeSingle();
    }

    public static Boolean isExist(String bookId) {
        return new Select().from(DownloadedBooks.class).where("book_id = ?", bookId).exists();
    }
}
