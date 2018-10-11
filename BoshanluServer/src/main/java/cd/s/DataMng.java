package cd.s;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cd.s.Async.IAd;
import cd.s.Async.IAddress;
import cd.s.Async.IArticle;
import cd.s.Async.IArticleInfo;
import cd.s.Async.IAvatarUrl;
import cd.s.Async.IBehavior;
import cd.s.Async.ICommentDelete;
import cd.s.Async.ICommentLike;
import cd.s.Async.ICommentList;
import cd.s.Async.IIO;
import cd.s.Async.IImage;
import cd.s.Async.IPostIntegral;
import cd.s.Async.IRecommend;
import cd.s.Async.IResultMsg;
import cd.s.Async.IResultMsgCode;
import cd.s.Async.IServiceTime;
import cd.s.Async.IString;
import cd.s.Async.ISyncIntegral;
import cd.s.Async.ITranslate;
import cd.s.Async.IUserInfo;
import cd.s.Integral.IntegralMng;
import cd.s.data.Ad;
import cd.s.data.Ad.AdItem;
import cd.s.data.AdInfo;
import cd.s.data.Address;
import cd.s.data.AddressInfo;
import cd.s.data.AppInfo;
import cd.s.data.Article;
import cd.s.data.DR;
import cd.s.data.ECode;
import cd.s.data.JniData;
import cd.s.data.SearchHistory;
import cd.s.data.User;
import cd.s.ucenter.LoginUser;
import cd.s.ucenter.UserMng;
import cd.util.AESMsgOrder;
import hf.http.data.CacheMode;
import hf.http.util.IO;
import hf.http.util.StrUtil;
import hf.key.cd.FKey;
import hz.dodo.FileUtil;
import hz.dodo.Logger;
import hz.dodo.PkgMng;
import hz.dodo.data.AESJava;
import hz.dodo.data.Empty;

public class DataMng
{
	//发布地址tid
	public static final String POST_TID = "4289";
	//论坛基地址2个地址 第一个校园网才能访问，第二个都可以
	public static final String BASE_URL = "https://boshanlu.com/";
	/**
	 * config
	 * todo 把一些常量移到这儿来
	 */

	//记录上次未读消息的id
	public static final String MY_SHP_NAME = "ruisi_shp";
	public static final String NOTICE_MESSAGE_REPLY_KEY = "message_notice_reply";
	public static final String NOTICE_MESSAGE_AT_KEY = "message_notice_at";
	public static final String THEME_KEY = "my_theme_key";
	public static final String AUTO_DARK_MODE_KEY = "auto_dark_mode";
	public static final String START_DARK_TIME_KEY = "start_dart_time";
	public static final String END_DARK_TIME_KEY = "end_dark_time";
	public static final String USER_UID_KEY = "user_uid";
	public static final String USER_NAME_KEY = "user_name";
	public static final String HASH_KEY = "forum_hash";
	public static final String USER_GRADE_KEY = "user_grade";
	public static final String IS_REMBER_PASS_USER = "login_rember_pass";
	public static final String LOGIN_NAME = "login_name";
	public static final String LOGIN_PASS = "login_pass";
	public static final String CHECK_UPDATE_KEY = "check_update_time";
	public static final String LOGIN_URL = "member.php?mod=logging&action=login";
	public static final String LOGIN = BASE_URL + "member.php?mod=logging&action=login&mobile=2";
	public static final String CHECK_POST_URL = "forum.php?mod=ajax&action=checkpostrule&ac=newthread&mobile=2";
	public static final String CHECK_UPDATE_URL = "forum.php?mod=viewthread&tid=" + POST_TID + "&mobile=2";

	public final String URL_FORUMS = "api/mobile/index.php?module=forumindex&version=4";
	public final String URL_POST = "forum.php?mod=guide&view=%s&page=%s&mobile=2";
	public final String URL_NEW_POST = "api/mobile/index.php?module=forumindex&version=4";
/*
*  String type = (currentType == TYPE_HOT) ? "hot" : "new";
        String url = "forum.php?mod=guide&view=" + type + "&page=" + CurrentPage + "&mobile=2";*/

	/**
	 *
	 * */
	private final String URL_POST_LIKE = "comment/api/articlelike/add";
	private final String URL_POST_FAVORITE = "comment/api/articleFavorite/add";
	private final String URL_POST_CANCEL_FAVORITE = "comment/api/articleFavorite/deleteByUseridArticleid";
	private final String URL_POST_MYFAVORITES = "comment/api/articleFavorite/queryByUserid";
	private final String URL_POST_Integral = "comment/api/points/add";
	private final String URL_NOTIFY = "comment/api/points/notify";
	private final String URL_ORDER = "comment/api/points/order";
	private final String URL_STOCKCOINS = "comment/api/points/preOrder";
	private final String URL_USERLEVEL = "comment/api/userlevel/?userIdJson=%s&se=%s";
	private final String URL_DAILYTASKS = "comment/api/dailytasks/?userIdJson=%s&se=%s";
	private final String URL_MYORDERS = "comment/api/ordersdetails/?userIdJson=%s&se=%s";
	private final String URL_COLUMN = "channels/enapp/columns.json";
	private final String URL_COLUMNS = "channels/enapp/columns/%s/stories.json";
	private final String URL_COLUMNSMORE = "channels/enapp/columns/%s/stories/more_%s.json";
	private final String URL_SEARCH = "search.json?channel=enapp&keyword=%s&page=%s&size=%s"; // "s/search.json?channel=enapp&keyword=%s&page=%s&size=%s";
	private final String URL_SPECIAL_SECOND = "specials/%s/stories.json";
	private final String URL_SPECIAL_SECOND_MORE = "specials/%s/stories/more_%s.json";
	private final String URL_POST_FEEDBACK = "comment/api/feedback/add";
	private final String URL_TIME = "comment/api/time";
	private final String URL_APPINFO = "comment/api/startupinfo/android";
	//TODO
	private final String URL_REGION = "comment/api/startupinfo/android";
	private final String URL_ARTICLEINFO = "comment/api/articlecomment/articleDetailNew";
	private final String URL_COMMENT_SUBMIT = "comment/api/articlecomment/add";
	private final String URL_COMMENT_LIST = "comment/api/articlecomment/queryByArticleidByPageNew";
	private final String URL_COMMENT_MYLIST = "comment//api/articlecomment/queryByUserid";
	private final String URL_COMMENT_DELETE = "comment/api/articlecomment/deleteByCommentid";
	private final String URL_COMMENT_LIKE = "comment/api/commentLike/addNew";
	private final String URL_PHOTO_RECOMMAND = "comment/api/recommand/photo";
	//相关
	private final String URL_ARTICLE_RELATED = "recommendmobileapi/newsRelRecommend";
	//FOR YOU
	private final String URL_FORYOU = "recommendmobileapi/mixRecommend";
	// 金山翻译
	private final String URL_CIBA = "api.iciba.com/renminribao/search.php?word=";

	// 保存video,audio
	public static String VIDEO;
	public static String AUDIO;
	public final String MOST_WATCHED = "Most Watched";
	public static final String LANGUAGE_EN = "en";
	public static final String LANGUAGE_ZH = "zh";
	private static DataMng mThis;

	Context ctx;
	LoginUser loginUser;
	AESJava aesJava;
	
	FileUtil fu;

	Request request;
	UserMng userMng;
	AppInfo appInfo;
	PathMng pathMng;
	IntegralMng integralMng;

	String KEY, PROTOCOL, PROTOCOL_S, // 协议
			DOMAIN_BSL, // 主域名
			DOMAIN_ISMP, // 域名
			DOMAIN_ZQ, // 志强评论等
			DOMAIN_SEARCH, // 搜索域名
			CHANNEL, // 渠道名称
			sLanguage,
			sVersionName,
			URL_AD; // 广告

	int iVersionCode = -1;
	
	public
	boolean
		isFormalServer;

	public static DataMng getInstance(Context ctx, final String channel, boolean isFormalServer)
	{
		if (mThis == null)
		{
			synchronized (DataMng.class)
			{
				if (mThis == null)
				{
					mThis = new DataMng(ctx, channel, isFormalServer);
				}
			}
		}
		return mThis;
	}

	private DataMng(Context ctx, final String channel, boolean isFormalServer)
	{
		this.ctx = ctx;
		this.CHANNEL = channel;
		this.isFormalServer = isFormalServer;
		getKey();
		loginUser = LoginUser.getInstance(ctx, getAESJava());
		request = new Request(ctx, this, getAESJava());
		userMng = UserMng.getInstance(ctx, this);
		pathMng = PathMng.getInstance(ctx);
		integralMng = IntegralMng.getInstance(this, ctx);
		fu = new FileUtil();
	}
	public void onDestroy()
	{
		mThis = null;
		ctx = null;
		request.onDestroy();
	}
	public void setLanguage(final String language)
	{
		sLanguage = "" + language;

		if (request != null)
		{
			request.setLanguage(sLanguage);
		}
	}
	
