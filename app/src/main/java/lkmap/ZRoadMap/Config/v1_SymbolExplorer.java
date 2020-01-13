package lkmap.ZRoadMap.Config;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Symbol.LineSymbol;
import lkmap.Symbol.PointSymbol;
import lkmap.Symbol.PolySymbol;
import lkmap.Tools.Tools;


public class v1_SymbolExplorer 
{
	private ASQLiteDatabase m_ASQLiteDatabase = null;
	
	/**
	 * �����ò�����
	 * @param projectDB
	 */
	public void SetBindSQLiteDatabase(ASQLiteDatabase asqlitedb)
	{
		this.m_ASQLiteDatabase = asqlitedb;
	}
	
	/**
	 * ��ȡ����ʾ��ͼ��
	 * @param SymbolBase64Str  ��������
	 * @param geoLayerType  ��������
	 * @return
	 */
	public v1_SymbolObject GetSymbolObject(String SymbolBase64Str,lkGeoLayerType geoLayerType)
	{
		return this.GetSymbolObject(SymbolBase64Str, geoLayerType,64);
	}
	public v1_SymbolObject GetSymbolObject(String SymbolBase64Str,lkGeoLayerType geoLayerType,int Width)
	{
		v1_SymbolObject SO = new v1_SymbolObject();
		int H = 40,W = Width;
		if (geoLayerType==lkGeoLayerType.enPoint)
		{
			PointSymbol PS = new PointSymbol();
			PS.CreateByBase64(SymbolBase64Str);
			SO.SymbolFigure = PS.ToFigureBitmap(W,H);
			SO.SymbolBase64Str = SymbolBase64Str;
		}
		if (geoLayerType==lkGeoLayerType.enPolyline)
		{
			LineSymbol LS = new LineSymbol();
			LS.CreateByBase64(SymbolBase64Str);
			SO.SymbolFigure = LS.ToFigureBitmap(W, H);
			SO.SymbolBase64Str = SymbolBase64Str;
		}
		if (geoLayerType==lkGeoLayerType.enPolygon)
		{
			PolySymbol PS = new PolySymbol();
			PS.CreateByBase64(SymbolBase64Str);
			SO.SymbolFigure = PS.ToFigureBitmap(W, H);
			SO.SymbolBase64Str = SymbolBase64Str;
		}
		return SO;
	}
	
