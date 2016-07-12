package me.czmc.imusic.proxy;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
/**
 * @author MZone
 *
 */
public class HandlerProxy
{
	private Context mContext;
	private Handler mHandler;
	private HandlerProxiable mHandlerProxiable;

	public HandlerProxy(Context context, HandlerProxiable handlerProxiable)
	{
		mContext = context;
		mHandlerProxiable = handlerProxiable;
		mHandler = new Handler(mContext.getMainLooper())
		{
			@Override
			public void handleMessage(Message msg)
			{
				processHandlerMessage(msg);
			}
		};
	}

	public void processHandlerMessage(Message msg)
	{
		mHandlerProxiable.processHandlerMessage(msg);
	}

	public Handler getHandler()
	{
		return mHandler;
	}

	public interface HandlerProxiable
	{
		void processHandlerMessage(Message msg);

		Handler getHandler();
	}
}
