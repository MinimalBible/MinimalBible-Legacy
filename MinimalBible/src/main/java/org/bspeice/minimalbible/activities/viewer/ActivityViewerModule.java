package org.bspeice.minimalbible.activities.viewer;

import dagger.Module;

/**
 * Created by bspeice on 6/18/14.
 */
@Module(
        injects = {
                BibleViewer.class,
                BookFragment.class
        }
)
public class ActivityViewerModule {
}
