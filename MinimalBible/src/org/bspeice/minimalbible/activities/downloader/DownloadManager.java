package org.bspeice.minimalbible.activities.downloader;

import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import java.util.Map;

import de.greenrobot.event.EventBus;

public class DownloadManager {

	private final String TAG = "DownloadManager";
	private static DownloadManager instance;
	private EventBus downloadBus;

	public static final BookCategory[] VALID_CATEGORIES = { BookCategory.BIBLE,
			BookCategory.COMMENTARY, BookCategory.DICTIONARY,
			BookCategory.MAPS };

	public static DownloadManager getInstance() {
		if (instance == null) {
			instance = new DownloadManager();
		}
		return instance;
	}

	private DownloadManager() {
		setDownloadDir();
		downloadBus = new EventBus();
		downloadEvents();
	}

	public Map<String, Installer> getInstallers() {
		return new InstallManager().getInstallers();
	}
	
	public Installer[] getInstallersArray() {
		Map<String, Installer> installers = getInstallers();
		return installers.values().toArray(new Installer[installers.size()]);
	}

	private void setDownloadDir() {
		// We need to set the download directory for jSword to stick with
		// Android.
		String home = MinimalBible.getAppContext().getFilesDir().toString();
		Log.d(TAG, "Setting jsword.home to: " + home);
		System.setProperty("jsword.home", home);
	}

	private void downloadEvents() {
		new BookRefreshTask(downloadBus).execute(getInstallersArray());
	}
	
	public EventBus getDownloadBus() {
		return this.downloadBus;
	}
}
