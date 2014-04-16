package org.bspeice.minimalbible.activities;

import org.bspeice.minimalbible.R;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class BaseActivity extends ActionBarActivity {

	// BaseActivity to take care of some stuff like setting the action bar color

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Only set the tint if the device is running KitKat or above
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(getResources().getColor(
					R.color.statusbar));
		}
	}

}
