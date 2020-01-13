package lkmap.ZRoadMap.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.Controls.YearPicker;
import com.dingtu.DTGIS.DataService.DictXZQH;
import com.dingtu.DTGIS.DataService.DuChaDB;
import com.dingtu.DTGIS.TuiGeng.TuiGengData_ExportTable;
import com.dingtu.DTGIS.Upload.UploadDataSelector;
import com.dingtu.SLDuCha.DuChaDataExporter;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.ForestryLayerType;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkRenderType;
import lkmap.Symbol.LineSymbol;
import lkmap.Symbol.PointSymbol;
import lkmap.Symbol.PolySymbol;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_layer_new {
	private v1_FormTemplate _Dialog = null;

	// ��ǰ���� ����
	private String _ProjectName = "";

	public v1_project_layer_new() {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.v1_project_layer_new);
		// _Dialog.ReSetSize(1f, 0.95f);
		_Dialog.ReSetSize(0.6f, 0.9f);
		// ��������
		this._ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;

		// ��ʼ��ʾ����ť
		_Dialog.SetCaption("��" + this._ProjectName + "��" + Tools.ToLocale("ͼ��"));
		_Dialog.SetButtonInfo("1," + R.drawable.icon_title_comfirm + "," + Tools.ToLocale("ȷ��") + "  ,ȷ��", pCallback);

		// �����ֶΣ��ֶ����ԣ�ɾ���ֶΰ�ť
		_Dialog.findViewById(R.id.pln_add).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.pln_edit).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.pln_delete).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.btnExportTanhui).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.btnMatchField).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.btnExportTuiGeng).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.btnExportGTPhoto).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.btnExportSTYMPhoto).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.btnExportAllPhoto).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.btnUploadTuiGeng).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.btnExportJCKP).setOnClickListener(new ViewClick());
		_Dialog.findViewById(R.id.btnExportDuChaPhoto).setOnClickListener(new ViewClick());

		// ������֧��
		int[] viewid = new int[] { R.id.pln_add, R.id.pln_edit, R.id.pln_delete, R.id.tvLocaleText1, R.id.tvLocaleText2,
				R.id.tvLocaleText3, R.id.tvLocaleText4 };
		for (int id : viewid) {
			Tools.ToLocale(_Dialog.findViewById(id));
		}

		// ��ͼ�������б�
		v1_DataBind.SetBindListSpinner(_Dialog, "ͼ������", Tools.StrArrayToList(new String[] { "��", "��", "��" }),
				R.id.sp_type);
		String msg = "";
		try {
			ApplicationInfo appInfo = PubVar.m_DoEvent.m_Context.getPackageManager()
					.getApplicationInfo(PubVar.m_DoEvent.m_Context.getPackageName(), PackageManager.GET_META_DATA);
			msg = appInfo.metaData.getString("version");
		} catch (Exception ex) {

		}

		initTuiGengEnum();
		// if(msg.equals("TGHL"))
		// {
		// v1_DataBind.SetBindListSpinner(_Dialog, "��������",
		// Tools.StrArrayToList(new String[]{"�Զ��幤��","�˸�����"}),
		// R.id.sp_projecttype);
		// }
		// else
		// {
		// v1_DataBind.SetBindListSpinner(_Dialog, "��������",
		// Tools.StrArrayToList(new
		// String[]{"�Զ��幤��","�ֵر��","�˸�����","̼��-ÿľ���","��Ȼ�ֱ���","�ٷ���","�ص��������ϵ����","����������"}),
		// R.id.sp_projecttype);
		// }

		List<String> listProjecTypes = new ArrayList<String>();

//		if (msg.equals("TGHL")) {
//			initTuiGengEnum();
//			// listProjecTypes = Tools.StrArrayToList(new
//			// String[]{ForestryLayerType.DefaultLayer,
//			// ForestryLayerType.TuigengLayer});
//			listProjecTypes = Tools.StrArrayToList(new String[] { ForestryLayerType.DefaultLayer,
//
//					ForestryLayerType.TuigengLayer, ForestryLayerType.LindibiangengLayer, ForestryLayerType.LinyeErdiao,
//					ForestryLayerType.XiaoBanXuji, ForestryLayerType.TianbaozaolinLayer,
//					ForestryLayerType.TianbaoFuYuLayer, ForestryLayerType.TianbaoFengYuLayer,
//					ForestryLayerType.TanhuiLayer });
//
//		} else if (msg.equals("WPZF")) {
//			listProjecTypes = Tools
//					.StrArrayToList(new String[] { ForestryLayerType.DefaultLayer, ForestryLayerType.WeipianShujuLayer,
//							ForestryLayerType.WeipianJianchaLayer, ForestryLayerType.LindibiangengLayer });
//		} else {
			initTuiGengEnum();
			listProjecTypes = Tools.StrArrayToList(new String[] { ForestryLayerType.DefaultLayer,
					ForestryLayerType.DuChaYanZheng,
					ForestryLayerType.HangPaiZhaoPian
//					ForestryLayerType.TuigengLayer,
//					ForestryLayerType.LindibiangengLayer, ForestryLayerType.LinyeErdiao, ForestryLayerType.XiaoBanXuji,
//					ForestryLayerType.TianbaozaolinLayer, ForestryLayerType.TianbaoFuYuLayer,
//					ForestryLayerType.TianbaoFengYuLayer, ForestryLayerType.TanhuiLayer 
					});
