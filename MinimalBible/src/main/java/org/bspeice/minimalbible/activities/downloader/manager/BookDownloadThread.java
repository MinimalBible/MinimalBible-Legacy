package org.bspeice.minimalbible.activities.downloader.manager;

import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;

import java.util.UUID;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by bspeice on 5/12/14.
 */
public class BookDownloadThread {

    private final String TAG = "BookDownloadThread";

    @Inject
    DownloadManager downloadManager;

    public BookDownloadThread() {
        MinimalBible.getApplication().inject(this);
    }

    public void downloadBook(final Book b) {
        // So, the JobManager can't be injected, but we'll make do

        // First, look up where the Book came from
        final Installer i = downloadManager.installerFromBook(b);

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

        JobManager.createJob(getJobId(b), b.getName(), worker);
        downloadManager.getRefreshBus().post(new DownloadProgressEvent(DownloadProgressEvent.PROGRESS_BEGINNING, b));
    }

    /**
     * Return a job ID for a given book. Must be consistent per book.
     * @param b
     * @return A string representing this job IDs
     */
    public static String getJobId(Book b) {
        return b.toString();
    }
}
