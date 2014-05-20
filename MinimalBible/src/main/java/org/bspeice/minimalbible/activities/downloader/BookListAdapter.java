package org.bspeice.minimalbible.activities.downloader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.todddavies.components.progressbar.ProgressWheel;
import org.bspeice.minimalbible.R;
import org.crosswire.jsword.book.Book;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Adapter to inflate list_download_items.xml
 */
public class BookListAdapter extends BaseAdapter {
    private List<Book> bookList;

    private LayoutInflater inflater;

    public BookListAdapter(LayoutInflater inflater, List<Book> bookList) {
        this.bookList = bookList;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Book getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookItemHolder viewHolder;
        // Nasty Android issue - if you don't check the getTag(), Android will start recycling,
        // and you'll get some really strange issues
        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.list_download_items, null);
            viewHolder = new BookItemHolder(convertView);
        } else {
            viewHolder = (BookItemHolder) convertView.getTag();
        }

        viewHolder.bindHolder(position);
        return convertView;
    }

    public class BookItemHolder {

        @InjectView(R.id.download_txt_item_acronym) TextView acronym;
        @InjectView(R.id.txt_download_item_name) TextView itemName;
        @InjectView(R.id.download_ibtn_download) ImageButton isDownloaded;
        @InjectView(R.id.download_prg_download) ProgressWheel downloadProgress;

        public BookItemHolder(View v) {
            ButterKnife.inject(this, v);
        }

        public void bindHolder(int position) {
            Book b = BookListAdapter.this.getItem(position);
            acronym.setText(b.getInitials());
            itemName.setText(b.getName());
        }

        @OnClick(R.id.download_ibtn_download)
        public void onDownloadItem(View v) {
            Log.d("BookListAdapter", v.toString());
            isDownloaded.setVisibility(View.GONE);
            downloadProgress.setVisibility(View.VISIBLE);
            downloadProgress.setProgress(75); // Out of 360
        }
    }
}
