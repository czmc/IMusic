package me.czmc.imusic.adapter.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by czmz on 15/12/6.
 */
public class ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    public int position;

    public ViewHolder(Context mContext,ViewGroup parent,int mItemResID,int position){
        this.position = position;
        this.mConvertView = LayoutInflater.from(mContext).inflate(mItemResID,parent,false);
        this.mConvertView.setTag(this);
        this.mViews = new SparseArray<View>();

    }
    public static ViewHolder get(Context mContext,View convertView,ViewGroup parent,int layoutId,int position){
        if(convertView == null){
            return new ViewHolder(mContext,parent,layoutId,position);
        }else{
            ViewHolder holder = (ViewHolder)convertView.getTag();
            holder.position = position;
            return holder;
        }
    }

    /**
     * 获取控件
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if(view == null){
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T)view;
    }
    public ViewHolder setText(int viewId,String text){
        TextView textview = getView(viewId);
        if(textview!=null) {
            textview.setText(text);
        }
        return this;
    }

    public View getConvertView(){
        return mConvertView;
    }


}
