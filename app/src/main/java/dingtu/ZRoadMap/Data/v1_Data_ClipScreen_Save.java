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
    	_Dialog.SetCaption("���ݽ�ͼ");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",����  ,����", pCallback);
    	
    	//�Զ���ȡ�´������� ����
    	String pngName = "���ݽ�ͼ��"+Tools.GetSystemDate().replace("-", "").replace(":", "").replace(" ", "")+"��";
		Tools.SetTextViewValueOnID(_Dialog, R.id.et_name, pngName);return;

    }
    
    /**
     * �����ϴα�������ƣ����ڶ�α�������
     * @param pngName
     */
    public void SetDefaultPngName(String pngName)
    {
    	if (pngName.equals("")) return;
    	Tools.SetTextViewValueOnID(_Dialog, R.id.et_name, pngName);return;
    }
    
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("����"))
	    	{
	    		String pngName = Tools.GetTextValueOnID(_Dialog, R.id.et_name);
	    		if (pngName.equals(""))
	    		{
	    			Tools.ShowMessageBox(_Dialog.getContext(), "������д��ͼ���ƣ�");return;
	    		}
	    		if (m_Callback!=null)m_Callback.OnClick("·��", pngName);
	    		_Dialog.dismiss();
		    	
	    	}
		}};


	
	//�򿪹��̺�Ļص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
    public void ShowDialog()
    {
    	_Dialog.show();
    }
}
