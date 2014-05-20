package org.bspeice.minimalbible.activities.downloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.f2prateek.dart.InjectExtra;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activities.BaseFragment;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.EventBookList;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookComparators;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.FilterUtil;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */

public class BookListFragment extends BaseFragment {
    /**
     * The fragment argument representing the section number for this fragment.
     * Not a candidate for Dart (yet) because I would have to write a Parcelable around it.
     */
    private static final String ARG_BOOK_CATEGORY = "book_category";

    private final String TAG = "BookListFragment";

    @InjectView(R.id.lst_download_available)
    ListView downloadsAvailable;

    @Inject DownloadManager downloadManager;

    @Inject DownloadPrefs_ downloadPrefs;

	private ProgressDialog refreshDialog;
    private LayoutInflater inflater;

    /**
     * Returns a new instance of this fragment for the given section number.
     * TODO: Switch to AutoFactory/@Provides rather than inline creation.
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
        MinimalBible.getApplication().inject(this); // Injection for Dagger goes here, not ctor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
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

    /**
     * Trigger the functionality to display a list of modules. Prompts user if downloading
     * from the internet is allowable.
     */
 	public void displayModules() {
		boolean dialogDisplayed = downloadPrefs.showedDownloadDialog().get();
		
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

    /**
     * Do the work of refreshing modules (download manager handles using cached copy vs. actual
     * refresh), and then displaying them when ready.
     */
	private void refreshModules() {
        // Check if the downloadManager has already refreshed everything
		List<Book> bookList = downloadManager.getBookList();
		if (bookList == null) {
            // downloadManager is in progress of refreshing
            downloadManager.getDownloadBus().register(this);
            refreshDialog = new ProgressDialog(getActivity());
            refreshDialog.setMessage("Refreshing available modules...");
            refreshDialog.setCancelable(false);
            refreshDialog.show();
        } else {
            displayBooks(bookList);
        }
	}

    /**
     * Used by GreenRobot for notifying us that the book refresh is complete
     */
    @SuppressWarnings("unused")
	public void onEventMainThread(EventBookList event) {
		if (refreshDialog != null) {
			refreshDialog.cancel();
		}
		displayBooks(event.getBookList());
	}

    /**
     * Do the hard work of creating the Adapter and displaying books.
     * @param bookList The (unfiltered) list of {link org.crosswire.jsword.Book}s to display
     */
    public void displayBooks(List<Book> bookList) {
        try {
            // TODO: Should the filter be applied earlier in the process?
            List<Book> displayList;

            BookCategory c = BookCategory.fromString(getArguments().getString(ARG_BOOK_CATEGORY));
            BookFilter f = FilterUtil.filterFromCategory(c);
            displayList = FilterUtil.applyFilter(bookList, f);
            Collections.sort(displayList, BookComparators.getInitialComparator());

            downloadsAvailable.setAdapter(new BookListAdapter(inflater, displayList));
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
            downloadPrefs.showedDownloadDialog().put(true);

			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Clicked ready to continue - allow downloading in the future
                downloadPrefs.hasEnabledDownload().put(true);

				// And warn them that it has been enabled in the future.
				Toast.makeText(getActivity(),
						"Downloading now enabled. Disable in settings.",
						Toast.LENGTH_SHORT).show();
				refreshModules();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				// Clicked to not download - Permanently disable downloading
				downloadPrefs.hasEnabledDownload().put(false);
				Toast.makeText(getActivity(),
						"Disabling downloading. Re-enable it in settings.",
						Toast.LENGTH_SHORT).show();
				refreshModules();
				break;
			}
		}
	}

}