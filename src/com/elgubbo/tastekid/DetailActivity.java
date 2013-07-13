package com.elgubbo.tastekid;


import com.elgubbo.tastekid.listener.ItemButtonClickListener;
import com.elgubbo.tastekid.model.Result;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The Class DetailActivity.
 *
 * @author alexander reichert
 */
public class DetailActivity extends YouTubeFailureRecoveryActivity{

	/** The result. */
	private Result result;
	
	/* (non-Javadoc)
	 * @see com.google.android.youtube.player.YouTubeBaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			result = savedInstanceState.getParcelable("result");
		}
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			result = extras.getParcelable("result");
		}
		if(Configuration.DEVMODE)
			Log.d("TasteKid", "Result passed to Popup is: "+result.name);
		setContentView(R.layout.list_item_popup);
		setupViews();

	}
	
	
	/**
	 * helper method to set up views in this activity.
	 */
	private void setupViews(){
	    YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);

		//When there is a video for the current result, show the youtube player
		if(result.yID != null && !result.yID.trim().equalsIgnoreCase(""))
		    youTubeView.initialize(Configuration.YOUTUBE_API_KEY, this);
		else
			youTubeView.setVisibility(View.GONE);
		TextView title = (TextView) findViewById(R.id.title);
		TextView description = (TextView) findViewById(R.id.description);
		LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayoutItem);
		buttonLayout.findViewById(R.id.wikiLinearLayout).setOnClickListener(new ItemButtonClickListener(result, this));
		title.setText(result.name);
		description.setText(result.wTeaser);
	}
	 
	/* (non-Javadoc)
	 * @see com.google.android.youtube.player.YouTubePlayer.OnInitializedListener#onInitializationSuccess(com.google.android.youtube.player.YouTubePlayer.Provider, com.google.android.youtube.player.YouTubePlayer, boolean)
	 */
	@Override
	  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
	      boolean wasRestored) {
	    if (!wasRestored) {
	      player.cueVideo(result.yID);
	    }
	  }

	  /* (non-Javadoc)
  	 * @see com.elgubbo.tastekid.YouTubeFailureRecoveryActivity#getYouTubePlayerProvider()
  	 */
  	@Override
	  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
	    return (YouTubePlayerView) findViewById(R.id.youtube_view);
	  }
}
