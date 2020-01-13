package com.dingtu.SLDuCha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.DTGIS.DataService.DuChaDB;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.DataExport.DataExport_DXF;
import lkmap.ZRoadMap.DataExport.DataExport_KML;
import lkmap.ZRoadMap.DataExport.DataExport_SHP;
import lkmap.ZRoadMap.Project.v1_Layer;

public class DuChaDataExporter {

	private v1_FormTemplate dialogView = null;
	private v1_Layer mLayer;

	@SuppressWarnings("deprecation")
	public DuChaDataExporter(v1_Layer layer) {
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.dialog_duchaexportdata);
		dialogView.ReSetSize(0.5f, 0.7f);

		dialogView.SetCaption(Tools.ToLocale("������������"));
		mLayer = layer;

		Tools.SetTextViewValueOnID(dialogView, R.id.et_LayerName, mLayer.GetLayerAliasName());

		EditText etfolder = (EditText) dialogView.findViewById(R.id.etEndDate);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date today = new Date();
		etfolder.setText(simpleDateFormat.format(today));

		today.setDate(today.getDate() - 7);
		EditText etEndDate = (EditText) dialogView.findViewById(R.id.etStartDate);
		etEndDate.setText(simpleDateFormat.format(today));

		v1_DataBind.SetBindListSpinner(dialogView, "������ʽ",
				new String[] { "ArcGIS(shp)", "AutoCad(dxf)", "Google(kml)" }, R.id.sp_format);
		dialogView.findViewById(R.id.bt_export).setOnClickListener(ExportEvent);

		dialogView.findViewById(R.id.bt_exporttable).setOnClickListener(new ViewClick());

