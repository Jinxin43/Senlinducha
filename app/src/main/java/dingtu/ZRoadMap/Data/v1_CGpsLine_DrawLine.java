package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.DataSource;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkDataCollectType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGpsReceiveDataStatus;
import lkmap.Map.Param;
import lkmap.Map.StaticObject;
import lkmap.MapControl.ICommand;
import lkmap.MapControl.IOnPaint;
import lkmap.MapControl.IOnTouchCommand;
import lkmap.MapControl.Pan;
import lkmap.Tools.Tools;

public class v1_CGpsLine_DrawLine implements IOnTouchCommand
{
	
    public v1_CGpsLine_DrawLine()
    {
    	this.m_GestureDetector = new GestureDetector(PubVar.m_MapControl.getContext(),this.m_MyOnGestureListener);
    	this._Pan = new Pan(PubVar.m_MapControl);
    }

    //移屏操作类
    private Pan _Pan = null;
    
    /**
     * 增加点位
     * @param ptCoor
     */
	private void AddPoint(MotionEvent e)
    {
		PointF pt = new PointF(e.getX(),e.getY());
		Coordinate mPoint = PubVar.m_Map.getViewConvert().ScreenToMap(pt);
    	PubVar.m_MapControl.invalidate();
    }
	
	
	//是否启用捕捉
	public boolean m_Snap = false;
	
	//是否启用流模式绘图
	private boolean m_StreamMode = true;
	/**
	 * 设置是否启用流模式绘图
	 * @param streamMode
	 */
	public void SetStreamMode(boolean streamMode)
	{
		this.m_StreamMode = streamMode;
	}
	
	//指示是移屏还是流绘图，1-流绘图，2-移屏，判断的依据为MouseDown内是否与最后一节点靠近
	private int m_DrawOrPan = 1;
	
	//表示流模式下可以续接的范围
	private int m_Tolerance = Tools.DPToPix(15);
	
	private GestureDetector m_GestureDetector = null;
    private SimpleOnGestureListener m_MyOnGestureListener = new SimpleOnGestureListener()
    {
        @Override  
        public boolean onDown(MotionEvent e)  
        { 
//        	if (!m_StreamMode)m_DrawOrPan=2;
//        	{
//	        	if (m_MeasurePointList.size()==0)m_DrawOrPan=1;
//	        	else
//	        	{
//	        		m_DrawOrPan = 1;
//	        		
//	        		Coordinate mPoint = m_MeasurePointList.get(m_MeasurePointList.size()-1);
//	        		PointF pf = PubVar.m_Map.getViewConvert().MapToScreenF(mPoint.getX(), mPoint.getY());
//	        		if (Math.abs(pf.x-e.getX())>=m_Tolerance || Math.abs(pf.y-e.getY())>=m_Tolerance)
//	        		{
//	        			m_DrawOrPan = 2;
//	        		}
//	        	}
//        	}
            return super.onDown(e);
        } 

        @Override  
        public boolean onScroll(MotionEvent e1, MotionEvent e2,  
                float distanceX, float distanceY)  
        {  
        	if (m_DrawOrPan==1)AddPoint(e2);;
        	if (m_DrawOrPan==2)
    		{
            	_Pan.MouseDown(e1);
    			_Pan.MouseMove(e2);
    		}
        	
            return super.onScroll(e1, e2, distanceX, distanceY);  
        }  
  
        @Override  
        public boolean onSingleTapUp(MotionEvent e)  
        {  
    	    AddPoint(e);
            PubVar.m_MapControl.invalidate();
            return super.onSingleTapUp(e); 
        }  

	};	
	
	@Override
	public void SetOnTouchEvent(MotionEvent e) {
		//主要用于移屏时的MouseUp事件
		if ((e.getAction() & MotionEvent.ACTION_MASK)==MotionEvent.ACTION_UP)
		{
			this._Pan.MouseUp(e);
		}
		this.m_GestureDetector.onTouchEvent(e);
	}
}
