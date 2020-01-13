package lkmap.ZRoadMap.Config;

import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class v1_SystemSet_TopXYFormate
{
	private v1_FormTemplate _Dialog = null; 
    public v1_SystemSet_TopXYFormate()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_systemset_topxyformate);
    	_Dialog.ReSetSize(0.5f,-1f);
    	_Dialog.SetCaption(Tools.ToLocale("系统设置"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	
    	//初始化列表
    	v1_DataBind.SetBindListSpinner(_Dialog, "坐标格式", m_CoorFormate.subList(0, 3), R.id.sp_coorformate);
    	v1_DataBind.SetBindListSpinner(_Dialog, "坐标数据",this.m_CoorData, R.id.sp_coordata,new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("OnItemSelected"))
				{
					if (m_CoorData.indexOf(ExtraStr.toString())==0)
					{
						v1_DataBind.SetBindListSpinner(_Dialog, "坐标格式", m_CoorFormate.subList(0, 3), R.id.sp_coorformate,new ICallback(){

							@Override
							public void OnClick(String Str, Object ExtraStr) {
								UpdateShowFormate();
							}});
					}
					
					if (m_CoorData.indexOf(ExtraStr.toString())==1)
					{
						v1_DataBind.SetBindListSpinner(_Dialog, "坐标格式", m_CoorFormate.subList(3, 4), R.id.sp_coorformate,new ICallback(){

							@Override
							public void OnClick(String Str, Object ExtraStr) {
								UpdateShowFormate();
							}});
					}
					UpdateShowFormate();
				}
			}});

    	//默认值
    	String[] formateCode = (PubVar.m_HashMap.GetValueObject("Tag_System_TopXYFormat_Code").Value+"").split("_");
    	if (formateCode[2].equals("0"))Tools.SetCheckValueOnID(_Dialog, R.id.cb_gc, false);
    	((CheckBox)_Dialog.findViewById(R.id.cb_gc)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				UpdateShowFormate();
			}});
    	if (formateCode[0].contains("GPS"))Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_coordata, this.m_CoorData.get(0));
    	if (formateCode[0].contains("PROJECT"))Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_coordata, this.m_CoorData.get(1));
    	int idx = Integer.parseInt(formateCode[1]);Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_coorformate, this.m_CoorFormate.get(idx));

    	//多语言支持
    	int[] ViewID = new int[]{R.id.tvLocaleText1,R.id.tvLocaleText2,R.id.tvLocaleText3,R.id.tvLocaleText4,R.id.cb_gc};
    	for(int vid : ViewID)
    	{
    		Tools.ToLocale(_Dialog.findViewById(vid));
    	}
    }
    
    //坐标数据
    private List<String> m_CoorData = Tools.StrArrayToList(new String[]{"GPS经纬度坐标","当前工程投影坐标"});
    //坐标格式
    private List<String> m_CoorFormate = Tools.StrArrayToList(new String[]{"DD°MM′SS.SSSS″","DD°MM.MMMMMM′","DD.DDDDDD°","X=0.000 Y=0.000"});
    
    //坐标格式配置参数
    private HashMap<String,String> m_CoorFormatePara = new HashMap<String,String>();
    
    //更新坐标显示格式
    private void UpdateShowFormate()
    {
    	String CoorData = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_coordata);
    	String CoorFormate = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_coorformate);
    	boolean showGC = Tools.GetCheckBoxValueOnID(_Dialog, R.id.cb_gc);
    	
    	if (this.m_CoorData.indexOf(CoorData.toString())==0)  //经纬度
    	{
    		String FatStr = CoorFormate.replace("D", "0").replace("M", "0").replace("S", "0");
    		FatStr = "N:"+FatStr+" E:"+FatStr;
    		
    		this.m_CoorFormatePara.put("Code2", this.m_CoorFormate.indexOf(CoorFormate)+"");
    		
    		//高程
    		if (showGC){FatStr+=" H:0.00";this.m_CoorFormatePara.put("Code3", "1");}
			else this.m_CoorFormatePara.put("Code3", "0");
    	
    		Tools.SetTextViewValueOnID(_Dialog, R.id.sp_showformate, FatStr);
    		
    		//标识
			this.m_CoorFormatePara.put("Code1", "GPS");
    	}
    	
    	if (CoorData.equals("当前工程投影坐标"))   //投影坐标
    	{
    		String FatStr = CoorFormate;
    		
    		this.m_CoorFormatePara.put("Code2",  this.m_CoorFormate.indexOf(CoorFormate)+"");
    		
    		//高程
    		if (showGC){FatStr+=" H=0.00";this.m_CoorFormatePara.put("Code3", "1");}
    		else this.m_CoorFormatePara.put("Code3", "0");
    		
    		Tools.SetTextViewValueOnID(_Dialog, R.id.sp_showformate, FatStr);
    		
    		//标识
    		this.m_CoorFormatePara.put("Code1", "PROJECT");
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
	    			//构造编码段，共分三段
		    		HashMap<String,String> value = new HashMap<String,String>();
		    		value.put("F2", m_CoorFormatePara.get("Code1")+"_"+m_CoorFormatePara.get("Code2")+"_"+m_CoorFormatePara.get("Code3"));
		    		value.put("F3", Tools.GetTextValueOnID(_Dialog, R.id.sp_showformate));
	    			m_Callback.OnClick("Tag_System_TopXYFormat", value);
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
