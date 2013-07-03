package com.elgubbo.tastekid.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.annotation.SuppressLint;

/**
 * 
 * @author Alexander Reichert
 * 
 */
public final class UnsecureHttpClient {

	/**
	 * Default constructor
	 */
	private UnsecureHttpClient() {
	}

	/**
	 * Static httpClient generator
	 * 
	 * @return DefaultHttpClient
	 */
	public static DefaultHttpClient getClient() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		// http scheme
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		// https scheme this is where the UnsecureSSLSocketFactory is needed to
		// accept the self signed SSL cert
		schemeRegistry.register(new Scheme("https",
				new UnsecureSSLSocketFactory(), 443));

		// set up some parameters for the Http connection
		HttpParams params = new BasicHttpParams();
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE,
				new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		// Create a threadsafe connection manager
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);
		// finally instantiate the http client
		DefaultHttpClient client = new DefaultHttpClient(cm, params);
		return client;
	}

	/**
	 * executes a http request and returns the resultMap
	 * 
	 * @param request
	 *            HttpRequest
	 * @return the resultmap (errocode, value)
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@SuppressLint("UseSparseArrays")
	public static HashMap<Integer, String> executeForResponse(
			HttpUriRequest request) throws ClientProtocolException, IOException {
		HashMap<Integer, String> resultMap = new HashMap<Integer, String>();
		DefaultHttpClient client = UnsecureHttpClient.getClient();

		HttpResponse exec = client.execute(request);
		StatusLine statusLine = exec.getStatusLine();
		InputStream is = exec.getEntity().getContent();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
		String json = "";
		String line = "";
		while ((line = buffer.readLine()) != null) {
			json += line;
		}

		// put the errorcodes with the resulting json in the hashmap
		switch (statusLine.getStatusCode()) {
		case 200:
			resultMap.put(200, json);
			break;
		case 201:
			resultMap.put(201, json);
			break;
		case 429:
			resultMap.put(429, json);
			break;
		case 400:
			resultMap.put(400, json);
			break;
		case 403:
			resultMap.put(403, json);
			break;
		}
		return resultMap;
	}

	/**
	 * Handle errors.
	 * 
	 * @param hashMap
	 *            the hash map
	 */
	public static void handleErrors(HashMap<Integer, String> hashMap) {
		// TODO Auto-generated method stub
	}
}
