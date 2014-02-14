package com.elgubbo.tastekid;

import java.util.ArrayList;

import com.elgubbo.tastekid.adapter.CardListArrayAdapter;
import com.elgubbo.tastekid.db.DBHelper;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

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
	GridView listView;

	/** The adapter. */
	CardListArrayAdapter adapter;

	/** The inflater. */
	LayoutInflater inflater;
	// Layouts

	// Which position the fragment is at
	/** The position. */
	int position;

	/** The database helper. */
	private DBHelper databaseHelper;
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	private CardListItemClickListener listItemClickListener;

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
		TasteKidActivity activity = (TasteKidActivity) TasteKidActivity
				.getActivityInstance();
		activity.showLoadingBar(false);
		if (adapter != null) {
			adapter.clear();
			adapter.notifyDataSetChanged();
		}

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

		results = ResultManager.getInstance().getResultsByPosition(position);
		info = ResultManager.getInstance().getInfo();
		adapter.clear();
		for (Result res : results) {
			adapter.add(res);
		}
		updateCards();

	}

	@Override
	public void onResume() {
		super.onResume();
		if(ResultManager.getInstance().resultsAvailable()){
//			onResultsReady();
		}
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
		state.putInt("position", position);
	}

	/**
	 * Sets up the list view that is displayed by this fragment.
	 */
	private void setupListView() {
		listView = (GridView) rootView.findViewById(R.id.cardListView);

		adapter = new CardListArrayAdapter(TasteKidActivity.getAppContext(),
				results);

		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		swingBottomInAnimationAdapter.setAbsListView(listView);
		listItemClickListener = new CardListItemClickListener();

		listView.setOnItemClickListener(listItemClickListener);
		listView.setAdapter(swingBottomInAnimationAdapter);
	}

	/**
	 * Refreshes the listView to fit the current resultSet.
	 */
	private void updateCards() {
		TasteKidActivity activity = (TasteKidActivity) TasteKidActivity
				.getActivityInstance();
		activity.showLoadingBar(false);

		adapter.notifyDataSetInvalidated();
		listView.smoothScrollToPosition(0);
	}

}
