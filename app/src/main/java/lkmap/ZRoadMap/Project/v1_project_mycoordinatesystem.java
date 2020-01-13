package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_mycoordinatesystem
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_mycoordinatesystem()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_mycoordinatesystem);
    	_Dialog.ReSetSize(1f, 0.96f);
    	
    	_Dialog.SetCaption(Tools.ToLocale("�ҵ�����ϵ"));
    	_Dialog.findViewById(R.id.btsave).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {pCallback.OnClick("����", "");}});
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_layer_field_delete+","+Tools.ToLocale("ɾ��")+"  ,ɾ��", pCallback);
    	
    	//������ת��
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText2));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText3));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText4));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText5));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText6));
    }
    
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ɾ��"))
	    	{
	    		final List<String> idList = new ArrayList<String>();
	    		List<String> nameList = new ArrayList<String>();
	    		for(HashMap<String,Object> hm:m_MyParamList)
	    		{
	    			if (Boolean.parseBoolean(hm.get("D1")+""))
    				{
    					idList.add(hm.get("ID")+"");
    					nameList.add("���ƣ�"+hm.get("D2")+"");
    				}
	    		}
	    		
	    		Tools.ShowYesNoMessage(_Dialog.getContext(), "�Ƿ�ɾ�������ҵ�����ϵ��\n\n"+Tools.JoinT("\n", nameList), new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
			    		if (!PubVar.m_DoEvent.m_UserConfigDB.GetMyCoodinateSystem().DeleteMyCoordinateSystem(idList))
			    		{
			    			Tools.ShowMessageBox("ɾ���ҵ�����ϵʧ�ܣ�");
			    		} else LoadMyCoordinateSystemInfo();
					}});

	    	}
	    	if (Str.equals("����"))
	    	{
	    		v1_project_mycoordinatesystem_new pmn = new v1_project_mycoordinatesystem_new();
	    		pmn.SetNewCoorSystemPara(m_NewCoorSystemPara);
	    		pmn.SetCallback(new ICallback(){

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (Str.equals("�½��ҵ�����ϵ"))
						{
							//���¼���
							LoadMyCoordinateSystemInfo(); 
						}
						
					}});
	    		pmn.ShowDialog();
	    	}
		}};
		
    //�ص�
    private ICallback _selectCallback = null;
    public void SetCallback(ICallback cb){this._selectCallback = cb;}
		
    //����������ϵ�Ĳ���
	private HashMap<String,String> m_NewCoorSystemPara = null;
	public void SetNewCoorSystemPara(HashMap<String,String> _NewCoorSystemPara)
	{
		this.m_NewCoorSystemPara = _NewCoorSystemPara;
		//FillPromptInfo(_Dialog,this.m_NewCoorSystemPara);
	}

	
	private List<HashMap<String,Object>> m_MyParamList = new ArrayList<HashMap<String,Object>>();
    /**
     * �����ҵ�����ϵ�б���Ϣ
     */
    private void LoadMyCoordinateSystemInfo()
    {
    	//���ҵ�����ϵ�б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "�ҵ�����ϵ",new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("�б�ѡ��"))
				{
					final HashMap<String,String> selCoorPara = (HashMap<String,String>)ExtraStr;
					v1_project_mycoordinatesystem_select pms = new v1_project_mycoordinatesystem_select();
					pms.SetNewCoorSystemPara(selCoorPara);
					pms.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (Str.equals("ѡ���ҵ�����ϵ"))
							{
								Tools.ShowYesNoMessage(_Dialog.getContext(), "�Ƿ񸲸ǹ��̵�ǰ������ϵ��", new ICallback(){
									@Override
									public void OnClick(String Str,Object ExtraStr) {
										if (Str.equals("YES"))
										{
											if (_selectCallback!=null)_selectCallback.OnClick("��������ϵ",selCoorPara);
											_Dialog.dismiss();
										}
									}});
							}
						}});
					pms.ShowDialog();
				}
			}});
    	
    	//��ȡ�Ѿ�������Щ�ҵ�����ϵ
    	this.m_MyParamList = PubVar.m_DoEvent.m_UserConfigDB.GetMyCoodinateSystem().GetMyCoordinateSystemList();
    	hvf.BindDataToListView(this.m_MyParamList);
    }
    
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadMyCoordinateSystemInfo();}}
    	);
    	_Dialog.show();
    }
    
    /**
     * ���������Ϣ��ʾ�ı��������ҵ�����ϵnew,select��
     * @param _Dialog
     * @param MyCoorPara
     */
    public static void FillPromptInfo(Dialog _Dialog,HashMap<String,String> MyCoorPara)
    {
		if (MyCoorPara.containsKey("CoorSystem"))
		{
			_Dialog.findViewById(R.id.ll_coorsystem).setVisibility(View.VISIBLE);
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_coorsystem, MyCoorPara.get("CoorSystem"));
			if (MyCoorPara.get("CoorSystem").equals("WGS-84����"))
			{
				MyCoorPara.put("CenterJX", "");MyCoorPara.put("TransMethod","��");
				return;
			}
		}

		_Dialog.findViewById(R.id.ll_centerjx).setVisibility(View.VISIBLE);
		Tools.SetTextViewValueOnID(_Dialog, R.id.tv_centerjx, MyCoorPara.get("CenterJX"));

		_Dialog.findViewById(R.id.ll_transmethod).setVisibility(View.VISIBLE);
		Tools.SetTextViewValueOnID(_Dialog, R.id.tv_transmethod, MyCoorPara.get("TransMethod"));

		_Dialog.findViewById(R.id.ll_pmtransmethod).setVisibility(View.VISIBLE);
		Tools.SetTextViewValueOnID(_Dialog, R.id.tv_pmtransmethod, MyCoorPara.get("PMTransMethod"));
		
		String TransMethod = MyCoorPara.get("TransMethod");
		if (TransMethod.contains("����"))
		{
			MyCoorPara.put("TransParam", "Xƽ��(��)="+MyCoorPara.get("P31")+"\n"+
										"Yƽ��(��)="+MyCoorPara.get("P32")+"\n"+
										"Zƽ��(��)="+MyCoorPara.get("P33")+"\n"+
										"�����="+MyCoorPara.get("P34")+"\n"+
										"���ʲ�="+MyCoorPara.get("P35"));
		}
		if (TransMethod.contains("�߲�"))
		{
			MyCoorPara.put("TransParam", "Xƽ��(��)="+MyCoorPara.get("P71")+"\n"+
										"Yƽ��(��)="+MyCoorPara.get("P72")+"\n"+
										"Zƽ��(��)="+MyCoorPara.get("P73")+"\n"+
										"X��ת(��)="+MyCoorPara.get("P74")+"\n"+
										"Y��ת(��)="+MyCoorPara.get("P75")+"\n"+
										"Z��ת(��)="+MyCoorPara.get("P76")+"\n"+
										"�߶�����(ppm)="+MyCoorPara.get("P77"));
		}
		
		String PMTransMethod = MyCoorPara.get("PMTransMethod");
		if (PMTransMethod.contains("�Ĳ�"))
		{
			MyCoorPara.put("PMTransParam", "Xƽ��(��)="+MyCoorPara.get("P41")+"\n"+
										"Yƽ��(��)="+MyCoorPara.get("P42")+"\n"+
										"��ת�Ƕ�(��)="+MyCoorPara.get("P43")+"\n"+
										"�߶�����="+MyCoorPara.get("P44"));
		}

			
		if (MyCoorPara.containsKey("TransParam"))
		{
			_Dialog.findViewById(R.id.ll_transparam).setVisibility(View.VISIBLE);
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_transparam, MyCoorPara.get("TransParam"));
		}
		if (MyCoorPara.containsKey("PMTransParam"))
		{
			_Dialog.findViewById(R.id.ll_pmtransparam).setVisibility(View.VISIBLE);
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_pmtransparam, MyCoorPara.get("PMTransParam"));
		}
    }
}
