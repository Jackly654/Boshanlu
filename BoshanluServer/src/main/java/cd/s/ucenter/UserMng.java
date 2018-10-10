package cd.s.ucenter;

import java.io.File;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import hf.http.request.BaseRequest;
import hz.dodo.Logger;
import hz.dodo.PkgMng;
import hz.dodo.StrUtil;
import hz.dodo.data.AESJava;
import hz.dodo.data.Empty;
import cd.s.Async.IAddress;
import cd.s.Async.IAvatarUrl;
import cd.s.Async.IBehavior;
import cd.s.Async.IResultMsg;
import cd.s.Async.IResultMsgCode;
import cd.s.Async.IUserInfo;
import cd.s.DataMng;
import cd.s.RequestUCenter;
import cd.s.data.Address;
import cd.s.data.DR;
import cd.s.data.ECode;
import cd.s.data.JniData;
import cd.s.data.User;
import cd.util.AESMsgOrder;

public class UserMng
{
	public static final int TYPE_UN = 1; // user name
	public static final int TYPE_EMAIL = 2;
	public static final int TYPE_MOBILE = 3;
	
	public static final int OPEN_TYPE_WEIBO = 1;
	public static final int OPEN_TYPE_WX = 2;
	public static final int OPEN_TYPE_QQ = 3;
	public static final int OPEN_TYPE_FACEBOOK = 4;
	public static final int OPEN_TYPE_TWITTER = 5;

	private final int REQ_TYPE;
	
	final String ANDROID = "android"; //标识请求系统
	
	final String URL_REGIST_BY_CODE = "ssomobilenewapi/messagelogin"; // 验证码登陆注册 20180503
	final String URL_EMAIL_CODE = "ssomobileapi/getEmailValidateCode"; // 通过邮箱获取验证码
	final String URL_MOBILE_CODE = "ssomobileapi/getPhoneValidateCode"; // 通过手机获取验证码
	final String URL_VALIDATE_CODE = "ssomobileapi/getValidateCodeByUid"; // 通过uid获取手机或邮箱验证码
	final String URL_NEW_USER_EXIST = "ssomobilenewapi/userIsExist"; // 新查询用户是否存在 20180503
	final String URL_MODIFY_PSW = "ssomobileapi/modifyPassword"; // 修改密码
	final String URL_MODIFY_INFO = "ssomobileapi/modifyUserInfo"; // 修改用户信息
	final String URL_USER_INFO = "ssomobileapi/getUserInfo"; // 获取用户信息
	final String URL_LOGIN = "ssomobileapi/login"; // 登录
	final String URL_BEHAVIOR = "ssomobileapi/staticUser"; // 用户行为(兴趣图谱/阅读趋势)
	final String URL_IS_NEWBINDED = "ssomobilenewapi/thirdIsExist"; // 新判断第三方账户是否已被绑定 20180503
	final String URL_NEW_ACCOUNT = "ssomobilenewapi/initNewAccount"; // 通过第三方登陆 20180503
	final String URL_BIND_ABANDONED_ACCOUNT = "ssomobilenewapi/initThirdAccount"; // 新第三方登录流程：第三方用户绑定接口（整合放弃账号）20180503
	final String URL_FIND_PSW_1 = "ssomobileapi/submitUserName"; // 找回密码时获取UID使用
	final String URL_FIND_PSW_2 = "ssomobileapi/validateUserInfo"; // 找回密码时校验信息
	final String URL_FIND_PSW_3 = "ssomobileapi/resetPassword"; // 设置新密码
	final String URL_NEW_USER_ACTION = "ssomobileapi/clickLog"; // 新上传用户阅读行为 20180503
	final String URL_SYNC_ACCOUNT = "comment/api/userinfo/addChange"; // 通知自有服务器同步用户帐号信息
	final String URL_AVATAR_UPLOAD = "comment/api/userinfo/uploadHeadPortrait"; // 上传头像
	final String URL_AVATAR_GET = "comment/api/userinfo/getHeadPortrait"; // 头像下载
	final String URL_NEW_UNBIND = "ssomobilenewapi/unbundThirdAconunt"; // 新第三方帐号解绑 20180503
	final String URL_NEW_SYNC_ACCOUNT = "comment/api/userinfo/addChangeNew"; // 新通知自有服务器同步用户帐号信息 20180830
	final String URL_UID_NEW_BIND_ACCOUNT = "ssomobilenewapi/bindAccountByUid"; // 新已登录的用户绑定第三方账户 20180503
	
	
//	final String AK_BAIDU_LBS = "842eece3fd0a70544ced64fce26134d4";
//	final String URL_LBS_BAIDU_API = "https://api.map.baidu.com/location/ip?ak=" + AK_BAIDU_LBS + "&mcode=67:ED:0D:81:26:74:4D:1A:2D:50:72:5C:4C:8D:E2:CD:7E:67:4B:EE;hf.lbe&coor=bd09ll";

