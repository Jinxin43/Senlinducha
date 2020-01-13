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

	// ����"��ʱ�û�"���������˼������������Ǽ�鵱ǰ�û��Ƿ�Ϊ"��ʱ�û�"����������ʾ
	private int m_PromptCount = 6; // ��ʾ���Ѵ�������0�����Ҫ�˳�
	private int m_PromptPerTimer = 1000 * 60 * 5 * 1; // ���Ѽ��������
	private Handler m_LSUserHandler = null;
	private Context mContext = null;
	private boolean isShowExired = false;
	private Runnable m_runnable = new Runnable() {
		public void run() {

			AuthorizeTools_UserInfo UI = GetUserInfo();

			if (!UI.SYS_UserType.equals("��ʱ�û�")) {
				m_LSUserHandler.removeCallbacks(m_runnable);
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sDateFormat.format(new java.util.Date());
				isExpired(date, true);
				return;
			}

			if (m_PromptCount <= 0) {
				String ss = "��������ʱ���ѵ���������Զ��رգ�";
				lkmap.Tools.Tools.ShowMessageBox(mContext, ss, new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						PubVar.m_DoEvent.DoCommand("��ȫ�˳�");
					}
				});
			} else {
				ShowNoPassMessage();
				m_PromptCount--;
				m_LSUserHandler.postDelayed(this, m_PromptPerTimer);
			}

		}
	};

	// ��ʾδͨ����֤�����ʾ��Ϣ
	private void ShowNoPassMessage() {

		try {
			AuthorizeTools_UserInfo UI = this.GetUserInfo();

			// ��ʱ�û���ʾ��Ϣ
			String LSMessageStr = "�𾴵ġ�%1$s����\r\n        ������ʹ�ñ������%2$s�����Ӳ��԰汾��Ϊ��֤����ʹ�ñ������ȫ�����ܣ�����ϵ��������߻�ȡ��ʽ��Ȩ�룡\r\n���������ϵͳ����";
			LSMessageStr = String.format(LSMessageStr, UI.SYS_UserType,
					this.m_PromptPerTimer * this.m_PromptCount / (60 * 1000));

			// ��Ȩ�����û�
			if (!UI.SYS_UserType.equals("��ʱ�û�")) {
				LSMessageStr = "�𾴵ġ�%1$s����\r\n        ����ǰ���û�����Ϊ��%2$s����������ֹ��%3$s�����ڣ�Ϊ��֤����ʹ�ñ������ȫ�����ܣ�����ϵ��������߻�ȡ��ʽ��Ȩ�룡\r\n���������ϵͳ����";
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
				String LSMessageStr = "�𾴵ġ�%1$s����\r\n       ��ʹ�ø�����Ľ�ֹ�����ǡ�%3$s���������Ҫ����ʹ�ã�����ϵ��������ߣ�\r\n���������ϵͳ����";
				LSMessageStr = String.format(LSMessageStr, UI.OT_UserName, UI.SYS_UserType, UI.SYS_StopDate);

				lkmap.Tools.Tools.ShowMessageBox(this.mContext, LSMessageStr, new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {

						// PubVar.m_DoEvent.DoCommand("��ȫ�˳�");
						if (PubVar.m_DoEvent.AlwaysOpenProject(false)) {
							PubVar.m_Map.setEmpty();
							String[] infoTextList = new String[] { "��ϵͳ��ʾ��", "��ʹ�ø�����Ľ�ֹ�����ǡ�" + stopDate + "��!",
									"�����Ҫ����ʹ�ã�����ϵ��������ߣ�", "���������ϵͳ��!" };
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
	// ��Ҫˢ���������ϵ�GPS״̬��Ϣ
	private Paint _TextFont = null; // ���ֵ�����

	private Paint GetTextFont() {
		if (this._TextFont == null) {
			this._TextFont = new Paint();
			this._TextFont.setAntiAlias(true);
			this._TextFont.setTextSize(Tools.SPToPix(26));
			this._TextFont.setColor(Color.WHITE);
			Typeface TF = Typeface.create("����", Typeface.BOLD);
			_TextFont.setTypeface(TF);
			// _TextFont.setShadowLayer(20, 0, 0, Color.WHITE);
		}
		return this._TextFont;
	}

	// �Ƿ�ͨ��ϵͳ��֤
	public boolean m_AuthorizePass = true;

	/**
	 * gpsʱ���Ƿ�ͨ����֤
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
			
			// ��ֹGPS��ת���ִ���2051�������
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
					lkmap.Tools.Tools.ShowMessageBox(this.mContext, "������Ȩ����1����ڣ��뼰ʱ��ϵ��������߹�����Ȩ������Ӱ������ʹ�ã�");
				}
				else if((GPSDate.getTime()+48*3600*1000)>= StopDate.getTime())
				{
					lkmap.Tools.Tools.ShowMessageBox(this.mContext, "������Ȩ����2����ڣ��뼰ʱ��ϵ��������߹�����Ȩ������Ӱ������ʹ�ã�");
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
	 * �õ���ǰ�û���Ϣ
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

	// �Ľ���֤��ʽ�������ѷ������û��룬���Կ����������û���������ʽ�û�
	private List<AuthorizeTools_UserInfo> m_UserInfoList = new ArrayList<AuthorizeTools_UserInfo>();

	private void CreateUserInfoList(String version) {
		this.m_UserInfoList = AuthorizeTools_UserList.CreateUserInfoList(version);
	}

	// ����"��ʱʹ���û�"
	private void CreateLSUserInfoForMe(String SoftCode) {
		// �жϵ�ǰ�û��Ƿ�����Ȩ�б���
		HashMap<String, String> userInfoCode = this.GetUserInfoCode();
		for (AuthorizeTools_UserInfo userInfo : this.m_UserInfoList) {
			for (String USC : userInfoCode.values()) {
				if (userInfo.SYS_SoftCode.toUpperCase().equals(USC.toUpperCase()))
					return;
			}
		}

		// �ӵ��û��б���
		AuthorizeTools_UserInfo userInfo = new AuthorizeTools_UserInfo();
		userInfo.SYS_SoftCode = SoftCode;

		userInfo.OT_UserName = "δ��Ȩ";
		userInfo.OT_UserUnit = "δ֪";
		userInfo.OT_UserDepartment = "��ʱ�û�";
		userInfo.HardCode = "";
		userInfo.SYS_UserType = "��ʱ�û�";
		userInfo.SYS_StopDate = "2050-6-14";

		this.m_UserInfoList.add(userInfo);
	}

	// ͨ��ϵͳ��MAC��ַ��Ҳ��������������ַ��ת���������û�ʹ����
	private HashMap<String, String> m_SoftCode = new HashMap<String, String>();

	// ��MAC ��ַת���ɣ��û���Ϣ��
	// MAC ��ʽ��402CF45C3212��ȥ���ֽ��м��ð��
	private HashMap<String, String> GetUserInfoCode() {
		if (this.m_SoftCode.size() == 0) {

			String MIEIStr = this.GetMIEI();
			if (MIEIStr.length() >= 12) {

				// MIEIStr = MIEIStr.substring(MIEIStr.length() - 12,
				// MIEIStr.length());
				MIEIStr = this.GetUserInfoCodeByHardCode(MIEIStr);
				this.m_SoftCode.put("MIEI", MIEIStr.toUpperCase()); // �ֻ�����
			} else {

				String strMac = this.GetMacID();
//				if (strMac.equals("020000000000")) {
//					strMac = GetNewVersionMacID();
//				}
				String newID = MIEIStr + strMac;
				if (newID.length() >= 12) {
					this.m_SoftCode.put("MAC", this.GetUserInfoCodeByHardCode(newID.substring(0, 12)).toUpperCase()); // ��MAC��ַ����
				} else {
					this.m_SoftCode.put("MAC", this.GetUserInfoCodeByHardCode(strMac).toUpperCase()); // MAC��ַ��
				}
			}

		}
		return this.m_SoftCode;
		// if (!this.m_SoftCode.equals("")) return this.m_SoftCode;
		//
		// //���ֻ�����Ϊ����MAC��ַΪ��
		// String MACCode = this.GetMIEI();
		// //if (MACCode.equals("")) MACCode = this.GetMacID(); //MAC��ַ��
		// if (MACCode.equals("")) return "";
		// return GetUserInfoCodeByHardCode(MACCode);

	}

	/**
	 * ͨ��Ӳ���뷴�������
	 * 
	 * @param HardCode
	 * @return
	 */
	public String GetUserInfoCodeByHardCode(String HardCode) {
		// 40 2C F4 5C 35 01
		// �û��룺181570-131666-6B601E-1B6E3E
		StringBuilder SB = new StringBuilder();
		SB.append(HardCode.substring(0, HardCode.length() - 4));

		// ��λ���ļ�λ��ǰ����ֹ�û��� �������ظ������
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

	// �õ��ֻ�����
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

	// �õ�Mac��ַ
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
			// �ѵ�ǰ�����ϵķ�������ӿڵĴ��� Enumeration������
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			Log.d("TEST_BUG", " interfaceName = " + interfaces);
			while (interfaces.hasMoreElements()) {
				NetworkInterface netWork = interfaces.nextElement();
				// �������Ӳ����ַ������ʹ�ø����ĵ�ǰȨ�޷��ʣ��򷵻ظ�Ӳ����ַ��ͨ���� MAC����
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
				// ��·�����������豸��MAC��ַ�б�����ӡ֤�豸Wifi�� name �� wlan0
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

	// ���ܴ�
	private char[] gsEnc = new char[] { ')', '!', '@', '#', '$', '%', '^', '&', '*', '(', '[', '}', '|', '{', '-', ')',
			'(', '&', '^', '@', '#', '$', '%' };
	private char[] gsEnc1 = new char[] { '|', '{', '&', '$', '@', '!', '#', '$', '-', '+', ')', '*' };

	// ��WIFI������
	private boolean m_WIFIEnable = false;

	public void OpenWIFI() {
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

		// �ж�WIFI�Ƿ���ã���������ã�Ϊ�Ժ�Ĺر���׼��
		this.m_WIFIEnable = wifi.isWifiEnabled();

		// �����͹ر�wifi
		if (!wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(true);
		}
	}

	// ���֮ǰWIFIΪ�ر�״̬�����ȡMAC����ڴ˹ر�WIFI
	private void CloseWIFIByUser() {
		if (!this.m_WIFIEnable) {
			WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			wifi.setWifiEnabled(false);
		}
	}

	/* ����Ϊ�û�����ܺ���� */
	private String GetMACByUserInfoCode(String sUserInfoCode) {

		// �û��� ת�� �� MAC
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
