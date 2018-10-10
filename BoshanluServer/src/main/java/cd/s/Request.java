package cd.s;

import hf.http.FHttpRequest;
import hf.http.data.CacheMode;
import hf.http.data.ECode;
import hf.http.data.HeaderEntry;
import hf.http.ifs.INet;
import hf.http.ifs.IRequestAsync;
import hf.http.ifs.IRequestAsync.OnReqIOListener;
import hf.http.ifs.IRequestAsync.OnReqImgListener;
import hf.http.ifs.IRequestAsync.OnReqStringListener;
import hf.http.util.IO;
import hf.http.util.StrUtil;
import hf.key.cd.FKey;
import hz.dodo.FileUtil;
import hz.dodo.Logger;
import hz.dodo.SIMMng;
import hz.dodo.data.AESJava;
import hz.dodo.data.Empty;
import cd.s.Async.IAd;
import cd.s.Async.IArtListSearch;
import cd.s.Async.IArticle;
import cd.s.Async.IArticles;
import cd.s.Async.IBlock;
import cd.s.Async.IColumn;
import cd.s.Async.IDataObjs;
import cd.s.Async.IFeedback;
import cd.s.Async.IIO;
import cd.s.Async.IImage;
import cd.s.Async.INotify;
import cd.s.Async.IPhotoRecommand;
import cd.s.Async.IPostIntegral;
import cd.s.Async.IPostMyFavorites;
import cd.s.Async.IResultMsgIncludeUid;
import cd.s.Async.IRecommend;
import cd.s.Async.IString;
import cd.s.Async.ITranslate;
import cd.s.data.Article;
import cd.s.data.Block;
import cd.s.data.Column;
import cd.s.data.MsgNotification;
import cd.s.data.Order;
import cd.s.data.SearchHistory;
import cd.s.data.StockCoinsResult;
import cd.util.FThreadPoolUtil;
import cd.util.CopyFileUtil;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

// 只负责数据请求,不负责数据保存
class Request
{
	Context
		ctx;
	DataMng
		dm;
	AESJava
		aesJava;
	
	PathMng
		pathMng;

	FHttpRequest
		reqHttp;
	
	FThreadPoolUtil
		fThreadPoolUtil;
	
	SortData 
		sort;
	
	String
		key;
	CacheMng
		cacheManager;
	DataCacheMng 
		dataCacheMng;

	HashMap<String, String>
		hmExtraValue;

	public Request(final Context ctx, DataMng dm, AESJava aesJava)
	{
		this.ctx = ctx;
		this.dm = dm;
		this.aesJava = aesJava;
		pathMng = PathMng.getInstance(ctx);
		cacheManager = new CacheMng(ctx, aesJava);
		dataCacheMng = DataCacheMng.getInstance(ctx);
		sort = new SortData();
		hmExtraValue = new HashMap<>();
		
		reqHttp = FHttpRequest.getInstance(key = FKey.getAesKey(), new INet()
		{
			@Override
			public boolean isConnect() 
			{
				return isNet();
			}
		}, dm.getAppendUserAgent());
	}
	private FThreadPoolUtil getThread()
	{
		if(fThreadPoolUtil == null)
		{
			fThreadPoolUtil = FThreadPoolUtil.getInstance();
		}
		return fThreadPoolUtil;
	}
	public void onDestroy()
	{
		if(fThreadPoolUtil != null)
		{
			fThreadPoolUtil.ondestroy();
		}
	}
	// 网络是否连接
	private boolean isNet()
	{
		return SIMMng.isConnected(ctx);
	}
	private boolean checkNetWork(final Async.IBase iBase, final CacheMode mode)
	{
		if(!isNet() && 
				(mode == CacheMode.ONLY_REQUEST_NETWORK || 
				mode == CacheMode.REQUEST_NETWORK_BY_CACHE))
		{
			onCheckFail(iBase, ECode.ERR_CODE_NONETWORK, "网络未连接");
			return false;
		}
		
		return true;
	}
	public void onCheckFail(final Async.IBase base, final int errorCode, final String msg)
	{
		if(base != null)
		{
			base.onStart(null);
			base.onError(errorCode, msg, null);
		}
	}
	protected void reqBase(final String url, final Async.IBase iBase, final CacheMode mode)
	{
		reqBase(url, null, iBase, mode);
	}
	private void reqBase(final String url, final Object obj, final Async.IBase iBase, final CacheMode mode)
	{
		reqBase(url, obj, iBase, mode, true);
	}

