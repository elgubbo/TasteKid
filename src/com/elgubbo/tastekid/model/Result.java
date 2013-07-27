package com.elgubbo.tastekid.model;

import java.sql.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "result")
public class Result implements Parcelable {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	@SerializedName("Name")
	public String name;
	@DatabaseField(canBeNull = true)
	@SerializedName("Type")
	public String type;
	@DatabaseField(canBeNull = true)
	public String wTeaser;
	@DatabaseField(canBeNull = true)
	public String wUrl;
	@DatabaseField(canBeNull = true)
	public String yTitle;
	@DatabaseField(canBeNull = true)
	public String yUrl;
	@DatabaseField(canBeNull = true)
	public String yID;
	@DatabaseField(canBeNull = true)
	public int parentId;
	@DatabaseField
	Date created;
	@DatabaseField
	public boolean favourite;

	public Result() {
		// no args constructor
	};

	public Result(int id, String name, String type, String wTeaser,
			String wUrl, String yTitle, String yUrl, String yID, int parentId,
			Date created, boolean favourite) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.wTeaser = wTeaser;
		this.wUrl = wUrl;
		this.yTitle = yTitle;
		this.yUrl = yUrl;
		this.yID = yID;
		this.parentId = parentId;
		this.created = created;
		this.favourite = favourite;
	}

	public Result(Parcel parcel) {
		name = parcel.readString();
		type = parcel.readString();
		wTeaser = parcel.readString();
		wUrl = parcel.readString();
		yTitle = parcel.readString();
		yUrl = parcel.readString();
		yID = parcel.readString();
		parentId = parcel.readInt();

	}
	
	public String toString(){
		return this.name;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(type);
		dest.writeString(wTeaser);
		dest.writeString(wUrl);
		dest.writeString(yTitle);
		dest.writeString(yUrl);
		dest.writeString(yID);
		dest.writeInt(parentId);
		// todo think of date solution
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static Creator<Result> CREATOR = new Creator<Result>() {
		public Result createFromParcel(Parcel parcel) {
			return new Result(parcel);
		}

		public Result[] newArray(int size) {
			return new Result[size];
		}
	};

}
