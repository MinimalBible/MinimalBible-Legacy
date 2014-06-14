package org.bspeice.minimalbible.test;

import android.test.InstrumentationTestCase;
import android.util.Log;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.MinimalBibleModules;
import org.bspeice.minimalbible.activities.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.DLProgressEvent;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.InstalledManager;
import org.bspeice.minimalbible.activities.downloader.manager.RefreshManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.passage.NoSuchKeyException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import dagger.Module;
import dagger.ObjectGraph;
import rx.Observable;

import static com.jayway.awaitility.Awaitility.await;

/**
 * Tests for the Download activity
 */
public class DownloadActivityTest extends InstrumentationTestCase {

    @Module(addsTo = MinimalBibleModules.class,
            injects = DownloadActivityTest.class)
    public static class DownloadActivityTestModule {}

    @Inject DownloadManager dm;
    @Inject InstalledManager im;
    @Inject RefreshManager rm;
    @Inject BookDownloadManager bdm;

    public void setUp() {
        MinimalBible application = MinimalBible.getApplication();
        ObjectGraph graph = application.getObjGraph();
        ObjectGraph plusGraph = graph.plus(DownloadActivityTestModule.class);
        plusGraph.inject(this);
    }

    public void testBasicAssertion() {
        assertEquals(true, true);
    }

    /**
     * Test that we can successfully download and remove a book
     */
    public void testInstallAndRemoveBook() {
        // Install a book
        Installer i = (Installer) dm.getInstallers().values().toArray()[0];
        final Book testBook = i.getBooks().get(0);
        bdm.installBook(testBook);
        await().atMost(30, TimeUnit.SECONDS)
                .until(new Callable<Boolean>() {
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
        await().atMost(10, TimeUnit.SECONDS)
                .until(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !Books.installed().getBooks().contains(testBook);
                    }
                });
    }

}
