package com.dingtu.DTGIS.DataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

public class TuiGengDB 
{
	SQLiteDatabase m_SQLiteDatabase = null;
	String dbPath = PubVar.m_SysAbsolutePath+"/Data/";
	String tableName = "";
	
	public TuiGengDB()
	{
		dbPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName();
		m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbPath,null);
	}
	
	public boolean saveDuixianData(String layerID,int objID,ArrayList<String> dataList)
	{
		boolean result = false;
		try
		{ 
			String sql = "update "+layerID+"_D set F68='%1$s', F69='%2$s',F70='%3$s',F71='%4$s',F72='%5$s',F73='%6$s',F74='%7$s',F75='%8$s' where SYS_ID ="+objID;
			sql = String.format(sql, dataList.get(0),dataList.get(1),dataList.get(2),dataList.get(3),dataList.get(4),dataList.get(5),dataList.get(6),dataList.get(7));
			m_SQLiteDatabase.execSQL(sql);
			result = true;
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(ex.getMessage());
		}
		
		return result;
	}
	
	public boolean saveFenhuData(String layerID,int objID,List<HashMap<String,Object>> dataList)	
	{
		boolean result = false;
		String humings = "";
		String diles = "";
		String mianjis ="";
		String linzhongs = "";
		String shuzhongs ="";
		String miaomus ="";
		String zhongzis ="";
		String sfpinkunhu="";
		
		int i = 0;
		for(HashMap<String,Object> hm:dataList)
		{
			if(i == 0)
			{
				humings = hm.get("huming")+"";
				diles = hm.get("dilei")+"";
				mianjis = hm.get("mianji")+"";
				linzhongs = hm.get("linzhong")+"";
				shuzhongs = hm.get("shuzhong")+"";
				miaomus = hm.get("miaomu")+"";
				zhongzis = hm.get("zhongzi")+"";
				sfpinkunhu = hm.get("pinkunhu")+"";
				
			}
			else
			{
				humings += ","+hm.get("huming");
				diles += ","+hm.get("dilei");
				mianjis += ","+hm.get("mianji");
				linzhongs += ","+hm.get("linzhong");
				shuzhongs += ","+hm.get("shuzhong");
				miaomus += ","+hm.get("miaomu");
				zhongzis += ","+hm.get("zhongzi");
				sfpinkunhu += ","+hm.get("pinkunhu")+"";
			}
			i++;
		}
		
		String sql = "update "+layerID+"_D set F61='%1$s', F62='%2$s',F63='%3$s',F64='%4$s',F65='%5$s',F66='%6$s',F67='%7$s',F77='%8$s' where SYS_ID ="+objID;
		sql = String.format(sql, humings,diles,mianjis,linzhongs,shuzhongs,miaomus,zhongzis,sfpinkunhu);
		try
		{
			m_SQLiteDatabase.execSQL(sql);
			result = true;
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(ex.getMessage());
		}
		
		return result;
	}
	
	public HashMap<String, String> getFenhuData(String layerID,String objId)
	{
		HashMap<String, String> hMap = new HashMap<String, String>();
		try
		{
			String sql = "select F10,F3,F4,F7,F61,F62,F63,F64,F65,F66,F67,F77 from "+layerID+"_D where SYS_ID="+objId;
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			if(reader.Read())
			{
				hMap.put("����s", reader.GetString("F3"));
				hMap.put("���ƴ�s", reader.GetString("F4"));
				hMap.put("С��s", reader.GetString("F7"));
				hMap.put("����s", reader.GetString("F61"));
				hMap.put("����s", reader.GetString("F62"));
				hMap.put("���s", reader.GetString("F63"));
				hMap.put("����s", reader.GetString("F64"));
				hMap.put("����s", reader.GetString("F65"));
				hMap.put("��ľs", reader.GetString("F66"));
				hMap.put("����s", reader.GetString("F67"));
				hMap.put("����ҵ���", reader.GetDouble("F10")+"");
				hMap.put("ƶ����s", reader.GetString("F77"));
			}
			reader.Close();
			
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
			return hMap;
		}
		
		return hMap;
	}
	
	public HashMap<String,String> getDuixianData(String layerID, String objId)
	{
		HashMap<String, String> hMap = new HashMap<String, String>();
		try
		{
			
			String sql = "select F68,F69,F70,F71,F72,F73,F74,F75 from "+layerID+"_D where SYS_ID="+objId;
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			if(reader.Read())
			{
				hMap.put("D����", reader.GetString("F68"));
				hMap.put("D�������", reader.GetString("F69"));
				hMap.put("D���ֽ��", reader.GetString("F70"));
				hMap.put("D���߲���", reader.GetString("F71"));
				hMap.put("D������׼", reader.GetString("F72"));
				hMap.put("D�����", reader.GetString("F73"));
				hMap.put("D�Ƿ����", reader.GetString("F74"));
				hMap.put("D��ע", reader.GetString("F75"));
				
			}
			reader.Close();
			
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
			return hMap;
		}
		
		return hMap;
	}
	
	public boolean updateLayerField(String layerID,String fieldJSON)
	{
		boolean result = false;
		
		String sql = "update T_Layer set FieldList='"+fieldJSON+"' where LayerID='"+layerID+"'";
		return PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sql);
		
	}
	
}
