package me.czmc.imusic.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.czmc.imusic.R;
import me.czmc.imusic.activity.base.BaseActivity;
import me.czmc.imusic.app.MyApplication;
import me.czmc.imusic.domain.LrcRow;
import me.czmc.imusic.domain.MusicData;
import me.czmc.imusic.service.IMusicManager;
import me.czmc.imusic.service.IOnMusicInfoListener;
import me.czmc.imusic.utils.Constans;
import me.czmc.imusic.utils.EditUtils;
import me.czmc.imusic.utils.MediaUtils;
import me.czmc.imusic.view.LrcView;

public class PlayerActivity extends BaseActivity implements View.OnClickListener, IOnMusicInfoListener {

    private IMusicManager service;
    private TextView mMusicName;
    private TextView mMusicArtist;
    private TextView mMusicTime;
    private SeekBar mDuration;
    private TextView mCurrentMusicTime;
    private ImageButton playBtn;
    private ImageButton nextBtn;
    private ImageButton forwardBtn;
    private ImageView loveBtn;
    private ImageView downloadBtn;
    private ImageView playModeBtn;
    private ArrayList<MusicData> musics;
    private boolean updateFlag = true;
    private LrcView lrcView;
    private MusicData music;
    private int playMode = 0;
    private android.support.v7.widget.Toolbar toolBar;
    private View view;
    private ViewPager pager;
    private ImageView img_cover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        MyApplication app = (MyApplication) getApplication();
        service = app.getMusicBinder();
        initView();
        initData();
        initEvent();

    }

    public void initView() {
        toolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mMusicName = (TextView) findViewById(R.id.music_name);
        view = findViewById(R.id.content);
        mMusicArtist = (TextView) findViewById(R.id.music_author);
        mMusicTime = (TextView) findViewById(R.id.music_time);
        mCurrentMusicTime = (TextView) findViewById(R.id.current_music_time);
        mDuration = (SeekBar) findViewById(R.id.current_music_duraction);
        View view1 = getLayoutInflater().inflate(R.layout.view_lrc, null);
        View view2 = getLayoutInflater().inflate(R.layout.view_img, null);
        getLayoutInflater().inflate(R.layout.view_img, null);
        lrcView = (LrcView) view1.findViewById(R.id.lrcview);
        img_cover = (ImageView) view2.findViewById(R.id.img_cover);
        pager = (ViewPager) findViewById(R.id.pager);
        final ArrayList<View> viewList = new ArrayList();
        viewList.add(view1);
        viewList.add(view2);
        loveBtn = (ImageView) findViewById(R.id.love);
        downloadBtn = (ImageView) findViewById(R.id.download);
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        });
        playBtn = (ImageButton) findViewById(R.id.btn_play);
        nextBtn = (ImageButton) findViewById(R.id.btn_next);
        forwardBtn = (ImageButton) findViewById(R.id.btn_forward);
        playModeBtn = (ImageView) findViewById(R.id.btn_play_mode);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.down);
    }

    public void initData() {
        try {
            playMode = EditUtils.getValue(getApplicationContext(), Constans.PLAY_MODE, Integer.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void initEvent() {
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        forwardBtn.setOnClickListener(this);
        playModeBtn.setOnClickListener(this);
        loveBtn.setOnClickListener(this);
        downloadBtn.setOnClickListener(this);
        try {
            service.registerOnMusicInfoListener(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int progress;

            /**
             * 获得进度
             * @param seekBar
             * @param progress
             * @param fromUser
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    this.progress = progress;
                    if (music != null) {
                        try {
                            int seekTime = (int) (music.duration * progress / seekBar.getMax());
                            service.setSeekState(true, progress, seekTime);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            /**
             * 接触时停止进度条更新
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /**
             * 停止时跳转进度条及音乐进度
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (music == null) return;
                    service.setSeekState(false, progress, 0);
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                try {
                    service.play_pause();
                    if (service.getPlayList() == null && musics != null) {
                        service.setPlayList(musics);
                        service.newplay(0);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_forward:
                try {
                    service.forward();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_next:
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_play_mode:
                playMode++;
                try {
                    service.setPlayMode(playMode);
                    EditUtils.putValue(getApplicationContext(), Constans.PLAY_MODE, playMode);
                    switch (playMode) {
                        case 1:
                            showToast(getApplicationContext(), "单曲播放");
                            break;
                        case 2:
                            showToast(getApplicationContext(), "单曲循环");
                            break;
                        case 3:
                            showToast(getApplicationContext(), "顺序播放");
                            break;
                        case 4:
                            showToast(getApplicationContext(), "顺序循环");
                            break;
                        case 5:
                            showToast(getApplicationContext(), "随机播放");
                        default:
                            playMode = 0;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.love:
                music.save();
                this.sendBroadcast(new Intent(Constans.NOTYFI_LIST_CHANGE));
                showToast("已加入播放列表");
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(0, 0, 0, "详情");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(this,DetailActivity.class);
                intent.putExtra(Constans.PASS＿MUSIC,music);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            service.showNotification(false);
            service.callbackCurrentMusicInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            service.showNotification(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnMusicChange(MusicData music) throws RemoteException {
        this.music = music;
        img_cover.setImageURI(Uri.parse(music.coverPath));
        Drawable drawable = BitmapDrawable.createFromPath(music.coverPath);
        drawable.setAlpha(0x33);
//        setBackgroundDrawable(drawable);
        view.setBackgroundDrawable(drawable);
        mMusicName.setText(music.name);
        mMusicArtist.setText(music.artist);
        mMusicTime.setText(MediaUtils.second2Time(music.duration));
        lrcView.setLrcList(getLrcList(music));
    }

    private ArrayList<LrcRow> getLrcList(MusicData music) {
        if (music.lrcPath == null) return null;
        ArrayList<LrcRow> lrcRows = new ArrayList<LrcRow>();
        String buffer = null;
        InputStreamReader read = null;//考虑到编码格式
        try {
            read = new InputStreamReader(
                    new FileInputStream(new File(music.lrcPath)), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            String lrcLine = null;
            while ((lrcLine = bufferedReader.readLine()) != null) {
                List<LrcRow> rows = LrcRow.createRows(lrcLine);
                if (rows != null && rows.size() > 0) {
                    lrcRows.addAll(rows);
                }
            }
            Collections.sort(lrcRows);

            for (int i = 0; i < lrcRows.size() - 1; i++) {
                lrcRows.get(i).setTotalTime(lrcRows.get(i + 1).getTime() - lrcRows.get(i).getTime());
            }
            lrcRows.get(lrcRows.size() - 1).setTotalTime(5000);
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lrcRows;
    }

    @Override
    public void onMusicPlayProgress(int progress, int currentSecond) throws RemoteException {
        mDuration.setProgress(progress);
        mCurrentMusicTime.setText(MediaUtils.second2Time(currentSecond));
        lrcView.seekTo(currentSecond);
    }

    @Override
    public void onChangePlayState(boolean isPlaying) throws RemoteException {
        if (isPlaying) {
            playBtn.setImageResource(R.drawable.pause2);
        } else {
            playBtn.setImageResource(R.drawable.play2);
        }
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    Toast toast = null;

    private void showToast(Context context, Object content) {
        String result = "";
        if (content instanceof Integer) {
            result = getString((int) content);
        }
        if (content instanceof String) {
            result = (String) content;
        } else {
            return;
        }
        if (toast != null) toast.cancel();
        toast = Toast.makeText(context, result, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            service.unregistertOnMusicInfoListener(this);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
