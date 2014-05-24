package org.bspeice.minimalbible.activities.downloader.manager;

import org.crosswire.jsword.book.Book;

/**
 * Used for notifying that a book's download progress is ongoing
 */
public class DownloadProgressEvent {
    private int progress;
    private Book b;

    public static final int PROGRESS_COMPLETE = 100;
    public static final int PROGRESS_BEGINNING = 0;

    public DownloadProgressEvent(int progress, Book b) {
        this.progress = progress;
        this.b = b;
    }

    public int getProgress() {
        return progress;
    }

    public Book getB() {
        return b;
    }

    public float toCircular() {
        return ((float)progress) * 360 / 100;
    }

    public boolean isComplete() {
        return progress >= 100;
    }
}
