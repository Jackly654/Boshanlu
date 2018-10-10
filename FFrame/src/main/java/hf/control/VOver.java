package hf.control;

import android.app.Activity;
import android.support.annotation.NonNull;

import hf.bean.TipConfigDialog;
import hf.bean.TipConfigProgress;
import hf.ifs.IGeneralString;
import hf.ifs.IOnClickOver;
import hf.lib.controls.TstUtil;

public class VOver
{
	static
	VOver
		mThis;
	Activity
		at;
	TstUtil
		tu;
	V7Dialog
		v7Dialog;
	VProgress
		vProgress;

	int
		fw, fh;

	public static VOver getInstance(final Activity at, final int fw, final int fh)
	{
		if(mThis == null)
		{
			synchronized(VOver.class)
			{
				if(mThis == null)
				{
					mThis = new VOver(at, fw, fh);
				}
			}
		}
		return mThis;
	}
	private VOver(final Activity at, final int fw, final int fh)
	{
		this.at = at;
		this.fw = fw;
		this.fh = fh;
	}
	public void onDestroy()
	{
		tu = null;
		if(v7Dialog != null)
		{
			v7Dialog.onDestroy();
			v7Dialog = null;
		}
		if(vProgress != null)
		{
			vProgress.onDestroy();
			vProgress = null;
		}
		mThis = null;
	}
	// 获取dialog实例
	public V7Dialog getDialog()
	{
		if(v7Dialog == null)
		{
			v7Dialog = new V7Dialog(at);
		}
		return v7Dialog;
	}
	// 获取progress实例
	private VProgress getProgress()
	{
		if(vProgress == null)
		{
			vProgress = new VProgress(at);
		}
		return vProgress;
	}
	// 显示提示框
	public void showDialog(TipConfigDialog tipConfig, IOnClickOver iOnClickOver)
	{
		getDialog().show(tipConfig, iOnClickOver);
	}
	public void showProgress(TipConfigProgress config, IGeneralString iGeneralString)
	{
		getProgress().showProgress(config, iGeneralString);
	}
	// 隐藏进度条
	public void dismissProgress(final boolean isSuccess, final @NonNull String tag)
	{
		if(vProgress != null && vProgress.isShowing(tag))
		{
			vProgress.dismiss();
		}
	}
	// TOAST 提示
	public void showTip(final String txt)
	{
		if(tu == null) tu = new TstUtil(at, TstUtil.SHORT);
		tu.showTst(txt);
	}

	protected boolean progressIsShow()
	{
		return vProgress != null && vProgress.isShowing();
	}
}
