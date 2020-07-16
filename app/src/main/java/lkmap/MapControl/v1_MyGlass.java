package lkmap.MapControl;

import lkmap.Cargeometry.Coordinate;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import dingtu.ZRoadMap.PubVar;


public class v1_MyGlass extends View implements IOnPaint{

	private String Id = "Fangdajing";
	public v1_MyGlass(Context context) {
		super(context);
		//PubVar.m_MapControl.AddOnPaint(this.Id, this);
	}
	public v1_MyGlass(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public v1_MyGlass(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	private int m_LocalX,m_LocalY;
	public void SetGlassPoint(double x, double y)
	{
		this.m_LocalX = (int)x;
		this.m_LocalY = (int)y;
		this.invalidate();
	}
	
	private String mScale="中";
	public void setGlassScale(String scale)
	{
		mScale = scale;
	}
	public String getGlassScale()
	{
		return mScale;
	}
	
	public void SetVisible(boolean visible)
	{
		if (visible)
		{
			this.setVisibility(View.VISIBLE);
		}
		else 
		{
			this.setVisibility(View.GONE);
		}
	}



//	int RADIUS = 35;
//	int RADIUS = 70;
//	int FACTOR = 2;
	
	@Override
	public void OnPaint(Canvas canvas) 
	{
		if (this.getVisibility()!=View.VISIBLE) return;
		if(PubVar.m_Map==null)return;
		if (PubVar.m_Map.bp==null) return;
		
		Bitmap bitmap =loadBitmapFromView(PubVar.m_MapControl);
		if(bitmap == null)
		{
			return;
		}
		
		int Scale = this.getWidth()/4;
		Rect r1 = new Rect(this.m_LocalX-Scale,this.m_LocalY-Scale,this.m_LocalX+Scale,this.m_LocalY+Scale);
		Rect r2 = new Rect(0,0,this.getWidth(),this.getHeight());
		 
		
		
//		Path mPath = new Path();
//		mPath.addCircle(this.getWidth()/2, this.getHeight()/2, this.getWidth()/2, Path.Direction.CCW);  
//		PubVar.m_Map.getDisplayGraphic().clipPath(mPath, Op.REPLACE);
		
		Path mPath = new Path();
		mPath.addCircle(this.getWidth()/2, this.getHeight()/2, this.getWidth()/2, Path.Direction.CCW);
		if(Build.VERSION.SDK_INT >= 28){
			canvas.clipPath(mPath);
		}else {
			canvas.clipPath(mPath, Op.REPLACE);
		}
		canvas.drawBitmap(bitmap, r1,r2,null);
		
		Paint mPaint = new Paint();
		mPaint.setColor(Color.GRAY);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(10);
		canvas.drawCircle(this.getWidth()/2, this.getHeight()/2, this.getWidth()/2, mPaint);
		
		
		
		//画十字
		mPaint.setColor(Color.BLUE);
		mPaint.setStrokeWidth(3);
		float gx = this.getWidth()/2,gy = this.getHeight()/2;
		canvas.drawLine(gx-50,gy,gx+50,gy, mPaint);
		canvas.drawLine(gx,gy-50,gx,gy+50, mPaint);
		
	}

	private Bitmap loadBitmapFromView(View v) {
		if (v == null) {
			return null;
		}
		Bitmap screenshot;
		screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(screenshot);
		c.translate(-v.getScrollX(), -v.getScrollY());
		v.draw(c);
		return screenshot;
	}



	@Override
	protected void onDraw(Canvas canvas)
	{
		OnPaint(canvas);
//		super.onDraw(canvas);
//		
//		if (this.getVisibility()!=View.VISIBLE) return;
//		if(PubVar.m_Map==null)return;
//		Bitmap bitmap = PubVar.m_Map.bp;
//        
//        if(bitmap == null)
//        {
//        	return;
//        }
        
//        //canvas.translate(-FACTOR*RADIUS,-FACTOR*RADIUS);
//        Path path = new Path();
//		path.addCircle(this.m_LocalX, this.m_LocalY, 2*100,Direction.CW);
//		
//		
//		
//		//放大镜加外框
//		Paint pBrush= new Paint();
//		pBrush.setStyle(Style.STROKE);
//		pBrush.setStrokeWidth(10);
//        pBrush.setColor(Color.GRAY);
//		canvas.drawCircle(2*100,2*100+400, 2*100, pBrush);
//				
//		canvas.translate(2*100-this.m_LocalX,2*100-this.m_LocalY+400);
//		
//		
//		
//		Matrix matrix = new Matrix();
//		matrix.setScale(2,2);
//		matrix.postTranslate(-(2-1)*this.m_LocalX, -(2-1)*this.m_LocalY);
//		canvas.clipPath(path,Op.REPLACE);
//		canvas.drawBitmap(PubVar.m_Map.bp, matrix, null);
//		
//		//画十字丝
//		pBrush.setColor(Color.RED);
//		pBrush.setStrokeWidth(3);
//		float gx = this.getWidth()/2,gy = this.getHeight()/2;
//		canvas.drawLine(gx-50,gy,gx+50,gy, pBrush);
//		canvas.drawLine(gx,gy-50,gx,gy+50, pBrush);
//		super.onDraw(canvas);
        
//		if (this.getVisibility()!=View.VISIBLE) return;
//		if(PubVar.m_Map==null)return;
//		Bitmap bitmap = PubVar.m_Map.bp;
//        
//        if(bitmap == null)
//        {
//        	return;
//        }
//        
//        Path path = new Path();
//		path.addCircle(this.m_LocalX, this.m_LocalY,FACTOR*RADIUS,Direction.CCW);
//		
//		
//		
//		//放大镜加外框
//		Paint pBrush= new Paint();
//		pBrush.setStyle(Style.STROKE);
//		pBrush.setStrokeWidth(5);
//        pBrush.setColor(Color.GRAY);
//		canvas.drawCircle(FACTOR*RADIUS,FACTOR*RADIUS, RADIUS*FACTOR, pBrush);
//		canvas.translate(FACTOR*RADIUS-this.m_LocalX,FACTOR*RADIUS-this.m_LocalY);


		//放大镜加外框
//		Paint pBrush= new Paint();
//		pBrush.setStyle(Style.STROKE);
//		pBrush.setStrokeWidth(5);
//        pBrush.setColor(Color.GRAY);
//		canvas.drawCircle(FACTOR*RADIUS,FACTOR*RADIUS, RADIUS*FACTOR, pBrush);
//		canvas.translate(FACTOR*RADIUS-m_LocalX,FACTOR*RADIUS-m_LocalY);
//		
//		Matrix matrix = new Matrix();
//		matrix.setScale(FACTOR,FACTOR);
//		matrix.postTranslate(-(FACTOR-1)*m_LocalX, -(FACTOR-1)*m_LocalY);
//		canvas.clipPath(path,Op.REPLACE);
//		canvas.drawBitmap(bitmap, matrix, null);
		
//		
//		Matrix matrix = new Matrix();
//		matrix.setScale(FACTOR,FACTOR);
//		matrix.postTranslate(-(FACTOR-1)*this.m_LocalX, -(FACTOR-1)*this.m_LocalY);
//		canvas.clipPath(path);
//		canvas.drawBitmap(bitmap, matrix, null);
//		
//		//画十字丝
//		pBrush.setColor(Color.RED);
//		pBrush.setStrokeWidth(3);
//		float gx = this.getWidth()/2,gy = this.getHeight()/2;
//		
//		canvas.drawLine(gx-100-FACTOR*RADIUS+this.m_LocalX,gy-FACTOR*RADIUS+this.m_LocalY,gx+100-FACTOR*RADIUS+this.m_LocalX,gy-FACTOR*RADIUS+this.m_LocalY, pBrush);
//		canvas.drawLine(gx-FACTOR*RADIUS+this.m_LocalX,gy-100-FACTOR*RADIUS+this.m_LocalY,gx-FACTOR*RADIUS+this.m_LocalX,gy+100-FACTOR*RADIUS+this.m_LocalY, pBrush);
//		super.onDraw(canvas);
	}

	public void Refresh() {
		this.invalidate();
	}

}
