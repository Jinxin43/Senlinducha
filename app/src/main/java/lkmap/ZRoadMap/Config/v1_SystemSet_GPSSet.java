package lkmap.ZRoadMap.Config;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class v1_SystemSet_GPSSet
{
	private v1_FormTemplate _Dialog = null; 
    public v1_SystemSet_GPSSet()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_systemset_gpsset);
    	_Dialog.ReSetSize(0.5f,-1f);
    	_Dialog.SetCaption(Tools.ToLocale("系统设置"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	//_Dialog.SetButtonInfo("1,"+R.drawable.v1_project_open+",打开  ,打开工程", pCallback);
    	
    	//初始化列表
    	v1_DataBind.SetBindListSpinner(_Dialog, "采样时间", new String[]{"不限","1.0","1.5","2.0","2.5","3.0"}, R.id.sp_time);
    	v1_DataBind.SetBindListSpinner(_Dialog, "采样距离", new String[]{"不限","1","1.5","2","2.5","3","3.5","4","4.5","5","5.5","10"}, R.id.sp_distance);
    	
    	//默认值
    	String gpsMinTime = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime").Value+"";
    	String gpsMinDis = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance").Value+"";
    	Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_time, gpsMinTime);
    	Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_distance, gpsMinDis);
    	
    	//多语言支持
    	int[] ViewID = new int[]{R.id.tvLocaleText1,R.id.tvLocaleText2,R.id.tvLocaleText3};
    	for(int vid : ViewID)
    	{
    		Tools.ToLocale(_Dialog.findViewById(vid));
    	}
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
		    		value.put("F2", Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_time));
		    		value.put("F3", Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_distance));
	    			m_Callback.OnClick("Tag_System_GPS", value);
	    		}

//	    		if (!PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().SaveUserPara("Tag_System_GPS", value))
//	    		{
//	    			Tools.ShowMessageBox(_Dialog.getContext(),"保存参数时失败！");return;
//	    		}
	    		_Dialog.dismiss();
	    	}
	    	
		}
    };
    
    
    public void ShowDialog()
    {
    	_Dialog.show();
    }
}
