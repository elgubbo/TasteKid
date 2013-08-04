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

	public int getId() {
		return _id;
	}

	@ForeignCollectionField(eager = true)
	@SerializedName("Info")
	Collection<Result> info;

	public void setInfo(Collection<Result> info) {
		this.info = info;
	}

	public ArrayList<Result> getInfo() {
		return new ArrayList<Result>(this.info);
	}

	@ForeignCollectionField(eager = true)
	@SerializedName("Results")
	Collection<Result> results;

	public void setResults(Collection<Result> results) {
		this.results = results;
	}

	public ArrayList<Result> getResults() {
		return new ArrayList<Result>(this.results);
	}

	public String toString() {
		String returnStr = "";
		returnStr += (results == null) ? 0 : results.size();
		return returnStr;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeList(getInfo());
		dest.writeList(getResults());
	}
	
	@SuppressWarnings("unchecked")
	public Similar(Parcel p){
		this._id = p.readInt();
		this.setInfo(p.readArrayList(Result.class.getClassLoader()));
		this.setResults(p.readArrayList(Result.class.getClassLoader()));
	}
	
	public Similar(){
		
	}
	
	
	public static Creator<Similar> CREATOR = new Creator<Similar>() {
		public Similar createFromParcel(Parcel parcel) {
			return new Similar(parcel);
		}

		public Similar[] newArray(int size) {
			return new Similar[size];
		}
	};
}
