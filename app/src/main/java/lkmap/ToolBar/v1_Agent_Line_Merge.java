package lkmap.ToolBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Edit.CopyFeature;
import lkmap.Edit.DeleteAddObject;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Layer.GeoLayer;
import lkmap.MapControl.Tools;
import lkmap.Spatial.LineTools;
import lkmap.Spatial.PolyTools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;

public class v1_Agent_Line_Merge implements v1_IToolsBarCommand {

	@Override
	public void OnDispose() {
	}
	
	@Override
	public void OnChange() {
	}
	
	@Override
	public void OnPrepare() {
	}

	
	private View m_View = null;
	public void SetView(View view)
	{
		this.m_View = view;
	}
	
    public class ViewClick implements OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    	}
    }
    
    //��ʼ�ϲ�����
    public static void StartMerge()
    {
    	//�ж��Ƿ�����㱻ѡ��
		if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
		if (!PubVar.m_DoEvent.m_GPSLine.CheckLayerValid())return;
		
		final List<HashMap<String,Object>> SelectObjList = GetSelectPolyline();
        if (SelectObjList.size() == 0)
        {
        	lkmap.Tools.Tools.ShowMessageBox("��ѡ����Ҫ���ӵ��ߣ�");
            return;
        }
        if (SelectObjList.size() == 1)
        {
        	lkmap.Tools.Tools.ShowMessageBox("�������޷��������Ӳ�����");
            return;
        }
        
        final List<Polyline> SelectPolygonList = new ArrayList<Polyline>();
        for(HashMap<String,Object> SelectObj:SelectObjList)
        {
        	SelectPolygonList.add((Polyline)SelectObj.get("Geometry"));
        }
		
        //�������Ժϲ�����
        v1_Agent_Poly_Merge_Feature apm = new v1_Agent_Poly_Merge_Feature();
        apm.SetQueryObj(SelectObjList);
        apm.SetICallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				
				//����ʵ��
				HashMap<String,Object> SelectObjEx = (HashMap<String,Object>)ExtraStr;
				String FeatureLayerId = SelectObjEx.get("LayerId")+"";
				int FeatureObjId = Integer.parseInt(SelectObjEx.get("ObjectId")+"");
				HashMap<String,String> FeatureList = CopyFeature.CopyFrom(PubVar.m_Workspace.GetDatasetById(FeatureLayerId), FeatureObjId);
				
		    	//��ʼ���Ӳ���
				Polyline MainPolyline = (Polyline)PubVar.m_Workspace.GetDatasetById(FeatureLayerId).GetGeometry(FeatureObjId);
				for(Polyline PL:SelectPolygonList)
				{
					if (MainPolyline.getSysId()==PL.getSysId()){SelectPolygonList.remove(PL);break;}
				}
				
		        Polyline mergePolygon = LineTools.StartConnect(MainPolyline, SelectPolygonList);
				
		        //������
		    	UnRedoParaStru UnRedoPara = new UnRedoParaStru();
		        UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;
		        
				UnRedoDataItem urDataItem = new UnRedoDataItem();
				urDataItem.Type = lkReUndoFlag.enRedo;
				UnRedoPara.DataItemList.add(urDataItem);
				
				IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
				urDataItem.DataList.add(urDelete);
				urDelete.LayerId = PubVar.m_DoEvent.m_GPSLine.GetDataset().getId();
				
				//���ºϲ�����ʵ��
				v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
				gpsDataObj.SetDataset(PubVar.m_DoEvent.m_GPSLine.GetDataset());
				gpsDataObj.SetSYS_ID(-1);
				int newId = gpsDataObj.SaveGeoToDb(mergePolygon, mergePolygon.getLength(true), -1);
				if (newId>=0)if (CopyFeature.CopyTo(FeatureList, PubVar.m_DoEvent.m_GPSLine.GetDataset(), newId))
				{
					urDelete.ObjectIdList.add(newId);
				}
				
				//ɾ����ʵ��
				UnRedoDataItem urDataItem2 = new UnRedoDataItem();
				urDataItem2.Type = lkReUndoFlag.enUndo;
				
				List<IURDataItem_DeleteAdd> urDAList = new ArrayList<IURDataItem_DeleteAdd>();
				for(HashMap<String,Object> SelectObj:SelectObjList)
				{
					IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
					urDelete2.LayerId = SelectObj.get("LayerId")+"";
					urDelete2.ObjectIdList.add(Integer.parseInt(SelectObj.get("ObjectId")+""));
					urDataItem2.DataList.add(urDelete2);
					urDAList.add(urDelete2);
				}
				
				//ɾ��ʵ��
				DeleteAddObject DA = new DeleteAddObject();
				DA.Delete(urDAList, false);
				
				UnRedoPara.DataItemList.add(urDataItem2);
				IUnRedo.AddHistory(UnRedoPara);


				v1_Agent_DrawlineEx.GetDrawLine_Poly().Cancel();
		    	
		        //���ѡ�񼯺�
		        PubVar.m_Map.ClearSelection();    	
		    	PubVar.m_Map.Refresh();
			}});
        apm.ShowDialog();
    }
    
    private static List<HashMap<String,Object>> GetSelectPolyline()
    {
    	List<HashMap<String,Object>> SelectObjList = new ArrayList<HashMap<String,Object>>();
    	
    	//�ж��Ƿ����Ѿ�ѡ�е���
        for  (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList())
        {
        	if (pGeoLayer.getType()!=lkGeoLayerType.enPolyline) continue;
            if (pGeoLayer.getSelSelection().getCount()>0)
            {
                for(int ObjIndex:pGeoLayer.getSelSelection().getGeometryIndexList())
                {
                	Polyline pPolyline = (Polyline)pGeoLayer.getDataset().GetGeometry(ObjIndex);
                	HashMap<String,Object> SelectObj = new HashMap<String,Object>();
                	SelectObj.put("LayerId", pGeoLayer.getDataset().getId());
                	SelectObj.put("ObjectId",ObjIndex);
                	SelectObj.put("Geometry",pPolyline);
                	SelectObjList.add(SelectObj);
                }
            }
        }
        return SelectObjList;
    }
    
    

}
