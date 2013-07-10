package com.elgubbo.tastekid;

import com.elgubbo.tastekid.model.Result;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class ItemButtonClickListener implements OnClickListener {

	private Result result;
	private Context appContext;

	public ItemButtonClickListener(Result result, Context appContext) {
		if (Configuration.DEVMODE)
			Log.d("TasteKid", "Created on ClickListener");
		this.result = result;
		this.appContext = appContext;
	}

	@Override
	public void onClick(View v) {
		if (Configuration.DEVMODE)
			Log.d("TasteKid", "Clicked: " + v.getId());
		switch (v.getId()) {
		case R.id.youtubeLinearLayout:
				Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("vnd.youtube:" + result.yID));
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
