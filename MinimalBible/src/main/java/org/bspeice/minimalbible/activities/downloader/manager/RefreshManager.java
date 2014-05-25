package org.bspeice.minimalbible.activities.downloader.manager;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.Installer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.event.EventBus;

/**
 * Created by bspeice on 5/24/14.
 */
@Singleton
public class RefreshManager {

    @Inject DownloadManager downloadManager;

    /**
     * Cached copy of modules that are available so we don't refresh for everyone who requests it.
     */
    private Map<Installer, List<Book>> availableModules;

    public RefreshManager() {
        MinimalBible.getApplication().inject(this);
        availableModules = new HashMap<Installer, List<Book>>();
        refreshModules();
    }

    /**
     * Do the work of kicking off the AsyncTask to refresh books, and make sure we know
     * when it's done.
     */
    private void refreshModules() {
        EventBus refreshBus = downloadManager.getDownloadBus();
        refreshBus.register(this);
        new BookRefreshTask().execute(downloadManager.getInstallersArray());
    }

    /**
     * When book refresh is done, cache the list so we can give that to someone else
     * @param event A POJO wrapper around the Book list
     */
    @SuppressWarnings("unused")
    public void onEvent(EventBookList event) {
        this.availableModules = event.getInstallerMapping();
    }

    /**
     * Get the cached book list
     * @return The cached book list, or null
     */
    public List<Book> getBookList() {
        if (availableModules.values().size() == 0) {
            return null;
        } else {
            List<Book> bookList = new ArrayList<Book>();
            for (List<Book> l : availableModules.values()) {
                bookList.addAll(l);
            }
            return bookList;
        }
    }

    /**
     * Find the installer that a Book comes from.
     * @param b The book to search for
     * @return The Installer that should be used for this book.
     */
    public Installer installerFromBook(Book b) {
        for (Map.Entry<Installer, List<Book>> entry : availableModules.entrySet()) {
            if (entry.getValue().contains(b)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
