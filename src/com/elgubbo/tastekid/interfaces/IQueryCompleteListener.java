package com.elgubbo.tastekid.interfaces;

import java.util.ArrayList;

import com.elgubbo.tastekid.model.ApiResponse;


/**
 * The listener interface for receiving queryComplete events. The class that is
 * interested in processing a queryComplete event implements this interface, and
 * the object created with that class is registered with a componentimport st.foodquest.foodquest.model.Place;
import st.foodquest.foodquest.model.Place.Wrapper;
 using the
 * component's <code>addQueryCompleteListener<code> method. When
 * the queryComplete event occurs, that object's appropriate
 * method is invoked.
 * 
 * @author alexander reichert
 * @see QueryCompleteEvent
 */
public interface IQueryCompleteListener {

	/**
	 * On query complete.
	 * 
	 * @param result
	 *            the result
	 */
	public void onQueryComplete(ArrayList<ApiResponse> apiResponses);

	/**
	 * On query failed.
	 * 
	 * @param e
	 *            the e
	 */
	public void onQueryFailed(Exception e);

	/**
	 * On default error.
	 * 
	 * @param e
	 *            the e
	 */
	public void onDefaultError(Exception e);
}
