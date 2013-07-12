package com.elgubbo.tastekid.api;



import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.client.methods.HttpGet;

import com.elgubbo.tastekid.Configuration;
import com.elgubbo.tastekid.interfaces.IQueryCompleteListener;

import android.util.Log;


/**
 * The Class APIWrapper to get the information from json into the application
 * 
 * @author Alexander Reichert
 * 
 */
public class APIWrapper {

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
		String urlString = (prefix == null) ? Configuration.API_URL
				: Configuration.API_URL + prefix;
		urlString += "?" + "k=" + Configuration.API_K;
		urlString += "&" + "f=" + Configuration.API_F;
		urlString += "&" + "format=JSON";
		urlString += "&" + "verbose=1";
		// add keys to urlString if needed
		if (map.containsKey("q")) {
			urlString += "&q="
					+ map.get("q").replaceAll("_", "%20");
		}

		if (Configuration.DEVMODE) {
			Log.d("APIWRAPPER", "the url string is: " + urlString);
		}
		HttpGet get = new HttpGet(urlString);
		return get;
	}

	
	/**
	 * @param callBack
	 * @param searchQuery
	 * @param type the type can be: music, movie, show, book, game or NULL for all
	 */
	public static void getResultsForType(IQueryCompleteListener callBack, String searchQuery){
		ApiTask qt = new ApiTask(callBack);
		HashMap<String, String> argMap = new HashMap<String, String>();
		try {
			argMap.put("q", (URLEncoder.encode(searchQuery, "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		qt.execute(new HttpGet[] { buildHttpGet(null, argMap) });
	}
	





}
