package com.elgubbo.tastekid.model;

import com.google.gson.annotations.SerializedName;

/**
 * The Class ApiResponse. Model (dao) to handle an API response
 */
public class ApiResponse {
	
	/** The similar results. */
	@SerializedName("Similar")
	public Similar similar;
	
	/** The error. */
	@SerializedName("Error")
	public String error;
}
