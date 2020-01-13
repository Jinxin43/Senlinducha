package lkmap.ZRoadMap.MyControl;

import java.util.ArrayList;
import java.util.List;

import lkmap.Tools.Tools;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import dingtu.ZRoadMap.PubVar;

public class v1_Pad_Graffiti extends RelativeLayout
{
	public v1_Pad_Graffiti(Context context) {
		super(context);
		this.IntiPad(context);
		this.SetBKImage(PubVar.m_Map.bp);
	}
	public v1_Pad_Graffiti(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.IntiPad(context);
		this.SetBKImage(PubVar.m_Map.bp);
	}
	public v1_Pad_Graffiti(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.IntiPad(context);
		this.SetBKImage(PubVar.m_Map.bp);
	}

	private ImageView m_ImageView = null;
	private SurfaceView m_SurfaceView = null;
	private SurfaceHolder m_SurfaceHolder = null;
	private void IntiPad(Context context)
	{
        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(0,0);
        para.height = LayoutParams.FILL_PARENT;
        para.width = LayoutParams.FILL_PARENT;
        para.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        para.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        para.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        para.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        
        
		//加入一个ImageView,一个SurfaceView
		this.m_ImageView  = new ImageView(context);
		this.addView(this.m_ImageView);
		this.m_ImageView.setLayoutParams(para);
		
		this.m_SurfaceView = new SurfaceView(context);
		this.addView(this.m_SurfaceView);
		this.m_SurfaceView.setLayoutParams(para);
		
		this.m_SurfaceView.setZOrderOnTop(true);
		this.m_SurfaceHolder = this.m_SurfaceView.getHolder();
		this.m_SurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
	}


    private Bitmap m_Bitmap = null;
    private Canvas m_Canvas = null;
    /**
     * 设置背景底图
     * @param bp
     */
    public void SetBKImage(Bitmap bp)
    {
        this.m_ImageView.setBackgroundDrawable(new BitmapDrawable(bp));
    }
    
