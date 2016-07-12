package me.czmc.imusic.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

import me.czmc.imusic.domain.MusicData;

/**
 * Created by czmz on 15/12/6.
 */
public class MusicPlayer {
    private final String TAG = "MusicPlayer";
    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private MusicData music;
    public MusicPlayer(Context mContext){
        mMediaPlayer = new MediaPlayer();
        this.mContext = mContext;
    }

    /**
     * 数据设置
     * @param music
     * @throws IOException
     */
    public void initData(MusicData music) throws IOException{
        Uri uri =Uri.parse(music.path);
        this.music = music;
        if(mMediaPlayer!=null) {
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mMediaPlayer=new MediaPlayer();
        }
        mMediaPlayer.setDataSource(mContext, uri);
        mMediaPlayer.prepare();
        mMediaPlayer.seekTo(0);

    }
    public void pause(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }
    public void play(){
        if(mMediaPlayer!=null)
        mMediaPlayer.start();
    }
    public void seekTo(int progress){
        mMediaPlayer.seekTo((int) ((progress * 1.0f) / 5000 * music.duration));
    }
    public void play(MusicData music) throws IOException{
        initData(music);
        mMediaPlayer.start();
    }
    public void reset(){
        mMediaPlayer.reset();
    }
    public void stop(){
        mMediaPlayer.stop();
    }
    public MediaPlayer getMediaPlayer(){
        return mMediaPlayer;
    }
    public void release(){
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
    public int getCurrentProgress(){
        if(music!=null )
            return (int)(mMediaPlayer.getCurrentPosition()*1.0f/music.duration *5000);
        return 0;
    }

}
