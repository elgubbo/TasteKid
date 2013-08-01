package com.elgubbo.tastekid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elgubbo.tastekid.model.ResultManager;

import android.app.Application;

/**
 * The Class TasteKidApp. This overrides Application to hold some static
 * variables used globally and some that should not be destroyed when an
 * activity/fragment is destroyed
 */
public class TasteKidApp extends Application {

	// Static variables
	/** The Constant POSITION_ALL. */
	public static final int POSITION_ALL = 0;

	/** The Constant POSITION_MOVIE. */
	public static final int POSITION_MOVIE = 2;

	/** The Constant POSITION_MUSIC. */
	public static final int POSITION_MUSIC = 1;

	/** The Constant POSITION_BOOK. */
	public static final int POSITION_BOOK = 3;

	/** The Constant POSITION_GAME. */
	public static final int POSITION_GAME = 4;

	/** The Constant POSITION_SHOW. */
	public static final int POSITION_SHOW = 5;

	/** The Constant TYPE_ARRAY. */
	public static final String[] TYPE_ARRAY = { null, "music", "movie", "book",
			"game", "show" };

	/** The result manager. */
	public static ResultManager resultManager;
	
	public static long ONE_WEEK_MILLIS = 604800000;
	public static long ONE_DAY_MILLIS = 86400000;
	public static long ONE_HOUR_MILLIS = 3600000;
	public static long ONE_MINUTE_MILLIS = 60000;

	/** The current query. */
	private static String currentQuery;
	

	/**
	 * A static map of possible icon resources
	 */
    public static final Map<String, Integer> ICON_MAP;
    static {
        HashMap<String, Integer> iconMapInit = new HashMap<String, Integer>();
	    iconMapInit.put("movie", R.drawable.movie);
	    iconMapInit.put("book", R.drawable.book);
	    iconMapInit.put("music", R.drawable.music);
	    iconMapInit.put("show", R.drawable.show);
	    iconMapInit.put("game", R.drawable.game);
	    iconMapInit.put("author", R.drawable.author);
        ICON_MAP = Collections.unmodifiableMap(iconMapInit);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		resultManager = ResultManager.getInstance();
	}

	/**
	 * Gets the current query.
	 * 
	 * @return the current query
	 */
	public static String getCurrentQuery() {
		return currentQuery;
	}

	/**
	 * Sets the current query.
	 * 
	 * @param currentQuery
	 *            the new current query
	 */
	public static void setCurrentQuery(String currentQuery) {
		TasteKidApp.currentQuery = currentQuery;
	}

}
