package com.elgubbo.tastekid.interfaces;


public interface IResultsReceiver {

	/**
	 * When Results are available
	 * 
	 * @param result
	 *            the result
	 */
	public void onResultsReady();

	public void onErrorReceived(String error);
}
