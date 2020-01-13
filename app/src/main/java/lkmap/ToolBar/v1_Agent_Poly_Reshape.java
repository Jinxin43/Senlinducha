package lkmap.ToolBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DuChaDB;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import lkmap.Cargeometry.Polygon;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Edit.CopyFeature;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Layer.GeoLayer;
import lkmap.Spatial.PolyTools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;

public class v1_Agent_Poly_Reshape implements v1_IToolsBarCommand {

	@Override
	public void OnDispose() {
	}

	@Override
	public void OnChange() {
	}

	@Override
	public void OnPrepare() {
	}

	public static void StartReshape() {
		// 判断是否有分割轨迹
		if (v1_Agent_DrawlineEx.GetDrawLine_Poly().GetTrackPointList().size() == 0) {
			lkmap.Tools.Tools.ShowMessageBox("请勾绘修边线！");
			return;
		}

		boolean isBGLayer = true;

		// 判断是否在可编辑图层有已经选中的面
		List<HashMap<String, Object>> selectObjectList = new ArrayList<HashMap<String, Object>>();
		for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList()) {
			if (pGeoLayer.getType() != lkGeoLayerType.enPolygon)
				continue;
			if (pGeoLayer.getDataset().getDataSource().getEditing() && pGeoLayer.getSelSelection().getCount() > 0) {
				isBGLayer = false;
				for (int ObjIndex : pGeoLayer.getSelSelection().getGeometryIndexList()) {
					HashMap<String, Object> selectObj = new HashMap<String, Object>();
					selectObj.put("GeoLayer", pGeoLayer);
					selectObj.put("ObjIndex", ObjIndex);
					selectObjectList.add(selectObj);
				}
			}
		}

