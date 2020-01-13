package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Dataset.FieldInfo;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.HashMapEx;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class v1_Data_Query_SQL
{
	private v1_FormTemplate _Dialog = null; 
    public v1_Data_Query_SQL()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_query_sql);
    	//_Dialog.ReSetSize(1f,0.96f);
    	_Dialog.ReSetSize(0.8f, 0.9f);
    	_Dialog.SetCaption("查询条件");
    	
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_clearscreen+","+Tools.ToLocale("重置查询条件")+"  ,重置", new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				DeleteAllFilter();
			}});
    	
    	_Dialog.findViewById(R.id.btadd).setOnClickListener(_ClickEvent);
    	_Dialog.findViewById(R.id.btdelete).setOnClickListener(_ClickEvent);
    	_Dialog.findViewById(R.id.btquery).setOnClickListener(_ClickEvent);
    }

    //按钮事件
    private android.view.View.OnClickListener _ClickEvent = new android.view.View.OnClickListener(){
		@Override
		public void onClick(View arg0) 
		{
			if (arg0.getTag().toString().equals("增加条件")){AddFilter();}
			if (arg0.getTag().toString().equals("删除条件")){DeleteFilter();}
			if (arg0.getTag().toString().equals("查询")){SQLQuery();}
		}};

	/**
	 * 增加查询条件
	 */
	private void AddFilter()
	{
		//检查条件是否齐全
		String OperStr = Tools.GetViewValue(_Dialog.findViewById(R.id.sp_operlist));
		String ValueStr = Tools.GetViewValue(_Dialog.findViewById(R.id.spqueryvalue));
		if (this._SelectedFieldItem==null || OperStr.equals("") || ValueStr.equals(""))
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "请填写完整的查询条件！");
			return;
		}
		String FieldStr = this._SelectedFieldItem.get("FieldName")+"";
		String FieldCapStr = this._SelectedFieldItem.get("FieldCaption")+"";
		OperStr = OperStr.split("\\(")[1];OperStr = OperStr.substring(0, OperStr.length()-1);
		
		//条件显示串
		String ShowFilterStr = FieldCapStr+" "+OperStr+" "+ValueStr;
		
		//判断此条件是否已经存在
		if (!this._QueryParam.containsKey("QueryList")){this._QueryParam.put("QueryList", new ArrayList<HashMap<String,Object>>());}
		List<HashMap<String,Object>> queryFilterList = ((ArrayList<HashMap<String,Object>>)this._QueryParam.get("QueryList"));
		for(HashMap<String,Object> hm:queryFilterList)
		{
			if (hm.get("D2").toString().equals(ShowFilterStr))
			{
				Tools.ShowToast(_Dialog.getContext(), "查询条件已经存在，不允许重复！");
				return;
			}
		}
		
		//增加新的查询条件
		HashMap<String,Object> filter = new HashMap<String,Object>();
		filter.put("QueryField", FieldStr);
		filter.put("QueryOper",OperStr);
		filter.put("QueryValue", ValueStr);

		filter.put("D1", true);
		filter.put("D2", ShowFilterStr);
		filter.put("D3", "并且");

		((ArrayList<HashMap<String,Object>>)this._QueryParam.get("QueryList")).add(filter);
		this.BindQueryFilterToList();
		
	}
	
	/**
	 * 删除查询条件
	 */
	private void DeleteFilter()
	{
		if (this._SelectedFilterItem==null)
		{
			Tools.ShowMessageBox(_Dialog.getContext(),"请在列表中选择查询条件！");
			return;
		}
		Tools.ShowYesNoMessage(_Dialog.getContext(), "是否删除以下查询条件？\n条件："+this._SelectedFilterItem.get("D2"), new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				((List<HashMap<String,Object>>)_QueryParam.get("QueryList")).remove(_SelectedFilterItem);
				BindQueryFilterToList();
			}});
	}
	
	/**
	 * 删除所有查询条件
	 */
	private void DeleteAllFilter()
	{
		if (!this._QueryParam.containsKey("QueryList"))return;
		if (((List<HashMap<String,Object>>)_QueryParam.get("QueryList")).size()==0) return;
		Tools.ShowYesNoMessage(_Dialog.getContext(), "是否删除所有的查询条件？", new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				((List<HashMap<String,Object>>)_QueryParam.get("QueryList")).clear();
				BindQueryFilterToList();
			}});
	}
	
	/**
	 * 开始查询
	 */
	private void SQLQuery()
	{
		//SQL_Filter
		//构建查询条件
		if (!this._QueryParam.containsKey("QueryList"))
		{
			Tools.ShowMessageBox(_Dialog.getContext(),"请增加查询条件！");
			return;
		}
		
		lkmap.Tools.Tools.OpenDialog(lkmap.Tools.Tools.ToLocale("正在查询")+"...",new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				GeoLayer pGeoLayer = (GeoLayer)_QueryParam.get("GeoLayer");
				List<String> filerList = new ArrayList<String>();
				List<HashMap<String,Object>> queryFilterList = (ArrayList<HashMap<String,Object>>)_QueryParam.get("QueryList");
				for(HashMap<String,Object> hm :queryFilterList)
				{
					if (Boolean.parseBoolean(hm.get("D1")+""))
					{
						String SubSQL = hm.get("QueryField")+" "+hm.get("QueryOper")+" '"+hm.get("QueryValue")+"'";
						if (hm.get("QueryOper").toString().toUpperCase().equals("LIKE"))
						{
							SubSQL = hm.get("QueryField")+" "+hm.get("QueryOper")+" '%"+hm.get("QueryValue")+"%'";
						}
						filerList.add(SubSQL);
					}
				}
				if (filerList.size()>0)
				{
					String SQL = "select * from "+pGeoLayer.getDataset().getDataTableName()+" where SYS_STATUS='0' and %1$s";
					SQL = String.format(SQL, Tools.JoinT(" and ", filerList));
					_QueryParam.put("SQL_Filter", SQL);
					if (m_Callback!=null)m_Callback.OnClick("SQL查询", "");
					_Dialog.dismiss();
				} else
				{
					Tools.ShowMessageBox(_Dialog.getContext(),"没有勾选查询条件！");
				}
			}});
		

	}

	private ICallback m_Callback = null;
	public void SetICallback(ICallback cb)
	{
		this.m_Callback = cb;
	}
	
	//查询参数，结构：SQL,GeoLayer,FilterList
	private HashMap<String,Object> _QueryParam = null;
	public void SetQueryParam(HashMap<String,Object> queryParam)
	{
		this._QueryParam = queryParam;
	}
	
	
	//当前选中的记录
	private HashMap<String,Object> _SelectedFilterItem = null;
	
	//当前选中的字段项
	private HashMapEx _SelectedFieldItem = null;
	

	/**
	 * 填充查询条件
	 */
	private void FillQueryFilterList()
	{
		GeoLayer pGeoLayer = (GeoLayer)this._QueryParam.get("GeoLayer");
		
		//查询图层
    	_Dialog.SetCaption("查询条件-【"+pGeoLayer.GetAliasName()+"】");
		//填充查询字段
		this.FillQueryFieldToList(pGeoLayer);
		
		//操作符列表
		List<String> operList =new ArrayList<String>();
		operList.add("等于(=)");operList.add("包含(like)");operList.add("大于(>)");
		operList.add("小于(<)");operList.add("不等于(<>)");operList.add("大等于(>=)");operList.add("小等于(<=)");
		v1_DataBind.SetBindListSpinner(_Dialog, "操作符", operList, R.id.sp_operlist);
		
		//绑定查询条件
		this.BindQueryFilterToList();

	}
	
	/**
	 * 绑定查询条件到列表
	 */
	private void BindQueryFilterToList()
	{
		this._SelectedFilterItem = null;
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_result1), "SQL查询条件列表",new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr)
			{
				if (Str.equals("列表选项"))
				{
					_SelectedFilterItem = (HashMap<String,Object>)ExtraStr;
				}
			}});
		if (this._QueryParam.containsKey("QueryList"))
		{
			hvf.BindDataToListView((List<HashMap<String,Object>>)this._QueryParam.get("QueryList"));
		}
    	
	}
	
	/**
	 * 填充查询字段到列表
	 * @param GeoLayer
	 */
	private void FillQueryFieldToList(GeoLayer pGeoLayer)
	{
		List<HashMapEx> FieldList = new ArrayList<HashMapEx>();
		
		if (pGeoLayer.getDataset().getDataSource().getEditing())
		{
			v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId());
			for(v1_LayerField LF:pLayer.GetFieldList())
			{
				HashMapEx hmx = new HashMapEx();
				hmx.put("D1", LF.GetFieldName());
				hmx.put("FieldName", LF.GetDataFieldName());
				hmx.put("FieldCaption", LF.GetFieldName());
				hmx.put("LayerId", pGeoLayer.getId());
				FieldList.add(hmx);
			}
		} 
		else
		{
			List<FieldInfo> FIList = pGeoLayer.getDataset().getTableStruct();
			for(FieldInfo FI : FIList)
			{
				if (FI.getType())continue;
				HashMapEx hmx = new HashMapEx();
				hmx.put("D1", FI.getCaption());
				hmx.put("FieldName", FI.getName());
				hmx.put("FieldCaption", FI.getCaption());
				hmx.put("LayerId", pGeoLayer.getId());
				FieldList.add(hmx);
			}
		}

		this._SelectedFieldItem = null;
		v1_DataBind.SetBindListSpinnerByHashMap(_Dialog, "查询字段", FieldList, R.id.sp_fieldlist,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				
				HashMapEx hmx = (HashMapEx)ExtraStr;
				GeoLayer pGeoLayer = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(hmx.get("LayerId")+"");
				if (pGeoLayer!=null)
				{
					_SelectedFieldItem = hmx;
					//填充查询值域
					FillQueryValueToList(pGeoLayer,hmx.get("FieldName")+"");
				}
				
			}});
	}
	
	/**
	 * 填充查询值域到列表
	 * @param pGeoLayer
	 * @param FieldName
	 */
	private void FillQueryValueToList(GeoLayer pGeoLayer,String FieldName)
	{
		//查询值域列表
		List<String> queryValueList = new ArrayList<String>();
//		String SQL = "select distinct %2$s from %1$s order by %2$s limit 20";
		String SQL = "select distinct %2$s from %1$s order by %2$s ";
		SQL = String.format(SQL, pGeoLayer.getDataset().getDataTableName(),FieldName);
		SQLiteDataReader DR = pGeoLayer.getDataset().getDataSource().Query(SQL);
		if (DR==null) return;
		while(DR.Read())
		{
			String value = DR.GetString(0);
			if (value!=null)queryValueList.add(value);
		}DR.Close();
		
		//绑定到列表
		v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.spqueryvalue);
		esd1.SetSelectItemList(queryValueList);
	}
	
	
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				FillQueryFilterList();
				}}
    	);
    	_Dialog.show();
    }
}
