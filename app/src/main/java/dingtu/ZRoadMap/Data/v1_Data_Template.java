package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.Project_Web;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkFieldType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;
import lkmap.ZRoadMap.Project.v1_project_layer;

public class v1_Data_Template
{

	private DataBindAlertDialog _DataDialog = null; 
    public v1_Data_Template()
    {
    	_DataDialog = new DataBindAlertDialog(PubVar.m_DoEvent.m_Context);
    	_DataDialog.SetOtherView(R.layout.v1_data_template);
    	_DataDialog.SetCaption(Tools.ToLocale("属性"));
    	_DataDialog.ReSetSize(0.4f,0);
    	//_DataDialog.ReSetSize(0.8f, 0);
    	_DataDialog.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("保存"))
				{
					lkmap.Tools.Tools.OpenDialog("正在保存数据...",new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							Save();
						}});
				}
			}});
    	
    	Tools.ToLocale(_DataDialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_DataDialog.findViewById(R.id.tvLocaleText2));
    	_DataDialog.findViewById(R.id.ll_status).setVisibility(View.GONE);

    }
    
    private ICallback _returnCallback = null;
    public void SetCallback(ICallback cb){this._returnCallback=cb;}
    
    //根据是否采集平均点选择，生成界面
    private v1_CGps_AveragePoint _MyAveragePoint = null;
    private void SetShowGpsAveragePointOption()
    {
    	if (this._CalAveragePoint)
    	{
    		if (this._Layer==null)return;
        	_DataDialog.findViewById(R.id.ll_status).setVisibility(View.VISIBLE);
        	_DataDialog.findViewById(R.id.bt_restart).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_DataDialog.findViewById(R.id.sp_pointcount);
					_MyAveragePoint.Start(Integer.parseInt(esd1.getText()));
				}});
        	
        	//初始化控件
        	v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_DataDialog.findViewById(R.id.sp_pointcount);
        	esd1.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
        	esd1.SetSelectItemList(Tools.StrArrayToList(new String[]{"3","4","5","10","20","30"}));
        	esd1.getEditTextView().setEnabled(true);
        	
        	//默认值
        	int gpsPointCount = 1;
        	if (this._Layer.GetLayerType()==lkGeoLayerType.enPoint)
        	{
        		String PointCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount").Value+"";
        		esd1.setText(PointCount);
        		if (Tools.IsInteger(PointCount))gpsPointCount = Integer.parseInt(PointCount);
        	} else
        	{
        		String VertexCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_VertexCount").Value+"";
        		esd1.setText(VertexCount);
        		if (Tools.IsInteger(VertexCount))gpsPointCount = Integer.parseInt(VertexCount);
        	}
        	this._MyAveragePoint = new v1_CGps_AveragePoint();
        	this._MyAveragePoint.Start(gpsPointCount);
        	this._MyAveragePoint.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
					if (Str.equals("采集状态"))
					{
						Tools.SetTextViewValueOnID(_DataDialog, R.id.et_gpspointcount, (ExtraStr+"").split(",")[0]+"  ");
					}
				}});
         
        	
    	}
    }
    
    

    
    //是否需要采集平均点
    private boolean _CalAveragePoint = false;
    /**
     * 是否需要采集平均点
     * @param _calAveragePoint
     */
    public void SetCalAveragePoint(boolean _calAveragePoint)
    {
    	this._CalAveragePoint = _calAveragePoint;
    	this.SetShowGpsAveragePointOption();
    }
    
    /**
     * 根据图层动态创建表单信息
     * @param vLayer
     */
    private void CreateForm(v1_Layer vLayer)
    {
    	
    	if (vLayer==null) return;
    	
    	//计算标签文本的最大长度，为对齐做准备
    	int LabelMaxLen = 0;
    	for(v1_LayerField LF:vLayer.GetFieldList())
    	{
    		if (Tools.CalStrLength(Tools.ToLocale(LF.GetFieldName()))>LabelMaxLen) LabelMaxLen = Tools.CalStrLength(Tools.ToLocale(LF.GetFieldName()));
    	}
    	
    	//根据图层的字段配置信息，动态生成属性表单
    	LinearLayout LL = (LinearLayout)_DataDialog.findViewById(R.id.baselist);
    	for(v1_LayerField LF:vLayer.GetFieldList())
    	{
    		//创建标签
    		LinearLayout SubLL = this.CreateFormRowHeader(LL, Tools.ToLocale(LF.GetFieldName()), LabelMaxLen);
    		
    		//数据容器
    		if (LF.GetFieldType()==lkFieldType.enString)  //文本
    		{
    			if (LF.GetFieldEnumCode().equals(""))
    			{
		    		EditText ET = new EditText(LL.getContext());
		    		LayoutParams LPET = new LayoutParams(ET.getWidth(),ET.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		ET.setLayoutParams(LPET);
		    		SubLL.addView(ET);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
    			} else
    			{
    				v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext());
		    		LayoutParams LPET = new LayoutParams(es.getWidth(),es.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		es.setLayoutParams(LPET);
		    		es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(),LF.GetFieldEnumCode()));
		    		es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
		    		SubLL.addView(es);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),es));
    			}
    		}
    		
       		if (LF.GetFieldType()==lkFieldType.enFloat)   //浮点数字
    		{
       			if (LF.GetFieldEnumCode().equals(""))
       			{
		    		EditText ET = new EditText(LL.getContext());
		    		ET.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		    		LayoutParams LPET = new LayoutParams(ET.getWidth(),ET.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		ET.setLayoutParams(LPET);
		    		SubLL.addView(ET);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
       			} else
       			{
    				v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext());
		    		LayoutParams LPET = new LayoutParams(es.getWidth(),es.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		es.setLayoutParams(LPET);
		    		es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		    		es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(),LF.GetFieldEnumCode()));
		    		es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
		    		SubLL.addView(es);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),es));
       			}
    		}
       		if (LF.GetFieldType()==lkFieldType.enInt)   //整数
    		{
       			if (LF.GetFieldEnumCode().equals(""))
       			{
		    		EditText ET = new EditText(LL.getContext());
		    		ET.setInputType(InputType.TYPE_CLASS_NUMBER);
		    		LayoutParams LPET = new LayoutParams(ET.getWidth(),ET.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		ET.setLayoutParams(LPET);
		    		SubLL.addView(ET);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
       			} else
       			{
    				v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext());
		    		LayoutParams LPET = new LayoutParams(es.getWidth(),es.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		es.setLayoutParams(LPET);
		    		es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
		    		es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(),LF.GetFieldEnumCode()));
		    		es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
		    		SubLL.addView(es);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),es));
       			}
    		}
       		if (LF.GetFieldType()==lkFieldType.enBoolean)  //布尔型
    		{
	    		Spinner SP = new Spinner(LL.getContext());
	    		v1_DataBind.SetBindListSpinner(_DataDialog.getContext(), "是否", Tools.StrArrayToList(new String[]{"是","否"}), SP);
	    		LayoutParams LPET = new LayoutParams(SP.getWidth(),SP.getHeight());
	    		LPET.width = LayoutParams.FILL_PARENT;
	    		LPET.height = LayoutParams.WRAP_CONTENT;
	    		SP.setLayoutParams(LPET);
	    		SubLL.addView(SP);
	    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),SP));
    		}
       		
       		if (LF.GetFieldType()==lkFieldType.enDateTime)  //日期
       		{
       			final v1_SpinnerDialog SP = new v1_SpinnerDialog(LL.getContext());
       			SP.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_Data_Template_DateTime dtd = new v1_Data_Template_DateTime();
						dtd.SetCallabck(new ICallback(){
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								v1_DataBind.SetBindListSpinner(_DataDialog.getContext(), "日期", Tools.StrArrayToList(new String[]{ExtraStr.toString()}), SP);
							}});
						dtd.ShowDialog();
						
					}});
	    		LayoutParams LPET = new LayoutParams(SP.getWidth(),SP.getHeight());
	    		LPET.width = LayoutParams.FILL_PARENT;
	    		LPET.height = LayoutParams.WRAP_CONTENT;
	    		SP.setLayoutParams(LPET);
	    		SubLL.addView(SP);
	    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),SP));
       		}
    		
    		LL.addView(SubLL);
    	}
    	
    	//在此加入采集数据的状态信息，如点的坐标，线的长度，面的面积等
    	List<String> objInnerFeatureList = new ArrayList<String>();
    	if (vLayer.GetLayerType()==lkGeoLayerType.enPoint)
    	{
    		objInnerFeatureList.add(Tools.ToLocale("坐标"));
    	}
    	if (vLayer.GetLayerType()==lkGeoLayerType.enPolyline)
    	{
    		objInnerFeatureList.add(Tools.ToLocale("长度"));
    	}
    	if (vLayer.GetLayerType()==lkGeoLayerType.enPolygon)
    	{
    		objInnerFeatureList.add(Tools.ToLocale("面积"));
    	}
    	LinearLayout LO = (LinearLayout)_DataDialog.findViewById(R.id.otherlist);
    	for(String FL:objInnerFeatureList)
    	{
    		LinearLayout SubLL = this.CreateFormRowHeader(LO, FL, LabelMaxLen);
    		EditText ET = new EditText(LO.getContext());
    		ET.setEnabled(false);
    		LayoutParams LPET = new LayoutParams(ET.getWidth(),ET.getHeight());
    		LPET.width = LayoutParams.FILL_PARENT;
    		LPET.height = LayoutParams.WRAP_CONTENT;
    		ET.setLayoutParams(LPET);
    		SubLL.addView(ET);
    		LO.addView(SubLL);
    		
    		this._FieldInnerFeauterViewList.add(new FieldView(FL,"",ET));
    	}
		
    }
    
    /**
     * 创建数据行的头文本标签
     * @param LL
     * @param Text
     * @param LabelMaxLen
     * @return
     */
    private LinearLayout CreateFormRowHeader(LinearLayout LL,String Text,int LabelMaxLen)
    {
		LinearLayout SubLL = new LinearLayout(LL.getContext());
		LayoutParams LP = new LayoutParams(SubLL.getWidth(),SubLL.getHeight());
		LP.width = LayoutParams.FILL_PARENT;
		LP.height = LayoutParams.WRAP_CONTENT;
		SubLL.setGravity(Gravity.CENTER);
		SubLL.setLayoutParams(LP);
		
		//标签文本
		TextView TV = new TextView(LL.getContext());
		TV.setText(Tools.PadLeft(Text,LabelMaxLen)+"：");
		TV.setTextColor(Color.BLACK);
		TV.setTextAppearance(LL.getContext(), android.R.attr.textAppearanceMedium);
		SubLL.addView(TV);
		return SubLL;
    }
    
    public void ShowDialog()
    {
    	_DataDialog.show();
    }
    
    //用于实体内部属性的字段视图列表
    private List<FieldView> _FieldInnerFeauterViewList = new ArrayList<FieldView>();
    
    
    //字段名称与字段视图列表，用于保存数据，格式：字段名称,字段视图
    private List<FieldView> _FieldNameViewList = new ArrayList<FieldView>();
    
    /**
     * 自定义保存操作
     */
    private void Save()
    {
    	boolean OK = true;
    	
    	//保存图形数据
    	if (this._CalAveragePoint)
    	{
    		//获取当前GPS点位
			Coordinate newGPSCoor = this._MyAveragePoint.CalGpsPoint();
			if (newGPSCoor==null){Tools.ShowMessageBox(_DataDialog.getContext(), "没有获取有效的GPS点位，请重试！");return;}
        	if (this._BaseObject.GetSYS_ID()==-1)  //对于新增操作，
        	{
    			//分类型存储
    			if (this._Layer.GetLayerType()==lkGeoLayerType.enPoint)
    			{
    				int SYS_ID = PubVar.m_DoEvent.m_GPSPoint.SaveGeoToDb(newGPSCoor, "GPS点位");
    				this._BaseObject.SetSYS_ID(SYS_ID);
    				if (SYS_ID==-1)OK = false;
    			}
        	}
        	
			if (this._Layer.GetLayerType()==lkGeoLayerType.enPolyline)
			{
				PubVar.m_DoEvent.m_GPSLine.AddPoint(newGPSCoor);
				int SYS_ID = PubVar.m_DoEvent.m_GPSLine.SaveGeoToDb(false);
				this._BaseObject.SetSYS_ID(SYS_ID);
				if (SYS_ID==-1)OK = false;
			}
			if (this._Layer.GetLayerType()==lkGeoLayerType.enPolygon)
			{
				PubVar.m_DoEvent.m_GPSPoly.getGPSLine().AddPoint(newGPSCoor);
				int SYS_ID = PubVar.m_DoEvent.m_GPSPoly.getGPSLine().SaveGeoToDb(false);
				this._BaseObject.SetSYS_ID(SYS_ID);
				if (SYS_ID==-1)OK = false;
			}
    	}
    	
    	//保存属性数据
    	this._BaseObject.RefreshViewValueToData();
		if (!this._BaseObject.SaveFeatureToDb())OK=false;
		if (!OK)
		{
			Tools.ShowMessageBox(this._DataDialog.getContext(),"数据保存失败！");
			return;
		}
		this._DataDialog.dismiss();
		if (this._returnCallback!=null) this._returnCallback.OnClick("OK", null);
    }
    
    //实体数据
	private v1_CGpsDataObject _BaseObject = null;
	
    //当前图层
    private v1_Layer _Layer = null;
    
    /**
     * 设置实体编辑信息，如果SYS_ID=-1表示新增，其它为修改
     * @param layerID
     * @param SYS_ID
     */
    public void SetEditInfo(String layerID,int SYS_ID)
    {
    	this._Layer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID);
    	this.CreateForm(this._Layer);
    	this.SetShowGpsAveragePointOption();
    	Dataset pDataset = PubVar.m_Workspace.GetDatasetById(this._Layer.GetLayerID());
    	//表示编辑状态
    	if (SYS_ID!=-1)
    	{
	    	//实体内部属性信息
	    	Geometry pGeometry = pDataset.GetGeometry(SYS_ID);
	    	if (pGeometry==null)
	    	{
	    		List<String> SYSIDList = new ArrayList<String>();
	    		SYSIDList.add(SYS_ID+"");
	    		List<Geometry> pGeometryList = pDataset.QueryGeometryFromDB1(SYSIDList);
	    		if (pGeometryList.size()!=0)pGeometry = pGeometryList.get(0);
	    	}
	    	if (pGeometry!=null)
	    	{
		    	if (this._Layer.GetLayerType()==lkGeoLayerType.enPoint)
		    	{
		    		TextView TV = (TextView)this._FieldInnerFeauterViewList.get(0).FieldView;
		    		Coordinate Coor = ((Point)pGeometry).getCoordinate();
		    		CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
		    		if (CS.GetName().equals("WGS-84坐标"))
		    		{
		    			Coordinate BLCoor = Project_Web.Web_XYToBL(Coor.getX(),Coor.getY());
		    			TV.setText(Tools.ConvertToDigi(BLCoor.getX()+"",6)+","+Tools.ConvertToDigi(BLCoor.getY()+"",6)+","+Tools.ConvertToDigi(Coor.getZ()+"",2));
		    		}else
		    		{
		    			TV.setText(Coor.ToString()+","+Tools.ConvertToDigi(Coor.getZ()+"",2));
		    		}
		    	}
		    	if (this._Layer.GetLayerType()==lkGeoLayerType.enPolyline)
		    	{
		    		TextView TV = (TextView)this._FieldInnerFeauterViewList.get(0).FieldView;
		    		TV.setText(Tools.ReSetDistance(((Polyline)pGeometry).getLength(true),true));
		    	}
		    	if (this._Layer.GetLayerType()==lkGeoLayerType.enPolygon)
		    	{
		    		TextView TV = (TextView)this._FieldInnerFeauterViewList.get(0).FieldView;
		    		TV.setText(Tools.ReSetArea(((Polygon)pGeometry).getArea(true),true));
		    	}
	    	}
    	}
    	
        //初始化实体并设置绑定项目
        this._BaseObject = new v1_CGpsDataObject();
        this._BaseObject.SetDataset(pDataset);
        //if (this._Layer.GetIfLabel()) this._BaseObject.SetLabelFieldName(this._Layer.GetLabelField());	   //设置标注字段名称
       
        this._BaseObject.SetSYS_ID(SYS_ID);   //设置SYS_ID
        
        for(FieldView FV:this._FieldNameViewList)
        {
        	this._BaseObject.AddDataBindItem(new DataBindOfKeyValue(FV.FieldName, FV.DataFieldName,"", FV.FieldView));	
        }

        this._BaseObject.ReadDataAndBindToView("SYS_ID="+SYS_ID);           //读取数据并更新状态
        this._DataDialog.SetGpsBasePointObject(this._BaseObject);
        this._DataDialog.UpdateDialogShowInfo();			 //更新界面显示

    }
    
    private class FieldView
    {
    	public FieldView(String fieldName,String dataFieldName,View view)
    	{
    		this.FieldName = fieldName;
    		this.DataFieldName = dataFieldName;
    		this.FieldView = view;
    	}
    	public String FieldName = "";
    	public String DataFieldName = "";
    	public View FieldView = null;
    }
}
