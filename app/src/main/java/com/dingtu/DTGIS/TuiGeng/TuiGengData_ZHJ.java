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
		dialogView.SetCaption("设置株行距");
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+",确定  ,确定", pCallback);
	}
	
	//选择后的回调
    private ICallback mCallback = null;
    public void SetCallback(ICallback cb){this.mCallback = cb;}
    
	
	 private ICallback pCallback = new ICallback()
	 {
			@Override
			public void OnClick(String Str, Object ExtraStr)
			{
		    	if (Str.equals("确定"))
		    	{
		    		try
		        	{
		        		Double.parseDouble(Tools.GetTextValueOnID(dialogView, R.id.et_cd));
		        	}
		        	catch(NumberFormatException e)
		    		{ 
		        		Tools.ShowMessageBox("输入格式不正确，请重新输入！");
		        		return ;
		        	}
		    		try
		    		{
		        		Double.parseDouble(Tools.GetTextValueOnID(dialogView, R.id.et_kd));
		        	}
		        	catch(NumberFormatException e)
		    		{ 
		        		Tools.ShowMessageBox("输入格式不正确，请重新输入！");
		        		return ;
		        	}
		    		
		    		if(mCallback!=null)
		    		{
		    			mCallback.OnClick("株行距", Tools.GetTextValueOnID(dialogView, R.id.et_cd)+"*"+Tools.GetTextValueOnID(dialogView, R.id.et_kd));
		    		}
		    		
		    		dialogView.dismiss();
		    	}
		}};
			
	 public void ShowDialog()
    {
	 	dialogView.show();
    }
}
