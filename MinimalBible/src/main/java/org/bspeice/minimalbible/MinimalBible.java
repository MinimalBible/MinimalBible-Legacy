package org.bspeice.minimalbible;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

public class MinimalBible extends Application {

    /**
     * The graph used by Dagger to track dependencies
     */
    private ObjectGraph graph;

    /**
     * A singleton reference to the Application currently being run.
     * Used mostly so we have a fixed point to get the App Context from
     */
	private static MinimalBible instance;

    /**
     * Create the application, and persist the application Context
     */
	public MinimalBible() {
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
    public static MinimalBible getApplication() {
        return (MinimalBible)getAppContext();
    }

    /**
     * Create the {@link android.app.Application}. Responsible for building and
     * holding on to the master ObjectGraph.
     */
    @Override
    public void onCreate() {
        //TODO: Is this necessary?
        inject(this);
    }

    /**
     * Inject a Dagger object
     * @param o The object to be injected
     */
    public void inject(Object o) {
        if (graph == null) {
            graph = ObjectGraph.create(MinimalBibleModules.class);
        }
        graph.inject(o);
    }

    public ObjectGraph getObjGraph() { return graph; }
}
