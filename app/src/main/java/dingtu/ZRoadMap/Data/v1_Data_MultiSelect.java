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
		_Dialog.SetCaption("�����б�");
		_Dialog.ReSetSize(1f, 0.96f);
	}

	/**
	 * ���ر�ѡ��ʵ���б���Ϣ
	 */
	private void LoadSelectObjectListInfo() {
		// �󶨹����б�
		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "��ʵ�������б�", new ICallback() {

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if (Str.equals("�б�ѡ��")) {
					// �򿪹������Դ���
					HashMap<String, Object> SO = (HashMap<String, Object>) ExtraStr;
					// PubVar.m_Map.ClearSelection();
					// GeoLayer tGeoLayer =
					// PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(SO.get("LayerID")+"");
					// if (tGeoLayer == null) return;
					// tGeoLayer.getSelSelection().Add(Integer.valueOf(SO.get("ObjectID")+""));
					// PubVar.m_DoEvent.DoCommand("����1");

					if (SO.get("D2").toString() == "��") {
						PubVar.m_DoEvent.m_GPSPoint.Edit((String) SO.get("LayerID"),
								Integer.valueOf(SO.get("ObjectID").toString()));
						PubVar.m_Map.ClearSelection();
						PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(SO.get("LayerID") + "")
								.getSelSelection().Add(Integer.valueOf(SO.get("ObjectID").toString()));
						_Dialog.dismiss();
					}
					if (SO.get("D2").toString() == "��") {
						PubVar.m_DoEvent.m_GPSLine.Edit((String) SO.get("LayerID"),
								Integer.valueOf(SO.get("ObjectID").toString()));
						PubVar.m_Map.ClearSelection();
						PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(SO.get("LayerID") + "")
								.getSelSelection().Add(Integer.valueOf(SO.get("ObjectID").toString()));
						_Dialog.dismiss();
					}
					if (SO.get("D2").toString() == "��") {
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

		// ��ȡѡ������Щʵ��
		List<HashMap<String, Object>> m_HeaderListViewDataItemList = new ArrayList<HashMap<String, Object>>();

		List<GeoLayer> GeoLayerList = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).getList();
		for (GeoLayer pGeoLayer : GeoLayerList) {
			if (pGeoLayer.getSelSelection().getCount() == 0)
				continue;

			// ��ȡ�ɲ�ѯID
			List<Integer> queryIDList = new ArrayList<Integer>();
			for (int Index : pGeoLayer.getSelSelection().getGeometryIndexList()) {
				queryIDList.add(Index);
			}

			// ��ȡ�ɲ�ѯ���ֶ�
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

			// ��ѯʵ������
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

				// �����ݿ������ID��
				int DIndex = Integer.parseInt(DR.GetString("SYS_ID") + "");

				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("LayerID", vLayer.GetLayerID()); // ͼ��ID
				hm.put("ObjectID", DIndex); // ʵ��ID
				hm.put("D1", vLayer.GetLayerAliasName()); // ͼ������
				hm.put("D2", vLayer.GetLayerTypeName()); // ͼ������
				hm.put("D3", Tools.JoinT(",", featureList)); // ���Լ��
				m_HeaderListViewDataItemList.add(hm);
			}
			DR.Close();

		}
		hvf.BindDataToListView(m_HeaderListViewDataItemList);
	}

	public void ShowDialog() {
		// �˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
		_Dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				LoadSelectObjectListInfo();
			}
		});
		_Dialog.show();
	}
}
