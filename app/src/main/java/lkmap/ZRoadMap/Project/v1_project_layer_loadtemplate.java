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
    	//初始显示及按钮
    	_Dialog.SetCaption(Tools.ToLocale("图层模板"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_datadiction_delete+","+Tools.ToLocale("删除")+"  ,删除", pCallback);
    	
    	//多语言支持 
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
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (m_Callback==null) _Dialog.dismiss();
	    		if(m_LayerList!=null)m_Callback.OnClick("模板列表", m_LayerList);
	    		_Dialog.dismiss();
	    	}
	    	
	    	if (Str.equals("删除"))
	    	{
	    		final String NameAndTime = Tools.GetSpinnerValueOnID(_Dialog, R.id.lt_name);
	    		Tools.ShowYesNoMessage(_Dialog.getContext(), "是否删除模板["+NameAndTime+"]？", new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (Str.equals("YES"))
						{
							if (PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().DeleteTemplateByName(NameAndTime.split("【")[0]))
								LoadTemplateListInfo();
						}
					}});
	    	}
		}};



	private List<v1_Layer> m_LayerList = null;

	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback=cb;}
	
	//加载模板信息列表
    private void LoadTemplateListInfo()
    {
    	//格式：名称,创建时间
    	List<String> templateNameList = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().ReadTemplateList("用户");
    	v1_DataBind.SetBindListSpinner(_Dialog, "选择图层模板", templateNameList,R.id.lt_name,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("OnItemSelected"))
				{
					String NameAndTime = ExtraStr.toString();
					LoadLayerListByTemplateName(NameAndTime.split("【")[0]);
				}
			}});
    	this.LoadLayerListByTemplateName("");
    	
    	if (templateNameList.size()>0)this.SetButtonEnable(true);else this.SetButtonEnable(false);
    }
	
    
	//根据模板名称加载图层列表
    private void LoadLayerListByTemplateName(String TemplateName)
    {
    	//绑定图层模板列表
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "加载图层模板列表");
    	
    	this.m_LayerList = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().ReadLayerTemplate(TemplateName);
    	
    	//将图层信息绑定到列表中
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
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadTemplateListInfo();}}
    	);
    	_Dialog.show();
    }
    

}
