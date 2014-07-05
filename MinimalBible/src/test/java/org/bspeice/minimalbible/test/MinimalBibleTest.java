package org.bspeice.minimalbible.test;

import android.content.Context;

import org.bspeice.minimalbible.MinimalBible;

import dagger.ObjectGraph;

public class MinimalBibleTest extends MinimalBible {

    /**
     * The graph used by Dagger to track dependencies
     */
    private ObjectGraph graph;

    /**
     * A singleton reference to the Application currently being run.
     * Used mostly so we have a fixed point to get the App Context from
     */
	private static MinimalBibleTest instance;

    private String TAG = "MinimalBibleTest";

    /**
     * Create the application, and persist the application Context
     */
	public MinimalBibleTest() {
		instance = this;		
	}

    /**
     * Get the Application Context. Please note, all attempts to get the App Context should come
     * through here, and please be sure that the Application won't satisfy what you need.
     * @return The Application Context
     */
	public static Context getAppContext() {
        return instance;
    }

    /**
     * Get the Application, rather than just the Application Context. You likely should be using
     * this, rather than {@link #getAppContext()}
     * @return The MinimalBible {@link android.app.Application} object
     */
    public static MinimalBibleTest getApplication() {
        return instance;
    }

    /**
     * Create the {@link android.app.Application}. Responsible for building and
     * holding on to the master ObjectGraph.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: Is this necessary?
        inject(this);
    }

    /**
     * Inject a Dagger object
     * @param o The object to be injected
     */
    @Override
    public void inject(Object o) {
        getObjGraph().inject(o);
    }

    public ObjectGraph getObjGraph() {
        if (graph == null) {
            graph = ObjectGraph.create(MinimalBibleModulesTest.class);
        }
        return graph;
    }
}
