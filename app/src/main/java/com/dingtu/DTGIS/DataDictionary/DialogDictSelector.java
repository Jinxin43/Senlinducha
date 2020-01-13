package com.dingtu.DTGIS.DataDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.DataDictionary.v1_DataDictionary_ZDName;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;

public class DialogDictSelector 
{
	private v1_FormTemplate dialogView = null; 
	public DialogDictSelector(String hangye,String yinzi,String selectValue)
	{
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.dialogdictselector);
		dialogView.ReSetSize(0.5f,-1f);
		dialogView.SetCaption("�����ֵ�");
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+",ȷ��  ,ȷ��", pCallback);
		dialogView.SetButtonInfo("2,"+R.drawable.v1_datadiction_delete+",�ÿ�  ,�ÿ�", pCallback);
		
		
		v1_SpinnerDialog sdHangye = (v1_SpinnerDialog)dialogView.findViewById(R.id.sdHangye);
		v1_DataBind.SetBindListSpinner(dialogView.getContext(),"",Tools.StrArrayToList(new String[]{hangye}),sdHangye);
		
		v1_SpinnerDialog sdProject = (v1_SpinnerDialog)dialogView.findViewById(R.id.sdProjectType);
		v1_DataBind.SetBindListSpinner(dialogView.getContext(),"",Tools.StrArrayToList(new String[]{yinzi}),sdProject);
		
		final String mHangye = hangye;
		final String mYinzi = yinzi;
		final v1_SpinnerDialog SD3 = (v1_SpinnerDialog)dialogView.findViewById(R.id.sdDictName);
		
		if(selectValue.equals("null"))
		{
			v1_DataBind.SetBindListSpinner(dialogView.getContext(), "", Tools.StrArrayToList(new String[]{""}), SD3);
		}
		else
		{
			v1_DataBind.SetBindListSpinner(dialogView.getContext(), "", Tools.StrArrayToList(new String[]{selectValue}), SD3);
		}
		
		
    	SD3.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				DialogDictNameSelector zdname = new DialogDictNameSelector(mHangye,mYinzi);
				
				zdname.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_DataBind.SetBindListSpinner(dialogView.getContext(), "", Tools.StrArrayToList(new String[]{ExtraStr.toString()}), SD3);
						pCallback.OnClick("��ѯ�ֵ�ֵ�б�", ExtraStr);
					}});
				zdname.ShowDialog();
			}});    	
	}
	
	 //ѡ���Ļص�
    private ICallback _SelectCallback = null;
    public void SetCallback(ICallback cb){this._SelectCallback = cb;}
    
	 //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("�ÿ�"))
			{
	    		if (_SelectCallback!=null)
    			{
    				_SelectCallback.OnClick("�����ֵ�", null);
    			}
	    		dialogView.dismiss();
			}
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (_SelectCallback!=null)
    			{
//					String ZDType = Tools.GetSpinnerValueOnID(dialogView, R.id.sd_zdtype);
//					String ZDSub = Tools.GetSpinnerValueOnID(dialogView, R.id.sd_zdsub);
//					String ZDName = Tools.GetSpinnerValueOnID(dialogView, R.id.sd_zdname);
//					HashMap<String,Object> dataDicObject = PubVar.m_DoEvent.m_ConfigDB.GetDataDictionaryExplorer().GetZDValueList(ZDType, ZDSub, ZDName);
	    			String selectValue = Tools.GetSpinnerValueOnID(dialogView, R.id.sdDictName);
	    			HashMap<String,Object> dataDicObject = new HashMap<String,Object>();
	    			dataDicObject.put("ZDList", Tools.StrArrayToList(new String[]{selectValue+""}));
	    			dataDicObject.put("ZDBM", selectValue);
    				_SelectCallback.OnClick("�����ֵ�", dataDicObject);
    			}
	    		dialogView.dismiss();
	    	}
	    	
	    	if (Str.equals("��ѯ�ֵ�ֵ�б�"))
	    	{
//				String ZDType = Tools.GetSpinnerValueOnID(dialogView, R.id.sd_zdtype);
//				String ZDSub = Tools.GetSpinnerValueOnID(dialogView, R.id.sd_zdsub);
//				String ZDName = Tools.GetSpinnerValueOnID(dialogView, R.id.sd_zdname);
//				HashMap<String,Object> dataDicObject = PubVar.m_DoEvent.m_ConfigDB.GetDataDictionaryExplorer().GetZDValueList(ZDType, ZDSub, ZDName);
//				if (dataDicObject.get("ZDList")==null)Tools.SetTextViewValueOnID(dialogView, R.id.tv_zdvalue,"");
//				else Tools.SetTextViewValueOnID(dialogView, R.id.tv_zdvalue, Tools.StrListToStr((List<String>)(dataDicObject.get("ZDList"))));
	    		Tools.SetTextViewValueOnID(dialogView, R.id.tv_zdvalue, ExtraStr+"");
	    		//Tools.SetSpinnerValueOnID(A, R.id.sdDictName, sdDictName);
	    	}
		}};


    public void ShowDialog()
    {
    	dialogView.show();
    }
}
