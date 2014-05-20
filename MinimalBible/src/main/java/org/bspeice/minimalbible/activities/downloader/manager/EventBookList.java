package org.bspeice.minimalbible.activities.downloader.manager;

import java.util.List;

import org.crosswire.jsword.book.Book;

/**
 * POJO class for {@link de.greenrobot.event.EventBus} to broadcast whenever
 * we've finished updating the book list.
 */
public class EventBookList {
	
	private List<Book> bookList;
	
	public EventBookList(List<Book> bookList) {
		this.bookList = bookList;
	}
	
	public List<Book> getBookList() {
		return bookList;
	}
}
