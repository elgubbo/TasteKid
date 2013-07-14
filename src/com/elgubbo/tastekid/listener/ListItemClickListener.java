package com.elgubbo.tastekid.listener;

import com.elgubbo.tastekid.DetailActivity;
import com.elgubbo.tastekid.ResultManager;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.adapter.CardListArrayAdapter;
import com.elgubbo.tastekid.model.Result;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;


public class ListItemClickListener implements OnItemClickListener{
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		
		ListView listView = (ListView) arg0;
		HeaderViewListAdapter wrapAdapter = (HeaderViewListAdapter) listView.getAdapter();
		SwingBottomInAnimationAdapter adapter = (SwingBottomInAnimationAdapter) wrapAdapter.getWrappedAdapter();
		Log.d("tastekid", "clicked item at position: "+position);
		Result res = (position==0) ? (Result) ResultManager.getInstance().getInfo().get(0) : (Result) adapter.getItem(position-1);
		
		Intent intent = new Intent(TasteKidActivity.getActivityInstance(), DetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("result", res);

		TasteKidActivity.getAppContext().startActivity(intent);

		//initiatePopupWindow(res, listView, displayWidth, displayHeight);
		
	}
	
//	private void initiatePopupWindow(Result result, View root, int width, int height) {
//	    try {
//	    	
//	        //We need to get the instance of the LayoutInflater, use the context of this activity
//	        LayoutInflater inflater = (LayoutInflater) TasteKidActivity.getActivityInstance()
//	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	        
//	        //Inflate the view from a predefined XML layout
//	        View layout = inflater.inflate(R.layout.list_item_popup,
//	                null);
//	        //TODO dimensions must be set dynamically
//	        pw = new PopupWindow(layout, width-80, height-160, true);
//	        pw.setBackgroundDrawable(new BitmapDrawable(root.getResources()));
//	        pw.setOutsideTouchable(true);
//	        // display the popup in the center
//	        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
//	 
//	 
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	}


}
