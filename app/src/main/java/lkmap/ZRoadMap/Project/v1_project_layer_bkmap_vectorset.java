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
    	
    	//��������
    	String _ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
    	
    	//�ϲ����ܰ�ť�¼���
    	_Dialog.SetCaption(Tools.ToLocale("ʸ����ͼ����"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_editlayer+","+Tools.ToLocale("��ͼ����")+"  ,��ͼ����", pCallback);
    	//_Dialog.GetButton("1").setEnabled(false);
    }
    

	//ѡ�е�ͼ������ȷ������ص�
	private ICallback m_Callback = null;
	
	/**
	 * ѡ�е�ͼ������ȷ������ص�
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	

    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		SaveVectorSet();
	    		_Dialog.dismiss();
	    		PubVar.m_Map.Refresh();
	    	}
	    	if (Str.equals("��ͼ����"))
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
	    	
	    	//��ͼ�ļ��б�ѡ�к�ص�
	    	if (Str.equals("�б�ѡ��"))
	    	{

	    		_Dialog.GetButton("1").setEnabled(true);
	    	}
		}};
		
	private List<HashMap<String, Object>> m_LayerSetList = null;
	
	
	private void SaveVectorSet()
	{
		//�������͸����
		List<v1_Layer> layerList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetLayerList();
		for(v1_Layer pLayer :layerList)
		{
			if (pLayer.GetLayerType()==lkGeoLayerType.enPolygon)
			{
				for(HashMap<String,Object> hashMap :this.m_LayerSetList)
				{
					if (pLayer.GetLayerID().equals(hashMap.get("LayerID")))
					{
						//͸����
						int transparent = Integer.parseInt(hashMap.get("D3").toString());
						pLayer.SetTransparent(transparent);
						
						//�ɼ���
						boolean visible = Boolean.parseBoolean(hashMap.get("D1").toString());
						pLayer.SetVisible(visible);
						
						//����ͼ���ڵ�ʵ�����
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
						
						//��������
						PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().SaveVectorLayerInfo(pLayer);
					}
				}
			}
		}
		
		//����ƫ��������
		double offsetX = 0,offsetY = 0;
		String offX = Tools.GetTextValueOnID(_Dialog, R.id.etOffsetX);
		String offY = Tools.GetTextValueOnID(_Dialog, R.id.etOffsetY);
		if (Tools.IsDouble(offX))offsetX = Double.parseDouble(offX);
		if (Tools.IsDouble(offY))offsetY = Double.parseDouble(offY);
		PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().SaveVectorOffset(offsetX, offsetY);
	}
	/**
	 * ���ص�ͼʸ��ͼ������
	 */
	private void LoadVectorSet()
	{
		//�������͸��������
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
        
        //����ƫ��������
        Tools.SetTextViewValueOnID(_Dialog, R.id.etOffsetX, PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetOffsetX()+"");
        Tools.SetTextViewValueOnID(_Dialog, R.id.etOffsetY, PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetOffsetY()+"");
	}
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadVectorSet();}});
    	_Dialog.show();
    }
    

}
