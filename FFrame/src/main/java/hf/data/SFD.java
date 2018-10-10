package hf.data;


public class SFD
{
	// gradlew -P market=markets.txt clean archiveApkRelease

	public static final String CHANNEL_NAME = "UMENG_CHANNEL";
	public static final String CHANNEL_DEBUG_VALUE = "DEBUG";

	public static final int S_VRL_RESUME = 1;
	public static final int S_VRL_PAUSE = 2;

	// 黄金分割比例
	public static final float DIVIDER_PERCENT = 0.618f;

	// view
	public static final int V_HOME = 0;

	//动画time
	public static final int ANIM_DURATION = 250;

	// 调用系统图库
	public static final int RESULT_LOAD_IMAGE = 1;
	public static final int RESULT_CLIP_IMAGE = 2; // 需要裁减
	public static final int RESULT_CLIP_HEAD = 3; // 裁减后
	public static final int RESULT_CAMERA = 4; // 拍照后返回

	// orientation
	public static final int OT_NORMAL = 0;
	public static final int OT_R = 1;
	public static final int OT_L = 2;

	// BACK TYPE
	public static final int BACK_TOUCH = 0;
	public static final int BACK_KEY = 1;
	public static final int BACK_AUTO = 2;

	// handler message
	public static final int MSG_CHANGE_VIEW = 0;
	public static final int MSG_PROGRESS = 1;
	public static final int MSG_IMG_DOWNLOAD = 2;

	//分享相关
	public static final int SHARE_WEIXIN = 0;
	public static final int SHARE_WEIXIN_MOMENT = 1;
	public static final int SHARE_FACEBOOK = 2;
	public static final int SHARE_TWITTER = 3;
	public static final int SHARE_SINA = 4;
	public static final int SHARE_SMS = 5;
	public static final int SHARE_EMAIL = 6;

	public static final int SHARE_FROM_NORMAL = 0;
	public static final int SHARE_FROM_APP = 1;

	// SP table name
	public static final String SP_TABLE_CONFIG = "HF_CONFIG";

	public static final String[] sArrAd = {
			"a.baidu.com",
			"api.share.baidu.com",
			"baidutv.baidu.com",
			"bar.baidu.com",
			"c.baidu.com",
			"cbjs.baidu.com",
			"cjhq.baidu.com",
			"cpro.baidu.com",
			"drmcmm.baidu.com",
			"e.baidu.com",
			"eiv.baidu.com",
			"hc.baidu.com",
			"hm.baidu.com",
			"libs.baidu.com",
			"ma.baidu.co",
			"nsclick.baidu.com",
			"pos.baidu.com",
			"spcode.baidu.com",
			"tk.baidu.com",
			"t10.baidu.com",
			"union.baidu.com",
			"ucstat.baidu.com",
			"utility.baidu.com",
			"utk.baidu.com",
			"vie.baidu.com",
			"focusbaiduafp.allyes.com",

			"ubmcmm.baidustatic.com",
			"uumcmm.baidustatic.com",
			"cpro2.baidustatic.com",
			"cpro.baidustatic.com",
			"dup.baidustatic.com",
			"wangmeng.baidu.com",
			"msg.video.qiyi.com",
			"track.cupid.qiyi.com",
			"show.cupid.qiyi.com",
			"msg.iqiyi.com",
			"mixer.cupid.iqiyi.com",
			"pic1.qiyipic.com",

			"images.sohu.com/cs/jsfile/js/c.js",
			"union.sogou.com",
			"sogou.com",

			"s.lianmeng.360.cn",
			"junfu360.com",

			"api.moogos.com",
			"c3.moogos.com",

			"api.dreamfull.cn",
			"log.dreamfull.cn",
			"bu.dreamfull.cn",
			"dreamfull.cn",

			"z4.cnzz.com",
			"z11.cnzz.com",
			"z13.cnzz.com",
			"s4.cnzz.com",
			"s11.cnzz.com",
			"c.cnzz.com",
			"cnzz.mmstat.com",
			"s95.cnzz.com",

			"macromedia.com",
			"wb.7k7k.com",
			"cpro.9xu.com",
			"news.766ba.net",
			"muthoe.com",
			"ad.1111cpc.com",
			"i.g1junfull.com",
			"ad2bus.com",
			"ad2bus.com.cn",
			"ad2bus.cn",
			"dreamfull.cn",
			"jufull.com",
			"log.g1.junfull.com",
			"bu.g1.junfull.com",
			"junfu400.com",
			"junfu400.cn",
			"junful.cn",
			"junfull.cn",
			"junful.com",
			"junfull.com",
			"jf400.cn",
			"quantuba.com",
			"tu2tu.cn",
			"gototu.com",
			"go2tu.com",
			"quantu8.com",
	};
}
