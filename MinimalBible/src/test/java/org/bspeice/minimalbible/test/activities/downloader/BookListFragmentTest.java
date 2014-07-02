package org.bspeice.minimalbible.test.activities.downloader;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.test.ActivityUnitTestCase;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.activities.downloader.BookListFragment;
import org.bspeice.minimalbible.activities.downloader.DownloadActivity;
import org.bspeice.minimalbible.activities.downloader.DownloadPrefs;
import org.bspeice.minimalbible.test.MinimalBibleModulesTest;
import org.bspeice.minimalbible.test.MinimalBibleTest;
import org.bspeice.minimalbible.test.MinimalBibleTestMockable;
import org.crosswire.jsword.book.BookCategory;

import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;

/**
 * Created by bspeice on 6/23/14.
 */
public class BookListFragmentTest extends ActivityUnitTestCase<DownloadActivity> {
    private static Class activityUnderTest = DownloadActivity.class;

    @Module(injects = TestDialogDisplayedIfFirstTimeFragment.class,
            addsTo = MinimalBibleModulesTest.class,
            overrides = true
    )
    protected static class BookListFragmentTestModule{
        @Provides
        DownloadPrefs providePrefs() {
            return Esperandro.getPreferences(DownloadPrefs.class,
                    MinimalBible.getApplication());
        }
    }

    public BookListFragmentTest() {
        super(activityUnderTest);
    }

    @Inject DownloadPrefs downloadPrefs;
    FragmentManager mFragmentManager;

    private Application mApplication;
    private Context mContext;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Set 'dexmaker.dexcache' system property, otherwise sometimes it is null and test will crash
        // System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        mContext = new ContextWrapper(getInstrumentation().getTargetContext()) {
            @Override
            public Context getApplicationContext() {
                return mApplication;
            }
        };

        mApplication = new MinimalBibleTestMockable(mContext) {
            @Override public Object[] getModules() {
                return new Object[]{new BookListFragmentTestModule()};
            }
        };

        setApplication(mApplication);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
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

    public void testApplicationReplacementWorks() {
        setActivityContext(mContext);
        startActivity(new Intent(mContext, activityUnderTest), null, null);

        assertTrue(getActivity().getApplicationContext() instanceof MinimalBibleTestMockable);
    }

    protected class TestDialogDisplayedIfFirstTimeFragment extends BookListFragment {
        /**
         * If the refresh dialog is blank after calling display, it must be showing the warning
         * @return Whether the warning dialog is showing
         */
        public boolean callDisplayModules() {
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
        setActivityContext(mContext);
        startActivity(new Intent(mContext, activityUnderTest), null, null);

        TestDialogDisplayedIfFirstTimeFragment f = new TestDialogDisplayedIfFirstTimeFragment();
        f.setArgs(BookCategory.BIBLE);
        startFragment(f);

        assertNotNull(f);

        downloadPrefs.hasShownDownloadDialog(false);
        assertTrue(f.callDisplayModules());
    }

    public void testRefreshDisplayedAfterFirstTime() {
        setActivityContext(mContext);
        startActivity(new Intent(mContext, activityUnderTest), null, null);

        TestDialogDisplayedIfFirstTimeFragment f = new TestDialogDisplayedIfFirstTimeFragment();
        f.setArgs(BookCategory.BIBLE);
        startFragment(f);

        assertNotNull(f);

        downloadPrefs.hasShownDownloadDialog(true);
        assertFalse(f.callDisplayModules());
    }
}