	/**
	 * ��ȡ����ʾ��ͼ��
	 * @param SymbolNameList  �����������飬���Ϊstring[]��Ϊ��ȡȫ������
	 * @param geoLayerType  ��������
	 * @return
	 */
	public List<v1_SymbolObject> GetSymbolObjectList(String[] SymbolNameList,lkGeoLayerType geoLayerType)
	{
		//СͼƬ�Ĵ�С
		int H = 40,W = 64;
		
		//��ѯ����
		String WhereName = "1=1";
		if (SymbolNameList!=null)if (SymbolNameList.length>0)WhereName = " name in ('"+Tools.Joins("','", SymbolNameList)+"')";
		
		//�����
		if (geoLayerType==lkGeoLayerType.enPoint)
		{
			List<v1_SymbolObject> SymbolFigureList = new ArrayList<v1_SymbolObject>();
			String SQL = "Select Name,Symbol from T_PointSymbol where "+WhereName;
			SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
			if (DR==null)return null;
			while(DR.Read())
			{
				v1_SymbolObject SO = new v1_SymbolObject();
				byte[] symByte = DR.GetBlob("Symbol");
				SO.SymbolName = DR.GetString("Name");
				if (symByte!=null)
				{
					Bitmap sbp = BitmapFactory.decodeByteArray(symByte, 0, symByte.length);
					PointSymbol PS = new PointSymbol();
					PS.setIcon(sbp);
					SO.SymbolFigure = PS.ToFigureBitmap(sbp.getWidth(),sbp.getHeight());
					SO.SymbolBase64Str = PS.ToBase64();
				}
				SymbolFigureList.add(SO);
				
			}DR.Close();
			return SymbolFigureList;
		}
		
		//�߷���
		if (geoLayerType==lkGeoLayerType.enPolyline)
		{
			List<v1_SymbolObject> SymbolFigureList = new ArrayList<v1_SymbolObject>();
			String SQL = "Select Name,Symbol from T_LineSymbol where "+WhereName;
			SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
			if (DR==null)return null;
			while(DR.Read())
			{
				v1_SymbolObject SO = new v1_SymbolObject();
				String SymStr = DR.GetString("Symbol");
				SO.SymbolName = DR.GetString("Name");
				if (!SymStr.equals(""))
				{
					LineSymbol LS = new LineSymbol();
					LS.CreateByBase64(SymStr);
					SO.SymbolFigure = LS.ToFigureBitmap(120, H);
					SO.SymbolBase64Str = LS.ToBase64();
				}
				SymbolFigureList.add(SO);
				
			}DR.Close();
			return SymbolFigureList;
		}
		
		//�����
		if (geoLayerType==lkGeoLayerType.enPolygon)
		{
			List<v1_SymbolObject> SymbolFigureList = new ArrayList<v1_SymbolObject>();
			String SQL = "Select Name,PColor,LColor,LWidth from T_PolySymbol where "+WhereName;
			SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
			if (DR==null)return null;
			while(DR.Read())
			{
				v1_SymbolObject SO = new v1_SymbolObject();
				String PColor = DR.GetString("PColor");
				String LColor = DR.GetString("LColor");
				String LWidth = DR.GetString("LWidth");
				SO.SymbolName = DR.GetString("Name");
				PolySymbol PS = new PolySymbol();
				PS.CreateByBase64(PColor+","+LColor+","+LWidth);
				SO.SymbolBase64Str = PS.ToBase64();
				SO.SymbolFigure = PS.ToFigureBitmap(W, H);
				SymbolFigureList.add(SO);
				
			}DR.Close();
			return SymbolFigureList;
		}
		
		return null;
		
	}
	
//	/**
//	 * ��ȡ�����
//	 * @param PointSymbolName
//	 * @return
//	 */
//	public PointSymbol GetPointSymbol(String PointSymbolName)
//	{
//		//��ȡ����
//		PointSymbol PS = new PointSymbol();
//		if (PointSymbolName.equals(""))PointSymbolName = "Ĭ��";
//		byte[] symByte = null;
//		String SQL = "Select Symbol from T_PointSymbol where name = '"+PointSymbolName+"'";
//		SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
//		if (DR==null)return PS;
//		if(DR.Read())
//		{
//			symByte = DR.GetBlob("Symbol");
//		}DR.Close();
//		if (symByte!=null) {PS.setIcon(BitmapFactory.decodeByteArray(symByte, 0, symByte.length));return PS;}
//		else return PS;
//	}
//
//	/**
//	 * ��ȡ�߷���
//	 * @param LineSymbolName
//	 * @return
//	 */
//	public LineSymbol GetLineSymbol(String LineSymbolName)
//	{
//		//��ȡ�߷��ţ�
//        LineSymbol LS = new LineSymbol();
//        if (LineSymbolName.equals(""))LineSymbolName = "Ĭ��";
//        
//        //Symbol��ʽ����ɫ,���@��ɫ,���@��ɫ,���...
//        String symbolInfo = "";
//        String SQL = "Select Symbol from T_LineSymbol where name = '"+LineSymbolName+"'";
//		SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
//		if (DR==null)return LS;
//		if(DR.Read())
//		{
//			symbolInfo = DR.GetString("Symbol");
//		}DR.Close();
//		
//		String[] symList = symbolInfo.split("@");
//		List<Paint> pPenList = new ArrayList<Paint>();
//		for(String sym:symList)  //��ʽ����ɫ,���,����ʽ
//		{
//	        Paint pPen = new Paint();
//	        pPen.setAntiAlias(true);
//	        pPen.setStyle(android.graphics.Paint.Style.STROKE);
//	        if (!sym.equals(""))
//	        {
//	        	String[] symStyle = sym.split(",");
//		        pPen.setColor(Color.parseColor(symStyle[0]));
//		        pPen.setStrokeWidth(Float.valueOf(symStyle[1]));
//		        if (symStyle.length==3)
//		        {
//		        	String[] syList = symStyle[2].split("-");
//		        	float[] syFList = new float[syList.length];
//		        	for(int i=0;i<syList.length;i++)syFList[i]=Float.parseFloat(syList[i]);
//			        PathEffect effects = new DashPathEffect(syFList,0);  
//			        pPen.setPathEffect(effects);
//		        }
//	        }
//	        pPenList.add(pPen);
//		}
//		LS.setStyle(pPenList);
//        return LS;
//	}
//	
//	/**
//	 * ��ȡ�߷���
//	 * @param LineSymbolName
//	 * @return
//	 */
//	public PolySymbol GetPolySymbol(String PolySymbolName)
//	{
//		//��ȡ����ţ�
//		PolySymbol PS = new PolySymbol();
//        if (PolySymbolName.equals(""))PolySymbolName = "Ĭ��";
//        
//        String SQL = "Select * from T_PolySymbol where name = '"+PolySymbolName+"'";
//		SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
//		if (DR==null)return PS;
//		if(DR.Read())
//		{
//			String PColor = DR.GetString("PColor");
//			String LColor = DR.GetString("LColor");
//			float LWidth = Float.parseFloat(DR.GetString("LWidth"));
//			Paint PBrush = new Paint();Paint LBrush = new Paint();
//			PBrush.setColor(Color.parseColor(PColor));
//			PBrush.setStyle(Paint.Style.FILL);
//			PBrush.setAntiAlias(true);
//			
//			LBrush.setColor(Color.parseColor(LColor));
//			LBrush.setStyle(Paint.Style.STROKE);
//			LBrush.setAntiAlias(true);
//			LBrush.setStrokeWidth(LWidth);
//			
//			PS.setPStyle(PBrush);PS.setLStyle(LBrush);
//		}DR.Close();
//		
//        return PS;
//	}
	

	
	
}
