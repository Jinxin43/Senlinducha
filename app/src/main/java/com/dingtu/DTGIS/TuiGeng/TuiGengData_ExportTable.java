package com.dingtu.DTGIS.TuiGeng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

import com.dingtu.senlinducha.R;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.DataExport.DataExport_DXF;
import lkmap.ZRoadMap.DataExport.DataExport_KML;
import lkmap.ZRoadMap.DataExport.DataExport_SHP;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class TuiGengData_ExportTable {
	private v1_FormTemplate dialogView = null;
	private v1_Layer mLayer;

	public TuiGengData_ExportTable(v1_Layer layer) {
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.tuigengdata_exporttable);
		dialogView.ReSetSize(0.5f, 0.6f);

		dialogView.SetCallback(m_Callback);
		dialogView.SetCaption(Tools.ToLocale("退耕数据处理"));
		mLayer = layer;

		v1_DataBind.SetBindListSpinner(dialogView, "导出格式",
				new String[] { "ArcGIS(shp)", "AutoCad(dxf)", "Google(kml)" }, R.id.sp_format);

		dialogView.findViewById(R.id.bt_export).setOnClickListener(ExportEvent);

		dialogView.findViewById(R.id.bt_prepair).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.bt_exporttable).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.bt_photo).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.bt_addMark).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.bt_exportSJKP).setOnClickListener(new ViewClick());

		Tools.SetTextViewValueOnID(dialogView, R.id.pn_projectname, layer.GetLayerAliasName());
	}

	class ViewClick implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			String tag = arg0.getTag().toString();
			if (tag.equals("小班整理")) {
				if (btnCallback != null) {
					btnCallback.OnClick("整理数据", "");
				}
			}
			if (tag.equals("导出退耕表格")) {
				ExportOneLayer(mLayer);
			}

			if (tag.equals("导出照片")) {
				ExportPhoto();
			}

			if (tag.equals("增加水印")) {
				if (btnCallback != null) {
					btnCallback.OnClick("增加水印", "");
				}
			}

			if (tag.equals("生成小班设计卡片")) {
				if (btnCallback != null) {
					btnCallback.OnClick("生成小班设计卡片", "");
				}
			}
		}
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
						String pathName = Tools.GetTextValueOnID(dialogView, R.id.pn_projectname);
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
								if (!DE.Export(pDataset, ExportPath + "/" + lyr.get("LayerName"),false))
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

	private ICallback btnCallback = new ICallback() {

		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("整理数据")) {
				ResortXiaoban();
			}

			if (Str.equals("导出照片")) {
				ExportPhoto();
			}

			if (Str.equals("增加水印")) {
				addWaterMark();
			}

			if (Str.equals("生成小班设计卡片")) {
				exportDiaoChaKaPian();
			}
		}
	};

	private void exportDiaoChaKaPian() {

		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
		String allFields = "乡镇,建制村,自然村,小班号,小地名,地类,图幅号,小班面积,可作业面积,权属,横坐标,纵坐标,_户名,坡位,"
				+ "坡度,坡向,海拔,土壤名称,土层厚度,侵蚀程度,土壤肥力,土壤PH值,植被类型,植被盖度,植被高度,造林方式,"
				+ "造林树种,造林面积,造林林种,株行距,混交比,抚育年次,种苗需要量,整地方式,整地规格,用工量合计,用工量整地," + "用工量造林,用工量补植,用工量抚育,备注";

		// String allFields =
		// "乡镇,建制村,自然村,小班号,小地名,地类,图幅号,小班面积,可作业面积,权属,横坐标,纵坐标,_户名,坡位";

		String querySql = "select * from " + pDataset.getDataTableName() + " where SYS_STATUS=0";

		SQLiteDataReader DR = pDataset.getDataSource().Query(querySql);
		while (DR.Read()) {

			Map<String, String> map = new HashMap<String, String>();
			map.put("$zlsj$", "2017-11-25");
			String xiangzhen = DR.GetString(mLayer.GetDataFieldNameByFieldName("乡镇"));
			String jianzhicun = DR.GetString(mLayer.GetDataFieldNameByFieldName("建制村"));
			String xiaobanhao = DR.GetString(mLayer.GetDataFieldNameByFieldName("小班号"));

			for (String field : allFields.split(",")) {
				try {
					String dfield = mLayer.GetDataFieldNameByFieldName(field);
					if (dfield == null || dfield.isEmpty()) {
						map.put("$" + field + "$", "");
					} else {

						if (DR.GetString(dfield) == null) {
							map.put("$" + field + "$", "");
							Log.e(field, "null");
						} else {
							map.put("$" + field + "$", DR.GetString(dfield));
							Log.e(field, DR.GetString(dfield));
						}
					}
				} catch (Exception ex) {
					Log.e("exportDiaoChaKaPian", field + ":" + ex.getMessage());
				}

			}

			String srcFile = PubVar.m_SysAbsolutePath + "/SysFile/sjkpmb.doc";

			String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/退耕图层/小班设计卡片/"
					+ xiangzhen + "_" + jianzhicun + "/";
			if (!lkmap.Tools.Tools.ExistFile(exportPath)) {
				(new File(exportPath)).mkdirs();
			}

			String newFile = exportPath + xiangzhen + "_" + jianzhicun + "_" + xiaobanhao + ".doc";
			writeDoc(srcFile, newFile, map);

		}

		Tools.ShowMessageBox("小班调查卡片已生成到文件夹：\\" + "\\鼎图外业数据采集系统\\Data\\"
				+ PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectShortName() + "\\退耕图层\\小班设计卡片目录下！");

	}

	/**
	 * demoFile 模板文件 newFile 生成文件 map 要填充的数据
	 */
	public void writeDoc(String demoFile, String newFile, Map<String, String> map) {
		try {

			FileInputStream in = new FileInputStream(demoFile);
			HWPFDocument hdt = new HWPFDocument(in);
			// Fields fields = hdt.getFields();
			// 读取word文本内容
			Range range = hdt.getRange();
			// System.out.println(range.text());
			Log.e("range", range.text());

			// 替换文本内容
			for (Map.Entry<String, String> entry : map.entrySet()) {
				range.replaceText(entry.getKey(), entry.getValue());
			}
			Log.e("range", range.text());

			File dest = new File(newFile);
			if (dest.exists()) {
				dest.delete();
			}
			dest.createNewFile();

			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			FileOutputStream out = new FileOutputStream(dest);

			hdt.write(ostream);
			// 输出字节流
			out.write(ostream.toByteArray());

			out.close();
			ostream.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("writeDoc", e.getMessage());
			e.printStackTrace();

		}
	}

	String mPhotoPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/Photo/";

	private void ExportPhoto() {
		final Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());

		String xianField = mLayer.GetDataFieldNameByFieldName("区县");
		String xiangField = mLayer.GetDataFieldNameByFieldName("乡镇");
		String cunField = mLayer.GetDataFieldNameByFieldName("建制村");
		String xiaobanField = mLayer.GetDataFieldNameByFieldName("小班号");

		String querySql = "select SYS_ID," + xianField + "," + xiangField + "," + cunField + "," + xiaobanField
				+ ",SYS_PHOTO from " + pDataset.getDataTableName() + " where SYS_STATUS=0 ";
		SQLiteDataReader DR = pDataset.getDataSource().Query(querySql);

		String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/退耕图层/"
				+ mLayer.GetLayerAliasName() + "/";

		int count = DR.GetCount();
		while (DR.Read()) {
			String path = DR.GetString(xianField) + "_" + DR.GetString(xiangField) + "_" + DR.GetString(cunField) + "_"
					+ DR.GetString(xiaobanField);
			String photoPath = exportPath + path;
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
							newFileName = photoPath + "/" + path + "_" + (i + 1) + ".jpg";

						} else {
							newFileName = photoPath + "/" + path + ".jpg";
						}

						// CopyFile(mPhotoPath+photos[i],newFileName);
						CopyFile(photos[i], newFileName);

					}
				}
			}
		}

		Tools.ShowMessageBox("照片导出完成！");
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

	private void addWaterMark() {
		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());

		String xianField = mLayer.GetDataFieldNameByFieldName("区县");
		String xiangField = mLayer.GetDataFieldNameByFieldName("乡镇");
		String cunField = mLayer.GetDataFieldNameByFieldName("建制村");
		String xiaobanField = mLayer.GetDataFieldNameByFieldName("小班号");
		final List<String> fileNameList = new ArrayList<String>();

		String querySql = "select SYS_ID," + xianField + "," + xiangField + "," + cunField + "," + xiaobanField
				+ ",SYS_PHOTO from " + pDataset.getDataTableName() + " where SYS_STATUS=0 ";
		SQLiteDataReader DR = pDataset.getDataSource().Query(querySql);

		String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/退耕图层/"
				+ mLayer.GetLayerAliasName() + "/";

		while (DR.Read()) {
			String path = DR.GetString(xianField) + "_" + DR.GetString(xiangField) + "_" + DR.GetString(cunField) + "_"
					+ DR.GetString(xiaobanField);
			String photoPath = exportPath + path;
			String strPhotos = DR.GetString("SYS_PHOTO");
			if (strPhotos != null && strPhotos.length() > 0) {
				String[] photos = strPhotos.split(",");
				for (int i = 0; i < photos.length; i++) {
					String fileName;
					if (photos.length > 1) {
						fileName = photoPath + "/" + path + "_" + (i + 1) + ".jpg";

					} else {
						fileName = photoPath + "/" + path + ".jpg";
					}
					fileNameList.add(DR.GetString(xiaobanField) + "," + fileName);
				}
			}
		}

		final ProgressDialog process = new ProgressDialog(dialogView.getContext(), ProgressDialog.THEME_HOLO_LIGHT);
		process.setTitle("照片添加水印：" + mLayer.GetLayerAliasName());
		process.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		process.setIcon(R.drawable.v1_messageinfo);
		process.setCancelable(true);
		process.setCanceledOnTouchOutside(true);

		process.setMax(fileNameList.size());

		process.show();
		process.onStart();

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < fileNameList.size(); i++) {
					try {
						process.setProgress(i + 1);
						String[] files = fileNameList.get(i).split(",");
						if (files.length == 2) {
							BitmapFactory.Options options = new BitmapFactory.Options();
							String fileName = files[1];
							String xiaoban = files[0];
							options.inPurgeable = true;
							options.inMutable = true;
							options.inInputShareable = true;
							FileInputStream iSteam = new FileInputStream(fileName);
							Bitmap bitmap = BitmapFactory.decodeStream(iSteam, null, options);
							iSteam.close();
							iSteam = null;

							int w = bitmap.getWidth();
							int h = bitmap.getHeight();
							String strCamerTime = "小班号：" + xiaoban;
							Canvas canvasTemp = new Canvas(bitmap);

							Paint p = new Paint();
							String familyName = "宋体";
							Typeface font = Typeface.create(familyName, Typeface.BOLD);
							p.setColor(Color.RED);
							p.setTypeface(font);
							p.setTextSize(30);
							canvasTemp.drawText(strCamerTime, 8, h - 250, p);
							File f = new File(fileName);
							f.delete();
							f.createNewFile();
							FileOutputStream fos = new FileOutputStream(f);
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
							fos.flush();
							fos.close();
						}
					} catch (Exception ex) {
						Log.d("照片加水印", ex.getMessage());
					}
				}

				process.cancel();
			}
		}).start();

	}

	private ProgressDialog process = null;

	private void ResortXiaoban() {
		final Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
		final v1_Layer layer = mLayer.Clone();
		String xianField = mLayer.GetDataFieldNameByFieldName("区县");
		String xiangField = mLayer.GetDataFieldNameByFieldName("乡镇");
		String cunField = mLayer.GetDataFieldNameByFieldName("建制村");

		String querySql = "select SYS_ID," + xianField + "," + xiangField + "," + cunField + " from "
				+ pDataset.getDataTableName() + " where SYS_STATUS=0 ";

		SQLiteDataReader DR = pDataset.getDataSource().Query(querySql);

		int count = 0;
		String key;
		List<String> listTmp;
		HashMap<String, List<String>> allXiaoBan = new HashMap<String, List<String>>();
		while (DR.Read()) {
			try {
				key = DR.GetString(xianField) + "_" + DR.GetString(xiangField) + "_" + DR.GetString(cunField);
				listTmp = allXiaoBan.get(key);
				if (listTmp == null) {
					listTmp = new ArrayList<String>();
					allXiaoBan.put(key, listTmp);
				}
				listTmp.add(DR.GetInt32(0) + "");
				count++;
			} catch (Exception ex) {

			}

		}

		final ArrayList<Geometry[]> allGeos = new ArrayList<Geometry[]>();
		for (String strKey : allXiaoBan.keySet()) {
			ArrayList<Geometry> cunGeoList = pDataset.QueryGeometryFromDB1(allXiaoBan.get(strKey));
			Geometry[] contents = new Geometry[cunGeoList.size()];
			cunGeoList.toArray(contents);
			Arrays.sort(contents, new XiaoBanComprator());
			allGeos.add(contents);
		}

		try {
			final ProgressDialog process = new ProgressDialog(dialogView.getContext(), ProgressDialog.THEME_HOLO_LIGHT);
			process.setTitle("整理小班信息：" + mLayer.GetLayerAliasName());
			process.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			process.setIcon(R.drawable.v1_messageinfo);
			process.setCancelable(true);
			process.setCanceledOnTouchOutside(true);

			process.setMax(count);

			process.show();
			process.onStart();

			new Thread(new Runnable() {
				@Override
				public void run() {
					String xbhField = mLayer.GetDataFieldNameByFieldName("小班号");
					int j = 0;

					if (xbhField.length() > 0) {
						for (Geometry[] geos : allGeos) {
							for (int i = 1; i <= geos.length; i++) {
								j++;
								pDataset.UpdateXiaoBanHao(geos[i - 1].getSysId(), xbhField, i + "");
								process.setProgress(j);
							}
						}
						// 如果有标注，更新标注
						if (mLayer.GetIfLabel()) {
							pDataset.getBindGeoLayer().getRender().UpdateAllLabel();
						}
					}

					process.cancel();
				}
			}).start();

		} catch (Exception e) {
			Tools.ShowMessageBox(e.getMessage());
		}
	}

	class XiaoBanComprator implements Comparator<Object> {
		@Override
		public int compare(Object arg0, Object arg1) {
			Coordinate t1 = ((Geometry) arg0).getCenterPoint();
			Coordinate t2 = ((Geometry) arg1).getCenterPoint();
			if (t1.getY() != t2.getY())
				return t1.getY() > t2.getY() ? -1 : 1;
			else
				return t1.getX() > t2.getX() ? -1 : 1;
		}
	}

	private void ExportOneLayer(v1_Layer layer) {

		try {
			String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/退耕图层";
			if (!lkmap.Tools.Tools.ExistFile(exportPath)) {
				(new File(exportPath)).mkdirs();
			}

			int index = 0;
			WritableWorkbook book = Workbook
					.createWorkbook(new File(exportPath + "/" + layer.GetLayerAliasName() + ".xls"));
			CheckBox hasSheji = (CheckBox) dialogView.findViewById(R.id.cb_zysj);
			if (hasSheji.isChecked()) {
				addSheet1(book, layer, index);
				index++;
			}
			CheckBox hasFenhu = (CheckBox) dialogView.findViewById(R.id.cb_zysjfh);
			if (hasFenhu.isChecked()) {
				addSheet2(book, layer, index);
				index++;
			}

			CheckBox hasDuixian = (CheckBox) dialogView.findViewById(R.id.cb_dxqk);
			if (hasDuixian.isChecked()) {
				addSheet3(book, layer, index);
			}
			book.write();
			book.close();
			Tools.ShowMessageBox("退耕图层表格已导出！请到" + exportPath + "/" + layer.GetLayerAliasName() + ".xls 查看！");

		} catch (Exception ex) {
			Tools.ShowMessageBox(ex.getMessage());
		}

	}

	private void addSheet3(WritableWorkbook book, v1_Layer layer, int index) {
		try {
			WritableSheet sheet = book.createSheet("兑现表", index);
			Label label1 = new Label(0, 0, "退耕还林还草工程营造林按户兑现表", getHeaderCellStyle());
			sheet.addCell(label1);
			sheet.mergeCells(0, 0, 11, 0);

			String[] needField = { "乡", "村", "小班号", "兑现年度", "户名", "地类", "造林（种\r\n草）面积", "林种", "树（草\r\n）种", "种苗费",
					"政策补\r\n助兑现", "兑现金额" };

			for (int b = 0; b < needField.length; b++) {
				Label labelConte = new Label(b, 2, needField[b], getTitleCellStyle());
				try {
					sheet.addCell(labelConte);
				} catch (Exception ex) {
					Tools.ShowMessageBox(ex.getMessage());
				}
			}

			ArrayList<ArrayList<String>> result = getSheet3Value(layer);
			for (int j = 0; j < result.size(); j++) {
				ArrayList<String> row = result.get(j);
				for (int i = 0; i < row.size(); i++) {
					Label labelConte = new Label(i, 3 + j, row.get(i), getBodyCellStyle());
					try {
						sheet.addCell(labelConte);
					} catch (Exception ex) {
						Tools.ShowMessageBox(ex.getMessage());
					}

				}
			}
		} catch (Exception ex) {
			Tools.ShowMessageBox(ex.getMessage());
		}
	}

	private void addSheet2(WritableWorkbook book, v1_Layer layer, int index) {
		try {
			WritableSheet sheet = book.createSheet("作业设计表4-2", index);
			Label label1 = new Label(0, 0, "退耕还林还草工程营造林按户作业设计表", getHeaderCellStyle());
			sheet.addCell(label1);
			sheet.mergeCells(0, 0, 9, 0);

			Label label2 = new Label(0, 1, "设计年度:");
			sheet.addCell(label2);
			Label label3 = new Label(1, 1, layer.getYear());
			sheet.addCell(label3);

			Label label4 = new Label(7, 1, "单位：亩、株、公斤", getBodyCellStyle());
			sheet.addCell(label4);
			sheet.mergeCells(7, 1, 9, 1);

			Label label5 = new Label(0, 2, "乡（镇）", getBodyCellStyle());
			sheet.addCell(label5);
			sheet.mergeCells(0, 2, 0, 3);

			Label label6 = new Label(1, 2, "村（场）", getBodyCellStyle());
			sheet.addCell(label6);
			sheet.mergeCells(1, 2, 1, 3);

			Label label7 = new Label(2, 2, "小班号", getBodyCellStyle());
			sheet.addCell(label7);
			sheet.mergeCells(2, 2, 2, 3);

			Label label8 = new Label(3, 2, "户名", getBodyCellStyle());
			sheet.addCell(label8);
			sheet.mergeCells(3, 2, 3, 3);

			Label label9 = new Label(4, 2, "地类", getBodyCellStyle());
			sheet.addCell(label9);
			sheet.mergeCells(4, 2, 4, 3);

			Label label10 = new Label(5, 2, "造林（种草）面积", getBodyCellStyle());
			sheet.addCell(label10);
			sheet.mergeCells(5, 2, 5, 3);

			Label label11 = new Label(6, 2, "林种", getBodyCellStyle());
			sheet.addCell(label11);
			sheet.mergeCells(6, 2, 6, 3);

			Label label12 = new Label(7, 2, "树（草）种", getBodyCellStyle());
			sheet.addCell(label12);
			sheet.mergeCells(7, 2, 7, 3);

			Label label13 = new Label(8, 2, "种苗需要量", getBodyCellStyle());
			sheet.addCell(label13);
			sheet.mergeCells(8, 2, 9, 2);

			Label label14 = new Label(8, 3, "苗木", getBodyCellStyle());
			sheet.addCell(label14);

			Label label15 = new Label(9, 3, "种子", getBodyCellStyle());
			sheet.addCell(label15);

			ArrayList<ArrayList<String>> result = getSheet2Value(layer);
			for (int j = 0; j < result.size(); j++) {
				ArrayList<String> row = result.get(j);
				for (int i = 0; i < row.size(); i++) {
					Label labelConte = new Label(i, 4 + j, row.get(i), getBodyCellStyle());
					try {
						sheet.addCell(labelConte);
					} catch (Exception ex) {
						Tools.ShowMessageBox(ex.getMessage());
					}

				}
			}

		} catch (Exception e) {
			Tools.ShowMessageBox(e.getMessage());
		}
	}

	private void addSheet1(WritableWorkbook book, v1_Layer layer, int index) {
		try {
			WritableSheet sheet = book.createSheet("作业设计表4-1", index);

			Label label1 = new Label(0, 0, "附表四-1:退耕耕还林还草工程营造林作业设计一览表", getHeaderCellStyle());
			sheet.addCell(label1);
			sheet.mergeCells(0, 0, 29, 0);

			Label label2 = new Label(0, 1, "设计年度:");
			sheet.addCell(label2);

			Label label3 = new Label(1, 1, layer.getYear());
			sheet.addCell(label3);

			Label label4 = new Label(0, 2, "单位", getBodyCellStyle());
			sheet.addCell(label4);
			sheet.mergeCells(0, 2, 0, 3);

			Label label5 = new Label(1, 2, "林班或组", getBodyCellStyle());
			sheet.addCell(label5);
			sheet.mergeCells(1, 2, 1, 3);

			Label label6 = new Label(2, 2, "小班号", getBodyCellStyle());
			sheet.addCell(label6);
			sheet.mergeCells(2, 2, 2, 3);

			Label label7 = new Label(3, 2, "农户数", getBodyCellStyle());
			sheet.addCell(label7);
			sheet.mergeCells(3, 2, 3, 3);

			Label label8 = new Label(4, 2, "小地名", getBodyCellStyle());
			sheet.addCell(label8);
			sheet.mergeCells(4, 2, 4, 3);

			Label label9 = new Label(5, 2, "小班面积", getBodyCellStyle());
			sheet.addCell(label9);
			sheet.mergeCells(5, 2, 5, 3);
			Label labe40 = new Label(5, 4, "亩", getBodyCellStyle());
			sheet.addCell(labe40);

			Label label10 = new Label(6, 2, "可作业面积", getBodyCellStyle());
			sheet.addCell(label10);
			sheet.mergeCells(6, 2, 6, 3);
			Label labe41 = new Label(6, 4, "亩", getBodyCellStyle());
			sheet.addCell(labe41);

			Label label11 = new Label(7, 2, "地类", getBodyCellStyle());
			sheet.addCell(label11);
			sheet.mergeCells(7, 2, 7, 3);

			Label label12 = new Label(8, 2, "立地条件", getBodyCellStyle());
			sheet.addCell(label12);
			sheet.mergeCells(8, 2, 11, 2);

			Label label13 = new Label(8, 3, "立地类型", getBodyCellStyle());
			sheet.addCell(label13);

			Label label14 = new Label(9, 3, "坡度", getBodyCellStyle());
			sheet.addCell(label14);

			Label label15 = new Label(10, 3, "坡向", getBodyCellStyle());
			sheet.addCell(label15);

			Label label16 = new Label(11, 3, "土层厚度", getBodyCellStyle());
			sheet.addCell(label16);

			Label label17 = new Label(12, 2, "营造模式", getBodyCellStyle());
			sheet.addCell(label17);
			sheet.mergeCells(12, 2, 14, 2);

			Label label18 = new Label(12, 3, "林种", getBodyCellStyle());
			sheet.addCell(label18);

			Label label19 = new Label(13, 3, "树草种", getBodyCellStyle());
			sheet.addCell(label19);

			Label label20 = new Label(14, 3, "营造方式", getBodyCellStyle());
			sheet.addCell(label20);

			Label label21 = new Label(15, 2, "造林种草措施设计", getBodyCellStyle());
			sheet.addCell(label21);
			sheet.mergeCells(15, 2, 21, 2);

			Label label22 = new Label(15, 3, "初植密度", getBodyCellStyle());
			sheet.addCell(label22);
			Label labe42 = new Label(15, 4, "株/亩", getBodyCellStyle());
			sheet.addCell(labe42);

			Label label23 = new Label(16, 3, "混交比例", getBodyCellStyle());
			sheet.addCell(label23);

			Label label24 = new Label(17, 3, "整地方式", getBodyCellStyle());
			sheet.addCell(label24);

			Label label25 = new Label(18, 3, "整地时间", getBodyCellStyle());
			sheet.addCell(label25);

			Label label26 = new Label(19, 3, "整地规格", getBodyCellStyle());
			sheet.addCell(label26);

			Label label27 = new Label(20, 3, "造林方式", getBodyCellStyle());
			sheet.addCell(label27);

			Label label28 = new Label(21, 3, "造林时间", getBodyCellStyle());
			sheet.addCell(label28);

			Label label29 = new Label(22, 2, "抚育设计", getBodyCellStyle());
			sheet.addCell(label29);
			sheet.mergeCells(22, 2, 23, 2);

			Label label30 = new Label(22, 3, "抚育时间", getBodyCellStyle());
			sheet.addCell(label30);

			Label label31 = new Label(23, 3, "抚育次数", getBodyCellStyle());
			sheet.addCell(label31);
			Label labe43 = new Label(23, 4, "次", getBodyCellStyle());
			sheet.addCell(labe43);

			Label label32 = new Label(24, 2, "种苗", getBodyCellStyle());
			sheet.addCell(label32);
			sheet.mergeCells(24, 2, 25, 2);

			Label label33 = new Label(24, 3, "需苗量", getBodyCellStyle());
			sheet.addCell(label33);
			Label labe44 = new Label(24, 4, "株", getBodyCellStyle());
			sheet.addCell(labe44);

			Label label34 = new Label(25, 3, "苗木规格", getBodyCellStyle());
			sheet.addCell(label34);
			Label labe45 = new Label(25, 4, "级", getBodyCellStyle());
			sheet.addCell(labe45);

			Label label35 = new Label(26, 2, "用工量", getBodyCellStyle());
			sheet.addCell(label35);
			sheet.mergeCells(26, 2, 26, 3);
			Label labe46 = new Label(26, 4, "个", getBodyCellStyle());
			sheet.addCell(labe46);

			Label label36 = new Label(27, 2, "投资预算", getBodyCellStyle());
			sheet.addCell(label36);
			sheet.mergeCells(27, 2, 27, 3);
			Label labe47 = new Label(27, 4, "元", getBodyCellStyle());
			sheet.addCell(labe47);

			Label label37 = new Label(28, 2, "公里坐标", getBodyCellStyle());
			sheet.addCell(label37);
			sheet.mergeCells(28, 2, 29, 2);

			Label label38 = new Label(28, 3, "横坐标", getBodyCellStyle());
			sheet.addCell(label38);

			Label label39 = new Label(29, 3, "纵坐标", getBodyCellStyle());
			sheet.addCell(label39);
			for (int i = 1; i < 31; i++) {
				Label labelIndex = new Label(i - 1, 5, i + "", getBodyCellStyle());
				sheet.addCell(labelIndex);
			}

			addSheet1Content(layer, sheet);

		} catch (Exception ex) {

		}
	}

	public void addSheet1Content(v1_Layer layer, WritableSheet wsSheet) {
		ArrayList<ArrayList<String>> result = getSheet1Value(layer);
		for (int j = 0; j < result.size(); j++) {
			ArrayList<String> row = result.get(j);
			for (int i = 0; i < row.size(); i++) {
				Label labelConte = new Label(i, 6 + j, row.get(i), getBodyCellStyle());
				try {
					wsSheet.addCell(labelConte);
				} catch (Exception ex) {

				}

			}
		}
	}

	public WritableCellFormat getHeaderCellStyle() {

		/*
		 * WritableFont.createFont("宋体")：设置字体为宋体 10：设置字体大小
		 * WritableFont.BOLD:设置字体加粗（BOLD：加粗 NO_BOLD：不加粗） false：设置非斜体
		 * UnderlineStyle.NO_UNDERLINE：没有下划线
		 */
		WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 18, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE);

		WritableCellFormat headerFormat = new WritableCellFormat(NumberFormats.TEXT);
		try {
			// 添加字体设置
			headerFormat.setFont(font);
			// 设置表头表格边框样式
			// 整个表格线为粗线、黑色
			headerFormat.setBorder(Border.ALL, BorderLineStyle.THICK, Colour.BLACK);
			// 表头内容水平居中显示
			headerFormat.setAlignment(Alignment.CENTRE);
		} catch (WriteException e) {
			System.out.println("表头单元格样式设置失败！");
		}
		return headerFormat;
	}

	public WritableCellFormat getTitleCellStyle() {

		/*
		 * WritableFont.createFont("宋体")：设置字体为宋体 10：设置字体大小
		 * WritableFont.BOLD:设置字体加粗（BOLD：加粗 NO_BOLD：不加粗） false：设置非斜体
		 * UnderlineStyle.NO_UNDERLINE：没有下划线
		 */
		WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE);

		WritableCellFormat headerFormat = new WritableCellFormat(NumberFormats.TEXT);
		try {
			// 添加字体设置
			headerFormat.setFont(font);
			// 设置表头表格边框样式
			// 整个表格线为粗线、黑色
			headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			// 表头内容水平居中显示
			headerFormat.setAlignment(Alignment.CENTRE);
			headerFormat.setWrap(true);

		} catch (WriteException e) {
			System.out.println("内容单元格样式设置失败！");
		}
		return headerFormat;
	}

	public WritableCellFormat getBodyCellStyle() {

		/*
		 * WritableFont.createFont("宋体")：设置字体为宋体 10：设置字体大小
		 * WritableFont.BOLD:设置字体加粗（BOLD：加粗 NO_BOLD：不加粗） false：设置非斜体
		 * UnderlineStyle.NO_UNDERLINE：没有下划线
		 */
		WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 9, WritableFont.NO_BOLD, false,
				UnderlineStyle.NO_UNDERLINE);

		WritableCellFormat headerFormat = new WritableCellFormat(NumberFormats.TEXT);
		try {
			// 添加字体设置
			headerFormat.setFont(font);
			// 设置表头表格边框样式
			// 整个表格线为粗线、黑色
			headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			// 表头内容水平居中显示
			headerFormat.setAlignment(Alignment.CENTRE);
		} catch (WriteException e) {
			System.out.println("内容单元格样式设置失败！");
		}
		return headerFormat;
	}

	private ArrayList<ArrayList<String>> getSheet3Value(v1_Layer layer) {
		ArrayList<HashMap<String, String>> fieldList = new ArrayList<HashMap<String, String>>();
		String[] needField = { "乡镇", "建制村", "小班号", "_户名", "_地类", "_面积", "_林种", "_树种", "D_户名", "D_兑现年度", "D_种苗费",
				"D_政策补助", "D_兑现金额", "D_是否兑现" };

		for (String name : needField) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("FieldName", name);
			v1_LayerField field = layer.GetDataFieldByFieldName(name);
			if (field != null) {
				hm.put("DataField", field.GetDataFieldName());
				hm.put("DataType", field.GetFieldTypeName());
			} else {
				hm.put("DataField", "");
				hm.put("DataType", "");
			}

			fieldList.add(hm);
		}

		String querySql = "select ";
		boolean isFirst = true;
		for (HashMap<String, String> hm : fieldList) {
			if (!hm.get("DataField").isEmpty()) {
				if (isFirst) {
					querySql += hm.get("DataField");
					isFirst = false;
				} else {
					querySql += "," + hm.get("DataField");
				}
			}
		}

		String xiaobanField = layer.GetDataFieldNameByFieldName("小班号");
		querySql += " from " + layer.GetLayerID() + "_D where SYS_STATUS=0 ";
		if (!xiaobanField.isEmpty()) {
			// TODO:排序应该加上村
			querySql += "order by " + xiaobanField;
		}

		SQLiteDataReader DR = PubVar.m_Workspace.GetDatasetById(layer.GetLayerID()).getDataSource().Query(querySql);
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		while (DR.Read()) {
			String xiangzhen = DR.GetString(layer.GetDataFieldNameByFieldName("乡镇"));
			String cun = DR.GetString(layer.GetDataFieldNameByFieldName("建制村"));
			String xiaobanhao = DR.GetString(layer.GetDataFieldNameByFieldName("小班号"));
			String humings = DR.GetString(layer.GetDataFieldNameByFieldName("_户名"));
			String dileis = DR.GetString(layer.GetDataFieldNameByFieldName("_地类"));
			String mianjis = DR.GetString(layer.GetDataFieldNameByFieldName("_面积"));
			String linzhongs = DR.GetString(layer.GetDataFieldNameByFieldName("_林种"));
			String shuzhongs = DR.GetString(layer.GetDataFieldNameByFieldName("_树种"));

			String Dhumings = "";
			try {
				Dhumings = DR.GetString(layer.GetDataFieldNameByFieldName("D_户名"));
			} catch (Exception ex) {
				// Dhumings=
				// DR.GetString(layer.GetDataFieldNameByFieldName("D户名"));
			}

			String Dniandus = DR.GetString(layer.GetDataFieldNameByFieldName("D_兑现年度"));
			String Dzhongmiaofeis = DR.GetString(layer.GetDataFieldNameByFieldName("D_种苗费"));
			String Dzcbz = DR.GetString(layer.GetDataFieldNameByFieldName("D_政策补助"));
			String Djine = DR.GetString(layer.GetDataFieldNameByFieldName("D_兑现金额"));

			if (humings != null && humings.length() > 0) {
				String[] arrHuMing = humings.split(",");
				String[] arrDiLei = dileis.split(",");
				String[] arrMianJi = mianjis.split(",");
				String[] arrLinZhong = linzhongs.split(",");
				String[] arrShuZhong = shuzhongs.split(",");

				if (Dhumings != null && Dhumings.length() > 0) {
					String[] arrDhuming = Dhumings.split(";");
					String[] arrDniandu = Dniandus.split(";");
					String[] arrDzmf = Dzhongmiaofeis.split(";");
					String[] arrDzcbz = Dzcbz.split(";");
					String[] arrDjine = Djine.split(";");
					for (int i = 0; i < arrDhuming.length; i++) {
						String dHumings = arrDhuming[i];
						for (int n = 1; n < 9; n++) {
							if (dHumings.startsWith(n + ".")) {
								String[] arrDfenhu = dHumings.replaceFirst(n + ".", "").split(",");
								for (int j = 0; j < arrDfenhu.length; j++) {
									String fhm = arrDfenhu[j];
									ArrayList<String> item = new ArrayList<String>();
									item.add(xiangzhen);
									item.add(cun);
									item.add(xiaobanhao);
									item.add(arrDniandu[i].replaceFirst(n + ".", "").split(",")[j]);
									item.add(fhm);

									boolean isfind = false;
									for (int k = 0; k < arrHuMing.length; k++) {
										if (arrHuMing[k].equals(fhm)) {
											item.add(arrDiLei[k]);
											item.add(arrMianJi[k]);
											item.add(arrLinZhong[k]);
											item.add(arrShuZhong[k]);
											isfind = true;
											continue;
										}
									}

									if (!isfind) {
										item.add("");
										item.add("");
										item.add("");
										item.add("");
									}

									item.add(arrDzmf[i].replaceFirst(n + ".", "").split(",")[j]);
									item.add(arrDzcbz[i].replaceFirst(n + ".", "").split(",")[j]);
									item.add(arrDjine[i].replaceFirst(n + ".", "").split(",")[j]);

									result.add(item);
								}

							}
						}

					}
				}
			}
		}

		return result;
	}

	private ArrayList<ArrayList<String>> getSheet2Value(v1_Layer layer) {
		ArrayList<HashMap<String, String>> fieldList = new ArrayList<HashMap<String, String>>();
		String[] needField = { "乡镇", "建制村", "小班号", "_户名", "_地类", "_面积", "_林种", "_树种", "_苗木", "_种子" };
		for (String name : needField) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("FieldName", name);
			v1_LayerField field = layer.GetDataFieldByFieldName(name);
			if (field != null) {
				hm.put("DataField", field.GetDataFieldName());
				hm.put("DataType", field.GetFieldTypeName());
			} else {
				hm.put("DataField", "");
				hm.put("DataType", "");
			}

			if (name.equals("_面积") || name.equals("_苗木") || name.equals("_种子")) {
				hm.put("isAdd", "true");
			} else {
				hm.put("isAdd", "false");
			}

			fieldList.add(hm);
		}

		String querySql = "select ";
		boolean isFirst = true;
		for (HashMap<String, String> hm : fieldList) {
			if (!hm.get("DataField").isEmpty()) {
				if (isFirst) {
					querySql += hm.get("DataField");
					isFirst = false;
				} else {
					querySql += "," + hm.get("DataField");
				}
			}
		}

		String xiaobanField = layer.GetDataFieldNameByFieldName("小班号");
		querySql += " from " + layer.GetLayerID() + "_D where SYS_STATUS=0 ";
		if (!xiaobanField.isEmpty()) {
			// TODO:排序应该加上村
			querySql += "order by " + xiaobanField;
		}

		SQLiteDataReader DR = PubVar.m_Workspace.GetDatasetById(layer.GetLayerID()).getDataSource().Query(querySql);

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		while (DR.Read()) {

			String xiangzhen = DR.GetString(layer.GetDataFieldNameByFieldName("乡镇"));
			String cun = DR.GetString(layer.GetDataFieldNameByFieldName("建制村"));
			String xiaobanhao = DR.GetString(layer.GetDataFieldNameByFieldName("小班号"));
			String humings = DR.GetString(layer.GetDataFieldNameByFieldName("_户名"));
			String dileis = DR.GetString(layer.GetDataFieldNameByFieldName("_地类"));
			String mianjis = DR.GetString(layer.GetDataFieldNameByFieldName("_面积"));
			String linzhongs = DR.GetString(layer.GetDataFieldNameByFieldName("_林种"));
			String shuzhongs = DR.GetString(layer.GetDataFieldNameByFieldName("_树种"));
			String miaomus = DR.GetString(layer.GetDataFieldNameByFieldName("_苗木"));
			String zhongzis = DR.GetString(layer.GetDataFieldNameByFieldName("_种子"));
			if (humings != null && humings.length() > 0) {
				String[] arrHuMing = humings.split(",");
				String[] arrDiLei = dileis.split(",");
				String[] arrMianJi = mianjis.split(",");
				String[] arrLinZhong = linzhongs.split(",");
				String[] arrShuZhong = shuzhongs.split(",");
				String[] arrMiaoMu = miaomus.split(",");
				String[] arrZhongzi = zhongzis.split(",");
				for (int i = 0; i < arrHuMing.length; i++) {
					ArrayList<String> item = new ArrayList<String>();
					item.add(xiangzhen);
					item.add(cun);
					item.add(xiaobanhao);
					item.add(arrHuMing[i]);
					item.add(arrDiLei[i]);
					item.add(arrMianJi[i]);
					item.add(arrLinZhong[i]);
					item.add(arrShuZhong[i]);
					item.add(arrMiaoMu[i]);
					item.add(arrZhongzi[i]);
					result.add(item);
				}
			}
		}

		return result;
	}

	private ArrayList<ArrayList<String>> getSheet1Value(v1_Layer layer) {
		ArrayList<HashMap<String, String>> fieldList = new ArrayList<HashMap<String, String>>();
		String[] needField = { "乡镇", "建制村", "小班号", "_户名", "小地名", "小班面积", "可作业面积", "地类", "立地类型", "坡度", "坡向", "土层厚度",
				"造林林种", "造林树种", "造林方式", "初植密度", "混交比", "整地方式", "整地时间", "整地规格", "造林方式", "造林时间", "抚育次数", "抚育时间", "种苗需要量",
				"苗木规格", "用工量合计", "投资预算", "横坐标", "纵坐标" };
		for (String name : needField) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("FieldName", name);
			v1_LayerField field = layer.GetDataFieldByFieldName(name);
			if (field != null) {
				hm.put("DataField", field.GetDataFieldName());
				hm.put("DataType", field.GetFieldTypeName());
			} else {
				hm.put("DataField", "");
				hm.put("DataType", "");
			}

			if (name.equals("小班面积") || name.equals("可作业面积") || name.equals("投资预算") || name.equals("用工量合计")) {
				hm.put("isAdd", "true");
			} else {
				hm.put("isAdd", "false");
			}

			fieldList.add(hm);
		}

		String querySql = "select ";
		boolean isFirst = true;
		for (HashMap<String, String> hm : fieldList) {
			if (!hm.get("DataField").isEmpty()) {
				if (isFirst) {
					querySql += hm.get("DataField");
					isFirst = false;
				} else {
					querySql += "," + hm.get("DataField");
				}
			}
		}

		String xianzhenField = layer.GetDataFieldNameByFieldName("乡镇");
		querySql += " from " + layer.GetLayerID() + "_D where SYS_STATUS=0 ";
		if (!xianzhenField.isEmpty()) {
			// TODO:排序应该加上村
			querySql += "order by " + xianzhenField;
		}
		SQLiteDataReader DR = PubVar.m_Workspace.GetDatasetById(layer.GetLayerID()).getDataSource().Query(querySql);

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		while (DR.Read()) {
			ArrayList<String> item = new ArrayList<String>();
			for (HashMap<String, String> hm : fieldList) {
				if (hm.get("FieldName").equals("_户名")) {
					if (hm.get("DataField").isEmpty()) {
						item.add("0");
					} else {
						String hushu = DR.GetString(hm.get("DataField"));
						if (hushu != null && hushu.length() > 0) {
							item.add(hushu.split(",").length + "");
						} else {
							item.add("0");
						}
					}
					continue;
				}

				if (hm.get("DataField").isEmpty()) {
					item.add("");
				} else {
					item.add(DR.GetString(hm.get("DataField")));
				}
			}
			result.add(item);
		}

		return result;
	}

	private ICallback m_Callback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("退出")) {
				PubVar.m_Map.Refresh();
			}
		}
	};

	public void ShowDialog() {
		// 此处这样做的目的是为了计算控件的尺寸
		dialogView.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {

			}
		});
		dialogView.show();
	}
}
