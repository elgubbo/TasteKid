package com.elgubbo.tastekid;

import java.sql.SQLException;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.elgubbo.tastekid.adapter.FavouriteResultArrayAdapter;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.listener.ItemButtonClickListener;
import com.elgubbo.tastekid.model.Result;
import com.elgubbo.tastekid.model.ResultManager;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.j256.ormlite.dao.Dao;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The Class DetailActivity.
 * 
 * @author alexander reichert
 */
public class DetailActivity extends YouTubeFailureRecoveryActivity {

	/** The result. */
	private Result result;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.elgubbo.tastekid.YouTubeFailureRecoveryActivity#getYouTubePlayerProvider
	 * ()
	 */
	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		// return (YouTubePlayerView) findViewById(R.id.youtube_view);

		return null;
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.right_slide_in,
				R.anim.bottom_side_slide_in);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.android.youtube.player.YouTubeBaseActivity#onCreate(android
	 * .os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		overridePendingTransition(R.anim.bottom_side_slide_out,
				R.anim.right_slide_out);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		//restore state when device is turned or activity is destroyed
		if (savedInstanceState != null) {
			result = savedInstanceState.getParcelable("result");
		}
		//get the data for this activity from the bundle in the intent
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			result = extras.getParcelable("result");
		}
		//layout
		setContentView(R.layout.activity_detail);
		setupViews();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
		getSupportMenuInflater().inflate(R.menu.detail, menu);

		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.menu_item_share);

		// Fetch and store ShareActionProvider
		ShareActionProvider mShareActionProvider = (ShareActionProvider) item
				.getActionProvider();

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		String shareBody = "Check out this cool " + result.type
				+ " i found with the TasteKid for Android app! ";
		if (result.yUrl != null && !result.yUrl.trim().equalsIgnoreCase(""))
			shareBody += "This is the trailer " + result.yUrl;
		shareBody += " ,and here is the link to the wikipedia entry "
				+ result.wUrl;
		String shareHeader = "I have a " + result.type
				+ " recommendation for you!";
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareHeader);
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		mShareActionProvider.setShareIntent(shareIntent);
		// Return true to display menu
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.android.youtube.player.YouTubePlayer.OnInitializedListener
	 * #onInitializationSuccess
	 * (com.google.android.youtube.player.YouTubePlayer.Provider,
	 * com.google.android.youtube.player.YouTubePlayer, boolean)
	 */
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.cueVideo(result.yID);
		}
		player.setShowFullscreenButton(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
		b.putParcelable("result", this.result);
	}

	/**
	 * helper method to set up views in this activity.
	 */
	private void setupViews() {
		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
	
		// When there is a video for the current result, show the youtube player
		if (result.yID != null && !result.yID.trim().equalsIgnoreCase("")) {
			 youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
		}
		else
			youTubeView.setVisibility(View.GONE);
		
		TextView title = (TextView) findViewById(R.id.title);
		TextView description = (TextView) findViewById(R.id.description);
		CheckBox favourite = (CheckBox) findViewById(R.id.favourite);
		LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayoutItem);
		ItemButtonClickListener itemButtonClickListener = new ItemButtonClickListener(
				result, this);
		buttonLayout.findViewById(R.id.wikiLinearLayout).setOnClickListener(
				itemButtonClickListener);
		buttonLayout.findViewById(R.id.shareLinearLayout).setOnClickListener(
				itemButtonClickListener);
		title.setText(result.name);
		favourite.setChecked(result.favourite);
		favourite
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Log.d("TasteKid", "onCheckdChanged");
						Log.d("TasteKid", "Result id is: " + result.getId());
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
						TasteKidActivity tastekidActivity = (TasteKidActivity) TasteKidActivity
								.getActivityInstance();
						FavouriteResultArrayAdapter adapter = (FavouriteResultArrayAdapter) tastekidActivity
								.getFavouriteListView().getAdapter();
						adapter.clear();
						for (Result result : ResultManager.getInstance().getFavouriteResults()) {
							adapter.add(result);
						}
						adapter.notifyDataSetChanged();
					}
				});
		description.setText(result.wTeaser);
		ImageView iconView = (ImageView) findViewById(R.id.iconView);
		iconView.setBackgroundResource(TasteKidApp.ICON_MAP.get(result.type) != null ? TasteKidApp.ICON_MAP
				.get(result.type) : 0);
	}

}