	private void reqBase(final String url, final Object obj, final Async.IBase iBase, final CacheMode mode, final boolean needAnalysis)
	{
		try
		{
			getThread().executeHttpTask(new Runnable()
			{
				@Override
				public void run()
				{
					if(!checkNetWork(iBase, mode)) return;
					
					final String msg = obj != null ? obj.toString() : "";

					final String path = getFilePath(iBase, msg);
					
					reqHttp.reqString(url, path, new IRequestAsync.OnReqStringListener() 
					{
						@Override
						public void onStart(Object tag) 
						{
							if(iBase != null)
							{
								iBase.onStart(tag);
							}
						}
						
						@Override
						public void onResponse(String value, final boolean isCache, final Object tag)
						{
							handleResult(iBase, value, isCache, tag);
						}
						
						@Override
						public void onFailure(int code, String msg, Object tag)
						{
							if(iBase != null)
							{
								iBase.onError(code, msg, tag);
							}
						}
					}, mode);
				}
			});
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqBase error == " + exc.toString());
		}
	}
	
	protected void postBase(final String url, final Map<String, ?> map, final Async.IBase iBase)
	{
		postBase(url, null, map, CacheMode.ONLY_REQUEST_NETWORK, iBase);
	}
	
	protected void postBase(final String url, final String cachePath, final Map<String, ?> map, final CacheMode mode, final Async.IBase iBase)
	{
		try
		{
			getThread().executeHttpTask(new Runnable() 
			{
				@Override
				public void run()
				{
					reqHttp.postObject(url, cachePath, new IRequestAsync.OnReqStringListener() 
					{
						@Override
						public void onStart(Object tag) 
						{
							handlerStart(iBase, tag, map);
						}
						
						@Override
						public void onResponse(String value, final boolean isCache, final Object tag)
						{
							handleResult(iBase, value, isCache, tag);
						}
						
						@Override
						public void onFailure(int code, String msg, Object tag)
						{
							handlerError(iBase, code, msg, tag, map);
						}
					}, map, mode);
				}
			});
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("postBase error == " + exc.toString());
		}
	}
	
	protected void handlerError(final Async.IBase iBase, int code, String msg, Object tag, final Map<String, ?> hm)
	{
		if(!Empty.isEmpty(hm))
		{
			Object object = hm.get("startTime");
			String startTime = "";
			if(object instanceof String)
			{
				startTime = (String) object;
			}
			handlerError(iBase, code, msg, tag, startTime);
		}
		
	}
	
	private void handlerError(final Async.IBase iBase, int code, String msg, Object tag, String startTime)
	{
		if (iBase != null) 
		{
			if(iBase instanceof Async.INotify)
			{
				onNotificationFailure((Async.INotify)iBase, tag, code, msg, startTime);
			}
			else
			{
				iBase.onError(code, msg, tag);
			}
		}
	}
	
	protected void handlerStart(final Async.IBase iBase, final Object tag, final Map<String, ?> hm)
	{
		if(!Empty.isEmpty(hm))
		{
			Object object = hm.get("startTime");
			String startTime = "";
			if(object instanceof String)
			{
				startTime = (String) object;
			}
			handlerStart(iBase, tag, startTime);
		}
		
	}
	
	protected void handlerStart(final Async.IBase iBase, final Object tag, final String startTime)
	{
		if(iBase != null)
		{
			if(iBase instanceof Async.INotify)
			{
				if(!Empty.isEmpty(startTime) && tag != null)
				{
					if(hmExtraValue != null)
					{
						hmExtraValue.put(tag.toString(), startTime);
					}
				}
			}
			iBase.onStart(tag);
		}
	}
	
