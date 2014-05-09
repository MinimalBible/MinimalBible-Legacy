package org.bspeice.minimalbible.activities.downloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.MinimalBibleConstants;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.EventBookList;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.FilterUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */

public class BookListFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_BOOK_CATEGORY = "book_category";

    private final String TAG = "BookListFragment";

    @InjectView(R.id.lst_download_available)
    ListView downloadsAvailable;

    @Inject
    DownloadManager downloadManager;

	private ProgressDialog refreshDialog;

    /**
     * Returns a new instance of this fragment for the given section number.
     * TODO: This will need to be switched to an @Provides class
     */
    public static BookListFragment newInstance(BookCategory c) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_CATEGORY, c.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        MinimalBible app = MinimalBible.getApplication(getActivity());
        app.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_download, container,
                false);
        ButterKnife.inject(this, rootView);
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
		EventBookList bookList = downloadManager.getDownloadBus().getStickyEvent(EventBookList.class);
		if (bookList == null) {
            downloadManager.getDownloadBus().registerSticky(this);
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

    public static void setInsets(Activity context, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        SystemBarTintManager tintManager = new SystemBarTintManager(context);
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        view.setPadding(0, config.getPixelInsetTop(true), config.getPixelInsetRight(), config.getPixelInsetBottom());
    }

    public void displayBooks(List<Book> bookList) {
        try {
            // TODO: Should the filter be applied earlier in the process?
            BookCategory c = BookCategory.fromString(getArguments().getString(ARG_BOOK_CATEGORY));
            BookFilter f = FilterUtil.filterFromCategory(c);
            List<Book> filteredBooks = FilterUtil.applyFilter(bookList, f);
            downloadsAvailable.setAdapter(new BookListAdapter(this.getActivity(), filteredBooks));
            setInsets(getActivity(), downloadsAvailable);
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