package cd.s.data;

public class ResultMsg extends DataBase
{
	public static final int CODE_SUCCESS = 0;
	public static final int CODE_PARAMETER_IS_NULL = 100;//参数为空
	public static final int CODE_PARAMETER_ERROE = 101;//参数错误
	public static final int CODE_DECRYPT_ERROR = 200;//解密后数据不匹配
	public static final int CODE_REPEAT_LIKE = 300;//重复点赞
	public static final int CODE_REPEAT_FAVORITE = 310;//重复收藏
	public static final int CODE_SYSTEM_COMMENT_OFF = 320;//系统禁止评论
	public static final int CODE_REPEAT_ARTICLE_OFF = 321;//文章禁止评论
	public static final int CODE_COMMENT_LIMITED = 322;//用户评论超过30秒,内容为具体的毫秒数
	public static final int CODE_ARTICLE_NOTFOUND = 404;//没有发现article
	public static final int CODE_USERID_NOTFOUND = 414;//没有发现userid(sso系统)
	public static final int CODE_SYSTEM_ERROE = 500;//内部错误
	public static final int CODE_CONNECT_ISMP_FAILE = 501;//连接ismp失败
	public static final int CODE_CONNECT_SSO_FAILE = 511;//连接sso失败(连接失败、!=200, 其他错误)
	public static final int CODE_DELETE_PARTFAVORITES_FAILE = 6301;//删除部分指定收藏列表(删除失败.)
	public static final int CODE_DELETE_FAVORITES_FAILE = 6302;//指定收藏列表都未删除.(删除失败.)
	public static final int CODE_DELETE_PART = 6301;  //删除部分数据
	public static final int CODE_DELETE_NONE = 6302;  //一条都没删除
	
	public
	String
		sMsgdesc;//结果描述
	
	public
	int
		iMsgcode;//结果返回码
}
