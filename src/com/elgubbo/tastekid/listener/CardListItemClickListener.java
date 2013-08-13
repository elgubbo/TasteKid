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
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;


public class CardListItemClickListener implements OnItemClickListener{
	

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		
		ListView listView = (ListView) arg0;
		HeaderViewListAdapter wrapAdapter = (HeaderViewListAdapter) listView.getAdapter();
		SwingBottomInAnimationAdapter adapter = (SwingBottomInAnimationAdapter) wrapAdapter.getWrappedAdapter();
		if(adapter==null || position-1 < 0)
			return;
		Result res = (position==0) ? (Result) ResultManager.getInstance().getInfo().get(0) : (Result) adapter.getItem(position-1);
		
		
		//Start the Detail Activity 
		Intent intent = new Intent(TasteKidActivity.getActivityInstance(), DetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("result", res);
		
		TasteKidActivity.getAppContext().startActivity(intent);

		
	}


}
