package com.elgubbo.tastekid;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import com.elgubbo.tastekid.api.TasteKidSpiceRequest;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.interfaces.IResultsReceiver;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * The Class ResultManager. It is a central singleton handling results from
 * queries to the api
 */
public class ResultManager implements RequestListener<ApiResponse> {

	/** The instance. */
	private static ResultManager instance;

	/** The call back. */
	private static IResultsReceiver callBack;

	/** The info. */
	private static ArrayList<Result> info;

	/** The results. */
	private static ArrayList<Result> results;

	/** The old query. */
	private static String oldQuery;

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
	 * Send results for query to.
	 * 
	 * @param callback
	 *            the callback
	 * @param query
	 *            the query
	 */
	public void sendResultsForQueryTo(IResultsReceiver callback, String query) {
		callBack = callback;

		if (query.equalsIgnoreCase(""))
			return;
		if (!query.equalsIgnoreCase(oldQuery)) {
			TasteKidActivity activity = (TasteKidActivity)TasteKidActivity.getActivityInstance();
			activity.getSpiceManager().execute(new TasteKidSpiceRequest(ApiResponse.class, query), this);
		} else {
			callback.onResultsReady();
		}
		oldQuery = query;

	}

	/** The database helper. */
	private DBHelper databaseHelper;


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

	/**
	 * Results available.
	 * 
	 * @return true, if successful
	 */
	public boolean resultsAvailable() {
		return (info != null && results != null) ? true : false;
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
				return results;
			for (Result result : results) {
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


	public List<Result> getLatestXQueries(long x) {
		List<Result> queryResults = new ArrayList<Result>();
		try {
			Dao<Result, Integer> resultDao = getHelper().getResultDao();
			PreparedQuery<Result> query = resultDao.queryBuilder().limit(x)
					.where().eq("parentId", 0).prepare();
			queryResults = resultDao.query(query);
			// results = resultDao.query(resultDao.queryBuilder().orderBy("id",
			// false).join(resultDao.queryBuilder().where().isNull("parentId").pre));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Configuration.DEVMODE)
			Log.d("TasteKid",
					"Found last queries. size is: " + queryResults.size());
		return queryResults;
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




	/**
	 * Gets the info.
	 * 
	 * @return the info
	 */
	public ArrayList<Result> getInfo() {
		if (info == null)
			info = new ArrayList<Result>();
		return info;
	}

	@Override
	public void onRequestFailure(SpiceException arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestSuccess(ApiResponse apiResponse) {
		String error = null;
		if (apiResponse.error == null) {
			info = apiResponse.similar.getInfo();
			results = apiResponse.similar.getResults();
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

}
