/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elgubbo.tastekid;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import android.content.Intent;
import android.widget.Toast;

/**
 * An abstract activity which deals with recovering from errors which may occur during API
 * initialization, but can be corrected through user action.
 */
public abstract class YouTubeFailureRecoveryActivity extends SherlockYouTubeBaseActivity implements
    YouTubePlayer.OnInitializedListener {

  /** The Constant RECOVERY_DIALOG_REQUEST. */
  private static final int RECOVERY_DIALOG_REQUEST = 1;

  /**
   * Gets the you tube player provider.
   *
   * @return the you tube player provider
   */
  protected abstract YouTubePlayer.Provider getYouTubePlayerProvider();

  /* (non-Javadoc)
   * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RECOVERY_DIALOG_REQUEST) {
      // Retry initialization if user performed a recovery action
      getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
    }
  }

  /* (non-Javadoc)
   * @see com.google.android.youtube.player.YouTubePlayer.OnInitializedListener#onInitializationFailure(com.google.android.youtube.player.YouTubePlayer.Provider, com.google.android.youtube.player.YouTubeInitializationResult)
   */
  @Override
  public void onInitializationFailure(YouTubePlayer.Provider provider,
      YouTubeInitializationResult errorReason) {
    if (errorReason.isUserRecoverableError()) {
      errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
    } else {
    	//TODO add string resource
      String errorMessage = String.format("Error with the youtube Player", errorReason.toString());
      Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
  }

}
