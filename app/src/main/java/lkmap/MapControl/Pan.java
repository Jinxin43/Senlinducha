package lkmap.MapControl;

import lkmap.Cargeometry.Coordinate;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import dingtu.ZRoadMap.PubVar;

public class Pan implements IOnPaint
{
	private MapControl _MapControl;

    //移动起止点(移屏)
    private android.graphics.PointF m_MoveStart;
    Integer GGdc;
    Paint brush = new Paint(Color.TRANSPARENT);
    Canvas g2;
    Bitmap MaskImage=null;
    
    public Pan(MapControl MC)
    {
        _MapControl = MC;
    }
    
    public boolean _MouseDown = false;
    public void MouseDown(MotionEvent e)
    {
    	if (this._MouseDown) return;
    	if(_MapControl.getMap()!=null) {
            _MapControl.getMap().setInvalidMap(true);
        }

        //清除动态矩形框
        _MapControl.TrackingRectangle = null;
        m_MoveStart = new android.graphics.PointF(e.getX(), e.getY());
        if(_MapControl.getMap()!=null) {
            MaskImage = _MapControl.getMap().MaskBitmap;
        }
        this._MouseDown = true;
    }

    private boolean _MouseMoving = false;
	float X1, Y1, W1, H1, X2, Y2, W2, H2,DeltX,DeltY;
	public void MouseMove(MotionEvent e)
    {
        if (MaskImage!=null)
        {
        	this._MouseMoving = true;
            DeltX = e.getX() - m_MoveStart.x;
            DeltY = e.getY() - m_MoveStart.y;
            float Width = _MapControl.getWidth(); 
            float Height = _MapControl.getHeight();

            if (DeltX >= 0)
            {
                if (DeltY >= 0)
                {
                    X1 = 0; Y1 = 0; W1 = DeltX; H1 = Height;
                    X2 = 0; Y2 = 0; W2 = Width; H2 = DeltY;
                }
                else
                {
                    X1 = 0; Y1 = 0; W1 = DeltX; H1 = Height;
                    X2 = 0; Y2 = Height+DeltY; W2 = Width; H2 = -DeltY;
               }
            }
            else
            {
                if (DeltY >= 0)
                {
                    X1 = 0; Y1 = 0; W1 = Width; H1 = DeltY;
                    X2 = Width + DeltX; Y2 = 0; W2 = -DeltX; H2 = Height;
                }
                else
                {
                    X1 = 0; Y1 = Height + DeltY; W1 = Width; H1 = -DeltY;
                    X2 = Width + DeltX; Y2 = 0; W2 = -DeltX; H2 = Height;
                }

            }
            this._MapControl.invalidate();
        }
    }

	private boolean _SelectMode = true;    //是否启动选择模式

	public void MouseUp(MotionEvent e)
    {
        Coordinate StartPoint, EndPoint;
        if (g2 != null) { g2 = null; }
        if (MaskImage != null) { MaskImage = null; }
        
        if (this._MouseDown&&_MapControl.getMap()!=null)
        {
	        StartPoint = _MapControl.getMap().getViewConvert().ScreenToMap(m_MoveStart);
	        EndPoint = _MapControl.getMap().getViewConvert().ScreenToMap(e.getX(), e.getY());
	        Coordinate newCenter = new Coordinate(_MapControl.getMap().getCenter().getX() - (EndPoint.getX() - StartPoint.getX()),
	        									 _MapControl.getMap().getCenter().getY() - (EndPoint.getY() - StartPoint.getY()));									
	        
	        this.SetNewCenter(newCenter);
	        
	        
	        //可以在此处加入选择标记的功能
	        if (_SelectMode)
	        {
	        	//判断是真正的移屏，还是选择操作
	        	if (!(Math.abs(e.getX()-m_MoveStart.x)>10 || Math.abs(e.getY()-m_MoveStart.y)>10))
	        	{
	        		//this._MapControl._Select.MouseUp(e);
	        	}
	        }
        }
        
        this._MouseDown = false;
        this._MouseMoving = false;
        if( _MapControl.getMap()!=null) {
            _MapControl.getMap().setInvalidMap(false);
        }
        //强制回收垃圾
        System.gc();

    }
	
	public void SetNewCenter(Coordinate newCenter)
	{
        _MapControl.getMap().getCenter().setX(newCenter.getX());
        _MapControl.getMap().getCenter().setY(newCenter.getY());
        _MapControl.getMap().getViewConvert().CalExtend();
        _MapControl.getMap().Refresh();
	}
	Paint mPaint = new Paint();
	@Override
	public void OnPaint(Canvas g)
	{
	   if (MaskImage!=null &&this._MouseMoving)
       {
	        brush.setColor(Color.GRAY);
	        brush.setStyle(Style.FILL);
	        g.drawRect(X1,Y1,X1+W1,Y1+H1,brush);   //填充移动后留下的空白区域
	        g.drawRect(X2, Y2, X2+W2, Y2+H2,brush);
	        g.drawBitmap(MaskImage,DeltX, DeltY,new Paint());
      }
	  
	 if(PubVar.CenterCrossShow)
     {
			//画十字
			mPaint.setColor(Color.RED);
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(2);
			DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
			int heightPixel = dm.heightPixels/2;
			int weightPixel = dm.widthPixels/2;
			g.drawLine(weightPixel-40,heightPixel,weightPixel+40,heightPixel, mPaint);
//			g.drawLine(weightPixel,heightPixel,weightPixel+70,heightPixel, mPaint);
			g.drawLine(weightPixel,heightPixel-40,weightPixel,heightPixel+40, mPaint);
//			g.drawLine(weightPixel,heightPixel,weightPixel,heightPixel+70, mPaint);
     }
	   
	}


}
