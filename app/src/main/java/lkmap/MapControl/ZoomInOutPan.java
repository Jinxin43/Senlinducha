package lkmap.MapControl;

import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Envelope;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import dingtu.ZRoadMap.PubVar;

public class ZoomInOutPan implements IOnTouchCommand,IOnPaint
{
    private MapControl _MapControl = null;
    private Bitmap MaskImage=null;
    private Pan _Pan = null;			//������
    public ZoomInOutPan(MapControl MC)
    {
        _MapControl = MC;
        _Pan = new Pan(MC);
    }

	private float m_FromDist = 0;
	private float m_ToDist = 0;
	private RectF m_ToRect = null;
	private PointF m_ScaleStartPoint = null;   //�������
	
	private float GetScaleDist(MotionEvent e)
	{
		float LT_X = Math.min(e.getX(0),e.getX(1));
		float LT_Y = Math.min(e.getY(0),e.getY(1));
		float RB_X = Math.max(e.getX(0),e.getX(1));
		float RB_Y = Math.max(e.getY(0),e.getY(1));
		return Float.valueOf(String.valueOf((Math.sqrt((LT_X-RB_X)*(LT_X-RB_X)+(LT_Y-RB_Y)*(LT_Y-RB_Y)))));
	}
	

	//�������ƶ���������,1=���ţ�2=�ƶ�
	private int m_ZoomOrPan = 1;
	
	public void PanMouseDown(MotionEvent e)
    {
		this.m_ZoomOrPan = 2;
		this._Pan.MouseDown(e); 
    }
	public void PanMouseUp(MotionEvent e)
    {
		if (this.m_ZoomOrPan ==2 )this._Pan.MouseUp(e);
		this._Pan._MouseDown=false;
    }
	
	public void MouseMove(MotionEvent e)
    {
		if (this.m_ZoomOrPan==2)
		{
			this._Pan.MouseMove(e);
		}
		if (this.m_ZoomOrPan==1) //��������
		{
			if (e.getPointerCount()<=1)return;
			this.m_ToDist = this.GetScaleDist(e);
			float ScaleDex = 1;
			if (this.m_FromDist==0 || this.m_ToDist==0)ScaleDex=1;
			ScaleDex = this.m_ToDist / this.m_FromDist;
			if (this.MaskImage!=null)
			{
				float newWidth = this.MaskImage.getWidth()*ScaleDex;
				float newHeight = this.MaskImage.getHeight()*ScaleDex;
				float StartX = -(ScaleDex-1)*this.m_ScaleStartPoint.x;
				float StartY = -(ScaleDex-1)*this.m_ScaleStartPoint.y;
				this.m_ToRect = new RectF(StartX,StartY,StartX+newWidth,StartY+newHeight);
			}
			this._MapControl.invalidate();
		}
    }


	public void Scroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY)
	{
		float ScaleDex = 1;
		float LT_X = Math.min(e1.getX(),e2.getX());
		float LT_Y = Math.min(e1.getY(),e2.getY());
		float RB_X = Math.max(e1.getX(),e2.getX());
		float RB_Y = Math.max(e1.getY(),e2.getY());
		this.m_ToDist= Float.valueOf(String.valueOf((Math.sqrt((LT_X-RB_X)*(LT_X-RB_X)+(LT_Y-RB_Y)*(LT_Y-RB_Y)))));
		if (this.m_FromDist==0 || this.m_ToDist==0)
		{
			ScaleDex=1;
		}
		ScaleDex = this.m_ToDist / this.m_FromDist;
		if (this.MaskImage!=null)
		{
			float newWidth = this.MaskImage.getWidth()*ScaleDex;
			float newHeight = this.MaskImage.getHeight()*ScaleDex;
			float StartX = -(ScaleDex-1)*this.m_ScaleStartPoint.x;
			float StartY = -(ScaleDex-1)*this.m_ScaleStartPoint.y;
			this.m_ToRect = new RectF(StartX,StartY,StartX+newWidth,StartY+newHeight);
		}
		this._MapControl.invalidate();
	}
	
	
	
	private void ZoomMouseUp(MotionEvent e)
	{
		if (this.MaskImage != null) { this.MaskImage = null; }
        if (this.m_ToRect != null) 
        {
        	float ScaleDex = 1;
    		if (this.m_FromDist==0 || this.m_ToDist==0)ScaleDex=1;
    		ScaleDex = this.m_ToDist / this.m_FromDist;

    		Coordinate Pt1 = PubVar.m_Map.getViewConvert().ScreenToMap(this.m_ScaleStartPoint);
            Envelope newExtend = this._MapControl.getMap().getExtend().Scale(1/ScaleDex);
        	this._MapControl.getMap().setExtend(newExtend);
            Coordinate Pt2 = PubVar.m_Map.getViewConvert().ScreenToMap(this.m_ScaleStartPoint);
            
            PubVar.m_Map.getCenter().setX(PubVar.m_Map.getCenter().getX()-(Pt2.getX() - Pt1.getX()));
            PubVar.m_Map.getCenter().setY(PubVar.m_Map.getCenter().getY()-(Pt2.getY() - Pt1.getY()));
            PubVar.m_Map.getViewConvert().CalExtend();
        }
        if( _MapControl.getMap()!=null) {
			_MapControl.getMap().setInvalidMap(false);
			this._MapControl.getMap().Refresh();
		}
	}
	private void ZoomMouseDown(MotionEvent e)
	{
		this.m_ZoomOrPan = 1;
		if(this._MapControl.getMap()!=null) {
			this._MapControl.getMap().setInvalidMap(true);
			//����ԭͼ�Ŀ���
			this.MaskImage = this._MapControl.getMap().MaskBitmap;
		}
        this.m_FromDist = this.GetScaleDist(e);
        this.m_ToRect = null;
        this.m_ScaleStartPoint = new PointF((e.getX(0)+e.getX(1))/2,(e.getY(0)+e.getY(1))/2);
	}

	@Override
	public void OnPaint(Canvas g)
	{
		if(this.m_ZoomOrPan==2)
		{
			this._Pan.OnPaint(g);return;
		}
		if(this.m_ZoomOrPan==1)
		{
			if (MaskImage!=null && this.m_ToRect!=null)
	        {
		        Paint brush = new Paint();
		        brush.setColor(Color.GRAY);
		        brush.setStyle(Style.FILL);
		        g.drawRect(0,0,MaskImage.getWidth(),MaskImage.getHeight(),brush);   //����ƶ������µĿհ�����
		        g.drawBitmap(MaskImage, new Rect(0,0,MaskImage.getWidth(),MaskImage.getHeight()),this.m_ToRect, brush);
	        }
		}
	}

	@Override
	public void SetOnTouchEvent(MotionEvent event) {
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				if (event.getPointerCount()<=1)this.PanMouseDown(event);
				break;
			case MotionEvent.ACTION_UP:
				if (event.getPointerCount()<=1)this.PanMouseUp(event);
				break;
			case MotionEvent.ACTION_MOVE:
				this.MouseMove(event);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				this.ZoomMouseDown(event);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				this.ZoomMouseUp(event);
				break;
		}
	}

}
