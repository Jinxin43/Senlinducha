package lkmap.ZRoadMap.Project;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import android.R.string;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.provider.Contacts.Intents.Insert;
import android.speech.tts.SynthesisRequest;
import android.util.Base64;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.LinearLayout;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkDatasetSourceType;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkRenderType;
import lkmap.Layer.GeoLayer;
import lkmap.Render.SimpleRender;
import lkmap.Render.UniqueValueRender;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import lkmap.ZRoadMap.MyControl.v1_LayerList_Adpter;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_VectorSet_Adpter;

public class v1_project_layer_manager {

	private v1_FormTemplate dialogView = null;
	private List<v1_Layer> copiedLayers = null;//拷贝的图层，用于编辑
	private List<HashMap<String,Object>> layerItemList = null;
	private v1_Layer m_SelectLayer = null;//当前在列表框内选中的图层
	private v1_LayerList_Adpter m_LayerList_Adpter = null;
	private v1_Project_Layer_BKMap_VectorSet_Adpter bkLayer_Adpter = null;
	private List<HashMap<String, Object>> gridBKLayerList = null;
	private List<HashMap<String, Object>> vectorBKLayerList = null;
	
	public v1_project_layer_manager(){
		
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.v1_project_layer_manage);
		dialogView.ReSetSize(0.8f, 0.9f,-153,0);
		
		dialogView.SetCaption(Tools.ToLocale("图层管理"));
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", btnCallback);
		dialogView.SetButtonInfo("2,"+R.drawable.v1_newlayer_shp+","+Tools.ToLocale("导入图层")+"  ,导入图层", btnCallback);
		dialogView.SetButtonInfo("3,"+R.drawable.v1_newlayer+","+Tools.ToLocale("新建图层")+"  ,新建图层", btnCallback);
		
		
		
		
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
	