    private void CreateImageBitmap()
    {
    	this.m_Bitmap = Bitmap.createBitmap(this.m_ImageView.getMeasuredWidth(),this.m_ImageView.getMeasuredHeight(), Config.ARGB_8888);
    	this.m_Bitmap.eraseColor(Color.TRANSPARENT);
    	this.m_Canvas = new Canvas(this.m_Bitmap);
    	this.m_ImageView.setImageBitmap(this.m_Bitmap);
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) 
    {
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				MouseDown(event);
				break;
			case MotionEvent.ACTION_UP:
				MouseUp(event);
				break;
			case MotionEvent.ACTION_MOVE:
				MouseMove(event);
				break;
		}
		return true;
	}
    /**
     * 保存涂鸦
     * @return
     */
    public boolean SaveTo(String fileName)
    {
    	if (this.m_Bitmap==null) this.CreateImageBitmap();
    	Bitmap exportBitmap = Bitmap.createBitmap(this.m_Bitmap,0,0,this.m_Bitmap.getWidth(),this.m_Bitmap.getHeight());
    	exportBitmap.eraseColor(Color.TRANSPARENT);
    	Canvas g = new Canvas(exportBitmap);
    	this.m_ImageView.getBackground().draw(g);
    	g.drawBitmap(this.m_Bitmap, 0,0, null);
    	boolean OK = Tools.SaveBitmapTo(fileName, exportBitmap);
    	if (!exportBitmap.isRecycled())exportBitmap.recycle();
    	return OK;
    }

    //画笔栈
    private List<DrawAction> m_DoDrawActionList = new ArrayList<DrawAction>();
    
    //回退画笔栈
    private List<DrawAction> m_UndoDrawActionList = new ArrayList<DrawAction>();
    
    public void Undo()
    {
    	if (m_DoDrawActionList.size()==0)
    	{
    		Tools.ShowToast(this.getContext(), "无法回退！");return;
    	}
    	DrawAction da = m_DoDrawActionList.get(m_DoDrawActionList.size()-1);
    	this.m_UndoDrawActionList.add(da);
    	this.m_DoDrawActionList.remove(da);
    	this.m_Bitmap.eraseColor(Color.TRANSPARENT);
    	this.m_ImageView.invalidate();
		this.onTDraw(2);
    }
    
    public void Redo()
    {
       	if (m_UndoDrawActionList.size()==0)
    	{
    		Tools.ShowToast(this.getContext(), "无法重做！");return;
    	}
    	DrawAction da = m_UndoDrawActionList.get(m_UndoDrawActionList.size()-1);
    	this.m_UndoDrawActionList.remove(da);
    	this.m_DoDrawActionList.add(da);
    	this.m_Bitmap.eraseColor(Color.TRANSPARENT);
    	this.m_ImageView.invalidate();
    	this.onTDraw(2);
    }
    
    public int m_DrawMode = 1;   //1-正常画笔，2-橡皮擦
    private boolean m_PenDown = false;
    private boolean m_PenMove = false;
    private DrawAction m_DrawAction = null;
    private void MouseDown(MotionEvent e)
    {
    	if (this.m_Canvas==null) this.CreateImageBitmap();
    	this.m_PenDown = true;
    	this.m_DrawAction = new DrawAction();
    	if (this.m_DrawMode==1)this.m_DrawAction.m_Erase=false;
    	if (this.m_DrawMode==2)this.m_DrawAction.m_Erase=true;

    	this.m_DoDrawActionList.add(this.m_DrawAction);
    	
    	//清回重做栈
    	this.m_UndoDrawActionList.clear();
    }

    private PointF m_LastPointF;
    private void MouseMove(MotionEvent e)
    {
    	if (this.m_PenDown)
    	{
    		this.m_PenMove=true;
    		this.m_LastPointF = new PointF(e.getX(),e.getY());
    		this.m_DrawAction.AddPoint(this.m_LastPointF);
        	this.onTDraw(1);
    	}

    }

    private void MouseUp(MotionEvent e)
    {
    	this.m_PenDown = false;this.m_PenMove=false;
    	this.onTDraw(2);
    }

    private Paint m_PenPaint = null;
    /**
     * 设置画笔样式
     */
    private void SetDrawPenStyle(DrawAction da)
    {
		this.m_PenPaint.setColor(da.m_PenColor);
		this.m_PenPaint.setStrokeWidth(da.m_PenWidth);
		this.m_PenPaint.setXfermode(null);
    }
    private void SetEarsePenStyle()
    {
		this.m_PenPaint.setStrokeWidth(20);
		this.m_PenPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
    }
    
    private void onTDraw(int DrawType)
    {
		if (this.m_DoDrawActionList.size()==0)return;
    	if (this.m_PenPaint==null)
    	{
    		this.m_PenPaint = new Paint();
    		this.m_PenPaint.setAlpha(0);
    		this.m_PenPaint.setDither(true);   //抖动处理，更加平滑
    		this.m_PenPaint.setStyle(Style.STROKE);
    		this.m_PenPaint.setAntiAlias(true);
    	}
    	

		if (DrawType==1) //部分
		{
			DrawAction da = this.m_DoDrawActionList.get(this.m_DoDrawActionList.size()-1);
			if (da.m_Erase)
			{
				this.SetEarsePenStyle();
	    		this.m_Canvas.drawPath(da.m_Path, this.m_PenPaint);
	    		this.m_ImageView.invalidate();
			} else
			{
		    	Canvas mCanvas = this.m_SurfaceHolder.lockCanvas();
		    	mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		    	this.SetDrawPenStyle(da);
	    		mCanvas.drawPath(da.m_Path, this.m_PenPaint);
	    		this.m_SurfaceHolder.unlockCanvasAndPost(mCanvas); 
			}
			if(this.m_DrawMode==2)
			{
				//画擦出圆圈
				if (this.m_PenPaint==null)return;
				Canvas mCanvas = this.m_SurfaceHolder.lockCanvas();
				mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
				Paint cirPen = new Paint();
				cirPen.setStyle(Style.STROKE);
				cirPen.setColor(Color.BLACK);
				mCanvas.drawCircle(this.m_LastPointF.x, this.m_LastPointF.y, this.m_PenPaint.getStrokeWidth()/2, cirPen);
				this.m_SurfaceHolder.unlockCanvasAndPost(mCanvas); 
			}
		}
		
		if (DrawType==2)  //全部
		{
			for(DrawAction da:this.m_DoDrawActionList)
			{
		    	if (!da.m_Erase)
		    	{
		    		this.SetDrawPenStyle(da);
		    		this.m_Canvas.drawPath(da.m_Path, this.m_PenPaint);
		    	} else
		    	{
		    		this.SetEarsePenStyle();
		    		this.m_Canvas.drawPath(da.m_Path, this.m_PenPaint);
		    	}
			}
			this.m_ImageView.invalidate();
	    	Canvas mCanvas = this.m_SurfaceHolder.lockCanvas();
	    	mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
	    	this.m_SurfaceHolder.unlockCanvasAndPost(mCanvas); 
			
		}
    }
    
	//画笔动作
	private class DrawAction
	{
		public List<PointF> m_PenPointList = new ArrayList<PointF>();
		public Path m_Path = new Path();
		public float m_PenWidth = 3;
		public int m_PenColor = Color.RED;
		public boolean m_Erase = false;
		
		public void AddPoint(PointF pt)
		{
			this.m_PenPointList.add(pt);
			if (this.m_PenPointList.size()==1)m_Path.moveTo(pt.x,pt.y);
    		else
			{
    			int len = this.m_PenPointList.size();
    			float previousX = this.m_PenPointList.get(len-2).x;  
    	        float previousY = this.m_PenPointList.get(len-2).y;
    	        float x = this.m_PenPointList.get(len-1).x;  
    	        float y = this.m_PenPointList.get(len-1).y; 
    	        
    	        float dx = Math.abs(x - previousX);  
    	        float dy = Math.abs(y - previousY);  
    	          
    	        //两点之间的距离大于等于3时，生成贝塞尔绘制曲线  
    	        if (dx >= 3 || dy >= 3)  
    	        {  
    	            //设置贝塞尔曲线的操作点为起点和终点的一半  
    	            float cX = (x + previousX) / 2;  
    	            float cY = (y + previousY) / 2;  
    	  
    	            //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点  
    	            m_Path.quadTo(previousX, previousY, cX, cY);  
    	        } else m_Path.quadTo(previousX,previousY,x,y);
				//p.lineTo(da.m_PenPointList.get(i).x, da.m_PenPointList.get(i).y);
			}
		}
	}
}
