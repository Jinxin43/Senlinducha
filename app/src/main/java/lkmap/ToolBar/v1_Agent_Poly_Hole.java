package lkmap.ToolBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DuChaDB;
import com.dingtu.senlinducha.R;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Edit.CopyFeature;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Enum.lkPartType;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Layer.GeoLayer;
import lkmap.Spatial.PolyTools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;

public class v1_Agent_Poly_Hole implements v1_IToolsBarCommand {

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

	public void SetView(View view) {
		this.m_View = view;
		this.m_View.findViewById(R.id.bt_drawline).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_snap).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_gps_start).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_clear).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_finish).setOnClickListener(new ViewClick());
	}

	public class ViewClick implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			if (Tag.equals("�ֶ�����")) {
				// PubVar.m_MapControl.setActiveTools(Tools.MoveObject,
				// v1_Agent_DrawlineEx.GetDrawLineEx(),
				// v1_Agent_DrawlineEx.GetDrawLineEx());
			}
			if (Tag.equals("���")) {
				// v1_Agent_DrawlineEx.GetDrawLineEx().Clear();
			}

			if (Tag.equals("ת��")) {
				// v1_Agent_DrawlineEx.GetDrawLineEx().ChangeEditDirection();
			}
			if (Tag.equals("���")) {
				StartClipHole();
			}

			// if (Tag.equals("ɾ��"))Delete();
			//
			// if (Tag.equals("����"))
			// {
			// PubVar.m_DoEvent.DoCommand(Tag);
			// }
			// if (Tag.equals("����"))
			// {
			// PubVar.m_DoEvent.DoCommand(Tag);
			// }
		}
	}

	public static void StartClipHole() {
		// �ж��Ƿ��зָ�켣
		if (v1_Agent_DrawlineEx.GetDrawLine_Poly().GetTrackPointList().size() == 0) {
			lkmap.Tools.Tools.ShowMessageBox("�빴��µ����ߣ�ע�⣺������㡢ֹ������������ڲ���");
			return;
		}

		// �ж��Ƿ�����㱻ѡ��
		if (!PubVar.m_DoEvent.AlwaysOpenProject())
			return;

		boolean isBGLayer = true;
		// �ж��Ƿ��ڿɱ༭ͼ�����Ѿ�ѡ�е���
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

		if (selectObjectList.size() != 1) {
			lkmap.Tools.Tools.ShowMessageBox("���ڿɱ༭ͼ����ѡ��1������Ҫ�и�µ����棡");
			return;
		}

		// �ж�ѡ�����Ƿ���ȫ�������ƵĹµ�
		GeoLayer pGeoLayer = (GeoLayer) selectObjectList.get(0).get("GeoLayer");
		int ObjIndex = Integer.parseInt(selectObjectList.get(0).get("ObjIndex") + "");
		Polygon MainPoly = (Polygon) pGeoLayer.getDataset().GetGeometry(ObjIndex);

		Polygon HolePoly = new Polygon();
		Part part = new Part();
		for (Coordinate Coor : v1_Agent_DrawlineEx.GetDrawLine_Poly().GetTrackPointList()) {
			part.getVertexList().add(Coor.Clone());
		}
		part.SetPartType(lkPartType.enPoly);
		HolePoly.AddPart(part);
		HolePoly.Closed();

		boolean CheckPass = PolyTools.FullContains(MainPoly, HolePoly);
		if (!CheckPass) {
			lkmap.Tools.Tools.ShowMessageBox("�������ڻ��ƹµ����ߣ�");
			return;
		}

		// ��ʼ�и
		Polygon ClipPolygon = PolyTools.ClipHole(MainPoly, HolePoly);

		UnRedoParaStru UnRedoPara = new UnRedoParaStru();
		UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;

		// ������
		UnRedoDataItem urDataItem = new UnRedoDataItem();
		urDataItem.Type = lkReUndoFlag.enRedo;
		UnRedoPara.DataItemList.add(urDataItem);
		IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
		urDelete.LayerId = pGeoLayer.getDataset().getId();
		urDataItem.DataList.add(urDelete);

		// ���Ը���
		HashMap<String, String> FeatureList = CopyFeature.CopyFrom(pGeoLayer.getDataset(), ObjIndex);

		List<Integer> newIDs=new ArrayList<Integer>();
		// �������
		v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
		gpsDataObj.SetDataset(pGeoLayer.getDataset());
		gpsDataObj.SetSYS_ID(-1);
		int newId1 = gpsDataObj.SaveGeoToDb(ClipPolygon, ClipPolygon.getLength(true), ClipPolygon.getArea(true));
		if (newId1 >= 0)
			newIDs.add(newId1);
			if (CopyFeature.CopyTo(FeatureList, pGeoLayer.getDataset(), newId1))
				urDelete.ObjectIdList.add(newId1);
		gpsDataObj.SetSYS_ID(-1);
		int newId2 = gpsDataObj.SaveGeoToDb(HolePoly, HolePoly.getLength(true), HolePoly.getArea(true));
		if (newId2 >= 0)
			newIDs.add(newId2);
			if (CopyFeature.CopyTo(FeatureList, pGeoLayer.getDataset(), newId2))
				urDelete.ObjectIdList.add(newId2);

		// �����ݿ��д���ɾ����ʶ�������Ժ�ָ�
		String SQL_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
		SQL_Del = String.format(SQL_Del, pGeoLayer.getDataset().getDataTableName(), ObjIndex);
		if (pGeoLayer.getDataset().getDataSource().ExcuteSQL(SQL_Del)) {
			MainPoly.setStatus(lkGeometryStatus.enDelete);

			// ������
			UnRedoDataItem urDataItem2 = new UnRedoDataItem();
			urDataItem2.Type = lkReUndoFlag.enUndo;
			IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
			urDelete2.LayerId = pGeoLayer.getDataset().getId();
			urDelete2.ObjectIdList.add(ObjIndex);
			urDataItem2.DataList.add(urDelete2);
			UnRedoPara.DataItemList.add(urDataItem2);
			
			updateJCKP(urDelete2,pGeoLayer.getId(),ObjIndex,newIDs);
		}

		IUnRedo.AddHistory(UnRedoPara);
		v1_Agent_DrawlineEx.GetDrawLine_Poly().Cancel();

		// ���ѡ�񼯺�
		PubVar.m_Map.ClearSelection();
		PubVar.m_Map.Refresh();

	}
	
	  //���¶����鿨Ƭ��
    private static void updateJCKP(IURDataItem_DeleteAdd urDelete,String layerID,int oldId,List<Integer> newIds)
    {
    	String strNewID = lkmap.Tools.Tools.IntListToStr(newIds);
    	String sql = "update T_DuChaJCKP set a160 ='"+ strNewID +"' where a159='" + layerID + "' and a160 = '"+ oldId+"'"; 
    	SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
    	try
    	{
    		m_SQLiteDatabase.execSQL(sql);
    		HashMap<String,String> hashMap = new HashMap<String,String>();
    		hashMap.put("oneID", oldId+"");
    		hashMap.put("splitedIDs", strNewID);    		
    		urDelete.spilteIDs.add(hashMap);
    		
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
    			String newA160 = oldA160.replace(","+oldId+",", ","+ strNewID+",").replace(","+oldId,  ","+ strNewID).replace(oldId+",", strNewID+",");
    			String updateSql =  "update T_DuChaJCKP set a160 ='"+ newA160 +"' where a159='" + layerID + "' and a160 = '"+ oldA160+"'"; 
    			try
    			{
    				m_SQLiteDatabase.execSQL(updateSql);
    				
    				hashMap = new HashMap<String,String>();
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
