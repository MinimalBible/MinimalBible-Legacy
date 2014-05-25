package org.bspeice.minimalbible.activities.downloader.manager;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manager to keep track of which books have been installed
 */
@Singleton
public class InstalledManager implements BooksListener {

    @Inject DownloadManager downloadManager;

    private List<Book> installedBooks;

    /**
     * Register our manager to receive events on Book install
     * This is a relatively expensive operation,
     * so we don't put it in the constructor.
     */
    public void initialize() {
        Books books = Books.installed();
        installedBooks = books.getBooks();
        books.addBooksListener(this);
    }

    public boolean isInstalled(Book b) {
        if (installedBooks == null) {
            initialize();
        }
        return installedBooks.contains(b);
    }

    @Override
    public void bookAdded(BooksEvent booksEvent) {
        Book b = booksEvent.getBook();
        if (!installedBooks.contains(b)) {
            installedBooks.add(b);
        }
    }

    @Override
    public void bookRemoved(BooksEvent booksEvent) {
        Book b = booksEvent.getBook();
        if (installedBooks.contains(b)) {
            installedBooks.remove(b);
        }
    }
}
