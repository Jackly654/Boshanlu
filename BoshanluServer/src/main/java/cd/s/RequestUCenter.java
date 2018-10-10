package cd.s;

import java.util.HashMap;

import cd.s.Async.IAddress;
import cd.s.Async.IBase;
import cd.s.Async.IBehavior;
import cd.s.Async.IUserInfo;
import cd.s.data.Address;
import cd.s.data.ResultMsgCode;
import cd.s.data.User;

import android.content.Context;
import hf.http.data.CacheMode;
import hf.http.util.Empty;
import hf.http.util.FileUtil;
import hz.dodo.Logger;
import hz.dodo.data.AESJava;

public class RequestUCenter extends Request
{
	public RequestUCenter(final Context ctx, DataMng dm, AESJava aesJava)
	{
		super(ctx, dm, aesJava);
	}
	// GET
	public void reqResultCode(String url, Async.IResultMsgCode iReq)
	{
		reqBase(url, iReq, CacheMode.ONLY_REQUEST_NETWORK);
	}
	public void reqUserInfo(String url, IUserInfo iReq)
	{
		reqBase(url, iReq, CacheMode.ONLY_REQUEST_NETWORK);
	}
	public void reqLogin(String url, IUserInfo iReq)
	{
		reqBase(url, iReq, CacheMode.ONLY_REQUEST_NETWORK);
	}
	public void reqBehavior(String url, IBehavior iReq)
	{
		reqBase(url, iReq, CacheMode.ONLY_REQUEST_NETWORK);
	}
	public void reqAddress(String url, IAddress iReq)
	{
		String uId = dm.getAccountId();
		CacheMng cache = new CacheMng(ctx, aesJava);
		String path = cache.getAddressFilePath(uId);

		boolean isReq = true;
		if(!Empty.isEmpty(path))
		{
			long lCur = System.currentTimeMillis();
			long lastModifyTimer = FileUtil.fileLastModify(path);
			
			if(Math.abs(lastModifyTimer - lCur) < 3600000) // 1小时
			{
				isReq = false;
			}
		}
		
		if(isReq)
		{
			reqBase(url, iReq, CacheMode.ONLY_REQUEST_NETWORK);
		}
	}
	// 获取本地已保存地址信息
	public Address getLocalAddress()
	{
		String uId = dm.getAccountId();
		CacheMng cache = new CacheMng(ctx, aesJava);
		
		String path = cache.getAddressFilePath(uId);
		if(!Empty.isEmpty(path))
		{
			String value = cache.readAddress(uId);
			if(!Empty.isEmpty(value))
			{
				return JsonParserUserCenter.getAddress(value);
			}
		}
		return null;
	}
	// POST
	public void postResultCode(String url, HashMap<String, ?> hm, Async.IResultMsgCode iReq)
	{
		postBase(url, hm, iReq);
	}
	
	public void postUserInfo(String url, HashMap<String, ?> hm, IUserInfo iReq)
	{
		postBase(url, hm, iReq);
	}
	public void postLogin(String url, HashMap<String, ?> hm, IUserInfo iReq)
	{
		postBase(url, hm, iReq);
	}
	public void postBehavior(String url, HashMap<String, ?> hm, IBehavior iReq)
	{
		postBase(url, hm, iReq);
	}
	public void postBase(String url, HashMap<String, ?> hm, IBase iReq)
	{
//		super.postBase(url, hm, iReq);
		postBaseForm(url, hm, iReq);
	}
	@Override
	protected void onCode(String value, Async.IResultMsgCode iReq, boolean cache, Object tag)
	{
		if(iReq != null)
		{
			iReq.onResultMsg(JsonParserUserCenter.getResultMsg(value), tag);
		}
	}
	@Override
	protected void onUserInfo(String value, Async.IUserInfo iReq, boolean cache, Object tag)
	{
		if(iReq != null)
		{
			User user = JsonParserUserCenter.getUser(value);
			ResultMsgCode rm = JsonParserUserCenter.getResultMsg(value);
			if(iReq.isHold())
			{
				Logger.i("需要保存用户信息 " + (rm != null ? ("status:" + rm.iStatus + ",code:" + rm.iMsgcode) : "rm == null"));
				if(rm.iStatus == 1 || rm.iMsgcode == 30) // 30 第三方帐号已绑定,会下发用户信息
				{
					Logger.i("服务器正常返回用户信息,可以保存");
					if(user != null)
					{
						Logger.i("Call writeAccount " + user.sId);
						new CacheMng(ctx, aesJava).writeAccount(user.sId, value);
					}
				}
				else
				{
					Logger.i("服务器未返回用户信息");
				}
			}
			else
			{
				Logger.i("不必保存用户信息");
			}
			iReq.onUserInfo(rm, user, tag);
		}
	}
	@Override
	protected void onBehavior(String value, Async.IBehavior iReq, boolean cache, Object tag)
	{
		if(iReq != null)
		{
			iReq.onBehavior(JsonParserUserCenter.getBehavior(value), tag);
		}
	}
	@Override
	protected void onAddress(String value, IAddress iReq, boolean cache, Object tag)
	{
		if(iReq != null)
		{
			iReq.onAddress(JsonParserUserCenter.getAddress(value), tag);
		}
		
		if(!cache)
		{
			new CacheMng(ctx, aesJava).writeAddress(dm.getAccountId(), value);
		}
	}
	protected void onAvatarUrl(String value, Async.IAvatarUrl iReq, boolean cache, Object tag)
	{
		if(iReq != null)
		{
			User user = JsonParserUserCenter.getAvatarUrl(value);
			iReq.onAvatarUrl(user != null ? user.sId : "", user != null ? user.sAvatarUrl : "", tag);
		}
	}
}
