package org.bspeice.minimalbible.activities.downloader.manager;

import java.util.List;

import org.crosswire.jsword.book.Book;

public class EventBookList {
	
	private List<Book> bookList;
	
	public EventBookList(List<Book> bookList) {
		this.bookList = bookList;
	}
	
	public List<Book> getBookList() {
		return bookList;
	}
}
