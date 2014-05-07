package org.bspeice.minimalbible.activities.downloader;

import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.event.EventBus;

@Singleton
public class DownloadManager {

	private final String TAG = "DownloadManager";

    @Inject
    protected EventBus downloadBus;

	public static final BookCategory[] VALID_CATEGORIES = { BookCategory.BIBLE,
			BookCategory.COMMENTARY, BookCategory.DICTIONARY,
			BookCategory.MAPS };

	public DownloadManager() {
        MinimalBible.getApplication().inject(this);
		setDownloadDir();
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
