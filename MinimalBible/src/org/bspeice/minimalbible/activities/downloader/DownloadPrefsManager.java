package org.bspeice.minimalbible.activities.downloader;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import javax.inject.Singleton;

/**
 * Created by Bradlee Speice on 5/8/2014.
 */
@Singleton
public class DownloadPrefsManager {
    private final SharedPreferences prefs;

    public static final String DOWNLOAD_PREFS_FILE = "DOWNLOADER_PREFERENCES";

    public static final String KEY_DOWNLOAD_ENABLED = "HAS_ENABLED_DOWNLOAD";
    public static final String KEY_SHOWED_DOWNLOAD_DIALOG = "SHOWED_DOWNLOAD_DIALOG";
    public static final String KEY_DOWNLOAD_REFRESHED_ON = "DOWNLOAD_REFRESHED_ON";

    public DownloadPrefsManager(Context ctx) {
        prefs = ctx.getSharedPreferences(DOWNLOAD_PREFS_FILE, Context.MODE_PRIVATE);
    }

    public boolean getDownloadEnabled() {
        return prefs.getBoolean(KEY_DOWNLOAD_ENABLED, false);
    }

    public void setDownloadEnabled(boolean val) {
        prefs.edit().putBoolean(KEY_DOWNLOAD_ENABLED, val).commit();
    }

    public boolean getShowedDownloadDialog() {
        return prefs.getBoolean(KEY_SHOWED_DOWNLOAD_DIALOG, false);
    }

    public void setShowedDownloadDialog(boolean val) {
        prefs.edit().putBoolean(KEY_SHOWED_DOWNLOAD_DIALOG, val).commit();
    }

    public Date getDownloadRefreshedOn() {
        return new Date(prefs.getLong(KEY_DOWNLOAD_REFRESHED_ON, 0));
    }

    public void setDownloadRefreshedOn(Date d) {
        prefs.edit().putLong(KEY_DOWNLOAD_REFRESHED_ON, d.getTime()).commit();
    }

}
