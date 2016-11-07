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

package im.ene.toro;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by eneim on 2/1/16.
 *
 * A helper class to support Video's callbacks from {@link RecyclerView.Adapter}
 */
public abstract class PlayerViewHelper implements Handler.Callback {

  @SuppressWarnings("unused") static final String TAG = "TORO:Helper";

  protected final ToroPlayer player;
  protected final View itemView;

  private Handler playerHandler;

  public PlayerViewHelper(@NonNull ToroPlayer player, @NonNull View itemView) {
    this.player = player;
    this.itemView = itemView;
  }

  /* BEGIN: Callback for View */

  /**
   * Callback from {@link RecyclerView.Adapter#onViewAttachedToWindow(RecyclerView.ViewHolder)}
   */
  @CallSuper public void onAttachedToWindow() {
    if (playerHandler == null) {
      playerHandler = new Handler(this);
    }

    playerHandler.removeCallbacksAndMessages(null);
    playerHandler.sendEmptyMessageDelayed(MSG_ATTACHED, MSG_DELAY);

    //player.preparePlayer(false);
    //ToroScrollListener listener = itemView.getParent() != null ?  //
    //    Toro.sInstance.mListeners.get(itemView.getParent().hashCode()) : null;
    //if (listener != null && listener.getManager().getPlayer() == null) {
    //  if (player.wantsToPlay() && Toro.getStrategy().allowsToPlay(player, itemView.getParent())) {
    //    listener.getManager().setPlayer(player);
    //    listener.getManager().restoreVideoState(player.getMediaId());
    //    listener.getManager().startPlayback();
    //  }
    //}
  }

  /**
   * Callback from {@link RecyclerView.Adapter#onViewDetachedFromWindow(RecyclerView.ViewHolder)}
   */
  @CallSuper public void onDetachedFromWindow() {
    if (playerHandler != null) {
      playerHandler.removeCallbacksAndMessages(null);
      playerHandler.sendEmptyMessage(MSG_DETACHED); // send the message immediately
    }

    //ToroScrollListener listener = itemView.getParent() != null ?  //
    //    Toro.sInstance.mListeners.get(itemView.getParent().hashCode()) : null;
    //// Manually save Video state
    //if (listener != null && player.equals(listener.getManager().getPlayer())) {
    //  if (player.isPlaying()) {
    //    listener.getManager().saveVideoState( //
    //        player.getMediaId(), player.getCurrentPosition(), player.getDuration());
    //    listener.getManager().pausePlayback();
    //  }
    //  // Detach current Player
    //  listener.getManager().setPlayer(null);
    //}
    //// Release player.
    //player.releasePlayer();
  }

  /* BEGIN: Callback for BaseMediaPlayer */

  /**
   * @param itemView main View of current ViewHolder
   * @param parent parent which holds current ViewHolder
   */
  @CallSuper protected void onPrepared(@NonNull View itemView, @Nullable ViewParent parent) {
    Toro.sInstance.onVideoPrepared(this.player, itemView, parent);
  }

  @Nullable protected final MediaPlayerManager getPlayerManager(ViewParent parent) {
    ToroScrollListener listener =
        parent != null ? Toro.sInstance.mListeners.get(parent.hashCode()) : null;
    return listener == null ? null : listener.getManager();
  }

  /**
   * Complete the playback
   */
  @CallSuper protected void onCompletion() {
    Toro.sInstance.onPlaybackCompletion(this.player);
  }

  protected final boolean onPlaybackError(Exception error) {
    return this.player.onPlaybackError(error) &&  //
        Toro.sInstance.onPlaybackError(this.player, error);
  }

  private static final int MSG_ATTACHED = 1;

  private static final int MSG_DETACHED = 2;

  private static final int MSG_DELAY = 150;

  @Override public boolean handleMessage(Message msg) {
    boolean handled;
    ToroScrollListener listener;
    switch (msg.what) {
      case MSG_ATTACHED:
        player.preparePlayer(false);
        listener = itemView.getParent() != null ?  //
            Toro.sInstance.mListeners.get(itemView.getParent().hashCode()) : null;
        if (listener != null && listener.getManager().getPlayer() == null) {
          if (player.wantsToPlay() && Toro.getStrategy()
              .allowsToPlay(player, itemView.getParent())) {
            listener.getManager().setPlayer(player);
            listener.getManager().restoreVideoState(player.getMediaId());
            listener.getManager().startPlayback();
          }
        }
        handled = true;
        break;
      case MSG_DETACHED:
        listener = itemView.getParent() != null ?  //
            Toro.sInstance.mListeners.get(itemView.getParent().hashCode()) : null;
        // Manually save Video state
        if (listener != null && player.equals(listener.getManager().getPlayer())) {
          if (player.isPlaying()) {
            listener.getManager().saveVideoState( //
                player.getMediaId(), player.getCurrentPosition(), player.getDuration());
            listener.getManager().pausePlayback();
          }
          // Detach current Player
          listener.getManager().setPlayer(null);
        }
        // Release player.
        player.releasePlayer();
        handled = true;
        break;
      default:
        handled = false;
        break;
    }

    return handled;
  }
}
