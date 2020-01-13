package lkmap.ZRoadMap.MyControl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import dingtu.ZRoadMap.Data.ICallback;

public class v1_ColorPicker_HS extends ImageView {

	public v1_ColorPicker_HS(Context context) 
	{
		super(context);
	}
	public v1_ColorPicker_HS(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public v1_ColorPicker_HS(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	private Canvas m_Graphics = null;
	
	/**
	 * 加载色调及饱合度图
	 */
	public void LoadHS()
	{
		Bitmap bp = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Config.ARGB_8888);
        this.m_Graphics = new Canvas(bp);
        this.setImageBitmap(bp);
        
        Paint pPen = new Paint();
        pPen.setStyle(Style.STROKE);

        for(int i=0;i<=360;i++)
        {
        	float w = this.getMeasuredWidth() / 360f;
        	pPen.setStrokeWidth(w+0.1f);
        	
        	float x1 = w*(i-1);
        	float y1 = 0;
        	float x2 = w*i-w/2;
        	float y2 = this.getMeasuredHeight();
        	
        	this.m_Color_RGB.ToColorByHValue(i, 100);
        	int startColor = this.m_Color_RGB.ToInt();
        	
            LinearGradient lg=new LinearGradient(x1,y1,x2,y2,startColor,Color.argb(255, 127, 127, 127),Shader.TileMode.MIRROR); 
            pPen.setShader(lg);
            this.m_Graphics.drawLine(x1,y1,x2,y2,pPen);
        }
        
        
        

	}
	
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback=cb;}
	
	@Override  
    protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
        Paint pPen = new Paint();
        pPen.setStyle(Style.STROKE);
        pPen.setColor(Color.WHITE);
        pPen.setStrokeWidth(2f);
        canvas.drawCircle(this.m_PickPointX, this.m_PickPointY, 8, pPen);
        
	}
	
	private v1_ColorPicker_RGB m_Color_RGB = new v1_ColorPicker_RGB();
	//制绘颜色拾取点
	private void DrawPickPoint()
	{
		this.invalidate();
		
		float d = 360f / this.getMeasuredWidth() * this.m_PickPointX;
		float s = 100 - 100f / this.getMeasuredHeight() * this.m_PickPointY;
		this.m_Color_RGB.ToColorByHValue(d, s);
		if (this.m_Callback!=null) this.m_Callback.OnClick("Color_HS", this.m_Color_RGB);
		
	}
	
	//选择点的位置
	private float m_PickPointX = 0,m_PickPointY = 0;
	@Override  
    public boolean onTouchEvent(MotionEvent event)
	{
		this.m_PickPointX = event.getX();
		this.m_PickPointY = event.getY();
		if (this.m_PickPointX<0)this.m_PickPointX=0;
		if (this.m_PickPointY<0)this.m_PickPointY=0;
		
		this.DrawPickPoint();
		return true;
	}
	

}
