package com.dingtu.DTGIS.DataService;

import java.util.Date;

import android.R.string;

public class GPSPoint 
{
	private int Id = 0;
	public int getId()
	{
		return Id;
	}
	public void setId(int id)
	{
		this.Id = id;
	}
	
	
	private String mgpsTime;
	public String getGPSTime()
	{
		return mgpsTime;
	};
	
	
	public void setGPSTime(String gpsTime)
	{
		mgpsTime = gpsTime;
	};
	
	private String mLongitude;
	public String getLongitude()
	{
		return mLongitude;
	};
	public void setLongitude(String longitude)
	{
		mLongitude = longitude;
	};
	
	private String mLatitude;
	public String getLatitude()
	{
		return mLatitude;
		
	};
	public void setLatitude(String latitude)
	{
		mLatitude = latitude;
	};
	
	private String mAltitude;
	public String getAltitude()
	{
		return mAltitude;
	};
	public void setAltitude(String altit)
	{
		mAltitude= altit;
	};
	
	private String mSpeed;
	public String getSpeed()
	{
		return mSpeed;
	};
	public void setSpeed(String speed)
	{
		mSpeed = speed;
	};
	
	private String mPDOP;
	public String getPDOP()
	{
		return mPDOP;
	};
	public void setPDOP(String pdop)
	{
		mPDOP = pdop;
	};
	
	private String mProject;
	public String getproject()
	{
		return mProject;
	};
	public void setproject(String prjname)
	{
		mProject = prjname;
	};
	
	private String mRecondTime;
	public String getRecordTime()
	{
		return mRecondTime;
	};
	public void setRecordTime(String time)
	{
		mRecondTime = time;
	};
	
	
}
