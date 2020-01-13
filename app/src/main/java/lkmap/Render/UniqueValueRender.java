package lkmap.Render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
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
import lkmap.ZRoadMap.Config.v1_SymbolObject;

public class UniqueValueRender extends IRender
{
    //ͼ��
    private GeoLayer _GeoLayer = null;
    public UniqueValueRender(GeoLayer pGeoLayer)
    {
        //��Ⱦ������
        this.setType(lkRenderType.enUniqueValue);

        //����Ⱦ������Ⱦ��ͼ��
        this._GeoLayer = pGeoLayer;
        
		//ȱʡ����
		if (this._GeoLayer.getType()==lkGeoLayerType.enPoint)this._DefaultSymbol = new PointSymbol();
		if (this._GeoLayer.getType()==lkGeoLayerType.enPolyline)this._DefaultSymbol = new LineSymbol();
		if (this._GeoLayer.getType()==lkGeoLayerType.enPolygon)this._DefaultSymbol = new PolySymbol();
		
    }
    
    /**
     * ����������ʾ����
     * @return
     */
    public static v1_SymbolObject CreateMSymbolObject(int W,int H)
    {
		//����������ʽ����ʾΪ��ֵ����
    	v1_SymbolObject SO = new v1_SymbolObject();
		Bitmap bp = Bitmap.createBitmap(W,H,Config.ARGB_8888);
		Canvas g = new Canvas(bp);
		int[] colorList = new int[]{Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW,Color.BLACK};
		Paint paint = new Paint();paint.setStyle(Style.FILL);
		for(int i=0;i<5;i++)
		{
			float subW = (float)W / 5f;
			paint.setAlpha(125);
			paint.setColor(colorList[i]);
			g.drawRect(i*subW, 2, (i+1)*subW, H-2, paint);
		}
		SO.SymbolFigure = bp;
		return SO;
    }
    
	//Ψһֵ�ֶ�
    private List<String> _UniqueValueFieldList = new ArrayList<String>();    //�ȼ�������,....
    public List<String> GetUniqueValueFieldList(){return this._UniqueValueFieldList;}
    public void SetUniqueValueFieldList(List<String> valueList){this._UniqueValueFieldList = valueList;}

    //Ψһֵ
    private List<String> _UniqueValueList= new ArrayList<String>();     //һ��,G201|����,G201|����,G301|....
    public List<String> GetUniqueValueList() {return this._UniqueValueList; }
    public void SetUniqueValueList(List<String> value) {this._UniqueValueList = value;}

    //Ψһֵ�������б�
    private List<ISymbol> _SymbolList = new ArrayList<ISymbol>();

    public List<ISymbol> GetUniqueSymbolList(){return this._SymbolList;}
    /**
     * ����Ψһֵ����,Base641&Base642&Base643...
     */
    public void SetUniqueSymbolList(List<String> base64List)
    {
    	this._SymbolList.clear();
    	for(String sym:base64List)
    	{
    		 this._SymbolList.add(SymbolConvertTools.StrToSymbol(sym, this._GeoLayer.getType()));
    	}
    	this.SetSymbolTransparent(this._SymbolTransparent);
    }
    
    //ȱʡ����
    private ISymbol _DefaultSymbol = null;
    public void SetDefaultSymbol(String symbolValue)
    {
    	this._DefaultSymbol = SymbolConvertTools.StrToSymbol(symbolValue, this._GeoLayer.getType());
    }
    public ISymbol getDefaultSymbol()
    {
        return this._DefaultSymbol;
    }
    
    //����͸���ȣ�Ŀǰֻ�������ţ�
    private int _SymbolTransparent = 0;
    public void SetSymbolTransparent(int transparent)
    {
    	this._SymbolTransparent = transparent;
		if (this._GeoLayer.getType()==lkGeoLayerType.enPolygon)
		{
			for(ISymbol pSymbol:this._SymbolList)
			{
				if (pSymbol!=null)((PolySymbol)pSymbol).SetTransparent(this._SymbolTransparent);
			}
			if (this._DefaultSymbol!=null)((PolySymbol)this._DefaultSymbol).SetTransparent(this._SymbolTransparent);
		}
    }

    //����ͼ��ʵ��ķ���
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
        	for(ISymbol pSymbol : this._SymbolList)pSymbol.setTextSymbol(TS);
		}
    }
	
    //ֻ����ָ������ʵ��ķ���
	@Override
	public void UpdateSymbol(Geometry pGeometry)
	{
		if (pGeometry==null) return;
        if(this._UniqueValueList==null)
        {
        	pGeometry.setSymbol(this.getDefaultSymbol());
        } 
        else
        {
            for (int idx = 0;idx<this._UniqueValueList.size();idx++)
            {
                if (this._UniqueValueList.get(idx).equals(pGeometry.getTagForUniqueSymbol()))
                { 
                	pGeometry.setSymbol(this._SymbolList.get(idx)); return; 
                }
            }
            pGeometry.setSymbol(this.getDefaultSymbol());
        }
    }
	
	/**
	 * ��������ʵ���Ψһֵ
	 */
	public void UpdateAllUniqueValue()
	{
		//�ڴ˼�Ψһֵ��Ⱦֵ
		String SQL = "select %1$s from "+this._GeoLayer.getDataset().getDataTableName()+" where SYS_ID in (%2$s) and SYS_STATUS='0'";
		String SelectField = "SYS_ID";
		if (this.GetUniqueValueFieldList().size()==0) return;
		SelectField += ",(" + Tools.JoinT("||','||", this.GetUniqueValueFieldList()) + ") as UniqueValueField";
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
        	pGeometry.setTagForUniqueSymbol(DR.GetString("UniqueValueField"));
        }DR.Close();
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
