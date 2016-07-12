package me.czmc.imusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.ArrayList;

import me.czmc.imusic.domain.LrcRow;

/**
 * Created by MZone on 2/18/2016.
 */
public class LrcView extends TextView{
    private  Paint mCurrentPaint;
    private Paint mCommomPaint;
    private int mDefalutShowLine = 7;
    private ArrayList<LrcRow> lrcList = null;
    private int mTextHeight=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30,getResources().getDisplayMetrics());
    private int mCurrentColor = Color.parseColor("#00ff00");
    private int mCommonColor = Color.parseColor("#000000");
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
    private float mCurrentTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,getResources().getDisplayMetrics());
    private int width;
    private int height;
    private int index;
    private Scroller mScroller;
    private float mHighLightTextSize;
    private float mCurFraction;
    private int duration;


    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScroller = new Scroller(context);
        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满
        mCurrentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式

        mCommomPaint = new Paint();
        mCommomPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满
        mCommomPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(canvas==null){
            return;
        }
        mCurrentPaint.setColor(mCurrentColor);
        mCurrentPaint.setTypeface(Typeface.SERIF);
        mCurrentPaint.setTextSize(mCurrentTextSize);

        mCommomPaint.setColor(mCommonColor);
        mCommomPaint.setTypeface(Typeface.DEFAULT);
        mCommomPaint.setTextSize(mTextSize);

        if(lrcList==null){

        }
        /** 当前行 **/
        mHighLightTextSize=mTextSize+(mCurrentTextSize-mTextSize)*mCurFraction;
        mCurrentPaint.setTextSize(mHighLightTextSize);
        if(lrcList==null){
            canvas.drawText("暂无歌词！", width / 2, height / 2, mCurrentPaint);
            return;
        }
        canvas.drawText(lrcList.get(index).getContent(), width / 2, height / 2, mCurrentPaint);
        /** 上几行 **/
        int showLine=index<mDefalutShowLine/2?index:mDefalutShowLine/2;
        float tempY=height/2;
        for(int i=index-1;i>=index-showLine;i--){
            tempY = tempY-mTextHeight;
            int curAlpha = 255-(Math.abs(i-index)*(230/mDefalutShowLine*2)); //求出当前歌词颜色的透明度
            mCommomPaint.setAlpha(curAlpha);
            if(index-1==i) {
                /** 上一行 **/
                mHighLightTextSize = mCurrentTextSize - (mCurrentTextSize - mTextSize) * mCurFraction;
                mCommomPaint.setTextSize(mHighLightTextSize);
                canvas.drawText(lrcList.get(i).getContent(), width / 2, height / 2 - mTextHeight, mCommomPaint);
                mCommomPaint.setTextSize(mTextSize);
                continue;
            }
            canvas.drawText(lrcList.get(i).getContent(),width/2,tempY, mCommomPaint);

        }
        /** 下几行 **/
        tempY=height/2;
        showLine=(index+mDefalutShowLine/2>=lrcList.size())?lrcList.size()-index-1:mDefalutShowLine/2;
        for(int i=index+1;i<=index+showLine;i++){
            tempY = tempY+mTextHeight;
            int curAlpha = 255-(Math.abs(i-index)*(230/mDefalutShowLine*2)); //求出当前歌词颜色的透明度
            mCommomPaint.setAlpha(curAlpha);
            canvas.drawText(lrcList.get(i).getContent(), width / 2, tempY, mCommomPaint);
        }

    }
    private void smoothScrollTo(int dstY,int duration){
        int oldScrollY = getScrollY();
        int offset = dstY - oldScrollY;
        mScroller.startScroll(getScrollX(), oldScrollY, getScrollX(), offset, duration);
        invalidate();
    }
    @Override
    public void computeScroll() {
        if (!mScroller.isFinished()) {
            if (mScroller.computeScrollOffset()) {
                int oldY = getScrollY();
                int y = mScroller.getCurrY();
                scrollTo(getScrollX(), y);
                mCurFraction = mScroller.timePassed()*3f/2000;//可用duration实现跟随滚动速度变化
                mCurFraction = Math.min(mCurFraction, 1F);
                invalidate();

            }
        }
    }
    public void reset() {
        if(!mScroller.isFinished()){
            mScroller.forceFinished(true);
        }
        scrollTo(getScrollX(), 0);
        invalidate();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }
    public void setLrcList(ArrayList<LrcRow> lrcList){
        reset();
        index=0;
        if(lrcList!=null) {
            if (lrcList.isEmpty()) {
                lrcList = null;
            }
        }
        this.lrcList = lrcList;
        invalidate();
    }
    public void seekTo(int currentTime){

        if(lrcList==null) return;
        for(int i =lrcList.size()-1;i>=0;i--){
            if(currentTime>=lrcList.get(i).getTime()){
                if(index!=i){
                    reset();
                    if(i<lrcList.size()-1) {
                        duration = lrcList.get(i + 1).getTime() - lrcList.get(i).getTime();
                        smoothScrollTo(mTextHeight, duration);
                    }
                    index=i;
                    invalidate();
                }
                break;
            }

        }
    }

}
