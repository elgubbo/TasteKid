package com.elgubbo.tastekid;

import java.sql.SQLException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elgubbo.tastekid.adapter.FavouriteResultArrayAdapter;
import com.elgubbo.tastekid.db.DBHelper;
import com.elgubbo.tastekid.listener.ItemButtonClickListener;
import com.elgubbo.tastekid.model.Result;
import com.elgubbo.tastekid.model.ResultManager;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.j256.ormlite.dao.Dao;

public class DetailFragment extends Fragment implements OnInitializedListener {

	private static final int RECOVERY_DIALOG_REQUEST = 1;

	/** The result. */
	private Result result;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.android.youtube.player.YouTubeBaseActivity#onCreate(android
	 * .os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (savedInstanceState != null) {
			result = savedInstanceState.getParcelable("result");
		}
		Bundle extras = getArguments();
		if (extras != null) {
			result = extras.getParcelable("result");
		}
		if (Configuration.DEVMODE)
			Log.d("TasteKid", "Result passed to Popup is: " + result.name);
		View view = inflater
				.inflate(R.layout.activity_detail, container, false);
		setupViews(view);
		return view;
	}

	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider,
			YouTubeInitializationResult errorReason) {
		if (errorReason.isUserRecoverableError()) {
			errorReason.getErrorDialog(
					(TasteKidActivity) TasteKidActivity.getActivityInstance(),
					RECOVERY_DIALOG_REQUEST).show();
		} else {
			// TODO add string resource
			String errorMessage = String.format(
					"Error with the youtube Player", errorReason.toString());
			Toast.makeText(TasteKidActivity.getAppContext(), errorMessage,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.cueVideo(result.yID);
		}
	}

	/**
	 * helper method to set up views in this activity.
	 */
	private void setupViews(View v) {
//		YouTubePlayerView youTubeView = (YouTubePlayerView) v
//				.findViewById(R.id.youtube_view);

	    YouTubePlayerSupportFragment youTubeView =
	        (YouTubePlayerSupportFragment) getFragmentManager().findFragmentById(R.id.youtube_view);
		// When there is a video for the current result, show the youtube player
		if (result.yID != null && !result.yID.trim().equalsIgnoreCase(""))
			youTubeView.initialize(Configuration.YOUTUBE_API_KEY, this);

		TextView title = (TextView) v.findViewById(R.id.title);
		TextView description = (TextView) v.findViewById(R.id.description);
		CheckBox favourite = (CheckBox) v.findViewById(R.id.favourite);
		LinearLayout buttonLayout = (LinearLayout) v
				.findViewById(R.id.buttonLayoutItem);
		ItemButtonClickListener itemButtonClickListener = new ItemButtonClickListener(
				result, TasteKidActivity.getAppContext());
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
						adapter.addAll(ResultManager.getInstance()
								.getFavouriteResults());
						adapter.notifyDataSetChanged();
					}
				});
		description.setText(result.wTeaser);

		ImageView iconView = (ImageView) v.findViewById(R.id.iconView);
		iconView.setBackgroundResource((result.type != null) ? TasteKidApp.ICON_MAP
				.get(result.type) : 0);
	}

}
