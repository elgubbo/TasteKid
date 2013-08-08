package com.elgubbo.tastekid.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.elgubbo.tastekid.Config;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;
import com.elgubbo.tastekid.model.Similar;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.octo.android.robospice.request.SpiceRequest;

public class TasteKidSpiceRequest extends SpiceRequest<ApiResponse> {

	/**
	 * Builds the url from get request
	 * 
	 * @param prefix
	 *            will be prefixed to the url
	 * @param mapcontex
	 *            a hashmap containing key,value pairs for the request url
	 * @return the HttpGet representing the get call to the api
	 */
	private static HttpGet buildHttpGet(String prefix,
			HashMap<String, String> map) {
		String urlString = (prefix == null) ? Config.API_URL
				: Config.API_URL + prefix;
		urlString += "?" + "k=" + Config.API_K;
		urlString += "&" + "f=" + Config.API_F;
		urlString += "&" + "format=JSON";
		urlString += "&" + "verbose=1";
		// add keys to urlString if needed
		if (map.containsKey("q")) {
			urlString += "&q=" + map.get("q").replaceAll("_", "%20");
		}

		if (Config.DEVMODE) {
			Log.d("APIWRAPPER", "the url string is: " + urlString);
		}
		HttpGet get = new HttpGet(urlString);
		return get;
	}

	String query;

	public TasteKidSpiceRequest(Class<ApiResponse> clazz, String query) {
		super(clazz);
		this.query = query.trim();
	}

	/**
	 * Gets the request.
	 * 
	 * @param url
	 *            the url
	 * @return the request
	 * @throws IOException
	 */
	private String getRequest(HttpGet get) throws IOException {
		// TODO implement error handling!!
		String json = "";
		HashMap<Integer, String> hashMap = UnsecureHttpClient
				.executeForResponse(get);
		if (hashMap.get(200) == null) {
			UnsecureHttpClient.handleErrors(hashMap);
		} else {
			return hashMap.get(200);
		}
		return json;
	}

	@Override
	public ApiResponse loadDataFromNetwork() throws Exception {
		ApiResponse fromDatabase = loadFromDatabase();
		if(fromDatabase != null)
			return fromDatabase;
		HashMap<String, String> argMap = new HashMap<String, String>();
		try {
			argMap.put("q", (URLEncoder.encode(this.query, "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String json = "";
		Gson gson = new Gson();
		// ApiResponse apiResponse = getRestTemplate().getForObject(
		// buildUrlString(null, argMap), ApiResponse.class );

		json = getRequest(buildHttpGet(null, argMap));

		if (Config.DEVMODE) {
			Log.d("TasteKid", "The JSON is: ");
			Log.d("TasteKid", json);
		}

		ApiResponse apiResponse = gson.fromJson(json, ApiResponse.class);
		apiResponse.query = query;
		apiResponse.created = new Timestamp(new Date().getTime());
		// Save similar
		saveToDatabase(apiResponse);
		return apiResponse;
	}

	private ApiResponse loadFromDatabase() {
		Dao<ApiResponse, Integer> apiResponseDao;
		DBHelper databaseHelper = OpenHelperManager.getHelper(
				TasteKidActivity.getAppContext(), DBHelper.class);
		Dao<Similar, Integer> similarDao;

		PreparedQuery<ApiResponse> dbQuery;
		try {
			apiResponseDao = databaseHelper.getApiResponseDao();
			similarDao = databaseHelper.getSimilarDao();
			dbQuery = apiResponseDao.queryBuilder().where().eq("query", this.query)
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
				Log.d("TasteKid", "Info in loadFromdatabase is: "+result.similar.getInfo().size());
			}
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	private boolean saveToDatabase(ApiResponse apiResponse) {
		// Save similar
		DBHelper databaseHelper = OpenHelperManager.getHelper(
				TasteKidActivity.getAppContext(), DBHelper.class);
		Dao<ApiResponse, Integer> apiResponseDao;
		Dao<Result, Integer> resultDao;
		Dao<Similar, Integer> similarDao;
		Timestamp currentTime = new Timestamp(new Date().getTime());

		try {
			apiResponseDao = databaseHelper.getApiResponseDao();
			resultDao = databaseHelper.getResultDao();
			similarDao = databaseHelper.getSimilarDao();
			apiResponse.created = currentTime;
			apiResponseDao.create(apiResponse);
			apiResponse.similar.parent = apiResponse;
			similarDao.create(apiResponse.similar);
			apiResponseDao.update(apiResponse);

			for (Result info : apiResponse.similar.getInfo()) {
				info.parent = apiResponse.similar;
				info.isInfo = true;
				info.setCreated(currentTime);
				resultDao.create(info);
			}
			for (Result result : apiResponse.similar.getResults()) {
				result.parent = apiResponse.similar;
				result.setCreated(currentTime);
				resultDao.create(result);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

}
