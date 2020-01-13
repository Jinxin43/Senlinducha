package lkmap.Render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import lkmap.Cargeometry.Geometry;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkRenderType;
import lkmap.Layer.GeoLayer;
import lkmap.Symbol.ISymbol;
import lkmap.Symbol.LineSymbol;
import lkmap.Symbol.PointSymbol;
import lkmap.Symbol.PolySymbol;
import lkmap.Symbol.SymbolConvertTools;
import lkmap.Symbol.TextSymbol;
import lkmap.Tools.Tools;

public class SimpleRender extends IRender 
{
	//����
    private ISymbol _Symbol = null;
    private GeoLayer _GeoLayer = null;
    public SimpleRender(GeoLayer pGeoLayer)
    {
        //��Ⱦ������
        this.setType(lkRenderType.enSimple);

        //����Ⱦ������Ⱦ��ͼ��
        this._GeoLayer = pGeoLayer;
        
		//�����ȡ����
		if (this._GeoLayer.getType()==lkGeoLayerType.enPoint)this._Symbol = new PointSymbol();
		if (this._GeoLayer.getType()==lkGeoLayerType.enPolyline)this._Symbol = new LineSymbol();
		if (this._GeoLayer.getType()==lkGeoLayerType.enPolygon)this._Symbol = new PolySymbol();
    }

    //ͼ�����
    public ISymbol getSymbol()
    {
    	return this._Symbol;
    }
    
    /**
     * ����ͼ����ţ�����Ÿ�ʽ����ValueΪBase64��ʽ��
     * 				�߷��Ÿ�ʽ����ɫ1,���1,���Ͷ���1@��ɫ2,���2,���Ͷ���2.....
     * 				����Ÿ�ʽ����ɫ,����ɫ,���߿�
     * @param value
     */
    public void setSymbol(String value)
    {
    	this._Symbol = SymbolConvertTools.StrToSymbol(value,this._GeoLayer.getType());
    	
		if (this._GeoLayer.getType()==lkGeoLayerType.enPolygon)
		{
			//͸����
			((PolySymbol)this._Symbol).SetTransparent(this._SymbolTransparent);
		}
    }

    //����͸���ȣ�Ŀǰֻ�������ţ�
    private int _SymbolTransparent = 0;
    public void SetSymbolTransparent(int transparent)
    {
    	this._SymbolTransparent = transparent;
		if (this._GeoLayer.getType()==lkGeoLayerType.enPolygon)
		{
			if (this._Symbol!=null)((PolySymbol)this._Symbol).SetTransparent(this._SymbolTransparent);
		}
    }
    
    
    /**
     * ����ͼ�����ã�����ͼ�����Symbol
     */
	@Override
	public void UpdateSymbolSet()
    {
		//��ע����
		if (this.getIfLabel())
		{
			String labelFont = this.getLabelFont();  //��ʽ����ɫ,��С
     		Paint _Font = new Paint();
        	_Font.setAntiAlias(true);
        	_Font.setTextSize(Float.valueOf(labelFont.split(",")[1]));
        	Typeface TF = Typeface.create("����", Typeface.NORMAL);
        	_Font.setTypeface(TF);
        	_Font.setColor(Color.parseColor(labelFont.split(",")[0]));
        	TextSymbol TS = new TextSymbol();
        	TS.setTextFont(_Font);
			this._Symbol.setTextSymbol(TS);
		}
    }

    //ֻ����ָ������ʵ��ķ���
	@Override
	public void UpdateSymbol(Geometry pGeometry)
    {
		if (pGeometry!=null) pGeometry.setSymbol(this._Symbol);
    }

	//���±�ע��Ϣ
	@Override
	public void UpdateAllLabel() 
	{
		//�ڴ˼Ӷ�ȡ��עֵ
		String SQL = "select %1$s from "+this._GeoLayer.getDataset().getDataTableName()+" where SYS_ID in (%2$s) and SYS_STATUS='0'";
		String SelectField = "SYS_ID,(" + this.getLabelField().replace(",","||','||") + ") as LabelField";
        List<String> SYSID = new ArrayList<String>();
        Collection<Geometry> geometryList = this._GeoLayer.getDataset().GetGeometryList();
        for(Geometry pGeometry:geometryList)SYSID.add(pGeometry.getSysId()+"");
        if (SYSID.size()==0) return;
        SQL = String.format(SQL, SelectField,Tools.JoinT(",", SYSID));
        SQLiteDataReader DR = this._GeoLayer.getDataset().getDataSource().Query(SQL);
        if (DR!=null)while(DR.Read())
        {
        	String SYS_ID = DR.GetString("SYS_ID");
        	Geometry pGeometry = this._GeoLayer.getDataset().GetGeometry(Integer.parseInt(SYS_ID));
        	pGeometry.setTag(DR.GetString("LabelField"));
        }DR.Close();
	}
}
