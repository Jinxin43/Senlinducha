package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.R.string;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import dingtu.ZRoadMap.PubVar;
import lkmap.Enum.ForestryLayerType;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkRenderType;
import lkmap.Render.UniqueValueRender;
import lkmap.Symbol.LineSymbol;
import lkmap.Symbol.PointSymbol;
import lkmap.Symbol.PolySymbol;
import lkmap.Tools.Tools;

public class v1_Layer 
{
	public v1_Layer()
	{
		this.SetRenderType(lkRenderType.enSimple);
	}
	
	public String Tag = "";
	
	//�༭ģʽ
	private lkEditMode _EditMode = lkEditMode.enUnkonw;
	
	/**
	 * ���ñ༭ģʽ
	 * @param editMode
	 */
	public void SetEditMode(lkEditMode editMode){this._EditMode = editMode;}
	public lkEditMode GetEditMode(){return this._EditMode;}
	
	
	//ͼ������
	private String _LayerAliasName = "";
	public String GetLayerAliasName(){return this._LayerAliasName;}
	public void SetLayerAliasName(String layerAliasName){this._LayerAliasName=layerAliasName;}
	
	//ͼ��IDֵ
	private String _LayerID = "T"+(UUID.randomUUID().toString()).replace("-", "").toUpperCase();
	public String GetLayerID(){return this._LayerID;}
	public void SetLayerID(String lyrID){this._LayerID = lyrID;}
	
	//ȡ�����ݱ��Լ�����������
	public String GetDataTableName(){return this._LayerID+"_D";}
	public String GetIndexTableName(){return this._LayerID+"_I";}

	//ͼ������
	private lkGeoLayerType _LayerType = lkGeoLayerType.enUnknow;
	public lkGeoLayerType GetLayerType(){return this._LayerType;}
	public void SetLayerType(lkGeoLayerType layerType){this._LayerType = layerType; }
	
	public String GetLayerTypeName()
	{
		if (this._LayerType == lkGeoLayerType.enPoint) return "��";
		if (this._LayerType == lkGeoLayerType.enPolyline) return "��";
		if (this._LayerType == lkGeoLayerType.enPolygon) return "��";
		return "";
	}
	public void SetLayerTypeName(String _layerType)
	{
		if (_layerType.equals("��"))this._LayerType = lkGeoLayerType.enPoint;
		if (_layerType.equals("��"))this._LayerType = lkGeoLayerType.enPolyline;
		if (_layerType.equals("��"))this._LayerType = lkGeoLayerType.enPolygon;
	}
	
	//��ҵ��������
	private String layerProjectType = "";
	@SuppressLint("NewApi")
	public String GetLayerProjecType()
	{
		if(layerProjectType == null)
		{
			return "�Զ��幤��";
		}
		
		return layerProjectType;
	}
	
	public void SetLayerProjectType(String layproType)
	{
		layerProjectType=layproType;
	}
	
	String tuigengCity ="";
	public void setCity(String city)
	{
		tuigengCity = city;
	}
	
	public String getCity()
	{
		return tuigengCity;
	}
	
	String tuigengCounty ="";
	public void setCounty(String county)
	{
		tuigengCounty = county;
	}
	
	public String getCounty()
	{
		return tuigengCounty;
	}
	
	String tuigengYear ="";
	public void setYear(String year)
	{
		tuigengYear = year;
	}
	public String getYear()
	{
		return tuigengYear;
	}
	
	String weipiandataLayer = "";
	public String getWeiPianDataLayer()
	{
		return weipiandataLayer;
	}
	public void setWeipianDataLayer(String dataLayer)
	{
		weipiandataLayer = dataLayer;
	}
	
	//ͼ��ɼ���
	private boolean _Visible = true;
	public boolean GetVisible(){return this._Visible;}
	public void SetVisible(boolean visible){this._Visible=visible;}
	
