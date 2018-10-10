package cd.s;

import java.util.List;
import cd.s.data.AdInfo;
import cd.s.data.Address;
import cd.s.data.AppInfo;
import cd.s.data.Article;
import cd.s.data.ArticleInfo;
import cd.s.data.Behavior;
import cd.s.data.Block;
import cd.s.data.StockCoinsResult;
import cd.s.data.Column;
import cd.s.data.Comment;
import cd.s.data.CommentDelete;
import cd.s.data.CommentLike;
import cd.s.data.FavoriteDelete;
import cd.s.data.IntegralResult;
import cd.s.data.MsgNotification;
import cd.s.data.Order;
import cd.s.data.PhotoRecommand;
import cd.s.data.ResultMsg;
import cd.s.data.ResultMsgCode;
import cd.s.data.SearchHistory;
import cd.s.data.ServiceTime;
import cd.s.data.Translate;
import cd.s.data.User;

public class Async
{
	public interface IBase
	{
		public void onStart(final Object tag);
		public void onError(final int code, final String msg, final Object tag);
	}
	public interface IString extends IBase
	{
		public void onResponse(final String value, final Object tag);
	}
	public interface IArticle extends IBase
	{
		public void onArticle(final Article article, final boolean isCache, final Object tag);
	}
	public interface IAppInfo extends IBase
	{
		public void onAppInfo(final AppInfo appInfo, final Object tag);
	}
	public interface IResultMsg extends IBase
	{
		public void onResultMsg(final ResultMsg resultMsg, final Object tag);
	}
	public interface IPostCancelFavorites extends IBase
	{
		public void onPostCancelFavorites(final FavoriteDelete favoriteDelete, final Object tag);
	}
	public interface IPostIntegral extends IBase
	{
		public void onPostIntegral(final IntegralResult interalResult, final Object tag);
	}
	public interface ISyncIntegral extends IBase
	{
		public void onFinished(boolean isSuccess);
	}
	public interface ICommentList extends IBase
	{
		public void onCommentList(final Comment comment, final Object tag);
	}
	public interface ICommentDelete extends IBase
	{
		public void onCommentDelete(final CommentDelete commentDelete, final Object tag);
	}
	public interface ICommentLike extends IBase
	{
		public void onCommentLike(final CommentLike commentLike, final Object tag);
	}
	public interface IServiceTime extends IBase
	{
		public void onServiceTime(final ServiceTime serviceTime, final Object tag);
	}
	public interface IAd extends IBase
	{
		public void onAd(final AdInfo adInfo, final boolean isCache, final Object tag);
	}
	public interface IArticleInfo extends IBase
	{
		public void onArticleInfo(final ArticleInfo articleInfo, final Object tag);
	}
	public interface IRecommend extends IBase
	{
		public void onIRecommend(final List<Article> articles, final boolean isCache, final Object tag);
	}
	public interface IPhotoRecommand extends IBase
	{
		public void onPhotoRecommand(final PhotoRecommand photoRecommand, final boolean isCache, final Object tag);
	}
	// 检查库存
	public interface ICheckStockCoins extends IBase
	{
		public void onCheckStockCoins(final StockCoinsResult checkSC, final Object tag);
	}
	// 订单提交
	public interface IOrder extends IBase
	{
		public void onOrder(final Order order, final Object tag);
	}
	// 消息提示
	public interface INotify extends IBase
	{
		public void onNotification(final MsgNotification msgNotification, final boolean isCache, final Object tag);
	}
	//翻译
	public interface ITranslate extends IBase
	{
		public void onTranslate(final Translate translate, final Object tag);
	}
	// 图片下载
	public interface IImage extends IBase
	{
		public void onImg(final String url, final String path, final Object tag);
	}
	//IO下载
	public interface IIO extends IBase
	{
		public void onIO(final String url, final String path, final Object tag);
	}
	//我的收藏
	public interface IPostMyFavorites extends IBase
	{
		public void onPostMyFavorites(final List<Article> ltArts, final boolean isCache, final Object tag);
	}
	/**
	 * 栏目列表
	 */

	public interface IColumn extends IBase
	{
		void onColumn(final List<Column> columnList, final boolean isCache, final Object tag);
	}

	/**
	 * 首页文章列表
	 */

	public interface IBlock extends IBase
	{
		void onBlock(final List<Block> blockList, final boolean isCache, final Object tag);
	}

	/**
	 * 普通文章列表
	 */

	public interface IArticles extends IBase
	{
		void onArticles(final List<Article> articleList, final boolean isCache, final Object tag);
	}

	/**
	 * 二级special列表
	 */

	public interface IDataObjs extends IBase
	{
		void onDataObjs(final List<Object> list, final boolean isCache, final Object tag);
	}


	/**
	 * 普通文章更多列表
	 */

	public interface IArticlesMore extends IBase
	{
		void onArticlesMore(final List<Article> articleList, final boolean isCache, final Object tag);
	}
	
	/*
	 * 上传点赞、收藏
	 */
	public interface IResultMsgIncludeUid extends IBase
	{
		void onResultMsg(final ResultMsg resultMsg, final Object tag);
	}
	
	/**
	 *	反馈
	 */
	public interface IFeedback extends IBase
	{
		public void onPostFeedback(final ResultMsg resultMsg, final Object tag);
	}
	
	/**
	 *	搜索
	 */
	public interface IArtListSearch extends IBase
	{
		void onSearchList(final List<Article> searchContents, final Object tag);
	}
	
	/**
	 *	搜索历史
	 */
	public interface ISearchHistory extends IBase
	{
		public void onSearchHistoryList(final List<SearchHistory> lt);
	}	
	
	/*
	 * 验证码
	 */
	public interface IResultMsgCode extends IBase
	{
		public void onResultMsg(final ResultMsgCode rm, final Object tag);
	}
	public interface IHold extends IBase
	{
		public boolean isHold();
	}
	/*
	 * 用户信息
	 */
	public interface IUserInfo extends IHold
	{
		public void onUserInfo(final ResultMsgCode rm, final User user, final Object tag);
	}
	/*
	 * 用户头像地址
	 */
	public interface IAvatarUrl extends IBase
	{
		public void onAvatarUrl(final String uId, final String url, final Object tag);
	}
	/*
	 * 用户行为(兴趣图谱/阅读趋势)
	 */
	public interface IBehavior extends IHold
	{
		public void onBehavior(final Behavior behavior, final Object tag);
	}
	/*
	 * LBS
	 */
	public interface IAddress extends IBase
	{
		public void onAddress(final Address address, final Object tag);
	}
}
