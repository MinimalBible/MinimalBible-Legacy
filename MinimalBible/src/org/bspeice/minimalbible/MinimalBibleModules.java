package org.bspeice.minimalbible;

import org.bspeice.minimalbible.activities.ActivityModules;

import dagger.Module;

/**
 * Master module for MinimalBible
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
