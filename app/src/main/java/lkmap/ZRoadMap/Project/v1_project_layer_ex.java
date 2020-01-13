package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkDatasetSourceType;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkMapFileType;
import lkmap.Enum.lkRenderType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import lkmap.ZRoadMap.DataImport.DataImport_Shp;
import lkmap.ZRoadMap.DataImport.DataImport_VMX;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.MyControl.v1_LayerList_Adpter;
import lkmap.ZRoadMap.MyControl.v1_ListEx_Adpter;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;

public class v1_project_layer_ex
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer_ex()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_ex);
    	_Dialog.ReSetSize(0.8f, 0.9f);
    
    	//��������
    	String _ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
    	
    	//�ϲ����ܰ�ť�¼���
    	_Dialog.SetCaption("��"+_ProjectName+"��"+Tools.ToLocale("ͼ�����"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_newlayer_shp+","+Tools.ToLocale("����ͼ��")+"  ,����ͼ��", pCallback);
    	_Dialog.SetButtonInfo("3,"+R.drawable.v1_newlayer+","+Tools.ToLocale("�½�ͼ��")+"  ,�½�ͼ��", pCallback);
    	
//    	//ͼ�㰴ť�¼���
    	_Dialog.findViewById(R.id.bt_vectorset).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.bt_gridset).setOnClickListener(new ViewClick());
    }
    
    
    class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		DoCommand(Tag);
    	}
    }
    //ͼ�����ť�¼�
    private void DoCommand(String StrCommand)
    {
    	//ʸ����ͼ����
    	if (StrCommand.equals("ʸ����ͼ����"))
    	{
    		//�ж��Ƿ���ʸ����ͼ
//    		if (!PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().AlwaysLoadDataSource())
//    		{
//    			Tools.ShowMessageBox("�����ȼ���ʸ����ͼ��");return;
//    		}
    		this._Dialog.DoCommand("�˳�");
    		v1_project_layer_bkmap_vectorset plbv = new v1_project_layer_bkmap_vectorset();
    		plbv.ShowDialog();
    		return;
    	}
    	
    	//դ���ͼ����
    	if (StrCommand.equals("դ���ͼ����"))
    	{
//    		//�ж��Ƿ���դ���ͼ
//    		if (PubVar.m_Map.GetGridLayers().GetList().size()==0)
//    		{
//    			Tools.ShowMessageBox("�����ȼ���դ���ͼ��");return;
//    		}
    		this._Dialog.DoCommand("�˳�");
    		v1_project_layer_bkmap_gridset plbv = new v1_project_layer_bkmap_gridset();
    		plbv.ShowDialog();
    		return;
    	}
    	
    	//�༭ͼ��
    	if (StrCommand.equals("����"))
    	{
    		v1_project_layer_new pln = new v1_project_layer_new();
    		pln.SetHaveLayerList(this.m_LayerList);
    		pln.SetEditLayer(this.m_SelectLayer);
    		pln.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					 ((v1_Layer)ExtraStr).CopyTo(m_SelectLayer);
				   	//������ʾ�б�
			    	for(HashMap<String,Object> lyr:m_LayerItemList)
			    	{
			        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
			        	{
			        		lyr.put("D3", m_SelectLayer.GetLayerAliasName());        //ͼ������
			        	}
			    	}
			    	m_LayerList_Adpter.notifyDataSetChanged();
			    	m_SelectLayer.SetEditMode(lkEditMode.enEdit);
					
				}}); 
    		pln.ShowDialog();
    		return;
    	}
    	
		if (StrCommand.equals("����"))
		{
			//����ֱ������ͼ����ŶԻ���
    		if (m_SelectLayer.GetRenderType()==lkRenderType.enSimple)
    		{
	    		v1_project_layer_render_symbolexplorer plrs = new v1_project_layer_render_symbolexplorer();
	    		plrs.SetGeoLayerType(m_SelectLayer.GetLayerType());
	    		plrs.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_SelectLayer.SetSimpleSymbol(((v1_SymbolObject)ExtraStr).SymbolBase64Str);
						
					   	//��ȡ�����̵�ͼ���б�
				    	for(HashMap<String,Object> lyr:m_LayerItemList)
				    	{
				        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
				        	{
				        		lyr.put("D4", m_SelectLayer.GetSymbolFigure());        //ͼ�����ָʾͼ
				        	}
				    	}
				    	m_LayerList_Adpter.notifyDataSetChanged();
				    	if (m_SelectLayer.GetEditMode()!=lkEditMode.enNew)
				    		m_SelectLayer.SetEditMode(lkEditMode.enEdit);
					}});
	    		v1_SymbolObject SO = new v1_SymbolObject();
	    		SO.SymbolBase64Str = m_SelectLayer.GetSimpleSymbol();
	    		SO.SymbolFigure = m_SelectLayer.GetSymbolFigure();
	    		plrs.SetDefaultSymbolObject(SO);
	    		plrs.ShowDialog();
    		}
    		if (m_SelectLayer.GetRenderType()==lkRenderType.enUniqueValue)
    		{
    			v1_project_layer_render_uniquevalue plru = new v1_project_layer_render_uniquevalue();
    			plru.SetEditLayer(m_SelectLayer);
    			plru.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
				    	if (m_SelectLayer.GetEditMode()!=lkEditMode.enNew)
				    		m_SelectLayer.SetEditMode(lkEditMode.enEdit);
					}});
    			plru.ShowDialog();
    		}
		}
    	
    	if (StrCommand.equals("��Ⱦ"))
    	{
    		v1_project_layer_render plr = new v1_project_layer_render();
    		plr.SetEditLayer(this.m_SelectLayer);
    		plr.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
				   	//��ȡ�����̵�ͼ���б�
			    	for(HashMap<String,Object> lyr:m_LayerItemList)
			    	{
			        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
			        	{
			        		lyr.put("D4", m_SelectLayer.GetSymbolFigure());        //ͼ�����ָʾͼ
			        	}
			    	}
			    	m_LayerList_Adpter.notifyDataSetChanged();
			    	if (m_SelectLayer.GetEditMode()!=lkEditMode.enNew)
			    		m_SelectLayer.SetEditMode(lkEditMode.enEdit);
				}});
    		plr.ShowDialog();
    		return;
    	}
    	
    	if (StrCommand.equals("����") || StrCommand.equals("����"))
    	{
		   	//��ͼ���б��л�ȡ��ǰѡ�еİ���Ϣ
    		int idx = 0;
    		HashMap<String,Object> moveObj = null;
    		for(int i=0;i<this.m_LayerItemList.size();i++)
    		{
    			HashMap<String,Object> lyr = this.m_LayerItemList.get(i);
	        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
	        	{
	        		moveObj = lyr;idx = i;
	        	}
    		}
    		
    		//���ϡ�������
    		if (idx==0 && StrCommand.toString().equals("����")){Tools.ShowToast(_Dialog.getContext(), "�Ѿ������ϲ㣡"); return;}
    		if (idx==this.m_LayerItemList.size()-1 && StrCommand.toString().equals("����")){Tools.ShowToast(_Dialog.getContext(), "�Ѿ������²㣡"); return;}
    		if (StrCommand.toString().equals("����"))idx--;
    		if (StrCommand.toString().equals("����"))idx++;
    		
    		this.m_LayerItemList.remove(moveObj);
    		this.m_LayerItemList.add(idx, moveObj);
 
	    	this.m_LayerList.remove(this.m_SelectLayer);
	    	this.m_LayerList.add(idx, this.m_SelectLayer);
	    	
	   		this.m_LayerList_Adpter.notifyDataSetChanged();
    		this.GetSelectLayerByHO(moveObj);
    	}

    	if (StrCommand.equals("ɾ��"))
    	{
    		Tools.ShowYesNoMessage(_Dialog.getContext(), Tools.ToLocale("�Ƿ�ɾ��ͼ��")+"��"+this.m_SelectLayer.GetLayerAliasName()+"����", new ICallback(){

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("YES"))
					{
			    		for(int i=0;i<m_LayerItemList.size();i++)
			    		{
			    			HashMap<String,Object> lyr = m_LayerItemList.get(i);
				        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
				        	{
				        		m_LayerItemList.remove(lyr);
				        		m_LayerList_Adpter.notifyDataSetChanged();
				        		m_SelectLayer.SetEditMode(lkEditMode.enDelete);
				        		return;
				        	}
			    		}
					}
					
				}});
    	}
    	
    	//����ͼ��ģ��
    	if (StrCommand.equals("��ģ��"))
    	{
    		List<v1_Layer> pLayerList = new ArrayList<v1_Layer>();
    		for(v1_Layer pLayer:this.m_LayerList){if (pLayer.GetEditMode()!=lkEditMode.enDelete)pLayerList.add(pLayer);}
    		if (pLayerList.size()==0){Tools.ShowMessageBox(_Dialog.getContext(), "ͼ���б�����Ϊ0���޷�����ģ�壡");return;}
    		v1_project_layer_savetemplate pls = new v1_project_layer_savetemplate();
    		pls.SetLayerList(pLayerList);
    		pls.ShowDialog();
    	}
    	
    	//��ȡͼ��ģ��
    	if (StrCommand.equals("��ģ��"))
    	{
    		v1_project_layer_loadtemplate pls = new v1_project_layer_loadtemplate();
    		pls.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
			    	//ͼ��ģ��ѡ�к�Ļص�
			    	if (Str.equals("ģ���б�"))
			    	{
			    		final List<v1_Layer> templateLayerList = (List<v1_Layer>)ExtraStr;
			    		if (m_LayerList.size()>0)
			    		{
			    			Tools.ShowYesNoMessage(_Dialog.getContext(), "�Ƿ�Ҫ�������ͼ���б�����ģ��ͼ�㣿", new ICallback(){
								@Override
								public void OnClick(String Str, Object ExtraStr) {
									if (Str.equals("YES"))UpdateLayerListByTemplate(templateLayerList);
								}});
			    		}  else UpdateLayerListByTemplate(templateLayerList);
			    		
			    		
			    	}
					
				}});
    		pls.ShowDialog();
    	}
    }
    
    //�ϲ���ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
				//�򿪹���ͼ�����
				lkmap.Tools.Tools.OpenDialog("���ڱ���ͼ������...",new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
			    		//����ͼ������
			    		if (SaveLayerInfo()) _Dialog.dismiss();
			    		if (m_Callback!=null)m_Callback.OnClick("", m_SelectLayer);
					}});
	    	}
	    	//����ͼ��
	    	if (Str.equals("����ͼ��"))
	    	{
	    		lkmap.ZRoadMap.ToolsBox.v1_selectdictionary sd = new lkmap.ZRoadMap.ToolsBox.v1_selectdictionary();
	    		sd.SetFileFilter(new String[]{"SHP","VMX"});
	    		sd.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, final Object ExtraStrT) {
						Tools.OpenDialog("���ڵ���ͼ��...", new ICallback(){
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								String StartTime = Tools.GetSystemDate();
								List<String> importFileList = (List<String>)ExtraStrT;
								for(String importFile:importFileList)
								{
									//�����ļ����Ͳ�ͬ���е�������
									String FileType = importFile.substring(importFile.length()-3, importFile.length());
									if (FileType.toUpperCase().equals("SHP"))
									{
							    		DataImport_Shp diSHP = new DataImport_Shp();
							    		v1_Layer pLayer = diSHP.CreateLayerByShp(importFile);
							    		if (pLayer!=null)m_LayerList.add(pLayer);
									}
									if (FileType.toUpperCase().equals("VMX"))
									{
							    		DataImport_VMX diVMX = new DataImport_VMX();
							    		List<v1_Layer> pLayerList = diVMX.CreateLayerByVMX(importFile);
							    		for(v1_Layer pLayer:pLayerList)m_LayerList.add(pLayer);
									}
								}
								String EndTime = Tools.GetSystemDate();
								Tools.ShowMessageBox(_Dialog.getContext(),"��ʼʱ�䣺"+StartTime+"\r\n����ʱ�䣺"+EndTime);
								//LoadLayerInfo();  //���¼���ͼ���б�
								
								
								
							}});

					}});
	    		sd.ShowDialog("");
	    		

	    	}
	    	
	    	//�½�ͼ��
	    	if (Str.equals("�½�ͼ��"))
	    	{
	    		v1_project_layer_new pln = new v1_project_layer_new();
	    		pln.SetHaveLayerList(m_LayerList);
	    		pln.SetCallback(new ICallback(){  //�½�ͼ�㣬�ص���־��ͼ�㣬Obj=��ͼ����
					@Override
					public void OnClick(String Str, Object ExtraStr) {
			    		v1_Layer newLayer = (v1_Layer)ExtraStr;
			    		if (CreateOrUpdateLayer(newLayer))
			    		{
			    			m_LayerList.add(newLayer);
			    			PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList().add(newLayer);
			    			LoadLayerInfo();  //���¼���ͼ���б�
			    		}
			    		
					}});

	    		pln.ShowDialog();
	    		return;
	    	}	    	
		}};

	//��ǰ���б����ѡ�е�ͼ��
	private v1_Layer m_SelectLayer = null;
		
	//����������ͼ���У����ڱ༭����
	private List<v1_Layer> m_LayerList = null;
	
	//ͼ���б�󶨵�������
	
	/**
	 * ͨ��ͼ��ģ����µ�ǰ��ͼ����ʾ�б�
	 * @param vLayerList
	 */
	private void UpdateLayerListByTemplate(List<v1_Layer> templateLayerList)
	{
		//������ͼ��ȫ����Ϊ"delete"
		for(v1_Layer vLayer:m_LayerList)vLayer.SetEditMode(lkEditMode.enDelete);
		
		//��ģ���б��ͼ����Ϊ"new"
		for(v1_Layer vLayer:templateLayerList)
		{	    	
			vLayer.SetEditMode(lkEditMode.enNew);
			m_LayerList.add(vLayer);
		}
		//���¼���ͼ���б�
		LoadLayerInfo();  
	}
	
	private ICallback m_Callback = null;
	public void SetCallback(ICallback callBack)
	{
		this.m_Callback = callBack;
	}
	
	private List<HashMap<String,Object>> m_LayerItemList = null;
	private v1_LayerList_Adpter m_LayerList_Adpter = null;
	
    /**
     * ����ͼ���б���Ϣ
     */
    private void LoadLayerInfo()
    {
    	//���õ�ͼͼ������
    	int VectorCount = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKFileList().size();
    	Tools.SetTextViewValueOnID(this._Dialog, R.id.bt_vectorcount, VectorCount+"");
    	
    	int GridCount = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList().size();
    	Tools.SetTextViewValueOnID(this._Dialog, R.id.bt_gridcount, GridCount+"");
    	
    	//��ȡ�����̵�ͼ���б�
    	if (this.m_LayerItemList == null) this.m_LayerItemList = new ArrayList<HashMap<String,Object>>();
    	this.m_LayerItemList.clear();
    	for(v1_Layer lyr:this.m_LayerList)
    	{
    		if (lyr.GetEditMode()==lkEditMode.enDelete)continue;
        	HashMap<String,Object> hm = new HashMap<String,Object>(); 
        	hm.put("UUID", UUID.randomUUID().toString());    //ΨһIdֵ
        	hm.put("LayerID", lyr.GetLayerID());		//ͼ��ID�����ڱ�ʶΨһͼ��
        	hm.put("D1", lyr.GetVisible());  			//�ɼ���
        	
        	//ͼ�����͵�ͼƬ��ʽ
        	int resourceId = R.drawable.v1_layertype_point;
        	if (lyr.GetLayerTypeName().equals("��"))resourceId = R.drawable.v1_layertype_point;
        	if (lyr.GetLayerTypeName().equals("��"))resourceId = R.drawable.v1_layertype_line;
        	if (lyr.GetLayerTypeName().equals("��"))resourceId = R.drawable.v1_layertype_poly;
        	hm.put("D2",Tools.GetBitmapByResources(resourceId));   //ͼ��������ʽͼƬ
        	hm.put("D3", lyr.GetLayerAliasName());  			   //ͼ������
        	hm.put("D4", lyr.GetSymbolFigure());        		   //ͼ�����ָʾͼ
        	this.m_LayerItemList.add(hm);
    	}

    	if (this.m_LayerList_Adpter==null)
    	{
    		this.m_LayerList_Adpter = new v1_LayerList_Adpter(_Dialog.getContext(),
									this.m_LayerItemList, 
									R.layout.v1_bk_selectlayer_item,
									new String[] { "D1","D2","D3","D4"}, 
									new int[] { R.id.cb_visible, R.id.iv_layertype,R.id.tv_layername,R.id.iv_symbol,
												R.id.iv_render,R.id.iv_feature,R.id.iv_moveup,R.id.iv_movedown,R.id.iv_delete}); 
    	}
		ListView lvList = (ListView)_Dialog.findViewById(R.id.lvList);
		lvList.setAdapter(this.m_LayerList_Adpter);
		this.m_LayerList_Adpter.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				GetSelectLayerByHO((HashMap<String,Object>)ExtraStr);
				DoCommand(Str);
			}});
		
		lvList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,long arg3) {
				ListView lvList = (ListView)arg0;
				v1_LayerList_Adpter la = (v1_LayerList_Adpter)lvList.getAdapter();
				la.SetSelectItemIndex(arg2);
				la.notifyDataSetChanged();
    		
	    		//��ȡ��ǰѡ�е�ͼ��
	    		HashMap<String,Object> selectObj = (HashMap<String,Object>)la.getItem(arg2);
	    		GetSelectLayerByHO(selectObj);
	    		
	    		if (m_Callback!=null)m_Callback.OnClick("", m_SelectLayer);
	    		_Dialog.dismiss();
			}});
		
		//ѡ��Ĭ��ֵ
		for(HashMap<String,Object> ho:this.m_LayerItemList)
		{
			if (ho.get("LayerID").equals(PubVar.m_DoEvent.m_GpsInfoManage.GetCurrentLayerId()))
			{
				GetSelectLayerByHO(ho);
			}
		}
    }
    
    //ѡ��ָ����ͼ��
    private void GetSelectLayerByHO(HashMap<String,Object> ho)
    {
		//��ȡ��ǰѡ�е�ͼ��
    	this.m_SelectLayer = null;
		HashMap<String,Object> selectObj = (HashMap<String,Object>)ho;
		String LayerID = selectObj.get("LayerID").toString();
    	for(v1_Layer lyr:this.m_LayerList)
    	{
    		if (lyr.GetLayerID().equals(LayerID))this.m_SelectLayer = lyr;
    	}
    	
    	for(int idx=0;idx<this.m_LayerItemList.size();idx++)
    	{
    		HashMap<String,Object> hox = this.m_LayerItemList.get(idx);
    		if (hox.get("UUID").equals(ho.get("UUID")))
    		{
    			ListView lvList = (ListView)_Dialog.findViewById(R.id.lvList);
				v1_LayerList_Adpter la = (v1_LayerList_Adpter)lvList.getAdapter();
				la.SetSelectItemIndex(idx);
				la.notifyDataSetChanged();
    		}
    	}
    }
    
    
    /**
     * ����ͼ���б�
     * @return
     */
    private boolean SaveLayerInfo()
    {
    	//�ɼ�����ͼ��
    	List<String> SortUpdateList = new ArrayList<String>();
    	
    	//���ȴ���ɾ��ͼ������
    	int LayerCount = this.m_LayerList.size(); 
    	for(int i=LayerCount-1;i>=0;i--)
    	{
    		v1_Layer lyr = this.m_LayerList.get(i);
    		if (lyr.GetEditMode()==lkEditMode.enDelete)
    		{
    			//ɾ��
    			String SQL = "delete from T_Layer where LayerId = '%1$s'";
    			SQL = String.format(SQL, lyr.GetLayerID());
        		if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL))
        		{
        			lyr.SetEditMode(lkEditMode.enUnkonw);
        			
        			//������Ⱦͼ��
        			PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).Remove(lyr.GetLayerID());
        			boolean OK = PubVar.m_Workspace.GetDataSourceByEditing().RemoveDataset(lyr.GetLayerID());
        			if (OK)this.m_LayerList.remove(i);
        		}
    		}
    	}
    	
    	//�ٴδ����������༭
    	int Sort=0;
    	for(v1_Layer lyr:this.m_LayerList)
    	{
    		SortUpdateList.add("when ID = '"+lyr.GetLayerID()+"' then '"+Sort+"'");Sort++;
    		
    		//����ͼ��Ŀɼ�������
    		for(HashMap<String,Object> hashObj:this.m_LayerItemList)
    		{
    			if (hashObj.get("LayerID").toString().equals(lyr.GetLayerID()))
				{
    				boolean visible = Boolean.parseBoolean(hashObj.get("D1").toString());
    				lyr.SetVisible(visible);
				}
    		}
    		CreateOrUpdateLayer(lyr);
    	}
    	
    	//����ͼ��ĵ�����˳������ 
    	String SortSQL = "update T_layer Set SortID = case "+Tools.JoinT(" ",SortUpdateList)+" end";
    	PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SortSQL);
    	
