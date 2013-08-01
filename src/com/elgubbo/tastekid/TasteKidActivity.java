package com.elgubbo.tastekid;

import com.elgubbo.tastekid.adapter.FavouriteResultArrayAdapter;
import com.elgubbo.tastekid.adapter.RecentSearchesArrayAdapter;
import com.elgubbo.tastekid.adapter.SectionsPagerAdapter;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.listener.FavouriteItemClickListener;
import com.elgubbo.tastekid.listener.RecentSearchItemClickListener;
import com.elgubbo.tastekid.listener.SearchQueryChangeListener;
import com.elgubbo.tastekid.model.ResultManager;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

/**
 * The Class TasteKidActivity. The main activity, contains all fragments
 * (ViewPager etc) and the actionbar
 */
public class TasteKidActivity extends BaseTasteKidSpiceActivity implements
		ActionBar.TabListener {

	// The searchview
	/** The m search view. */
	SearchView mSearchView;

	public SearchView getmSearchView() {
		return mSearchView;
	}

	/** The m sections pager adapter. */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/** The m view pager. */
	ViewPager mViewPager;

	/** the Favourites Listview **/
	ListView favouriteListView;

	public ListView getFavouriteListView() {
		return favouriteListView;
	}

	/** the Recent searches listview **/
	ListView recentListView;

	public ListView getRecentListView() {
		return recentListView;
	}

	/** The app context. */
	private static Context appContext;

	/** The activity instance. */
	private static Activity activityInstance;

	/** The m search query change listener. */
	SearchQueryChangeListener mSearchQueryChangeListener;

	public SearchQueryChangeListener getmSearchQueryChangeListener() {
		return mSearchQueryChangeListener;
	}

	/** The database helper. */
	private DBHelper databaseHelper;

	private Menu menu;

	public Menu getMenu() {
		return menu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setBehindContentView(R.layout.slidingmenu);

		setContentView(R.layout.activity_main);
		appContext = getApplicationContext();
		activityInstance = this;
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		// TODO fix this dirty workaround
		actionBar.setTitle("");
		actionBar.setSubtitle("explore your taste");
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

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

		// set up the sidebar
		setupSlidingMenu();
	}

	private void setupSlidingMenu() {
		// configure the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		/*
		 * sm.setShadowWidthRes(R.dimen.shadow_width);
		 * sm.setShadowDrawable(android
		 * .R.drawable.screen_background_dark_transparent);
		 */
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		recentListView = (ListView) sm.findViewById(R.id.sideBarList1);
		favouriteListView = (ListView) sm.findViewById(R.id.sideBarList2);

		favouriteListView.setAdapter(new FavouriteResultArrayAdapter(this,
				R.layout.sidebar_list_item, ResultManager.getInstance()
						.getFavouriteResults()));
		favouriteListView
				.setOnItemClickListener(new FavouriteItemClickListener());
		recentListView.setAdapter(new RecentSearchesArrayAdapter(this,
				R.layout.sidebar_list_item, ResultManager.getInstance()
						.getRecentSearches()));
		recentListView.setOnItemClickListener(new RecentSearchItemClickListener());
	}

	/**
	 * Gets the app context.
	 * 
	 * @return the app context
	 */
	public static Context getAppContext() {
		return appContext;
	}

	/**
	 * Gets the activity instance.
	 * 
	 * @return the activity instance
	 */
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

	public void showLoadingBar() {
		setProgressBarIndeterminateVisibility(true);
	}

	public void hideLoadingBar() {
		setProgressBarIndeterminateVisibility(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) searchItem.getActionView();

		setupSearchView(searchItem);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
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

	/**
	 * Gets the helper.
	 * 
	 * @return the helper
	 */
	public DBHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, DBHelper.class);
		}
		return databaseHelper;
	}

	/**
	 * Sets the up search view.
	 * 
	 * @param searchItem
	 *            the new up search view
	 */
	private void setupSearchView(MenuItem searchItem) {

		SearchManager mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		mSearchView.setSubmitButtonEnabled(true);
		mSearchView.setSearchableInfo(mSearchManager
				.getSearchableInfo(getComponentName()));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.app.ActionBar.TabListener#onTabSelected(android.app.ActionBar
	 * .Tab, android.app.FragmentTransaction)
	 */
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.app.ActionBar.TabListener#onTabUnselected(android.app.ActionBar
	 * .Tab, android.app.FragmentTransaction)
	 */
	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.app.ActionBar.TabListener#onTabReselected(android.app.ActionBar
	 * .Tab, android.app.FragmentTransaction)
	 */
	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        toggle();
	        return true;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

}
