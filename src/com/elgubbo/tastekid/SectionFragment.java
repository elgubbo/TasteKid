package com.elgubbo.tastekid;

import java.util.ArrayList;

import com.elgubbo.tastekid.adapter.CardListArrayAdapter;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.interfaces.IResultsReceiver;
import com.elgubbo.tastekid.listener.ItemButtonClickListener;
import com.elgubbo.tastekid.listener.ListItemClickListener;
import com.elgubbo.tastekid.model.Result;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * The Class SectionFragment. Displays a fragment for a given position (category)
 *
 * @author alexander reichert
 */
public class SectionFragment extends Fragment implements IResultsReceiver {

	/**
	 * Inits the.
	 *
	 * @param position the position
	 * @param adapterHolder the adapter holder
	 * @return the section fragment
	 */
	public static SectionFragment init(int position,
			SparseArray<CardListArrayAdapter> adapterHolder) {

		SectionFragment fragment = new SectionFragment();
		fragment.position = position;
		fragment.results = new ArrayList<Result>();
		fragment.info = new ArrayList<Result>();
		return fragment;
	}
	
	/** The results. */
	ArrayList<Result> results;
	
	/** The info. */
	ArrayList<Result> info;
	
	/** The root view. */
	View rootView;
	
	/** The list view. */
	ListView listView;
	
	/** The adapter. */
	CardListArrayAdapter adapter;

	/** The inflater. */
	LayoutInflater inflater;
	// Layouts
	/** The progress layout. */
	private LinearLayout progressLayout;
	
	/** The header layout. */
	private LinearLayout headerLayout;
	
	/** The info layout. */
	private LinearLayout infoLayout;
	
	/** The help layout. */
	private LinearLayout helpLayout;

	/** The error layout. */
	private LinearLayout errorLayout;

	// Which position the fragment is at
	/** The position. */
	int position;

	/** The database helper. */
	private DBHelper databaseHelper;

	/**
	 * Adds the on click listener to buttons.
	 *
	 * @param buttonLayout linearLayout that conains clickable linear Layouts
	 * @param result a result that should be used by the buttons
	 */
	private void addOnClickListenerToButtons(LinearLayout buttonLayout,
			Result result) {

		if (result == null)
			return;
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
	}


	/**
	 * fills the header content with a header listview item that displays the info about the current search result.
	 */
	public void fillHeaderInfo() {
		Result result;
		if (info.size() == 0)
			return;
		else {
			result = info.get(0);
		}
		TextView title = (TextView) infoLayout.findViewById(R.id.title);
		TextView description = (TextView) infoLayout
				.findViewById(R.id.description);
		title.setText(result.name);
		description.setText(result.wTeaser);
		setupHeaderButtons(infoLayout, result);
		infoLayout.setVisibility(View.VISIBLE);
		helpLayout.setVisibility(View.GONE);
	}

	/**
	 * fills the header content with a Layout that is displayed when no results are found.
	 */
	private void fillHeaderNoResults() {
		LinearLayout buttonLayout = (LinearLayout) infoLayout
				.findViewById(R.id.buttonLayout);
		TextView title = (TextView) infoLayout.findViewById(R.id.title);
		TextView description = (TextView) infoLayout
				.findViewById(R.id.description);
		title.setText("No results for " + TasteKidApp.getCurrentQuery());
		description.setText("Try searching for something different");
		infoLayout.setVisibility(View.VISIBLE);
		helpLayout.setVisibility(View.GONE);
		buttonLayout.setVisibility(View.GONE);

	}

