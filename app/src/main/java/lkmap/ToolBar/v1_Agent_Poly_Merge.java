package lkmap.ToolBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import lkmap.Cargeometry.Polygon;
import lkmap.Edit.CopyFeature;
import lkmap.Edit.DeleteAddObject;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Layer.GeoLayer;
import lkmap.Spatial.PolyTools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;

public class v1_Agent_Poly_Merge implements v1_IToolsBarCommand {

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
		this.m_View.findViewById(R.id.bt_merge).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_blast).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_undo).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_redo).setOnClickListener(new ViewClick());
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
				// StartPublicBorder();
			}

			// if (Tag.equals("删除"))Delete();
			//
			if (Tag.equals("回退")) {
				PubVar.m_DoEvent.DoCommand(Tag);
			}
			if (Tag.equals("重做")) {
				PubVar.m_DoEvent.DoCommand(Tag);
			}
		}
	}

	// 开始合并操作
	public static void StartMerge() {
		// 判断是否有面层被选中
		if (!PubVar.m_DoEvent.AlwaysOpenProject())
			return;
		if (!PubVar.m_DoEvent.m_GPSPoly.getGPSLine().CheckLayerValid())
			return;

		final List<HashMap<String, Object>> SelectObjList = GetSelectPolygon();
		if (SelectObjList.size() == 0) {
			lkmap.Tools.Tools.ShowMessageBox("请选择需要合并的面！");
			return;
		}
		if (SelectObjList.size() == 1) {
			lkmap.Tools.Tools.ShowMessageBox("单个面无法进行合并操作！");
			return;
		}

		List<Polygon> SelectPolygonList = new ArrayList<Polygon>();
		for (HashMap<String, Object> SelectObj : SelectObjList) {
			SelectPolygonList.add((Polygon) SelectObj.get("Geometry"));
		}

		// 开始合并操作
		final Polygon mergePolygon = PolyTools.MergePolygon(SelectPolygonList);
		if (mergePolygon == null)
			return;
		mergePolygon.Closed();

		// 弹出属性合并窗体
		v1_Agent_Poly_Merge_Feature apm = new v1_Agent_Poly_Merge_Feature();
		apm.SetQueryObj(SelectObjList);
		apm.SetICallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {

				// 属性实体
				HashMap<String, Object> SelectObjEx = (HashMap<String, Object>) ExtraStr;
				String FeatureLayerId = SelectObjEx.get("LayerId") + "";
				int FeatureObjId = Integer.parseInt(SelectObjEx.get("ObjectId") + "");
				HashMap<String, String> FeatureList = CopyFeature
						.CopyFrom(PubVar.m_Workspace.GetDatasetById(FeatureLayerId), FeatureObjId);

				// 回退区
				UnRedoParaStru UnRedoPara = new UnRedoParaStru();
				UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;

				UnRedoDataItem urDataItem = new UnRedoDataItem();
				urDataItem.Type = lkReUndoFlag.enRedo;
				UnRedoPara.DataItemList.add(urDataItem);

				IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
				urDataItem.DataList.add(urDelete);
				urDelete.LayerId = PubVar.m_DoEvent.m_GPSPoly.getGPSLine().GetDataset().getId();

				// 加新合并后新实体
				v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
				gpsDataObj.SetDataset(PubVar.m_DoEvent.m_GPSPoly.getGPSLine().GetDataset());
				gpsDataObj.SetSYS_ID(-1);
				int newId = gpsDataObj.SaveGeoToDb(mergePolygon, mergePolygon.getLength(true),
						mergePolygon.getArea(true));
				if (newId >= 0)
					if (CopyFeature.CopyTo(FeatureList, PubVar.m_DoEvent.m_GPSPoly.getGPSLine().GetDataset(), newId))
						urDelete.ObjectIdList.add(newId);

				// 删除旧实体
				UnRedoDataItem urDataItem2 = new UnRedoDataItem();
				urDataItem2.Type = lkReUndoFlag.enUndo;

				List<IURDataItem_DeleteAdd> urDAList = new ArrayList<IURDataItem_DeleteAdd>();
				for (HashMap<String, Object> SelectObj : SelectObjList) {
					IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
					urDelete2.LayerId = SelectObj.get("LayerId") + "";
					urDelete2.ObjectIdList.add(Integer.parseInt(SelectObj.get("ObjectId") + ""));
					urDataItem2.DataList.add(urDelete2);
					urDAList.add(urDelete2);
				}

				// 删除实体
				DeleteAddObject DA = new DeleteAddObject();
				DA.Delete(urDAList, false);

				UnRedoPara.DataItemList.add(urDataItem2);
				IUnRedo.AddHistory(UnRedoPara);

				v1_Agent_DrawlineEx.GetDrawLine_Poly().Cancel();

				// 清空选择集合
				PubVar.m_Map.ClearSelection();
				PubVar.m_Map.Refresh();
			}
		});
		apm.ShowDialog();
	}

	private static List<HashMap<String, Object>> GetSelectPolygon() {
		List<HashMap<String, Object>> SelectObjList = new ArrayList<HashMap<String, Object>>();

		boolean isBGLayer = true;

		// 判断是否有已经选中的面
		for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList()) {
			if (pGeoLayer.getType() != lkGeoLayerType.enPolygon)
				continue;
			if (pGeoLayer.getSelSelection().getCount() > 0) {
				isBGLayer = false;
				for (int ObjIndex : pGeoLayer.getSelSelection().getGeometryIndexList()) {
					Polygon pPolygon = (Polygon) pGeoLayer.getDataset().GetGeometry(ObjIndex);
					HashMap<String, Object> SelectObj = new HashMap<String, Object>();
					SelectObj.put("LayerId", pGeoLayer.getDataset().getId());
					SelectObj.put("ObjectId", ObjIndex);
					SelectObj.put("Geometry", pPolygon);
					SelectObjList.add(SelectObj);
				}
			}
		}

		if (isBGLayer) {
			for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).getList()) {
				for (int ObjIndex : pGeoLayer.getSelSelection().getGeometryIndexList()) {
					Polygon pPolygon = (Polygon) pGeoLayer.getDataset().GetGeometry(ObjIndex);
					HashMap<String, Object> SelectObj = new HashMap<String, Object>();
					SelectObj.put("LayerId", pGeoLayer.getDataset().getId());
					SelectObj.put("ObjectId", ObjIndex);
					SelectObj.put("Geometry", pPolygon);
					SelectObjList.add(SelectObj);
				}
			}
		}
		return SelectObjList;
	}

}
