package org.bspeice.minimalbible.activities.downloader.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.activities.downloader.DownloadPrefs_;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BookRefreshTask extends AsyncTask<Installer, Integer, List<Book>> {
	private static final String TAG = "EventBookRefreshTask";

    // If last refresh was before the below, force an internet refresh
    private final Long refreshAfter = System.currentTimeMillis() - 604800000L; // 1 Week in millis

    @Inject
    DownloadPrefs_ downloadPrefs;

	private EventBus downloadBus;
	private BookFilter filter;

	public BookRefreshTask(EventBus downloadBus) {
		this.downloadBus = downloadBus;
        MinimalBible.getApplication().inject(this);
	}

	public BookRefreshTask(EventBus downloadBus, BookFilter f) {
		this.downloadBus = downloadBus;
		this.filter = f;
        MinimalBible.getApplication().inject(this);
	}

	@Override
	protected List<Book> doInBackground(Installer... params) {
		List<Book> books = new LinkedList<Book>();

		int index = 0;
		for (Installer i : params) {
			if (doRefresh()) {
				try {
					i.reloadBookList();
                    downloadPrefs.downloadRefreshedOn().put(System.currentTimeMillis());
				} catch (InstallException e) {
					Log.e(TAG,
							"Error downloading books from installer: "
									+ i.toString(), e);
				}
			}

			if (filter != null) {
				books.addAll(i.getBooks(filter));
			} else {
				books.addAll(i.getBooks());
			}
			publishProgress(++index, params.length);
		}

		downloadBus.postSticky(new EventBookList(books));
		return books;
	}

	private boolean doRefresh() {
		// Check if we should refresh over the internet, or use the local copy
		// TODO: Discover if we need to refresh over Internet, or use a cached
		// copy - likely something time-based, also check network state.
		// Fun fact - jSword handles the caching for us.

        return (isWifi() && downloadEnabled() && needsRefresh());
	}

    private boolean isWifi() {
        ConnectivityManager mgr = (ConnectivityManager)MinimalBible.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    private boolean downloadEnabled() {
        return downloadPrefs.hasEnabledDownload().get();
    }

    private boolean needsRefresh() {
        return (downloadPrefs.downloadRefreshedOn().get() > refreshAfter);
    }
}
