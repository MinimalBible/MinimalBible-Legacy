package org.bspeice.minimalbible.activities.downloader.manager;

import org.crosswire.jsword.book.Book;

/**
 * Created by bspeice on 5/19/14.
 */
public class DownloadProgressEvent {
    private int progress;
    private Book b;

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