		if (isBGLayer) {
			for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).getList()) {
				for (int ObjIndex : pGeoLayer.getSelSelection().getGeometryIndexList()) {
					HashMap<String, Object> selectObj = new HashMap<String, Object>();
					selectObj.put("GeoLayer", pGeoLayer);
					selectObj.put("ObjIndex", ObjIndex);
					selectObjectList.add(selectObj);
				}
			}
		}
		if (isBGLayer) {
			if (selectObjectList.size() < 2) {
				lkmap.Tools.Tools.ShowMessageBox("请至少选择受影响的两个面进行修改！");
				return;
			} else {

				UnRedoParaStru UnRedoPara = new UnRedoParaStru();
				UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;

				for (HashMap<String, Object> hm : selectObjectList) {
					GeoLayer pGeoLayer = (GeoLayer) hm.get("GeoLayer");
					int ObjIndex = Integer.parseInt(hm.get("ObjIndex") + "");
					Polygon pPolygon = (Polygon) pGeoLayer.getDataset().GetGeometry(ObjIndex);
					Polygon reShapePolygon = PolyTools.ReshapePolygon(pPolygon,
							v1_Agent_DrawlineEx.GetDrawLine_Poly().GetTrackPointList());
					if (reShapePolygon == null) {
						lkmap.Tools.Tools.ShowMessageBox("修边线与一个小班没有相交，不进行计算！");
						continue;
					}

					// 属性复制
					HashMap<String, String> FeatureList = CopyFeature.CopyFrom(pGeoLayer.getDataset(), ObjIndex);
					// 回退区
					UnRedoDataItem urDataItem = new UnRedoDataItem();
					urDataItem.Type = lkReUndoFlag.enRedo;
					IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
					urDelete.LayerId = pGeoLayer.getDataset().getId();
					urDataItem.DataList.add(urDelete);

					// 保存修边后的新实体
					v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
					gpsDataObj.SetDataset(pGeoLayer.getDataset());
					gpsDataObj.SetSYS_ID(-1);
					int newId = gpsDataObj.SaveGeoToDb(reShapePolygon, reShapePolygon.getLength(true),
							reShapePolygon.getArea(true));
					if (newId >= 0) {
						if (CopyFeature.CopyTo(FeatureList, pGeoLayer.getDataset(), newId))
							urDelete.ObjectIdList.add(newId);
					}

					UnRedoPara.DataItemList.add(urDataItem);

					// 在数据库中打上删除标识，方便以后恢复
					String SQL_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
					SQL_Del = String.format(SQL_Del, pGeoLayer.getDataset().getDataTableName(), ObjIndex);
					if (pGeoLayer.getDataset().getDataSource().ExcuteSQL(SQL_Del)) {
						pPolygon.setStatus(lkGeometryStatus.enDelete);

						// 回退区
						UnRedoDataItem urDataItem2 = new UnRedoDataItem();
						urDataItem2.Type = lkReUndoFlag.enUndo;
						IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
						urDelete2.LayerId = pGeoLayer.getDataset().getId();
						urDelete2.ObjectIdList.add(ObjIndex);
						urDataItem2.DataList.add(urDelete2);
						UnRedoPara.DataItemList.add(urDataItem2);
					}

				}

				IUnRedo.AddHistory(UnRedoPara);
				v1_Agent_DrawlineEx.GetDrawLine_Poly().Cancel();

				// 清空选择集合
				PubVar.m_Map.ClearSelection();
				PubVar.m_Map.Refresh();
			}
		} else {
			if (selectObjectList.size() != 1) {
				lkmap.Tools.Tools.ShowMessageBox("请在可编辑图层中选择需要修边的面，只能选择一个面！");
				return;
			} else {
				GeoLayer pGeoLayer = (GeoLayer) selectObjectList.get(0).get("GeoLayer");
				int ObjIndex = Integer.parseInt(selectObjectList.get(0).get("ObjIndex") + "");
				Polygon pPolygon = (Polygon) pGeoLayer.getDataset().GetGeometry(ObjIndex);
				Polygon reShapePolygon = PolyTools.ReshapePolygon(pPolygon,
						v1_Agent_DrawlineEx.GetDrawLine_Poly().GetTrackPointList());
				if (reShapePolygon == null) {
					lkmap.Tools.Tools.ShowMessageBox("修边线的画法不正确，请重新绘制！");
					return;
				} else {
					UnRedoParaStru UnRedoPara = new UnRedoParaStru();
					UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;

					// 属性复制
					HashMap<String, String> FeatureList = CopyFeature.CopyFrom(pGeoLayer.getDataset(), ObjIndex);

					// 回退区
					UnRedoDataItem urDataItem = new UnRedoDataItem();
					urDataItem.Type = lkReUndoFlag.enRedo;
					IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
					urDelete.LayerId = pGeoLayer.getDataset().getId();
					urDataItem.DataList.add(urDelete);

					// 保存修边后的新实体
					v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
					gpsDataObj.SetDataset(pGeoLayer.getDataset());
					gpsDataObj.SetSYS_ID(-1);
					int newId = gpsDataObj.SaveGeoToDb(reShapePolygon, reShapePolygon.getLength(true),
							reShapePolygon.getArea(true));
					if (newId >= 0) {
						if (CopyFeature.CopyTo(FeatureList, pGeoLayer.getDataset(), newId))
							urDelete.ObjectIdList.add(newId);
					}
					UnRedoPara.DataItemList.add(urDataItem);

					// 在数据库中打上删除标识，方便以后恢复
					String SQL_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
					SQL_Del = String.format(SQL_Del, pGeoLayer.getDataset().getDataTableName(), ObjIndex);
					if (pGeoLayer.getDataset().getDataSource().ExcuteSQL(SQL_Del)) {
						Log.e("Del Resharp", SQL_Del);
						pPolygon.setStatus(lkGeometryStatus.enDelete);

						// 回退区
						UnRedoDataItem urDataItem2 = new UnRedoDataItem();
						urDataItem2.Type = lkReUndoFlag.enUndo;
						IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
						urDelete2.LayerId = pGeoLayer.getDataset().getId();
						urDelete2.ObjectIdList.add(ObjIndex);
						urDataItem2.DataList.add(urDelete2);
						UnRedoPara.DataItemList.add(urDataItem2);
						updateJCKP(urDelete2,pGeoLayer.getDataset().getId(),ObjIndex,newId);
					}

					IUnRedo.AddHistory(UnRedoPara);
					v1_Agent_DrawlineEx.GetDrawLine_Poly().Cancel();

					// 清空选择集合
					PubVar.m_Map.ClearSelection();
					PubVar.m_Map.Refresh();
				}
			}

		}

	}
	
	  //更新督查检查卡片表
    private static void updateJCKP(IURDataItem_DeleteAdd urDelete,String layerID,int oldId,int newId)
    {
    	
//    	String strNewID = lkmap.Tools.Tools.IntListToStr(newIds);
    	String sql = "update T_DuChaJCKP set a160 ='"+ newId +"' where a159='" + layerID + "' and a160 = '"+ oldId+"'"; 
    	SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
    	try
    	{
    		m_SQLiteDatabase.execSQL(sql);
    		HashMap<String,String> hashMap = new HashMap<String,String>();
    		hashMap.put("oneID", oldId+"");
    		hashMap.put("splitedIDs", newId+"");    		
    		urDelete.spilteIDs.add(hashMap);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
		
    	try
    	{
    		sql ="select a160 from T_DuChaJCKP where a159='" + layerID + "' and ( a160 like '%,"+oldId+",%' OR a160 like '"+oldId+",%' OR a160 like '%,"+oldId+"') ";
        	SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
        	
        	ArrayList<String> oldA160s=new ArrayList<String>();
    		while(reader.Read())
    		{
    			String preA160 = reader.GetString("a160");
    			oldA160s.add(preA160);
    			
    		}
    		reader.Close();
    		
    		for(String oldA160:oldA160s)
    		{
    			String newA160 = oldA160.replace(","+oldId+",", ","+ newId+",").replace(","+oldId,  ","+ newId).replace(oldId+",", newId+",");
    			String updateSql =  "update T_DuChaJCKP set a160 ='"+ newA160 +"' where a159='" + layerID + "' and a160 = '"+ oldA160+"'"; 
    			try
    			{
    				m_SQLiteDatabase.execSQL(updateSql);
    				
    				HashMap<String,String> hashMap = new HashMap<String,String>();
    	    		hashMap.put("oneID", oldA160);
    	    		hashMap.put("splitedIDs", newA160);    		
    	    		urDelete.spilteIDs.add(hashMap);
    			}
    			catch(Exception ex)
    			{
    				ex.printStackTrace();
    			}
    		}
    		
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	
    	m_SQLiteDatabase.close();
    }

}
