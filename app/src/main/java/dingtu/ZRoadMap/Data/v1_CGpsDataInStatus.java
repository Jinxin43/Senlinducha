package dingtu.ZRoadMap.Data;

import lkmap.Tools.Tools;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.widget.ImageView;
import android.widget.TextView;

public class v1_CGpsDataInStatus 
{
	private ImageView m_StatusView = null;
	public v1_CGpsDataInStatus(){}
	
	/**
	 * 设置采集状态显示控件
	 * @param tv
	 */
	public void SetStatusView(ImageView tv)
	{
		this.m_StatusView = tv;
	}
	
	private String m_LineStatus = "";
	private String m_PolyStatus = "";
	
	/**
	 * 更新采集状态
	 * @param Type
	 * @param LenStr
	 */
	public void UpdateLineStatus(String Type,double len)
	{
		if (Type.equals("停止") || Type.equals("")){ this.m_LineStatus="";}
		else this.m_LineStatus = "【"+Tools.ToLocale(Type)+"】"+Tools.ToLocale("线")+"="+Tools.ReSetDistance(len, true); 
		
		this.UpdateShow();
	}
	
	/**
	 * 更新采集状态
	 * @param Type
	 * @param AreaStr
	 */
	public void UpdatePolyStatus(String Type,double area)
	{
		if (Type.equals("停止") || Type.equals("")){ this.m_PolyStatus="";}
		else this.m_PolyStatus = "【"+Tools.ToLocale(Type)+"】"+Tools.ToLocale("面")+"="+Tools.ReSetArea(area, true);
		this.UpdateShow();
	}
	
	
	private Canvas m_g = null;
	private Bitmap m_bp = null;

	/**
	 * 更新状态显示
	 */
	private void UpdateShow()
	{
		if (this.m_g==null)
		{
			this.m_bp = Bitmap.createBitmap(this.m_StatusView.getMeasuredWidth(), this.m_StatusView.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
			this.m_g = new Canvas(this.m_bp);
			this.m_StatusView.setImageBitmap(this.m_bp);
		}
		
		//计算文字位置以及文字换行的控制
		this.m_bp.eraseColor(Color.TRANSPARENT);
		float TextX = 0,TextY = this.m_g.getHeight()-4,OffsetY = 0;
		if (!this.m_PolyStatus.equals(""))
		{
			OffsetY = this.getTextPen().getTextSize();
			TextX = (float) (this.m_g.getWidth() * 0.5 - this.getTextPen().measureText(this.m_PolyStatus)* 0.5);
			this.DrawTextStroke(this.m_PolyStatus, TextX,TextY);
			TextY -= (OffsetY);
		}
		
		if (!this.m_LineStatus.equals(""))
		{
			TextX = (float) (this.m_g.getWidth() * 0.5 - this.getTextPen().measureText(this.m_LineStatus)* 0.5);
			this.DrawTextStroke(this.m_LineStatus, TextX,TextY);
		}
	}
	
	/**
	 * 画描边文字
	 * @param Text
	 * @param X
	 * @param Y
	 */
	private void DrawTextStroke(String Text,float TextX,float TextY)
	{
		//画描边文字
		this.m_g.drawText(Text, TextX-2, TextY, this.getBKFont());
		this.m_g.drawText(Text, TextX+2, TextY, this.getBKFont());
		this.m_g.drawText(Text, TextX, TextY-2, this.getBKFont());
		this.m_g.drawText(Text, TextX, TextY+2, this.getBKFont());
		this.m_g.drawText(Text, TextX+2, TextY+2, this.getBKFont());
		this.m_g.drawText(Text, TextX-2, TextY-2, this.getBKFont());
		this.m_g.drawText(Text, TextX+2, TextY-2, this.getBKFont());
		this.m_g.drawText(Text, TextX-2, TextY+2, this.getBKFont());
		
		this.m_g.drawText(Text, TextX,TextY , this.getTextPen());
	}
	
	//标注字体
	private TextPaint m_TextPen = null;
	private TextPaint getTextPen()
	{
		if (this.m_TextPen==null)
		{
	    	this.m_TextPen = new TextPaint();
	    	this.m_TextPen.setColor(Color.BLUE);
	    	this.m_TextPen.setAntiAlias(true);
	    	Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
	    	this.m_TextPen.setTypeface(TF);
	    	this.m_TextPen.setTextSize(Tools.SPToPix(18));
		}
		return this.m_TextPen;
	}
	
    //标注的描边
    private Paint _BKFont = null;
    private Paint getBKFont()
    {
        if (_BKFont == null) 
        {
        	_BKFont = new Paint();
        	_BKFont.setColor(Color.WHITE);
        	_BKFont.setAntiAlias(true);
        	
        	Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
        	_BKFont.setTypeface(TF);
        	_BKFont.setAntiAlias(true);
        }
        _BKFont.setTextSize(this.m_TextPen.getTextSize());
        return _BKFont;
    }
}
