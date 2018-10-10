package hf.title;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import hf.data.FSize;
import hf.data.FrameCLR;
import hf.lib.controls.FRela;
import hf.lib.data.Empty;
import hf.lib.data.StrUtil;
import hf.lib.graphic.PaintUtil;

/**
 *
 * Created by fanjl on 2016/10/12.
 */

public class VTitleBase extends FRela
{
	public static final int CLICK_NA = 0;
	public static final int CLICK_LEFT = 1;
	public static final int CLICK_CENTER = 2;
	public static final int CLICK_RIGHT = 3;

	public interface Callback
	{
		public void onTitleClick(int location);
	}

	protected
	Callback
		callback;

	protected
	Paint
		paintTmp;
	protected
	int
		iTouchId, // 见CLICK_*

		idLeft0, idLeft1, idRight0, idRight1,
		unitw, unitw_h,
		leftw, leftw_h, rightw, rightw_h, // 左/右两侧按钮宽度
		iStrokeWidth,

		bgClr, // 背景颜色
		btmLineClr; // 底部分割线

	protected
	String
		sTitle,
		sLeft, sRight;

	public VTitleBase(Activity at, int vw, int vh)
	{
		super(at, vw, vh);
		sTitle = "";
		paintTmp = new Paint();
		paintTmp.setTextSize(PaintUtil.FONTS_30);
		bgClr = FrameCLR.WHITE;
		btmLineClr = FrameCLR.DIVIDER;
		unitw = vw/8;
		unitw_h = unitw/2;
		leftw = unitw;
		leftw_h = leftw/2;
		rightw = unitw;
		rightw_h = rightw/2;
	}
	public void onDestroy()
	{
		callback = null;
	}
	public void setOnClick(Callback callback)
	{
		this.callback = callback;
	}
	public void setBackgroundColor(int color)
	{
		bgClr = color;
		reDraw();
	}
	public void setBottomLine(int color, int strokeWidth)
	{
		btmLineClr = color;
		iStrokeWidth = strokeWidth;
		reDraw();
	}
	@Override
	protected void onDraw(Canvas cvs)
	{
		cvs.setDrawFilter(PaintUtil.pfd);
		cvs.drawColor(bgClr);

		// 标题
		if(!Empty.isEmpty(sTitle))
		{
			paint.setColor(FrameCLR.WHITE);
			paint.setTextSize(PaintUtil.FONTS_30);
			paint.setFakeBoldText(true);
			cvs.drawText(sTitle, vw_h - StrUtil.pixHalf(paint, sTitle), vh_h + PaintUtil.FONTHH_30, paint);
			paint.setFakeBoldText(false);
		}

		Bitmap bm = null;
		// 左侧按钮,优先绘制图片
		if(idLeft0 != 0)
		{
			if(null != (bm = im.getBmId(iTouchId == CLICK_LEFT ? idLeft1 : idLeft0)))
			{
				cvs.drawBitmap(bm, FSize.getInstance().getDeadLR(), vh_h - bm.getHeight()/2, null);
			}
		}
		else
		{
			if(!Empty.isEmpty(sLeft))
			{
				if(iTouchId == CLICK_LEFT)
				{
					paint.setColor(FrameCLR.DIVIDER);
					cvs.drawRect(0, 0, leftw, vh, paint);
				}
				paint.setColor(FrameCLR.WHITE);
				paint.setTextSize(PaintUtil.fontS_4);
				cvs.drawText(sTitle, FSize.getInstance().getDeadLR(), vh_h + PaintUtil.fontHH_4, paint);
			}
		}
		// 右侧按钮,优先绘制图片
		if(idRight0 != 0)
		{
			if(null != (bm = im.getBmId(iTouchId == CLICK_RIGHT ? idRight1 : idRight0)))
			{
				cvs.drawBitmap(bm, vw - FSize.getInstance().getDeadLR(), vh_h - bm.getHeight()/2, null);
			}
		}
		else
		{
			if(!Empty.isEmpty(sRight))
			{
				if(iTouchId == CLICK_RIGHT)
				{
					paint.setColor(FrameCLR.DIVIDER);
					cvs.drawRect(vw - rightw, 0, vw, vh, paint);
				}
				paint.setColor(FrameCLR.WHITE);
				paint.setTextSize(PaintUtil.FONTS_26);
				cvs.drawText(sRight, vw - rightw_h - StrUtil.pixHalf(paint, sRight), vh_h + PaintUtil.FONTHH_26, paint);
			}
		}

		// 底部分割线
		paint.setColor(btmLineClr);
		paint.setStrokeWidth(iStrokeWidth);
		cvs.drawLine(0, vh - iStrokeWidth / 2, vw, vh - iStrokeWidth / 2, paint);
		paint.setStrokeWidth(0);
	}
	@Override
	protected void onTouchDown(float tdx, float tdy)
	{
		if(tdx < unitw || tdx < leftw)
		{
			iTouchId = CLICK_LEFT;
			reDraw();
		}
		else if(tdx > vw - unitw || tdx > vw - rightw)
		{
			iTouchId = CLICK_RIGHT;
			reDraw();
		}
		else
		{
			iTouchId = CLICK_CENTER;
		}
	}
	@Override
	protected void onTouchMoved()
	{
		iTouchId = CLICK_NA;
		reDraw();
	}
	@Override
	protected void onTouchCancel()
	{
		iTouchId = CLICK_NA;
		reDraw();
	}
	@Override
	protected void onTouchUp(boolean bMoved, float tux, float tuy)
	{
		if(!bMoved)
		{
			switch(iTouchId)
			{
				case CLICK_LEFT:
					if(idLeft0 != 0 || !Empty.isEmpty(sLeft))
					{
						onTitleClick(CLICK_LEFT);
					}
					break;
				case CLICK_RIGHT:
					if(idRight0 != 0 || !Empty.isEmpty(sRight))
					{
						onTitleClick(CLICK_RIGHT);
					}
					break;
				case CLICK_CENTER:
					onTitleClick(CLICK_CENTER);
					break;
			}
		}
		iTouchId = CLICK_NA;
		reDraw();
	}
	public void setLeftBtn(int rIdNormal, int rIdSelect)
	{
		idLeft0 = rIdNormal;
		idLeft1 = rIdSelect;
		reDraw();
	}
	public void setLeftBtn(String btn)
	{
		sLeft = btn;
		reDraw();
	}
	public void setRightBtn(int rIdNormal, int rIdSelect)
	{
		idRight0 = rIdNormal;
		idRight1 = rIdSelect;
		reDraw();
	}
	public void setRightBtn(String btn)
	{
		sRight = btn;
		if(Empty.isEmpty(sRight))
		{
			rightw = 0;
			rightw_h = 0;
		}
		else
		{
			Paint paint1 = new Paint();
			paint1.setTextSize(PaintUtil.fontS_4);
			rightw = FSize.getInstance().getDeadLR() + StrUtil.pix(paint1, sRight);
			rightw_h = rightw/2;

			int childCount = getChildCount();
			if(childCount > 0)
			{
				int i1 = 0;
				View view;
				ViewGroup.LayoutParams lp = null;
				while(i1 < childCount)
				{
					if(null != (view = getChildAt(i1)))
					{
						if(null != (lp = view.getLayoutParams()))
						{
							if(lp instanceof LayoutParams)
							{
								lp = reInitCenterViewLayoutParams((LayoutParams) lp);
							}
							else
							{
								lp = getCenterViewLayoutParams();
							}

							view.setLayoutParams(lp);
						}
					}
					++i1;
				}
			}
		}
		reDraw();
	}
	public void setTitle(String title)
	{
		sTitle = Empty.isEmpty(title) ? "" : (StrUtil.breakText(title, vw - unitw*2, paintTmp));
		reDraw();
	}
	public void addCenterView(View view)
	{
		if(view == null) return;
		ViewParent vp = view.getParent();
		if(vp != null && vp instanceof ViewGroup)
		{
			((ViewGroup) vp).removeView(view);
		}

		addView(view, getCenterViewLayoutParams());
	}
	protected void onTitleClick(int location)
	{
		if(callback != null)
		{
			callback.onTitleClick(location);
		}
	}
	private LayoutParams getCenterViewLayoutParams()
	{
		return reInitCenterViewLayoutParams(new LayoutParams(vw - leftw - rightw, vh));
	}
	private LayoutParams reInitCenterViewLayoutParams(LayoutParams lp)
	{
		if(lp == null) return null;
		lp.addRule(FRela.CENTER_VERTICAL);
		lp.setMargins(leftw, 0, rightw, 0);
		return lp;
	}
}
