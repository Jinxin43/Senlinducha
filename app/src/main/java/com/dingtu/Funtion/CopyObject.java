package com.dingtu.Funtion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import dingtu.ZRoadMap.Data.v1_CGpsPoint;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.ListView;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class CopyObject {
	private v1_FormTemplate DialogView = null;
	private List<HashMap<String, Object>> layerList = new ArrayList<HashMap<String, Object>>();
	private SelectLayerCopytoAdapter copytoLayer = null;
	private List<GeoLayer> fromList = new ArrayList<GeoLayer>();

	public CopyObject() {
		boolean hasPoly = false;
		boolean hasLine = false;
		boolean hasPoint = false;
		int SelectObectCount = 0;

		for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).getList()) {
			if (pGeoLayer.getSelSelection().getCount() > 0) {
				fromList.add(pGeoLayer);
				SelectObectCount += pGeoLayer.getSelSelection().getCount();
				if (pGeoLayer.getType() == lkGeoLayerType.enPoint) {
					hasPoint = true;
				}
				if (pGeoLayer.getType() == lkGeoLayerType.enPolyline) {
					hasLine = true;
				}
				if (pGeoLayer.getType() == lkGeoLayerType.enPolygon) {
					hasPoly = true;
				}
			}
		}

		if (SelectObectCount == 0) {
			Tools.ShowMessageBox("没有选择任何对象，无法复制！");
			return;
		}

		if (hasLine && hasPoint) {
			Tools.ShowMessageBox("不能同时选择点和线对象！");
			return;
		}

		if (hasPoly && hasPoint) {
			Tools.ShowMessageBox("不能同时选择点和面对象！");
			return;
		}

		for (v1_Layer layer : PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList()) {
			if (hasPoint) {
				if (layer.GetLayerType() == lkGeoLayerType.enPoint) {
					HashMap<String, Object> feature = new HashMap<String, Object>();
					feature.put("LayerID", layer.GetLayerID());
					feature.put("D1", layer.GetLayerAliasName());
					feature.put("D2", "点");
					feature.put("D3", layer.GetLayerProjecType());
					layerList.add(feature);
				}
			}

			if (hasPoly || hasLine) {
				if (layer.GetLayerType() == lkGeoLayerType.enPolyline
						|| layer.GetLayerType() == lkGeoLayerType.enPolygon) {
					HashMap<String, Object> feature = new HashMap<String, Object>();
					feature.put("LayerID", layer.GetLayerID());
					feature.put("D1", layer.GetLayerAliasName());
					if (layer.GetLayerType() == lkGeoLayerType.enPolyline) {
						feature.put("D2", "线");
					} else {
						feature.put("D2", "面");
					}
					feature.put("D3", layer.GetLayerProjecType());
					layerList.add(feature);
				}
			}

		}

		DialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		DialogView.SetOtherView(R.layout.d_selectcopytolayer);
		DialogView.ReSetSize(0.6f, 0.7f);
		DialogView.SetCaption("选择目标图层");
		// TODO：添加可选择图层
		DialogView.show();

		copytoLayer = new SelectLayerCopytoAdapter(DialogView.getContext(), layerList, R.layout.c_fz_copytolayeritem,
				new String[] { "D1", "D2", "D3" }, new int[] { R.id.tvtcmc, R.id.tvlx, R.id.tvgclx });

		ListView copytoLayers = (ListView) DialogView.findViewById(R.id.listView1);
		copytoLayers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				if (pCallBack != null) {
					pCallBack.OnClick("", arg2);
				}

			}
		});
		copytoLayers.setAdapter(copytoLayer);
		copytoLayer.notifyDataSetInvalidated();

	}

	ICallback pCallBack = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			int copiedCount = 0;
			Dataset toDataSet = PubVar.m_Workspace
					.GetDatasetById(layerList.get(Integer.valueOf(ExtraStr + "")).get("LayerID") + "");

			v1_CGpsDataObject dataObject = new v1_CGpsDataObject();
			dataObject.SetDataset(toDataSet);
			dataObject.SetSYS_TYPE("对象复制");
			dataObject.SetSYS_STATUS("0");

			for (GeoLayer fromLayer : fromList) {
				if (toDataSet.getType() == lkGeoLayerType.enPoint) {
					v1_CGpsPoint cGPSPoint = new v1_CGpsPoint();
					cGPSPoint.SetDataset(PubVar.m_Workspace
							.GetDatasetById(layerList.get(Integer.valueOf(ExtraStr + "")).get("LayerID") + ""));
					for (int sysID : fromLayer.getSelSelection().getGeometryIndexList()) {
						Point p = (Point) fromLayer.getSelSelection().getDataset().GetGeometry(sysID).Clone();
						int objID = cGPSPoint.SaveGeoToDb(p.getCoordinate(), "选项复制");
						if (objID != -1) {
							copiedCount++;
							SaveFeature(fromLayer.getId(), sysID, toDataSet.getId(), objID);
						}
					}
				} else {
					if (fromLayer.getType() == lkGeoLayerType.enPolyline) {
						for (int sysID : fromLayer.getSelSelection().getGeometryIndexList()) {
							Geometry gemmetry = fromLayer.getSelSelection().getDataset().GetGeometry(sysID).Clone();

							dataObject.SetSYS_ID(-1);
							int objID = dataObject.SaveGeoToDb(gemmetry, ((Polyline) gemmetry).getLength(true), 0);
							if (objID != -1) {
								copiedCount++;
								SaveFeature(fromLayer.getId(), sysID, toDataSet.getId(), objID);
							}
						}
					}

					if (fromLayer.getType() == lkGeoLayerType.enPolygon) {
						for (int sysID : fromLayer.getSelSelection().getGeometryIndexList()) {
							Geometry gemmetry = fromLayer.getSelSelection().getDataset().GetGeometry(sysID).Clone();
							dataObject.SetSYS_ID(-1);
							int toObjId = dataObject.SaveGeoToDb(gemmetry, ((Polygon) gemmetry).getLength(true),
									((Polygon) gemmetry).getArea(true));
							if (toObjId != -1) {
								copiedCount++;
								SaveFeature(fromLayer.getId(), sysID, toDataSet.getId(), toObjId);
							}
						}
					}

				}

			}
			PubVar.m_Map.ClearSelection();
			PubVar.m_Map.Refresh();
			Tools.ShowToast(PubVar.m_MapControl.getContext(), copiedCount + "个对象被复制到所选图层！");
			DialogView.dismiss();
		}
	};

	private void SaveFeature(String flayerID, int objID, String tLayerID, int toObjId) {
		try {
			List<String> FeatureList = new ArrayList<String>();
			v1_CGpsDataObject fromDataObject = new v1_CGpsDataObject();
			fromDataObject.SetDataset(PubVar.m_Workspace.GetDatasetById(flayerID));
			v1_Layer fLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(flayerID);
			if (fLayer == null) {
				fLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
						.GetLayerByID(flayerID);
			}
			v1_Layer toLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(tLayerID);
			if (fLayer == null) {
				Tools.ShowMessageBox("找不到来源层属性！");
				return;
			}

			if (toLayer == null) {
				Tools.ShowMessageBox("找不到目标层属性！");
				return;
			}

			HashMap<String, String> matchDataFileds = new HashMap<String, String>();
			for (v1_LayerField tField : toLayer.GetFieldList()) {

				for (v1_LayerField fField : fLayer.GetFieldList()) {
					boolean isFind = false;
					if (tField.GetFieldName().equals(fField.GetFieldName())) {
						matchDataFileds.put(fField.GetDataFieldName(), tField.GetDataFieldName());
						isFind = true;
						break;
					}
					if (!isFind) {
						if (tField.GetFieldShortName().equals(fField.GetFieldName())) {
							matchDataFileds.put(fField.GetDataFieldName(), tField.GetDataFieldName());
							isFind = true;
							break;
						}
					}

					if (!isFind) {
						if (tField.GetFieldName().equals(fField.GetFieldShortName())) {
							matchDataFileds.put(fField.GetDataFieldName(), tField.GetDataFieldName());
							isFind = true;
							break;
						}
					}
				}
			}

			HashMap<String, Object> fFieldsValue = fromDataObject.ReadDataAllFieldsValue(objID);
			for (String key : fFieldsValue.keySet()) {
				if (matchDataFileds.containsKey(key)) {
					FeatureList.add(matchDataFileds.get(key) + "='" + fFieldsValue.get(key) + "'");
				}
			}

			String SQL = "update %1$s set %2$s where SYS_ID=%3$s";
			SQL = String.format(SQL, toLayer.GetDataTableName(), Tools.JoinT(",", FeatureList), toObjId);

			if (FeatureList.size() > 0) {
				if (!PubVar.m_Workspace.GetDatasetById(tLayerID).getDataSource().ExcuteSQL(SQL)) {
					Tools.ShowToast(DialogView.getContext(), "复制属性到目标图层失败！");
				}
			}

		} catch (Exception ex) {
			Tools.ShowToast(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}
	}
}