	Context
		ctx;
	static
	UserMng
		mThis;
	DataMng
		dm;
	RequestUCenter
		req;
	User
		user;
	
	String
		APPKEY, // 中科院
		PROTOCOL,
		PROTOCOL_SSL,
		DOMAIN_U_CENTER, // 中科院服务器域名
		pDomain,
		pDomainZQ, // 志强服务器
		DOMAIN_ZQ;
	
	public static UserMng getInstance(Context ctx, DataMng dm)
	{
		if(mThis == null)
		{
			mThis = new UserMng(ctx, dm);
		}
		return mThis;
	}
	private UserMng(Context ctx, DataMng dm)
	{
		this.ctx = ctx;
		this.dm = dm;
		REQ_TYPE = BaseRequest.METHOD_POST_FORM;
		DOMAIN_ZQ = dm.getDomainZQ();
		getProtocol();
		getPDomain();
		getPDomainZQ();
		req = new RequestUCenter(ctx, dm, dm.getAESJava());
	}
	private String getProtocol()
	{
		try
		{
			if(Empty.isEmpty(PROTOCOL))
			{
				PROTOCOL = JniData.getInstance().getProtocol();
			}
			return PROTOCOL;
		}
		catch(Exception exc)
		{
			Logger.e("UserMng getProtocol() " + exc.toString());
		}
		return null;
	}
	public String getProtocolSSL()
	{
		try
		{
			if(Empty.isEmpty(PROTOCOL_SSL))
			{
				PROTOCOL_SSL = JniData.getInstance().getProtocolSSL();
			}
			return PROTOCOL_SSL;
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("DataMng getProtocol == " + exc.toString());
		}
		return null;
	}
	public String getDomainUCenter()
	{
		try
		{
			if(Empty.isEmpty(DOMAIN_U_CENTER))
			{
				//TODO 新测试地址
				DOMAIN_U_CENTER = dm.isFormalServer ? (DR.CHANNEL_TESTING == PkgMng.getApplicationMetaData(ctx, ctx.getPackageName(), DR.CHANNEL_DEBUG_VALUE)) ? "124.127.180.219:10019/" : JniData.getInstance().getDomainUCenter() : "124.127.180.219:10019/";
			}
			return DOMAIN_U_CENTER;
		}
		catch(Exception exc)
		{
			Logger.e("" + exc.toString());
		}
		return null;
	}
	public String getPDomain()
	{
		if(Empty.isEmpty(pDomain))
		{
			if(!Empty.isEmpty(getProtocol()))
			{
				pDomain = getProtocol() + getDomainUCenter();
			}
		}
		return pDomain;
	}
	public String getPDomainZQ()
	{
		if(Empty.isEmpty(pDomainZQ))
		{
			if(dm == null || dm.isFormalServer)
			{
				if(!Empty.isEmpty(getProtocolSSL()))
				{
					pDomainZQ = getProtocolSSL() + DOMAIN_ZQ;
				}
			}
			else
			{
				if(!Empty.isEmpty(getProtocol()))
				{
					pDomainZQ = getProtocol() + DOMAIN_ZQ;
				}
			}
		}
		return pDomainZQ;
	}
	private String getUId()
	{
		return dm != null ? dm.getUId() : "";
	}
	public String getTimer()
	{
		return StrUtil.formatTime2(System.currentTimeMillis());
	}
	public String getToken(String value)
	{
		if(Empty.isEmpty(APPKEY))
		{
			APPKEY = JniData.getInstance().getAppKeyUCenter();
		}
		return StrUtil.getMD5Upper(value + APPKEY);
	}
	
