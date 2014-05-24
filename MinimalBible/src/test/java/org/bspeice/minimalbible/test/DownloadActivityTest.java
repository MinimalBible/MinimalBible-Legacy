package org.bspeice.minimalbible.test;

import android.test.InstrumentationTestCase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.MinimalBibleModules;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activities.downloader.BookItemHolder;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadProgressEvent;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.Installer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Module;

/**
 * Tests for the Download activity
 */
public class DownloadActivityTest extends InstrumentationTestCase {

    @Module(addsTo = MinimalBibleModules.class,
            injects = DownloadActivityTest.class)
    public static class DownloadActivityTestModule {}

    @Inject
    DownloadManager dm;

    public void setUp() {
        MinimalBible.getApplication().getObjGraph()
                .plus(DownloadActivityTestModule.class).inject(this);
    }

    public void testBasicAssertion() {
        assertEquals(true, true);
    }

    /**
     * When we start a download, make sure a progress event of 0 is triggered.
     */
    public void testInitialProgressEventOnDownload() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        Installer i = (Installer) dm.getInstallers().values().toArray()[0];
        Book testBook = i.getBooks().get(0);
        View dummyView = LayoutInflater.from(MinimalBible.getApplication())
                .inflate(R.layout.list_download_items, null);
        BookItemHolder holder = new BookItemHolder(dummyView, testBook);

        dm.getDownloadBus().register(new Object() {
            public void onEvent(DownloadProgressEvent event) {
                Log.d("testInitial", Integer.toString(event.getProgress()));
                if (event.getProgress() == 0) {
                    signal.countDown();
                }
            }
        });
        holder.onDownloadItem(dummyView);

        signal.await(10, TimeUnit.SECONDS);
        if (signal.getCount() != 0) {
            fail("Event did not trigger!");
        }
    }

}
