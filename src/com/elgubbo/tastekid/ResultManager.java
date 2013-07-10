package com.elgubbo.tastekid;

import java.sql.SQLException;
import java.util.ArrayList;

import android.util.Log;

import com.elgubbo.tastekid.api.APIWrapper;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.interfaces.IQueryCompleteListener;
import com.elgubbo.tastekid.interfaces.IResultsReceiver;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class ResultManager implements IQueryCompleteListener{

	private static ResultManager instance;
	private static IResultsReceiver callBack;
	private static ArrayList<Result> info;
	private static ArrayList<Result> results;
	private static String oldQuery;
	
	public static ResultManager getInstance(){
		if(instance == null)
			instance = new ResultManager();
		return instance;
	}

	public static void sendResultsForQueryTo(IResultsReceiver callback, String query){
		callBack = callback;
		if(query.equalsIgnoreCase(""))
			return;
		if(!query.equalsIgnoreCase(oldQuery)){
			APIWrapper.getResultsForType(ResultManager.getInstance(),
					query);
		}else{
			callback.onResultsReady();
		}
		oldQuery = query;

	}

	private DBHelper databaseHelper;
	
	@Override
	public void onQueryComplete(ArrayList<ApiResponse> apiResponses) {
		for (ApiResponse apiResponse : apiResponses) {
			info = (ArrayList<Result>) apiResponse.similar.getInfo();
			results = (ArrayList<Result>) apiResponse.similar.getResults();
		}

		
//		saveResults();
		callBack.onResultsReady();
	}
	
	public DBHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(TasteKidActivity.getAppContext(), DBHelper.class);
		}
		return databaseHelper;
	}
	
	public static ArrayList<Result> getresultsByType(String type){
		if(type == null)
			return results;
		ArrayList<Result> filteredResults = new ArrayList<Result>();
		for (Result result : results) {
			if(result.type.equalsIgnoreCase(type)){
				filteredResults.add(result);
			}
		}
		return filteredResults;
	}
	
	public static ArrayList<Result> getResultsByPosition(int position){
		return getresultsByType(TasteKidActivity.TYPE_ARRAY[position]);
	}
	
	private void saveResults() {
		
		try {
			Dao<Result, Integer> resultDao = getHelper().getResultDao();
			int infoId = 0;
			for (Result inf : info) {
				resultDao.create(inf);
				infoId = inf.getId();
			}
			for (Result result : results) {
				result.parentId = infoId;
				resultDao.create(result);
				if(Configuration.DEVMODE)
					Log.d("TasteKid", "created persistant result with id: "+result.getId()+" and parentId: "+result.parentId);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onQueryFailed(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDefaultError(Exception e) {
		// TODO Auto-generated method stub
		
	}

	public static ArrayList<Result> getInfo() {
		return info;
	}

}
