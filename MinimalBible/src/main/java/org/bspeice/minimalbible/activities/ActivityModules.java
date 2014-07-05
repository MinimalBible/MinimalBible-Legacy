package org.bspeice.minimalbible.activities;

import org.bspeice.minimalbible.activities.downloader.ActivityDownloaderModule;
import org.bspeice.minimalbible.activities.viewer.ActivityViewerModule;

import dagger.Module;

/**
 * Modules for all activities
 */
@Module(
    includes = {
        ActivityDownloaderModule.class,
        ActivityViewerModule.class
    }
)
public class ActivityModules {
}
