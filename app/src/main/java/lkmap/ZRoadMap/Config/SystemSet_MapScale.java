package lkmap.ZRoadMap.Config;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class SystemSet_MapScale 
{
	private v1_FormTemplate mDialog = null; 
	
	public SystemSet_MapScale()
	{
		mDialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		mDialog.SetOtherView(R.layout.systemset_mapscale);
		mDialog.ReSetSize(0.35f,0.2f);
		mDialog.SetCaption(Tools.ToLocale("系统设置"));
		mDialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
		
		//初始化列表
    	String[] areaUnitList = new String[]{"1:1万","1:5万"};
    	v1_DataBind.SetBindListSpinner(mDialog, "图幅号比例",areaUnitList, R.id.sp_MapScale);
    	
    	//默认值
    	String areaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_MapScale").Value+"";
    	Tools.SetSpinnerValueOnID(mDialog, R.id.sp_MapScale, areaUnit);
	}
	
	private ICallback m_Callback = null;
	 public void SetCallback(ICallback cb){this.m_Callback = cb;}
   
	    //按钮事件
	    private ICallback pCallback = new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr)
			{
		    	if (Str.equals("确定"))
		    	{
		    		if (m_Callback!=null)
		    		{
			    		HashMap<String,String> value = new HashMap<String,String>();
			    		value.put("F2", Tools.GetSpinnerValueOnID(mDialog, R.id.sp_MapScale));
		    			m_Callback.OnClick("Tag_System_MapScale", value);
		    		}
		    		mDialog.dismiss();
		    	}
		    	
			}
	    };

	    
	    public void ShowDialog()
	    {
	    	mDialog.show();
	    }
}
