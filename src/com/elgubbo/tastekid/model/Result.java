package com.elgubbo.tastekid.model;

import java.sql.Timestamp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "result")
public class Result implements Parcelable {

	@DatabaseField(generatedId = true)
	private int _id;
	@DatabaseField
	@SerializedName("Name")
	public String name;
	@DatabaseField(canBeNull = true)
	@SerializedName("Type")
	public String type;
	@DatabaseField(canBeNull = false)
	public String wTeaser;
	@DatabaseField(canBeNull = false)
	public String wUrl;
	@DatabaseField(canBeNull = true)
	public String yTitle;
	@DatabaseField(canBeNull = true)
	public String yUrl;
	@DatabaseField(canBeNull = true)
	public String yID;
	@DatabaseField(canBeNull = true, foreign = true)
	public Similar parent;
	@DatabaseField(dataType = DataType.SERIALIZABLE)	
	Timestamp created;
	@DatabaseField
	public boolean favourite;
	@DatabaseField
	public boolean isInfo;

	public static Creator<Result> CREATOR = new Creator<Result>() {
		public Result createFromParcel(Parcel parcel) {
			return new Result(parcel);
		}

		public Result[] newArray(int size) {
			return new Result[size];
		}
	};;

	public Result() {
		// no args constructor
	}

	public Result(int id, String name, String type, String wTeaser,
			String wUrl, String yTitle, String yUrl, String yID, Similar parentId,
			Timestamp created, boolean favourite, boolean isInfo) {
		this._id = id;
		this.name = name;
		this.type = type;
		this.wTeaser = wTeaser;
		this.wUrl = wUrl;
		this.yTitle = yTitle;
		this.yUrl = yUrl;
		this.yID = yID;
		this.parent = parentId;
		this.setCreated(created);
		this.favourite = favourite;
		this.isInfo = isInfo;
	}
	
	public Result(Parcel parcel) {
		_id = parcel.readInt();
		name = parcel.readString();
		type = parcel.readString();
		wTeaser = parcel.readString();
		wUrl = parcel.readString();
		yTitle = parcel.readString();
		yUrl = parcel.readString();
		yID = parcel.readString();
		favourite = parcel.readInt() == 0 ? false : true;
		isInfo = parcel.readInt() == 0 ? false : true;;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Timestamp getCreated() {
		return created;
	}

	public int getId() {
		return _id;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public void setId(int id) {
		this._id = id;
	}

	public String toString(){
		return this.name;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(name);
		dest.writeString(type);
		dest.writeString(wTeaser);
		dest.writeString(wUrl);
		dest.writeString(yTitle);
		dest.writeString(yUrl);
		dest.writeString(yID);
		dest.writeInt(favourite ? 1 : 0);
		dest.writeInt(isInfo ? 1 : 0);
		// todo think of date solution
	}

}
