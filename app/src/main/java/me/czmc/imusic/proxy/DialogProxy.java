package me.czmc.imusic.proxy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import me.czmc.imusic.R;

/**
 * @author MZone
 *
 */
public class DialogProxy {
	private static final int MSG_SHOW_MSG_DIALOG = 0x1111;
	private static final int MSG_SHOW_MSG_DIALOG_IN_SIZE = 0x1112;
	private static final int MSG_SHOW_PROGRESS_DIALOG = 0x1113;
	private static final int MSG_CANCEL_MSG_DIALOG = 0x1114;
	private static final int MSG_CANCEL_PROGRESS_DIALOG = 0x1115;

	private static int mMsgDialogLayoutRes = -1;
	private static int mMsgDialogTheme = -1;
	private static int mProgressDialogLayoutRes = -1;
	private static int mProgressDialogTheme = -1;

	private Context mContext;

	// message dialog start
	private Dialog mMsgDialog;

	private TextView mMsgDialogDetailMsg;

	private TextView mMsgDialogBtnLeft;
	private TextView mMsgDialogBtnRight;

	private OnClickListener mMsgBtnLeftClickListener;
	private OnClickListener mMsgBtnRightClickListener;

	// ProgressDialog start
	private Dialog mProgressDialog;
	private TextView mProgressDialogMsg;
	private OnCancelListener mProgressDialogCancelListener;

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MSG_SHOW_MSG_DIALOG:
				_showMsgDialog();
				break;
			case MSG_SHOW_MSG_DIALOG_IN_SIZE:
				_showMsgDialogWithSize(msg.arg1, msg.arg2);
				break;
			case MSG_SHOW_PROGRESS_DIALOG:
				_showProgressDialog();
				break;
			case MSG_CANCEL_MSG_DIALOG:
				_cancelMsgDialog();
				break;
			case MSG_CANCEL_PROGRESS_DIALOG:
				_cancelProgressDialog();
				break;
			default:
				break;
			}
		}

	};

	public DialogProxy(Context context)
	{
		if (context == null)
		{
			throw new NullPointerException("context non-null");
		}
		mContext = context;
		mMsgDialog = null;
		mMsgBtnLeftClickListener = null;
		mMsgBtnRightClickListener = null;

		mProgressDialog = null;
	}

	public static void setProgressDialogLayoutRes(int res)
	{
		mProgressDialogLayoutRes = res;
	}

	public static void setMsgDialogLayoutRes(int res)
	{
		mMsgDialogLayoutRes = res;
	}

	public static void setMsgDialogTheme(int theme)
	{
		mMsgDialogTheme = theme;
	}

	public static void setProgressDialogTheme(int theme)
	{
		mProgressDialogTheme = theme;
	}

	protected View createMsgDialogView()
	{
		return LayoutInflater.from(mContext).inflate(mMsgDialogLayoutRes, null);
	}

	protected Dialog createMsgDialog(View view)
	{
		Dialog ret = new Dialog(mContext, mMsgDialogTheme);
		ret.setContentView(view);
		return ret;
	}

	protected void initMsgDialog()
	{
		if (mMsgDialog == null)
		{
			View view = createMsgDialogView();
			mMsgDialogDetailMsg = (TextView) view
					.findViewById(R.id.content);
			mMsgDialogBtnLeft = (TextView) view
					.findViewById(R.id.msg_dialog_btn_left);
			mMsgDialogBtnLeft.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					if (mMsgDialog != null && mMsgDialog.isShowing())
					{
						mMsgDialog.dismiss();
					}
					if (mMsgBtnLeftClickListener != null)
					{
						mMsgBtnLeftClickListener.onClick(v);
					}
				}
			});

			mMsgDialogBtnRight = (TextView) view
					.findViewById(R.id.msg_dialog_btn_right);
			mMsgDialogBtnRight.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					if (mMsgDialog != null && mMsgDialog.isShowing())
					{
						mMsgDialog.dismiss();
					}
					if (mMsgBtnRightClickListener != null)
					{
						mMsgBtnRightClickListener.onClick(v);
					}
				}
			});
			mMsgDialog = createMsgDialog(view);
		}
	}

	public void showMsgDialog()
	{
		mHandler.sendEmptyMessage(MSG_SHOW_MSG_DIALOG);
	}

	protected void _showMsgDialog()
	{
		initMsgDialog();
		mMsgDialog.show();
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		Window window = mMsgDialog.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = dm.widthPixels * 3 / 4;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(params);
	}

	public void showMsgDialogWithSize(int width, int height)
	{
		Message msg = mHandler.obtainMessage(MSG_SHOW_MSG_DIALOG_IN_SIZE,
				width, height);
		msg.sendToTarget();
	}

	protected void _showMsgDialogWithSize(int width, int height)
	{
		_showMsgDialog();
		mMsgDialog.getWindow().setLayout(width, height);
	}

	public void setMsgDialogCanceledOnTouchOutside(boolean v)
	{
		initMsgDialog();
		mMsgDialog.setCanceledOnTouchOutside(v);
	}

	public void setMsgDialogCancelable(boolean v)
	{
		initMsgDialog();
		mMsgDialog.setCancelable(v);
	}

	public void setMsgDialogBtnLeftClickListener(
			OnClickListener mMsgBtnLeftClickListener)
	{
		this.mMsgBtnLeftClickListener = mMsgBtnLeftClickListener;
	}

	public void setMsgDilaogBtnRightClickListener(
			OnClickListener mMsgBtnRightClickListener)
	{
		this.mMsgBtnRightClickListener = mMsgBtnRightClickListener;
	}


	public void setMsgDialogDetailMsg(int resId)
	{
		initMsgDialog();
		mMsgDialogDetailMsg.setText(resId);
	}

	public void setMsgDialogIsSystemDialog()
	{
		initMsgDialog();
		mMsgDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	}

	public void setMsgDialogDetailMsg(String str)
	{
		initMsgDialog();
		mMsgDialogDetailMsg.setText(str);
	}

	public void setMsgDialogBtnLeftText(int resId)
	{
		initMsgDialog();
		mMsgDialogBtnLeft.setText(resId);
	}

	public void setMsgDialogBtnLeftText(String str)
	{
		initMsgDialog();
		mMsgDialogBtnLeft.setText(str);
	}

	public void setMsgDialogBtnRightText(int resId)
	{
		initMsgDialog();
		mMsgDialogBtnRight.setText(resId);
	}

	public void setMsgDialogBtnRightText(String str)
	{
		initMsgDialog();
		mMsgDialogBtnRight.setText(str);
	}


	public void hideMsgDialogDetailMsg()
	{
		initMsgDialog();
		mMsgDialogDetailMsg.setVisibility(View.GONE);
	}

	public void showMsgDialogDetailMsg()
	{
		initMsgDialog();
		mMsgDialogDetailMsg.setVisibility(View.VISIBLE);
	}




	public void hideMsgDialogBtnLeft()
	{
		initMsgDialog();
		mMsgDialogBtnLeft.setVisibility(View.GONE);
	}

	public void showMsgDialogBtnLeft()
	{
		initMsgDialog();
		mMsgDialogBtnLeft.setVisibility(View.VISIBLE);
	}

	public void hideMsgDialogBtnRight()
	{
		initMsgDialog();
		mMsgDialogBtnRight.setVisibility(View.GONE);
	}

	public void showMsgDialogBtnRight()
	{
		initMsgDialog();
		mMsgDialogBtnRight.setVisibility(View.VISIBLE);
	}

	public void cancelMsgDialog()
	{
		mHandler.sendEmptyMessage(MSG_CANCEL_MSG_DIALOG);
	}

	protected void _cancelMsgDialog()
	{
		if (mMsgDialog != null && mMsgDialog.isShowing())
		{
			mMsgDialog.dismiss();
		}
	}

	/*******************************************************************************
	 * ProgressDialog
	 *******************************************************************************/
	protected View createProgressDialogView()
	{
		return LayoutInflater.from(mContext).inflate(mProgressDialogLayoutRes,
				null);
	}

	protected Dialog createProgressDialog(View view)
	{
		Dialog ret = new Dialog(mContext, mProgressDialogTheme);
		ret.setContentView(view);
		return ret;
	}

	protected void initProgressDialog()
	{
		if (mProgressDialog == null)
		{
			View rootView = createProgressDialogView();
			mProgressDialogMsg = (TextView) rootView
					.findViewById(R.id.progress_dialog_msg);
			mProgressDialog = createProgressDialog(rootView);
		}
		mProgressDialog.setOnCancelListener(mProgressDialogCancelListener);
	}

	public void setProgressDialogMsgText(int resId)
	{
		initProgressDialog();
		mProgressDialogMsg.setText(resId);
	}

	public void setProgressDialogMsgText(String v)
	{
		initProgressDialog();
		mProgressDialogMsg.setText(v);
	}

	public void hideProgressDialogMsg()
	{
		initProgressDialog();
		mProgressDialogMsg.setVisibility(View.GONE);
	}

	public void showProgressDialogMsg()
	{
		initProgressDialog();
		mProgressDialogMsg.setVisibility(View.VISIBLE);
	}

	public void setProgressDialogCancelable(boolean flag)
	{
		initProgressDialog();
		mProgressDialog.setCancelable(flag);
	}

	public void setProgressDialogCanceledOnTouchOutside(boolean cancel)
	{
		initProgressDialog();
		mProgressDialog.setCanceledOnTouchOutside(cancel);
	}

	public void showProgressDialog()
	{
		mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG);
	}

	protected void _showProgressDialog()
	{
		initProgressDialog();
		mProgressDialog.show();
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		Window window = mProgressDialog.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		if(params.width>dm.widthPixels * 3 / 4) {
			params.width = dm.widthPixels * 3 / 4;
		}
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(params);
	}

	public void cancelProgressDialog()
	{
		mHandler.sendEmptyMessage(MSG_CANCEL_PROGRESS_DIALOG);
	}

	protected void _cancelProgressDialog()
	{
		if (mProgressDialog != null && mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
		}
	}

	public void setProgressDialogCancelListener(OnCancelListener listener)
	{
		this.mProgressDialogCancelListener = listener;
	}


	public interface DialogExtProxiable
	{
		void showProgressDialog(String msg, OnCancelListener listener,
								boolean cancelable);

		void showProgressDialog(String msg);

		void showProgressDialog(int resid);

		void showMsgDialog(String detials, String btnLeft,
						   String btnRight, OnClickListener btnLeftListener,
						   OnClickListener btnRightListener);

		void showMsgDialog(String detials, String btnLeft,
						   OnClickListener btnLeftListener);

		void showMsgDialog(String detials);

		void showMsgDialog(int res);
		void cancelMsgDialog();
		void cancelProgressDialog();
	}
}
