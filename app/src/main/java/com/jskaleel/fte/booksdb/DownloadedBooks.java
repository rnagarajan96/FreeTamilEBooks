package com.jskaleel.fte.booksdb;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by khaleeljageer on 03-12-2016.
 */

@Table(name = "DownloadedBooks")
public class DownloadedBooks extends Model {

    @Column(name="book_id", unique = true, notNull = true)
    private String bookId;

    @Column(name="title")
    private String bookTitle;

    @Column(name="author")
    private String author;

    @Column(name="image")
    private String imageUrl;

    @Column(name="epub_link")
    private String epubLink;

    @Column(name="category")
    private String category;

    @Column(name="download_id")
    private long downloadId;

    @Column(name="file_path")
    private String filePath;

    @Column(name="download_status")
    private String downloadStatus;

    public DownloadedBooks() {
        super();
    }

    public DownloadedBooks(String bookId, String bookTitle, String author, String imageUrl,
                           String epubLink, String category, long downloadId, String filePath, String downloadStatus) {
        super();
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.author = author;
        this.imageUrl = imageUrl;
        this.epubLink = epubLink;
        this.category = category;
        this.downloadId = downloadId;
        this.filePath = filePath;
        this.downloadStatus = downloadStatus;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getEpubLink() {
        return epubLink;
    }

    public String getCategory() {
        return category;
    }

    public Long getDownloadId() {
        return downloadId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setEpubLink(String epubLink) {
        this.epubLink = epubLink;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
