package cd.s.data;

public class AppInfo extends DataBase
{
	public static final int MSGDESC_SUCCESS = 0;
	
	public static final int COMMENT_NORMAL = 0;
	
	public class Message extends DataBase
	{
		public
		String
			sTitle, // 非强制升级的标题
			sContent,
			sCancelButtonName,
			sLinkButtonName,
			sLink;
		
		public
		String[]
			sArrDisplayContent; // 用于显示
	}
	
	private
	Message
		msg,
		forceMsg;
	
	public
	String
		sPreloaded;
	
	public
	int
		iVersionCode,
		iForceVersion, // 最低版本要求,本地版本低于该值执行强制升级
		iAllCommentFlag;//评论总开关
	
	public void setMessage(Message msg)
	{
		this.msg = msg;
	}
	
	public Message getMessage()
	{
		return msg;
	}
	
	public void setForceMessage(Message forceMsg)
	{
		this.forceMsg = forceMsg;
	}
	
	public Message getForceMessage()
	{
		return forceMsg;
	}
}
