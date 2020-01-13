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

	// 采集数据文件名称
	public static String m_SysDataName = "TAData";

	// 系统主目录名称
	public static String m_SysDictionaryName = ""; // 该名称从String.xml->app_name获取

	// 系统完整路径
	public static String m_SysAbsolutePath = "";

	// 自动存盘的点数
	public static int AutoSavePoints = 30;
	// GPS采样间隔，也就是数据内点的疏密程度
	public static double GPSIntervalDistance = 10;

	// 数据采集时间
	public static String SaveDataDate = "";

	// 是否自动移屏
	public static boolean AutoPan = true;

	public static boolean allSelectWF = false;
	
	public static boolean isZhuijiaing = false;
	public static boolean isRemoveTuban = false;
	
	public static CheckCard zhuijiaCheckCard= null;

	public static boolean AutoUpate = true;

	public static boolean VectorBGEditable = false;
	
	

	public static String Version = "";

	// 是否显示屏幕中心十字
	public static boolean CenterCrossShow = true;

	// 是否自动移屏
	public static String xian = "";

	// 是否记录航迹
	public static boolean recordGPS = true;

	// 是否叠加显示Google地图
	public static boolean ShowGoogleMap = false;
	public static HashMap<String, Object> imageEffect;
	public static Bitmap OriginalMap;

	public static String m_Version = ""; // 省份版本
	public static int m_ConfigDBVersion = 5;

	//上传地址
	public static String serverUrl = "http://114.115.255.125:8002/";
	// public static String serverUrl="http://192.168.1.105:8002";

	public static String softCode = "";

	public static boolean m_Photo_LockGPS = true; // 拍照时是否需要GPS支持，也就是否在相片中存储GPS信息

	// 优化全局变量的使用方法(Key-变量名称，Value-自定义类）
	public static HashMapEx m_HashMap = new HashMapEx();

	// 系统的语言
	public static Locale m_AppLocale = Locale.ENGLISH; // Locale.CHINESE中文

	// DisplayMetrics
	public static DisplayMetrics m_DisplayMetrics = null;

	public static float m_WindowScaleW = 0.8f;
	public static float m_WindowScaleH = 0.8f;

	public static int MinTanhuiIndex = 8;
	public static int maxTanhuiIndex = 38;

	public static String preYangdihao = "";

	public static AuthorizeTools_UserInfo currentUserInfo = null;
}
