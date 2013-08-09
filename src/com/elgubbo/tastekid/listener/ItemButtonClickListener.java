package com.elgubbo.tastekid.listener;

import java.util.List;

import com.elgubbo.tastekid.Config;
import com.elgubbo.tastekid.R;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.model.Result;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

public class ItemButtonClickListener implements OnClickListener {

	private Result result;
	private Context appContext;

	private static final int REQ_START_STANDALONE_PLAYER = 1;
	private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

	/**
	 * @param result
	 *            the result that should be handled/displayed by this
	 *            clickListener
	 * @param appContext
	 *            the appContext to startA new Activity
	 */
	public ItemButtonClickListener(Result result, Context appContext) {

		this.result = result;
		this.appContext = appContext;
	}

	private boolean canResolveIntent(Intent intent) {
		List<ResolveInfo> resolveInfo = TasteKidActivity.getActivityInstance()
				.getPackageManager().queryIntentActivities(intent, 0);
		return resolveInfo != null && !resolveInfo.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.youtubeLinearLayout:
			TasteKidActivity activity = (TasteKidActivity) TasteKidActivity
					.getActivityInstance();
			Intent youtubeIntent = YouTubeStandalonePlayer.createVideoIntent(
					activity, Config.YOUTUBE_API_KEY, result.yID, 0,
					true, true);
			if (canResolveIntent(youtubeIntent)) {
				activity.startActivityForResult(youtubeIntent,
						REQ_START_STANDALONE_PLAYER);
			} else {
				// Could not resolve the intent - must need to install or update
				// the YouTube API service.
				YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(
						activity, REQ_RESOLVE_SERVICE_MISSING).show();
			}

			break;
		case R.id.wikiLinearLayout:
			Intent wikiIntent = new Intent(Intent.ACTION_VIEW);
			wikiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			wikiIntent.setData(Uri.parse(result.wUrl));
			appContext.startActivity(wikiIntent);
			break;
		case R.id.shareLinearLayout:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			String shareBody = "Check out this cool " + result.type
					+ " i found with the TasteKid for Android app! ";
			if (result.yUrl != null && !result.yUrl.trim().equalsIgnoreCase(""))
				shareBody += "This is the video " + result.yUrl;
			shareBody += " ,and here is the link to the wikipedia entry " + result.wUrl;
			String shareHeader = "I have a " + result.type + " recommendation for you!";
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					shareHeader);
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			appContext.startActivity(Intent.createChooser(shareIntent,
					"Share via"));
			break;
		default:
			break;
		}
	}

}
