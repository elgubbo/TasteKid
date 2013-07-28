package com.elgubbo.tastekid.model;

import java.util.ArrayList;
import java.util.Collection;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class Similar {

	@DatabaseField(canBeNull = true, foreign = true)
	public ApiResponse parent;

	@DatabaseField(generatedId = true)
	protected int id;
	
	public int getId() {
		return id;
	}


    @ForeignCollectionField(eager = true)
	@SerializedName("Info")
	Collection<Result> info;
	public ArrayList<Result> getInfo() {
		Log.d("TasteKid", "Info in Similar is: "+info.size());
		ArrayList<Result> infoList = new ArrayList<Result>();
		infoList.addAll(this.info);
		return infoList;
	}




    @ForeignCollectionField(eager = true)
	@SerializedName("Results")
	Collection<Result> results;
	
	public ArrayList<Result> getResults() {
		ArrayList<Result> resultList = new ArrayList<Result>();
		resultList.addAll(results);
		return resultList;	}

	

	
	public String toString(){
		String returnStr = "";
		returnStr += (results==null) ? 0 : results.size();
		return returnStr;
	}
}
