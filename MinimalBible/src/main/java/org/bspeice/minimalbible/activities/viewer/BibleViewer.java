package org.bspeice.minimalbible.activities.viewer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activities.BaseActivity;
import org.bspeice.minimalbible.activities.BaseNavigationDrawerFragment;
import org.bspeice.minimalbible.activities.downloader.DownloadActivity;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class BibleViewer extends BaseActivity implements
		BaseNavigationDrawerFragment.NavigationDrawerCallbacks {

    @Inject BookManager bookManager;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private ViewerNavDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        MinimalBible.getApplication().inject(this);

        // If no books are installed, we need to download one first.
        int count = bookManager.getInstalledBooks()
                .count()
                .toBlocking()
                .last();
        if (count <= 0) {
            Intent i = new Intent(this, DownloadActivity.class);
            startActivityForResult(i, 0);
            finish();
        } else {
            bookManager.getInstalledBooks()
                    .first()
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Book>() {
                        @Override
                        public void call(Book book) {
                            Log.d("BibleViewer", "Subscribed to display book: " + book.getName());
                            displayMainBook(book);
                        }
                    });
        }

		setContentView(R.layout.activity_bible_viewer);

		mNavigationDrawerFragment = (ViewerNavDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// Handle a navigation movement
	}

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        mTitle = title;
        actionBar.setTitle(title);
    }

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_downloads) {
			startActivity(new Intent(this, DownloadActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

    private void displayMainBook(Book b) {
        Log.d("BibleViewer", "Initializing main book: " + b.getName());
        Log.d("MainThread?", Boolean.toString(Looper.myLooper() == Looper.getMainLooper()));
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = BookFragment.newInstance(b.getName());
        fragmentManager.beginTransaction()
                .replace(R.id.container, f)
                .commit();
    }
}
