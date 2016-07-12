// IMusicManager.aidl
package me.czmc.imusic.service;

import me.czmc.imusic.domain.MusicData;
import me.czmc.imusic.service.IOnMusicInfoListener;
// Declare any non-default types here with import statements
interface IMusicManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void newplay(int position);
    void play_pause();
    void next();
    void forward();
     void setPlayList(in List<MusicData> playList);
     List<MusicData> getPlayList();
     void seekTo(int progress);
     int getCurrentProgress();
     void registerOnMusicInfoListener(IOnMusicInfoListener mOnMusicInfoListener);
     void unregistertOnMusicInfoListener(IOnMusicInfoListener mOnMusicInfoListener);
     void showNotification(boolean isBackground);
     void callbackCurrentMusicInfo();
     void setSeekState(boolean isTrackingTouch,int progress,int mCurrentMoveTime);
     void setPlayMode(int playMode);
}
