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
		dialogView.SetCaption("ѡ�������ֵ�");
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+",ȷ��  ,ȷ��", pCallback);
		mHangye = hangye;
		mYinzi = yinzi;
		
	}
	
	
    private ICallback _SelectCallback = null;
    public void SetCallback(ICallback cb){this._SelectCallback = cb;}
    
	 
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
	    			String ZDType = ((HashMap<String,Object>)m_SelectItem).get("D1").toString();
		    		if (_SelectCallback!=null)_SelectCallback.OnClick("�����ֵ�", ZDType);
	    		}
	    		dialogView.dismiss();
	    	}
		}};

	
	private Object m_SelectItem = null;
	
	private void LoadDataDictionary(String hangye,String yinzi)
    {
    	//�󶨹����б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(dialogView.findViewById(R.id.zdname_list), "��Ŀϸ���б�",new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				dialogView.GetButton("1").setEnabled(true);
				m_SelectItem = ExtraStr;
			}});
    	
    	//��ȡ�Ѿ�������Щ����
    	List<HashMap<String,Object>> ZDNameList = PubVar.m_DoEvent.m_DictDataDB.getDictData(hangye, yinzi,"");
    	
    	//��������Ϣ�󶨵��б���
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
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	dialogView.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadDataDictionary(mHangye,mYinzi);
			}});
    	dialogView.show();
    }
}