	//��С�ɼ�����
	private double _VisibleScaleMin = 0;
	public void SetVisibleScaleMin(double min){this._VisibleScaleMin=min;}
	public double GetVisibleScaleMin(){return this._VisibleScaleMin;}
	
	//���ɼ�����
	private double _VisibleScaleMax = Integer.MAX_VALUE;
	public void SetVisibleScaleMax(double max){this._VisibleScaleMax=max;}
	public double GetVisibleScaleMax(){return this._VisibleScaleMax;}
	
	//����͸���ȣ�Ŀǰֻ�������ţ�
	private int _SymbolTransparent = 0;  //0��͸��
	public void SetTransparent(int transparent){this._SymbolTransparent=transparent;}
	public int GetTransparet(){return this._SymbolTransparent;}
	
	//�Ƿ��ע
	private boolean _IfLabel = false;
	public void SetIfLabel(boolean ifLabel){this._IfLabel = ifLabel;}
	public boolean GetIfLabel(){return this._IfLabel;}
	
	private boolean showWaterMark = false;
	public void SetShowWaterMark(boolean showWaterMark)
	{
		this.showWaterMark = showWaterMark;
	}
	public boolean GetShowWaterMark()
	{
		return this.showWaterMark;
	}
	private List<String> waterMarkFieldList = new ArrayList<String>();
	private List<String> waterMarkDataFieldList = new ArrayList<String>();
	public String GetWaterMarkFieldStr ()
	{
		return Tools.JoinT(",", this.waterMarkFieldList);
	}
	public String GetWaterMarkDataFieldStr(){
		return Tools.JoinT(",", this.waterMarkDataFieldList);
	}
	
	
	
	//��ע�ֶ�
	private List<String> _LabelFieldList = new ArrayList<String>();
	private List<String> _LabelDataFieldList = new ArrayList<String>();
	public String GetLabelFieldStr(){return Tools.JoinT(",",this._LabelFieldList);}
	public String GetLabelDataFieldStr(){return Tools.JoinT(",",this._LabelDataFieldList);}
	
	public void setWaterMarkDataFields(String waterMarkField)
	{
		this.waterMarkFieldList.clear();
		this.waterMarkDataFieldList.clear();
		
		if(waterMarkField == null)
		{
			return;
		}
		String[] fieldList = waterMarkField.split(",");
		for(String field:fieldList)
		{
			for(v1_LayerField LF :this.GetFieldList())
			{
				if (LF.GetDataFieldName().equals(field))
				{
					this.waterMarkFieldList.add(LF.GetFieldName());
					this.waterMarkDataFieldList.add(LF.GetDataFieldName());
				}
			}
		}
	}
	
	
	/**
	 * ���ñ�ע�ֶΣ���ʽ���ֶ�1,�ֶ�2,.....�����ŷָ�
	 * @param labelField
	 */
	public void SetLabelDataField(String labelField)
	{
		this._LabelFieldList.clear();
		this._LabelDataFieldList.clear();
		
		String[] fieldList = labelField.split(",");
		for(String field:fieldList)
		{
			for(v1_LayerField LF :this.GetFieldList())
			{
				if (LF.GetDataFieldName().equals(field))
				{
					this._LabelFieldList.add(LF.GetFieldName());
					this._LabelDataFieldList.add(LF.GetDataFieldName());
				}
			}
		}
	}
	
	//��ע��ʽ����ɫ,��С��
	private String _LabelFont = "#000000,10";  
	
	/**
	 * ��ע���ţ���ʽ����ɫ,��С
	 * @return
	 */
	public String GetLabelFont(){if (this._LabelFont.equals(""))_LabelFont = "#000000,10";return this._LabelFont;}
	public void SetLabelFont(String labelFont){this._LabelFont=labelFont;}
	
	//ͼ���ע�������С��Χ
	private double _LabelScaleMin=0,_LabelScaleMax=Integer.MAX_VALUE;
	public void SetLabelScaleMin(double min){this._LabelScaleMin = min;}
	public double GetLabelScaleMin(){return this._LabelScaleMin;}
	public void SetLabelScaleMax(double max){this._LabelScaleMax = max;}
	public double GetLabelScaleMax(){return this._LabelScaleMax;}
	
