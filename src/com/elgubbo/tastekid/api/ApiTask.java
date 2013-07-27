package com.elgubbo.tastekid.api;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;


import com.elgubbo.tastekid.Configuration;
import com.elgubbo.tastekid.interfaces.IQueryCompleteListener;
import com.elgubbo.tastekid.model.ApiResponse;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

/**
 * The Class APITask to get Info form json (in async)
 * 
 * @author Alexander Reichert
 */
@SuppressLint("UseSparseArrays")
public class ApiTask extends
	AsyncTask<HttpGet, Integer, ArrayList<ApiResponse>> {

	/** The call back. */
	private final IQueryCompleteListener callBack;

	/** The exception. */
	private Exception exception;

	/**
	 * Instantiates a new query task.
	 * 
	 * @param listener
	 *            the listener
	 */
	public ApiTask(IQueryCompleteListener listener) {
		this.callBack = listener;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[]) do a HttpGet request
	 * in the background and get the resulting Json String
	 */
	@Override
	protected ArrayList<ApiResponse> doInBackground(HttpGet... params) {
		String json = "";
		ArrayList<ApiResponse> apiResponses = new ArrayList<ApiResponse>();
		Gson gson = new Gson();

		for (HttpGet get : params) {
			try {
				json = getRequest(get);
				if(Configuration.DEVMODE){
					Log.d("TasteKid", "The JSON is: ");
					Log.d("TasteKid", json);
				}
				apiResponses.add(gson.fromJson(json, ApiResponse.class));
			} catch (ClientProtocolException e) {
				if (exception == null)
					exception = e;
			} catch (IOException e) {
				if (exception == null)
					exception = e;
				e.printStackTrace();
			}
		}
		return apiResponses;
	}

	/*tag
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(ArrayList<ApiResponse> apiResponses) {
		if (exception != null) {
			if (exception instanceof IOException) {
				IOException e = (IOException) exception;
				callBack.onQueryFailed(e);
			} else {
				callBack.onDefaultError(exception);
			}
		} else {
			callBack.onQueryComplete(apiResponses);
		}
	}
}