	/**
	 * Fills the header content with a Help box that describes what to do to the user.
	 */
	public void fillHelpInfo() {
		helpLayout.setOnClickListener(null);
		TextView title = (TextView) helpLayout.findViewById(R.id.title);
		LinearLayout buttonLayout = (LinearLayout) helpLayout
				.findViewById(R.id.buttonLayoutItem);
		buttonLayout.setVisibility(View.GONE);

		TextView description = (TextView) helpLayout
				.findViewById(R.id.description);
		switch (this.position) {
		case TasteKidApp.POSITION_ALL:
			title.setText("All categories");
			description
					.setText("Press the search button on top to explore your taste. Just search for the name of a book, movie, artist, or game you liked and you will get suggestions.");
			break;
		case TasteKidApp.POSITION_BOOK:
			title.setText("Books");
			description
					.setText("Press the search button on top to explore your taste in Books you might like. Just search for the name of a book you liked and you will get suggestions.");
			break;
		case TasteKidApp.POSITION_GAME:
			title.setText("Games");
			description
					.setText("Press the search button on top to explore your taste in games you might like. Just search for the name of a game you liked and you will get suggestions.");
			break;
		case TasteKidApp.POSITION_MOVIE:
			title.setText("Movies");
			description
					.setText("Press the search button on top to explore your taste in movies you might like. Just search for the name of a movie you liked and you will get suggestions.");
			break;
		case TasteKidApp.POSITION_MUSIC:
			title.setText("Music");
			description
					.setText("Press the search button on top to explore your taste in music you might like. Just search for the name of an artist you liked and you will get suggestions.");
			break;
		case TasteKidApp.POSITION_SHOW:
			title.setText("Shows");
			description
					.setText("Press the search button on top to explore your taste in shows you might like. Just search for the name of a show you liked and you will get suggestions.");
			break;
		default:
			break;
		}
		headerLayout.removeView(helpLayout);
		headerLayout.addView(helpLayout);
		helpLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Hides the loading bar.
	 */
	public void hideLoadingBar() {
		if (Configuration.DEVMODE)
			Log.d("TasteKid", "hiding loadingbar");
		TasteKidActivity activity = (TasteKidActivity) TasteKidActivity.getActivityInstance();
		activity.hideLoadingBar();
		progressLayout.setVisibility(View.GONE);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.inflater = inflater;
		this.rootView = inflater.inflate(R.layout.fragment_main_layout,
				container, false);
		this.helpLayout = (LinearLayout) inflater.inflate(R.layout.list_item,
				null);
		this.errorLayout = (LinearLayout) inflater.inflate(R.layout.list_item,
				null);
		progressLayout = new LinearLayout(TasteKidActivity.getAppContext());

		if(getArguments() != null)
			this.position = getArguments().getInt("position");
		if(savedInstanceState != null)
			this.position = savedInstanceState.getInt("position");
		this.info = ResultManager.getInstance().getInfo();
		this.results = ResultManager.getInstance().getResultsByPosition(position);
		
		this.setupListView();

		if (Configuration.DEVMODE) {
			Log.d("TasteKid", "results in oncreate of sectionfragment is: "
					+ results);
			Log.d("TasteKid",
					"position in oncreateview of sectionfragment is: "
							+ position);
			Log.d("TasteKid", "creating layout");

		}
		return rootView;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		/*
		 * Handle DB closing
		 */
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	/* (non-Javadoc)
	 * @see com.elgubbo.tastekid.interfaces.IResultsReceiver#onErrorReceived(java.lang.String)
	 */
	@Override
	public void onErrorReceived(String error) {
		headerLayout.removeView(errorLayout);
		errorLayout.setBackgroundColor(getResources().getColor(
				android.R.color.holo_orange_light));
		LinearLayout buttonLayout = (LinearLayout) errorLayout.findViewById(R.id.buttonLayoutItem);
		errorLayout.removeView(buttonLayout);
		errorLayout.setOnClickListener(null);
		TextView title = (TextView) errorLayout.findViewById(R.id.title);
		TextView content = (TextView) errorLayout
				.findViewById(R.id.description);
		title.setText("Error");
		content.setText(error);
		headerLayout.addView(errorLayout);
		hideLoadingBar();
	}

	/* (non-Javadoc)
	 * @see com.elgubbo.tastekid.interfaces.IResultsReceiver#onResultsReady()
	 */
	@Override
	public void onResultsReady() {
		results = TasteKidApp.resultManager.getResultsByPosition(position);
		info = TasteKidApp.resultManager.getInfo();
		adapter.clear();
		adapter.addAll(results);
		updateCards();
		hideLoadingBar();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		if (Configuration.DEVMODE)
			Log.i("TasteKid", "onSaveInstanceState()");
		state.putParcelableArrayList("results", results);
		state.putParcelableArrayList("info", info);
		state.putInt("position", position);
	}

	/**
	 * Sets up the ONE header layout that contains all other views that should be displayed in the header.
	 */
	private void setupHeader() {
		headerLayout = new LinearLayout(TasteKidActivity.getAppContext());
		headerLayout.setGravity(Gravity.CENTER);
		headerLayout.setOrientation(LinearLayout.VERTICAL);
		headerLayout.setLayoutParams(new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		infoLayout = (LinearLayout) inflater
				.inflate(R.layout.header_item, null);
		headerLayout.addView(infoLayout);
		infoLayout.setVisibility(View.GONE);
		if (info == null || info.size() == 0)
			fillHelpInfo();
		else if (results.size() == 0)
			fillHeaderNoResults();
		else
			fillHeaderInfo();

		setupLoadingBar();

		listView.addHeaderView(headerLayout);
	}

	
	/**
	 * Setup header buttons.
	 *
	 * @param headerView the view that contains the info about the current search result
	 * @param result the result
	 */
	private void setupHeaderButtons(View headerView, Result result) {
		LinearLayout buttonLayout = (LinearLayout) headerView
				.findViewById(R.id.buttonLayout);
		addOnClickListenerToButtons(buttonLayout, result);
	}

	
	/**
	 * Sets up the list view that is displayed by this fragment.
	 */
	private void setupListView() {
		listView = (ListView) rootView.findViewById(R.id.cardListView);
		// setup or recycle adapter

		adapter = new CardListArrayAdapter(TasteKidActivity.getAppContext(),
				results);

		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
		swingBottomInAnimationAdapter.setAbsListView(listView);
		setupHeader();
		listView.setAdapter(swingBottomInAnimationAdapter);
		listView.setOnItemClickListener(new ListItemClickListener());

	}
	
	
	/**
	 * Sets up the loading bar that is shown during requests.
	 */
	private void setupLoadingBar() {
		progressLayout.setGravity(Gravity.CENTER);
		progressLayout.setBackgroundResource(R.drawable.card_background);
		TextView loadingText = new TextView(TasteKidActivity.getAppContext());
		loadingText.setText("Loading...");
		loadingText.setTextColor(Color.BLACK);
		loadingText.setGravity(Gravity.CENTER);
		ProgressBar loadingProgressBar = new ProgressBar(
				TasteKidActivity.getAppContext());
		progressLayout.addView(loadingText);
		progressLayout.addView(loadingProgressBar);
		headerLayout.addView(progressLayout);
		progressLayout.setVisibility(View.GONE);
	}

	
	/**
	 * Shows the loading bar.
	 */
	public void showLoadingBar() {
		if (Configuration.DEVMODE)
			Log.d("TasteKid", "showing loadingbar");
		TasteKidActivity activity = (TasteKidActivity) TasteKidActivity.getActivityInstance();
		activity.showLoadingBar();
		progressLayout.setVisibility(View.VISIBLE);
		helpLayout.setVisibility(View.GONE);
	}

	/**
	 * Refreshes the listView to fit the current resultSet.
	 */
	private void updateCards() {
		if (results.size() == 0) {
			if (info.size() == 0)
				fillHelpInfo();
			else
				fillHeaderNoResults();
		} else
			fillHeaderInfo();
		adapter.notifyDataSetChanged();
	}

}
