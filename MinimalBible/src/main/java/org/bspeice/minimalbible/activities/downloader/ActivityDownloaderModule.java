package org.bspeice.minimalbible.activities.downloader;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.activities.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.BookDownloadThread;
import org.bspeice.minimalbible.activities.downloader.manager.BookRefreshTask;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.RefreshManager;
import org.crosswire.common.progress.JobManager;

import java.sql.Ref;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;
import de.greenrobot.event.EventBus;

/**
 * Module mappings for the classes under the Download Activity
 */
@Module(
        injects = {
            BookListFragment.class,
            DownloadManager.class,
            BookRefreshTask.class,
            BookItemHolder.class,
            BookDownloadManager.class,
            BookDownloadThread.class,
            RefreshManager.class
        }
)
public class ActivityDownloaderModule {

    @Provides
    EventBus provideBus() {
        return new EventBus();
    }

    @Provides //@Singleton
    DownloadPrefs provideDownloadPrefs() {
        return Esperandro.getPreferences(DownloadPrefs.class, MinimalBible.getAppContext());
    }
}
