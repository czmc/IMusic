package me.czmc.imusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import me.czmc.imusic.R;
import me.czmc.imusic.activity.PlayerActivity;
import me.czmc.imusic.domain.MusicData;
import me.czmc.imusic.utils.Constans;
import me.czmc.imusic.utils.EditUtils;


public class MusicService extends Service implements IOnMusicInfoListener {

    private RemoteViews remoteViews;
    private NotificationManager manager;
    private Notification notification;

    public enum PlayMode {
        SINGLE, LIST, SINGLE_LOOP, LIST_LOOP, RANDOM_LOOP
    }

    private MusicBinder binder = new MusicBinder();
    private final String TAG = "MusicService";
    public final int MSG_UPDATE = 0x22;
    public final int MSG_OVER = 0x23;
    public final int MSG_PAUSE = 0x24;
    public Context context;

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int requestCode = intent.getIntExtra(Constans.REQUEST_CODE, 0);
            switch (requestCode) {
                case Constans.RQC_PLAY:
                    binder.play_pause();
                    break;
                case Constans.RQC_NEXT:
                    try {
                        binder.next();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //监听电话
        TelephonyManager tmgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tmgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constans.MEDIA_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
        context = getApplicationContext();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        binder.registerOnMusicInfoListener(this);
        int playMode = 1;
        try {
            playMode = EditUtils.getValue(context, Constans.PLAY_MODE, Integer.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            binder.setPlayMode(playMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean mResumeAfterCall = false;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int ringvolume = audioManager
                        .getStreamVolume(AudioManager.STREAM_RING);
                if (ringvolume > 0) {
                    mResumeAfterCall = (binder.player.getMediaPlayer().isPlaying() || mResumeAfterCall);
                    binder.player.getMediaPlayer().pause();
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                // pause the music while a conversation is in progress
                mResumeAfterCall = (binder.player.getMediaPlayer().isPlaying() || mResumeAfterCall);
                    binder.player.pause();
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                // start playing again
                if (mResumeAfterCall) {
                    // resume playback only if music was playing
                    // when the call was answered
                        binder.player.play();
                    mResumeAfterCall = false;
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        if (binder.player != null)
            binder.player.release();
        TelephonyManager tmgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tmgr.listen(mPhoneStateListener, 0);
        super.onDestroy();
    }

    public void showNotifiCation(MusicData music, boolean isPlaying) {
        notification = new Notification();
        notification.icon = R.drawable.user_icon_default_circle;
        notification.when = System.currentTimeMillis();

        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews = new RemoteViews(getPackageName(), R.layout.notifycation_musicplayer);

        if (music != null) {
            remoteViews.setTextViewText(R.id.current_music_name, music.name);
            remoteViews.setTextViewText(R.id.current_music_author, music.artist);
            remoteViews.setImageViewUri(R.id.current_music_icon, Uri.parse(music.coverPath));
        }

        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.btn_play, R.drawable.pause_white);
            notification.flags = Notification.FLAG_NO_CLEAR;
        } else {
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            remoteViews.setImageViewResource(R.id.btn_play, R.drawable.play_white);
        }
        Intent intent1 = new Intent();
        intent1.setAction(Constans.MEDIA_ACTION);
        intent1.putExtra(Constans.REQUEST_CODE, Constans.RQC_PLAY);
        PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0x11, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_play, onClickPendingIntent);

        Intent intent2 = new Intent();
        intent2.setAction(Constans.MEDIA_ACTION);
        intent2.putExtra(Constans.REQUEST_CODE, Constans.RQC_NEXT);
        onClickPendingIntent = PendingIntent.getBroadcast(context, 0x12, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_next, onClickPendingIntent);

        notification.contentView = remoteViews;
        notification.contentIntent = pendingIntent;
        manager.notify(2, notification);
    }

    @Override
    public void OnMusicChange(MusicData music) throws RemoteException {
        if (binder.isBackground) {
            showNotifiCation(music, true);
        }
    }

    @Override
    public void onMusicPlayProgress(int progress, int currentSecond) throws RemoteException {

    }

    @Override
    public void onChangePlayState(boolean isPlaying) throws RemoteException {
        if (binder.isBackground) {
            showNotifiCation(null, isPlaying);
        }
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * 服务binder对象
     */
    public class MusicBinder extends IMusicManager.Stub implements MediaPlayer.OnCompletionListener {
        PlayMode playMode = PlayMode.LIST;
        private ArrayList<MusicData> playList;

        private boolean isTrackingTouch;

        private MusicData music;
        private int position;
        public boolean isBackground;
        private ArrayList<IOnMusicInfoListener> mOnMusicInfoListeners = new ArrayList<IOnMusicInfoListener>();
        private MusicPlayer player = new MusicPlayer(context);

        private android.os.Handler mHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_UPDATE:
                        this.sendEmptyMessageDelayed(MSG_UPDATE, 10);
                        if (!isTrackingTouch)
                            allMusicProgress(getCurrentProgress(), player.getMediaPlayer().getCurrentPosition());
                        break;
                    case MSG_OVER:
                        this.removeMessages(MSG_UPDATE);
                        allMusicProgress(0, 0);
                        break;
                    case MSG_PAUSE:
                        this.removeMessages(MSG_UPDATE);
                        break;
                }
            }
        };

        /**
         * 开始一个新的音乐播放
         *
         * @param position
         */
        @Override
        public void newplay(int position) {
            allChangePlayState(true);
            this.position = position;
            if (playList == null) return;
            if (music != null && music.equals(playList.get(position))) return;
            music = playList.get(position);
            try {
                allMusicChange(music);
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 10);
                player.play(music);
                player.getMediaPlayer().setOnCompletionListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "play music!");
        }

        /**
         * 根据状态实行按键的暂停或播放
         */
        @Override
        public void play_pause() {
            if (player.getMediaPlayer().isPlaying()) {
                allChangePlayState(false);
                player.pause();
                mHandler.sendEmptyMessage(MSG_PAUSE);
            } else {
                allChangePlayState(true);
                player.play();
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 10);
            }
        }

