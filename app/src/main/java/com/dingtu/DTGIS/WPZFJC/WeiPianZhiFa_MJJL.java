package com.dingtu.DTGIS.WPZFJC;

import com.dingtu.senlinducha.R;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class WeiPianZhiFa_MJJL 
{
	private v1_FormTemplate _Dialog = null; 
	
	private String mode = "new";
	
	public WeiPianZhiFa_MJJL()
	{
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.weipianzhifa_mjcl_edit);
    	_Dialog.SetCaption(Tools.ToLocale("面积测量点"));
    	//_Dialog.ReSetSize(1f,-1f);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
	}
	
	public void SetEditMode(String pMode,String cedian,String pFangweijiao,String pQingxiejiao,String pXieju,String pHeizuoibao,String pZongzuobiao)
	{
		mode = pMode;
		if(mode=="edit")
		{
			Tools.SetTextViewValueOnID(_Dialog, R.id.et_fwj,pFangweijiao);
			Tools.SetTextViewValueOnID(_Dialog, R.id.et_qxj, pQingxiejiao);
			Tools.SetTextViewValueOnID(_Dialog, R.id.et_xj, pXieju);
			
			Tools.SetTextViewValueOnID(_Dialog, R.id.et_hzb, pHeizuoibao);
			Tools.SetTextViewValueOnID(_Dialog, R.id.et_zzb, pZongzuobiao);
		}
	}
	
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb)
	{
		this.m_Callback = cb;
	}
	
	 private ICallback pCallback = new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr)
			{
				if (Str.equals("确定"))
		    	{
		    		
		    	}
			}
		};

}