	//ͼ��������С��Χ
	private double _MinX=0,_MinY=0,_MaxX=0,_MaxY=0;
	public void SetMinX(double minx){this._MinX = minx;}
	public double GetMinX(){return this._MinX;}
	public void SetMinY(double miny){this._MinY = miny;}
	public double GetMinY(){return this._MinY;}
	public void SetMaxX(double maxx){this._MaxX = maxx;}
	public double GetMaxX(){return this._MaxX;}
	public void SetMaxY(double maxy){this._MaxY = maxy;}
	public double GetMaxY(){return this._MaxY;}
	
	
	//�Ƿ��ѡ��
	private boolean _Selectable = true;
	public boolean GetSelectable(){return this._Selectable;}
	public void SetSelectable(boolean selectable){this._Selectable=selectable;}
	
	//�Ƿ�ɱ༭
	private boolean _Editable = true;
	public boolean GetEditable(){return this._Editable;}
	public void SetEditable(boolean editable){this._Editable=editable;}
	
	//�Ƿ�ɲ�׽
	private boolean _Snapable = true;
	public boolean GetSnapable(){return this._Snapable;}
	public void SetSnapable(boolean snapable){this._Snapable=snapable;}
	
	
	//�������ͣ�Ĭ��Ϊ������
	private lkRenderType _RenderType = lkRenderType.enSimple;
	public void SetRenderType(lkRenderType renderType)
	{
		this._RenderType = renderType;
		if (this._UniqueSymbolInfoList==null)
		{
			this._UniqueSymbolInfoList = new HashMap<String,Object>();
			this._UniqueSymbolInfoList.put("UniqueValueField", new ArrayList<String>());
			this._UniqueSymbolInfoList.put("UniqueValueList", new ArrayList<String>());
			this._UniqueSymbolInfoList.put("UniqueSymbolList", new ArrayList<String>());
			this._UniqueSymbolInfoList.put("UniqueDefaultSymbol", "");
		}
	}
	public void SetRenderTypeInt(int renderTypeInt)
	{
		if (renderTypeInt==1)this.SetRenderType(lkRenderType.enSimple);
		if (renderTypeInt==2)this.SetRenderType(lkRenderType.enUniqueValue);
	}
	public lkRenderType GetRenderType(){return this._RenderType;}
	public int GetRenderTypeInt()
	{
		if (this._RenderType==lkRenderType.enSimple) return 1;
		if (this._RenderType==lkRenderType.enUniqueValue)return 2;
		return 0;
	}
	
	//Ψһֵ������Ϣ
	private HashMap<String,Object> _UniqueSymbolInfoList = null;
	public HashMap<String,Object> GetUniqueSymbolInfoList(){return this._UniqueSymbolInfoList;}
	
	
	
	//��ֵ����
	private String _SimpleSymbol = "";  //ע��˴�Ӧ��Ϊ����ʵ������
	public String GetSimpleSymbol()
	{
		if (!this._SimpleSymbol.equals(""))return _SimpleSymbol;
		if (this._LayerType == lkGeoLayerType.enPoint) {this._SimpleSymbol = (new PointSymbol()).ToBase64();}
		if (this._LayerType == lkGeoLayerType.enPolyline) {this._SimpleSymbol = (new LineSymbol()).ToBase64();}
		if (this._LayerType == lkGeoLayerType.enPolygon) 
		{
			this._SimpleSymbol = (new PolySymbol()).ToBase64();
		}
		return this._SimpleSymbol;
	}
	public void SetSimpleSymbol(String Sym)
	{
		this._SimpleSymbol=Sym;
	}
	
	
	//����ָʾͼ
	public Bitmap GetSymbolFigure()
	{
		//ͼ�����ָʾͼ
    	if(this.GetRenderType()==lkRenderType.enSimple)
    		return PubVar.m_DoEvent.m_ConfigDB.GetSymbolExplorer().GetSymbolObject(this._SimpleSymbol, this._LayerType).SymbolFigure;      
    	if (this.GetRenderType()==lkRenderType.enUniqueValue)
    		return UniqueValueRender.CreateMSymbolObject(64, 30).SymbolFigure;
    	return null;
	}
	
