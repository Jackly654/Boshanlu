package cd.s.data;

import java.util.List;

public class Comment extends DataBase
{
	public
	int
		iCommentFlag;
	
	private
	List<CommentItem>
		ltCommentItem;
	
	private 
	User
		user;
	
	private
	ResultMsg
		resultMsg;
	
	public void setUser(final User user)
	{
		this.user = user;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public void setCommentItemList(final List<CommentItem> ltCommentItem)
	{
		this.ltCommentItem = ltCommentItem;
	}
	
	public List<CommentItem> getCommentItemList()
	{
		return ltCommentItem;
	}
	
	public void setResultMsg(final ResultMsg resultMsg)
	{
		this.resultMsg = resultMsg;
	}
	
	public ResultMsg getResultMsg()
	{
		return resultMsg;
	}
}
