package me.czmc.imusic.proxy;

import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.view.View;

import me.czmc.imusic.utils.StringUtils;


/**
 * @author MZone
 */
public class ActivityProxy implements ToastProxy.ToastProxiable {
    private ToastProxy mToastProxy;
    private DialogProxy mDialogProxy;
    private Context mContext;
    private Handler mHandler = new Handler();

    public ActivityProxy(Context context) {
        this.mContext = context;
    }

    public void onDestroy() {
        if (mDialogProxy != null) {
            mDialogProxy.cancelMsgDialog();
            mDialogProxy.cancelProgressDialog();
        }
    }

    public void initToastProxy() {
        if (mToastProxy == null) {
            mToastProxy = new ToastProxy(mContext);
        }
    }

    public void showToast(final String text) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initToastProxy();
                mToastProxy.showToast(text);
            }
        });

    }


    public void showToast(final int resourceId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initToastProxy();
                mToastProxy.showToast(resourceId);
            }
        });
    }

    protected void initDialogProxy() {
        if (mDialogProxy == null) {
            mDialogProxy = new DialogProxy(mContext);
        }
    }

    public void showMsgDialog() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initDialogProxy();
                mDialogProxy.showMsgDialog();
            }
        });
    }

    public void showMsgDialogWithSize(final int width, final int height) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initDialogProxy();
                mDialogProxy.showMsgDialogWithSize(width, height);
            }
        });
    }

    public void showProgressDialog() {
        // TODO Auto-generated method stub
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initDialogProxy();
                mDialogProxy.showProgressDialog();
            }
        });
    }

    public void cancelProgressDialog() {
        // TODO Auto-generated method stub
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initDialogProxy();
                mDialogProxy.cancelProgressDialog();
            }
        });
    }

    public DialogProxy getDialogProxy() {
        // TODO Auto-generated method stub
        initDialogProxy();
        return mDialogProxy;
    }

    public void cancelMsgDialog() {
        // TODO Auto-generated method stub
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initDialogProxy();
                mDialogProxy.cancelMsgDialog();
            }
        });
    }

    /*******************************************************************************
     * DialogProxy expand
     *******************************************************************************/
    public void showMsgDialog(final String detials,
                              final String btnLeft, final String btnRight,
                              final View.OnClickListener btnLeftListener,
                              final View.OnClickListener btnRightListener) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                DialogProxy dialogProxy = getDialogProxy();

                if (!StringUtils.isStringInvalid(detials)) {
                    dialogProxy.showMsgDialogDetailMsg();
                    dialogProxy.setMsgDialogDetailMsg(detials);
                } else {
                    dialogProxy.hideMsgDialogDetailMsg();
                }

                if (!StringUtils.isStringInvalid(btnLeft)) {
                    dialogProxy.showMsgDialogBtnLeft();
                    dialogProxy.setMsgDialogBtnLeftText(btnLeft);
                } else {
                    dialogProxy.hideMsgDialogBtnLeft();
                }

                if (!StringUtils.isStringInvalid(btnRight)) {
                    dialogProxy.showMsgDialogBtnRight();
                    dialogProxy.setMsgDialogBtnRightText(btnRight);
                } else {
                    dialogProxy.hideMsgDialogBtnRight();
                }
                dialogProxy.setMsgDialogCanceledOnTouchOutside(true);
                dialogProxy.setMsgDialogBtnLeftClickListener(btnLeftListener);
                dialogProxy.setMsgDilaogBtnRightClickListener(btnRightListener);
                dialogProxy.showMsgDialog();
            }
        });
    }

    public void showProgressDialog(final String msg,
                                   final OnCancelListener listener, final boolean cancelable) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                DialogProxy dialogProxy = getDialogProxy();

                if (!StringUtils.isStringInvalid(msg)) {
                    dialogProxy.showProgressDialogMsg();
                    dialogProxy.setProgressDialogMsgText(msg);
                } else {
                    dialogProxy.hideProgressDialogMsg();
                }

                dialogProxy.setProgressDialogCancelable(cancelable);
                dialogProxy.setProgressDialogCancelListener(listener);

                dialogProxy.showProgressDialog();
            }
        });
    }

}
