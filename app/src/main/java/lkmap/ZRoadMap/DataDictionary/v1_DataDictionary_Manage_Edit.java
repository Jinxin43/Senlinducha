package lkmap.ZRoadMap.DataDictionary;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_ListEx_Adpter;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.Button;
import android.widget.ListView;

public class v1_DataDictionary_Manage_Edit 
{
	private v1_FormTemplate _Dialog = null; 
    public v1_DataDictionary_Manage_Edit()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_dictionary_manage_edit);
    	_Dialog.ReSetSize(0.6f,0.96f);
    	_Dialog.SetCaption("编辑数据字典");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",确定  ,确定", pCallback);
    	
    	_Dialog.findViewById(R.id.bt_addedit).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Button bt = (Button)v;
				if (bt.getText().equals("新增"))
				{
					AddNew();
				}
				if (bt.getText().equals("修改"))
				{
					Edit();
				}
				
			}});
    }
    
    private void AddNew()
    {
    	String newItemStr = Tools.GetTextValueOnID(_Dialog,R.id.et_addedit).trim();
    	if (newItemStr.equals(""))
    	{
    		Tools.ShowMessageBox(_Dialog.getContext(), "请输入新增项目的名称！");
    		return;
    	}

		//判读指定的字典类型是否存在
		if (this.HaveEqualItemName(newItemStr))
		{
    		Tools.ShowMessageBox(_Dialog.getContext(), "新增项目名称【"+newItemStr+"】已经存在，无法增加！");
    		return;
		}
		
		HashMap<String,String> newItemHM = new HashMap<String,String>();
		if (this.m_EditPara.get("Type").equals("1"))
		{
			newItemHM.put("ZDType", newItemStr);
			newItemHM.put("ZDSub", "");
			newItemHM.put("ZDName", "");
			newItemHM.put("ZDList", "");
			newItemHM.put("ZDBM", "");
		}
		if (this.m_EditPara.get("Type").equals("2"))
		{
			newItemHM.put("ZDType", this.m_EditPara.get("ZDType"));
			newItemHM.put("ZDSub", newItemStr);
			newItemHM.put("ZDName", "");
			newItemHM.put("ZDList", "");
			newItemHM.put("ZDBM", "");
		}
		if (this.m_EditPara.get("Type").equals("3"))
		{
			newItemHM.put("ZDType", this.m_EditPara.get("ZDType"));
			newItemHM.put("ZDSub", this.m_EditPara.get("ZDSub"));
			newItemHM.put("ZDName", newItemStr);
			newItemHM.put("ZDList", "");
			
			//计算最大编号
			int MaxCode = 0;
			for(HashMap<String,String> hm:this.m_ZDAllDataList)
			{
				if (Tools.IsInteger(hm.get("ZDBM")+""))
				{
					int bh = Integer.parseInt(hm.get("ZDBM")+"");
					if (bh>MaxCode)MaxCode = bh;
				}
			}
			newItemHM.put("ZDBM", (MaxCode+1)+"");
		}
		this.m_ZDAllDataList.add(newItemHM);
		this.LoadEditList();

    }
    
    private void Edit()
    {
    	String newItemStr = Tools.GetTextValueOnID(_Dialog,R.id.et_addedit).trim();
    	if (newItemStr.equals(""))
    	{
    		Tools.ShowMessageBox(_Dialog.getContext(), "请输入项目的新名称！");
    		return;
    	}
    	
		//新旧名称是否相同
		String OldItemStr = this._Dialog.findViewById(R.id.bt_addedit).getTag()+"";
		if (newItemStr.equals(OldItemStr))
		{
			this.LoadEditList(); return;
		}

		//判读新项目名称是否存在
		if (this.HaveEqualItemName(newItemStr))
		{
    		Tools.ShowMessageBox(_Dialog.getContext(), "项目名称【"+newItemStr+"】已经存在，无法修改！");
    		return;
		}
		

		for(HashMap<String,String> hm:this.m_ZDAllDataList)
		{
			if (this.m_EditPara.get("Type").equals("1"))
			{
				if (hm.get("ZDType").equals(OldItemStr)) hm.put("ZDType", newItemStr);
			}
	    	if (this.m_EditPara.get("Type").equals("2"))
	    	{
    			if (hm.get("ZDType").equals(this.m_EditPara.get("ZDType")) &&
    			    hm.get("ZDSub").equals(OldItemStr))hm.put("ZDSub", newItemStr);
	    	}
	    	if (this.m_EditPara.get("Type").equals("3"))
	    	{
    			if (hm.get("ZDType").equals(this.m_EditPara.get("ZDType")) &&
    				hm.get("ZDSub").equals(this.m_EditPara.get("ZDSub")) &&	
    			    hm.get("ZDName").equals(OldItemStr)) hm.put("ZDName", newItemStr);
	    	}
		}
		this.LoadEditList();

    }
    
    //指定的项目是否存在
    private boolean HaveEqualItemName(String ItemName)
    {
    	if (this.m_EditPara.get("Type").equals("1"))
    	{
    		for(HashMap<String,String> hm:this.m_ZDAllDataList)
    		{
    			if (hm.get("ZDType").equals(ItemName)) return true;
    		}
    	}
    	if (this.m_EditPara.get("Type").equals("2"))
    	{
    		for(HashMap<String,String> hm:this.m_ZDAllDataList)
    		{
    			if (hm.get("ZDType").equals(this.m_EditPara.get("ZDType")) &&
    			    hm.get("ZDSub").equals(ItemName)) return true;
    		}
    	}
    	if (this.m_EditPara.get("Type").equals("3"))
    	{
    		for(HashMap<String,String> hm:this.m_ZDAllDataList)
    		{
    			if (hm.get("ZDType").equals(this.m_EditPara.get("ZDType")) &&
    				hm.get("ZDSub").equals(this.m_EditPara.get("ZDSub")) &&	
    			    hm.get("ZDName").equals(ItemName)) return true;
    		}
    	}
    	
    	return false;
    }
    
    //选择后的回调
    private ICallback m_Callback = null;
    public void SetCallback(ICallback cb){this.m_Callback = cb;}
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (m_Callback!=null)m_Callback.OnClick("编辑完成", m_ZDAllDataList);
	    		_Dialog.dismiss();
	    	}
		}};
		
	//所有数据字典项目
	private List<HashMap<String,String>> m_ZDAllDataList = null;
	
	//编辑参数：格式：Type=1,2,3，ZDType,ZDSub,ZDName
	private HashMap<String,String> m_EditPara = null;
	public void SetZDAllDataList(List<HashMap<String,String>> dataList,HashMap<String,String> editPara)
	{
		this.m_ZDAllDataList = new ArrayList<HashMap<String,String>>();
		for(HashMap<String,String> data:dataList)
		{
			HashMap<String,String> newItem = new HashMap<String,String>();
			for(String key:data.keySet())
			{
				newItem.put(key, data.get(key));
			}
			this.m_ZDAllDataList.add(newItem);
		}
		this.m_EditPara = editPara;
	}

	

	private void LoadEditList()
	{
		Button bt = (Button)_Dialog.findViewById(R.id.bt_addedit);
		bt.setText("新增");
		bt.setTag("");
		Tools.SetTextViewValueOnID(_Dialog, R.id.et_addedit, "");
		
		List<String> dataList = new ArrayList<String>();
		for(HashMap<String,String> hm:this.m_ZDAllDataList)
		{
			if (this.m_EditPara.get("Type").equals("1"))
			{
				if (!dataList.contains(hm.get("ZDType")))dataList.add(hm.get("ZDType"));
			}
			if (this.m_EditPara.get("Type").equals("2"))
			{
				if (hm.get("ZDType").equals(this.m_EditPara.get("ZDType")))
				{
					if (!dataList.contains(hm.get("ZDSub")))dataList.add(hm.get("ZDSub"));
				}
			}
			if (this.m_EditPara.get("Type").equals("3"))
			{
				if (hm.get("ZDType").equals(this.m_EditPara.get("ZDType")) && 
					hm.get("ZDSub").equals(this.m_EditPara.get("ZDSub")))
				{
					if (!dataList.contains(hm.get("ZDName")))dataList.add(hm.get("ZDName"));
				}
			}
		}
		
		List<HashMap<String,Object>> dataHoList = new ArrayList<HashMap<String,Object>>();
		for(String Str:dataList)
		{
			if (Str.equals(""))continue;
			HashMap<String,Object> ho = new HashMap<String,Object>();
			ho.put("Name", Str);
			dataHoList.add(ho);
		}
		
		ListView lvList = (ListView)this._Dialog.findViewById(R.id.lv_list);
        v1_ListEx_Adpter adapter = new v1_ListEx_Adpter(PubVar.m_DoEvent.m_Context,dataHoList, 
											       R.layout.v1_bk_datadictionary_item, new String[] {"Name"}, 
											       new int[] {R.id.tv_fullname,R.id.bt_delete});  
        lvList.setAdapter(adapter);
        adapter.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, final Object ExtraStr) {
				
				final HashMap<String,Object> ho = (HashMap<String,Object>)ExtraStr;
				final String DelZSItemStr = ho.get("Name")+"";
				Tools.ShowYesNoMessage(_Dialog.getContext(), "是否删除【"+DelZSItemStr+"】？", new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr1) {
						DeleteItem(DelZSItemStr);
						LoadEditList();
					}});
			}});
        lvList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				
				v1_ListEx_Adpter la = (v1_ListEx_Adpter)((ListView)arg0).getAdapter();
				la.SetSelectItemIndex(arg2);
				la.notifyDataSetChanged();
				HashMap<String,Object> ho = (HashMap<String,Object>)la.getItem(arg2);
				Button bt = (Button)_Dialog.findViewById(R.id.bt_addedit);
				bt.setText("修改");
				bt.setTag(ho.get("Name")+"");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_addedit,ho.get("Name")+"");
			}});        
	}
	
	//删除指定项目
	private void DeleteItem(String deleteItemName)
	{
		int AllItemCount = this.m_ZDAllDataList.size()-1;
		for(int i=AllItemCount;i>=0;i--)
		{
			if (m_EditPara.get("Type").equals("1"))
			{
				if (this.m_ZDAllDataList.get(i).get("ZDType").equals(deleteItemName))
				{
					this.m_ZDAllDataList.remove(i);
				}
			}
			if (m_EditPara.get("Type").equals("2"))
			{
				if (this.m_ZDAllDataList.get(i).get("ZDType").equals(m_EditPara.get("ZDType")) &&
					this.m_ZDAllDataList.get(i).get("ZDSub").equals(deleteItemName))
				{
					this.m_ZDAllDataList.remove(i);
				}
			}
			if (m_EditPara.get("Type").equals("3"))
			{
				if (this.m_ZDAllDataList.get(i).get("ZDType").equals(m_EditPara.get("ZDType")) &&
					this.m_ZDAllDataList.get(i).get("ZDSub").equals(m_EditPara.get("ZDSub")) &&
					this.m_ZDAllDataList.get(i).get("ZDName").equals(deleteItemName))
				{
					this.m_ZDAllDataList.remove(i);
				}
			}
		}
	}
	
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadEditList();
			}
    	});
    	_Dialog.show();
    }
}
