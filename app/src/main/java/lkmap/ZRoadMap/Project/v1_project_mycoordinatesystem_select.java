package lkmap.ZRoadMap.Project;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;

public class v1_project_mycoordinatesystem_select
{
	private v1_FormTemplate _Dialog = null; 

    public v1_project_mycoordinatesystem_select()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_mycoordinatesystem_select);

    	//���ñ���
    	_Dialog.SetCaption("ѡ���ҵ�����ϵ");
    	
    	//����Ĭ�ϰ�ť
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",��Ϊ��������ϵ  ,ȷ��", _Callback);

    }
    
    //�ص�
    private ICallback _selectCallback = null;
    public void SetCallback(ICallback cb){this._selectCallback = cb;}
    
    private ICallback _Callback = new ICallback(){

		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("ȷ��"))
			{
				if (_selectCallback!=null)
				{
					_selectCallback.OnClick("ѡ���ҵ�����ϵ", m_SelCoorSystemPara);
				}
				_Dialog.dismiss();
			}

		}};
    
    //����ѡ������ϵ�Ĳ���
	private HashMap<String,String> m_SelCoorSystemPara = null;
	public void SetNewCoorSystemPara(HashMap<String,String> _NewCoorSystemPara)
	{
		this.m_SelCoorSystemPara = _NewCoorSystemPara;
		v1_project_mycoordinatesystem.FillPromptInfo(_Dialog, this.m_SelCoorSystemPara);
	}
    
    public void ShowDialog()
    {
    	_Dialog.show();
    }

}