	private void DoCommand(String strCommond)
	{
		//vectorlayer setting
    	if (strCommond.equals("矢量底图设置"))
    	{
    		this.dialogView.DoCommand("退出");
    		v1_project_layer_bkmap_vectorset plbv = new v1_project_layer_bkmap_vectorset();
    		plbv.ShowDialog();
    		return;
    	}
    	
    	//gridlayer setting
    	if (strCommond.equals("栅格底图设置"))
    	{
    		this.dialogView.DoCommand("退出");
    		v1_project_layer_bkmap_gridset plbv = new v1_project_layer_bkmap_gridset();
    		plbv.ShowDialog();
    		return;
    	}
    	
    	if (strCommond.equals("属性"))
    	{
    		v1_project_layer_new pln = new v1_project_layer_new();
    		pln.SetHaveLayerList(this.copiedLayers);
    		pln.SetEditLayer(this.m_SelectLayer);
    		pln.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					 ((v1_Layer)ExtraStr).CopyTo(m_SelectLayer);
				   	//更新显示列表
			    	for(HashMap<String,Object> lyr:layerItemList)
			    	{
			        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
			        	{
			        		lyr.put("D3", m_SelectLayer.GetLayerAliasName());        //图层名称
			        	}
			    	}
			    	m_LayerList_Adpter.notifyDataSetChanged();
			    	m_SelectLayer.SetEditMode(lkEditMode.enEdit);
					
				}}); 
    		pln.ShowDialog();
    		return;
    	}
    	
    	if (strCommond.equals("符号"))
		{
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
				    	for(HashMap<String,Object> lyr:layerItemList)
				    	{
				        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
				        	{
				        		lyr.put("D4", m_SelectLayer.GetSymbolFigure());        //图层符号指示图
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
    	
    	if (strCommond.equals("渲染"))
    	{
    		v1_project_layer_render plr = new v1_project_layer_render();
    		plr.SetEditLayer(this.m_SelectLayer);
    		plr.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
				   	//读取本工程的图层列表
			    	for(HashMap<String,Object> lyr:layerItemList)
			    	{
			        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
			        	{
			        		lyr.put("D4", m_SelectLayer.GetSymbolFigure());        //图层符号指示图
			        	}
			    	}
			    	m_LayerList_Adpter.notifyDataSetChanged();
			    	if (m_SelectLayer.GetEditMode()!=lkEditMode.enNew)
			    		m_SelectLayer.SetEditMode(lkEditMode.enEdit);
				}});
    		plr.ShowDialog();
    		return;
    	}
    	
    	if (strCommond.equals("向上") || strCommond.equals("向下"))
    	{
		   	//从图层列表中获取当前选中的绑定信息
    		int idx = 0;
    		HashMap<String,Object> moveObj = null;
    		for(int i=0;i<this.layerItemList.size();i++)
    		{
    			HashMap<String,Object> lyr = this.layerItemList.get(i);
	        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
	        	{
	        		moveObj = lyr;idx = i;
	        	}
    		}
    		
    		//最上、最下了
    		if (idx==0 && strCommond.toString().equals("向上")){Tools.ShowToast(dialogView.getContext(), "已经在最上层！"); return;}
    		if (idx==this.layerItemList.size()-1 && strCommond.toString().equals("向下")){Tools.ShowToast(dialogView.getContext(), "已经在最下层！"); return;}
    		if (strCommond.toString().equals("向上"))idx--;
    		if (strCommond.toString().equals("向下"))idx++;
    		
    		this.layerItemList.remove(moveObj);
    		this.layerItemList.add(idx, moveObj);
 
	    	this.copiedLayers.remove(this.m_SelectLayer);
	    	this.copiedLayers.add(idx, this.m_SelectLayer);
	    	
	   		this.m_LayerList_Adpter.notifyDataSetChanged();
    		this.GetSelectLayerByHO(moveObj);
    	}
    	
    	if (strCommond.equals("删除"))
    	{
    		Tools.ShowYesNoMessage(dialogView.getContext(), Tools.ToLocale("是否删除图层")+"【"+this.m_SelectLayer.GetLayerAliasName()+"】？", new ICallback(){

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("YES"))
					{
			    		for(int i=0;i<layerItemList.size();i++)
			    		{
			    			HashMap<String,Object> lyr = layerItemList.get(i);
				        	if (lyr.get("LayerID").equals(m_SelectLayer.GetLayerID()))
				        	{
				        		layerItemList.remove(lyr);
				        		m_LayerList_Adpter.notifyDataSetChanged();
				        		m_SelectLayer.SetEditMode(lkEditMode.enDelete);
				        		return;
				        	}
			    		}
					}
					
				}});
    	}
    	
    	//保存图层模板
    	if (strCommond.equals("存模板"))
    	{
    		List<v1_Layer> pLayerList = new ArrayList<v1_Layer>();
    		for(v1_Layer pLayer:this.copiedLayers){if (pLayer.GetEditMode()!=lkEditMode.enDelete)pLayerList.add(pLayer);}
    		if (pLayerList.size()==0){Tools.ShowMessageBox(dialogView.getContext(), "图层列表数量为0，无法保存模板！");return;}
    		v1_project_layer_savetemplate pls = new v1_project_layer_savetemplate();
    		pls.SetLayerList(pLayerList);
    		pls.ShowDialog();
    	}
    	
    	//调取图层模板
    	if (strCommond.equals("调模板"))
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
			    		if (copiedLayers.size()>0)
			    		{
			    			Tools.ShowYesNoMessage(dialogView.getContext(), "是否要清空现有图层列表，加载模板图层？", new ICallback(){
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
	
	private void UpdateLayerListByTemplate(List<v1_Layer> templateLayerList)
	{
		//将现有图层全部置为"delete"
		for(v1_Layer vLayer:copiedLayers)vLayer.SetEditMode(lkEditMode.enDelete);
		
		//将模板列表的图层置为"new"
		for(v1_Layer vLayer:templateLayerList)
		{	    	
			vLayer.SetEditMode(lkEditMode.enNew);
			copiedLayers.add(vLayer);
		}
		//重新加载图层列表
		LoadLayerInfo();  
	}
	
	private ICallback m_Callback = null;
	public void SetCallback(ICallback callBack)
	{
		this.m_Callback = callBack;
	}
	
	
	private void loadGridBKLayer()
	{
		if (this.gridBKLayerList == null)
		{
			this.gridBKLayerList = new ArrayList<HashMap<String,Object>>();
		}	
    	this.gridBKLayerList.clear();
    	
    	List<HashMap<String,Object>> bkGridList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList();
		for(HashMap<String,Object> ho :bkGridList)
		{
			HashMap<String,Object> feature = new HashMap<String,Object>();
			feature.put("UUID", UUID.randomUUID().toString());
			feature.put("BKMapFile", ho.get("BKMapFile"));
	        feature.put("D1", ho.get("Visible"));   //可见性
	        feature.put("D2", ho.get("BKMapFile"));
	        feature.put("D3", Integer.parseInt(ho.get("Transparent")+""));
	        
	        this.gridBKLayerList.add(feature);
		}
		if (this.bkLayer_Adpter==null)
			this.bkLayer_Adpter = new v1_Project_Layer_BKMap_VectorSet_Adpter(dialogView.getContext(),
					        			this.gridBKLayerList, R.layout.v1_bk_bkmap_gridset, 
					        			new String[] { "D1",  "D2","D3" }, 
					        			new int[] { R.id.rp_itemlayout1, R.id.rp_itemtext,R.id.rp_itemlayout2,R.id.bt_moveup,R.id.bt_movedown });

		this.bkLayer_Adpter.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				HashMap<String,Object> ho=(HashMap<String,Object>)ExtraStr;
				if (Str.equals("向上"))move(ho,-1);
				if (Str.equals("向下"))move(ho,1);
			}});
		
		 (((ListView)dialogView.findViewById(R.id.lvGridBKList))).setAdapter(this.bkLayer_Adpter);
	        this.bkLayer_Adpter.notifyDataSetChanged();
	}
	
	
	//向下，向上，移动一个位置
	private void move(HashMap<String,Object> HO,int Step)
	{
		//查找指定实体的位置
		int Pos = -1;
		for(int i=0;i<this.gridBKLayerList.size();i++)
		{
			HashMap<String,Object> ho = this.gridBKLayerList.get(i);
			if (ho.get("UUID").equals(HO.get("UUID")))Pos=i;
		}
		Pos+=Step;
		
		//最上、最下了
		if (Pos<0){Tools.ShowToast(dialogView.getContext(), "已经在最上层！"); return;}
		if (Pos>this.gridBKLayerList.size()-1){Tools.ShowToast(dialogView.getContext(), "已经在最下层！"); return;}
		
		this.gridBKLayerList.remove(HO);
		this.gridBKLayerList.add(Pos, HO);
		this.bkLayer_Adpter.notifyDataSetChanged();
		this.bkLayer_Adpter.SetSelectItemIndex(Pos);
	}
	
	private void loadVectorBKLayer()
	{
		//加载面层透明度设置
		this.vectorBKLayerList = new ArrayList<HashMap<String, Object>>();
		List<v1_Layer> layerList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetLayerList();
		for(v1_Layer pLayer :layerList)
		{
			if (pLayer.GetLayerType()==lkGeoLayerType.enPolygon)
			{
				HashMap<String,Object> feature = new HashMap<String,Object>();
				feature.put("LayerID", pLayer.GetLayerID());
		        feature.put("D1", pLayer.GetVisible());
		        feature.put("D2", pLayer.GetLayerAliasName());
		        feature.put("D3", pLayer.GetTransparet());
		        this.vectorBKLayerList.add(feature);
			}
		}
        v1_Project_Layer_BKMap_VectorSet_Adpter adapter = new v1_Project_Layer_BKMap_VectorSet_Adpter(dialogView.getContext(),
        																								this.vectorBKLayerList, 
        																								R.layout.v1_bk_bkmap_vectorset, 
        																								new String[] { "D1",  "D2","D3" }, 
        																								new int[] { R.id.rp_itemlayout1,
        																											R.id.rp_itemtext,
        																											R.id.rp_itemlayout2 });  
        (((ListView)dialogView.findViewById(R.id.lvVectorBKBKList))).setAdapter(adapter);
        
        //加载偏移量设置
        //Tools.SetTextViewValueOnID(dialogView, R.id.etOffsetX, PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetOffsetX()+"");
        //Tools.SetTextViewValueOnID(dialogView, R.id.etOffsetY, PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetOffsetY()+"");
	}
	
	private void LoadLayerInfo()
    {
    	//设置底图图层数量
//    	int VectorCount = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKFileList().size();
//    	Tools.SetTextViewValueOnID(this.dialogView, R.id.bt_vectorcount, VectorCount+"");
    	
//    	int GridCount = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList().size();
//    	Tools.SetTextViewValueOnID(this.dialogView, R.id.bt_gridcount, GridCount+"");
		loadGridBKLayer();
		loadVectorBKLayer();
		
    	//读取本工程的图层列表
    	if (this.layerItemList == null) this.layerItemList = new ArrayList<HashMap<String,Object>>();
    	this.layerItemList.clear();
    	for(v1_Layer lyr:this.copiedLayers)
    	{
    		if (lyr.GetEditMode()==lkEditMode.enDelete)continue;
        	HashMap<String,Object> hm = new HashMap<String,Object>(); 
        	hm.put("UUID", UUID.randomUUID().toString());    //唯一Id值
        	hm.put("LayerID", lyr.GetLayerID());		//图层ID，用于标识唯一图层
        	hm.put("D1", lyr.GetVisible());  			//可见性
        	
        	//图层类型的图片样式
        	int resourceId = R.drawable.v1_layertype_point;
        	if (lyr.GetLayerTypeName().equals("点"))resourceId = R.drawable.v1_layertype_point;
        	if (lyr.GetLayerTypeName().equals("线"))resourceId = R.drawable.v1_layertype_line;
        	if (lyr.GetLayerTypeName().equals("面"))resourceId = R.drawable.v1_layertype_poly;
        	hm.put("D2",Tools.GetBitmapByResources(resourceId));   //图层类型样式图片
        	hm.put("D3", lyr.GetLayerAliasName());  			   //图层名称
        	hm.put("D4", lyr.GetSymbolFigure());        		   //图层符号指示图
        	this.layerItemList.add(hm);
    	}

    	if (this.m_LayerList_Adpter==null)
    	{
    		this.m_LayerList_Adpter = new v1_LayerList_Adpter(dialogView.getContext(),
									this.layerItemList, 
									R.layout.v1_bk_selectlayer_item,
									new String[] { "D1","D2","D3","D4"}, 
									new int[] { R.id.cb_visible, R.id.iv_layertype,R.id.tv_layername,R.id.iv_symbol,
												R.id.iv_render,R.id.iv_feature,R.id.iv_moveup,R.id.iv_movedown,R.id.iv_delete}); 
    	}
		ListView lvList = (ListView)dialogView.findViewById(R.id.lvList);
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
    		
	    		//提取当前选中的图层
	    		HashMap<String,Object> selectObj = (HashMap<String,Object>)la.getItem(arg2);
	    		GetSelectLayerByHO(selectObj);
	    		
	    		if (m_Callback!=null)m_Callback.OnClick("", m_SelectLayer);
	    		dialogView.dismiss();
			}});
		
		//选中默认值
		for(HashMap<String,Object> ho:this.layerItemList)
		{
			if (ho.get("LayerID").equals(PubVar.m_DoEvent.m_GpsInfoManage.GetCurrentLayerId()))
			{
				GetSelectLayerByHO(ho);
			}
		}
    }
	
	private void GetSelectLayerByHO(HashMap<String,Object> ho)
    {
		//提取当前选中的图层
    	this.m_SelectLayer = null;
		HashMap<String,Object> selectObj = (HashMap<String,Object>)ho;
		String LayerID = selectObj.get("LayerID").toString();
    	for(v1_Layer lyr:this.copiedLayers)
    	{
    		if (lyr.GetLayerID().equals(LayerID))
			{
				this.m_SelectLayer = lyr;
			}
    	}
    	
    	for(int idx=0;idx<this.layerItemList.size();idx++)
    	{
    		HashMap<String,Object> hox = this.layerItemList.get(idx);
    		if (hox.get("UUID").equals(ho.get("UUID")))
    		{
    			ListView lvList = (ListView)dialogView.findViewById(R.id.lvList);
				v1_LayerList_Adpter la = (v1_LayerList_Adpter)lvList.getAdapter();
				la.SetSelectItemIndex(idx);
				la.notifyDataSetChanged();
    		}
    	}
    }
	
	//顶部按钮事件
	private ICallback btnCallback = new ICallback() {
		
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			// TODO Auto-generated method stub
			
			if(Str.equals("确定")){
				Tools.OpenDialog("正在保存图层设置...", new ICallback() {
					
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						// TODO Auto-generated method stu
			    		if (saveLayerInfo())
			    		{
			    			dialogView.dismiss();
			    		}
			    		if (m_Callback!=null)
			    		{
			    			m_Callback.OnClick("", m_SelectLayer);
			    		}
					}
				});
			}
		
	}
	};
	
	private boolean saveLayerInfo()
	{
		//先处理删除的图层
		int layerCount = this.copiedLayers.size();
		for(int i=layerCount-1;i>=0;i--)
		{
			v1_Layer layer = this.copiedLayers.get(i);
			if(layer.GetEditMode() == lkEditMode.enDelete)
			{
				String delSql = "delete from T_Layer where LayerId ='%1$s'";
				delSql = String.format(delSql, layer.GetLayerID());
				if(PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(delSql))
				{
					layer.SetEditMode(lkEditMode.enUnkonw);
					
					PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).Remove(layer.GetLayerID());
					boolean okay = PubVar.m_Workspace.GetDataSourceByEditing().RemoveDataset(layer.GetLayerID());
					if(okay)
					{
						this.copiedLayers.remove(i);
					}
				}
			}
		}
		
		//然后处理新增和修改
		List<String> sortLayerList = new ArrayList<String>();
		int sort = 0;
		
		for(v1_Layer layer:this.copiedLayers)
		{
			sortLayerList.add("when ID='"+layer.GetLayerID()+"' then '"+sort+"'");
			sort++;
			
			for(HashMap<String, Object> hashObj:this.layerItemList)
			{
				if(hashObj.get("LayerID").toString().equals(layer.GetLayerID()))
				{
					boolean visible = Boolean.parseBoolean(hashObj.get("D1").toString());
					layer.SetVisible(visible);
				}
			}
			createOrUpdateLayer(layer);
		}
		
		//save sort of layers
		String sortSql = "Update T_Layer Set SortId = case "+Tools.JoinT(" ", sortLayerList)+" end";
		PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sortSql);
		
		//save backgroud layer
		PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().SaveLayerFormLayerList(this.copiedLayers);
		
		for(int idx = 0;idx<this.copiedLayers.size();idx++)
		{
			v1_Layer layer = this.copiedLayers.get(idx);
			PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).MoveTo(layer.GetLayerID(), idx);
			PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).GetLayerById(layer.GetLayerID()).setVisible(layer.GetVisible());
		}
		
		saveVectorBKLayer();
		saveBKGridLayer();
		
		//refersh view
		PubVar.m_Map.Refresh();
		return true;
	}
	
	private boolean saveBKGridLayer()
	{
		for(int i=0;i<this.gridBKLayerList.size();i++)
		{
			HashMap<String,Object> hashMap = this.gridBKLayerList.get(i);
			//透明度
			int transparent = Integer.parseInt(hashMap.get("D3").toString());
			
			//可见性
			boolean visible = Boolean.parseBoolean(hashMap.get("D1").toString());
			
			String MapFileName = hashMap.get("BKMapFile")+"";
			
			List<HashMap<String,Object>> hoList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList();
			for(HashMap<String,Object> ho:hoList)
			{
				if (ho.get("BKMapFile").equals(MapFileName))
				{
					ho.put("Transparent", transparent);
					ho.put("Visible", visible);
					ho.put("Sort", i);
				}
			}
		}
		
		if (PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().SaveBKLayer())
		{
			PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().LoadBKLayer();
			PubVar.m_Map.GetGridLayers().GetList().clear();
			PubVar.m_Map.GetGridLayers().SetMapFileList(PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList());
			return true;
		}
		
		return false;
	}
	
	private void saveVectorBKLayer()
	{
		//保存面层透明度
		List<v1_Layer> layerList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetLayerList();
		for(v1_Layer pLayer :layerList)
		{
			if (pLayer.GetLayerType()==lkGeoLayerType.enPolygon)
			{
				for(HashMap<String,Object> hashMap :this.vectorBKLayerList)
				{
					if (pLayer.GetLayerID().equals(hashMap.get("LayerID")))
					{
						//透明度
						int transparent = Integer.parseInt(hashMap.get("D3").toString());
						pLayer.SetTransparent(transparent);
						
						//可见性
						boolean visible = Boolean.parseBoolean(hashMap.get("D1").toString());
						pLayer.SetVisible(visible);
						
						//更新图层内的实体符号
						GeoLayer pGeoLayer = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).GetLayerById(pLayer.GetLayerID());
						pGeoLayer.setVisible(visible);
						pLayer.SetVisible(visible);
						if (pGeoLayer.getRender().getType()==lkRenderType.enSimple)
						{
							((SimpleRender)pGeoLayer.getRender()).SetSymbolTransparent(transparent);
						}
						if (pGeoLayer.getRender().getType()==lkRenderType.enUniqueValue)
						{
							((UniqueValueRender)pGeoLayer.getRender()).SetSymbolTransparent(transparent);
						}
						
						//保存设置
						PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().SaveVectorLayerInfo(pLayer);
					}
				}
			}
		}
		
