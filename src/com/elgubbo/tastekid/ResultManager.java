package com.elgubbo.tastekid;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.elgubbo.tastekid.api.APIWrapper;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.interfaces.IQueryCompleteListener;
import com.elgubbo.tastekid.interfaces.IResultsReceiver;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;
import com.elgubbo.tastekid.model.Similar;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

/**
 * The Class ResultManager. It is a central singleton handling results from
 * queries to the api
 */
public class ResultManager implements IQueryCompleteListener {

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
		ApiResponse dbResults = getDatabaseResultsSimilarTo(query);
		if(dbResults.similar.getResults() != null && dbResults.similar.getResults().size()>0){
			ArrayList<ApiResponse> apiResponses= new ArrayList<ApiResponse>();
			apiResponses.add(dbResults);
			this.onQueryComplete(apiResponses);
		}
		if (!query.equalsIgnoreCase(oldQuery)) {
			APIWrapper.getResultsForType(ResultManager.getInstance(), query);
		} else {
			callback.onResultsReady();
		}
		oldQuery = query;

	}

	/** The database helper. */
	private DBHelper databaseHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.elgubbo.tastekid.interfaces.IQueryCompleteListener#onQueryComplete
	 * (java.util.ArrayList)
	 */
	@Override
	public void onQueryComplete(ArrayList<ApiResponse> apiResponses) {
		String error = null;
		for (ApiResponse apiResponse : apiResponses) {
			if (apiResponse.error == null) {
				info = (ArrayList<Result>) apiResponse.similar.getInfo();
				results = (ArrayList<Result>) apiResponse.similar.getResults();
			} else {
				error = apiResponse.error;
				if (Configuration.DEVMODE)
					Log.d("TasteKid", error);
			}
		}
		if (error != null)
			callBack.onErrorReceived(error);
		else {
			callBack.onResultsReady();
			saveResults();

		}
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

	/**
	 * Save results.
	 */
	private void saveResults() {

		try {
			Dao<Result, Integer> resultDao = getHelper().getResultDao();
			int infoId = 0;
			for (Result inf : info) {
				List<Result> queryResults = resultDao.queryForMatchingArgs(inf);
				Result foundResult = (queryResults.size() > 0) ? queryResults
						.get(0) : null;
				if (foundResult == null)
					resultDao.create(inf);
				infoId = (foundResult == null) ? inf.getId() : foundResult
						.getId();
				if (Configuration.DEVMODE)
					if (foundResult != null)
						Log.d("TasteKid", "found result is: " + foundResult);
			}
			HashMap<String, Object> matchMap = new HashMap<String, Object>();
			matchMap.put("parentId", infoId);
			List<Result> resultsFromQuery = resultDao
					.queryForFieldValuesArgs(matchMap);
			if (resultsFromQuery.size() != results.size()) {
				resultDao.delete(results);
				for (Result result : results) {
					result.parentId = infoId;
					resultDao.create(result);
					if (Configuration.DEVMODE)
						Log.d("TasteKid", "created persistant result with id: "
								+ result.getId() + " and parentId: "
								+ result.parentId);
				}
			}

		} catch (SQLException e) {
			// TODO just catch this? or do something?
			e.printStackTrace();
		}
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

	public ApiResponse getDatabaseResultsSimilarTo(String query) {
		List<Result> results = null;
		ApiResponse response = new ApiResponse();

		try {
			Dao<Result, Integer> resultDao = getHelper().getResultDao();
			Result parentResult = resultDao.queryBuilder().where()
					.eq("parentId", 0).and().like("name", query + "%")
					.queryForFirst();
			if (parentResult != null)
				results = resultDao.query(resultDao.queryBuilder().where()
						.eq("parentId", parentResult.getId()).prepare());
			
			ArrayList<Result> parentResults = new ArrayList<Result>();
			parentResults.add(parentResult);
			Similar similar = new Similar();
			similar.setInfo(parentResults);
			similar.setResults(results);
			response.similar = similar;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.elgubbo.tastekid.interfaces.IQueryCompleteListener#onQueryFailed(
	 * java.lang.Exception)
	 */
	@Override
	public void onQueryFailed(Exception e) {
		if (e instanceof IOException)
			callBack.onErrorReceived("You currently have no internet connectivity, please try again, when you are on-line.");
		else
			callBack.onErrorReceived(e.toString());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.elgubbo.tastekid.interfaces.IQueryCompleteListener#onDefaultError
	 * (java.lang.Exception)
	 */
	@Override
	public void onDefaultError(Exception e) {
		// TODO Auto-generated method stub

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

}
