package dingtu.ZRoadMap;

import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.Tools.Tools;

public class AuthorizeTools {
	public AuthorizeTools(Context pContext) {

		mContext = pContext;
		// this.OpenWIFI();
		this.GetUserInfoCode();

		try {
			ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(),
					PackageManager.GET_META_DATA);
			String msg = appInfo.metaData.getString("version");
			this.CreateUserInfoList(msg);
			PubVar.currentUserInfo = GetUserInfo();
		} catch (Exception ex) {

		}

		if (this.m_LSUserHandler == null) {
			this.m_LSUserHandler = new Handler();
			this.m_LSUserHandler.postDelayed(this.m_runnable, 1000 * 5);
		}

	}

	// 定制"临时用户"监听器，此监听器的作用是检查当前用户是否为"临时用户"，并给出提示
	private int m_PromptCount = 6; // 表示提醒次数，到0表达需要退出
	private int m_PromptPerTimer = 1000 * 60 * 5 * 1; // 提醒间隔，分钟
	private Handler m_LSUserHandler = null;
	private Context mContext = null;
	private boolean isShowExired = false;
	private Runnable m_runnable = new Runnable() {
		public void run() {

			AuthorizeTools_UserInfo UI = GetUserInfo();

			if (!UI.SYS_UserType.equals("临时用户")) {
				m_LSUserHandler.removeCallbacks(m_runnable);
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sDateFormat.format(new java.util.Date());
				isExpired(date, true);
				return;
			}

			if (m_PromptCount <= 0) {
				String ss = "本次试用时间已到，软件将自动关闭！";
				lkmap.Tools.Tools.ShowMessageBox(mContext, ss, new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						PubVar.m_DoEvent.DoCommand("完全退出");
					}
				});
			} else {
				ShowNoPassMessage();
				m_PromptCount--;
				m_LSUserHandler.postDelayed(this, m_PromptPerTimer);
			}

		}
	};

	// 显示未通过验证后的提示信息
	private void ShowNoPassMessage() {

		try {
			AuthorizeTools_UserInfo UI = this.GetUserInfo();

			// 临时用户提示信息
			String LSMessageStr = "尊敬的【%1$s】：\r\n        您正在使用本软件【%2$s】分钟测试版本，为保证您能使用本软件的全部功能，请联系软件开发者获取正式授权码！\r\n详见【关于系统】！";
			LSMessageStr = String.format(LSMessageStr, UI.SYS_UserType,
					this.m_PromptPerTimer * this.m_PromptCount / (60 * 1000));

			// 授权试用用户
			if (!UI.SYS_UserType.equals("临时用户")) {
				LSMessageStr = "尊敬的【%1$s】：\r\n        您当前的用户类型为【%2$s】，试用期止【%3$s】到期，为保证您能使用本软件的全部功能，请联系软件开发者获取正式授权码！\r\n详见【关于系统】！";
				LSMessageStr = String.format(LSMessageStr, UI.OT_UserName, UI.SYS_UserType, UI.SYS_StopDate);
			}
			lkmap.Tools.Tools.ShowMessageBox(this.mContext, LSMessageStr, null);
		} catch (Exception e) {
			Log.e("NOPass", e.getMessage());
		}

	}

	private void ShowExpiredMessage() {

		if (!isShowExired) {
			try {
				AuthorizeTools_UserInfo UI = this.GetUserInfo();
				stopDate = UI.SYS_StopDate;
				String LSMessageStr = "尊敬的【%1$s】：\r\n       您使用该软件的截止日期是【%3$s】，如果需要继续使用，请联系软件开发者！\r\n详见【关于系统】！";
				LSMessageStr = String.format(LSMessageStr, UI.OT_UserName, UI.SYS_UserType, UI.SYS_StopDate);

				lkmap.Tools.Tools.ShowMessageBox(this.mContext, LSMessageStr, new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {

						// PubVar.m_DoEvent.DoCommand("完全退出");
						if (PubVar.m_DoEvent.AlwaysOpenProject(false)) {
							PubVar.m_Map.setEmpty();
							String[] infoTextList = new String[] { "【系统提示】", "您使用该软件的截止日期是【" + stopDate + "】!",
									"如果需要继续使用，请联系软件开发者！", "详见【关于系统】!" };
							for (int i = 0; i < infoTextList.length; i++) {
								String infoText = infoTextList[i];
								float tw = GetTextFont().measureText(infoText);
								PubVar.m_Map.getDisplayGraphic().drawText(infoText,
										PubVar.m_Map.getDisplayGraphic().getWidth() / 2 - tw / 2,
										PubVar.m_Map.getDisplayGraphic().getHeight() / 2
												+ i * GetTextFont().getTextSize(),
										GetTextFont());
							}
						}
					}
				});
				isShowExired = true;

			} catch (Exception e) {

				Log.e("NOPass", e.getMessage());
			}

		}
	}

	private String stopDate = "";
	// 主要刷新主界面上的GPS状态信息
	private Paint _TextFont = null; // 文字的字体

	private Paint GetTextFont() {
		if (this._TextFont == null) {
			this._TextFont = new Paint();
			this._TextFont.setAntiAlias(true);
			this._TextFont.setTextSize(Tools.SPToPix(26));
			this._TextFont.setColor(Color.WHITE);
			Typeface TF = Typeface.create("宋体", Typeface.BOLD);
			_TextFont.setTypeface(TF);
			// _TextFont.setShadowLayer(20, 0, 0, Color.WHITE);
		}
		return this._TextFont;
	}

	// 是否通过系统认证
	public boolean m_AuthorizePass = true;

	/**
	 * gps时间是否通过验证
	 * 
	 * @param gpsDate
	 * @return
	 */
	public boolean IfDateAuthorizePass(String gpsDate, boolean ShowMessage) {
		if (this.m_AuthorizePass)
			return true;
		AuthorizeTools_UserInfo UI = this.GetUserInfo();
		if (UI == null) {
			if (ShowMessage)
				this.ShowNoPassMessage();
			return false;
		}
		try {
			String[] gpsD = gpsDate.split(" ");
			if (gpsD.length != 2) {
				if (ShowMessage)
					this.ShowNoPassMessage();
				return false;
			}
			SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
			Date StopDate = SDF.parse(UI.SYS_StopDate);
			Date GPSDate = SDF.parse(gpsD[0]);
			if (GPSDate.getTime() >= StopDate.getTime()) {
				if (ShowMessage)
					this.ShowNoPassMessage();
				return false;
			}
		} catch (ParseException e) {
			if (ShowMessage)
				this.ShowNoPassMessage();
			return false;
		}
		return true;
	}

	public boolean isExpired(String Date, boolean showMessage) {
		return isExpired(Date, showMessage, null);
	}

	public boolean isExpired(String Date, boolean showMessage, ICallback pCallback) {
		AuthorizeTools_UserInfo UI = this.GetUserInfo();
		if (UI == null) {
			if (showMessage) {
				this.ShowExpiredMessage();
			}
			m_AuthorizePass = false;
			return false;
		}

		try {
			String[] gpsD = Date.split(" ");
			if (gpsD.length != 2) {
//				if (showMessage)
//					this.ShowExpiredMessage();
				m_AuthorizePass = true;
				return false;
			}
			SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
			Date StopDate = SDF.parse(UI.SYS_StopDate);
			Date GPSDate = SDF.parse(gpsD[0]);
			
			// 防止GPS翻转出现大于2051年的问题
			Date maxDate = SDF.parse("2051-01-02");
			if (GPSDate.getTime() > maxDate.getTime()) {
				GPSDate = new Date();
			}
			String lastDate = "";
			if (PubVar.m_DoEvent == null || PubVar.m_DoEvent.m_UserConfigDB == null) {
				m_AuthorizePass = false;
				return false;
			}
			HashMap<String, String> tagLastDate = PubVar.m_DoEvent.m_UserConfigDB.GetUserParam()
					.GetUserPara("Tag_LastDate");
			if (tagLastDate != null) {
				lastDate = tagLastDate.get("F2") + "";
				if (lastDate != null && lastDate != "") {
					SimpleDateFormat last = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date dblastDate = last.parse(lastDate);
						if (dblastDate.getTime() < GPSDate.getTime()) {
							saveNewLastDate(gpsD[0]);
						} else {
							GPSDate = dblastDate;
						}
					} catch (Exception exx) {

					}
				} else {
					saveNewLastDate(gpsD[0]);
				}
			} else {
				saveNewLastDate(gpsD[0]);
			}

			if (GPSDate.getTime() >= StopDate.getTime()) {
				if (showMessage) {
					this.ShowExpiredMessage();
				}
				m_AuthorizePass = false;
				return false;
			}
			else
			{
				if((GPSDate.getTime()+24*3600*1000)>= StopDate.getTime())
				{
					lkmap.Tools.Tools.ShowMessageBox(this.mContext, "您的授权还有1天过期，请及时联系软件开发者购买授权，以免影响您的使用！");
				}
				else if((GPSDate.getTime()+48*3600*1000)>= StopDate.getTime())
				{
					lkmap.Tools.Tools.ShowMessageBox(this.mContext, "您的授权还有2天过期，请及时联系软件开发者购买授权，以免影响您的使用！");
				}
				
			}
		} catch (ParseException e) {

			m_AuthorizePass = false;
			if (showMessage) {
				this.ShowExpiredMessage();
			}
			return false;
		}

		m_AuthorizePass = true;
		return true;
	}

	private void saveNewLastDate(String dateString) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("F2", dateString);
		PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().SaveUserPara("Tag_LastDate", param);
	}

	/**
	 * 得到当前用户信息
	 * 
	 * @return
	 */
	public AuthorizeTools_UserInfo GetUserInfo() {
		HashMap<String, String> userInfoCode = this.GetUserInfoCode();
		for (AuthorizeTools_UserInfo userInfo : m_UserInfoList) {
			for (String USC : userInfoCode.values()) {
				if (userInfo.SYS_SoftCode.toUpperCase().equals(USC.toUpperCase()))
					return userInfo;
			}
		}
		for (String USC : userInfoCode.values()) {
			if (!USC.equals("")) {
				this.CreateLSUserInfoForMe(USC);
				return this.GetUserInfo();
			}
		}
		return this.GetUserInfo();
	}

	// 改进认证方式，保存已发布的用户码，并对可区分试用用户，还是正式用户
	private List<AuthorizeTools_UserInfo> m_UserInfoList = new ArrayList<AuthorizeTools_UserInfo>();

	private void CreateUserInfoList(String version) {
		this.m_UserInfoList = AuthorizeTools_UserList.CreateUserInfoList(version);
	}

	// 创建"临时使用用户"
	private void CreateLSUserInfoForMe(String SoftCode) {
		// 判断当前用户是否在授权列表内
		HashMap<String, String> userInfoCode = this.GetUserInfoCode();
		for (AuthorizeTools_UserInfo userInfo : this.m_UserInfoList) {
			for (String USC : userInfoCode.values()) {
				if (userInfo.SYS_SoftCode.toUpperCase().equals(USC.toUpperCase()))
					return;
			}
		}

		// 加到用户列表内
		AuthorizeTools_UserInfo userInfo = new AuthorizeTools_UserInfo();
		userInfo.SYS_SoftCode = SoftCode;

		userInfo.OT_UserName = "未授权";
		userInfo.OT_UserUnit = "未知";
		userInfo.OT_UserDepartment = "临时用户";
		userInfo.HardCode = "";
		userInfo.SYS_UserType = "临时用户";
		userInfo.SYS_StopDate = "2050-6-14";

		this.m_UserInfoList.add(userInfo);
	}

	// 通过系统的MAC地址，也就是无线网卡地址，转换而来的用户使用码
	private HashMap<String, String> m_SoftCode = new HashMap<String, String>();

	// 把MAC 地址转换成，用户信息码
	// MAC 格式：402CF45C3212，去掉字节中间的冒号
	private HashMap<String, String> GetUserInfoCode() {
		if (this.m_SoftCode.size() == 0) {

			String MIEIStr = this.GetMIEI();
			if (MIEIStr.length() >= 12) {

				// MIEIStr = MIEIStr.substring(MIEIStr.length() - 12,
				// MIEIStr.length());
				MIEIStr = this.GetUserInfoCodeByHardCode(MIEIStr);
				this.m_SoftCode.put("MIEI", MIEIStr.toUpperCase()); // 手机串号
			} else {

				String strMac = this.GetMacID();
//				if (strMac.equals("020000000000")) {
//					strMac = GetNewVersionMacID();
//				}
				String newID = MIEIStr + strMac;
				if (newID.length() >= 12) {
					this.m_SoftCode.put("MAC", this.GetUserInfoCodeByHardCode(newID.substring(0, 12)).toUpperCase()); // 用MAC地址补齐
				} else {
					this.m_SoftCode.put("MAC", this.GetUserInfoCodeByHardCode(strMac).toUpperCase()); // MAC地址码
				}
			}

		}
		return this.m_SoftCode;
		// if (!this.m_SoftCode.equals("")) return this.m_SoftCode;
		//
		// //以手机串号为主，MAC地址为辅
		// String MACCode = this.GetMIEI();
		// //if (MACCode.equals("")) MACCode = this.GetMacID(); //MAC地址码
		// if (MACCode.equals("")) return "";
		// return GetUserInfoCodeByHardCode(MACCode);

	}

	/**
	 * 通过硬件码反算软件码
	 * 
	 * @param HardCode
	 * @return
	 */
	public String GetUserInfoCodeByHardCode(String HardCode) {
		// 40 2C F4 5C 35 01
		// 用户码：181570-131666-6B601E-1B6E3E
		StringBuilder SB = new StringBuilder();
		SB.append(HardCode.substring(0, HardCode.length() - 4));

		// 把位数的几位移前，防止用户码 看起来重复的情况
		SB.insert(6, HardCode.charAt(HardCode.length() - 4));
		SB.insert(4, HardCode.charAt(HardCode.length() - 3));
		SB.insert(2, HardCode.charAt(HardCode.length() - 2));
		SB.insert(0, HardCode.charAt(HardCode.length() - 1));
		char[] ca = SB.toString().toCharArray();

		String sChEnc = "";
		int len = HardCode.length();
		for (int i = 0; i < len; i++) {
			char a = gsEnc[i];
			char b = ca[i];
			char ch12 = (char) (b ^ a);
			String hexStr = String.format("%02X", (int) ch12);
			sChEnc += hexStr;
			if (i > 0 && i < len - 1 && (i + 1) % 3 == 0) {
				sChEnc += "-";
			}
		}
		return sChEnc;
	}

	// 得到手机串号
	private String GetMIEI() {

		try {
			TelephonyManager telephonemanage = (TelephonyManager) (mContext
					.getSystemService(Context.TELEPHONY_SERVICE));
			String MIEI = telephonemanage.getDeviceId();
			if (MIEI != null && MIEI.length() >= 12) {
				// MIEI = MIEI.substring(MIEI.length() - 12, MIEI.length());
				return MIEI;
			} else
				return "";
		} catch (Exception e) {
			String s = e.getMessage();
			return "";
		}
	}

	// 得到Mac地址
	private String GetMacID() {

		WifiManager wifi = (WifiManager) (mContext.getSystemService(Context.WIFI_SERVICE));
		WifiInfo info = wifi.getConnectionInfo();
		String MacAddress = info.getMacAddress();
		if (MacAddress == null || MacAddress == "")
			return "";
		else {
			this.CloseWIFIByUser();
			return MacAddress.replace(":", "");
		}

	}

	private String GetNewVersionMacID() {
		String address = "02:00:00:00:00:00";
		try {
			OpenWIFI();
			// 把当前机器上的访问网络接口的存入 Enumeration集合中
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			Log.d("TEST_BUG", " interfaceName = " + interfaces);
			while (interfaces.hasMoreElements()) {
				NetworkInterface netWork = interfaces.nextElement();
				// 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
				byte[] by = netWork.getHardwareAddress();
				if (by == null || by.length == 0) {
					continue;
				}
				StringBuilder builder = new StringBuilder();
				for (byte b : by) {
					builder.append(String.format("%02X:", b));
				}
				if (builder.length() > 0) {
					builder.deleteCharAt(builder.length() - 1);
				}
				String mac = builder.toString();
				Log.d("TEST_BUG", "interfaceName=" + netWork.getName() + ", mac=" + mac);
				// 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
				if (netWork.getName().equals("wlan0")) {
					Log.d("TEST_BUG", " interfaceName =" + netWork.getName() + ", mac=" + mac);
					address = mac;
				}

			}

			CloseWIFIByUser();
		} catch (Exception ex) {
			String a = ex.getMessage();
		}

		return address.replace(":", "");
	}

	// 加密串
	private char[] gsEnc = new char[] { ')', '!', '@', '#', '$', '%', '^', '&', '*', '(', '[', '}', '|', '{', '-', ')',
			'(', '&', '^', '@', '#', '$', '%' };
	private char[] gsEnc1 = new char[] { '|', '{', '&', '$', '@', '!', '#', '$', '-', '+', ')', '*' };

	// 打开WIFI管理器
	private boolean m_WIFIEnable = false;

	public void OpenWIFI() {
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

		// 判读WIFI是否可用，如果不可用，为以后的关闭做准备
		this.m_WIFIEnable = wifi.isWifiEnabled();

		// 开启和关闭wifi
		if (!wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(true);
		}
	}

	// 如果之前WIFI为关闭状态，则获取MAC码后，在此关闭WIFI
	private void CloseWIFIByUser() {
		if (!this.m_WIFIEnable) {
			WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			wifi.setWifiEnabled(false);
		}
	}

	/* 此码为用户码加密后的码 */
	private String GetMACByUserInfoCode(String sUserInfoCode) {

		// 用户码 转换 成 MAC
		String ss = sUserInfoCode.replaceAll("-", "");

		byte[] by12 = new byte[12];
		byte[] by12OK = new byte[12];
		for (int i = 0; i < 12; i++) {
			by12[i] = xString2Byte(ss.substring(i * 2, i * 2 + 2));
			by12OK[i] = (byte) (by12[i] ^ (byte) gsEnc1[i]);
		}

		String str = new String(by12OK);

		StringBuilder sb = new StringBuilder();
		sb.append(str);
		String s1, s2, s3, s4;
		s1 = str.substring(0, 1);
		s2 = str.substring(3, 4);
		s3 = str.substring(6, 7);
		s4 = str.substring(9, 10);

		sb.deleteCharAt(9);
		sb.deleteCharAt(6);
		sb.deleteCharAt(3);
		sb.deleteCharAt(0);
		str = sb.toString() + (s4 + s3 + s2 + s1);

		return str;
	}

	private static byte xString2Byte(String str1) {
		byte bytes = (byte) 0;
		byte tmp;
		byte ntmp = 0;

		for (int i = 0; i < 2; i++) {
			int a = str1.charAt(i);

			if (a >= 48 && a <= 57) {
				a = a - 48;
			} else if (a >= 65 && a <= 70) {
				a = a - 55;
			} else if (a >= 97 && a <= 122) {
				a = a - 87;
			} else {
				return 0;
			}
			tmp = (byte) a;

			if (i % 2 == 0)
				ntmp = (byte) (tmp << 4);
			else {
				ntmp = ((byte) (ntmp | tmp));
				bytes = ntmp;
			}
		}
		return bytes;
	}

}
