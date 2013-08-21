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
import com.elgubbo.tastekid.listener.ItemButtonClickListener;
import com.elgubbo.tastekid.listener.RecentSearchItemClickListener;
import com.elgubbo.tastekid.listener.SearchQueryChangeListener;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;
import com.elgubbo.tastekid.model.ResultManager;
import com.elgubbo.tastekid.ui.ExpandCollapseAnimation;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import android.annotation.SuppressLint;
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

import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * The Class TasteKidActivity. The main activity, contains all fragments
 * (ViewPager etc) and the actionbar
 */
public class TasteKidActivity extends BaseTasteKidSpiceActivity implements
		TabListener {

	SearchView mSearchView;
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager fragmentViewPager;
	ListView favouriteListView;
	ListView recentListView;
	DrawerLayout mDrawerLayout;
	LinearLayout drawerLinearLayout;
	// LinearLayout overlayLayout;
	LinearLayout headerLayout;
	boolean headerCollapsed = false;
	private static Context appContext;
	private static Activity activityInstance;
	SearchQueryChangeListener mSearchQueryChangeListener;
	private DBHelper databaseHelper;
	private Menu menu;
	private ActionBarDrawerToggle mDrawerToggle;
	// handler for received Intents for the "autocompletesuggestions" event
	private BroadcastReceiver mAutocompleteMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent
			if (intent.getExtras() != null) {
				final ArrayList<String> suggestions = intent.getExtras()
						.getStringArrayList("autocompletesuggestions");

				// setup MatrixCursor because suggestions of searchview will
				// only accept CursorAdapter and we dont have database data but
				// data in an arraylist
				MatrixCursor matrixCursor = new MatrixCursor(new String[] {
						"_id", "name" });
				int cursorId = 0;
				for (String suggestion : suggestions) {
					matrixCursor.addRow(new Object[] { cursorId, suggestion });
					cursorId++;
				}
				// setup the simple cursor adapter
				SimpleCursorAdapter mSimpleCursorAdapter = new SimpleCursorAdapter(
						appContext, R.layout.suggestion_list_item,
						matrixCursor, new String[] { "name" },
						new int[] { R.id.itemTitle },
						SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
				mSearchView.setSuggestionsAdapter(mSimpleCursorAdapter);
				// add the onsuggestionlistener, fire query when a suggestion is
				// clicked
				mSearchView.setOnSuggestionListener(new OnSuggestionListener() {
					@Override
					public boolean onSuggestionClick(int position) {
						mSearchView.setQuery(suggestions.get(position), true);
						return true;
					}

					@Override
					public boolean onSuggestionSelect(int position) {
						// don't do anything on select
						return false;
					}
				});

			}
		}
	};

	// handler for received Intents for the "update-event" event
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent
			String error = null;
			if (intent.getExtras() != null)
				error = intent.getExtras().getString("error");
			if (error != null) {
				onErrorReceived(error);
			} else {
				onResultsReady();

			}
		}
	};
	private LinearLayout headerItem;

	public DrawerLayout getmDrawerLayout() {
		return mDrawerLayout;
	}

	protected void onResultsReady() {
		final Result result = ResultManager.getInstance().getInfo().get(0);
		headerLayout.findViewById(R.id.help_layout).setVisibility(View.GONE);
		headerItem = (LinearLayout) headerLayout
				.findViewById(R.id.header_item_layout);
		TextView headerTitle = (TextView) headerItem.findViewById(R.id.title);
		TextView headerDescription = (TextView) headerItem
				.findViewById(R.id.description);
		headerTitle.setText(result.name);
		headerItem.findViewById(R.id.iconView).setBackgroundResource(
				TasteKidApp.ICON_MAP.get(result.type));
		headerDescription.setText(result.wTeaser);
		final LinearLayout textContainer = (LinearLayout) headerItem
				.findViewById(R.id.text_container);

		// setup onclicklisteners to buttons (youtube and wiki)
		LinearLayout buttonLayout = (LinearLayout) headerItem
				.findViewById(R.id.buttonLayout);
		LinearLayout yTButton = (LinearLayout) buttonLayout
				.findViewById(R.id.youtubeLinearLayout);
		LinearLayout wikiButton = (LinearLayout) buttonLayout
				.findViewById(R.id.wikiLinearLayout);
		ItemButtonClickListener listener = new ItemButtonClickListener(result,
				TasteKidActivity.getAppContext());
		if (result.yID != null && !result.yID.trim().equalsIgnoreCase("")) {
			yTButton.setOnClickListener(listener);
			yTButton.setVisibility(View.VISIBLE);
		} else
			yTButton.setVisibility(View.INVISIBLE);
		if (result.wUrl != null && !result.wUrl.trim().equalsIgnoreCase("")) {
			wikiButton.setOnClickListener(listener);
			wikiButton.setVisibility(View.VISIBLE);
		} else
			wikiButton.setVisibility(View.INVISIBLE);
		
		
		final ImageView collapseButton = (ImageView) headerItem
				.findViewById(R.id.dropdown_iconView);
		ExpandCollapseAnimation anim = new ExpandCollapseAnimation(
				textContainer, 1);
		textContainer.startAnimation(anim);
		headerCollapsed = true;
		collapseButton
				.setBackgroundResource(headerCollapsed ? R.drawable.navigation_expand
						: R.drawable.navigation_collapse);

		final RotateAnimation rotAnim = new RotateAnimation(0.0f, 180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotAnim.setDuration(200);
		rotAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				collapseButton
						.setBackgroundResource(headerCollapsed ? R.drawable.navigation_expand
								: R.drawable.navigation_collapse);
			}
		});
		collapseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int height = headerCollapsed ? 300 : 0;
				ExpandCollapseAnimation anim = new ExpandCollapseAnimation(
						textContainer, headerCollapsed ? 0 : 1);
				anim.setDuration(200);
				textContainer.startAnimation(anim);
				headerCollapsed = !headerCollapsed;
				collapseButton.startAnimation(rotAnim);
			}
		});
		headerItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TasteKidActivity
						.getActivityInstance(), DetailActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("result", result);
				TasteKidActivity.getActivityInstance().startActivity(intent);
			}
		});

		headerItem.setVisibility(View.VISIBLE);
	}

	protected void onErrorReceived(String error) {
		// TODO Auto-generated method stub

	}

	public static Context getAppContext() {
		return appContext;
	}

	public static Context getActivityInstance() {
		return activityInstance;
	}

	public ListView getFavouriteListView() {
		return favouriteListView;
	}

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
		return fragmentViewPager;
	}

	public ListView getRecentListView() {
		return recentListView;
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
	public void onCreate(Bundle savedInstanceState) {
		appContext = getApplicationContext();
		activityInstance = this;
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		// TODO fix this dirty workaround
		actionBar.setTitle("");
		actionBar.setSubtitle("explore your taste");
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// Overlay for the loading screen
		// overlayLayout = (LinearLayout) findViewById(R.id.overlay);
		// sidebar/drawer
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		drawerLinearLayout = (LinearLayout) findViewById(R.id.drawer_linear_layout);
		// Create the adapter that will return a fragment for each of the
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), this);
		// Set up the ViewPager with the sections adapter.
		fragmentViewPager = (ViewPager) findViewById(R.id.pager);
		fragmentViewPager.setOffscreenPageLimit(5);
		fragmentViewPager.setAdapter(mSectionsPagerAdapter);
		// When swiping between different sections, select the corresponding
		// tab.
		fragmentViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
						ViewParent root = findViewById(android.R.id.content)
								.getParent();
						findAndUpdateSpinner(root, position);

					}
				});

		mSearchQueryChangeListener = new SearchQueryChangeListener();

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		// Setup the header views
		headerLayout = (LinearLayout) findViewById(R.id.header_layout);

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

	private int originalHeaderTextHeight;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (savedInstanceState != null) {
			TasteKidApp.setCurrentQuery(savedInstanceState.getString("query"));
			ApiResponse restoredResponse = (ApiResponse) savedInstanceState
					.getParcelable("apiResponse");
			if (restoredResponse != null) {
				ResultManager.getInstance().setApiResponse(restoredResponse);
				onResultsReady();
			}
			headerCollapsed = savedInstanceState.getBoolean("headerCollapsed");
		}
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(
			android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

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
		// register message receivers to receive messages.
		LocalBroadcastManager.getInstance(
				TasteKidActivity.getActivityInstance()).registerReceiver(
				mAutocompleteMessageReceiver,
				new IntentFilter("update-autocomplete"));
		LocalBroadcastManager.getInstance(
				TasteKidActivity.getActivityInstance()).registerReceiver(
				mMessageReceiver, new IntentFilter("update-event"));

		if (Config.DEVMODE)
			ViewServer.get(this).setFocusedWindow(this);

	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		// save current query and apiresponse in the outgoing bundle
		b.putString("query", TasteKidApp.getCurrentQuery());
		b.putParcelable("apiResponse", ResultManager.getInstance()
				.getApiResponse());
		b.putBoolean("headerCollapsed", headerCollapsed);
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
		fragmentViewPager.setCurrentItem(tab.getPosition());

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
		// DO NOTHING
	}

	public void showLoadingBar(boolean show) {
		// overlayLayout.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		getSherlock().setProgressBarIndeterminateVisibility(show);
		setSupportProgressBarIndeterminateVisibility(show);
		mDrawerLayout.closeDrawers();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// DO NOTHING
	}

}
