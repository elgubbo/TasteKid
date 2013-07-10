package com.elgubbo.tastekid;

import com.elgubbo.tastekid.interfaces.IResultsReceiver;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.widget.SearchView;

public class SearchQueryChangeListener implements
		SearchView.OnQueryTextListener {

	int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private SparseArray<String> lastSearches;

	public SearchQueryChangeListener(
			SectionsPagerAdapter mSectionsPagerAdapter, int position,
			ViewPager mViewPager) {
		this.mSectionsPagerAdapter = mSectionsPagerAdapter;
		this.position = position;
		this.mViewPager = mViewPager;
		this.lastSearches = new SparseArray<String>();
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (newText.length() > 2) {

		}
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		SectionFragment currentFragment = (SectionFragment) mSectionsPagerAdapter
				.getActiveFragment(mViewPager, mViewPager.getCurrentItem());
		this.position = currentFragment.position;
		if(lastSearches.get(position, "").equals(query))
			return false;
		if (query.trim().equalsIgnoreCase("")) {
			if (Configuration.DEVMODE)
				Log.d("TasteKid", "onQueryTextSubmit doing nothing");
			return false;
		}

		currentFragment.showLoadingBar();

		String type = TasteKidActivity.TYPE_ARRAY[position];
		
		if (Configuration.DEVMODE) {
			Log.d("TasteKid", "Position is:" + this.position);
			Log.d("TasteKid", "type is: " + type);
		}
		ResultManager.sendResultsForQueryTo((IResultsReceiver) currentFragment, query);

		lastSearches.append(position, query);
		return true;
	}

}
