package com.elgubbo.tastekid.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class Similar implements Parcelable{
	
	

	@DatabaseField(canBeNull = true, foreign = true)
	public ApiResponse parent;

	@DatabaseField(generatedId = true)
	protected int _id;

	@ForeignCollectionField(eager = true)
	@SerializedName("Info")
	Collection<Result> info;

	@ForeignCollectionField(eager = true)
	@SerializedName("Results")
	Collection<Result> results;

	public static Creator<Similar> CREATOR = new Creator<Similar>() {
		public Similar createFromParcel(Parcel parcel) {
			return new Similar(parcel);
		}

		public Similar[] newArray(int size) {
			return new Similar[size];
		}
	};

	public Similar(){
		
	}

	@SuppressWarnings("unchecked")
	public Similar(Parcel p){
		this._id = p.readInt();
		this.setInfo(p.readArrayList(Result.class.getClassLoader()));
		this.setResults(p.readArrayList(Result.class.getClassLoader()));
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getId() {
		return _id;
	}

	public ArrayList<Result> getInfo() {
		return new ArrayList<Result>(this.info);
	}

	public ArrayList<Result> getResults() {
		return new ArrayList<Result>(this.results);
	}

	public void setInfo(Collection<Result> info) {
		this.info = info;
	}
	
	public void setResults(Collection<Result> results) {
		this.results = results;
	}
	
	public String toString() {
		String returnStr = "";
		returnStr += (results == null) ? 0 : results.size();
		return returnStr;
	}
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeList(getInfo());
		dest.writeList(getResults());
	}
}
