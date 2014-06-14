package org.bspeice.minimalbible.activities.downloader.manager;

import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.InstallException;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

/**
 * Thread that handles downloading a book
 */
//TODO: Refactor to BookDownloadManager, downloadBook() creates its own thread
public class BookDownloadThread {

    private final String TAG = "BookDownloadThread";

    @Inject
    BookDownloadManager bookDownloadManager;
    @Inject
    RefreshManager refreshManager;

    public BookDownloadThread() {
        MinimalBible.getApplication().inject(this);
    }

    public void downloadBook(final Book b) {
        // So, the JobManager can't be injected, but we'll make do

        // First, look up where the Book came from
        refreshManager.installerFromBook(b)
                .subscribeOn(Schedulers.io())
                .subscribe((installer) -> {
                    try {
                        installer.install(b);
                    } catch (InstallException e) {
                        Log.d(TAG, e.getMessage());
                    }

                    bookDownloadManager.getDownloadEvents().onNext(new DLProgressEvent(DLProgressEvent.PROGRESS_BEGINNING, b));
                });
    }

    /**
     * Build what the installer creates the job name as.
     * Likely prone to be brittle.
     * TODO: Make sure to test that this is an accurate job name
     *
     * @param b The book to predict the download job name of
     * @return The name of the job that will/is download/ing this book
     */

    public static String getJobId(Book b) {
        return "INSTALL_BOOK-" + b.getInitials();
    }
}
