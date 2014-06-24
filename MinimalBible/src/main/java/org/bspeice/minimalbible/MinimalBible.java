package org.bspeice.minimalbible;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.crosswire.jsword.book.sword.SwordBookPath;

import java.io.File;

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

    private String TAG = "MinimalBible";

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
        setJswordHome();
    }

    /**
     * Inject a Dagger object
     * @param o The object to be injected
     */
    public void inject(Object o) {
        getObjGraph().inject(o);
    }

    public ObjectGraph getObjGraph() {
        if (graph == null) {
            graph = ObjectGraph.create(MinimalBibleModules.class);
        }
        return graph;
    }

    public void plusObjGraph(Object... modules) {
        graph = graph.plus(modules);
    }

    /**
     * Notify jSword that it needs to store files in the Android internal directory
     * NOTE: Android will uninstall these files if you uninstall MinimalBible.
     */
    @SuppressWarnings("null")
    private void setJswordHome() {
        // We need to set the download directory for jSword to stick with
        // Android.
        String home = MinimalBible.getAppContext().getFilesDir().toString();
        Log.d(TAG, "Setting jsword.home to: " + home);
        System.setProperty("jsword.home", home);
        System.setProperty("sword.home", home);
        SwordBookPath.setDownloadDir(new File(home));
        Log.d(TAG, "Sword download path: " + SwordBookPath.getSwordDownloadDir());
    }
}
