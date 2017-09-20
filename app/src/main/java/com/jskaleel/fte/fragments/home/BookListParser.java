package com.jskaleel.fte.fragments.home;

import java.io.Serializable;
import java.util.ArrayList;


class BookListParser implements Serializable {

	private static final long serialVersionUID = 1L;
	Books books;

	class Books {
		ArrayList<Book> book;
		class Book {
			String getAuthor() {
				return author;
			}

			public String getCategory() {
				return category;
			}

			public String getTitle() {
				return title;
			}

			public String getBookid() {
				return bookid;
			}

			public String getPdf() {
				return pdf;
			}

			String getEpub() {
				return epub;
			}

			public String getLink() {
				return link;
			}

			public String getImage() {
				return image;
			}

			public String getDate() {
				return date;
			}

			String author, category, title, bookid, pdf, epub, link, image, date;
		}
	}
}
