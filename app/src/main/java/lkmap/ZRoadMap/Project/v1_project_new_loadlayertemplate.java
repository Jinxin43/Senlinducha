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

public class v1_project_new_loadlayertemplate
{
	private v1_FormTemplate _Dialog = null; 
	

    public v1_project_new_loadlayertemplate()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_new_loadlayertemplate);
    	_Dialog.ReSetSize(0.5f,0.86f);
    	//��ʼ��ʾ����ť
    	_Dialog.SetCaption(Tools.ToLocale("����ͼ��ģ��"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+" ,ȷ��", pCallback);
    	
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText2));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText3));
    	Tools.ToLocale(_Dialog.findViewById(R.id.cb_default));
    	
    }
    
    private void SetButtonEnable(boolean enable)
    {
    	_Dialog.GetButton("1").setEnabled(enable);
    }
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (m_Callback==null) _Dialog.dismiss();
	    		else 
    			{
	    			String templateName = Tools.GetSpinnerValueOnID(_Dialog, R.id.lt_name).split("��")[0];
	    			
	    			//����ΪĬ��ģ��
	    			if (Tools.GetCheckBoxValueOnID(_Dialog, R.id.cb_default))
	    			{
	    				HashMap<String,String> hm = new HashMap<String,String>();
	    				hm.put("F2", templateName);
	    				PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().SaveUserPara("Tag_SysLayerTemplateName", hm);
	    			}
	    			
    				m_Callback.OnClick("ģ���б�", templateName);
    			}
	    		_Dialog.dismiss();
	    	}
		}};

	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback=cb;}
	
	private String m_DefaultTemplateName = "��";
	public void SetDefaultTemplateName(String templateName)
	{
		this.m_DefaultTemplateName = templateName;
	}
	
	//����ģ����Ϣ�б�
    private void LoadTemplateListInfo()
    {
    	//��ʽ������,����ʱ��
    	List<String> templateNameList_User = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().ReadTemplateList("�û�");
    	List<String> templateNameList_Sys = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().ReadTemplateList("ϵͳ");
    	templateNameList_User.add(0, templateNameList_Sys.get(0));
    	templateNameList_User.add(0, "��");
    	v1_DataBind.SetBindListSpinner(_Dialog, "ѡ��ͼ��ģ��", templateNameList_User,R.id.lt_name,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("OnItemSelected"))
				{
					String NameAndTime = ExtraStr.toString();
					LoadLayerListByTemplateName(NameAndTime.split("��")[0]);
				}
			}});
    	if (templateNameList_User.size()>0)this.SetButtonEnable(true);else this.SetButtonEnable(false);
    	
    	for(String name:templateNameList_User)
    	{
    		if (name.split("��")[0].equals(this.m_DefaultTemplateName))Tools.SetSpinnerValueOnID(_Dialog, R.id.lt_name, name);
    	}
    	
    }
	
    
	//����ģ�����Ƽ���ͼ���б�
    private void LoadLayerListByTemplateName(String TemplateName)
    {
    	//��ͼ��ģ���б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "����ͼ��ģ���б�");
    	
    	List<v1_Layer> m_LayerList = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().ReadLayerTemplate(TemplateName);
    	
    	//��ͼ����Ϣ�󶨵��б���
    	List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
    	
    	if (m_LayerList!=null)
    	{
	    	for(v1_Layer vLayer:m_LayerList)
	    	{
	        	HashMap<String,Object> hm = new HashMap<String,Object>();
	        	hm.put("D1", vLayer.GetLayerAliasName());
	        	hm.put("D2", Tools.ToLocale(vLayer.GetLayerTypeName()));
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
