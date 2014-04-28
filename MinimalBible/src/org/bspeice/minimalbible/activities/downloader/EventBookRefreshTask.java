package org.bspeice.minimalbible.activities.downloader;

import java.util.LinkedList;
import java.util.List;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.MinimalBibleConstants;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;

import de.greenrobot.event.EventBus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class EventBookRefreshTask extends AsyncTask<Installer, Integer, List<Book>> {
	private static final String TAG = "EventBookRefreshTask";

	private EventBus downloadBus;
	private BookFilter filter;

	public EventBookRefreshTask(EventBus downloadBus) {
		this.downloadBus = downloadBus;
	}

	public EventBookRefreshTask(EventBus downloadBus, BookFilter f) {
		this.downloadBus = downloadBus;
		this.filter = f;
	}

	@Override
	protected List<Book> doInBackground(Installer... params) {
		List<Book> books = new LinkedList<Book>();

		int index = 0;
		for (Installer i : params) {
			if (doRefresh()) {
				try {
					i.reloadBookList();
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

		SharedPreferences prefs = MinimalBible.getAppContext()
				.getSharedPreferences(
						MinimalBibleConstants.DOWNLOAD_PREFS_FILE,
						Context.MODE_PRIVATE);

		// Refresh if download enabled
		return prefs.getBoolean(MinimalBibleConstants.KEY_DOWNLOAD_ENABLED, false);
	}
}
