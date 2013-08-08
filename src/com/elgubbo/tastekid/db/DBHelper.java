package com.elgubbo.tastekid.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.elgubbo.tastekid.Config;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;
import com.elgubbo.tastekid.model.Similar;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.octo.android.robospice.persistence.ormlite.RoboSpiceDatabaseHelper;

/**
 * The Class DBHelper. Contains helper methods to initialize the ORM models and
 * the database
 */
public class DBHelper extends RoboSpiceDatabaseHelper {

	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "results.db";

	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 5;

	// the DAO object we use to access the Result table
	/** The result dao. */
	private Dao<Result, Integer> resultDao = null;
	private Dao<Similar, Integer> similarDao = null;

	private Dao<ApiResponse, Integer> apiResponseDao = null;

	/**
	 * Instantiates a new dB helper.
	 */
	public DBHelper() {
		super(TasteKidActivity.getAppContext(), DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	/**
	 * Instantiates a new dB helper.
	 */
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		resultDao = null;
		similarDao = null;
		apiResponseDao = null;
	}

	/**
	 * Returns the Database Access Object (DAO) for our Result class. It will
	 * create it or just give the cached value.
	 * 
	 * @return the result dao
	 * @throws SQLException
	 *             the sQL exception
	 * @throws SQLException
	 *             the sQL exception
	 */
	public Dao<ApiResponse, Integer> getApiResponseDao() throws SQLException,
			java.sql.SQLException {
		if (apiResponseDao == null) {
			apiResponseDao = getDao(ApiResponse.class);
		}
		return apiResponseDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our Result class. It will
	 * create it or just give the cached value.
	 * 
	 * @return the result dao
	 * @throws SQLException
	 *             the sQL exception
	 * @throws SQLException
	 *             the sQL exception
	 */
	public Dao<Result, Integer> getResultDao() throws SQLException,
			java.sql.SQLException {
		if (resultDao == null) {
			resultDao = getDao(Result.class);
		}
		return resultDao;
	}

	/**
	 * Returns the Database Access Object (DAO) for our Result class. It will
	 * create it or just give the cached value.
	 * 
	 * @return the result dao
	 * @throws SQLException
	 *             the sQL exception
	 * @throws SQLException
	 *             the sQL exception
	 */
	public Dao<Similar, Integer> getSimilarDao() throws SQLException,
			java.sql.SQLException {
		if (similarDao == null) {
			similarDao = getDao(Similar.class);
		}
		return similarDao;
	}


	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 * 
	 * @param db
	 *            the db
	 * @param connectionSource
	 *            the connection source
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			if (Config.DEVMODE)
				Log.i(DBHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, ApiResponse.class);
			TableUtils.createTable(connectionSource, Similar.class);
			TableUtils.createTable(connectionSource, Result.class);

		} catch (SQLException e) {
			Log.e(DBHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#onUpgrade(android
	 * .database.sqlite.SQLiteDatabase,
	 * com.j256.ormlite.support.ConnectionSource, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			if (Config.DEVMODE)
				Log.i(DBHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Result.class, true);
			TableUtils.dropTable(connectionSource, ApiResponse.class, true);
			TableUtils.dropTable(connectionSource, Similar.class, true);

			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			if (Config.DEVMODE)
				Log.e(DBHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}
	

}
