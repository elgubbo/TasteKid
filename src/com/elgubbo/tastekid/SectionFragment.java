package com.elgubbo.tastekid;

import java.util.ArrayList;
import java.util.List;

import com.elgubbo.tastekid.interfaces.IQueryCompleteListener;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class SectionFragment extends Fragment implements IQueryCompleteListener {

	ArrayList<Result> results;
	ArrayList<Result> info;
	View rootView;
	Context appContext;
	ListView listView;
	CardListArrayAdapter adapter;
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
		rootView = inflater.inflate(R.layout.fragment_main_layout, container,
				false);
		listView = (ListView) rootView.findViewById(R.id.cardListView);
		adapter = new CardListArrayAdapter(appContext, results);
		listView.setAdapter(adapter);

		Log.d("TasteKid", "creating layout");
		return rootView;
	}

	private void updateCards() {
		adapter.notifyDataSetChanged();
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
