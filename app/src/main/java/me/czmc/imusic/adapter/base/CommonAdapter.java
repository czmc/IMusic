package me.czmc.imusic.adapter.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by czmz on 15/12/6.
 */
public abstract class CommonAdapter<T> extends BaseAdapter{

    private Context mContext;
    private ArrayList<T> mDataList;
    private int mItemResID;

    public CommonAdapter(Context mContext,ArrayList<T> mDataList,int mItemResID) {
        this.mDataList= mDataList;
        this.mItemResID = mItemResID;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public T getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, mItemResID, position);
        convert(holder,getItem(position),position);
        return holder.getConvertView();
    }

    public abstract void convert(ViewHolder holder,T t,int position);
}
