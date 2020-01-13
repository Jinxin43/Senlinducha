package lkmap.Symbol;
import lkmap.Enum.lkSelectionType;
import lkmap.Enum.lkTextPosition;
import android.graphics.*;
import android.graphics.Paint.Style;
import dingtu.ZRoadMap.PubVar;

public class TextSymbol 
{
    public TextSymbol() {}
    
    //��������
    private String _Name = "";
    public String getName()
    {
         return _Name;
    }
    public void setName(String value)
    {
    	 _Name = value;
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
    
    
    private Paint _Font = null;
    public Paint getTextFont()
    {
        if (_Font == null) 
        {
        	_Font = new Paint();
        	_Font.setAntiAlias(true);
        	_Font.setTextSize(20);
        	Typeface TF = Typeface.create("����", Typeface.NORMAL);
        	_Font.setTypeface(TF);
        }
        _Font.setAntiAlias(true);
        return _Font;
    }
    
    public void setTextFont(Paint value)
    {
    	 float s = (float)PubVar.m_DisplayMetrics.densityDpi / 96f;
    	_Font = value;
    	_Font.setTextSize(_Font.getTextSize()*s);
    }

    //��ע��ɫ
    public int getColor()
    {
        return this.getTextFont().getColor();
    }
    public void setColor(int value)
    {
    	this.getTextFont().setColor(value);
    }

    //����ͼ��
    public void Draw(Canvas g, float TextX, float TextY, String Text,lkTextPosition lkTP,lkSelectionType pSelectionType)
    {
    	if (Text==null) return;
        if (lkTP == lkTextPosition.enCenter)
        {
            float LableW = this.getTextFont().measureText(Text);
            TextX -= LableW / 2;
        }

        //if (pSelectionType == lkSelectionType.enSelect) g.DrawString(Text, this.TextFont,System.Drawing.Brushes.Blue, TextX, TextY);
        //g.DrawString(Text, this.TextFont, this._Brush, TextX, TextY);

        //����Google��ͼ�����ּӱ߿�
        //if (PubVar.m_Map.getGMap().getIfLoadGoogleMap())
        {
	        g.drawText(Text, TextX-1, TextY, this.getBKFont());
	        g.drawText(Text, TextX+1, TextY, this.getBKFont());
	        g.drawText(Text, TextX, TextY-1, this.getBKFont());
	        g.drawText(Text, TextX, TextY+1, this.getBKFont());
	        g.drawText(Text, TextX+1, TextY+1, this.getBKFont());
	        g.drawText(Text, TextX-1, TextY-1, this.getBKFont());
	        g.drawText(Text, TextX+1, TextY-1, this.getBKFont());
	        g.drawText(Text, TextX-1, TextY+1, this.getBKFont());
        }
        g.drawText(Text,TextX, TextY, this.getTextFont());
        
      //  g.drawTextOnPath(text, path, hOffset, vOffset, paint)

    }
}
