package org.bspeice.minimalbible.activities.downloader.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.Installer;

/**
 * POJO class for {@link de.greenrobot.event.EventBus} to broadcast whenever
 * we've finished updating the book list.
 */
public class EventBookList {
	
	private Map<Installer, List<Book>> bookMapping;
	
	public EventBookList(Map<Installer, List<Book>> bookList) {
		this.bookMapping = bookList;
	}

	public Map<Installer, List<Book>> getInstallerMapping() {
		return bookMapping;
	}

    public List<Book> getBookList() {
        List<Book> bookList = new ArrayList<Book>();
        for (Installer i: bookMapping.keySet()) {
            bookList.addAll(bookMapping.get(i));
        }
        return bookList;
    }
}
