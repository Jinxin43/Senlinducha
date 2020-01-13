package com.dingtu.DTGIS.DataService;

import java.io.File;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.TaskStackBuilder;
import android.database.sqlite.SQLiteDatabase;
import android.inputmethodservice.Keyboard.Key;
import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.GPS.LocationEx;
import lkmap.Map.StaticObject;
import lkmap.Tools.Tools;

public class LogDB 
{
	SQLiteDatabase m_SQLiteDatabase = null;
	String logPath = PubVar.m_SysAbsolutePath+"/Log";
	
	public LogDB()
	{
		
	}
	
	private boolean openTrackDB(String trackYear)
	{
		
		try
		{
			if(!lkmap.Tools.Tools.ExistFile(logPath))
			{
				if (!(new File(logPath)).mkdirs())
		    	{
					Log.e("CreateLogDirFail","mkdirs "+ logPath +" fail");
					return false;
		    	}
			}
			m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(logPath+"/GPS"+trackYear+".dbx", null);
		}
		catch(Exception ex)
		{
			Log.e("OpenGPSDB","Create or Open GPS DB faild. "+ex.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean logGps(String year, String date,LocationEx location)
	{
		if(openTrackDB(year))
		{
			if(CreateTable(date))
			{
				try
				{ 
					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
		        	String currentTime = sDateFormat.format(new java.util.Date()); 
		        	
					String sql = "insert into TGPS"+date+" (GPSTime,Longitude,Latitude,Altitude,Speed,PDOP,project,recordTime) values " +
					   		  "('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s','%8$s')";
					sql = String.format(sql, location.GetGpsDate()+" "+location.GetGpsTime(),
												location.GetGpsLongitude(),
												location.GetGpsLatitude(),
												location.GetGpsAltitude(),
												location.GetGpsSpeed(),
												location.GetGpsPDOP(),
												PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectShortName(),
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
	
	private boolean CreateTable(String date)
	{
		try
		{
			String sql = "CREATE TABLE if not exists TGPS"+date+" ("+
					" ID         INTEGER PRIMARY KEY AUTOINCREMENT,"+
					"GPSTime    TIME,"+
					"Longitude  VARCHAR,"+
					"Latitude   VARCHAR,"+
					"Altitude   VARCHAR,"+
					"Speed      VARCHAR,"+
					"PDOP       VARCHAR,"+
					"project    VARCHAR,"+
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
			Log.e("CreateGPSTable", ex.getMessage());
			return false;
		}
		
		return true;
	}
	
	public List<UploadTraceModel> QueryUnloadPoint(Date startDate,Date endDate)
	{
		List<UploadTraceModel> allPoint = new ArrayList<UploadTraceModel>();
		try
		{
			SQLiteDatabase  database = SQLiteDatabase.openDatabase(logPath+"/GPS"+(startDate.getYear()+1900)+".dbx",null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			if(database == null)
			{
				return allPoint;
			}
			
			SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat allSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String sql = "";
			//结束日期不能早于开始日期
			if(startDate.getMonth()== endDate.getMonth() && startDate.getDate()==endDate.getDate())
			{
				if(isExistTable("TGPS"+SDF.format(startDate), database))
				{
					sql = "select * from TGPS"+SDF.format(startDate)+" where F1 is NULL";//+
							//" where gpsTime >= '"+allSDF.format(startDate)+"'and gpsTime <= '"+allSDF.format(endDate)+"'";
					allPoint.addAll(parseUnloadPointFromReader(sql,database));
				}
			}
			else
			{
				if(isExistTable("TGPS"+SDF.format(startDate), database))
				{
					sql = "select * from TGPS"+SDF.format(startDate)+" where F1 is NULL";//+
							//" where gpsTime >= '"+allSDF.format(startDate)+"'";
					allPoint.addAll(parseUnloadPointFromReader(sql,database));
				}
				
				Date sDate = SDF.parse(SDF.format(startDate));
				Date eDate = SDF.parse(SDF.format(endDate));
				Calendar calStart = Calendar.getInstance();
				calStart.setTime(sDate);
				calStart.add(Calendar.DATE, 1);
				Calendar calEnd = Calendar.getInstance();
				calEnd.setTime(eDate);
				while (calStart.before(calEnd)) 
				{
					if(isExistTable("TGPS"+SDF.format(calStart.getTime()), database))
					{
						sql = "select * from TGPS"+SDF.format(calStart.getTime())+" where F1 is NULL";
						allPoint.addAll(parseUnloadPointFromReader(sql,database));
					}
					
					calStart.add(Calendar.DATE, 1);
				}
				
				if(isExistTable("TGPS"+SDF.format(endDate), database))
				{
					sql = "select * from TGPS"+SDF.format(endDate)+" where F1 is NULL";
							//" where gpsTime <= '"+allSDF.format(endDate)+"'";
					allPoint.addAll(parseUnloadPointFromReader(sql,database));
				}
			}
			
			database.close();
		}
		catch(Exception e)
		{
			Log.e("查询航迹", e.getMessage());
		}
		
		return allPoint;
	}
	
	public List<GPSPoint> QueryGPSPoint(Date startDate,Date endDate)
	{
		List<GPSPoint> allPoint = new ArrayList<GPSPoint>();
		try
		{
			SQLiteDatabase  database = SQLiteDatabase.openDatabase(logPath+"/GPS"+(startDate.getYear()+1900)+".dbx",null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			if(database == null)
			{
				return allPoint;
			}
			
			SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat allSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String sql = "";
			//结束日期不能早于开始日期
			if(startDate.getMonth()== endDate.getMonth() && startDate.getDate()==endDate.getDate())
			{
				if(isExistTable("TGPS"+SDF.format(startDate), database))
				{
					sql = "select * from TGPS"+SDF.format(startDate);//+
							//" where gpsTime >= '"+allSDF.format(startDate)+"'and gpsTime <= '"+allSDF.format(endDate)+"'";
					allPoint.addAll(parsePointFromReader(sql,database));
				}
			}
			else
			{
				if(isExistTable("TGPS"+SDF.format(startDate), database))
				{
					sql = "select * from TGPS"+SDF.format(startDate);//+
							//" where gpsTime >= '"+allSDF.format(startDate)+"'";
					allPoint.addAll(parsePointFromReader(sql,database));
				}
				
				Date sDate = SDF.parse(SDF.format(startDate));
				Date eDate = SDF.parse(SDF.format(endDate));
				Calendar calStart = Calendar.getInstance();
				calStart.setTime(sDate);
				calStart.add(Calendar.DATE, 1);
				Calendar calEnd = Calendar.getInstance();
				calEnd.setTime(eDate);
				while (calStart.before(calEnd)) 
				{
					if(isExistTable("TGPS"+SDF.format(calStart.getTime()), database))
					{
						sql = "select * from TGPS"+SDF.format(calStart.getTime());
						allPoint.addAll(parsePointFromReader(sql,database));
					}
					
					calStart.add(Calendar.DATE, 1);
				}
				
				if(isExistTable("TGPS"+SDF.format(endDate), database))
				{
					sql = "select * from TGPS"+SDF.format(endDate);
							//" where gpsTime <= '"+allSDF.format(endDate)+"'";
					allPoint.addAll(parsePointFromReader(sql,database));
				}
			}
			
			database.close();
		}
		catch(Exception e)
		{
			Log.e("查询航迹", e.getMessage());
		}
		
		return allPoint;
	}
	
	private ArrayList<UploadTraceModel> parseUnloadPointFromReader(String sql,SQLiteDatabase database)
	{
		SQLiteDataReader reader = new SQLiteDataReader(database.rawQuery(sql, null));
		
		ArrayList<UploadTraceModel> allPoint = new ArrayList<UploadTraceModel>();
		
		while (reader.Read()) 
		{
			UploadTraceModel point = new UploadTraceModel();
			point.setId(reader.GetInt32("ID"));
			point.setGPSTime(reader.GetString("GPSTime"));
			point.setLatitude(reader.GetString("Latitude"));
			point.setLongitude(reader.GetString("Longitude"));
			point.setAltitude(reader.GetString("Altitude"));
			point.setPDOP(reader.GetString("PDOP"));
			point.setproject(reader.GetString("project"));
			point.setSpeed(reader.GetString("Speed"));
			point.setRecordTime(reader.GetString("recordTime"));
			
			Coordinate newCoor = StaticObject.soProjectSystem.WGS84ToXY(Double.parseDouble(point.getLongitude()), Double.parseDouble(point.getLatitude()),0);
			if(newCoor != null)
			{
				point.setX(newCoor.getX());
				point.setY(newCoor.getY());
				allPoint.add(point);	
			}
			
		}
		
		return allPoint;
	}
	
	private ArrayList<GPSPoint> parsePointFromReader(String sql,SQLiteDatabase database)
	{
		SQLiteDataReader reader = new SQLiteDataReader(database.rawQuery(sql, null));
		
		ArrayList<GPSPoint> allPoint = new ArrayList<GPSPoint>();
		
		while (reader.Read()) 
		{
			GPSPoint point = new GPSPoint();
			point.setId(reader.GetInt32("ID"));
			point.setGPSTime(reader.GetString("GPSTime"));
			point.setLatitude(reader.GetString("Latitude"));
			point.setLongitude(reader.GetString("Longitude"));
			point.setAltitude(reader.GetString("Altitude"));
			point.setPDOP(reader.GetString("PDOP"));
			point.setproject(reader.GetString("project"));
			point.setSpeed(reader.GetString("Speed"));
			point.setRecordTime(reader.GetString("recordTime"));
			
			allPoint.add(point);
		}
		
		return allPoint;
	}
	
	private boolean isExistTable(String tableName,SQLiteDatabase database)
	{
		String sql = "select count(*) as Count  from sqlite_master where type='table' and name = '"+tableName+"';";
		SQLiteDataReader reader = new SQLiteDataReader(database.rawQuery(sql, null));
		if(reader != null)
		{
			reader.Read();
			if(reader.GetInt32("Count")>0)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void updateUploadStatus(List<String> gpsTimes)
	{
		//TODO:暂不考虑跨年
		String year = gpsTimes.get(0).substring(0, 4);
		SQLiteDatabase  database = SQLiteDatabase.openDatabase(logPath+"/GPS"+year+".dbx",null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		if(database == null)
		{
			return ;
		}
		
		HashMap<String, ArrayList<String>> tables = new HashMap<String, ArrayList<String>>();
		for(String gTime:gpsTimes)
		{
			String key = "TGPS"+gTime.substring(0,10).replace("-", "").trim();
			ArrayList<String> times = tables.get(key);
			if(times == null)
			{
				times = new ArrayList<String>();
			}
			times.add(gTime);
			tables.put(key, times);
		}
		
		for(String t:tables.keySet())
		{
			if(isExistTable(t, database))
			{
				StringBuilder strTimes=new StringBuilder();
				int index= 0;
				for(String g:tables.get(t))
				{
					if(index>0)
					{
						strTimes.append(",'"+g+"'");
					}
					else
					{
						strTimes.append("'"+g+"'");
					}
					index++;
				}
				String sql = "update "+ t +" set F1 = '已上传' where GPSTime IN ("+strTimes+")";
			
				try
				{
					database.execSQL(sql);
				}
				catch(Exception ex)
				{
					Log.e("updateUploadStatus", ex.getMessage());
				}
				
			}
			
		}
		
		Log.v("updateUploadStatus", "done");
		database.close();
		
	}
}
