package me.czmc.imusic.frag;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import me.czmc.imusic.R;
import me.czmc.imusic.activity.MainActivity;
import me.czmc.imusic.adapter.MusicAdapter;
import me.czmc.imusic.app.MyApplication;
import me.czmc.imusic.domain.MusicData;
import me.czmc.imusic.service.IMusicManager;
import me.czmc.imusic.utils.CharacterParser;
import me.czmc.imusic.utils.Constans;
import me.czmc.imusic.utils.PinyinComparator;
import me.czmc.imusic.view.MyLetterView;


public class LocalMusicFragment extends Fragment implements AdapterView.OnItemClickListener, MyLetterView.OnTouchingLetterChangedListener, AdapterView.OnItemLongClickListener {
    private final String TAG = "LocalMusicFragment";
    private MyApplication app;
    private IMusicManager service;
    public ArrayList<MusicData> musics;
    private ListView mListView;
    private View secondTitle;
    private View rootView;
    private MusicAdapter mMusicAdapter;
    private MyLetterView mLetterView;
    private TextView dialog;
    private MainActivity mMainActivity;
    private boolean meunflag = false;
    private float menuHeight = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getActivity().getApplication();
        service = app.getMusicBinder();
        mMainActivity = (MainActivity) getActivity();
        menuHeight = getResources().getDimension(R.dimen.menu_height);
    }

    private void initView() {
        musics = getMusicFileList();
        mListView = (ListView) rootView.findViewById(R.id.local_music_list);
        mLetterView = (MyLetterView) rootView.findViewById(R.id.right_letter);
        dialog = (TextView) rootView.findViewById(R.id.dialog);
        mLetterView.setTextView(dialog);
        mMusicAdapter = new MusicAdapter(getActivity(), musics, R.layout.item_local_music);
        mLetterView.setOnTouchingLetterChangedListener(this);
        mListView.setAdapter(mMusicAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        try {
            service.registerOnMusicInfoListener(mMusicAdapter);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initData() {

    }

    private void initEvent() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (meunflag) {
                    if (lastMenu != null) {
                        resetListHeight(lastMenu);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_local_music, container, false);
        initView();
        initData();
        initEvent();
        return rootView;
    }

    private ArrayList<MusicData> getMusicFileList() {
        ArrayList<MusicData> list = new ArrayList<MusicData>();
        String[] projection = new String[]{MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.IS_MUSIC,
        };

        long time1 = System.currentTimeMillis();
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int colIsMusicIndex = cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            int colNameIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int colTimeIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int colPathIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int colArtistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int colAlbumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int colAlbumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int colIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int colSizeIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int fileNum = cursor.getCount();
            for (int counter = 0; counter < fileNum; counter++) {
                if (cursor.getInt(colIsMusicIndex) == 0) {
                    cursor.moveToNext();
                    continue;
                }
                Log.i("Test", "" + cursor.getInt(colIsMusicIndex));
                MusicData data = new MusicData();
                data.name = cursor.getString(colNameIndex);
                data.duration = cursor.getInt(colTimeIndex);
                data.path = cursor.getString(colPathIndex);
                data.artist = cursor.getString(colArtistIndex);
                data.albumId = cursor.getLong(colAlbumIdIndex);
                data.album = cursor.getString(colAlbumIndex);
                data.musicid = cursor.getLong(colIdIndex);
                data.size = cursor.getLong(colSizeIndex);
                data = setLrcPath(data);
                data = setCoverPath(data);
                data.firstLetter = CharacterParser.getInstance().getFirstLetter(data.name);
                list.add(data);
                cursor.moveToNext();
            }

            cursor.close();
        }
        long time2 = System.currentTimeMillis();
        Collections.sort(list, new PinyinComparator());
        Log.i(TAG, "seach filelist cost = " + (time2 - time1));
        return list;
    }

    private MusicData setCoverPath(MusicData music) {
        File file = new File("/storage/emulated/0/Music/Cover");
        if (file.exists()) {
            for (File file1 : file.listFiles()) {
                String name = file1.getName();
                if ((name.contains(music.artist) && name.contains(music.name))
                        || name.contains(music.album)) {
                    music.coverPath = file1.getAbsolutePath();
                }
            }
        }
        return music;
    }

    private MusicData setLrcPath(MusicData music) {
        File file = new File("/storage/emulated/0/Music/Lyric");
        if (file.exists()) {
            for (File file1 : file.listFiles()) {
                String name = file1.getName();
                if ((name.contains(music.artist) && name.contains(music.name)) || name.contains(music.name)) {
                    music.lrcPath = file1.getAbsolutePath();
                }
            }
        }
        return music;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            service.setPlayList(musics);
            service.newplay(position);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        if (mMusicAdapter != null) {
            int position = mMusicAdapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                mListView.setSelection(position);
                dialog.setText(s);
            }
        }
    }

    View lastMenu = null;

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if(meunflag) {
            if (lastMenu != null) {
                resetListHeight( lastMenu);
            }
        }
        View menuitem = showListViewMenu( view);
        menuitem.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.showMsgDialog("是否确定删除？", "确定", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMainActivity.showToast("删除..");
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMainActivity.cancelProgressDialog();
                    }
                });
            }
        });
        menuitem.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.showToast("已加入播放列表");
                mMainActivity.sendBroadcast(new Intent(Constans.NOTYFI_LIST_CHANGE));
                musics.get(position).save();
            }
        });
        lastMenu = view;
        return true;
    }

    public View showListViewMenu(View item) {

        View menuitem = item.findViewById(R.id.menu);
        menuitem.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = mListView.getMeasuredHeight()+(int)menuHeight;
        mListView.setLayoutParams(params);
        meunflag = true;
        return menuitem;
    }
    public void resetListHeight(View item) {
        View menuitem = item.findViewById(R.id.menu);
        menuitem.setVisibility(View.GONE);
        meunflag = false;
    }
}
