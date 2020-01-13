package lkmap.ZRoadMap.Project;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import dingtu.ZRoadMap.HashValueObject;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_select
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_select()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_select);
    	_Dialog.ReSetSize(0.5f,0.96f);
    	
    	//��ǰ���ڴ򿪵Ĺ���
    	_Dialog.findViewById(R.id.ll_currentproject).setVisibility(View.GONE);
    	HashValueObject HO = PubVar.m_HashMap.GetValueObject("Project");
    	if (HO!=null)
		{
    		_Dialog.findViewById(R.id.ll_currentproject).setVisibility(View.VISIBLE);
    		Tools.SetTextViewValueOnID(_Dialog, R.id.bt_currentproject, " "+HO.Value);
    		_Dialog.findViewById(R.id.bt_currentproject).setTag(HO.Value);
    		_Dialog.findViewById(R.id.bt_currentproject).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					//�򿪹������Դ���
					HashMap<String,String> prjInfo = new HashMap<String,String>();
					prjInfo.put("Name",arg0.getTag().toString());
					prjInfo.put("CreateTime","");
					v1_project_open po = new v1_project_open();
					po.SetProject(prjInfo);
					po.SetCallback(pCallback);
					po.ShowDialog();
					
				}});
		}
    	
    	_Dialog.SetCaption(Tools.ToLocale("���̹���"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_project_new+","+Tools.ToLocale("�½�")+" ,�½�����", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_deleteobject+","+Tools.ToLocale("ɾ��")+" ,ɾ������", pCallback);
    	//_Dialog.SetButtonInfo("1,"+R.drawable.v1_project_open+",��  ,�򿪹���", pCallback);
    	
    	//������������ת��
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText2));
    	//_Dialog.findViewById(R.id.tvl)
    }
    
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, final Object ExtraStr)
		{
	    	if (Str.equals("�½�����"))
	    	{
	    		v1_project_new vpn = new v1_project_new();
	    		vpn.SetNewProjectCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						LoadProjectListInfo();
					}});
	    		vpn.ShowDialog();
	    	}
	    	if (Str.equals("�򿪹���"))
	    	{
	    		Tools.OpenDialog(Tools.ToLocale("���ڴ򿪹���")+"...", new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr1) {
		    			if (!OpenProject(ExtraStr.toString())) return;
		    			_Dialog.dismiss();
					}});
	    		
	    		_Dialog.dismiss();
	    	}
	    	
	    	if (Str.equals("ɾ������"))
	    	{
	    		final List<String> delLayerNameList = new ArrayList<String>();
	       		//��ȡѡ�е�ͼ��
	    		for(HashMap<String,Object> hashObj:m_HeaderListViewDataItemList)
	    		{
	    			//��ѡ
    				boolean selected = Boolean.parseBoolean(hashObj.get("D1").toString());
    				String PrjName = hashObj.get("D2").toString();
    				if (selected)delLayerNameList.add(PrjName);
	    		}
	    		if (delLayerNameList.size()==0)
	    		{
	    			Tools.ShowMessageBox(_Dialog.getContext(),Tools.ToLocale("�빴ѡ��Ҫɾ���Ĺ���")+"��");
	    			return;
	    		}
	    		else
	    		{
	    			Tools.ShowYesNoMessage(_Dialog.getContext(), Tools.ToLocale("�Ƿ�ȷ��Ҫɾ�����¹���")+"��\r\n"+Tools.ToLocale("��������")+"��"+Tools.JoinT("\r\n"+Tools.ToLocale("��������")+"��", delLayerNameList), new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) 
						{
							if (Str.equals("YES"))
							{
 								String prjPath = PubVar.m_SysAbsolutePath+"/Data";
								for(String prjName:delLayerNameList)
								{
									File f = new File(prjPath+"/"+prjName);
									Tools.DeleteAll(f);
								}
								LoadProjectListInfo();
							}
						}});
	    		}
	    	}
		}
    };
    
	/**
	 * �򿪹���
	 * @param ProjectName �������ƣ�����·��
	 * @return
	 */
	private boolean OpenProject(String ProjectName)
	{
		//�ڴ��ж��Ƿ�����ٴδ��µĹ��̣�Ҳ������һ�ι����Ƿ������ڲɼ�������
		if (PubVar.m_DoEvent.CheckHasDataInTask("���ڲɼ������У���ֹͣ�ɼ�������ٳ����л����̣�")) return false;

		
		//������ǰ������Ŀ��Ϣ
		HashValueObject hvo = new HashValueObject();
		hvo.Value = ProjectName;
		PubVar.m_HashMap.Add("Project",hvo);
		
		//�򿪹���
		PubVar.m_DoEvent.m_ProjectDB.CloseProject();
		if (PubVar.m_DoEvent.m_ProjectDB.OpenProject(hvo.Value))  //ͼ���б��Ѿ��γ�
		{
			PubVar.m_DoEvent.DoCommand("����_��");
		}
		return true;
	}

	//�����б�󶨵�������
	private List<HashMap<String,Object>> m_HeaderListViewDataItemList = null;
	
    /**
     * ���ع����б���Ϣ
     */
    private void LoadProjectListInfo()
    {
    	//�󶨹����б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "�����б�",new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("�б�ѡ��"))
				{
					_Dialog.GetButton("1").setEnabled(true);
					
					//��ȡ����Ԥ��ͼ
					ShowDataPreview((HashMap<String,Object>)ExtraStr);
				}
			}});
    	
    	//��ȡ�Ѿ�������Щ����
    	List<String> ProjectList = Tools.GetProjectList();
    	
		//��ǰ���ڴ򿪵Ĺ��̣�������ɾ��
    	String CurrentProjectName = "";
		HashValueObject HO = PubVar.m_HashMap.GetValueObject("Project");
		if (HO!=null)CurrentProjectName = HO.Value;
		
    	//��������Ϣ�󶨵��б���
    	this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String,Object>>();
    	for(String Project:ProjectList)
    	{
    		if (CurrentProjectName.equals(Project.split(",")[0]))continue;
    		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
    		String date = sDateFormat.format(new java.util.Date(Long.parseLong(Project.split(",")[1]))); 
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", false);  //��ѡ��
        	hm.put("D2", Project.split(",")[0]);  //��������
        	hm.put("D3", date);    //����ʱ��
        	hm.put("D4", BitmapFactory.decodeResource(PubVar.m_DoEvent.m_Context.getResources(),R.drawable.v1_rightmore));    //��ϸ
        	this.m_HeaderListViewDataItemList.add(hm);
    	}
    	hvf.BindDataToListView(this.m_HeaderListViewDataItemList,new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {

				OpenDetailProjectInfo((HashMap<String,Object>)ExtraStr);
				//��ȡ����Ԥ��ͼ
				ShowDataPreview((HashMap<String,Object>)ExtraStr);
			}});
    }
    
    /**
     * ����ϸ�Ĺ�����Ϣ����
     * @param prjInfo
     */
    private void OpenDetailProjectInfo(HashMap<String,Object> prjInfo)
    {
		//�򿪹�����ϸ��Ϣ����
		HashMap<String,String> prjDetailInfo = new HashMap<String,String>();
		prjDetailInfo.put("Name",prjInfo.get("D2").toString());
		prjDetailInfo.put("CreateTime",prjInfo.get("D3").toString());
		v1_project_open po = new v1_project_open();
		po.SetProject(prjDetailInfo);
		po.SetCallback(pCallback);
		po.ShowDialog();
    }
    
    /**
     * �򿪹�������Ԥ��ͼ
     * @param prjInfo
     */
    private void ShowDataPreview(final HashMap<String,Object> prjInfo)
    {
    	//���ع��߰�ť
    	Button ibOpenProject = (Button)_Dialog.findViewById(R.id.bt_openproject);
    	ibOpenProject.setVisibility(View.VISIBLE);
    	ibOpenProject.setText(Tools.ToLocale(ibOpenProject.getText()+""));
    	
    	Button ibProjectDetail = (Button)_Dialog.findViewById(R.id.bt_projectinfo);
    	ibProjectDetail.setVisibility(View.VISIBLE);
    	ibProjectDetail.setText(Tools.ToLocale(ibProjectDetail.getText()+""));
    	
    	Button ibFullScreen = (Button)_Dialog.findViewById(R.id.bt_fullscreen);
    	ibFullScreen.setVisibility(View.INVISIBLE);
    	ibFullScreen.setText(Tools.ToLocale(ibFullScreen.getText()+""));
    	
    	//����Ԥ������ 
    	((TextView)_Dialog.findViewById(R.id.tvLocaleText2)).setText("��"+prjInfo.get("D2")+"��"+Tools.ToLocale("����Ԥ��ͼ"));
    	
    	//��������Ԥ��ͼ
    	ImageView iv = (ImageView)_Dialog.findViewById(R.id.iv_datapreview);iv.setEnabled(false);
    	iv.setImageBitmap(null);
    	final String DataPreviewImageName = PubVar.m_SysAbsolutePath+"/Data/"+prjInfo.get("D2")+"/DataPreview.jpg";
    	if (Tools.ExistFile(DataPreviewImageName))
    	{
	    	iv.setImageBitmap(BitmapFactory.decodeFile(DataPreviewImageName));iv.setEnabled(true);
	    	ibFullScreen.setVisibility(View.VISIBLE);
    	}
    	
    	//���Ԥ��ͼ����ʾ��ͼ
    	iv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				v1_project_select_datapreview psd = new v1_project_select_datapreview();
				psd.SetDataPreviewInfo(prjInfo, DataPreviewImageName);
				psd.SetCallback(pCallback);
				psd.ShowDialog();
			}});
    	
    	//��ʾ��ͼ
    	ibFullScreen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				v1_project_select_datapreview psd = new v1_project_select_datapreview();
				psd.SetDataPreviewInfo(prjInfo, DataPreviewImageName);
				psd.SetCallback(pCallback);
				psd.ShowDialog();
			}});
    	
    	//������ϸ��Ϣ
    	ibProjectDetail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				OpenDetailProjectInfo(prjInfo);
			}});
    	//�򿪹���
    	ibOpenProject.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				pCallback.OnClick("�򿪹���",prjInfo.get("D2"));
			}});    	
    }
    
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadProjectListInfo();
				
				//�ڴ˼����ϴδ򿪵Ĺ������ѹ��ܣ��������������ʱ��ʾ
				String beforePrjTag = "Tag_BeforeOpenProject";
				HashValueObject HO = PubVar.m_HashMap.GetValueObject(beforePrjTag);
				if (HO==null)
				{
					HO = PubVar.m_HashMap.GetValueObject(beforePrjTag,true);
					
					//��ȡ�ϴδ򿪵Ĺ�����Ϣ
					final HashMap<String,String> beforeOpenPrjInfo = PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().GetUserPara(beforePrjTag);
					if (beforeOpenPrjInfo!=null)
					{
						//����ϴδ򿪵Ĺ��������Ƿ���Ч�����ܴ����Ѿ�ɾ�������
						boolean PrjValid = false;
						for(HashMap<String,Object> ho:m_HeaderListViewDataItemList)
						{
							if (ho.get("D2").equals(beforeOpenPrjInfo.get("F2")))PrjValid=true;
						}
						if(PrjValid)
						{
							//��ʾ�Ƿ��ڴ�
							String Message = Tools.ToLocale("�Ƿ���ϴι���")+"��\r\n\r\n"+Tools.ToLocale("��������")+"��%1$s\r\n"+Tools.ToLocale("�ϴ�ʱ��")+"��%2$s";
							Message = String.format(Message, beforeOpenPrjInfo.get("F2"),beforeOpenPrjInfo.get("F3"));
							Tools.ShowYesNoMessage(_Dialog.getContext(),Message , new ICallback(){
								@Override
								public void OnClick(String Str, Object ExtraStr) 
								{
									Tools.OpenDialog(Tools.ToLocale("���ڴ򿪹���")+"...", new ICallback(){
										@Override
										public void OnClick(String Str,Object ExtraStr) {
											OpenProject(beforeOpenPrjInfo.get("F2"));
											_Dialog.dismiss();
										}});
								}});
						}
					}
				}
				if (m_HeaderListViewDataItemList.size()==0 && !PubVar.m_DoEvent.m_ProjectDB.AlwaysOpenProject())
				{
					Tools.ShowYesNoMessage(_Dialog.getContext(),Tools.ToLocale("��ǰû�д����κι��̣��Ƿ���Ҫ��������")+"��",new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) 
						{
							if (Str.equals("YES"))pCallback.OnClick("�½�����", "");
						}});
				}
			}}
    	
    	
    	);
    	_Dialog.show();
    }
}