		dialogView.findViewById(R.id.bt_exportImportData).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.bt_BindingExportedPhoto).setOnClickListener(new ViewClick());
	}

	class ViewClick implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			String tag = arg0.getTag().toString();
			if (tag.equals("����������Ƭ")) {
				ExportDuChaExcel2019();
				// ExportDuChaExcel();
			} else if (tag.equals("������׷������")) {
				ExportZhuijiaData();
			} else if (tag.equals("������ѵ�������Ƭ")) {
				BindingPhotoFromExport();
			}
		}
	};

	private void ExportZhuijiaData() {

		ExportDCKPdb(mLayer.GetLayerAliasName());
		ExportDataForZhuijia();
	}

	private void BindingPhotoFromExport() {

		String strTADataFileName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName();

		ASQLiteDatabase SorceDB = new ASQLiteDatabase();
		SorceDB.setDatabaseName(strTADataFileName);
		try {
			String sql = "update " + mLayer.GetLayerID() + "_D set SYS_Photo = NULL";
			SorceDB.ExcuteSQL(sql);

			String srcPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���/"
					+ mLayer.GetLayerAliasName() + "/";
			File f = new File(srcPath);
			if (f.isDirectory()) {

				File[] subs = f.listFiles();
				for (File file : subs) {
					if (file.isFile()) {
						String fileName = file.getName();
						if (fileName.contains("ͼ��")) {
							int indexSperate = fileName.indexOf("_");
							if (indexSperate > 0) {
								String tbhDF = mLayer.GetDataFieldNameByFieldName("ͼ�ߺ�");
								String tubanhao = fileName.subSequence(0, indexSperate).toString().replace("ͼ��", "");
								String selSql = "select * from " + mLayer.GetLayerID() + "_D where " + tbhDF + "='"
										+ tubanhao + "' and SYS_STATUS=0";
								SQLiteDataReader reader = SorceDB.Query(selSql);
								while (reader.Read()) {
									String toFile = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer()
											.GetProjectFullName() + "/Photo/" + fileName;
									String fromFile = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer()
											.GetProjectFullName() + "/ɭ�ֶ������ݵ���/" + mLayer.GetLayerAliasName() + "/"
											+ fileName;
									String initPhoto = reader.GetString("SYS_PHOTO");
									if (Tools.CopyFile(fromFile, toFile)) {
										if (initPhoto == null || initPhoto.length() == 0) {
											initPhoto = toFile;
										} else {
											initPhoto += "," + toFile;
										}

										FileOutputStream fos2 = null;
										try {
											BitmapFactory.Options options = new BitmapFactory.Options();
											options.inPurgeable = true;
											options.inMutable = true;
											options.inInputShareable = true;
											// options.inSampleSize = 2;
											FileInputStream iSteam = new FileInputStream(toFile);
											Bitmap bitmap = BitmapFactory.decodeStream(iSteam, null, options);
											iSteam.close();
											iSteam = null;

											File newPhoto = new File(toFile);
											// fos = new
											// FileOutputStream(newPhoto);
											// bitmap.compress(Bitmap.CompressFormat.JPEG,
											// 100, fos);
											File smallF = new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer()
													.GetProjectFullName() + "/SmallPhoto/" + fileName);
											fos2 = new FileOutputStream(smallF);
											Bitmap b = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 10,
													bitmap.getHeight() / 10, false);
											b.compress(Bitmap.CompressFormat.JPEG, 100, fos2);

											bitmap.recycle();
											bitmap = null;

										} catch (FileNotFoundException e) {
											e.printStackTrace();
										} finally {

											if (fos2 != null) {
												try {
													fos2.flush();
													fos2.close();
													fos2 = null;

												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
										//
										int id = reader.GetInt32("SYS_ID");
										String updateSql = "update " + mLayer.GetLayerID() + "_D set SYS_Photo = '"
												+ initPhoto + "' where SYS_ID=" + id;
										SorceDB.ExcuteSQL(updateSql);

									}

								}
							}

						}
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Tools.ShowMessageBox("������ĳɣ�ͼƬ�Ѿ�������Photo�ļ���");

	}

	private void ExportDataForZhuijia() {
		String strTADataFileName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName();

		String sTargetDB = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/׷��ͼ��/"
				+ mLayer.GetLayerAliasName() + "-����׷��.dtz";

		SQLiteDatabase dckpSorceDB = SQLiteDatabase.openOrCreateDatabase(sTargetDB, null);

		try {
			if (CreateDataset(dckpSorceDB, "TAData")) {
				dckpSorceDB.close();
				String attachSql = String.format("ATTACH DATABASE \'%s\' AS %s", dckpSorceDB.getPath(), "targetDB");
				dckpSorceDB = SQLiteDatabase.openOrCreateDatabase(strTADataFileName, null);
				dckpSorceDB.execSQL(attachSql);
				String dileiField = mLayer.GetDataFieldNameByFieldName("ǰ����");
				StringBuilder insertSql = new StringBuilder();
				insertSql.append("insert into targetDB.TAData_D select * from " + mLayer.GetLayerID() + "_D where "
						+ dileiField + " !='' and SYS_STATUS=0");
				dckpSorceDB.execSQL(insertSql.toString());
				String indexSql = "insert into targetDB.TAData_I select * from " + mLayer.GetLayerID()
						+ "_I where SYS_ID in (select SYS_ID from targetDB.TAData_D)";
				dckpSorceDB.execSQL(indexSql);
				dckpSorceDB.close();

				ASQLiteDatabase zhuijiaDB = new ASQLiteDatabase();
				zhuijiaDB.setDatabaseName(strTADataFileName);

				String getPhoto = "select SYS_PHOTO from " + mLayer.GetLayerID() + "_D where " + dileiField + " !=''";
				SQLiteDataReader reader = zhuijiaDB.Query(getPhoto);
				while (reader.Read()) {
					String photoPaths = reader.GetString("SYS_PHOTO");
					if (photoPaths != null && photoPaths.length() > 0) {
						if (photoPaths.toUpperCase().equals("NULL")) {
							continue;
						}

						String[] photos = photoPaths.split(",");
						for (String photoName : photos) {
							try {
								File file = new File(photoName);
								int indexS = photoName.lastIndexOf("/");
								String newName = photoName;

								if (indexS > 0) {
									newName = photoName.substring(indexS + 1);
									Log.e("newPhotoName", newName);

								}

								String samllFileName = photoName.replace("Photo", "smallPhoto");
								String newSmallName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer()
										.GetProjectFullName() + "/׷��ͼ��/Photos/SamllPhotos/" + newName;
								newName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
										+ "/׷��ͼ��/Photos/" + newName;

								if (file.exists()) {
									Tools.CopyFile(photoName, newName);
								}
								File smallFile = new File(samllFileName);
								if (smallFile.exists()) {
									Tools.CopyFile(samllFileName, newSmallName);
								}

							} catch (Exception ex) {
								ex.printStackTrace();
							}

						}
					}
				}

				reader.Close();
				Tools.ShowMessageBox(
						"����׷�ӵ������ļ��ѵ������뽫�ļ��� " + PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
								+ "/׷��ͼ�� ������Ŀ���豸����׷�Ӻϲ�");

			}

		} catch (Exception ex) {
			Tools.ShowMessageBox(ex.getMessage());
		} finally {
			dckpSorceDB.close();
		}

	}

	public boolean CreateDataset(SQLiteDatabase dckpSorceDB, String DatasetID) {
		// ����(_D)���ݱ��Լ�(_I)������
		List<String> createSQL = new ArrayList<String>();
		createSQL.add("CREATE TABLE " + DatasetID + "_D (");
		createSQL.add("SYS_ID integer primary key ,");
		createSQL.add("SYS_GEO Blob,"); // ͼ��ʵ��
		createSQL.add("SYS_STATUS int,"); // ״̬
		createSQL.add("SYS_TYPE varchar(50),"); // ʵ������
		createSQL.add("SYS_OID varchar(50),"); // ΨһֵGUID
		createSQL.add("SYS_LABEL varchar(50),"); // ��עֵ
		createSQL.add("SYS_DATE varchar(50),"); // �ɼ�ʱ��
		createSQL.add("SYS_PHOTO Text,"); // ��Ƭ�ֶ�
		createSQL.add("SYS_Length double,"); // ����
		createSQL.add("SYS_Area double,"); // ���
		createSQL.add("SYS_BZ1 Text,"); // ��ע�ֶ�1
		createSQL.add("SYS_BZ2 Text,"); // ��ע�ֶ�2
		createSQL.add("SYS_BZ3 Text,"); // ��ע�ֶ�3
		createSQL.add("SYS_BZ4 Text,"); // ��ע�ֶ�4
		createSQL.add("SYS_BZ5 Text,"); // ��ע�ֶ�5

		for (int i = 1; i <= 225; i++) {
			String FName = "F" + i;
			String FType = "varchar(255) default ''";
			createSQL.add(FName + " " + FType + ",");
		}
		String EndStr = createSQL.get(createSQL.size() - 1);
		createSQL.remove(createSQL.size() - 1);
		createSQL.add(EndStr.substring(0, EndStr.length() - 1));
		createSQL.add(")");

		String SQL_D = Tools.JoinT("\r\n", createSQL);
		String SQL_I = "CREATE TABLE " + DatasetID + "_I (" + "SYS_ID INTEGER PRIMARY KEY NOT NULL,"
				+ "RIndex int,CIndex int,MinX double,MinY double,MaxX double,MaxY double)";
		// String SQL_Index_D = "CREATE UNIQUE INDEX 'Sys_ID_Index" +
		// UUID.randomUUID().toString() + "' on " + DatasetID
		// + "_D (SYS_ID ASC)";
		// String SQL_Index_I = "CREATE INDEX 'Sys_ID_Index" +
		// UUID.randomUUID().toString() + "' on " + DatasetID
		// + "_I (RIndex ASC,CIndex ASC)";
		try {
			dckpSorceDB.execSQL(SQL_D);
			dckpSorceDB.execSQL(SQL_I);
			return true;
		} catch (Exception ex) {
			Tools.ShowMessageBox(ex.getMessage());
			return false;
		}
	}

	private void ExportDCKPdb(String layerId) {
		SQLiteDatabase dckpSorceDB;
		try {
			String sTargetDB = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/׷��ͼ��/"
					+ layerId + "-����׷��.dtz";
			File dbFile = new File(sTargetDB);
			if (dbFile.exists()) {
				dbFile.delete();
			}

			Tools.CopyFile(PubVar.m_SysAbsolutePath + "/sysfile/Template.dbx", sTargetDB);
			dckpSorceDB = SQLiteDatabase.openOrCreateDatabase(sTargetDB, null);

			dckpSorceDB.execSQL("PRAGMA encoding = 'UTF-16le' ");
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE if not exists T_DuChaJCKP (a1 INTEGER PRIMARY KEY");
			for (int i = 2; i < 178; i++) {
				String field = ",a" + i + " TEXT";
				sql.append(field);
			}
			sql.append(")");
			dckpSorceDB.execSQL(sql.toString());

			// SQLiteDatabase dckpDB =
			// SQLiteDatabase.openOrCreateDatabase(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName(),null);
			//
			String attachSql = String.format("ATTACH DATABASE \'%s\' AS %s", dckpSorceDB.getPath(), "targetDB");
			PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(attachSql);
			StringBuilder insertSql = new StringBuilder();
			insertSql.append("insert into targetDB.T_DuChaJCKP select a1"); // +"from
																			// T_DuChaJCKP
																			// where
																			// "
			for (int i = 2; i < 178; i++) {
				insertSql.append(",a" + i);
			}
			insertSql.append(" from T_DuChaJCKP");
			// insertSql.append(" where main.a159='"+mLayer.GetLayerID() +"' and
			// main.a160 !='' and main.al60 != null");
			insertSql.append(" where a159='" + mLayer.GetLayerID() + "'");

			PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(insertSql.toString());

			// dckpDB.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			String detachDB = "DETACH DATABASE 'targetDB'";
			try {
				PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(detachDB);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	private void ExportDuChaExcel2019() {
		try {
			if (!lkmap.Tools.Tools
					.ExistFile(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���")) {
				(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���"))
						.mkdirs();
			}

			InputStream inputStream = dialogView.getContext().getResources().openRawResource(R.raw.sldu2019);// ��raw�е�test.db������������
			FileOutputStream fileOutputStream = new FileOutputStream(
					PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���/"
							+ mLayer.GetLayerAliasName() + ".xls");// ���µ��ļ������������
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

		getExcelContent2019();
	}

	private void ExportDuChaExcel() {
		try {
			if (!lkmap.Tools.Tools
					.ExistFile(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���")) {
				(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���"))
						.mkdirs();
			}

			InputStream inputStream = dialogView.getContext().getResources().openRawResource(R.raw.slduyztndckp);// ��raw�е�test.db������������
			FileOutputStream fileOutputStream = new FileOutputStream(
					PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���/"
							+ mLayer.GetLayerAliasName() + ".xls");// ���µ��ļ������������
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
							+ "/ɭ�ֶ������ݵ���/" + mLayer.GetLayerAliasName() + ".xls"));
			WritableWorkbook book = Workbook
					.createWorkbook(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
							+ "/ɭ�ֶ������ݵ���/" + mLayer.GetLayerAliasName() + ".xls"), wb);

			String allIds = "";
			CheckBox cbTBYZ = (CheckBox) dialogView.findViewById(R.id.cb_duchaTable1);
			String tubanyanzhen = "";
			if (cbTBYZ.isChecked()) {
				allIds = getTbyzContent(book.getSheet(1));
			}

			CheckBox cbJCKP = (CheckBox) dialogView.findViewById(R.id.cb_duchaTable2);
			if (cbJCKP.isChecked()) {
				WritableSheet jckpSheet = book.getSheet(0);

				SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
				String sql = "select * from T_DuChaJCKP where a159='" + mLayer.GetLayerID() + "'  and a2 is not null";
				if (!cbTBYZ.isChecked()) {

					String dateFilter = getDateRange();
					if (dateFilter.length() > 0) {
						sql += " and a158 in (" + dateFilter + ")";
					}

				} else {
					if (allIds.length() > 0) {
						// sql += " and a160 in ("+ allIds +")";
					} else {
						String dateFilter = getDateRange();
						if (dateFilter.length() > 0) {
							sql += " and a158 in (" + dateFilter + ")";
						}
					}
				}

				// sql += " order by a6";

				SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
				int j = 6;

				while (reader.Read()) {

					boolean isContain = false;
					if (cbTBYZ.isChecked() && allIds.length() > 0) {

						String ids = reader.GetString("a160");
						if (ids.contains(",")) {
							for (String id : ids.split(",")) {
								for (String a : allIds.split(",")) {
									if (id.equals(a)) {
										isContain = true;
										break;
									}
								}

								if (isContain) {
									break;
								}
							}
						} else {
							for (String a : allIds.split(",")) {
								if (ids.equals(a)) {
									isContain = true;
									break;
								}
							}
						}

					} else {
						isContain = true;
					}

					if (isContain) {

						Label label = new Label(0, j, reader.GetInt32("a1") + "");
						jckpSheet.addCell(label);

						for (int i = 1; i < 158; i++) {
							String value = reader.GetString("a" + (i + 1));

							if (i > 145) {
								// �°浼����ʽ
								Label label2 = new Label(i + 18, j, value);
								jckpSheet.addCell(label2);
							} else {

								Label label2 = new Label(i, j, value);
								jckpSheet.addCell(label2);
							}
						}
						j++;
					}

				}
				reader.Close();
			}

			book.write();
			book.close();
			wb.close();
		} catch (Exception ex) {
			Tools.ShowMessageBox("��������ʧ��" + ex.getMessage());
			return;
		}

		CheckBox cb_duchaPhoto = (CheckBox) dialogView.findViewById(R.id.cb_duchaPhoto);
		if (cb_duchaPhoto.isChecked()) {
			ExportDuChaPhotos();
		}

		Tools.ShowMessageBox("ɭ�ֶ��������ѵ������뵽" + PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
				+ "/ɭ�ֶ������ݵ���/" + mLayer.GetLayerAliasName() + ".xls �鿴��");
	}

	private void getExcelContent2019() {

		List<String> sheetAFields = new ArrayList<String>();
		List<String> sheetBFields = new ArrayList<String>();

		String a16 = mLayer.GetDataFieldNameByFieldName("��״����");
		String b35 = mLayer.GetDataFieldNameByFieldName("ʹ���ֵ�����");

		if (a16.length() == 0) {
			Tools.ShowMessageBox("û����״�����ֶΣ��޷�����");
			return;
		}

		if (b35.length() == 0) {
			Tools.ShowMessageBox("û��ʹ���ֵ������ֶΣ��޷�����");
			return;
		}

		String a1 = mLayer.GetDataFieldNameByFieldName("˳���");
		String a2 = mLayer.GetDataFieldNameByFieldName("�ж�ͼ�߱��");
		String a3 = mLayer.GetDataFieldNameByFieldName("ʡ");
		String a4 = mLayer.GetDataFieldNameByFieldName("��");
		String a5 = mLayer.GetDataFieldNameByFieldName("����");
		String a6 = mLayer.GetDataFieldNameByFieldName("��");
		String a7 = mLayer.GetDataFieldNameByFieldName("��ҵ��");
		String a8 = mLayer.GetDataFieldNameByFieldName("�ֳ�");
		String a9 = mLayer.GetDataFieldNameByFieldName("�ְ�");
		String a10 = mLayer.GetDataFieldNameByFieldName("������");
		String a11 = mLayer.GetDataFieldNameByFieldName("������");
		String a12 = mLayer.GetDataFieldNameByFieldName("�ж����");
		sheetAFields.add(a1);
		sheetAFields.add(a2);
		sheetAFields.add(a3);
		sheetAFields.add(a4);
		sheetAFields.add(a5);
		sheetAFields.add(a6);
		sheetAFields.add(a7);
		sheetAFields.add(a8);
		sheetAFields.add(a9);
		sheetAFields.add(a10);
		sheetAFields.add(a11);
		sheetAFields.add(a12);
		
		sheetBFields.add(a1);
		sheetBFields.add(a2);
		sheetBFields.add(a3);
		sheetBFields.add(a4);
		sheetBFields.add(a5);
		sheetBFields.add(a6);
		sheetBFields.add(a7);
		sheetBFields.add(a8);
		sheetBFields.add(a9);
		sheetBFields.add(a10);
		sheetBFields.add(a11);
		sheetBFields.add(a12);

		String a13 = mLayer.GetDataFieldNameByFieldName("�ж�����");
		String a14 = mLayer.GetDataFieldNameByFieldName("�ж��仯ԭ��");
		String a15 = mLayer.GetDataFieldNameByFieldName("ǰ����");
		String a17 = mLayer.GetDataFieldNameByFieldName("�仯ԭ��");
		String a18 = mLayer.GetDataFieldNameByFieldName("�ж���ע");
		sheetAFields.add(a13);
		sheetAFields.add(a14);
		sheetAFields.add(a15);
		sheetAFields.add(a16);
		sheetAFields.add(a17);
		sheetAFields.add(a18);

		String b13 = mLayer.GetDataFieldNameByFieldName("��ʵϸ�ߺ�");
		String b14 = mLayer.GetDataFieldNameByFieldName("�ֵع���λ");
		String b15 = mLayer.GetDataFieldNameByFieldName("�仯ԭ��");
		String b16 = mLayer.GetDataFieldNameByFieldName("��Ŀ����");
		String b17 = mLayer.GetDataFieldNameByFieldName("����ĺ�");
		String b18 = mLayer.GetDataFieldNameByFieldName("������");
		String b19 = mLayer.GetDataFieldNameByFieldName("������");
		String b20 = mLayer.GetDataFieldNameByFieldName("ʵ�ʸı��ֵ���;���");
		String b21 = mLayer.GetDataFieldNameByFieldName("Υ��Υ���ı��ֵ���;���");
		String b22 = mLayer.GetDataFieldNameByFieldName("Υ��Υ������Ȼ���������");
		String b23 = mLayer.GetDataFieldNameByFieldName("��Ȼ����������");
		String b24 = mLayer.GetDataFieldNameByFieldName("��Ȼ�����ؼ���");
		String b25 = mLayer.GetDataFieldNameByFieldName("Υ��Υ������ľ�ֵ����");
		String b26 = mLayer.GetDataFieldNameByFieldName("Υ��Υ�����������");
		String b27 = mLayer.GetDataFieldNameByFieldName("Υ��Υ���к��������");
		String b28 = mLayer.GetDataFieldNameByFieldName("Υ��Υ���й����ع������");
		String b29 = mLayer.GetDataFieldNameByFieldName("Υ��Υ����������ľ�����");
		String b30 = mLayer.GetDataFieldNameByFieldName("Υ��Υ���������ֵ����");
		String b31 = mLayer.GetDataFieldNameByFieldName("Υ��Υ����һ�����ҹ��������");
		String b32 = mLayer.GetDataFieldNameByFieldName("Υ��Υ���ж������ҹ��������");
		String b33 = mLayer.GetDataFieldNameByFieldName("Υ��Υ���еط����������");
		String b34 = mLayer.GetDataFieldNameByFieldName("Υ��Υ������Ʒ�����");

		String b36 = mLayer.GetDataFieldNameByFieldName("��ľ�ɷ����֤��");
		String b37 = mLayer.GetDataFieldNameByFieldName("��֤���");
		String b38 = mLayer.GetDataFieldNameByFieldName("��֤���");
		String b39 = mLayer.GetDataFieldNameByFieldName("ƾ֤�ɷ����");
		String b40 = mLayer.GetDataFieldNameByFieldName("ƾ֤�ɷ����");
		String b41 = mLayer.GetDataFieldNameByFieldName("��֤�ɷ����");
		String b42 = mLayer.GetDataFieldNameByFieldName("��֤�ɷ����");
		String b43 = mLayer.GetDataFieldNameByFieldName("��֤�ɷ����");
		String b44 = mLayer.GetDataFieldNameByFieldName("��֤�ɷ����");
		String b45 = mLayer.GetDataFieldNameByFieldName("��ע");
		sheetBFields.add(b13);
		sheetBFields.add(b14);
		sheetBFields.add(b15);
		sheetBFields.add(b16);
		sheetBFields.add(b17);
		sheetBFields.add(b18);
		sheetBFields.add(b19);
		sheetBFields.add(b20);
		sheetBFields.add(b21);
		sheetBFields.add(b22);
		sheetBFields.add(b23);
		sheetBFields.add(b24);
		sheetBFields.add(b25);
		sheetBFields.add(b26);
		sheetBFields.add(b27);
		sheetBFields.add(b28);
		sheetBFields.add(b29);
		sheetBFields.add(b30);
		sheetBFields.add(b31);
		sheetBFields.add(b32);
		sheetBFields.add(b33);
		sheetBFields.add(b34);
		sheetBFields.add(b35);
		sheetBFields.add(b36);
		sheetBFields.add(b37);
		sheetBFields.add(b38);
		sheetBFields.add(b39);
		sheetBFields.add(b40);
		sheetBFields.add(b41);
		sheetBFields.add(b42);
		sheetBFields.add(b43);
		sheetBFields.add(b44);
		sheetBFields.add(b45);

		String sql = "select *  ";

		CheckBox cbTable1 = (CheckBox) dialogView.findViewById(R.id.cb_duchaTable1);
		CheckBox cbTable2 = (CheckBox) dialogView.findViewById(R.id.cb_duchaTable2);
		boolean exportSheet0 = cbTable1.isChecked();
		boolean exportSheet1 = cbTable2.isChecked();

		try {

			if (exportSheet0 || exportSheet1) {
				Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
				sql = sql + "from " + pDataset.getDataTableName() + " where SYS_STATUS = 0 ";

				Workbook wb = Workbook
						.getWorkbook(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
								+ "/ɭ�ֶ������ݵ���/" + mLayer.GetLayerAliasName() + ".xls"));

				WritableWorkbook book = Workbook
						.createWorkbook(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
								+ "/ɭ�ֶ������ݵ���/" + mLayer.GetLayerAliasName() + ".xls"), wb);

				WritableSheet sheet0 = book.getSheet(0);
				WritableSheet sheet1 = book.getSheet(1);

				SQLiteDataReader reader = pDataset.getDataSource().Query(sql);
				int sheetARow = 1;
				int sheetBRow = 1;

				while (reader.Read()) {

					if (exportSheet0) {

						int sheetAColum = 0;
						String xianzhuang = reader.GetString(a16);
//						if (xianzhuang == null || xianzhuang.length() == 0) {
//
//						} else {
							for (String field : sheetAFields) {
								String content = reader.GetString(field);
								Label label = new Label(sheetAColum, sheetARow, content);
								sheet0.addCell(label);
								sheetAColum++;
							}
							sheetARow++;
//						}

					}

					if (exportSheet1) {
						int sheetBColum = 0;
//						String syldxz = reader.GetString(b35);
//						if (syldxz == null || syldxz.length() == 0) {
//
//						} else {
							for (String field : sheetBFields) {
								String content = reader.GetString(field);
								Label label = new Label(sheetBColum, sheetBRow, content);
								sheet1.addCell(label);
								sheetBColum++;
							}
							sheetBRow++;
//						}
					}

				}
				reader.Close();

				book.write();
				book.close();
				wb.close();
			}

			CheckBox cb_duchaPhoto = (CheckBox) dialogView.findViewById(R.id.cb_duchaPhoto);
			if (cb_duchaPhoto.isChecked()) {
				ExportDuChaPhotos();
			}

			Tools.ShowMessageBox("ɭ�ֶ��������ѵ������뵽" + PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
					+ "/ɭ�ֶ������ݵ���/" + mLayer.GetLayerAliasName() + ".xls �鿴��");

		} catch (Exception ex) {
			ex.printStackTrace();
			Tools.ShowMessageBox("��������ͼ�߱��ʧ��" + ex.getMessage());
		}

	}

	private String getTbyzContent(WritableSheet sheet) {

		HashMap<String, String> hpProvince = new HashMap<String, String>();
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

		hpProvince.put("����", "61");
		hpProvince.put("����", "62");
		hpProvince.put("�ຣ", "63");
		hpProvince.put("�½��������", "85");
		hpProvince.put("�ӱ�", "13");
		hpProvince.put("ɽ��", "14");
		hpProvince.put("����", "21");
		hpProvince.put("����", "22");
		hpProvince.put("������", "23");
		hpProvince.put("����", "32");
		hpProvince.put("�㽭", "33");
		hpProvince.put("����", "34");
		hpProvince.put("����", "35");
		hpProvince.put("����", "36");
		hpProvince.put("ɽ��", "37");
		hpProvince.put("����", "41");
		hpProvince.put("����", "42");
		hpProvince.put("����", "43");
		hpProvince.put("�㶫", "44");
		hpProvince.put("����", "45");
		hpProvince.put("����", "46");
		hpProvince.put("�Ĵ�", "51");
		hpProvince.put("����", "52");
		hpProvince.put("����", "53");

		try {
			String b2 = mLayer.GetDataFieldNameByFieldName("ʡ");
			String b3 = mLayer.GetDataFieldNameByFieldName("��");
			String b4 = mLayer.GetDataFieldNameByFieldName("�������");

			String b5 = mLayer.GetDataFieldNameByFieldName("����");
			String b6 = mLayer.GetDataFieldNameByFieldName("��");
			String b7 = mLayer.GetDataFieldNameByFieldName("ͼ�ߺ�");
			String b8 = mLayer.GetDataFieldNameByFieldName("ǰ��ʱ��");
			String b9 = mLayer.GetDataFieldNameByFieldName("����ʱ��");
			String b10 = mLayer.GetDataFieldNameByFieldName("������");
			String b11 = mLayer.GetDataFieldNameByFieldName("������");
			String b12 = mLayer.GetDataFieldNameByFieldName("�ж����");
			String b13 = mLayer.GetDataFieldNameByFieldName("��ע");

			String b14 = mLayer.GetDataFieldNameByFieldName("ǰ����");
			String b15 = mLayer.GetDataFieldNameByFieldName("�ֵ���");
			String b16 = mLayer.GetDataFieldNameByFieldName("�ص���̬��������");
			String b17 = mLayer.GetDataFieldNameByFieldName("�ı����");
			String b18 = mLayer.GetDataFieldNameByFieldName("Υ��ı����");
			String b19 = mLayer.GetDataFieldNameByFieldName("�ɷ����");
			String b20 = mLayer.GetDataFieldNameByFieldName("Υ��ɷ����");
			String b21 = mLayer.GetDataFieldNameByFieldName("�仯ԭ��");
			String b22 = mLayer.GetDataFieldNameByFieldName("��鼶��");
			String b23 = mLayer.GetDataFieldNameByFieldName("����Ƿ�һ��");
			String b24 = mLayer.GetDataFieldNameByFieldName("��鱸ע");
			String b25 = mLayer.GetDataFieldNameByFieldName("��鵥λ����");
			String b26 = mLayer.GetDataFieldNameByFieldName("�����Ա");
			String b27 = mLayer.GetDataFieldNameByFieldName("�������");

			String sql = "select DISTINCT " + b2 + " as b2," + b3 + " as b3," + b4 + " as b4," + b5 + " as b5," + b6
					+ " as b6," + b7 + " as b7," + b8 + " as b8," + b9 + " as b9," + b10 + " as b10," + b11 + " as b11,"
					+ b12 + " as b12," + b13 + " as b13," + b13 + " as b13," + b14 + " as b14," + b15 + " as b15," + b16
					+ " as b16," + b17 + " as b17," + b18 + " as b18," + b19 + " as b19," + b20 + " as b20," + b21
					+ " as b21," + b22 + " as b22," + b23 + " as b23," + b24 + " as b24," + b25 + " as b25," + b26
					+ " as b26," + b27 + " as b27 ";

			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
			sql = sql + "from " + pDataset.getDataTableName() + " where SYS_STATUS = 0 ";
			String sqlId = "select SYS_ID," + b7 + " from " + pDataset.getDataTableName() + " where SYS_STATUS = 0 ";
			CheckBox cbOlnyCheckData = (CheckBox) dialogView.findViewById(R.id.cb_duchaHasData);
			if (cbOlnyCheckData.isChecked()) {

				sql += " and " + b17 + " !='' ";
				sqlId += " and " + b17 + " !='' ";
			}

			CheckBox cbFilterDate = (CheckBox) dialogView.findViewById(R.id.cb_filterDate);
			if (cbFilterDate.isChecked()) {
				String startText = ((TextView) dialogView.findViewById(R.id.etStartDate)).getText().toString();
				String endText = ((TextView) dialogView.findViewById(R.id.etEndDate)).getText().toString();

				if (startText == null || startText.length() == 0) {
					Tools.ShowMessageBox("��ʼʱ�䲻��Ϊ�գ�");
					// Tools.ShowMessageBox("��ʵʱ�����ڸ�ʽ���ԣ�����д�����������������ա���λ���ڸ�ʽ");
					return "";
				}
				if (endText == null || endText.length() == 0) {
					Tools.ShowMessageBox("����ʱ�䲻��Ϊ�գ�");
					return "";
				}

				SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
				String jcrqDF = mLayer.GetDataFieldNameByFieldName("�������");
				if (jcrqDF.length() == 0) {
					Tools.ShowMessageBox("�Ҳ�������ɸѡ�ֶΣ�");
					return "";
				}

				try {

					Date sDate = SDF.parse(startText);
					Date eDate = SDF.parse(endText);

					StringBuilder dateInSql = new StringBuilder();

					if (sDate.compareTo(eDate) == 0) {
						dateInSql.append(" and " + b27 + " in ('" + startText + "')");
						sql += dateInSql.toString();
						sqlId += dateInSql.toString();
					} else if (sDate.compareTo(eDate) > 0) {
						Tools.ShowMessageBox("��ʼ���ڲ��ܴ��ڽ�������");
						return "";
					} else {

						dateInSql.append(" and " + b27 + " in ('" + startText + "'");
						Calendar calendar = new GregorianCalendar();
						calendar.setTime(sDate);
						calendar.add(calendar.DATE, 1);// ��������������һ��.����������,������ǰ�ƶ�
						Date nextDate = calendar.getTime();

						while (nextDate.compareTo(eDate) <= 0) {
							dateInSql.append(",'" + SDF.format(nextDate) + "'");
							calendar.setTime(nextDate);
							calendar.add(calendar.DATE, 1);// ��������������һ��.����������,������ǰ�ƶ�
							nextDate = calendar.getTime();

						}
						dateInSql.append(")");

						sql += dateInSql.toString();
						sqlId += dateInSql.toString();
					}

				} catch (ParseException ex) {
					Tools.ShowMessageBox("��ʼ��������ڸ�ʽ���ԣ�����д�����������������ա���λ���ڸ�ʽ");
					return "";
				} catch (Exception ex) {
					Tools.ShowMessageBox(ex.getMessage());
					return "";
				}
			}

			// sql += " order by b7";
			// sqlId += "order by "+b7;
			SQLiteDataReader reader = pDataset.getDataSource().Query(sql);
			int j = 6;
			int k = 1;
			while (reader.Read()) {

				Label label = new Label(0, j, k + "");
				sheet.addCell(label);
				k++;

				for (int i = 1; i < 27; i++) {

					String value = reader.GetString("b" + (i + 1));
					if (i == 1) {
						String hmValue = hpProvince.get(value);
						if (hmValue != null) {
							value = hmValue;
						}
					}

					Label label2 = new Label(i, j, value);
					sheet.addCell(label2);
				}
				j++;
			}
			reader.Close();

			String sys_ids = "";
			SQLiteDataReader reader2 = pDataset.getDataSource().Query(sqlId);
			try {

				while (reader2.Read()) {
					if (sys_ids.length() > 0) {
						sys_ids += "," + reader2.GetString("SYS_ID");
					} else {
						sys_ids += reader2.GetString("SYS_ID");
					}

				}
			} catch (Exception ex) {
				ex.printStackTrace();
				Tools.ShowMessageBox("��ȡ��֤ͼ��ʧ�ܣ�" + ex.getMessage());

			} finally {
				reader2.Close();
			}

			return sys_ids;
		} catch (Exception ex) {
			Tools.ShowMessageBox("������֤ͼ��ʧ��" + ex.getMessage());
			Log.e("ExportYZTB", ex.getMessage());
		}

		return "";
	}

	private String getDateRange() {

		StringBuilder dateInSql = new StringBuilder();
		CheckBox cbFilterDate = (CheckBox) dialogView.findViewById(R.id.cb_filterDate);
		if (cbFilterDate.isChecked()) {
			String startText = ((TextView) dialogView.findViewById(R.id.etStartDate)).getText().toString();
			String endText = ((TextView) dialogView.findViewById(R.id.etEndDate)).getText().toString();

			if (startText == null || startText.length() == 0) {
				Tools.ShowMessageBox("��ʼʱ�䲻��Ϊ�գ�");
				// Tools.ShowMessageBox("��ʵʱ�����ڸ�ʽ���ԣ�����д�����������������ա���λ���ڸ�ʽ");
				return "";
			}
			if (endText == null || endText.length() == 0) {
				Tools.ShowMessageBox("����ʱ�䲻��Ϊ�գ�");
				return "";
			}

			SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
			String jcrqDF = mLayer.GetDataFieldNameByFieldName("�������");
			if (jcrqDF.length() == 0) {
				Tools.ShowMessageBox("�Ҳ�������ɸѡ�ֶΣ�");
				return "";
			}

			try {

				Date sDate = SDF.parse(startText);
				Date eDate = SDF.parse(endText);

				if (sDate.compareTo(eDate) == 0) {
					dateInSql.append(startText);
				} else if (sDate.compareTo(eDate) > 0) {
					Tools.ShowMessageBox("��ʼ���ڲ��ܴ��ڽ�������");
					return "";
				} else {

					dateInSql.append(startText);
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(sDate);
					calendar.add(calendar.DATE, 1);// ��������������һ��.����������,������ǰ�ƶ�
					Date nextDate = calendar.getTime();

					while (nextDate.compareTo(eDate) <= 0) {
						dateInSql.append(",'" + SDF.format(nextDate) + "'");
						calendar.setTime(nextDate);
						calendar.add(calendar.DATE, 1);// ��������������һ��.����������,������ǰ�ƶ�
						nextDate = calendar.getTime();

					}
				}

				// dateInSql.append(")");
			} catch (Exception ex) {

			}
		}

		return dateInSql.toString();
	}

	private void ExportDuChaPhotos() {
		String mPhotoPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/Photo/";
		final Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());

		// String tbhDF = mLayer.GetDataFieldNameByFieldName("ͼ�ߺ�");
		String tbhDF = mLayer.GetDataFieldNameByFieldName("�ж�ͼ�߱��");
		String xibanhao = mLayer.GetDataFieldNameByFieldName("��ʵϸ�ߺ�");

		if (tbhDF.isEmpty()) {
			Tools.ShowMessageBox("�����Ƿ��С��ж�ͼ�߱�š��ֶΣ������޷���������");
			return;
		}

		String querySql = "select SYS_ID,"+xibanhao+"," + tbhDF + ",SYS_PHOTO from " + pDataset.getDataTableName()
				+ " where SYS_STATUS=0 ";
		SQLiteDataReader DR = pDataset.getDataSource().Query(querySql);

		String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/ɭ�ֶ������ݵ���/"
				+ mLayer.GetLayerAliasName() + "/";

		if (!lkmap.Tools.Tools.ExistFile(exportPath)) {
			(new File(exportPath)).mkdirs();
		}
		HashMap<String, Integer> tubanShu = new HashMap<String, Integer>();
		while (DR.Read()) {

			int no = 1;
			if (tubanShu.containsKey(DR.GetString(tbhDF))) {
				no = tubanShu.get(DR.GetString(tbhDF));
				tubanShu.put(DR.GetString(tbhDF), no++);
			} else {
				tubanShu.put(DR.GetString(tbhDF), 1);
			}
			String strPhotos = DR.GetString("SYS_PHOTO");

			if (strPhotos != null && strPhotos.length() > 0) {
				String[] photos = strPhotos.split(",");
				if(photos.length>0)
				{
					String pathName = exportPath + "/ͼ��" + DR.GetString(tbhDF);
					File path = new File(pathName);
					if(!path.exists())
					{
						path.mkdir();
					}
				}
				for (int i = 0; i < photos.length; i++) {
					// File f1 = new File(mPhotoPath+photos[i]);
					try {
						File f1 = new File(photos[i]);
						if (f1.exists()) {

							
							String newFileName;
							newFileName = exportPath+"/ͼ��" + DR.GetString(tbhDF) + "/ͼ��" + DR.GetString(tbhDF) + "_" + no + "_" + (i + 1) + ".jpg";
							// if(no ==1)
							// {
							// if (photos.length> 1) {
							// newFileName = exportPath + "/ͼ��" +
							// DR.GetString(tbhDF) + "_" + (i + 1) + ".jpg";
							//
							// } else {
							// newFileName = exportPath + "/ͼ��" +
							// DR.GetString(tbhDF) + ".jpg";
							// }
							// }
							// else
							// {
							// newFileName = exportPath + "/ͼ��" +
							// DR.GetString(tbhDF) + "_"+ no+"_"+ (i + 1) +
							// ".jpg";
							// }

							// CopyFile(mPhotoPath+photos[i],newFileName);
							CopyFile(photos[i], newFileName);
							// f1.delete();

						} else {
							Log.e("SYS_PHOTO", photos[i] + " is not exist, tuban:" + DR.GetString(tbhDF) + "_" + no);
						}
					} catch (Exception ex) {
						Log.e("SYS_PHOTO", photos[i] + ex.getMessage());
					}

				}
			} else {

				// Log.e("SYS_PHOTO", DR.GetString(tbhDF)+" is null");
			}
		}

		Tools.ShowMessageBox("��Ƭ�������,�뵽" + exportPath + "Ŀ¼�²鿴��");
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
			Log.e("CopyFile", ex.getMessage());
			// Tools.ShowMessageBox(ex.getMessage());
		}

		return -1;
	}

	private View.OnClickListener ExportEvent = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			final List<HashMap<String, String>> expDatasetNameList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("LayerID", mLayer.GetLayerID());
			hm.put("LayerName", mLayer.GetLayerAliasName());
			expDatasetNameList.add(hm);
			if (expDatasetNameList.size() <= 0) {
				Tools.ShowMessageBox("û��ѡ���κ�ͼ�㣡");
			} else {
				lkmap.Tools.Tools.OpenDialog("���ڵ�������...", new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						// ��������·��
						String pathName = Tools.GetTextValueOnID(dialogView, R.id.et_LayerName);
						String ExportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
								+ "/���ݵ���/" + pathName;
						if (!Tools.ExistFile(ExportPath))
							(new File(ExportPath)).mkdirs();

						// ���󵼳�ͼ���б�
						List<String> ExportErrorList = new ArrayList<String>();

						// �����͵���
						String ExpType = Tools.GetSpinnerValueOnID(dialogView, R.id.sp_format);
						if (ExpType.equals("ArcGIS(shp)")) {
							for (HashMap<String, String> lyr : expDatasetNameList) {
								// ��ʼ����
								Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.get("LayerID"));
								DataExport_SHP DE = new DataExport_SHP();
								if (!DE.Export(pDataset, ExportPath + "/" + lyr.get("LayerName"), false))
									ExportErrorList.add("LayerName");
							}
						}
						if (ExpType.equals("AutoCad(dxf)")) {
							String ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
							DataExport_DXF DF = new DataExport_DXF();
							ExportErrorList = DF.Export(expDatasetNameList, ExportPath + "/" + ProjectName + ".dxf");
						}
						if (ExpType.equals("Google(kml)")) {
							String ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
							DataExport_KML kml = new DataExport_KML();
							ExportErrorList = kml.Export(expDatasetNameList, ExportPath + "/" + ProjectName + ".kml");
						}

						if (ExportErrorList.size() > 0) {
							Tools.ShowMessageBox(dialogView.getContext(),
									"����ͼ�����ݵ���ʧ�ܣ�\r\n\r\n" + Tools.JoinT("\r\n", ExportErrorList));
						} else {
							Tools.ShowMessageBox(dialogView.getContext(), "���ݳɹ�������\r\n\r\nλ�ڣ���" + ExportPath + "��");
						}
					}
				});
			}

		}
	};

	public void ShowDialog() {

		dialogView.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				// LoadLayerInfo();
			}
		});
		dialogView.show();
	}
}
