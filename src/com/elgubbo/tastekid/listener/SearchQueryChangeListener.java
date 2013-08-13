package com.elgubbo.tastekid.listener;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.elgubbo.tastekid.R;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.TasteKidApp;
import com.elgubbo.tastekid.adapter.RecentSearchesArrayAdapter;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.AutoCompleteManager;
import com.elgubbo.tastekid.model.ResultManager;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class SearchQueryChangeListener implements
		OnQueryTextListener {


	public SearchQueryChangeListener() {
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SearchView.OnQueryTextListener#onQueryTextChange(java.
	 * lang.String)
	 */
	@Override
	public boolean onQueryTextChange(String newText) {
		if (newText.length() > 1) 
			AutoCompleteManager.getInstance().getAutoCompleteSuggestions(newText);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SearchView.OnQueryTextListener#onQueryTextSubmit(java.
	 * lang.String)
	 */
	@Override
	public boolean onQueryTextSubmit(String query) {
		TasteKidApp.setCurrentQuery(query);
		TasteKidActivity tasteKidActivity = (TasteKidActivity) TasteKidActivity
				.getActivityInstance();
		tasteKidActivity.showLoadingBar(true);
		tasteKidActivity.getmViewPager().setCurrentItem(0,true);
		
		//close searchview
		MenuItem searchItem = tasteKidActivity.getMenu().findItem(
				R.id.action_search);
		searchItem.collapseActionView();

		//update recent searches list in the drawer
		RecentSearchesArrayAdapter recentsAdapter = (RecentSearchesArrayAdapter) tasteKidActivity
				.getRecentListView().getAdapter();
		recentsAdapter.clear();
		for (ApiResponse response : ResultManager.getInstance().getRecentSearches()) {
			recentsAdapter.add(response);
		}
		recentsAdapter.notifyDataSetChanged();

		//hide soft keyboard
		InputMethodManager inputManager = (InputMethodManager) TasteKidActivity
				.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(((Activity) TasteKidActivity
				.getActivityInstance()).getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

		if (query.trim().equalsIgnoreCase(""))
			return false;
		

		ResultManager.getInstance().getResultsForQuery(query);

		return true;
	}



}
