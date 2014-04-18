package org.bspeice.minimalbible.activities.downloader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.bspeice.minimalbible.MinimalBible;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadManager {
	
	private final String TAG = "DownloadManager";
	
	public DownloadManager() {
		setDownloadDir();
	}
	
	public InstallerReloadTask fetchAvailableBooks(boolean forceRefresh, BookRefreshListener bookRefreshListener) {
		
		Map<String, Installer> installers = getInstallers();
		
		return (InstallerReloadTask)
				new InstallerReloadTask(bookRefreshListener).execute(installers.values().toArray(new Installer[installers.size()]));
	}
	
	public Map<String, Installer> getInstallers() {
		return new InstallManager().getInstallers();		
	}
	
	private void setDownloadDir() {
		// We need to set the download directory for jSword to stick with Android.
		String home = MinimalBible.getAppContext().getFilesDir().toString();
		Log.d(TAG, "Setting jsword.home to: " + home);
		System.setProperty("jsword.home", home);
	}
	
	public class InstallerReloadTask extends AsyncTask<Installer, Float, List<Book>> {
		private BookRefreshListener listener;
		
		public InstallerReloadTask(BookRefreshListener listener) {
			this.listener = listener;
		}

		@Override
		protected List<Book> doInBackground(Installer... params) {
			List<Book> books = new LinkedList<Book>();
			for (Installer i: params) {
				try {
					i.reloadBookList();
				} catch (InstallException e) {
					Log.e(TAG, "Error downloading books from installer: " + i.toString(), e);
				}
				books.addAll(i.getBooks());
			}
			
			return books;
		}
		
		@Override
		protected void onPostExecute(List<Book> result) {
			super.onPostExecute(result);
			listener.onRefreshComplete(result);
		}
	}
	
	public interface BookRefreshListener {
		public void onRefreshComplete(List<Book> results);
	}

}
