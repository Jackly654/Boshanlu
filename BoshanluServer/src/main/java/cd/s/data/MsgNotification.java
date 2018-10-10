package cd.s.data;

public class MsgNotification 
{
	public static final String START_TIME = "0";

	public 
	ResultMsg
		msgResult;
	
	public 
	int
		iTotalCoins,
		iNewAuthorReplyFlag,
		iNewPointsFlag;
	
	public
	String
		sResponseTime,
		sLevelName;
	
	public void setResultMsgCode(ResultMsg resultMsg)
	{
		this.msgResult = resultMsg;
	}
}
