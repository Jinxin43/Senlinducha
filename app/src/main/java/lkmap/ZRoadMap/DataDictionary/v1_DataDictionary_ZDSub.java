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

public class v1_DataDictionary_ZDSub 
{
	private v1_FormTemplate _Dialog = null; 
    public v1_DataDictionary_ZDSub()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_dictionary_zdtype);
    	_Dialog.ReSetSize(0.5f,0.86f);
    	_Dialog.SetCaption("��Ŀ����");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",ȷ��  ,ȷ��", pCallback);
    	//_Dialog.SetButtonInfo("2,"+R.drawable.v1_layer_field_add+",����  ,����", pCallback);
    }
    
    //ѡ���Ļص�
    private ICallback _SelectCallback = null;
    public void SetCallback(ICallback cb){this._SelectCallback = cb;}
    
    //�ֵ����
    private String _ZDType = "";
    public void SetZDType(String zdType)
    {
    	this._ZDType = zdType;
    }
    
    //��ť�¼�
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
	    		_Dialog.dismiss();
	    	}
		}};

	
	private Object m_SelectItem = null;
    /**
     * �����ֵ�����б���Ϣ
     */
    private void LoadDataDictionary()
    {
    	//�󶨹����б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.zdtype_list), "��Ŀ�����б�",new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				_Dialog.GetButton("1").setEnabled(true);
				m_SelectItem = ExtraStr;
			}});
    	
    	//��ȡ�Ѿ�������Щ����
    	List<String> ZDSubList = PubVar.m_DoEvent.m_ConfigDB.GetDataDictionaryExplorer().GetZDSubList(this._ZDType);
    	
    	//��������Ϣ�󶨵��б���
    	List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
    	for(String ZDSub:ZDSubList)
    	{
    		if (ZDSub.equals(""))continue;
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", ZDSub);
        	dataList.add(hm);
    	}
    	hvf.BindDataToListView(dataList);
    }
    
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadDataDictionary();}});
    	_Dialog.show();
    }
}
