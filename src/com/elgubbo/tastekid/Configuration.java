package com.elgubbo.tastekid;


/**
 * This class is holding all app specific configurations
 * 
 * @author Alexander Reichert
 */
public abstract class Configuration {
	/*
	 * GLOBAL APP SETTINGS
	 */

	/**
	 * This value toggles the app specific logging on and off
	 */
	public static final boolean DEVMODE = true;


	/*
	 * API SETTINGS
	 */

	/** The Constant API_KEY. */
	public static final String API_K = "otjhzwnmmtni";
	public static final String API_F = "tasteki1113";
	// the url of the API
	/** The Constant API_URL. */
	public static final String API_URL = "http://www.tastekid.com/ask/ws/";


	/**
	 * Default settings for ListView
	 * 
	 */
	public final static int DEFAULT_SHOWN_RESTAURANT_NUMBER = 3;

	/*
	 * SEARCH UIs
	 */

	/**
	 * Threshold in milliseconds when to submit a query after user stops typing
	 */
	public final static int SEARCH_INPUT_THRESHOLD = 500;

	/**
	 * Number of maximum results delivered by the GeoCoder
	 */
	public final static int SEARCH_GEOCODER_MAX_RESULTS = 20;

	/**
	 * border radius of list view in search location UI
	 */
	public final static float SEARCH_LOCATION_ITEM_BORDER_RADIUS = 20.0f;

	/**
	 * UserCredential settings for database
	 */
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String USERID = "userid";

}