	//�ֶ��б�
	private List<v1_LayerField> _FieldList = new ArrayList<v1_LayerField>();
	public List<v1_LayerField> GetFieldList(){return this._FieldList;}
	public String GetFieldListJsonStr()
	{
 	    try 
 	    {
			JSONObject parentJSObject = new JSONObject();
			JSONArray jsArray = new JSONArray();
			for(v1_LayerField lf:this._FieldList)
			{
				JSONObject childJSObject = new JSONObject();
				childJSObject.put("FieldName", lf.GetFieldName());
				childJSObject.put("DataFieldName", lf.GetDataFieldName());
				childJSObject.put("FieldTypeName", lf.GetFieldTypeName());
				childJSObject.put("FieldSize", lf.GetFieldSize());
				childJSObject.put("FieldDecimal", lf.GetFieldDecimal());
				childJSObject.put("FieldEnumCode", lf.GetFieldEnumCode());
				childJSObject.put("FieldEnumEdit", lf.GetFieldEnumEdit());
				childJSObject.put("IsSelect", lf.getIsSelect());
				childJSObject.put("FieldShortName", lf.GetFieldShortName());
				jsArray.put(childJSObject);
			}
			parentJSObject.put("Data", jsArray);
			return parentJSObject.toString();

		} catch (JSONException e) {
			return "";
		}
	}
	public String GetFieldNameByDataFieldName(String dataFieldName)
	{
		for(v1_LayerField lf:this._FieldList)
		{
			if (lf.GetDataFieldName().equals(dataFieldName)) return lf.GetFieldName();
		}
		return "";
	}
	public String GetDataFieldNameByFieldName(String fieldName)
	{
		for(v1_LayerField lf:this._FieldList)
		{
			if (lf.GetFieldName().equals(fieldName))
			{
				return lf.GetDataFieldName();
			}
		}
		return "";
	}
	
	public v1_LayerField GetDataFieldByFieldName(String fieldName)
	{
		for(v1_LayerField lf:this._FieldList)
		{
			if (lf.GetFieldName().equals(fieldName))
			{
				return lf;
			}
		}
		return null;
	}
	
	public void SetFieldList(List<v1_LayerField> fieldList){this._FieldList = fieldList;}
	/**
	 * �����ֶ��б�
	 * @param fieldListJSONStr����ʽ���ֶ�����-�����ֶ�����-����-��С-����-�����ֵ�-�Ƿ���ֶ������ֵ�@��������
	 */
	public void SetFieldList(String fieldListJSONStr)
	{
		this._FieldList.clear();
 	    try 
 	    {
		    JSONTokener jsonParser = new JSONTokener(fieldListJSONStr);  
		    JSONObject jsObj = (JSONObject)jsonParser.nextValue();
		    JSONArray jsArray = jsObj.getJSONArray("Data");
			for(int i=0;i<jsArray.length();i++)
			{
				JSONObject Obj = jsArray.getJSONObject(i);
				v1_LayerField LF = new v1_LayerField();
				LF.SetFieldName(Obj.getString("FieldName"));        		//�ֶκ�������
				LF.SetDataFieldName(Obj.getString("DataFieldName"));		//Ӣ�������ֶ���
				LF.SetFieldTypeName(Obj.getString("FieldTypeName"));		//�ֶ�����
				LF.SetFieldSize(Obj.getInt("FieldSize"));					//�ֶδ�С
				LF.SetFieldDecimal(Obj.getInt("FieldDecimal"));				//�ֶ�С��λ��
				LF.SetFieldEnumCode(Obj.getString("FieldEnumCode"));		//�ֶ�ö��ֵ�б�
				LF.SetFieldEnumEdit(Obj.getBoolean("FieldEnumEdit"));		//ö���Ƿ������
				
				try
				{
					LF.SetIsSelect(Obj.getBoolean("IsSelect"));
				}
				catch(Exception ex)
				{
					LF.SetIsSelect(true);
				}
				try
				{
					LF.SetFieldShortName(Obj.getString("FieldShortName"));
				}
				catch(Exception ex)
				{
					LF.SetFieldShortName("");
				}
				this._FieldList.add(LF);
			}

		} catch (JSONException e) 
		{
			e.printStackTrace();
		}  

	}
	