	protected void postBaseForm(final String url, final Map<String, ?> map, final Async.IBase iReq)
	{
		postBaseForm(url, null, CacheMode.ONLY_REQUEST_NETWORK, map, iReq);
	}
	protected void postBaseForm(final String url, final String absPath, final CacheMode mode, final Map<String, ?> map, final Async.IBase iReq)
	{
		try
		{
			getThread().executeHttpTask(new Runnable() 
			{
				@Override
				public void run()
				{
					reqHttp.postObjectForm(url, absPath, mode, map, new IRequestAsync.OnReqStringListener() 
					{
						@Override
						public void onStart(Object tag) 
						{
							if(iReq != null)
							{
								iReq.onStart(tag);
							}
						}
						
						@Override
						public void onResponse(String value, final boolean isCache, final Object tag)
						{
							handleResult(iReq, value, isCache, tag);
						}
						
						@Override
						public void onFailure(int code, String msg, Object tag)
						{
							if(iReq != null)
							{
								iReq.onError(code, msg, tag);
							}
						}
					});
				}
			});
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("postBaseForm error == " + exc.toString());
		}
	}
	public void reqString(final String url, final IString iReq)
	{
		try
		{
			getThread().executeHttpTask(new Runnable()
			{
				@Override
				public void run()
				{
					reqHttp.reqString(url, new OnReqStringListener()
					{
						@Override
						public void onStart(Object tag)
						{
							if(iReq != null)
							{
								iReq.onStart(tag);
							}
						}
						@Override
						public void onResponse(String value, boolean isCache, Object tag)
						{
							if(iReq != null)
							{
								iReq.onResponse(value, tag);
							}
						}
						@Override
						public void onFailure(int code, String msg, Object tag)
						{
							if(iReq != null)
							{
								iReq.onError(code, msg, tag);
							}
						}
					});
				}
			});
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqString()" + exc.toString());
		}
	}
	private void reqIO(final String url, final String path, final Async.IIO iBase)
	{
		try
		{
			getThread().executeHttpTask(new Runnable() 
			{
				@Override
				public void run()
				{
					reqHttp.reqIO(url, path, new OnReqIOListener()
					{
						@Override
						public void onStart(Object tag)
						{
							if(iBase != null)
							{
								iBase.onStart(tag);
							}
						}
						
						@Override
						public void onResponse(String path, boolean isCache, Object tag)
						{
							if(iBase != null)
							{
								iBase.onIO(url, path, tag);
							}
						}
						
						@Override
						public void onFailure(int code, String msg, Object tag)
						{
							if(iBase != null)
							{
								iBase.onError(code, msg, tag);
							}
						}
					}, CacheMode.NONE_CACHE_REQUEST_NETWORK);
				}
			});
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqIO error == " + exc.toString());
		}
	}
	
	// 图片本地是否已存在
	public String hasLocalImg(String url)
	{
		if(!Empty.isEmpty(url))
		{
			String path = pathMng.getImgDir() + StrUtil.getMD5(url);
			return FileUtil.isExists(path) != null ? path : null;
		}
		return null;
	}
	
	public String hasLocalHeadImg(String url)
	{
		if(!Empty.isEmpty(url))
		{
			String sUserId = dm.getUId();
			if(!Empty.isEmpty(sUserId))
			{
				String path = pathMng.getAccountDir() + sUserId + PathMng.SEPARATOR + pathMng.getAccountImgDir() + StrUtil.getMD5(url);
				return FileUtil.isExists(path) != null ? path : null;
			}
		}
		return null;
	}
	
	public void reqImg(final String url, final IImage iBase)
	{
		final String path = pathMng.getImgDir() + StrUtil.getMD5(url);
		
		reqImg(url, path, iBase);
	}
	
	public void reqImg(final String url, final String path, final IImage iBase)
	{
		getThread().executeHttpTask(new Runnable() 
		{
			@Override
			public void run() 
			{
				reqHttp.reqImg(url, path, new OnReqImgListener()
				{
					@Override
					public void onStart(Object tag) 
					{
						if(iBase != null)
						{
							iBase.onStart(tag);
						}
					}
					
					@Override
					public void onResponse(String path, boolean isCache, Object tag) 
					{
						if(iBase != null)
						{
							iBase.onImg(url, path, tag);
						}
					}
					
					@Override
					public void onFailure(int code, String msg, Object tag)
					{
						if(iBase != null)
						{
							iBase.onError(code, msg, tag);
						}
					}
				}, CacheMode.NONE_CACHE_REQUEST_NETWORK);
			}
		});
	}
	
