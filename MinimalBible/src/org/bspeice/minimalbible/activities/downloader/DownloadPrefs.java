package org.bspeice.minimalbible.activities.downloader;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Bradlee Speice on 5/8/2014.
 */
@SharedPref(value= SharedPref.Scope.UNIQUE)
public interface DownloadPrefs {

    @DefaultBoolean(false)
    boolean hasEnabledDownload();

    @DefaultBoolean(false)
    boolean showedDownloadDialog();

    @DefaultLong(0)
    long downloadRefreshedOn();
}