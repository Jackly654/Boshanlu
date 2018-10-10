package cd.s.data;

import java.util.ArrayList;
import java.util.List;

public class Article extends DataBase
{
	//稿件类型
	public static final String STORYTYPE_COMPO = "COMPO";// 普通稿
	public static final String STORYTYPE_VIDEO = "VIDEO";// 视频稿
	public static final String STORYTYPE_GALLERY = "PHOTO";// 图集稿
	public static final String STORYTYPE_AUDIO = "AUDIO";// 音频稿
	public static final String STORYTYPE_HREF = "HREF";// 链接稿
	
	//内容类型
	public static final int CONTENTTYPE_COMPO = 0;// 图文
	public static final int CONTENTTYPE_GALLERY = 1;// 图集
	public static final int CONTENTTYPE_SPECIAL = 2;// 专题
	public static final int CONTENTTYPE_LIVE = 3;// 直播
	public static final int CONTENTTYPE_AUDIO = 4;// 音频
	public static final int CONTENTTYPE_VIDEO = 5;// 视频
	public static final int CONTENTTYPE_AD = 6;// 广告
	public static final int CONTENTTYPE_RECORDING = 7;// 录播
	
	//缩略图样式
	public static final int THUMBNAILSTYPE_NORMAL = 0;// 普通
	public static final int THUMBNAILSTYPE_BIG = 1;// 大图
	public static final int THUMBNAILSTYPE_THREE = 3;// 三图
	public static final int THUMBNAILSTYPE_NOIMG = 9;// 无图
	
	//block样式
	public static final int BLOCKTYPE_NORMAL = 0;// 普通
	public static final int BLOCKTYPE_TAKETURNS = -1;// 轮播
	public static final int BLOCKTYPE_HORVIDEO = -2;// 滑动视频
	public static final int BLOCKTYPE_HARDAD = -3;// 硬广告
	public static final int BLOCKTYPE_SPECIAL = -4;// 专题
	
	//模板类型
	public static final String TEMPLATETYPE_BILINGUAL = "content-Bi";//双语
	
	public
	String
		sTitle,
		sComment,
		sUpdated,
		sContent,
		sChannelId,
		sColumnId,
		sSpecialId,
		sAuthor,
		sUrl,
		sStoryType,
		sPublishTime,
		sLanguage,
		sTag,
		sSummary,
		sColumnName,
		sSource,
		sOriginUrl,
		sChannelName,
		sJsonUrl,
		sColumnDirname,
		sShareUrl,
		sEditor,
		sAlias,
		sOriginSpecialId,
		sLinkTitle,
		sTemplateType,
		sLiveFlvUrl;
	
	public
	String[]
		sArrTitle;
	
	public
	int
		iThumbnailStyle,
		iContentType,
		iCanComment,
		iWordCount,
		iImageCount,
		iCategories,
		iPadding,
		iPaddingBottom = -1,
		iFont,
		iFontHH,
		iBlockType,
		iPopularSortNumber;
	
	public
	long
		lFavoriteid;
	
	public
	boolean
		bShowColumName;
	
	Media
	media;
	
	List<Image>
		ltPictures;
	
	List<Image>
		ltThumbnails;
	
	public void setMedia(final Media media)
	{
		this.media = media;
	}
	
	public void addThumbnail(final Image image)
	{
		if(image != null)
		{
			if(ltThumbnails == null)
			{
				ltThumbnails = new ArrayList<Image>();
			}
			ltThumbnails.add(image);
		}
	}
	
	public void setThumbnail(final List<Image> ltThumbnails)
	{
		this.ltThumbnails = ltThumbnails;
	}
	
	public void setPicture(final List<Image> ltPictures)
	{
		this.ltPictures = ltPictures;
	}
	
	public void addPicture(final Image image)
	{
		if(image != null)
		{
			if(ltPictures == null)
			{
				ltPictures = new ArrayList<Image>();
			}
			ltPictures.add(image);
		}
	}
	
	public Media getMedia()
	{
		return media;
	}
	
	public List<Image> getThumbnails()
	{
		return ltThumbnails;
	}
	
	public List<Image> getPictures()
	{
		return ltPictures;
	}
}