	private String getFilePath(final Async.IBase iBase, String obj)
	{
		if(iBase instanceof IArticle)
		{
			return pathMng.getArticleDir() + obj;
		}
		else if(iBase instanceof IArticles || iBase instanceof IDataObjs)
		{
			return pathMng.getArticleListDir() + obj;
		}
		else if(iBase instanceof IColumn)
		{
			String sDefaultColumns = "defaultColumns";
			
			String uid = dm.getUId();
			if(!Empty.isEmpty(uid))
			{
				String path = pathMng.getAccountColumnDir(uid);
				File file;
				if(!Empty.isEmpty(path) && (file = FileUtil.isExists(path)) == null)
				{
					if(CopyFileUtil.copyFolderFile(pathMng.getAccountDir(sDefaultColumns), pathMng.getAccountDir(uid)))
					{
						if((file = FileUtil.isExists(path)) != null)
						{
							File filedefault;
							if((filedefault = FileUtil.isExists(pathMng.getAccountColumnDir(sDefaultColumns))) != null)
							{
								file.setLastModified(filedefault.lastModified());
							}
						}
					}
				}
			}
			
			return pathMng.getAccountColumnDir(!Empty.isEmpty(uid) ? uid : sDefaultColumns);
		}
		else if(iBase instanceof IBlock)
		{
			return pathMng.getArticleListDir() + obj;
		}
		else if(iBase instanceof IAd)
		{
			return pathMng.getAdFile();
		}

		return "";
	}
	public boolean checkUserId(final Async.IBase base)
	{
		if(Empty.isEmpty(dm.getUId()))
		{
			onCheckFail(base, ECode.ERR_CODE_PARAMS, "userid为空");
			return false;
		}
		
		return true;
	}
	protected void handleResult(final Async.IBase iBase, final String value, final boolean cache, final Object tag)
	{
		if(iBase instanceof Async.IArticle)
		{
			onArticle(value, (Async.IArticle)iBase, cache, tag);
		}
		else if(iBase instanceof Async.IAppInfo)
		{
			onAppInfo(value, (Async.IAppInfo)iBase, tag);
		}
		else if(iBase instanceof Async.IResultMsg)
		{
			onResultMsg(value, (Async.IResultMsg)iBase, tag);
		}
		else if(iBase instanceof Async.ICommentList)
		{
			onCommentList(value, (Async.ICommentList)iBase, tag);
		}
		else if(iBase instanceof Async.ICommentDelete)
		{
			onCommentDelete(value, (Async.ICommentDelete)iBase, tag);
		}
		else if(iBase instanceof Async.ICommentLike)
		{
			onCommentLike(value, (Async.ICommentLike)iBase, tag);
		}
		else if(iBase instanceof Async.IAd)
		{
			onAd(value, (Async.IAd)iBase, cache, tag);
		}
		else if(iBase instanceof Async.IArticleInfo)
		{
			onArticleInfo(value, (Async.IArticleInfo)iBase, tag);
		}
		else if(iBase instanceof Async.ITranslate)
		{
			onTranslate(value, (Async.ITranslate)iBase, tag);
		}
		else if(iBase instanceof Async.IServiceTime)
		{
			onServiceTime(value, (Async.IServiceTime)iBase, tag);
		}
		else if(iBase instanceof Async.IPostMyFavorites)
		{
			onPostMyFavorites(value, (Async.IPostMyFavorites)iBase, tag);
		}
		else if(iBase instanceof IColumn)
		{
			onColumn(value, (Async.IColumn) iBase, cache, tag);
		}
		else if(iBase instanceof Async.IBlock)
		{
			onBlock(value, (Async.IBlock) iBase, cache, tag);
		}
		else if(iBase instanceof Async.IArticles)
		{
			onArticles(value, (Async.IArticles) iBase, cache, tag);
		}
		else if(iBase instanceof Async.IArticlesMore)
		{
			onArticlesMore(value, (Async.IArticlesMore) iBase, cache, tag);
		}
		else if(iBase instanceof Async.IResultMsgCode)
		{
			onCode(value, (Async.IResultMsgCode) iBase, cache, tag);
		}
		else if(iBase instanceof Async.IUserInfo)
		{
			onUserInfo(value, (Async.IUserInfo) iBase, cache, tag);
		}
		else if(iBase instanceof Async.IBehavior)
		{
			onBehavior(value, (Async.IBehavior) iBase, cache, tag);
		}
		else if(iBase instanceof Async.IAddress)
		{
			onAddress(value, (Async.IAddress) iBase, cache, tag);
		}
		else if(iBase instanceof Async.IPostCancelFavorites)
		{
			onPostCancelFavorites(value, (Async.IPostCancelFavorites) iBase, tag);
		}
		else if(iBase instanceof Async.IFeedback)
		{
			onFeedback(value, (Async.IFeedback) iBase, tag);
		}
		else if(iBase instanceof Async.IArtListSearch)
		{
			onSearch(value, (Async.IArtListSearch) iBase, tag);
		}
		else if(iBase instanceof Async.IAvatarUrl)
		{
			onAvatarUrl(value, (Async.IAvatarUrl) iBase, false, tag);
		}
		else if(iBase instanceof Async.IRecommend)
		{
			onRelatedArtList(value, (Async.IRecommend) iBase, cache, tag);
		}
		else if(iBase instanceof Async.IResultMsgIncludeUid)
		{
			onResultMsgIncludeUid(value, (Async.IResultMsgIncludeUid)iBase, tag);
		}
		else if(iBase instanceof Async.IPhotoRecommand)
		{
			onPhotoRecommand(value, (Async.IPhotoRecommand)iBase, cache, tag);
		}
		else if(iBase instanceof Async.IDataObjs)
		{
			onDataObjs(value, (Async.IDataObjs)iBase, cache, tag);
		}
		else if(iBase instanceof IPostIntegral)
		{
			onIntegralResult(value, (Async.IPostIntegral)iBase, tag);
		}		
		else if(iBase instanceof Async.INotify)
		{
			onNotification(value, (Async.INotify)iBase, cache, tag);
		}	
		else if(iBase instanceof Async.IOrder)
		{
			onOrder(value, (Async.IOrder)iBase, tag);
		}
		else if(iBase instanceof Async.ICheckStockCoins)
		{
			onCheckStockCoins(value, (Async.ICheckStockCoins)iBase, tag);
		}	
	}
	
