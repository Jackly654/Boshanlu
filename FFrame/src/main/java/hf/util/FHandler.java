package hf.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by DX on 2017/3/6.
 */

public class FHandler
{
	private
	Handler
		handler;

	public FHandler()
	{
		this(null);
	}
	public FHandler(final Handler.Callback callback)
	{
		handler = new Handler(Looper.getMainLooper(), callback);
	}

	public void sendMessage(Message msg)
	{
		if(msg != null)
		{
			removeMessages(msg.what);
			msg.sendToTarget();
		}
	}
	public void sendEmptyMessage(int what)
	{
		if(handler != null)
		{
			removeMessages(what);
			handler.sendEmptyMessage(what);
		}
	}
	public void removeMessages(int what)
	{
		if(handler != null)
		{
			handler.removeMessages(what);
		}
	}
	public void sendEmptyMessageDelayed(int what, long delayMillis)
	{
		if(handler != null)
		{
			removeMessages(what);
			handler.sendEmptyMessageDelayed(what, delayMillis);
		}
	}

	public void sendMessageDelayed(Message msg, long delayMillis)
	{
		if(msg != null && handler != null)
		{
			removeMessages(msg.what);
			handler.sendMessageDelayed(msg, delayMillis);
		}
	}
	public Message obtainMessage(int what)
	{
		return handler != null ? handler.obtainMessage(what) : null;
	}
	public void clear()
	{
		if(handler != null)
		{
			handler.removeCallbacksAndMessages(null);
		}
	}
}
