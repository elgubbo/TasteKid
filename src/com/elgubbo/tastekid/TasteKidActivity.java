package com.elgubbo.tastekid;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnSuggestionListener;
import com.elgubbo.tastekid.adapter.FavouriteResultArrayAdapter;
import com.elgubbo.tastekid.adapter.RecentSearchesArrayAdapter;
import com.elgubbo.tastekid.adapter.SectionsPagerAdapter;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.helper.ViewServer;
import com.elgubbo.tastekid.listener.FavouriteItemClickListener;
import com.elgubbo.tastekid.listener.RecentSearchItemClickListener;
import com.elgubbo.tastekid.listener.SearchQueryChangeListener;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.ResultManager;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;

import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

/**
 * The Class TasteKidActivity. The main activity, contains all fragments
 * (ViewPager etc) and the actionbar
 */
public class TasteKidActivity extends BaseTasteKidSpiceActivity implements
		TabListener {

	/**
	 * Gets the activity instance.
	 * 
	 * @return the activity instance
	 */
	public static Context getActivityInstance() {
		return activityInstance;
	}

	// The searchview
	/** The m search view. */
	SearchView mSearchView;

	/** The m sections pager adapter. */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/** The m view pager. */
	ViewPager mViewPager;

	/** the Favourites Listview **/
	ListView favouriteListView;

	/** the Recent searches listview **/
	ListView recentListView;

	DrawerLayout mDrawerLayout;

	LinearLayout drawerLinearLayout;

	public DrawerLayout getmDrawerLayout() {
		return mDrawerLayout;
	}

	/** The app context. */
	private static Context appContext;

	/** The activity instance. */
	private static Activity activityInstance;

	/**
	 * Gets the app context.
	 * 
	 * @return the app context
	 */
	public static Context getAppContext() {
		return appContext;
	}

	/** The m search query change listener. */
	SearchQueryChangeListener mSearchQueryChangeListener;

	/** The database helper. */
	private DBHelper databaseHelper;

	private Menu menu;

	// handler for received Intents for the "autocompletesuggestions" event
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent
			if (intent.getExtras() != null) {
				final ArrayList<String> suggestions = intent.getExtras()
						.getStringArrayList("autocompletesuggestions");
				MatrixCursor matrixCursor = new MatrixCursor(new String[] {
						"_id", "name" });
				int cursorId = 0;
				for (String suggestion : suggestions) {
					matrixCursor.addRow(new Object[] { cursorId, suggestion });
					cursorId++;
				}
				SimpleCursorAdapter mSimpleCursorAdapter = new SimpleCursorAdapter(
						appContext, R.layout.suggestion_list_item,
						matrixCursor, new String[] { "name" },
						new int[] { R.id.itemTitle },
						SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
				mSearchView.setSuggestionsAdapter(mSimpleCursorAdapter);
				mSearchView.setOnSuggestionListener(new OnSuggestionListener() {

					@Override
					public boolean onSuggestionClick(int position) {
						mSearchView.setQuery(suggestions.get(position), true);
						return true;
					}

					@Override
					public boolean onSuggestionSelect(int position) {
						return false;
					}
				});

			}
		}
	};

	private ActionBarDrawerToggle mDrawerToggle;

	// private TitlePageIndicator titleIndicator;

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

	public ListView getFavouriteListView() {
		return favouriteListView;
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

	public Menu getMenu() {
		return menu;
	}

	public SearchQueryChangeListener getmSearchQueryChangeListener() {
		return mSearchQueryChangeListener;
	}

	public SearchView getmSearchView() {
		return mSearchView;
	}

	public ViewPager getmViewPager() {
		return mViewPager;
	}

	public ListView getRecentListView() {
		return recentListView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		appContext = getApplicationContext();
		activityInstance = this;
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			TasteKidApp.setCurrentQuery(savedInstanceState.getString("query"));
			ApiResponse restoredResponse = (ApiResponse) savedInstanceState
					.getParcelable("apiResponse");
			if (restoredResponse != null) {
				ResultManager.getInstance().setApiResponse(restoredResponse);
			}

		}
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.main_layout);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		// TODO fix this dirty workaround
		actionBar.setTitle("");
		actionBar.setSubtitle("explore your taste");
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		);

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		drawerLinearLayout = (LinearLayout) findViewById(R.id.drawer_linear_layout);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), this);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(5);
		mViewPager.setAdapter(mSectionsPagerAdapter);
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

					}
				});
		// titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);

		// titleIndicator.setViewPager(mViewPager);
		mSearchQueryChangeListener = new SearchQueryChangeListener(
				mSectionsPagerAdapter, mViewPager.getCurrentItem(), mViewPager);

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		// Setup navigation drawer listviews
		recentListView = (ListView) findViewById(R.id.sideBarList1);
		favouriteListView = (ListView) findViewById(R.id.sideBarList2);

		favouriteListView.setAdapter(new FavouriteResultArrayAdapter(this,
				R.layout.sidebar_list_item, ResultManager.getInstance()
						.getFavouriteResults()));
		favouriteListView
				.setOnItemClickListener(new FavouriteItemClickListener());
		recentListView.setAdapter(new RecentSearchesArrayAdapter(this,
				R.layout.sidebar_list_item, ResultManager.getInstance()
						.getRecentSearches()));
		recentListView
				.setOnItemClickListener(new RecentSearchItemClickListener());

		showLoadingBar(false);
		if (Config.DEVMODE)
			ViewServer.get(this).addWindow(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(
			android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
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
		getSupportMenuInflater().inflate(R.menu.main, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);

		mSearchView = (SearchView) searchItem.getActionView();

		mSearchView.setSubmitButtonEnabled(true);
		mSearchView.setOnQueryTextListener(mSearchQueryChangeListener);

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

		// Handle DB closing
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
		ViewServer.get(this).removeWindow(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(drawerLinearLayout)) {
				mDrawerLayout.closeDrawer(drawerLinearLayout);
			} else {
				mDrawerLayout.openDrawer(drawerLinearLayout);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		// Register mMessageReceiver to receive messages.
		LocalBroadcastManager.getInstance(
				TasteKidActivity.getActivityInstance()).registerReceiver(
				mMessageReceiver, new IntentFilter("update-autocomplete"));
		if (Config.DEVMODE)
			ViewServer.get(this).setFocusedWindow(this);

	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		b.putString("query", TasteKidApp.getCurrentQuery());
		b.putParcelable("apiResponse", ResultManager.getInstance()
				.getApiResponse());
		super.onSaveInstanceState(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.app.ActionBar.TabListener#onTabSelected(android.app.ActionBar
	 * .Tab, android.app.FragmentTransaction)
	 */
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
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
	public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
	}

	public void showLoadingBar(boolean show) {
		getSherlock().setProgressBarIndeterminateVisibility(show);
		setSupportProgressBarIndeterminateVisibility(show);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

}
