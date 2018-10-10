package cd.s.data;

import java.util.HashMap;

public class User extends DataBase
{
	public static final String KEY_NICK = "nickname";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_YEAR = "year";
	public static final String KEY_MONTH = "month";
	public static final String KEY_DAY = "day";
	public static final String KEY_OCCUPATION = "jobIndustry"; // 职业
	public static final String KEY_EDUCATION = "educationlevel"; // 教育程度
	
	public static final int USERTYPE_AUTHOR = 1;//作者回复
	
	//用户等级
	public static final String VIPLEVEL_INITIAL = "Initial";
	public static final String VIPLEVEL_VIP1 = "Vip1";
	public static final String VIPLEVEL_VIP2 = "Vip2";
	public static final String VIPLEVEL_VIP3 = "Vip3";
	public static final String VIPLEVEL_VIP4 = "Vip4";
	public static final String VIPLEVEL_VIP5 = "Vip5";
	public static final String VIPLEVEL_GOLDEN = "Golden";
	public static final String VIPLEVEL_PLATINUM = "Platinum";
	public static final String VIPLEVEL_DIAMOND = "Diamond";
	public static final String VIPLEVEL_CROWN = "Crown";
	
	public
	String
		sUserName,
		sNickname,
		sEmail,
		sRegistDate, // 注册日期
//		sGender, // 性别 1=男 2=女
		sCountry, // 国家
		sProvince, // 省份
		sRealName, // 真实名称
		sEnglishName, // 英文名称
		sEducation, // 学历
		sOccupation, // 职业
		sSchool, // 学校
		sCompany, // 公司
		sMobile, // 手机号码
		sAvatarUrl, // 头像地址
		sFindPswKey, // 找回密码第二步会下发这个key,第三步使用 
		sUnBindUserKey,//解绑最后一个第三方，绑定其他账号发现被占用的用户key
		sLeveName;
	
	public
	int
		iGender, // 性别1=男 2=女
		iUserType;//1(作者回复)
	
	HashMap<String, BindInfo>
		hmBindInfo;
	
	public void setBindInfo(HashMap<String, BindInfo> hm)
	{
		hmBindInfo = hm;
	}
	public HashMap<String, BindInfo> getBindInfo()
	{
		return hmBindInfo;
	}
}
