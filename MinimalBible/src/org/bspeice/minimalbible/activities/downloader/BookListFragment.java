package org.bspeice.minimalbible.activities.downloader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;
import org.bspeice.minimalbible.MinimalBibleConstants;
import org.bspeice.minimalbible.R;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.FilterUtil;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
@EFragment(R.layout.fragment_download)
public class BookListFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */

    private final String TAG = "BookListFragment";

    @FragmentArg
	BookCategory bookCategory;

    @ViewById(R.id.section_label)
	protected TextView tv;

	private ProgressDialog refreshDialog;

    @Trace
    @AfterViews
    public void updateName() {
        ((DownloadActivity) getActivity()).onSectionAttached(bookCategory.toString());
        tv.setText(bookCategory.toString());
        displayModules();
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
		DownloadManager dm = DownloadManager.getInstance();
		EventBookList bookList = dm.getDownloadBus().getStickyEvent(EventBookList.class);
		if (bookList == null) {
            dm.getDownloadBus().registerSticky(this);
            refreshDialog = new ProgressDialog(getActivity());
            refreshDialog.setMessage("Refreshing available modules...");
            refreshDialog.setCancelable(false);
            refreshDialog.show();
        } else {
            displayBooks(bookList.getBookList());
        }
	}

    /*
    Used by GreenRobot for notifying us that the book refresh is complete
     */
    @SuppressWarnings("unused")
	public void onEventMainThread(EventBookList event) {
		if (refreshDialog != null) {
			refreshDialog.cancel();
		}
		displayBooks(event.getBookList());
	}

    public void displayBooks(List<Book> bookList) {
        try {
            BookFilter f = FilterUtil.filterFromCategory(bookCategory);
            List<Book> filteredBooks = FilterUtil.applyFilter(bookList, f);
            tv.setText(filteredBooks.get(0).getName());
        } catch (FilterUtil.InvalidFilterCategoryMappingException e) {
            // To be honest, there should be no reason you end up here.
            Log.e(TAG, e.getMessage());
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