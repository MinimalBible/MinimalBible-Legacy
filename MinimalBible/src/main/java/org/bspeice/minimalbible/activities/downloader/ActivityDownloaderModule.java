package org.bspeice.minimalbible.activities.downloader;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.activities.downloader.manager.BookRefreshTask;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;

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
            BookRefreshTask.class
        }
)
public class ActivityDownloaderModule {

    /**
     * Provide a Singleton DownloadManager for injection
     * Note that we need to annotate Singleton here, only annotating on the
     * DownloadManager itself is not enough.
     * @return Global DownloadManager instance
     */
    @Provides @Singleton
    DownloadManager provideDownloadManager() {
        return new DownloadManager();
    }

    @Provides
    EventBus provideBus() {
        return new EventBus();
    }


    @Provides //@Singleton
    DownloadPrefs provideDownloadPrefs() {
        return Esperandro.getPreferences(DownloadPrefs.class, MinimalBible.getAppContext());
    }
}