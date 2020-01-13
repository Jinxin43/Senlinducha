package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Enum.lkEditMode;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_layer_savetemplate
{
	private v1_FormTemplate _Dialog = null; 
	

    public v1_project_layer_savetemplate()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_savetemplate);
    	_Dialog.ReSetSize(0.5f,-1f);
    	//��ʼ��ʾ����ť
    	_Dialog.SetCaption(Tools.ToLocale("ͼ��ģ��"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);

    	//������֧�� 
    	int[] ViewID = new int[]{R.id.tvLocaleText1,R.id.tvLocaleText2,R.id.tvLocaleText3,R.id.tvLocaleText4,R.id.cb_overwrite};
    	for(int vid : ViewID)
    	{
    		Tools.ToLocale(_Dialog.findViewById(vid));
    	}
    }
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (SaveLayerInfo())_Dialog.dismiss();
	    	}
		}};



	private List<v1_Layer> m_LayerList = null;
	/**
	 * ������Ҫ�����ͼ���б�
	 * @param vLayerList
	 */
	public void SetLayerList(List<v1_Layer> vLayerList)
	{
		this.m_LayerList = vLayerList;
		Tools.SetTextViewValueOnID(_Dialog, R.id.lt_layercount, this.m_LayerList.size()+"");
		Tools.SetTextViewValueOnID(_Dialog, R.id.lt_time, Tools.GetSystemDate());
	}
	
	/**
	 * ����ͼ��ģ����Ϣ
	 * @return
	 */
	private boolean SaveLayerInfo()
	{
		//��ȡģ�屣����Ϣ
		String CreateTime = Tools.GetTextValueOnID(_Dialog, R.id.lt_time);	//����ʱ��
		String LayerCount = Tools.GetTextValueOnID(_Dialog, R.id.lt_layercount);	//ͼ������
		boolean OverWrite = Tools.GetCheckBoxValueOnID(_Dialog, R.id.cb_overwrite);  //����ͬ��ģ��
		String TempName = Tools.GetTextValueOnID(_Dialog, R.id.lt_name);	//ģ������
		
		//��֤ģ��������Ϣ
		if (TempName.equals(""))
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "ģ�����Ʋ�����Ϊ��ֵ��");return false;
		}
		if (this.m_LayerList.size()==0)
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "ͼ���б�����Ϊ0���޷�����ģ�壡");return false;
		}
		
		//�������
		HashMap<String,Object> tempatePara = new HashMap<String,Object>();
		tempatePara.put("Name", TempName);
		tempatePara.put("CreateTime", CreateTime);
		tempatePara.put("OverWrite", OverWrite);
		tempatePara.put("LayerList", this.m_LayerList);
		
		//����
		String returnStr = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().SaveLayerTemplate(tempatePara);
		if (returnStr.equals("OK")) return true;
		else
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "�޷�����ģ�壬ԭ��"+returnStr);return false;
		}
	}
	

    public void ShowDialog()
    {
    	_Dialog.show();
    }
    

}
