package org.bspeice.minimalbible.activities.viewer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activities.BaseFragment;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookFragment extends BaseFragment {

    @Inject BookManager bookManager;

    @InjectView(R.id.section_label)
    TextView sectionLabel;

    private static final String ARG_BOOK_NAME = "book_name";

    private Book mBook;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static BookFragment newInstance(String bookName) {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_NAME, bookName);
        fragment.setArguments(args);
        return fragment;
    }

    public BookFragment() {
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        MinimalBible.getApplication().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viewer_main, container,
                false);
        ButterKnife.inject(this, rootView);

        // TODO: Load initial text from SharedPreferences

        // And due to Observable async, we can kick off fetching the actual book asynchronously!
        bookManager.getInstalledBooks()
                .first(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        String mBookName = getArguments().getString(ARG_BOOK_NAME);
                        return book.getName().equals(mBookName);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        BookFragment.this.mBook = book;
                        displayBook(book);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("BookFragment", "No books installed?");
                    }
                });

        return rootView;
    }

    // TODO: Remove?
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void displayBook(Book b) {
        Log.d("BookFragment", b.getName());
        ((BibleViewer)getActivity()).setActionBarTitle(b.getInitials());
        sectionLabel.setText(b.getName());
    }
}
