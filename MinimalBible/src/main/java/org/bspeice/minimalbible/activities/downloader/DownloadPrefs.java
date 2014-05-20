package org.bspeice.minimalbible.activities.downloader;

import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by bspeice on 5/19/14.
 */
@SharedPreferences(name="DownloadPrefs")
public interface DownloadPrefs {

    boolean hasEnabledDownload();
    void hasEnabledDownload(boolean hasEnabledDownload);

    boolean hasShownDownloadDialog();
    void hasShownDownloadDialog(boolean hasShownDownloadDialog);

    long downloadRefreshedOn();
    void downloadRefreshedOn(long downloadRefreshedOn);

}
