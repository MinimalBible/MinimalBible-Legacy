package org.bspeice.minimalbible.activities.downloader.manager;

import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.common.progress.JobManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;

import javax.inject.Inject;

/**
 * Created by bspeice on 5/12/14.
 */
public class BookDownloadThread {

    private final String TAG = "BookDownloadThread";

    @Inject
    DownloadManager downloadManager;
    @Inject RefreshManager refreshManager;

    public BookDownloadThread() {
        MinimalBible.getApplication().inject(this);
    }

    public void downloadBook(final Book b) {
        // So, the JobManager can't be injected, but we'll make do

        // First, look up where the Book came from
        final Installer i = refreshManager.installerFromBook(b);

        final Thread worker = new Thread() {
            @Override
            public void run() {
                try {
                    i.install(b);
                } catch (InstallException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        };
        worker.start();
        // The worker automatically communicates with the JobManager for its progress.

        downloadManager.getDownloadBus().post(new DLProgressEvent(DLProgressEvent.PROGRESS_BEGINNING, b));
    }

    /**
     * Build what the installer creates the job name as.
     * Likely prone to be brittle.
     * TODO: Make sure to test that this is an accurate job name
     * @param b
     * @return
     */
    public static String getJobId(Book b) {
        return "INSTALL_BOOK-" + b.getInitials();
    }
}
