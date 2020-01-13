package lkmap.ZRoadMap.DataDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;

public class v1_DataDictionary_ZDType 
{
	private v1_FormTemplate _Dialog = null; 
    public v1_DataDictionary_ZDType()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_dictionary_zdtype);
    	_Dialog.ReSetSize(0.5f,0.86f);
    	_Dialog.SetCaption("数据字典");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",确定  ,确定", pCallback);
    	//_Dialog.SetButtonInfo("2,"+R.drawable.v1_layer_field_add+",新增  ,新增", pCallback);
    }
    
    //选择后的回调
    private ICallback _SelectCallback = null;
    public void SetCallback(ICallback cb){this._SelectCallback = cb;}
    
    //按钮事件
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
	    		_Dialog.dismiss();
	    	}
		}};

	
	private Object m_SelectItem = null;
    /**
     * 加载字典类别列表信息
     */
    private void LoadDataDictionary()
    {
    	//绑定工程列表
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.zdtype_list), "字典类别列表",new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				_Dialog.GetButton("1").setEnabled(true);
				m_SelectItem = ExtraStr;
			}});
    	
    	//读取已经创建哪些工程
    	List<String> ZDTypeList = PubVar.m_DoEvent.m_ConfigDB.GetDataDictionaryExplorer().GetZDTypeList();
    	
    	//将工程信息绑定到列表中
    	List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
    	for(String ZDType:ZDTypeList)
    	{
    		if (ZDType.equals(""))continue;
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", ZDType);
        	dataList.add(hm);
    	}
    	hvf.BindDataToListView(dataList);
    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadDataDictionary();}});
    	_Dialog.show();
    }
}
