package org.bspeice.minimalbible.activities.downloader;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.bspeice.minimalbible.R;
import org.crosswire.jsword.book.Book;

@EViewGroup(R.layout.list_download_items)
public class BookItemView extends RelativeLayout {

    @ViewById (R.id.img_download_icon) ImageView downloadIcon;
    @ViewById(R.id.txt_download_item_name) TextView itemName;
    @ViewById(R.id.img_download_index_downloaded) ImageView isIndexedDownloaded;
    @ViewById(R.id.img_download_item_downloaded) ImageView isDownloaded;

    public BookItemView (Context ctx) {
        super(ctx);
    }

    public void bind(Book b) {
        itemName.setText(b.getName());
    }
}
