package lkmap.Render;

import lkmap.Cargeometry.Geometry;
import lkmap.Enum.lkRenderType;

public abstract class IRender 
{
	//��Ⱦ����
    private lkRenderType _Type = lkRenderType.enSimple;
    public lkRenderType getType()
    {
         return _Type;
    }
    public void setType(lkRenderType value)
    {
    	_Type = value;
    }

    //ͼ���Ƿ��ע
    private boolean _IfLabel = false;
    public boolean getIfLabel()
    {
        return _IfLabel;
    }
    public void setIfLabel(boolean value)
    {
    	_IfLabel = value; 
    }

    //ͼ���ע�ֶ�
    private String _LabelField = "";
    public String getLabelField()
    {
        return _LabelField;
    }
    public void setLabelField(String value)
    {
    	 _LabelField = value;
    }

    //ͼ���ע������ʽ
    private String _LabelStyleName = "NULL";  //��ɫ����С
    public String getLabelFont()
    {
        return _LabelStyleName;
    }
    public void setLabelFont(String value)
    {
    	_LabelStyleName = value;
    }

    //ͼ���ע��С��ʾ����
    private double _LabelScaleMin = 0;
    public double getLabelScaleMin()
    {
         return _LabelScaleMin; 
    }
    public void setLabelScaleMin(double value)
    {
    	_LabelScaleMin = value; 
    }

    //ͼ���ע�����ʾ����
    private double _LabelScaleMax = Double.MAX_VALUE;
    public double getLabelScaleMax()
    {
         return _LabelScaleMax; 
    }
    public void setLabelScaleMax(double value)
    {
    	_LabelScaleMax = value;
    }

//    public void RenderLabel(LKMap.Layers.GeoLayer _GeoLayer, List<int> ObjectIDList)
//    {
        ////���·��ŵı�ע��Ϣ
        //string[] FieldList = this.LabelField.Split(',');
        //if (this.IfLabel)
        //{
        //    string SQL = "select [SYS_ID]," + this.LabelField + " from " + _GeoLayer.Dataset.TableName;
        //    if (ObjectIDList != null)
        //    {
        //        string IDList = "";
        //        foreach (int oid in ObjectIDList)
        //        {
        //            IDList += oid.ToString() + "\r\n";
        //        }
        //        //System.IO.File.(_GeoLayer.Map.TempDataFileFullName, IDList);
        //        string where = "SYS_ID in (SELECT F1 FROM [Text;FMT=Delimited;HDR=No;DATABASE=" + _GeoLayer.Map.TempDataFilePath + "]." + _GeoLayer.Map.TempDataFileName + ")";
        //        SQL += " where " + where;
        //    }
                
        //    SQL+=" order by SYS_ID";
        //    using (System.Data.DataSet pDataSet = _GeoLayer.Dataset.DataSource.Query(SQL))
        //    {
        //        LKMap.CartoGeometry.IGeometry pGeometry = null;
        //        foreach (System.Data.DataRow pDataRow in pDataSet.Tables[0].Rows)
        //        {
        //            pGeometry = _GeoLayer.Dataset.GetGeometry(((int)pDataRow["SYS_ID"]-1));
        //            string LabelText = "";
        //            foreach (string FL in FieldList)
        //            {
        //                LabelText += pDataRow[FL].ToString() + ",";
        //            }
        //            LabelText = LabelText.Substring(0, LabelText.Length - 1);
        //            pGeometry.Tag = LabelText;
        //        }
        //    }
        //}
//    }
//    protected void RenderLabel(LKMap.Layers.GeoLayer _GeoLayer)
//    {
        ////���·��ŵı�ע��Ϣ
        //string[] FieldList = this.LabelField.Split(',');
        //if (this.IfLabel)
        //{
        //    string SQL = "select [SYS_ID]," + this.LabelField + " from " + _GeoLayer.Dataset.TableName + " order by SYS_ID";
        //    using (System.Data.DataSet pDataSet = _GeoLayer.Dataset.DataSource.Query(SQL))
        //    {
        //        int idx = 0; LKMap.CartoGeometry.IGeometry pGeometry = null;
        //        foreach (System.Data.DataRow pDataRow in pDataSet.Tables[0].Rows)
        //        {
        //            pGeometry = _GeoLayer.Dataset.GetGeometry(idx);
        //            string LabelText = "";
        //            foreach (string FL in FieldList)
        //            {
        //                LabelText += pDataRow[FL].ToString() + ",";
        //            }
        //            LabelText = LabelText.Substring(0, LabelText.Length - 1);
        //            pGeometry.Tag = LabelText;
        //            idx++;
        //        }
        //    }
        //}
//    }

    public abstract void UpdateSymbol(Geometry pGeometry);
    public abstract void UpdateAllLabel();
    public abstract void UpdateSymbolSet();
}
