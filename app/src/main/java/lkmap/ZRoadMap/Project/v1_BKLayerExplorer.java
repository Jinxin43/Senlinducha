package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

public class v1_BKLayerExplorer 
{
	private v1_ProjectDB m_ProjectDB = null;
	
	/**
	 * �󶨹��̲�����
	 * @param projectDB
	 */
	public void SetBindProjectDB(v1_ProjectDB projectDB)
	{
		this.m_ProjectDB = projectDB;
	}
	
	//����ʸ��ͼ
	private v1_BKVectorLayerExplorer m_BKVectorLayerExplorer = new v1_BKVectorLayerExplorer();
	public v1_BKVectorLayerExplorer GetVectorLayerExplorer(){return this.m_BKVectorLayerExplorer;}
	
	//����դ��ͼ
	private v1_BKGridLayerExplorer m_BKGridLayerExplorer = new v1_BKGridLayerExplorer();
	public v1_BKGridLayerExplorer GetGridLayerExplorer(){return this.m_BKGridLayerExplorer;}
	
	/**
	 * �򿪵�ͼ���ݿ⣬��DoEvent_Project.DoCommand("����_��")�е���
	 */
	public void OpenBKDataSource()
	{
		this.m_BKVectorLayerExplorer.OpenVectorDataSource();
		this.m_BKGridLayerExplorer.OpenGridDataSource();
		//this.m_BKVectorLayerExplorer.SetBKVisible(true);
		this.m_BKGridLayerExplorer.SetBKVisible(true);
		this.m_BKVectorLayerExplorer.SetVectorSelectable(false);
	}

	/**
	 * ���ش˹��̶�Ӧ�ĵ�ͼͼ�����ƣ���v1_ProjectDB.OpenProject()�е��ã�����֮��ŵ���OpenBKDataSource();
	 * Type text,  ʸ�� ��դ��
	 * BKMapFile text, �ļ���
	 * MinX text,MinY text,MaxX text,MaxY text,  ���Χ
	 * CoorSystem text, ����ϵͳ
	 * Transparent text,	͸����
	 * Sort text,	�����
	 * Visible text,	�ɼ���
	 * F1 text,F2 text,F3 text,F4 text,F5 text,F6 text,F7 text,F8 text,F9 text,F10 text)	�����ֶ�
	 * 
	 */
	public void LoadBKLayer()
	{
		//��ȡ�˹��̶�Ӧ�ı���ͼ����Ϣ
		String[] BKMapTypeList = {"ʸ��","դ��"};
		for(String BKMapType:BKMapTypeList)
		{
			String SQL = "select * from T_BKLayer where Type='%1$s' order by Sort";
			SQL = String.format(SQL, BKMapType);
			SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query(SQL);
			if (DR==null) return;
			List<HashMap<String,Object>> BkMapFileList = new ArrayList<HashMap<String,Object>>();
			while(DR.Read())
			{
				HashMap<String,Object> ho = new HashMap<String,Object>();
				ho.put("Type", "");   //����ͼ���ͣ�ʸ����դ��
				ho.put("BKMapFile", "");
				ho.put("MinX", "");
				ho.put("MinY", "");
				ho.put("MaxX", "");
				ho.put("MaxY", "");
				ho.put("CoorSystem", "");
				ho.put("Transparent", "");
				ho.put("Sort", "");
				ho.put("Visible", "");
				for(String key:ho.keySet())ho.put(key, DR.GetString(key));
				String path = DR.GetString("F1");//�洢·��
				if(path == null || path.length() == 0)
				{
					path = PubVar.m_SysAbsolutePath+"/Map/";
				}
				ho.put("F1",path);
				ho.put("Select",true);
				BkMapFileList.add(ho);
			}DR.Close();
			if (BKMapType.equals("ʸ��"))this.m_BKVectorLayerExplorer.SetBKFileList(BkMapFileList);
			if (BKMapType.equals("դ��"))this.m_BKGridLayerExplorer.SetBKFileList(BkMapFileList);
		}
	}
	
