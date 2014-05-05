package org.bspeice.minimalbible.activities.downloader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.crosswire.jsword.book.Book;

import java.util.List;

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
            itemView = BookItemView_.build(this.ctx);

        } else {
            itemView = (BookItemView) convertView;
        }

        itemView.bind(getItem(position));
        return itemView;
    }
}
