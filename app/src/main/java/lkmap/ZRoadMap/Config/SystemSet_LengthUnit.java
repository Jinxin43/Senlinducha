package lkmap.ZRoadMap.Config;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class SystemSet_LengthUnit 
{
	private v1_FormTemplate mDialog = null; 
	public SystemSet_LengthUnit()
	{
		mDialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		mDialog.SetOtherView(R.layout.systemset_lengthunit);
		mDialog.ReSetSize(0.5f,-1f);
		mDialog.SetCaption(Tools.ToLocale("ϵͳ����"));
		mDialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	
    	//��ʼ���б�
    	String[] areaUnitList = new String[]{"��","����","ǧ��","Ӣ��"};
    	v1_DataBind.SetBindListSpinner(mDialog, "�����λ",areaUnitList, R.id.sp_LengthUnit);
    	
    	//Ĭ��ֵ
    	String areaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit").Value+"";
    	Tools.SetSpinnerValueOnID(mDialog, R.id.sp_LengthUnit, areaUnit);
	}
	
	 private ICallback m_Callback = null;
	 public void SetCallback(ICallback cb){this.m_Callback = cb;}
    
	    //��ť�¼�
	    private ICallback pCallback = new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr)
			{
		    	if (Str.equals("ȷ��"))
		    	{
		    		if (m_Callback!=null)
		    		{
			    		HashMap<String,String> value = new HashMap<String,String>();
			    		value.put("F2", Tools.GetSpinnerValueOnID(mDialog, R.id.sp_LengthUnit));
		    			m_Callback.OnClick("Tag_System_LengthUnit", value);
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
