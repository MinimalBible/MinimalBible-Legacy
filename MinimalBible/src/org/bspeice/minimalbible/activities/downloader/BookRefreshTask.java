package org.bspeice.minimalbible.activities.downloader;

import java.util.LinkedList;
import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;

import android.os.AsyncTask;
import android.util.Log;

public class BookRefreshTask extends AsyncTask<Installer, Integer, List<Book>> {
	
	private static final String TAG = "BookRefreshTask";
	
	private BookRefreshListener listener;
	private boolean refresh;
	private BookFilter filter;
	
	public BookRefreshTask(boolean refresh, BookRefreshListener listener) {
		this.refresh = refresh;
		this.listener = listener;
	}
	
	public BookRefreshTask(boolean refresh, BookFilter f, BookRefreshListener listener) {
		this.refresh = refresh;
		this.filter = f;
		this.listener = listener;
	}

	@Override
	protected List<Book> doInBackground(Installer... params) {
		List<Book> books = new LinkedList<Book>();
		
		for (Installer i: params) {
			if (refresh) {
				try {
					i.reloadBookList();
				} catch (InstallException e) {
					Log.e(TAG, "Error downloading books from installer: " + i.toString(), e);
				}
			}
			
			if (filter != null) {
				books.addAll(i.getBooks(filter));
			} else {
				books.addAll(i.getBooks());
			}
		}
		
		return books;
	}
	
	@Override
	protected void onPostExecute(List<Book> result) {
		super.onPostExecute(result);
		listener.onRefreshComplete(result);
	}
	
	public interface BookRefreshListener {
		public void onRefreshComplete(List<Book> results);
	}
}