	private void onIntegralResult(String value, IPostIntegral iIntegral, Object tag) 
	{
		if(iIntegral != null)
		{
			iIntegral.onPostIntegral(JsonParser.getIntegralResult(value), tag);
		}
	}
	
	public void postCheckStockCoins(String url, final Map<String, Object> hm, final Async.ICheckStockCoins iBase)
	{
		postBase(url, hm, iBase);
	}
	
	//TODO 
	private void onCheckStockCoins(String value, Async.ICheckStockCoins iReq, Object tag)
	{
		if (iReq != null)
		{
			//if(checkUserId(iReq))
			{
				StockCoinsResult stockCoinsResult = JsonParser.getStockCoinsResult(value);
				iReq.onCheckStockCoins(stockCoinsResult, tag);
			}
		}
	}
	
	public void postOrder(String url, final Map<String, Object> hm, final Async.IOrder iBase)
	{
		postBase(url, hm, iBase);
	}
	
	private void onOrder(String value, Async.IOrder iReq, Object tag)
	{
		if (iReq != null)
		{
			//if(checkUserId(iReq))
			{
				//MsgNotification notification = JsonParser.getNotification(value);
				Order order = JsonParser.getOrderResult(value);
				iReq.onOrder(order, tag);
			}
		}
	}
	
	
	//消息提示
	public void postNotify(String url, final Map<String, Object> hm, final Async.INotify iBase)
	{
		postBase(url, hm, iBase);
	}
	
	private void onNotification(String value, Async.INotify iReq, final boolean cache, Object tag)
	{
		if (iReq != null)
		{
			if(checkUserId(iReq))
			{
				String sUserId = dm.getUId();
				
				dataCacheMng.saveNotificationJson(value, sUserId);
				
				MsgNotification notification = JsonParser.getNotification(value);
				
				if(notification != null)
				{
					iReq.onNotification(notification, cache, tag);
					
					dataCacheMng.saveResponseTime(sUserId, notification);
					dataCacheMng.saveFileModifyTime(sUserId);
				}
				else
				{
					String responseTime = IO.read(pathMng.getNotifiyResponseTimeDir(sUserId));
					onNotificationFailure(iReq, tag, cd.s.data.ECode.E_INPUTSTREAM, "没有网络数据", responseTime);
				}
			}
		}
	}
	
	private void onNotificationFailure(final INotify iReq, final Object tag, final int code, final String msg, final String responseTime)
	{
		if(!MsgNotification.START_TIME.equals(responseTime))
		{
			MsgNotification notifiyLocalData = getNotifiyLocalData(dm.getAccountId());
			if(notifiyLocalData != null)
			{
				iReq.onNotification(notifiyLocalData, true, tag);
			}
			else
			{
				iReq.onError(code, msg, tag);
			}
		}
		else
		{
			iReq.onError(code, msg, tag);
		}
	}
	
