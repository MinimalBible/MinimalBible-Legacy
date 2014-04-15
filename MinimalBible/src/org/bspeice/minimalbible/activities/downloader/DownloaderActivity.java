package org.bspeice.minimalbible.activities.downloader;

import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.R.layout;
import org.bspeice.minimalbible.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;

public class DownloaderActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloader);
		
		displayInternetWarning();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.downloader, menu);
		return true;
	}
	
	private void displayInternetWarning() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		DownloadDialogListener dialogListener = new DownloadDialogListener();
		builder.setMessage("About to contact servers to download content. Continue?")
			.setPositiveButton("Yes", dialogListener).setNegativeButton("No", dialogListener)
			.setCancelable(false).show();
	}
	
	private void downloadModules() {
		
	}
	
	private class DownloadDialogListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which){
			case DialogInterface.BUTTON_POSITIVE:
				// Clicked ready to continue
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
