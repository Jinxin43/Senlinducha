package com.dingtu.DTGIS.DataService;

import java.util.HashMap;

import android.R.string;
import android.database.sqlite.SQLiteDatabase;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

public class UserConfigDB 
{
	String configPath = PubVar.m_SysAbsolutePath+"/SysFile/UserConfig.dbx";
	SQLiteDatabase m_SQLiteDatabase=null;
	
	public UserConfigDB()
	{
		m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
	}
	
	public boolean SavePreviewTGHL(String value)
	{
		boolean isUpdate = false;
		try
		{
			
			String readSql = "select F1 from T_UserParam where F1='previewTGHLvalue' ";
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(readSql, null));
			if(reader.Read())
			{
				if(reader.GetCount()>0)
				{
					isUpdate = true;
				}
			}
			reader.Close();
			
			String sql = "insert into T_UserParam (F1,F2) values('previewTGHLvalue','"+value+"')";
			
			if(isUpdate)
			{
				sql = "update T_UserParam set F2='"+value+"' where F1='previewTGHLvalue'";
			}
		
			m_SQLiteDatabase.execSQL(sql);
			m_SQLiteDatabase.close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
			return false;
		}
		
		return true;
	}
	
	public String readPreviewTGHL()
	{
		String result ="";
		try
		{
			String readSql = "select F2 from T_UserParam where F1='previewTGHLvalue' ";
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(readSql, null));
			if(reader.Read())
			{
				if(reader.GetCount()>0)
				{
					result = reader.GetString("F2");
				}
			}
			reader.Close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
			
		}
		
		return result;
	}
	
	public void addTransformPoint(HashMap<String, Object> calcPoints)
	{
		try
		{
			String sql = "insert into T_TransformationPointPair (SourceX,SourceY,TargetX,TargetY) values " +
			   		  "('%1$s','%2$s','%3$s','%4$s')";
			sql = String.format(sql, calcPoints.get("X1"),
									 calcPoints.get("Y1"),
									 calcPoints.get("X2"),
									 calcPoints.get("Y2"));
			
			this.m_SQLiteDatabase.execSQL(sql);
			this.m_SQLiteDatabase.close();
			
		}
		catch(Exception ex){
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "保存计算点失败! "+ex.getMessage());
		}
		
	}
	
	public HashMap<String, Object> getTransformPoint()
	{
		HashMap<String, Object> result = new HashMap<String, Object>();
		String readSql = "select * from T_TransformationPointPair ";
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(readSql, null));
		while(reader.Read())
		{
			result.put("X1", reader.GetString("SourceX"));
			result.put("Y1", reader.GetString("SourceY"));
			result.put("X2", reader.GetString("TargetX"));
			result.put("Y2", reader.GetString("TargetY"));
		}
		reader.Close();
		return result;
	}
	
	public boolean addSelectedShuZhong(String selectedSZ)
	{
		boolean isOK = false;
		String result = getSelectedShuZhong();
		String sql;
		if(result == null)
		{
			sql = "insert into T_UserParam (F1,F2) values('SelectedShuZhong','"+selectedSZ+"')";
		}
		else
		{
			sql = "update T_UserParam set F2='"+selectedSZ+"' where F1='SelectedShuZhong'";
		}
		
		try {
			
			this.m_SQLiteDatabase.execSQL(sql);
			this.m_SQLiteDatabase.close();
			isOK = true;
			
		} catch (Exception e) {
			Tools.ShowMessageBox("保存常选树种出错："+e.getMessage());
		}
		
		return isOK;
	}
	
	
	public String getSelectedShuZhong()
	{
		String result=null;
		String readSql = "select * from T_UserParam where F1='SelectedShuZhong'";
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(readSql, null));
		while(reader.Read())
		{
			result = reader.GetString("F2");
		}
		return result;
	}
}
