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

public class v1_SystemSet_AreaUnit
{
	private v1_FormTemplate _Dialog = null; 
    public v1_SystemSet_AreaUnit()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_systemset_areaunit);
    	_Dialog.ReSetSize(0.5f,-1f);
    	_Dialog.SetCaption(Tools.ToLocale("ϵͳ����"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	
    	//��ʼ���б�
    	String[] areaUnitList = new String[]{"ƽ����","ƽ������","Ķ","����"};
    	v1_DataBind.SetBindListSpinner(_Dialog, "�����λ",areaUnitList, R.id.sp_areaunit);
    	
    	//Ĭ��ֵ
    	String areaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit").Value+"";
    	Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_areaunit, areaUnit);
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
		    		value.put("F2", Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_areaunit));
	    			m_Callback.OnClick("Tag_System_AreaUnit", value);
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
