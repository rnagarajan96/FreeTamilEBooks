package com.jskaleel.fte.booksdb;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by khaleeljageer on 03-12-2016.
 */

@Table(name = "DownloadedBooks")
public class DownloadedBooks extends Model {

    @Column(name="book_id")
    public String bookId;

    @Column(name="book_name")
    public String bookName;



    public DownloadedBooks() {
        super();
    }

    public DownloadedBooks(String bookId, String bookName) {
        super();
        this.bookId = bookId;
        this.bookName = bookName;
    }
}
