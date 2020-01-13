package lkmap.ZRoadMap.DataDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.Funtion.DivisionManager;
import com.dingtu.senlinducha.R;

import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_ListEx_Adpter;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;

public class v1_DataDictionary_Manage 
{
	private v1_FormTemplate _Dialog = null; 
    public v1_DataDictionary_Manage()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_dictionary_manage);
    	_Dialog.ReSetSize(0.5f,0.96f);
    	_Dialog.SetCaption("数据字典");
    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+",确定  ,确定", pCallback);
    	_Dialog.findViewById(R.id.btnXZQH).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				DivisionManager dm = new DivisionManager();
				dm.ShowDialog();
				
			}});
    	
    	_Dialog.findViewById(R.id.edit_zdtype).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				v1_DataDictionary_Manage_Edit dme = new v1_DataDictionary_Manage_Edit();
				HashMap<String,String> editPara = new HashMap<String,String>();
				editPara.put("Type", "1");
				editPara.put("ZDType", Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdtype));
				dme.SetZDAllDataList(m_DataDictionaryList,editPara);
				dme.SetCallback(pCallback);
				dme.ShowDialog();
				
			}});
    	
    	_Dialog.findViewById(R.id.edit_zdsub).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				v1_DataDictionary_Manage_Edit dme = new v1_DataDictionary_Manage_Edit();
				HashMap<String,String> editPara = new HashMap<String,String>();
				editPara.put("Type", "2");
				editPara.put("ZDType", Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdtype));
				editPara.put("ZDSub", Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdsub));
				dme.SetZDAllDataList(m_DataDictionaryList,editPara);	
				dme.SetCallback(pCallback);
				dme.ShowDialog();
				
			}});
    	_Dialog.findViewById(R.id.edit_zdname).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				v1_DataDictionary_Manage_Edit dme = new v1_DataDictionary_Manage_Edit();
				HashMap<String,String> editPara = new HashMap<String,String>();
				editPara.put("Type", "3");
				editPara.put("ZDType", Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdtype));
				editPara.put("ZDSub", Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdsub));
				editPara.put("ZDName", Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdname));
				dme.SetZDAllDataList(m_DataDictionaryList,editPara);	
				dme.SetCallback(pCallback);
				dme.ShowDialog();
				
			}});   

    	_Dialog.findViewById(R.id.edit_addedit).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				//新增数据字典项目
				String newZDItemStr = Tools.GetTextValueOnID(_Dialog, R.id.et_addedit).trim();
				if (newZDItemStr.equals(""))
				{
					Tools.ShowMessageBox(_Dialog.getContext(), "请输入新增细类明细信息！");
					return;
				}

				String ZDType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdtype);
				String ZDSub = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdsub);
				String ZDName = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdname);
				
				if (ZDType.equals("") || ZDSub.equals("") || ZDName.equals(""))
				{
					Tools.ShowMessageBox(_Dialog.getContext(), "【字典、大类、细类】不全，无法填加细类明细！");
					return;
				}
				for(HashMap<String,String> HM:m_DataDictionaryList)
				{
					if (HM.get("ZDType").equals(ZDType) && HM.get("ZDSub").equals(ZDSub) && HM.get("ZDName").equals(ZDName))
					{
						if (HM.get("ZDList").contains(newZDItemStr))
						{
							Tools.ShowMessageBox(_Dialog.getContext(), "项目【"+newZDItemStr+"】已经存在，无法重复增加！");
							return;
						}
						HM.put("ZDList", HM.get("ZDList")+","+newZDItemStr);
					}
				}
				RefreshZDData(ZDType,ZDSub,ZDName);

			}});   
    	

    }
    
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		SaveDataDictionary();
	    	}
	    	
	    	if (Str.equals("编辑完成"))
	    	{
	    		m_DataDictionaryList.clear();
	    		List<HashMap<String,String>> hmList = (List<HashMap<String,String>>)ExtraStr;
	    		for(HashMap<String,String> hm:hmList)
	    		{
	    			m_DataDictionaryList.add(hm);
	    		}
	    		LoadDataDictionary();
	    	}
		}};


	//数据字典列表，格式：ZDType,ZDSub,ZDName,ZDList,ZDBM
	private List<HashMap<String,String>> m_DataDictionaryList = null;
	
	//加载数据字典
	private void LoadDataDictionary()
	{
		//清空列表
		v1_DataBind.SetBindListSpinner(_Dialog, "字典类别", new String[]{}, R.id.sd_zdtype);
		v1_DataBind.SetBindListSpinner(_Dialog, "字典大类", new String[]{}, R.id.sd_zdsub);
		v1_DataBind.SetBindListSpinner(_Dialog, "字典细类", new String[]{}, R.id.sd_zdname);

				
		//填充列表
		List<String> ZDTypeList = new ArrayList<String>();
		for(HashMap<String,String> HM:this.m_DataDictionaryList)
		{
			String ZDType = HM.get("ZDType");
			if (ZDType.equals(""))continue;
			if (!ZDTypeList.contains(ZDType))ZDTypeList.add(ZDType);
		}
		
		v1_DataBind.SetBindListSpinner(_Dialog, "字典类别", ZDTypeList, R.id.sd_zdtype, new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				ClearList();
				v1_DataBind.SetBindListSpinner(_Dialog, "字典大类", new String[]{}, R.id.sd_zdsub);
				v1_DataBind.SetBindListSpinner(_Dialog, "字典细类", new String[]{}, R.id.sd_zdname);				
				RefreshZDSub(ExtraStr+"");
			}});
	}
	
	//刷新字典大类
	private void RefreshZDSub(String ZDType)
	{
		//填充列表
		ClearList();
		v1_DataBind.SetBindListSpinner(_Dialog, "字典细类", new String[]{}, R.id.sd_zdname);	
		List<String> ZDSubList = new ArrayList<String>();
		for(HashMap<String,String> HM:this.m_DataDictionaryList)
		{
			if (HM.get("ZDType").equals(ZDType))
			{
				String ZDSub = HM.get("ZDSub");
				if (ZDSub.equals(""))continue;
				if (!ZDSubList.contains(ZDSub))ZDSubList.add(ZDSub);
			}
		}
		v1_DataBind.SetBindListSpinner(_Dialog, "字典大类", ZDSubList, R.id.sd_zdsub, new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				String ZDType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdtype);
				RefreshZDName(ZDType,ExtraStr+"");
			
			}});
		
	}
	
	//刷新字典细类
	private void RefreshZDName(String ZDType,String ZDSub)
	{
		ClearList();
		//填充列表
		List<String> ZDNameList = new ArrayList<String>();
		for(HashMap<String,String> HM:this.m_DataDictionaryList)
		{
			if (HM.get("ZDSub").equals(ZDSub) && HM.get("ZDType").equals(ZDType))
			{
				String ZDName = HM.get("ZDName")+"";
				if (ZDName.equals(""))continue;
				if (!ZDNameList.contains(ZDName))ZDNameList.add(ZDName);
			}
		}
		v1_DataBind.SetBindListSpinner(_Dialog, "字典细类", ZDNameList, R.id.sd_zdname, new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				String ZDType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdtype);
				String ZDSub = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdsub);
				RefreshZDData(ZDType,ZDSub,ExtraStr+"");
			}});
	}
	
	//刷新字典细类数据列表
	private void RefreshZDData(String ZDType,String ZDSub,String ZDName)
	{
		//填充列表
		List<HashMap<String,Object>> ZDDataList = new ArrayList<HashMap<String,Object>>();
		for(HashMap<String,String> HM:this.m_DataDictionaryList)
		{
			if (HM.get("ZDSub").equals(ZDSub) && HM.get("ZDType").equals(ZDType)&& HM.get("ZDName").equals(ZDName))
			{
				String[] dataList = HM.get("ZDList").split(",");
				for(String data:dataList)
				{
					if (data.equals(""))continue;
					HashMap<String,Object> ho = new HashMap<String,Object>();
					ho.put("ZDDataList", data);
					ho.put("ZDItem", HM);
					ZDDataList.add(ho);
				}

			}
		}
		ListView lvList = (ListView)this._Dialog.findViewById(R.id.lv_list);
        v1_ListEx_Adpter adapter = new v1_ListEx_Adpter(PubVar.m_DoEvent.m_Context,ZDDataList, 
											       R.layout.v1_bk_datadictionary_item, new String[] {"ZDDataList"}, 
											       new int[] {R.id.tv_fullname,R.id.bt_delete});  
        lvList.setAdapter(adapter);
        adapter.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, final Object ExtraStr) {
				
				final HashMap<String,Object> ho = (HashMap<String,Object>)ExtraStr;
				final String DelZSItemStr = ho.get("ZDDataList")+"";
				Tools.ShowYesNoMessage(_Dialog.getContext(), "是否删除【"+DelZSItemStr+"】？", new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr1) {
						HashMap<String,String> hm = (HashMap<String,String>)ho.get("ZDItem");
						hm.put("ZDList",hm.get("ZDList").replace(DelZSItemStr, "").replace(",,",","));
						ListView lvList = (ListView)_Dialog.findViewById(R.id.lv_list);
						v1_ListEx_Adpter la = (v1_ListEx_Adpter)lvList.getAdapter();
						la.GetDataList().remove(ExtraStr);
						la.notifyDataSetChanged();
					}});

			}});
	}
	
	
	//保存数据字典，以事务形式
	private void SaveDataDictionary()
	{
		if (PubVar.m_DoEvent.m_ConfigDB.GetDataDictionaryExplorer().Save(this.m_DataDictionaryList))
		{
			Tools.ShowMessageBox(_Dialog.getContext(),"成功保存数据字典！");
		}
		else
		{
			Tools.ShowMessageBox(_Dialog.getContext(),"注意：数据字典保存失败！");
		}
	}
	
	//清空列表
	private void ClearList()
	{
		ListView lvList = (ListView)this._Dialog.findViewById(R.id.lv_list);
		if (lvList.getAdapter()!=null)
		{
			v1_ListEx_Adpter la = (v1_ListEx_Adpter)lvList.getAdapter();
			la.GetDataList().clear();la.notifyDataSetChanged();
		}
	}
	
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				m_DataDictionaryList = PubVar.m_DoEvent.m_ConfigDB.GetDataDictionaryExplorer().GetZDAllDataList();
				LoadDataDictionary();
			}
    	});
    	_Dialog.show();
    }
}
