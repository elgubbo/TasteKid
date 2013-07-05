package com.elgubbo.tastekid;

import java.util.List;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

public class TasteKidActivity extends FragmentActivity implements
		ActionBar.TabListener {

	public static final int POSITION_ALL = 0;
	public static final int POSITION_MOVIE = 2;
	public static final int POSITION_MUSIC = 1;
	public static final int POSITION_BOOK = 3;
	public static final int POSITION_GAME = 4;
	public static final int POSITION_SHOW = 5;
	// The searchview
	SearchView mSearchView;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	SearchQueryChangeListener mSearchQueryChangeListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
						if (Configuration.DEVMODE)
							Log.d("TasteKid", "searchQuery is: "
									+ mSearchView.getQuery().toString());
						mSearchQueryChangeListener
								.onQueryTextSubmit(mSearchView.getQuery()
										.toString());
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) searchItem.getActionView();
		setupSearchView(searchItem);
		return true;
	}

	private void setupSearchView(MenuItem searchItem) {

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if (searchManager != null) {
//			List<SearchableInfo> searchables = searchManager
//					.getSearchablesInGlobalSearch();
//
//			SearchableInfo info = searchManager
//					.getSearchableInfo(getComponentName());
//			for (SearchableInfo inf : searchables) {
//				if (inf.getSuggestAuthority() != null
//						&& inf.getSuggestAuthority().startsWith("applications")) {
//					info = inf;
//				}
//			}
//			mSearchView.setSearchableInfo(info);
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
