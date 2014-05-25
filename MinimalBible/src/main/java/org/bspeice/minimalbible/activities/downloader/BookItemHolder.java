package org.bspeice.minimalbible.activities.downloader;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.todddavies.components.progressbar.ProgressWheel;

import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activities.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activities.downloader.manager.DLProgressEvent;
import org.bspeice.minimalbible.activities.downloader.manager.DownloadManager;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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
    @Inject BookDownloadManager bookDownloadManager;

    Book b;

    public BookItemHolder(View v, Book b) {
        ButterKnife.inject(this, v);
        MinimalBible.getApplication().inject(this);
        this.b = b;
    }

    public void bindHolder() {
        acronym.setText(b.getInitials());
        itemName.setText(b.getName());
        DLProgressEvent dlProgressEvent = bookDownloadManager.getInProgressDownloadProgress(b);
        if (dlProgressEvent != null) {
            displayProgress((int) dlProgressEvent.toCircular());
        }
        // TODO: Display a remove icon if the book has been downloaded.
    }

    @OnClick(R.id.download_ibtn_download)
    public void onDownloadItem(View v) {
        downloadManager.getDownloadBus().register(this);
        bookDownloadManager.installBook(this.b);
    }

    public void onEventMainThread(DLProgressEvent event) {
        if (event.getB().getName().equals(b.getName())) {
            displayProgress((int) event.toCircular());
        }
    }

    /**
     * Display the current progress of this download
     * @param progress The progress out of 360 (degrees of a circle)
     */
    private void displayProgress(int progress) {


        if (progress == DLProgressEvent.PROGRESS_BEGINNING) {
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

            downloadProgress.stopSpinning();
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
