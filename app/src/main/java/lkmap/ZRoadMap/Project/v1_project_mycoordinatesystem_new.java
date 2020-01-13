package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.DataBindOfKeyValue;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.EditText;
import android.widget.Spinner;

import lkmap.Enum.lkDataSourceType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_ConfigDB;
import lkmap.ZRoadMap.Config.v1_UserConfigDB_MyCoordinateSystem;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;

public class v1_project_mycoordinatesystem_new
{
	private v1_FormTemplate _Dialog = null; 

	
    public v1_project_mycoordinatesystem_new()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_mycoordinatesystem_new);
    	_Dialog.ReSetSize(1f,-1f);
    	//���ñ���
    	_Dialog.SetCaption("�ҵ�������ϵ");
    	
    	//����Ĭ�ϰ�ť
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",ȷ��  ,ȷ��", _Callback);
    }
    
    //�½��ҵ�����ϵ��Ļص�
    private ICallback _newCallback = null;
    public void SetCallback(ICallback cb){this._newCallback = cb;}
    
    private ICallback _Callback = new ICallback(){

		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("ȷ��"))
			{
				//��ȡ�ҵ�����ϵ����
				String myCoorName = Tools.GetTextValueOnID(_Dialog, R.id.et_name);
				String resultStr = PubVar.m_DoEvent.m_UserConfigDB.GetMyCoodinateSystem().SaveMyCoordinateSystem(myCoorName, m_NewCoorSystemPara);
				if (!resultStr.equals("OK"))
				{
					Tools.ShowMessageBox(_Dialog.getContext(), "�ҵ�����ϵ����ʧ�ܣ�\r\nԭ��"+resultStr);
					return;
				} else
				{
					if (_newCallback!=null)_newCallback.OnClick("�½��ҵ�����ϵ",null);
					_Dialog.dismiss();
				}
			}
		}};
    

    //����������ϵ�Ĳ���
	private HashMap<String,String> m_NewCoorSystemPara = null;
	public void SetNewCoorSystemPara(HashMap<String,String> _NewCoorSystemPara)
	{
		this.m_NewCoorSystemPara = _NewCoorSystemPara;
		v1_project_mycoordinatesystem.FillPromptInfo(_Dialog, this.m_NewCoorSystemPara);
	}
    public void ShowDialog()
    {
    	_Dialog.show();
    }
    


}
