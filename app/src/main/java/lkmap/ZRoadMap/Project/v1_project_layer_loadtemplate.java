package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_layer_loadtemplate
{
	private v1_FormTemplate _Dialog = null; 
	

    public v1_project_layer_loadtemplate()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_loadtemplate);
    	_Dialog.ReSetSize(1f,0.96f);
    	//��ʼ��ʾ����ť
    	_Dialog.SetCaption(Tools.ToLocale("ͼ��ģ��"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_datadiction_delete+","+Tools.ToLocale("ɾ��")+"  ,ɾ��", pCallback);
    	
    	//������֧�� 
    	int[] ViewID = new int[]{R.id.tvLocaleText1,R.id.tvLocaleText2};
    	for(int vid :ViewID)
    	{
    		Tools.ToLocale(_Dialog.findViewById(vid));
    	}
    }
    
    private void SetButtonEnable(boolean enable)
    {
    	_Dialog.GetButton("1").setEnabled(enable);
    	_Dialog.GetButton("2").setEnabled(enable);
    }
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (m_Callback==null) _Dialog.dismiss();
	    		if(m_LayerList!=null)m_Callback.OnClick("ģ���б�", m_LayerList);
	    		_Dialog.dismiss();
	    	}
	    	
	    	if (Str.equals("ɾ��"))
	    	{
	    		final String NameAndTime = Tools.GetSpinnerValueOnID(_Dialog, R.id.lt_name);
	    		Tools.ShowYesNoMessage(_Dialog.getContext(), "�Ƿ�ɾ��ģ��["+NameAndTime+"]��", new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (Str.equals("YES"))
						{
							if (PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().DeleteTemplateByName(NameAndTime.split("��")[0]))
								LoadTemplateListInfo();
						}
					}});
	    	}
		}};



	private List<v1_Layer> m_LayerList = null;

	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback=cb;}
	
	//����ģ����Ϣ�б�
    private void LoadTemplateListInfo()
    {
    	//��ʽ������,����ʱ��
    	List<String> templateNameList = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().ReadTemplateList("�û�");
    	v1_DataBind.SetBindListSpinner(_Dialog, "ѡ��ͼ��ģ��", templateNameList,R.id.lt_name,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("OnItemSelected"))
				{
					String NameAndTime = ExtraStr.toString();
					LoadLayerListByTemplateName(NameAndTime.split("��")[0]);
				}
			}});
    	this.LoadLayerListByTemplateName("");
    	
    	if (templateNameList.size()>0)this.SetButtonEnable(true);else this.SetButtonEnable(false);
    }
	
    
	//����ģ�����Ƽ���ͼ���б�
    private void LoadLayerListByTemplateName(String TemplateName)
    {
    	//��ͼ��ģ���б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "����ͼ��ģ���б�");
    	
    	this.m_LayerList = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().ReadLayerTemplate(TemplateName);
    	
    	//��ͼ����Ϣ�󶨵��б���
    	List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
    	
    	if (this.m_LayerList!=null)
    	{
	    	for(v1_Layer vLayer:this.m_LayerList)
	    	{
	        	HashMap<String,Object> hm = new HashMap<String,Object>();
	        	hm.put("D1", vLayer.GetLayerAliasName());
	        	hm.put("D2", vLayer.GetLayerTypeName());
	        	dataList.add(hm);
	    	}
    	}
    	hvf.BindDataToListView(dataList);
    }

    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadTemplateListInfo();}}
    	);
    	_Dialog.show();
    }
    

}
