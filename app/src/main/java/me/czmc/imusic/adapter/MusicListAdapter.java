package me.czmc.imusic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import me.czmc.imusic.R;
import me.czmc.imusic.adapter.base.CommonAdapter;
import me.czmc.imusic.adapter.base.ViewHolder;
import me.czmc.imusic.domain.MusicData;
import me.czmc.imusic.service.IOnMusicInfoListener;
import me.czmc.imusic.utils.ImageUtils;
import me.czmc.imusic.utils.MediaUtils;


/**
 * Created by czmz on 15/12/6.
 */
public class MusicListAdapter extends CommonAdapter<MusicData> implements IOnMusicInfoListener {
    private ArrayList<MusicData> mDataList;
    private MusicData music;

    public MusicListAdapter(Context mContext, ArrayList<MusicData> mDataList, int mItemResID) {
        super(mContext, mDataList, mItemResID);
        this.mDataList = mDataList;
    }

    @Override
    public void convert(ViewHolder holder, MusicData musicData, int position) {

        holder.setText(R.id.item_music_info, musicData.name)
                .setText(R.id.item_music_author, musicData.artist)
                .setText(R.id.item_music_duration, MediaUtils.second2Time(musicData.duration));
        TextView tag = holder.getView(R.id.letter_tips);
        if (getPositionForSection(musicData.firstLetter.charAt(0)) == position) {
            tag.setVisibility(View.VISIBLE);
            tag.setText(musicData.firstLetter);
        } else {
            tag.setVisibility(View.GONE);
        }
        TextView textview = (TextView) holder.getView(R.id.item_music_duration);
        final ImageView imageView = (ImageView) holder.getView(R.id.item_mucic_cover);
        //imageView.setImageURI(Uri.parse(musicData.coverPath));
        String path = "";
        if (musicData.coverPath != null)
            path = "file://" + musicData.coverPath;
        ImageLoader.getInstance().displayImage(path, imageView, ImageUtils.getOptions(R.drawable.img_default));

        if (music == null) return;
        if (musicData.path.equals(music.path)) {
            holder.getView(R.id.item_tag).setVisibility(View.VISIBLE);
            textview.setTextColor(Color.RED);
            //holder.getConvertView().setBackgroundResource(R.color.colorPrimary);
        } else {
            holder.getView(R.id.item_tag).setVisibility(View.GONE);
            // holder.getConvertView().setBackgroundColor(Color.TRANSPARENT);
            textview.setTextColor(Color.GRAY);
        }
    }

    @Override
    public void OnMusicChange(MusicData music) throws RemoteException {
        this.music = music;
        notifyDataSetChanged();
    }

    @Override
    public void onMusicPlayProgress(int progress, int currentSecond) throws RemoteException {

    }

    @Override
    public void onChangePlayState(boolean isPlaying) throws RemoteException {

    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mDataList.get(i).firstLetter;
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}
