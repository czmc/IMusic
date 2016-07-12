package me.czmc.imusic.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import me.czmc.imusic.R;


/**
 * Created by MZone on 1/23/2016.
 */
public class FloatingImageButton extends ImageButton implements View.OnTouchListener,View.OnLongClickListener{
    private OnClickListener mOnClickListener;
    public WindowManager.LayoutParams  params;
    public WindowManager mWindowManager;
    public int mStatusBarHeight;
    int rotateAngel=180;

    public FloatingImageButton(final Activity activity,int x,int y) {
        super(activity.getApplicationContext());
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setImageResource(R.drawable.floatingbuttonbg);
        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0,0, PixelFormat.TRANSPARENT);
        params.flags= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        params.type = WindowManager.LayoutParams.TYPE_TOAST  ;
        mStatusBarHeight = getStatusBarHeight();
        params.gravity=Gravity.LEFT | Gravity.TOP;
        params.x=x;
        params.y=y;
        mWindowManager= activity.getWindowManager();
        mWindowManager.addView(this, params);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onclick(v);
                }
                ViewPropertyAnimator valueAnimator = ViewPropertyAnimator.animate(v);
                valueAnimator.setDuration(300);
                valueAnimator.rotation(rotateAngel).start();
                rotateAngel = rotateAngel + 180;
            }
        });
        setOnTouchListener(this);
        setOnLongClickListener(this);

    }
    public void setOnClickListener(OnClickListener l){
        this.mOnClickListener = l;
    }
    int deltaX;
    int deltaY;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int rawX = (int)event.getRawX();
        int rawY = (int)event.getRawY()-mStatusBarHeight;//扣除状态栏的高度
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //得到偏差值
                deltaX = rawX-params.x;
                deltaY = rawY-params.y;
                break;
            case MotionEvent.ACTION_MOVE:
                //移动标记
                if(!flag) break;
                params.x =rawX-deltaX;
                params.y =rawY-deltaY;
                mWindowManager.updateViewLayout(v,params);
                break;
            case MotionEvent.ACTION_UP:
                flag=false;
                ViewHelper.setScaleX(v, 1);
                ViewHelper.setScaleY(v,1);
            break;
            default:
                break;
        }

        return false;
    }
    boolean flag=false;
    @Override
    public boolean onLongClick(View v) {
        int width = v.getWidth();
        int height = v.getHeight();
        ViewHelper.setScaleX(v, (width - 30) * 1.0f / width);
        ViewHelper.setScaleY(v, (height - 30)*1.0f/height);
        flag =true;
        return true;
    }

    public interface OnClickListener{
        public void onclick(View v);
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
