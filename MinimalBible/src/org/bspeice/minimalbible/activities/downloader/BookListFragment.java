package org.bspeice.minimalbible.activities.downloader;

import java.util.List;

import org.bspeice.minimalbible.MinimalBibleConstants;
import org.bspeice.minimalbible.R;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

	protected TextView tv;

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
		tv = (TextView) rootView.findViewById(R.id.section_label);
		tv.setText(getArguments().getString(ARG_BOOK_CATEGORY));
		displayModules();
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((DownloadActivity) activity).onSectionAttached(getArguments()
				.getString(ARG_BOOK_CATEGORY));
	}

	public void displayModules() {
		SharedPreferences prefs = getActivity()
				.getSharedPreferences(
						MinimalBibleConstants.DOWNLOAD_PREFS_FILE,
						Context.MODE_PRIVATE);
		boolean dialogDisplayed = prefs.getBoolean(
				MinimalBibleConstants.KEY_SHOWED_DOWNLOAD_DIALOG, false);
		
		if (!dialogDisplayed) {
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
		BookCategory bc = BookCategory.fromString(getArguments().getString(
				ARG_BOOK_CATEGORY));
		BookFilter f;
		// We wouldn't need this switch if BookFilters.BookCategoryFilter were
		// public...
		switch (bc) {
		case BIBLE:
			f = BookFilters.getBibles();
			break;
		case COMMENTARY:
			f = BookFilters.getCommentaries();
			break;
		case DAILY_DEVOTIONS:
			f = BookFilters.getDailyDevotionals();
			break;
		case DICTIONARY:
			f = BookFilters.getDictionaries();
			break;
		case GENERAL_BOOK:
			f = BookFilters.getGeneralBooks();
			break;
		case GLOSSARY:
			f = BookFilters.getGlossaries();
			break;
		case MAPS:
			f = BookFilters.getMaps();
			break;
		default:
			// DownloadManager takes care of accepting a null value
			f = null;
			break;
		}
		DownloadManager dm = new DownloadManager();

		ProgressDialog refreshDialog = new ProgressDialog(getActivity());
		if (dm.willRefresh()) {
			refreshDialog.setMessage("Refreshing available modules...");
		} else {
			refreshDialog
					.setMessage("Fetching available modules from cache...");
		}
		refreshDialog.setCancelable(false);
		refreshDialog.show();
		dm.fetchAvailableBooks(f, new DlBookRefreshListener(refreshDialog));
	}

	private class DlBookRefreshListener implements
			BookRefreshTask.BookRefreshListener {
		// TODO: Figure out why I need to pass in the ProgressDialog, and can't
		// cancel it from onRefreshComplete.
		ProgressDialog dl;

		public DlBookRefreshListener(ProgressDialog dl) {
			this.dl = dl;
		}

		@Override
		public void onRefreshComplete(List<Book> results) {
			if (dl != null) {
				dl.cancel();
			}
			tv.setText(results.get(0).getName());

		}
	}

	private class DownloadDialogListener implements
			DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			SharedPreferences prefs = getActivity().getSharedPreferences(
					MinimalBibleConstants.DOWNLOAD_PREFS_FILE,
					Context.MODE_PRIVATE);
			prefs.edit().putBoolean(MinimalBibleConstants.KEY_SHOWED_DOWNLOAD_DIALOG, true)
				.commit();

			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Clicked ready to continue - allow downloading in the future
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
				// Clicked to not download - Permanently disable downloading
				prefs.edit()
						.putBoolean(MinimalBibleConstants.KEY_DOWNLOAD_ENABLED,
								false).commit();
				Toast.makeText(getActivity(),
						"Disabling downloading. Re-enable it in settings.",
						Toast.LENGTH_SHORT).show();
				refreshModules();
				break;
			}
		}
	}

}