	/**
	 * �����ͼ�ļ�����
	 * @param BKVectorFile ʸ��ͼ�ļ��б�
	 * @param BKGridFile դ��ͼ�ļ��б�
	 * @return
	 */
	public boolean SaveBKLayer(String BKMapType,List<HashMap<String,Object>> BKMapFileList)
	{
		if (BKMapType.equals("ʸ��"))
		{
			//�����ͼ�ļ���Ϣ
			String[] FieldList = {"Type","BKMapFile","MinX","MinY","MaxX","MaxY","CoorSystem","Transparent","Sort","Visible","F1"};
			String SQL_DEL = "delete from T_BKLayer where Type = 'ʸ��'";
			
			//���沢����
			if (this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_DEL))
			{
				String SQL_INS = "insert into T_BKLayer (%1$s) values ('%2$s')";
				for(HashMap<String,Object> ho:BKMapFileList)
				{
					SQL_INS = "insert into T_BKLayer (%1$s) values ('%2$s')";
					
					List<String> ValueList = new ArrayList<String>();
					for(String field:FieldList)ValueList.add(ho.get(field)+"");
					SQL_INS = String.format(SQL_INS,Tools.Joins(",", FieldList),Tools.JoinT("','", ValueList));
					this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_INS);
				}
			} else return false;
			this.m_BKVectorLayerExplorer.SetBKFileList(BKMapFileList);
			this.m_BKVectorLayerExplorer.ClearVectorLayer();
			this.m_BKVectorLayerExplorer.OpenVectorDataSource();
			return true;
		}
		if (BKMapType.equals("դ��"))
		{
			this.m_BKGridLayerExplorer.SetBKFileList(BKMapFileList);
			if (this.m_BKGridLayerExplorer.SaveBKLayer())
			{
				this.m_BKGridLayerExplorer.OpenGridDataSource();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �ж������б��Ƿ���ͬ
	 * @param list1
	 * @param list2
	 * @return
	 */
	private boolean ListEqual(List<HashMap<String,Object>> list1,List<HashMap<String,Object>> list2)
	{
		if (list1==null && list2==null) return true;
		if (list1==null || list2==null) return false;
		if (list1.size()!=list2.size()) return false;
		for(int i=0;i<list1.size();i++)
		{
			if (!list1.get(i).equals(list2.get(i))) return false;
		}
		return true;
	}
	
	/**
	 * ת������ʵ�嵽�б�
	 * @param josnObjectStr
	 * @return
	 */
	private List<HashMap<String,Object>> JSONObjectToList(String josnObjectStr)
	{
		List<HashMap<String,Object>> BKMapFileList = new ArrayList<HashMap<String,Object>>();
		try 
		{  
		    JSONTokener jsonParser = new JSONTokener(josnObjectStr);  
		    JSONObject AllLayerJSON = (JSONObject)jsonParser.nextValue();  
		    
		    // �������ľ���JSON����Ĳ�����  
		    JSONArray lyrJSONList = AllLayerJSON.getJSONArray("AllBKMapList");  
		    for(int i=0;i<lyrJSONList.length();i++)
		    {
		    	HashMap<String,Object> hmObj = new HashMap<String,Object>();
		    	JSONObject lyrJSON = lyrJSONList.getJSONObject(i);
		    	hmObj.put("Select", lyrJSON.getBoolean("Select"));				//�Ƿ�ѡ��Ĭ��true
		    	hmObj.put("GridTransparent", (lyrJSON.has("GridTransparent")?lyrJSON.getString("GridTransparent"):255));//դ��ͼ��͸����
		    	hmObj.put("MapFileName", lyrJSON.getString("MapFileName"));		//����ͼ�ļ����ƣ�ʸ��[*.vmx]��դ��[*.imx])
		    	hmObj.put("MinX", lyrJSON.getString("MinX"));					//���Χ������դ��ͼ������Ϊ�ж��Ƿ���ص�����
		    	hmObj.put("MinY", lyrJSON.getString("MinY"));
		    	hmObj.put("MaxX", lyrJSON.getString("MaxX"));
		    	hmObj.put("MaxY", lyrJSON.getString("MaxY"));
		    	hmObj.put("CoorSystem", lyrJSON.getString("CoorSystem"));		//����ϵͳ����ʽ���磺����80��129������WGS84
		    	BKMapFileList.add(hmObj);
		    }
		} catch (JSONException ex) {  
		   return null;
		}  
		return BKMapFileList;
	}
	
	/**
	 * �������б�ת��ΪString
	 * @return
	 */
	public static String ListToJSONObject(List<HashMap<String,Object>> BKMapFileList)
	{
		try
		{
			JSONObject LyrAllJSON = new JSONObject();  
			JSONArray LyrJSONList = new JSONArray(); 

			for(HashMap<String,Object> bmMapFile:BKMapFileList)
			{
				JSONObject LyrJSON = new JSONObject();  
				LyrJSON.put("Select", true);
				LyrJSON.put("GridTransparent", bmMapFile.get("GridTransparent"));
				LyrJSON.put("MapFileName", bmMapFile.get("MapFileName"));
				LyrJSON.put("MinX", bmMapFile.get("MinX")==null?0:bmMapFile.get("MinX"));  
				LyrJSON.put("MinY", bmMapFile.get("MinY")==null?0:bmMapFile.get("MinY"));  
				LyrJSON.put("MaxX", bmMapFile.get("MaxX")==null?0:bmMapFile.get("MaxX"));  
				LyrJSON.put("MaxY", bmMapFile.get("MaxY")==null?0:bmMapFile.get("MaxY"));  
				LyrJSON.put("CoorSystem", bmMapFile.get("CoorSystem")==null?"":bmMapFile.get("CoorSystem"));  
				LyrJSONList = LyrJSONList.put(LyrJSON);
			}
			LyrAllJSON.put("AllBKMapList", LyrJSONList);
			return LyrAllJSON.toString();
		} catch (JSONException ex) {  
		    return "";
		} 
		
	}
}
