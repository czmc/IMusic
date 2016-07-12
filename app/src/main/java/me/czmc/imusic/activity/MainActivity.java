package me.czmc.imusic.activity;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import me.czmc.imusic.R;
import me.czmc.imusic.activity.base.BaseActivity;
import me.czmc.imusic.app.MyApplication;
import me.czmc.imusic.domain.MusicData;
import me.czmc.imusic.frag.AboutFragment;
import me.czmc.imusic.frag.DownloadMusicFragment;
import me.czmc.imusic.frag.LocalMusicFragment;
import me.czmc.imusic.frag.MusicListFragment;
import me.czmc.imusic.frag.NetMusicFragment;
import me.czmc.imusic.service.IMusicManager;
import me.czmc.imusic.service.IOnMusicInfoListener;
import me.czmc.imusic.utils.Constans;
import me.czmc.imusic.utils.EditUtils;
import me.czmc.imusic.utils.ImageUtils;
import me.czmc.imusic.utils.MediaUtils;
import me.czmc.imusic.view.FloatingImageButton;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, IOnMusicInfoListener {

    private final int INDEX_LOCAL = 0;
    private final int INDEX_NET = 1;
    private final int INDEX_LIST = 2;
    private final int INDEX_DOWNLOAD = 3;
    private final int INDEX_ABOUT = 4;

    private final String TAG = "MainActivity";

    private LocalMusicFragment mLocalMusicFragment;
    private NetMusicFragment mNetMusicFragment;
    private MusicListFragment mMusicListFragment;
    private DownloadMusicFragment mDownloadMusicFragment;
    private AboutFragment mAboutFragment;

    private Toolbar toolbar;
    private IMusicManager service;

    private TextView musicAuthor;
    private TextView musicName;
    private ImageView musicIcon;
    private TextView timeView;
    private TextView currentTimeView;
    private SeekBar seekBar;
    private ImageView playBtn;
    private ImageView nextBtn;
    private MusicData music;

    private int themeResid;
    private int themeMenuResid;
    private boolean updateFlag = true;
    private ListChangeBroadCastReceiver receiver;
    private View bottomBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //主题提取
        theme_init();
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(themeResid);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        service = ((MyApplication) getApplication()).getMusicBinder();
        try {
            service.registerOnMusicInfoListener(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        receiver = new ListChangeBroadCastReceiver();
        IntentFilter filter = new IntentFilter(Constans.NOTYFI_LIST_CHANGE);
        registerReceiver(receiver, filter);
        initBottomView();
        initFloatingButton();
        initEvent();
        OnTabSelected(INDEX_LOCAL);
    }
    public void initFloatingButton(){
        FloatingImageButton floatingButton = new FloatingImageButton(this,800,1000);
        floatingButton.setOnClickListener(new FloatingImageButton.OnClickListener() {
            @Override
            public void onclick(View v) {
                try {
                    if(service.getPlayList()==null){
                        List<MusicData> musics = new Select().from(MusicData.class).orderBy("firstletter ASC").execute();
                        if(musics==null){
                            showToast("播放列表没有歌曲！");
                            return;
                        }
                        service.setPlayList(musics);
                    }
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        try {
            service.unregistertOnMusicInfoListener(this);
            unregisterReceiver(receiver);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initBottomView() {
        musicAuthor = (TextView) findViewById(R.id.current_music_author);
        musicName = (TextView) findViewById(R.id.current_music_name);
        musicIcon = (ImageView) findViewById(R.id.current_music_icon);
        seekBar = (SeekBar) findViewById(R.id.current_music_duraction);
        currentTimeView = (TextView) findViewById(R.id.current_music_time);
        timeView = (TextView) findViewById(R.id.music_time);
        playBtn = (ImageView) findViewById(R.id.btn_play);
        nextBtn = (ImageView) findViewById(R.id.btn_next);
        bottomBar = (View) findViewById(R.id.bottomBar);
    }

    public void initEvent() {
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        musicIcon.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    this.progress = progress;
                    if (music != null) {
                        try {
                            int seekTime = (int) (music.duration * progress / seekBar.getMax());
                            service.setSeekState(true, progress, seekTime);
                            Log.i(TAG, "seekTime: " + seekTime);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

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

    //主题初始提取
    public void theme_init() {
        try {
            themeResid = EditUtils.getValue(this, Constans.THEME_COLOR, Integer.class);
            themeMenuResid = EditUtils.getValue(this, Constans.THEME_MENU_COLOR, Integer.class);
            if (themeResid == 0) {
                themeResid = R.color.theme_default_actionbar;
            }
            if (themeMenuResid == 0) {
                themeMenuResid = R.drawable.side_nav_bar;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //主题改变响应
    public void theme_change() {
        theme_init();
        toolbar.setBackgroundResource(themeResid);
    }

    private long currenttime = 0;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - currenttime < 2000) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
            }
            currenttime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.theme_default) {
            EditUtils.putValue(this, Constans.THEME_COLOR, R.color.theme_default_actionbar);
            EditUtils.putValue(this, Constans.THEME_MENU_COLOR, R.drawable.side_nav_bar);
            theme_change();
            return true;
        }
        if (id == R.id.theme_black) {
            EditUtils.putValue(this, Constans.THEME_COLOR, R.color.theme_black_actionbar);
            EditUtils.putValue(this, Constans.THEME_MENU_COLOR, R.color.theme_black_menu);
            theme_change();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_local_music) {
            OnTabSelected(INDEX_LOCAL);
        } else if (id == R.id.nav_net_music) {
            OnTabSelected(INDEX_NET);
        } else if (id == R.id.nav_favourite_music) {
            OnTabSelected(INDEX_LIST);
        } else if (id == R.id.nav_download_music) {
            OnTabSelected(INDEX_DOWNLOAD);
        } else if (id == R.id.nav_setting) {
            OnTabSelected(INDEX_ABOUT);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void OnTabSelected(int index) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (index) {
            case INDEX_LOCAL:
                getSupportActionBar().setSubtitle(R.string.item_local_music);
                hideFragments(transaction);
                if (null == mLocalMusicFragment) {
                    mLocalMusicFragment = new LocalMusicFragment();
                    transaction.add(R.id.fragment_area, mLocalMusicFragment);
                } else {
                    transaction.show(mLocalMusicFragment);
                }
                break;
            case INDEX_NET:
                getSupportActionBar().setSubtitle(R.string.item_network_music);
                hideFragments(transaction);
                if (null == mNetMusicFragment) {
                    mNetMusicFragment = new NetMusicFragment();
                    transaction.add(R.id.fragment_area, mNetMusicFragment);
                } else {
                    transaction.show(mNetMusicFragment);
                }
                break;
            case INDEX_LIST:
                getSupportActionBar().setSubtitle(R.string.item_music_list);
                hideFragments(transaction);
                if (null == mMusicListFragment) {
                    mMusicListFragment = new MusicListFragment();
                    transaction.add(R.id.fragment_area, mMusicListFragment);
                } else {
                    transaction.show(mMusicListFragment);
                }
                break;
            case INDEX_DOWNLOAD:
                getSupportActionBar().setSubtitle(R.string.item_download_music);
                hideFragments(transaction);
                if (null == mDownloadMusicFragment) {
                    mDownloadMusicFragment = new DownloadMusicFragment();
                    transaction.add(R.id.fragment_area, mDownloadMusicFragment);
                } else {
                    transaction.show(mDownloadMusicFragment);
                }
                break;
            case INDEX_ABOUT:
                getSupportActionBar().setSubtitle(R.string.about);
                hideFragments(transaction);
                bottomBar.setVisibility(View.GONE);
                if (null == mAboutFragment) {
                    mAboutFragment = new AboutFragment();
                    transaction.add(R.id.fragment_area, mAboutFragment);
                } else {
                    transaction.show(mAboutFragment);
                }
                initBottomView();
                break;
        }
        transaction.commit();
    }

    /**
     * 将所有fragment都置为隐藏状态
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        bottomBar.setVisibility(View.VISIBLE);
        if (mLocalMusicFragment != null) {
            transaction.hide(mLocalMusicFragment);
        }
        if (mNetMusicFragment != null) {
            transaction.hide(mNetMusicFragment);
        }
        if (mMusicListFragment != null) {
            transaction.hide(mMusicListFragment);
        }
        if (mDownloadMusicFragment != null) {
            transaction.hide(mDownloadMusicFragment);
        }
        if (mAboutFragment != null) {
            transaction.hide(mAboutFragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                try {
                    service.play_pause();
                    if (service.getPlayList() == null && mLocalMusicFragment != null) {
                        service.setPlayList(mLocalMusicFragment.musics);
                        service.newplay(0);
                    }
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
            case R.id.current_music_icon:
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void OnMusicChange(MusicData music) throws RemoteException {
        this.music = music;
        musicName.setText(music.name);
        musicAuthor.setText(music.artist);
        ImageLoader.getInstance().displayImage(music.coverPath == null ? "" : "file://" + music.coverPath, musicIcon, ImageUtils.getOptions(R.drawable.cd));
        timeView.setText(MediaUtils.second2Time(music.duration));
    }

    @Override
    public void onMusicPlayProgress(int progress, int currentSecond) throws RemoteException {
        seekBar.setProgress(progress);
        currentTimeView.setText(MediaUtils.second2Time(currentSecond));
    }

    @Override
    public void onChangePlayState(boolean isPlaying) throws RemoteException {
        if (isPlaying) {
            playBtn.setImageResource(R.drawable.pause);
        } else {
            playBtn.setImageResource(R.drawable.play);
        }
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    private class ListChangeBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMusicListFragment != null) {
                mMusicListFragment.reloadMusicList();
            }
        }
    }
}
