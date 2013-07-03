package com.elgubbo.tastekid;

import java.util.ArrayList;
import com.elgubbo.tastekid.interfaces.IQueryCompleteListener;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SectionFragment extends Fragment implements IQueryCompleteListener {

	ArrayList<Result> results;
	ArrayList<Result> info;
	View rootView;
	Context appContext;
	ListView listView;
	CardListArrayAdapter adapter;
	LayoutInflater inflater;
	
	
	//Layouts
	private LinearLayout progressLayout;
	private LinearLayout headerLayout;
	private LinearLayout infoLayout;

	int position;

	public static SectionFragment init(int val, Context context) {

		SectionFragment fragment = new SectionFragment();
		fragment.position = val;
		fragment.results = new ArrayList<Result>();
		fragment.info = new ArrayList<Result>();
		fragment.appContext = context;

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		rootView = inflater.inflate(R.layout.fragment_main_layout, container,
				false);
		listView = (ListView) rootView.findViewById(R.id.cardListView);
		adapter = new CardListArrayAdapter(appContext, results);
		
		setupHeader();
		
		listView.setAdapter(adapter);
		
		
		
		Log.d("TasteKid", "creating layout");
		return rootView;
	}

	private void updateCards() {
		fillHeaderInfo(info.get(0));
		adapter.notifyDataSetChanged();
	}
	
	private void setupHeader(){
		headerLayout = new LinearLayout(appContext);
		headerLayout.setGravity(Gravity.CENTER);
		
		headerLayout.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT));
		
		infoLayout = (LinearLayout) inflater.inflate(R.layout.header_item, null);
		headerLayout.addView(infoLayout);
		infoLayout.setVisibility(View.GONE);

		setupLoadingBar();
		
		listView.addHeaderView(headerLayout);
	}
	
	public void fillHeaderInfo(Result result){
		TextView title = (TextView) infoLayout.findViewById(R.id.title);
		TextView description = (TextView) infoLayout.findViewById(R.id.description);
		title.setText(result.name);
		description.setText(result.wTeaser);
		infoLayout.setVisibility(View.VISIBLE);
	}
	
	private void setupLoadingBar(){
		progressLayout = new LinearLayout(appContext);
		progressLayout.setGravity(Gravity.CENTER);
		progressLayout.setBackgroundResource(R.drawable.card_background);
		TextView loadingText = new TextView(appContext);
		loadingText.setText("Loading...");
		loadingText.setTextColor(Color.BLACK);
		loadingText.setGravity(Gravity.CENTER);
		ProgressBar loadingProgressBar = new ProgressBar(appContext);
		progressLayout.addView(loadingText);
		progressLayout.addView(loadingProgressBar);
		headerLayout.addView(progressLayout);
		progressLayout.setVisibility(View.GONE);
	}
	
	public void showLoadingBar(){
		if(Configuration.DEVMODE)
			Log.d("TasteKid", "showing loadingbar");
		progressLayout.setVisibility(View.VISIBLE);
	}
	
	public void hideLoadingBar(){
		if(Configuration.DEVMODE)
			Log.d("TasteKid", "hiding loadingbar");
		progressLayout.setVisibility(View.GONE);
	}
	

	@Override
	public void onQueryComplete(ArrayList<ApiResponse> apiResponses) {
		for (ApiResponse apiResponse : apiResponses) {
			this.info=(ArrayList<Result>) apiResponse.similar.getInfo();
			this.results= (ArrayList<Result>) apiResponse.similar.getResults();
		}
		adapter.clear();
		adapter.addAll(results);
		updateCards();
		hideLoadingBar();
	}

	@Override
	public void onQueryFailed(Exception e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDefaultError(Exception e) {
		// TODO Auto-generated method stub

	}
}