	//获取本地消息数据
	private MsgNotification getNotifiyLocalData(final String uid)
	{
		if(!Empty.isEmpty(uid))
		{
			String inJson = IO.read(pathMng.getMsgNotifiyJsonDir(uid));
			return JsonParser.getNotification(inJson);
		}
	    return null;
	}	
	private void onDataObjs(String value, IDataObjs iDataObjs, boolean cache, Object tag) 
	{
		if(iDataObjs != null)
		{
			iDataObjs.onDataObjs(JsonParser.getDataObjs(value), cache, tag);
		}
	}
	private void onPhotoRecommand(final String value, final IPhotoRecommand iPhotoRecommand, final boolean cache, final Object tag)
	{
		if(iPhotoRecommand != null)
		{
			iPhotoRecommand.onPhotoRecommand(JsonParser.getPhotoRecommand(value), cache, tag);
		}
	}
	
	private void onSearch(String value, IArtListSearch iArtList, Object tag)
	{
		if(iArtList != null)
		{
			iArtList.onSearchList(JsonParser.getArticleSearch(value), tag);
		}
	}
	
	private void onResultMsgIncludeUid(String value, IResultMsgIncludeUid iBase, Object tag)
	{
		if(iBase != null)
		{
			iBase.onResultMsg(JsonParser.getResultMsgIncludeUid(value, dm.getAccountId()), tag);
		}
	}
	
	private void onFeedback(String value, Async.IFeedback iBase, Object tag)
	{
		if(iBase != null)
		{
			iBase.onPostFeedback(JsonParser.getFeedbackResultMsg(value), tag);
		}
	}
	
	private void onPostCancelFavorites(String value, Async.IPostCancelFavorites iBase, Object tag)
	{
		if(iBase != null)
		{
			iBase.onPostCancelFavorites(JsonParser.getFavoriteDeletes(value, dm.getAccountId()), tag);
		}
	}
	
	private void onPostMyFavorites(String value, Async.IPostMyFavorites iBase, Object tag)
	{
		if(iBase != null)
		{
			iBase.onPostMyFavorites(JsonParser.getArtFavorites(value), false, tag);
		}
	}
	
