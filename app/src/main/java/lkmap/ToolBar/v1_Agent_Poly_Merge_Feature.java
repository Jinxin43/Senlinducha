package lkmap.ToolBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.ListView;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_ListEx_Adpter;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class v1_Agent_Poly_Merge_Feature {
	private v1_FormTemplate _Dialog = null;

	public v1_Agent_Poly_Merge_Feature() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.v1_agent_poly_merge_feature);
		_Dialog.ReSetSize(0.5f, 0.5f);
		_Dialog.SetCaption("合并后属性");
	}

	private ICallback m_Callback = null;

	public void SetICallback(ICallback callBack) {
		this.m_Callback = callBack;
	}

	// 查询实体的参数 ，格式：LayerId,SYS_ID
	private List<HashMap<String, Object>> m_QueryObjList = null;

	/**
	 * 设置查询实体
	 * 
	 * @param queryObj
	 */
	public void SetQueryObj(List<HashMap<String, Object>> queryObjList) {
		this.m_QueryObjList = queryObjList;
	}

	// 填充属性查询对话框
	private List<HashMap<String, Object>> m_FeatureList = null;

	private void QueryFeature() {
		this.m_FeatureList = new ArrayList<HashMap<String, Object>>();

		for (HashMap<String, Object> SelectObj : this.m_QueryObjList) {
			List<String> ValueList = new ArrayList<String>();
			String LayerId = SelectObj.get("LayerId") + "";
			String ObjectId = SelectObj.get("ObjectId") + "";
			Geometry pGeometry = (Geometry) SelectObj.get("Geometry");
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(LayerId);
			String SQL = "select * from " + pDataset.getDataTableName() + " where SYS_ID = " + ObjectId;

			v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(LayerId);
			if (pLayer == null) {
				pLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
						.GetLayerByID(LayerId);
			}
			List<v1_LayerField> fieldList = pLayer.GetFieldList();
			SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
			if (DR != null) {
				if (DR.Read()) {
					for (v1_LayerField FI : fieldList) {
						ValueList.add(FI.GetFieldName() + "：" + DR.GetString(FI.GetDataFieldName()));
					}
				}
				DR.Close();
			}
			HashMap<String, Object> feature = new HashMap<String, Object>();
			feature.put("ValueList", Tools.JoinT(",", ValueList));
			feature.put("LayerName", pLayer.GetLayerAliasName());

			String AreaStr = "";
			if (pGeometry.GetType() == lkGeoLayerType.enPolygon) {
				AreaStr = Tools.ReSetArea(((Polygon) pGeometry).getArea(true), true);
			} else {
				AreaStr = Tools.ReSetDistance(((Polyline) pGeometry).getLength(true), true);
			}
			feature.put("Area", AreaStr);

			this.m_FeatureList.add(feature);
		}

		v1_ListEx_Adpter adapter = new v1_ListEx_Adpter(_Dialog.getContext(), this.m_FeatureList,
				R.layout.v1_bk_agent_poly_merge_feautre, new String[] { "LayerName", "Area", "ValueList" },
				new int[] { R.id.tvLayerName, R.id.tvArea, R.id.tvFeature });
		ListView lvList = (ListView) _Dialog.findViewById(R.id.listView1);
		lvList.setAdapter(adapter);
		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				ListView lvList = (ListView) arg0;
				((v1_ListEx_Adpter) lvList.getAdapter()).SetSelectItemIndex(arg2);
				((v1_ListEx_Adpter) lvList.getAdapter()).notifyDataSetChanged();

				HashMap<String, Object> selectObj = m_FeatureList.get(arg2);

				// 给出确认提示
				Tools.ShowYesNoMessage(_Dialog.getContext(), "是否按以下属性赋值？\r\n" + selectObj.get("ValueList"),
						new ICallback() {

							@Override
							public void OnClick(String Str, Object ExtraStr) {
								if (m_Callback != null) {
									m_Callback.OnClick("", m_QueryObjList.get(arg2));
									_Dialog.dismiss();
								}
							}
						});
			}
		});

	}

	public void ShowDialog() {
		// 此处这样做的目的是为了计算控件的尺寸
		_Dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				QueryFeature();
			}
		});
		_Dialog.show();
	}
}
