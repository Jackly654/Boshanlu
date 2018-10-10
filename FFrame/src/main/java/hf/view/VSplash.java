package hf.view;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import hf.data.FSize;
import hf.data.FrameCLR;
import hf.data.ResMng;
import hf.frame.R;
import hf.frame.VFrameRoot;
import hf.ifs.IOnClick;
import hf.lib.controls.FRela;
import hf.lib.controls.FView;
import hf.lib.data.Empty;
import hf.lib.data.Logger;
import hf.lib.data.StrUtil;
import hf.lib.graphic.PaintUtil;
import hf.lib.handler.IHandleMsg;
import hf.lib.handler.Msg;
import hf.lib.handler.SHandler;

public class VSplash extends FRela implements IHandleMsg
{
	private final int MSG_TIME_STEP = 1;

	Activity
		at;
	VFrameRoot
		vRoot;

	ResMng
		resMng;

	ImageView
		imageBottom;

	View
		adView;

	VSkip
		vSkip;

	SHandler
		sHandler;

	protected
	int
		iTopH,
		iTimer,
		iAdH,
		iTopOffset,
		paddingTB;

	public VSplash(final Activity at, final VFrameRoot vRoot, int btmResId, final int fw, final int fh)
	{
		super(at, fw, fh);
		this.at = at;
		this.vRoot = vRoot;

		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		sHandler = SHandler.getInstance();

		iTopH = FSize.getInstance().getRelativeHeight(1040);
		paddingTB = FSize.getInstance().getRelativeHeight(15);

		iTimer = 3;

		resMng = ResMng.getInstance(at);

		iAdH = 1722 * vw / 1242;

		iTopOffset = (iTopH - iAdH) / 2;

		initBitmap(btmResId);
	}
	protected void initBitmap(int btmResId)
	{
		try
		{
			imageBottom = new ImageView(at);
			imageBottom.setBackgroundColor(FrameCLR.WHITE);
			LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, vh - iTopH);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			imageBottom.setLayoutParams(layoutParams);
			imageBottom.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageBottom.setImageBitmap(im.getBmId(btmResId, FSize.getInstance().getRelativeWidth(350)));
			addView(imageBottom);
		}
		catch(Exception exc)
		{
			vRoot.dismissSplash();
		}
	}
	public void addAdView(View view)
	{
		adView = view;
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);

		if(adView != null)
		{
			adView.layout(0, iTopOffset, vw, iTopOffset + iAdH);
		}
	}

	public void onPermissionsAccept()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				loopNormalAd(null);
			}
		}.start();
	}
	public void onDestroy()
	{
		removeAllViews();
		imageBottom = null;
	}
	@Override
	protected void onDraw(Canvas cvs)
	{
		cvs.setDrawFilter(PaintUtil.pfd);
		cvs.drawColor(FrameCLR.WHITE);
	}
	private void loopNormalAd(final String path)
	{
		if(sHandler != null)
		{
			Msg msg = sHandler.obtainMessage();
			msg.what = MSG_TIME_STEP;
			msg.iHandleMessage = this;
			msg.obj = path;
			msg.delay = 1000;
			sHandler.sendMsg(msg);
		}
	}
	@Override
	public void onHandleMsg(Msg msg)
	{
		try
		{
			switch(msg.what)
			{
				case MSG_TIME_STEP:
					-- iTimer;
					if(iTimer < 0)
					{
						iTimer = 0;

						if(vRoot != null)
						{
							vRoot.dismissSplash();
						}
					}
					else
					{
						if(adView != null && iTimer == 1)
						{
							iTimer = 4;

							addView(adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, iAdH));

							if(vSkip == null)
							{
								vSkip = new VSkip(at, vw, vh, new IOnClick()
								{
									@Override
									public void onClick(int code, String msg)
									{
										vRoot.dismissSplash();
									}
								});
								addView(vSkip);
							}
						}
						if(vSkip != null)
						{
							vSkip.setShowNumber(iTimer);
						}
						loopNormalAd(null);
					}
					break;
			}
		}
		catch(Exception exc)
		{
			Logger.e("VSplash handleMessage()" + exc.toString());
			vRoot.dismissSplash();
		}
	}

	class VSkip extends FView
	{
		IOnClick
			clickBack;
		RectF
			rectF;
		String
			sJump;
		int
			iNumber,
			paddingLR,
			spacing,
			spacingNumber,
			rx;
		boolean
			bShowNumber;

		public VSkip(Activity at, int vw, int vh, IOnClick clickBack)
		{
			super(at, vw, vh);
			this.clickBack = clickBack;

			paddingLR = FSize.getInstance().getRelativeWidth(26);
			spacing = FSize.getInstance().getRelativeWidth(15);

			sJump = resMng.getString(R.string.jump);

			paint.setTextSize(PaintUtil.FONTS_24);
			this.vw = paddingLR * 4 + StrUtil.pix(paint, sJump);
			this.vh = paddingTB * 4 + PaintUtil.FONTS_24;

			LayoutParams layoutParams = new LayoutParams(this.vw, this.vh);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			setLayoutParams(layoutParams);

			rx = FSize.getInstance().getRelativeWidth(30);

			rectF = new RectF(paddingLR, paddingTB, this.vw - paddingLR, this.vh - paddingTB);
		}

		public void setShowNumber(final int number)
		{
			iNumber = number;
			bShowNumber = true;

			reDraw();
			if(spacingNumber == 0)
			{
				spacingNumber = FSize.getInstance().getRelativeWidth(10);
				paint.setTextSize(PaintUtil.FONTS_24);
				this.vw = paddingLR * 4 + StrUtil.pix(paint, sJump) + spacingNumber + StrUtil.pix(paint, "8");
				rectF.left = paddingLR;
				rectF.right = this.vw - paddingLR;

				ViewGroup.LayoutParams layoutParams = getLayoutParams();
				if(layoutParams != null)
				{
					layoutParams.width = vw;
					layoutParams.height = vh;
				}
				else
				{
					layoutParams = new ViewGroup.LayoutParams(vw, vh);
				}
				setLayoutParams(layoutParams);
			}
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			canvas.setDrawFilter(PaintUtil.pfd);

			if(rectF != null)
			{
				paint.setColor(FrameCLR.TS);
				canvas.drawRoundRect(rectF, rx, rx, paint);

				paint.setColor(FrameCLR.WHITE);
				paint.setTextSize(PaintUtil.FONTS_24);
				if(!Empty.isEmpty(sJump))
				{
					canvas.drawText(sJump, rectF.left + paddingLR, rectF.centerY() + PaintUtil.FONTHH_24, paint);
				}
				if(bShowNumber)
				{
					canvas.drawText(iNumber + "", rectF.right - paddingLR - StrUtil.pix(paint, "8"), rectF.centerY() + PaintUtil.FONTHH_24, paint);
				}
			}
		}

		@Override
		protected void onTouchUp(boolean bMoved, float tux, float tuy)
		{
			if(!bMoved)
			{
				if(clickBack != null)
				{
					clickBack.onClick(0, null);
				}
			}
		}
	}
}
