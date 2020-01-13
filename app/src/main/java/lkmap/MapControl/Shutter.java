package lkmap.MapControl;

import lkmap.Layer.GridLayer;
import lkmap.Layer.GridLayers;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.view.MotionEvent;

public class Shutter implements IOnPaint,IOnTouchCommand
{
	private MapControl _MapControl;
    
    //���ϲ���ͼ
    private Bitmap m_TopMaskImage=null;
    
    public Shutter(MapControl MC)
    {
        _MapControl = MC;
    }
    public void StartShutter()
    {
    	_MapControl.getMap().Refresh();
    	
    	
        //���ϲ���ͼ
    	if (this.m_TopMaskImage==null)
    	{
    		this.m_TopMaskImage = Bitmap.createBitmap(_MapControl.getMap().getViewConvert().getSize().getWidth(), 
	    									_MapControl.getMap().getViewConvert().getSize().getHeight(),Config.ARGB_8888);
    	}
    	(new Canvas(this.m_TopMaskImage)).drawBitmap(_MapControl.getMap().MaskBitmap, 0, 0, null);
    	
    	//�²�ͼ��Ҳ��������������դ��ͼ��ͼ
    	GridLayer TopGridLayer = null;
    	GridLayers pGridLayers = _MapControl.getMap().GetGridLayers();
    	for(int i=0;i<pGridLayers.GetList().size();i++)
    	{
    		GridLayer pGridLayer = pGridLayers.GetList().get(i);
    		if (pGridLayer.GetShowGird())TopGridLayer = pGridLayer;
    	}
    	if (TopGridLayer!=null)
    	{
    		TopGridLayer.SetShowGrid(false);
    		_MapControl.getMap().FastRefresh();
    		TopGridLayer.SetShowGrid(true);
    	}
    	_MapControl.getMap().setInvalidMap(true);
    	this._MapControl.invalidate();
    }
    
    //�ƶ���ֹ��
    private android.graphics.PointF m_MoveStart;
    private android.graphics.PointF m_MoveEnd;
    
    public boolean _MouseDown = false;
    public void MouseDown(MotionEvent e)
    {
    	if (this._MouseDown) return;
        //���
        m_MoveStart = new android.graphics.PointF(e.getX(), e.getY());
        this._MouseDown = true;
    }

    private boolean _MouseMoving = false;
	public void MouseMove(MotionEvent e)
    {
    	this._MouseMoving = true;
    	m_MoveEnd = new android.graphics.PointF(e.getX(), e.getY());
        this._MapControl.invalidate();
    }

	public void MouseUp(MotionEvent e)
    {
        if (this._MouseDown)
        {

        }
        
        this._MouseDown = false;
        this._MouseMoving = false;
        _MapControl.getMap().setInvalidMap(false);
        this._MapControl.invalidate();
        mDragFlip = 0;
        //ǿ�ƻ�������
        System.gc();

    }
	
	int mDragFlip = 0;   //0��δȷ����1�Ǻ���2������
	@Override
	public void OnPaint(Canvas g)
	{
	   if (this.m_TopMaskImage!=null)
       {

		   if (this._MouseMoving)
		   {
			   g.save();
			   Path clipPath = new Path();
			   clipPath.addRect(new RectF(0,0,g.getWidth(),g.getHeight()),Direction.CCW);
			   RectF drawRect = null;
			   
			 
			   //�ж��Ǻ���������������������
			   //int DragFlip = 1;   //����
			   float DelY = Math.abs(this.m_MoveEnd.y-this.m_MoveStart.y);
			   float DelX = Math.abs(this.m_MoveEnd.x-this.m_MoveStart.x);
			   if(mDragFlip == 0)
			   {
				   if(DelY>DelX)
				   {
					   mDragFlip = 2;
				   }
				   else
				   {
					   mDragFlip = 1; 
				   }  
			   }
			   
			   
			   //if (DelY>DelX)DragFlip=2;   //����
			   
			   if (mDragFlip==1)
			   {
				   if (this.m_MoveStart.x<=g.getWidth()/2)
				   {
					   drawRect = new RectF(0,0,this.m_MoveEnd.x,g.getHeight());
				   }
				   else
				   {
					   drawRect = new RectF(this.m_MoveEnd.x,0,g.getWidth(),g.getHeight());
				   }
			   }
			   if (mDragFlip==2)
			   {
				   if (this.m_MoveStart.y<=g.getHeight()/2)
				   {
					   drawRect = new RectF(0,0,g.getWidth(),this.m_MoveEnd.y);
				   }
				   else
				   {
					   drawRect = new RectF(0,this.m_MoveEnd.y,g.getWidth(),g.getHeight());
				   }
			   }
			   
			   
			   //��Ч���η�Χ
			   clipPath.addRect(drawRect,Direction.CW);
			   g.clipPath(clipPath);
			
			   Paint p = new Paint();
			   p.setAlpha(255);
			   g.drawBitmap(this.m_TopMaskImage,0, 0,p);
			   
			   //���Ʒָ���
			   p.setStrokeWidth(lkmap.Tools.Tools.DPToPix(4));
			   p.setColor(Color.RED);
			   p.setStyle(Style.STROKE);
			   g.drawRect(drawRect, p);
			   g.restore();
		   } 
		   else
		   {
			   g.drawBitmap(this.m_TopMaskImage,0, 0,null);
		   }


      }
	}

	@Override
	public void SetOnTouchEvent(MotionEvent event) {
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				this.MouseDown(event);
				break;
			case MotionEvent.ACTION_UP:
				this.MouseUp(event);
				break;
			case MotionEvent.ACTION_MOVE:
				this.MouseMove(event);
				break;
		}
	}
}
