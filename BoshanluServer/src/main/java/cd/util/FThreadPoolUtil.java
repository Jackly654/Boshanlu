package cd.util;

import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import hz.dodo.data.Empty;

public class FThreadPoolUtil
{
	private
	static
	FThreadPoolUtil
			mThis;
	
	private
	ExecutorService
		executorServiceHttp,
		executorServiceLogic,
		executorServiceSingleSync;
	
	private
	Hashtable<String, Config>
		hashExecutorService;

	public static FThreadPoolUtil getInstance()
	{
		if(mThis == null)
		{
			synchronized(FThreadPoolUtil.class)
			{
				if(mThis == null)
				{
					mThis = new FThreadPoolUtil();
				}
			}
		}

		return mThis;
	}
	
	public void executeHttpTask(final Runnable runnable)
	{
		if(runnable != null)
		{
			if(executorServiceHttp == null)
			{
				executorServiceHttp = Executors.newFixedThreadPool(4);
			}
			
			if (!executorServiceHttp.isShutdown()) 
			{
				executorServiceHttp.execute(runnable);
			}
		}
	}
	
	private void shutdownHttpTask()
	{
		if(executorServiceHttp != null)
		{
			executorServiceHttp.shutdownNow();
			executorServiceHttp = null;
		}
	}
	
	public void executeLogicTask(final Runnable runnable)
	{
		if(runnable != null)
		{
			if(executorServiceLogic == null)
			{
				executorServiceLogic = Executors.newFixedThreadPool(2);
			}
			
			if (!executorServiceLogic.isShutdown()) 
			{
				executorServiceLogic.execute(runnable);
			}
		}
	}
	
	private void shutdownLogicTask()
	{
		if(executorServiceLogic != null)
		{
			executorServiceLogic.shutdownNow();
			executorServiceLogic = null;
		}
	}
	
	public void executeSingleSyncTask(final Runnable runnable)
	{
		if(runnable != null)
		{
			if(executorServiceSingleSync == null)
			{
				executorServiceSingleSync = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			}
			
			if (!executorServiceSingleSync.isShutdown()) 
			{
				executorServiceSingleSync.execute(runnable);
			}
		}
	}
	
	private void shutdownSingleSyncTask()
	{
		if(executorServiceSingleSync != null)
		{
			executorServiceSingleSync.shutdownNow();
			executorServiceSingleSync = null;
		}
	}
	
	/**
	 * @param taskName 需要定时的方法名
	 * @param initialDelay 首次启动的时间
	 * @param period 间隔时间
	 * @param unit 单位
	 * @param runnable
	 */
	public void executeScheduledTask(final String taskName, final long initialDelay, final long period, final TimeUnit unit, final Runnable runnable)
	{
		if(runnable != null)
		{
			if(!Empty.isEmpty(taskName))
			{
				Config config = null;
				
				if(hashExecutorService == null)
				{
					hashExecutorService = new Hashtable<>();
				}
				else
				{
					config = hashExecutorService.get(taskName);
				}
				
				boolean needDoSomeThing = true;
				
				if(config != null)
				{
					if(config.scheduledExecutorService == null || config.initialDelay != initialDelay || config.period != period || config.unit != unit)
					{
						shutdownScheduledTask(taskName);
						
						config = null;
					}
					else if(config.runnable == runnable)
					{
						needDoSomeThing = false;
					}
				}
				
				if(needDoSomeThing)
				{
					if(config == null)
					{
						config = new Config();
						config.initialDelay = initialDelay;
						config.period = period;
						config.unit = unit;
						config.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
						config.runnable = runnable;
						
						hashExecutorService.put(taskName, config);
					}
					
					if(config.scheduledExecutorService != null && !config.scheduledExecutorService.isShutdown())
					{
						config.scheduledExecutorService.scheduleAtFixedRate(runnable, initialDelay, period, unit);
					}
				}
			}
		}
	}
	
	public void shutdownScheduledTask(final String taskName)
	{
		if(!Empty.isEmpty(hashExecutorService))
		{
			Config config = hashExecutorService.remove(taskName);
			if(config != null && config.scheduledExecutorService != null)
			{
				config.scheduledExecutorService.shutdownNow();
			}
		}
	}
	
	public void shutdownAllScheduledTask()
	{
		if(!Empty.isEmpty(hashExecutorService))
		{
			for(Config config : hashExecutorService.values())
			{
				if(config != null)
				{
					config.scheduledExecutorService.shutdownNow();
				}
			}
			
			hashExecutorService.clear();
		}
	}
	
	public void ondestroy()
	{
		shutdownHttpTask();
		shutdownLogicTask();
		shutdownSingleSyncTask();
		shutdownAllScheduledTask();
	}
	
	class Config
	{
		private
		ScheduledExecutorService
			scheduledExecutorService;
		
		private
		Runnable
			runnable;
		
		private
		long 
			initialDelay, 
			period;
		
		private
		TimeUnit 
			unit;
	}
}
