package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.ListView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkMapFileType;
import lkmap.Enum.lkRenderType;
import lkmap.Layer.GeoLayer;
import lkmap.Render.SimpleRender;
import lkmap.Render.UniqueValueRender;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_VectorSet_Adpter;

public class v1_project_layer_bkmap_vectorset
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer_bkmap_vectorset()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_bkmap_vectorset);
    	_Dialog.ReSetSize(1f, 0.96f);
    	
    	//工程名称
    	String _ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
    	
    	//上部功能按钮事件绑定
    	_Dialog.SetCaption(Tools.ToLocale("矢量底图管理"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_editlayer+","+Tools.ToLocale("底图管理")+"  ,底图管理", pCallback);
    	//_Dialog.GetButton("1").setEnabled(false);
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
	    		SaveVectorSet();
	    		_Dialog.dismiss();
	    		PubVar.m_Map.Refresh();
	    	}
	    	if (Str.equals("底图管理"))
	    	{
	    		v1_project_layer_bkmap plb = new v1_project_layer_bkmap();
	    		plb.SetBKMapType(lkMapFileType.enVector);
	    		plb.SetMapFileList(PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKFileList());
	    		plb.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						LoadVectorSet();
					}});
	    		plb.ShowDialog();
	    	}
	    	
	    	//底图文件列表选中后回调
	    	if (Str.equals("列表选项"))
	    	{

	    		_Dialog.GetButton("1").setEnabled(true);
	    	}
		}};
		
	private List<HashMap<String, Object>> m_LayerSetList = null;
	
	
	private void SaveVectorSet()
	{
		//保存面层透明度
		List<v1_Layer> layerList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetLayerList();
		for(v1_Layer pLayer :layerList)
		{
			if (pLayer.GetLayerType()==lkGeoLayerType.enPolygon)
			{
				for(HashMap<String,Object> hashMap :this.m_LayerSetList)
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
		
		//保存偏移量设置
		double offsetX = 0,offsetY = 0;
		String offX = Tools.GetTextValueOnID(_Dialog, R.id.etOffsetX);
		String offY = Tools.GetTextValueOnID(_Dialog, R.id.etOffsetY);
		if (Tools.IsDouble(offX))offsetX = Double.parseDouble(offX);
		if (Tools.IsDouble(offY))offsetY = Double.parseDouble(offY);
		PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().SaveVectorOffset(offsetX, offsetY);
	}
	/**
	 * 加载底图矢量图层设置
	 */
	private void LoadVectorSet()
	{
		//加载面层透明度设置
		this.m_LayerSetList = new ArrayList<HashMap<String, Object>>();
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
		        this.m_LayerSetList.add(feature);
			}
		}
        v1_Project_Layer_BKMap_VectorSet_Adpter adapter = new v1_Project_Layer_BKMap_VectorSet_Adpter(_Dialog.getContext(),this.m_LayerSetList, R.layout.v1_bk_bkmap_vectorset, new String[] { "D1",  "D2","D3" }, new int[] { R.id.rp_itemlayout1, R.id.rp_itemtext,R.id.rp_itemlayout2 });  
        (((ListView)_Dialog.findViewById(R.id.lvList))).setAdapter(adapter);
        
        //加载偏移量设置
        Tools.SetTextViewValueOnID(_Dialog, R.id.etOffsetX, PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetOffsetX()+"");
        Tools.SetTextViewValueOnID(_Dialog, R.id.etOffsetY, PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetOffsetY()+"");
	}
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadVectorSet();}});
    	_Dialog.show();
    }
    

}
