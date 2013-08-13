package com.elgubbo.tastekid.model;

import java.sql.Timestamp;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * The Class ApiResponse. Model (dao) to handle an API response
 */
public class ApiResponse implements Parcelable{
	
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	public Timestamp created;
	@DatabaseField(generatedId = true)
	protected int _id;
	
	/** The similar results. */
    @DatabaseField(canBeNull = false, foreign = true)
	@SerializedName("Similar")
	public Similar similar;
	/** The error. */
	@SerializedName("Error")
	public String error;

	@DatabaseField
	public String query;
	
	public static Creator<ApiResponse> CREATOR = new Creator<ApiResponse>() {
		public ApiResponse createFromParcel(Parcel parcel) {
			return new ApiResponse(parcel);
		}

		public ApiResponse[] newArray(int size) {
			return new ApiResponse[size];
		}
	};
	
	public ApiResponse(){
		
	}
	
	public ApiResponse(Parcel p){
		this._id= p.readInt();
		this.similar = p.readParcelable(Similar.class.getClassLoader());
		this.error = p.readString();
		this.query = p.readString();
		
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public int getId() {
		return _id;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeParcelable(similar, PARCELABLE_WRITE_RETURN_VALUE);
		dest.writeString(error);
		dest.writeString(query);
	}
	

}