        /**
         * 下一曲
         *
         * @throws RemoteException
         */
        @Override
        public void next() throws RemoteException {
            if (player == null) return;
            if (playList == null) return;
            switch (playMode) {
                case SINGLE:
                case LIST:
                case SINGLE_LOOP:
                case LIST_LOOP:
                    if (position >= playList.size() - 1) {
                        position = -1;
                    }
                    newplay(++position);
                    break;
                case RANDOM_LOOP:
                    int pos = -1;
                    do {
                        pos = (int) (Math.random() * playList.size());
                    } while (pos == position);
                    position = pos;
                    newplay(position);
                    break;
            }
        }

        @Override
        public void forward() throws RemoteException {
            if (player == null) return;
            if (playList == null) return;
            if (player.getMediaPlayer().isPlaying() && player.getCurrentProgress() > 30) {
                seekTo(0);
            } else {
                switch (playMode) {
                    case SINGLE:
                    case LIST:
                    case SINGLE_LOOP:
                    case LIST_LOOP:
                        if (position < 0) {
                            position = 0;
                        }
                        newplay(--position);
                        break;
                    case RANDOM_LOOP:
                        int pos = -1;
                        do {
                            pos = (int) (Math.random() * playList.size());
                        } while (pos == position);
                        position = pos;
                        newplay(position);
                        break;
                }
            }
        }

        /**
         * 传递播放列表
         *
         * @param playList
         */
        @Override
        public void setPlayList(List<MusicData> playList) {
            this.playList = (ArrayList) playList;
        }

        /**
         * 得到播放列表
         *
         * @return
         */
        @Override
        public List<MusicData> getPlayList() {
            return playList;
        }

        /**
         * 改变进度条
         *
         * @param progress
         */
        @Override
        public void seekTo(int progress) {
            if (player == null) return;
            player.seekTo(progress);
        }

        /**
         * 得到当前的歌曲播放进度
         *
         * @return
         */
        @Override
        public int getCurrentProgress() {
            return player.getCurrentProgress();
        }

        /**
         * 歌曲信息相关改变监听器
         *
         * @param mOnMusicInfoListener
         */
        @Override
        public void registerOnMusicInfoListener(IOnMusicInfoListener mOnMusicInfoListener) {
            if (!mOnMusicInfoListeners.contains(mOnMusicInfoListener)) {
                mOnMusicInfoListeners.add(mOnMusicInfoListener);
            }
        }

