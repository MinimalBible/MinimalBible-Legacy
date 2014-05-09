package org.bspeice.minimalbible;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

public class MinimalBible extends Application {

    private ObjectGraph graph;
	
	private static MinimalBible instance;
	
	public MinimalBible() {
		instance = this;		
	}
	
	public static Context getAppContext() {
		return instance;
	}

    public static MinimalBible getApplication(Context ctx) {
        return (MinimalBible)ctx.getApplicationContext();
    }

    public static MinimalBible getApplication() {
        return (MinimalBible)getAppContext();
    }

    @Override
    public void onCreate() {
        graph = ObjectGraph.create(new MinimalBibleModules());
        graph.inject(this);
    }

    public void inject(Object o) {
        graph.inject(o);
    }
}
