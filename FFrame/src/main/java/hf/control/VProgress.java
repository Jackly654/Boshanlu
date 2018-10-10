package hf.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import hf.bean.TipConfigProgress;
import hf.data.FrameCLR;
import hf.frame.R;
import hf.ifs.IGeneralString;
import hf.lib.data.CLR;
import hf.lib.data.Empty;
import hf.lib.data.Logger;
import hf.lib.graphic.PaintUtil;
import hf.util.FHandler;

/**
 *
 * Created by duxin on 2016/9/7.
 */

public class VProgress extends LinearLayout implements Handler.Callback
{
	public static final String ROOT_TAG = "PROGRESS ROOT TAG";

	private final int MSG_SHOW = 0;
	private final int MSG_DISMISS = 1;

	RotateAnimation
		rotateAnimation;

	private
	LinearLayout
		linearLayout;

	private
	TextView
		textView;

	private
	ImageView
		imageView;

	private
	ViewGroup
		vParent;

	FHandler
		fHandler;

	TipConfigProgress
		config;

	boolean
		mShowing;

	private
	int
		width, height;

	IGeneralString
		iGeneral;

	public VProgress(Context context)
	{
		super(context);

		try
		{
			setWillNotDraw(false);

			vParent = (ViewGroup) (((Activity)context).findViewById(android.R.id.content));

			DisplayMetrics metrics = new DisplayMetrics();
			((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

			width = metrics.widthPixels;
			height = metrics.heightPixels;

			if(height < width)
			{
				width ^= height;
				height ^= width;
				width ^= height;
			}

			LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			params.leftMargin = width / 10;
			params.rightMargin = width / 10;

			int minHeight = height / 10,
					iSpaceLR = width / 15,
					iSpaceTB = height / 40;

			GradientDrawable gradientDrawable = new GradientDrawable();
			gradientDrawable.setCornerRadius(minHeight / 8);
			gradientDrawable.setColor(CLR.TS);

			linearLayout = new LinearLayout(context);
			linearLayout.setOrientation(linearLayout.HORIZONTAL);
			linearLayout.setBackground(gradientDrawable);
			linearLayout.setMinimumHeight(minHeight);
			addView(linearLayout, params);

			params = new LayoutParams(width * 23 / 100, minHeight);
			imageView = new ImageView(context);
			imageView.setPadding(iSpaceLR, iSpaceTB, iSpaceLR, iSpaceTB);
			imageView.setImageResource(R.drawable.loading);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			linearLayout.addView(imageView, params);

			params = new LayoutParams(width - width / 8 - width * 23 / 100, ViewGroup.LayoutParams.WRAP_CONTENT);
			textView = new TextView(context);
			textView.setPadding(0, iSpaceTB, iSpaceLR, iSpaceTB);
			textView.setTextColor(CLR.F1);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			textView.setMinimumHeight(minHeight);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			linearLayout.addView(textView, params);

			rotateAnimation = new RotateAnimation(0, 360, width * 23 / 200, minHeight / 2);
			rotateAnimation.setDuration(1000);
			rotateAnimation.setInterpolator(new LinearInterpolator());
			rotateAnimation.setRepeatCount(Animation.INFINITE);

			fHandler = new FHandler(this);
		}
		catch(Exception e)
		{
			Logger.e("VProgress VProgress error == " + e.toString());
		}
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.setDrawFilter(PaintUtil.pfd);
		canvas.drawColor(FrameCLR.TS);
	}
	public void showProgress(TipConfigProgress config, IGeneralString iGeneralString)
	{
		setListener(iGeneralString);
		if(config != null)
		{
			setProgressBmId(config.iBmResId);
			setMessage(config.sMsg);
		}
		show();
	}
	public void setListener(IGeneralString iGeneral)
	{
		this.iGeneral = iGeneral;
	}
	private void setProgressBmId(int id)
	{
		if(id != 0)
		{
			if(imageView != null)
			{
				imageView.setImageResource(id);
			}
		}
	}
	private void setMessage(String msg)
	{
		try
		{
			if(textView != null)
			{
				if(Empty.isEmpty(msg))
				{
					if(textView.getVisibility() != GONE)
					{
						textView.setVisibility(GONE);

						if(linearLayout.getLayoutParams() instanceof LayoutParams)
						{
							LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();

							if(layoutParams != null)
							{
								int marginLR = width * 19 / 50;
								layoutParams.leftMargin = marginLR;
								layoutParams.rightMargin = marginLR;
								linearLayout.setLayoutParams(layoutParams);
							}
						}
					}
				}
				else
				{
					textView.setText(msg);

					if(textView.getVisibility() != VISIBLE)
					{
						textView.setVisibility(VISIBLE);

						if(linearLayout.getLayoutParams() instanceof LayoutParams)
						{
							LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();

							if(layoutParams != null)
							{
								int marginLR = width / 16;
								layoutParams.leftMargin = marginLR;
								layoutParams.rightMargin = marginLR;
								linearLayout.setLayoutParams(layoutParams);
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			Logger.e("VProgress setMessage error == " + e.toString());
		}
	}

	public void show()
	{
		try
		{
			if(mShowing)
			{
				return;
			}
			mShowing = true;

			if (Looper.myLooper() == Looper.getMainLooper())
			{
				showDialog();
			}
			else
			{
				fHandler.sendEmptyMessage(MSG_SHOW);
			}
		}
		catch(Exception e)
		{
			Logger.e("VProgress show error == " + e.toString());
		}
	}

	private void setFocus()
	{
		setFocusable(true);
		setFocusableInTouchMode(true); // 设置焦点
		requestFocus(); // 请求焦点
	}

	private void showDialog()
	{
		try
		{
			if(vParent != null && getParent() == null)
			{
				vParent.addView(this);
			}

			if(imageView != null && rotateAnimation != null)
			{
				imageView.clearAnimation();
				imageView.setAnimation(rotateAnimation);
				rotateAnimation.start();
			}

			setFocus();
		}
		catch(Exception e)
		{
			Logger.e("VProgress showDialog error == " + e.toString());
		}
	}

	private void dismissDialog()
	{
		try
		{
			mShowing = false;
			config = null;

			if(vParent != null && getParent() != null)
			{
				vParent.removeView(this);
			}

			if(rotateAnimation != null)
			{
				rotateAnimation.cancel();
			}
		}
		catch(Exception e)
		{
			Logger.e("VProgress dismissDialog error == " + e.toString());
		}
	}

	public boolean isShowing()
	{
		return isShowing(config != null ? config.sTag : null);
	}
	public boolean isShowing(String tag)
	{
		String tmpTag = null;
		if(config != null)
		{
			tmpTag = config.sTag;
		}

		if(!Empty.isEmpty(tag))
		{
			if(tag.equals(tmpTag))
			{
				return true;
			}
		}
		return mShowing;
	}
	public TipConfigProgress getConfig()
	{
		return config;
	}
	public void onDestroy()
	{
		try
		{
			if(fHandler != null)
			{
				fHandler.clear();
				fHandler = null;
			}

			dismissDialog();

			if(textView != null)
			{
				textView.setText("");
				textView = null;
			}

			if(imageView != null)
			{
				imageView.clearAnimation();
				imageView.setBackground(null);
				imageView = null;
			}

			config = null;
			iGeneral = null;
		}
		catch(Exception e)
		{
			Logger.e("VProgress onDestroy error == " + e.toString());
		}
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
		{
			onBackPressed();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return true;
	}
	private void onBackPressed()
	{
		if (config != null && config.canBack)
		{
			dismiss();
			if(iGeneral != null)
			{
				iGeneral.onResponse(config.sTag);
			}
		}
	}

	public void dismiss()
	{
		if (Looper.myLooper() == Looper.getMainLooper())
		{
			dismissDialog();
		}
		else
		{
			if(fHandler != null)
			{
				fHandler.sendEmptyMessage(MSG_DISMISS);
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what)
		{
			case MSG_SHOW:
				showDialog();
				break;
			case MSG_DISMISS:
				dismissDialog();
				break;
		}
		return true;
	}
}
