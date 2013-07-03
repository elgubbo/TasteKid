package com.elgubbo.tastekid.api.querylistener;

import java.util.ArrayList;

import com.elgubbo.tastekid.Configuration;
import com.elgubbo.tastekid.interfaces.IQueryCompleteListener;
import com.elgubbo.tastekid.model.ApiResponse;
import android.util.Log;


public class QueryCompleteListener implements IQueryCompleteListener{


	@Override
	public void onQueryFailed(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDefaultError(Exception e){
		try {
			throw e;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void onQueryComplete(ArrayList<ApiResponse> apiResponses) {
		if(Configuration.DEVMODE)
			Log.d("TasteKid", apiResponses.get(0).similar.getInfo().get(0).name);		
	}



}
