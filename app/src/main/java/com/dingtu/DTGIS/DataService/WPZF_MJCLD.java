package com.dingtu.DTGIS.DataService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.GPS.LocationEx;

public class WPZF_MJCLD 
{
	private String dbName;
	SQLiteDatabase m_SQLiteDatabase = null;
	
	public WPZF_MJCLD()
	{
		dbName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName();
		m_SQLiteDatabase = SQLiteDatabase.openDatabase(dbName,null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		
	}
	
	private boolean openMJCLTable()
	{
		try
		{
			m_SQLiteDatabase = SQLiteDatabase.openDatabase(dbName,null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}
		catch(Exception ex)
		{
			Log.e("OpenMJCLDB","Create or Open GPS DB faild. "+ex.getMessage());
			return false;
		}
		
		return true;
	}
	
	private boolean CreateTable(String tableName)
	{
		try
		{
			String sql = "CREATE TABLE if not exists "+tableName+" ("+
					"ID         INTEGER PRIMARY KEY AUTOINCREMENT,"+
					"LayerID    VARCHAR,"+
					"ObjID      VARCHAR,"+
					"SortID     INTEGER,"+
					"GPSTime    TIME,"+
					"Longitude  VARCHAR,"+
					"Latitude   VARCHAR,"+
					"Altitude   VARCHAR,"+
					"FWJ        VARCHAR,"+
					"QXJ        VARCHAR,"+
					"XJ         VARCHAR,"+
					"recordTime TIME,"+
					"F1         VARCHAR,"+
					"F2         VARCHAR,"+
					"F3         VARCHAR,"+
					"F4         VARCHAR,"+
					"F5         VARCHAR)";
			
			 this.m_SQLiteDatabase.execSQL(sql);
		}
		catch(Exception ex)
		{
			Log.e("CreateMJCLDB", ex.getMessage());
			return false;
		}
		
		return true;
	}
	
	public List<HashMap<String,Object>> QueryCLD(String layerID,String objID,String tableName)
	{
		List<HashMap<String,Object>> allPoint = new ArrayList<HashMap<String,Object>>();
		String sql = "select * from tableName where LayerID='"+layerID+"' and ObjID='"+objID+"' and order by SortID";
		
		try
		{
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			while (reader.Read()) 
			{
				HashMap<String,Object> item = new HashMap<String,Object>();
				item.put("ID", reader.GetInt32("ID"));
				item.put("SortID", reader.GetInt32("SortID"));
				item.put("Longitude", reader.GetString("Longitude"));
				item.put("Latitude", reader.GetString("Latitude"));
				item.put("FWJ", reader.GetString("FWJ"));
				item.put("QXJ", reader.GetString("QXJ"));
				item.put("XJ", reader.GetString("XJ"));
				
				allPoint.add(item);
			}
		}
		catch(Exception ex)
		{
			
		}
		
		return allPoint;
	}
	
	public boolean DeleteAll(String tableName,String pLayerID,String pObjID)
	{
		boolean result = false;
		if(openMJCLTable())
		{
			if(CreateTable(tableName))
			{
				try
				{
					String sql = "delete "+tableName+" where LayerID='"+pLayerID+"' and ObjID='"+pObjID+"'"; 
					this.m_SQLiteDatabase.execSQL(sql);
					result = true;
				}
				catch(Exception ex)
				{
					
					result =  false;
				}
				
			}
		}
		return result;
	}
	
	@SuppressLint("SimpleDateFormat")
	public boolean AddCLD(String tableName,String pLayerID,String pObjID,String pfwj,String pqxj,String pxj,int sortID,LocationEx location)
	{
		if(openMJCLTable())
		{
			if(CreateTable(tableName))
			{
				try
				{ 
					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
		        	String currentTime = sDateFormat.format(new java.util.Date()); 
		        	
					String sql = "insert into"+ tableName +"(LayerID,ObjID,SortID,GPSTime,Longitude,Latitude,Altitude,FWJ,QXJ,XJ,recordTime) values " +
					   		  "('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s','%8$s','%9$s','%10$s','%11$s')";
					sql = String.format(sql, pLayerID,
											 pObjID,
											 sortID,
												location.GetGpsDate()+" "+location.GetGpsTime(),
												location.GetGpsLongitude(),
												location.GetGpsLatitude(),
												location.GetGpsAltitude(),
												pfwj,
												pqxj,
												pxj,
												currentTime);
					this.m_SQLiteDatabase.execSQL(sql);
					this.m_SQLiteDatabase.close();
					
				}
				catch(Exception e)
				{
					Log.e("LogGPS", e.getMessage());
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
		
		return true;
	}

}
