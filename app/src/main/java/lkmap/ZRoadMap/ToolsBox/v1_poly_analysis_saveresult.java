package lkmap.ZRoadMap.ToolsBox;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import android.content.Context;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class v1_poly_analysis_saveresult
{
	private v1_FormTemplate _Dialog = null; 
    public v1_poly_analysis_saveresult(Context context)
    {
    	_Dialog = new v1_FormTemplate(context);
    	_Dialog.SetOtherView(R.layout.v1_poly_analysis_saveresult);
    	_Dialog.ReSetSize(0.4f,-1f);
    	_Dialog.SetCaption("保存分析结果");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",保存  ,保存", pCallback);
    	
    	//自动获取新创建工程 名称
    	String pngName = "面分析结果【"+Tools.GetSystemDate().replace("-", "").replace(":", "").replace(" ", "")+"】";
		Tools.SetTextViewValueOnID(_Dialog, R.id.et_name, pngName);return;

    }
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("保存"))
	    	{
	    		String TxtName = Tools.GetTextValueOnID(_Dialog, R.id.et_name);
	    		if (TxtName.equals(""))
	    		{
	    			Tools.ShowMessageBox(_Dialog.getContext(), "必须填写分析结果名称！");return;
	    		} else
	    		{
	    			HashMap<String,String> result = new HashMap<String,String>();
	    			result.put("名称", TxtName);
	    			result.put("说明", Tools.GetTextValueOnID(_Dialog, R.id.et_memo));
		    		if (m_Callback!=null)m_Callback.OnClick("OK", result);
		    		_Dialog.dismiss();
	    		}
		    	
	    	}
		}};


	
	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
    public void ShowDialog()
    {
    	_Dialog.show();
    }
}
