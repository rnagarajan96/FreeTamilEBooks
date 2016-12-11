package com.jskaleel.fte.fragments.download;

import com.jskaleel.fte.booksdb.DownloadedBooks;

interface DownloadedItemClicked {
    void openDownloaded(DownloadedBooks singleItem);
}