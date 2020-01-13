package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.widget.CheckBox;
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
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;

public class v1_project_layer
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer);
    	_Dialog.ReSetSize(1f, 0.96f);
    
    	//��������
    	String _ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
    	
    	//�ϲ����ܰ�ť�¼���
    	_Dialog.SetCaption("��"+_ProjectName+"��"+Tools.ToLocale("ͼ�����"));
    	//_Dialog.SetButtonInfo("2,"+R.drawable.v1_project_savetemplate+",����ģ��  ,����ģ��", pCallback);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	
    	//ͼ�㰴ť�¼���
    	_Dialog.findViewById(R.id.bt_vectorset).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.bt_gridset).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.pl_new).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.pl_edit).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.pl_up).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.pl_down).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.pl_delete).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.pl_render).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.pl_savetemplate).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.pl_loadtemplate).setOnClickListener(new ViewClick());
    	this.SetLayerButtonEnable(false);
    	this.BindBKLayerInfo();
    }
    
    /**
     * �󶨵�ͼ��Ϣ
     */
    private void BindBKLayerInfo()
    {
    	//��ͼ�ļ��б��
    	v1_SpinnerDialog vectorSD = (v1_SpinnerDialog)_Dialog.findViewById(R.id.sp_bkvector);
    	vectorSD.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
		    	//��ͼ�ļ��б�����Ļص�ֵ
		    	if (Str.equals("SpinnerCallback"))
		    	{
		    		v1_project_layer_bkmap plb = new v1_project_layer_bkmap();
		    		plb.SetBKMapType(lkMapFileType.enVector);
		    		Object obj = _Dialog.findViewById(R.id.sp_bkvector).getTag();
		    		plb.SetMapFileList((obj==null)?null:(List<HashMap<String,Object>>)obj);
		    		plb.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
					    		v1_DataBind.SetBindListSpinner(_Dialog, "", new String[]{Str}, R.id.sp_bkvector);
					    		_Dialog.findViewById(R.id.sp_bkvector).setTag(ExtraStr);
						}});
		    		plb.ShowDialog();
		    	}
			}});
    	

    	v1_SpinnerDialog gridSD = (v1_SpinnerDialog)_Dialog.findViewById(R.id.sp_bkgrid);
    	gridSD.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
		    	//��ͼ�ļ��б�����Ļص�ֵ
		    	if (Str.equals("SpinnerCallback"))
		    	{
		    		v1_project_layer_bkmap plb = new v1_project_layer_bkmap();
		    		plb.SetBKMapType(lkMapFileType.enGrid);
		    		Object obj = _Dialog.findViewById(R.id.sp_bkgrid).getTag();
		    		plb.SetMapFileList((obj==null)?null:(List<HashMap<String,Object>>)obj);
		    		plb.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
					    		v1_DataBind.SetBindListSpinner(_Dialog, "", new String[]{Str}, R.id.sp_bkgrid);
					    		_Dialog.findViewById(R.id.sp_bkgrid).setTag(ExtraStr);
						}});
		    		plb.ShowDialog();
		    	}
			}});
    	
    	v1_DataBind.SetBindListSpinner(_Dialog, "", new String[]{PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKFileListStr()}, R.id.sp_bkvector);
    	_Dialog.findViewById(R.id.sp_bkvector).setTag(PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKFileList());
    	
    	v1_DataBind.SetBindListSpinner(_Dialog, "", new String[]{PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileListStr()}, R.id.sp_bkgrid);
    	_Dialog.findViewById(R.id.sp_bkgrid).setTag(PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList());
    	
    	//��ͼ�ɼ���
    	((CheckBox)this._Dialog.findViewById(R.id.cb_bkvector)).setChecked(PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKVisible());
      	((CheckBox)this._Dialog.findViewById(R.id.cb_bkgrid)).setChecked(PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKVisible());
    }
    
    /**
     * ����ͼ�㰴ť�Ŀ�����
     * @param enable
     */
    private void SetLayerButtonEnable(boolean enabled)
    {
    	//_Dialog.findViewById(R.id.pl_new).setEnabled(enabled);
    	_Dialog.findViewById(R.id.pl_edit).setEnabled(enabled);
    	_Dialog.findViewById(R.id.pl_up).setEnabled(enabled);
    	_Dialog.findViewById(R.id.pl_down).setEnabled(enabled);
    	_Dialog.findViewById(R.id.pl_delete).setEnabled(enabled);
    	_Dialog.findViewById(R.id.pl_render).setEnabled(enabled);
    	
    	if (!enabled)m_SelectLayer = null;
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
    		if (!PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().AlwaysLoadDataSource())
    		{
    			Tools.ShowMessageBox("�����ȼ���ʸ����ͼ��");return;
    		}
    		v1_project_layer_bkmap_vectorset plbv = new v1_project_layer_bkmap_vectorset();
    		plbv.ShowDialog();
    		return;
    	}
    	
    	//դ���ͼ����
    	if (StrCommand.equals("դ���ͼ����"))
    	{
    		//�ж��Ƿ���դ���ͼ
    		if (PubVar.m_Map.GetGridLayers().GetList().size()==0)
    		{
    			Tools.ShowMessageBox("�����ȼ���դ���ͼ��");return;
    		}
    		v1_project_layer_bkmap_gridset plbv = new v1_project_layer_bkmap_gridset();
    		plbv.ShowDialog();
    		return;
    	}
    	
    	//�½�ͼ��
    	if (StrCommand.equals("�½�"))
    	{
    		v1_project_layer_new pln = new v1_project_layer_new();
    		pln.SetHaveLayerList(this.m_LayerList);
    		pln.SetCallback(new ICallback(){  //�½�ͼ�㣬�ص���־��ͼ�㣬Obj=��ͼ����
				@Override
				public void OnClick(String Str, Object ExtraStr) {
		    		v1_Layer newLayer = (v1_Layer)ExtraStr;
		    		m_LayerList.add(newLayer);
		    		LoadLayerInfo();  //���¼���ͼ���б�
				}});   

    		pln.ShowDialog();
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
			    	for(HashMap<String,Object> lyr:m_HeaderListViewDataItemList)
			    	{
			        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
			        	{
			        		lyr.put("D2", m_SelectLayer.GetLayerAliasName());        //ͼ������
			        	}
			    	}
			    	if (hvf!=null)hvf.notifyDataSetInvalidated();
			    	m_SelectLayer.SetEditMode(lkEditMode.enEdit);
					
				}}); 
    		pln.ShowDialog();
    		return;
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
			    	for(HashMap<String,Object> lyr:m_HeaderListViewDataItemList)
			    	{
			        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
			        	{
			        		lyr.put("D4", m_SelectLayer.GetSymbolFigure());        //ͼ�����ָʾͼ
			        	}
			    	}
			    	if (hvf!=null)hvf.notifyDataSetInvalidated();
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
    		for(int i=0;i<m_HeaderListViewDataItemList.size();i++)
    		{
    			HashMap<String,Object> lyr = m_HeaderListViewDataItemList.get(i);
	        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
	        	{
	        		moveObj = lyr;idx = i;
	        	}
    		}
    		
    		//���ϡ�������
    		if (idx==0 && StrCommand.toString().equals("����")){Tools.ShowToast(_Dialog.getContext(), "�Ѿ������ϲ㣡"); return;}
    		if (idx==m_HeaderListViewDataItemList.size()-1 && StrCommand.toString().equals("����")){Tools.ShowToast(_Dialog.getContext(), "�Ѿ������²㣡"); return;}
    		if (StrCommand.toString().equals("����"))idx--;
    		if (StrCommand.toString().equals("����"))idx++;
    		m_HeaderListViewDataItemList.remove(moveObj);
    		m_HeaderListViewDataItemList.add(idx, moveObj);
	    	if (hvf!=null)hvf.notifyDataSetInvalidated();
	    	
	    	this.m_LayerList.remove(this.m_SelectLayer);
	    	this.m_LayerList.add(idx, this.m_SelectLayer);
	    	hvf.SetSelectItemIndex(idx, pCallback);
    	}

    	if (StrCommand.equals("ɾ��"))
    	{
    		Tools.ShowYesNoMessage(_Dialog.getContext(), Tools.ToLocale("�Ƿ�ɾ��ͼ��")+"��"+this.m_SelectLayer.GetLayerAliasName()+"����", new ICallback(){

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("YES"))
					{
			    		for(int i=0;i<m_HeaderListViewDataItemList.size();i++)
			    		{
			    			HashMap<String,Object> lyr = m_HeaderListViewDataItemList.get(i);
				        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
				        	{
				        		m_HeaderListViewDataItemList.remove(lyr);
				        		hvf.notifyDataSetInvalidated();
				        		m_SelectLayer.SetEditMode(lkEditMode.enDelete);
				        		SetLayerButtonEnable(false);
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
					}});
	    	}
	    	

	    	
	    	//ͼ���б���ѡ�к�Ļص�
	    	if (Str.equals("�б�ѡ��"))
	    	{
	    		SetLayerButtonEnable(true);
	    		//��ȡ��ǰѡ�е�ͼ��
	    		HashMap<String,Object> selectObj = (HashMap<String,Object>)ExtraStr;
	    		String LayerID = selectObj.get("LayerID").toString();
	        	for(v1_Layer lyr:m_LayerList)
	        	{
	        		if (lyr.GetLayerID().equals(LayerID))m_SelectLayer = lyr;
	        	}
	    	}
		}};

	//��ǰ���б����ѡ�е�ͼ��
	private v1_Layer m_SelectLayer = null;
		
	//����������ͼ���У����ڱ༭����
	private List<v1_Layer> m_LayerList = null;
	
	//ͼ���б�󶨵�������
	private List<HashMap<String,Object>> m_HeaderListViewDataItemList = null;
	private v1_HeaderListViewFactory hvf = null;
	
	/**
	 * ͨ��ͼ��ģ����µ�ǰ��ͼ����ʾ�б�
	 * @param vLayerList
	 */
	private void UpdateLayerListByTemplate(List<v1_Layer> templateLayerList)
	{
		//������ͼ��ȫ����Ϊ"delete"
		m_HeaderListViewDataItemList.clear();
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
	
    /**
     * ����ͼ���б���Ϣ
     */
    private void LoadLayerInfo()
    {
    	//��ͼ���б�
    	hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_result1), "ͼ���б�",pCallback);
    	
    	//��ȡ�����̵�ͼ���б�
    	this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String,Object>>();
    	for(v1_Layer lyr:this.m_LayerList)
    	{
    		if (lyr.GetEditMode()==lkEditMode.enDelete)continue;
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("LayerID", lyr.GetLayerID());		//ͼ��ID�����ڱ�ʶάһͼ��
        	hm.put("D1", lyr.GetVisible());  			//�ɼ���
        	hm.put("D2", lyr.GetLayerAliasName());  			//ͼ������
        	hm.put("D3", Tools.ToLocale(lyr.GetLayerTypeName()));		//ͼ������
        	hm.put("D4", lyr.GetSymbolFigure());        //ͼ�����ָʾͼ
        	this.m_HeaderListViewDataItemList.add(hm);
    	}
    	hvf.BindDataToListView(this.m_HeaderListViewDataItemList,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				pCallback.OnClick(Str, ExtraStr);
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
					    	for(HashMap<String,Object> lyr:m_HeaderListViewDataItemList)
					    	{
					        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
					        	{
					        		lyr.put("D4", m_SelectLayer.GetSymbolFigure());        //ͼ�����ָʾͼ
					        	}
					    	}
					    	if (hvf!=null)hvf.notifyDataSetInvalidated();
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
			}});
    	
    	//������ͼ�㰴ť����༭֮���
    	this.SetLayerButtonEnable(false);
    }
    
    
    /**
     * ����ͼ���б�
     * @return
     */
    private boolean SaveLayerInfo()
    {
    	//�ɼ�����ͼ��
    	List<String> SortUpdateList = new ArrayList<String>();int Sort=0;
    	
    	//���ȴ���ɾ��ͼ������
    	int LayerCount = this.m_LayerList.size(); 
    	for(int i=LayerCount-1;i>=0;i--)
    	{
    		v1_Layer lyr = this.m_LayerList.get(i);
    		if (lyr.GetEditMode()==lkEditMode.enDelete)
    		{
    			//ɾ��
    			String SQL = "delete from T_Layer where ID = '%1$s'";
    			SQL = String.format(SQL, lyr.GetLayerID());
        		if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL))
        		{
        			lyr.SetEditMode(lkEditMode.enUnkonw);
        			
        			//������Ⱦͼ��
        			PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).Remove(lyr.GetLayerID());
        			PubVar.m_Workspace.GetDataSourceByEditing().RemoveDataset(lyr.GetLayerID());
        			this.m_LayerList.remove(i);
        		}
    		}
    	}
    	
    	//�ٴδ����������༭
    	for(v1_Layer lyr:this.m_LayerList)
    	{
    		SortUpdateList.add("when ID = '"+lyr.GetLayerID()+"' then '"+Sort+"'");Sort++;
    		
    		//����ͼ��Ŀɼ�������
    		for(HashMap<String,Object> hashObj:this.m_HeaderListViewDataItemList)
    		{
    			if (hashObj.get("LayerID").toString().equals(lyr.GetLayerID()))
				{
    				boolean visible = Boolean.parseBoolean(hashObj.get("D1").toString());
    				lyr.SetVisible(visible);
				}
    		}
    		
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
        	            pDataset.GetMapCellIndex().setEnvelope(PubVar.m_Map.getFullExtend());
        	            PubVar.m_Workspace.GetDataSourceByEditing().getDatasets().add(pDataset);
        	            PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForAdd(lyr);
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
        			PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForUpdate(lyr);
        			
//        			//�ж��Ƿ���Ҫ���±�ע��Ϣ
//        			v1_Layer OleLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(lyr.GetLayerID());
//        			if (!((OleLayer.GetIfLabel()==lyr.GetIfLabel()==true) && (OleLayer.GetLabelDataField().equals(lyr.GetLabelDataField()))))
//        			{
//        				//if (lyr.GetIfLabel()) PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForUpdateAllLabel(lyr);
//        			}
        		}
    		}
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