//		}

		v1_DataBind.SetBindListSpinner(_Dialog, "��������", listProjecTypes, R.id.sp_projecttype);

		this.SetButtonEnable(false);
	}

	class ProjectTypeOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
			// initOthers();
			m_EditLayer.SetLayerProjectType(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype));

			if (m_EditLayer.GetLayerProjecType().contains("�˸�����")
					|| m_EditLayer.GetLayerProjecType().contains("�ֵر��")) {
				Tools.ShowYesNoMessage(_Dialog.getContext(), "�Ƿ���ͼ��ģ�������ֶν���ƥ�䣿", new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {

						if (Str.equals("YES")) {
							DoCommand("�ֶ�ƥ��");
						}
					}
				});
			} else if (m_EditLayer.GetLayerProjecType().equals(ForestryLayerType.DuChaYanZheng)) {
				_Dialog.findViewById(R.id.ll_duchashuju).setVisibility(View.VISIBLE);
				Tools.ShowYesNoMessage(_Dialog.getContext(), "�Ƿ���ͼ��ģ�������ֶν���ƥ�䣿", new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {

						if (Str.equals("YES")) {
							DoCommand("�ֶ�ƥ��");
						}
					}
				});

			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	private void initOthers() {

		_Dialog.findViewById(R.id.ll_weipiandataselect).setVisibility(View.GONE);
		_Dialog.findViewById(R.id.ll_tuigeng).setVisibility(View.VISIBLE);
		_Dialog.findViewById(R.id.btnExportTuiGeng).setVisibility(View.GONE);
		_Dialog.findViewById(R.id.ll_pipei).setVisibility(View.GONE);
		// m_EditLayer.SetLayerProjectType(Tools.GetSpinnerValueOnID(_Dialog,
		// R.id.sp_projecttype));

		if (Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype).contains("̼��")) {
			_Dialog.findViewById(R.id.btnExportTanhui).setVisibility(View.VISIBLE);
		}

		if (Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype).contains("�˸�����")) {

			// _Dialog.findViewById(R.id.ll_field).setVisibility(View.GONE);

			_Dialog.findViewById(R.id.btnExportTuiGeng).setVisibility(View.VISIBLE);

			// DoCommand("�ֶ�ƥ��");
			_Dialog.findViewById(R.id.ll_pipei).setVisibility(View.VISIBLE);
		}
		if (Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype).contains("�ֵر��")) {
			// DoCommand("�ֶ�ƥ��");
			_Dialog.findViewById(R.id.ll_pipei).setVisibility(View.VISIBLE);
		}

		if (Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype).equals(ForestryLayerType.WeipianJianchaLayer)) {
			// DoCommand("�ֶ�ƥ��");
			_Dialog.findViewById(R.id.ll_weipiandataselect).setVisibility(View.VISIBLE);
			setWeipianxiafaLayer();
			if (!m_EditLayer.getWeiPianDataLayer().isEmpty()) {
				for (int i = 0; i < listWPSJLayerIDs.size(); i++) {
					if (m_EditLayer.getWeiPianDataLayer().equals(listWPSJLayerIDs.get(i))) {
						Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_wpsjxf, listWPSJXFLayer.get(i));
					}
				}

			}
		}

		if (Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype).equals(ForestryLayerType.DuChaYanZheng)) {
			_Dialog.findViewById(R.id.ll_duchashuju).setVisibility(View.VISIBLE);

		}

	}

	ArrayList<String> listWPSJXFLayer = new ArrayList<String>();
	ArrayList<String> listWPSJLayerIDs = new ArrayList<String>();

	private void setWeipianxiafaLayer() {
		listWPSJXFLayer.clear();
		listWPSJLayerIDs.clear();

		List<v1_Layer> dataGridList = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList();
		for (v1_Layer layer : dataGridList) {
			if (layer.GetLayerProjecType().equals(ForestryLayerType.WeipianShujuLayer)) {
				listWPSJXFLayer.add(layer.GetLayerAliasName());
				listWPSJLayerIDs.add(layer.GetLayerID());
			}
		}

		ArrayAdapter<Object> nfAdapter = new ArrayAdapter<Object>(_Dialog.getContext(),
				android.R.layout.simple_spinner_item, listWPSJXFLayer.toArray());
		nfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spWPXFLayer = ((Spinner) _Dialog.findViewById(R.id.sp_wpsjxf));
		spWPXFLayer.setAdapter(nfAdapter);

	}

	private void initTuiGengEnum() {
		DictXZQH xzqh = new DictXZQH();
		city = xzqh.getXZQH("61", "��");
		ArrayList<String> cityNames = new ArrayList<String>();
		cityNames.add("");
		for (HashMap<String, Object> hm : city) {
			cityNames.add(hm.get("D1").toString());
		}

		ArrayAdapter<Object> nfAdapter = new ArrayAdapter<Object>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, cityNames.toArray());
		nfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner tgds = ((Spinner) _Dialog.findViewById(R.id.sp_tgds));
		tgds.setAdapter(nfAdapter);
		tgds.setOnItemSelectedListener(new tgdsOnItemSelectedListener());
		// tgds.setEnabled(false);
	}

	List<HashMap<String, Object>> city = new ArrayList<HashMap<String, Object>>();

	class tgdsOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
			DictXZQH xzqh = new DictXZQH();
			List<HashMap<String, Object>> xian = xzqh
					.getXZQH(xzqh.getCodeByName(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgds), "��", "61"), "��");

			ArrayList<String> cityNames = new ArrayList<String>();
			cityNames.add("");
			for (HashMap<String, Object> hm : xian) {
				cityNames.add(hm.get("D1").toString());
			}

			ArrayAdapter<Object> nfAdapter = new ArrayAdapter<Object>(PubVar.m_DoEvent.m_Context,
					android.R.layout.simple_spinner_item, cityNames.toArray());
			nfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Spinner tgds = ((Spinner) _Dialog.findViewById(R.id.sp_tgqx));
			tgds.setAdapter(nfAdapter);

			try {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_tgqx, m_EditLayer.getCounty());
			} catch (Exception ex) {

			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}

	// ���ð�ť��״̬
	private void SetButtonEnable(boolean enabled) {
		_Dialog.findViewById(R.id.pln_edit).setEnabled(enabled);
		_Dialog.findViewById(R.id.pln_delete).setEnabled(enabled);
		((TextView) _Dialog.findViewById(R.id.tv_edit)).setTextColor((enabled ? Color.BLACK : Color.GRAY));
		((TextView) _Dialog.findViewById(R.id.tv_delete)).setTextColor((enabled ? Color.BLACK : Color.GRAY));
	}

	class ViewClick implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			DoCommand(Tag);
		}
	}

	// ��ť�¼�
	private void DoCommand(String StrCommand) {
		this.m_EditLayer.SetLayerProjectType(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype));
		if (StrCommand.equals("�ֶ�ƥ��")) {
			MatchFields mf = new MatchFields(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_projecttype),
					m_EditLayer.GetLayerID());
			mf.SetCallback(pCallback); // �ֶβ�����ɺ�Ļص� ���ص���־���ֶ�ƥ��
			mf.ShowDialog();
		}
		if (StrCommand.equals("����ÿľ��߱�")) {
			PubVar.m_DoEvent.DoCommand("��̼�㵼��");
		}
		if (StrCommand.equals("�˸����ݵ���")) {
			TuiGengData_ExportTable tuigengData = new TuiGengData_ExportTable(m_EditLayer);
			tuigengData.ShowDialog();
		}

		if (StrCommand.equals("������鿨Ƭ")) {
//			ExportDuChaExcel();
			DuChaDataExporter duchaExplorer = new DuChaDataExporter(m_EditLayer);
			duchaExplorer.ShowDialog();
			return;
		}
		
