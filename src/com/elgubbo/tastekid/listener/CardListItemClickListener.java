package com.elgubbo.tastekid.listener;

import com.elgubbo.tastekid.DetailActivity;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.model.Result;
import com.elgubbo.tastekid.model.ResultManager;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


public class CardListItemClickListener implements OnItemClickListener{
	

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		GridView listView = (GridView) arg0;
		SwingBottomInAnimationAdapter adapter = (SwingBottomInAnimationAdapter) listView.getAdapter();
		Result res = (Result) adapter.getItem(position);
		//Start the Detail Activity 
		Intent intent = new Intent(TasteKidActivity.getActivityInstance(), DetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("result", res);
		
		TasteKidActivity.getAppContext().startActivity(intent);

		
	}


}
