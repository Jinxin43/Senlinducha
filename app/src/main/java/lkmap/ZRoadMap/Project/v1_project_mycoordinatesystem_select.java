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

    	//设置标题
    	_Dialog.SetCaption("选择我的坐标系");
    	
    	//设置默认按钮
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",设为工程坐标系  ,确定", _Callback);

    }
    
    //回调
    private ICallback _selectCallback = null;
    public void SetCallback(ICallback cb){this._selectCallback = cb;}
    
    private ICallback _Callback = new ICallback(){

		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("确定"))
			{
				if (_selectCallback!=null)
				{
					_selectCallback.OnClick("选择我的坐标系", m_SelCoorSystemPara);
				}
				_Dialog.dismiss();
			}

		}};
    
    //设置选择坐标系的参数
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
