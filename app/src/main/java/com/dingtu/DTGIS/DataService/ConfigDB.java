package com.dingtu.DTGIS.DataService;

import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.database.sqlite.SQLiteDatabase;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

public class ConfigDB 
{
	String configPath = PubVar.m_SysAbsolutePath+"/SysFile/Config.dbx";
	SQLiteDatabase m_SQLiteDatabase=null;
	
	public ConfigDB()
	{
		m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
	}
	
	public static int getVersion(String configpath)
	{
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(configpath,null);
		int dbversion =0;
		String readSql = "select * from dbconfig where field='configversion'";
		try
		{
			SQLiteDataReader reader = new SQLiteDataReader(database.rawQuery(readSql, null));
			while (reader.Read()) 
			{
				String version = reader.GetString("value");
				if(version != null&& version.length()>0)
				{
					dbversion = Integer.parseInt(version);
					
				}
			}
		}catch(Exception ex)
		{
			
		}
		
		database.close();
		return dbversion;
				
	}
	
	
	public List<DaDiPoint> getAllPoint()
	{
		ArrayList<DaDiPoint> allPoint = new ArrayList<DaDiPoint>();
		try
		{
			
			String readSql = "select DianHao,LatDegree,LatFen,LatMiao,LonDegree,LonFen,LonMiao,[105X],[105Y],[108X],[108Y],[111X],[111Y] from DDTG ";
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(readSql, null));
			while (reader.Read()) 
			{
				DaDiPoint point = new DaDiPoint();
				point.setDianhao(reader.GetString(0));
				point.setLatDu(Integer.parseInt(reader.GetString("LatDegree")));
				point.setLatFen(Integer.parseInt(reader.GetString("LatFen")));
				point.setLatMiao(Double.parseDouble(reader.GetString("LatMiao")));
				point.setLonDu(Integer.parseInt(reader.GetString("LonDegree")));
				point.setLonFen(Integer.parseInt(reader.GetString("LonFen")));
				point.setLonMiao(Double.parseDouble(reader.GetString("LonMiao")));
				point.setX105(reader.GetDouble("105X"));
				point.setY105(reader.GetDouble("105Y"));
				point.setX108(reader.GetDouble("108X"));
				point.setY108(reader.GetDouble("108Y"));
				point.setX111(reader.GetDouble("111X"));
				point.setY111(reader.GetDouble("111Y"));
				allPoint.add(point);
			}
			
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
			return null;
		}
		
		return allPoint;
	}
	
	public void closeDB()
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
