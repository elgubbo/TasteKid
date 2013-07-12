package com.elgubbo.tastekid;

import com.elgubbo.tastekid.adapter.SectionsPagerAdapter;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.listener.SearchQueryChangeListener;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.SearchView;
import android.widget.Spinner;

public class TasteKidActivity extends FragmentActivity implements
		ActionBar.TabListener {


	// The searchview
	SearchView mSearchView;

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
	
    private static Context appContext;
    private static Activity activityInstance;

	SearchQueryChangeListener mSearchQueryChangeListener;
	
	private DBHelper databaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appContext = getApplicationContext();
		activityInstance = this;
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		// TODO fix this dirty workaround
		actionBar.setTitle("");
		actionBar.setSubtitle("explore your taste");
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), this);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mSearchQueryChangeListener = new SearchQueryChangeListener(
				mSectionsPagerAdapter, mViewPager.getCurrentItem(), mViewPager);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
						ViewParent root = findViewById(android.R.id.content)
								.getParent();
						findAndUpdateSpinner(root, position);
						if (mSearchView != null) {
							if (Configuration.DEVMODE)
								Log.d("TasteKid", "searchQuery is: "
										+ mSearchView.getQuery().toString());
							mSearchQueryChangeListener
									.onQueryTextSubmit(mSearchView.getQuery()
											.toString());
						}

					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

	}

	public static Context getAppContext() {
		return appContext;
	}
	public static Context getActivityInstance() {
		return activityInstance;
	}

	/**
	 * Searches the view hierarchy excluding the content view for a possible
	 * Spinner in the ActionBar.
	 * 
	 * @param root
	 *            The parent of the content view
	 * @param position
	 *            The position that should be selected
	 * @return if the spinner was found and adjusted
	 */
	private boolean findAndUpdateSpinner(Object root, int position) {
		if (root instanceof android.widget.Spinner) {
			// Found the Spinner
			Spinner spinner = (Spinner) root;
			spinner.setSelection(position);
			return true;
		} else if (root instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) root;
			if (group.getId() != android.R.id.content) {
				// Found a container that isn't the container holding our screen
				// layout
				for (int i = 0; i < group.getChildCount(); i++) {
					if (findAndUpdateSpinner(group.getChildAt(i), position)) {
						// Found and done searching the View tree
						return true;
					}
				}
			}
		}
		// Nothing found
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);

		mSearchView = (SearchView) searchItem.getActionView();

		setupSearchView(searchItem);
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		/*
		 * Handle DB closing
		 */
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}
	

	public DBHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, DBHelper.class);
		}
		return databaseHelper;
	}

	private void setupSearchView(MenuItem searchItem) {

		SearchManager mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		
		mSearchView.setSubmitButtonEnabled(true);
	    mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));
		if (mSearchManager != null) {
			// List<SearchableInfo> searchables = searchManager
			// .getSearchablesInGlobalSearch();
			//
			// SearchableInfo info = searchManager
			// .getSearchableInfo(getComponentName());
			// for (SearchableInfo inf : searchables) {
			// if (inf.getSuggestAuthority() != null
			// && inf.getSuggestAuthority().startsWith("applications")) {
			// info = inf;
			// }
			// }
			// mSearchView.setSearchableInfo(info);
		}
		mSearchView.setOnQueryTextListener(mSearchQueryChangeListener);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	

}
