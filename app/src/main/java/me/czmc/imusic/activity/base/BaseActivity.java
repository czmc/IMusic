package me.czmc.imusic.activity.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.czmc.imusic.proxy.ActivityProxy;
import me.czmc.imusic.proxy.DialogProxy;
import me.czmc.imusic.proxy.HandlerProxy;
import me.czmc.imusic.proxy.ToastProxy;


public  class BaseActivity extends AppCompatActivity implements HandlerProxy.HandlerProxiable,
        DialogProxy.DialogExtProxiable,ToastProxy.ToastProxiable
{
    private ActivityProxy mActivityProxy;
    private HandlerProxy mHandleProxy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityProxy=new ActivityProxy(this);
        mHandleProxy = new HandlerProxy(this,this);
    }

    @Override
    public void processHandlerMessage(Message msg) {

    }

    @Override
    public Handler getHandler() {
        return mHandleProxy.getHandler();
    }

    @Override
    public void showProgressDialog(String msg, DialogInterface.OnCancelListener listener, boolean cancelable) {
        mActivityProxy.showProgressDialog(msg, listener, cancelable);
    }

    @Override
    public void showProgressDialog(String msg) {
        mActivityProxy.showProgressDialog(msg, null, true);
    }

    @Override
    public void showProgressDialog(int resid) {
        showProgressDialog(getString(resid));
    }

    @Override
    public void showMsgDialog(String detials, String btnLeft, String btnRight, View.OnClickListener btnLeftListener, View.OnClickListener btnRightListener) {
        mActivityProxy.showMsgDialog(detials,btnLeft,btnRight,btnLeftListener,btnRightListener);
    }


    @Override
    public void showMsgDialog(String detials, String btnLeft, View.OnClickListener btnLeftListener) {
        mActivityProxy.showMsgDialog(detials,null,btnLeft,null,btnLeftListener);
    }

    @Override
    public void showMsgDialog(String detials) {
        mActivityProxy.showMsgDialog(detials,null,null,null,null);
    }

    @Override
    public void showMsgDialog(int res) {
        showMsgDialog(getString(res));
    }

    @Override
    public void cancelMsgDialog() {
        mActivityProxy.cancelMsgDialog();
    }

    @Override
    public void cancelProgressDialog() {
        mActivityProxy.cancelProgressDialog();
    }

    @Override
    public void showToast(String text) {
        mActivityProxy.showToast(text);
    }

    @Override
    public void showToast(int resourceId) {
        mActivityProxy.showToast(resourceId);
    }
}
