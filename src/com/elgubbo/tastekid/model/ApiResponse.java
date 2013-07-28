package com.elgubbo.tastekid.model;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * The Class ApiResponse. Model (dao) to handle an API response
 */
public class ApiResponse {
	
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	public Timestamp created;
	
	@DatabaseField(generatedId = true)
	protected int id;
	/** The similar results. */
    @DatabaseField(canBeNull = false, foreign = true)
	@SerializedName("Similar")
	public Similar similar;
	
	/** The error. */
	@SerializedName("Error")
	public String error;
	
	@DatabaseField
	public String query;
}
