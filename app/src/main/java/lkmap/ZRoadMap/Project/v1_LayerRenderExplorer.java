package lkmap.ZRoadMap.Project;

import java.util.List;

import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Geometry;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkDatasetSourceType;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkRenderType;
import lkmap.Layer.GeoLayer;
import lkmap.Render.SimpleRender;
import lkmap.Render.UniqueValueRender;
import lkmap.Tools.Tools;

public class v1_LayerRenderExplorer 
{
	public boolean RenderLayerForAdd(v1_Layer vLayer)
	{
		return this.RenderLayer(vLayer, lkEditMode.enNew);
	}
	public boolean RenderLayerForUpdate(v1_Layer vLayer)
	{
		return this.RenderLayer(vLayer, lkEditMode.enEdit);
	}
	/**
	 * �������ݲɼ�ͼ��ķ��Ż���Ϣ
	 * @param vLayer
	 * @param editMode
	 * @return
	 */
	private boolean RenderLayer(v1_Layer vLayer,lkEditMode editMode)
	{
		//ֻҪ�����µĲɼ�ͼ��
		if (editMode==lkEditMode.enNew)
		{
			//�����µ����ݼ�
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(vLayer.GetLayerID());

            //�����µ�GeoLayer
			GeoLayer pGeoLayer = new GeoLayer(PubVar.m_Map);
			pGeoLayer.setId(pDataset.getId());
			pGeoLayer.setDataset(pDataset);
            pDataset.setBindGeoLayer(pGeoLayer);
            if (pDataset.getSourceType()==lkDatasetSourceType.enBackgroundData)
            	 PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).AddLayer(pGeoLayer);
            if (pDataset.getSourceType()==lkDatasetSourceType.enEditingData)
            	PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).AddLayer(pGeoLayer);
            
            this.RenderLayer(vLayer, lkEditMode.enEdit);
		}
		if (editMode==lkEditMode.enEdit)
		{
			GeoLayer pGeoLayer = PubVar.m_Workspace.GetDatasetById(vLayer.GetLayerID()).getBindGeoLayer();
			this.RenderLayerForGeoLayer(pGeoLayer,vLayer);
		}
		return true;
	}
	
	
