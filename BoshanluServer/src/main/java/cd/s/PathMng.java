package cd.s;

import java.io.File;
import cd.s.data.IntegralInfo;
import hz.dodo.SDCard;
import hz.dodo.data.Empty;
import android.content.Context;

// 路径管理
public class PathMng
{
	public static final String SEPARATOR = File.separator;
	
	private
	static
	PathMng
		mThis;
	
	Context
		ctx;
	
	String
		sAppRootPath,
		sAppRootPathZh,
		sAppRootPathEn,
		sLanguage;

	public static PathMng getInstance(final Context ctx)
	{
		if(mThis == null)
		{
			mThis = new PathMng(ctx);
		}
		return mThis;
	}
	private PathMng(final Context ctx)
	{
		this.ctx = ctx;
		getRootDir();
	}
	public void onDestroy()
	{
		mThis = null;
		sAppRootPath = null;
	}
	public void setLanguage(final String language)
	{
		sLanguage = language;
	}
	// /data/data/files/
	private String getDataFileDir()
	{
		File file = ctx.getFilesDir();
		if(file != null)
		{
			return handlePathEndSeparator(file.getAbsolutePath());
		}
		
		return null;
	}
	private String getExternalDataFileDir()
	{
		File file = ctx.getExternalFilesDir(null);
		if(file != null)
		{
			return handlePathEndSeparator(file.getAbsolutePath());
		}
		
		return "";
	}
	private String handlePathEndSeparator(String path)
	{
		if(!Empty.isEmpty(path))
		{
			if(!path.endsWith(SEPARATOR))
			{
				path += SEPARATOR;
			}
			
			return path;
		}
		
		return path;
	}
	// 项目文件存放根路径
	private String getRootDir()
	{
		if(sAppRootPath == null)
		{
			String path = "";
			
			if(SDCard.checkSdcard())
			{
				path = getExternalDataFileDir();
			}
			else
			{
				path = getDataFileDir();
			}
			
			if(!Empty.isEmpty(path))
			{
				sAppRootPath = path + ".Cd" + SEPARATOR;
			}
		}
		
		return sAppRootPath;
	}
	public String getIntegralDirByuid(String uid)
	{
		String sPath = getDataFileDir();
		return !Empty.isEmpty(sPath) ? sPath + uid + SEPARATOR : null;
	}
	public String getLanguageDir()
	{
		if(DataMng.LANGUAGE_ZH.equals(sLanguage))
		{
			if(sAppRootPathZh == null)
			{
				sAppRootPathZh = getRootDir() + DataMng.LANGUAGE_ZH + SEPARATOR;
			}
			
			return sAppRootPathZh;
		}
		else
		{
			if(sAppRootPathEn == null)
			{
				sAppRootPathEn = getRootDir() + DataMng.LANGUAGE_EN + SEPARATOR;
			}
			
			return sAppRootPathEn;
		}
	}
	public String getTempEventDir()
	{
		String pathLanguage = getLanguageDir();
		return Empty.isEmpty(pathLanguage) ? null : pathLanguage + "tempEvent" + SEPARATOR;
	}  
	
