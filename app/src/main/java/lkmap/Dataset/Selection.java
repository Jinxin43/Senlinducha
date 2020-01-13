package lkmap.Dataset;

import java.util.ArrayList;
import java.util.List;

import lkmap.Cargeometry.Envelope;
import lkmap.Cargeometry.Geometry;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Enum.lkSelectionType;
import lkmap.Symbol.ISymbol;

public class Selection 
{
    //private static final String Dataset = null;
	public Selection() {}

    //������Ա����
    //ѡ���е�ʵ���б�
    private List<Integer> _GeometryIndexList = new ArrayList<Integer>();
    public List<Integer> getGeometryIndexList()
    {
        return _GeometryIndexList; 
    }

    //ѡ�񼯵�����
    public lkSelectionType _Type = lkSelectionType.enUnKnow;
    public lkSelectionType getType()
    {
        return _Type;
    }
    public void setType(lkSelectionType value)
    {
    	_Type = value;
    }

    //ѡ����ʵ��ĸ���
    public int getCount()
    {
    	int Count = 0;
    	//�˴��������Ѿ�ɾ����ʵ�壬������Ҫ���¹���
    	for(int SYS_ID:this._GeometryIndexList)
    	{
    		Geometry pGeometry = this._Dataset.GetGeometry(SYS_ID);
    		if (pGeometry==null){Count++;continue;}
    		if (pGeometry.getStatus()==lkGeometryStatus.enNormal)Count++;
    	}
        return Count;
    }

    //ѡ���ʵ����ʽ
    private ISymbol _Style = null;
    public ISymbol getStyle()
    {
         return _Style; 
    }
    public void setStyle(ISymbol value)
    {
    	_Style = value; 
    }

    //ѡ�����������ݼ�
    private Dataset _Dataset = null;
    public Dataset getDataset()
    {
        return _Dataset;   
    }
    
    public void setDataset(Dataset value)
    {
    	this._Dataset=value;
    	////�������ݼ�������ȷ��ѡ�񼯵ķ�����ʽ
        //if (_Dataset.Type == LKMap.Enum.lkGeoLayerType.enPoint) 
        //    this.Style = Stylelib.GetDefaultSymbol(lkSymbolType.enPointSymbol);

        //if (_Dataset.Type == LKMap.Enum.lkGeoLayerType.enPolyline)
        //    this.Style = Stylelib.GetDefaultSymbol(lkSymbolType.enLineSymbol);

        //if (_Dataset.Type == LKMap.Enum.lkGeoLayerType.enPolygon)
        //    this.Style = Stylelib.GetDefaultSymbol(lkSymbolType.enPolygonSymbol);

    }

    //ѡ�񼯵������Ӿ���
    public Envelope getEnvelope()
    {
        if (this.getGeometryIndexList().size() == 0) return null;
        Envelope pEnvelope = this._Dataset.GetGeometry(this.getGeometryIndexList().get(0)).getEnvelope();
        for (int i = 1; i < this.getGeometryIndexList().size(); i++)
        {
            pEnvelope = pEnvelope.Merge(this._Dataset.GetGeometry(this.getGeometryIndexList().get(i)).getEnvelope());
        }
        return pEnvelope;
    }

    //��ѡ����������ʵ��
    public boolean Add(Geometry newGeometry)
    {
        return this.Add(newGeometry.getSysId());
    }
    //��ѡ����������ʵ��
    public boolean Add(int GeometryIndex)
    {
        //�ж��Ƿ��Ѿ����б��У��������������
        if (this.InList(GeometryIndex)) return true;
        this.getGeometryIndexList().add(GeometryIndex);
        return true;
    }
    
    /**
     * ָ����ʵ�������Ƿ����б��ڲ�
     * @param GeometryIndex
     * @return
     */
    public boolean InList(int GeometryIndex)
    {
    	if (this.getGeometryIndexList().indexOf(GeometryIndex) >= 0) return true; else return false;
    }
    

    //��ѡ����ɾ��ָ��ObjectID��ʵ��
    public boolean Remove(int GeometryIndex) 
    {
    	return this.getGeometryIndexList().remove((Integer)GeometryIndex);
    }

    //��ѡ����ɾ��ָ����ʵ��
    public boolean Remove(Geometry pGeometry) 
    {
    	return Remove(pGeometry.getSysId());
    }

    //��ѡ����ɾ������ʵ��
    public boolean RemoveAll() 
    {
    	_GeometryIndexList.clear(); return true;
    }

    //��ѡ��ת����Recordset
//    public Recordset ToRecordset() 
//    {
//        string SQL = "select * from " + this.Dataset.TableName + " where [SYS_ID] in (";
//        foreach(int ID in this.GeometryIndexList)
//        {
//            SQL+=(ID+1).ToString()+",";
//        }
//        SQL = SQL.Substring(0,SQL.Length-1)+")";
//        return null;// new Recordset(this.Dataset, this.Dataset.DataSource.Query(SQL));
//    }

    //��Recordsetת����Selection
    public boolean FromRecordset() { return true; }

}
