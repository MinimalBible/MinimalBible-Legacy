package org.bspeice.minimalbible.activities.downloader;

import java.util.List;

import org.bspeice.minimalbible.MinimalBibleConstants;
import org.bspeice.minimalbible.R;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookListFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_BOOK_CATEGORY = "book_category";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static BookListFragment newInstance(BookCategory c) {
		BookListFragment fragment = new BookListFragment();
		Bundle args = new Bundle();
		args.putString(ARG_BOOK_CATEGORY, c.toString());
		fragment.setArguments(args);
		return fragment;
	}

	public BookListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_download, container,
				false);
		TextView textView = (TextView) rootView
				.findViewById(R.id.section_label);
		textView.setText(getArguments().getString(ARG_BOOK_CATEGORY));
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((DownloadActivity) activity).onSectionAttached(getArguments()
				.getString(ARG_BOOK_CATEGORY));
	}

	public void displayModules() {
		DownloadManager dm = new DownloadManager();

		if (dm.willRefresh()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			DownloadDialogListener dialogListener = new DownloadDialogListener();
			builder.setMessage(
					"About to contact servers to download content. Continue?")
					.setPositiveButton("Yes", dialogListener)
					.setNegativeButton("No", dialogListener)
					.setCancelable(false).show();
		} else {
			refreshModules();
		}
	}

	private void refreshModules() {
		DownloadManager dm = new DownloadManager();

		if (dm.willRefresh()) {
			ProgressDialog refreshDialog = new ProgressDialog(getActivity());
			refreshDialog.setMessage("Refreshing available modules...");
			refreshDialog.setCancelable(false);
			refreshDialog.show();
			dm.fetchAvailableBooks(new DlBookRefreshListener(refreshDialog));
		} else {
			dm.fetchAvailableBooks(new DlBookRefreshListener());
		}
	}

	private class DlBookRefreshListener implements
			BookRefreshTask.BookRefreshListener {
		// TODO: Figure out why I need to pass in the ProgressDialog, and can't
		// cancel it from onRefreshComplete.
		ProgressDialog dl;

		public DlBookRefreshListener(ProgressDialog dl) {
			this.dl = dl;
		}
		
		public DlBookRefreshListener() {
		}

		@Override
		public void onRefreshComplete(List<Book> results) {
			if (dl != null) {
				dl.cancel();
			}
			
			for (Book b : results) {
				Log.d("DlBookRefreshListener", b.getName());
			}
		}
	}

	private class DownloadDialogListener implements
			DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Clicked ready to continue - allow downloading in the future
				SharedPreferences prefs = getActivity().getSharedPreferences(
						MinimalBibleConstants.DOWNLOAD_PREFS_FILE, Context.MODE_PRIVATE);
				prefs.edit()
						.putBoolean(MinimalBibleConstants.KEY_DOWNLOAD_ENABLED,
								true).commit();

				// And warn them that it has been enabled in the future.
				Toast.makeText(getActivity(),
						"Downloading now enabled. Disable in settings.",
						Toast.LENGTH_SHORT).show();
				refreshModules();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				// Not going to continue, still show what has
				// already been downloaded.
				break;
			}

		}
	}

}