//	/**
//	 * ����ָ��ͼ���ȫ����ע��Ϣ��Ҳ���Ǵ����ݿ������¼�����ע
//	 * @param vLayer
//	 */
//	public void RenderLayerForUpdateAllLabel(v1_Layer vLayer)
//	{
//		DataSource pDataSource = PubVar.m_Workspace.GetDataSourceByEditing();
//		Dataset pDataset = pDataSource.GetDatasetByName(vLayer.GetLayerID());
//		String SQL = "select SYS_ID,"+vLayer.GetLabelDataField() +" from "+vLayer.GetLayerID();
//		SQLiteDataReader DR = pDataSource.Query(SQL);
//		if (DR==null) return;
//		while(DR.Read())
//		{
//			String SYSID = DR.GetString("SYS_ID");
//			String Label = DR.GetString(vLayer.GetLabelDataField());
//			pDataset.GetGeometry(Integer.parseInt(SYSID)).setTag(Label);
//		}DR.Close();
//	}
	
	
//	/**
//	 * ��Ⱦ��ͼͼ�㣬ע���ʱ��Dataset�Ѿ������ˣ���v1_BKVectorLayerExplorer.OpenVectorDataSource()�д�����
//	 * @param vLayer
//	 * @return
//	 */
//	public boolean RenderBKLayerForAdd(v1_Layer vLayer)
//	{
//		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(vLayer.GetLayerID());
//        //�����µ�GeoLayer
//		GeoLayer pGeoLayer = new GeoLayer(PubVar.m_Map);
//		pGeoLayer.setDataset(pDataset);
//		pGeoLayer.SetAliasName(vLayer.GetLayerName());   //ͼ�������һ��Ϊ����
//        pGeoLayer.setName(pDataset.getName());
//        pGeoLayer.setType(pDataset.getType());
//        pDataset.setBindGeoLayer(pGeoLayer);
//        PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).AddLayer(pGeoLayer);
//        this.RenderLayerForGeoLayer(pGeoLayer, vLayer);
//        return true;
//	}
//	

	
	/**
	 * ��Ⱦָ����ͼ��
	 * @param pGeoLayer
	 */
	private void RenderLayerForGeoLayer(GeoLayer pGeoLayer,v1_Layer vLayer)
	{
		pGeoLayer.SetAliasName(vLayer.GetLayerAliasName());								//ͼ�������һ��Ϊ����
		pGeoLayer.setId(vLayer.GetLayerID());											//ͼ��ID
		pGeoLayer.setType(vLayer.GetLayerType());										//ͼ������
		pGeoLayer.setVisible(vLayer.GetVisible());          							//ͼ��Ŀɼ���

        pGeoLayer.setVisibleScaleMin(vLayer.GetVisibleScaleMin());
        pGeoLayer.setVisibleScaleMax(vLayer.GetVisibleScaleMax());
        
        //��Ⱦ��Ϣ���˴����������Ŀ������Ӧ����ֵ���ֵ���л�
        if (vLayer.GetRenderType()==lkRenderType.enSimple)
        {
        	if (pGeoLayer.getRender()==null) pGeoLayer.setRender(new SimpleRender(pGeoLayer));
        	else if (pGeoLayer.getRender().getType()==lkRenderType.enUniqueValue)pGeoLayer.setRender(new SimpleRender(pGeoLayer));
        }
        
        //�Ƿ����¶�ȡΨһֵ
        boolean ReadUniqueValue = false;
        if (vLayer.GetRenderType()==lkRenderType.enUniqueValue)
        {
        	if (pGeoLayer.getRender()==null) {pGeoLayer.setRender(new UniqueValueRender(pGeoLayer));ReadUniqueValue=true;}
        	else if (pGeoLayer.getRender().getType()==lkRenderType.enSimple){pGeoLayer.setRender(new UniqueValueRender(pGeoLayer));ReadUniqueValue=true;}
        }

        //��ע
        boolean ReadLabel = true;   //�Ƿ����¶�ȡ��ע��Ϣ
        if (!vLayer.GetIfLabel())ReadLabel = false; 
        else
        {
        	if (pGeoLayer.getRender().getIfLabel() && pGeoLayer.getRender().getLabelField().equals(vLayer.GetLabelDataFieldStr()))ReadLabel=false;
        }
        pGeoLayer.getRender().setIfLabel(vLayer.GetIfLabel());
        pGeoLayer.getRender().setLabelField(vLayer.GetLabelDataFieldStr());
        pGeoLayer.getRender().setLabelFont(vLayer.GetLabelFont());
        if (ReadLabel) pGeoLayer.getRender().UpdateAllLabel();
        
        //��ѡ��
        pGeoLayer.setSelectable(vLayer.GetSelectable());

        //��Ⱦ��Ϣ
        if (pGeoLayer.getRender().getType()==lkRenderType.enSimple)
        {
        	SimpleRender SR = (SimpleRender)pGeoLayer.getRender();
        	SR.setSymbol(vLayer.GetSimpleSymbol());
        	SR.SetSymbolTransparent(vLayer.GetTransparet());
        	SR.UpdateSymbolSet();
        }
        if (pGeoLayer.getRender().getType()==lkRenderType.enUniqueValue)
        {
        	UniqueValueRender VR = (UniqueValueRender)pGeoLayer.getRender();
        	List<String> oldFieldList = VR.GetUniqueValueFieldList();
        	List<String> newFieldList = (List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueValueField");
        	if (!Tools.JoinT(",", oldFieldList).equals(Tools.JoinT(",", newFieldList)))ReadUniqueValue=true;
        	VR.SetUniqueValueFieldList((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueValueField"));
        	VR.SetUniqueValueList((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueValueList"));
        	VR.SetUniqueSymbolList((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueSymbolList"));
        	VR.SetDefaultSymbol(vLayer.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol")+"");
        	VR.SetSymbolTransparent(vLayer.GetTransparet());
        	if (ReadUniqueValue)VR.UpdateAllUniqueValue();
        }

        //���������õķ��Ÿ�����ʾʵ��
        for(Geometry pGeometry : pGeoLayer.getDataset().GetGeometryList())
        {
        	pGeoLayer.getRender().UpdateSymbol(pGeometry);
        }
	}
}
