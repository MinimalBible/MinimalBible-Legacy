package org.bspeice.minimalbible.test;

import android.content.Context;

import org.bspeice.minimalbible.Injectable;

import dagger.ObjectGraph;

/**
 * Created by bspeice on 6/27/14.
 */
public abstract class MinimalBibleTestMockable extends MinimalBibleTest implements Injectable {

    private ObjectGraph mObjectGraph;

    public MinimalBibleTestMockable(Context context) {
        super(context);
        mObjectGraph = ObjectGraph.create(getModules());
    }

    @Override
    public abstract Object[] getModules();

    @Override
    public void inject(Object o) {
        mObjectGraph.inject(o);
    }
}
