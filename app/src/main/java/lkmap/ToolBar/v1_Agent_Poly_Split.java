package lkmap.ToolBar;

import java.io.File;
import java.text.DecimalFormat;
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
import lkmap.Cargeometry.Polygon;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Edit.CopyFeature;
import lkmap.Enum.ForestryLayerType;
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
import lkmap.ZRoadMap.Project.v1_Layer;

public class v1_Agent_Poly_Split implements v1_IToolsBarCommand {

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
		this.m_View.findViewById(R.id.bt_changearrow).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_finish).setOnClickListener(new ViewClick());
	}

	public class ViewClick implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			if (Tag.equals("手动绘线")) {
				// PubVar.m_MapControl.setActiveTools(Tools.MoveObject,
				// v1_Agent_DrawlineEx.GetDrawLineEx(),
				// v1_Agent_DrawlineEx.GetDrawLineEx());
			}
			if (Tag.equals("清空")) {
				// v1_Agent_DrawlineEx.GetDrawLineEx().Clear();
			}
			if (Tag.equals("转向")) {
				// v1_Agent_DrawlineEx.GetDrawLineEx().ChangeEditDirection();
			}
			if (Tag.equals("完成")) {
				StartClip();
			}

			// if (Tag.equals("删除"))Delete();
			//
			// if (Tag.equals("回退"))
			// {
			// PubVar.m_DoEvent.DoCommand(Tag);
			// }
			// if (Tag.equals("重做"))
			// {
			// PubVar.m_DoEvent.DoCommand(Tag);
			// }
		}
	}

	public static void StartPolyClip1() {
		List<HashMap<String, Object>> selectObjectList = new ArrayList<HashMap<String, Object>>();

		// 获取所有被选择的面
		for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList()) {
			if (pGeoLayer.getType() != lkGeoLayerType.enPolygon) {
				continue;
			}

			if (pGeoLayer.getSelSelection().getCount() > 0) {
				for (int objIndex : pGeoLayer.getSelSelection().getGeometryIndexList()) {
					HashMap<String, Object> selectObj = new HashMap<String, Object>();
					selectObj.put("GeoLayer", pGeoLayer);
					selectObj.put("ObjIndex", objIndex);
					selectObjectList.add(selectObj);
				}
			}
		}

		// 如果选择面小于2个，
		if (selectObjectList.size() <= 1) {
			lkmap.Tools.Tools.ShowMessageBox("请至少选择两个面进行面分割操作！");
			return;
		}

		// 初始化回退工具
		UnRedoParaStru UnRedoPara = new UnRedoParaStru();
		UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;

		HashMap<String, Object> firstObj = selectObjectList.get(0);
		GeoLayer layer = (GeoLayer) firstObj.get("GeoLayer");
		int index = Integer.parseInt(firstObj.get("ObjIndex") + "");
		Polygon poly1 = (Polygon) layer.getDataset().GetGeometry(index);

		List<HashMap<String, Object>> clipPolys = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> firstPoly = new HashMap<String, Object>();
		firstPoly.put("poly", poly1);
		firstPoly.put("layer", layer);
		clipPolys.add(firstPoly);
		ArrayList<HashMap<String, Object>> SubPolygonList1 = new ArrayList<HashMap<String, Object>>();

		for (int j = 1; j < selectObjectList.size(); j++) {
			boolean b1NoInter = false;
			HashMap<String, Object> selObj = selectObjectList.get(j);
			GeoLayer pGeoLayer = (GeoLayer) selObj.get("GeoLayer");
			int ObjIndex = Integer.parseInt(selObj.get("ObjIndex") + "");
			Polygon pPolygon = (Polygon) pGeoLayer.getDataset().GetGeometry(ObjIndex);

			if (SubPolygonList1.size() > 0) {
				clipPolys.clear();
				clipPolys.addAll(SubPolygonList1);
			}

			for (HashMap<String, Object> hashMap : clipPolys) {
				GeoLayer aLayer = (GeoLayer) hashMap.get("layer");
				Polygon aPoly = (Polygon) hashMap.get("poly");
				SubPolygonList1 = new ArrayList<HashMap<String, Object>>();
				HashMap<String, String> FeatureList = CopyFeature.CopyFrom(pGeoLayer.getDataset(), ObjIndex);

				boolean isReplated = false;
				for (Polygon p : PolyTools.RepatePoly(pPolygon, aPoly)) {
					if (p.getArea(true) > 0.000001) {
						isReplated = true;
						HashMap<String, Object> newPoly1 = new HashMap<String, Object>();
						newPoly1.put("layer", pGeoLayer);
						newPoly1.put("poly", p);
						v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
						gpsDataObj.SetDataset(pGeoLayer.getDataset());

						gpsDataObj.SetSYS_ID(-1);
						int newId1 = gpsDataObj.SaveGeoToDb(p, p.getLength(true), p.getArea(true));
						UnRedoDataItem urDataItem = new UnRedoDataItem();
						urDataItem.Type = lkReUndoFlag.enRedo;
						IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
						urDelete.LayerId = pGeoLayer.getDataset().getId();

						if (CopyFeature.CopyTo(FeatureList, pGeoLayer.getDataset(), newId1)) {
							urDelete.ObjectIdList.add(newId1);
						}

						urDataItem.DataList.add(urDelete);
						UnRedoPara.DataItemList.add(urDataItem);
						SubPolygonList1.add(newPoly1);
					}

				}

				if (isReplated) {
					b1NoInter = true;
					for (Polygon p2 : PolyTools.ClipPoly(aPoly, pPolygon)) {
						HashMap<String, Object> newPoly2 = new HashMap<String, Object>();
						newPoly2.put("layer", pGeoLayer);
						newPoly2.put("poly", p2);
						v1_CGpsDataObject gpsDataObj2 = new v1_CGpsDataObject();
						gpsDataObj2.SetDataset(pGeoLayer.getDataset());

						gpsDataObj2.SetSYS_ID(-1);
						int newId2 = gpsDataObj2.SaveGeoToDb(p2, p2.getLength(true), p2.getArea(true));
						UnRedoDataItem urDataItem = new UnRedoDataItem();
						urDataItem.Type = lkReUndoFlag.enRedo;
						IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
						urDelete.LayerId = pGeoLayer.getDataset().getId();

						if (CopyFeature.CopyTo(FeatureList, pGeoLayer.getDataset(), newId2)) {

						}
						urDelete.ObjectIdList.add(newId2);
						urDataItem.DataList.add(urDelete);
						UnRedoPara.DataItemList.add(urDataItem);
						SubPolygonList1.add(newPoly2);

					}

					HashMap<String, String> FeatureList2 = CopyFeature.CopyFrom(aLayer.getDataset(), aPoly.getSysId());
					for (Polygon p3 : PolyTools.ClipPoly(pPolygon, aPoly)) {

						HashMap<String, Object> newPoly = new HashMap<String, Object>();
						newPoly.put("layer", aLayer);
						newPoly.put("poly", p3);
						v1_CGpsDataObject gpsDataObj3 = new v1_CGpsDataObject();
						gpsDataObj3.SetDataset(aLayer.getDataset());

						gpsDataObj3.SetSYS_ID(-1);
						int newId3 = gpsDataObj3.SaveGeoToDb(p3, p3.getLength(true), p3.getArea(true));
						UnRedoDataItem urDataItem = new UnRedoDataItem();
						urDataItem.Type = lkReUndoFlag.enRedo;
						IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
						urDelete.LayerId = aLayer.getDataset().getId();

						if (CopyFeature.CopyTo(FeatureList2, aLayer.getDataset(), newId3)) {

						}

						urDelete.ObjectIdList.add(newId3);
						urDataItem.DataList.add(urDelete);

						UnRedoPara.DataItemList.add(urDataItem);

						SubPolygonList1.add(newPoly);
					}

					// 在数据库中打上删除标识，方便以后恢复
					String SQL_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
					SQL_Del = String.format(SQL_Del, aLayer.getDataset().getDataTableName(), aPoly.getSysId());
					if (aLayer.getDataset().getDataSource().ExcuteSQL(SQL_Del)) {
						aPoly.setStatus(lkGeometryStatus.enDelete);

						// 回退区
						UnRedoDataItem urDataItem2 = new UnRedoDataItem();
						urDataItem2.Type = lkReUndoFlag.enUndo;
						IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
						urDelete2.LayerId = aLayer.getDataset().getId();
						urDelete2.ObjectIdList.add(aPoly.getSysId());
						urDataItem2.DataList.add(urDelete2);
						UnRedoPara.DataItemList.add(urDataItem2);
					}
				} else {
					SubPolygonList1.add(hashMap);
				}

			}

			if (b1NoInter) {
				// 在数据库中打上删除标识，方便以后恢复
				String SQL2_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
				SQL2_Del = String.format(SQL2_Del, pGeoLayer.getDataset().getDataTableName(), ObjIndex);
				if (pGeoLayer.getDataset().getDataSource().ExcuteSQL(SQL2_Del)) {
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
			} else {

				HashMap<String, Object> newPoly4 = new HashMap<String, Object>();
				newPoly4.put("layer", pGeoLayer);
				newPoly4.put("poly", pPolygon);

				SubPolygonList1.add(newPoly4);
			}

		}

		// 清空选择集合
		PubVar.m_Map.ClearSelection();
		PubVar.m_Map.Refresh();

		IUnRedo.AddHistory(UnRedoPara);
		PubVar.m_Map.Refresh();
	}

	public static void StartPolyClip() {
		List<HashMap<String, Object>> selectObjectList = new ArrayList<HashMap<String, Object>>();

		// 获取所有被选择的面
		for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList()) {
			if (pGeoLayer.getType() != lkGeoLayerType.enPolygon) {
				continue;
			}

			if (pGeoLayer.getSelSelection().getCount() > 0) {
				for (int objIndex : pGeoLayer.getSelSelection().getGeometryIndexList()) {
					HashMap<String, Object> selectObj = new HashMap<String, Object>();
					selectObj.put("layer", pGeoLayer);
					selectObj.put("poly", pGeoLayer.getDataset().GetGeometry(objIndex));
					selectObjectList.add(selectObj);
				}
			}
		}

		// 如果选择面小于2个，
		if (selectObjectList.size() < 2) {
			lkmap.Tools.Tools.ShowMessageBox("请至少选择两个面进行面分割操作！");
			return;
		}

		// 初始化回退工具
		UnRedoParaStru UnRedoPara = new UnRedoParaStru();
		UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;

		// HashMap<String,Object> firstObj = selectObjectList.get(0);
		// GeoLayer layer = (GeoLayer)firstObj.get("GeoLayer");
		// int index = Integer.parseInt(firstObj.get("ObjIndex")+"");
		// Polygon poly1 = (Polygon)layer.getDataset().GetGeometry(index);

		ArrayList<HashMap<String, Object>> SubPolygonList1 = new ArrayList<HashMap<String, Object>>();
		// HashMap<String,Object> firstPoly = new HashMap<String,Object>();
		// firstPoly.put("poly", poly1);
		// firstPoly.put("layer", layer);
		SubPolygonList1.add(selectObjectList.get(0));

		// selectObjectList.remove(0);
		// clipPolys(selectObjectList,SubPolygonList1,UnRedoPara);

		ArrayList<HashMap<String, Object>> clipPolys = new ArrayList<HashMap<String, Object>>();

		for (int j = 1; j < selectObjectList.size(); j++) {
			boolean b1NoInter = false;
			HashMap<String, Object> selObj = selectObjectList.get(j);
			GeoLayer pGeoLayer = (GeoLayer) selObj.get("layer");
			Polygon pPolygon = (Polygon) selObj.get("poly");

			clipPolys.addAll(SubPolygonList1);
			SubPolygonList1.clear();

			while (clipPolys.size() > 0) {
				HashMap<String, Object> hashMap = clipPolys.get(0);
				GeoLayer aLayer = (GeoLayer) hashMap.get("layer");
				Polygon aPoly = (Polygon) hashMap.get("poly");

				ArrayList<HashMap<String, Object>> SubPolygonList2 = new ArrayList<HashMap<String, Object>>();

				boolean isRepeated = clipTwoPoly(SubPolygonList2, aLayer, aPoly, pGeoLayer, pPolygon, UnRedoPara);

				clipPolys.remove(0);

				if (isRepeated)// 两个面相交
				{
					b1NoInter = true;

					if (clipPolys.size() > 0) {
						while (clipPolys.size() > 0) {
							HashMap<String, Object> hm = clipPolys.get(0);
							GeoLayer cLayer = (GeoLayer) hm.get("layer");
							Polygon cPoly = (Polygon) hm.get("poly");
							SubPolygonList1.addAll(clipPolyss(SubPolygonList2, cLayer, cPoly, UnRedoPara));
							clipPolys.remove(0);
						}

					} else {
						SubPolygonList1.addAll(SubPolygonList2);
					}

				} else {
					SubPolygonList1.add(hashMap);
				}

			}

			if (b1NoInter) {
				// 在数据库中打上删除标识，方便以后恢复
				String SQL2_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
				SQL2_Del = String.format(SQL2_Del, pGeoLayer.getDataset().getDataTableName(), pPolygon.getSysId());
				if (pGeoLayer.getDataset().getDataSource().ExcuteSQL(SQL2_Del)) {
					pPolygon.setStatus(lkGeometryStatus.enDelete);

					// 回退区
					UnRedoDataItem urDataItem2 = new UnRedoDataItem();
					urDataItem2.Type = lkReUndoFlag.enUndo;
					IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
					urDelete2.LayerId = pGeoLayer.getDataset().getId();
					urDelete2.ObjectIdList.add(pPolygon.getSysId());
					urDataItem2.DataList.add(urDelete2);
					UnRedoPara.DataItemList.add(urDataItem2);
				}
			} else {
				HashMap<String, Object> newPoly4 = new HashMap<String, Object>();
				newPoly4.put("layer", pGeoLayer);
				newPoly4.put("poly", pPolygon);

				SubPolygonList1.add(newPoly4);
			}

		}

		// 清空选择集合
		PubVar.m_Map.ClearSelection();
		PubVar.m_Map.Refresh();

		IUnRedo.AddHistory(UnRedoPara);
		PubVar.m_Map.Refresh();
	}

	public static ArrayList<HashMap<String, Object>> clipPolyss(ArrayList<HashMap<String, Object>> clipPolys,
			GeoLayer pGeoLayer, Polygon pPolygon, UnRedoParaStru UnRedoPara) {
		boolean b1NoInter = false;
		ArrayList<HashMap<String, Object>> SubPolygonList1 = new ArrayList<HashMap<String, Object>>();

		while (clipPolys.size() > 0) {
			HashMap<String, Object> hashMap = clipPolys.get(0);
			GeoLayer aLayer = (GeoLayer) hashMap.get("layer");
			Polygon aPoly = (Polygon) hashMap.get("poly");

			ArrayList<HashMap<String, Object>> SubPolygonList2 = new ArrayList<HashMap<String, Object>>();

			boolean isRepeated = clipTwoPoly(SubPolygonList2, aLayer, aPoly, pGeoLayer, pPolygon, UnRedoPara);

			clipPolys.remove(0);

			if (isRepeated)// 两个面相交
			{
				b1NoInter = true;

				String SQL2_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
				SQL2_Del = String.format(SQL2_Del, aLayer.getDataset().getDataTableName(), aPoly.getSysId());
				if (pGeoLayer.getDataset().getDataSource().ExcuteSQL(SQL2_Del)) {
					aPoly.setStatus(lkGeometryStatus.enDelete);

					// 回退区
					UnRedoDataItem urDataItem2 = new UnRedoDataItem();
					urDataItem2.Type = lkReUndoFlag.enUndo;
					IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
					urDelete2.LayerId = aLayer.getDataset().getId();
					urDelete2.ObjectIdList.add(aPoly.getSysId());
					urDataItem2.DataList.add(urDelete2);
					UnRedoPara.DataItemList.add(urDataItem2);
				}

				if (clipPolys.size() > 0) {
					while (clipPolys.size() > 0) {
						HashMap<String, Object> hm = clipPolys.get(0);
						GeoLayer cLayer = (GeoLayer) hm.get("layer");
						Polygon cPoly = (Polygon) hm.get("poly");
						SubPolygonList1.addAll(clipPolyss(SubPolygonList2, cLayer, cPoly, UnRedoPara));
						clipPolys.remove(0);
					}

				} else {
					SubPolygonList1.addAll(SubPolygonList2);
				}
			} else {

				SubPolygonList1.add(hashMap);
			}

		}

		if (b1NoInter) {
			// 在数据库中打上删除标识，方便以后恢复
			String SQL2_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
			SQL2_Del = String.format(SQL2_Del, pGeoLayer.getDataset().getDataTableName(), pPolygon.getSysId());
			if (pGeoLayer.getDataset().getDataSource().ExcuteSQL(SQL2_Del)) {
				pPolygon.setStatus(lkGeometryStatus.enDelete);

				// 回退区
				UnRedoDataItem urDataItem2 = new UnRedoDataItem();
				urDataItem2.Type = lkReUndoFlag.enUndo;
				IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
				urDelete2.LayerId = pGeoLayer.getDataset().getId();
				urDelete2.ObjectIdList.add(pPolygon.getSysId());
				urDataItem2.DataList.add(urDelete2);
				UnRedoPara.DataItemList.add(urDataItem2);
			}
		} else {
			HashMap<String, Object> newPoly4 = new HashMap<String, Object>();
			newPoly4.put("layer", pGeoLayer);
			newPoly4.put("poly", pPolygon);

			SubPolygonList1.add(newPoly4);
		}
		return SubPolygonList1;
	}

	public static ArrayList<HashMap<String, Object>> clipPolys(List<HashMap<String, Object>> polys1,
			List<HashMap<String, Object>> polys2, UnRedoParaStru UnRedoPara) {
		ArrayList<HashMap<String, Object>> SubPolygonList1 = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < polys1.size(); i++) {
			boolean b1NoInter = false;
			HashMap<String, Object> hashMap = polys1.get(i);
			GeoLayer gLayer = (GeoLayer) hashMap.get("layer");
			Polygon gPoly = (Polygon) hashMap.get("poly");
			while (polys2.size() > 0) {
				HashMap<String, Object> hashMap2 = polys2.get(0);
				GeoLayer aLayer = (GeoLayer) hashMap2.get("layer");
				Polygon aPoly = (Polygon) hashMap2.get("poly");

				ArrayList<HashMap<String, Object>> SubPolygonList2 = new ArrayList<HashMap<String, Object>>();

				boolean isRepeated = clipTwoPoly(SubPolygonList2, gLayer, gPoly, aLayer, aPoly, UnRedoPara);

				polys2.remove(0);

				if (isRepeated)// 两个面相交
				{
					b1NoInter = true;
					if (polys2.size() > 0) {
						List<HashMap<String, Object>> a = polys2;
						SubPolygonList1.addAll(SubPolygonList2);

						SubPolygonList1.addAll(clipPolys(SubPolygonList1, a, UnRedoPara));
						SubPolygonList1.addAll(a);
						// clipPolys(a,SubPolygonList1,UnRedoPara);
					}

					polys2.clear();
				} else {
					SubPolygonList1.add(hashMap2);
					// 在数据库中打上删除标识，方便以后恢复
					String SQL2_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
					SQL2_Del = String.format(SQL2_Del, gLayer.getDataset().getDataTableName(), gPoly.getSysId());
					if (gLayer.getDataset().getDataSource().ExcuteSQL(SQL2_Del)) {
						gPoly.setStatus(lkGeometryStatus.enDelete);

						// 回退区
						UnRedoDataItem urDataItem2 = new UnRedoDataItem();
						urDataItem2.Type = lkReUndoFlag.enUndo;
						IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
						urDelete2.LayerId = gLayer.getDataset().getId();
						urDelete2.ObjectIdList.add(gPoly.getSysId());
						urDataItem2.DataList.add(urDelete2);
						UnRedoPara.DataItemList.add(urDataItem2);
					}
				}
			}

			if (b1NoInter) {

			} else {
				HashMap<String, Object> newPoly4 = new HashMap<String, Object>();
				newPoly4.put("layer", gLayer);
				newPoly4.put("poly", gPoly);

				SubPolygonList1.add(newPoly4);
			}

			polys2.addAll(SubPolygonList1);
		}

		return SubPolygonList1;
	}

	public static boolean clipTwoPoly(ArrayList<HashMap<String, Object>> SubPolygonList1, GeoLayer aLayer,
			Polygon aPoly, GeoLayer pGeoLayer, Polygon pPolygon, UnRedoParaStru UnRedoPara) {
		HashMap<String, String> FeatureList = CopyFeature.CopyFrom(pGeoLayer.getDataset(), pPolygon.getSysId());

		boolean isReplated = false;
		for (Polygon p : PolyTools.RepatePoly(pPolygon, aPoly)) {
			if (p.getArea(true) > 0.0001) {
				isReplated = true;
				HashMap<String, Object> newPoly1 = new HashMap<String, Object>();
				newPoly1.put("layer", pGeoLayer);
				newPoly1.put("poly", p);
				v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
				gpsDataObj.SetDataset(pGeoLayer.getDataset());

				gpsDataObj.SetSYS_ID(-1);
				int newId1 = gpsDataObj.SaveGeoToDb(p, p.getLength(true), p.getArea(true));
				UnRedoDataItem urDataItem = new UnRedoDataItem();
				urDataItem.Type = lkReUndoFlag.enRedo;
				IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
				urDelete.LayerId = pGeoLayer.getDataset().getId();

				urDelete.ObjectIdList.add(newId1);
				if (CopyFeature.CopyTo(FeatureList, pGeoLayer.getDataset(), newId1)) {

				}

				urDataItem.DataList.add(urDelete);
				UnRedoPara.DataItemList.add(urDataItem);
				SubPolygonList1.add(newPoly1);
			}

		}

		if (isReplated) {
			for (Polygon p2 : PolyTools.ClipPoly(aPoly, pPolygon)) {
				HashMap<String, Object> newPoly2 = new HashMap<String, Object>();
				newPoly2.put("layer", pGeoLayer);
				newPoly2.put("poly", p2);
				v1_CGpsDataObject gpsDataObj2 = new v1_CGpsDataObject();
				gpsDataObj2.SetDataset(pGeoLayer.getDataset());

				gpsDataObj2.SetSYS_ID(-1);
				int newId2 = gpsDataObj2.SaveGeoToDb(p2, p2.getLength(true), p2.getArea(true));
				UnRedoDataItem urDataItem = new UnRedoDataItem();
				urDataItem.Type = lkReUndoFlag.enRedo;
				IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
				urDelete.LayerId = pGeoLayer.getDataset().getId();

				if (CopyFeature.CopyTo(FeatureList, pGeoLayer.getDataset(), newId2)) {

				}
				urDelete.ObjectIdList.add(newId2);
				urDataItem.DataList.add(urDelete);
				UnRedoPara.DataItemList.add(urDataItem);
				SubPolygonList1.add(newPoly2);

			}

			HashMap<String, String> FeatureList2 = CopyFeature.CopyFrom(aLayer.getDataset(), aPoly.getSysId());
			for (Polygon p3 : PolyTools.ClipPoly(pPolygon, aPoly)) {

				HashMap<String, Object> newPoly = new HashMap<String, Object>();
				newPoly.put("layer", aLayer);
				newPoly.put("poly", p3);
				v1_CGpsDataObject gpsDataObj3 = new v1_CGpsDataObject();
				gpsDataObj3.SetDataset(aLayer.getDataset());

				gpsDataObj3.SetSYS_ID(-1);
				int newId3 = gpsDataObj3.SaveGeoToDb(p3, p3.getLength(true), p3.getArea(true));
				UnRedoDataItem urDataItem = new UnRedoDataItem();
				urDataItem.Type = lkReUndoFlag.enRedo;
				IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
				urDelete.LayerId = aLayer.getDataset().getId();

				if (CopyFeature.CopyTo(FeatureList2, aLayer.getDataset(), newId3)) {

				}

				urDelete.ObjectIdList.add(newId3);
				urDataItem.DataList.add(urDelete);

				UnRedoPara.DataItemList.add(urDataItem);

				SubPolygonList1.add(newPoly);
			}

			// 在数据库中打上删除标识，方便以后恢复
			String SQL_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
			SQL_Del = String.format(SQL_Del, aLayer.getDataset().getDataTableName(), aPoly.getSysId());
			if (aLayer.getDataset().getDataSource().ExcuteSQL(SQL_Del)) {
				aPoly.setStatus(lkGeometryStatus.enDelete);

				// 回退区
				UnRedoDataItem urDataItem2 = new UnRedoDataItem();
				urDataItem2.Type = lkReUndoFlag.enUndo;
				IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
				urDelete2.LayerId = aLayer.getDataset().getId();
				urDelete2.ObjectIdList.add(aPoly.getSysId());
				urDataItem2.DataList.add(urDelete2);
				UnRedoPara.DataItemList.add(urDataItem2);
			}
		}

		return isReplated;
	}

	public static void StartClip() {
		// 判断是否有分割轨迹
		if (v1_Agent_DrawlineEx.GetDrawLine_Poly().GetTrackPointList().size() == 0) {
			lkmap.Tools.Tools.ShowMessageBox("请勾绘分割线！");
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

		if (selectObjectList.size() == 0) {
			lkmap.Tools.Tools.ShowMessageBox("请在可编辑图层中选择需要切割的面！");
			return;
		} else {
			UnRedoParaStru UnRedoPara = new UnRedoParaStru();
			UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;

			v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
			for (HashMap<String, Object> selObj : selectObjectList) {
				GeoLayer pGeoLayer = (GeoLayer) selObj.get("GeoLayer");
				int ObjIndex = Integer.parseInt(selObj.get("ObjIndex") + "");
				Polygon pPolygon = (Polygon) pGeoLayer.getDataset().GetGeometry(ObjIndex);

				List<Polygon> SubPolygonList = PolyTools
						.ClipPoly(v1_Agent_DrawlineEx.GetDrawLine_Poly().GetTrackPointList(), pPolygon);
				if (SubPolygonList.size() > 1) {
					// 属性复制
					HashMap<String, String> FeatureList = CopyFeature.CopyFrom(pGeoLayer.getDataset(), ObjIndex);

					// 回退区
					UnRedoDataItem urDataItem = new UnRedoDataItem();
					urDataItem.Type = lkReUndoFlag.enRedo;
					IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
					urDelete.LayerId = pGeoLayer.getDataset().getId();
					urDataItem.DataList.add(urDelete);

					List<Integer> newIDs=new ArrayList<Integer>();
					// 保存新分割的两个实体
					// String mjDanwei = "";
					gpsDataObj.SetDataset(pGeoLayer.getDataset());
					
					boolean hasSplited = false;
					int xibanMax = 0;
					String tubanhao = "";
					String dfTuBanHao = "";
					String dfHeShiXiBan = "";
					String dfShuiXuHao = "";
					
					v1_Layer layer;
					if (isBGLayer) {
						layer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
								.GetLayerByID(pGeoLayer.getId());
					} else {
						layer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId());
					}
					
					if (layer.GetLayerProjecType().equals(ForestryLayerType.DuChaYanZheng)) {
						dfTuBanHao = layer.GetDataFieldNameByFieldName("判读图斑编号");
						dfHeShiXiBan = layer.GetDataFieldNameByFieldName("核实细斑号");
						dfShuiXuHao = layer.GetDataFieldNameByFieldName("顺序号");

						if (FeatureList.containsKey(dfHeShiXiBan)) {
							if (FeatureList.get(dfHeShiXiBan) != null && FeatureList.get(dfHeShiXiBan).length() > 0) {
								hasSplited = true;
								xibanMax = CopyFeature.getSameIdTuban(pGeoLayer.getDataset(), dfShuiXuHao,
										FeatureList.get(dfShuiXuHao));
							}

							if (FeatureList.containsKey(dfTuBanHao)) {
								tubanhao = FeatureList.get(dfTuBanHao);
							}
						}

					}

					HashMap<String, String> newFeature = (HashMap<String, String>) FeatureList.clone();
					int index = 1;
					
					
					for (Polygon pPoly : SubPolygonList) {
						FeatureList = CopyFeature.CopyFrom(pGeoLayer.getDataset(), ObjIndex);
						gpsDataObj.SetSYS_ID(-1);
						int newId = gpsDataObj.SaveGeoToDb(pPoly, pPoly.getLength(true), pPoly.getArea(true));
						if (newId >= 0) {

//							v1_Layer layer;
//							if (isBGLayer) {
//								layer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
//										.GetLayerByID(pGeoLayer.getId());
//							} else {
//								layer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId());
//								
//								//为督查检查卡片更新用
//		    					newIDs.add(newId);
//							}

							// 如果是林地变更图层，按比例分配分割后面积
							// if
							// (layer.GetLayerProjecType().equals(ForestryLayerType.LindibiangengLayer))
							// {
							String mjDataField = layer.GetDataFieldNameByFieldName("面积");
							if (!mjDataField.isEmpty()) {
								String mj = FeatureList.get(mjDataField);

								try {
									double yuanMJ = Double.parseDouble(mj);

									double diliMJ = (pPoly.getArea(true) / pPolygon.getArea(true)) * yuanMJ;

									DecimalFormat df1 = new DecimalFormat("0.00");
									FeatureList.put(mjDataField, df1.format(diliMJ));
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
							// }

							if (layer.GetLayerProjecType().equals(ForestryLayerType.DuChaYanZheng)) {
								if (!hasSplited) {
									newFeature.put(dfHeShiXiBan, tubanhao + "-" + index);

								} else {
									if (index > 1) {
										int newIndex = xibanMax + index - 1;
										newFeature.put(dfHeShiXiBan, tubanhao + "-" + newIndex);
									}

								}

								index++;
							}
							
							if (CopyFeature.CopyTo(newFeature, pGeoLayer.getDataset(), newId))
								urDelete.ObjectIdList.add(newId);
						}
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
						
						
//						updateJCKP(urDelete2,pGeoLayer.getId(),ObjIndex,newIDs);
					}

					// 清空选择集合
					PubVar.m_Map.ClearSelection();
					PubVar.m_Map.Refresh();

					
				}
			}

			IUnRedo.AddHistory(UnRedoPara);
			v1_Agent_DrawlineEx.GetDrawLine_Poly().Cancel();
			
		
        	
//        	new Handler().post(new Runnable(){
//
//				@Override
//				public void run() {
//					updateJCKP(pGeoLayer.getId(),ObjIndex,newIDs);
//					
//				}
//        		
//        	});
			
			PubVar.m_Map.Refresh();
		}

	}
	
	  //更新督查检查卡片表
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
