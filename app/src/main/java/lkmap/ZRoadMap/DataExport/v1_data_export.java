package lkmap.ZRoadMap.DataExport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Dataset.Dataset;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.Project.v1_Layer;

public class v1_data_export {
	private v1_FormTemplate _Dialog = null;

	public v1_data_export() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.v1_data_export);
		_Dialog.ReSetSize(0.65f, 0.9f);

		// 设置标题
		_Dialog.SetCaption(Tools.ToLocale("数据导出"));

		// 设置默认按钮
		_Dialog.SetButtonInfo("1," + R.drawable.v1_ok + "," + Tools.ToLocale("导出数据") + "  ,导出数据", _Callback);

		// 导出格式
		v1_DataBind.SetBindListSpinner(_Dialog, "导出格式", new String[] { "ArcGIS(shp)", "AutoCad(dxf)", "Google(kml)" },
				R.id.sp_format);

		// 自动获取导出目录名称，以当前时间为准
		String pathName = Tools.GetSystemDate().replace("-", "").replace(":", "").replace(" ", "");
		String PrjPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/数据导出/" + pathName;

		Tools.SetTextViewValueOnID(_Dialog, R.id.pn_projectname, pathName);

		_Dialog.findViewById(R.id.layout_exportSetting).setVisibility(View.GONE);

		// 多语言支持
		int[] ViewID = new int[] { R.id.tvLocaleText1, R.id.tvLocaleText2, R.id.tvLocaleText3, R.id.tvLocaleText4 };
		for (int vid : ViewID) {
			Tools.ToLocale(_Dialog.findViewById(vid));
		}
	}

	private ICallback _Callback = new ICallback() {

		@Override
		public void OnClick(String Str, Object ExtraStr) {

			if (Str.equals("导出数据")) {
				final List<HashMap<String, String>> expDatasetNameList = new ArrayList<HashMap<String, String>>();
				for (HashMap<String, Object> hashObj : m_HeaderListViewDataItemList) {
					boolean export = Boolean.parseBoolean(hashObj.get("D1").toString());
					if (!export)
						continue;
					String datasetName = hashObj.get("LayerID").toString();
					String dsName = hashObj.get("LayerName").toString();

					HashMap<String, String> expLayer = new HashMap<String, String>();
					expLayer.put("LayerID", datasetName);
					expLayer.put("LayerName", dsName);
					expDatasetNameList.add(expLayer);
				}

				if (expDatasetNameList.size() <= 0) {
					Tools.ShowMessageBox("请选择要导出的图层");
				} else {
					// 打开工程图层管理
					lkmap.Tools.Tools.OpenDialog("正在导出数据...", new ICallback() {
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							// 导出数据路径
							String pathName = Tools.GetTextValueOnID(_Dialog, R.id.pn_projectname);
							String ExportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
									+ "/数据导出/" + pathName;
							if (!Tools.ExistFile(ExportPath))
								(new File(ExportPath)).mkdirs();

							// 错误导出图层列表
							List<String> ExportErrorList = new ArrayList<String>();

							// 分图层导出数据，格式：LayerID,LayerName

							// 分类型导出
							String ExpType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_format);
							if (ExpType.equals("ArcGIS(shp)")) {
								for (HashMap<String, String> lyr : expDatasetNameList) {
									// 开始导出
									Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.get("LayerID"));
									DataExport_SHP DE = new DataExport_SHP();
									if (!DE.Export(pDataset, ExportPath + "/" + lyr.get("LayerName"),false))
										ExportErrorList.add("LayerName");
								}
							}
							if (ExpType.equals("AutoCad(dxf)")) {
								String ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
								DataExport_DXF DF = new DataExport_DXF();
								ExportErrorList = DF.Export(expDatasetNameList,
										ExportPath + "/" + ProjectName + ".dxf");
							}
							if (ExpType.equals("Google(kml)")) {
								String ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
								DataExport_KML kml = new DataExport_KML();
								ExportErrorList = kml.Export(expDatasetNameList,
										ExportPath + "/" + ProjectName + ".kml");
							}

							// 导出后的提示信息，ExportErrorList.size()>0表示有图层没有导出成功
							if (ExportErrorList.size() > 0) {
								Tools.ShowMessageBox(_Dialog.getContext(),
										"以下图层数据导出失败！\r\n\r\n" + Tools.JoinT("\r\n", ExportErrorList));
							} else {
								Tools.ShowMessageBox(_Dialog.getContext(), "数据成功导出！\r\n\r\n位于：【" + ExportPath + "】");
							}
						}
					});
				}
			}

		}
	};

	public void ShowDialog() {
		// 此处这样做的目的是为了计算控件的尺寸
		_Dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				LoadLayerInfo();
			}
		});
		_Dialog.show();
	}

	// 图层列表绑定的数据项
	private List<HashMap<String, Object>> m_HeaderListViewDataItemList = null;

	/**
	 * 加载可导出图层列表信息
	 */
	private void LoadLayerInfo() {
		// 绑定图层列表
		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_result1), "导出图层列表");

		// 读取本工程的图层列表
		this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String, Object>>();
		for (v1_Layer lyr : PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList()) {
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.GetLayerID());
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("LayerID", lyr.GetLayerID()); // 图层ID，用于标识维一图层
			hm.put("LayerName", lyr.GetLayerAliasName()); // 图层名称
			hm.put("D1", false); // 是否可导出
			hm.put("D2", lyr.GetLayerAliasName()); // 图层名称
			hm.put("D3", Tools.ToLocale(lyr.GetLayerTypeName())); // 图层类型
			hm.put("D4", pDataset.GetAllObjectCount()); // 实体数量
			this.m_HeaderListViewDataItemList.add(hm);
		}

		for (v1_Layer lyr : PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetLayerList()) {
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.GetLayerID());
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("LayerID", lyr.GetLayerID()); // 图层ID，用于标识维一图层
			hm.put("LayerName", lyr.GetLayerAliasName()); // 图层名称
			hm.put("D1", false); // 是否可导出
			hm.put("D2", lyr.GetLayerAliasName() + "(底图)"); // 图层名称
			hm.put("D3", Tools.ToLocale(lyr.GetLayerTypeName())); // 图层类型
			hm.put("D4", pDataset.GetAllObjectCount()); // 实体数量
			this.m_HeaderListViewDataItemList.add(hm);
		}

		hvf.BindDataToListView(this.m_HeaderListViewDataItemList);

	}
}
