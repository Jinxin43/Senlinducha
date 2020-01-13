package lkmap.ZRoadMap.ToolsBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
import dingtu.ZRoadMap.PubVar;

public class v1_UserConfigDB_PolyAnalysisOption {
	public List<HashMap<String, Object>> GetPolyAnalysisOption() {
		HashMap<String, String> optionList = PubVar.m_DoEvent.m_UserConfigDB.GetUserParam()
				.GetUserPara("Poly_Analysis_Option");
		if (optionList == null)
			return new ArrayList<HashMap<String, Object>>();
		String optionJSONStr = optionList.get("F2");

		try {
			List<HashMap<String, Object>> optItemList = new ArrayList<HashMap<String, Object>>();
			JSONTokener jsonParser = new JSONTokener(optionJSONStr);
			JSONObject AllLayerJSON = (JSONObject) jsonParser.nextValue();

			// 接下来的就是JSON对象的操作了
			JSONArray lyrJSONList = AllLayerJSON.getJSONArray("All");
			for (int i = 0; i < lyrJSONList.length(); i++) {
				HashMap<String, Object> optItem = new HashMap<String, Object>();
				JSONObject lyrJSON = lyrJSONList.getJSONObject(i);
				optItem.put("LayerName", lyrJSON.getString("LayerName"));
				optItem.put("LayerId", lyrJSON.getString("LayerId"));
				optItem.put("FieldNameList", this.JSONArrayToList(lyrJSON.getJSONArray("FieldNameList")));
				optItem.put("FieldCaptionList", this.JSONArrayToList(lyrJSON.getJSONArray("FieldCaptionList")));
				optItemList.add(optItem);
			}
			return optItemList;
		} catch (JSONException ex) {
			return null;
		}
	}

	public boolean SavePolyAnalysisOption(List<HashMap<String, Object>> optItemList) {
		try {
			JSONObject optJSON = new JSONObject();
			JSONArray jsonAry = new JSONArray();
			for (HashMap<String, Object> optItem : optItemList) {
				JSONObject optItemJSON = new JSONObject();
				optItemJSON.put("LayerName", optItem.get("LayerName") + "");
				optItemJSON.put("LayerId", optItem.get("LayerId") + "");
				optItemJSON.put("FieldNameList", this.ListToJSONArray((List<String>) optItem.get("FieldNameList")));
				optItemJSON.put("FieldCaptionList",
						this.ListToJSONArray((List<String>) optItem.get("FieldCaptionList")));
				jsonAry.put(optItemJSON);
			}
			optJSON.put("All", jsonAry);

			String optJSONStr = optJSON.toString();
			HashMap<String, String> newOptItem = new HashMap<String, String>();
			newOptItem.put("F2", optJSONStr);
			return PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().SaveUserPara("Poly_Analysis_Option", newOptItem);

		} catch (JSONException ex) {
			Log.e("SavePolyAnalysisOption", ex.getMessage());
			return false;
		}
	}

	private JSONArray ListToJSONArray(List<String> list) {
		JSONArray jsList = new JSONArray();
		for (String str : list)
			jsList.put(str);
		return jsList;
	}

	private List<String> JSONArrayToList(JSONArray jsList) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < jsList.length(); i++) {
			try {
				list.add(jsList.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
}
