package com.elgubbo.tastekid;

import java.util.ArrayList;

import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.interfaces.IResultsReceiver;
import com.elgubbo.tastekid.model.Result;
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
import android.widget.SearchView;
import android.widget.TextView;

public class SectionFragment extends Fragment implements IResultsReceiver {

	ArrayList<Result> results;
	ArrayList<Result> info;
	View rootView;
	ListView listView;
	CardListArrayAdapter adapter;
	LayoutInflater inflater;

	// Layouts
	private LinearLayout progressLayout;
	private LinearLayout headerLayout;
	private LinearLayout infoLayout;
	private LinearLayout helpLayout;

	// Which position the fragment is at
	int position;
	private DBHelper databaseHelper;
	
	public static SectionFragment init(int position, SparseArray<CardListArrayAdapter> adapterHolder) {

		SectionFragment fragment = new SectionFragment();
		fragment.position = position;
		fragment.results = new ArrayList<Result>();
		fragment.info = new ArrayList<Result>();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.inflater = inflater;
		this.rootView = inflater.inflate(R.layout.fragment_main_layout, container,
				false);
		this.helpLayout = (LinearLayout) inflater.inflate(R.layout.list_item, null);

		if(savedInstanceState != null){
			this.info = savedInstanceState.getParcelableArrayList("info");
			this.results = savedInstanceState.getParcelableArrayList("results");
		}
		this.setupListView();
		Log.d("TasteKid", "creating layout");
		return rootView;
	}
	
	@Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.i("TasteKid", "onSaveInstanceState()");
        state.putParcelableArrayList("results", results);
        state.putParcelableArrayList("info", info);

        state.putString("saved_thing", "some_value");
    }
	private void setupListView() {
		listView = (ListView) rootView.findViewById(R.id.cardListView);
		//setup or recycle adapter

		adapter = new CardListArrayAdapter(TasteKidActivity.getAppContext(), results);


		setupHeader();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new ListItemClickListener());


	}

	private void updateCards() {
		if(results.size() == 0){
			if(info.size() == 0)
				fillHelpInfo();
			else
				fillHeaderNoResults();
		}
		else
			fillHeaderInfo();
		adapter.notifyDataSetChanged();
	}

	private void fillHeaderNoResults() {
		SearchView mSearchView = (SearchView) getActivity().findViewById(R.id.action_search);
		LinearLayout buttonLayout = (LinearLayout) infoLayout.findViewById(R.id.buttonLayout);
		TextView title = (TextView) infoLayout.findViewById(R.id.title);
		TextView description = (TextView) infoLayout
				.findViewById(R.id.description);
			title.setText("No results for " + mSearchView.getQuery() );
		description.setText("Try searching for something different");
		infoLayout.setVisibility(View.VISIBLE);
		helpLayout.setVisibility(View.GONE);	
		buttonLayout.setVisibility(View.GONE);
		
	}
	
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
	


	private void setupHeader() {
		headerLayout = new LinearLayout(TasteKidActivity.getAppContext());
		headerLayout.setGravity(Gravity.CENTER);
		headerLayout.setLayoutParams(new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		infoLayout = (LinearLayout) inflater
				.inflate(R.layout.header_item, null);
		headerLayout.addView(infoLayout);
		infoLayout.setVisibility(View.GONE);
		if (info != null && info.size() == 0)
			fillHelpInfo();
		else if(results.size()==0)
			fillHeaderNoResults();
		else
			fillHeaderInfo();

		setupLoadingBar();

		listView.addHeaderView(headerLayout);
	}

	public void fillHelpInfo() {

		TextView title = (TextView) helpLayout.findViewById(R.id.title);
		LinearLayout buttonLayout = (LinearLayout) helpLayout.findViewById(R.id.buttonLayoutItem);
		buttonLayout.setVisibility(View.GONE);

		TextView description = (TextView) helpLayout
				.findViewById(R.id.description);
		switch (this.position) {
		case TasteKidActivity.POSITION_ALL:
			title.setText("All categories");
			description
					.setText("Press the search button on top to explore your taste. Just search for the name of a book, movie, artist, or game you liked and you will get suggestions.");
			break;
		case TasteKidActivity.POSITION_BOOK:
			title.setText("Books");
			description
					.setText("Press the search button on top to explore your taste in Books you might like. Just search for the name of a book you liked and you will get suggestions.");
			break;
		case TasteKidActivity.POSITION_GAME:
			title.setText("Games");
			description
					.setText("Press the search button on top to explore your taste in games you might like. Just search for the name of a game you liked and you will get suggestions.");
			break;
		case TasteKidActivity.POSITION_MOVIE:
			title.setText("Movies");
			description
					.setText("Press the search button on top to explore your taste in movies you might like. Just search for the name of a movie you liked and you will get suggestions.");
			break;
		case TasteKidActivity.POSITION_MUSIC:
			title.setText("Music");
			description
					.setText("Press the search button on top to explore your taste in music you might like. Just search for the name of an artist you liked and you will get suggestions.");
			break;
		case TasteKidActivity.POSITION_SHOW:
			title.setText("Shows");
			description
					.setText("Press the search button on top to explore your taste in shows you might like. Just search for the name of a show you liked and you will get suggestions.");
			break;
		default:
			break;
		}
		headerLayout.addView(helpLayout);
		helpLayout.setVisibility(View.VISIBLE);
	}

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

	private void setupLoadingBar() {
		progressLayout = new LinearLayout(TasteKidActivity.getAppContext());
		progressLayout.setGravity(Gravity.CENTER);
		progressLayout.setBackgroundResource(R.drawable.card_background);
		TextView loadingText = new TextView(TasteKidActivity.getAppContext());
		loadingText.setText("Loading...");
		loadingText.setTextColor(Color.BLACK);
		loadingText.setGravity(Gravity.CENTER);
		ProgressBar loadingProgressBar = new ProgressBar(TasteKidActivity.getAppContext());
		progressLayout.addView(loadingText);
		progressLayout.addView(loadingProgressBar);
		headerLayout.addView(progressLayout);
		progressLayout.setVisibility(View.GONE);
	}

	public void showLoadingBar() {
		if (Configuration.DEVMODE)
			Log.d("TasteKid", "showing loadingbar");
		progressLayout.setVisibility(View.VISIBLE);
		helpLayout.setVisibility(View.GONE);
	}

	public void hideLoadingBar() {
		if (Configuration.DEVMODE)
			Log.d("TasteKid", "hiding loadingbar");
		progressLayout.setVisibility(View.GONE);
	}
	
	private void setupHeaderButtons(View headerView, Result result) {
		LinearLayout buttonLayout = (LinearLayout) headerView
				.findViewById(R.id.buttonLayout);
		addOnClickListenerToButtons(buttonLayout, result);
	}

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

	@Override
	public void onResultsReady() {
		results = ResultManager.getResultsByPosition(position);
		info = ResultManager.getInfo();
		adapter.clear();
		adapter.addAll(results);
		updateCards();
		hideLoadingBar();
		
	}


/*	@Override
	public void onQueryComplete(ArrayList<ApiResponse> apiResponses) {
		for (ApiResponse apiResponse : apiResponses) {
			this.info = (ArrayList<Result>) apiResponse.similar.getInfo();
			this.results = (ArrayList<Result>) apiResponse.similar.getResults();
		}
		adapter.clear();
		adapter.addAll(results);
		saveResults();
		updateCards();
		hideLoadingBar();
	}
*/

/*	@Override
	public void onQueryFailed(Exception e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDefaultError(Exception e) {
		// TODO Auto-generated method stub

	}*/
}
