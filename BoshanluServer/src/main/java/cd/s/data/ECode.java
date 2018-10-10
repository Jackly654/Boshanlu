package cd.s.data;

// 错误码
public class ECode
{
	public static final int E_NET_DISCONNECT = -101; // 网络未连接,无法请求
	public static final int E_NET_ERROR = -102; // 网络错误
	public static final int E_INPUTSTREAM = -103; // 网络返回流出错
	public static final int E_URL_ADDRESS = -104; // 无效URL
	public static final int E_URL_CDN_ADDRESS = -105; // 无效的CDN URL
	public static final int E_PARAME = -106; // 参数有误
	public static final int E_IMG_LOCAL_DIR = -107; // 本地图片存放路径出错
	public static final int E_ZH2URL = -108; // 转中文地址失败
	public static final int E_LOAD_LOCAL_ARTICLE = -109; // 获取本地文章失败
	public static final int E_LOAD_LOCAL = -110; // 获取本地数据失败
	public static final int E_UID_NULL = -111; // 用户id为空
}