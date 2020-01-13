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

public class v1_ColorPicker_B extends ImageView {

	public v1_ColorPicker_B(Context context) 
	{
		super(context);
	}
	public v1_ColorPicker_B(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public v1_ColorPicker_B(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	private Canvas m_Graphics = null;
	private Bitmap m_Bitmap = null;
	
	private v1_ColorPicker_RGB m_Base_Color_RGB = null;
	
	/**
	 * 设置基色
	 */
	public void Set(int R,int G,int B)
	{
		if (this.m_Base_Color_RGB==null)this.m_Base_Color_RGB = new v1_ColorPicker_RGB();
		this.m_Base_Color_RGB.R = R;this.m_Base_Color_RGB.G=G;this.m_Base_Color_RGB.B=B;
		
		if (this.m_Bitmap==null)
		{
			this.m_Bitmap = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Config.ARGB_8888);
			this.setImageBitmap(this.m_Bitmap);
			this.m_Graphics = new Canvas(this.m_Bitmap);
		}

        Paint pPen = new Paint();
        pPen.setStyle(Style.STROKE);
        pPen.setStrokeWidth(this.getMeasuredHeight());
        int[] Colors = new int[]{Color.WHITE,Color.argb(255,R,G,B),Color.BLACK};
        LinearGradient lg=new LinearGradient(0,0, this.getMeasuredWidth(),this.getMeasuredHeight(), Colors, null, Shader.TileMode.MIRROR);
        //LinearGradient lg=new LinearGradient(0,0, this.getWidth(),this.getHeight(), Color.WHITE, Color.argb(255,R,G,B), Shader.TileMode.MIRROR);
        
        pPen.setShader(lg);
        this.m_Graphics.drawLine(0,this.getMeasuredHeight()/2, this.getMeasuredWidth(),this.getMeasuredHeight()/2,pPen);
        
        this.DrawPickPoint();
	}
	
	/**
	 * 设置缺省值
	 */
	public void SetDefaultValue()
	{
		this.m_PickPointX=this.getMeasuredWidth()/2;
		this.Set(255,0,0);
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
	
	private v1_ColorPicker_RGB m_Color_RGB = new v1_ColorPicker_RGB();
	
	//制绘颜色拾取点
	private void DrawPickPoint()
	{
		this.invalidate();
		if (this.m_PickPointX<=0)this.m_PickPointX=1;
		float bright = 100f / this.getMeasuredWidth() * this.m_PickPointX;
		this.m_Color_RGB.R = this.m_Base_Color_RGB.R;
		this.m_Color_RGB.G = this.m_Base_Color_RGB.G;
		this.m_Color_RGB.B = this.m_Base_Color_RGB.B;
		this.m_Color_RGB.ToColorByB(bright);
		if (this.m_Callback!=null) this.m_Callback.OnClick("Color_B", this.m_Color_RGB);
		
	}
	
	//选择点的位置
	private float m_PickPointX = 0;
	@Override  
    public boolean onTouchEvent(MotionEvent event)
	{
		this.m_PickPointX = event.getX();
		if (this.m_PickPointX<=0)this.m_PickPointX=1;
		if (this.m_PickPointX>this.getMeasuredWidth())this.m_PickPointX=this.getMeasuredWidth();
		this.DrawPickPoint();
		return true;
	}
	
}