	/**
	 * ��¡ͼ����
	 * @return
	 */
	public v1_Layer Clone()
	{
		v1_Layer lyr = new v1_Layer();
		this.CopyTo(lyr);
		return lyr;
	}
	
	/**
	 * ��������
	 * @param vLayer
	 */
	public void CopyTo(v1_Layer vLayer)
	{
		vLayer.SetLayerAliasName(this.GetLayerAliasName());
		vLayer.SetLayerID(this.GetLayerID());
		vLayer.SetLayerTypeName(this.GetLayerTypeName());
		vLayer.SetVisible(this.GetVisible());
		vLayer.SetTransparent(this.GetTransparet());
		vLayer.SetVisibleScaleMin(this.GetVisibleScaleMin());
		vLayer.SetVisibleScaleMax(this.GetVisibleScaleMax());
		vLayer.SetFieldList(this.GetFieldListJsonStr());
		vLayer.SetIfLabel(this.GetIfLabel());
		vLayer.SetLabelDataField(this.GetLabelDataFieldStr());
		vLayer.SetLabelFont(this.GetLabelFont());
		vLayer.SetMinX(this.GetMinX());
		vLayer.SetMinY(this.GetMinY());
		vLayer.SetMaxX(this.GetMaxX());
		vLayer.SetMaxY(this.GetMaxY());
		vLayer.SetSelectable(this.GetSelectable());
		vLayer.SetEditable(this.GetEditable());
		vLayer.SetSelectable(this.GetSelectable());
		vLayer.SetRenderType(this.GetRenderType());
		vLayer.SetSimpleSymbol(this.GetSimpleSymbol());
		
		vLayer.SetShowWaterMark(this.GetShowWaterMark());
		vLayer.setWaterMarkDataFields(this.GetWaterMarkDataFieldStr());
		
		vLayer.SetLayerProjectType(this.GetLayerProjecType());
//		if(vLayer.GetLayerProjecType().contains("�˸�����"))
//		{
			vLayer.setCity(this.getCity());
			vLayer.setCounty(this.getCounty());
			vLayer.setYear(this.getYear());
//		}
		
		if(vLayer.GetLayerProjecType().equals(ForestryLayerType.WeipianJianchaLayer))
		{
			vLayer.setWeipianDataLayer(this.getWeiPianDataLayer());
		}
		
		
		List<String> UVF = (List<String>)this.GetUniqueSymbolInfoList().get("UniqueValueField");
		List<String> UVL = (List<String>)this.GetUniqueSymbolInfoList().get("UniqueValueList");
		List<String> USL = (List<String>)this.GetUniqueSymbolInfoList().get("UniqueSymbolList");
		
		List<String> UVFTo = new ArrayList<String>();for(String v :UVF)UVFTo.add(v);
		List<String> UVLTo = new ArrayList<String>();for(String v :UVL)UVLTo.add(v);
		List<String> USLTo = new ArrayList<String>();for(String v :USL)USLTo.add(v);
		
		vLayer.GetUniqueSymbolInfoList().put("UniqueValueField", UVFTo);
		vLayer.GetUniqueSymbolInfoList().put("UniqueValueList", UVLTo);
		vLayer.GetUniqueSymbolInfoList().put("UniqueSymbolList", USLTo);
		vLayer.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", this.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol"));
	}
	
}
