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
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
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
                    .map(new Func1<Installer, Map<Installer, List<Book>>>() {
                        @Override
                        public Map<Installer, List<Book>> call(Installer installer) {
                            Map<Installer, List<Book>> map = new HashMap<Installer, List<Book>>();
                            map.put(installer, installer.getBooks());
                            return map;
                        }
                    }).subscribeOn(Schedulers.io())
                    .cache();

            // Set refresh complete when it is.
            availableModules.observeOn(Schedulers.io())
                    .subscribe(new Action1<Map<Installer, List<Book>>>() {
                        @Override
                        public void call(Map<Installer, List<Book>> onNext) {}
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable onError) {}
                    }, new Action0() {
                        @Override
                        public void call() {
                            refreshComplete.set(true);
                        }
                    });
        }
    }

    public Observable<Map<Installer, List<Book>>> getAvailableModules() {
        return availableModules;
    }

    public Observable<Book> getAvailableModulesFlattened() {
        return availableModules
                // First flatten the Map to its lists
                .flatMap(new Func1<Map<Installer, List<Book>>, Observable<? extends List<Book>>>() {
                    @Override
                    public Observable<? extends List<Book>> call(Map<Installer, List<Book>> books) {
                        return Observable.from(books.values());
                    }
                })
                // Then flatten the lists
                .flatMap(new Func1<List<Book>, Observable<? extends Book>>() {
                    @Override
                    public Observable<? extends Book> call(List<Book> t1) {
                        return Observable.from(t1);
                    }
                });
    }

    /**
     * Get the cached book list
     * @return The cached book list, or null
     */
    public List<Book> getBookList() {
        List<Book> availableList = new ArrayList<Book>();
        availableModules.reduce(availableList,
                new Func2<List<Book>, Map<Installer, List<Book>>, List<Book>>() {
            @Override
            public List<Book> call(List<Book> books, Map<Installer, List<Book>> installerListMap) {
                for (List<Book> l : installerListMap.values()) {
                    books.addAll(l);
                }
                return books;
            }
        });
        return availableList;
    }

    /**
     * Find the installer that a Book comes from.
     * @param b The book to search for
     * @return The Installer that should be used for this book.
     */
    public Observable<Installer> installerFromBook(final Book b) {
        return availableModules.filter(new Func1<Map<Installer, List<Book>>, Boolean>() {
            @Override
            public Boolean call(Map<Installer, List<Book>> installerListMap) {
                for (List<Book> element : installerListMap.values()) {
                    if (element.contains(b)) {
                        return true;
                    }
                }
                return false;
            }
        })
        .first()
        .map(new Func1<Map<Installer, List<Book>>, Installer>() {
            @Override
            public Installer call(Map<Installer, List<Book>> element) {
                return element.entrySet().iterator().next().getKey();
            }
        });
    }

    public boolean isRefreshComplete() {
        return refreshComplete.get();
    }
}
