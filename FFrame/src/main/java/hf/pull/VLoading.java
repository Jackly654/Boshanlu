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

// refreshing
public class VLoading extends FView
{
	Bitmap
		bm;
	RotateAnimation
		anim;
	
	public VLoading(Activity at, int vw, int vh)
	{
		super(at, vw, vh);
		bm = im.getBmId(R.drawable.refreshing);
		
		anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(500);
		anim.setFillAfter(true);
		anim.setRepeatCount(Animation.INFINITE);

		dismiss();
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
	public void show()
	{
		if(getVisibility() != VISIBLE)
		{
			setVisibility(VISIBLE);
			startAnimation(anim);
		}
	}
	public void dismiss()
	{
		if(getVisibility() != INVISIBLE)
		{
			setVisibility(INVISIBLE);
			clearAnimation();
		}
	}
}
