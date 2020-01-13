package lkmap.ZRoadMap.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Coordinate;
import lkmap.CoordinateSystem.CoorParamTools;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Project_New_CheckPoint_Adpter;

public class v1_transformation_paramanage_add
{
	private v1_FormTemplate _Dialog = null; 
    public v1_transformation_paramanage_add()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_transformation_paramanage_add);
    	//_Dialog.ReSetSize(PubVar.m_WindowScaleW,-1f);
    	_Dialog.SetCaption("����");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+" ,ȷ��", pCallback);
    }
    
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("ȷ��"))
			{
				if (m_EditParam==null)m_EditParam = new HashMap<String,Object>();
				m_EditParam.put("Type",m_ParamType);
				m_EditParam.put("DH",Tools.GetTextValueOnID(_Dialog, R.id.et_DH));
				m_EditParam.put("P1",Tools.GetTextValueOnID(_Dialog, R.id.et_P1));
				m_EditParam.put("P2",Tools.GetTextValueOnID(_Dialog, R.id.et_P2));
				m_EditParam.put("P3",Tools.GetTextValueOnID(_Dialog, R.id.et_P3));
				m_EditParam.put("P4",Tools.GetTextValueOnID(_Dialog, R.id.et_P4));
				m_EditParam.put("P5",Tools.GetTextValueOnID(_Dialog, R.id.et_P5));
				m_EditParam.put("P6",Tools.GetTextValueOnID(_Dialog, R.id.et_P6));
				m_EditParam.put("P7",Tools.GetTextValueOnID(_Dialog, R.id.et_P7));
				String resultStr = PubVar.m_DoEvent.m_UserConfigDB.GetTransformationParam().SaveTransformationParam(m_EditParam);
				if (resultStr.equals("OK")){if (m_Callback!=null)m_Callback.OnClick("OK", "");_Dialog.dismiss();}
				else 
				{
					Tools.ShowMessageBox(resultStr);return;
				}
			}
		}};
		

	//�򿪹��̺�Ļص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	//����������ͣ������������������Ĳ����������߲���
	private String m_ParamType = "";
	public void SetCallParamType(String ParamType)
	{
		this.m_ParamType = ParamType;
	}
	
	//�༭�Ĳ���
	private HashMap<String,Object> m_EditParam = null;
	public void SetEditParam(HashMap<String,Object> editParam)
	{
		this.m_EditParam = editParam;
	}
	
	//ˢ�²����б�
	private void RefreshParamList()
	{
		if (this.m_ParamType.equals("������"))
		{
			_Dialog.findViewById(R.id.et_P4).setVisibility(View.GONE);_Dialog.findViewById(R.id.tv_P4).setVisibility(View.GONE);
			_Dialog.findViewById(R.id.et_P5).setVisibility(View.GONE);_Dialog.findViewById(R.id.tv_P5).setVisibility(View.GONE);
			_Dialog.findViewById(R.id.et_P6).setVisibility(View.GONE);_Dialog.findViewById(R.id.tv_P6).setVisibility(View.GONE);
			_Dialog.findViewById(R.id.et_P7).setVisibility(View.GONE);_Dialog.findViewById(R.id.tv_P7).setVisibility(View.GONE);
			
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P1, "Xƽ��(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P2, "Yƽ��(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P3, "Zƽ��(��)��");
			if (this.m_EditParam!=null)
			{
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_DH, this.m_EditParam.get("DH").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P1, this.m_EditParam.get("P1").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P2, this.m_EditParam.get("P2").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P3, this.m_EditParam.get("P3").toString());
			} else
			{
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P1, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P2, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P3, "0");
			}
			
		}
		if (this.m_ParamType.equals("�Ĳ���"))
		{
			_Dialog.findViewById(R.id.et_P5).setVisibility(View.GONE);_Dialog.findViewById(R.id.tv_P5).setVisibility(View.GONE);
			_Dialog.findViewById(R.id.et_P6).setVisibility(View.GONE);_Dialog.findViewById(R.id.tv_P6).setVisibility(View.GONE);
			_Dialog.findViewById(R.id.et_P7).setVisibility(View.GONE);_Dialog.findViewById(R.id.tv_P7).setVisibility(View.GONE);
			
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P1, "Xƽ��(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P2, "Yƽ��(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P3, "  ��ת(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P4, "        �߶ȣ�");
			if (this.m_EditParam!=null)
			{
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_DH, this.m_EditParam.get("DH").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P1, this.m_EditParam.get("P1").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P2, this.m_EditParam.get("P2").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P3, this.m_EditParam.get("P3").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P4, this.m_EditParam.get("P4").toString());
			} else
			{
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P1, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P2, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P3, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P4, "1");
			}
		}
		if (this.m_ParamType.equals("�߲���"))
		{
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P1, "Xƽ��(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P2, "Yƽ��(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P3, "Zƽ��(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P4, "X��ת(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P5, "Y��ת(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P6, "Z��ת(��)��");
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_P7, "�߶�(ppm)��");
			if (this.m_EditParam!=null)
			{
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_DH, this.m_EditParam.get("DH").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P1, this.m_EditParam.get("P1").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P2, this.m_EditParam.get("P2").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P3, this.m_EditParam.get("P3").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P4, this.m_EditParam.get("P4").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P5, this.m_EditParam.get("P5").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P6, this.m_EditParam.get("P6").toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P7, this.m_EditParam.get("P7").toString());
			} else
			{
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P1, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P2, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P3, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P4, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P5, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P6, "0");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_P7, "1");
			}
		}
		
		

	}

    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				if (m_EditParam==null)_Dialog.SetCaption("������"+m_ParamType+"��");
				else _Dialog.SetCaption("�޸ġ�"+m_ParamType+"��");
				RefreshParamList();
				}}
    	);
    	_Dialog.show();
    }
}