//		//保存偏移量设置
//		double offsetX = 0,offsetY = 0;
//		String offX = Tools.GetTextValueOnID(_Dialog, R.id.etOffsetX);
//		String offY = Tools.GetTextValueOnID(_Dialog, R.id.etOffsetY);
//		if (Tools.IsDouble(offX))offsetX = Double.parseDouble(offX);
//		if (Tools.IsDouble(offY))offsetY = Double.parseDouble(offY);
//		PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().SaveVectorOffset(offsetX, offsetY);
	}
	
	public static boolean createOrUpdateLayer(v1_Layer layer)
	{
		String sql = "1=1";
		//新增图层
		if(layer.GetEditMode() == lkEditMode.enNew)
		{
			sql = "Insert into T_Layer (Name,LayerId,Type,Visible,Transparent,VisibleScaleMin,VisibleScaleMax,LabelScaleMin,LableScaleMax,MinX,MinY,MaxX,MaxY,IfLabel,LabelField,LabelFont,FieldList,"+
										"Selectable,Editable,Snapable,RenderType,SimpleRender,UniqueValueList,UniqueValueList,UniqueSymbolList,UniqueDefaultSymbol) values"+
										"('%1$s', '%2$s','%3$s','%4$s','%5$s','%6$s','%7$s','%8$s','%9$s','%10$s','%11$s','%12$s','%13$s','%14$s','%15$s','%16$s','%17$s','%18$s',"+
										"'%19$s','%20$s','%21$s','%22$s','%23$s','%24$s','%25$s','%26$s')";
			sql= String.format(sql, layer.GetLayerAliasName(),layer.GetLayerID(),layer.GetLayerTypeName(),layer.GetTransparet(),layer.GetVisibleScaleMin(),layer.GetVisibleScaleMax(),
									layer.GetLabelScaleMin(),layer.GetLabelScaleMax(),layer.GetMinX(),layer.GetMinY(),layer.GetMaxX(),layer.GetMaxY(),layer.GetIfLabel(),
									layer.GetLabelDataFieldStr(),layer.GetLabelFont(),layer.GetFieldListJsonStr(),layer.GetSelectable(),layer.GetEditable(),layer.GetSnapable(),
									layer.GetRenderTypeInt(),layer.GetSimpleSymbol(),Tools.ListToJSONStr((List<String>)layer.GetUniqueSymbolInfoList().get("UniqueValueField")),
									Tools.ListToJSONStr((List<String>)layer.GetUniqueSymbolInfoList().get("UniqueValueList")),
									Tools.ListToJSONStr((List<String>)layer.GetUniqueSymbolInfoList().get("UniqueSymbolList")),
									layer.GetUniqueSymbolInfoList().get("UniqueValueField"));	
			if(PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sql))
			{
				layer.SetEditMode(lkEditMode.enUnkonw);
				
				//Create new Data Table
				if(PubVar.m_Workspace.GetDataSourceByEditing().CreateDataset(layer.GetLayerID()))
				{
					Dataset dataset = new Dataset(PubVar.m_Workspace.GetDataSourceByEditing());
					dataset.setSourceType(lkDatasetSourceType.enEditingData);
					dataset.setId(layer.GetLayerID());
					dataset.setType(layer.GetLayerType());
					dataset.GetMapCellIndex().setEnvelope(PubVar.m_Map.getFullExtend());
					PubVar.m_Workspace.GetDataSourceByEditing().getDatasets().add(dataset);
					return PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForAdd(layer);
				}
				
			}
		}
		
		if (layer.GetEditMode()==lkEditMode.enEdit)
		{
			//编辑
			sql = "update T_Layer set Name='%1$s',LayerId='%2$s',Type='%3$s',Visible='%4$s',Transparent='%5$s',VisibleScaleMin='%6$s',VisibleScaleMax='%7$s',"+
									 "IfLabel='%8$s',LabelField='%9$s',LabelFont='%10$s',FieldList='%11$s',Selectable='%12$s',Editable='%13$s',Snapable='%14$s',"+
									 "RenderType='%15$s',SimpleRender='%16$s',UniqueValueField='%17$s',UniqueValueList='%18$s',UniqueSymbolList='%19$s',UniqueDefaultSymbol='%20$s' where LayerId = '"+layer.GetLayerID()+"'";
			sql = String.format(sql, layer.GetLayerAliasName(),layer.GetLayerID(),layer.GetLayerTypeName(),layer.GetVisible(),
									 layer.GetTransparet(),layer.GetVisibleScaleMin(),layer.GetVisibleScaleMax(),layer.GetIfLabel(),
									 layer.GetLabelDataFieldStr(),layer.GetLabelFont(),layer.GetFieldListJsonStr(),layer.GetSelectable(),layer.GetEditable(),
									 layer.GetSnapable(),layer.GetRenderTypeInt(),layer.GetSimpleSymbol(),
									 Tools.ListToJSONStr((List<String>)layer.GetUniqueSymbolInfoList().get("UniqueValueField")),
									 Tools.ListToJSONStr((List<String>)layer.GetUniqueSymbolInfoList().get("UniqueValueList")),
									 Tools.ListToJSONStr((List<String>)layer.GetUniqueSymbolInfoList().get("UniqueSymbolList")),
									 layer.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol"));
    		if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sql))
    		{
    			layer.SetEditMode(lkEditMode.enUnkonw);
    			
    			//重新渲染图层
    			return PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForUpdate(layer);
    		}
		}
		
		return false;
	}
	
	public void ShowDialog()
    {
    	//先拷贝出图层列表，用于编辑操作
    	this.copiedLayers =  PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CopyLayerList();
    	
    	//此处这样做的目的是为了计算控件的尺寸
    	dialogView.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadLayerInfo();
			}});
      
    	 Window window = dialogView.getWindow();
    	 window.setWindowAnimations(R.style.DialogAnimation);
		 
    	 dialogView.show();
    }
	
}
