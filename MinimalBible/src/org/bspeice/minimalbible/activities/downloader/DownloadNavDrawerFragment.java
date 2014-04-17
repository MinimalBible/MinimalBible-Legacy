package org.bspeice.minimalbible.activities.downloader;

import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activities.BaseNavigationDrawerFragment;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DownloadNavDrawerFragment extends BaseNavigationDrawerFragment {
	
	private final BookCategory[] displayCategories = {BookCategory.BIBLE, BookCategory.COMMENTARY,
			BookCategory.DICTIONARY, BookCategory.IMAGES, BookCategory.MAPS
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDrawerListView = (ListView) inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false);
		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}
				});
		
		String[] sCategories = new String[displayCategories.length];
		for (int i = 0; i < displayCategories.length; i++) {
			sCategories[i] = displayCategories[i].toString();
		}

		mDrawerListView.setAdapter(new ArrayAdapter<String>(getActionBar()
				.getThemedContext(), android.R.layout.simple_list_item_1,
				android.R.id.text1, sCategories));
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		return mDrawerListView;
	}

}
