package cd.s.data;

import java.util.List;

public class Block
{
	/**
	 * uuid : 59642f3882ce383723555e0e
	 * name : 推荐
	 * orderTag : 1
	 * stories : [{"id":"AP5996a40a82cee8af5d33e0f8","title":"77777","updated":"2017-08-21T11:19:34.353+0000","properties":{"leadinLineUrl":null,"link_title":null,"templateTag":null},"columnId":"59476c6b82ce8b8340b7e1b2","author":"","url":"http://newsmedia.neusoft.com:6280/content/AP5996a40a82cee8af5d33e0f8.html","storyType":"COMPO","thumbnailStyle":0,"contentType":0,"publishTime":"2017-08-18T08:22:43.000+0000","summary":"","columnName":"测试栏目","columnDirname":"jinji","jsonUrl":"http://newsmedia.neusoft.com:6280/content/AP5996a40a82cee8af5d33e0f8.json","categories":0,"source":"","channelName":"app测试渠道","thumbnails":[]}]
	 */

	//内容类型
	public static final int BLOCK_NORMAL = 0;//普通
	public static final int BLOCK_BIG_IAMGE = 1;// 大图
	public static final int BLOCK_NO_IAMGE = 2;// 无图
	public static final int BLOCK_THREE_IAMGE = 3;// 三图
	public static final int BLOCK_SCROLL_VIDEO = 4;//横向视频
	public static final int BLOCK_AD = 5;// 广告
	public static final int BLOCK_BANNER_ROLLNEWS = 6;// 滚动新闻
	public static final int BLOCK_BANNER_CAROUSEL = 7;//轮播图
	public static final int BLOCK_NEW_ARTICLE = 10;//文章新显示类型（显示原有）
	public static final int BLOCK_SPECIL_COVER = 11;//二级专题封面类型
	
	public 
	String 
		sUuid,
		sName;
	
	public
	int 
		iOrderTag,
		iPosition,
		iContentStyle;
	
	public
	boolean
		isPopular,
		isBilingualTopBlock;
	
	private List<Article> stories;

	

	public List<Article> getStories()
	{
		return stories;
	}

	public void setStories(List<Article> stories)
	{
		this.stories = stories;
	}
}
