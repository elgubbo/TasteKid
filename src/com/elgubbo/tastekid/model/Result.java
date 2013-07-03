package com.elgubbo.tastekid.model;

import com.google.gson.annotations.SerializedName;

public class Result {
    	
	@SerializedName("Name")
	public String name;
	@SerializedName("Type")
	public String type;
	public String wTeaser;
	public String wUrl;
	public String yTitle;
	public String yUrl;
	public String yID;
}
