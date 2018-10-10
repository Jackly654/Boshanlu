package hf.pull;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import hf.frame.R;
import hf.lib.controls.FView;
import hf.lib.graphic.PaintUtil;

public class VArrow extends FView
{
	Bitmap
		bm;
	RotateAnimation
		anim;
	
	public VArrow(Activity at, int vw, int vh)
	{
		super(at, vw, vh);
		bm = im.getBmId(R.drawable.pull_down);
		
		anim = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(100);
		anim.setFillAfter(true);
		anim.setRepeatCount(0);
	}
	@Override
	protected void onDraw(Canvas cvs)
	{
		cvs.setDrawFilter(PaintUtil.pfd);
		if(bm != null)
		{
			cvs.drawBitmap(bm, vw_h - bm.getWidth()/2, vh_h - bm.getHeight()/2, null);
		}
	}
	public void rotate()
	{
		startAnimation(anim);
	}
	public void reset()
	{
		clearAnimation();
	}
	public void show()
	{
		if(getVisibility() != VISIBLE)
		{
			setVisibility(VISIBLE);
		}
	}
	public void dismiss()
	{
		if(getVisibility() != INVISIBLE)
		{
			setVisibility(INVISIBLE);
			reset();
		}
	}
}
