package org.bspeice.minimalbible.activities.downloader;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Renamed while waiting for https://github.com/square/dagger/issues/410 to get resolved.
 * Once the issue is fixed, this should go back to being DownloadPrefs
 */
@SharedPref(value= SharedPref.Scope.UNIQUE)
public interface _DownloadPrefs {

    @DefaultBoolean(false)
    boolean hasEnabledDownload();

    @DefaultBoolean(false)
    boolean showedDownloadDialog();

    @DefaultLong(0)
    long downloadRefreshedOn();
}