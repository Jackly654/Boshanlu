package cd.s;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cd.s.data.Address;
import cd.s.data.Behavior;
import cd.s.data.BindInfo;
import cd.s.data.Coordinate;
import cd.s.data.Radar;
import cd.s.data.ResultMsgCode;
import cd.s.data.User;
import hf.http.util.Empty;
import hz.dodo.Logger;
import hz.dodo.data.JsonBase;

public class JsonParserUserCenter extends JsonBase
{
	public static ResultMsgCode getResultMsg(String value)
	{
		JSONObject json = s2Json(value);
		if(json != null)
		{
			ResultMsgCode rm = new ResultMsgCode();
			
			rm.iStatus = getInt(json, "status");
			rm.iMsgcode = getInt(json, "code");
			rm.sMsgdesc = getString(json, "msg");
			
			return rm;
		}
		return null;
	}

	public static User getUser(String value)
	{
		JSONObject json = s2Json(value);
		if(json != null)
		{
			JSONObject jUser = s2Json(getString(json, "info"));
			if(jUser != null)
			{
				return getUser(json, jUser);
			}
		}
		return null;
	}
	private static User getUser(JSONObject json, JSONObject jUser)
	{
		if(jUser != null)
		{
			User user = new User();
			
			user.sId = getString(jUser, "uid");
			user.sSchool = getString(jUser, "sso_school");
			user.sUserName = getString(jUser, "username");
			user.sEducation = getString(jUser, "sso_educationlevel");
			user.sOccupation = getString(jUser, "jobIndustry");
			user.sRegistDate = getString(jUser, "regdate");
			user.sProvince = getString(jUser, "province");
			user.iGender = getInt(jUser, "gender");
			user.sCompany = getString(jUser, "sso_company");
			user.sMobile = getString(jUser, "sso_phone");
			user.sNickname = getString(jUser, "nickname");
			user.sRealName = getString(jUser, "sso_realname");
			user.sEnglishName = getString(jUser, "sso_englishname");
			user.sCountry = getString(jUser, "country");
			user.sEmail = getString(jUser, "email");
			user.sAvatarUrl = getString(jUser, "thirdHeadPortraitUrl");
			user.sFindPswKey = getString(json, "key");
			user.sUnBindUserKey = getString(jUser, "key");
			user.setBindInfo(getBindInfo(getString(jUser, "bindThirdInfo")));
			
			return user;
		}
		return null;
	}
	private static BindInfo getBindInfo(JSONObject json)
	{
		if(json != null)
		{
			BindInfo bind = new BindInfo();
			
			bind.sOpenId = getString(json, "openid");
			bind.sAccount = getString(json, "oauth_account");
			bind.iType = getInt(json, "type");
			
			return bind;
		}
		return null;
	}
	public static HashMap<String, BindInfo> getBindInfo(String inJson)
	{
		JSONArray jArr = s2Arr(inJson);
		if(jArr != null && jArr.length() > 0)
		{
			HashMap<String, BindInfo> hm = new HashMap<String, BindInfo>(jArr.length());
			
			BindInfo bind;
			int i1 = 0;
			while(i1 < jArr.length())
			{
				try
				{
					bind = getBindInfo(jArr.getJSONObject(i1));
					if(bind != null)
					{
						hm.put("" + bind.iType, bind);
					}
				}
				catch (Exception exc)
				{
					exc.printStackTrace();
					Logger.e("getBindInfo()" + exc.toString());
				}
				++i1;
			}
			
			return hm;
		}
		return null;
	}
//	public static User getUser1(String value)
//	{
//		JSONObject json = s2Json(value);
//		if(json != null)
//		{
//			User user = new User();
//
//			user.sId = getString(json, "uid");
//			user.sMobile = getString(json, "sso_phone");
//			user.sEmail = getString(json, "email");
//
//			return user;
//		}
//		return null;
//	}
	public static Behavior getBehavior(String value)
	{
		JSONObject json = s2Json(value);
		if(json != null)
		{
			Behavior beha = new Behavior();
			
			beha.setRadar(getListRadar(getString(json, "rader")));
			beha.setCoordinate(getListCoordinate(getString(json, "pageview")));
			
			return beha;
		}
		return null;
	}
	public static List<Radar> getListRadar(String value)
	{
		JSONArray jArr = s2Arr(value);
		if(jArr != null && jArr.length() > 0)
		{
			List<Radar> lt = new ArrayList<Radar>(jArr.length());

			Radar radar;
			int i1 = 0;
			while(i1 < jArr.length())
			{
				try
				{
					radar = getRadar(jArr.getJSONObject(i1));
					if(radar != null)
					{
						lt.add(radar);
					}
				}
				catch(Exception exc)
				{
					exc.printStackTrace();
				}
				++i1;
			}
			
			return lt;
		}
		return null;
	}
	public static Radar getRadar(JSONObject json)
	{
		try
		{
			if(json != null)
			{
				Radar radar = new Radar();
				
				radar.sId = getString(json, "id");
				radar.sName = getString(json, "name");
				
				int max = getInt(json, "max");
				int value = getInt(json, "value");
				
				if(max > 0 && value < max)
				{
					radar.fPercent = (value*1.0f) / (max * 1.0f);
				}
				
				return radar;
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return null;
	}
	public static List<Coordinate> getListCoordinate(String value)
	{
		JSONArray jArr = s2Arr(value);
		if(jArr != null && jArr.length() > 0)
		{
			List<Coordinate> lt = new ArrayList<Coordinate>(jArr.length());

			Coordinate coor;
			int i1 = 0;
			while(i1 < jArr.length())
			{
				try
				{
					coor = getCoordinate(jArr.getJSONObject(i1));
					if(coor != null)
					{
						lt.add(coor);
					}
				}
				catch(Exception exc)
				{
					exc.printStackTrace();
					Logger.e("getListCoordinate()" + exc.toString());
				}
				++i1;
			}
			
			return lt;
		}
		return null;
	}
	public static Coordinate getCoordinate(JSONObject json)
	{
		try
		{
			if(json != null)
			{
				Coordinate coor = new Coordinate();
				
				coor.sId = getString(json, "id");
				coor.iAmount = getInt(json, "value");
				coor.sDate = getString(json, "date");
				
				return coor;
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			Logger.e("getRadar()" + exc.toString());
		}
		return null;
	}
	public static Address getAddress(String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			if(json != null)
			{
				int status = getInt(json, "status");
				if(status == 0)
				{
					Address address = new Address();
					
					String dress = getString(json, "address");
					if(!Empty.isEmpty(dress))
					{
						String[] sArr = dress.split("\\|");
						if(!Empty.isEmpty(sArr) && sArr.length >= 5)
						{
							address.sCountry = sArr[0];
							address.sProvince = sArr[1];
							address.sCity = sArr[2];
							address.sDistrict = sArr[3];
							address.sOperator = sArr[4];
						}
					}
					
					JSONObject jsonContent = s2Json(getString(json, "content"));
					if(jsonContent != null)
					{
						JSONObject jsonPoint = s2Json(getString(jsonContent, "point"));
						if(jsonPoint != null)
						{
							address.sLongitude = getString(jsonPoint, "x");
							address.sLatitude = getString(jsonPoint, "y");
						}
						
						JSONObject jsonDetail = s2Json(getString(jsonContent, "address_detail"));
						if(jsonDetail != null)
						{
							if(Empty.isEmpty(address.sProvince) || "None".equals(address.sProvince))
							{
								address.sProvince = getString(jsonDetail, "province");
							}
							if(Empty.isEmpty(address.sCity) || "None".equals(address.sCity))
							{
								address.sCity = getString(jsonDetail, "city");
							}
							if(Empty.isEmpty(address.sDistrict) || "None".equals(address.sDistrict))
							{
								address.sDistrict = getString(jsonDetail, "district");
							}
							
							address.sStreet = getString(jsonDetail, "street");
							address.sStreetNum = getString(jsonDetail, "street_number");
							address.sId = getString(jsonDetail, "city_code");
						}
					}
					
					return address;
				}
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			Logger.e("getAddress()" + exc.toString());
		}
		return null;
	}
	public static User getAvatarUrl(String inJson)
	{
		return getUser(null, s2Json(inJson));
	}
}
