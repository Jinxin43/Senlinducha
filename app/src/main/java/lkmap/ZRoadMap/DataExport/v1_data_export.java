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

		// ���ñ���
		_Dialog.SetCaption(Tools.ToLocale("���ݵ���"));

		// ����Ĭ�ϰ�ť
		_Dialog.SetButtonInfo("1," + R.drawable.v1_ok + "," + Tools.ToLocale("��������") + "  ,��������", _Callback);

		// ������ʽ
		v1_DataBind.SetBindListSpinner(_Dialog, "������ʽ", new String[] { "ArcGIS(shp)", "AutoCad(dxf)", "Google(kml)" },
				R.id.sp_format);

		// �Զ���ȡ����Ŀ¼���ƣ��Ե�ǰʱ��Ϊ׼
		String pathName = Tools.GetSystemDate().replace("-", "").replace(":", "").replace(" ", "");
		String PrjPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/���ݵ���/" + pathName;

		Tools.SetTextViewValueOnID(_Dialog, R.id.pn_projectname, pathName);

		_Dialog.findViewById(R.id.layout_exportSetting).setVisibility(View.GONE);

		// ������֧��
		int[] ViewID = new int[] { R.id.tvLocaleText1, R.id.tvLocaleText2, R.id.tvLocaleText3, R.id.tvLocaleText4 };
		for (int vid : ViewID) {
			Tools.ToLocale(_Dialog.findViewById(vid));
		}
	}

	private ICallback _Callback = new ICallback() {

		@Override
		public void OnClick(String Str, Object ExtraStr) {

			if (Str.equals("��������")) {
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
					Tools.ShowMessageBox("��ѡ��Ҫ������ͼ��");
				} else {
					// �򿪹���ͼ�����
					lkmap.Tools.Tools.OpenDialog("���ڵ�������...", new ICallback() {
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							// ��������·��
							String pathName = Tools.GetTextValueOnID(_Dialog, R.id.pn_projectname);
							String ExportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
									+ "/���ݵ���/" + pathName;
							if (!Tools.ExistFile(ExportPath))
								(new File(ExportPath)).mkdirs();

							// ���󵼳�ͼ���б�
							List<String> ExportErrorList = new ArrayList<String>();

							// ��ͼ�㵼�����ݣ���ʽ��LayerID,LayerName

							// �����͵���
							String ExpType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_format);
							if (ExpType.equals("ArcGIS(shp)")) {
								for (HashMap<String, String> lyr : expDatasetNameList) {
									// ��ʼ����
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

							// ���������ʾ��Ϣ��ExportErrorList.size()>0��ʾ��ͼ��û�е����ɹ�
							if (ExportErrorList.size() > 0) {
								Tools.ShowMessageBox(_Dialog.getContext(),
										"����ͼ�����ݵ���ʧ�ܣ�\r\n\r\n" + Tools.JoinT("\r\n", ExportErrorList));
							} else {
								Tools.ShowMessageBox(_Dialog.getContext(), "���ݳɹ�������\r\n\r\nλ�ڣ���" + ExportPath + "��");
							}
						}
					});
				}
			}

		}
	};

	public void ShowDialog() {
		// �˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
		_Dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				LoadLayerInfo();
			}
		});
		_Dialog.show();
	}

	// ͼ���б�󶨵�������
	private List<HashMap<String, Object>> m_HeaderListViewDataItemList = null;

	/**
	 * ���ؿɵ���ͼ���б���Ϣ
	 */
	private void LoadLayerInfo() {
		// ��ͼ���б�
		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_result1), "����ͼ���б�");

		// ��ȡ�����̵�ͼ���б�
		this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String, Object>>();
		for (v1_Layer lyr : PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList()) {
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.GetLayerID());
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("LayerID", lyr.GetLayerID()); // ͼ��ID�����ڱ�ʶάһͼ��
			hm.put("LayerName", lyr.GetLayerAliasName()); // ͼ������
			hm.put("D1", false); // �Ƿ�ɵ���
			hm.put("D2", lyr.GetLayerAliasName()); // ͼ������
			hm.put("D3", Tools.ToLocale(lyr.GetLayerTypeName())); // ͼ������
			hm.put("D4", pDataset.GetAllObjectCount()); // ʵ������
			this.m_HeaderListViewDataItemList.add(hm);
		}

		for (v1_Layer lyr : PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetLayerList()) {
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.GetLayerID());
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("LayerID", lyr.GetLayerID()); // ͼ��ID�����ڱ�ʶάһͼ��
			hm.put("LayerName", lyr.GetLayerAliasName()); // ͼ������
			hm.put("D1", false); // �Ƿ�ɵ���
			hm.put("D2", lyr.GetLayerAliasName() + "(��ͼ)"); // ͼ������
			hm.put("D3", Tools.ToLocale(lyr.GetLayerTypeName())); // ͼ������
			hm.put("D4", pDataset.GetAllObjectCount()); // ʵ������
			this.m_HeaderListViewDataItemList.add(hm);
		}

		hvf.BindDataToListView(this.m_HeaderListViewDataItemList);

	}
}
