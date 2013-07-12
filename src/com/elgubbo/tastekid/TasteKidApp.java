package com.elgubbo.tastekid;

import android.app.Application;

public class TasteKidApp extends Application{

	//Static variables
	public static final int POSITION_ALL = 0;
	public static final int POSITION_MOVIE = 2;
	public static final int POSITION_MUSIC = 1;
	public static final int POSITION_BOOK = 3;
	public static final int POSITION_GAME = 4;
	public static final int POSITION_SHOW = 5;
	public static final String[] TYPE_ARRAY = { null, "music", "movie", "book",
			"game", "show" };
	
	public static ResultManager resultManager;
	private static String currentQuery;
	@Override
	public void onCreate(){
		resultManager = ResultManager.getInstance();
	}
	public static String getCurrentQuery() {
		return currentQuery;
	}
	public static void setCurrentQuery(String currentQuery) {
		TasteKidApp.currentQuery = currentQuery;
	}
	
}
