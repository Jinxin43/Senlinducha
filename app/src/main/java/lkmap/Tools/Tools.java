package lkmap.Tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dingtu.DTGIS.DataService.ConfigDB;
import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Envelope;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.ProjectSystem;
import lkmap.CoordinateSystem.Project_GK;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkMapFileType;
import lkmap.Layer.GeoLayer;
import lkmap.Map.Param;
import lkmap.Map.StaticObject;

public class Tools {

    private static long lastClickTime=0;
    public static boolean IsMyTime(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >1000) {
            lastClickTime = currentTime;
            return  true;
        }

        return false;
    }
    /**
	 * �б�ת��ΪJSON�ַ���
	 * 
	 * @param StrList
	 * @return
	 */
	public static String ListToJSONStr(List<String> StrList) {
		try {
			JSONObject parentJSObject = new JSONObject();
			JSONArray jsArray = new JSONArray();
			for (String Str : StrList)
				jsArray.put(Str);
			parentJSObject.put("Data", jsArray);
			return parentJSObject.toString();
		} catch (JSONException e) {
			return "";
		}
	}

	/**
	 * JSONStr�ַ���תList<String>
	 * 
	 * @param JsonStr
	 * @return
	 */
	public static List<String> JSONStrToList(String JsonStr) {
		List<String> result = new ArrayList<String>();
		try {
			JSONTokener jsonParser = new JSONTokener(JsonStr);
			JSONObject jsObj = (JSONObject) jsonParser.nextValue();
			JSONArray jsArray = jsObj.getJSONArray("Data");
			for (int i = 0; i < jsArray.length(); i++)
				result.add(jsArray.getString(i));
			return result;
		} catch (JSONException e) {
			return result;
		}
	}

	/**
	 * ת��Ϊʮ������
	 * 
	 * @return
	 */
	public static String ColorToHexStr(int intColor) {
		String RStr = Integer.toHexString(Color.red(intColor));
		if (RStr.length() == 1)
			RStr = "0" + RStr;
		String GStr = Integer.toHexString(Color.green(intColor));
		if (GStr.length() == 1)
			GStr = "0" + GStr;
		String BStr = Integer.toHexString(Color.blue(intColor));
		if (BStr.length() == 1)
			BStr = "0" + BStr;
		return "#" + (RStr + GStr + BStr).toUpperCase();
	}

	public static String ColorToHexStr2(int intColor) {
		return String.format("#%06X", (0xFFFFFF & intColor));
	}

	public static String Color2String(Color color) {
		String R = Integer.toHexString(color.RED);
		R = R.length() < 2 ? ('0' + R) : R;
		String B = Integer.toHexString(color.BLUE);
		B = B.length() < 2 ? ('0' + B) : B;
		String G = Integer.toHexString(color.GREEN);
		G = G.length() < 2 ? ('0' + G) : G;
		return '#' + R + B + G;
	}

	/**
	 * ����ϵͳ������ת������
	 * 
	 * @param text
	 * @return
	 */
	public static String ToLocale(String text) {
		if (PubVar.m_AppLocale == Locale.ENGLISH) {
			List<String> LocaleTextList = new ArrayList<String>();
			LocaleTextList.add("����,Back");
			LocaleTextList.add("����,Back");
			LocaleTextList.add("ϵͳ��ʾ,Message");
			LocaleTextList.add("ȷ��,OK");
			LocaleTextList.add("ȡ��,Cancel");
			LocaleTextList.add("��ʾ,Message");
			LocaleTextList.add("���ڼ������ݣ����Ժ�,Loading data��Please wait");
			LocaleTextList.add("ϵͳû�м����κι�����Ϣ���޷���ɲ�����,First��Please open the project��");
			LocaleTextList.add("�Ƿ�ȷ���˳��ɼ�ϵͳ��,Whether you want to quit system��");

			LocaleTextList.add("���̹���,Project Manage");
			LocaleTextList.add("ѡ��,Select");
			LocaleTextList.add("����,Name");
			LocaleTextList.add("��������,Name");
			LocaleTextList.add("������Ϣ,Project Information");
			LocaleTextList.add("����ʱ��,Create Time");
			LocaleTextList.add("��ϸ,Detail");
			LocaleTextList.add("��ϸ��Ϣ,Detail Information");

			LocaleTextList.add("�½�,New");
			LocaleTextList.add("�½�����,New Project");
			LocaleTextList.add("����,Create");

			LocaleTextList.add("ɾ��,Delete");
			LocaleTextList.add("��,Open");
			LocaleTextList.add("�򿪹���,Open");
			LocaleTextList.add("�Ŵ�,Zoom");

			LocaleTextList.add("����,Feature");
			LocaleTextList.add("�ɼ�,Data");
			LocaleTextList.add("����,Save");
			LocaleTextList.add("����,Tools");
			LocaleTextList.add("����,Project");
			LocaleTextList.add("ͼ��,Layer");
			LocaleTextList.add("����Ԥ��ͼ,Data Preview");

			LocaleTextList.add("��ǰ�򿪹���,Current Open Project");
			LocaleTextList.add("����ͼ����Ϣ,Layer List");
			LocaleTextList.add("ͼ���б�,Layer List");
			LocaleTextList.add("�����б�,Project List");

			LocaleTextList.add("����������Ϣ,Coordinate system");
			LocaleTextList.add("����ϵͳ,Coordinate System");
			LocaleTextList.add("���뾭��,Central Meridian");
			LocaleTextList.add("ת������,Transfor Method");
			LocaleTextList.add("ת������,Transfor Parameters");
			LocaleTextList.add("ͼ������,Layer Name");
			LocaleTextList.add("ͼ������,Layer Type");
			LocaleTextList.add("ͼ��ģ��,Layer Template");
			LocaleTextList.add("ͼ�����,Layer Manage");
			LocaleTextList.add("������,Records");
			LocaleTextList.add("�빴ѡ��Ҫɾ���Ĺ���,Please check the need to delete the projects");
			LocaleTextList.add("�Ƿ�ȷ��Ҫɾ�����¹���,Whether you want to delete the following project");
			LocaleTextList.add("���ڴ򿪹���,Open project");
			LocaleTextList.add("�Ƿ���ϴι���,Whether you want to open the last project");
			LocaleTextList.add("�ϴ�ʱ��,Last Time");
			LocaleTextList.add("��ǰû�д����κι��̣��Ƿ���Ҫ��������,Whether you need to create a project");
			LocaleTextList.add("�ҵ�����ϵ,My System");
			LocaleTextList.add("ģ������,Template Name");
			LocaleTextList.add("����ΪĬ��ģ��,Set Default Template");
			LocaleTextList.add("�ҵ�����ϵ����,My System Information");
			LocaleTextList.add("��ǰ��������ϵ����,Current Project System Information");

			LocaleTextList.add("��Ⱦ,Render");
			LocaleTextList.add("����,MoveUp");
			LocaleTextList.add("����,MoveDown");
			LocaleTextList.add("��ģ��,Save");
			LocaleTextList.add("��ģ��,Load");
			LocaleTextList.add("ʸ����ͼ,Vector Map");
			LocaleTextList.add("դ���ͼ,    Grid Map");
			LocaleTextList.add("��ʾ,Visible");
			LocaleTextList.add("����,Type");
			LocaleTextList.add("����,Symbol");
			LocaleTextList.add("�ɼ�����ͼ��,Data Layer");
			LocaleTextList.add("��ͼͼ��,Background Layer");
			LocaleTextList.add("�ر�,Close");
			LocaleTextList.add("��Ϣ,Information");
			LocaleTextList.add("������Ϣ,Satellite Information");
			LocaleTextList.add("λ����Ϣ,Location Information");
			LocaleTextList.add("�ٶ�,Speed");
			LocaleTextList.add("�߳�,Elevation");
			LocaleTextList.add("����,Precision");
			LocaleTextList.add("״̬,Status");
			LocaleTextList.add("����,Longitude");
			LocaleTextList.add("γ��,Latitude");
			LocaleTextList.add("�ɼ�������,Visible Satellites");
			LocaleTextList.add("����������,Calculate satellite");
			LocaleTextList.add("��,");
			LocaleTextList.add("��,N");
			LocaleTextList.add("��,S");
			LocaleTextList.add("��,W");
			LocaleTextList.add("��,E");
			LocaleTextList.add("����,NE");
			LocaleTextList.add("����,SE");
			LocaleTextList.add("����,WS");
			LocaleTextList.add("����,NW");
			LocaleTextList.add("��,m");
			LocaleTextList.add("ƽ����,�O");
			LocaleTextList.add("����,km");
			LocaleTextList.add("ƽ������,k�O");
			LocaleTextList.add("�Ѷ�λ,Located");
			LocaleTextList.add("δ��λ,No Locate");

			LocaleTextList.add("����,Length");
			LocaleTextList.add("����,Length");
			LocaleTextList.add("����,Area");
			LocaleTextList.add("���,Area");
			LocaleTextList.add("����,Coordinate");
			LocaleTextList.add("ˢ��,Clear");
			LocaleTextList.add("��ͼ,Capture");
			LocaleTextList.add("����,More");
			LocaleTextList.add("�˳�,Quit");

			LocaleTextList.add("������,By XY");
			LocaleTextList.add("�ֻ��,By Manual");
			LocaleTextList.add("GPS��λ,By GPS");

			LocaleTextList.add("�ֻ�,Manual");
			LocaleTextList.add("GPS����,GPS Point");
			LocaleTextList.add("GPS�켣,GPS Track");

			LocaleTextList.add("ȡ��,Cancel");
			LocaleTextList.add("����,Generate");

			LocaleTextList.add("���ݲɼ�,Data Mode");
			LocaleTextList.add("�ɼ�ģʽ ����ǰ[��]ͼ�㣺,Mode��Current[Polygon]Layer��");
			LocaleTextList.add("�ɼ�ģʽ ����ǰ[��]ͼ�㣺,Mode��Current[Line]Layer��");
			LocaleTextList.add("�ɼ�ģʽ ����ǰ[��]ͼ�㣺,Mode��Current[Point]Layer��");
			LocaleTextList.add("ѡ��ͼ��,Select Layer");
			LocaleTextList.add("���ڲɼ�����ͼ��,Collecting Data Layer");
			LocaleTextList.add("��ѡ����Ч������ͼ�㣡,Please select valid data layer��");

			LocaleTextList.add("��,Point");
			LocaleTextList.add("��ͼ��,Point Layer");
			LocaleTextList.add("��,Line");
			LocaleTextList.add("��ͼ��,Line Layer");
			LocaleTextList.add("��ͼ��,Polygon Layer");
			LocaleTextList.add("��,Polygon");
			LocaleTextList.add("�ֻ�,Manual");

			LocaleTextList.add("������Ϣ,Coordinate Information");
			LocaleTextList.add("������Ϣ,Coordinate Information");
			LocaleTextList.add(" X����, X");
			LocaleTextList.add(" Y����, Y");
			LocaleTextList.add("��Ƭ,Photo");
			LocaleTextList.add("��������,Base Feature");
			LocaleTextList.add("������Ϣ,Data Information");
			LocaleTextList.add("��ע,Note");

			LocaleTextList.add("�ƶ�,Move");
			LocaleTextList.add("�ӽڵ�,Add Vertex");
			LocaleTextList.add("ɾ�ڵ�,Del Vertex");
			LocaleTextList.add("�ƽڵ�,Move Vertex");

			LocaleTextList.add("�����ȿ���GPS�豸��,Please first open the GPS device��");
			LocaleTextList.add("GPSû��λ����ȴ�...,GPS positioning��please wait...");
			LocaleTextList.add("��ǰû�����ڲɼ���ʵ�壡,Currently there is no collecting objects��");
			LocaleTextList.add("�ڵ�����Ϊ0���޷����ˣ�,Number of vertexs was 0, not back��");
			LocaleTextList.add("�޿ɻָ�������¼��,No restore operation record��");
			LocaleTextList.add("�Ƿ�ȷ��ɾ�����ϱ�ѡ��ʵ�壿,Is sure to delete the selected objects��");
			LocaleTextList.add("������,Tools Box");
			LocaleTextList.add("����ѡ��,Tools Option");

			LocaleTextList.add("ϵͳ����,Settings");
			LocaleTextList.add("�ɼ�ͳ��,Statistics");
			LocaleTextList.add("��������,Save");
			LocaleTextList.add("���ݵ���,Export");
			LocaleTextList.add("����ϵͳ,About");
			LocaleTextList.add("�˳�ϵͳ,Quit");

			LocaleTextList.add("ѡ������,Select Language");
			LocaleTextList.add("ϵͳ����,System Language");
			LocaleTextList.add("����,Chinese");
			LocaleTextList.add("Ӣ��,English");
			LocaleTextList.add("ϵͳ������һ������ʱ�л���,The system will be in the next time you start switch to");
			LocaleTextList.add("���Ի���,language");

			LocaleTextList.add("ϵͳ��Ϣ,System Information");
			LocaleTextList.add("�����,SoftName");
			LocaleTextList.add("�汾��,Version");
			LocaleTextList.add("��    Ȩ,Authorization");
			LocaleTextList.add("�����,SoftCode");
			LocaleTextList.add("������,Developers");
			LocaleTextList.add("��    ��,Language");
			LocaleTextList.add("����Ȩ,Authorized");
			LocaleTextList.add("δ��Ȩ,No Authorized");
			LocaleTextList.add("�����û�,Trial User");
			LocaleTextList.add("��ʱ�û�,Temporary User");
			LocaleTextList.add("��ʽ�û�,Formal User");
			LocaleTextList.add("��������,Trial Date To");
			LocaleTextList.add("����,Numbers");
			LocaleTextList.add("����,Add New");
			LocaleTextList.add("��,Pieces");
			LocaleTextList.add("���ڱ�������,Saving data");
			LocaleTextList.add("���ݱ���ɹ���,Data save successfully��");
			LocaleTextList.add("���ݽ�ͼ,Data Capture");
			LocaleTextList.add("��ѡ����Ҫ��ѯ��ʵ�壡,Please select need query objects��");
			LocaleTextList.add("������Ϣ,Base Information");
			LocaleTextList.add("�ֶ���Ϣ,Field List");
			LocaleTextList.add("��С,Size");
			LocaleTextList.add("�����ֵ�,Dictionary");
			LocaleTextList.add("�ֶ�����,Field Information");
			LocaleTextList.add("�ֶ����ݹ���,Field Dictionary");
			LocaleTextList.add("ֵ��,Domain");
			LocaleTextList.add("�Ƿ����������ֵ,Enter other value");
			LocaleTextList.add("�Ƿ�ɾ���ֶ�,Whether delete fields");
			LocaleTextList.add("�Ƿ�ɾ��ͼ��,Whether delete selected layer");

			LocaleTextList.add("ͼ����Ⱦ,Render");
			LocaleTextList.add("ͼ�����,Symbol");
			LocaleTextList.add("͸���ȣ�,Transparent��");
			LocaleTextList.add("͸����,Transparent");
			LocaleTextList.add("ͼ���ע,Label");
			LocaleTextList.add("�Ƿ��ע,IF Label");
			LocaleTextList.add("��ע�ֶ�,Field");
			LocaleTextList.add("��ע��ɫ,Color");
			LocaleTextList.add("��ע��С,Size");
			LocaleTextList.add("ʾ������,Text");

			LocaleTextList.add("ͼ��ģ��,Layer Template");
			LocaleTextList.add("ͼ��ģ����Ϣ,Template Information");
			LocaleTextList.add("ģ������,Name");
			LocaleTextList.add("����ʱ��,Create Time");
			LocaleTextList.add("ͼ������,Layer Numbers");
			LocaleTextList.add("ͼ����,Layers");

			LocaleTextList.add("������ͬ����ģ��,Overwrite Template");
			LocaleTextList.add("��ͼ�ļ�,Background Map");
			LocaleTextList.add("�޵�ͼ,No Map");
			LocaleTextList.add("�ļ�����,Name");
			LocaleTextList.add("ʸ��ͼ,Vector Map");
			LocaleTextList.add("դ��ͼ,Grid Map");
			LocaleTextList.add("Ӱ��ͼ,Grid Map");
			LocaleTextList.add("ʸ����ͼ����,Vector Map Set");
			LocaleTextList.add("���͸����,Polygon Transparent");
			LocaleTextList.add("��ʾ,Visible");

			LocaleTextList.add("GPS����������ʱ�������,GPS Sampling Time and Distance");
			LocaleTextList.add("GPS���ݵ㲶��ģʽ,GPS Data Capture Mode");
			LocaleTextList.add("������������ʾ,Head Bar Coordinate Display");
			LocaleTextList.add("ƽ��ֵ,Average");
			LocaleTextList.add("��������,Save");
			LocaleTextList.add("����ʱ��,Time");
			LocaleTextList.add("���,Distance");
			LocaleTextList.add("��������,Distance");
			LocaleTextList.add("�ڵ�,Vertex");
			LocaleTextList.add("��,");
			LocaleTextList.add("��,s");
			LocaleTextList.add("ƽ�����㡿����,Average Points");
			LocaleTextList.add("ƽ�����ڵ㡿��,Average Vertexs");
			LocaleTextList.add("��������,Data");
			LocaleTextList.add("�����ʽ,Formate");
			LocaleTextList.add("��ʾ�߳�,Elevation");
			LocaleTextList.add("��ʾ��ʽ,Show Formate");
			LocaleTextList.add("��������,Export");
			LocaleTextList.add("���ݵ���,Data Export");
			LocaleTextList.add("����Ŀ¼,Directory");
			LocaleTextList.add("������ʽ,Formate");
			LocaleTextList.add("����ͼ��,Layers");
			LocaleTextList.add("����,Export");

			LocaleTextList.add("���,Point");
			LocaleTextList.add("�߲�,Line");
			LocaleTextList.add("���,Polygon");

			LocaleTextList.add("����ͳ��,Statistic");
			LocaleTextList.add("����(��),Length(m)");
			LocaleTextList.add("���(ƽ����),Area(�O)");

			LocaleTextList.add("�����б�,Symbol List");
			LocaleTextList.add("����,Add");
			LocaleTextList.add("���ſ�,Symbol Library");

			for (String localeText : LocaleTextList) {
				String[] textInfo = localeText.split(",");
				if (textInfo[0].equals(text))
					if (textInfo.length == 2)
						return textInfo[1];
					else
						return "";

				// β����"��"���
				if (text.contains("��")) {
					if (textInfo[0].equals(text.replace("��", "")))
						return textInfo[1] + "��";
				}

				// �����пո����
				if (text.length() != text.trim().length()) {
					// ��ȡ�����ո�
					if (textInfo[0].equals(text.trim())) {
						return text.replace(text.trim(), textInfo[1]);
					}
				}
			}
		}
		return text;
	}

	/**
	 * ����ϵͳ����ת������
	 * 
	 * @param text
	 * @return
	 */
	public static void ToLocale(View view) {
		if (view == null)
			return;
		String VType = view.getClass().getName();
		if (VType.equals("android.widget.TextView")) {
			TextView tv = (TextView) view;
			tv.setText(ToLocale(tv.getText() + ""));
		}
		if (VType.equals("android.widget.CheckBox")) {
			CheckBox cb = (CheckBox) view;
			cb.setText(ToLocale(cb.getText() + ""));
		}
		if (VType.equals("android.widget.Button")) {
			Button bt = (Button) view;
			bt.setText(ToLocale(bt.getText() + ""));
		}
	}

	/**
	 * ��ȡstring.xml��Դ�ļ��ж�ӦID������
	 * 
	 * @param id
	 * @return
	 */
	public static String GetResourceStr(int id) {
		return PubVar.m_DoEvent.m_Context.getResources().getString(id);
	}

	/**
	 * ����ϵͳ���Ի���
	 * 
	 * @param context
	 */
	public static void SetLocale(Context context) {
		Resources resources = context.getResources();// ���res��Դ����
		Configuration config = resources.getConfiguration();// ������ö���
		DisplayMetrics dm = resources.getDisplayMetrics();// �����Ļ��������Ҫ�Ƿֱ��ʣ����صȡ�
		config.locale = PubVar.m_AppLocale;
		resources.updateConfiguration(config, dm);
	}

	/**
	 * �Զ���ȡϵͳ���õ����Ի���
	 * 
	 * @param context
	 */
	public static void AutoGetSystemLanguage() {
		// ����ϵͳ�����Ի���
		PubVar.m_AppLocale = Locale.CHINESE;
		String SystemLanguage = PubVar.m_HashMap.GetValueObject("Tag_System_Language").Value;
		if ((SystemLanguage.equals("����") || SystemLanguage.equals("Chinese")))
			PubVar.m_AppLocale = Locale.CHINESE;
		if ((SystemLanguage.equals("Ӣ��") || SystemLanguage.equals("English")))
			PubVar.m_AppLocale = Locale.ENGLISH;
		if ((SystemLanguage.equals("ϵͳ����") || SystemLanguage.equals("System Language"))) {
			String language = Locale.getDefault().getLanguage();
			if (language.endsWith("en"))
				PubVar.m_AppLocale = Locale.ENGLISH;
			else
				PubVar.m_AppLocale = Locale.CHINESE;
		}
	}

	/**
	 * �������괮
	 * 
	 * @param list
	 */
	public static void ReverseList(List<Coordinate> list) {
		List<Coordinate> newList = new ArrayList<Coordinate>();
		for (int i = 0; i < list.size(); i++)
			newList.add(0, list.get(i));
		list.clear();
		for (int i = 0; i < newList.size(); i++)
			list.add(newList.get(i));
	}

	/**
	 * ����ͼƬ���ļ�
	 * 
	 * @param fileName
	 * @param bp
	 * @return
	 */
	public static boolean SaveBitmapTo(String fileName, Bitmap bp) {
		File f = new File(fileName);
		try {
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		bp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
			fOut.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static int CalStrLength2(String str) {
		// Paint paint=new Paint();
		// Rect rect=new Rect();
		// paint.getTextBounds(str,0,str.length(),rect);
		// int width=rect.width();
		// return width;
		Paint paint = new Paint();
		float width = paint.measureText(str);
		int maxWidth = (int) (width + 0.5);
		return maxWidth;
	}

	/**
	 * ��ǰ���ո�
	 * 
	 * @param Str
	 * @param Len
	 * @return
	 */
	public static String PadLeft(String Str, int Len) {
		if (Tools.CalStrLength(Str) >= Len)
			return Str;
		String space = "";
		for (int i = 1; i <= Len - Tools.CalStrLength(Str); i++)
			space += " ";
		return space + Str;
	}

	/**
	 * �����ַ��г��ȣ��ɰ�������
	 * 
	 * @param value
	 * @return
	 */
	public static int CalStrLength(String value) {
		double valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		String capital = "[\u0041-\u005a]";
		// ��ȡ�ֶ�ֵ�ĳ��ȣ�����������ַ�����ÿ�������ַ�����Ϊ2������Ϊ1
		for (int i = 0; i < value.length(); i++) {
			// ��ȡһ���ַ�
			String temp = value.substring(i, i + 1);
			// �ж��Ƿ�Ϊ�����ַ�
			if (temp.matches(chinese)) {
				valueLength += 4;
			} else if (temp.matches(capital)) {
				// �����ַ�����Ϊ0.5
				valueLength += 2.3;
			} else {
				valueLength += 2;
			}
		}
		// ��λȡ��
		return (int) valueLength;
	}

	/**
	 * ��ʾToast��Ϣ
	 * 
	 * @param context
	 * @param Message
	 */
	public static void ShowToast(Context context, String Message) {
		Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * �Ƿ�Ϊ��ֵ
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}
		if (str.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �Ƿ�Ϊ����
	 * 
	 * @param str
	 * @return
	 */
	public static boolean IsInteger(String str) {
		if (str.trim().equals(""))
			return false;
		Matcher isNum = Pattern.compile("[0-9]*").matcher(str);
		;
		return isNum.matches();
	}

	/**
	 * �Ƿ�Ϊ������
	 * 
	 * @param Str
	 * @return
	 */
	public static boolean IsFloat(String Str) {
		try {
			float f = Float.parseFloat(Str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * �Ƿ�Ϊ������
	 * 
	 * @param Str
	 * @return
	 */
	public static boolean IsDouble(String Str) {
		if (Str.equals("NaN"))
			return false;
		try {
			double f = Double.parseDouble(Str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * �򿪶Ի��򣬴��н��ȿ�
	 * 
	 * @param pCallback
	 */
	public static void OpenDialog(final ICallback pCallback) {
		OpenDialog(Tools.ToLocale("���ڼ������ݣ����Ժ�") + "...", pCallback);
	}

	public static void OpenDialog(String CaptionInfo, final ICallback pCallback) {
		// ����ProgressDialog����
		final ProgressDialog m_pDialog = new ProgressDialog(PubVar.m_DoEvent.m_Context);

		// ���ý�������񣬷��ΪԲ�Σ���ת��
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		m_pDialog.setIcon(R.drawable.v1_messageinfo);

		// ����ProgressDialog ����
		m_pDialog.setTitle(Tools.ToLocale("��ʾ"));

		// ����ProgressDialog ��ʾ��Ϣ
		m_pDialog.setMessage(CaptionInfo);

		// ����ProgressDialog �Ľ������Ƿ���ȷ
		m_pDialog.setIndeterminate(false);

		// ����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
		m_pDialog.setCancelable(false);
		m_pDialog.show();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				pCallback.OnClick("OK", null);
				m_pDialog.cancel();
			}
		}, 100 * 1);

	}

	/**
	 * ʹ�������ֺ�ͼƬ�İ�ť������ʾ���������
	 * 
	 * @param btn
	 */
	public static void SetButtonImageAndTextOnCenter(final Button btn, int imageId) {
		ImageGetter imgGetter = new Html.ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				Drawable drawable = PubVar.m_DoEvent.m_Context.getResources().getDrawable(Integer.parseInt(source));
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				return drawable;
			}
		};

		StringBuffer sb = new StringBuffer();
		sb.append("<img src=\"").append(imageId).append("\"/>")
				.append("<font color=\"000000\">" + btn.getText() + "</font>");
		;
		Spanned span = Html.fromHtml(sb.toString(), imgGetter, null);
		btn.setText(span);
		sb = null;
	}

	/**
	 * ��ȡϵͳʱ��
	 * 
	 * @return ��ʽ��2011-10-1 23:23:22
	 */
	public static String GetSystemDate() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // HH��ʾ24����
		String date = sDateFormat.format(new java.util.Date());
		return date;
	}

	public static String GetSystemDateValue() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); // HH��ʾ24����
		String date = sDateFormat.format(new java.util.Date());
		return date;
	}

	// �ر����뷨
	public static void CloseInputMethod(Activity C) {
		// �ر����뷨
		InputMethodManager inputMethodManager = (InputMethodManager) C.getSystemService(Context.INPUT_METHOD_SERVICE);
		View V = C.getCurrentFocus();
		if (V != null) {
			inputMethodManager.hideSoftInputFromWindow(V.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			return;
		}
	}

	// �ر�ָ����Activity
	public static void CloseActivity(Activity A) {
		lkmap.Tools.Tools.CloseInputMethod(A);
		A.finish();
	}

	// ��ʾ�Ƿ�Ի���
	public static void ShowYesNoMessage(Context C, String Message, final ICallback callback) {
		// ����AlertDialog
		AlertDialog.Builder menuDialog = new AlertDialog.Builder(C);
		menuDialog.setTitle(("ϵͳ��ʾ"));
		menuDialog.setIcon(R.drawable.v1_messageinfo);
		menuDialog.setCancelable(true);
		menuDialog.setMessage(Message);
		menuDialog.setPositiveButton(("ȷ��"), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.OnClick("YES", "");
				dialog.dismiss();
			}
		});
		menuDialog.setNegativeButton(("ȡ��"), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		menuDialog.show();
	}

	public static void ShowYesContinuMessage(Context C, String Message, final ICallback callback) {
		// ����AlertDialog
		AlertDialog.Builder menuDialog = new AlertDialog.Builder(C);
		menuDialog.setTitle(("ϵͳ��ʾ"));
		menuDialog.setIcon(R.drawable.v1_messageinfo);
		menuDialog.setCancelable(true);
		menuDialog.setMessage(Message);
		menuDialog.setPositiveButton(("�õ�"), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.OnClick("YES", "");
				dialog.dismiss();
			}
		});
		menuDialog.setNegativeButton(("��������"), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.OnClick("NO", "");
				dialog.dismiss();
			}
		});
		menuDialog.show();
	}

	/**
	 * ��С������ת��Ϊ�ȷ����ʽ
	 * 
	 * @param numStr
	 *            ����Ҫ����ͼƬ�д洢��γ�ȣ�
	 * @return
	 */
	public static String ConvertToSexagesimal(String numStr) {
		double num = Double.parseDouble(numStr);
		int du = (int) Math.floor(Math.abs(num)); // ��ȡ��������
		double temp = GetdPoint(Math.abs(num)) * 60;
		int fen = (int) Math.floor(temp); // ��ȡ��������
		double miao = GetdPoint(temp) * 60;

		int miaoI = (int) Math.floor(miao * 1000000);
		if (num < 0)
			return "-" + du + "/1," + fen + "/1," + miaoI + "/1000000";

		return du + "/1," + fen + "/1," + miaoI + "/1000000";

	}

	// ��ȡС������
	private static double GetdPoint(double num) {
		double d = num;
		int fInt = (int) d;
		BigDecimal b1 = new BigDecimal(Double.toString(d));
		BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
		double dPoint = b1.subtract(b2).floatValue();
		return dPoint;
	}

	// �滯���Ȳ���ֵ
	public static String ReSetDistance(double Distance) {
		return ReSetDistance(Distance, true);
	}

	public static String ReSetDistance(double Distance, boolean HaveUnit) {
		DecimalFormat df1 = new DecimalFormat("0.0");
		DecimalFormat df2 = new DecimalFormat("0.000");
		if (Distance < 1000)
			return df1.format(Distance) + (HaveUnit ? "(" + Tools.ToLocale("��") + ")" : "");
		else
			return df2.format((Distance / 1000)) + (HaveUnit ? "(" + Tools.ToLocale("����") + ")" : "");
	}

	public static String ReSetArea(double Area, boolean HaveUnit) {
		// DecimalFormat df1 = new DecimalFormat("0.0");
		// ����Ҫ��Ķ��������С����
		DecimalFormat df1 = new DecimalFormat("0.00");
		DecimalFormat df2 = new DecimalFormat("0.0000");

		String areaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit").Value + "";
		if (areaUnit.equals("ƽ����")) {
			return df1.format(Area) + (HaveUnit ? "(" + areaUnit + ")" : "");
		}
		if (areaUnit.equals("ƽ������")) {
			return df2.format(Area / (1000 * 1000)) + (HaveUnit ? "(" + areaUnit + ")" : "");
		}
		if (areaUnit.equals("Ķ")) {
			return df1.format(Area / 666.6666666667) + (HaveUnit ? "(" + areaUnit + ")" : "");
		}
		if (areaUnit.equals("����")) {
			return df2.format(Area / 10000) + (HaveUnit ? "(" + areaUnit + ")" : "");
		}
		return df1.format(Area) + (HaveUnit ? "(" + areaUnit + ")" : "");
		// if (Area < 1000 * 1000) return df1.format(Area) + (HaveUnit?
		// "("+Tools.ToLocale("ƽ����")+")":"");
		// else return df2.format((Area / (1000*1000))) + (HaveUnit?
		// "("+Tools.ToLocale("ƽ������")+")":"");
	}

	public static double MToKM(double M) {
		return Save3Point(M / 1000);
		// DecimalFormat df2 = new DecimalFormat("#.000");
		// return Double.valueOf(df2.format(M / 1000));
	}

	public static double Save3Point(double M) {
		DecimalFormat df2 = new DecimalFormat("#.000");
		return Double.valueOf(df2.format(M));
	}

	public static float ParseFloat(String value, int decimal) {
		if (value.equals(""))
			return 0;
		DecimalFormat df2 = new DecimalFormat("#.000");
		if (decimal == 2)
			df2 = new DecimalFormat("#.00");
		if (decimal == 1)
			df2 = new DecimalFormat("#.0");
		return Float.valueOf(df2.format(Double.parseDouble(value)));
	}

	public static int ParseInt(String value) {
		if (value.equals(""))
			return 0;
		return Integer.parseInt(value);
	}

	public static double GetTwoPointDistance(Coordinate FirstPoint, Coordinate SencordPoint) {
		return GetTwoPointDistance(FirstPoint.getX(), FirstPoint.getY(), SencordPoint.getX(), SencordPoint.getY(),
				true);
	}

	public static double GetTwoPointDistance(Coordinate FirstPoint, Coordinate SencordPoint, boolean IfConvert) {
		return GetTwoPointDistance(FirstPoint.getX(), FirstPoint.getY(), SencordPoint.getX(), SencordPoint.getY(),
				IfConvert);
	}

	public static double GetTwoPointDistance(double x1, double y1, double x2, double y2, boolean IfConvert) {
		if (IfConvert) {
			if (StaticObject.soProjectSystem.GetCoorSystem().GetName().equals("WGS-84����")) {
				Coordinate Coor1 = StaticObject.soProjectSystem.XYToWGS84(x1, y1, 0);
				Coordinate Coor2 = StaticObject.soProjectSystem.XYToWGS84(x2, y2, 0);
				StaticObject.soProjectSystem.GetCoorSystem()
						.SetCenterMeridian(ProjectSystem.AutoCalCenterJX(Coor1.getX(), Coor1.getY()));

				Coor1 = Project_GK.GK_BLToXY(Coor1.getX(), Coor1.getY(), StaticObject.soProjectSystem.GetCoorSystem());
				Coor2 = Project_GK.GK_BLToXY(Coor2.getX(), Coor2.getY(), StaticObject.soProjectSystem.GetCoorSystem());
				if (Coor1 == null || Coor2 == null) {
					return 0;
				}
				x1 = Coor1.getX();
				y1 = Coor1.getY();
				x2 = Coor2.getX();
				y2 = Coor2.getY();
			}
		}
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	// �õ������б�ĳ���ֵ
	public static double GetListDistance(List<Coordinate> CoorList) {
		double D = 0;
		if (CoorList.size() <= 1)
			return D;
		for (int i = 0; i < CoorList.size() - 1; i++) {
			D += GetTwoPointDistance(CoorList.get(i), CoorList.get(i + 1));
		}
		return D;
	}

	/// <summary>��֪�������꣬�����м��н�
	/// </summary>
	/// <param name="PointA"></param>
	/// <param name="PointB"></param>
	/// <param name="PointC"></param>
	/// <returns></returns>
	public static double AngleB(Coordinate PointA, Coordinate PointB, Coordinate PointC) {
		double AB = GetTwoPointDistance(PointA, PointB);
		double AC = GetTwoPointDistance(PointA, PointC);
		double BC = GetTwoPointDistance(PointB, PointC);
		double CosB = (BC * BC + AB * AB - AC * AC) / (2 * BC * AB);
		return Math.acos(CosB) * (180 / Math.PI);
	}

	// ��List<int>��ʽת�����ַ�������sperate�ָ�
	public static String Join(String sperate, List<Integer> intList) {
		String JStr = "";
		for (Integer it : intList) {
			JStr += String.valueOf(it) + sperate;
		}
		return JStr.substring(0, JStr.length() - sperate.length());
	}

	public static String JoinT(String sperate, List<String> StrList) {
		if (StrList.size() == 0)
			return "";
		String JStr = "";
		for (String it : StrList) {
			JStr += String.valueOf(it) + sperate;
		}
		return JStr.substring(0, JStr.length() - sperate.length());
	}

	public static String JoinIntT(String sperate, List<Integer> intList) {
		if (intList.size() == 0)
			return "";
		String JStr = "";
		for (Integer it : intList) {
			JStr += String.valueOf(it) + sperate;
		}
		return JStr.substring(0, JStr.length() - sperate.length());
	}

	public static String Joins(String sperate, String[] StrList) {
		String JStr = "";
		for (String it : StrList) {
			JStr += String.valueOf(it) + sperate;
		}
		return JStr.substring(0, JStr.length() - sperate.length());
	}

	public static int[] IntListToArray(List<Integer> StrList) {
		int[] aStringList = new int[StrList.size()];
		int i = 0;
		for (Integer it : StrList) {
			aStringList[i] = it;
			i++;
		}
		return aStringList;
	}

	public static String[] StrListToArray(List<String> StrList) {
		String[] aStringList = new String[StrList.size()];
		int i = 0;
		for (String it : StrList) {
			aStringList[i] = it;
			i++;
		}
		return aStringList;
	}

	public static List<String> StrArrayToList(String[] StrArray) {
		List<String> StrList = new ArrayList<String>();
		for (String it : StrArray) {
			StrList.add(it);
		}
		return StrList;
	}
	
	public static String IntListToStr(List<Integer> StrList) {
		String aStringList = "";
		for (Integer it : StrList) {
			aStringList = aStringList + it + ",";
		}
		if (aStringList.length() == 0)
			return "";
		return aStringList.substring(0, aStringList.length() - 1);
	}

	public static String StrListToStr(List<String> StrList) {
		String aStringList = "";
		for (String it : StrList) {
			aStringList = aStringList + it + ",";
		}
		if (aStringList.length() == 0)
			return "";
		return aStringList.substring(0, aStringList.length() - 1);
	}

	// �����ļ�����չ��
	public static String ChangeExName(String FilePathAndName, String NewExName) {
		String FilePath = GetFilePath(FilePathAndName);
		return FilePath + "/" + GetFileName_NoEx(FilePathAndName) + "." + NewExName;
	}

	// ���ļ�·������ȡ�ļ����ƣ�������չ��
	public static String GetFileName(String FilePathAndName) {
		int start = FilePathAndName.lastIndexOf("/");
		int end = FilePathAndName.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return FilePathAndName.substring(start + 1, end + 4);
		} else {
			return null;
		}
	}

	// ���ļ�·������ȡ�ļ����ƣ���������չ��
	public static String GetFileName_NoEx(String FilePathAndName) {
		int start = FilePathAndName.lastIndexOf("/");
		int end = FilePathAndName.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return FilePathAndName.substring(start + 1, end);
		} else {
			return null;
		}
	}

	// ���ļ�·������ȡ�ļ�·��
	public static String GetFilePath(String FilePathAndName) {
		int start = FilePathAndName.lastIndexOf("/");
		if (start != -1) {
			return FilePathAndName.substring(0, start);
		} else {
			return null;
		}
	}
	
	 public static void deleteDirWihtFile(File dir) {
	        if (dir == null || !dir.exists() || !dir.isDirectory())
	            return;
	        for (File file : dir.listFiles()) {
	            if (file.isFile())
	                file.delete(); // ɾ�������ļ�
	            else if (file.isDirectory())
	                deleteDirWihtFile(file); // �ݹ�ķ�ʽɾ���ļ���
	        }
	        dir.delete();// ɾ��Ŀ¼����
	    }

	// ��ͼƬת��ΪBYte[]
	public static byte[] readStream(String FileName) {
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(FileName);
			byte[] buffer = new byte[1024];
			int len = -1;
			ByteArrayOutputStream outStream;
			try {
				outStream = new ByteArrayOutputStream();
				while ((len = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				byte[] data = outStream.toByteArray();
				outStream.close();
				inStream.close();
				return data;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// /**
	// * ��ȡϵͳ����չĿ¼���˷���������
	// * @return
	// */
	// private static List<String> getExternalPathList()
	// {
	// List<String> pathList = new ArrayList<String>();
	// try
	// {
	// String HEAD = "dev_mount";
	// File VOLD_FSTAB = new
	// File(Environment.getRootDirectory().getAbsoluteFile()
	// + File.separator
	// + "etc"
	// + File.separator
	// + "vold.fstab");
	// BufferedReader br = new BufferedReader(new FileReader(VOLD_FSTAB));
	// String tmp = null;
	// while ((tmp = br.readLine()) != null)
	// {
	// if (tmp.startsWith(HEAD))
	// {
	// boolean Have = false;
	// String[] sinfo = tmp.trim().split(" ");
	// for(String PathStr:pathList)
	// {
	// if (PathStr.equals(sinfo[2]))Have = true;
	// }
	// if (!Have)pathList.add(sinfo[2]);
	// }
	// }
	// br.close();
	// return pathList;
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return pathList;
	// }

	/**
	 * �õ����д洢������Ϣ�б�
	 * 
	 * @return
	 */
	public static List<HashMap<String, Object>> GetAllSDCardInfoList(Context C) {
		// SDPath=����·��,AllSize=ȫ������,FreeSize=��������
		List<HashMap<String, Object>> SDCardInfoList = new ArrayList<HashMap<String, Object>>();

		// ��ȡ�洢��·��
		StorageManager SM = (StorageManager) C.getSystemService(Context.STORAGE_SERVICE);
		List<String> SDCardPathList = new ArrayList<String>();

		try {
			String[] paths = (String[]) SM.getClass().getMethod("getVolumePaths", new  Class[ 0 ]).invoke(SM, new  Object[]{});
			SDCardPathList = Arrays.asList(paths);
		} catch (NoSuchMethodException e1) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (Exception e) {
		}
		;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String _SystemPath = Environment.getExternalStorageDirectory().getPath();
			if (!SDCardPathList.contains(_SystemPath))
				SDCardPathList.add(_SystemPath);
		}

		// �жϴ洢��·������Ч��
		for (String SDPath : SDCardPathList) {
			try {
				StatFs sf = new StatFs(SDPath); // ��ȡ�������ݿ�Ĵ�С
				long blockSize = sf.getBlockSize(); // ��ȡ�������ݿ���
				long allBlocks = sf.getBlockCount(); // ����SD����С //
				float allsize = (allBlocks * blockSize) / 1024f / 1024f / 1024f; // G

				String FreeUnit = "MB";
				float allfreen = (blockSize * sf.getAvailableBlocks()) / 1024f / 1024f; // M
				if (allfreen > 1) {
					if (allfreen > 1000) {
						allfreen = allfreen / 1024f;
						FreeUnit = "GB";
					} // G
					HashMap<String, Object> SDCarInfo = new HashMap<String, Object>();
					SDCarInfo.put("SDPath", SDPath);
					SDCarInfo.put("AllSize", Tools.ConvertToDigi(allsize + "", 3) + " GB");
					SDCarInfo.put("FreeSize", Tools.ConvertToDigi(allfreen + "", 3) + " " + FreeUnit);
					boolean canWrite = Environment.getExternalStorageDirectory().canWrite();
					// boolean canWrite = (new File(SDPath)).canWrite();
					SDCarInfo.put("CanWrite", canWrite);
					if (canWrite)
						SDCarInfo.put("Status", "����");
					else
						SDCarInfo.put("Status", "��ֻ�����޷�����ϵͳĿ¼��");
					SDCardInfoList.add(SDCarInfo);
				}
			} catch (IllegalArgumentException e) {
			} catch (Exception e) {
			}
		}

		return SDCardInfoList;
	}

	/**
	 * ���ϵͳ�ļ��ṹ�����û��ϵͳĿ¼�򴴽�
	 * 
	 * @return HashMap:Result=OK ��ʾͨ����飬����Ϊ������Ϣ,Path=�������յ�ϵͳ·��
	 */
	public static HashMap<String, String> CheckSystemFile(Context C) {
		HashMap<String, String> resultHM = new HashMap<String, String>();
		resultHM.put("Result", "OK");
		resultHM.put("Path", "");

		// �õ�ϵͳ��Ŀ¼�б�SDPath=����·��,AllSize=ȫ������,FreeSize=��������
		List<HashMap<String, Object>> SysDirList = Tools.GetAllSDCardInfoList(C);

		// �ж���Ŀ¼
		String SysDir = "";
		for (HashMap<String, Object> syObj : SysDirList) {
			String SysDirTemp = syObj.get("SDPath") + "/" + PubVar.m_SysDictionaryName;
			if (Tools.ExistFile(SysDirTemp)) {
				SysDir = SysDirTemp;
				break;
			}
		}

		// ������Ŀ����������
		List<String> dirList = new ArrayList<String>();
		List<String> fileList = new ArrayList<String>();
		dirList.add(SysDir + "/Map"); // ��ͼ�洢Ŀ¼
		dirList.add(SysDir + "/Data"); // �ɼ����ݴ洢Ŀ¼
		dirList.add(SysDir + "/SysFile"); // ϵͳ�ļ�Ŀ¼
		fileList.add(SysDir + "/SysFile/Config.dbx" + "," + R.raw.config); // ϵͳ�����ļ�
		fileList.add(SysDir + "/SysFile/Project.dbx" + "," + R.raw.project); // ���������ļ�
		fileList.add(SysDir + "/SysFile/Template.dbx" + "," + R.raw.template); // �ɼ�����ģ���ļ�
		fileList.add(SysDir + "/SysFile/UserConfig.dbx" + "," + R.raw.userconfig); // �û��Զ�����ģ���ļ�

		if (SysDir.equals("")) {
			resultHM.put("Result", "ϵͳ��Ŀ¼ȱʧ");
			return resultHM;
		}
		for (String dir : dirList) {
			if (!Tools.ExistFile(dir)) {
				if (!new File(dir).mkdirs()) {
					resultHM.put("Result", "�޷�����Ŀ¼��" + dir + "���������޷��������У�");
					return resultHM;
				}
			}
		}

		// �ж�ϵͳ�����ļ�
		for (String sysFile : fileList) {
			String fileName = sysFile.split(",")[0];
			int rowID = Integer.parseInt(sysFile.split(",")[1]);
			if (!Tools.ExistFile(fileName)) {
				if (!CopyToFileFromRawID(C, fileName, rowID)) {
					resultHM.put("Result", "�޷����������ļ���" + sysFile + "���������޷��������У�");
					return resultHM;
				}
			}
		}

		int currentVersion = ConfigDB.getVersion(SysDir + "/SysFile/Config.dbx");
		if (PubVar.m_ConfigDBVersion > currentVersion) {
			CopyFileDefinitely(C, SysDir + "/SysFile/Config.dbx", R.raw.config);
		}

		resultHM.put("Result", "OK");
		resultHM.put("Path", SysDir);
		return resultHM;
	}

	public static boolean CopyFileDefinitely(Context C, String fileName, int rawID) {
		try {
			OutputStream out = new FileOutputStream(new File(fileName));
			Resources r = C.getResources();
			InputStream in = r.openRawResource(rawID);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			out.write(buffer);
			in.close();
			out.close();

		} catch (Exception e) {
			return false;
		}

		return true;

	}

	public static boolean CopyToFileFromRawID(Context C, String fileName, int rawID) {
		if (!Tools.ExistFile(fileName)) {
			// ����ϵͳ�ļ�
			try {
				OutputStream out = new FileOutputStream(new File(fileName));
				Resources r = C.getResources();
				InputStream in = r.openRawResource(rawID);
				int length = in.available();
				byte[] buffer = new byte[length];
				in.read(buffer);
				out.write(buffer);
				in.close();
				out.close();
				return true;
			} catch (Exception e) {
				return false;
			}
		} else
			return true;

	}

	// ����ļ��Ƿ����
	public static boolean ExistFile(String FileName) {
		File file = new File(FileName);
		return file.exists();
	}

	private static String FileNameT = "";

	public static boolean ExistFileEx(String Path, String FileName) {
		FileNameT = FileName;
		File f = new File(Path);
		File[] files = f.listFiles(new Tools.UUIDfileFilter());
		if (files.length > 0)
			return true;
		else
			return false;
	}

	/**
	 * ɾ�������ļ�
	 * 
	 * @param File
	 */
	public static boolean DeleteAll(File f) {
		if (!f.exists()) {
			System.out.println("ָ��Ŀ¼������:" + f.getName());
		} else {
			boolean rslt = true;// �����м���
			// ���ļ��зǿա�ö�١��ݹ�ɾ����������
			File subs[] = f.listFiles();
			for (int i = 0; i <= subs.length - 1; i++) {
				if (subs[i].isDirectory())
					DeleteAll(subs[i]);// �ݹ�ɾ�����ļ�������
				rslt = subs[i].delete();// ɾ�����ļ���
			}
			rslt = f.delete();// ɾ���ļ��б���
		}
		return true;
	}

	public static boolean DeleteAllSub(File f) {
		if (!f.exists()) {
			System.out.println("ָ��Ŀ¼������:" + f.getName());
		} else {
			boolean rslt = true;// �����м���
			// ���ļ��зǿա�ö�١��ݹ�ɾ����������
			File subs[] = f.listFiles();
			for (int i = 0; i <= subs.length - 1; i++) {
				if (subs[i].isDirectory())
					DeleteAll(subs[i]);// �ݹ�ɾ�����ļ�������
				rslt = subs[i].delete();// ɾ�����ļ���
			}
		}
		return true;
	}

	public static boolean DeleteAllData() {

		return false;
	}

	static class UUIDfileFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String filename) {
			if (filename.indexOf(FileNameT) >= 0)
				return true;
			else
				return false;
		}
	}

	// �����ļ�
	public static boolean CopyFile(String From, String To) {
		File toFile = new File(To);
		if (!toFile.getParentFile().exists())
			toFile.getParentFile().mkdirs();

		InputStream is;
		OutputStream op;
		try {
			is = new FileInputStream(From);
			op = new FileOutputStream(To);
			BufferedInputStream bis = new BufferedInputStream(is);
			BufferedOutputStream bos = new BufferedOutputStream(op);

			byte[] bt = new byte[8192];
			int len;
			try {
				len = bis.read(bt);
				while (len != -1) {
					bos.write(bt, 0, len);
					len = bis.read(bt);
				}
				bis.close();
				bos.close();
			} catch (IOException e) {
				return false;
			}
			return true;

		} catch (FileNotFoundException e) {
			return false;
		}

	}

	// ��List<PointF>ת����float[]
	public static float[] PointListToFloatList(List<PointF> PointList) {
		int pStart = 0;
		float[] floatList = new float[PointList.size() * 2];
		for (PointF pt : PointList) {
			floatList[pStart] = pt.x;
			floatList[pStart + 1] = pt.y;
			pStart += 2;
		}
		return floatList;

	}

	// ��Point[] ת���� float[]
	public static float[] PointListToFloatList(Point[] PointList) {
		int pStart = 0;
		float[] floatList = new float[PointList.length * 2];
		for (Point pt : PointList) {
			floatList[pStart] = pt.x;
			floatList[pStart + 1] = pt.y;
			pStart += 2;
		}
		return floatList;
	}

	public static float[] PointListToFloatList(PointF[] PointList) {
		int pStart = 0;
		float[] floatList = new float[PointList.length * 2];
		for (PointF pt : PointList) {
			floatList[pStart] = pt.x;
			floatList[pStart + 1] = pt.y;
			pStart += 2;
		}
		return floatList;
	}

	// /// <summary>���ֽ���ת����ʵ�壬��Ҫ���ڱ�����ͼ��ת��TP.dbx
	// /// </summary>
	// /// <param name="bytes"></param>
	// /// <param name="lktype"></param>
	// /// <returns></returns>
	// public static Geometry ByteToGeometryEx(byte[] bytes, lkGeoLayerType
	// lktype)
	// {
	// Geometry m_Geometry = null;
	// int Offset = 0;
	// int PartCount = BitConverter.ToInt(BitConverter.Reverse(bytes,0,4), 0);
	// Offset+=4; //�ಿ������
	// if (lktype == lkGeoLayerType.enPoint)
	// {
	// //int FirstPartIndex = BitConverter.ToInt(bytes, 0);
	// Offset+=4; //��ʼ���ֵ�����
	// double X = BitConverter.ToDouble(bytes, Offset);
	// double Y = BitConverter.ToDouble(bytes, Offset+8);
	// lkmap.Cargeometry.Point P = new lkmap.Cargeometry.Point(X, Y);
	// m_Geometry = P;
	// }
	// else
	// {
	// List<Integer> PartIndexList = null;
	// if (PartCount > 1) //��ʾ�ಿ��
	// {
	// PartIndexList = new ArrayList<Integer>();
	// for (int i = 1; i <= PartCount; i++)
	// {
	// int PartStartIndex =
	// BitConverter.ToInt(BitConverter.Reverse(bytes,Offset,4), 0); Offset+=4;
	// //���ֵ���ʼ����
	// PartIndexList.add(PartStartIndex);
	// }
	// }
	// else Offset+=4;
	//
	// //��ȡ������Ϣ
	// List<Coordinate> CoorList = new ArrayList<Coordinate>();
	// int CoorCount = (bytes.length-Offset) / 16;
	// for (int idx = 0; idx <CoorCount ; idx++)
	// {
	// double X = BitConverter.ToDouble(bytes, Offset);Offset+=8;
	// double Y = BitConverter.ToDouble(bytes, Offset);Offset+=8;
	// CoorList.add(new Coordinate(X, Y));
	// }
	//
	// //ת��Ϊʵ��
	// if (lktype == lkGeoLayerType.enPolygon)
	// {
	// Polygon PlY = new Polygon();
	// PlY.SetAllCoordinateList(CoorList);
	// if (PartCount > 1){
	// PlY.ConvertToPolyline().setPartIndex(PartIndexList);PlY.IsSimple(false);PlY.ConvertToPolyline().IsSimple(false);}
	// PlY.ConvertToPolyline().SetAllCoordinateList(CoorList);
	// m_Geometry = PlY;
	// }
	// else
	// {
	// Polyline PL = new Polyline();
	// if (PartCount > 1){ PL.setPartIndex(PartIndexList);PL.IsSimple(false);}
	// PL.SetAllCoordinateList(CoorList);
	// m_Geometry = PL;
	//
	// }
	//
	// }
	// return m_Geometry;
	// }

	/**
	 * ���ֽ���ת����ʵ��
	 * 
	 * @param bytes
	 *            �ֽ�����
	 * @param lktype
	 *            ͼ������
	 * @param containGeo
	 *            �Ƿ������������
	 * @return
	 */
	public static Geometry ByteToGeometry(byte[] bytes, lkGeoLayerType lktype) {
		int Offset = 0;
		int PartCount = BitConverter.ToInt(bytes, 0);
		Offset += 4; // �ಿ������

		// ��ȡ�ಿ����Ϣ
		List<Integer> partIndexList = new ArrayList<Integer>();
		for (int i = 1; i <= PartCount; i++) {
			int partIndex = BitConverter.ToInt(bytes, Offset);
			Offset += 4; // ���ֵ���ʼ����
			partIndexList.add(partIndex);
		}

		// ��ȡ������Ϣ
		int Step = 24;
		List<Coordinate> CoorList = new ArrayList<Coordinate>();
		for (int idx = 0; idx < (bytes.length - Offset) / Step; idx++) {
			double X = BitConverter.ToDouble(bytes, Step * idx + Offset);
			double Y = BitConverter.ToDouble(bytes, Step * idx + Offset + 8);
			double Z = BitConverter.ToDouble(bytes, Step * idx + Offset + 16);
			Coordinate Coor = new Coordinate(X, Y, Z);
			CoorList.add(Coor);
		}

		// ����ͼ��ʵ��
		if (lktype == lkGeoLayerType.enPoint) {
			lkmap.Cargeometry.Point P = new lkmap.Cargeometry.Point(CoorList.get(0));
			return P;
		} else {
			if (lktype == lkGeoLayerType.enPolyline) {
				Polyline PL = new Polyline();

				// �ֲ�������
				partIndexList.add(CoorList.size());
				for (int idx = 0; idx < partIndexList.size() - 1; idx++) {
					int startIdx = partIndexList.get(idx);
					int endIdx = partIndexList.get(idx + 1);
					Part pPart = new Part(CoorList.subList(startIdx, endIdx));
					PL.AddPart(pPart);
				}

				return PL;
			} else {
				Polygon PLY = new Polygon();
				// �ֲ�������
				partIndexList.add(CoorList.size());
				for (int idx = 0; idx < partIndexList.size() - 1; idx++) {
					int startIdx = partIndexList.get(idx);
					int endIdx = partIndexList.get(idx + 1);
					Part pPart = new Part();
					for (int pi = startIdx; pi < endIdx; pi++) {
						pPart.getVertexList().add(CoorList.get(pi));
					}
					pPart.AutoSetPartType();
					PLY.AddPart(pPart);
				}
				return PLY;
			}
		}
	}

	// ��ʵ��ת��Ϊ�ֽ���
	public static byte[] GeometryToByte(Geometry pGeometry) {
		int SingleCoorSize = 24;

		// ���������ĳ���
		int byteSize = pGeometry.getVertexCount() * SingleCoorSize;

		// �ಿ��
		int PartCount = pGeometry.getPartCount();
		List<Integer> partIndexList = pGeometry.GetPartIndexList();
		byteSize += (PartCount + 1) * 4;

		// д��ಿ������
		int idx = 0;
		byte[] _Tb = new byte[byteSize];
		byte[] b1 = BitConverter.GetBytes(PartCount);
		ArrayCopy(b1, _Tb, idx);
		idx += 4;

		// д��ಿ������
		for (int partIdx : partIndexList) {
			byte[] b2 = BitConverter.GetBytes(partIdx);
			ArrayCopy(b2, _Tb, idx);
			idx += 4;
		}

		// д������
		for (int p = 0; p < pGeometry.getPartCount(); p++) {
			Part part = pGeometry.GetPartAt(p);
			for (Coordinate Coor : part.getVertexList()) {
				byte[] Xb = BitConverter.GetBytes(Coor.getX());
				byte[] Yb = BitConverter.GetBytes(Coor.getY());
				byte[] Zb = BitConverter.GetBytes(Coor.getZ());
				// byte[] geoXb = BitConverter.GetBytes(Coor.getGeoX());
				// byte[] geoYb = BitConverter.GetBytes(Coor.getGeoY());

				ArrayCopy(Xb, _Tb, idx);
				ArrayCopy(Yb, _Tb, idx + 8);
				ArrayCopy(Zb, _Tb, idx + 16);
				// ArrayCopy(geoXb, _Tb, idx + 24);
				// ArrayCopy(geoYb, _Tb, idx + 32);
				idx += SingleCoorSize;
			}
		}
		return _Tb;
	}

	public static void ArrayCopy(byte[] from, byte[] to, int toIndex) {
		for (int i = 0; i < from.length; i++) {
			to[toIndex + i] = from[i];
		}
	}

	// �õ�Ψһѡ���ʵ��
	public static boolean GetSelectOneObjectInfo(Param GeoLayerName, Param SYSID) {
		// �ж��Ƿ��Ѿ���ʵ�屻ѡ��
		int ObjCount = 0;
		List<GeoLayer> pGeoLayerList = PubVar.m_MapControl.getMap().getGeoLayers(lkGeoLayersType.enAll).getList();
		for (GeoLayer pGeoLayer : pGeoLayerList) {
			ObjCount += pGeoLayer.getSelSelection().getCount();
			if (pGeoLayer.getSelSelection().getCount() == 1) {
				SYSID.setValue(pGeoLayer.getSelSelection().getGeometryIndexList().get(0));
				GeoLayerName.setValue(pGeoLayer.getId());
			}
		}
		if (ObjCount != 1) {
			GeoLayerName = null;
			SYSID = null;
			return false;
		} else
			return true;
	}

	/**
	 * �õ�ѡ��ʵ������� DataSourceType:-1=ȫ����1=���ɱ༭��2=�ɱ༭
	 * LayerType=-1��ȫ����LayerType=0,�㣬LayerTYpe=1����,LayerTYpe=2����
	 */
	public static int GetSelectObjectsCount(int DataSourceType, int LayerType) {
		int ObjCount = 0;
		List<GeoLayer> pGeoLayerList = PubVar.m_MapControl.getMap().getGeoLayers(lkGeoLayersType.enAll).getList();
		for (GeoLayer pGeoLayer : pGeoLayerList) {

			boolean CalCount = false;
			if (DataSourceType == 1 && !pGeoLayer.getDataset().getDataSource().getEditing())
				CalCount = true;
			if (DataSourceType == 2 && pGeoLayer.getDataset().getDataSource().getEditing())
				CalCount = true;
			if (DataSourceType == -1)
				CalCount = true;
			if (CalCount) {
				if (LayerType == -1)
					ObjCount += pGeoLayer.getSelSelection().getCount();
				else {
					if ((pGeoLayer.getType() == lkGeoLayerType.enPoint && LayerType == 0))
						ObjCount += pGeoLayer.getSelSelection().getCount();
					if ((pGeoLayer.getType() == lkGeoLayerType.enPolyline && LayerType == 1))
						ObjCount += pGeoLayer.getSelSelection().getCount();
					if ((pGeoLayer.getType() == lkGeoLayerType.enPolygon && LayerType == 2))
						ObjCount += pGeoLayer.getSelSelection().getCount();
				}
			}
		}

		return ObjCount;
	}

	public static int GetSelectObjectsCount() {
		return GetSelectObjectsCount(-1, -1);
	}

	// ��ʾ��
	public static void ShowMessageBox(Context C, String Message, final ICallback callBack) {
		// ����AlertDialog
		AlertDialog.Builder menuDialog = new AlertDialog.Builder(C);
		menuDialog.setIcon(R.drawable.v1_messageinfo);
		menuDialog.setTitle(("ϵͳ��ʾ"));
		menuDialog.setCancelable(true);
		// menuDialog.setView(listView);
		menuDialog.setMessage(Message);
		menuDialog.setCancelable(false); // ����Ӧ���˼�
		menuDialog.setNegativeButton(("ȷ��"), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (callBack != null)
					callBack.OnClick("OK", "");
				dialog.dismiss();

			}
		});

		menuDialog.show();
	}

	public static void ShowMessageBox(Context C, String Message) {
		ShowMessageBox(C, Message, null);
	}

	public static void ShowMessageBox(String Message) {
		ShowMessageBox(PubVar.m_DoEvent.m_Context, Message);
	}

	public static void DeletePhoto(Context C, String Filename, final ICallback callback) {
		final String FileName = Filename;
		final Context cc = C;
		// ����AlertDialog
		AlertDialog.Builder menuDialog = new AlertDialog.Builder(C);
		menuDialog.setIcon(R.drawable.v1_messageinfo);
		menuDialog.setTitle(Tools.ToLocale("ϵͳ��ʾ"));
		// menuDialog.setView(listView);
		menuDialog.setMessage("�Ƿ�ȷ��ɾ������Ƭ��");
		menuDialog.setPositiveButton("ȷ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				File f = new File(FileName);
				f.delete();
				callback.OnClick("OK", "");
				// ((Photo)cc).StartLookImage();
				dialog.dismiss();
			}
		});
		menuDialog.setNegativeButton("ȡ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		menuDialog.show();

	}

	// ����ID��ȡ�ÿؼ����ı�ֵ
	public static String GetTextValueOnID(Activity A, int Controlid) {
		TextView tv = (TextView) A.findViewById(Controlid);
		return String.valueOf(tv.getText());
	}

	// ����ID��ȡ�ÿؼ����ı�ֵ
	public static String GetTextValueOnID(View A, int Controlid) {
		TextView tv = (TextView) A.findViewById(Controlid);
		return String.valueOf(tv.getText());
	}

	public static String GetTextValueOnID(Dialog A, int Controlid) {
		TextView tv = (TextView) A.findViewById(Controlid);
		return String.valueOf(tv.getText());
	}

	// ����ID��ȡ���ı��ؼ�
	public static TextView GetTextViewOnID(Activity A, int Controlid) {
		TextView tv = (TextView) A.findViewById(Controlid);
		return tv;
	}

	public static TextView GetTextViewOnID(View A, int Controlid) {
		TextView tv = (TextView) A.findViewById(Controlid);
		return tv;
	}

	public static TextView GetTextViewOnID(Dialog A, int Controlid) {
		TextView tv = (TextView) A.findViewById(Controlid);
		return tv;
	}

	public static void SetTextViewValueOnID(Activity A, int Controlid, String Value) {
		Tools.GetTextViewOnID(A, Controlid).setText(Value);
	}

	public static void SetTextViewValueOnID(View A, int Controlid, String Value) {
		Tools.GetTextViewOnID(A, Controlid).setText(Value);
	}

	public static void SetTextViewValueOnID(Dialog A, int Controlid, String Value) {
		Tools.GetTextViewOnID(A, Controlid).setText(Value);
	}

	// ���ݿؼ����͸�ֵ
	public static void SetValueToView(String Value, View v) {
		if (v == null)
			return;
		String VType = v.getClass().getName();
		if (VType.equals("android.widget.EditText")) {
			((EditText) v).setText(Value);
		}
		if (VType.equals("android.widget.TextView")) {
			((TextView) v).setText(Value);
		}
		if (VType.equals("android.widget.Spinner")) {
			Spinner sn = (Spinner) v;
			if ((ArrayAdapter<CharSequence>) sn.getAdapter() == null)
				return;
			int p = ((ArrayAdapter<CharSequence>) sn.getAdapter()).getPosition(Value);
			sn.setSelection(p, true);
		}
		if (VType.equals("lkmap.ZRoadMapData.DataCombox")) {
			((dingtu.ZRoadMap.Data.DataCombox) v).setText(Value);
		}

		if (VType.equals("lkmap.ZRoadMap.MyControl.v1_SpinnerDialog")) {
			Spinner sp = (Spinner) v;
			v1_DataBind.SetBindListSpinner(v.getContext(), "", Tools.StrArrayToList(new String[] { Value }), sp);
		}
		if (VType.equals("lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog")) {
			lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog sp = (lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog) v;
			sp.setText(Value);
		}
		if(VType.equals("android.widget.CheckBox"))
		{
			CheckBox cb = (CheckBox)v;
			if(Value != null && Value.length()>0)
			{
				try
				{
					int index = Integer.parseInt(Value);
					if(index>0)
					{
						cb.setChecked(true);
					}
					else
					{
						cb.setChecked(false);
					}
				}
				catch(Exception ex)
				{
					cb.setChecked(false);
				}
				
				
			}
			else
			{
				cb.setChecked(false);
			}
			
				
		}
	}

	// �õ��ؼ�ֵ
	public static String GetViewValue(View v) {
		if (v == null)
			return "";
		String VType = v.getClass().getName();
		if (VType.equals("android.widget.EditText")) {
			return String.valueOf(((EditText) v).getText());
		}
		if (VType.equals("android.widget.Spinner")) {
			Spinner sp = (Spinner) v;
			String value = String.valueOf(sp.getSelectedItem());
			if (value.startsWith("(") && value.indexOf(")") > -1 && value.indexOf(")") < (value.length() - 1)) {
				value = value.substring(1, value.indexOf(")"));
			}
			return value;
		}
		if (VType.equals("android.widget.TextView")) {
			return String.valueOf(((TextView) v).getText());
		}
		if (VType.equals("lkmap.ZRoadMapData.DataCombox")) {
			return ((dingtu.ZRoadMap.Data.DataCombox) v).getText();
		}
		if (VType.equals("lkmap.ZRoadMap.MyControl.v1_SpinnerDialog")) {
			Spinner sp = (Spinner) v;
			String value = String.valueOf(sp.getSelectedItem());
			if (value.startsWith("(") && value.indexOf(")") > -1 && value.indexOf(")") < (value.length() - 1)) {
				value = value.substring(1, value.indexOf(")"));
			}
			return value;
		}
		if (VType.equals("lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog")) {
			lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog sp = (lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog) v;
			String value = sp.getText();
			if (value.startsWith("(") && value.indexOf(")") > -1 && value.indexOf(")") < (value.length() - 1)) {
				value = value.substring(1, value.indexOf(")"));
			}
			return value;
		}
		
		if (VType.equals("android.widget.CheckBox")) {
			CheckBox cb = (CheckBox)v;
			if(cb.isChecked())
			{
				return "1";
			}
			else
			{
				return "0";
			}
		}
		
		return "";
	}

	// ����ID��ȡ�ÿؼ����ı�ֵ
	public static String GetSpinnerValueOnID(Activity A, int Controlid) {
		Spinner tv = (Spinner) A.findViewById(Controlid);
		return String.valueOf(tv.getSelectedItem());
	}

	public static String GetSpinnerValueOnID(View A, int Controlid) {
		Spinner tv = (Spinner) A.findViewById(Controlid);
		return String.valueOf(tv.getSelectedItem());
	}

	public static String GetSpinnerValueOnID(Dialog A, int Controlid) {
		Spinner tv = (Spinner) A.findViewById(Controlid);
		return String.valueOf(tv.getSelectedItem());
	}

	public static boolean GetCheckBoxValueOnID(Dialog A, int Controlid) {
		CheckBox tv = (CheckBox) A.findViewById(Controlid);
		return tv.isChecked();
	}

	// ����ID��ȡ��Spinner�ؼ�
	public static void SetSpinnerValueOnID(Activity A, int Controlid, String Value) {
		Spinner tv = (Spinner) A.findViewById(Controlid);
		if ((ArrayAdapter<CharSequence>) tv.getAdapter() == null)
			return;
		int p = ((ArrayAdapter<CharSequence>) tv.getAdapter()).getPosition(Value);
		tv.setSelection(p, true);
	}

	public static void SetSpinnerValueOnID(Dialog A, int Controlid, String Value) {
		Spinner tv = (Spinner) A.findViewById(Controlid);
		if ((ArrayAdapter<CharSequence>) tv.getAdapter() == null)
			return;
		int p = ((ArrayAdapter<CharSequence>) tv.getAdapter()).getPosition(Value);
		tv.setSelection(p, true);
	}

	public static void SetCheckValueOnID(Dialog A, int Controlid, boolean Value) {
		CheckBox tv = (CheckBox) A.findViewById(Controlid);
		tv.setChecked(Value);
	}

	// ����ID��ȡ��Spinner�ؼ�
	public static Spinner GetSpinnerViewOnID(Activity A, int Controlid) {
		Spinner tv = (Spinner) A.findViewById(Controlid);
		return tv;
	}

	// ���GPS״̬
	public static boolean ReadyGPS() {
		return ReadyGPS(false);
	}

	public static boolean ReadyGPS(boolean ShowMessage) {
		if (PubVar.m_GPSLocate == null) {
			if (ShowMessage)
				lkmap.Tools.Tools.ShowMessageBox(Tools.ToLocale("�����ȿ���GPS�豸��"));
			return false;
		}
		if (PubVar.m_GPSLocate.m_LTManager == null) {
			if (ShowMessage)
				lkmap.Tools.Tools.ShowMessageBox(Tools.ToLocale("�����ȿ���GPS�豸��"));
			return false;
		}

		if (!PubVar.m_GPSLocate.AlwaysFix()) {
			if (ShowMessage)
				lkmap.Tools.Tools.ShowMessageBox(Tools.ToLocale("GPSû��λ����ȴ�..."));
			return false;
		}

		// ��ȡ�����û��ĵ���ʱ��
		return PubVar.m_DoEvent.m_AuthorizeTools.isExpired(PubVar.SaveDataDate, ShowMessage);

	}

	public static String GetDDMMSS(double DDD) {
		// DD��MM'SS.SSSS��
		int dd = (int) Math.floor(DDD);
		double MM = (DDD - dd) * 60;
		int mm = (int) Math.floor(MM);

		double SS = (MM - mm) * 60;
		String ss = Tools.ConvertToDigi(SS + "", 4);
		return dd + "��" + mm + "'" + ss + "��";
	}

	public static double GetCoorFromDDMMSS(double d, double f, double m) {
		double coord = 0;
		coord = coord + d + f / 60 + m / 3600;
		return coord;
	}

	/**
	 * ����ת��Ϊ�ȷַ�
	 * 
	 * @param DDD
	 * @return
	 */
	public static String GetDDMM(double DDD) {
		// DD��MM.MMMMMM"
		int dd = (int) Math.floor(DDD);
		double MM = (DDD - dd) * 60;
		return dd + "��" + Tools.ConvertToDigi(MM + "", 6) + "'";

	}

	// �ж��Ƿ���ֵ
	public static float ConvertToFloat(String Str) {
		if (Str == null)
			return 0;
		try {
			return Float.parseFloat(Str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	// �ж��Ƿ���ֵ
	public static double ConvertToDouble(String Str) {
		if (Str == null)
			return 0;
		try {
			return Double.parseDouble(Str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * ��������С��λ���������ѧ��������ʾ
	 * 
	 * @param V
	 * @param Digi
	 * @return
	 */
	public static String ConvertToDigi(String V, int Digi) {
		char[] ch = new char[Digi];
		Arrays.fill(ch, '0');
		String DigiStr = new String(ch);
		DecimalFormat df = new DecimalFormat("0." + DigiStr);
		return df.format(Double.valueOf(V));
	}

	public static String ConvertToDigi(String V) {
		BigDecimal bd = new BigDecimal(V);
		return bd.toString();
	}

	public static String ConvertToDigi(double d) {
		BigDecimal bd = new BigDecimal(((Double) d).toString());
		return bd.toString();
	}

	public static String ConvertToDigi(double d, int Digi) {
		char[] ch = new char[Digi];
		Arrays.fill(ch, '0');
		String DigiStr = new String(ch);
		DecimalFormat df = new DecimalFormat("0." + DigiStr);
		return df.format(d);
	}

	public static String CalcTuFuHao(Coordinate coord, String scale) {
		Coordinate gpsCoor = StaticObject.soProjectSystem.XYToWGS84(coord);
		// DD��MM'SS.SSSS��
		int xdd = (int) Math.floor(gpsCoor.getX());
		double xMM = (gpsCoor.getX() - xdd) * 60;
		int xmm = (int) Math.floor(xMM);

		double xSS = (xMM - xmm) * 60;
		int xss = (int) Math.floor(xSS);

		int ydd = (int) Math.floor(gpsCoor.getY());
		double yMM = (gpsCoor.getY() - ydd) * 60;
		int ymm = (int) Math.floor(yMM);

		double ySS = (yMM - ymm) * 60;
		int yss = (int) Math.floor(ySS);

		String firstChar = getFirstChar(ydd, scale);
		int firstNum = (int) Math.floor(xdd / 6) + 31;

		int secondNum = 0;
		int thirdNum = 0;
		String result = "";

		if (scale.equals("1:1��")) {
			secondNum = 96 - (int) Math.floor(((ydd * 60 + ymm + yss / 60) % 240) / 2.5);
			thirdNum = (int) Math.floor(((xdd * 60 + xmm + xss / 60) % 360) / 3.75) + 1;
			result = firstChar + firstNum + "G" + getThreeChar(secondNum) + getThreeChar(thirdNum);
		}
		if (scale.equals("1:5��")) {
			secondNum = 240 / 10 - (int) Math.floor(((ydd * 60 + ymm + yss / 60) % 240) / 10);
			thirdNum = (int) Math.floor(((xdd * 60 + xmm + xss / 60) % 360) / 15) + 1;
			result = firstChar + firstNum + "E" + getThreeChar(secondNum) + getThreeChar(thirdNum);
		}

		// return xdd+"��"+xmm+"'"+xss+"��";
		return result;
	}

	public static String getThreeChar(int number) {
		if (number < 10) {
			return "00" + number;
		} else {
			if (100 > number) {
				return "0" + number;
			} else {
				return number + "";
			}
		}

	}

	public static String getFirstChar(int wd, String scalc) {
		int result = (int) Math.floor(wd / 4);
		if (scalc.equals("1:1��")) {
			if (result == 3) {
				return "D";
			}
			if (result == 4) {
				return "E";
			}
			if (result == 5) {
				return "F";
			}
			if (result == 6) {
				return "G";
			}
			if (result == 7) {
				return "H";
			}
			if (result == 8) {
				return "I";
			}
			if (result == 9) {
				return "J";
			}
		}

		if (scalc.equals("1:5��")) {
			if (result == 3) {
				return "D";
			}
			if (result == 4) {
				return "E";
			}
			if (result == 5) {
				return "F";
			}
			if (result == 6) {
				return "G";
			}
			if (result == 7) {
				return "H";
			}
			if (result == 8) {
				return "I";
			}
			if (result == 9) {
				return "J";
			}
		}

		return "";
	}

	// ��ȡMapĿ¼����Ч��ͼ�㣬��Чָ������Ŀ¼��Ҫ��TP.dbx,TP.idx�ļ�
	public static List<String> GetValidMapSubPath() {
		List<String> ValidMapSubPath = new ArrayList<String>();
		String MapPath = PubVar.m_SysAbsolutePath + "/Map";
		File f = new File(MapPath);
		File[] files = f.listFiles();// �г������ļ�
		for (File ff : files) {
			if (ff.isDirectory()) {
				if (lkmap.Tools.Tools.ExistFile(ff + "/TP.dbx") && lkmap.Tools.Tools.ExistFile(ff + "/TP.idx")) {
					ValidMapSubPath.add(ff.getName());
				}
			}
		}
		return ValidMapSubPath;
	}

	/**
	 * ��ȡϵͳĿ¼����Ч�Ĺ��̣���Чָ����Ŀ¼��Ҫ��Project.dbx��Data�ļ�
	 * 
	 * @return List<�ļ�����,����ʱ��>
	 */
	public static List<String> GetProjectList() {
		List<String> ValidProjectPath = new ArrayList<String>();
		String MapPath = PubVar.m_SysAbsolutePath + "/Data";
		File f = new File(MapPath);
		File[] files = f.listFiles();// �г������ļ�
		for (File ff : files) {
			if (ff.isDirectory()) {
				if (lkmap.Tools.Tools.ExistFile(ff + "/Project.dbx")
						&& lkmap.Tools.Tools.ExistFile(ff + "/TAData.dbx")) {
					ValidProjectPath.add(ff.getName() + "," + ff.lastModified());
				}
			}
		}
		Collections.sort(ValidProjectPath, new FileComparator());// ͨ����дComparator��ʵ����FileComparator��ʵ�ְ��ļ�����ʱ������
		return ValidProjectPath;
	}

	public static List<HashMap<String, Object>> GetBKMapListFromFolder(String folderPath, lkMapFileType mapFileType) {
		List<HashMap<String, Object>> ValidMapFileList = new ArrayList<HashMap<String, Object>>();
		File f = new File(folderPath);
		File[] files = f.listFiles();// �г������ļ�

		for (File ff : files) {
			if (ff.isFile()) {
				String FileName = ff.getAbsolutePath();
				if (FileName.length() < 5)
					continue;
				String ExtName = FileName.substring(FileName.length() - 4, FileName.length());

				if (((mapFileType == lkMapFileType.enVector)
						&& (ExtName.toUpperCase().equals(".DTV") | ExtName.toUpperCase().equals(".VMX")))
						|| ((mapFileType == lkMapFileType.enGrid)
								&& (ExtName.toUpperCase().equals(".DTI") || ExtName.toUpperCase().equals(".IMX")))) {
					// ��ǰ����ϵͳ
					CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
					String CoorSystemInfo = CS.GetName() + "��" + CS.GetCenterMeridian() + "��";
					if (CS.GetName().equals("WGS-84����"))
						CoorSystemInfo = CS.GetName();

					// ��ȡ����ͼ��Ϣ
					ASQLiteDatabase bkDB = new ASQLiteDatabase();
					bkDB.setDatabaseName(FileName);
					SQLiteDataReader DR = bkDB.Query("select * from MapInfo");
					if (DR != null)
						while (DR.Read()) {
							String CoorSystemName = DR.GetString("CoorType");
							String CenterJX = DR.GetString("CenterJX");
							if (CenterJX.equals(""))
								CenterJX = "0";

							double MinX = 0, MinY = 0, MaxX = 0, MaxY = 0;
							if (mapFileType == lkMapFileType.enGrid) {
								MinX = DR.GetDouble("Min_X");
								MinY = DR.GetDouble("Min_Y");
								MaxX = DR.GetDouble("Max_X");
								MaxY = DR.GetDouble("Max_Y");
							}

							if (CS.GetName().equals("WGS-84����")) {
								if (!CS.GetName().equals(CoorSystemName))
									continue;
							} else {
								if (!(CS.GetName().equals(CoorSystemName)
										&& CS.GetCenterMeridian() == Integer.parseInt(CenterJX)))
									continue;
							}

							// ���뷵���б�
							HashMap<String, Object> hm = new HashMap<String, Object>();
							String BKMapType = "";
							if (mapFileType == lkMapFileType.enVector)
								BKMapType = "ʸ��";
							if (mapFileType == lkMapFileType.enGrid)
								BKMapType = "դ��";
							hm.put("Type", BKMapType);
							hm.put("Select", true);
							hm.put("BKMapFile", ff.getName());
							hm.put("CoorSystem", CoorSystemInfo);
							hm.put("MinX", MinX);
							hm.put("MinY", MinY);
							hm.put("MaxX", MaxX);
							hm.put("MaxY", MaxY);
							hm.put("Transparent", 0);
							hm.put("Visible", true);
							hm.put("Sort", Integer.MAX_VALUE);
							hm.put("F1", folderPath);
							ValidMapFileList.add(hm);
						}
					DR.Close();
					bkDB.Close();
				}

			}
		}
		// Collections.sort(ValidMapFileList, new
		// FileComparator());//ͨ����дComparator��ʵ����FileComparator��ʵ�ְ��ļ�����ʱ������
		return ValidMapFileList;
	}

	/**
	 * ��ȡϵͳĿ¼����Ч�ĵ�ͼ�ļ��б�
	 * 
	 * @return List<HashMap<String,Object>>
	 */
	public static List<HashMap<String, Object>> GetBKMapList(String SubPath, lkMapFileType mapFileType) {
		List<HashMap<String, Object>> ValidMapFileList = new ArrayList<HashMap<String, Object>>();
		String MapPath = PubVar.m_SysAbsolutePath + "/Map";
		if (!SubPath.equals(""))
			MapPath += "/" + SubPath;
		if (!Tools.ExistFile(MapPath))
			return ValidMapFileList;
		File f = new File(MapPath);
		File[] files = f.listFiles();// �г������ļ�
		for (File ff : files) {
			if (ff.isFile()) {
				String FileName = ff.getAbsolutePath();
				if (FileName.length() < 5)
					continue;
				String ExtName = FileName.substring(FileName.length() - 4, FileName.length());

				if (((mapFileType == lkMapFileType.enVector)
						&& (ExtName.toUpperCase().equals(".DTV") || ExtName.toUpperCase().equals(".VMX")))
						|| ((mapFileType == lkMapFileType.enGrid)
								&& (ExtName.toUpperCase().equals(".DTI") || ExtName.toUpperCase().equals(".IMX")))) {
					// ��ǰ����ϵͳ
					CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
					String CoorSystemInfo = CS.GetName() + "��" + CS.GetCenterMeridian() + "��";
					if (CS.GetName().equals("WGS-84����"))
						CoorSystemInfo = CS.GetName();

					// ��ȡ����ͼ��Ϣ
					ASQLiteDatabase bkDB = new ASQLiteDatabase();
					bkDB.setDatabaseName(FileName);
					SQLiteDataReader DR = bkDB.Query("select * from MapInfo");
					if (DR != null)
						while (DR.Read()) {
							String CoorSystemName = DR.GetString("CoorType");
							String CenterJX = DR.GetString("CenterJX");
							if (CenterJX.equals(""))
								CenterJX = "0";

							double MinX = 0, MinY = 0, MaxX = 0, MaxY = 0;
							if (mapFileType == lkMapFileType.enGrid) {
								MinX = DR.GetDouble("Min_X");
								MinY = DR.GetDouble("Min_Y");
								MaxX = DR.GetDouble("Max_X");
								MaxY = DR.GetDouble("Max_Y");
							}

							if (CS.GetName().equals("WGS-84����")) {
								if (!CS.GetName().equals(CoorSystemName))
									continue;
							} else {
								if (!(CS.GetName().equals(CoorSystemName)
										&& CS.GetCenterMeridian() == Integer.parseInt(CenterJX)))
									continue;
							}

							// ���뷵���б�
							HashMap<String, Object> hm = new HashMap<String, Object>();
							String BKMapType = "";
							if (mapFileType == lkMapFileType.enVector)
								BKMapType = "ʸ��";
							if (mapFileType == lkMapFileType.enGrid)
								BKMapType = "դ��";
							hm.put("Type", BKMapType);
							hm.put("Select", true);
							hm.put("BKMapFile", (SubPath.equals("") ? ff.getName() : SubPath + "/" + ff.getName()));
							hm.put("CoorSystem", CoorSystemInfo);
							hm.put("MinX", MinX);
							hm.put("MinY", MinY);
							hm.put("MaxX", MaxX);
							hm.put("MaxY", MaxY);
							hm.put("Transparent", 0);
							hm.put("Visible", true);
							hm.put("F1", MapPath);
							hm.put("Sort", Integer.MAX_VALUE);
							ValidMapFileList.add(hm);
						}
					DR.Close();
					bkDB.Close();
				}

			}
		}
		// Collections.sort(ValidMapFileList, new
		// FileComparator());//ͨ����дComparator��ʵ����FileComparator��ʵ�ְ��ļ�����ʱ������
		return ValidMapFileList;
	}

	// ������ͼ��Χ��
	public static boolean SaveViewExtend() // ViewExtend=1,2,3,4
	{
		String LeftTop = PubVar.m_Map.getViewConvert().getExtend().getLeftTop().ToString();
		String RightBottom = PubVar.m_Map.getViewConvert().getExtend().getRightBottom().ToString();
		return SaveConfigItem("ViewExtend", LeftTop + "," + RightBottom);
	}

	// �ָ���ͼ��Χ
	public static boolean RestoreViewExtend() {
		String StrExtend = ReadConfigItem("ViewExtend");
		if (!StrExtend.equals("")) {
			String[] ViewExtend = StrExtend.split(",");
			if (ViewExtend.length == 4) {
				Envelope env = new Envelope(Double.parseDouble(ViewExtend[0]), Double.parseDouble(ViewExtend[1]),
						Double.parseDouble(ViewExtend[2]), Double.parseDouble(ViewExtend[3]));
				PubVar.m_Map.setExtend(env);
				PubVar.m_Map.Refresh();
				return true;
			}
		}
		return false;
	}

	// �����ļ�ͳһ�ŵ�SysFile/Config.CF�У��ļ���ʽ����ʶ=Value
	public static String ReadConfigItem(String ItemName) {
		try {
			String FPath = PubVar.m_Map.getSystemPath() + "/SysFile/Config.CF";
			if (lkmap.Tools.Tools.ExistFile(FPath)) {
				FileReader fr = new FileReader(FPath);
				BufferedReader br = new BufferedReader(fr);
				String line = null;
				String ItemValue = "";
				while ((line = br.readLine()) != null) {
					if (line.indexOf(ItemName) == 0)
						ItemValue = line.split("=")[1];
				}
				br.close();
				fr.close();
				return ItemValue;
			}
			return "";
		} catch (Exception e) {
			return "";
		}
	}

	// �����ļ�ͳһ�ŵ�SysFile/Config.CF�У��ļ���ʽ����ʶ=Value
	public static boolean SaveConfigItem(String ItemName, String ItemValue) {
		try {
			String FPath = PubVar.m_Map.getSystemPath() + "/SysFile/Config.CF";
			if (!lkmap.Tools.Tools.ExistFile(PubVar.m_Map.getSystemPath()))
				return false;
			List<String> ItemList = new ArrayList<String>();
			if (lkmap.Tools.Tools.ExistFile(FPath)) {
				// ��ȡԭ�е�������
				FileReader fr = new FileReader(FPath);
				BufferedReader br = new BufferedReader(fr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (line.indexOf(ItemName) < 0) {
						ItemList.add(line);
					}
				}
				br.close();
				fr.close();
			}

			// ����������
			ItemList.add(ItemName + "=" + ItemValue);
			FileWriter fw = new FileWriter(FPath);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String Item : ItemList)
				bw.write(Item + "\r\n");
			bw.close();
			fw.close();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static List<HashMap<String, Object>> ListHMSSToListHMSO(List<HashMap<String, String>> hmList) {
		List<HashMap<String, Object>> hmobjList = new ArrayList<HashMap<String, Object>>();
		for (HashMap<String, String> hm : hmList) {
			HashMap<String, Object> ho = new HashMap<String, Object>();
			for (String key : hm.keySet())
				ho.put(key, hm.get(key));
			hmobjList.add(ho);
		}
		return hmobjList;
	}

	public static List<HashMap<String, String>> ListHMSOToListHMSS(List<HashMap<String, Object>> hoList) {
		List<HashMap<String, String>> hmobjList = new ArrayList<HashMap<String, String>>();
		for (HashMap<String, Object> hm : hoList) {
			HashMap<String, String> ho = new HashMap<String, String>();
			for (String key : hm.keySet())
				ho.put(key, hm.get(key) + "");
			hmobjList.add(ho);
		}
		return hmobjList;
	}

	/**
	 * SPת��������
	 * 
	 * @param sp
	 * @return
	 */
	public static int SPToPix(int sp) {
		return (int) (sp * PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
	}

	public static int DPToPix(int dp) {
		return (int) (dp * PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics().density + 0.5f);
	}

	/**
	 * ���ù�������Ŀ��״̬
	 * 
	 * @param buttonView
	 * @param ifSelect
	 */
	public static void SetToolsBarItemSelect(View buttonView, boolean ifSelect) {
		// int resourceId = R.drawable.buttonstyle_edittoolbar;
		// if (ifSelect)resourceId = R.drawable.buttonstyle_edittoolbar_select;
		//
		// //��ȡ��Դ
		// Resources res = PubVar.m_DoEvent.m_Context.getResources();
		// Drawable draw=res.getDrawable(resourceId);
		//
		// //���ñ���ͼ
		// buttonView.setBackgroundDrawable(draw);
		buttonView.setSelected(ifSelect);
	}

	/**
	 * ����ָ����ԴId��ȡͼƬ
	 * 
	 * @param resourceId
	 * @return
	 */
	public static Bitmap GetBitmapByResources(int resourceId) {
		return BitmapFactory.decodeResource(PubVar.m_DoEvent.m_Context.getResources(), resourceId);
	}

	/**
	 * ���¹�������ѡ��ť������ı�ѡ��ʵ������ֵ
	 */
	public static void UpdateShowSelectCount() {
		if (PubVar.m_DoEvent.m_MainBottomToolBar != null)
			PubVar.m_DoEvent.m_MainBottomToolBar.UpdateShowSelectCount();
	}
	
	public static void UpdateDuCha()
	{
		if (PubVar.m_DoEvent.m_MainBottomToolBar != null)
			PubVar.m_DoEvent.m_MainBottomToolBar.ShwoWFWFButton();
	}

	public static void UpdateScaleBar() {
		PubVar.m_DoEvent.m_GpsInfoManage.UpdateScaleBar();
	}
}
