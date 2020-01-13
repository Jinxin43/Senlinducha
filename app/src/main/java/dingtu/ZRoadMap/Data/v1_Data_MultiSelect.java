package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.FieldInfo;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class v1_Data_MultiSelect {
	private v1_FormTemplate _Dialog = null;

	public v1_Data_MultiSelect() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.v1_data_multiselect);
		_Dialog.SetCaption("属性列表");
		_Dialog.ReSetSize(1f, 0.96f);
	}

	/**
	 * 加载被选择实体列表信息
	 */
	private void LoadSelectObjectListInfo() {
		// 绑定工程列表
		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "多实体属性列表", new ICallback() {

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if (Str.equals("列表选项")) {
					// 打开工程属性窗体
					HashMap<String, Object> SO = (HashMap<String, Object>) ExtraStr;
					// PubVar.m_Map.ClearSelection();
					// GeoLayer tGeoLayer =
					// PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(SO.get("LayerID")+"");
					// if (tGeoLayer == null) return;
					// tGeoLayer.getSelSelection().Add(Integer.valueOf(SO.get("ObjectID")+""));
					// PubVar.m_DoEvent.DoCommand("属性1");

					if (SO.get("D2").toString() == "点") {
						PubVar.m_DoEvent.m_GPSPoint.Edit((String) SO.get("LayerID"),
								Integer.valueOf(SO.get("ObjectID").toString()));
						PubVar.m_Map.ClearSelection();
						PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(SO.get("LayerID") + "")
								.getSelSelection().Add(Integer.valueOf(SO.get("ObjectID").toString()));
						_Dialog.dismiss();
					}
					if (SO.get("D2").toString() == "线") {
						PubVar.m_DoEvent.m_GPSLine.Edit((String) SO.get("LayerID"),
								Integer.valueOf(SO.get("ObjectID").toString()));
						PubVar.m_Map.ClearSelection();
						PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(SO.get("LayerID") + "")
								.getSelSelection().Add(Integer.valueOf(SO.get("ObjectID").toString()));
						_Dialog.dismiss();
					}
					if (SO.get("D2").toString() == "面") {
						// m_GPSPoly.Edit(pDataset.getId(),SYSID.getInt());
						PubVar.m_DoEvent.m_GPSPoly.Edit((String) SO.get("LayerID"),
								Integer.valueOf(SO.get("ObjectID").toString()));
						PubVar.m_Map.ClearSelection();
						PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(SO.get("LayerID") + "")
								.getSelSelection().Add(Integer.valueOf(SO.get("ObjectID").toString()));
						_Dialog.dismiss();
					}
					// else {
					// if (PubVar.VectorBGEditable) {
					// VectorDataFeature vdF = new VectorDataFeature((String)
					// SO.get("LayerID"),
					// Integer.valueOf(SO.get("ObjectID").toString()));
					// } else {
					v1_Data_Back_Feature v1dbf = new v1_Data_Back_Feature();
					v1dbf.ShowDialog();
					// }
					//
					// }

					PubVar.m_Map.FastRefresh();
				}
			}
		});

		// 读取选中了哪些实体
		List<HashMap<String, Object>> m_HeaderListViewDataItemList = new ArrayList<HashMap<String, Object>>();

		List<GeoLayer> GeoLayerList = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).getList();
		for (GeoLayer pGeoLayer : GeoLayerList) {
			if (pGeoLayer.getSelSelection().getCount() == 0)
				continue;

			// 提取可查询ID
			List<Integer> queryIDList = new ArrayList<Integer>();
			for (int Index : pGeoLayer.getSelSelection().getGeometryIndexList()) {
				queryIDList.add(Index);
			}

			// 提取可查询的字段
			v1_Layer vLayer = null;
			vLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId());
			if (vLayer == null) {
				vLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
						.GetLayerByID(pGeoLayer.getId());
				if (vLayer.GetFieldList().size() == 0) {
					List<FieldInfo> FieldList = pGeoLayer.getDataset().getTableStruct();
					List<v1_LayerField> LFLis = new ArrayList<v1_LayerField>();
					for (FieldInfo FI : FieldList) {
						if (FI.getType())
							continue;
						v1_LayerField LF = new v1_LayerField();
						LF.SetFieldName(FI.getCaption());
						LF.SetDataFieldName(FI.getName());
						LFLis.add(LF);
					}
					vLayer.SetFieldList(LFLis);
				}
			}
			if (vLayer == null)
				continue;
			List<String> queryFieldList = new ArrayList<String>();
			for (v1_LayerField VF : vLayer.GetFieldList()) {
				queryFieldList.add(VF.GetDataFieldName());
			}

			// 查询实体属性
			String SQL = "select SYS_ID,%1$s from %2$s  where (SYS_ID) in (%3$s)";
			SQL = String.format(SQL, Tools.JoinT(",", queryFieldList), pGeoLayer.getDataset().getDataTableName(),
					Tools.Join(",", queryIDList));
			SQLiteDataReader DR = pGeoLayer.getDataset().getDataSource().Query(SQL);
			if (DR == null)
				continue;
			while (DR.Read()) {
				List<String> featureList = new ArrayList<String>();
				for (String queryField : queryFieldList) {
					String FCaption = vLayer.GetFieldNameByDataFieldName(queryField);
					String FValue = DR.GetString(queryField);
					if (FValue == null)
						FValue = "";
					if (!FValue.equals(""))
						featureList.add(FCaption + "=" + FValue);
					if (featureList.size() >= 3)
						break;
				}

				// 与数据库关联的ID号
				int DIndex = Integer.parseInt(DR.GetString("SYS_ID") + "");

				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("LayerID", vLayer.GetLayerID()); // 图层ID
				hm.put("ObjectID", DIndex); // 实体ID
				hm.put("D1", vLayer.GetLayerAliasName()); // 图层名称
				hm.put("D2", vLayer.GetLayerTypeName()); // 图层类型
				hm.put("D3", Tools.JoinT(",", featureList)); // 属性简介
				m_HeaderListViewDataItemList.add(hm);
			}
			DR.Close();

		}
		hvf.BindDataToListView(m_HeaderListViewDataItemList);
	}

	public void ShowDialog() {
		// 此处这样做的目的是为了计算控件的尺寸
		_Dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				LoadSelectObjectListInfo();
			}
		});
		_Dialog.show();
	}
}
