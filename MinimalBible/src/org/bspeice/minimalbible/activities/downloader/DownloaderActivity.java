package org.bspeice.minimalbible.activities.downloader;

import org.bspeice.minimalbible.MinimalBibleConstants;
import org.bspeice.minimalbible.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class DownloaderActivity extends Activity {
	
	private static final String KEY_DOWNLOAD_ENABLED = "HAS_ENABLED_DOWNLOAD";
	public static final String KEY_PERM_DISABLE_DOWNLOAD = "PERMANENTLY_DISABLE_DOWNLOAD";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloader);
		
		// Display a warning about internet connectivity
		displayInternetWarning();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.downloader, menu);
		return true;
	}
	
	private void displayInternetWarning() {
		SharedPreferences prefs = getSharedPreferences(MinimalBibleConstants.DOWNLOAD_PREFS_FILE, MODE_PRIVATE);
		
		// If downloading has not been enabled, or user has permanently disabled downloading, WARN THEM!
		if (!prefs.getBoolean(KEY_DOWNLOAD_ENABLED, false) || prefs.getBoolean(KEY_PERM_DISABLE_DOWNLOAD, false)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			DownloadDialogListener dialogListener = new DownloadDialogListener();
			builder.setMessage("About to contact servers to download content. Continue?")
				.setPositiveButton("Yes", dialogListener).setNegativeButton("No", dialogListener)
				.setCancelable(false).show();
		}
	}
	
	private void downloadModules() {
		
	}
	
	private class DownloadDialogListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which){
			case DialogInterface.BUTTON_POSITIVE:
				// Clicked ready to continue - allow downloading in the future
				SharedPreferences prefs = getSharedPreferences(MinimalBibleConstants.DOWNLOAD_PREFS_FILE, MODE_PRIVATE);
				prefs.edit().putBoolean(KEY_DOWNLOAD_ENABLED, true).commit();
				
				// And warn them that it has been enabled in the future.
				Toast.makeText(DownloaderActivity.this,
						"Downloading now enabled. Disable in settings.", Toast.LENGTH_SHORT).show();
				downloadModules();
				break;
				
			case DialogInterface.BUTTON_NEGATIVE:
				// Not going to continue, still show what has
				// already been downloaded.
				break;
			}
			
		}
	}

}
