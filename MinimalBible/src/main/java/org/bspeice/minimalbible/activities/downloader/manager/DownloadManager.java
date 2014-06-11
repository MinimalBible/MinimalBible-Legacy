package org.bspeice.minimalbible.activities.downloader.manager;

import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.sword.SwordBookPath;

import java.io.File;
import java.util.Map;

import javax.inject.Singleton;

// TODO: Listen to BookInstall events?
@Singleton
public class DownloadManager {

	private final String TAG = "DownloadManager";

	public static final BookCategory[] VALID_CATEGORIES = { BookCategory.BIBLE,
			BookCategory.COMMENTARY, BookCategory.DICTIONARY,
			BookCategory.MAPS };

    /**
     * Set up the DownloadManager, and notify jSword of where it should store files at
     */
	public DownloadManager() {
        MinimalBible.getApplication().inject(this);
		setDownloadDir();
	}

    /**
     * Get the installers available to jSword - this is how we get access to the actual modules
     * @return All available {@link org.crosswire.jsword.book.install.Installer}s
     */
	public Map<String, Installer> getInstallers() {
		return new InstallManager().getInstallers();
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
        System.setProperty("sword.home", home);
        SwordBookPath.setDownloadDir(new File(home));
        Log.d(TAG, "Sword download path: " + SwordBookPath.getSwordDownloadDir());
	}
}
