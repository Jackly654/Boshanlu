package hf.frame;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import hf.FrameAt;
import hf.data.FSize;
import hf.data.SFD;
import hf.lib.controls.FRela;
import hf.lib.data.HZDodo;
import hf.lib.data.Logger;
import hf.lib.graphic.PaintUtil;
import hf.lib.img.ImgMng;
import hf.view.VSplash;

public class VFrameRoot extends FRela
{
	protected FrameAt
		at;

	protected
	VFrameRL
		vFrameRL;

	protected
	VSplash
		vSplash;
//	CheckPermissions
//		cp;
	ValueAnimator
		animator;
	public
	String
		sChannel;
	int
		alpha,
		iVRLVisible;

	boolean
		isFitWindows,
		isDisallowIntercept;

	public VFrameRoot(final FrameAt at, final String sChannel)
	{
		super(at);

		this.at = at;
		this.sChannel = sChannel;
//		setFitsSystemWindows(isFitWindows = false);
		isFitWindows = false;

		PaintUtil.getInstance(at.getWindowManager());
		im = ImgMng.getInstance(at);

		iVRLVisible = SFD.S_VRL_PAUSE;

		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				getViewTreeObserver().removeOnGlobalLayoutListener(this);
				//	if(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == at.getRequestedOrientation())
				{
					Logger.i("OnGlobalLayoutListener " + (Thread.currentThread().getName()));
					vw = getWidth();
					vh = getHeight() - (isFitWindows ? HZDodo.getSBar(getContext()) : 0);

					// !!! 重要,获取到宽/高后初始化尺寸管理类
					FSize.getInstance().setScreenSize(vw, vh);

					loadSplash(at, 0, vw, vh);
//					checkPermissions();
					onPermissionsAccept();
				}
			}
		});
	}
	@Override
	public void onDraw(Canvas cvs)
	{
	}
	public void onNewIntent(Intent intent)
	{
		if(vFrameRL != null)
		{
			vFrameRL.onNewIntent(intent);
		}
	}
	public int getVRLVisible()
	{
		return iVRLVisible;
	}

	public void onPause()
	{
		if(iVRLVisible != SFD.S_VRL_PAUSE)
		{
			iVRLVisible = SFD.S_VRL_PAUSE;
		}

		if(vFrameRL != null)
		{
			vFrameRL.onPause();
		}
	}
	public void onResume()
	{
		if(vSplash != null)
		{
			if(iVRLVisible != SFD.S_VRL_PAUSE)
			{
				iVRLVisible = SFD.S_VRL_PAUSE;
			}
		}
		else
		{
			if(iVRLVisible != SFD.S_VRL_RESUME)
			{
				iVRLVisible = SFD.S_VRL_RESUME;
			}
		}

		if(vFrameRL != null)
		{
			vFrameRL.onResume();
		}
	}
	public void onDestroy()
	{
		if(vFrameRL != null)
		{
			vFrameRL.onDestroy();
		}
		ImgMng.getInstance(at).destroy();
	}
	public void onPressBack()
	{
		if(vFrameRL != null)
		{
			vFrameRL.onPressBack(SFD.BACK_KEY);
		}
		else
		{
			at.exit();
		}
	}

	protected void loadSplash(FrameAt at, int redId, int vw, int vh)
	{
		addView(vSplash = new VSplash(at, this, redId, vw, vh));
	}
//	private void checkPermissions()
//	{
//		cp = new CheckPermissions(at, new CheckPermissions.IPermission()
//		{
//			@Override
//			public void onAccept()
//			{
//				onPermissionsAccept();
//			}
//			@Override
//			public void onRefuse()
//			{
//				exit();
//			}
//		});
//	}
	private void onPermissionsAccept()
	{
		if(vSplash != null)
		{
			vSplash.onPermissionsAccept();
		}

		postDelayed(new Runnable()
		{
			public void run()
			{
				if(checkSignMd5())
				{
					loadMain();
				}
			}
		}, 1000);
	}
	// 子类继承该方法,load自己的VRL
	protected void loadMain()
	{
		if(vFrameRL == null)
		{
			vFrameRL = getVRL();
			addView(vFrameRL, 0);
		}
	}
	// 子类继承
	protected VFrameRL getVRL()
	{
		return new VFrameRL(at, this, vw, vh, sChannel);
	}
	public void dismissSplash()
	{
		if(vSplash == null)
		{
			iVRLVisible = SFD.S_VRL_RESUME;
			return;
		}

		at.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(animator == null) animator = ValueAnimator.ofFloat(1.0f, 0.0f);
				if(animator.isRunning()) return;

				animator.setInterpolator(new AccelerateInterpolator());
				animator.setDuration(500).start();
				animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
				{
					public void onAnimationUpdate(ValueAnimator animation)
					{
						Float value = ((Float) animation.getAnimatedValue()).floatValue();

						if(vSplash != null)
						{
							vSplash.setAlpha(value);
							alpha = (int) (255*(1-value));
							reDraw();
						}

						if(value == 0)
						{
							if(vSplash != null)
							{
								removeView(vSplash);
								vSplash.onDestroy();
								vSplash = null;

								alpha = 255;
								reDraw();
							}

							iVRLVisible = SFD.S_VRL_RESUME;
						}
					}
				});
			}
		});
	}
	protected boolean checkSignMd5()
	{
		if(SFD.CHANNEL_DEBUG_VALUE.equals(sChannel)) // DEBUG 模式不需要校验
		{
			return true;
		}

		return false;
	}
	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept)
	{
		// keep the info about if the innerViews do requestDisallowInterceptTouchEvent
		isDisallowIntercept = disallowIntercept;
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		// the incorrect array size will only happen in the multi-touch scenario.
		try
		{
			if(ev != null)
			{
				if (ev.getPointerCount() > 1 && isDisallowIntercept)
				{
					requestDisallowInterceptTouchEvent(false);
					boolean handled = super.dispatchTouchEvent(ev);
					requestDisallowInterceptTouchEvent(true);
					return handled;
				}
			}
		}
		catch(Exception exc)
		{
			Logger.e("VRoot dispatchTouchEvent " + exc.toString());
		}

		return super.dispatchTouchEvent(ev);
	}
	public void exit()
	{
		if(at != null)
		{
			at.exit();
		}
	}
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
//		if(cp != null)
//		{
//			cp.onRequestPermissionsResult(requestCode, permissions, grantResults);
//		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(vFrameRL != null)
		{
			vFrameRL.onActivityResult(requestCode, resultCode, data);
		}
	}
}