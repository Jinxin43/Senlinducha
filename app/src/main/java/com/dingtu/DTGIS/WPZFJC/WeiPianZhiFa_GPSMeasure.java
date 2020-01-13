package com.dingtu.DTGIS.WPZFJC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Style;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Enum.lkDataCollectType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGpsReceiveDataStatus;
import lkmap.MapControl.IOnPaint;
import lkmap.MapControl.ZoomInOutPan;
import lkmap.Tools.Tools;

public class WeiPianZhiFa_GPSMeasure implements IOnPaint 
{
	private Polygon m_Polygon = null;
	private ZoomInOutPan m_ZoomPan =null; 
	private String Id = "gpsMeasurePaint";
	
	public WeiPianZhiFa_GPSMeasure()
	{
		this.m_Polygon = new Polygon();
    	this.m_Polygon.AddPart(new Part(this.m_GPSTrackPointList));
    	this.m_ZoomPan = new ZoomInOutPan(PubVar.m_MapControl);
    	PubVar.m_MapControl.AddOnPaint(this.Id, this);
	}
	/**
     * ����GPSλ������
     * @param newCoor
     */
    private long m_BeforeGpsPosUpateTime = 0;
    public void UpdateGpsPosition(Coordinate newCoor)
    {
    	if (this.m_DataCollectStatus==lkGpsReceiveDataStatus.enStop) 
		{
			
			return;
			
		}
    	
    	if (!lkmap.Tools.Tools.ReadyGPS(true))
		{
    		return;
		}
    	
    	//�ڴ˿��Ʋɼ���ʱ��������С���룬��ϵͳ���� Tag_System_GPSMinTime,Tag_System_GPSMinDistance ����
    	
    	//��Ҫ�ڴ˼������������Ҳ���Ǿ���̫�ĵĹ��˵���Ĭ��ֵ��v1_UserConfigDB.LoadSystemConfig
    	double MinDistance = 0;
    	if (this.m_GPSTrackPointList.size()>0)
    	{
    		Coordinate PT = this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-1);
    		MinDistance = Tools.GetTwoPointDistance(newCoor, PT);
    		
    		double LimitDistance = 0;
        	if (!(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance").Value+"").equals("����"))
        	{
        		LimitDistance = Double.parseDouble(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance").Value+"");
        	}
    		if (MinDistance<LimitDistance) return;
    	}

    	
    	//GPS����ʱ�����ȥ����Ĭ��ֵ��v1_UserConfigDB.LoadSystemConfig
    	double LimitTime = 0;
    	long CurrentTime = System.currentTimeMillis();
    	if (!(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime").Value+"").equals("����"))
    	{
    		LimitTime = Double.parseDouble(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime").Value+"")*1000;
    	}
    	if ((CurrentTime-this.m_BeforeGpsPosUpateTime)>=(LimitTime-100))
    	{
    		this.m_GPSTrackPointList.add(newCoor);
    		this.AddMValue(MinDistance,(this.m_CalArea?this.m_Polygon.getArea(true):0));
    		this.m_BeforeGpsPosUpateTime = CurrentTime;
    		PubVar.m_DoEvent.m_SoundTool.PlaySound(5);   //�����������
    		HashMap<String,Object> result = new HashMap<String,Object>();
    		result.put("area", this.m_Polygon.getArea(true));
    		result.put("point", this.m_Polygon.getArea(true));
    		
    		if (m_Callback!=null)
        		m_Callback.OnClick("����", result);
    		
    		PubVar.m_MapControl.invalidate();
    	}
    	
    	
    }

    public void Stop(lkGeoLayerType geoType)
    {
    	 //�ж��Ѿ��ɼ��ߵ������������2�����򲻴�
        if ((this.m_GPSTrackPointList.size() >= 2 && geoType==lkGeoLayerType.enPolyline)||
        	(this.m_GPSTrackPointList.size() >= 3 && geoType==lkGeoLayerType.enPolygon))
        {
        	//�������һ���ɼ���
			if (lkmap.Tools.Tools.ReadyGPS(false))
			{
				UpdateGpsPosition(PubVar.m_DoEvent.m_GPSLocate.getGPSCoordinate());
			}
        }
    }
    
    public void ClearPoint()
    {
    	m_GPSTrackPointList.clear();
    }
    
    //���ٲ���ֵ
  	private List<MValue> m_MValueList = new ArrayList<MValue>();
  	
  	//�Ƿ�̬�����������Ҫ�����ڲɼ���
    private boolean m_CalArea = true;
    
    /**
     * ��̬���������
     * @param mtype
     */
    public void SetIfCalArea(boolean calArea){this.m_CalArea = calArea;}
    
    private void AddMValue(double length,double area)
	{
		MValue mv = new MValue();
		if (this.m_MValueList.size()==0){mv.Length = length;mv.Area = area;}
		else 
		{
			mv.Length = this.m_MValueList.get(this.m_MValueList.size()-1).Length+length;
			mv.Area = area;
		}
		this.m_MValueList.add(mv);
	}
    /**
     * GPS�߲���
     */
    public lkGpsReceiveDataStatus m_DataCollectStatus = lkGpsReceiveDataStatus.enStarting;  //���ݲɼ�״̬
	private List<Coordinate> m_GPSTrackPointList = new ArrayList<Coordinate>();	 		 //GPS�����ߵĹ켣��
	public List<Coordinate> GetTrackPointList(){return this.m_GPSTrackPointList;}
	
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb)
	{
		this.m_Callback = cb;
	}
	@Override
	public void OnPaint(Canvas canvas) 
	{
		this.m_ZoomPan.OnPaint(canvas);
    	//if (PubVar.m_Map.getInvalidMap()) return;
    	
		if (this.m_DataCollectStatus==lkGpsReceiveDataStatus.enStop) 
		{
			//this.UpdateDataInStatus();
			return;
			
		}
		//���ƹ켣����Ϣ���γɹ켣��
		Point[] PList = PubVar.m_MapControl.getMap().getViewConvert().MapPointsToScreePoints(this.m_GPSTrackPointList);
     	if (PList.length > 0)
     	{
			Path p = new Path();
        	for(int i=0;i<PList.length;i++)
        	{
        		if (i==0)p.moveTo(PList[i].x, PList[i].y);
        		else p.lineTo(PList[i].x, PList[i].y);
        	}

        	Paint pPen = new Paint();
        	pPen.setStyle(Style.STROKE);
        	pPen.setStrokeWidth(Tools.DPToPix(3));
        	pPen.setColor(Color.RED);
        	canvas.drawPath(p, pPen);
     	}
     	
     	//���ƹ켣�ڵ�
     	int H = Tools.DPToPix(6);
     	Paint pBrush = new Paint();
    	for(int i=0;i<PList.length;i++)
    	{
    		pBrush.setStyle(Style.FILL);
            pBrush.setColor(Color.YELLOW);
            canvas.drawCircle(PList[i].x, PList[i].y, H/2, pBrush);
            
            pBrush.setStyle(Style.STROKE);
            pBrush.setStrokeWidth(2);
            pBrush.setColor(Color.RED);
            canvas.drawCircle(PList[i].x, PList[i].y, H/2, pBrush);
    	}
    	
    	
    	
	}
	
	private class MValue
	{
		public double Length = 0;
		public double Area = 0;
	}

}
