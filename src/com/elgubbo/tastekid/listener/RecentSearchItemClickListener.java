package com.elgubbo.tastekid.listener;

import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.adapter.RecentSearchesArrayAdapter;
import com.elgubbo.tastekid.model.ApiResponse;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RecentSearchItemClickListener implements OnItemClickListener{

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		
		ListView listView = (ListView) arg0;
		RecentSearchesArrayAdapter adapter = (RecentSearchesArrayAdapter) listView.getAdapter();
		Log.d("tastekid", "clicked item at position: "+position);
		ApiResponse res = (ApiResponse) adapter.getItem(position);
		
		TasteKidActivity tasteKidAcitivity = (TasteKidActivity) TasteKidActivity.getActivityInstance();
		tasteKidAcitivity.getmSearchView().setQuery(res.query, true);
		tasteKidAcitivity.getSlidingMenu().toggle();
	}

}
