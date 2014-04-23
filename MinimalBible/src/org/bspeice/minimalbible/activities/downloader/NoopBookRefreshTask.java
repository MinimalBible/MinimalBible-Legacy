package org.bspeice.minimalbible.activities.downloader;

import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.Installer;

/*
 * There's probably a better way of doing this, but this allows me to avoid networking,
 * while still maintaining the code structure of being asynchronous.
 */
public class NoopBookRefreshTask extends BookRefreshTask {
	BookRefreshListener listener;
	List<Book> books;
	
	public NoopBookRefreshTask(List<Book> books, BookRefreshListener listener) {
		super(listener);
		this.books = books;
	}
	
	@Override
	protected List<Book> doInBackground(Installer... params) {
		return books;
	}
}
