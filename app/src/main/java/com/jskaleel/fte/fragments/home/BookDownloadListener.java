package com.jskaleel.fte.fragments.home;

interface BookDownloadListener {
    void booksDownloaded(BookListParser.Books.Book bookItem, String response);
}