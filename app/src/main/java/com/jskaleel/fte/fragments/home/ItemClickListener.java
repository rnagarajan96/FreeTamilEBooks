package com.jskaleel.fte.fragments.home;

interface ItemClickListener {
	void downloadPressed(BookListParser.Books.Book singleItem);
	void openPressed(BookListParser.Books.Book singleItem);
}
