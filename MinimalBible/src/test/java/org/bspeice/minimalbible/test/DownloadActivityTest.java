package org.bspeice.minimalbible.test;

import android.test.InstrumentationTestCase;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.MinimalBibleModules;
import org.bspeice.minimalbible.activities.downloader.manager.BookDownloadThread;
import org.bspeice.minimalbible.activities.downloader.manager.DLProgressEvent;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.InstalledManager;
import org.bspeice.minimalbible.activities.downloader.manager.RefreshManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.passage.NoSuchKeyException;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Module;
import dagger.ObjectGraph;

import static com.jayway.awaitility.Awaitility.*;

/**
 * Tests for the Download activity
 */
public class DownloadActivityTest extends InstrumentationTestCase {

    @Module(addsTo = MinimalBibleModules.class,
            injects = DownloadActivityTest.class)
    public static class DownloadActivityTestModule {}

    @Inject DownloadManager dm;
    @Inject InstalledManager im;
    @Inject Provider<BookDownloadThread> bookDownloadThreadProvider;
    @Inject RefreshManager rm;

    public void setUp() {
        MinimalBible application = MinimalBible.getApplication();
        ObjectGraph graph = application.getObjGraph();
        graph.plus(DownloadActivityTestModule.class).inject(this);
    }

    public void testBasicAssertion() {
        assertEquals(true, true);
    }

    /**
     * When we start a download, make sure a progress event of 0 is triggered.
     */
    public void testInitialProgressEventOnDownload() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        // Need to make sure we've refreshed the refreshmanager first
        Installer i = (Installer) dm.getInstallers().values().toArray()[0];
        final Book testBook = i.getBooks().get(0);
        await().atMost(30, TimeUnit.SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return rm.installerFromBook(testBook) != null;
            }
        });

        // And wait for the actual download
        dm.getDownloadBus().register(new Object() {
            public void onEvent(DLProgressEvent event) {
                if (event.getProgress() == 0) {
                    signal.countDown();
                }
            }
        });

        BookDownloadThread thread = bookDownloadThreadProvider.get();
        thread.downloadBook(testBook);

        signal.await(10, TimeUnit.SECONDS);
        if (signal.getCount() != 0) {
            fail("Event did not trigger!");
        }
    }

    /**
     * Test that we can successfully download and remove a book
     */
    public void testInstallAndRemoveBook() {
        // Install a book
        Installer i = (Installer) dm.getInstallers().values().toArray()[0];
        final Book testBook = i.getBooks().get(0);
        await().atMost(30, TimeUnit.SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return Books.installed().getBooks().contains(testBook);
            }
        });

        // Validate that we can actually do something with the book
        // TODO: Validate that the book exists on the filesystem too
        try {
            assertNotNull(testBook.getRawText(testBook.getKey("Gen 1:1")));
        } catch (BookException e) {
            fail(e.getMessage());
        } catch (NoSuchKeyException e) {
            fail(e.getMessage());
        }

        // Remove the book and make sure it's gone
        // TODO: Validate that the book is off the filesystem
        im.removeBook(testBook);
        assertFalse(Books.installed().getBooks().contains(testBook));
    }

}
