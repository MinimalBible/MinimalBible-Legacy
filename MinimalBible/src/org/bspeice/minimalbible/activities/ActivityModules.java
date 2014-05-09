package org.bspeice.minimalbible.activities;

import org.bspeice.minimalbible.activities.downloader.manager.ActivityDownloaderModule;

import dagger.Module;

/**
 * Modules for all activities
 */
@Module(
    includes = {
        ActivityDownloaderModule.class
    }
)
public class ActivityModules {
}
