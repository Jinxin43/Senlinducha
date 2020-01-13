package lkmap.MapControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsInfoManage;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Map.StaticObject;
import lkmap.MapControl.IOnPaint;
import lkmap.Tools.Tools;

public class v1_Measure implements IOnTouchCommand,IOnPaint
{
	
    public v1_Measure()
    {
    	this.m_MeasurePointList = new ArrayList<Coordinate>();
    	this._Polygon = new Polygon();
    	this._Polygon.AddPart(new Part(this.m_MeasurePointList));
    	this.m_GestureDetector = new GestureDetector(PubVar.m_MapControl.getContext(),this.m_MyOnGestureListener);
    	_Pan = new Pan(PubVar.m_MapControl);
    }

    private Pan _Pan = null;
    
    //������
	private List<Coordinate> m_MeasurePointList = null;

	//����ֵ
	private List<Double> _LengthList = new ArrayList<Double>();
	//���ֵ
	private double _Area = 0;
	private Polygon _Polygon = null;
	
	//0-�ۺϣ�1-���ߣ�2-����
	private int _Mode = 1;  
	public void SetMode(int mode){this._Mode = mode;}
	
	public Coordinate getFisrtCoor()
	{
		if(m_MeasurePointList == null)
		{
			return null;
		}
		
		if(m_MeasurePointList.isEmpty())
		{
			return null;
		}
		else
		{
			return m_MeasurePointList.get(0);
		}
	}
	
    /**
     * ���ӵ�λ
     * @param ptCoor
     */
	private void AddPoint(MotionEvent e)
    {
		PointF pt = new PointF(e.getX(),e.getY());
		Coordinate mPoint = PubVar.m_Map.getViewConvert().ScreenToMap(pt);
		
	    //��Map����ת��Ϊ�������ֻ꣬�������ֶ��ӵ�����
	    //Coordinate lb84 = StaticObject.soProjectSystem.XYToWGS84(mPoint);
	    //mPoint.setGeoX(lb84.getX());mPoint.setGeoY(lb84.getY());
	    
    	this.m_MeasurePointList.add(mPoint);
    	if (this.m_MeasurePointList.size()==1)this._LengthList.add(0.0);
    	else this._LengthList.add(this._Polygon.getLength(true));
    	if (this.m_MeasurePointList.size()>=3)
		{
			this._Area = this._Polygon.getArea(true);
		}
    	
    	PubVar.m_MapControl.invalidate();
    }
	
	/**
	 * ���
	 */
	public void Clear()
	{
		this.m_MeasurePointList.clear();this._LengthList.clear();
		PubVar.m_MapControl.invalidate();
	}
	
	public boolean m_Snap = false;
    

	public void MouseDown(MotionEvent e) 
	{

	}

	public void MouseMove(MotionEvent e) {}

	public void MouseUp(MotionEvent e) {}
	
    public GestureDetector m_GestureDetector = null;
    private SimpleOnGestureListener m_MyOnGestureListener = new SimpleOnGestureListener(){

    	@Override  
        public boolean onDoubleTap(MotionEvent e)  
        {  
    		Clear();
    		return super.onDoubleTap(e);
        }  
  
        @Override  
        public boolean onDown(MotionEvent e)  
        {  
            return super.onDown(e);  
        } 

        @Override  
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
                float velocityY)  
        {  
            // TODO Auto-generated method stub  
            //Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityY" + velocityY);  

            return super.onFling(e1, e2, velocityX, velocityY);  
        }  
  
        @Override  
        public void onLongPress(MotionEvent e)  
        {  
            // TODO Auto-generated method stub  
            //Log.i("TEST", "onLongPress");  
            super.onLongPress(e);  
        }  
  
        @Override  
        public boolean onScroll(MotionEvent e1, MotionEvent e2,  
                float distanceX, float distanceY)  
        {  
        	_Pan.MouseDown(e1);
        	_Pan.MouseMove(e2);
            return super.onScroll(e1, e2, distanceX, distanceY);  
        }  
  