//    	//��ͼͼ��
//    	Object VectorBKFileList = _Dialog.findViewById(R.id.sp_bkvector).getTag();
//    	Object GridBKFileList = _Dialog.findViewById(R.id.sp_bkgrid).getTag();
//    	if (PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().SaveBKLayer(
//    					(VectorBKFileList!=null?(List<HashMap<String,Object>>)VectorBKFileList:null),
//    					(GridBKFileList!=null?(List<HashMap<String,Object>>)GridBKFileList:null)))
//    	{
//        	boolean bkVectorVisible = ((CheckBox)this._Dialog.findViewById(R.id.cb_bkvector)).isChecked();
//        	boolean bkGridVisible = ((CheckBox)this._Dialog.findViewById(R.id.cb_bkgrid)).isChecked();
//        	PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().SetBKVisible(bkVectorVisible);
//        	PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().SetBKVisible(bkGridVisible);
//    	}


    	//����ͼ���б�
    	PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().SaveLayerFormLayerList(this.m_LayerList);
    	
    	
    	//����ɼ�����ͼ������ϡ����¡��ɼ�������
    	for(int idx=0;idx<this.m_LayerList.size();idx++)
    	{
    		v1_Layer vLayer = this.m_LayerList.get(idx);
    		PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).MoveTo(vLayer.GetLayerID(), idx);
    		PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).GetLayerById(vLayer.GetLayerID()).setVisible(vLayer.GetVisible());
    	}
    	
    	//ˢ����ʾ
    	PubVar.m_Map.Refresh();
    	return true;
    }
    
    //���������ͼ��
    public static boolean CreateOrUpdateLayer(v1_Layer lyr)
    {
		String SQL = "1=1";
		if (lyr.GetEditMode()==lkEditMode.enNew)
		{
			//����
			SQL = "insert into T_Layer (Name,LayerId,Type,Visible,Transparent,VisibleScaleMin,VisibleScaleMax,LabelScaleMin,LabelScaleMax,MinX,MinY,MaxX,MaxY,IfLabel,LabelField,LabelFont,FieldList,"+
									   "Selectable,Editable,Snapable,RenderType,SimpleRender,UniqueValueField,UniqueValueList,UniqueSymbolList,UniqueDefaultSymbol) values " +
							   		  "('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s','%8$s','%9$s','%10$s','%11$s','%12$s','%13$s','%14$s','%15$s','%16$s','%17$s','%18$s','%19$s','%20$s',"+
							   		  "'%21$s','%22$s','%23$s','%24$s','%25$s','%26$s')";
							   		  		
			SQL = String.format(SQL, lyr.GetLayerAliasName(),lyr.GetLayerID(),lyr.GetLayerTypeName(),lyr.GetVisible(),
									 lyr.GetTransparet(),lyr.GetVisibleScaleMin(),lyr.GetVisibleScaleMax(),lyr.GetLabelScaleMin(),lyr.GetLabelScaleMax(),
									 lyr.GetMinX(),lyr.GetMinY(),lyr.GetMaxX(),lyr.GetMaxY(),lyr.GetIfLabel(),
									 lyr.GetLabelDataFieldStr(),lyr.GetLabelFont(),lyr.GetFieldListJsonStr(),lyr.GetSelectable(),lyr.GetEditable(),
									 lyr.GetSelectable(),lyr.GetRenderTypeInt(),lyr.GetSimpleSymbol(),
									 Tools.ListToJSONStr((List<String>)lyr.GetUniqueSymbolInfoList().get("UniqueValueField")),
									 Tools.ListToJSONStr((List<String>)lyr.GetUniqueSymbolInfoList().get("UniqueValueList")),
									 Tools.ListToJSONStr((List<String>)lyr.GetUniqueSymbolInfoList().get("UniqueSymbolList")),
									 lyr.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol"));
    		if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL))
    		{
    			lyr.SetEditMode(lkEditMode.enUnkonw);
    			
    			//�����µ����ݱ�
    			if (PubVar.m_Workspace.GetDataSourceByEditing().CreateDataset(lyr.GetLayerID()))
    			{
    	            Dataset pDataset = new Dataset(PubVar.m_Workspace.GetDataSourceByEditing()); 
    	            pDataset.setSourceType(lkDatasetSourceType.enEditingData);
    	            pDataset.setId(lyr.GetLayerID());
    	            pDataset.setType(lyr.GetLayerType());
    	            pDataset.setPorjectType(lyr.GetLayerProjecType());
    	            pDataset.GetMapCellIndex().setEnvelope(PubVar.m_Map.getFullExtend());
    	            PubVar.m_Workspace.GetDataSourceByEditing().getDatasets().add(pDataset);
    	            return PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForAdd(lyr);
    			}
    			
    		}
		}
		if (lyr.GetEditMode()==lkEditMode.enEdit)
		{
			//�༭
			SQL = "update T_Layer set Name='%1$s',LayerId='%2$s',Type='%3$s',Visible='%4$s',Transparent='%5$s',VisibleScaleMin='%6$s',VisibleScaleMax='%7$s',"+
									 "IfLabel='%8$s',LabelField='%9$s',LabelFont='%10$s',FieldList='%11$s',Selectable='%12$s',Editable='%13$s',Snapable='%14$s',"+
									 "RenderType='%15$s',SimpleRender='%16$s',UniqueValueField='%17$s',UniqueValueList='%18$s',UniqueSymbolList='%19$s',UniqueDefaultSymbol='%20$s' where LayerId = '"+lyr.GetLayerID()+"'";
			SQL = String.format(SQL, lyr.GetLayerAliasName(),lyr.GetLayerID(),lyr.GetLayerTypeName(),lyr.GetVisible(),
									 lyr.GetTransparet(),lyr.GetVisibleScaleMin(),lyr.GetVisibleScaleMax(),lyr.GetIfLabel(),
									 lyr.GetLabelDataFieldStr(),lyr.GetLabelFont(),lyr.GetFieldListJsonStr(),lyr.GetSelectable(),lyr.GetEditable(),
									 lyr.GetSelectable(),lyr.GetRenderTypeInt(),lyr.GetSimpleSymbol(),
									 Tools.ListToJSONStr((List<String>)lyr.GetUniqueSymbolInfoList().get("UniqueValueField")),
									 Tools.ListToJSONStr((List<String>)lyr.GetUniqueSymbolInfoList().get("UniqueValueList")),
									 Tools.ListToJSONStr((List<String>)lyr.GetUniqueSymbolInfoList().get("UniqueSymbolList")),
									 lyr.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol"));
    		if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL))
    		{
    			lyr.SetEditMode(lkEditMode.enUnkonw);
    			
    			//������Ⱦͼ��
    			return PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForUpdate(lyr);
    		}
		}
		return false;
    }
    
    public void ShowDialog()
    {
    	//�ȿ�����ͼ���б����ڱ༭����
    	this.m_LayerList =  PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CopyLayerList();
    	
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadLayerInfo();}});
    	_Dialog.show();
    }
    


}
