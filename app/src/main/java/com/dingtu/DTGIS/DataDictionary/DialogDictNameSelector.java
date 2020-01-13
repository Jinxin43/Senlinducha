package com.dingtu.DTGIS.DataDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class DialogDictNameSelector 
{
	private v1_FormTemplate dialogView = null;
	private String mHangye;
	private String mYinzi;
	public DialogDictNameSelector(String hangye,String yinzi)
	{
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.v1_data_dictionary_zdname);
		dialogView.ReSetSize(0.5f,0.86f);
		dialogView.SetCaption("选择数据字典");
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+",确定  ,确定", pCallback);
		mHangye = hangye;
		mYinzi = yinzi;
		
	}
	
	
    private ICallback _SelectCallback = null;
    public void SetCallback(ICallback cb){this._SelectCallback = cb;}
    
	 
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
	    			String ZDType = ((HashMap<String,Object>)m_SelectItem).get("D1").toString();
		    		if (_SelectCallback!=null)_SelectCallback.OnClick("数据字典", ZDType);
	    		}
	    		dialogView.dismiss();
	    	}
		}};

	
	private Object m_SelectItem = null;
	
	private void LoadDataDictionary(String hangye,String yinzi)
    {
    	//绑定工程列表
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(dialogView.findViewById(R.id.zdname_list), "条目细类列表",new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				dialogView.GetButton("1").setEnabled(true);
				m_SelectItem = ExtraStr;
			}});
    	
    	//读取已经创建哪些工程
    	List<HashMap<String,Object>> ZDNameList = PubVar.m_DoEvent.m_DictDataDB.getDictData(hangye, yinzi,"");
    	
    	//将工程信息绑定到列表中
    	List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
    	for(HashMap<String,Object> ZDName:ZDNameList)
    	{
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", ZDName.get("Name"));
        	dataList.add(hm);
    	}
    	hvf.BindDataToListView(dataList);
    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	dialogView.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadDataDictionary(mHangye,mYinzi);
			}});
    	dialogView.show();
    }
}