        @Override  
        public boolean onSingleTapUp(MotionEvent e)  
        {  
    	    AddPoint(e);
            PubVar.m_MapControl.invalidate();
            return super.onSingleTapUp(e); 
        }  
        @Override  
        public void onShowPress(MotionEvent e)  
        {  
        	
        } 
	};	
	
	@Override
	public void SetOnTouchEvent(MotionEvent e) 
	{
		if ((e.getAction() & MotionEvent.ACTION_MASK)==MotionEvent.ACTION_UP)
		{
			this._Pan.MouseUp(e);
		}
		this.m_GestureDetector.onTouchEvent(e);
	}

    /**
     * �ֶ����߽ӿ�
     */
	@Override
	public void OnPaint(Canvas canvas) 
	{
    	this._Pan.OnPaint(canvas);
    	if (PubVar.m_Map.getInvalidMap()) return;
		if (this.m_MeasurePointList.size()==0) return;
		//���ƹ켣����Ϣ���γɹ켣��
		Point[] PList = PubVar.m_MapControl.getMap().getViewConvert().MapPointsToScreePoints(this.m_MeasurePointList);
     	if (PList.length >=1)
     	{
			Path p = new Path();
        	for(int i=0;i<PList.length;i++)
        	{
        		if (i==0)p.moveTo(PList[i].x, PList[i].y);
        		else p.lineTo(PList[i].x, PList[i].y);
        	}

        	//�Ȼ��棬�󻭱���
        	Paint pPen = new Paint();
        	if (this._Mode==2)
    		{
        		pPen.setColor(Color.parseColor("#33ff00ff"));
    			pPen.setStyle(Style.FILL);
    			canvas.drawPath(p, pPen);
    		}
        	
        	pPen.setStrokeWidth(Tools.DPToPix(2));
        	pPen.setColor(Color.BLUE);
        	pPen.setStyle(Style.STROKE);
        	canvas.drawPath(p, pPen);
        	
        	
     	}
     	
     	//���ƹ켣�ڵ�
     	int H = Tools.DPToPix(8);
    	for(int i=0;i<PList.length;i++)
    	{
    		//���Ƶ����ڵ�
            Paint pBrush = new Paint();
            pBrush.setStrokeWidth(Tools.DPToPix(5));
            if (i==0) pBrush.setColor(Color.GREEN);  //��
            if (i==PList.length-1) pBrush.setColor(Color.RED);  //��
            if (i>0 && i<PList.length-1)pBrush.setColor(Color.YELLOW);
            canvas.drawCircle(PList[i].x, PList[i].y, H/2, pBrush);
            pBrush.setStyle(Style.STROKE);
            pBrush.setColor(Color.BLUE);
            pBrush.setStrokeWidth(Tools.DPToPix(2));
            canvas.drawCircle(PList[i].x, PList[i].y, H/2, pBrush);
    	}
    	
    	if (this._Mode==1)
    	{
	    	for(int i=0;i<PList.length;i++)
	    	{
	    		if (i==0) continue;  //0�㲻��
	            //�ڵ��Ҳ���ƾ�������
	            String Text = Tools.ReSetDistance(this._LengthList.get(i), true);
	            float TextX = PList[i].x+H;
	            float TextY = PList[i].y;
	            this.DrawText(canvas, Text, TextX, TextY);
	    	}
    	}
    	
    	if (this._Mode==2)
    	{
	        //���м�λ�û������ֵ
	    	if (this.m_MeasurePointList.size()>=3)
	    	{
	    		Coordinate CPT = this.m_MeasurePointList.get(this.m_MeasurePointList.size()-1);
	    		String AreaText = Tools.ReSetArea(this._Area, true);
	    		Point PT = PubVar.m_MapControl.getMap().getViewConvert().MapToScreen(CPT);
	    		this.DrawText(canvas,AreaText , PT.x+H, PT.y);
	    	}
    	}
    	if (this._Mode==0)
    	{
	        //���ƽ��
    		String ResultStr = "";
    		
    		if (this._LengthList.size()==1)
    		{
                //�ڵ��Ҳ���ƾ�������
    	    	Coordinate CPT = this.m_MeasurePointList.get(this.m_MeasurePointList.size()-1);
    	    	HashMap<String,String> CoorHM = v1_CGpsInfoManage.ConvertCoordinateFormate(StaticObject.soProjectSystem.XYToWGS84(CPT));
        		
    	    	String XCoorStr = (CoorHM.containsKey("X")?CoorHM.get("X"):"");
    	    	String YCoorStr = (CoorHM.containsKey("Y")?CoorHM.get("Y"):"");
    	    	Point PT = PubVar.m_MapControl.getMap().getViewConvert().MapToScreen(CPT);

	    		FontMetrics fm = this.getTextFont().getFontMetrics();   
	            int textHeight = (int) (Math.ceil(fm.descent - fm.ascent) + 2); 
	            this.DrawText(canvas,XCoorStr , PT.x+H, PT.y);
	    		this.DrawText(canvas,YCoorStr , PT.x+H, PT.y+textHeight);
    		}
    		
    		if (this._LengthList.size()>1)
    		{
                //�ڵ��Ҳ���ƾ�������
    			ResultStr = "����:"+Tools.ReSetDistance(this._LengthList.get(this._LengthList.size()-1), true);
    	    	Coordinate CPT = this.m_MeasurePointList.get(this.m_MeasurePointList.size()-1);
        		Point PT = PubVar.m_MapControl.getMap().getViewConvert().MapToScreen(CPT);
        		this.DrawText(canvas,ResultStr , PT.x+H, PT.y);
    		}

            
	    	if (this.m_MeasurePointList.size()>=3)
	    	{
	    		ResultStr= "���:"+Tools.ReSetArea(this._Area, true);
		    	Coordinate CPT = this.m_MeasurePointList.get(this.m_MeasurePointList.size()-1);
	    		Point PT = PubVar.m_MapControl.getMap().getViewConvert().MapToScreen(CPT);
	    		FontMetrics fm = this.getTextFont().getFontMetrics();   
	            int textHeight = (int) (Math.ceil(fm.descent - fm.ascent) + 2);   
	    		this.DrawText(canvas,ResultStr , PT.x+H, PT.y+textHeight);
	    	}

    	}
    	

	}
	
	//��������
	private void DrawText(Canvas canvas,String Text,float TextX,float TextY)
	{
		float Offset = 2;
        canvas.drawText(Text, TextX-Offset, TextY, this.getBKFont());
        canvas.drawText(Text, TextX+Offset, TextY, this.getBKFont());
        canvas.drawText(Text, TextX, TextY-Offset, this.getBKFont());
        canvas.drawText(Text, TextX, TextY+Offset, this.getBKFont());
        canvas.drawText(Text, TextX+Offset, TextY+Offset, this.getBKFont());
        canvas.drawText(Text, TextX-Offset, TextY-Offset, this.getBKFont());
        canvas.drawText(Text, TextX+Offset, TextY-Offset, this.getBKFont());
        canvas.drawText(Text, TextX-Offset, TextY+Offset, this.getBKFont());
        canvas.drawText(Text,TextX, TextY, this.getTextFont());
	}
	
	
    private Paint _Font = null;
    private Paint getTextFont()
    {
        if (_Font == null) 
        {
        	_Font = new Paint();
        	_Font.setAntiAlias(true);
        	_Font.setTextSize(Tools.SPToPix(18));
        	Typeface TF = Typeface.create("����", Typeface.NORMAL);
        	_Font.setTypeface(TF);
        }
        _Font.setAntiAlias(true);
        return _Font;
    }
    
    //��ע����
    private Paint _BKFont = null;
    private Paint getBKFont()
    {
        if (_BKFont == null)
        {
        	_BKFont = new Paint();
        	_BKFont.setColor(Color.WHITE);
        	_BKFont.setAntiAlias(true);
        	
        	Typeface TF = Typeface.create("����", Typeface.NORMAL);
        	_BKFont.setTypeface(TF);
        	_BKFont.setAntiAlias(true);
        }
        _BKFont.setTextSize(this.getTextFont().getTextSize());
        return _BKFont;
    }


}
