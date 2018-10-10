package hf.pull;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;

import hf.data.FrameCLR;
import hf.data.ResMng;
import hf.frame.R;
import hf.lib.controls.FRela;
import hf.lib.data.Logger;
import hf.lib.data.StrUtil;
import hf.lib.graphic.PaintUtil;
import hf.util.FHandler;

public class FRefreshHeader extends FRela implements Handler.Callback
{
	Activity
		at;
	ResMng
		rm;
	FHandler
		handler;

	Bitmap
		bm;

	VLoading
		vLoading;
	VArrow
		vArrow;

	String
		sTip;
	int
		dx,
		divider,
		btmh, btmh_h,
		status;

	public FRefreshHeader(Activity at, int vw, int vh)
	{
		super(at, vw, vh);
		this.at = at;

		btmh = dip2px(64);
		btmh_h = btmh/2;
		divider = dip2px(10);
		rm = ResMng.getInstance(at);
		sTip = rm.getString(R.string.pull_to_refresh);
		status = FPull.INIT;

		LayoutParams lp = new LayoutParams(vw / 2, btmh);
		vLoading = new VLoading(at, lp.width, lp.height);
		addView(vLoading, lp);

		vArrow = new VArrow(at, lp.width, lp.height);
		addView(vArrow, lp);
		handler = new FHandler(this);
	}

	public void setNewH(int vh)
	{
		this.vh = vh;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		if(vArrow != null)
		{
			vArrow.layout(0, vh - btmh, vw / 2, vh);
		}

		if(vLoading != null)
		{
			vLoading.layout(0, vh - btmh, vw / 2, vh);
		}
	}

	@Override
	protected void onDraw(Canvas cvs)
	{
		cvs.setDrawFilter(PaintUtil.pfd);

		Paint paint =  PaintUtil.paint;

		paint.setColor(FrameCLR.GRAY_1);
		paint.setTextSize(PaintUtil.fontS_4);

		if(sTip != null)
		{
			cvs.drawText(sTip, dx = vw_h - StrUtil.pixHalf(paint, sTip), vh - btmh_h + PaintUtil.fontHH_4, paint);
		}

		switch(status)
		{
			case FPull.SUCCEED:
				// bm = im.getBmId(R.drawable.pull_succeed);
				bm = null;
				break;
			case FPull.FAIL:
				// bm = im.getBmId(R.drawable.pull_failed);
				bm = null;
				break;
			default:
				bm = null;
				break;
		}
		if(bm != null)
		{
			dx -= divider;
			cvs.drawBitmap(bm, dx - bm.getWidth(), vh - btmh_h - bm.getHeight()/2, null);
		}
	}

	public int dip2px(int dipValue)
	{
		final float scale = at.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}
	public void chgStatus(final int state)
	{
		status = state;

		Message msg = handler.obtainMessage(0);
		msg.arg1 = state;
		msg.sendToTarget();
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		// sCo = "";
		switch(msg.what)
		{
			case 0:

				switch (msg.arg1)
				{
					case FPull.INIT:
						// Logger.i("初始状态");
						// sTip = "下拉刷新";
						sTip = rm.getString(R.string.pull_to_refresh);
						if(vArrow != null)
						{
							vArrow.show();
							vArrow.reset();
						}
						if(vLoading != null)
						{
							vLoading.dismiss();
						}

						break;
					case FPull.RELEASE_TO_REFRESH:
						// 释放刷新状态
						// Logger.i("释放刷新");
						// sCo = rm.getString(R.string.release_to_co);
						// sTip = "释放刷新";
						sTip = rm.getString(R.string.release_to_refresh);
						if(vArrow != null)
						{
							vArrow.rotate();
						}
						if(vLoading != null)
						{
							vLoading.dismiss();
						}

						break;
					case FPull.REFRESHING:
						// 正在刷新状态
						// Logger.i("正在刷新");
						// sTip = "正在刷新";
						sTip = rm.getString(R.string.refreshing);
						if(vArrow != null)
						{
							vArrow.dismiss();
						}
						if(vLoading != null)
						{
							vLoading.show();
						}

						break;
					case FPull.SUCCEED:
						// Logger.i("刷新成功");
						// sTip = "刷新成功";
						// sTip = rm.getString(R.string.refresh_succeed);
						sTip = "";
						if(vArrow != null)
						{
							vArrow.dismiss();
						}
						if(vLoading != null)
						{
							vLoading.dismiss();
						}

						break;
					case FPull.FAIL:
						// Logger.i("刷新失败");
						// sTip = "刷新失败";
						// sTip = rm.getString(R.string.refresh_fail);
						sTip = "";
						if(vArrow != null)
						{
							vArrow.dismiss();
						}
						if(vLoading != null)
						{
							vLoading.dismiss();
						}

						break;
					default:
						Logger.i("未处理的状态: " + msg.arg1);
						break;
				}
				reDraw();

				break;
		}

		return true;
	}
}
