package org.bspeice.minimalbible.test.activities.downloader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;
import android.view.ContextThemeWrapper;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.MinimalBibleModules;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activities.downloader.ActivityDownloaderModule;
import org.bspeice.minimalbible.activities.downloader.BookListFragment;
import org.bspeice.minimalbible.activities.downloader.DownloadActivity;
import org.bspeice.minimalbible.activities.downloader.DownloadPrefs;
import org.crosswire.jsword.book.BookCategory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import de.devland.esperandro.Esperandro;

import static com.jayway.awaitility.Awaitility.await;

/**
 * Created by bspeice on 6/23/14.
 */
public class BookListFragmentTest extends ActivityInstrumentationTestCase2<DownloadActivity> {

    @Module(injects = TestDialogDisplayedIfFirstTimeFragment.class,
            addsTo = ActivityDownloaderModule.class
    )
    protected static class BookListFragmentTestModule{}

    public BookListFragmentTest() {
        super(DownloadActivity.class);
    }

    FragmentManager mFragmentManager;

    public void setUp() throws Exception {
        super.setUp();

        mFragmentManager = getActivity().getSupportFragmentManager();
        assertNotNull(mFragmentManager);
    }


    public <F extends Fragment> F startFragment(F fragment) {
        try {
            mFragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final CountDownLatch signal = new CountDownLatch(1);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mFragmentManager.executePendingTransactions();
                signal.countDown();
            }
        });
        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (F)(mFragmentManager.findFragmentById(android.R.id.content));
    }


    protected class TestDialogDisplayedIfFirstTimeFragment extends BookListFragment {
        /**
         * If the refresh dialog is blank after calling display, it must be showing the warning
         * @return Whether the warning dialog is showing
         */
        public boolean callDisplayModules(DownloadPrefs prefs) {
            // Inject the new preferences...
            this.downloadPrefs = prefs;
            displayModules();
            return (refreshDialog == null);
        }

        public void setArgs(BookCategory c) {
            Bundle args = new Bundle();
            args.putString(ARG_BOOK_CATEGORY, c.toString());
            this.setArguments(args);
        }
    }

    public void testDialogDisplayedIfFirstTime() {
        /*
         SharedPreferences prefs = getActivity()
                .getSharedPreferences("DownloadPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("hasShownDownloadDialog", false);
        */
        ((MinimalBible)getActivity().getApplication()).plusObjGraph(BookListFragmentTestModule.class);
        TestDialogDisplayedIfFirstTimeFragment f = new TestDialogDisplayedIfFirstTimeFragment();
        f.setArgs(BookCategory.BIBLE);
        startFragment(f);

        assertNotNull(f);
        assertTrue(f.callDisplayModules(Esperandro.getPreferences(DownloadPrefs.class, getActivity())));
    }
}
