package me.czmc.imusic.frag;

import android.app.Fragment;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import me.czmc.imusic.R;
import me.czmc.imusic.activity.MainActivity;
import me.czmc.imusic.adapter.MusicListAdapter;
import me.czmc.imusic.app.MyApplication;
import me.czmc.imusic.domain.MusicData;
import me.czmc.imusic.service.IMusicManager;
import me.czmc.imusic.view.MyLetterView;


public class MusicListFragment extends Fragment implements AdapterView.OnItemClickListener, MyLetterView.OnTouchingLetterChangedListener, AdapterView.OnItemLongClickListener {
    private MyApplication app;
    private IMusicManager service;
    public List<MusicData> musics;
    private ListView mListView;
    private View secondTitle;
    private View rootView;
    private MusicListAdapter mMusicListAdapter;
    private MyLetterView mLetterView;
    private TextView dialog;
    private MainActivity mMainActivity;
    private boolean meunflag = false;
    private float menuHeight = 0;
    private View lastMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getActivity().getApplication();
        service = app.getMusicBinder();
        mMainActivity = (MainActivity) getActivity();
        menuHeight = getResources().getDimension(R.dimen.menu_height);
    }

    private void initView() {
        musics = new Select().from(MusicData.class).orderBy("firstletter ASC").execute();
        mListView = (ListView) rootView.findViewById(R.id.music_list);
        mLetterView = (MyLetterView) rootView.findViewById(R.id.right_letter);
        dialog = (TextView) rootView.findViewById(R.id.dialog);
        mLetterView.setTextView(dialog);
        mMusicListAdapter = new MusicListAdapter(getActivity(), (ArrayList<MusicData>) musics, R.layout.item_music_list);
        mLetterView.setOnTouchingLetterChangedListener(this);
        mListView.setAdapter(mMusicListAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        try {
            service.registerOnMusicInfoListener(mMusicListAdapter);
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
        rootView = inflater.inflate(R.layout.fragment_music_list, container, false);
        initView();
        initData();
        initEvent();
        return rootView;
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
        if (mMusicListAdapter != null) {
            int position = mMusicListAdapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                mListView.setSelection(position);
                dialog.setText(s);
            }
        }
    }

    public void reloadMusicList() {
        musics.clear();
        List<MusicData> temp = new Select().from(MusicData.class).orderBy("firstletter ASC").execute();
        musics.addAll(temp);
        mMusicListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if(meunflag) {
            if (lastMenu != null) {
                resetListHeight( lastMenu);
            }
        }
        View menuitem = showListViewMenu(view);
        menuitem.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.showMsgDialog("是否确定删除？", "确定", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MusicData.delete(MusicData.class, musics.get(position).getId());
                        mMainActivity.showToast("删除..");
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetListHeight(mListView);
                        mMainActivity.cancelProgressDialog();
                    }
                });
            }
        });
        menuitem.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicData.delete(MusicData.class, musics.get(position).getId());
                reloadMusicList();

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
