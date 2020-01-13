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
    	_Dialog.SetCaption("��ѯ����");
    	
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_clearscreen+","+Tools.ToLocale("���ò�ѯ����")+"  ,����", new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				DeleteAllFilter();
			}});
    	
    	_Dialog.findViewById(R.id.btadd).setOnClickListener(_ClickEvent);
    	_Dialog.findViewById(R.id.btdelete).setOnClickListener(_ClickEvent);
    	_Dialog.findViewById(R.id.btquery).setOnClickListener(_ClickEvent);
    }

    //��ť�¼�
    private android.view.View.OnClickListener _ClickEvent = new android.view.View.OnClickListener(){
		@Override
		public void onClick(View arg0) 
		{
			if (arg0.getTag().toString().equals("��������")){AddFilter();}
			if (arg0.getTag().toString().equals("ɾ������")){DeleteFilter();}
			if (arg0.getTag().toString().equals("��ѯ")){SQLQuery();}
		}};

	/**
	 * ���Ӳ�ѯ����
	 */
	private void AddFilter()
	{
		//��������Ƿ���ȫ
		String OperStr = Tools.GetViewValue(_Dialog.findViewById(R.id.sp_operlist));
		String ValueStr = Tools.GetViewValue(_Dialog.findViewById(R.id.spqueryvalue));
		if (this._SelectedFieldItem==null || OperStr.equals("") || ValueStr.equals(""))
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "����д�����Ĳ�ѯ������");
			return;
		}
		String FieldStr = this._SelectedFieldItem.get("FieldName")+"";
		String FieldCapStr = this._SelectedFieldItem.get("FieldCaption")+"";
		OperStr = OperStr.split("\\(")[1];OperStr = OperStr.substring(0, OperStr.length()-1);
		
		//������ʾ��
		String ShowFilterStr = FieldCapStr+" "+OperStr+" "+ValueStr;
		
		//�жϴ������Ƿ��Ѿ�����
		if (!this._QueryParam.containsKey("QueryList")){this._QueryParam.put("QueryList", new ArrayList<HashMap<String,Object>>());}
		List<HashMap<String,Object>> queryFilterList = ((ArrayList<HashMap<String,Object>>)this._QueryParam.get("QueryList"));
		for(HashMap<String,Object> hm:queryFilterList)
		{
			if (hm.get("D2").toString().equals(ShowFilterStr))
			{
				Tools.ShowToast(_Dialog.getContext(), "��ѯ�����Ѿ����ڣ��������ظ���");
				return;
			}
		}
		
		//�����µĲ�ѯ����
		HashMap<String,Object> filter = new HashMap<String,Object>();
		filter.put("QueryField", FieldStr);
		filter.put("QueryOper",OperStr);
		filter.put("QueryValue", ValueStr);

		filter.put("D1", true);
		filter.put("D2", ShowFilterStr);
		filter.put("D3", "����");

		((ArrayList<HashMap<String,Object>>)this._QueryParam.get("QueryList")).add(filter);
		this.BindQueryFilterToList();
		
	}
	
	/**
	 * ɾ����ѯ����
	 */
	private void DeleteFilter()
	{
		if (this._SelectedFilterItem==null)
		{
			Tools.ShowMessageBox(_Dialog.getContext(),"�����б���ѡ���ѯ������");
			return;
		}
		Tools.ShowYesNoMessage(_Dialog.getContext(), "�Ƿ�ɾ�����²�ѯ������\n������"+this._SelectedFilterItem.get("D2"), new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				((List<HashMap<String,Object>>)_QueryParam.get("QueryList")).remove(_SelectedFilterItem);
				BindQueryFilterToList();
			}});
	}
	
	/**
	 * ɾ�����в�ѯ����
	 */
	private void DeleteAllFilter()
	{
		if (!this._QueryParam.containsKey("QueryList"))return;
		if (((List<HashMap<String,Object>>)_QueryParam.get("QueryList")).size()==0) return;
		Tools.ShowYesNoMessage(_Dialog.getContext(), "�Ƿ�ɾ�����еĲ�ѯ������", new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				((List<HashMap<String,Object>>)_QueryParam.get("QueryList")).clear();
				BindQueryFilterToList();
			}});
	}
	
	/**
	 * ��ʼ��ѯ
	 */
	private void SQLQuery()
	{
		//SQL_Filter
		//������ѯ����
		if (!this._QueryParam.containsKey("QueryList"))
		{
			Tools.ShowMessageBox(_Dialog.getContext(),"�����Ӳ�ѯ������");
			return;
		}
		
		lkmap.Tools.Tools.OpenDialog(lkmap.Tools.Tools.ToLocale("���ڲ�ѯ")+"...",new ICallback(){
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
					if (m_Callback!=null)m_Callback.OnClick("SQL��ѯ", "");
					_Dialog.dismiss();
				} else
				{
					Tools.ShowMessageBox(_Dialog.getContext(),"û�й�ѡ��ѯ������");
				}
			}});
		

	}

	private ICallback m_Callback = null;
	public void SetICallback(ICallback cb)
	{
		this.m_Callback = cb;
	}
	
	//��ѯ�������ṹ��SQL,GeoLayer,FilterList
	private HashMap<String,Object> _QueryParam = null;
	public void SetQueryParam(HashMap<String,Object> queryParam)
	{
		this._QueryParam = queryParam;
	}
	
	
	//��ǰѡ�еļ�¼
	private HashMap<String,Object> _SelectedFilterItem = null;
	
	//��ǰѡ�е��ֶ���
	private HashMapEx _SelectedFieldItem = null;
	

	/**
	 * ����ѯ����
	 */
	private void FillQueryFilterList()
	{
		GeoLayer pGeoLayer = (GeoLayer)this._QueryParam.get("GeoLayer");
		
		//��ѯͼ��
    	_Dialog.SetCaption("��ѯ����-��"+pGeoLayer.GetAliasName()+"��");
		//����ѯ�ֶ�
		this.FillQueryFieldToList(pGeoLayer);
		
		//�������б�
		List<String> operList =new ArrayList<String>();
		operList.add("����(=)");operList.add("����(like)");operList.add("����(>)");
		operList.add("С��(<)");operList.add("������(<>)");operList.add("�����(>=)");operList.add("С����(<=)");
		v1_DataBind.SetBindListSpinner(_Dialog, "������", operList, R.id.sp_operlist);
		
		//�󶨲�ѯ����
		this.BindQueryFilterToList();

	}
	
	/**
	 * �󶨲�ѯ�������б�
	 */
	private void BindQueryFilterToList()
	{
		this._SelectedFilterItem = null;
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_result1), "SQL��ѯ�����б�",new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr)
			{
				if (Str.equals("�б�ѡ��"))
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
	 * ����ѯ�ֶε��б�
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
		v1_DataBind.SetBindListSpinnerByHashMap(_Dialog, "��ѯ�ֶ�", FieldList, R.id.sp_fieldlist,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				
				HashMapEx hmx = (HashMapEx)ExtraStr;
				GeoLayer pGeoLayer = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(hmx.get("LayerId")+"");
				if (pGeoLayer!=null)
				{
					_SelectedFieldItem = hmx;
					//����ѯֵ��
					FillQueryValueToList(pGeoLayer,hmx.get("FieldName")+"");
				}
				
			}});
	}
	
	/**
	 * ����ѯֵ���б�
	 * @param pGeoLayer
	 * @param FieldName
	 */
	private void FillQueryValueToList(GeoLayer pGeoLayer,String FieldName)
	{
		//��ѯֵ���б�
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
		
		//�󶨵��б�
		v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.spqueryvalue);
		esd1.SetSelectItemList(queryValueList);
	}
	
	
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
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
