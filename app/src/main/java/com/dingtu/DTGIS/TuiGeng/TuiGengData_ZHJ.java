package com.dingtu.DTGIS.TuiGeng;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import jxl.demo.CSV;
import lkmap.Tools.Tools;

public class TuiGengData_ZHJ 
{
	private v1_FormTemplate dialogView = null;
	public TuiGengData_ZHJ()
	{
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.tuigengdata_sheji_zhj);
		dialogView.ReSetSize(0.4f,-1);
		dialogView.SetCaption("�������о�");
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+",ȷ��  ,ȷ��", pCallback);
	}
	
	//ѡ���Ļص�
    private ICallback mCallback = null;
    public void SetCallback(ICallback cb){this.mCallback = cb;}
    
	
	 private ICallback pCallback = new ICallback()
	 {
			@Override
			public void OnClick(String Str, Object ExtraStr)
			{
		    	if (Str.equals("ȷ��"))
		    	{
		    		try
		        	{
		        		Double.parseDouble(Tools.GetTextValueOnID(dialogView, R.id.et_cd));
		        	}
		        	catch(NumberFormatException e)
		    		{ 
		        		Tools.ShowMessageBox("�����ʽ����ȷ�����������룡");
		        		return ;
		        	}
		    		try
		    		{
		        		Double.parseDouble(Tools.GetTextValueOnID(dialogView, R.id.et_kd));
		        	}
		        	catch(NumberFormatException e)
		    		{ 
		        		Tools.ShowMessageBox("�����ʽ����ȷ�����������룡");
		        		return ;
		        	}
		    		
		    		if(mCallback!=null)
		    		{
		    			mCallback.OnClick("���о�", Tools.GetTextValueOnID(dialogView, R.id.et_cd)+"*"+Tools.GetTextValueOnID(dialogView, R.id.et_kd));
		    		}
		    		
		    		dialogView.dismiss();
		    	}
		}};
			
	 public void ShowDialog()
    {
	 	dialogView.show();
    }
}
