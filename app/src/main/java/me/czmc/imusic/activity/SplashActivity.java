package me.czmc.imusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import me.czmc.imusic.R;
import me.czmc.imusic.app.MyApplication;
import me.czmc.imusic.utils.Constans;
import me.czmc.imusic.utils.EditUtils;

/**
 * Created by czmz on 15/12/4.
 */
public class SplashActivity extends Activity {
    /**
     * 欢迎界面停留
     */
    private android.os.Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case Constans.MSG_LANUCH:
                    lanuch();
                    break;
            }
        }
    };
    private Integer themeResid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication application = (MyApplication)getApplication();
        if(application.isFirstLauch()){
            application.setFirstLaunch(false);
            setContentView(R.layout.activity_splash);
            try {
                themeResid = EditUtils.getValue(this, Constans.THEME_COLOR, Integer.class);
                if(themeResid==0){
                    themeResid=R.color.colorPrimary;
                }else {
                    findViewById(R.id.content).setBackgroundResource(themeResid);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            mHandler.sendEmptyMessageDelayed(Constans.MSG_LANUCH,3000);
        }else{
            lanuch();
        }

    }

    /**
     * 启动主界面，取消欢迎页面
     */
    private void lanuch() {
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
