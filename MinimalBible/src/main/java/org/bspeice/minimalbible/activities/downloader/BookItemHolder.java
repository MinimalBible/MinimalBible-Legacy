package org.bspeice.minimalbible.activities.downloader;

import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.todddavies.components.progressbar.ProgressWheel;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadProgressEvent;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
* Created by bspeice on 5/20/14.
*/
public class BookItemHolder {

    // TODO: The holder should register and unregister itself for DownloadProgress events
    // so that we can display live updates.

    @InjectView(R.id.download_txt_item_acronym) TextView acronym;
    @InjectView(R.id.txt_download_item_name) TextView itemName;
    @InjectView(R.id.download_ibtn_download) ImageButton isDownloaded;
    @InjectView(R.id.download_prg_download) ProgressWheel downloadProgress;

    @Inject DownloadManager downloadManager;

    Book b;

    public BookItemHolder(View v, Book b) {
        ButterKnife.inject(this, v);
        MinimalBible.getApplication().inject(this);
        this.b = b;
    }

    public void bindHolder() {
        acronym.setText(b.getInitials());
        itemName.setText(b.getName());
        DownloadProgressEvent downloadProgressEvent = downloadManager.getInProgressDownloadProgress(b);
        if (downloadProgressEvent != null) {
            displayProgress((int) downloadProgressEvent.toCircular());
        }
        // TODO: Display a remove icon if the book has been downloaded.
    }

    @OnClick(R.id.download_ibtn_download)
    public void onDownloadItem(View v) {
        downloadManager.getRefreshBus().register(this);
        downloadManager.installBook(this.b);
    }

    public void onEventMainThread(DownloadProgressEvent event) {
        if (event.getB().equals(b)) {
            displayProgress((int) event.toCircular());
        }
    }

    /**
     * Display the current progress of this download
     * @param progress The progress out of 360 (degrees of a circle)
     */
    private void displayProgress(int progress) {


        if (progress == DownloadProgressEvent.PROGRESS_BEGINNING) {
            // Download starting
            RelativeLayout.LayoutParams acronymParams =
                    (RelativeLayout.LayoutParams)acronym.getLayoutParams();
            acronymParams.addRule(RelativeLayout.LEFT_OF, downloadProgress.getId());

            RelativeLayout.LayoutParams nameParams =
                    (RelativeLayout.LayoutParams)itemName.getLayoutParams();
            nameParams.addRule(RelativeLayout.LEFT_OF, downloadProgress.getId());

            isDownloaded.setVisibility(View.GONE);
            downloadProgress.setVisibility(View.VISIBLE);

            downloadProgress.spin();
        } else if (progress < 360) {
            // Download in progress
            RelativeLayout.LayoutParams acronymParams =
                    (RelativeLayout.LayoutParams)acronym.getLayoutParams();
            acronymParams.addRule(RelativeLayout.LEFT_OF, downloadProgress.getId());

            RelativeLayout.LayoutParams nameParams =
                    (RelativeLayout.LayoutParams)itemName.getLayoutParams();
            nameParams.addRule(RelativeLayout.LEFT_OF, downloadProgress.getId());

            isDownloaded.setVisibility(View.GONE);
            downloadProgress.setVisibility(View.VISIBLE);

            downloadProgress.setProgress(progress);
        } else {
            // Download complete
            RelativeLayout.LayoutParams acronymParams =
                    (RelativeLayout.LayoutParams)acronym.getLayoutParams();
            acronymParams.addRule(RelativeLayout.LEFT_OF, isDownloaded.getId());

            RelativeLayout.LayoutParams nameParams =
                    (RelativeLayout.LayoutParams)itemName.getLayoutParams();
            nameParams.addRule(RelativeLayout.LEFT_OF, isDownloaded.getId());

            isDownloaded.setVisibility(View.VISIBLE);
            downloadProgress.setVisibility(View.GONE);
        }
    }
}
