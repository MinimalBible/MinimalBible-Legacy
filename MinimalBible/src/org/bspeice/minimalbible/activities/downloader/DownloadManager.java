package org.bspeice.minimalbible.activities.downloader;

import java.util.List;
import java.util.Map;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.activities.downloader.BookRefreshTask.BookRefreshListener;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import de.greenrobot.event.EventBus;

import android.util.Log;

public class DownloadManager {

	private final String TAG = "DownloadManager";
	private static DownloadManager instance;
	private List<Book> books;
	private EventBus downloadBus;

	public static final BookCategory[] VALID_CATEGORIES = { BookCategory.BIBLE,
			BookCategory.COMMENTARY, BookCategory.DICTIONARY,
			BookCategory.IMAGES, BookCategory.MAPS };

	public static DownloadManager getInstance() {
		if (instance == null) {
			instance = new DownloadManager();
		}
		return instance;
	}

	private DownloadManager() {
		setDownloadDir();
		downloadBus = new EventBus();
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

		if (!isLoaded()) {
			return (BookRefreshTask) new BookRefreshTask(
					new DmBookRefreshListener(bookRefreshListener))
					.execute(getInstallersArray());
		} else {
			return (BookRefreshTask) new NoopBookRefreshTask(books,
					bookRefreshListener).execute(getInstallersArray());
		}
	}

	public boolean isLoaded() {
		// Let methods know if we're going to take a while to reload everything
		return (books != null);
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

	// Create our own refresh listener to save a reference to the books
	private class DmBookRefreshListener implements BookRefreshListener {
		private BookRefreshListener listener;

		public DmBookRefreshListener(BookRefreshListener listener) {
			this.listener = listener;
		}

		@Override
		public void onRefreshComplete(List<Book> results) {
			books = results;
			listener.onRefreshComplete(results);
		}
	}
	
	private void downloadEvents() {
		new EventBookRefreshTask(downloadBus).execute(getInstallersArray());
	}
	
	public EventBus getDownloadBus() {
		return this.downloadBus;
	}
}
