package hf.control;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.Hashtable;

import hf.lib.data.CLR;
import hf.lib.data.Empty;
import hf.lib.data.HZDodo;
import hf.lib.data.StrUtil;
import hf.lib.recycler.FoldingCirclesDrawable;
import hf.util.FHandler;

/**
 * Created by fanjl on 2017-12-26.
 */

public class VBusy implements Handler.Callback
{
	private final int MSG_ADD_PRO = 1;
	private final int MSG_REMOVE_PRO = 2;

	Activity
		at;

	private
	static VBusy
		mThis;
	ViewGroup
		vDecor;
	FHandler
		handler;

	ProgressBar
		progressBar;
	Hashtable<String, String>
		ht;

	public static VBusy getInstance(Activity at)
	{
		if(mThis == null)
		{
			synchronized(VBusy.class)
			{
				if(mThis == null)
				{
					mThis = new VBusy(at);
				}
			}
		}
		return mThis;
	}
	private VBusy(Activity at)
	{
		this.at = at;
		handler = new FHandler(this);
		vDecor = (ViewGroup) at.getWindow().getDecorView();

		ht = new Hashtable<>();

		progressBar = new ProgressBar(at);

		int h = HZDodo.getSBar(at);
		int margin = h/10;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(h - margin*2, h - margin*2);
		lp.setMargins(margin, margin, margin, margin);
		progressBar.setLayoutParams(lp);

		int[] iArr = new int[4];
		iArr[0] = CLR.HOLO_BLUE_LIGHT;
		iArr[1] = CLR.HOLO_RED_LIGHT;
		iArr[2] = CLR.HOLO_GREEN_LIGHT;
		iArr[3] = CLR.HOLO_ORANGE_LIGHT;
		progressBar.setIndeterminateDrawable(new FoldingCirclesDrawable(iArr));
	}
	// 繁忙提示,主要用于网络请求时的提醒
	public void busy(String tag)
	{
		if(handler != null)
		{
			Message msg = handler.obtainMessage(MSG_ADD_PRO);
			msg.obj = tag;
			handler.sendMessage(msg);
		}
	}
	// UI Thread
	private void showProgress(String tag)
	{
		String md5 = getMd5(tag);
		if(!Empty.isEmpty(md5))
		{
			ht.put(md5, md5);
		}

		if(progressBar != null && progressBar.getParent() == null)
		{
			if(!Empty.isEmpty(ht))
			{
				if(vDecor != null)
				{
					vDecor.addView(progressBar);
				}
			}
		}
	}
	public void idle(String tag)
	{
		if(handler != null)
		{
			Message msg = handler.obtainMessage(MSG_REMOVE_PRO);
			msg.obj = tag;
			msg.sendToTarget();
		}
	}
	// UI Thread
	private void hideProgress(String tag)
	{
		if(!Empty.isEmpty(ht))
		{
			String md5 = getMd5(tag);
			if(!Empty.isEmpty(md5))
			{
				ht.remove(md5);
			}
		}
		if(Empty.isEmpty(ht))
		{
			if(progressBar != null)
			{
				ViewGroup vg = (ViewGroup) progressBar.getParent();
				if(vg != null)
				{
					vg.removeView(progressBar);
				}
			}
		}
	}
	private String getMd5(String value)
	{
		return StrUtil.getMD5Lower(value);
	}
	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what)
		{
			case MSG_ADD_PRO:

				showProgress(msg.obj != null ? msg.obj.toString() : null);

				break;

			case MSG_REMOVE_PRO:

				hideProgress(msg.obj != null ? msg.obj.toString() : null);

				break;
		}
		return true;
	}
}
