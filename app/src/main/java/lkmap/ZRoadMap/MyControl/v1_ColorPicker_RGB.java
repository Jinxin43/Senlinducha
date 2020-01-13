package lkmap.ZRoadMap.MyControl;

import android.graphics.Color;

/**
 * RGB��
 * @author lmgk
 *
 */
public class v1_ColorPicker_RGB 
{
	public int R = 0;
	public int G = 0;
	public int B = 0;
	public int A = 255;
	/**
	 * ת��Ϊʮ������
	 * @return
	 */
	public String ToHex()
	{
		String AStr = Integer.toHexString(this.A);if (AStr.length()==1)AStr="0"+AStr;
		String RStr = Integer.toHexString(this.R);if (RStr.length()==1)RStr="0"+RStr;
		String GStr = Integer.toHexString(this.G);if (GStr.length()==1)GStr="0"+GStr;
		String BStr = Integer.toHexString(this.B);if (BStr.length()==1)BStr="0"+BStr;
		return (AStr+RStr+GStr+BStr).toUpperCase();
	}
	
	public int ToInt()
	{
		return Color.argb(this.A , this.R,this.G,this.B);
	}
	
	/**
	 * ����ɫ�����������Ͷȼ�����ɫֵ
	 * @param ɫ������(0-360��)
	 * @param ���Ͷ���(0-100%) 
	 */
	public void ToColorByHValue(float d,float s)
	{
		//ɫ������
		if (d>=0 && d<=60){this.R = 255;this.G = (int)(255f / 60 * d);this.B = 0;}
		if (d>=61 && d<=120){this.R = (int)(255-255f /60 * (d-60));this.G = 255;this.B = 0;}
		if (d>=121 && d<=180){this.R = 0;this.G = 255;this.B = (int)(255f / 60 * (d-120));}
		if (d>=181 && d<=240){this.R = 0;this.G = (int)(255-255f / 60 * (d-180));this.B = 255;}
		if (d>=241 && d<=300){this.R = (int)(255f / 60 * (d-240));this.G = 2;this.B = 255;}
		if (d>=301 && d<=360){this.R = 255;this.G = 0;this.B = (int)(255-255f / 60 * (d-300));}
		
		//���Ͷȼ���
		int r = 127,g = 127,b = 127;
		float ds = 1- s / 100;
		this.R = (int)((1-ds) * this.R + ds * r);
		this.G = (int)((1-ds) * this.G + ds * g);
		this.B = (int)((1-ds) * this.B + ds * b);
	}
	
	/**
	 * �������ȼ�����ɫֵ
	 * @param bright 0-100
	 */
	public void ToColorByB(float bright)
	{
		float ds = 0;
		//0-50��ʾ����ɫ��51-100��ʾ����ɫ
		int r=255,g=255,b=255;
		if (bright>=0 && bright<=50){r=255;g=255;b=255;ds = 1- bright / 50f;}
		if (bright>=51 && bright<=100){r=0;g=0;b=0;bright-=50;ds = bright / 50f;}
		
		this.R = (int)((1-ds) * this.R + ds * r);
		this.G = (int)((1-ds) * this.G + ds * g);
		this.B = (int)((1-ds) * this.B + ds * b);
	}

}
