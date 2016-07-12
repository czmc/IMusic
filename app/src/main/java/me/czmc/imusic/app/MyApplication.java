package me.czmc.imusic.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import me.czmc.imusic.R;
import me.czmc.imusic.domain.MusicData;
import me.czmc.imusic.proxy.DialogProxy;
import me.czmc.imusic.service.IMusicManager;
import me.czmc.imusic.service.MusicService;


/**
 * Created by czmz on 15/12/4.
 */
public class MyApplication extends com.activeandroid.app.Application {
    private boolean firstLaunch = true;
    private IMusicManager musicBinder;
    private MusicData music = null;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = IMusicManager.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBinder = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        Intent serviceIntent = new Intent(this, MusicService.class);
        this.bindService(serviceIntent, conn, BIND_AUTO_CREATE);
        initImageLoader(getApplicationContext());
        initDialogRes();
    }
    public void initDialogRes(){
        DialogProxy.setMsgDialogLayoutRes(R.layout.layout_msg_dialog);
        DialogProxy.setMsgDialogTheme(R.style.Custom_Dialog_Dim);
        DialogProxy.setProgressDialogLayoutRes(R.layout.layout_progress_dialog);
        DialogProxy.setProgressDialogTheme(R.style.Custom_Dialog_Dim);
    }
    public void setFirstLaunch(boolean state){
        firstLaunch =state;
    }
    public boolean isFirstLauch(){
        return firstLaunch;
    }
    public IMusicManager getMusicBinder(){
        return musicBinder;
    }
    public void initImageLoader(Context context){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCache(new LruMemoryCache(5*1024*1024))
                .memoryCacheSize(10*1024*1024)
                .build();
        ImageLoader.getInstance().init(config);
    }

}
