package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.FieldInfo;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Map.Param;

public class v1_Data_Back_Feature {
	private v1_FormTemplate _Dialog = null;

	public v1_Data_Back_Feature() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.v1_data_back_featurelist);
		_Dialog.ReSetSize(0.5f, 0.96f);
		_Dialog.SetCaption("属性列表");
		_Dialog.SetCallback(_returnCallback);
	}

	private ICallback _returnCallback = null;

	public void SetCallback(ICallback cb) {
		this._returnCallback = cb;
	}

	// 查询实体的参数 ，格式：LayerId,SYS_ID
	private HashMap<String, Object> _QueryObj = null;

	/**
	 * 设置查询实体
	 * 
	 * @param queryObj
	 */
	public void SetQueryObj(HashMap<String, Object> queryObj) {
		this._QueryObj = queryObj;
	}

	// 弹出属性查询对话框
	private void QueryFeature() {
		List<Map<String, Object>> FeatureList = new ArrayList<Map<String, Object>>();

		String LayerId = "", SYS_ID = "";
		if (this._QueryObj != null) {
			LayerId = this._QueryObj.get("LayerId") + "";
			SYS_ID = this._QueryObj.get("SYS_ID") + "";
		} else {
			Param GeoLayerName = new Param();
			Param DIndex = new Param();
			if (!lkmap.Tools.Tools.GetSelectOneObjectInfo(GeoLayerName, DIndex))
				return;
			LayerId = GeoLayerName.getStringValue();
			SYS_ID = DIndex.getInt() + "";
		}

		if (PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).GetLayerById(LayerId) == null) {
			_Dialog.dismiss();
			return;
		}

		Dataset pDataset = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).GetLayerById(LayerId)
				.getDataset();

		String SQL = "select * from " + pDataset.getDataTableName() + " where SYS_ID = " + SYS_ID;
		List<FieldInfo> FieldList = pDataset.getTableStruct();
		SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
		if (DR != null) {
			while (DR.Read()) {
				for (FieldInfo FI : FieldList) {
					String v = FI.getName();
					if (FI.getType())
						continue;

					Map<String, Object> feature = new HashMap<String, Object>();
					feature.put("Field", FI.getCaption() + "：");
					feature.put("Value", " " + DR.GetString(FI.getName()));
					FeatureList.add(feature);
				}
			}
			DR.Close();
		}

		SimpleAdapter adapter = new SimpleAdapter(_Dialog.getContext(), FeatureList, R.layout.v1_bk_backdatalist,
				new String[] { "Field", "Value" }, new int[] { R.id.tv_caption, R.id.tv_value });
		(((ListView) _Dialog.findViewById(R.id.lvFeatureList))).setAdapter(adapter);
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
