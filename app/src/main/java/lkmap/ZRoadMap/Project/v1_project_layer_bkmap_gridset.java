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
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkMapFileType;
import lkmap.Enum.lkRenderType;
import lkmap.Layer.GeoLayer;
import lkmap.Layer.GridLayer;
import lkmap.Render.SimpleRender;
import lkmap.Render.UniqueValueRender;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_LayerList_Adpter;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_VectorSet_Adpter;

public class v1_project_layer_bkmap_gridset
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer_bkmap_gridset()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_bkmap_gridset);
    	_Dialog.ReSetSize(1f, 0.96f);
    	
    	//工程名称
    	String _ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
    	
    	//上部功能按钮事件绑定
    	_Dialog.SetCaption(Tools.ToLocale("栅格底图设置"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_editlayer+","+Tools.ToLocale("底图管理")+"  ,底图管理", pCallback);
    }
    

	//选中底图并按【确定】后回调
	private ICallback m_Callback = null;
	
	/**
	 * 选中底图并按【确定】后回调
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	

    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (!SaveGridSet())
	    		{
	    			Tools.ShowMessageBox(_Dialog.getContext(), "栅格图层设置保存失败！");
	    			return;
	    		}
	    		_Dialog.dismiss();
	    		PubVar.m_Map.Refresh();
	    	}
	    	
	    	if (Str.equals("底图管理"))
	    	{
	    		v1_project_layer_bkmap plb = new v1_project_layer_bkmap();
	    		plb.SetBKMapType(lkMapFileType.enGrid);
	    		plb.SetMapFileList(PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList());
	    		plb.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						LoadGridSet();
					}});
	    		plb.ShowDialog();
	    	}

		}};
		
	private boolean SaveGridSet()
	{
		for(int i=0;i<this.m_LayerSetList.size();i++)
		{
			HashMap<String,Object> hashMap = this.m_LayerSetList.get(i);
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
	
	private List<HashMap<String, Object>> m_LayerSetList = new ArrayList<HashMap<String, Object>>();
	private v1_Project_Layer_BKMap_VectorSet_Adpter m_Adpter = null;
	/**
	 * 加载底图栅格图层设置
	 */
	private void LoadGridSet()
	{
		this.m_LayerSetList.clear();
		List<HashMap<String,Object>> bkGridList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList();
		for(HashMap<String,Object> ho :bkGridList)
		{
			HashMap<String,Object> feature = new HashMap<String,Object>();
			feature.put("UUID", UUID.randomUUID().toString());
			feature.put("BKMapFile", ho.get("BKMapFile"));
	        feature.put("D1", ho.get("Visible"));   //可见性
	        feature.put("D2", ho.get("BKMapFile"));
	        feature.put("D3", Integer.parseInt(ho.get("Transparent")+""));
	        
	        this.m_LayerSetList.add(feature);
		}
		if (this.m_Adpter==null)
			this.m_Adpter = new v1_Project_Layer_BKMap_VectorSet_Adpter(_Dialog.getContext(),
					        			this.m_LayerSetList, R.layout.v1_bk_bkmap_gridset, 
					        			new String[] { "D1",  "D2","D3" }, 
					        			new int[] { R.id.rp_itemlayout1, R.id.rp_itemtext,R.id.rp_itemlayout2,R.id.bt_moveup,R.id.bt_movedown });

		this.m_Adpter.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				HashMap<String,Object> ho=(HashMap<String,Object>)ExtraStr;
				if (Str.equals("向上"))Move(ho,-1);
				if (Str.equals("向下"))Move(ho,1);
			}});

//		this.m_Adpter.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,long arg3) {
//				m_Adpter.SetSelectItemIndex(arg2);
//				m_Adpter.notifyDataSetChanged();
//			}});
        (((ListView)_Dialog.findViewById(R.id.lvList))).setAdapter(this.m_Adpter);
        this.m_Adpter.notifyDataSetChanged();
        
	}
	
	//向下，向上，移动一个位置
	private void Move(HashMap<String,Object> HO,int Step)
	{
		//查找指定实体的位置
		int Pos = -1;
		for(int i=0;i<this.m_LayerSetList.size();i++)
		{
			HashMap<String,Object> ho = this.m_LayerSetList.get(i);
			if (ho.get("UUID").equals(HO.get("UUID")))
				{
					Pos=i;
				}
		}
		Pos+=Step;
		
		//最上、最下了
		if (Pos<0){Tools.ShowToast(_Dialog.getContext(), "已经在最上层！"); return;}
		if (Pos>this.m_LayerSetList.size()-1){Tools.ShowToast(_Dialog.getContext(), "已经在最下层！"); return;}
		
		this.m_LayerSetList.remove(HO);
		this.m_LayerSetList.add(Pos, HO);
		this.m_Adpter.notifyDataSetChanged();
		this.m_Adpter.SetSelectItemIndex(Pos);
	}
	
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadGridSet();}});
    	_Dialog.show();
    }
    

}
