package com.elgubbo.tastekid.listener;

import com.elgubbo.tastekid.Configuration;
import com.elgubbo.tastekid.R;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.model.Result;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class ItemButtonClickListener implements OnClickListener {

	private Result result;
	private Context appContext;

	/**
	 * @param result the result that should be handled/displayed by this clickListener
	 * @param appContext the appContext to startA new Activity
	 */
	public ItemButtonClickListener(Result result, Context appContext) {
		if (Configuration.DEVMODE)
			Log.d("TasteKid", "Created on ClickListener");
		this.result = result;
		this.appContext = appContext;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (Configuration.DEVMODE)
			Log.d("TasteKid", "Clicked: " + v.getId());
		switch (v.getId()) {
		case R.id.youtubeLinearLayout:
				Intent youtubeIntent = YouTubeStandalonePlayer.createVideoIntent((Activity) TasteKidActivity.getActivityInstance(), Configuration.YOUTUBE_API_KEY, result.yID,0, true, true);
				youtubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				appContext.startActivity(youtubeIntent);

			break;
		case R.id.wikiLinearLayout:
				Intent wikiIntent = new Intent(Intent.ACTION_VIEW);
				wikiIntent.setData(Uri.parse(result.wUrl));
				appContext.startActivity(wikiIntent);

			break;
		default:
			break;
		}
	}

}
