package lkmap.ZRoadMap.ToolsBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.ListView;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.FieldInfo;
import lkmap.Enum.lkDatasetSourceType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.Layer.GeoLayers;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.HashMapEx;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_MapFileAdapter;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class v1_poly_analysis_set_addfilter
{
	private v1_FormTemplate _Dialog = null; 

    public v1_poly_analysis_set_addfilter()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_poly_analysis_set_addfilter);
    	_Dialog.ReSetSize(0.6f,0.56f);
    	
    	//���ñ���
    	_Dialog.SetCaption(Tools.ToLocale("����ͳ��ѡ��"));
    	
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+" ,ȷ��", pCallback);
    	
    	CheckBox cb = (CheckBox)_Dialog.findViewById(R.id.cbselectall);
    	cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				for(HashMap<String,Object> hm:m_FieldList)hm.put("Select",arg1);
		    	((v1_Project_Layer_BKMap_MapFileAdapter)(((ListView)_Dialog.findViewById(R.id.listView1))).getAdapter()).notifyDataSetChanged();
			}});
    }
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		HashMap<String,Object> OptItem = new HashMap<String,Object>();
	    		Object obj = _Dialog.findViewById(R.id.sp_layerlist).getTag();
	    		if (obj==null)
	    		{
	    			Tools.ShowMessageBox(_Dialog.getContext(), "��ѡ��ͳ��ͼ�㣡");
	    			return;
	    		} 
	    		else
	    		{
	    			HashMapEx hme = (HashMapEx)obj;
	    			OptItem.put("LayerName", hme.get("LayerName")+"");
	    			OptItem.put("LayerId", hme.get("LayerId")+"");
	    		}
	    		
	    		List<String> FieldNameList = new ArrayList<String>();
	    		List<String> FieldCaptionList = new ArrayList<String>();
	    		for(HashMap<String,Object> hm:m_FieldList)
	    		{
	    			if (Boolean.parseBoolean(hm.get("Select")+""))
	    			{
	    				FieldNameList.add(hm.get("FieldName")+"");
	    				FieldCaptionList.add(hm.get("FieldCaption")+"");
	    			}
	    		}
	    		OptItem.put("Select", true);
	    		OptItem.put("FieldNameList", FieldNameList);
	    		OptItem.put("FieldCaptionList", FieldCaptionList);
	    		OptItem.put("FieldCaptionListStr", Tools.JoinT(",", FieldCaptionList));
		    	if (m_Callback!=null)m_Callback.OnClick("����", OptItem);
	    		_Dialog.dismiss();

	    	}
		}};
		
	//�ص�
	private ICallback m_Callback = null;
	
	/**
	 * �ص�
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		

    /**
     * ����ͼ���б�
     */
	private void LoadLayerList()
	{
		List<HashMapEx> hmLayerList = new ArrayList<HashMapEx>();
		GeoLayers pGeoLayers = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll);
		for(GeoLayer pGeoLayer:pGeoLayers.getList())
		{
			if (pGeoLayer.getType()!=lkGeoLayerType.enPolygon)continue;
			String LayerType = "�����ݡ�";
			if (pGeoLayer.getDataset().getSourceType()==lkDatasetSourceType.enBackgroundData)LayerType = "����ͼ��";
			HashMapEx hme = new HashMapEx();
			hme.put("LayerId", pGeoLayer.getId());
			hme.put("LayerName", pGeoLayer.GetAliasName());
			hme.put("D1",LayerType+pGeoLayer.GetAliasName());
			hmLayerList.add(hme);
		}
		
		v1_DataBind.SetBindListSpinnerByHashMap(_Dialog, "ѡ��ͼ��", hmLayerList, R.id.sp_layerlist, new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				HashMapEx hme = (HashMapEx)ExtraStr;
				LoadFieldListByLayer(hme.get("LayerId")+"");
				_Dialog.findViewById(R.id.sp_layerlist).setTag(hme);
			}});
	}
	
	/**
	 * ��ȡͼ����ֶ��б��󶨵�ListView
	 * @param LayerId
	 */
	private void LoadFieldListByLayer(String LayerId)
	{
		this.m_FieldList.clear();
		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(LayerId);
		
		v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
		if (pLayer==null)pLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetLayerByID(pDataset.getId());
		
		for(v1_LayerField LF:pLayer.GetFieldList())
		{
			HashMap<String,Object> hmField = new HashMap<String,Object>();
			hmField.put("Select", false);
			hmField.put("FieldName", LF.GetDataFieldName());
			hmField.put("FieldCaption", LF.GetFieldName());
			this.m_FieldList.add(hmField);
		}

		//ˢ���б�
    	v1_Project_Layer_BKMap_MapFileAdapter adapter = new v1_Project_Layer_BKMap_MapFileAdapter(_Dialog.getContext(),this.m_FieldList, 
											       R.layout.v1_bk_polyanalysis_set_addfilter_list, 
											       new String[] {"Select", "FieldCaption"}, 
											       new int[] {R.id.cbselect, R.id.tvname});
		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
	}
	
	//�ֶ��б�
	private List<HashMap<String,Object>> m_FieldList = new ArrayList<HashMap<String,Object>>();
	
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadLayerList();}}
    	);
    	_Dialog.show();
    }

}
