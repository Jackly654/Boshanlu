package hf.pull;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import hf.lib.controls.FRela;
import hf.lib.data.HZDodo;
import hf.lib.data.Logger;
import hf.util.FHandler;

/**
 * Created by Jackly on 2017/2/9.
 */

public class FPull extends FRela implements Handler.Callback
{

	private final int DURATION_RESULT = 500; // 显示成功或者失败时长

	public static final int INIT = 0; // 初始状态
	public static final int RELEASE_TO_REFRESH = 1; // 释放刷新
	public static final int REFRESHING = 2; // 正在刷新
	public static final int RELEASE_TO_LOAD = 3; // 释放加载
	public static final int LOADING = 4; // 正在加载
	public static final int SUCCEED = 5; // 刷新成功
	public static final int FAIL = 6; // 刷新失败

	ValueAnimator
		animator;
	FHandler
		handler;

	protected
	int
		vw, vh,
		mEvents, // 过滤多点触碰
		state, // 当前状态

		request, // 当前请在请求下拉/上拉
		refreshOffset;

	float
		tdx, tmx, tlx, tdy, tmy, tly, refreshDist, // 释放刷新的距离
		pullDownY, // 下拉的距离
		radio; // 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化

	boolean
		isTouch; // 在刷新过程中滑动操作
	View
		view; // 内容区域的view

	protected
	FRefreshHeader
		vHeader;

	public FPull(Activity at,int vw, int vh)
	{
		super(at, vw, vh);
		init(at, vw, vh, 0);
	}
	public FPull(Activity at, int vw, int vh, int refreshOffset)
	{
		super(at, vw, vh);
		init(at, vw, vh, refreshOffset);
	}
	private void init(Activity at, int vw, int vh, int refreshOffset)
	{
		setLayoutParams(new LayoutParams(vw, vh));
		this.vw = vw;
		this.vh = vh;
		this.refreshOffset = refreshOffset;
		radio = 2;
		handler = new FHandler(this);
		initView(at, vw, vh);
	}

	public void onDestroy()
	{
	}

	/**
	 * 在这里初始化想作为content的view, 并 addView(view);
	 * @param at
	 * @param vw
	 * @param vh
	 */
	protected void initView(Activity at, int vw, int vh)
	{
		// 初始化下拉布局
		vHeader = new FRefreshHeader(at, vw, vh);
		addView(vHeader);
		refreshDist = vHeader.btmh;
		pullDownY = 0;
	}
	// 完成刷新操作，显示刷新结果 !!!注意：刷新完成后一定要调用这个方法
	public void refreshFinish(final int refreshResult)
	{
		request = 0;
		int duration = 0;
		switch(refreshResult)
		{
			case SUCCEED:
				// 刷新成功
				changeState(SUCCEED);
				break;
			case FAIL:
			default:
				// 刷新失败
				changeState(FAIL);
				// duration = DURATION_RESULT;
				break;
		}
		// 刷新结果停留 DURATION_RESULT
		handler.removeMessages(0);
		handler.sendEmptyMessageDelayed(0, duration);
	}

	protected void loadMoreFinish(final int refreshResult)
	{
	}

	protected void changeState(int to)
	{
		state = to;
		if(vHeader != null)
		{
			vHeader.chgStatus(to);
		}
	}

	protected boolean canPullDown()
	{
		return false;
	}

	protected boolean canPullUp()
	{
		return false;
	}

	/*
	 * （非 Javadoc）由父控件决定是否分发事件，防止事件冲突
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		switch(ev.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
				tdy = ev.getY();
				tly = tdy;
				mEvents = 0;
				movedy = 0;
				cancelAnim();

				if(view != null)
				{
					view.onTouchEvent(ev);
					Logger.i("view--onTouchEvent---"+ev);
				}

				break;
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_POINTER_UP:
				// 过滤多点触碰
				mEvents = -1;
				break;
			case MotionEvent.ACTION_MOVE:

				if(mEvents == 0)
				{
					tmy = ev.getY();

					movedy = (int) Math.abs(tmy - tdy);
					if(movedy >= HZDodo.getScaledTouchSlop(getContext()))
					{
						if(canPullDown())
						{
							// 可以下拉，正在加载时不能下拉
							// 对实际滑动距离做缩小，造成用力拉的感觉
							// 根据下拉距离改变比例
							radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight() * pullDownY));
							pullDownY += (tmy - tly) / radio;
							if(pullDownY <= 0)
							{
								pullDownY = 0;
							}

							if(state == REFRESHING)
							{
								// 正在刷新的时候触摸移动
								isTouch = true;
							}
						}else
						{
							if(pullDownY > 0)
							{
								pullDownY += (tmy - tly);
							}
							// Logger.i("canPullDown false");
						}
					}
				}else
				{
					mEvents = 0;
				}

				tly = tmy;
				tlx = tmx;
				reLayout(pullDownY);

				if(pullDownY <= refreshDist && state == RELEASE_TO_REFRESH)
				{
					// 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
					changeState(INIT);
				}
				if(pullDownY >= refreshDist && state == INIT)
				{
					// 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
					changeState(RELEASE_TO_REFRESH);
				}

				// 因为刷新和加载操作不能同时进行，所以pullDownY和pullUpY不会同时不为0，因此这里用(pullDownY +
				// Math.abs(pullUpY))就可以不对当前状态作区分了
				//				if ((pullDownY + Math.abs(pullUpY)) > 8)
				if(pullDownY > 8)
				{
					// 防止下拉过程中误触发长按事件和点击事件
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}

				break;
			case MotionEvent.ACTION_UP:
				if(pullDownY >= refreshDist)
				{
					// 正在刷新时往下拉（正在加载时往上拉），释放后下拉头（上拉头）不隐藏
					isTouch = false;

					if(state == RELEASE_TO_REFRESH)
					{
						changeState(REFRESHING);
						reLayout(pullDownY = refreshDist);

						// 刷新操作
						toRefresh();
					}else if(state == REFRESHING)
					{
						reLayout(pullDownY = refreshDist);
					}else
					{
						hide();
					}
				}else
				{
					hide();
				}
			default:
				break;
		}
		return super.dispatchTouchEvent(ev);
	}
	protected void reLayout(float downy)
	{
		if(vHeader != null)
		{
			vHeader.setTranslationY(downy);
		}
		if(view != null)
		{
			view.setTranslationY(downy);
		}
	}

	protected void hide()
	{
		reLayout(pullDownY = 0);
		changeState(INIT);
	}

	protected void cancelAnim()
	{
		if(animator != null && animator.isRunning())
		{
			animator.cancel();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		// super.onLayout(changed, l, t, r, b);
		if(view != null && view.getParent() == this)
		{
			int height = vh;
			ViewGroup.LayoutParams lp = view.getLayoutParams();
			if(lp != null && lp.height > 0)
			{
				height = lp.height;
			}
			view.layout(0, 0, vw, height);
		}
		if(vHeader != null && vHeader.getParent() == this)
		{
			vHeader.layout(0, -vh + refreshOffset, vw, refreshOffset);
		}
	}

	// 主动下拉刷新
	public void pullToRefresh()
	{
		if(state == INIT)
		{
			isTouch = false;
			changeState(REFRESHING);
			reLayout(pullDownY = refreshDist);

			toRefresh();
		}else
		{
			Logger.i("未执行下拉刷新 state: " + state);
		}
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what)
		{
			case 0:
				hide();
				break;
		}
		return true;
	}

	protected void toRefresh()
	{
		if(request == 0)
		{
			request = 1;
			onRefresh();
		}else
		{
			hide();
		}
	}

	public void onRefresh()
	{
	}
}
