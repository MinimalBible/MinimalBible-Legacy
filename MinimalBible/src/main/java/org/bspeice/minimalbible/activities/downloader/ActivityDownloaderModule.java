package org.bspeice.minimalbible.activities.downloader;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.activities.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.BookDownloadThread;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.InstalledManager;
import org.bspeice.minimalbible.activities.downloader.manager.RefreshManager;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;

/**
 * Module mappings for the classes under the Download Activity
 */
@Module(
        injects = {
            BookListFragment.class,
            DownloadManager.class,
            BookItemHolder.class,
            BookDownloadManager.class,
            BookDownloadThread.class,
            RefreshManager.class,
            InstalledManager.class
        }
)
public class ActivityDownloaderModule {

    @Provides //@Singleton
    DownloadPrefs provideDownloadPrefs() {
        return Esperandro.getPreferences(DownloadPrefs.class, MinimalBible.getAppContext());
    }
}
