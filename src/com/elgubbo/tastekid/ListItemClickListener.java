package com.elgubbo.tastekid;

import com.elgubbo.tastekid.model.Result;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;


public class ListItemClickListener implements OnItemClickListener{
	
	private PopupWindow pw;
	private Context appContext;
	private ViewGroup rootView;
	public ListItemClickListener(Context ctx, ViewGroup rootView){
		this.appContext = ctx;
		this.rootView = rootView;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		
		ListView listView = (ListView) arg0;
		HeaderViewListAdapter wrapAdapter = (HeaderViewListAdapter) listView.getAdapter();
		CardListArrayAdapter adapter = (CardListArrayAdapter) wrapAdapter.getWrappedAdapter();

		Result res = (Result) adapter.getItem(position);
		if(res != null)
			Log.d("TasteKid", res.name);
		
		WindowManager wm = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		Point size = new Point();
		display.getSize(size);
		int displayWidth = size.x;
		int displayHeight = size.y;
		
		initiatePopupWindow(res, listView, displayWidth, displayHeight);
		
	}
	
	private void initiatePopupWindow(Result result, View root, int width, int height) {
	    try {
	        //We need to get the instance of the LayoutInflater, use the context of this activity
	        LayoutInflater inflater = (LayoutInflater) appContext
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        //Inflate the view from a predefined XML layout
	        View layout = inflater.inflate(R.layout.list_item,
	                null);
	        // create a 300px width and 470px height PopupWindow
	        pw = new PopupWindow(layout, width-80, height-160, true);
	        // display the popup in the center
	        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
	 
	 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


}
