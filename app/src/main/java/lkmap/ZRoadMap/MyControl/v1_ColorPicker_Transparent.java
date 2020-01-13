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

public class v1_ColorPicker_Transparent extends ImageView {

	public v1_ColorPicker_Transparent(Context context) 
	{
		super(context);
	}
	public v1_ColorPicker_Transparent(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public v1_ColorPicker_Transparent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	private Canvas m_Graphics = null;
	private Bitmap m_Bitmap = null;
	
	
	/**
	 * 创建表面
	 */
	public void Create()
	{
		if (this.m_Bitmap==null)
		{
			this.m_Bitmap = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Config.ARGB_8888);
			this.setImageBitmap(this.m_Bitmap);
			this.m_Graphics = new Canvas(this.m_Bitmap);
		}

        Paint pPen = new Paint();
        pPen.setStyle(Style.STROKE);
        pPen.setColor(Color.LTGRAY);
        pPen.setStrokeWidth(this.getMeasuredHeight());
        this.m_Graphics.drawLine(0,this.getMeasuredHeight()/2, this.getMeasuredWidth(),this.getMeasuredHeight()/2,pPen);
        
        this.m_PickPointX=this.getMeasuredWidth();
        
        this.DrawPickPoint();
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
        //canvas.drawLine(this.m_PickPointX, 0, this.m_PickPointX, this.getHeight(), pPen);
        canvas.drawRect(this.m_PickPointX-3, -3, this.m_PickPointX+3, this.getHeight()+3, pPen);
	}
	
	//制绘颜色拾取点
	private void DrawPickPoint()
	{
		this.invalidate();
		
		float bright = 255f / this.getMeasuredWidth() * this.m_PickPointX;
		if (this.m_Callback!=null) this.m_Callback.OnClick("Color_Transparent", (int)bright);
		
	}
	
	//选择点的位置
	private float m_PickPointX = 0;
	@Override  
    public boolean onTouchEvent(MotionEvent event)
	{
		this.m_PickPointX = event.getX();
		if (this.m_PickPointX<0)this.m_PickPointX=0;
		if (this.m_PickPointX>this.getMeasuredWidth())this.m_PickPointX=this.getMeasuredWidth();
		this.DrawPickPoint();
		return true;
	}
	
}
