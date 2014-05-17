package org.bspeice.minimalbible.activities.downloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.IconButton;
import android.widget.IconTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify;

import org.bspeice.minimalbible.R;
import org.crosswire.jsword.book.Book;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Adapter to inflate list_download_items.xml
 */
public class BookListAdapter extends BaseAdapter {
    private List<Book> bookList;

    private Context ctx;

    public BookListAdapter(Context context, List<Book> bookList) {
        this.bookList = bookList;
        this.ctx = context;
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
        BookItemView itemView;
        if (convertView == null) {
            itemView = new BookItemView(this.ctx);
        } else {
            itemView = (BookItemView) convertView;
        }

        itemView.bind(getItem(position));
        return itemView;
    }

    public class BookItemView extends RelativeLayout {

        @InjectView(R.id.img_download_icon) ImageView downloadIcon;
        @InjectView(R.id.download_txt_item_acronym) TextView acronym;
        @InjectView(R.id.txt_download_item_name) TextView itemName;
        @InjectView(R.id.download_ibtn_download) ImageButton isDownloaded;

        public BookItemView (Context ctx) {
            super(ctx);
            View v = LayoutInflater.from(ctx).inflate(R.layout.list_download_items, this);
            ButterKnife.inject(this, v);
        }

        public void bind(Book b) {
            acronym.setText(b.getInitials());
            itemName.setText(b.getName());
        }
    }
}
