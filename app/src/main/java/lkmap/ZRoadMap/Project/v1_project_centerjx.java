package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.CoordinateSystem.ProjectSystem;
import lkmap.Tools.Tools;

public class v1_project_centerjx
{
	private v1_FormTemplate _Dialog = null; 

    public v1_project_centerjx()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_centerjx);
    	//_Dialog.ReSetSize(1f, -1f);
    	
    	//���ñ���
    	_Dialog.SetCaption("���뾭��");
    	
    	//����Ĭ�ϰ�ť
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",ȷ��  ,ȷ��", _pCallback);

    	//��ѡ�����б�
    	List<String> fddsList = Tools.StrArrayToList(new String[]{"3��","6��"});
    	v1_DataBind.SetBindListSpinner(_Dialog, "ѡ��ִ�����",fddsList , R.id.sp_fd,_pCallback);
    	
    	if(PubVar.Version.equals("GT"))
    	{
    		((CheckBox)_Dialog.findViewById(R.id.cb_hasDH)).setChecked(true);
    	}
    }
    
    //��д���뾭��ɺ�Ļص�
    private ICallback _pCallbackT = null;
    public void SetCallback(ICallback callBack)
    {
    	
    	this._pCallbackT = callBack;
    }
    
    private ICallback _pCallback = new ICallback(){

		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			
			if (Str.equals("ȷ��"))
			{
				EditText te = (EditText)_Dialog.findViewById(R.id.et_centerjx);
				String centerjx = te.getText().toString();
				if (centerjx.equals(""))
				{
					Tools.ShowMessageBox(_Dialog.getContext(), "����д��ȷ�����뾭�ߣ�");
					return;
				}
				
				HashMap<String, Object> hashResult = new HashMap<String, Object>();
				hashResult.put("CenterJX", centerjx);
				
				CheckBox cbHasDH =  (CheckBox)_Dialog.findViewById(R.id.cb_hasDH);
				hashResult.put("isHasDH", cbHasDH.isChecked());
				
				if(cbHasDH.isChecked())
				{
					Spinner spinner = (Spinner)_Dialog.findViewById(R.id.sp_dh);
					hashResult.put("DH", String.valueOf(spinner.getSelectedItem()));
				}
				
				String fendai = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_fd);
				hashResult.put("Fendai", fendai);
				
//				if (_pCallbackT!=null)_pCallbackT.OnClick("���뾭��", centerjx);
				if (_pCallbackT!=null)_pCallbackT.OnClick("���뾭��", hashResult);
				_Dialog.dismiss();
			}
			
			if (Str.equals("OnItemSelected"))
			{
				int fdType = 6;
				if (ExtraStr.toString().equals("3��"))fdType = 3;
				if (ExtraStr.toString().equals("6��"))fdType = 6;
				int dh1 = ProjectSystem.GetDH(73, fdType);
				int dh2 = ProjectSystem.GetDH(135, fdType);
				List<String> dhList = new ArrayList<String>();
				for(int i=dh1;i<=dh2;i++)dhList.add(i+"");
				v1_DataBind.SetBindListSpinner(_Dialog, "ѡ�����", dhList, R.id.sp_dh,new ICallback(){

					@Override
					public void OnClick(String Str, Object ExtraStr) 
					{
						int fdType1 = Integer.parseInt(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_fd).replace("��", ""));
						int dh  = Integer.parseInt(ExtraStr.toString());
						String centerjx = ProjectSystem.GetCenterJX(dh, fdType1)+"";
						Tools.SetTextViewValueOnID(_Dialog, R.id.et_centerjx, centerjx);
					}});
			}
		}};
    

    public void ShowDialog()
    {
    	_Dialog.show();
    }
}
