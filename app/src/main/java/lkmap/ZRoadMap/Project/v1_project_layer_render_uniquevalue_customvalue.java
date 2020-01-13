package lkmap.ZRoadMap.Project;

import com.dingtu.senlinducha.R;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class v1_project_layer_render_uniquevalue_customvalue
{
	private v1_FormTemplate _Dialog = null; 
	
    public v1_project_layer_render_uniquevalue_customvalue()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_render_uniquevalue_customvalue);
    	//_Dialog.ReSetSize(1f, -1f);
    	_Dialog.SetCaption("自定义值");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    }

    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		String Value = Tools.GetTextValueOnID(_Dialog, R.id.etvalue);
	    		Value = Value.replace("，", ",");
	    		if (m_Callback!=null)m_Callback.OnClick("自定义值", Value);
	    		_Dialog.dismiss();
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
