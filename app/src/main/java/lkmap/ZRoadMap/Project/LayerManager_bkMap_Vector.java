package lkmap.ZRoadMap.Project;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Enum.lkMapFileType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_MapFileAdapter;

public class LayerManager_bkMap_Vector
{
	private v1_FormTemplate _Dialog = null; 
    public LayerManager_bkMap_Vector()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.layermanager_bkmap_vector);
    	//_Dialog.ReSetSize(1f, 0.96f);
    	_Dialog.ReSetSize(0.5f, 0.8f);
    	//��������
    	String _ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
    	
    	//�ϲ����ܰ�ť�¼���
    	_Dialog.SetCaption("��"+_ProjectName+"��"+Tools.ToLocale("ʸ����ͼ�ļ�"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_project_clearbkmap+","+Tools.ToLocale("�޵�ͼ")+"  ,�޵�ͼ", pCallback);
    	
    	//ѡ�����и�ѡ��
    	((CheckBox)_Dialog.findViewById(R.id.cbselectall)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (m_MapFileList==null) return;
				for(HashMap<String,Object> hmObj:m_MapFileList)
				{
					hmObj.put("Select", arg1);
				}
				((v1_Project_Layer_BKMap_MapFileAdapter)(((ListView)_Dialog.findViewById(R.id.listView1))).getAdapter()).notifyDataSetChanged();
				//BindMapFileListToView(m_MapFileList);
			}});
    	
    	_Dialog.findViewById(R.id.btnXuanZeMulu).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				lkmap.ZRoadMap.ToolsBox.v1_selectdictionary sd = new lkmap.ZRoadMap.ToolsBox.v1_selectdictionary();
				sd.isSelectFolder = true;
				sd.SetCallback(new ICallback()
				{
					@Override
					public void OnClick(String Str, final Object ExtraStrT) 
					{
						List<String> folderList = (List<String>)ExtraStrT;
						for(String importFile:folderList)
						{
							List<HashMap<String,Object>> mapFileList = Tools.GetBKMapListFromFolder(importFile,lkMapFileType.enVector);
							AddMapFileList(mapFileList);
						}
						
				    	
					}
				});
				sd.ShowDialog(PubVar.m_SysAbsolutePath);
				
			}
    		
    	});
    	
    	//��ȡMapĿ¼�µ����е�һ����Ŀ¼
    	List<String> ValidSubPathList = new ArrayList<String>();
    	ValidSubPathList.add("��ǰ��ͼ");ValidSubPathList.add("��Ŀ¼");
    	String MapPath = PubVar.m_SysAbsolutePath+"/Map";
        File f = new File(MapPath);  
        File[] files = f.listFiles();
        for(File ff:files)if (ff.isDirectory())ValidSubPathList.add(ff.getName());
		
    	//�󶨵�ͼĿ¼
    	v1_DataBind.SetBindListSpinner(_Dialog, "��ͼĿ¼", ValidSubPathList, R.id.sp_subpath,new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if (ExtraStr.equals("��Ŀ¼"))ExtraStr="";
				QueryBKMapFileOnSpecialPath(ExtraStr+"");
			}});

    }
    
    private void AddMapFileList(List<HashMap<String,Object>> mapFileList)
    {
    	if(mapFileList != null)
    	{
    		this.m_MapFileList.addAll(mapFileList);
    		v1_Project_Layer_BKMap_MapFileAdapter adapter = new v1_Project_Layer_BKMap_MapFileAdapter(_Dialog.getContext(),m_MapFileList, 
				       R.layout.v1_bk_project_layer_bkmap_list, 
				       new String[] {"Select", "BKMapFile", "F1"}, 
				       new int[] {R.id.cbselect, R.id.tvname, R.id.tvcoorsystem});  
    		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
    	}
    	
    }
    
	//ѡ�е�ͼ������ȷ������ص�
	private ICallback m_Callback = null;
	
	/**
	 * ѡ�е�ͼ������ȷ������ص�
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
    //�ϲ���ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("�޵�ͼ"))
			{
				m_MapFileList.clear();
				pCallback.OnClick("ȷ��", "");
			}
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (m_Callback==null) _Dialog.dismiss();

	    		//ѡ�еı�����ͼ��
	    		if (m_MapFileList!=null)
	    		{
	    			List<HashMap<String,Object>> SelectMapFileList = new ArrayList<HashMap<String,Object>>();
	    			for(HashMap<String,Object> mapFile:m_MapFileList)
	    			{
	    				if (Boolean.parseBoolean(mapFile.get("Select")+""))
						{
	    					SelectMapFileList.add(mapFile);
						}
	    			}
	    	    	if(!PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().SaveBKLayer("ʸ��",SelectMapFileList))
	    	    	{
	    	    		Tools.ShowMessageBox("ʸ����ͼ����ʧ�ܣ�");
	    	    	}
	    	    	if (m_Callback!=null)m_Callback.OnClick("","");
	    			//m_Callback.OnClick("��"+MapFileNameList.size()+"��"+Tools.JoinT(",", MapFileNameList), SelectMapFileList);
	    		}
	    		_Dialog.dismiss();
	    	}
	    	
		}};
		
	
    /**
     * ����ָ��Ŀ¼�µ�ͼ�ļ��б���Ϣ
     */
	private void QueryBKMapFileOnSpecialPath(String SubPath)
	{
		if (SubPath.equals("��ǰ��ͼ"))
		{
			this.BindMapFileListToView(this.m_CurrentMapFileList);
			return;
		}
    	
    	//��ȡ�Ѿ�����Щ��ͼ�ļ�
    	List<HashMap<String,Object>> mapFileList = Tools.GetBKMapList(SubPath,lkMapFileType.enVector);
    	this.BindMapFileListToView(mapFileList);
	}
	
	//��ǰ��ͼ�б�
	private List<HashMap<String,Object>> m_CurrentMapFileList = null;
	
	//�µ�ͼ�б�
	private List<HashMap<String,Object>> m_MapFileList = null;
	
	/**
	 * �󶨵�ͼ�б�listView
	 * @param mapFileList
	 */
    private void BindMapFileListToView(List<HashMap<String,Object>> mapFileList)
    {
		//ˢ���б�
    	if (mapFileList==null)mapFileList = new ArrayList<HashMap<String,Object>>();
    	v1_Project_Layer_BKMap_MapFileAdapter adapter = new v1_Project_Layer_BKMap_MapFileAdapter(_Dialog.getContext(),mapFileList, 
											       R.layout.v1_bk_project_layer_bkmap_list, 
											       new String[] {"Select", "BKMapFile", "CoorSystem"}, 
											       new int[] {R.id.cbselect, R.id.tvname, R.id.tvcoorsystem});  
		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
		this.m_MapFileList = mapFileList;
    }

    //���õ�ͼ�ļ��б�
    public void SetMapFileList(List<HashMap<String,Object>> mapFileList)
    {
    	this.m_CurrentMapFileList = mapFileList;
    	this.m_MapFileList = mapFileList;
    }
    
    /**
     * ���ص�ͼ��Ϣ
     */
    private void LoadBKMapInfo()
    {
		//����ϵͳ
		CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
		String CoorSystemInfo = CS.GetName()+"��"+CS.GetCenterMeridian()+"��";
		if (CS.GetName().equals("WGS-84����"))CoorSystemInfo = CS.GetName();
    	Tools.SetTextViewValueOnID(_Dialog, R.id.et_prjInfo, CoorSystemInfo);
    	
    	//��ȡ��ǰ�������󶨵ı���ͼ
    	this.BindMapFileListToView(this.m_MapFileList);

    }
    
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadBKMapInfo();}});
    	_Dialog.show();
    }
}

