package org.bspeice.minimalbible.activities.downloader.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.activities.downloader.DownloadPrefs;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BookRefreshTask extends AsyncTask<Installer, Integer, List<Book>> {
	private static final String TAG = "EventBookRefreshTask";

    // If last refresh was before the below, force an internet refresh
    private final Long refreshAfter = System.currentTimeMillis() - 604800000L; // 1 Week in millis

    @Inject
    DownloadPrefs downloadPrefs;

    @Inject DownloadManager downloadManager;

	public BookRefreshTask() {
        MinimalBible.getApplication().inject(this);
	}

	@Override
	protected List<Book> doInBackground(Installer... params) {
        Map<Installer, List<Book>> bookList = new HashMap<Installer, List<Book>>();

		int index = 0;
		for (Installer i : params) {
			if (doRefresh()) {
				try {
					i.reloadBookList();
                    downloadPrefs.downloadRefreshedOn(System.currentTimeMillis());
				} catch (InstallException e) {
					Log.e(TAG,
							"Error downloading books from installer: "
									+ i.toString(), e);
				}
			}
            bookList.put(i, i.getBooks());
			publishProgress(++index, params.length);
		}

        // Pre-cache the DownloadManager with the list of installed books
        downloadManager.isInstalled(bookList.values().iterator().next().get(0));
        EventBookList event = new EventBookList(bookList);
        downloadManager.getDownloadBus().post(event);

        return event.getBookList();
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
        return downloadPrefs.hasEnabledDownload();
    }

    private boolean needsRefresh() {
        return (downloadPrefs.downloadRefreshedOn() > refreshAfter);
    }
}