	public String getKey()	{
		try
		{
			if(Empty.isEmpty(KEY)) 
			{
				KEY = JniData.getInstance().getKey();
			}
			return KEY;
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("DataMng getProtocol == " + exc.toString());
		}
		return null;
	}
	public String getAppendUserAgent()
	{
		return " CDAndroid/" + getVersionName() + " (" + CHANNEL + "; " + PkgMng.getVersionCode(ctx) + ")";
	}
	private String getVersionName()
	{
		try
		{
			if(Empty.isEmpty(sVersionName))
			{
				PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
				if(pi != null)
				{
					sVersionName = pi.versionName;
				}
			}
		}
		catch (PackageManager.NameNotFoundException var3)
		{
			Logger.e("DataMng getVersionName()=" + var3.toString());
		}

		return sVersionName;
	}
	public String getProtocol()
	{
		try
		{
			if(Empty.isEmpty(PROTOCOL))
			{
				PROTOCOL = JniData.getInstance().getProtocol(); // getAESJava().decrypt(FKey.getProtocol());
			}
			return PROTOCOL;
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("DataMng getProtocol == " + exc.toString());
		}
		return null;
	}
	public String getProtocolSSL()
	{
		try
		{
			if(isFormalServer)
			{
				if(Empty.isEmpty(PROTOCOL_S))
				{
					PROTOCOL_S = JniData.getInstance().getProtocolSSL();
				}
				return PROTOCOL_S;
			}
			return getProtocol();
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("DataMng getProtocol == " + exc.toString());
		}
		return null;
	}
	public String getDomainISMP()
	{
		try
		{
			if(Empty.isEmpty(DOMAIN_ISMP))
			{
				DOMAIN_ISMP = isFormalServer ? JniData.getInstance().getDomainISMP() : "enapp.i-newsroom.top/" ;
			}
			return DOMAIN_ISMP;
		}
		catch(Exception exc)
		{
			Logger.e("DataMng getDomainISMP == " + exc.toString());
		}
		return null;
	}

	public String getDomainBSL()
	{
		try
		{
			if(Empty.isEmpty(DOMAIN_BSL))
			{
				DOMAIN_BSL = isFormalServer ?  JniData.getInstance().getDomainZQ() : DR.CHANNEL_TESTING.equals(CHANNEL) ? "124.127.180.233:8400/" : "192.168.11.19:8400/";
			}
			return DOMAIN_BSL;
		}
		catch(Exception exc)
		{
			Logger.e("DataMng getDomainBSL == " + exc.toString());
		}
		return null;
	}

	public String getDomainZQ()
	{
		try
		{
			if(Empty.isEmpty(DOMAIN_ZQ))
			{
				DOMAIN_ZQ = isFormalServer ?  JniData.getInstance().getDomainZQ() : DR.CHANNEL_TESTING.equals(CHANNEL) ? "124.127.180.233:8400/" : "192.168.11.19:8400/";  
			}
			return DOMAIN_ZQ;
		}
		catch(Exception exc)
		{
			Logger.e("DataMng getDomainISMP == " + exc.toString());
		}
		return null;
	}
	public String getDomainSearch()
	{
		try
		{
			if(Empty.isEmpty(DOMAIN_SEARCH))
			{
				//TODO 2018/08/23 测试H5显示title需要有地址不能为空
				DOMAIN_SEARCH = isFormalServer ? JniData.getInstance().getDomainSearch() : JniData.getInstance().getDomainSearch(); 
			}
			return DOMAIN_SEARCH;
		}
		catch(Exception exc)
		{
			Logger.e("DataMng getDomainISMP == " + exc.toString());
		}
		return null;
	}
	public String getDomainUCenter()
	{
		try
		{
			return userMng.getDomainUCenter();
		}
		catch(Exception exc)
		{
			Logger.e("DataMng getDomainUCenter == " + exc.toString());
		}
		return null;
	}
	public AESJava getAESJava()
	{
		if (aesJava == null)
		{
			aesJava = new AESJava(getKey());
		}
		return aesJava;
	}
	public LoginUser getLoginUserInstance()
	{
		return loginUser;
	}
	public String getUId()
	{
		return loginUser.getUId();
//		return "2592675";
	}
	public String getUniqueId()
	{
		return loginUser.getUniqueId();
	}
	public String getAccountId()
	{
		return loginUser.getAccountId();
	}
	public User getLoginUser()
	{
		return getLoginUser(false);
	}
	public User getLoginUser(boolean force)
	{
		return loginUser.getLoginUser(force);
	}
	
	public String getLoginUserRegistData()
	{
		User loginUser = getLoginUser();
		if(loginUser != null)
		{
			return loginUser.sRegistDate;
		}
		return null;
	}
	public String getTimer()
	{
		return userMng.getTimer();
	}
	public String getToken(String value)
	{
		return userMng.getToken(value);
	}
	public String getPolicyUrl()
	{
		return getProtocolSSL() + getDomainZQ() + "comment/api/static/html/ChinadailyTermOfService.html";
	}
	
	public String getPrivacyPolicyUrl()
	{
		return getProtocolSSL() + getDomainZQ() + "comment/api/static/html/PrivacyPolicy.html";
	}
	
	public String getTermsofUseUrl()
	{
		return getProtocolSSL() + getDomainZQ() + "comment/api/static/html/TermsofUse.html";
	}
	
	public String getStoreUrl()
	{
		return getProtocolSSL() + getDomainZQ() + "comment/api/dailytasks/store";
	}
	private boolean checkUrl(final String url, final Async.IBase base)
	{
		if (Empty.isEmpty(url))
		{
			onCheckFail(base, ECode.E_URL_ADDRESS, "无效URL");
			return false;
		}

		return true;
	}

	private String getPackageName()
	{
		return ctx.getPackageName();
	}

	private String getChannel()
	{
		return PkgMng.getApplicationMetaData(ctx, getPackageName(), DR.CHANNEL_NAME);
	}

	public int getVersionCode()
	{
		if (iVersionCode == -1)
		{
			iVersionCode = PkgMng.getVersionCode(ctx);
		}
		return iVersionCode;
	}
	public void onCheckFail(final Async.IBase base, final int errorCode, final String msg)
	{
		request.onCheckFail(base, errorCode, msg);
	}

	// 图片本地是否已存在
	public String hasLocalImg(String url)
	{
		return request.hasLocalImg(url);
	}

	public String hasLocalHeadImg(String url)
	{
		return request.hasLocalHeadImg(url);
	}

	// 请求图片
	public void reqImg(final String url, final IImage ibase)
	{
		if (!checkUrl(url, ibase))
			return;

		request.reqImg(url, ibase);
	}

	// 请求头像
	public void reqHeadImg(final String url, final IImage ibase)
	{
		if (!checkUrl(url, ibase))
			return;

		if (!Empty.isEmpty(getUId()))
		{
			request.reqHeadImg(url, ibase);
		}
		else
		{
			onCheckFail(ibase, ECode.E_PARAME, "userid为空");
		}
	}

	// 请求字符串
	public void reqString(final String url, IString iReq)
	{
		if (!checkUrl(url, iReq))
			return;
		request.reqString(url, iReq);

	}

	// 请求应用信息
	public void reqAppInfo(final boolean isAuto, final Async.IAppInfo iAppInfo)
	{
		if (isAuto)
		{
			// 如果是自动更新,有数据的话,会立即返回
			if (iAppInfo != null)
			{
				if (appInfo != null)
				{
					iAppInfo.onAppInfo(appInfo, null);
					return;
				}
			}
		}

		request.reqAppInfo(getProtocolSSL() + getDomainZQ() + URL_APPINFO, iAppInfo, CacheMode.ONLY_REQUEST_NETWORK);
	}
	
	//TODO 请求所在国家地区
	/*public void reqCountryRegion()
	{
		request.reqCountryRegion(getProtocolSSL() + getDomainZQ() + , iBase, CacheMode.CACHE_THEN_REQUEST_NETWORK)
	}*/

	// 请求详情数据
	public void reqArticle(final String url, final IArticle ibase)
	{
		reqArticle(url, ibase, CacheMode.CACHE_THEN_REQUEST_NETWORK);
	}
	
	public void reqArticle(final String url, final IArticle ibase, final CacheMode cacheMode)
	{
		if (!checkUrl(url, ibase))
			return;

		request.reqArticle(url, StrUtil.getMD5(url), ibase, cacheMode);
	}
	