        @Override
        public void unregistertOnMusicInfoListener(IOnMusicInfoListener mOnMusicInfoListener) throws RemoteException {
            if (mOnMusicInfoListeners.contains(mOnMusicInfoListener)) {
                mOnMusicInfoListeners.remove(mOnMusicInfoListener);
            }
        }

        /**
         * 根据状态是否在后台施行通知显示与否
         *
         * @param isBackground
         * @throws RemoteException
         */
        @Override
        public void showNotification(boolean isBackground) throws RemoteException {
            if (isBackground && player.getMediaPlayer().isPlaying()) {
                this.isBackground = isBackground;
                showNotifiCation(music, true);
            } else {
                this.isBackground = false;
                manager.cancel(2);
            }
        }

        @Override
        public void callbackCurrentMusicInfo() throws RemoteException {
            if (music != null)
                allMusicChange(music);
            if (player != null) {
                allChangePlayState(player.getMediaPlayer().isPlaying());
                allMusicProgress(getCurrentProgress(), player.getMediaPlayer().getCurrentPosition());
            }
        }

        @Override
        public void setSeekState(boolean isTrackingTouch, int progress, int mCurrentMoveTime) throws RemoteException {
            if (music == null) return;
            this.isTrackingTouch = isTrackingTouch;
            if (isTrackingTouch) {
                allMusicProgress(progress, mCurrentMoveTime);
            }
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            switch (playMode) {
                case 1:
                    this.playMode = PlayMode.SINGLE;
                    break;
                case 2:
                    this.playMode = PlayMode.SINGLE_LOOP;
                    break;
                case 3:
                    this.playMode = PlayMode.LIST;
                    break;
                case 4:
                    this.playMode = PlayMode.LIST_LOOP;
                    break;
                case 5:
                    this.playMode = PlayMode.RANDOM_LOOP;
                    break;
            }
        }

        /**
         * 批量监听操作
         *
         * @param music
         */
        private void allMusicChange(MusicData music) {
            if (mOnMusicInfoListeners == null) return;
            if (mOnMusicInfoListeners.isEmpty()) return;
            for (int i = 0; i < mOnMusicInfoListeners.size(); i++) {
                try {
                    mOnMusicInfoListeners.get(i).OnMusicChange(music);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        private void allMusicProgress(int progress, int currentSecond) {
            if (mOnMusicInfoListeners == null) return;
            if (mOnMusicInfoListeners.isEmpty()) return;
            for (int i = 0; i < mOnMusicInfoListeners.size(); i++) {
                try {
                    mOnMusicInfoListeners.get(i).onMusicPlayProgress(progress, currentSecond);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        private void allChangePlayState(boolean isPlaying) {
            if (mOnMusicInfoListeners == null) return;
            if (mOnMusicInfoListeners.isEmpty()) return;
            for (int i = 0; i < mOnMusicInfoListeners.size(); i++) {
                try {
                    mOnMusicInfoListeners.get(i).onChangePlayState(isPlaying);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            allChangePlayState(false);
            switch (playMode) {
                case SINGLE:
                    mHandler.sendEmptyMessage(MSG_OVER);
                    seekTo(0);
                    break;
                case SINGLE_LOOP:
                    allChangePlayState(true);
                    if (playList == null) return;
                    music = playList.get(position);
                    try {
                        allMusicChange(music);
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 10);
                        player.play(music);
                        player.getMediaPlayer().setOnCompletionListener(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case LIST_LOOP:
                    if (position >= playList.size() - 1) {
                        position = 0;
                    }
                    newplay(++position);
                    break;
                case LIST:
                    if (position >= playList.size() - 1) {
                        mHandler.sendEmptyMessage(MSG_OVER);
                    } else {
                        newplay(++position);
                    }
                    break;
                case RANDOM_LOOP:
                    int pos = -1;
                    do {
                        pos = (int) (Math.random() * playList.size());
                    } while (pos == position);
                    position = pos;
                    newplay(position);
                    break;

            }
        }
    }

}
