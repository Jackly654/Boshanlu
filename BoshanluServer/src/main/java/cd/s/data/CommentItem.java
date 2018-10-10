package cd.s.data;

import java.util.List;

public class CommentItem extends DataBase
{
	public
	String
		sComment,
		sCreatetime,
		sArticleId;
	
	public
	long
		lCommentid;
	
	public
	int
		iLikesCnt,
		iLikesFlag; //本人是否点赞过
	
	private
	Article
		article;
	
	private 
	User
		user;
	
	public
	boolean
		bCanShowDelete = true;
	
	private
	List<CommentItem>
		ltCommentItems;
	
	public void setUser(final User user)
	{
		this.user = user;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public void setArticle(final Article article)
	{
		this.article = article;
	}
	
	public Article getArticle()
	{
		return article;
	}
	
	public void setCommentItems(final List<CommentItem>	ltCommentItems)
	{
		this.ltCommentItems = ltCommentItems;
	}
	
	public List<CommentItem> getCommentItems()
	{
		return ltCommentItems;
	}
}
