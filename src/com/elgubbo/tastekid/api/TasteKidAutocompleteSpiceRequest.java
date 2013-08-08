package com.elgubbo.tastekid.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.elgubbo.tastekid.Config;
import com.elgubbo.tastekid.model.AutoCompleteResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.request.SpiceRequest;

public class TasteKidAutocompleteSpiceRequest extends
		SpiceRequest<AutoCompleteResponse> {

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

	public TasteKidAutocompleteSpiceRequest(Class<AutoCompleteResponse> clazz,
			String query) {
		super(clazz);
		this.query = query;
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
	public AutoCompleteResponse loadDataFromNetwork() throws Exception {
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

		json = getRequest(buildHttpGet("autocomplete", argMap));
		
		Type collectionType = new TypeToken<Collection<String>>(){}.getType();
		Collection<String> strings = gson.fromJson(json, collectionType);
		ArrayList<String> suggestions = new ArrayList<String>(strings);
		AutoCompleteResponse response = new AutoCompleteResponse();
		response.setSuggestions(suggestions);

		return response;
	}
}
