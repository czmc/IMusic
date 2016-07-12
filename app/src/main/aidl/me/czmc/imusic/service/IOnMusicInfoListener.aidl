// IOnMusicInfoListener.aidl
package me.czmc.imusic.service;

// Declare any non-default types here with import statements
import me.czmc.imusic.domain.MusicData;
interface IOnMusicInfoListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
            void OnMusicChange(out MusicData music);
            void onMusicPlayProgress(int progress,int currentSecond);
            void onChangePlayState(boolean isPlaying);
}