	public String getAccountDir()
	{
		String pathRoot = getRootDir();
		return Empty.isEmpty(pathRoot) ? null : pathRoot + "Account" + SEPARATOR;
	}
	public String getAccountDir(final String uid)
	{
		if(!Empty.isEmpty(uid))
		{
			String pathAccount = getAccountDir();
			return Empty.isEmpty(pathAccount) ? null : pathAccount + uid + SEPARATOR;
		}
		
		return null;
	}
	public String getAccountTempDir(final String uid)
	{
		if(!Empty.isEmpty(uid))
		{
			String pathAccount = getAccountDir(uid);
			return Empty.isEmpty(pathAccount) ? null : pathAccount + "Temp" + SEPARATOR;
		}
		
		return null;
	}
	public String getAccountFavoriteTempDir(final String uid)
	{
		if(!Empty.isEmpty(uid))
		{
			String pathTemp = getAccountTempDir(uid);
			return Empty.isEmpty(pathTemp) ? null : pathTemp + "FavoriteTemp" + SEPARATOR;
		}
		return null;
	}
	public String getAccountIntegralFilePath(final String uid, int iIntegralType)
	{
		if(!Empty.isEmpty(uid))
		{
			String path = getAccountIntegralDir(uid);
			if(!Empty.isEmpty(path))
			{
				switch(iIntegralType)
				{
					case IntegralInfo.INTEGRALTYPE_READ:
						return path + "read";
					case IntegralInfo.INTEGRALTYPE_SHARE:
						return path + "share";
					case IntegralInfo.INTEGRALTYPE_AUDIO:
						return path + "audio";
					case IntegralInfo.INTEGRALTYPE_LIKE:
						return path + "like";
				}
			}
		}
		return null;
	}
	public String getAccountIntegralResultFilePath(final String uid, int iIntegralType)
	{
		if(!Empty.isEmpty(uid))
		{
			String path = getAccountIntegralDir(uid);
			if(!Empty.isEmpty(path))
			{
				switch(iIntegralType)
				{
					case IntegralInfo.INTEGRALTYPE_READ:
						return path + "read_result";
					case IntegralInfo.INTEGRALTYPE_SHARE:
						return path + "share_result";
					case IntegralInfo.INTEGRALTYPE_AUDIO:
						return path + "audio_result";
					case IntegralInfo.INTEGRALTYPE_LIKE:
						return path + "like_result";
				}
			}
		}
		return null;
	}
	//获取用户积分文件夹
	public String getAccountIntegralDir(final String uid)
	{
		if(!Empty.isEmpty(uid))
		{
			String pathTemp = getIntegralDirByuid(uid);
			return Empty.isEmpty(pathTemp) ? null : pathTemp + "Integral" + SEPARATOR;
		}
		return null;
	}
	//获取积分兑换礼品本地Address信息文件
	public String getAccountAddressInfo(final String uid)
	{
		if(!Empty.isEmpty(uid))
		{
			String pathTemp = getAccountDir(uid);
			return Empty.isEmpty(pathTemp) ? null : pathTemp + "AddressInfo";
		}
		return null;
	}
	//图集推荐
	public String getAccountPhotoRecommendDir(final String uid)
	{
		if(!Empty.isEmpty(uid))
		{
			String pathTemp = getAccountDir(uid);
			return Empty.isEmpty(pathTemp) ? null : pathTemp + "PhotoRecommend" + SEPARATOR;
		}
		
		return null;
	}
	// 详情文件夹
	public String getArticleDir()
	{
		String pathLanguage = getLanguageDir();
		return Empty.isEmpty(pathLanguage) ? null : pathLanguage + "Articles" + SEPARATOR;
	}
	// 详情相关新闻文件夹
	public String getArticleRelatedDir()
	{
		String pathLanguage = getLanguageDir();
		return Empty.isEmpty(pathLanguage) ? null : pathLanguage + "ArticleRelated" + SEPARATOR;
	}
	// 图片存放路径
	public String getAccountImgDir()
	{
		String pathLanguage = getLanguageDir();
		return Empty.isEmpty(pathLanguage) ? null : "Img" + SEPARATOR;
	}
	public String getImgDir()
	{
		String pathLanguage = getLanguageDir();
		return Empty.isEmpty(pathLanguage) ? null : pathLanguage + "Img" + SEPARATOR;
	}
	public String getTempImgDir()
	{
		String root = getRootDir();
		return Empty.isEmpty(root) ? null : root + "Temp" + SEPARATOR;
	}
	
	//列表存放路径
	public String getAccountColumnDir(final String uid)
	{
		String pathAccount = getAccountDir(uid);
		return Empty.isEmpty(pathAccount) ? null : pathAccount + "Columns";
	}
	
	//消息文件夹最后修改时间存放目录
	public String getNotifiyLastTime(final String uid)
	{
		String pathAccount = getAccountDir(uid);
		return Empty.isEmpty(pathAccount) ? null : pathAccount + "NotifiyLastModifyTime" + SEPARATOR + "lastTime";
	}
	//用户消息responseTime存放路径
	public String getNotifiyResponseTimeDir(final String uid)
	{
		return getNotifyRootDir(uid) + "responseTime";
	}
	
	//用户消息存放路径
	public String getMsgNotifiyJsonDir(final String uid)
	{
		return getNotifyRootDir(uid) + "MsgNotifiy";
	}
	
	//用户红点消息存放路径
	public String getMsgRedPointJsonDir(final String uid)
	{
		return getNotifyRootDir(uid) + "MsgRedPoint";
	}
		
	//用户消息存放根路径
	public String getNotifyRootDir(final String uid)
	{
		String pathAccount = getAccountDir(uid);
		return Empty.isEmpty(pathAccount) ? null : pathAccount + "Notification" + SEPARATOR;
	}
		
	//文章列表存放路径
	public String getArticleListDir()
	{
		String pathLanguage = getLanguageDir();
		return Empty.isEmpty(pathLanguage) ? null : pathLanguage + "ArticleList" + SEPARATOR;
	}
	private String getAdDir()
	{
		String pathLanguage = getLanguageDir();
		return Empty.isEmpty(pathLanguage) ? null : pathLanguage + "Ad" + SEPARATOR;
	}
	// 广告文件
	public String getAdFile()
	{
		return Empty.isEmpty(getAdDir()) ? null : getAdDir() + "adJson";
	}
	// 广告文件文件夹
	public String getAdDataFile()
	{
		return Empty.isEmpty(getAdDir()) ? null : getAdDir() + "File" + SEPARATOR;
	}
	// 文件存放路径
	public String getFileDir()
	{
		return Empty.isEmpty(getLanguageDir()) ? null : getLanguageDir() + "File" + SEPARATOR;
	}

	// 获取用户搜索历史记录的文件路径
	public String getSearchHistoryFile()
	{
		return Empty.isEmpty(getFileDir()) ? null : getFileDir() + "Search" + SEPARATOR + "searchhistory";
	}
	// for you
	public String getForYouFileDir(final String uid)
	{
		String pathAccount = getAccountDir(uid);
		return Empty.isEmpty(pathAccount) ? null : pathAccount + "forYou";
	}
	
	//用户主动保存图片路径
	public String getSaveImgFileDir()
	{
		String sdcardPath = SDCard.getSDCardRootPath(ctx);
		return !Empty.isEmpty(sdcardPath) ? sdcardPath + SEPARATOR + "hf" + SEPARATOR + "cd" + SEPARATOR + "pic" + SEPARATOR : null;
	}
}