	/*
	 * 用户验证码注册
	 * uName:用户名由6-16个字符组成,区分大小写,不能为全数字组合,不能包含空格和@符号（先校验账号是否已存在）
	 * channel:手机号码或者邮箱后台自动判读是什么类型（先校验手机号或邮箱是否已存在）
	 *
	 * codes:验证码
	 * isVerify:0 不校验验证码 1 校验验证码
	 */
	//TODO 
	public void reqRegistLoginByCode(final String channel, final String codes, final IUserInfo iReq)
	{
		try
		{
			if(Empty.isEmpty(channel) || Empty.isEmpty(codes))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String token = getToken(channel + time);
			String url = getPDomain() + URL_REGIST_BY_CODE;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(5);
				
					hm.put("channel", "" + channel);
					hm.put("codes", "" + codes);
					hm.put("time", "" + time);
					hm.put("sys_type", "" + ANDROID);
					hm.put("token", "" + token);
					
					req.postLogin(url, hm, iReq);
					
					break;
				default:
					
					url += ("?channel=" + channel + "&codes=" + codes + "&time=" + time + "&sys_type=" + ANDROID + time + "&token=" + token);
					req.reqLogin(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqRegistLoginByCode()" + exc.toString());
		}
	}
	
	
// 获取邮箱验证码
	public void reqEmailCode(String email, IResultMsgCode iReq)
	{
		try
		{
			if(Empty.isEmpty(email))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String token = getToken(time);
			String url = getPDomain() + URL_EMAIL_CODE;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(3);
					hm.put("email", "" + email);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postResultCode(url, hm, iReq);

					break;
				default:
					
					url += ("?email=" + email + "&time=" + time + "&token=" + token);
					req.reqResultCode(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqEmailCode()" + exc.toString());
		}
	}
	// 获取手机验证码
	public void reqPhoneCode(String phonenum, IResultMsgCode iReq)
	{
		try
		{
			if(Empty.isEmpty(phonenum))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String token = getToken(time);
			String url = getPDomain() + URL_MOBILE_CODE;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(3);
					hm.put("phonenum", "" + phonenum);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postResultCode(url, hm, iReq);
					
					break;
				default:
					
					url += ("?phonenum=" + phonenum + "&time=" + time + "&token=" + token);
					req.reqResultCode(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqPhoneCode()" + exc.toString());
		}
	}
	// 获取手机或邮箱验证码
	public void reqValidateCode(String hide_channel, IResultMsgCode iReq)
	{
		try
		{
			if(Empty.isEmpty(hide_channel))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}

			String uId = getUId();
			String time = getTimer();
			String token = getToken(uId + time);
			String url = getPDomain() + URL_VALIDATE_CODE;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(4);
					hm.put("uid", "" + uId);
					hm.put("hide_channel", "" + hide_channel);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postResultCode(url, hm, iReq);
					
					break;
				default:
					
					url += ("?uid=" + uId + "&hide_channel=" + hide_channel + "&time=" + time + "&token=" + token);
					req.reqResultCode(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqPhoneCode()" + exc.toString());
		}
	}
	//TODO  新检查用户是否存在
	public void reqNewUserIsExist(String uName, int type, IUserInfo iReq)
	{
		try
		{
			if(Empty.isEmpty(uName))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String token = getToken(uName + time);
			String url = getPDomain() + URL_NEW_USER_EXIST;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(4);
					hm.put("username", "" + uName);
					hm.put("type", "" + type);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postUserInfo(url, hm, iReq);
					
					break;
				default:
					
					url += ("?username=" + uName + "&type=" + type + "&time=" + time + "&token=" + token);
					req.reqUserInfo(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqNewUserIsExist()" + exc.toString());
		}
	}
// 修改密码
	public void reqModifyPsw(String psw, String newPsw, IResultMsgCode iReq)
	{
		try
		{
			if(Empty.isEmpty(psw) || Empty.isEmpty(newPsw))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String uId = dm.getUId();
			String token = getToken(uId + time);
			String url = getPDomain() + URL_MODIFY_PSW;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(5);
					hm.put("uid", "" + uId);
					hm.put("password", "" + psw);
					hm.put("newPassword", "" + newPsw);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postResultCode(url, hm, iReq);

					break;
				default:
					
					url += ("?uid=" + uId + "&password=" + psw + "&newPassword" + newPsw + "&time=" + time + "&token=" + token);
					req.reqResultCode(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqModifyPsw()" + exc.toString());
		}
	}
	// 修改用户信息
	public void reqModifyInfo(HashMap<String, String> hm, IUserInfo iReq)
	{
		try
		{
			if(Empty.isEmpty(hm))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String uId = getUId();
			String token = getToken(uId + time);
			String url = getPDomain() + URL_MODIFY_INFO;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					hm.put("uid", "" + uId);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postUserInfo(url, hm, iReq);
					
					break;
					
				default:
					
					String urlPart = "";
					String value;
					
					value = hm.get(User.KEY_NICK);
					if(!Empty.isEmpty(value))
					{
						urlPart += ("&nickname=" + StrUtil.zh2url(value));
					}
					value = hm.get(User.KEY_GENDER);
					if(!Empty.isEmpty(value))
					{
						urlPart += ("&gender=" + value);
					}
					value = hm.get(User.KEY_YEAR);
					if(!Empty.isEmpty(value))
					{
						urlPart += ("&year=" + value);
					}
					value = hm.get(User.KEY_MONTH);
					if(!Empty.isEmpty(value))
					{
						urlPart += ("&month=" + value);
					}
					value = hm.get(User.KEY_DAY);
					if(!Empty.isEmpty(value))
					{
						urlPart += ("&day=" + value);
					}
					value = hm.get(User.KEY_OCCUPATION);
					if(!Empty.isEmpty(value))
					{
						urlPart += ("&jobIndustry=" + value);
					}
					value = hm.get(User.KEY_EDUCATION);
					if(!Empty.isEmpty(value))
					{
						urlPart += ("&educationlevel=" + value);
					}
					
					url += ("?uid=" + uId + urlPart + "&time=" + time + "&token=" + token);
					req.reqUserInfo(url, iReq);
					
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqModifyInfo()" + exc.toString());
		}
	}
	// 获取用户信息
	public void reqUserInfo(IUserInfo iReq)
	{
		try
		{
			String time = getTimer();
			String uId =  getUId();
			String token = getToken(uId + time);
			String url = getPDomain() + URL_USER_INFO;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(3);
					hm.put("uid", "" + uId);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postUserInfo(url, hm, iReq);

					break;
				default:
					
					url += ("?uid=" + uId + "&time=" + time + "&token=" + token);
					req.reqUserInfo(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqUserInfo()" + exc.toString());
		}
	}
	//TODO 增加方法校验是否可评论
	
	// 登录
	public void reqLogin(String uName, String psw, IUserInfo iReq)
	{
		try
		{
			if(Empty.isEmpty(uName) || Empty.isEmpty(psw))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String token = getToken(uName + time);
			String url = getPDomain() + URL_LOGIN;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(4);
					hm.put("username", "" + uName);
					hm.put("password", "" + psw);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postLogin(url, hm, iReq);
					
					break;
				default:
					
					url += ("?username=" + uName + "&password=" + psw + "&time=" + time + "&token=" + token);
					req.reqLogin(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqLogin()" + exc.toString());
		}
	}
	// 第三方帐号是否已绑定
	public void reqIsNewBinded(String openId, int type, IUserInfo iReq)
	{
		try
		{
			if(Empty.isEmpty(openId))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String token = getToken(openId + time);
			String url = getPDomain() + URL_IS_NEWBINDED;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(4);
					hm.put("oauth_openid", "" + openId);
					hm.put("time", "" + time);
					hm.put("c_type", "" + type);
					hm.put("token", "" + token);
					
					req.postUserInfo(url, hm, iReq);
					
					break;
				default:
					
					url += ("?oauth_openid=" + openId + "&time=" + time + "&c_type=" + type + "&token=" + token);
					req.reqUserInfo(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqModifyPsw()" + exc.toString());
		}
	}
	//TODO 新已登录用户绑定第三方账户(通过uid)
	public void reqNewBindAccountByUid(String openId, String openNick, int type, IResultMsgCode iReq)
	{
		try
		{
			if(Empty.isEmpty(openId) || Empty.isEmpty(openNick))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String url = getPDomain() + URL_UID_NEW_BIND_ACCOUNT;
			String uId = getUId();
			String time = getTimer();
			String token = getToken(uId + time);
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:

					HashMap<String, Object> hm = new HashMap<>(6);
					hm.put("uid", "" + uId);			
					hm.put("oauth_openid", "" + openId);
					hm.put("oauth_account", "" + openNick);
					hm.put("c_type", "" + type);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postResultCode(url, hm, iReq);

					break;
				default:
					
					url += "?uid=" + uId + "&oauth_openid=" + openId + "&oauth_account=" + StrUtil.zh2url(openNick) + "&c_type=" + type + "&time=" + time + "&token=" + token;
					req.reqResultCode(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqNewBindAccountByUid()" + exc.toString());
		}
	}
	//TODO 第三方帐号解绑
	public void reqNewUnBind(String openId, int type, IUserInfo iReq)
	{
		try
		{
			if(Empty.isEmpty(openId))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}

			String time = getTimer();
			String uId = getUId();
			String token = getToken(uId + openId + time);
			String url = getPDomain() + URL_NEW_UNBIND;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(5);
					
					hm.put("uid", "" + uId);
					hm.put("oauth_openid", "" + openId);
					hm.put("c_type", "" + type);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postUserInfo(url, hm, iReq);
					
					break;
				default:
					
					url += ("?uid=" + uId + "&oauth_openid=" + openId + "&c_type=" + type + "&time=" + time + "&token=" + token);
					req.reqUserInfo(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqNewUnBind()" + exc.toString());
		}
	}
	

	//TODO  通过第三方openid登陆
	// 微博:1;微信:2;QQ:3;facebook:4;twitter:5
	public void reqNewAccount(final String openid, final String openNick, final int type, final IUserInfo iReq)
	{
		reqAccount(getPDomain() + URL_NEW_ACCOUNT, openid, openNick, type, iReq);
	}
	private void reqAccount(final String inUrl, final String openId, final String openNick, final int type, final IUserInfo iReq)
	{
		try
		{
			if(Empty.isEmpty(openId) || Empty.isEmpty(openNick))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String token = getToken(openId + time);
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(6);
				
					hm.put("oauth_openid", "" + openId);
					hm.put("oauth_account", "" + openNick);
					hm.put("c_type", "" + type);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					hm.put("sys_type", "" + ANDROID);
					
					req.postUserInfo(inUrl, hm, iReq);

					break;
				default:
					
					String url = inUrl + "?oauth_openid=" + openId + "&oauth_account=" + StrUtil.zh2url(openNick) + "&c_type=" + type  + "&time=" + time  + "&token=" + token + "&sys_type=" + ANDROID;
					req.reqUserInfo(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqAccount()" + exc.toString());
		}
	}
	//TODO 通过第三方openid绑定(整合放弃原账号)
	public void reqBindAccount(final String channel, final String openid, final String codes, final String key, final int type, final IUserInfo iReq)
	{
		reqBindAbandonedAccount(getPDomain() + URL_BIND_ABANDONED_ACCOUNT, channel, openid, codes, key, type, iReq);
	}
	private void reqBindAbandonedAccount(final String inUrl, final String channel, final String openId, final String codes, final String key, final int type, final IUserInfo iReq)
	{
		try
		{
			if(Empty.isEmpty(channel) || Empty.isEmpty(codes + ""))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String uId = getUId();
			String token = getToken(channel + time);
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(8);
					hm.put("channel", "" + channel);
					hm.put("codes", "" + codes);
					hm.put("key", "" + key);
					hm.put("uid", "" + uId);
					hm.put("oauth_openid", "" + openId);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					hm.put("c_type", "" + type);
					
					req.postUserInfo(inUrl, hm, iReq);

					break;
				default:
					
					String url = inUrl + "?channel=" + channel + "&codes=" + codes + "&key=" + key + "&uid=" + uId +"&oauth_openid=" + openId + "&c_type=" + type + "&time=" + time + "&token=" + token;
					req.reqUserInfo(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqBindAbandonedAccount()" + exc.toString());
		}
	}
	
	// 用户行为(兴趣图谱/阅读趋势)
	public void reqBehavior(IBehavior iReq)
	{
		try
		{
			String uId = getUId();
			String time = getTimer();
			String token = getToken(uId + time);
			String url = getPDomain() + URL_BEHAVIOR;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(3);
					hm.put("uid", "" + uId);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postBehavior(url, hm, iReq);

					break;
				default:
					
					url += ("?uid=" + uId + "&time=" + time + "&token=" + token);
					req.reqBehavior(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqBehavior()" + exc.toString());
		}
	}
	// 找回密码 No.1
	public void reqFindPsw1(String uName, IUserInfo iReq)
	{
		try
		{
			if(Empty.isEmpty(uName))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String token = getToken(uName + time);
			String url = getPDomain() + URL_FIND_PSW_1;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(3);
					hm.put("username", "" + uName);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postUserInfo(url, hm, iReq);

					break;
				default:
					
					url += ("?username=" + uName + "&time=" + time + "&token=" + token);
					req.reqUserInfo(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqFindPsw1()" + exc.toString());
		}
	}
	// 找回密码 No.2
	public void reqFindPsw2(String uId, String channel, int type, String codes, IUserInfo iReq)
	{
		try
		{
			if(Empty.isEmpty(uId) || Empty.isEmpty(channel) || Empty.isEmpty(codes))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String time = getTimer();
			String token = getToken(uId + time);
			String url = getPDomain() + URL_FIND_PSW_2;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(7);
					hm.put("uid", "" + uId);
					hm.put("type", "" + (type == TYPE_MOBILE ? 1 : type)); // 该接口 1:手机号,2:邮箱
					hm.put("channel", "" + channel);
					hm.put("codes", "" + codes);
					hm.put("isVerify", "1");
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postUserInfo(url, hm, iReq);

					break;
				default:
					
					url += ("?uid=" + uId + "&type=" + type + "&channel=" + channel + "&codes=" + codes + "&isVerify=1&time=" + time + "&token=" + token);
					req.reqUserInfo(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqFindPsw2()" + exc.toString());
		}
	}
	// 找回密码 No.3
	// key 为 reqFindPsw2 返回的key
	public void reqFindPsw3(String uId, String key, String newPsw, IResultMsgCode iReq)
	{
		try
		{
			if(Empty.isEmpty(uId) || Empty.isEmpty(key) || Empty.isEmpty(newPsw))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}

			String time = getTimer();
			String token = getToken(uId + time);
			String url = getPDomain() + URL_FIND_PSW_3;
			
			switch(REQ_TYPE)
			{
				case BaseRequest.METHOD_POST_FORM:
					
					HashMap<String, Object> hm = new HashMap<>(6);
					hm.put("uid", "" + uId);
					hm.put("passwordNew", "" + newPsw);
					hm.put("repasswordNew", "" + newPsw);
					hm.put("key", "" + key);
					hm.put("time", "" + time);
					hm.put("token", "" + token);
					
					req.postResultCode(url, hm, iReq);

					break;
				default:
					
					url += ("?uid=" + uId + "&passwordNew=" + newPsw + "&repasswordNew=" + newPsw + "&key=" + key + "&time=" + time + "&token=" + token);
					req.reqResultCode(url, iReq);
					
					break;
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqFindPsw3()" + exc.toString());
		}
	}
	// 获取位置信息
	public void reqAddress(IAddress iReq)
	{
//		req.reqAddress(URL_LBS_BAIDU_API, iReq);
	}
	// 获取本地已保存地址信息
	public Address getLocalAddress()
	{
		if(req != null)
		{
			return req.getLocalAddress();
		}
		return null;
	}
	// 上传用户阅读行为
	public void reqUserAction(HashMap<String, String> hm, IResultMsgCode iReq)
	{
		try
		{
			if(Empty.isEmpty(hm))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String url = getPDomain() + "ssomobileapi/clickLog"; // getPDomain() + URL_USER_ACTION;
			req.postResultCode(url, hm, iReq);
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqUserAction()" + exc.toString());
		}
	}
	
	//TODO 新上传用户阅读行为
	public void reqNewUserAction(HashMap<String, String> hm, IResultMsgCode iReq)
	{
		try
		{
			if(Empty.isEmpty(hm))
			{
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, "参数错误");
				}
				return;
			}
			
			String url = getPDomain() + URL_NEW_USER_ACTION; // getPDomain() + URL_USER_ACTION;
			req.postResultCode(url, hm, iReq);
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			Logger.e("reqNewUserAction()" + exc.toString());
		}
	}
	// 通知自有服务器新增/更新用户信息
	public void reqSyncAccount(int type, int version, AESJava aesJava)
	{
		String uId = getUId();
		if(!Empty.isEmpty(uId))
		{
			long time = System.currentTimeMillis();

			// 顺序按首字母排,首字母相同按下一个字母排
			String value = "time=" + time + "&type=" + type + "&uid=" + uId + "&version=" + version;
			String token = aesJava.encrypt(value);

			if(!Empty.isEmpty(token))
			{
				HashMap<String, Object> hm = new HashMap<String, Object>(6);
				hm.put("platform", "android");
				hm.put("time", "" + time);
				hm.put("type", "" + type);
				hm.put("uid", "" + uId);
				hm.put("version", "" + version);
				hm.put("token", "" + token);

				String url = getPDomainZQ() + URL_SYNC_ACCOUNT;
				req.postBase(url, hm, null);
			}
		}
	}
	
	//TODO 新通知自有服务器新增/更新用户信息
	public void reqSyncAccount(int type, String channel, int version, AESJava aesJava)
	{
		try
		{
			String uId = getUId();
			if(!Empty.isEmpty(uId))
			{
				JSONObject jsonObject = new JSONObject();
				
				jsonObject.put("type", type);
				jsonObject.put("platform", "android");
				jsonObject.put("uid", "" + uId);
				jsonObject.put("channel", channel);
				jsonObject.put("version", "" + version);
				
				AESMsgOrder aesMsgOrder = new AESMsgOrder(dm.getKey());
				
				String sPayload = aesMsgOrder.encrypt(jsonObject.toString());
				if(!Empty.isEmpty(sPayload))
				{
					HashMap<String, Object> hm = new HashMap<String, Object>(1);
					hm.put("payload", "" + sPayload);

					String url = getPDomainZQ() + URL_NEW_SYNC_ACCOUNT;
					req.postBase(url, hm, null);
				}
			}
		}
		catch(Exception exc)
		{
			Logger.e("reqSyncAccount : " + exc.toString());
		}
	}
	
	// 上传头像
	public void reqAvatar(int type, int version, File file, String avatarUrl, AESJava aesJava, IResultMsg iReq)
	{
		String uId = getUId();
		if(!Empty.isEmpty(uId))
		{
			long time = System.currentTimeMillis();

			// 顺序按首字母排,首字母相同按下一个字母排
			String value = "headPortraitType=" + type + "&thirdHeadPortraitUrl=" + (!Empty.isEmpty(avatarUrl) ? avatarUrl : "") + "&time=" + time + "&uid=" + uId + "&version=" + version;
			String token = aesJava.encrypt(value);

			if(!Empty.isEmpty(token))
			{
				HashMap<String, Object> hm = new HashMap<String, Object>(8);
				hm.put("platform", "android");
				hm.put("time", "" + time);
				hm.put("headPortraitType", "" + type);
				hm.put("uid", "" + uId);
				hm.put("version", "" + version);
				hm.put("thirdHeadPortraitUrl", (!Empty.isEmpty(avatarUrl) ? avatarUrl : ""));
				hm.put("token", "" + token);

				switch(type)
				{
					case 0:
						if(file != null)
						{
							hm.put("headPortraitImage", file);
						}
						break;
//					default:
//						if(!Empty.isEmpty(avatarUrl))
//						{
//							hm.put("thirdHeadPortraitUrl", avatarUrl);
//						}
//						break;
				}

				String url = getPDomainZQ() + URL_AVATAR_UPLOAD;
				req.postBase(url, hm, iReq);
			}
		}
		else
		{
			if(iReq != null)
			{
				iReq.onError(ECode.E_UID_NULL, "用户ID为空", null);
			}
		}
	}
	// 请求用户头像地址
	public void reqAvatarUrl(int version, AESJava aesJava, IAvatarUrl iReq)
	{
		String uId = getUId();
		if(!Empty.isEmpty(uId))
		{
			long time = System.currentTimeMillis();

			// 顺序按首字母排,首字母相同按下一个字母排
			String value = "time=" + time + "&uid=" + uId + "&version=" + version;
			String token = aesJava.encrypt(value);

			if(!Empty.isEmpty(token))
			{
				HashMap<String, Object> hm = new HashMap<String, Object>(5);
				hm.put("platform", "android");
				hm.put("time", "" + time);
				hm.put("uid", "" + uId);
				hm.put("version", "" + version);
				hm.put("token", "" + token);

				String url = getPDomainZQ() + URL_AVATAR_GET;
				req.postBase(url, hm, iReq);
			}
		}
		else
		{
			if(iReq != null)
			{
				iReq.onError(ECode.E_UID_NULL, "用户ID为空", null);
			}
		}
	}
}
