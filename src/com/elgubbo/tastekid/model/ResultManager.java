package com.elgubbo.tastekid.model;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.elgubbo.tastekid.Configuration;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.TasteKidApp;
import com.elgubbo.tastekid.api.TasteKidSpiceRequest;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.interfaces.IResultsReceiver;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultManager. It is a central singleton handling results from
 * queries to the api
 */
public class ResultManager implements RequestListener<ApiResponse> {

	/** The instance. */
	private static ResultManager instance;

	/** The call back. */
	private static IResultsReceiver callBack;

	public static void setCallBack(IResultsReceiver callBack) {
		ResultManager.callBack = callBack;
	}

	/** The old query. */
	private static String oldQuery;

	/** The database helper. */
	private DBHelper databaseHelper;

	private ApiResponse apiResponse;

	public void setApiResponse(ApiResponse apiResponse) {
		this.apiResponse = apiResponse;
	}

	/**
	 * Gets the single instance of ResultManager.
	 * 
	 * @return single instance of ResultManager
	 */
	public static ResultManager getInstance() {
		if (instance == null)
			instance = new ResultManager();
		return instance;
	}

	/**
	 * Instantiates a new result manager.
	 */
	private ResultManager() {
		// empty private constructor for singleton pattern
	}

	/**
	 * Send results for query to.
	 * 
	 * @param callback
	 *            the callback
	 * @param query
	 *            the query
	 */
	public void sendResultsForQueryTo(IResultsReceiver callback, String query) {
		callBack = callback;

		if (!query.trim().equalsIgnoreCase(oldQuery) && !query.trim().equalsIgnoreCase("")) {
			TasteKidActivity activity = (TasteKidActivity) TasteKidActivity
					.getActivityInstance();
			activity.getSpiceManager().execute(
					new TasteKidSpiceRequest(ApiResponse.class, query), this);
		} else {
			callback.onResultsReady();
		}
		oldQuery = query;

	}

	/**
	 * Gets the helper.
	 * 
	 * @return the helper
	 */
	public DBHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(
					TasteKidActivity.getAppContext(), DBHelper.class);
		}
		return databaseHelper;
	}
	
	//TODO duplicate code to TasteKidSpiceRequest.loadfromdatabase()
	public void restoreApiResponseFromQuery(String query){
		if(query==null)
			return;
		Dao<ApiResponse, Integer> apiResponseDao;
		DBHelper databaseHelper = OpenHelperManager.getHelper(
				TasteKidActivity.getAppContext(), DBHelper.class);
		Dao<Similar, Integer> similarDao;

		PreparedQuery<ApiResponse> dbQuery;
		try {
			apiResponseDao = databaseHelper.getApiResponseDao();
			similarDao = databaseHelper.getSimilarDao();
			dbQuery = apiResponseDao.queryBuilder().where().eq("query", query)
					.prepare();
			ApiResponse result =  apiResponseDao.queryForFirst(dbQuery);
			if(result!=null){
				similarDao.refresh(result.similar);
				apiResponseDao.refresh(result);
				//TODO BAD BAD BAD - just a temporary fix.
				ArrayList<Result> newInfo = new ArrayList<Result>();
				ArrayList<Result> newResults = new ArrayList<Result>();
				for (Result res : result.similar.getInfo()) {
					if(res.isInfo)
						newInfo.add(res);
				}
				for (Result resres : result.similar.getResults()) {
					if(!resres.isInfo)
						newResults.add(resres);
				}
				result.similar.setInfo(newInfo);
				result.similar.setResults(newResults);
			}
			this.apiResponse = result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Results available.
	 * 
	 * @return true, if successful
	 */
	public boolean resultsAvailable() {
		if(apiResponse == null)
			return false;
		return (apiResponse.similar.info != null && apiResponse.similar.results != null) ? true : false;
	}

	/**
	 * Gets the results by type.
	 * 
	 * @param type
	 *            the type
	 * @return the results by type
	 */
	public ArrayList<Result> getresultsByType(String type) {
		ArrayList<Result> filteredResults = new ArrayList<Result>();

		if (resultsAvailable()) {
			if (type == null)
				return (ArrayList<Result>) apiResponse.similar.results;
			for (Result result : apiResponse.similar.results) {
				if (result.type.equalsIgnoreCase(type)) {
					filteredResults.add(result);
				}
			}
		}

		return filteredResults;
	}

	/**
	 * Gets the results by position.
	 * 
	 * @param position
	 *            the position
	 * @return the results by position
	 */
	public ArrayList<Result> getResultsByPosition(int position) {
		return getresultsByType(TasteKidApp.TYPE_ARRAY[position]);
	}

	/**
	 * Gets the info.
	 * 
	 * @return the info
	 */
	public ArrayList<Result> getInfo() {
		if(apiResponse == null)
			return new ArrayList<Result>();
		if (apiResponse.similar.info == null)
			apiResponse.similar.info = new ArrayList<Result>();
		return (ArrayList<Result>) apiResponse.similar.info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.octo.android.robospice.request.listener.RequestListener#onRequestFailure
	 * (com.octo.android.robospice.persistence.exception.SpiceException)
	 */
	@Override
	public void onRequestFailure(SpiceException arg0) {
		// TODO Auto-generated method stub

	}

	public List<Result> getFavouriteResults() {
		List<Result> results = null;
		try {
			Dao<Result, Integer> resultDao = getHelper().getResultDao();
			results = resultDao.query(resultDao.queryBuilder().where()
					.eq("favourite", true).prepare());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	public Result getResultById(int id) {
		Result result = null;
		try {
			Dao<Result, Integer> resultDao = getHelper().getResultDao();
			result = resultDao.queryForId(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public List<ApiResponse> getRecentSearches(){
		List<ApiResponse> results = new ArrayList<ApiResponse>();
		try {
			Dao<ApiResponse, Integer> apiResponseDao = getHelper().getApiResponseDao();
			QueryBuilder<ApiResponse, Integer> apiResponseQuery = apiResponseDao.queryBuilder();
			apiResponseQuery.orderBy("_id", false);
			List<ApiResponse> temp = apiResponseDao.query(apiResponseQuery.prepare());
			for(int i = 0; i<Configuration.RECENT_SEARCH_COUNT; i++){
				if(temp.size() > i)
					results.add(temp.get(i));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.octo.android.robospice.request.listener.RequestListener#onRequestSuccess
	 * (java.lang.Object)
	 */
	@Override
	public void onRequestSuccess(ApiResponse apiResponse) {
		this.apiResponse = apiResponse;;
		String error = null;
		if (apiResponse.error == null) {
			apiResponse.similar.info = apiResponse.similar.getInfo();
			apiResponse.similar.results = apiResponse.similar.getResults();
		} else {
			error = apiResponse.error;
			if (Configuration.DEVMODE)
				Log.d("TasteKid", error);
		}

		if (error != null)
			callBack.onErrorReceived(error);
		else {
			callBack.onResultsReady();
		}
	}

	public ApiResponse getApiResponse() {
		return apiResponse;
	}


}
