package org.bspeice.minimalbible.activities.downloader.manager;

import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.jsword.book.Book;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import de.greenrobot.event.EventBus;

/**
 * Wrapper to convert JSword progress events to MinimalBible EventBus-based
 */
@Singleton
public class BookDownloadManager implements WorkListener{

    /**
     * Mapping of Job ID to the EventBus we should trigger progress on
     */
    private Map<String, Book> bookMappings;

    /**
     * Cached copy of downloads in progress so views displaying this info can get it quickly.
     */
    private Map<Book, DLProgressEvent> inProgressDownloads;

    @Inject
    Provider<BookDownloadThread> dlThreadProvider;

    @Inject DownloadManager downloadManager;

    public BookDownloadManager() {
        bookMappings = new HashMap<String, Book>();
        inProgressDownloads = new HashMap<Book, DLProgressEvent>();
        JobManager.addWorkListener(this);
        MinimalBible.getApplication().inject(this);
    }

    public void installBook(Book b) {
        BookDownloadThread dlThread = dlThreadProvider.get();
        dlThread.downloadBook(b);
        addJob(BookDownloadThread.getJobId(b), b);
    }

    public void addJob(String jobId, Book b) {
        bookMappings.put(jobId, b);
    }

    @Override
    public void workProgressed(WorkEvent ev) {
        Progress job = ev.getJob();
        EventBus downloadBus = downloadManager.getDownloadBus();
        if (bookMappings.containsKey(job.getJobID())) {
            Book b = bookMappings.get(job.getJobID());

            if (job.getWorkDone() == job.getTotalWork()) {
                // Download is complete
                inProgressDownloads.remove(bookMappings.get(job.getJobID()));
                downloadBus.post(new DLProgressEvent(DLProgressEvent.PROGRESS_COMPLETE, b));
            } else {
                // Track the ongoing download
                DLProgressEvent event = new DLProgressEvent(job.getWorkDone(),
                        job.getTotalWork(), b);
                inProgressDownloads.put(b, event);
                downloadBus.post(event);
            }
        }
    }

    /**
     * Check the status of a book download in progress.
     * @param b
     * @return The most recent DownloadProgressEvent for the book, or null if not downloading
     */
    public DLProgressEvent getInProgressDownloadProgress(Book b) {
        if (inProgressDownloads.containsKey(b)) {
            return inProgressDownloads.get(b);
        } else {
            return null;
        }
    }

    @Override
    public void workStateChanged(WorkEvent ev) {
        Log.d("BookDownloadManager", ev.toString());
    }
}
