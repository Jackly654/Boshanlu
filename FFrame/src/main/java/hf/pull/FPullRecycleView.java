package hf.pull;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import hf.data.ResMng;
import hf.frame.R;
import hf.ifs.IScrollListener;
import hf.lib.data.Logger;
import hf.lib.graphic.PaintUtil;
import hf.lib.recycler.FRecyclerView;
import hf.lib.recycler.data.LoadMoreTextConfig;
import hf.lib.recycler.notice.IItem;
import hf.lib.recycler.notice.IRecycleView;

/**
 * Created by Jackly on 2017/2/9.
 */

public class FPullRecycleView extends FPull
{
	IScrollListener
		iScrollListener;

	protected FRecyclerView
		vRecycle; // 内容区域的view
	View
		seatView; // 占位view,充当 recycle view header view

	public FPullRecycleView(Activity at, int vw, int vh)
	{
		super(at, vw, vh, 0);
	}
	public FPullRecycleView(Activity at, int vw, int vh, int refreshOffset)
	{
		super(at, vw, vh, refreshOffset);
	}

	@SuppressLint("InflateParams")
	protected void initView(Activity at, int vw, int vh)
	{
		// 初始化下拉布局
		super.initView(at, vw, vh);
		vRecycle = new FRecyclerView(at, vw, vh);
		view = vRecycle;

		LoadMoreTextConfig config = new LoadMoreTextConfig();
		config.value = ResMng.getInstance(at).getString(R.string.loading_more);
		config.size = PaintUtil.fontS_4;
		vRecycle.setLoadMoreTextConfig(config);

		vRecycle.setOverScrollMode(OVER_SCROLL_NEVER);

		if(refreshOffset > 0)
		{
			seatView = new View(at);
			seatView.setLayoutParams(new ViewGroup.LayoutParams(vw, refreshOffset));

			vRecycle.addHeadView(seatView);
			vRecycle.addOnScrollListener(new RecyclerView.OnScrollListener()
			{
				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy)
				{
					super.onScrolled(recyclerView, dx, dy);
					if(iScrollListener != null && recyclerView != null)
					{
						iScrollListener.onScrollChanged(refreshOffset - recyclerView.computeVerticalScrollOffset(), dy);
					}
				}

				@Override
				public void onScrollStateChanged(RecyclerView recyclerView, int newState)
				{
					super.onScrollStateChanged(recyclerView, newState);
					if(iScrollListener != null)
					{
						iScrollListener.onScrollStateChanged(newState);
					}
				}
			});
		}

		vRecycle.setOnItemListener(new IItem()
		{
			@Override
			public void onViewChanged(View view, int status)
			{
				FPullRecycleView.this.onViewChanged(view, status);
			}

			@Override
			public View onCreateItem()
			{
				return FPullRecycleView.this.onCreateItem();
			}
			@Override
			public void onInitItem(View view, int position, Object obj)
			{
				FPullRecycleView.this.onInitItem(view, position, obj);
			}

			@Override
			public void onScrollChanged(int newState)
			{
				FPullRecycleView.this.onScrollChanged(newState);
			}
		});
		vRecycle.setOnRecycleListener(new IRecycleView()
		{
			@Override
			public void onRecycleViewScrollChanged(final int newState)
			{
			}
			@Override
			public void onRecycleViewScrolled(int dx, int dy)
			{
			}
			@Override
			public void onLoadMore()
			{
				if(request == 0)
				{
					Logger.i("可以去加载更多");
					request = 2;
					state = LOADING;
					FPullRecycleView.this.onLoadMore();
				}else
				{
					Logger.i("不允许加载更多,当前状态: " + state);
				}
			}
		});

		addView(vRecycle);
	}
	public void setOnScrollListener(IScrollListener iScrollListener)
	{
		this.iScrollListener = iScrollListener;
	}
	public void loadMoreFinish(final int refreshResult)
	{
		request = 0;
		state = INIT;
		switch(refreshResult)
		{
			case SUCCEED:
				// 加载成功
				// changeState(SUCCEED);
				if(vRecycle != null)
				{
					vRecycle.loadMoreResult(true);
				}
				break;
			case FAIL:
			default:
				// 加载失败
				// changeState(FAIL);
				if(vRecycle != null)
				{
					vRecycle.loadMoreResult(false);
				}
				break;
		}
	}
	public void onRefresh()
	{
	}

	// recycler view begin
	public void onViewChanged(View view, int i)
	{
	}

	public View onCreateItem()
	{
		return null;
	}

	public void onInitItem(final View view, final int position, final Object obj)
	{
	}

	public void onLoadMore()
	{
	}

	public void onScrollChanged(int newState)
	{
	}
	protected void notifyAdapter()
	{
		if(vRecycle != null)
		{
			vRecycle.dataChanged();
		}
	}
}
