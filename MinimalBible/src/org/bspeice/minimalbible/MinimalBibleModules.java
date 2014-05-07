package org.bspeice.minimalbible;

import org.bspeice.minimalbible.activities.ActivityModules;

import dagger.Module;

/**
 * Modules for the global application
 */
@Module(
    injects = {
        MinimalBible.class
    },
    includes = {
        ActivityModules.class
    }
)
public class MinimalBibleModules {
}
