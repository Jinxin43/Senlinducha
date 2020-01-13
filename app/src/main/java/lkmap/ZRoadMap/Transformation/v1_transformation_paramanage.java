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

public class v1_transformation_paramanage
{
	private v1_FormTemplate _Dialog = null; 
    public v1_transformation_paramanage()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_transformation_paramanage);
    	_Dialog.SetCaption("ת����������");
    	_Dialog.ReSetSize(0.5f, 0.96f);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+" ,ȷ��", pCallback);

    	_Dialog.findViewById(R.id.bt_add).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("���Ӳ���", "");}});
    	
       	_Dialog.findViewById(R.id.bt_edit).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("�༭����", "");}});
       	
       	_Dialog.findViewById(R.id.bt_delete).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("ɾ������", "");}});
       	
       	_Dialog.findViewById(R.id.bt_cal).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("�������", "");}});
       	
    	((ListView)_Dialog.findViewById(R.id.listView1)).setOnItemClickListener(new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{
			ListView lv = (ListView)arg0;
			v1_Project_New_CheckPoint_Adpter hva = (v1_Project_New_CheckPoint_Adpter)lv.getAdapter();
			hva.SetSelectItemIndex(arg2);
			hva.notifyDataSetInvalidated();//���������Ѿ��䶯
			m_SelectItem = (HashMap<String,Object>)hva.getItem(arg2);
			_Dialog.findViewById(R.id.bt_edit).setEnabled(true);
			_Dialog.findViewById(R.id.bt_delete).setEnabled(true);
			_Dialog.GetButton("1").setEnabled(true);
		}});
    	
    	//�󶨲��������б�
    	String[] paramType = new String[]{"������","�߲���","�Ĳ���"};
    	v1_DataBind.SetBindListSpinner(_Dialog, "ѡ���������", Tools.StrArrayToList(paramType), R.id.spType, new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				RefreshParamList();
			}});
    }
    
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("ȷ��"))
			{
				if (m_SelectItem!=null && m_Callback!=null) 
				{
					m_Callback.OnClick("����", m_SelectItem);
				}
				_Dialog.dismiss();
			}
			
	    	if (Str.equals("���Ӳ���"))
	    	{
	    		v1_transformation_paramanage_add tpa = new v1_transformation_paramanage_add();
	    		String ParamType = Tools.GetSpinnerValueOnID(_Dialog, R.id.spType);
	    		tpa.SetCallParamType(ParamType);
	    		tpa.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						RefreshParamList();
					}});
	    		tpa.ShowDialog();
	    	}

	    	if (Str.equals("�༭����"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
		    		v1_transformation_paramanage_add tpa = new v1_transformation_paramanage_add();
		    		String ParamType = Tools.GetSpinnerValueOnID(_Dialog, R.id.spType);
		    		tpa.SetEditParam(m_SelectItem);
		    		tpa.SetCallParamType(ParamType);
		    		tpa.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							RefreshParamList();
						}});
		    		tpa.ShowDialog();
	    		}
	    	}
	    	if (Str.equals("ɾ������"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
	    			String MessageStr = "�Ƿ�ɾ�����²�����\r\n����=��"+m_SelectItem.get("DH")+"��";
	    			Tools.ShowYesNoMessage(_Dialog.getContext(), MessageStr, new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							String ID = m_SelectItem.get("ID").toString();
							if (PubVar.m_DoEvent.m_UserConfigDB.GetTransformationParam().DeleteTransformationParam(ID))
							{
								RefreshParamList();
							} else
							{
								Tools.ShowMessageBox("ɾ������ʧ�ܣ�");
							}
							
						}});
	    		}
	    	}
	    	if (Str.equals("�������"))
	    	{
	    		int CalPointCount = 0;
	    		for(HashMap<String,Object> hm:m_ParamList)
	    		{
	    			if (Boolean.parseBoolean(hm.get("Select")+""))CalPointCount++;
	    		}
	    		if (CalPointCount==0 ){Tools.ShowMessageBox("���ڽ�����Ƶ�����Ϊ��0�����޷����������");return;}
	    		
//	    		if (m_CalParamType.equals("�����Ĳ���"))
//	    		{
//	    			HashMap<String,Object> param = CalFourParam();
//	    			String Message = "Xƽ��(��)��"+param.get("DX")+"\nYƽ��(��)��"+param.get("DY")+"\n��ת(��)��"+param.get("R")+"\n�߶ȣ�"+param.get("K");
//	    			lkmap.Tools.Tools.ShowMessageBox("�Ĳ���������ɣ��������£�\n"+Message);
//	    		}
	    	}
		}};
		

	//�򿪹��̺�Ļص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	//�������ͣ����������Ĳ������߲���
	public void SetCallParamType(String calParamType)
	{
    	String[] paramType = new String[]{calParamType};
    	v1_DataBind.SetBindListSpinner(_Dialog, "ѡ���������", Tools.StrArrayToList(paramType), R.id.spType, new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				RefreshParamList();
			}});
    	_Dialog.findViewById(R.id.spType).setEnabled(false);
	}
	
	//��ǰѡ�еĲ�����
	private HashMap<String,Object> m_SelectItem = null;
	
	//�����б�
	List<HashMap<String,Object>> m_ParamList = new ArrayList<HashMap<String,Object>>();
	
	//ˢ�²����б�
	private void RefreshParamList()
	{
		this.m_ParamList.clear();
		
		int LayoutViewId = -1;
		String[] keyItem = null;
		int[] ViewItem = null;
		
		//�����ÿ��ж�ȡ�����б�
		String ParamType = Tools.GetSpinnerValueOnID(_Dialog, R.id.spType);
		this.m_ParamList = PubVar.m_DoEvent.m_UserConfigDB.GetTransformationParam().GetTransformationParamList(ParamType);
		
		for(HashMap<String,Object> hm:this.m_ParamList)
		{
			if (ParamType.equals("������"))
			{
				hm.put("T1", "Xƽ��(��)��"+hm.get("P1").toString());
				hm.put("T2", "Yƽ��(��)��"+hm.get("P2").toString());
				hm.put("T3", "Zƽ��(��)��"+hm.get("P3").toString());
				LayoutViewId = R.layout.v1_bk_transformation_paramanage_threepara_list;
				keyItem = new String[] {"DH", "T1","T2","T3"};
				ViewItem = new int[] {R.id.tvDH, R.id.tvparam1, R.id.tvparam2,R.id.tvparam3};
			}
			if (ParamType.equals("�Ĳ���"))
			{
				hm.put("T1", "Xƽ��(��)��"+hm.get("P1").toString());
				hm.put("T2", "Yƽ��(��)��"+hm.get("P2").toString());
				hm.put("T3", "��ת(��)��"+hm.get("P3").toString());
				hm.put("T4", "�߶ȣ�"+hm.get("P4").toString());
				LayoutViewId = R.layout.v1_bk_transformation_paramanage_fourpara_list;
				keyItem = new String[] {"DH", "T1","T2","T3","T4"};
				ViewItem = new int[] {R.id.tvDH, R.id.tvparam1, R.id.tvparam2,R.id.tvparam3,R.id.tvparam4};
			}
			if (ParamType.equals("�߲���"))
			{
				hm.put("T1", "Xƽ��(��)��"+hm.get("P1").toString());
				hm.put("T2", "Yƽ��(��)��"+hm.get("P2").toString());
				hm.put("T3", "Zƽ��(��)��"+hm.get("P3").toString());
				hm.put("T4", "X��ת(��)��"+hm.get("P4").toString());
				hm.put("T5", "Y��ת(��)��"+hm.get("P5").toString());
				hm.put("T6", "Z��ת(��)��"+hm.get("P6").toString());				
				hm.put("T7", "�߶�(ppm)��"+hm.get("P7").toString());
				LayoutViewId = R.layout.v1_bk_transformation_paramanage_sevenpara_list;
				keyItem = new String[] {"DH", "T1","T2","T3","T4","T5","T6","T7"};
				ViewItem = new int[] {R.id.tvDH, R.id.tvparam1, R.id.tvparam2,R.id.tvparam3,R.id.tvparam4,R.id.tvparam5,R.id.tvparam6,R.id.tvparam7};
			}
		}
		
		//ˢ���б�
        v1_Project_New_CheckPoint_Adpter adapter = new v1_Project_New_CheckPoint_Adpter(_Dialog.getContext(),this.m_ParamList, LayoutViewId, keyItem, ViewItem);  
		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
		
		_Dialog.findViewById(R.id.bt_edit).setEnabled(false);
		_Dialog.findViewById(R.id.bt_delete).setEnabled(false);
		_Dialog.GetButton("1").setEnabled(false);
	}

    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				RefreshParamList();
				}}
    	);
    	_Dialog.show();
    }
}
