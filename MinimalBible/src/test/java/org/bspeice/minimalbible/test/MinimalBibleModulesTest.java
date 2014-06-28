package org.bspeice.minimalbible.test;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.MinimalBibleModules;

import dagger.Module;

/**
 * Master module for MinimalBible
 */
@Module(
    injects = {
        MinimalBible.class
    },
    includes = {
            MinimalBibleModules.class
    }
)
public class MinimalBibleModulesTest {
}
