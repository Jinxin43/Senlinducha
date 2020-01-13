package lkmap.Symbol;

import java.util.ArrayList;
import java.util.List;

import lkmap.Enum.lkGeoLayerType;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;

public class SymbolConvertTools 
{
	
    /**
     * ����ͼ����ţ�����Ÿ�ʽ����ValueΪBase64��ʽ��
     * 				�߷��Ÿ�ʽ����ɫ1,���1,���Ͷ���1@��ɫ2,���2,���Ͷ���2.....
     * 				����Ÿ�ʽ����ɫ,����ɫ,���߿�
     * @param value
     */
    public static ISymbol StrToSymbol(String value,lkGeoLayerType geoLayerType)
    {
		if (geoLayerType==lkGeoLayerType.enPoint)
		{
			PointSymbol PS = new PointSymbol();
			PS.CreateByBase64(value);
			return PS;
		}
		if (geoLayerType==lkGeoLayerType.enPolyline)
		{
			LineSymbol LS = new LineSymbol();
			LS.CreateByBase64(value);
			return LS;
		}
		if (geoLayerType==lkGeoLayerType.enPolygon)
		{
			//��ɫ,����ɫ,���߿�
			PolySymbol PS = new PolySymbol();
			PS.CreateByBase64(value);
			return PS;
		}
		
		return null;
    }

//    //�ַ���ת������ɫ
//    private static int StrToColor(String StrColor)
//    {
//        return StrToColor(StrColor,255);
//
//    }
//    private static int StrToColor(String StrColor,int TranValue)
//    {
//        String[] ColorInfo = StrColor.split("-");
//        return Color.argb(TranValue, Integer.valueOf(ColorInfo[0]),
//        					 		 Integer.valueOf(ColorInfo[1]),
//        					 		 Integer.valueOf(ColorInfo[2]));
//    }
}
