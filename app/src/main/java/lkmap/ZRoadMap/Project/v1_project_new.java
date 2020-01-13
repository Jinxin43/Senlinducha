package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.DataBindOfKeyValue;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_BaseDataObject;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Dataset.DataSource;
import lkmap.Enum.lkDataSourceType;
import lkmap.Enum.lkEditMode;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_ConfigDB;
import lkmap.ZRoadMap.Config.v1_UserConfigDB_MyCoordinateSystem;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import lkmap.ZRoadMap.Transformation.v1_transformation_calparam;
import lkmap.ZRoadMap.Transformation.v1_transformation_paramanage;

public class v1_project_new
{
	private v1_BaseDataObject _BaseObject = null;
	private v1_FormTemplate _Dialog = null; 
	private String fendai = "3";

	
    public v1_project_new()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_new);
    	//_Dialog.ReSetSize(PubVar.m_WindowScaleW,-1f);
    	//设置标题
    	_Dialog.SetCaption(Tools.ToLocale("新建工程"));
    	
    	//设置默认按钮
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("创建")+" ,创建工程", _Callback);

    	//我的坐标系按钮
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_mycoordinatesystem+","+Tools.ToLocale("我的坐标系")+" ,我的坐标系", _Callback);
    	
    	//中央经线
    	v1_SpinnerDialog sd =(v1_SpinnerDialog)_Dialog.findViewById(R.id.sp_centerjx);
    	sd.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				v1_project_centerjx pc = new v1_project_centerjx();
				pc.SetCallback(_Callback);
				pc.ShowDialog();
			}});
    	
    	//图层模板
    	v1_SpinnerDialog vsd = (v1_SpinnerDialog)_Dialog.findViewById(R.id.sp_layertemplate);
    	vsd.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				v1_project_new_loadlayertemplate pnl = new v1_project_new_loadlayertemplate();
				pnl.SetDefaultTemplateName(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_layertemplate));
				pnl.SetCallback(_Callback);
				pnl.ShowDialog();
				
			}});
    	
    	
    	//自动获取新创建工程 名称
    	String PrjName = "新建工程";int i=1;
    	while(true)
    	{
    		String PrjPath = PubVar.m_SysAbsolutePath+"/Data/"+PrjName;
    		if (Tools.ExistFile(PrjPath+i+""))i++;
    		else {Tools.SetTextViewValueOnID(_Dialog, R.id.pn_projectname, PrjName+i+"");break;}
    	}
    	
    	//多语言转换
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText2));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText3));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText4));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText5));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText6));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText7));
    	
    	//参数计算器及参数管理器
    	_Dialog.findViewById(R.id.btcalparam3).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {_Callback.OnClick("三参数计算器", "");}});
    	_Dialog.findViewById(R.id.btparamanage3).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {_Callback.OnClick("参数管理器", "三参数");}});
    	_Dialog.findViewById(R.id.btcalparam4).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {_Callback.OnClick("四参数计算器", "");}});
    	_Dialog.findViewById(R.id.btparamanage4).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {_Callback.OnClick("参数管理器", "四参数");}});
    	_Dialog.findViewById(R.id.btcalparam7).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {_Callback.OnClick("七参数计算器", "");}});
    	_Dialog.findViewById(R.id.btparamanage7).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {_Callback.OnClick("参数管理器", "七参数");}});
    }
    
    //新建工程后的回调
    private ICallback _newProjectCallback = null;
    public void SetNewProjectCallback(ICallback cb){this._newProjectCallback = cb;}
    
    private ICallback _Callback = new ICallback(){

		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			
			if (Str.equals("四参数计算器"))
			{
				v1_transformation_calparam tcp = new v1_transformation_calparam();
				tcp.SetCallParamType("计算四参数");
				tcp.SetUseGPS(false);
				tcp.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						HashMap<String,Object> hm = (HashMap<String,Object>)ExtraStr;
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p1,hm.get("DX").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p2,hm.get("DY").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p3,hm.get("R").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p4,hm.get("K").toString());
					}});
				tcp.ShowDialog();
			}
			if (Str.equals("三参数计算器"))
			{
				v1_transformation_calparam tcp = new v1_transformation_calparam();
				tcp.SetCallParamType("计算三参数");
				tcp.SetUseGPS(false);
				tcp.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						HashMap<String,Object> hm = (HashMap<String,Object>)ExtraStr;
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p1,hm.get("DX").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p2,hm.get("DY").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p3,hm.get("DZ").toString());
					}});
				tcp.ShowDialog();
			}
			if (Str.equals("七参数计算器"))
			{
				v1_transformation_calparam tcp = new v1_transformation_calparam();
				tcp.SetCallParamType("计算七参数");
				tcp.SetUseGPS(false);
				tcp.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						HashMap<String,Object> hm = (HashMap<String,Object>)ExtraStr;
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p1,hm.get("P1").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p2,hm.get("P2").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p3,hm.get("P3").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p4,hm.get("P4").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p5,hm.get("P5").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p6,hm.get("P6").toString());
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p7,hm.get("P7").toString());
						
					}});
				tcp.ShowDialog();
			}
			
			
			if (Str.equals("参数管理器"))
			{
				v1_transformation_paramanage tp = new v1_transformation_paramanage();
				tp.SetCallParamType(ExtraStr.toString());
				tp.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						HashMap<String,Object> hm = (HashMap<String,Object>)ExtraStr;
						if (hm.get("Type").equals("三参数"))
						{
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p1,hm.get("P1").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p2,hm.get("P2").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p3,hm.get("P3").toString());
						}
						if (hm.get("Type").equals("四参数"))
						{
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p1,hm.get("P1").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p2,hm.get("P2").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p3,hm.get("P3").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p4,hm.get("P4").toString());
						}
						if (hm.get("Type").equals("七参数"))
						{
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p1,hm.get("P1").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p2,hm.get("P2").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p3,hm.get("P3").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p4,hm.get("P4").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p5,hm.get("P5").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p6,hm.get("P6").toString());
							Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p7,hm.get("P7").toString());
						}
					}});
				tp.ShowDialog();
			}
			
			if (Str.equals("我的坐标系"))
			{
				v1_project_mycoordinatesystem vms = new v1_project_mycoordinatesystem();
				HashMap<String,String> newCoorSystemPara = new HashMap<String,String>();
				newCoorSystemPara.put("CoorSystem", Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_coorsystem));
				newCoorSystemPara.put("CenterJX", Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_centerjx));
				newCoorSystemPara.put("TransMethod", Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_transmethod));
				newCoorSystemPara.put("PMTransMethod", Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_pmtransmethod));

				String TransMethod = newCoorSystemPara.get("TransMethod");
				String PMTransMethod = newCoorSystemPara.get("PMTransMethod");
				if (TransMethod.contains("三参转换"))
				{
					newCoorSystemPara.put("P31", Tools.GetTextValueOnID(_Dialog, R.id.pp3_p1));
					newCoorSystemPara.put("P32", Tools.GetTextValueOnID(_Dialog, R.id.pp3_p2));
					newCoorSystemPara.put("P33", Tools.GetTextValueOnID(_Dialog, R.id.pp3_p3));
					newCoorSystemPara.put("P34", Tools.GetTextValueOnID(_Dialog, R.id.pp3_p4));
					newCoorSystemPara.put("P35", Tools.GetTextValueOnID(_Dialog, R.id.pp3_p5));

				}
				if (PMTransMethod.contains("四参转换"))
				{
					newCoorSystemPara.put("P41", Tools.GetTextValueOnID(_Dialog, R.id.pp4_p1));
					newCoorSystemPara.put("P42", Tools.GetTextValueOnID(_Dialog, R.id.pp4_p2));
					newCoorSystemPara.put("P43", Tools.GetTextValueOnID(_Dialog, R.id.pp4_p3));
					newCoorSystemPara.put("P44", Tools.GetTextValueOnID(_Dialog, R.id.pp4_p4));
				}
				if (TransMethod.contains("七参转换"))
				{
					newCoorSystemPara.put("P71", Tools.GetTextValueOnID(_Dialog, R.id.pp7_p1));
					newCoorSystemPara.put("P72", Tools.GetTextValueOnID(_Dialog, R.id.pp7_p2));
					newCoorSystemPara.put("P73", Tools.GetTextValueOnID(_Dialog, R.id.pp7_p3));
					newCoorSystemPara.put("P74", Tools.GetTextValueOnID(_Dialog, R.id.pp7_p4));
					newCoorSystemPara.put("P75", Tools.GetTextValueOnID(_Dialog, R.id.pp7_p5));
					newCoorSystemPara.put("P76", Tools.GetTextValueOnID(_Dialog, R.id.pp7_p6));
					newCoorSystemPara.put("P77", Tools.GetTextValueOnID(_Dialog, R.id.pp7_p7));
				}
				vms.SetNewCoorSystemPara(newCoorSystemPara);
				vms.SetCallback(_Callback);
				vms.ShowDialog();
			}
			
			if (Str.equals("设置坐标系"))
			{
				HashMap<String,String> newCoorPara = (HashMap<String,String>)ExtraStr;
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_coorsystem, newCoorPara.get("CoorSystem"));
				if (!newCoorPara.get("CoorSystem").equals("WGS-84坐标"))
				{
					v1_DataBind.SetBindListSpinner(_Dialog, "中央经线", new String[]{newCoorPara.get("CenterJX")}, R.id.sp_centerjx);
					Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_transmethod, newCoorPara.get("TransMethod"));
					Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_pmtransmethod, newCoorPara.get("PMTransMethod"));
					String TransMethod = newCoorPara.get("TransMethod");
					String PMTransMethod = newCoorPara.get("PMTransMethod");
					if (TransMethod.contains("三参"))
					{
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p1,newCoorPara.get("P31"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p2,newCoorPara.get("P32"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p3,newCoorPara.get("P33"));
//						Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p4,newCoorPara.get("P4"));
//						Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p5,newCoorPara.get("P5"));
					}
					if (PMTransMethod.contains("四参"))
					{
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p1,newCoorPara.get("P41"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p2,newCoorPara.get("P42"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p3,newCoorPara.get("P43"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp4_p4,newCoorPara.get("P44"));
					}
					if (TransMethod.contains("七参"))
					{
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p1,newCoorPara.get("P71"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p2,newCoorPara.get("P72"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p3,newCoorPara.get("P73"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p4,newCoorPara.get("P74"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p5,newCoorPara.get("P75"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p6,newCoorPara.get("P76"));
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp7_p7,newCoorPara.get("P77"));
					}
				}
				
			}
			
			if (Str.equals("创建工程"))
			{
				//获取工程名称
				EditText te = (EditText)_Dialog.findViewById(R.id.pn_projectname);
				String PrjName = te.getText().toString();
				
				//创建工程，也就是复制相关工程文件
				if (!PubVar.m_DoEvent.m_ProjectDB.CreateProject(PrjName))
				{
					Tools.ShowMessageBox(_Dialog.getContext(), "创建工程失败！");
					return;
				}

				//保存新工程
				_BaseObject.RefreshViewValueToData();
		        _BaseObject.SetSQLiteDatabase(PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase());
				if (_BaseObject.SaveToDB())
				{
					//通过图层模板名称创建图层信息
//					if (!CreateLayerByTemplateName(PrjName,Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_layertemplate)))
//					{
//						Tools.ShowMessageBox(_Dialog.getContext(), "根据图层模板创建图层失败！");
//					}

					//新建后的回调
					if (_newProjectCallback!=null)_newProjectCallback.OnClick("OK", null);
					_Dialog.dismiss();
				}
				else
				{
					Tools.ShowMessageBox(_Dialog.getContext(), "数据保存失败！");
					return;
				}

			}
			
			//中央经线选择后的回调
			if (Str.equals("中央经线"))
			{
		    	List<String> CenterMeridianList = new ArrayList<String>();
		    	HashMap<String, Object> hashMap = (HashMap<String, Object>)ExtraStr;
		    	CenterMeridianList.add(hashMap.get("CenterJX")+"");
		    	v1_DataBind.SetBindListSpinner(_Dialog, "中央经线", CenterMeridianList, R.id.sp_centerjx);
		    	Boolean isHasDH = (Boolean)hashMap.get("isHasDH");
		    	if(isHasDH)
		    	{
		    		Tools.SetTextViewValueOnID(_Dialog, R.id.etFenDaihao, hashMap.get("DH")+"");
		    	}
		    	
		    	Tools.SetTextViewValueOnID(_Dialog, R.id.etFenDai, hashMap.get("Fendai")+"");
		    	
			}
			
			
			//图层模板
			if (Str.equals("模板列表"))
			{
//		    	List<String> templateList = new ArrayList<String>();
//		    	templateList.add(ExtraStr.toString());
//		    	v1_DataBind.SetBindListSpinner(_Dialog, "图层模板", templateList, R.id.sp_layertemplate);
			}
		}};
    
	//通过图层模板名称创建图层信息
	private boolean CreateLayerByTemplateName(String PrjName,String templateName)
	{
		boolean OK = true;
		//根据图层模板创建图层信息
		if (templateName.equals("无")) return true;
		List<v1_Layer> vLayerList = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().ReadLayerTemplate(templateName);  //GetLayerTemplate()中有创建模板表部分
		if (vLayerList==null) return true;
		
		DataSource pDataSource = new DataSource(PubVar.m_SysAbsolutePath+"/Data/"+PrjName+"/TAData.dbx");
		for(v1_Layer lyr:vLayerList)
		{
			//默认透明度设为50%
			lyr.SetTransparent(125);
			String FieldNameList = "Name,LayerId,Type,Visible,Transparent,IfLabel,LabelField,LabelFont,LabelScaleMin,LabelScaleMax,MinX,MinY,MaxX,MaxY,"+
								   "FieldList,VisibleScaleMin,VisibleScaleMax,Selectable,Editable,Snapable,RenderType,SimpleRender,F1,"+
								   "UniqueValueField,UniqueValueList,UniqueSymbolList,UniqueDefaultSymbol";
			String[] FeildValueList = {lyr.GetLayerAliasName(),lyr.GetLayerID(),lyr.GetLayerTypeName(),lyr.GetVisible()+"",lyr.GetTransparet()+"",lyr.GetIfLabel()+"",
									  lyr.GetLabelFieldStr(),lyr.GetLabelFont(),lyr.GetLabelScaleMin()+"",lyr.GetLabelScaleMax()+"",
									  lyr.GetMinX()+"",lyr.GetMinY()+"",lyr.GetMaxX()+"",lyr.GetMaxY()+"",
									  lyr.GetFieldListJsonStr(),lyr.GetVisibleScaleMin()+"",lyr.GetVisibleScaleMax()+"",lyr.GetSelectable()+"",
									  lyr.GetEditable()+"",lyr.GetSnapable()+"",lyr.GetRenderTypeInt()+"",lyr.GetSimpleSymbol(),lyr.GetLayerProjecType(),
									  Tools.ListToJSONStr((List<String>)lyr.GetUniqueSymbolInfoList().get("UniqueValueField")),
									  Tools.ListToJSONStr((List<String>)lyr.GetUniqueSymbolInfoList().get("UniqueValueList")),
									  Tools.ListToJSONStr((List<String>)lyr.GetUniqueSymbolInfoList().get("UniqueSymbolList")),
									  lyr.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol")+""};
			String SQL = "insert into T_Layer (%1$s) values (%2$s)";
			SQL = String.format(SQL,FieldNameList,"'"+Tools.Joins("','",FeildValueList)+"'");
    		
			if (!PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL))OK=false;
    		if (!pDataSource.CreateDataset(lyr.GetLayerID()))OK=false;
		}
		return OK;
	}
	
	
    //加载并绑定列表项
    private void LoadConfigInfo()
    {
    	//加载图层模板信息,检查用户配置中是否存在设置的默认模板名称，如果没有则读取系统默认模板
    	List<String> SysLayerTemplateList = new ArrayList<String>();
    	HashMap<String,String> param = PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().GetUserPara("Tag_SysLayerTemplateName");
    	if (param==null) 
		{
    		//格式：模板名称，模板时间
    		List<String> templateInfo = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().ReadTemplateList("系统");
			SysLayerTemplateList.add(templateInfo.get(0).split("【")[0]);
		}
    	else {SysLayerTemplateList.add(param.get("F2"));}
    	v1_DataBind.SetBindListSpinner(_Dialog, "图层模板", SysLayerTemplateList, R.id.sp_layertemplate);
    	
    	//中央经线
    	List<String> CenterMeridianList = new ArrayList<String>();
    	CenterMeridianList.add("72");
    	v1_DataBind.SetBindListSpinner(_Dialog, "中央经线", CenterMeridianList, R.id.sp_centerjx);
    	
    	//加载坐标系统
    	List<String> CoorSystemList = PubVar.m_DoEvent.m_ConfigDB.ReadConfigItem("坐标系统");
    	v1_DataBind.SetBindListSpinner(_Dialog, "选择坐标系统", CoorSystemList, R.id.sp_coorsystem,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if (Str.equals("OnItemSelected"))
				{
					if (ExtraStr.toString().equals("WGS-84坐标"))
					{
						_Dialog.findViewById(R.id.ll_coorsystempara).setVisibility(View.GONE);
					} else
					{
						_Dialog.findViewById(R.id.ll_coorsystempara).setVisibility(View.VISIBLE);
					}
					
					if (Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_transmethod).indexOf("三参")>=0)
					{
						_Dialog.findViewById(R.id.project_param_3).setVisibility(View.VISIBLE);
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p4,CoorSystem.GetThreePara_DA(ExtraStr.toString())+"");
						Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p5,Tools.ConvertToDigi(CoorSystem.GetThreePara_DF(ExtraStr.toString())+"",7));
					}
				}
				
			}});
    	
    	//椭球转换方法
    	final List<String> CoorSystemTransMethodList = PubVar.m_DoEvent.m_ConfigDB.ReadConfigItem("椭球转换方法");
    	v1_DataBind.SetBindListSpinner(_Dialog, "椭球转换方法", CoorSystemTransMethodList, R.id.sp_transmethod,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {

				String CTM = ExtraStr.toString();
				if (CTM.equals("无")){_Dialog.findViewById(R.id.ll_param).setVisibility(View.GONE);return;}
				
				_Dialog.findViewById(R.id.project_param_3).setVisibility(View.GONE);
				_Dialog.findViewById(R.id.project_param_7).setVisibility(View.GONE);
				_Dialog.findViewById(R.id.ll_param).setVisibility(View.VISIBLE);
				
				if (CTM.indexOf("三参")>=0)
				{
					_Dialog.findViewById(R.id.project_param_3).setVisibility(View.VISIBLE);
					String CoorSystemName = Tools.GetSpinnerValueOnID(_Dialog,R.id.sp_coorsystem);
					Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p4,CoorSystem.GetThreePara_DA(CoorSystemName)+"");
					Tools.SetTextViewValueOnID(_Dialog, R.id.pp3_p5,Tools.ConvertToDigi(CoorSystem.GetThreePara_DF(CoorSystemName)+"",7));
				}
				if (CTM.indexOf("七参")>=0)_Dialog.findViewById(R.id.project_param_7).setVisibility(View.VISIBLE);
				
			}});
    	
    	//平面转换方法
    	List<String> PMTransMethodList = PubVar.m_DoEvent.m_ConfigDB.ReadConfigItem("平面转换方法");
    	v1_DataBind.SetBindListSpinner(_Dialog, "平面转换方法", PMTransMethodList,R.id.sp_pmtransmethod,new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if (ExtraStr.toString().equals("无"))
				{
					_Dialog.findViewById(R.id.ll_pmparam).setVisibility(View.GONE);
				}
				if (ExtraStr.toString().equals("四参转换"))
				{
					_Dialog.findViewById(R.id.ll_pmparam).setVisibility(View.VISIBLE);
				}
				
			}});
    }
    
    public void ShowDialog()
    {
    	this.LoadConfigInfo();
    	_Dialog.show();
    	
    	this.BindDefaultViewAndValue();
    }
    

    //将操作实体与视图控件进行绑定
    private void BindDefaultViewAndValue()
    {
        this._BaseObject = new v1_BaseDataObject();

        this._BaseObject.SetDataTable("T_Project");   //设置关联的表名

        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("ProjectName", _Dialog.findViewById(R.id.pn_projectname)));		//项目名称
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("CreateTime", Tools.GetSystemDate(),null));						//创建时间
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("CoorSystem", _Dialog.findViewById(R.id.sp_coorsystem)));		//坐标系统
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("CenterMeridian", _Dialog.findViewById(R.id.sp_centerjx)));		//中央经线
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("TransMethod", _Dialog.findViewById(R.id.sp_transmethod)));		//椭球转换方法
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("PMTransMethod", _Dialog.findViewById(R.id.sp_pmtransmethod))); //平面转换方法
        
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P31", _Dialog.findViewById(R.id.pp3_p1)));						//三参数-1
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P32", _Dialog.findViewById(R.id.pp3_p2)));						//三参数-2
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P33", _Dialog.findViewById(R.id.pp3_p3)));						//三参数-3

        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P41", _Dialog.findViewById(R.id.pp4_p1)));						//平面转换-四参数-1
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P42", _Dialog.findViewById(R.id.pp4_p2)));						//平面转换-四参数-2
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P43", _Dialog.findViewById(R.id.pp4_p3)));						//平面转换-四参数-3
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P44", _Dialog.findViewById(R.id.pp4_p4)));						//平面转换-四参数-4
        
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P71", _Dialog.findViewById(R.id.pp7_p1)));						//七参数-1
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P72", _Dialog.findViewById(R.id.pp7_p2)));						//七参数-2
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P73", _Dialog.findViewById(R.id.pp7_p3)));						//七参数-3
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P74", _Dialog.findViewById(R.id.pp7_p4)));						//七参数-4
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P75", _Dialog.findViewById(R.id.pp7_p5)));						//七参数-5
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P76", _Dialog.findViewById(R.id.pp7_p6)));						//七参数-6
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("P77", _Dialog.findViewById(R.id.pp7_p7)));						//七参数-7
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("F2", _Dialog.findViewById(R.id.etFenDaihao)));	
        this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("F3", _Dialog.findViewById(R.id.etFenDai)));	
    }
}
