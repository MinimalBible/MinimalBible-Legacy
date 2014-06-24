package org.bspeice.minimalbible.test;

import android.test.InstrumentationTestCase;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.MinimalBibleModules;
import org.bspeice.minimalbible.activities.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.InstalledManager;
import org.bspeice.minimalbible.activities.downloader.manager.RefreshManager;

import javax.inject.Inject;

import dagger.Module;
import dagger.ObjectGraph;

import static com.jayway.awaitility.Awaitility.await;

/**
 * Tests for the Download activity
 */
public class MinimalBibleTest extends InstrumentationTestCase {

    @Module(addsTo = MinimalBibleModules.class,
            injects = MinimalBibleTest.class)
    public static class DownloadActivityTestModule {}

    public void setUp() {
        MinimalBible application = MinimalBible.getApplication();
        ObjectGraph graph = application.getObjGraph();
        ObjectGraph plusGraph = graph.plus(DownloadActivityTestModule.class);
        plusGraph.inject(this);
    }

    /**
     * If we've made it to the actual test, injection seems to be working correctly.
     */
    public void testBasicInjection() {
        assertEquals(true, true);
    }

}
