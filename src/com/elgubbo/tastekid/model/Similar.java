package com.elgubbo.tastekid.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Similar {



	@SerializedName("Info")
	List<Result> info;
	public List<Result> getInfo() {
		return info;
	}
	public void setInfo(List<Result> info) {
		this.info = info;
	}



	@SerializedName("Results")
	private List<Result> results;
	
	public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	

	
	public String toString(){
		String returnStr = "";
		returnStr += (results==null) ? 0 : results.size();
		return returnStr;
	}
}
