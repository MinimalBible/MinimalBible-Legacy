package org.bspeice.minimalbible.activities.downloader;

import java.util.Map;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.MinimalBibleConstants;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class DownloadManager {
	
	// TODO: Probably should be a singleton.

	private final String TAG = "DownloadManager";

	public static final BookCategory[] VALID_CATEGORIES = { BookCategory.BIBLE,
			BookCategory.COMMENTARY, BookCategory.DICTIONARY,
			BookCategory.IMAGES, BookCategory.MAPS };

	public DownloadManager() {
		setDownloadDir();
	}

	public BookRefreshTask fetchAvailableBooks(
			BookRefreshTask.BookRefreshListener bookRefreshListener) {
		return _fetchAvailableBooks(null, bookRefreshListener);
	}
	
	public BookRefreshTask fetchAvailableBooks(BookFilter f,
			BookRefreshTask.BookRefreshListener bookRefreshListener) {
		return _fetchAvailableBooks(f, bookRefreshListener);
	}

	private BookRefreshTask _fetchAvailableBooks(BookFilter f,
			BookRefreshTask.BookRefreshListener bookRefreshListener) {
		
		Map<String, Installer> installers = getInstallers();

		return (BookRefreshTask) new BookRefreshTask(willRefresh(),
				bookRefreshListener).execute(installers.values().toArray(
				new Installer[installers.size()]));
	}

	public boolean willRefresh() {
		// Method to determine if we need a refresh
		// Public, so other modules can predict and take action accordingly.
		// TODO: Discover if we need to refresh over Internet, or use a cached
		// copy - likely something time-based, also check network state.
		// Fun fact - jSword handles the caching for us.

		SharedPreferences prefs = MinimalBible.getAppContext()
				.getSharedPreferences(
						MinimalBibleConstants.DOWNLOAD_PREFS_FILE,
						Context.MODE_PRIVATE);

		return (prefs.getBoolean(MinimalBibleConstants.KEY_DOWNLOAD_ENABLED, false));
	}

	public Map<String, Installer> getInstallers() {
		return new InstallManager().getInstallers();
	}

	private void setDownloadDir() {
		// We need to set the download directory for jSword to stick with
		// Android.
		String home = MinimalBible.getAppContext().getFilesDir().toString();
		Log.d(TAG, "Setting jsword.home to: " + home);
		System.setProperty("jsword.home", home);
	}

}
