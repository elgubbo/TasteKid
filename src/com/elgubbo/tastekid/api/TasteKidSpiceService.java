package com.elgubbo.tastekid.api;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;
import com.elgubbo.tastekid.model.Similar;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.ormlite.InDatabaseObjectPersisterFactory;

public class TasteKidSpiceService extends SpiceService {
//    private static final int WEBSERVICES_TIMEOUT = 10000;

	@Override
	public CacheManager createCacheManager(Application application) {
		CacheManager cacheManager = new CacheManager();
		List<Class<?>> classCollection = new ArrayList<Class<?>>();

		// add persisted classes to class collection
		classCollection.add(ApiResponse.class);
		classCollection.add(Similar.class);
		classCollection.add(Result.class);

		// init
		DBHelper databaseHelper = new DBHelper();
		InDatabaseObjectPersisterFactory inDatabaseObjectPersisterFactory = new InDatabaseObjectPersisterFactory(
				application, databaseHelper, classCollection);
		cacheManager.addPersister(inDatabaseObjectPersisterFactory);
		return cacheManager;
	}


}
