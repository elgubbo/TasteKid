package com.elgubbo.tastekid.model;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.api.TasteKidAutocompleteSpiceRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class AutoCompleteManager implements RequestListener<AutoCompleteResponse> {

	private static AutoCompleteManager instance;
	/**
	 * Gets the single instance of AutoCompleteManager.
	 * 
	 * @return single instance of ResultManager
	 */
	public static AutoCompleteManager getInstance() {
		if (instance == null)
			instance = new AutoCompleteManager();
		return instance;
	}

	private AutoCompleteResponse response;

	/**
	 * Instantiates a new result manager.
	 */
	private AutoCompleteManager() {
	}
	
	public void getAutoCompleteSuggestions(String query) {
		TasteKidActivity activity = (TasteKidActivity) TasteKidActivity
				.getActivityInstance();
		if (!query.trim().equalsIgnoreCase("")) {

			activity.getSpiceManager().execute(
					new TasteKidAutocompleteSpiceRequest(AutoCompleteResponse.class, query), this);
		}
	}
	
	public AutoCompleteResponse getResponse() {
		return response;
	}

	@Override
	public void onRequestFailure(SpiceException arg0) {
		//Fail silently, worst thing that could happen is no Suggestions are shown to the user
	}

	@Override
	public void onRequestSuccess(AutoCompleteResponse arg0) {
		this.response = arg0;
		update(null);
	}

	// Send an Intent with an action named "my-event". 
	private void update(String error) {
	  Intent intent = new Intent("update-autocomplete");
	  if(error!=null)
		  intent.putExtra("error", error);
	  intent.putStringArrayListExtra("autocompletesuggestions", response.getSuggestions());
	  LocalBroadcastManager.getInstance(TasteKidActivity.getActivityInstance()).sendBroadcast(intent);
	}

}
