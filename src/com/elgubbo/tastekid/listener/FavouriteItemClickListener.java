package com.elgubbo.tastekid.listener;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.elgubbo.tastekid.DetailActivity;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.adapter.FavouriteResultArrayAdapter;
import com.elgubbo.tastekid.model.Result;

public class FavouriteItemClickListener implements OnItemClickListener{


	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		
		ListView listView = (ListView) arg0;
		FavouriteResultArrayAdapter adapter = (FavouriteResultArrayAdapter) listView.getAdapter();
		Log.d("tastekid", "clicked item at position: "+position);
		Result res = (Result) adapter.getItem(position);
		
		Intent intent = new Intent(TasteKidActivity.getActivityInstance(), DetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("result", res);

		TasteKidActivity.getAppContext().startActivity(intent);
		
	}
}
