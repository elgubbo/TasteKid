package com.elgubbo.tastekid.interfaces;

import com.elgubbo.tastekid.model.ResultManager;

/**
 * The Interface IResultsReceiver. Must be implemented by any
 * fragment/activity/custom class that should receive a list of results
 */
public interface IResultsReceiver {

	/**
	 * When Results are available.
	 * this gets called. results can be found in the singleton {@link ResultManager}
	 */
	public void onResultsReady();

	/**
	 * On error received.
	 * 
	 * @param error
	 *            the error
	 */
	public void onErrorReceived(String error);

}
