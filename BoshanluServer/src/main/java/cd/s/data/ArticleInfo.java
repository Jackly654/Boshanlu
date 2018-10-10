package cd.s.data;

import java.util.List;

public class ArticleInfo extends DataBase
{
	public
	int
		iLikesFlag, //本人是否点赞过
		iFavoriteFlag,//本人是否抽藏过
		iCommentFlag,
		iLikesCnt,
		iCommentsCnt;
	
	private
	List<CommentItem>
		ltCommentItem;
	
	public void setCommentItemList(final List<CommentItem> ltCommentItem)
	{
		this.ltCommentItem = ltCommentItem;
	}
	
	public List<CommentItem> getCommentItemList()
	{
		return ltCommentItem;
	}
}
