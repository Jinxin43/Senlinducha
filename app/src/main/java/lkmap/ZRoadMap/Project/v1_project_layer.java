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
    
    	//工程名称
    	String _ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
    	
    	//上部功能按钮事件绑定
    	_Dialog.SetCaption("【"+_ProjectName+"】"+Tools.ToLocale("图层管理"));
    	//_Dialog.SetButtonInfo("2,"+R.drawable.v1_project_savetemplate+",保存模板  ,保存模板", pCallback);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	
    	//图层按钮事件绑定
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
     * 绑定底图信息
     */
    private void BindBKLayerInfo()
    {
    	//底图文件列表绑定
    	v1_SpinnerDialog vectorSD = (v1_SpinnerDialog)_Dialog.findViewById(R.id.sp_bkvector);
    	vectorSD.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
		    	//底图文件列表关联的回调值
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
		    	//底图文件列表关联的回调值
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
    	
    	//底图可见性
    	((CheckBox)this._Dialog.findViewById(R.id.cb_bkvector)).setChecked(PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKVisible());
      	((CheckBox)this._Dialog.findViewById(R.id.cb_bkgrid)).setChecked(PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKVisible());
    }
    
    /**
     * 设置图层按钮的可用性
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
    //图层管理按钮事件
    private void DoCommand(String StrCommand)
    {
    	//矢量底图设置
    	if (StrCommand.equals("矢量底图设置"))
    	{
    		//判断是否有矢量底图
    		if (!PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().AlwaysLoadDataSource())
    		{
    			Tools.ShowMessageBox("请首先加载矢量底图！");return;
    		}
    		v1_project_layer_bkmap_vectorset plbv = new v1_project_layer_bkmap_vectorset();
    		plbv.ShowDialog();
    		return;
    	}
    	
    	//栅格底图设置
    	if (StrCommand.equals("栅格底图设置"))
    	{
    		//判断是否有栅格底图
    		if (PubVar.m_Map.GetGridLayers().GetList().size()==0)
    		{
    			Tools.ShowMessageBox("请首先加载栅格底图！");return;
    		}
    		v1_project_layer_bkmap_gridset plbv = new v1_project_layer_bkmap_gridset();
    		plbv.ShowDialog();
    		return;
    	}
    	
    	//新建图层
    	if (StrCommand.equals("新建"))
    	{
    		v1_project_layer_new pln = new v1_project_layer_new();
    		pln.SetHaveLayerList(this.m_LayerList);
    		pln.SetCallback(new ICallback(){  //新建图层，回调标志：图层，Obj=新图层类
				@Override
				public void OnClick(String Str, Object ExtraStr) {
		    		v1_Layer newLayer = (v1_Layer)ExtraStr;
		    		m_LayerList.add(newLayer);
		    		LoadLayerInfo();  //重新加载图层列表
				}});   

    		pln.ShowDialog();
    		return;
    	}
    	
    	//编辑图层
    	if (StrCommand.equals("属性"))
    	{
    		v1_project_layer_new pln = new v1_project_layer_new();
    		pln.SetHaveLayerList(this.m_LayerList);
    		pln.SetEditLayer(this.m_SelectLayer);
    		pln.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					 ((v1_Layer)ExtraStr).CopyTo(m_SelectLayer);
				   	//更新显示列表
			    	for(HashMap<String,Object> lyr:m_HeaderListViewDataItemList)
			    	{
			        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
			        	{
			        		lyr.put("D2", m_SelectLayer.GetLayerAliasName());        //图层名称
			        	}
			    	}
			    	if (hvf!=null)hvf.notifyDataSetInvalidated();
			    	m_SelectLayer.SetEditMode(lkEditMode.enEdit);
					
				}}); 
    		pln.ShowDialog();
    		return;
    	}
    	
    	if (StrCommand.equals("渲染"))
    	{
    		v1_project_layer_render plr = new v1_project_layer_render();
    		plr.SetEditLayer(this.m_SelectLayer);
    		plr.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
				   	//读取本工程的图层列表
			    	for(HashMap<String,Object> lyr:m_HeaderListViewDataItemList)
			    	{
			        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
			        	{
			        		lyr.put("D4", m_SelectLayer.GetSymbolFigure());        //图层符号指示图
			        	}
			    	}
			    	if (hvf!=null)hvf.notifyDataSetInvalidated();
			    	if (m_SelectLayer.GetEditMode()!=lkEditMode.enNew)
			    		m_SelectLayer.SetEditMode(lkEditMode.enEdit);
				}});
    		plr.ShowDialog();
    		return;
    	}
    	
    	if (StrCommand.equals("向上") || StrCommand.equals("向下"))
    	{
		   	//从图层列表中获取当前选中的绑定信息
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
    		
    		//最上、最下了
    		if (idx==0 && StrCommand.toString().equals("向上")){Tools.ShowToast(_Dialog.getContext(), "已经在最上层！"); return;}
    		if (idx==m_HeaderListViewDataItemList.size()-1 && StrCommand.toString().equals("向下")){Tools.ShowToast(_Dialog.getContext(), "已经在最下层！"); return;}
    		if (StrCommand.toString().equals("向上"))idx--;
    		if (StrCommand.toString().equals("向下"))idx++;
    		m_HeaderListViewDataItemList.remove(moveObj);
    		m_HeaderListViewDataItemList.add(idx, moveObj);
	    	if (hvf!=null)hvf.notifyDataSetInvalidated();
	    	
	    	this.m_LayerList.remove(this.m_SelectLayer);
	    	this.m_LayerList.add(idx, this.m_SelectLayer);
	    	hvf.SetSelectItemIndex(idx, pCallback);
    	}

    	if (StrCommand.equals("删除"))
    	{
    		Tools.ShowYesNoMessage(_Dialog.getContext(), Tools.ToLocale("是否删除图层")+"【"+this.m_SelectLayer.GetLayerAliasName()+"】？", new ICallback(){

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
    	
    	//保存图层模板
    	if (StrCommand.equals("存模板"))
    	{
    		List<v1_Layer> pLayerList = new ArrayList<v1_Layer>();
    		for(v1_Layer pLayer:this.m_LayerList){if (pLayer.GetEditMode()!=lkEditMode.enDelete)pLayerList.add(pLayer);}
    		if (pLayerList.size()==0){Tools.ShowMessageBox(_Dialog.getContext(), "图层列表数量为0，无法保存模板！");return;}
    		v1_project_layer_savetemplate pls = new v1_project_layer_savetemplate();
    		pls.SetLayerList(pLayerList);
    		pls.ShowDialog();
    	}
    	
    	//调取图层模板
    	if (StrCommand.equals("调模板"))
    	{
    		v1_project_layer_loadtemplate pls = new v1_project_layer_loadtemplate();
    		pls.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
			    	//图层模板选中后的回调
			    	if (Str.equals("模板列表"))
			    	{
			    		final List<v1_Layer> templateLayerList = (List<v1_Layer>)ExtraStr;
			    		if (m_LayerList.size()>0)
			    		{
			    			Tools.ShowYesNoMessage(_Dialog.getContext(), "是否要清空现有图层列表，加载模板图层？", new ICallback(){
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
    
    //上部按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
				//打开工程图层管理
				lkmap.Tools.Tools.OpenDialog("正在保存图层设置...",new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
			    		//保存图层设置
			    		if (SaveLayerInfo()) _Dialog.dismiss();
					}});
	    	}
	    	

	    	
	    	//图层列表项选中后的回调
	    	if (Str.equals("列表选项"))
	    	{
	    		SetLayerButtonEnable(true);
	    		//提取当前选中的图层
	    		HashMap<String,Object> selectObj = (HashMap<String,Object>)ExtraStr;
	    		String LayerID = selectObj.get("LayerID").toString();
	        	for(v1_Layer lyr:m_LayerList)
	        	{
	        		if (lyr.GetLayerID().equals(LayerID))m_SelectLayer = lyr;
	        	}
	    	}
		}};

	//当前在列表框内选中的图层
	private v1_Layer m_SelectLayer = null;
		
	//拷贝出来的图层列，用于编辑操作
	private List<v1_Layer> m_LayerList = null;
	
	//图层列表绑定的数据项
	private List<HashMap<String,Object>> m_HeaderListViewDataItemList = null;
	private v1_HeaderListViewFactory hvf = null;
	
	/**
	 * 通过图层模板更新当前的图层显示列表
	 * @param vLayerList
	 */
	private void UpdateLayerListByTemplate(List<v1_Layer> templateLayerList)
	{
		//将现有图层全部置为"delete"
		m_HeaderListViewDataItemList.clear();
		for(v1_Layer vLayer:m_LayerList)vLayer.SetEditMode(lkEditMode.enDelete);
		
		//将模板列表的图层置为"new"
		for(v1_Layer vLayer:templateLayerList)
		{	    	
			vLayer.SetEditMode(lkEditMode.enNew);
			m_LayerList.add(vLayer);
		}
		//重新加载图层列表
		LoadLayerInfo();  
	}
	
    /**
     * 加载图层列表信息
     */
    private void LoadLayerInfo()
    {
    	//绑定图层列表
    	hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_result1), "图层列表",pCallback);
    	
    	//读取本工程的图层列表
    	this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String,Object>>();
    	for(v1_Layer lyr:this.m_LayerList)
    	{
    		if (lyr.GetEditMode()==lkEditMode.enDelete)continue;
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("LayerID", lyr.GetLayerID());		//图层ID，用于标识维一图层
        	hm.put("D1", lyr.GetVisible());  			//可见性
        	hm.put("D2", lyr.GetLayerAliasName());  			//图层名称
        	hm.put("D3", Tools.ToLocale(lyr.GetLayerTypeName()));		//图层类型
        	hm.put("D4", lyr.GetSymbolFigure());        //图层符号指示图
        	this.m_HeaderListViewDataItemList.add(hm);
    	}
    	hvf.BindDataToListView(this.m_HeaderListViewDataItemList,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				pCallback.OnClick(Str, ExtraStr);
				//弹出直接设置图层符号对话框
	    		if (m_SelectLayer.GetRenderType()==lkRenderType.enSimple)
	    		{
		    		v1_project_layer_render_symbolexplorer plrs = new v1_project_layer_render_symbolexplorer();
		    		plrs.SetGeoLayerType(m_SelectLayer.GetLayerType());
		    		plrs.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							m_SelectLayer.SetSimpleSymbol(((v1_SymbolObject)ExtraStr).SymbolBase64Str);
							
						   	//读取本工程的图层列表
					    	for(HashMap<String,Object> lyr:m_HeaderListViewDataItemList)
					    	{
					        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
					        	{
					        		lyr.put("D4", m_SelectLayer.GetSymbolFigure());        //图层符号指示图
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
    	
    	//不可用图层按钮，如编辑之类的
    	this.SetLayerButtonEnable(false);
    }
    
    
    /**
     * 保存图层列表
     * @return
     */
    private boolean SaveLayerInfo()
    {
    	//采集数据图层
    	List<String> SortUpdateList = new ArrayList<String>();int Sort=0;
    	
    	//首先处理删除图层的情况
    	int LayerCount = this.m_LayerList.size(); 
    	for(int i=LayerCount-1;i>=0;i--)
    	{
    		v1_Layer lyr = this.m_LayerList.get(i);
    		if (lyr.GetEditMode()==lkEditMode.enDelete)
    		{
    			//删除
    			String SQL = "delete from T_Layer where ID = '%1$s'";
    			SQL = String.format(SQL, lyr.GetLayerID());
        		if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL))
        		{
        			lyr.SetEditMode(lkEditMode.enUnkonw);
        			
        			//重新渲染图层
        			PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).Remove(lyr.GetLayerID());
        			PubVar.m_Workspace.GetDataSourceByEditing().RemoveDataset(lyr.GetLayerID());
        			this.m_LayerList.remove(i);
        		}
    		}
    	}
    	
    	//再次处理新增、编辑
    	for(v1_Layer lyr:this.m_LayerList)
    	{
    		SortUpdateList.add("when ID = '"+lyr.GetLayerID()+"' then '"+Sort+"'");Sort++;
    		
    		//处理图层的可见性问题
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
    			//新增
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
        			
        			//创建新的数据表
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
    			//编辑
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
        			
        			//重新渲染图层
        			PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForUpdate(lyr);
        			
//        			//判断是否需要更新标注信息
//        			v1_Layer OleLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(lyr.GetLayerID());
//        			if (!((OleLayer.GetIfLabel()==lyr.GetIfLabel()==true) && (OleLayer.GetLabelDataField().equals(lyr.GetLabelDataField()))))
//        			{
//        				//if (lyr.GetIfLabel()) PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForUpdateAllLabel(lyr);
//        			}
        		}
    		}
    	}
    	
    	//保存图层的调整后顺序问题 
    	String SortSQL = "update T_layer Set SortID = case "+Tools.JoinT(" ",SortUpdateList)+" end";
    	PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SortSQL);
    	
//    	//底图图层
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


    	//整理图层列表
    	PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().SaveLayerFormLayerList(this.m_LayerList);
    	
    	
    	//整理采集数据图层的向上、向下、可见性问题
    	for(int idx=0;idx<this.m_LayerList.size();idx++)
    	{
    		v1_Layer vLayer = this.m_LayerList.get(idx);
    		PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).MoveTo(vLayer.GetLayerID(), idx);
    		PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).GetLayerById(vLayer.GetLayerID()).setVisible(vLayer.GetVisible());
    	}
    	
    	//刷新显示
    	PubVar.m_Map.Refresh();
    	return true;
    }
    
    public void ShowDialog()
    {
    	//先拷贝出图层列表，用于编辑操作
    	this.m_LayerList =  PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CopyLayerList();
    	
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadLayerInfo();}});
    	_Dialog.show();
    }
    


}
