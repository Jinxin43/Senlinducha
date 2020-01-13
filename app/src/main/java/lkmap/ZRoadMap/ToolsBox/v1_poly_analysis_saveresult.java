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
    	_Dialog.SetCaption("����������");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",����  ,����", pCallback);
    	
    	//�Զ���ȡ�´������� ����
    	String pngName = "����������"+Tools.GetSystemDate().replace("-", "").replace(":", "").replace(" ", "")+"��";
		Tools.SetTextViewValueOnID(_Dialog, R.id.et_name, pngName);return;

    }
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("����"))
	    	{
	    		String TxtName = Tools.GetTextValueOnID(_Dialog, R.id.et_name);
	    		if (TxtName.equals(""))
	    		{
	    			Tools.ShowMessageBox(_Dialog.getContext(), "������д����������ƣ�");return;
	    		} else
	    		{
	    			HashMap<String,String> result = new HashMap<String,String>();
	    			result.put("����", TxtName);
	    			result.put("˵��", Tools.GetTextValueOnID(_Dialog, R.id.et_memo));
		    		if (m_Callback!=null)m_Callback.OnClick("OK", result);
		    		_Dialog.dismiss();
	    		}
		    	
	    	}
		}};


	
	//�ص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
    public void ShowDialog()
    {
    	_Dialog.show();
    }
}
