package org.bspeice.minimalbible.activities.downloader.manager;

import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.event.EventBus;

@Singleton
public class DownloadManager {

	private final String TAG = "DownloadManager";

    /**
     * Cached copy of modules that are available so we don't refresh for everyone who requests it.
     */
    private List<Book> availableModules;

    /**
     * Cached copy of downloads in progress so views displaying this info can get it quickly.
     */
    private Map<Book, DownloadProgressEvent> inProgressDownloads;

    @Inject
    protected EventBus downloadBus;

	public static final BookCategory[] VALID_CATEGORIES = { BookCategory.BIBLE,
			BookCategory.COMMENTARY, BookCategory.DICTIONARY,
			BookCategory.MAPS };

    /**
     * Set up the DownloadManager, and notify jSword of where it should store files at
     */
	public DownloadManager() {
        MinimalBible.getApplication().inject(this);
		setDownloadDir();
		refreshModules();
        inProgressDownloads = new HashMap<Book, DownloadProgressEvent>();
	}

    /**
     * Get the installers available to jSword - this is how we get access to the actual modules
     * @return All available {@link org.crosswire.jsword.book.install.Installer}s
     */
	public Map<String, Installer> getInstallers() {
		return new InstallManager().getInstallers();
	}

    /**
     * Helper method to transform the installers map to an array
     * @return Array with all available {@link org.crosswire.jsword.book.install.Installer} objects
     */
	public Installer[] getInstallersArray() {
		Map<String, Installer> installers = getInstallers();
		return installers.values().toArray(new Installer[installers.size()]);
	}

    /**
     * Notify jSword that it needs to store files in the Android internal directory
     * NOTE: Android will uninstall these files if you uninstall MinimalBible.
     */
    @SuppressWarnings("null")
	private void setDownloadDir() {
		// We need to set the download directory for jSword to stick with
		// Android.
		String home = MinimalBible.getAppContext().getFilesDir().toString();
		Log.d(TAG, "Setting jsword.home to: " + home);
		System.setProperty("jsword.home", home);
	}

    /**
     * Do the work of kicking off the AsyncTask to refresh books, and make sure we know
     * when it's done.
     */
	private void refreshModules() {
        downloadBus.register(this);
		new BookRefreshTask(downloadBus).execute(getInstallersArray());
	}

    /**
     * When book refresh is done, cache the list so we can give that to someone else
     * @param event A POJO wrapper around the Book list
     */
    @SuppressWarnings("unused")
    public void onEvent(EventBookList event) {
        this.availableModules = event.getBookList();
    }

    /**
     * Get the cached book list
     * @return The cached book list, or null
     */
    public List<Book> getBookList() {
        return availableModules;
    }

    /**
     * Get the current download bus if you want to know when refresh is done.
     * Please note that you will not be notified if the book refresh has already
     * been completed, make sure to check {@link #getBookList()} first.
     * @return The EventBus the DownloadManager is using
     */
	public EventBus getDownloadBus() {
		return this.downloadBus;
	}

    /**
     * Handle a book download progress event.
     * Mostly responsible for caching the in progress status to check on it easily
     * @param event
     */
    public void onEvent(DownloadProgressEvent event) {
        if (event.isComplete() && inProgressDownloads.containsKey(event.getB())) {
            inProgressDownloads.remove(event.getB());
        } else {
            inProgressDownloads.put(event.getB(), event);
        }
    }

    /**
     * Check the status of a book download in progress.
     * @param b
     * @return The most recent DownloadProgressEvent for the book, or null if not downloading
     */
    public DownloadProgressEvent getInProgressDownloadProgress(Book b) {
        if (inProgressDownloads.containsKey(b)) {
            return inProgressDownloads.get(b);
        } else {
            return null;
        }
    }

}
