package org.bspeice.minimalbible;

import android.app.Application;
import android.content.Context;

public class MinimalBible extends Application {
	
	private static MinimalBible instance;
	
	public MinimalBible() {
		instance = this;		
	}
	
	public static Context getAppContext() {
		return instance;
	}

}
