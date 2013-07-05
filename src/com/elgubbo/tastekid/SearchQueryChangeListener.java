package com.elgubbo.tastekid;

import com.elgubbo.tastekid.api.APIWrapper;
import com.elgubbo.tastekid.interfaces.IQueryCompleteListener;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.widget.SearchView;

public class SearchQueryChangeListener implements
		SearchView.OnQueryTextListener {

	int position;

	// this is temporary TODO add persistent data storage
	SparseArray<String> lastSearches;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

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
		// check if the current query is equal to the last query, don't to
		// anything if it is
		if(lastSearches.get(position, "").equals(query))
			return false;
		if (query.trim().equalsIgnoreCase("")) {
			if (Configuration.DEVMODE)
				Log.d("TasteKid", "onQueryTextSubmit doing nothing");
			return false;
		}

		currentFragment.showLoadingBar();

		String type = null;
		switch (this.position) {
		case TasteKidActivity.POSITION_ALL:
			break;
		case TasteKidActivity.POSITION_BOOK:
			type = "book";
			break;
		case TasteKidActivity.POSITION_GAME:
			type = "game";
			break;
		case TasteKidActivity.POSITION_MOVIE:
			type = "movie";
			break;
		case TasteKidActivity.POSITION_MUSIC:
			type = "music";
			break;
		case TasteKidActivity.POSITION_SHOW:
			type = "show";
			break;
		default:
			break;
		}
		if (Configuration.DEVMODE) {
			Log.d("TasteKid", "Position is:" + this.position);
			Log.d("TasteKid", "type is: " + type);
		}

		APIWrapper.getResultsForType((IQueryCompleteListener) currentFragment,
				query, type);
		lastSearches.append(position, query);

		return true;
	}

}
