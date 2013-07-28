package com.elgubbo.tastekid.adapter;

import java.sql.SQLException;

import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.model.Result;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;


public class FavouriteResultCursorAdapter extends CursorAdapter{

	public FavouriteResultCursorAdapter() {
		super(TasteKidActivity.getAppContext(), initCursor(), true);
	}
	
	private static Cursor initCursor(){
		Cursor cursor;
		Dao<Result, Integer> resultDao;
		DBHelper databaseHelper = OpenHelperManager.getHelper(
				TasteKidActivity.getAppContext(), DBHelper.class);

		// when you are done, prepare your query and build an iterator
		CloseableIterator<Result> iterator = null;
		try {
			// build your query
			resultDao = databaseHelper.getResultDao();
			QueryBuilder<Result, Integer> qb = resultDao.queryBuilder();
			qb.where().eq("favourite", true);
			iterator = resultDao.iterator(qb.prepare());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
		   // get the raw results which can be cast under Android
		   AndroidDatabaseResults results =
		       (AndroidDatabaseResults)iterator.getRawResults();
		   cursor = results.getRawCursor();
		} finally {
		   iterator.closeQuietly();
		}
		return cursor;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
