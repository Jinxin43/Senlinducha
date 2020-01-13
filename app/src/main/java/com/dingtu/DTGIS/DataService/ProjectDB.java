package com.dingtu.DTGIS.DataService;


import java.util.HashMap;

import com.dingtu.DTGIS.Upload.HttpProjectModel;

import android.R.bool;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;

public class ProjectDB {
	
	String dbPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/project.dbx";
	SQLiteDatabase m_SQLiteDatabase=null;
	String mTableName = "T_Layer";

	
	public ProjectDB()
	{
		m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbPath,null);
		
	}
	
	public HttpProjectModel getHttpProjectModel()
	{
		HttpProjectModel httpProjectModel = null;
		String sql = "select * from T_Project where id=2";
		SQLiteDataReader reader=  new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
		if(reader.Read())
		{
			httpProjectModel = new HttpProjectModel();
			httpProjectModel.setName(reader.getUnNullString("ProjectName"));
			httpProjectModel.setCreateTime(reader.getUnNullString("CreateTime"));
			httpProjectModel.setCoorSystem(reader.getUnNullString("CoorSystem"));
			httpProjectModel.setCenterMeridian(reader.getUnNullString("CenterMeridian"));
			httpProjectModel.setTransMethod(reader.getUnNullString("TransMethod"));
			httpProjectModel.setPmTransMethod(reader.getUnNullString("PMTransMethod"));
			httpProjectModel.setP31(reader.getUnNullString("P31"));
			httpProjectModel.setP32(reader.getUnNullString("P32"));
			httpProjectModel.setP33(reader.getUnNullString("P33"));
			
			httpProjectModel.setP41(reader.getUnNullString("P41"));
			httpProjectModel.setP42(reader.getUnNullString("P42"));
			httpProjectModel.setP43(reader.getUnNullString("P43"));
			httpProjectModel.setP44(reader.getUnNullString("P44"));
			
			httpProjectModel.setP71(reader.getUnNullString("P71"));
			httpProjectModel.setP72(reader.getUnNullString("P72"));
			httpProjectModel.setP73(reader.getUnNullString("P73"));
			httpProjectModel.setP74(reader.getUnNullString("P74"));
			httpProjectModel.setP75(reader.getUnNullString("P75"));
			httpProjectModel.setP76(reader.getUnNullString("P76"));
			httpProjectModel.setP77(reader.getUnNullString("P77"));
			
			httpProjectModel.setF1(reader.getUnNullString("F1"));
			httpProjectModel.setF2(reader.getUnNullString("F2"));
			httpProjectModel.setF3(reader.getUnNullString("F3"));
			httpProjectModel.setF4(reader.getUnNullString("F4"));
			httpProjectModel.setF5(reader.getUnNullString("F5"));
			httpProjectModel.setF6(reader.getUnNullString("F6"));
			httpProjectModel.setF7(reader.getUnNullString("F7"));
			httpProjectModel.setF8(reader.getUnNullString("F8"));
			httpProjectModel.setF9(reader.getUnNullString("F9"));
			httpProjectModel.setF10(reader.getUnNullString("F10"));
		}
		
		return httpProjectModel;
	}
	
	public String getProjectServerId()
	{
		String serverId="";
		String sql = "select F10 from T_Project where id=2";
		SQLiteDataReader reader=  new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
		if(reader.Read())
		{
			serverId = reader.GetString("F10");
		}
		
		return serverId;
	}
	
	public boolean updateProjectServerId(String serverId)
	{
		boolean result = true;
		String sql = "UPDATE T_Project set F10 = '"+serverId+"' where id=2";
		try {
			m_SQLiteDatabase.execSQL(sql);
		} catch (Exception e) {
			Log.e("update project server Id", e.getMessage());
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	public HashMap<String, Object> getImagetEffect()
	{
		HashMap<String, Object> result = new HashMap<String, Object>();
		String sql = "select Name,F2,F3 from T_ProjectUserConfig where Name='底图画质增强'";
		SQLiteDataReader reader =  new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
		if(reader.Read())
		{

			if(reader.GetString("Name") == null)
			{
				result.put("isEffect", false);
			}
			else {
				result.put("isEffect", true);
				result.put("bright", reader.GetString("F2"));
				result.put("contrast", reader.GetString("F3"));
			}
			
		}
		else
		{
			result.put("isEffect", false);
		}
		reader.Close();
		return result;
	}
	
	public Boolean updateImageEffect(HashMap<String, Object> hashMap)
	{
		String sql ;
		Boolean isEffect = (Boolean)hashMap.get("isEffect");
		Boolean result = false;
		if(isEffect)
		{
			String bright = hashMap.get("bright")+"";
			String saturation = hashMap.get("contrast")+"";
			
			sql = "select Name,F2,F3 from T_ProjectUserConfig where Name='底图画质增强'";
			SQLiteDataReader reader=  new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			Boolean isExist = reader.Read();
			reader.Close();
			if(isExist)
			{
				
				sql = "UPDATE T_ProjectUserConfig set Name='底图画质增强',F2 = '"+bright+"',F3='"+saturation+"' where Name='底图画质增强'";
				try {
					m_SQLiteDatabase.execSQL(sql);
					result = true;
				} catch (Exception e) 
				{
				}
			}
			else
			{
				sql = "Insert into T_ProjectUserConfig (Name,F2,F3) Values('底图画质增强','"+bright+"','"+saturation+"')";
				try {
					m_SQLiteDatabase.execSQL(sql);
					result = true;
				} catch (Exception e) 
				{
				}
			}
		}
		else
		{
			sql = "delete from T_ProjectUserConfig where  Name='底图画质增强'";
			try {
				m_SQLiteDatabase.execSQL(sql);
				result = true;
			} catch (Exception e) 
			{
			}
		}
		return result;
	}
	
	
	public void Close()
	{
		try
		{
			m_SQLiteDatabase.close();
		}
		catch(Exception ex)
		{
			
		}
	}
}
