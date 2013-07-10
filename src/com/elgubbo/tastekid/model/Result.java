package com.elgubbo.tastekid.model;

import java.sql.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "result")
public class Result {
	
	@DatabaseField(generatedId = true)
	int id;
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

}