//		if (StrCommand.equals("����������Ƭ")) {
//			// TODO:
//			ExportDuChaPhotos();
//			return;
//		}
		if (StrCommand.equals("׷�Ӻϲ� ")) {
			ImportZhuijiaTuBan();
			return;
		}
		

		if (StrCommand.equals("������Ƭ")) {
			v1_project_layer_render_uniquevalue_selectfield pnl = new v1_project_layer_render_uniquevalue_selectfield();
			pnl.SetEditLayer(m_EditLayer);
			pnl.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					List<v1_LayerField> LFList = (List<v1_LayerField>) ExtraStr;
					List<String> FieldNameList = new ArrayList<String>();
					for (v1_LayerField LF : LFList) {
						FieldNameList.add(LF.GetFieldName());
					}
					ExportPhotos(FieldNameList, false);
				}
			});
			pnl.ShowDialog();
		}

		if (StrCommand.equals("������̬������Ƭ")) {
			String mPhotoPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/Photo/";
			final Dataset pDataset = PubVar.m_Workspace.GetDatasetById(m_EditLayer.GetLayerID());

			String XBH = m_EditLayer.GetDataFieldNameByFieldName("�ؿ���");
			String XiangZhen = m_EditLayer.GetDataFieldNameByFieldName("��������");
			String Cun = m_EditLayer.GetDataFieldNameByFieldName("XZQMC");

			if (XBH.isEmpty() || XiangZhen.isEmpty() || Cun.isEmpty()) {
				Tools.ShowMessageBox("�����Ƿ��С��ؿ��ţ�����XZQMC�������ֶΣ������޷���������");
				return;
			}

			String querySql = "select SYS_ID," + XBH + "," + XiangZhen + "," + Cun + ",SYS_PHOTO from "
					+ pDataset.getDataTableName() + " where SYS_STATUS=0 ";
			SQLiteDataReader DR = pDataset.getDataSource().Query(querySql);

			String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/��Ƭ����/"
					+ m_EditLayer.GetLayerAliasName() + "/";

			int count = DR.GetCount();
			while (DR.Read()) {
				String path = DR.GetString(XiangZhen) + "_" + DR.GetString(Cun) + "/";
				String photoPath = exportPath + path + DR.GetString(XiangZhen) + "_" + DR.GetString(Cun) + "_"
						+ DR.GetString(XBH) + "/";

				String strPhotos = DR.GetString("SYS_PHOTO");
				if (strPhotos != null && strPhotos.length() > 0) {
					String[] photos = strPhotos.split(",");
					for (int i = 0; i < photos.length; i++) {
						File f1 = new File(photos[i]);
						if (f1.exists()) {

							if (!lkmap.Tools.Tools.ExistFile(photoPath)) {
								(new File(photoPath)).mkdirs();
							}

							String newFileName;
							if (photos.length > 1) {
								newFileName = photoPath + "/" + DR.GetString(XiangZhen) + "_" + DR.GetString(Cun) + "_"
										+ DR.GetString(XBH) + "_" + (i + 1) + ".jpg";

							} else {
								newFileName = photoPath + "/" + DR.GetString(XiangZhen) + "_" + DR.GetString(Cun) + "_"
										+ DR.GetString(XBH) + ".jpg";
							}
							CopyFile(photos[i], newFileName);

						}
					}
				}
			}

			Tools.ShowMessageBox("��Ƭ������ɣ�");

		}

		if (StrCommand.equals("���������ҹ���Ƭ")) {
			String mPhotoPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/Photo/";
			final Dataset pDataset = PubVar.m_Workspace.GetDatasetById(m_EditLayer.GetLayerID());

			String XBH = m_EditLayer.GetDataFieldNameByFieldName("XBH");
			String XiangZhen = m_EditLayer.GetDataFieldNameByFieldName("����");
			String Cun = m_EditLayer.GetDataFieldNameByFieldName("ZLDWMC");

			if (XBH.isEmpty() || XiangZhen.isEmpty() || Cun.isEmpty()) {
				Tools.ShowMessageBox("�����Ƿ��С�XBH������ZLDWMC�������ֶΣ������޷���������");
				return;
			}

			String querySql = "select SYS_ID," + XBH + "," + XiangZhen + "," + Cun + ",SYS_PHOTO from "
					+ pDataset.getDataTableName() + " where SYS_STATUS=0 ";
			SQLiteDataReader DR = pDataset.getDataSource().Query(querySql);

			String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/��Ƭ����/"
					+ m_EditLayer.GetLayerAliasName() + "/";

			int count = DR.GetCount();
			while (DR.Read()) {
				String path = DR.GetString(XiangZhen) + "_" + DR.GetString(Cun) + "/";
				String photoPath = exportPath + path + DR.GetString(XiangZhen) + "_" + DR.GetString(Cun) + "_"
						+ DR.GetString(XBH) + "/";

				String strPhotos = DR.GetString("SYS_PHOTO");
				if (strPhotos != null && strPhotos.length() > 0) {
					String[] photos = strPhotos.split(",");
					for (int i = 0; i < photos.length; i++) {
						// File f1 = new File(mPhotoPath+photos[i]);
						File f1 = new File(photos[i]);
						if (f1.exists()) {

							if (!lkmap.Tools.Tools.ExistFile(photoPath)) {
								(new File(photoPath)).mkdirs();
							}

							String newFileName;
							if (photos.length > 1) {
								newFileName = photoPath + "/" + DR.GetString(XiangZhen) + "_" + DR.GetString(Cun) + "_"
										+ DR.GetString(XBH) + "_" + (i + 1) + ".jpg";

							} else {
								newFileName = photoPath + "/" + DR.GetString(XiangZhen) + "_" + DR.GetString(Cun) + "_"
										+ DR.GetString(XBH) + ".jpg";
							}

							// CopyFile(mPhotoPath+photos[i],newFileName);
							CopyFile(photos[i], newFileName);

						}
					}
				}
			}

			Tools.ShowMessageBox("��Ƭ������ɣ�");

		}

		if (StrCommand.equals("�˸������ϴ�")) {
			UploadDataSelector upload = new UploadDataSelector();
			upload.ShowDialog();
		}

		if (StrCommand.equals("�����ֶ�")) {
			v1_project_layer_field plf = new v1_project_layer_field();
			plf.SetEditLayer(this.m_EditLayer);
			plf.SetCallback(pCallback); // �ֶβ�����ɺ�Ļص� ���ص���־���ֶ�
			plf.ShowDialog();
		}
		if (StrCommand.equals("�ֶ�����")) {
			v1_project_layer_field plf = new v1_project_layer_field();
			plf.SetEditLayer(this.m_EditLayer);
			plf.SetEditField(this.m_SelectField);
			plf.SetCallback(pCallback); // �ֶβ�����ɺ�Ļص� ���ص���־���ֶ�
			plf.ShowDialog();
		}
		if (StrCommand.equals("ɾ���ֶ�")) {
			Tools.ShowYesNoMessage(_Dialog.getContext(),
					Tools.ToLocale("�Ƿ�ɾ���ֶ�") + "��" + this.m_SelectField.GetFieldName() + "����", new ICallback() {

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (Str.equals("YES")) {
								List<v1_LayerField> lyrFieldList = m_EditLayer.GetFieldList();
								for (v1_LayerField Field : lyrFieldList) {
									if (Field.GetFieldName().equals(m_SelectField.GetFieldName())) {
										lyrFieldList.remove(Field);
										LoadLayerInfo();
										return;
									}
								}
							}

						}

					});

		}
	}

	private void backupProjectFile()
	{
		String projectFullName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/Project.dbx";
		String projectJournal = projectFullName+"-journal";
		String DataFile = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName();
		String DataJournal = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName()+"-journal";
		
		
		String pathName = Tools.GetSystemDate().replace("-", "").replace(":", "").replace(" ", "");
		String toPorjctFullName =  PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/backup/"+pathName+"/Project.dbx";
		String toDataFileName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/backup/"+pathName+"/TAData.dbx";
		Tools.CopyFile(projectFullName, toPorjctFullName);
		Tools.CopyFile(DataFile,toDataFileName);
		
		File projectJournalFile = new File(projectJournal);
		if(projectJournalFile.exists())
		{
			String toProjectJournal =  toPorjctFullName+"-journal";
			Tools.CopyFile(projectJournal, toProjectJournal);
		}
		
		File dataJournalFile = new File(DataJournal);
		if(dataJournalFile.exists())
		{
			String toDataJournal =  toDataFileName+"-journal";
			Tools.CopyFile(DataJournal,toDataJournal);
		}
		
	}
	
	
	private void ImportZhuijiaTuBan()
	{
		lkmap.ZRoadMap.ToolsBox.v1_selectdictionary sd = new lkmap.ZRoadMap.ToolsBox.v1_selectdictionary();

		sd.SetFileFilter(new String[] { "DTZ"});
		sd.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, final Object ExtraStrT) {

				backupProjectFile();
//				Stsring StartTime = Tools.GetSystemDate();
				List<String> importFileList = (List<String>) ExtraStrT;
				for (String importFile : importFileList) {
					// �����ļ����Ͳ�ͬ���е�������
					String FileType = importFile.substring(importFile.length() - 3, importFile.length());
					if (FileType.toUpperCase().equals("DTZ")) {
						final String  fileName = importFile;
						final String qiandilei = m_EditLayer.GetDataFieldNameByFieldName("ǰ����");
						
						Log.e("importFile", importFile);
						try
						{
							ASQLiteDatabase zhuijiaDB = new ASQLiteDatabase();
							zhuijiaDB.setDatabaseName(importFile);
							
							
							String attachSql = String.format("ATTACH DATABASE \'%s\' AS %s", PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName(), "targetDB");
							zhuijiaDB.ExcuteSQL(attachSql);
							String sql = "select distinct "+ m_EditLayer.GetDataFieldNameByFieldName("ͼ�ߺ�")+" from TAData_D where  SYS_STATUS=0 and "+ m_EditLayer.GetDataFieldNameByFieldName("ͼ�ߺ�")+" in (select "+m_EditLayer.GetDataFieldNameByFieldName("ͼ�ߺ�") +" from targetDB."+m_EditLayer.GetLayerID()+"_D where SYS_STATUS=0 AND "+qiandilei+" !='')";
							SQLiteDataReader reader = zhuijiaDB.Query(sql);
							StringBuilder strTubanhao = new StringBuilder(); 
							boolean hasRepeat = false;
							while(reader.Read())
							{
//								int id = reader.GetInt32("SYS_ID");
								String tubanhao = reader.GetString(m_EditLayer.GetDataFieldNameByFieldName("ͼ�ߺ�"));
								strTubanhao.append(tubanhao+",");
								hasRepeat = true;
							}
							if(hasRepeat)
							{
								strTubanhao.deleteCharAt(strTubanhao.length()-1);
							}
							
							zhuijiaDB.Close();
							
							if(hasRepeat)
							{
								Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "����ͼ���ڵ�ǰϵͳ���Ѿ���ͼ����֤��Ϣ���Ƿ���׷�����ݸ��ǣ� "+strTubanhao.toString(), new ICallback()
										{

											@Override
											public void OnClick(String Str, Object ExtraStr) {
												
												if(Str.equals("YES"))
												{
													UpdateZhuijiaData(fileName,true);
													importDJCP(fileName);
													PubVar.m_Map.Refresh();
													Tools.ShowMessageBox("����׷�Ӻϲ���ɣ�");
													
												}
												else
												{
//													UpdateZhuijiaData(fileName,true);
//													importDJCP(fileName);
//													PubVar.m_Map.Refresh();
//													Tools.ShowMessageBox("����׷�Ӻϲ���ɣ�");
												}
											}
									
										});
							
							}
							else
							{
								UpdateZhuijiaData(fileName,true);
								importDJCP(fileName);
								PubVar.m_Map.Refresh();
								Tools.ShowMessageBox("����׷�Ӻϲ���ɣ�");
							}
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
							Tools.ShowMessageBox(ex.getMessage());
						}
					}
				}
				
				
			}
		});
		sd.ShowDialog(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName());
		
	}
	
	
	private void importDJCP(String fileName)
	{
//		ASQLiteDatabase zhuijiaDB = new ASQLiteDatabase();
//		zhuijiaDB.setDatabaseName(fileName);
		
		try
		{
			String attachSql = String.format("ATTACH DATABASE \'%s\' AS %s", fileName, "fromDB");
			PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(attachSql);
			
			String doData = "targetDB.T_DuChaJCKP";
			StringBuilder insertSql= new StringBuilder();
			insertSql.append("insert into T_DuChaJCKP select NULL,");
			for(int i=2;i<159;i++)
			{
				insertSql.append("a"+i+",");
			}
			insertSql.append("'"+m_EditLayer.GetLayerID()+"',");
			for(int i= 160;i<177;i++)
			{
				insertSql.append("a"+i+",");
			}
			insertSql.append("'zhuijia' from fromDB.T_DuChaJCKP");
			PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(insertSql.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				String attachSql = String.format("DETACH  DATABASE 'fromDB'");
				PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(attachSql);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		
	}
	
	private void updateAndCopyPhoto(ASQLiteDatabase zhuijiaDB,String zhuijiaPath,int newID,String photosPath,String tubanhao)
	{
		if(photosPath != null && photosPath.length()>0)
		{
//			int id = readerPhoto.GetInt32("SYS_ID");
			if(!photosPath.toUpperCase().equals("NULL"))
			{
				
//				updateAndCopyPhoto();
				StringBuilder newPhotoPath= new StringBuilder();
				for(String photoPath:photosPath.split(","))
				{
					
					int indexS = photoPath.lastIndexOf("/");
					String newName=photoPath;
					String smallNewName="";
					if(indexS>0)
					{
						newName = zhuijiaPath+"Photos/"+photoPath.substring(indexS+1);
						Log.d("soruce PhotoName", newName);
						smallNewName = zhuijiaPath+"Photos/SamllPhotos/"+photoPath.substring(indexS+1);
					}
					
					
					File file = new File(newName);
					String destName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/Photo/"+photoPath.substring(indexS+1);
					String smallDestName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/smallPhoto/"+photoPath.substring(indexS+1);
					Log.e("dest PhotoName", destName);
					newPhotoPath.append(destName+",");
					if(file.exists())
					{
						Tools.CopyFile(newName, destName);

					}
					else
					{
						Log.e("miss photo", newName);
						Toast.makeText(PubVar.m_DoEvent.m_Context, "�޷�����ͼ��"+tubanhao+"����Ƭ", Toast.LENGTH_SHORT).show();
					}
					File samllFile = new File(smallNewName);
					if(samllFile.exists())
					{
						Tools.CopyFile(smallNewName, smallDestName);
					}
					
				}
				
				if(newPhotoPath.length()>0)
				{
					newPhotoPath.deleteCharAt(newPhotoPath.length()-1);
					
					String updatePhotos = "update targetDB."+m_EditLayer.GetLayerID()+"_D set SYS_Photo='"+newPhotoPath.toString()+"' where SYS_ID="+newID;
					zhuijiaDB.ExcuteSQL(updatePhotos);
				}
			}
			else
			{
				String updatePhotos = "update targetDB."+m_EditLayer.GetLayerID()+"_D set SYS_Photo='' where SYS_ID="+newID;
				zhuijiaDB.ExcuteSQL(updatePhotos);
			}
		}
	}
	
	private void UpdateZhuijiaData(String fileName,boolean isRepet)
	{
		ASQLiteDatabase zhuijiaDB = new ASQLiteDatabase();
		zhuijiaDB.setDatabaseName(fileName);
		String attachSql = String.format("ATTACH DATABASE \'%s\' AS %s", PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName(), "targetDB");
		zhuijiaDB.ExcuteSQL(attachSql);
		
		try
		{
			String tubanhhao = m_EditLayer.GetDataFieldNameByFieldName("ͼ�ߺ�");
			String doData = "targetDB."+m_EditLayer.GetLayerID()+"_D";
			StringBuilder updateSql = new StringBuilder();
			updateSql.append("update "+doData +" set SYS_GEO=(select SYS_GEO from TAData_D where SYS_ID= "+doData+".SYS_ID limit 1),");
			updateSql.append("SYS_STATUS=(select SYS_STATUS from TAData_D where SYS_ID= "+doData+".SYS_ID limit 1),");
//			updateSql.append("SYS_TYPE= 'ZhuiJia',");
			updateSql.append("SYS_OID=(select SYS_OID from TAData_D where SYS_ID= "+doData+".SYS_ID limit 1),");
			updateSql.append("SYS_LABEL=(select SYS_LABEL from TAData_D where SYS_ID= "+doData+".SYS_ID limit 1),");
			updateSql.append("SYS_DATE=(select SYS_DATE from TAData_D where SYS_ID= "+doData+".SYS_ID limit 1),");
			updateSql.append("SYS_PHOTO=(select SYS_PHOTO from TAData_D where SYS_ID= "+doData+".SYS_ID limit 1),");
			updateSql.append("SYS_Length=(select SYS_Length from TAData_D where SYS_ID= "+doData+".SYS_ID limit 1),");
			updateSql.append("SYS_Area=(select SYS_Area from TAData_D where SYS_ID= "+doData+".SYS_ID limit 1),");
			for(int i = 1;i<45;i++)
			{
				updateSql.append("F"+i+"=(select F"+i+" from TAData_D where SYS_ID= "+doData+".SYS_ID limit 1),");
			}
			updateSql.deleteCharAt(updateSql.length()-1);
			if(isRepet)
			{
				updateSql.append(" where exists(select * from TAData_D where TAData_D.SYS_ID="+doData+".SYS_ID and SYS_TYPE='SHP')");
			}
			else
			{
				updateSql.append(" where exists(select * from TAData_D where TAData_D.SYS_ID="+doData+".SYS_ID)");
			}
			
			zhuijiaDB.ExcuteSQL(updateSql.toString());
			//TODO:update _I
			
			
			String getPhotos = "select SYS_ID,SYS_PHOTO,"+tubanhhao+" from TAData_D where SYS_TYPE='SHP'";//+doData+" where SYS_ID in (select SYS_ID from TAData_D )";
			
			int indexzhuijia = fileName.lastIndexOf("/");
			String zhuijiaPath = fileName.substring(0,indexzhuijia+1);
			SQLiteDataReader readerPhoto = zhuijiaDB.Query(getPhotos);
			while(readerPhoto.Read())
			{
				String tuban = readerPhoto.GetString(tubanhhao);
				int id = readerPhoto.GetInt32("SYS_ID");
				String strPhoto = readerPhoto.GetString("SYS_PHOTO");
				updateAndCopyPhoto(zhuijiaDB,zhuijiaPath,id,strPhoto,tuban);

			}
			
			
			
//			String deletedSql="select distinct "+tubanhhao+" from TAData_D where SYS_ID not in (select SYS_ID from  "+doData+" )";
			String deletedSql="select distinct "+tubanhhao+" from TAData_D  where SYS_TYPE is NULL";
			
			
			SQLiteDataReader deletedReader = zhuijiaDB.Query(deletedSql);
			StringBuilder deletedTuBan= new StringBuilder();
			while(deletedReader.Read())
			{
				deletedTuBan.append(deletedReader.GetString(tubanhhao)+",");
			}
			
			if(deletedTuBan.length()>0)
			{
				if(deletedTuBan.length()>1)
				{
					deletedTuBan.deleteCharAt(deletedTuBan.length()-1);
				}
				
				String deleteTuBanSql = "DELETE from "+doData+" where "+tubanhhao+" in ("+deletedTuBan.toString()+")";
//				String deleteTuBanIndex = "DELETE from targetDB."+m_EditLayer.GetLayerID()+"_I where SYS_ID in ("+deletedId.toString()+")";
				Log.e("deleteTuBanSql", deleteTuBanSql);
				zhuijiaDB.ExcuteSQL(deleteTuBanSql);
//				zhuijiaDB.ExcuteSQL(deleteTuBanIndex);
			}
			
			StringBuilder updateIndex = new StringBuilder();
//			updateIndex.append("update targetDB."+m_EditLayer.GetLayerID()+"_I select * from  TAData_I where exists(select * from TAData_I TAData_D.SYS_ID="+doData+".SYS_ID)");
			updateIndex.append("update targetDB."+m_EditLayer.GetLayerID()+"_I select * from  TAData_I where exists(select * from TAData_I where TAData_D.SYS_ID=TAData_I.SYS_ID AND TADATA_D.SYS_TYEP ='SHP')");
			
			//��Ҫ����DCKP
			ArrayList<String> changedIDs=new ArrayList<String>();
			String notInSql="select SYS_ID,"+tubanhhao+" from TAData_D where SYS_TYPE is NULL";
			SQLiteDataReader DR = zhuijiaDB.Query(notInSql);
			while (DR.Read()) {
				int sysID = DR.GetInt32("SYS_ID");
				
				
				StringBuilder insertSql= new StringBuilder();
				insertSql.append("insert into "+doData+" select NULL,SYS_GEO,SYS_STATUS,SYS_TYPE,SYS_OID,SYS_LABEL,SYS_DATE,SYS_PHOTO,SYS_Length,SYS_Area,SYS_BZ1,SYS_BZ2,SYS_BZ3,SYS_BZ4,SYS_BZ5,");
				for(int i=1;i<226;i++)
				{
					insertSql.append("F"+i+",");
				}
				insertSql.deleteCharAt(insertSql.length()-1);
				insertSql.append(" from TAData_D where SYS_ID = "+sysID+"");
				zhuijiaDB.ExcuteSQL(insertSql.toString());
				
				
				String SQL = "select max(SYS_ID)as SYSID,"+ tubanhhao +" from " + doData;
				SQLiteDataReader reader = zhuijiaDB.Query(SQL);
				if (reader.Read()) {
					int newId = reader.GetInt32("SYSID");
					Log.e("sysID", "old id is:"+sysID+",newID is:"+newId);
					if(newId != sysID)
					{
						changedIDs.add(sysID+","+newId);
						
					}
					
					try
					{
						String insertPhotos = "select SYS_PHOTO, "+tubanhhao+" from "+doData+" where SYS_ID="+newId;
						SQLiteDataReader readerPhotos = zhuijiaDB.Query(insertPhotos);
						if(readerPhotos.Read())
						{
							String tuban = readerPhotos.GetString(tubanhhao);
							String strInsertPhotos = readerPhotos.GetString("SYS_PHOTO");
							updateAndCopyPhoto(zhuijiaDB,zhuijiaPath,newId,strInsertPhotos,tuban);
						}
						
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					
					String indexSql = "insert into targetDB."+m_EditLayer.GetLayerID()+"_I select "+newId+",RIndex,CIndex,MinX,MinY,MaxX,MaxY from TAData_I where SYS_ID = "+sysID;
					zhuijiaDB.ExcuteSQL(indexSql);
					
					
				}
				reader.Close();
				
				
			}
			DR.Close();
			
			ChangeDCKPIds(zhuijiaDB,changedIDs);
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(ex.getMessage());
			ex.printStackTrace();
		}
		
		zhuijiaDB.Close();
	}
	
	private void ChangeDCKPIds(ASQLiteDatabase zhuijiaDB,ArrayList<String> changedIDs)
	{
		for(int i = changedIDs.size()-1;i>=0;i--)
		{
			String[] couples = changedIDs.get(i).split(",");
			if(couples.length == 2)
			{
				String sysID =  couples[0];
				String newId = couples[1];
				
				String updateDCKP1 ="update T_DuchaJCKP set a160="+newId+" where a160="+sysID;
				zhuijiaDB.ExcuteSQL(updateDCKP1);
//				
				String likeSql = "select a160 from T_DuchaJCKP where a160 like '%,"+sysID+",%' OR a160 like '"+sysID+",%' OR a160 like '%,"+sysID+"'";
				SQLiteDataReader readerLike = zhuijiaDB.Query(likeSql);
				while(readerLike.Read())
				{
				
					String ids = readerLike.GetString("a160");
					String id = readerLike.GetString("a160");
					Log.e("old A160", ids);
					String newIDs = ids.replace(","+sysID+",", ","+newId+",").replace(","+sysID, ","+newId).replace(sysID+",", newId+",");
					Log.e("new A160", newIDs);
					String updateLike = "update T_DuchaJCKP set a160='"+newIDs+"' where a160='"+id+"'";
					Log.e("updateLike", updateLike);
					zhuijiaDB.ExcuteSQL(updateLike);
				}
				readerLike.Close();
				
			}
		}
	}
	
	private void ExportDuChaPhotos()
	{
		String mPhotoPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/Photo/";
		final Dataset pDataset = PubVar.m_Workspace.GetDatasetById(m_EditLayer.GetLayerID());

		String tbhDF = m_EditLayer.GetDataFieldNameByFieldName("ͼ�ߺ�");

		if (tbhDF.isEmpty()) {
			Tools.ShowMessageBox("�����Ƿ��С�ͼ�ߺš��ֶΣ������޷���������");
			return;
		}

		String querySql = "select SYS_ID," + tbhDF + ",SYS_PHOTO from "
				+ pDataset.getDataTableName() + " where SYS_STATUS=0 ";
		SQLiteDataReader DR = pDataset.getDataSource().Query(querySql);

		String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���/"
				+ m_EditLayer.GetLayerAliasName() + "/";

		if (!lkmap.Tools.Tools.ExistFile(exportPath)) {
			(new File(exportPath)).mkdirs();
		}
		
		while (DR.Read()) {

			String strPhotos = DR.GetString("SYS_PHOTO");
			if (strPhotos != null && strPhotos.length() > 0) {
				String[] photos = strPhotos.split(",");
				for (int i = 0; i < photos.length; i++) {
					// File f1 = new File(mPhotoPath+photos[i]);
					File f1 = new File(photos[i]);
					if (f1.exists()) {

						String newFileName;
						if (photos.length> 1) {
							newFileName = exportPath + "/ͼ��" + DR.GetString(tbhDF) + "_" + (i + 1) + ".jpg";

						} else {
							newFileName = exportPath + "/ͼ��" + DR.GetString(tbhDF) + ".jpg";
						}

						// CopyFile(mPhotoPath+photos[i],newFileName);
						CopyFile(photos[i], newFileName);

					}
				}
			}
		}

		Tools.ShowMessageBox("��Ƭ�������,�뵽"+exportPath+"Ŀ¼�²鿴��");
	}
	
	private void ExportDuChaExcel() {
		try {
			if (!lkmap.Tools.Tools
					.ExistFile(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���")) {
				(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���"))
						.mkdirs();
			}

			InputStream inputStream = _Dialog.getContext().getResources().openRawResource(R.raw.slduyztndckp);// ��raw�е�test.db������������
			FileOutputStream fileOutputStream = new FileOutputStream(
					PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���/"
							+ m_EditLayer.GetLayerAliasName() + ".xls");// ���µ��ļ������������
			byte[] buff = new byte[8192];
			int len = 0;
			while ((len = inputStream.read(buff)) > 0) {
				fileOutputStream.write(buff, 0, len);
			}
			fileOutputStream.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			Tools.ShowMessageBox("�����ļ�ʧ��" + e.getMessage());
			return;
		}

		try {

			Workbook wb = Workbook
					.getWorkbook(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
							+ "/ɭ�ֶ������ݵ���/" + m_EditLayer.GetLayerAliasName() + ".xls"));
			WritableWorkbook book = Workbook
					.createWorkbook(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
							+ "/ɭ�ֶ������ݵ���/" + m_EditLayer.GetLayerAliasName() + ".xls"), wb);

			getTbyzContent(book.getSheet(1));
			
			WritableSheet jckpSheet = book.getSheet(0);
			
			SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
			String sql = "select * from T_DuChaJCKP where a159='" + m_EditLayer.GetLayerID() + "' and a2 is not null";
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			int j = 6;
			while (reader.Read()) {

				Label label = new Label(0, j, reader.GetInt32("a1") + "");
				jckpSheet.addCell(label);

				for (int i = 1; i < 158; i++) {
					String value = reader.GetString("a" + (i + 1));

					Label label2 = new Label(i, j, value);
					jckpSheet.addCell(label2);
				}
				j++;
			}
			reader.Close();

			book.write();
			book.close();
			wb.close();
		} catch (Exception ex) {
			Tools.ShowMessageBox("��������ʧ��" + ex.getMessage());
			return;
		}

		Tools.ShowMessageBox("ɭ�ֶ��������ѵ������뵽" + PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
				+ "/ɭ�ֶ������ݵ���/" + m_EditLayer.GetLayerAliasName() + ".xls �鿴��");
	}

	private void getTbyzContent(WritableSheet sheet) {

		HashMap<String,String> hpProvince =new HashMap<String,String>();
		hpProvince.put("����ʡ", "61");
		hpProvince.put("����ʡ", "62");
		hpProvince.put("�ຣʡ", "63");
		hpProvince.put("����", "64");
		hpProvince.put("�½�", "65");
		hpProvince.put("�½�����", "85");
		hpProvince.put("����", "11");
		hpProvince.put("���", "12");
		hpProvince.put("�ӱ�ʡ", "13");
		hpProvince.put("ɽ��ʡ", "14");
		hpProvince.put("���ɹ�", "15");
		hpProvince.put("����ʡ", "21");
		hpProvince.put("����ʡ", "22");
		hpProvince.put("������ʡ", "23");
		hpProvince.put("�Ϻ�", "31");
		hpProvince.put("����ʡ", "32");
		hpProvince.put("�㽭ʡ", "33");
		hpProvince.put("����ʡ", "34");
		hpProvince.put("����ʡ", "35");
		hpProvince.put("����ʡ", "36");
		hpProvince.put("ɽ��ʡ", "37");
		hpProvince.put("����ʡ", "41");
		hpProvince.put("����ʡ", "42");
		hpProvince.put("����ʡ", "43");
		hpProvince.put("�㶫ʡ", "44");
		hpProvince.put("����ʡ", "45");
		hpProvince.put("����ʡ", "46");
		hpProvince.put("����", "50");
		hpProvince.put("�Ĵ�ʡ", "51");
		hpProvince.put("����ʡ", "52");
		hpProvince.put("����ʡ", "53");
		hpProvince.put("����", "54");
		hpProvince.put("����ɭ��", "81");
		hpProvince.put("����ɭ��", "82");
		hpProvince.put("��������", "83");
		hpProvince.put("���˰���", "84");
		
		try
		{
			String b2 = m_EditLayer.GetDataFieldNameByFieldName("ʡ");
			String b3 = m_EditLayer.GetDataFieldNameByFieldName("��");
			String b4 = m_EditLayer.GetDataFieldNameByFieldName("�������");
			
			String b5 = m_EditLayer.GetDataFieldNameByFieldName("����");
			String b6 = m_EditLayer.GetDataFieldNameByFieldName("��");
			String b7 = m_EditLayer.GetDataFieldNameByFieldName("ͼ�ߺ�");
			String b8 = m_EditLayer.GetDataFieldNameByFieldName("ǰ��ʱ��");
			String b9 = m_EditLayer.GetDataFieldNameByFieldName("����ʱ��");
			String b10 = m_EditLayer.GetDataFieldNameByFieldName("������");
			String b11 = m_EditLayer.GetDataFieldNameByFieldName("������");
			String b12 = m_EditLayer.GetDataFieldNameByFieldName("�ж����");
			String b13 = m_EditLayer.GetDataFieldNameByFieldName("��ע");
			
			String b14 = m_EditLayer.GetDataFieldNameByFieldName("ǰ����");
			String b15 = m_EditLayer.GetDataFieldNameByFieldName("�ֵ���");
			String b16 = m_EditLayer.GetDataFieldNameByFieldName("�ص���̬��������");
			String b17 = m_EditLayer.GetDataFieldNameByFieldName("�ı����");
			String b18 = m_EditLayer.GetDataFieldNameByFieldName("Υ��ı����");
			String b19 = m_EditLayer.GetDataFieldNameByFieldName("�ɷ����");
			String b20 = m_EditLayer.GetDataFieldNameByFieldName("Υ��ɷ����");
			String b21 = m_EditLayer.GetDataFieldNameByFieldName("�仯ԭ��");
			String b22 = m_EditLayer.GetDataFieldNameByFieldName("��鼶��");
			String b23 = m_EditLayer.GetDataFieldNameByFieldName("����Ƿ�һ��");
			String b24 = m_EditLayer.GetDataFieldNameByFieldName("��鱸ע");
			String b25 = m_EditLayer.GetDataFieldNameByFieldName("��鵥λ����");
			String b26 = m_EditLayer.GetDataFieldNameByFieldName("�����Ա");
			String b27 = m_EditLayer.GetDataFieldNameByFieldName("�������");
			
			
			
			String sql = "select DISTINCT " + b2 + " as b2,"+ b3+" as b3,"+b4+" as b4,"+b5+" as b5,"+b6+" as b6,"+b7+" as b7,"+b8+" as b8,"
			+b9+" as b9,"+b10+" as b10,"+b11+" as b11,"+b12+" as b12,"+b13+" as b13,"+b13+" as b13,"+b14+" as b14,"+b15+" as b15,"+
			b16+" as b16,"+b17+" as b17,"+b18+" as b18,"+b19+" as b19,"+b20+" as b20,"+b21+" as b21,"+b22+" as b22,"+b23+" as b23,"+
			b24+" as b24,"+b25+" as b25,"+b26+" as b26,"+b27+" as b27 ";
			
			
			 Dataset pDataset = PubVar.m_Workspace.GetDatasetById(m_EditLayer.GetLayerID());
			 sql = sql+ "from "+pDataset.getDataTableName()+" where SYS_STATUS = 0 and b14 is not null";
			 SQLiteDataReader reader = pDataset.getDataSource().Query(sql);
			 int j = 6;
			 int k = 1;
			while (reader.Read()) {
				
				Label label = new Label(0, j, k+ "");
				sheet.addCell(label);
				k++;
				
				for (int i = 1; i < 27; i++) {
					
					String value = reader.GetString("b" + (i + 1));
					if(i==1)
					{
						String hmValue = hpProvince.get(value);
						if(hmValue != null)
						{
							value = hmValue;
						}
					}
					
					Label label2 = new Label(i, j, value);
					sheet.addCell(label2);
				}
				j++;
			}
			reader.Close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox("������֤ͼ��ʧ��" + ex.getMessage());
			Log.e("ExportYZTB", ex.getMessage());
		}
		
		 
		 
		 
		 
	}

	private void ExportPhotos(List<String> dataFields, boolean isNewPath) {
		String mPhotoPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/Photo/";
		final Dataset pDataset = PubVar.m_Workspace.GetDatasetById(m_EditLayer.GetLayerID());

		String[] dfName = new String[dataFields.size()];
		String sqlField = "";
		for (int i = 0; i < dataFields.size(); i++) {
			dfName[i] = m_EditLayer.GetDataFieldNameByFieldName(dataFields.get(i));
			if (dfName[i].isEmpty()) {
				Tools.ShowMessageBox("�����Ƿ����ֶΡ�" + dataFields.get(i) + "���������޷���������");
				return;
			} else {
				sqlField += dfName[i] + ",";
			}
		}

		String querySql = "select SYS_ID," + sqlField + "SYS_PHOTO from " + pDataset.getDataTableName()
				+ " where SYS_STATUS=0 ";
		SQLiteDataReader DR = pDataset.getDataSource().Query(querySql);

		String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/��Ƭ����/"
				+ m_EditLayer.GetLayerAliasName() + "/";

		int count = DR.GetCount();
		while (DR.Read()) {
			String photoNamePrefix = "";
			for (String field : dfName) {
				photoNamePrefix += "_" + DR.GetString(field);
			}

			String photoPath = exportPath + photoNamePrefix.replaceFirst("_", "") + "/";

			String strPhotos = DR.GetString("SYS_PHOTO");
			if (strPhotos != null && strPhotos.length() > 0) {
				String[] photos = strPhotos.split(",");
				for (int i = 0; i < photos.length; i++) {
					File f1 = new File(photos[i]);
					if (f1.exists()) {

						if (!lkmap.Tools.Tools.ExistFile(photoPath)) {
							(new File(photoPath)).mkdirs();
						}

						String newFileName;
						newFileName = photoPath + "/" + photoNamePrefix.replaceFirst("_", "") + "_" + (i + 1) + ".jpg";

						CopyFile(photos[i], newFileName);

					}
				}
			}
		}

		Tools.ShowMessageBox("��Ƭ������ɣ�");
	}

	private int CopyFile(String fromFile, String toFile) {
		try {
			File dest = new File(toFile);
			if (dest.exists()) {
				dest.delete();
			}
			dest.createNewFile();

			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(dest);
			int size = fosfrom.available();
			byte bt[] = new byte[size];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;

		} catch (Exception ex) {
			Tools.ShowMessageBox(ex.getMessage());
		}

		return -1;
	}

	// ��ť�¼�
	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("ȷ��")) {

				if (SaveLayerInfo()) {
					m_OldEditLayer = m_EditLayer;

					if (m_Callback != null)
						m_Callback.OnClick("ͼ��", m_OldEditLayer);
					_Dialog.dismiss();
				}
			}

			if (Str.equals("�ֶ�")) // ��ʾ�ֶα༭��ɺ�Ļص�
			{
				LoadLayerInfo(); // ����ˢ��ͼ���б�
			}

			// ѡ���ֶ��б��Ļص�
			if (Str.equals("�б�ѡ��")) {
				HashMap<String, Object> obj = (HashMap<String, Object>) ExtraStr;
				String FieldName = obj.get("D2").toString();
				List<v1_LayerField> lyrFieldList = m_EditLayer.GetFieldList();
				for (v1_LayerField Field : lyrFieldList) {
					if (Field.GetFieldName().equals(FieldName))
						m_SelectField = Field;
				}
				SetButtonEnable(true);
			}

			if (Str.equals("�ֶ�ƥ��")) {
				if (m_EditLayer != null) {
					m_EditLayer.SetFieldList((List<v1_LayerField>) ExtraStr);
					LoadLayerInfo();
				}
			}

		}
	};

	// ����ͼ���Ļص�
	private ICallback m_Callback = null;

	/**
	 * ����ͼ���Ļص�
	 * 
	 * @param cb
	 */
	public void SetCallback(ICallback cb) {
		this.m_Callback = cb;
	}

	// ���е�ͼ���б�
	private List<v1_Layer> m_HaveLayerList = null;

	public void SetHaveLayerList(List<v1_Layer> haveLayerList) {
		this.m_HaveLayerList = haveLayerList;
	}

	// ��ǰ�ֶ��б���ѡ�е��ֶ���
	private v1_LayerField m_SelectField = null;

	private v1_Layer m_OldEditLayer = null;
	private v1_Layer m_EditLayer = null;

	/**
	 * ���õ�ǰ���ڱ༭��ͼ��
	 * 
	 * @param lyr
	 */
	public void SetEditLayer(v1_Layer lyr) {
		this.m_OldEditLayer = lyr;
		this.m_EditLayer = lyr.Clone();

		if (lyr.GetLayerAliasName().contains("�����ҹ�")) {
			_Dialog.findViewById(R.id.btnExportGTPhoto).setVisibility(View.VISIBLE);

		} else if (lyr.GetLayerAliasName().contains("��̬����")) {
			_Dialog.findViewById(R.id.btnExportSTYMPhoto).setVisibility(View.VISIBLE);
		} else {
			// _Dialog.findViewById(R.id.btnExportAllPhoto).setVisibility(View.VISIBLE);
		}

	}

	/**
	 * ����ͼ����Ϣ
	 * 
	 * @return
	 */
	private boolean SaveLayerInfo() {
		// ��ȡͼ����Ϣ
		String LayerName = Tools.GetTextValueOnID(_Dialog, R.id.et_name); // ͼ������
		String LayerType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_type); // ͼ������

		// ��֤ͼ����Ϣ
		String ErrorInfo = "";
		if (LayerName.equals(""))
			ErrorInfo += "��ͼ�����ơ�������Ϊ��ֵ��\r\n";
		for (v1_Layer lyr : this.m_HaveLayerList) {
			if (lyr.GetLayerAliasName().equals(LayerName) && (!lyr.GetLayerID().equals(this.m_EditLayer.GetLayerID())))
				ErrorInfo += "��ͼ�����ơ��������ظ���\r\n";
		}
		if (this.m_EditLayer.GetFieldList().size() == 0) {
			ErrorInfo += "���ֶΡ�����������Ϊ0����\r\n";
		} else {
			try {
				int i = 0;
				for (v1_LayerField field : this.m_EditLayer.GetFieldList()) {
					field.SetIsSelect(Boolean.parseBoolean(dataList.get(i).get("D1") + ""));
					i++;
				}
			} catch (Exception ex) {
				Tools.ShowMessageBox("�����ֶ��Ƿ���ʾ����" + ex.getMessage());
			}
		}

		if (!ErrorInfo.equals("")) {
			Tools.ShowMessageBox(_Dialog.getContext(), ErrorInfo);
			return false;
		}

		// if(this.m_EditLayer.GetLayerProjecType().contains("�˸�����"))
		// {
		m_EditLayer.setCity(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgds));
		m_EditLayer.setCounty(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgqx));
		// DatePicker datepicker =
		// (DatePicker)_Dialog.findViewById(R.id.dp_StartDate);
		YearPicker year = (YearPicker) _Dialog.findViewById(R.id.yearPicker);
		m_EditLayer.setYear(year.getYear());
		// }

		if (this.m_EditLayer.GetLayerProjecType().equals(ForestryLayerType.WeipianJianchaLayer)) {
			if (listWPSJLayerIDs.size() > 0) {
				Spinner spWPXF = (Spinner) _Dialog.findViewById(R.id.sp_wpsjxf);
				m_EditLayer.setWeipianDataLayer(listWPSJLayerIDs.get(spWPXF.getSelectedItemPosition()));
			}
		}

		// ����
		this.m_EditLayer.SetLayerAliasName(LayerName);
		this.m_EditLayer.SetLayerTypeName(LayerType);
		if ((this.m_EditLayer.GetEditMode() == lkEditMode.enNew)) {
			// ����ͼ����������Ĭ�Ϸ���
			if (this.m_EditLayer.GetLayerType() == lkGeoLayerType.enPoint) {
				PointSymbol PS = new PointSymbol();
				this.m_EditLayer.SetSimpleSymbol(PS.ToBase64());
			}
			if (this.m_EditLayer.GetLayerType() == lkGeoLayerType.enPolyline) {
				LineSymbol LS = new LineSymbol();
				this.m_EditLayer.SetSimpleSymbol(LS.ToBase64());
			}
			if (this.m_EditLayer.GetLayerType() == lkGeoLayerType.enPolygon) {
				PolySymbol PS = new PolySymbol();
				this.m_EditLayer.SetSimpleSymbol(PS.ToBase64());
			}

			this.m_EditLayer.SetRenderType(lkRenderType.enSimple);

		} else {
			this.m_EditLayer.SetEditMode(lkEditMode.enEdit);
		}

		return true;
	}

	/**
	 * ����ͼ�������Ϣ
	 */
	List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();

	private void LoadLayerInfo() {
		// ��ͼ���б�
		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_listview), "ͼ���ֶ��б�", pCallback);

		// �ж��Ƿ���Ĭ��ͼ�㣬����Ϊ�༭״̬
		if (this.m_EditLayer != null) {

			dataList.clear();
			// ͼ����ֶ��б�
			List<v1_LayerField> lyrFieldList = this.m_EditLayer.GetFieldList();
			for (v1_LayerField Field : lyrFieldList) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("D1", Field.getIsSelect());
				hm.put("D2", Field.GetFieldName());
				hm.put("D3", Field.GetFieldTypeName());
				hm.put("D4", Field.GetFieldSize());
				hm.put("D5", Field.GetFieldDecimal());
				hm.put("D6", Field.GetFieldEnumCode());
				dataList.add(hm);
			}
			hvf.BindDataToListView(dataList);

			// ͼ������
			if (Tools.GetTextValueOnID(_Dialog, R.id.et_name).equals(""))
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_name, this.m_EditLayer.GetLayerAliasName());

			// ͼ������
			Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, this.m_EditLayer.GetLayerTypeName());
			_Dialog.findViewById(R.id.sp_type).setEnabled(false);

			// ͼ������
			if (this.m_EditLayer.GetLayerProjecType() != null && !this.m_EditLayer.GetLayerProjecType().isEmpty()) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_projecttype, this.m_EditLayer.GetLayerProjecType());
				if (this.m_EditLayer.GetLayerProjecType().contains("̼��")) {
					_Dialog.findViewById(R.id.btnExportTanhui).setVisibility(View.VISIBLE);
				}
			}
			// _Dialog.findViewById(R.id.sp_type).setEnabled(true);

			if (m_EditLayer.getYear() != null) {
				YearPicker yearPicker = (YearPicker) _Dialog.findViewById(R.id.yearPicker);
				yearPicker.setYear(m_EditLayer.getYear());
			}
			if (m_EditLayer.getCity() != null && m_EditLayer.getCity().length() > 0) {
				Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_tgds, m_EditLayer.getCity());
				DictXZQH xzqh = new DictXZQH();
				List<HashMap<String, Object>> xian = xzqh
						.getXZQH(xzqh.getCodeByName(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_tgds), "��", "61"), "��");

				ArrayList<String> qxNames = new ArrayList<String>();
				for (HashMap<String, Object> hm : xian) {
					qxNames.add(hm.get("D1").toString());
				}

				ArrayAdapter<Object> nfAdapter = new ArrayAdapter<Object>(PubVar.m_DoEvent.m_Context,
						android.R.layout.simple_spinner_item, qxNames.toArray());
				nfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Spinner tgqx = ((Spinner) _Dialog.findViewById(R.id.sp_tgqx));
				tgqx.setAdapter(nfAdapter);

				if (m_EditLayer.getCounty() != null && m_EditLayer.getCounty().length() > 0) {
					try {
						Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_tgqx, m_EditLayer.getCounty());
					} catch (Exception ex) {

					}

				}

				// tgqx.setEnabled(false);
			}
		}

		initOthers();

		this.SetButtonEnable(false);

		Spinner splayertype = (Spinner) _Dialog.findViewById(R.id.sp_projecttype);
		splayertype.setOnItemSelectedListener(new ProjectTypeOnItemSelectedListener());

	}

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

}
