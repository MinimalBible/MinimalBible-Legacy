package org.bspeice.minimalbible.activities.downloader.manager;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.Installer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Handle refreshing the list of books available as needed
 */
@Singleton
public class RefreshManager {

    @Inject DownloadManager downloadManager;
    @Inject InstalledManager installedManager;

    /**
     * Cached copy of modules that are available so we don't refresh for everyone who requests it.
     */
    private Observable<Map<Installer, List<Book>>> availableModules;
    private final AtomicBoolean refreshComplete = new AtomicBoolean();

    public RefreshManager() {
        MinimalBible.getApplication().inject(this);
        refreshModules();
    }

    /**
     * Do the work of kicking off the AsyncTask to refresh books, and make sure we know
     * when it's done.
     * TODO: Should I have a better way of scheduling than Schedulers.io()?
     */
    private void refreshModules() {
        if (availableModules == null) {
            availableModules = Observable.from(downloadManager.getInstallers().values())
                    .map(installer -> {
                        Map<Installer, List<Book>> map = new HashMap<Installer, List<Book>>();
                        map.put(installer, installer.getBooks());
                        return map;
                    }).subscribeOn(Schedulers.io())
                    .cache();

            // Set refresh complete when it is.
            availableModules.observeOn(Schedulers.io())
                    .subscribe((onNext) -> {}, (onError) -> {}, () -> refreshComplete.set(true));
        }
    }

    public Observable<Map<Installer, List<Book>>> getAvailableModules() {
        return availableModules;
    }

    public Observable<Book> getAvailableModulesFlattened() {
        return availableModules
                // First flatten the Map to its lists
                .flatMap((books) -> Observable.from(books.values()))
                // Then flatten the lists
                .flatMap(Observable::from);
    }

    /**
     * Get the cached book list
     * @return The cached book list, or null
     */
    public List<Book> getBookList() {
        List<Book> availableList = new ArrayList<>();
        availableModules.reduce(availableList, (books, installerListMap) -> {
            for (List<Book> l : installerListMap.values()) {
                books.addAll(l);
            }
            return books;
        });
        return availableList;
    }

    /**
     * Find the installer that a Book comes from.
     * @param b The book to search for
     * @return The Installer that should be used for this book.
     */
    public Observable<Installer> installerFromBook(Book b) {
        return availableModules.filter(installerListMap -> {
            for (List<Book> element : installerListMap.values()) {
                if (element.contains(b)) {
                    return true;
                }
            }
            return false;
        })
        .first()
        .map(element -> element.entrySet().iterator().next().getKey());
    }

    public boolean isRefreshComplete() {
        return refreshComplete.get();
    }
}
