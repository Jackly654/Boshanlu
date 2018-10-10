package hf.control;

import android.app.Activity;
import android.graphics.Canvas;

import hf.data.FSize;
import hf.data.FrameCLR;
import hf.data.SFD;
import hf.frame.R;
import hf.frame.VFrameRL;
import hf.ifs.IBase;
import hf.lib.controls.FLinear;
import hf.lib.graphic.PaintUtil;
import hf.title.VTitleBase;

/**
 *
 * Created by fanjl on 2016/10/12.
 */
public class VBaseLinear extends FLinear implements IBase, VTitleBase.Callback
{
	protected
	Activity
		at;
	protected
	VFrameRL
		vrl;
	protected
	VTitleBase
		vTitle;
	protected
	int
		dyCont,
		iStatus;

	public VBaseLinear(Activity at, VFrameRL vrl, int vw, int vh, boolean hasDefaultTitle)
	{
		super(at, vw, vh);
		this.vrl = vrl;
		this.vw = vw;
		this.vh = vh;
		this.vw_h = vw / 2;
		this.vh_h = vh / 2;
		setOrientation(FLinear.VERTICAL);
		setWillNotDraw(false);

		this.at = at;

		if(hasDefaultTitle)
		{
			vTitle = new VTitleBase(at, vw, dyCont = FSize.getInstance().getTtH());
			vTitle.setOnClick(this);
			vTitle.setId(vTitle.hashCode());
			vTitle.setLeftBtn(R.drawable.back_n, R.drawable.back_s);
			addView(vTitle);
		}
	}
	@Override
	public void onDestroy()
	{
	}
	@Override
	protected void onDraw(Canvas cvs)
	{
		cvs.setDrawFilter(PaintUtil.pfd);
		cvs.drawColor(FrameCLR.BG);
	}
	@Override
	public void onChangedView(final int status, final Object obj)
	{
		iStatus = status;
		switch(status)
		{
			case S_ENTER_PREV:
				onPreEnter(obj);
				break;
			case S_ENTER_COMPLETE:
				break;
			case S_PAUSE_PREV:
				break;
			case S_PAUSE_COMPLETE:
				break;
			case S_RESUME_PREV:
				break;
			case S_RESUME_COMPLETE:
				onReEnter(obj);
				break;
			case S_LEAVE_PREV:
				break;
			case S_LEAVE_COMPLETE:
				break;
		}
	}
	protected void onPreEnter(Object obj)
	{
	}
	protected void onReEnter(Object obj)
	{
	}
	@Override
	public boolean onPressBack()
	{
		return false;
	}

	@Override
	public void onTitleClick(int location)
	{
		switch(location)
		{
			case VTitleBase.CLICK_LEFT:
				onTitleClickLeft();
				break;
			case VTitleBase.CLICK_CENTER:
				onTitleClickCenter();
				break;
			case VTitleBase.CLICK_RIGHT:
				onTitleClickRight();
				break;
		}
	}
	protected void onTitleClickLeft()
	{
		if(vrl != null)
		{
			vrl.onPressBack(SFD.BACK_TOUCH);
		}
	}
	protected void onTitleClickRight()
	{
	}
	protected void onTitleClickCenter()
	{
	}
	// 当前页面是否在前台
	protected boolean isForeground()
	{
		return iStatus == IBase.S_ENTER_COMPLETE || iStatus == IBase.S_RESUME_COMPLETE;
	}
	protected void showTip(final int id)
	{
		if(isForeground() && vrl != null)
		{
			vrl.showTip(id);
		}
	}
}
