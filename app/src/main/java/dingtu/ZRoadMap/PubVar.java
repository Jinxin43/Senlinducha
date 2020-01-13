package dingtu.ZRoadMap;

import java.util.HashMap;
import java.util.Locale;

import com.dingtu.SLDuCha.CheckCard;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.Dataset.Workspace;
import lkmap.GPS.GPSLocate;
import lkmap.GPS.GPSMap;
import lkmap.Map.Map;
import lkmap.MapControl.MapControl;
import lkmap.ZRoadMap.DoEvent.DoEvent;

public class PubVar {
	public static Workspace m_Workspace = null;
	public static MapControl m_MapControl = null;
	public static GPSLocate m_GPSLocate = null;
	public static DoEvent m_DoEvent = null;

	public static Map m_Map = null;
	public static GPSMap m_GPSMap = null;

	public static ICallback m_Callback = null;

	// �ɼ������ļ�����
	public static String m_SysDataName = "TAData";

	// ϵͳ��Ŀ¼����
	public static String m_SysDictionaryName = ""; // �����ƴ�String.xml->app_name��ȡ

	// ϵͳ����·��
	public static String m_SysAbsolutePath = "";

	// �Զ����̵ĵ���
	public static int AutoSavePoints = 30;
	// GPS���������Ҳ���������ڵ�����̶ܳ�
	public static double GPSIntervalDistance = 10;

	// ���ݲɼ�ʱ��
	public static String SaveDataDate = "";

	// �Ƿ��Զ�����
	public static boolean AutoPan = true;

	public static boolean allSelectWF = false;
	
	public static boolean isZhuijiaing = false;
	public static boolean isRemoveTuban = false;
	
	public static CheckCard zhuijiaCheckCard= null;

	public static boolean AutoUpate = true;

	public static boolean VectorBGEditable = false;
	
	

	public static String Version = "";

	// �Ƿ���ʾ��Ļ����ʮ��
	public static boolean CenterCrossShow = true;

	// �Ƿ��Զ�����
	public static String xian = "";

	// �Ƿ��¼����
	public static boolean recordGPS = true;

	// �Ƿ������ʾGoogle��ͼ
	public static boolean ShowGoogleMap = false;
	public static HashMap<String, Object> imageEffect;
	public static Bitmap OriginalMap;

	public static String m_Version = ""; // ʡ�ݰ汾
	public static int m_ConfigDBVersion = 5;

	//�ϴ���ַ
	public static String serverUrl = "http://114.115.255.125:8002/";
	// public static String serverUrl="http://192.168.1.105:8002";

	public static String softCode = "";

	public static boolean m_Photo_LockGPS = true; // ����ʱ�Ƿ���ҪGPS֧�֣�Ҳ���Ƿ�����Ƭ�д洢GPS��Ϣ

	// �Ż�ȫ�ֱ�����ʹ�÷���(Key-�������ƣ�Value-�Զ����ࣩ
	public static HashMapEx m_HashMap = new HashMapEx();

	// ϵͳ������
	public static Locale m_AppLocale = Locale.ENGLISH; // Locale.CHINESE����

	// DisplayMetrics
	public static DisplayMetrics m_DisplayMetrics = null;

	public static float m_WindowScaleW = 0.8f;
	public static float m_WindowScaleH = 0.8f;

	public static int MinTanhuiIndex = 8;
	public static int maxTanhuiIndex = 38;

	public static String preYangdihao = "";

	public static AuthorizeTools_UserInfo currentUserInfo = null;
}
