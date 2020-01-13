package com.dingtu.DTGIS.TuiGeng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.UserConfigDB;
import com.dingtu.senlinducha.R;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import lkmap.Tools.Tools;

public class TuiGengData_ZLSZ {
	private v1_FormTemplate dialogView = null;
	private List<HashMap<String, Object>> szSource = new ArrayList<HashMap<String, Object>>();
	private List<HashMap<String, Object>> cxSource = new ArrayList<HashMap<String, Object>>();
	private MultiAutoCompleteTextView mutliAutoSelectSZ;

	public TuiGengData_ZLSZ(String curValue) {
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.tuigengdata_sheji_zlsz_select);
		dialogView.ReSetSize(0.5f, 0.9f);
		dialogView.SetCaption("选择造林树种");
		dialogView.SetButtonInfo("1," + R.drawable.v1_ok + ",确定  ,确定", pCallback);
		mutliAutoSelectSZ = (MultiAutoCompleteTextView) dialogView.findViewById(R.id.id_multiTextViewSZ);
		((TextView) dialogView.findViewById(R.id.tv_selectSZ)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String selsecedString = mutliAutoSelectSZ.getText().toString();
				System.out.println(mutliAutoSelectSZ.getText().toString());
				String[] selectedSZ = selsecedString.split(",");
				String result = Tools.GetTextValueOnID(dialogView, R.id.tv_select);
				for (String sz : selectedSZ) {
					sz = sz.replace(" ", "");
					if (sz != null && sz.length() > 0) {
						if (sz.contains("(")) {
							sz = sz.substring(0, sz.indexOf("("));
						}

						if (result.length() > 0) {
							result += "+" + sz;
						} else {
							result = sz;
						}
					}
				}
				Tools.SetTextViewValueOnID(dialogView, R.id.tv_select, result);
			}
		});

		((TextView) dialogView.findViewById(R.id.tv_ClearSZ)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Tools.SetTextViewValueOnID(dialogView, R.id.tv_select, "");

			}
		});
		initZLSZList(curValue);

	}

	private ICallback mCallback = null;

	public void SetCallback(ICallback cb) {
		this.mCallback = cb;
	}

	private ICallback pCallback = new ICallback() {
		@SuppressLint("NewApi")
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("确定")) {
				if (mCallback != null) {
					mCallback.OnClick("造林树种", Tools.GetTextValueOnID(dialogView, R.id.tv_select));
				}

				String result = Tools.GetTextValueOnID(dialogView, R.id.tv_select);
				if (result != null && result.length() > 0) {
					String[] selectedSZ = result.split("\\+");
					UserConfigDB userDB = new UserConfigDB();
					String cxShuZhong = userDB.getSelectedShuZhong();

					if (cxShuZhong != null) {
						for (String sz : selectedSZ) {
							if (!cxShuZhong.contains(sz)) {
								if (cxShuZhong.length() > 0) {
									cxShuZhong += "," + sz;
								} else {
									cxShuZhong = sz;
								}
							}
						}
					} else {
						for (String sz : selectedSZ) {
							if (cxShuZhong == null) {
								cxShuZhong = sz;
							} else {
								cxShuZhong += "," + sz;
							}
						}
					}

					userDB.addSelectedShuZhong(cxShuZhong);

				}

				dialogView.dismiss();
			}

			if (Str.equals("选择树种")) {
				try {
					HashMap<String, Object> hm = (HashMap<String, Object>) ExtraStr;

					boolean isSelect = Boolean.parseBoolean(hm.get("isAdd").toString());
					int position = Integer.parseInt(hm.get("indexN") + "");
					((HashMap<String, Object>) cxSource.get(position)).put("isCheck", isSelect);

					String selectStr = "";
					boolean isFirst = true;
					for (HashMap<String, Object> szHM : cxSource) {
						boolean hasSelect = Boolean.parseBoolean(szHM.get("isCheck").toString());
						String name = szHM.get("Name").toString();
						if (hasSelect) {
							if (isFirst) {
								selectStr = name;
								isFirst = false;
							} else {
								selectStr += "+" + name;

							}
						}
					}
					Tools.SetTextViewValueOnID(dialogView, R.id.tv_select, selectStr);

				} catch (Exception ex) {
					Tools.ShowMessageBox(ex.getMessage());
				}

			}
		}
	};

	private void initZLSZList(String curValue) {
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_select, curValue);

		UserConfigDB userDB = new UserConfigDB();
		String cxShuZhong = userDB.getSelectedShuZhong();
		if (cxShuZhong == null || cxShuZhong.length() == 0) {
			cxShuZhong = "";
		}

		String[] arryShuZhong = cxShuZhong.split(",");
		szSource = PubVar.m_DoEvent.m_DictDataDB.getDictData("林业", "退耕还林", "造林树种");

		boolean[] checks = new boolean[szSource.size()];
		List<String> selectedSZ = Arrays.asList(curValue.split("\\+"));
		String strAllShuzhonghong = "";
		String strSelectedShuzhong = "";

		for (int i = 0; i < szSource.size(); i++) {
			if (i == 0) {
				strAllShuzhonghong = szSource.get(i).get("Name") + "(" + szSource.get(i).get("Code") + ")";
			} else {
				strAllShuzhonghong += "," + szSource.get(i).get("Name") + "(" + szSource.get(i).get("Code") + ")";
			}

			if (cxShuZhong.contains(szSource.get(i).get("Name") + "")) {
				if (selectedSZ.contains(szSource.get(i).get("Name"))) {
					szSource.get(i).put("isCheck", true);
					checks[i] = true;
					if (strSelectedShuzhong.length() == 0) {
						strSelectedShuzhong = szSource.get(i).get("Name") + "(" + szSource.get(i).get("Code") + ")";
					} else {
						strSelectedShuzhong += "," + szSource.get(i).get("Name") + "(" + szSource.get(i).get("Code")
								+ ")";
					}
				} else {
					szSource.get(i).put("isCheck", false);
					checks[i] = false;
				}
				cxSource.add(szSource.get(i));
			}

		}

		TuiGengDataZLSZSelectAdapter adapter = new TuiGengDataZLSZSelectAdapter(dialogView.getContext(), cxSource,
				checks, R.layout.tuigeng_sheji_zlsz_item, new String[] { "Name", "Code", "isCheck" },
				new int[] { R.id.ll_select, R.id.tv_code });
		adapter.SetCallback(pCallback);
		(((ListView) dialogView.findViewById(R.id.lvList))).setAdapter(adapter);

		mutliAutoSelectSZ.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(dialogView.getContext(),
				android.R.layout.simple_spinner_dropdown_item, strAllShuzhonghong.split(","));
		mutliAutoSelectSZ.setAdapter(arrayAdapter);
		mutliAutoSelectSZ.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void ShowDialog() {
		dialogView.show();
	}
}
