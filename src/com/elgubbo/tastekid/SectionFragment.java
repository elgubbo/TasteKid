package com.elgubbo.tastekid;

import java.util.ArrayList;

import com.elgubbo.tastekid.adapter.CardListArrayAdapter;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.listener.ItemButtonClickListener;
import com.elgubbo.tastekid.listener.CardListItemClickListener;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;
import com.elgubbo.tastekid.model.ResultManager;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

/**
 * The Class SectionFragment. Displays a fragment for a given position
 * (category)
 * 
 * @author alexander reichert
 */
public class SectionFragment extends Fragment {

	/**
	 * Inits the.
	 * 
	 * @param position
	 *            the position
	 * @param adapterHolder
	 *            the adapter holder
	 * @return the section fragment
	 */
	public static SectionFragment init(int position) {

		SectionFragment fragment = new SectionFragment();
		fragment.position = position;
		// fragment.results = ResultManager.getInstance().getResultsByPosition(
		// position);
		// fragment.info = ResultManager.getInstance().getInfo();

		return fragment;
	}

	ApiResponse apiResponse;
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
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	private CardListItemClickListener listItemClickListener;

	// handler for received Intents for the "my-event" event
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent
			String error =null;
			if(intent.getExtras() != null)
			error = intent.getExtras().getString("error");
			if (error != null) {
				onErrorReceived(error);
			} else {
				onResultsReady();

			}
		}
	};

	/**
	 * Adds the on click listener to buttons.
	 * 
	 * @param buttonLayout
	 *            linearLayout that conains clickable linear Layouts
	 * @param result
	 *            a result that should be used by the buttons
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
	 * fills the header content with a header listview item that displays the
	 * info about the current search result.
	 */
	public void fillHeaderInfo() {
		Result infoResult;
		if (this.info.size() == 0)
			return;
		else {
			infoResult = info.get(0);
		}
		TextView title = (TextView) infoLayout.findViewById(R.id.title);
		TextView description = (TextView) infoLayout
				.findViewById(R.id.description);
		title.setText(infoResult.name);
		description.setText(infoResult.wTeaser);
		infoLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//Start the Detail Activity 
				Intent intent = new Intent(TasteKidActivity.getActivityInstance(), DetailActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("result", ResultManager.getInstance().getInfo().get(0));
				
				TasteKidActivity.getAppContext().startActivity(intent);				
			}
		});
		LinearLayout buttonLayout = (LinearLayout) infoLayout
				.findViewById(R.id.buttonLayout);
		buttonLayout.setVisibility(View.VISIBLE);
		addOnClickListenerToButtons(buttonLayout, infoResult);
		infoLayout.setVisibility(View.VISIBLE);
		helpLayout.setVisibility(View.GONE);
	}

	/**
	 * fills the header content with a Layout that is displayed when no results
	 * are found.
	 */
	private void fillHeaderNoResults() {
		LinearLayout buttonLayout = (LinearLayout) infoLayout
				.findViewById(R.id.buttonLayout);
		TextView title = (TextView) infoLayout.findViewById(R.id.title);
		TextView description = (TextView) infoLayout
				.findViewById(R.id.description);
		title.setText("No results for "
				+ ResultManager.getInstance().getApiResponse().query);
		description.setText("Try searching for something different");
		infoLayout.setVisibility(View.VISIBLE);
		helpLayout.setVisibility(View.GONE);
		buttonLayout.setVisibility(View.GONE);

	}

	/**
	 * Fills the header content with a Help box that describes what to do to the
	 * user.
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



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
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
		if (getArguments() != null)
			this.position = getArguments().getInt("position");
		if (savedInstanceState != null)
			this.position = savedInstanceState.getInt("position");
		this.info = ResultManager.getInstance().getInfo();
		this.results = ResultManager.getInstance().getResultsByPosition(
				position);

		this.setupListView();

		return rootView;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.elgubbo.tastekid.interfaces.IResultsReceiver#onErrorReceived(java
	 * .lang.String)
	 */
	public void onErrorReceived(String error) {
		TasteKidActivity activity = (TasteKidActivity) TasteKidActivity.getActivityInstance();
		activity.showLoadingBar(false);
		if(adapter!=null){
			adapter.clear();
			adapter.notifyDataSetChanged();
		}
		
		headerLayout.removeAllViews();
		errorLayout.setBackgroundResource(R.drawable.card_background);
		LinearLayout buttonLayout = (LinearLayout) errorLayout
				.findViewById(R.id.buttonLayoutItem);
		errorLayout.removeView(buttonLayout);
		errorLayout.setOnClickListener(null);
		TextView title = (TextView) errorLayout.findViewById(R.id.title);
		TextView content = (TextView) errorLayout
				.findViewById(R.id.description);
		title.setText("Error");
		content.setText(error);
		helpLayout.setVisibility(View.INVISIBLE);
		errorLayout.setVisibility(View.VISIBLE);
		headerLayout.addView(errorLayout);
	}

	@Override
	public void onPause() {
		// Unregister since the activity is not visible
		LocalBroadcastManager.getInstance(
				TasteKidActivity.getActivityInstance()).unregisterReceiver(
				mMessageReceiver);
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.elgubbo.tastekid.interfaces.IResultsReceiver#onResultsReady()
	 */
	public void onResultsReady() {
		errorLayout.setVisibility(View.GONE);

		results = ResultManager.getInstance().getResultsByPosition(position);
		info = ResultManager.getInstance().getInfo();
		headerLayout.removeAllViews();
		headerLayout.addView(infoLayout);
		adapter.clear();
		for (Result res : results) {
			adapter.add(res);
		}
//		adapter.addAll(results);
		updateCards();
	}

	@Override
	public void onResume() {
		super.onResume();

		// Register mMessageReceiver to receive messages.
		LocalBroadcastManager.getInstance(
				TasteKidActivity.getActivityInstance()).registerReceiver(
				mMessageReceiver, new IntentFilter("update-event"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		if (Config.DEVMODE)
			Log.i("TasteKid", "onSaveInstanceState()");
		state.putParcelableArrayList("results", results);
		state.putParcelableArrayList("info", info);
		state.putInt("position", position);
	}

	/**
	 * Sets up the ONE header layout that contains all other views that should
	 * be displayed in the header.
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

		listView.addHeaderView(headerLayout);
	}



	/**
	 * Sets up the list view that is displayed by this fragment.
	 */
	private void setupListView() {
		listView = (ListView) rootView.findViewById(R.id.cardListView);

		adapter = new CardListArrayAdapter(TasteKidActivity.getAppContext(),
				results);

		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		swingBottomInAnimationAdapter.setAbsListView(listView);
		listItemClickListener = new CardListItemClickListener();

		setupHeader();
		listView.setOnItemClickListener(listItemClickListener);
		listView.setAdapter(swingBottomInAnimationAdapter);

	}


	/**
	 * Refreshes the listView to fit the current resultSet.
	 */
	private void updateCards() {
		TasteKidActivity activity = (TasteKidActivity) TasteKidActivity.getActivityInstance();
		activity.showLoadingBar(false);
 		
		helpLayout.setVisibility(View.GONE);
		if (results.size() == 0) {
			if (info.size() == 0)
				fillHelpInfo();
			else
				fillHeaderNoResults();
		} else
			fillHeaderInfo();
		adapter.notifyDataSetInvalidated();
		listView.smoothScrollToPosition(0);
	}

}