	public void postPhotoRecommand(final String articleId, final String columnId, final Async.IPhotoRecommand ibase)
	{
		try
		{
			boolean checkResult = false;

			String msg = "";

			if (!Empty.isEmpty(articleId) && !Empty.isEmpty(columnId))
			{
				String sUserId = getAccountId();

				if (!Empty.isEmpty(sUserId))
				{
					long time = System.currentTimeMillis();
					// 顺序按首字母排,首字母相同按下一个字母排
					String value = "articleid=" + articleId + "&columnId=" + columnId + "&time=" + time + "&uid=" + sUserId + "&version=" + getVersionCode();
					String token = getAESJava().encrypt(value);

					if (!Empty.isEmpty(token))
					{
						checkResult = true;

						HashMap<String, Object> hm = new HashMap<String, Object>(9);
						hm.put("articleid", "" + articleId);
						hm.put("columnId", "" + columnId);
						hm.put("time", "" + time);
						hm.put("uid", "" + sUserId);
						hm.put("version", "" + getVersionCode());
						hm.put("token", token);

						request.postPhotoRecommand(getProtocolSSL() + getDomainZQ() + URL_PHOTO_RECOMMAND, ibase, hm, articleId + "_" + columnId);
					}
					else
					{
						msg = "token错误";
					}
				}
				else
				{
					msg = "userid错误";
				}
			}
			else
			{
				msg = "参数错误";
			}

			if (!checkResult)
			{
				onCheckFail(ibase, ECode.E_PARAME, msg);
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("postComment error == " + exc.toString());
		}
	}
	
	public Article getLocalArticle(final String url)
	{
		return request.getLocalArticle(StrUtil.getMD5(url));
	}

	// 广告
	public void reqAd()
	{
		request.reqAd(getProtocolSSL() + getDomainZQ() + URL_AD + getPackageName() + "_" + getVersionCode() + "_" + getChannel() + ".json", new IAd()
		{
			@Override
			public void onStart(Object tag)
			{
			}

			@Override
			public void onError(int code, String msg, Object tag)
			{
			}

			@Override
			public void onAd(AdInfo adInfo, boolean isCache, Object tag)
			{
				if (adInfo != null)
				{
					handleAd(adInfo.arcAd);
					handleAd(adInfo.bsAd);
				}
			}
		}, CacheMode.REQUEST_NETWORK_BY_CACHE);
	}

	private void handleAd(final Ad ad)
	{
		if (ad != null)
		{
			List<AdItem> ltAdItem = ad.getAdItemList();
			if (!Empty.isEmpty(ltAdItem))
			{
				int i1 = 0;
				while (i1 < ltAdItem.size())
				{
					final AdItem adItem;
					if ( (adItem = ltAdItem.get(i1)) != null)
					{
						reqAdIO(adItem.sFileUrl, null);
					}
					i1++;
				}
			}
		}
	}

	public void reqAdIO(final String url, final IIO ibase)
	{
		if (!checkUrl(url, ibase))
			return;

		request.reqAdIO(url, ibase, CacheMode.NONE_CACHE_REQUEST_NETWORK);
	}

	// 翻译
	public void reqTranslate(final String word, final ITranslate ibase)
	{
		if (Empty.isEmpty(word) || ibase == null)
		{
			onCheckFail(ibase, ECode.E_PARAME, "参数错误");

			return;
		}

		request.reqTranslate(getProtocol() + URL_CIBA + word + "&authkey=" + StrUtil.getMD5(word + "^IcibaEnjoyrenmin$"), ibase, CacheMode.ONLY_REQUEST_NETWORK);
	}

	// 提交评论
	public void postComment(final String articleId, final String comment, final ICommentList ibase)
	{
		try
		{
			boolean checkResult = false;

			String msg = "";

			if (!Empty.isEmpty(articleId) && comment != null)
			{
				String sUserId = getUId();

				if (!Empty.isEmpty(sUserId))
				{
					long time = System.currentTimeMillis();
					// 顺序按首字母排,首字母相同按下一个字母排
					String value = "articleid=" + articleId + "&comment=" + comment + "&platform=" + "android" + "&time=" + time + "&uid=" + sUserId + "&version=" + getVersionCode();
					String token = getAESJava().encrypt(value);

					if (!Empty.isEmpty(token))
					{
						checkResult = true;

						HashMap<String, Object> hm = new HashMap<String, Object>(9);
						hm.put("articleid", "" + articleId);
						hm.put("comment", "" + comment);
						hm.put("platform", "android");
						hm.put("time", "" + time);
						hm.put("uid", "" + sUserId);
						hm.put("version", "" + getVersionCode());
						hm.put("token", token);

						request.postComment(getProtocolSSL() + getDomainZQ() + URL_COMMENT_SUBMIT, ibase, hm);
					}
					else
					{
						msg = "token错误";
					}
				}
				else
				{
					msg = "userid错误";
				}
			}
			else
			{
				msg = "参数错误";
			}

			if (!checkResult)
			{
				onCheckFail(ibase, ECode.E_PARAME, msg);
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("postComment error == " + exc.toString());
		}
	}

	// 评论列表
	public void postCommentList(final String articleId, final int pageNo, final ICommentList ibase)
	{
		try
		{
			boolean checkResult = true;

			String msg = "";

			if (!Empty.isEmpty(articleId) && pageNo >= 0)
			{
				String sUniqueId = getUniqueId();
				if (!Empty.isEmpty(sUniqueId))
				{
					JSONObject jsonObject = new JSONObject();
					if(!Empty.isEmpty(getUId()))
					{
						jsonObject.put("uid", getUId());
					}
					jsonObject.put("articleid", "" + articleId);
					jsonObject.put("deviceId", "" + sUniqueId);
					jsonObject.put("pageNo", pageNo);
					jsonObject.put("version", "" + getVersionCode());
					
					AESMsgOrder aesMsgOrder = new AESMsgOrder(getKey());
					
					String sPayload = aesMsgOrder.encrypt(jsonObject.toString());
					if(!Empty.isEmpty(sPayload))
					{
						checkResult = true;

						HashMap<String, Object> hm = new HashMap<String, Object>(1);
						hm.put("payload", "" + sPayload);

						request.postCommentList(getProtocolSSL() + getDomainZQ() + URL_COMMENT_LIST, ibase, hm);
					}
					else
					{
						msg = "加密错误";
					}
				}
				else
				{
					msg = "uniqueId错误";
				}
			}
			else
			{
				msg = "参数错误";
			}

			if (!checkResult)
			{
				onCheckFail(ibase, ECode.E_PARAME, msg);
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("postCommentList error == " + exc.toString());
		}
	}

	// 删除评论
	public void postDeleteComment(final long commentid, final String articleid, final ICommentDelete ibase)
	{
		try
		{
			boolean checkResult = false;

			String msg = "";

			if (commentid > 0)
			{
				String sUserId = getUId();
				if (!Empty.isEmpty(sUserId))
				{
					long time = System.currentTimeMillis();
					// 顺序按首字母排,首字母相同按下一个字母排
					String value = "articleid=" + articleid + "&commentid=" + commentid + "&time=" + time + "&uid=" + sUserId + "&version=" + getVersionCode();
					String token = getAESJava().encrypt(value);

					if (!Empty.isEmpty(token))
					{
						checkResult = true;

						HashMap<String, Object> hm = new HashMap<String, Object>(4);
						hm.put("articleid", "" + articleid);
						hm.put("commentid", "" + commentid);
						hm.put("time", "" + time);
						hm.put("uid", "" + sUserId);
						hm.put("version", "" + getVersionCode());
						hm.put("token", token);

						request.postDeleteComment(getProtocolSSL() + getDomainZQ() + URL_COMMENT_DELETE, ibase, hm);
					}
					else
					{
						msg = "token错误";
					}
				}
				else
				{
					msg = "userid错误";
				}
			}
			else
			{
				msg = "参数错误";
			}

			if (!checkResult)
			{
				onCheckFail(ibase, ECode.E_PARAME, msg);
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqComment error == " + exc.toString());
		}
	}
	
	// 评论点赞
	public void postLikeComment(final long commentid, final String articleid, final ICommentLike ibase)
	{
		try
		{
			boolean checkResult = false;

			String msg = "";

			if (commentid > 0 && !Empty.isEmpty(articleid))
			{
				String sUniqueId = getUniqueId();
				if (!Empty.isEmpty(sUniqueId))
				{
					JSONObject jsonObject = new JSONObject();
					if(!Empty.isEmpty(getUId()))
					{
						jsonObject.put("uid", getUId());
					}
					jsonObject.put("articleid", "" + articleid);
					jsonObject.put("deviceId", "" + sUniqueId);
					jsonObject.put("commentid", commentid);
					jsonObject.put("version", "" + getVersionCode());
					
					AESMsgOrder aesMsgOrder = new AESMsgOrder(getKey());
					
					String sPayload = aesMsgOrder.encrypt(jsonObject.toString());
					if(!Empty.isEmpty(sPayload))
					{
						checkResult = true;

						HashMap<String, Object> hm = new HashMap<String, Object>(1);
						hm.put("payload", "" + sPayload);

						request.postLikeComment(getProtocolSSL() + getDomainZQ() + URL_COMMENT_LIKE, ibase, hm);
					}
					else
					{
						msg = "加密错误";
					}
				}
				else
				{
					msg = "uniqueId错误";
				}
			}
			else
			{
				msg = "参数错误";
			}

			if (!checkResult)
			{
				onCheckFail(ibase, ECode.E_PARAME, msg);
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqComment error == " + exc.toString());
		}
	}

	// 请求详情评论点赞相关接口
	public void postArticleInfo(final String articleId, final IArticleInfo ibase)
	{
		try
		{
			boolean checkResult = false;

			String msg = "";

			if (!Empty.isEmpty(articleId))
			{
				String sUniqueId = getUniqueId();
				if (!Empty.isEmpty(sUniqueId))
				{
					JSONObject jsonObject = new JSONObject();
					if(!Empty.isEmpty(getUId()))
					{
						jsonObject.put("uid", getUId());
					}
					jsonObject.put("articleid", "" + articleId);
					jsonObject.put("deviceId", "" + sUniqueId);
					jsonObject.put("version", "" + getVersionCode());
					
					AESMsgOrder aesMsgOrder = new AESMsgOrder(getKey());
					
					String sPayload = aesMsgOrder.encrypt(jsonObject.toString());
					if(!Empty.isEmpty(sPayload))
					{
						checkResult = true;

						HashMap<String, Object> hm = new HashMap<String, Object>(1);
						hm.put("payload", "" + sPayload);

						request.postArticleInfo(getProtocolSSL() + getDomainZQ() + URL_ARTICLEINFO, ibase, hm);
					}
					else
					{
						msg = "加密错误";
					}
				}
				else
				{
					msg = "uniqueId错误";
				}
			}
			else
			{
				msg = "参数错误";
			}

			if (!checkResult)
			{
				onCheckFail(ibase, ECode.E_PARAME, msg);
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqComment error == " + exc.toString());
		}
	}
	

	// 请求我的评论列表
	public void postMyComments(final long lastCommentid, final ICommentList ibase)
	{
		try
		{
			boolean checkResult = false;

			String msg = "";

			if (lastCommentid >= 0)
			{
				String sUserId = getUId();

				if (!Empty.isEmpty(sUserId))
				{
					long time = System.currentTimeMillis();
					// 顺序按首字母排,首字母相同按下一个字母排
					String value = "commentid=" + lastCommentid + "&time=" + time + "&uid=" + sUserId + "&version=" + getVersionCode();
					String token = getAESJava().encrypt(value);

					if (!Empty.isEmpty(token))
					{
						checkResult = true;

						HashMap<String, Object> hm = new HashMap<String, Object>(5);
						hm.put("commentid", "" + lastCommentid);
						hm.put("time", "" + time);
						hm.put("uid", "" + sUserId);
						hm.put("version", "" + getVersionCode());
						hm.put("token", token);

						request.postCommentList(getProtocolSSL() + getDomainZQ() + URL_COMMENT_MYLIST, ibase, hm);
					}
					else
					{
						msg = "token错误";
					}
				}
				else
				{
					msg = "userid错误";
				}
			}
			else
			{
				msg = "参数错误";
			}

			if (!checkResult)
			{
				onCheckFail(ibase, ECode.E_PARAME, msg);
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqComment error == " + exc.toString());
		}
	}

	// 请求服务器时间
	public void postServerTime(final IServiceTime ibase)
	{
		try
		{
			boolean checkResult = false;

			String msg = "";

			long time = System.currentTimeMillis();
			// 顺序按首字母排,首字母相同按下一个字母排
			String value = "time=" + time + "&version=" + getVersionCode();
			String token = getAESJava().encrypt(value);

			if (!Empty.isEmpty(token))
			{
				checkResult = true;

				HashMap<String, Object> hm = new HashMap<String, Object>(3);
				hm.put("time", "" + time);
				hm.put("version", "" + getVersionCode());
				hm.put("token", token);

				request.postServerTime(getProtocolSSL() + getDomainZQ() + URL_TIME, ibase, hm);
			}
			else
			{
				msg = "token错误";
			}

			if (!checkResult)
			{
				onCheckFail(ibase, ECode.E_PARAME, msg);
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("postServerTime error == " + exc.toString());
		}
	}
	
	// 请求详情相关数据
	public void reqRelated(final String articleID, final IRecommend ibase)
	{
		try
		{
			boolean checkResult = false;

			String msg = "";
			
			if(!Empty.isEmpty(articleID))
			{
				String time = userMng.getTimer();
				String token = userMng.getToken(articleID + time);

				if (!Empty.isEmpty(token))
				{
					checkResult = true;

					HashMap<String, Object> hm = new HashMap<String, Object>(3);
					hm.put("newsid", articleID);
					hm.put("time", "" + time);
					hm.put("token", token);

					request.reqRelated(userMng.getPDomain() + URL_ARTICLE_RELATED, articleID, ibase, hm);
				}
				else
				{
					msg = "token错误";
				}
			}
			else
			{
				msg = "参数错误";
			}

			if (!checkResult)
			{
				onCheckFail(ibase, ECode.E_PARAME, msg);
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("postRelated error == " + exc.toString());
		}
	}

	public void postMyFavorites(final long favoriteId, final boolean isLocalCache, Async.IPostMyFavorites iReq)
	{
		try
		{
			boolean paramIsOk = false;
			String sError = "";
			if (favoriteId > 0)
			{
				String userId = getUId();
				if (!Empty.isEmpty(userId))
				{
					// String key = getKey();
					// if (!Empty.isEmpty(key))
					{
						long time = System.currentTimeMillis();
						String value = "favoriteid=" + favoriteId + "&time=" + time + "&uid=" + userId + "&version=" + getVersionCode();
						// String token = AES128.encrypt(key, value);
						String token = getAESJava().encrypt(value);
						if (!Empty.isEmpty(token))
						{
							HashMap<String, Object> hm = new HashMap<String, Object>(5);
							hm.put("favoriteid", "" + favoriteId);
							hm.put("time", "" + time);
							hm.put("uid", "" + userId);
							hm.put("version", "" + getVersionCode());
							hm.put("token", token);
							request.postMyFavorites(getProtocolSSL() + getDomainZQ() + URL_POST_MYFAVORITES, hm, iReq);
							paramIsOk = true;
						}
						else
						{
							sError = "token错误";
						}
					}
					// else
					// {
					// sError = "key错误";
					// }
				}
				else
				{
					sError = "userId错误";
				}
			}
			else
			{
				sError = "favoriteId错误";
			}
			if (!paramIsOk)
			{
				onCheckFail(iReq, ECode.E_PARAME, sError);
			}
		}
		catch (Exception ext)
		{
			Logger.e("postFavorites(): " + ext.toString());
		}
	}

	public void postFavorite(final String articleId, final Async.IResultMsgIncludeUid iReq)
	{
		try
		{
			boolean paramIsOk = false;
			String sError = "";
			if (!Empty.isEmpty(articleId))
			{
				String userId = getUId();
				if (!Empty.isEmpty(userId))
				{
					// String key = getKey();
					// if(!Empty.isEmpty(key))
					{
						long time = System.currentTimeMillis();
						String value = "articleid=" + articleId + "&time=" + time + "&uid=" + userId + "&version=" + getVersionCode();
						// String token = AES128.encrypt(key, value);
						String token = getAESJava().encrypt(value);
						if (!Empty.isEmpty(token))
						{
							HashMap<String, Object> hm = new HashMap<String, Object>(5);
							hm.put("articleid", "" + articleId);
							hm.put("time", "" + time);
							hm.put("uid", "" + userId);
							hm.put("version", "" + getVersionCode());
							hm.put("token", token);
							request.postFavorite(getProtocolSSL() + getDomainZQ() + URL_POST_FAVORITE, hm, iReq);
							paramIsOk = true;
						}
						else
						{
							sError = "token错误";
						}
					}
					// else
					// {
					// sError = "key错误";
					// }
				}
				else
				{
					sError = "userId错误";
				}
			}
			else
			{
				sError = "articleId错误";
			}
			if (!paramIsOk)
			{
				onCheckFail(iReq, ECode.E_PARAME, sError);
			}
		}
		catch (Exception ext)
		{
			Logger.e("postFavorite(): " + ext.toString());
		}
	}

	public void postLike(final String articleId, final Async.IResultMsgIncludeUid iReq)
	{
		try
		{
			boolean paramIsOk = false;
			String sError = "";
			if (!Empty.isEmpty(articleId))
			{
				String userId = getAccountId();
				if (!Empty.isEmpty(userId))
				{
					// String key = getKey();
					// if(!Empty.isEmpty(key))
					{
						long time = System.currentTimeMillis();
						String value = "articleid=" + articleId + "&time=" + time + "&uid=" + userId + "&version=" + getVersionCode();
						// String token = AES128.encrypt(key, value);
						String token = getAESJava().encrypt(value);
						if (!Empty.isEmpty(token))
						{
							HashMap<String, Object> hm = new HashMap<String, Object>(5);
							hm.put("articleid", "" + articleId);
							hm.put("time", "" + time);
							hm.put("uid", "" + userId);
							hm.put("version", "" + getVersionCode());
							hm.put("token", token);
							request.postLike(getProtocolSSL() + getDomainZQ() + URL_POST_LIKE, hm, iReq);
							paramIsOk = true;
						}
						else
						{
							sError = "token错误";
						}
					}
					// else
					// {
					// sError = "key错误";
					// }
				}
				else
				{
					sError = "userId错误";
				}
			}
			else
			{
				paramIsOk = false;
				sError = "articleId错误";
			}
			if (!paramIsOk)
			{
				onCheckFail(iReq, ECode.E_PARAME, sError);
			}
		}
		catch (Exception ext)
		{
			Logger.e("postLike() " + ext.toString());
		}
	}

	public void postCancelFavorite(final String articleIds, final Async.IPostCancelFavorites iReq)
	{
		try
		{
			boolean paramIsOk = false;
			String sError = "";
			if (!Empty.isEmpty(articleIds))
			{
				String userId = getUId();
				if (!Empty.isEmpty(userId))
				{
					// String key = getKey();
					// if(!Empty.isEmpty(key))
					{
						long time = System.currentTimeMillis();
						String value = "articleidList=" + articleIds + "&time=" + time + "&uid=" + userId + "&version=" + getVersionCode();
						// String token = AES128.encrypt(key, value);
						String token = getAESJava().encrypt(value);
						if (!Empty.isEmpty(token))
						{
							HashMap<String, Object> hm = new HashMap<String, Object>(5);
							hm.put("articleidList", "" + articleIds);
							hm.put("time", "" + time);
							hm.put("uid", "" + userId);
							hm.put("version", "" + getVersionCode());
							hm.put("token", "" + token);
							request.postCancelFavorite(getProtocolSSL() + getDomainZQ() + URL_POST_CANCEL_FAVORITE, hm, iReq);
							paramIsOk = true;
						}
						else
						{
							sError = "token错误";
						}
					}
					// else
					// {
					// sError = "key错误";
					// }
				}
				else
				{
					sError = "userId错误";
				}
			}
			else
			{
				sError = "articleIds错误";
			}
			if (!paramIsOk)
			{
				onCheckFail(iReq, ECode.E_PARAME, sError);
			}
		}
		catch (Exception ext)
		{
			Logger.e("postCancelFavorite() " + ext.toString());
		}
	}

	//FOR YOU
	public void reqForYou(final Async.IRecommend iReq, final boolean isLocalCachePriority)
	{
		try
		{
			String uid = getUId();
			
			boolean paramIsOk = false;
			String sError = "";
			String time = userMng.getTimer();
			String token = userMng.getToken(uid + getUniqueId() + time);
			
			if (!Empty.isEmpty(token))
			{
				paramIsOk = true;
				
				HashMap<String, Object> hm = new HashMap<String, Object>(5);
				hm.put("time", "" + time);
				hm.put("token", "" + token);
				hm.put("u_cookie", "" + getUniqueId());
				
				if(!Empty.isEmpty(uid))
				{
					hm.put("uid", uid);
				}

				request.reqForYou(userMng.getPDomain() + URL_FORYOU, iReq, hm, isLocalCachePriority && iReq != null ? CacheMode.CACHE_THEN_REQUEST_NETWORK : CacheMode.REQUEST_NETWORK_BY_CACHE);
			}
			else
			{
				sError = "token错误";
			}
			if (!paramIsOk)
			{
				onCheckFail(iReq, ECode.E_PARAME, sError);
			}
		}
		catch (Exception ext)
		{
			Logger.e("postForYou() " + ext.toString());
		}
	}
	
	// 请求栏目列表
	public void reqColumns(Async.IColumn iColumn)
	{
		this.reqColumns(iColumn, CacheMode.CACHE_THEN_REQUEST_NETWORK);
	}

	private void reqColumns(final Async.IColumn iColumn, final CacheMode mode)
	{
		request.requestColumn(formatSourceColumnUrl(), iColumn, mode);
	}

	// 请求所有版块列表
	public void reqForums(Async.IColumn iColumn)
	{
		this.reqForums(iColumn, CacheMode.CACHE_THEN_REQUEST_NETWORK);
	}

	private void reqForums(final Async.IColumn iColumn, final CacheMode mode)
	{
		request.requestColumn(formatSourceForumUrl(), iColumn, mode);
	}

	public void reqPostList()
	{
		this.reqPostList(String type, int page, iBlock, isLocalCachePriority && iBlock != null ? CacheMode.CACHE_THEN_REQUEST_NETWORK : CacheMode.REQUEST_NETWORK_BY_CACHE);
	}

	private void reqPostList(final String type, final int page, final Async.IBlock iBlock, final CacheMode mode)
	{
		String url = formatPostColumnUrl(type, page);
		request.requestHomeArts(url, StrUtil.getMD5(url), iBlock, mode);
	}
	
	public void postCheckStock(final int productId, final String sha1, final Async.ICheckStockCoins iReq)
	{
		boolean checkResult = false;
		String msg = "";
		String payload = null;
		if(!Empty.isEmpty(sha1) && productId > 0)
		{
			if(!Empty.isEmpty(payload = getPrePayload(productId, sha1)))
			{
				checkResult = true;
	
				HashMap<String, Object> hm = new HashMap<String, Object>(1);
				hm.put("payload", "" + payload);
				request.postCheckStockCoins(formatCheckStockCoinsUrl(), hm, iReq);
			}
			else
			{
				msg = "payload错误";
			}
		}
		else
		{
			msg = "参数错误";
		}
		
		if (!checkResult)
		{
			onCheckFail(iReq, ECode.E_PARAME, msg);
		}
	}
	
	private String getPrePayload(final int productId, final String sha1)
	{
		try
		{
			if(!Empty.isEmpty(sha1) && productId > 0)
			{
				JSONObject jsonObject = new JSONObject();
				//TODO 测试uid
				String uid = Empty.isEmpty(getUId()) ? "123" : getUId();
				String payload = "";
				if(!Empty.isEmpty(uid))
				{
					jsonObject.put("platform", "android"); //platform
					jsonObject.put("sha1", sha1);//sha1
					jsonObject.put("uid", uid); //uid
					jsonObject.put("productId", productId); //productId
				}
	
				String value = jsonObject.toString();
				if(!Empty.isEmpty(value))
				{
					payload = new AESMsgOrder(FKey.getAesKey()).encrypt(value);
				}
				return payload;
			}
		}
		catch(Exception exc)
		{
			Logger.e("getPrePayload : " + exc.toString());
		}
		return null;
	}
	
	public void postOrder(final int productId, final AddressInfo addressInfo, final String sha1, final Async.IOrder iReq)
	{
		boolean checkResult = false;

		String msg = "";
		String payload = null;

		if(!Empty.isEmpty(payload = getOrderPayload(productId, addressInfo, sha1)))
		{
			checkResult = true;

			HashMap<String, Object> hm = new HashMap<String, Object>(1);
			hm.put("payload", "" + payload);
			request.postOrder(formatOrderUrl(), hm, iReq);
		}
		else
		{
			msg = "payload错误";
		}
		if (!checkResult)
		{
			onCheckFail(iReq, ECode.E_PARAME, msg);
		}
	}
	
	private String getOrderPayload(final int productId, final AddressInfo addressInfo, final String sha1)
	{
		try
		{
			if(!Empty.isEmpty(sha1) && productId > 0 && addressInfo != null)
			{
				JSONObject jsonObject = new JSONObject();
				//测试uid
				String uid = Empty.isEmpty(getUId()) ? "123" : getUId();
				String payload = "";
				if(!Empty.isEmpty(uid))
				{
					jsonObject.put("platform", "android"); //platform
	
					jsonObject.put("sha1", sha1);//sha1
	
					JSONObject jsonUserOrder = new JSONObject();
					jsonUserOrder.put("uuid", UUID.randomUUID()); //uid
					jsonUserOrder.put("uid", uid); //uid
					jsonUserOrder.put("productId", productId); //productId
					jsonUserOrder.put("recipientsName",addressInfo.sName); //productId
					jsonUserOrder.put("phone", addressInfo.sPhoneNumber); //phone
					jsonUserOrder.put("zipcode", addressInfo.sZipCode); //zipcode
					jsonUserOrder.put("mailAddress", addressInfo.sAddress); //mailAddress
	
					jsonObject.put("userOrder", jsonUserOrder);
				}
	
				String value = jsonObject.toString();
				if(!Empty.isEmpty(value))
				{
					payload = new AESMsgOrder(FKey.getAesKey()).encrypt(value);
				}
				return payload;
			}
		}
		catch(Exception exc)
		{
			Logger.e("getOrderPayload : " + exc.toString());
		}
		return null;
	}
	
	private String getNotifiyPayload(final String uid, final String startTime, final String sha1)
	{
		try
		{
			if(!Empty.isEmpty(uid) && !Empty.isEmpty(startTime) && !Empty.isEmpty(sha1))
			{
				String payload = "";
				
				//文件最后修改时间存放路径
				
				JSONObject jsonObject = new JSONObject();
				
				if(!Empty.isEmpty(uid))
				{
					jsonObject.put("platform", "android"); //platform
					jsonObject.put("uid", uid); //uid
					jsonObject.put("sha1", sha1);//sha1
					jsonObject.put("time", Long.valueOf(startTime == null ? "0" :startTime));
				}
		
				String value = jsonObject.toString();
				if(!Empty.isEmpty(value))
				{
					payload = new AESMsgOrder(FKey.getAesKey()).encrypt(value);
				}
				return payload;
			}
		}
		catch(Exception exc)
		{
			Logger.e("getNotifiyPayload : " + exc.toString());
		}
		return null;
	}
	
	public void postNotification(final String sha1, final Async.INotify iReq)
	{
		try
		{
			boolean checkResult = false;
	
			String msg = "";
			if(!Empty.isEmpty(sha1))
			{
				//TODO 测试uid
				String uid = getUId();
				
				if(!Empty.isEmpty(uid) && !Empty.isEmpty(sha1))
				{
					String startTime = getStartTime(uid);
					String payload = getNotifiyPayload(uid, startTime, sha1);
					if(!Empty.isEmpty(payload))
					{
						checkResult = true;
			
						HashMap<String, Object> hm = new HashMap<String, Object>(1);
						hm.put("payload", "" + payload);
						if(!Empty.isEmpty(startTime))
						{
							hm.put("startTime", "" + startTime);
						}
						request.postNotify(formatNotifyUrl(), hm, iReq);
					}
					else
					{
						msg = "payload错误";
					}
				}
				else
				{
					msg = "userid错误";
				}
			}
			else
			{
				msg = "参数错误";
			}
			if(!checkResult)
			{
				onCheckFail(iReq, ECode.E_PARAME, msg);
			}
		}
		catch(Exception exc)
		{
			Logger.e("postNotification() " + exc.toString());
		}
	}

	private String getStartTime(String uid) 
	{
		String startTime = "0";
		if(!Empty.isEmpty(uid))
		{
			String dataPath;
			
			if(!Empty.isEmpty(dataPath = pathMng.getNotifyRootDir(uid)) && pathMng != null)
			{
				String lastModifyPath = pathMng.getNotifiyLastTime(uid);
				if(!Empty.isEmpty(lastModifyPath))
				{
					String time = IO.read(lastModifyPath);
					if(!Empty.isEmpty(time))
					{
						time = time.trim();
						File mFile = new File(dataPath);
						//如果文件夹时间一致，用存放的responseTime请求
						if(mFile.lastModified() == Long.valueOf(time))
						{
							String readResponseTimeFileString = IO.read(pathMng.getNotifiyResponseTimeDir(uid));
							if(!Empty.isEmpty(readResponseTimeFileString))
							{
								startTime = readResponseTimeFileString;
							}
						}
					}
				}
			}
			if("0".equals(startTime))
			{
				FileUtil.delete(FileUtil.isExists(pathMng.getNotifiyResponseTimeDir(uid)));
			}
			return startTime;
		}
		return startTime;
	}
	
	//请求Bilingual栏目
	
	public void reqBilingualArts(final String columnId, Async.IDataObjs IDataObj, final boolean isLocalCachePriority)
	{
		this.reqBilingualArts(columnId, IDataObj, isLocalCachePriority && IDataObj != null ? CacheMode.CACHE_THEN_REQUEST_NETWORK : CacheMode.REQUEST_NETWORK_BY_CACHE);
	}
	
	private void reqBilingualArts(final String columnId, Async.IDataObjs IDataObj, final CacheMode mode)
	{
		if (Empty.isEmpty(columnId))
		{
			onCheckFail(IDataObj, ECode.E_PARAME, "参数有误");
			return;
		}
		// 第一页的地址
		String url = formatColumnUrl(columnId);
		request.requestDataObjs(url, StrUtil.getMD5(url), IDataObj, mode);
	}
	
	//请求二级专题
	public void reqSpecialSecondList(final String specialId, Async.IDataObjs IDataObj, final boolean isLocalCachePriority)
	{
		if (Empty.isEmpty(specialId))
		{
			onCheckFail(IDataObj, ECode.E_PARAME, "参数有误");
			return;
		}
		// 第一页的地址
		String url = formatSpecialUrl(specialId);
		request.requestDataObjs(url, StrUtil.getMD5(url), IDataObj, (isLocalCachePriority ? CacheMode.CACHE_THEN_REQUEST_NETWORK : CacheMode.REQUEST_NETWORK_BY_CACHE));
	}
	
	//请求二级专题更多
	public void reqSpecialSecondListMore(final String specialId, final int page, Async.IArticlesMore iArticles)
	{
		if (Empty.isEmpty(specialId) || page <= 0)
		{
			onCheckFail(iArticles, ECode.E_PARAME, "参数有误");
			return;
		}
		// 更多页的地址
		String url = formatSpecialMoreUrl(specialId, page);
		request.requestMoreArts(url, StrUtil.getMD5(url), iArticles, CacheMode.ONLY_REQUEST_NETWORK);
	}
	
	// 请求普通文章列表
	public void reqArts(final String columnId, Async.IArticles iArticles, final boolean isLocalCachePriority)
	{
		this.reqArts(columnId, iArticles, isLocalCachePriority && iArticles != null ? CacheMode.CACHE_THEN_REQUEST_NETWORK : CacheMode.REQUEST_NETWORK_BY_CACHE);
	}

	private void reqArts(final String columnId, final Async.IArticles iArticles, final CacheMode mode)
	{
		if (Empty.isEmpty(columnId))
		{
			onCheckFail(iArticles, ECode.E_PARAME, "参数有误");
			return;
		}

		// 第一页的地址
		String url = formatColumnUrl(columnId);
		request.requestArts(url, StrUtil.getMD5(url), iArticles, mode);
	}

	// 请求普通更多文章列表
	public void reqArtsMore(final String columnId, final int page, Async.IArticlesMore iArticles)
	{
		if (Empty.isEmpty(columnId) || page < 0)
		{
			onCheckFail(iArticles, ECode.E_PARAME, "参数有误");
			return;
		}
		String url;
		if (page > 0) // 更多页的地址
		{
			url = formatColumnMoreUrl(columnId, page);
			request.requestMoreArts(url, StrUtil.getMD5(url), iArticles, CacheMode.ONLY_REQUEST_NETWORK);
		}
	}

	// 请求首页文章列表
	public void reqHomeArts(final String columnId, Async.IBlock iBlock, final boolean isLocalCachePriority)
	{
		this.reqHomeArts(columnId, iBlock, isLocalCachePriority && iBlock != null ? CacheMode.CACHE_THEN_REQUEST_NETWORK : CacheMode.REQUEST_NETWORK_BY_CACHE);
	}

	private void reqHomeArts(final String columnId, final Async.IBlock iBlock, final CacheMode mode)
	{
		if (Empty.isEmpty(columnId))
		{
			onCheckFail(iBlock, ECode.E_PARAME, "参数有误");
			return;
		}

		String url = formatColumnUrl(columnId);
		request.requestHomeArts(formatColumnUrl(columnId), StrUtil.getMD5(url), iBlock, mode);
	}

	// 请求首页文章列表
	public void reqHomePostArts(final String columnId, Async.IBlock iBlock, final boolean isLocalCachePriority)
	{
		this.reqHomeArts(columnId, iBlock, isLocalCachePriority && iBlock != null ? CacheMode.CACHE_THEN_REQUEST_NETWORK : CacheMode.REQUEST_NETWORK_BY_CACHE);
	}

	private void reqHomePostArts(final String type, final int page, final Async.IBlock iBlock, final CacheMode mode)
	{
		if (Empty.isEmpty(type))
		{
			onCheckFail(iBlock, ECode.E_PARAME, "参数有误");
			return;
		}

		String url = formatPostColumnUrl(type, page);
		request.requestHomeArts(url, StrUtil.getMD5(url), iBlock, mode);
	}

	// TODO 请求Video栏目中的视频
	public void reqHotVideoFromVideoColumn(final Async.IArticles iArticles)
	{
		if(!Empty.isEmpty(VIDEO))
		{
			String url = formatColumnUrl(VIDEO);
			request.requestArts(url, StrUtil.getMD5(url), iArticles, CacheMode.CACHE_THEN_REQUEST_NETWORK);
		}
		else
		{
			if(iArticles != null)
			{
				iArticles.onStart(null);
				iArticles.onError(ECode.E_URL_ADDRESS, "video 为空", null);
			}
		}
	}

	// TODO 反馈
	public void postFeedback(final String profile, final String name, final String email, final String content, final List<File> lt, final Async.IFeedback iFeedback)
	{
		try
		{

			boolean paramIsOk = false;
			String msg = "";
			long time = System.currentTimeMillis();

			// 顺序按首字母排,首字母相同按下一个字母排
			String value = "content=" + content + "&email=" + email + "&name=" + name + "&profile=" + profile + "&time=" + time + "&version=" + getVersionCode();

			String token = getAESJava().encrypt(value);

			if (Empty.isEmpty(token))
			{
				if (iFeedback != null)
				{
					msg = "加密失败";
				}
			}
			else
			{
				HashMap<String, Object> hm = new HashMap<String, Object>(10);
				hm.put("profile", "" + profile);
				hm.put("content", "" + content);
				hm.put("name", "" + name);
				hm.put("email", "" + email);
				hm.put("version", "" + getVersionCode());
				hm.put("time", "" + time);
				hm.put("token", token);

				String keyFile = "image";
				if (!Empty.isEmpty(lt))
				{
					File file;
					int i1 = 0;
					while (i1 < lt.size())
					{
						if (null != (file = lt.get(i1)))
						{
							hm.put(keyFile + i1 + 1, file);
						}
						++i1;
					}
				}

				request.postFeedback(getProtocolSSL() + getDomainZQ() + URL_POST_FEEDBACK, hm, iFeedback);
				paramIsOk = true;

			}
			if (!paramIsOk)
			{
				onCheckFail(iFeedback, ECode.E_PARAME, msg);
			}
		}
		catch (Exception ext)
		{
			Logger.e("postFeedback() " + ext.toString());
		}
	}
	
	public void reqSearch(final String keyword, final int page, final Async.IArtListSearch iArtList, final Async.ISearchHistory iSearchHistoryList)
	{
		Logger.i("### reqSearch()");
		if(Empty.isEmpty(keyword))
		{
			onCheckFail(iArtList, ECode.E_PARAME, "参数有误");
			return;
		}
		
		String sKey = hz.dodo.StrUtil.zh2url(keyword);
		if(Empty.isEmpty(sKey))
		{
			if(iArtList != null)
			{
				onCheckFail(iArtList, ECode.E_ZH2URL, "转中文地址失败");
			}
			return;
		}
		
		if(saveSearchHistory(keyword))
		{
			if(iSearchHistoryList != null)
			{
				iSearchHistoryList.onSearchHistoryList(getSearchHistory());
			}
		}
		
		String url = formatSearchUrl(keyword, page, 10);
		
		request.reqSearch(url, iArtList, CacheMode.ONLY_REQUEST_NETWORK);
	}
	
	public List<SearchHistory> getSearchHistory()
	{
		return request.getSearchHistory(pathMng.getSearchHistoryFile());
	}
	private boolean saveSearchHistory(final String key)
	{
		return request.saveSearchHistory(key, pathMng.getSearchHistoryFile());
	}
	public boolean removeSearchHistory(final SearchHistory searchHistory)
	{
		return request.removeSearchHistory(searchHistory, pathMng.getSearchHistoryFile());
	}
	public boolean removeAllSearchHistory()
	{
		return request.removeAllSearchHistory(pathMng.getSearchHistoryFile());
	}

	public void reqGuessFavourite(final String columnId, Async.IArticlesMore iArticles)
	{
		// TODO 结构和more页一致(假数据)
		if (Empty.isEmpty(columnId))
		{
			onCheckFail(iArticles, ECode.E_PARAME, "参数有误");
			return;
		}
		int page = 1;
		String url = formatColumnMoreUrl(columnId, page);
		request.requestMoreArts(url, StrUtil.getMD5(url), iArticles, CacheMode.ONLY_REQUEST_NETWORK);

	}
	//TODO 用户验证码登陆注册
	public void reqRegistLoginByCode(final String channel, final String codes, final IUserInfo iReq)
	{
		if (userMng != null)
		{
			userMng.reqRegistLoginByCode(channel, codes, iReq);
		}
	}
	
	//TODO 通过第三方账号登陆
	public void reqNewAccount(final String openid, final String openNick, final int type, final IUserInfo iReq)
	{
		if (userMng != null)
		{
			userMng.reqNewAccount(openid, openNick, type, iReq);
		}
	}

	// 邮箱验证码
	public void reqEmailCode(String email, IResultMsgCode iReq)
	{
		if (userMng != null)
		{
			userMng.reqEmailCode(email, iReq);
		}
	}

	// 手机验证码
	public void reqPhoneCode(String phonenum, IResultMsgCode iReq)
	{
		if (userMng != null)
		{
			userMng.reqPhoneCode(phonenum, iReq);
		}
	}
	// 获取手机或邮箱验证码
	public void reqValidateCode(String channel, IResultMsgCode iReq)
	{
		if (userMng != null)
		{
			userMng.reqValidateCode(channel, iReq);
		}
	}

	//TODO 检查用户是否存在
	public void reqNewUserIsExist(String uName, int type, IUserInfo iReq)
	{
		if (userMng != null)
		{
			userMng.reqNewUserIsExist(uName, type, iReq);
		}
	}

	// 修改密码
	public void reqModifyPsw(String psw, String newPsw, IResultMsgCode iReq)
	{
		if (userMng != null)
		{
			userMng.reqModifyPsw(psw, newPsw, iReq);
		}
	}

	// 修改用户信息
	public void reqModifyInfo(HashMap<String, String> hm, IUserInfo iReq)
	{
		if (userMng != null)
		{
			userMng.reqModifyInfo(hm, iReq);
		}
	}

	// 获取用户信息
	public void reqUserInfo(IUserInfo iReq)
	{
		if (userMng != null)
		{
			userMng.reqUserInfo(iReq);
		}
	}

	// 登录
	public void reqLogin(String uName, String psw, IUserInfo iReq)
	{
		if (userMng != null)
		{
			userMng.reqLogin(uName, psw, iReq);
		}
	}

	// TODO 新判断第三方帐号是否已绑定
	public void reqIsNewBinded(String openid, int type, IUserInfo iReq)
	{
		if (userMng != null)
		{
			userMng.reqIsNewBinded(openid, type, iReq);
		}
	}

	//TODO 新已登录用户绑定第三方账户(通过uid)
	public void reqNewBindAccountByUid(String openId, String openNick, int type, IResultMsgCode iReq)
	{
		if (userMng != null)
		{
			userMng.reqNewBindAccountByUid(openId, openNick, type, iReq);
		}
	}

	//TODO  新第三方登录流程：第三方用户绑定接口（整合放弃账号）
	public void reqBindAbandonedAccount(final String openid, final String channel,final String codes, final int type, final String key, final IUserInfo iReq)
	{
		if (userMng != null)
		{
			userMng.reqBindAccount(channel, openid, codes, key, type, iReq);
		}
	}
	
	//TODO 第三方解绑
	public void reqNewUnBind(String openId, int type, IUserInfo iReq)
	{
		if(userMng != null)
		{
			userMng.reqNewUnBind(openId, type, iReq);
		}
	}
	
	// 兴趣图谱/阅读量
	public void reqBehavior(IBehavior iReq)
	{
		if (userMng != null)
		{
			userMng.reqBehavior(iReq);
		}
	}

	// 找回密码 No.1
	public void reqFindPsw1(String uName, IUserInfo iReq)
	{
		if (userMng != null)
		{
			userMng.reqFindPsw1(uName, iReq);
		}
	}

	// 找回密码 No.2
	public void reqFindPsw2(String uId, String channel, int type, String codes, IUserInfo iReq)
	{
		if (userMng != null)
		{
			userMng.reqFindPsw2(uId, channel, type, codes, iReq);
		}
	}

	// 找回密码 No.3
	public void reqFindPsw3(String uId, String key, String newPsw, IResultMsgCode iReq)
	{
		if (userMng != null)
		{
			userMng.reqFindPsw3(uId, key, newPsw, iReq);
		}
	}

	// 通知自有服务器新增/更新用户信息 (type 1注册 2更新)
	public void reqSyncAccount(int type)
	{
		if (userMng != null)
		{
			userMng.reqSyncAccount(type, getVersionCode(), getAESJava());
		}
	}
	
	//TODO 新通知自有服务器新增/更新用户信息 (type 1注册 2更新)
	public void reqSyncAccount(int type, String channel)
	{
		if (userMng != null)
		{
			userMng.reqSyncAccount(type, channel, getVersionCode(), getAESJava());
		}
	}

	// 上传头像
	public void reqAvatar(int type, File file, String avatarUrl, IResultMsg iReq)
	{
		if (userMng != null)
		{
			userMng.reqAvatar(type, getVersionCode(), file, avatarUrl, getAESJava(), iReq);
		}
	}
	// 获取用户头像地址
	public void reqAvatarUrl(IAvatarUrl iReq)
	{
		if(userMng != null)
		{
			userMng.reqAvatarUrl(getVersionCode(), getAESJava(), iReq);
		}
	}
	// 位置信息
	public void reqAddress(IAddress iReq)
	{
		if (userMng != null)
		{
			userMng.reqAddress(iReq);
		}
	}

	// 获取本地已保存地址信息
	public Address getLocalAddress()
	{
		if (userMng != null)
		{
			return userMng.getLocalAddress();
		}
		return null;
	}

	// 上传用户阅读行为
	public void reqUserAction(HashMap<String, String> hm, IResultMsgCode iReq)
	{
		if (userMng != null)
		{
			userMng.reqUserAction(hm, iReq);
		}
	}
	
	// 新上传用户阅读行为
	public void reqNewUserAction(HashMap<String, String> hm, IResultMsgCode iReq)
	{
		if (userMng != null)
		{
			userMng.reqNewUserAction(hm, iReq);
		}
	}

	public String formatPosts(String type, int page)
	{
		return getProtocolSSL() + getDomainBSL() + String.format(URL_POST, type, page);
	}
	
	/**
	 * 获取MyOrders
	 * http://localhost:8400/comment/api/ordersdetails/?userIdJson=CEFF9E5252EA132AB6C0B4E65CF63CE530B9A110A4A2EB60DEBEAB20C6CFEF317586D2719ECC3AE76B20F5F5FE7F77AB&se=abcdefghi
	 */
	public String formatMyOrders(String userIdJson, String se)
	{
		return getProtocol() + getDomainZQ() + String.format(URL_MYORDERS, userIdJson, se);
	}
	
	/**
	 * 获取DailyTasks
	 * http://192.168.30.111:8400/comment/api/userlevel/?userIdJson=CEFF9E5252EA132AB6C0B4E65CF63CE530B9A110A4A2EB60DEBEAB20C6CFEF317586D2719ECC3AE76B20F5F5FE7F77AB&se=abcdefghi
	 */
	public String formatDailyTasks(String userIdJson, String se)
	{
		return getProtocol() + getDomainZQ() + String.format(URL_DAILYTASKS, userIdJson, se);
	}
	
	/**
	 * 获取Userlevel
	 * http://192.168.30.111:8400/comment/api/userlevel/?userIdJson=CEFF9E5252EA132AB6C0B4E65CF63CE530B9A110A4A2EB60DEBEAB20C6CFEF317586D2719ECC3AE76B20F5F5FE7F77AB&se=abcdefghi
	 */
	public String formatUserLevel(String userIdJson, String se)
	{
		return getProtocol() + getDomainZQ() + String.format(URL_USERLEVEL, userIdJson, se);
	}
	
	/**
	 * http://192.168.11.19:8400/comment/api/points/order
     * 参数说明: payload=加密后的json.stringfy()
	 */
	private String formatCheckStockCoinsUrl()
	{
		return getProtocol() + getDomainZQ() + URL_STOCKCOINS;
	}
	
	/**
	 * http://192.168.11.19:8400/comment/api/points/order
     * 参数说明: payload=加密后的json.stringfy()
	 */
	private String formatOrderUrl()
	{
		return getProtocol() + getDomainZQ() + URL_ORDER;
	}

	
	/**
	 * http://192.168.11.19:8400/comment/api/points/notify
	 * 参数: payload=加密后的json.stringfy()
	 */
	private String formatNotifyUrl()
	{
		return getProtocol() + getDomainZQ() + URL_NOTIFY;
	}

	/**
	 * http://ismp.i-newsroom.top/channels/enapp/columns.json 栏目列表
	 */
	private String formatSourceColumnUrl()
	{
		return getProtocolSSL() + getDomainISMP() + URL_COLUMN;
	}

	private String formatSourceForumUrl()
	{
		return getProtocolSSL() + getDomainBSL() + URL_FORUMS;
	}

	/**
	 * http://ismp.i-newsroom.top/channels/enapp/columns/5943
	 * a694a770d137a603e5e0/stories.json 文章列表数据地址
	 */
	private String formatColumnUrl(String columnId)
	{
		return getProtocolSSL() + getDomainISMP() + String.format(URL_COLUMNS, columnId);
	}

	private String formatPostColumnUrl(String type, int page)
	{
		return getProtocolSSL() + getDomainBSL() + String.format(URL_POST, type, page);
	}

	/**
	 * http://ismp.i-newsroom.top/channels/enapp/columns/5943
	 * a694a770d137a603e5e0/stories/more_0.json 文章列表更多数据地址
	 */
	private String formatColumnMoreUrl(String columnId, int page)
	{
		return getProtocolSSL() + getDomainISMP() + String.format(URL_COLUMNSMORE, columnId, page);
	}

	/**
	 * http://ismp.i-newsroom.top/s/search.json?channel=enapp&keyword=China&page=0&size=10 搜索
	 */
	private String formatSearchUrl(String keyword, int page, int pageSize)
	{
		return getProtocolSSL() + getDomainSearch() + String.format(URL_SEARCH, keyword, page, pageSize);
	}

	
	/**
	 * https://ismp.i-newsroom.top/specials/{special_id}/stories.json
	 * 专题二级列表首页
	 */
	private String formatSpecialUrl(String specialId)
	{
		return getProtocolSSL() + getDomainISMP() + String.format(URL_SPECIAL_SECOND, specialId);
	}

	/**
	 * https://ismp.i-newsroom.top/specials/{special_id}/stories/more_{pageNo}.json
	 * 专题二级列表更多
	 */
	private String formatSpecialMoreUrl(String specialId, int page)
	{
		return getProtocolSSL() + getDomainISMP() + String.format(URL_SPECIAL_SECOND_MORE, specialId, page);
	}
	
	//同步积分到服务器
	public void syncLocalIntegral(final String aeskey, final String sha1, final ISyncIntegral iReq)
	{
		if(integralMng != null)
		{
			integralMng.syncLocalIntegral(aeskey, sha1, iReq);
		}
	}
	
	//客户端本地积分手动调用
	public void addIntegral(String aeskey, String sha1, int integralType, String sArtId, Async.IPostIntegral iReq)
	{
		if(integralMng != null)
		{
			integralMng.addIntegral(aeskey, sha1, integralType, sArtId, iReq);
		}	
	}
	
	public void postIntegralToServer(String aeskey, String sha1, String sUid, IPostIntegral iReq, JSONArray jsonIntegral)
	{
		try
		{
			boolean paramIsOk = false;
			String sError = "";
			if (!Empty.isEmpty(sUid))
			{
				if(jsonIntegral != null)
				{
					if(!Empty.isEmpty(aeskey))
					{
						if(!Empty.isEmpty(sha1))
						{
							String sPayload = JsonParser.putIntegralPayload(jsonIntegral, sha1);
							if(!Empty.isEmpty(sPayload))
							{
								HashMap<String, Object> hm = new HashMap<String, Object>(1);
								hm.put("payload", new AESJava(aeskey).encrypt(sPayload));
								request.postIntegral(getProtocolSSL() + getDomainZQ() + URL_POST_Integral, hm, iReq);
								paramIsOk = true;
							}
							else
							{
								sError = "payload错误";
							}
						}
						else
						{
							sError = "sha1错误";
						}
					}
					else
					{
						sError = "aeskey错误";
					}
				}
				else
				{
					sError = "jsonIntegral数据错误";
				}
			}
			else
			{
				sError = "userId错误";
			}
			if (!paramIsOk)
			{
				onCheckFail(iReq, ECode.E_PARAME, sError);
			}
		}
		catch (Exception ext)
		{
			Logger.e("DataMng postIntegralToServer(): " + ext.toString());
		}
	}
	//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑以上是用户积分代码↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓以下用户积分兑换本地信息代码↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	//保存本地address信息
	public void saveAddressInfo(AddressInfo info)
	{
		if(!Empty.isEmpty(getUId()) && fu != null && pathMng != null)
		{
			if(info != null)
			{
				String sPath = pathMng.getAccountAddressInfo(getUId());
				if(!Empty.isEmpty(sPath))
				{
					fu.write(getAESJava().encrypt(JsonParser.putAddressInfo(info)), sPath);
				}
			}
		}
	}
	//读取本地address信息
	public AddressInfo getAddressInfo()
	{
		if(!Empty.isEmpty(getUId()) && fu != null && pathMng != null)
		{
			String sPath = pathMng.getAccountAddressInfo(getUId());
			if(!Empty.isEmpty(sPath))
			{
				String text = fu.read(sPath);
				if(!Empty.isEmpty(text))
				{
					return JsonParser.getAddressInfo(getAESJava().decrypt(text.trim()));
				}
			}
		}
		return null;
	}
	//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑以上是用户积分兑换本地个人信息代码↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
}
