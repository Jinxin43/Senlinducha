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

		dialogView.SetCaption(Tools.ToLocale("导出督查数据"));
		mLayer = layer;

		Tools.SetTextViewValueOnID(dialogView, R.id.et_LayerName, mLayer.GetLayerAliasName());

		EditText etfolder = (EditText) dialogView.findViewById(R.id.etEndDate);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date today = new Date();
		etfolder.setText(simpleDateFormat.format(today));

		today.setDate(today.getDate() - 7);
		EditText etEndDate = (EditText) dialogView.findViewById(R.id.etStartDate);
		etEndDate.setText(simpleDateFormat.format(today));

		v1_DataBind.SetBindListSpinner(dialogView, "导出格式",
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
			if (tag.equals("导出表格和照片")) {
				ExportDuChaExcel2019();
				// ExportDuChaExcel();
			} else if (tag.equals("导出可追加数据")) {
				ExportZhuijiaData();
			} else if (tag.equals("反向绑定已导出的照片")) {
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

			String srcPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/森林督查数据导出/"
					+ mLayer.GetLayerAliasName() + "/";
			File f = new File(srcPath);
			if (f.isDirectory()) {

				File[] subs = f.listFiles();
				for (File file : subs) {
					if (file.isFile()) {
						String fileName = file.getName();
						if (fileName.contains("图斑")) {
							int indexSperate = fileName.indexOf("_");
							if (indexSperate > 0) {
								String tbhDF = mLayer.GetDataFieldNameByFieldName("图斑号");
								String tubanhao = fileName.subSequence(0, indexSperate).toString().replace("图斑", "");
								String selSql = "select * from " + mLayer.GetLayerID() + "_D where " + tbhDF + "='"
										+ tubanhao + "' and SYS_STATUS=0";
								SQLiteDataReader reader = SorceDB.Query(selSql);
								while (reader.Read()) {
									String toFile = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer()
											.GetProjectFullName() + "/Photo/" + fileName;
									String fromFile = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer()
											.GetProjectFullName() + "/森林督查数据导出/" + mLayer.GetLayerAliasName() + "/"
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

		Tools.ShowMessageBox("反向绑定文成，图片已经拷贝到Photo文件夹");

	}

	private void ExportDataForZhuijia() {
		String strTADataFileName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName();

		String sTargetDB = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/追加图斑/"
				+ mLayer.GetLayerAliasName() + "-用于追加.dtz";

		SQLiteDatabase dckpSorceDB = SQLiteDatabase.openOrCreateDatabase(sTargetDB, null);

		try {
			if (CreateDataset(dckpSorceDB, "TAData")) {
				dckpSorceDB.close();
				String attachSql = String.format("ATTACH DATABASE \'%s\' AS %s", dckpSorceDB.getPath(), "targetDB");
				dckpSorceDB = SQLiteDatabase.openOrCreateDatabase(strTADataFileName, null);
				dckpSorceDB.execSQL(attachSql);
				String dileiField = mLayer.GetDataFieldNameByFieldName("前地类");
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
										.GetProjectFullName() + "/追加图斑/Photos/SamllPhotos/" + newName;
								newName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
										+ "/追加图斑/Photos/" + newName;

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
						"用于追加的数据文件已导出，请将文件夹 " + PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
								+ "/追加图斑 拷贝到目标设备进行追加合并");

			}

		} catch (Exception ex) {
			Tools.ShowMessageBox(ex.getMessage());
		} finally {
			dckpSorceDB.close();
		}

	}

	public boolean CreateDataset(SQLiteDatabase dckpSorceDB, String DatasetID) {
		// 创建(_D)数据表以及(_I)索引表
		List<String> createSQL = new ArrayList<String>();
		createSQL.add("CREATE TABLE " + DatasetID + "_D (");
		createSQL.add("SYS_ID integer primary key ,");
		createSQL.add("SYS_GEO Blob,"); // 图形实体
		createSQL.add("SYS_STATUS int,"); // 状态
		createSQL.add("SYS_TYPE varchar(50),"); // 实体类型
		createSQL.add("SYS_OID varchar(50),"); // 唯一值GUID
		createSQL.add("SYS_LABEL varchar(50),"); // 标注值
		createSQL.add("SYS_DATE varchar(50),"); // 采集时间
		createSQL.add("SYS_PHOTO Text,"); // 相片字段
		createSQL.add("SYS_Length double,"); // 长度
		createSQL.add("SYS_Area double,"); // 面积
		createSQL.add("SYS_BZ1 Text,"); // 备注字段1
		createSQL.add("SYS_BZ2 Text,"); // 备注字段2
		createSQL.add("SYS_BZ3 Text,"); // 备注字段3
		createSQL.add("SYS_BZ4 Text,"); // 备注字段4
		createSQL.add("SYS_BZ5 Text,"); // 备注字段5

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
			String sTargetDB = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/追加图斑/"
					+ layerId + "-用于追加.dtz";
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
					.ExistFile(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/森林督查数据导出")) {
				(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/森林督查数据导出"))
						.mkdirs();
			}

			InputStream inputStream = dialogView.getContext().getResources().openRawResource(R.raw.sldu2019);// 将raw中的test.db放入输入流中
			FileOutputStream fileOutputStream = new FileOutputStream(
					PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/森林督查数据导出/"
							+ mLayer.GetLayerAliasName() + ".xls");// 将新的文件放入输出流中
			byte[] buff = new byte[8192];
			int len = 0;
			while ((len = inputStream.read(buff)) > 0) {
				fileOutputStream.write(buff, 0, len);
			}
			fileOutputStream.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			Tools.ShowMessageBox("导出文件失败" + e.getMessage());
			return;
		}

		getExcelContent2019();
	}

	private void ExportDuChaExcel() {
		try {
			if (!lkmap.Tools.Tools
					.ExistFile(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/森林督查数据导出")) {
				(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/森林督查数据导出"))
						.mkdirs();
			}

			InputStream inputStream = dialogView.getContext().getResources().openRawResource(R.raw.slduyztndckp);// 将raw中的test.db放入输入流中
			FileOutputStream fileOutputStream = new FileOutputStream(
					PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/森林督查数据导出/"
							+ mLayer.GetLayerAliasName() + ".xls");// 将新的文件放入输出流中
			byte[] buff = new byte[8192];
			int len = 0;
			while ((len = inputStream.read(buff)) > 0) {
				fileOutputStream.write(buff, 0, len);
			}
			fileOutputStream.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			Tools.ShowMessageBox("导出文件失败" + e.getMessage());
			return;
		}

		try {

			Workbook wb = Workbook
					.getWorkbook(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
							+ "/森林督查数据导出/" + mLayer.GetLayerAliasName() + ".xls"));
			WritableWorkbook book = Workbook
					.createWorkbook(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
							+ "/森林督查数据导出/" + mLayer.GetLayerAliasName() + ".xls"), wb);

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
								// 新版导出格式
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
			Tools.ShowMessageBox("导出数据失败" + ex.getMessage());
			return;
		}

		CheckBox cb_duchaPhoto = (CheckBox) dialogView.findViewById(R.id.cb_duchaPhoto);
		if (cb_duchaPhoto.isChecked()) {
			ExportDuChaPhotos();
		}

		Tools.ShowMessageBox("森林督查数据已导出！请到" + PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
				+ "/森林督查数据导出/" + mLayer.GetLayerAliasName() + ".xls 查看！");
	}

	private void getExcelContent2019() {

		List<String> sheetAFields = new ArrayList<String>();
		List<String> sheetBFields = new ArrayList<String>();

		String a16 = mLayer.GetDataFieldNameByFieldName("现状地类");
		String b35 = mLayer.GetDataFieldNameByFieldName("使用林地性质");

		if (a16.length() == 0) {
			Tools.ShowMessageBox("没有现状地类字段，无法导出");
			return;
		}

		if (b35.length() == 0) {
			Tools.ShowMessageBox("没有使用林地性质字段，无法导出");
			return;
		}

		String a1 = mLayer.GetDataFieldNameByFieldName("顺序号");
		String a2 = mLayer.GetDataFieldNameByFieldName("判读图斑编号");
		String a3 = mLayer.GetDataFieldNameByFieldName("省");
		String a4 = mLayer.GetDataFieldNameByFieldName("县");
		String a5 = mLayer.GetDataFieldNameByFieldName("乡镇");
		String a6 = mLayer.GetDataFieldNameByFieldName("村");
		String a7 = mLayer.GetDataFieldNameByFieldName("林业局");
		String a8 = mLayer.GetDataFieldNameByFieldName("林场");
		String a9 = mLayer.GetDataFieldNameByFieldName("林班");
		String a10 = mLayer.GetDataFieldNameByFieldName("横坐标");
		String a11 = mLayer.GetDataFieldNameByFieldName("纵坐标");
		String a12 = mLayer.GetDataFieldNameByFieldName("判读面积");
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

		String a13 = mLayer.GetDataFieldNameByFieldName("判读地类");
		String a14 = mLayer.GetDataFieldNameByFieldName("判读变化原因");
		String a15 = mLayer.GetDataFieldNameByFieldName("前地类");
		String a17 = mLayer.GetDataFieldNameByFieldName("变化原因");
		String a18 = mLayer.GetDataFieldNameByFieldName("判读备注");
		sheetAFields.add(a13);
		sheetAFields.add(a14);
		sheetAFields.add(a15);
		sheetAFields.add(a16);
		sheetAFields.add(a17);
		sheetAFields.add(a18);

		String b13 = mLayer.GetDataFieldNameByFieldName("核实细斑号");
		String b14 = mLayer.GetDataFieldNameByFieldName("林地管理单位");
		String b15 = mLayer.GetDataFieldNameByFieldName("变化原因");
		String b16 = mLayer.GetDataFieldNameByFieldName("项目名称");
		String b17 = mLayer.GetDataFieldNameByFieldName("审核文号");
		String b18 = mLayer.GetDataFieldNameByFieldName("审核年度");
		String b19 = mLayer.GetDataFieldNameByFieldName("审核面积");
		String b20 = mLayer.GetDataFieldNameByFieldName("实际改变林地用途面积");
		String b21 = mLayer.GetDataFieldNameByFieldName("违规违法改变林地用途面积");
		String b22 = mLayer.GetDataFieldNameByFieldName("违规违法中自然保护地面积");
		String b23 = mLayer.GetDataFieldNameByFieldName("自然保护地名称");
		String b24 = mLayer.GetDataFieldNameByFieldName("自然保护地级别");
		String b25 = mLayer.GetDataFieldNameByFieldName("违规违法中乔木林地面积");
		String b26 = mLayer.GetDataFieldNameByFieldName("违规违法中竹林面积");
		String b27 = mLayer.GetDataFieldNameByFieldName("违规违法中红树林面积");
		String b28 = mLayer.GetDataFieldNameByFieldName("违规违法中国家特灌林面积");
		String b29 = mLayer.GetDataFieldNameByFieldName("违规违法中其他灌木林面积");
		String b30 = mLayer.GetDataFieldNameByFieldName("违规违法中其他林地面积");
		String b31 = mLayer.GetDataFieldNameByFieldName("违规违法中一级国家公益林面积");
		String b32 = mLayer.GetDataFieldNameByFieldName("违规违法中二级国家公益林面积");
		String b33 = mLayer.GetDataFieldNameByFieldName("违规违法中地方公益林面积");
		String b34 = mLayer.GetDataFieldNameByFieldName("违规违法中商品林面积");

		String b36 = mLayer.GetDataFieldNameByFieldName("林木采伐许可证号");
		String b37 = mLayer.GetDataFieldNameByFieldName("发证面积");
		String b38 = mLayer.GetDataFieldNameByFieldName("发证蓄积");
		String b39 = mLayer.GetDataFieldNameByFieldName("凭证采伐面积");
		String b40 = mLayer.GetDataFieldNameByFieldName("凭证采伐蓄积");
		String b41 = mLayer.GetDataFieldNameByFieldName("超证采伐面积");
		String b42 = mLayer.GetDataFieldNameByFieldName("超证采伐蓄积");
		String b43 = mLayer.GetDataFieldNameByFieldName("无证采伐面积");
		String b44 = mLayer.GetDataFieldNameByFieldName("无证采伐蓄积");
		String b45 = mLayer.GetDataFieldNameByFieldName("备注");
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
								+ "/森林督查数据导出/" + mLayer.GetLayerAliasName() + ".xls"));

				WritableWorkbook book = Workbook
						.createWorkbook(new File(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
								+ "/森林督查数据导出/" + mLayer.GetLayerAliasName() + ".xls"), wb);

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

			Tools.ShowMessageBox("森林督查数据已导出！请到" + PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
					+ "/森林督查数据导出/" + mLayer.GetLayerAliasName() + ".xls 查看！");

		} catch (Exception ex) {
			ex.printStackTrace();
			Tools.ShowMessageBox("导出督查图斑表格失败" + ex.getMessage());
		}

	}

	private String getTbyzContent(WritableSheet sheet) {

		HashMap<String, String> hpProvince = new HashMap<String, String>();
		hpProvince.put("陕西省", "61");
		hpProvince.put("甘肃省", "62");
		hpProvince.put("青海省", "63");
		hpProvince.put("宁夏", "64");
		hpProvince.put("新疆", "65");
		hpProvince.put("新疆兵团", "85");
		hpProvince.put("北京", "11");
		hpProvince.put("天津", "12");
		hpProvince.put("河北省", "13");
		hpProvince.put("山西省", "14");
		hpProvince.put("内蒙古", "15");
		hpProvince.put("辽宁省", "21");
		hpProvince.put("吉林省", "22");
		hpProvince.put("黑龙江省", "23");
		hpProvince.put("上海", "31");
		hpProvince.put("江苏省", "32");
		hpProvince.put("浙江省", "33");
		hpProvince.put("安徽省", "34");
		hpProvince.put("福建省", "35");
		hpProvince.put("江西省", "36");
		hpProvince.put("山东省", "37");
		hpProvince.put("河南省", "41");
		hpProvince.put("湖北省", "42");
		hpProvince.put("湖南省", "43");
		hpProvince.put("广东省", "44");
		hpProvince.put("广西省", "45");
		hpProvince.put("海南省", "46");
		hpProvince.put("重庆", "50");
		hpProvince.put("四川省", "51");
		hpProvince.put("贵州省", "52");
		hpProvince.put("云南省", "53");
		hpProvince.put("西藏", "54");
		hpProvince.put("内蒙森工", "81");
		hpProvince.put("吉林森工", "82");
		hpProvince.put("龙江集团", "83");
		hpProvince.put("大兴安岭", "84");

		hpProvince.put("陕西", "61");
		hpProvince.put("甘肃", "62");
		hpProvince.put("青海", "63");
		hpProvince.put("新疆建设兵团", "85");
		hpProvince.put("河北", "13");
		hpProvince.put("山西", "14");
		hpProvince.put("辽宁", "21");
		hpProvince.put("吉林", "22");
		hpProvince.put("黑龙江", "23");
		hpProvince.put("江苏", "32");
		hpProvince.put("浙江", "33");
		hpProvince.put("安徽", "34");
		hpProvince.put("福建", "35");
		hpProvince.put("江西", "36");
		hpProvince.put("山东", "37");
		hpProvince.put("河南", "41");
		hpProvince.put("湖北", "42");
		hpProvince.put("湖南", "43");
		hpProvince.put("广东", "44");
		hpProvince.put("广西", "45");
		hpProvince.put("海南", "46");
		hpProvince.put("四川", "51");
		hpProvince.put("贵州", "52");
		hpProvince.put("云南", "53");

		try {
			String b2 = mLayer.GetDataFieldNameByFieldName("省");
			String b3 = mLayer.GetDataFieldNameByFieldName("县");
			String b4 = mLayer.GetDataFieldNameByFieldName("调查年度");

			String b5 = mLayer.GetDataFieldNameByFieldName("乡镇");
			String b6 = mLayer.GetDataFieldNameByFieldName("村");
			String b7 = mLayer.GetDataFieldNameByFieldName("图斑号");
			String b8 = mLayer.GetDataFieldNameByFieldName("前期时间");
			String b9 = mLayer.GetDataFieldNameByFieldName("后期时间");
			String b10 = mLayer.GetDataFieldNameByFieldName("横坐标");
			String b11 = mLayer.GetDataFieldNameByFieldName("纵坐标");
			String b12 = mLayer.GetDataFieldNameByFieldName("判读面积");
			String b13 = mLayer.GetDataFieldNameByFieldName("备注");

			String b14 = mLayer.GetDataFieldNameByFieldName("前地类");
			String b15 = mLayer.GetDataFieldNameByFieldName("现地类");
			String b16 = mLayer.GetDataFieldNameByFieldName("重点生态区域名称");
			String b17 = mLayer.GetDataFieldNameByFieldName("改变面积");
			String b18 = mLayer.GetDataFieldNameByFieldName("违规改变面积");
			String b19 = mLayer.GetDataFieldNameByFieldName("采伐蓄积");
			String b20 = mLayer.GetDataFieldNameByFieldName("违规采伐蓄积");
			String b21 = mLayer.GetDataFieldNameByFieldName("变化原因");
			String b22 = mLayer.GetDataFieldNameByFieldName("检查级别");
			String b23 = mLayer.GetDataFieldNameByFieldName("结果是否一致");
			String b24 = mLayer.GetDataFieldNameByFieldName("检查备注");
			String b25 = mLayer.GetDataFieldNameByFieldName("检查单位名称");
			String b26 = mLayer.GetDataFieldNameByFieldName("检查人员");
			String b27 = mLayer.GetDataFieldNameByFieldName("检查日期");

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
					Tools.ShowMessageBox("起始时间不能为空！");
					// Tools.ShowMessageBox("其实时间日期格式不对，请填写‘年年年年月月日日’八位日期格式");
					return "";
				}
				if (endText == null || endText.length() == 0) {
					Tools.ShowMessageBox("结束时间不能为空！");
					return "";
				}

				SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
				String jcrqDF = mLayer.GetDataFieldNameByFieldName("检查日期");
				if (jcrqDF.length() == 0) {
					Tools.ShowMessageBox("找不到日期筛选字段！");
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
						Tools.ShowMessageBox("起始日期不能大于结束日期");
						return "";
					} else {

						dateInSql.append(" and " + b27 + " in ('" + startText + "'");
						Calendar calendar = new GregorianCalendar();
						calendar.setTime(sDate);
						calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
						Date nextDate = calendar.getTime();

						while (nextDate.compareTo(eDate) <= 0) {
							dateInSql.append(",'" + SDF.format(nextDate) + "'");
							calendar.setTime(nextDate);
							calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
							nextDate = calendar.getTime();

						}
						dateInSql.append(")");

						sql += dateInSql.toString();
						sqlId += dateInSql.toString();
					}

				} catch (ParseException ex) {
					Tools.ShowMessageBox("起始或结束日期格式不对，请填写‘年年年年月月日日’八位日期格式");
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
				Tools.ShowMessageBox("获取验证图斑失败！" + ex.getMessage());

			} finally {
				reader2.Close();
			}

			return sys_ids;
		} catch (Exception ex) {
			Tools.ShowMessageBox("导出验证图斑失败" + ex.getMessage());
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
				Tools.ShowMessageBox("起始时间不能为空！");
				// Tools.ShowMessageBox("其实时间日期格式不对，请填写‘年年年年月月日日’八位日期格式");
				return "";
			}
			if (endText == null || endText.length() == 0) {
				Tools.ShowMessageBox("结束时间不能为空！");
				return "";
			}

			SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
			String jcrqDF = mLayer.GetDataFieldNameByFieldName("检查日期");
			if (jcrqDF.length() == 0) {
				Tools.ShowMessageBox("找不到日期筛选字段！");
				return "";
			}

			try {

				Date sDate = SDF.parse(startText);
				Date eDate = SDF.parse(endText);

				if (sDate.compareTo(eDate) == 0) {
					dateInSql.append(startText);
				} else if (sDate.compareTo(eDate) > 0) {
					Tools.ShowMessageBox("起始日期不能大于结束日期");
					return "";
				} else {

					dateInSql.append(startText);
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(sDate);
					calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
					Date nextDate = calendar.getTime();

					while (nextDate.compareTo(eDate) <= 0) {
						dateInSql.append(",'" + SDF.format(nextDate) + "'");
						calendar.setTime(nextDate);
						calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
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

		// String tbhDF = mLayer.GetDataFieldNameByFieldName("图斑号");
		String tbhDF = mLayer.GetDataFieldNameByFieldName("判读图斑编号");
		String xibanhao = mLayer.GetDataFieldNameByFieldName("核实细斑号");

		if (tbhDF.isEmpty()) {
			Tools.ShowMessageBox("请检查是否有“判读图斑编号“字段，否则无法导出！”");
			return;
		}

		String querySql = "select SYS_ID,"+xibanhao+"," + tbhDF + ",SYS_PHOTO from " + pDataset.getDataTableName()
				+ " where SYS_STATUS=0 ";
		SQLiteDataReader DR = pDataset.getDataSource().Query(querySql);

		String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/森林督查数据导出/"
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
					String pathName = exportPath + "/图斑" + DR.GetString(tbhDF);
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
							newFileName = exportPath+"/图斑" + DR.GetString(tbhDF) + "/图斑" + DR.GetString(tbhDF) + "_" + no + "_" + (i + 1) + ".jpg";
							// if(no ==1)
							// {
							// if (photos.length> 1) {
							// newFileName = exportPath + "/图斑" +
							// DR.GetString(tbhDF) + "_" + (i + 1) + ".jpg";
							//
							// } else {
							// newFileName = exportPath + "/图斑" +
							// DR.GetString(tbhDF) + ".jpg";
							// }
							// }
							// else
							// {
							// newFileName = exportPath + "/图斑" +
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

		Tools.ShowMessageBox("照片导出完成,请到" + exportPath + "目录下查看！");
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
				Tools.ShowMessageBox("没有选中任何图层！");
			} else {
				lkmap.Tools.Tools.OpenDialog("正在导出数据...", new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						// 导出数据路径
						String pathName = Tools.GetTextValueOnID(dialogView, R.id.et_LayerName);
						String ExportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
								+ "/数据导出/" + pathName;
						if (!Tools.ExistFile(ExportPath))
							(new File(ExportPath)).mkdirs();

						// 错误导出图层列表
						List<String> ExportErrorList = new ArrayList<String>();

						// 分类型导出
						String ExpType = Tools.GetSpinnerValueOnID(dialogView, R.id.sp_format);
						if (ExpType.equals("ArcGIS(shp)")) {
							for (HashMap<String, String> lyr : expDatasetNameList) {
								// 开始导出
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
									"以下图层数据导出失败！\r\n\r\n" + Tools.JoinT("\r\n", ExportErrorList));
						} else {
							Tools.ShowMessageBox(dialogView.getContext(), "数据成功导出！\r\n\r\n位于：【" + ExportPath + "】");
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
