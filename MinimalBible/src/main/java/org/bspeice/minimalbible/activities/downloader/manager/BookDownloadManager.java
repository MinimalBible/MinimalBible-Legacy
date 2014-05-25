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

    @Inject
    Provider<BookDownloadThread> dlThreadProvider;

    /* Going to fix this in the next commit, right now it's circular
    @Inject
    */
    DownloadManager downloadManager;

    public BookDownloadManager() {
        bookMappings = new HashMap<String, Book>();
        JobManager.addWorkListener(this);
        MinimalBible.getApplication().inject(this);
    }

    public void downloadBook(Book b) {
        BookDownloadThread dlThread = dlThreadProvider.get();
        dlThread.downloadBook(b);
        addJob(BookDownloadThread.getJobId(b), b);
    }

    public void addJob(String jobId, Book b) {
        bookMappings.put(jobId, b);
    }

    @Override
    public void workProgressed(WorkEvent ev) {
        Log.d("BookDownloadManager", ev.toString());
        Progress job = ev.getJob();
        if (bookMappings.containsKey(job.getJobID())) {
            downloadManager.getRefreshBus()
                    .post(new DownloadProgressEvent(job.getTotalWork(), job.getWorkDone(),
                            bookMappings.get(job.getJobID())));
        }
    }

    @Override
    public void workStateChanged(WorkEvent ev) {
        Log.d("BookDownloadManager", ev.toString());
    }

}
