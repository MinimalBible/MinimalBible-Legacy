package org.bspeice.minimalbible.activities.downloader;

import java.util.Map;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import android.util.Log;

public class DownloadManager {

	private final String TAG = "DownloadManager";

	public static final BookCategory[] VALID_CATEGORIES = { BookCategory.BIBLE,
			BookCategory.COMMENTARY, BookCategory.DICTIONARY,
			BookCategory.IMAGES, BookCategory.MAPS };

	public DownloadManager() {
		setDownloadDir();
	}

	public BookRefreshTask fetchAvailableBooks(boolean refresh,
			BookRefreshTask.BookRefreshListener bookRefreshListener) {

		Map<String, Installer> installers = getInstallers();

		return (BookRefreshTask) new BookRefreshTask(refresh,
				bookRefreshListener).execute(installers.values().toArray(
				new Installer[installers.size()]));
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
