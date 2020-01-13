package dingtu.ZRoadMap.Data;

import com.dingtu.senlinducha.R;

import android.content.Context;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class v1_Data_ClipScreen_Save
{
	private v1_FormTemplate _Dialog = null; 
    public v1_Data_ClipScreen_Save(Context context)
    {
    	_Dialog = new v1_FormTemplate(context);
    	_Dialog.SetOtherView(R.layout.v1_data_clipscreen_save);
    	//_Dialog.ReSetSize(1f,0.96f);
    	_Dialog.SetCaption("数据截图");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",保存  ,保存", pCallback);
    	
    	//自动获取新创建工程 名称
    	String pngName = "数据截图【"+Tools.GetSystemDate().replace("-", "").replace(":", "").replace(" ", "")+"】";
		Tools.SetTextViewValueOnID(_Dialog, R.id.et_name, pngName);return;

    }
    
    /**
     * 设置上次保存的名称，用于多次保存的情况
     * @param pngName
     */
    public void SetDefaultPngName(String pngName)
    {
    	if (pngName.equals("")) return;
    	Tools.SetTextViewValueOnID(_Dialog, R.id.et_name, pngName);return;
    }
    
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("保存"))
	    	{
	    		String pngName = Tools.GetTextValueOnID(_Dialog, R.id.et_name);
	    		if (pngName.equals(""))
	    		{
	    			Tools.ShowMessageBox(_Dialog.getContext(), "必须填写截图名称！");return;
	    		}
	    		if (m_Callback!=null)m_Callback.OnClick("路径", pngName);
	    		_Dialog.dismiss();
		    	
	    	}
		}};


	
	//打开工程后的回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
    public void ShowDialog()
    {
    	_Dialog.show();
    }
}
