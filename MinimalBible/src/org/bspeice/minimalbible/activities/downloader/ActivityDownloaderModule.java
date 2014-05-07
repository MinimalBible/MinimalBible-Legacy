package org.bspeice.minimalbible.activities.downloader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * Module mappings for the classes under the Download Activity
 */
@Module(
        injects = {
            BookListFragment.class,
            DownloadManager.class
        }
)
public class ActivityDownloaderModule {

    @Provides @Singleton
    DownloadManager provideDownloadManager() {
        return new DownloadManager();
    }

    @Provides
    EventBus provideBus() {
        return new EventBus();
    }
}
