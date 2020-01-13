package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import dingtu.ZRoadMap.HashValueObject;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkCoorTransMethod;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_open
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_open()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_open);
    	_Dialog.ReSetSize(0.5f,0.96f);
    	
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_editmycoordinatesystem+",�޸�����ϵ  ,�޸�����ϵ", pCallback);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_project_open+","+Tools.ToLocale("�򿪹���")+" ,�򿪹���", pCallback);
    	_Dialog.GetButton("2").setEnabled(false);
    	_Dialog.GetButton("2").setVisibility(View.INVISIBLE);  //�޸�����ϵ��ʱ����
    	
    	//�����л�����
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText2));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText3));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText4));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText5));
    }
    
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("�򿪹���"))
	    	{
	    		_Dialog.dismiss();
		    	if (m_Callback!=null)m_Callback.OnClick("�򿪹���", m_ProjectInfo.get("Name"));
	    	}
		}};

	//���õ�ǰ�Ĺ�����Ϣ
	private HashMap<String,String> m_ProjectInfo = null;
	/**
	 * ���õ�ǰ�Ĺ�����Ϣ
	 * @param _PrjInfo ��ʽ��Name=�������ƣ�CreateTime=����ʱ��
	 */
	public void SetProject(HashMap<String,String> _PrjInfo)
	{
		this.m_ProjectInfo = _PrjInfo;
		
		//�޸ı��� 
		_Dialog.SetCaption("��"+_PrjInfo.get("Name")+"��"+Tools.ToLocale("��ϸ��Ϣ"));
		
		//�жϵ�ǰ�Ĺ����Ƿ�Ϊ�򿪵�
		HashValueObject hvo = PubVar.m_HashMap.GetValueObject("Project");
		if (hvo==null)return;
		if (hvo.Value.equals(_PrjInfo.get("Name")))
		{
			//�Ѿ��򿪹���
			_Dialog.GetButton("1").setEnabled(false);
			_Dialog.GetButton("2").setEnabled(true);
		}
	}
	
	//�򿪹��̺�Ļص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
    /**
     * ���ص�ǰ������Ϣ
     */
    private void LoadProjectInfo()
    {
    	v1_ProjectDB pd = new v1_ProjectDB();
    	
    	if (!pd.OpenProject(this.m_ProjectInfo.get("Name"),false))return;
    	
    	//������Ϣ
    	CoorSystem CS = pd.GetProjectExplorer().GetCoorSystem();
    	HashMap<String,String> CoorSystemPara = new HashMap<String,String>();
    	CoorSystemPara.put("CoorSystem",CS.GetName());
    	CoorSystemPara.put("CenterJX",CS.GetCenterMeridian()+"");
    	CoorSystemPara.put("TransMethod",CS.GetCoorTransMethodName());
    	CoorSystemPara.put("PMTransMethod",CS.GetPMTransMethodName());
    	if (CS.GetCoorTransMethod()==lkCoorTransMethod.enThreePara)
    	{
    		CoorSystemPara.put("P31", CS.GetTransToP31()+"");
    		CoorSystemPara.put("P32", CS.GetTransToP32()+"");
    		CoorSystemPara.put("P33", CS.GetTransToP33()+"");
    		CoorSystemPara.put("P34", CS.GetTransToP34()+"");
    		CoorSystemPara.put("P35", Tools.ConvertToDigi(CS.GetTransToP35()+"",7));

    	}
    	if (CS.GetPMTransMethod()==lkCoorTransMethod.enFourPara)
    	{
    		CoorSystemPara.put("P41", CS.GetTransToP41()+"");
    		CoorSystemPara.put("P42", CS.GetTransToP42()+"");
    		CoorSystemPara.put("P43", CS.GetTransToP43()+"");
    		CoorSystemPara.put("P44", CS.GetTransToP44()+"");
    	}
    	if (CS.GetCoorTransMethod()==lkCoorTransMethod.enServenPara)
    	{
    		CoorSystemPara.put("P71", CS.GetTransToP71()+"");
    		CoorSystemPara.put("P72", CS.GetTransToP72()+"");
    		CoorSystemPara.put("P73", CS.GetTransToP73()+"");
    		CoorSystemPara.put("P74", CS.GetTransToP74()+"");
    		CoorSystemPara.put("P75", CS.GetTransToP75()+"");
    		CoorSystemPara.put("P76", CS.GetTransToP76()+"");
    		CoorSystemPara.put("P77", CS.GetTransToP77()+"");
    	}
    	
    	v1_project_mycoordinatesystem.FillPromptInfo(_Dialog,CoorSystemPara);
    	
    	//�󶨹���ͼ���б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "ͼ��ģ���б�");
    	
    	//������ͼ����Ϣ�󶨵��б���
    	List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
    	for(v1_Layer vLayer:pd.GetLayerExplorer().GetLayerList())
    	{
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", vLayer.GetLayerAliasName());
        	hm.put("D2", Tools.ToLocale(vLayer.GetLayerTypeName()));
        	
        	//��ȡ��������Ϣ
        	ASQLiteDatabase m_SQLiteDatabase = new ASQLiteDatabase();
			m_SQLiteDatabase.setDatabaseName(pd.GetProjectExplorer().GetProjectDataFileName());
        	SQLiteDataReader DR = m_SQLiteDatabase.Query("select count(*) from "+vLayer.GetDataTableName()+" where SYS_STATUS='0'");
        	DR.Read();hm.put("D3",DR.GetString(0));DR.Close();m_SQLiteDatabase.Close();
        	dataList.add(hm);
    	}
    	hvf.BindDataToListView(dataList);
    }
    
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadProjectInfo();}}
    	);
    	_Dialog.show();
    }
}
