package com.elgubbo.tastekid;

import com.elgubbo.tastekid.api.APIWrapper;
import com.elgubbo.tastekid.interfaces.IQueryCompleteListener;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.SearchView;

public class SearchQueryChangeListener implements SearchView.OnQueryTextListener{

	int position;
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	public SearchQueryChangeListener(SectionsPagerAdapter mSectionsPagerAdapter, int position, ViewPager mViewPager){
		this.mSectionsPagerAdapter = mSectionsPagerAdapter;
		this.position = position;
		this.mViewPager = mViewPager;
	}
	@Override
	public boolean onQueryTextChange(String newText) {
		if(newText.length()>2){
			
		}
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if(query==null || query.trim() == "")
			return true;
		SectionFragment currentFragment = (SectionFragment) mSectionsPagerAdapter.getActiveFragment(mViewPager, mViewPager.getCurrentItem());
		
		currentFragment.showLoadingBar();
		
		String type = null;
		switch (this.position) {
		case TasteKidActivity.POSITION_ALL:
			break;
		case TasteKidActivity.POSITION_BOOK:
			type="book";
			break;
		case TasteKidActivity.POSITION_GAME:
			type="game";
			break;
		case TasteKidActivity.POSITION_MOVIE:
			type="movie";
			break;
		case TasteKidActivity.POSITION_MUSIC:
			type="music";
			break;
		default:
			break;
		}
		if(Configuration.DEVMODE){
			Log.d("TasteKid", "Type is: "+type);
			Log.d("TasteKid", "Position is:"+this.position);
		}
		APIWrapper.getResultsForType(
				(IQueryCompleteListener) currentFragment
				, query, type);
		return true;
	}

}
