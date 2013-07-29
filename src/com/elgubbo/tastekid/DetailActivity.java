package com.elgubbo.tastekid;


import java.sql.SQLException;

import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.listener.ItemButtonClickListener;
import com.elgubbo.tastekid.model.Result;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.j256.ormlite.dao.Dao;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

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
		ToggleButton favourite = (ToggleButton) findViewById(R.id.favourite);
		LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayoutItem);
		ItemButtonClickListener itemButtonClickListener = new ItemButtonClickListener(result,this);
		buttonLayout.findViewById(R.id.wikiLinearLayout).setOnClickListener(itemButtonClickListener);
		buttonLayout.findViewById(R.id.shareLinearLayout).setOnClickListener(itemButtonClickListener);
		title.setText(result.name);
		favourite.setChecked(result.favourite);
		favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("TasteKid", "onCheckdChanged");
				Log.d("TasteKid", "Result id is: "+ result.getId());
		  		DBHelper db = new DBHelper();
		  		Dao<Result, Integer> resultDao;

				try {
					
					resultDao = db.getResultDao();
			  		result.favourite = isChecked;


					resultDao.update(result);

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				buttonView.setChecked(result.favourite);
				
			}
		});
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