	private void onArticle(final String value, final Async.IArticle iBase, final boolean cache, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onArticle(JsonParser.getArticle(value), cache, tag);
		}
	}
	
	private void onAppInfo(final String value, final Async.IAppInfo iBase, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onAppInfo(JsonParser.getAppInfo(value), tag);
		}
	}
	
	private void onArticleInfo(final String value, final Async.IArticleInfo iBase, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onArticleInfo(JsonParser.getArticleInfo(value), tag);
		}
	}
	
	private void onRelatedArtList(final String value, final Async.IRecommend iBase, final boolean isCache, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onIRecommend(JsonParser.getRelatedArtList(value), isCache, tag);
		}
	}
	
	private void onTranslate(final String value, final Async.ITranslate iBase, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onTranslate(JsonParser.getTranslate(value), tag);
		}
	}
	
	private void onServiceTime(final String value, final Async.IServiceTime iBase, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onServiceTime(JsonParser.getServiceTime(value), tag);
		}
	}
	
	private void onResultMsg(final String value, final Async.IResultMsg iBase, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onResultMsg(JsonParser.getResultMsg(value), tag);
		}
	}
	
	private void onCommentList(final String value, final Async.ICommentList iBase, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onCommentList(JsonParser.getComment(value), tag);
		}
	}
	
	private void onCommentDelete(final String value, final Async.ICommentDelete iBase, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onCommentDelete(JsonParser.getCommentDelete(value), tag);
		}
	}
	
	private void onCommentLike(final String value, final Async.ICommentLike iBase, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onCommentLike(JsonParser.getCommentLike(value), tag);
		}
	}
	
	private void onAd(final String value, final Async.IAd iBase, final boolean isCache, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onAd(JsonParser.getAdInfo(value), isCache, tag);
		}
	}
	
	public void reqAd(final String url, final Async.IAd iBase, final CacheMode mode)
	{
		reqBase(url, iBase, mode);
	}
	
	public void reqAdIO(final String url, final IIO iBase, final CacheMode mode)
	{
		String path = pathMng.getAdDataFile() + StrUtil.getMD5(url);
		
		reqIO(url, path, iBase);
	}

	public void reqHeadImg(final String url, final IImage iBase)
	{
		String path = pathMng.getAccountDir() + dm.getUId() + PathMng.SEPARATOR + pathMng.getImgDir() + StrUtil.getMD5(url);

		reqImg(url, path, iBase);
	}
	
	public void reqTranslate(final String url, final ITranslate iBase, final CacheMode mode)
	{
		reqBase(url, iBase, mode);
	}
	
	public void reqArticle(final String url, final Object obj, final Async.IArticle iBase, final CacheMode mode)
	{
		reqBase(url, obj, iBase, mode);
	}
	
	public void reqAppInfo(final String url, final Async.IAppInfo iBase, final CacheMode mode)
	{
		reqBase(url, iBase, mode);
	}
	
	//TODO 
	public void reqCountryRegion(final String url, final Async.IAppInfo iBase, final CacheMode mode)
	{
		reqBase(url, iBase, mode);
	}
	
	public void postComment(final String url, final Async.ICommentList iBase, final Map<String, Object> map)
	{
		postBase(url, map, iBase);
	}
	
	public void postPhotoRecommand(final String url, final Async.IPhotoRecommand iBase, final Map<String, Object> map, final String fileName)
	{
		String path = pathMng.getAccountPhotoRecommendDir(dm.getAccountId());
		if(!Empty.isEmpty(path))
		{
			path += StrUtil.getMD5(url + fileName);
		}
		
		postBase(url, path, map, CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE, iBase);
	}
	
	public void postCommentList(final String url, final Async.ICommentList iBase, final Map<String, Object> map)
	{
		postBase(url, map, iBase);
	}
	
	public void postServerTime(final String url, final Async.IServiceTime iBase, final Map<String, Object> map)
	{
		postBase(url, map, iBase);
	}
	
	public void reqRelated(final String url, final String id, final IRecommend iBase, final HashMap<String, Object> map)
	{
		String path = pathMng.getArticleRelatedDir();
		if(!Empty.isEmpty(path))
		{
			path += StrUtil.getMD5(url + id);
		}
		postBaseForm(url, path, CacheMode.CACHE_THEN_REQUEST_NETWORK, map, iBase);
	}
	
	public void postDeleteComment(final String url, final Async.ICommentDelete iBase, final Map<String, Object> map)
	{
		postBase(url, map, iBase);
	}
	
	public void postLikeComment(final String url, final Async.ICommentLike iBase, final Map<String, Object> map)
	{
		postBase(url, map, iBase);
	}
	
	public void postArticleInfo(final String url, final Async.IArticleInfo iBase, final Map<String, Object> map)
	{
		postBase(url, map, iBase);
	}
	
	public void postFavorite(final String url, final HashMap<String, Object> hm, final Async.IResultMsgIncludeUid iReq) 
	{
		postBase(url, hm, iReq);
	}
	
	public void postLike(final String url, final HashMap<String, Object> hm, final Async.IResultMsgIncludeUid iReq)
	{
		postBase(url, hm, iReq);
	}
	
	public void postCancelFavorite(final String url, final HashMap<String, Object> hm, final Async.IPostCancelFavorites iReq)
	{
		postBase(url, hm, iReq);
	}
	
	public void postMyFavorites(final String url, final HashMap<String, Object> hm, final IPostMyFavorites iBase) 
	{
		postBase(url, hm, iBase);
	}
	
	public void postIntegral(final String url, final HashMap<String, Object> hm, final IPostIntegral iBase)
	{
		postBase(url, hm, iBase);
	}
	
	public void reqForYou(final String url, final Async.IRecommend iBase, final Map<String, Object> map, final CacheMode mode)
	{
		String sDefaultForYou = "defaultForYou";
		
		String uid = dm.getUId();
		if(!Empty.isEmpty(uid))
		{
			String path = pathMng.getForYouFileDir(uid);
			File file;
			if(!Empty.isEmpty(path) && (file = FileUtil.isExists(path)) == null)
			{
				if(CopyFileUtil.copyFolderFile(pathMng.getAccountDir(sDefaultForYou), pathMng.getAccountDir(uid)))
				{
					if((file = FileUtil.isExists(path)) != null)
					{
						File filedefault;
						if((filedefault = FileUtil.isExists(pathMng.getForYouFileDir(sDefaultForYou))) != null)
						{
							file.setLastModified(filedefault.lastModified());
						}
					}
				}
			}
		}
		
		postBaseForm(url, pathMng.getForYouFileDir(!Empty.isEmpty(uid) ? uid : sDefaultForYou), mode, map, iBase);
	}
	
	public void postFeedback(final String url, final HashMap<String, Object> hm, final IFeedback iBase)
	{
		postBase(url, hm, iBase);
	}
	
	
	public void requestColumn(final String url, Async.IColumn iColumn, final CacheMode mode)
	{
		reqBase(url, iColumn, mode);
	}
	
	public void requestHomeArts(final String url, final Object object, Async.IBlock iBlock, final CacheMode mode)
	{
		reqBase(url, object, iBlock, mode);
	}

	public void requestArts(final String url, final Object object, Async.IArticles iArticles, final CacheMode mode)
	{
		reqBase(url, object, iArticles, mode);
	}
	
	public void requestMoreArts(final String url, final Object object, Async.IArticlesMore iArticles, final CacheMode mode)
	{
		reqBase(url, object, iArticles, mode);
	}
	
	public void reqSearch(final String url, IArtListSearch iArtList, final CacheMode mode)
	{
		reqBase(url, iArtList, mode);
	}
	

	private void onColumn(String value, Async.IColumn iBase, boolean cache, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onColumn(handlerColumnList(value), cache, tag);
		}
	}

	private List<Column> handlerColumnList(String value) 
	{
		if(!Empty.isEmpty(value))
		{
			List<Column> columnsList = JsonParser.getColumns(value);
			if(!Empty.isEmpty(columnsList))
			{
				SortData.SortColumnsPosition asp = sort.new SortColumnsPosition();
				Collections.sort(columnsList, asp);
				asp = null;
				
			}
			return columnsList;
		}
		return null;
		
	}
	//请求首页文章列表
	public void onBlock(String value, Async.IBlock iBase, boolean cache, final Object tag)
	{
		if(iBase != null)
		{
			//TODO 排序
			iBase.onBlock(handlerBlocksList(value), cache, tag);
		}
	}

	public void onArticles(String value, Async.IArticles iBase, boolean cache, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onArticles(JsonParser.getArticles(value), cache, tag);
		}
	}

	public void onArticlesMore(String value, Async.IArticlesMore iBase, boolean cache, final Object tag)
	{
		if(iBase != null)
		{
			iBase.onArticlesMore(JsonParser.getArticlesMore(value), cache, tag);
		}
	}
	
	private List<Block> handlerBlocksList(String value)
	{
		if(!Empty.isEmpty(value))
		{
			List<Block> blockArticles = JsonParser.getBlockArticles(value);
			if(!Empty.isEmpty(blockArticles))
			{
				SortData.SortArticlePosition asp = sort.new SortArticlePosition();
				Collections.sort(blockArticles, asp);
				asp = null;
				
			}
			return blockArticles;
		}
		return null;
	}
	
	public void setLanguage(final String language)
	{
		if(pathMng != null)
		{
			pathMng.setLanguage(language);
		}
	}
	protected void onCode(String value, Async.IResultMsgCode iReq, boolean cache, Object tag)
	{
		// requestUCenter 实现
	}
	protected void onUserInfo(String value, Async.IUserInfo iReq, boolean cache, Object tag)
	{
		// requestUCenter 实现
	}
	protected void onBehavior(String value, Async.IBehavior iReq, boolean cache, Object tag)
	{
		// requestUCenter 实现
	}
	protected void onAddress(String value, Async.IAddress iReq, boolean cache, Object tag)
	{
		// requestUCenter 实现
	}
	protected void onAvatarUrl(String value, Async.IAvatarUrl iReq, boolean cache, Object tag)
	{
		// requestUCenter 实现
	}
	
	public List<SearchHistory> getSearchHistory(final String path)
	{
		return cacheManager.getSearchHistory(path);
	}
	
	public boolean saveSearchHistory(final String key, final String path)
	{
		return cacheManager.saveSearchHistory(key, path);
	}
	
	public boolean removeSearchHistory(final SearchHistory searchHistory, final String path)
	{
		return cacheManager.removeSearchHistory(searchHistory, path);
	}
	
	public boolean removeAllSearchHistory(final String path)
	{
		return cacheManager.removeAllSearchHistory(path);
	}
	
	public Article getLocalArticle(final String name)
	{
		if(!Empty.isEmpty(name))
		{
			String path = pathMng.getArticleDir() + name;
			HeaderEntry entry = new HeaderEntry(path);
			return JsonParser.getArticle(IO.read(path, entry.getCharset(), key));
		}
		
		return null;
	}
	
	public void requestDataObjs(String url, Object obj, IDataObjs iDataObj, CacheMode mode)
	{
		reqBase(url, obj, iDataObj, mode);
	}
}
