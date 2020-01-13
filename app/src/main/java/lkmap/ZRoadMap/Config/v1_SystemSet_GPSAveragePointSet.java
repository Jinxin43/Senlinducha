package lkmap.ZRoadMap.Config;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import android.text.InputType;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;

public class v1_SystemSet_GPSAveragePointSet
{
	private v1_FormTemplate _Dialog = null; 
    public v1_SystemSet_GPSAveragePointSet()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_systemset_gpsaveragepoint);
    	_Dialog.ReSetSize(0.5f,-1f);
    	_Dialog.SetCaption(Tools.ToLocale("系统设置"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	
    	//初始化列表
    	v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_pointcount);
    	esd1.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
    	esd1.SetSelectItemList(Tools.StrArrayToList(new String[]{"3","4","5","10"}));
    	esd1.getEditTextView().setEnabled(true);
    	
    	v1_EditSpinnerDialog esd2 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_vertexcount);
    	esd2.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
    	esd2.SetSelectItemList(Tools.StrArrayToList(new String[]{"3","4","5","10"}));
    	esd2.getEditTextView().setEnabled(true);
    	
    	
    	//默认值
    	String PointCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount").Value+"";
    	String VertexCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_VertexCount").Value+"";
    	esd1.setText(PointCount);
    	esd2.setText(VertexCount);
    	
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
		    		value.put("F2", "true");
		    		value.put("F3", Tools.GetViewValue(_Dialog.findViewById(R.id.sp_pointcount)));
		    		value.put("F4", Tools.GetViewValue(_Dialog.findViewById(R.id.sp_vertexcount)));
	    			m_Callback.OnClick("Tag_System_GPS_AveragePoint", value);
	    		}
	    		_Dialog.dismiss();
	    	}
	    	
		}
    };
    
    
    public void ShowDialog()
    {
    	_Dialog.show();
    }
}
