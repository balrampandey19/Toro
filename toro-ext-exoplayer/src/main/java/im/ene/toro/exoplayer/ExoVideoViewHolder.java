/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.toro.exoplayer;

import android.support.annotation.CallSuper;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.view.View;
import im.ene.lab.toro.ToroAdapter;
import im.ene.lab.toro.ToroPlayer;
import im.ene.lab.toro.ToroUtil;
import im.ene.lab.toro.ToroViewHolder;

/**
 * Created by eneim on 6/11/16.
 */
public abstract class ExoVideoViewHolder extends ToroAdapter.ViewHolder
    implements ToroPlayer, ToroViewHolder {

  @NonNull protected final ExoVideoView videoView;
  protected final ExoPlayerViewHelper helper;
  private boolean playable = true; // normally true

  public ExoVideoViewHolder(View itemView) {
    super(itemView);
    videoView = findVideoView(itemView);
    if (videoView == null) {
      throw new NullPointerException("A valid ExoVideoView is required.");
    }
    helper = new ExoPlayerViewHelper(this, itemView);
    // !IMPORTANT: Helper is helpful, don't forget it.
    videoView.setPlayerCallback(helper);
  }

  protected abstract ExoVideoView findVideoView(View itemView);

  @CallSuper @Override public void onActivityActive() {

  }

  @CallSuper @Override public void onActivityInactive() {
    // Release listener to prevent memory leak
  }

  @CallSuper @Override public void onAttachedToWindow() {
    helper.onAttachedToWindow();
  }

  @CallSuper @Override public void onDetachedFromWindow() {
    helper.onDetachedFromWindow();
  }

  @Override public int getPlayOrder() {
    return getAdapterPosition();
  }

  @Override public void onPlaybackStarted() {

  }

  @Override public void onPlaybackPaused() {

  }

  @Override public void onPlaybackCompleted() {

  }

  @Override public float visibleAreaOffset() {
    return ToroUtil.visibleAreaOffset(this, itemView.getParent());
  }

  @Override public void preparePlayer(boolean playWhenReady) {
    videoView.preparePlayer(playWhenReady);
  }

  @Override public void releasePlayer() {
    videoView.releasePlayer();
  }

  // Client could override this method for better practice
  @Override public void start() {
    videoView.start();
  }

  @Override public void pause() {
    videoView.pause();
  }

  @Override public long getDuration() {
    return videoView.getDuration();
  }

  @Override public long getCurrentPosition() {
    return videoView.getCurrentPosition();
  }

  @Override public void seekTo(long pos) {
    videoView.seekTo(pos);
  }

  @Override public boolean isPlaying() {
    return videoView.isPlaying();
  }

  @Override public boolean wantsToPlay() {
    // Default implementation
    return visibleAreaOffset() >= 0.75 && playable;
  }

  @CallSuper @Override public void onVideoPrepared() {
    playable = true;
  }

  @Override public void onVideoPreparing() {

  }

  @Override public int getBufferPercentage() {
    return videoView.getBufferPercentage();
  }

  @Override public boolean onPlaybackError(Exception error) {
    playable = false;
    return true;
  }

  @Override public void stop() {
    videoView.stop();
  }

  @NonNull @Override public View getPlayerView() {
    return videoView;
  }

  @Override public void setVolume(@FloatRange(from = 0.f, to = 1.f) float volume) {
    videoView.setVolume(volume);
  }
}