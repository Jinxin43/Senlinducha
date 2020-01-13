package lkmap.ZRoadMap.DataExport;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
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
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class TuiGengDataExporter {

	private v1_FormTemplate dialogView = null;

	public TuiGengDataExporter() {
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.dataimport_tghl);
		dialogView.ReSetSize(0.5f, 0.9f);

		dialogView.SetCaption(Tools.ToLocale("���ݵ���"));

		v1_DataBind.SetBindListSpinner(dialogView, "������ʽ",
				new String[] { "ArcGIS(shp)", "AutoCad(dxf)", "Google(kml)" }, R.id.sp_format);

		dialogView.findViewById(R.id.bt_export).setOnClickListener(ExportEvent);
		dialogView.findViewById(R.id.bt_prepair).setOnClickListener(new ViewClick());
		dialogView.findViewById(R.id.bt_exporttable).setOnClickListener(new ViewClick());
		String pathName = Tools.GetSystemDate().replace("-", "").replace(":", "").replace(" ", "");
		String PrjPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/���ݵ���/" + pathName;

		Tools.SetTextViewValueOnID(dialogView, R.id.pn_projectname, pathName);

	}

	class ViewClick implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			String tag = arg0.getTag().toString();
			if (tag.equals("��������")) {
				List<HashMap<String, String>> expDatasetNameList = getSelectLayers();
				if (expDatasetNameList.size() == 0) {
					Tools.ShowMessageBox("û��ѡ���κ�ͼ�㣡");
					return;
				}

				if (btnCallback != null) {
					btnCallback.OnClick("��������", "");
				}
			}
			if (tag.equals("�������")) {
				ExportExcel();
			}
		}
	}

	private void ExportExcel() {
		for (v1_Layer layer : PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList()) {
			if (layer.GetLayerProjecType().equals("�˸�����")) {
				ExportOneLayer(layer);
			}
		}
	}

	private void ExportOneLayer(v1_Layer layer) {
		String querySql = "select ";
		String[] needField = { "��λ", "�ְ����", "С���", "ũ����", "С����", "С�����", "����ҵ���", "����", "��������", "�¶�", "����", "������",
				"����", "������", "Ӫ�췽ʽ", "��ֲ�ܶ�", "�콻����", "���ط�ʽ", "����ʱ��", "���ع��", "���ַ�ʽ", "����ʱ��", "��������", "����ʱ��", "������Ҫ��",
				"��ľ���", "�ù���", "Ͷ��Ԥ��", "������", "������" };

		try {
			if (!lkmap.Tools.Tools.ExistFile(PubVar.m_SysAbsolutePath + "/Data/�����ֵ�/")) {
				(new File(PubVar.m_SysAbsolutePath + "/Data/�˸�ͼ����/")).mkdirs();
			}

			WritableWorkbook book = Workbook.createWorkbook(
					new File(PubVar.m_SysAbsolutePath + "/Data/�˸�ͼ����/" + layer.GetLayerAliasName() + ".xls"));
			addSheet1(book, layer);
			addSheet2(book, layer);
			addSheet3(book, layer);
			book.write();
			book.close();
			Tools.ShowMessageBox("�˸�ͼ�����ѵ�����");

		} catch (Exception ex) {
			Tools.ShowMessageBox(ex.getMessage());
		}

	}

	private void addSheet3(WritableWorkbook book, v1_Layer layer) {
		try {
			WritableSheet sheet = book.createSheet("���ֱ�", 2);
			Label label1 = new Label(0, 0, "�˸����ֻ��ݹ���Ӫ���ְ������ֱ�", getHeaderCellStyle());
			sheet.addCell(label1);
			sheet.mergeCells(0, 0, 11, 0);

			String[] needField = { "��", "��", "С���", "�������", "����", "����", "���֣���\r\n�ݣ����", "����", "������\r\n����", "�����",
					"���߲�\r\n������", "���ֽ��" };

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

	private void addSheet2(WritableWorkbook book, v1_Layer layer) {
		try {
			WritableSheet sheet = book.createSheet("��ҵ��Ʊ�4-2", 1);
			Label label1 = new Label(0, 0, "�˸����ֻ��ݹ���Ӫ���ְ�����ҵ��Ʊ�", getHeaderCellStyle());
			sheet.addCell(label1);
			sheet.mergeCells(0, 0, 9, 0);

			Label label2 = new Label(0, 1, "������:");
			sheet.addCell(label2);
			Label label3 = new Label(1, 1, layer.getYear());
			sheet.addCell(label3);

			Label label4 = new Label(7, 1, "��λ��Ķ���ꡢ����", getBodyCellStyle());
			sheet.addCell(label4);
			sheet.mergeCells(7, 1, 9, 1);

			Label label5 = new Label(0, 2, "�磨��", getBodyCellStyle());
			sheet.addCell(label5);
			sheet.mergeCells(0, 2, 0, 3);

			Label label6 = new Label(1, 2, "�壨����", getBodyCellStyle());
			sheet.addCell(label6);
			sheet.mergeCells(1, 2, 1, 3);

			Label label7 = new Label(2, 2, "С���", getBodyCellStyle());
			sheet.addCell(label7);
			sheet.mergeCells(2, 2, 2, 3);

			Label label8 = new Label(3, 2, "����", getBodyCellStyle());
			sheet.addCell(label8);
			sheet.mergeCells(3, 2, 3, 3);

			Label label9 = new Label(4, 2, "����", getBodyCellStyle());
			sheet.addCell(label9);
			sheet.mergeCells(4, 2, 4, 3);

			Label label10 = new Label(5, 2, "���֣��ֲݣ����", getBodyCellStyle());
			sheet.addCell(label10);
			sheet.mergeCells(5, 2, 5, 3);

			Label label11 = new Label(6, 2, "����", getBodyCellStyle());
			sheet.addCell(label11);
			sheet.mergeCells(6, 2, 6, 3);

			Label label12 = new Label(7, 2, "�����ݣ���", getBodyCellStyle());
			sheet.addCell(label12);
			sheet.mergeCells(7, 2, 7, 3);

			Label label13 = new Label(8, 2, "������Ҫ��", getBodyCellStyle());
			sheet.addCell(label13);
			sheet.mergeCells(8, 2, 9, 2);

			Label label14 = new Label(8, 3, "��ľ", getBodyCellStyle());
			sheet.addCell(label14);

			Label label15 = new Label(9, 3, "����", getBodyCellStyle());
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

	private void addSheet1(WritableWorkbook book, v1_Layer layer) {
		try {
			WritableSheet sheet = book.createSheet("��ҵ��Ʊ�4-1", 0);

			Label label1 = new Label(0, 0, "������-1:�˸������ֻ��ݹ���Ӫ������ҵ���һ����", getHeaderCellStyle());
			sheet.addCell(label1);
			sheet.mergeCells(0, 0, 29, 0);

			Label label2 = new Label(0, 1, "������:");
			sheet.addCell(label2);

			Label label3 = new Label(1, 1, layer.getYear());
			sheet.addCell(label3);

			Label label4 = new Label(0, 2, "��λ", getBodyCellStyle());
			sheet.addCell(label4);
			sheet.mergeCells(0, 2, 0, 3);

			Label label5 = new Label(1, 2, "�ְ����", getBodyCellStyle());
			sheet.addCell(label5);
			sheet.mergeCells(1, 2, 1, 3);

			Label label6 = new Label(2, 2, "С���", getBodyCellStyle());
			sheet.addCell(label6);
			sheet.mergeCells(2, 2, 2, 3);

			Label label7 = new Label(3, 2, "ũ����", getBodyCellStyle());
			sheet.addCell(label7);
			sheet.mergeCells(3, 2, 3, 3);

			Label label8 = new Label(4, 2, "С����", getBodyCellStyle());
			sheet.addCell(label8);
			sheet.mergeCells(4, 2, 4, 3);

			Label label9 = new Label(5, 2, "С�����", getBodyCellStyle());
			sheet.addCell(label9);
			sheet.mergeCells(5, 2, 5, 3);
			Label labe40 = new Label(5, 4, "Ķ", getBodyCellStyle());
			sheet.addCell(labe40);

			Label label10 = new Label(6, 2, "����ҵ���", getBodyCellStyle());
			sheet.addCell(label10);
			sheet.mergeCells(6, 2, 6, 3);
			Label labe41 = new Label(6, 4, "Ķ", getBodyCellStyle());
			sheet.addCell(labe41);

			Label label11 = new Label(7, 2, "����", getBodyCellStyle());
			sheet.addCell(label11);
			sheet.mergeCells(7, 2, 7, 3);

			Label label12 = new Label(8, 2, "��������", getBodyCellStyle());
			sheet.addCell(label12);
			sheet.mergeCells(8, 2, 11, 2);

			Label label13 = new Label(8, 3, "��������", getBodyCellStyle());
			sheet.addCell(label13);

			Label label14 = new Label(9, 3, "�¶�", getBodyCellStyle());
			sheet.addCell(label14);

			Label label15 = new Label(10, 3, "����", getBodyCellStyle());
			sheet.addCell(label15);

			Label label16 = new Label(11, 3, "������", getBodyCellStyle());
			sheet.addCell(label16);

			Label label17 = new Label(12, 2, "Ӫ��ģʽ", getBodyCellStyle());
			sheet.addCell(label17);
			sheet.mergeCells(12, 2, 14, 2);

			Label label18 = new Label(12, 3, "����", getBodyCellStyle());
			sheet.addCell(label18);

			Label label19 = new Label(13, 3, "������", getBodyCellStyle());
			sheet.addCell(label19);

			Label label20 = new Label(14, 3, "Ӫ�췽ʽ", getBodyCellStyle());
			sheet.addCell(label20);

			Label label21 = new Label(15, 2, "�����ֲݴ�ʩ���", getBodyCellStyle());
			sheet.addCell(label21);
			sheet.mergeCells(15, 2, 21, 2);

			Label label22 = new Label(15, 3, "��ֲ�ܶ�", getBodyCellStyle());
			sheet.addCell(label22);
			Label labe42 = new Label(15, 4, "��/Ķ", getBodyCellStyle());
			sheet.addCell(labe42);

			Label label23 = new Label(16, 3, "�콻����", getBodyCellStyle());
			sheet.addCell(label23);

			Label label24 = new Label(17, 3, "���ط�ʽ", getBodyCellStyle());
			sheet.addCell(label24);

			Label label25 = new Label(18, 3, "����ʱ��", getBodyCellStyle());
			sheet.addCell(label25);

			Label label26 = new Label(19, 3, "���ع��", getBodyCellStyle());
			sheet.addCell(label26);

			Label label27 = new Label(20, 3, "���ַ�ʽ", getBodyCellStyle());
			sheet.addCell(label27);

			Label label28 = new Label(21, 3, "����ʱ��", getBodyCellStyle());
			sheet.addCell(label28);

			Label label29 = new Label(22, 2, "�������", getBodyCellStyle());
			sheet.addCell(label29);
			sheet.mergeCells(22, 2, 23, 2);

			Label label30 = new Label(22, 3, "����ʱ��", getBodyCellStyle());
			sheet.addCell(label30);

			Label label31 = new Label(23, 3, "��������", getBodyCellStyle());
			sheet.addCell(label31);
			Label labe43 = new Label(23, 4, "��", getBodyCellStyle());
			sheet.addCell(labe43);

			Label label32 = new Label(24, 2, "����", getBodyCellStyle());
			sheet.addCell(label32);
			sheet.mergeCells(24, 2, 25, 2);

			Label label33 = new Label(24, 3, "������", getBodyCellStyle());
			sheet.addCell(label33);
			Label labe44 = new Label(24, 4, "��", getBodyCellStyle());
			sheet.addCell(labe44);

			Label label34 = new Label(25, 3, "��ľ���", getBodyCellStyle());
			sheet.addCell(label34);
			Label labe45 = new Label(25, 4, "��", getBodyCellStyle());
			sheet.addCell(labe45);

			Label label35 = new Label(26, 2, "�ù���", getBodyCellStyle());
			sheet.addCell(label35);
			sheet.mergeCells(26, 2, 26, 3);
			Label labe46 = new Label(26, 4, "��", getBodyCellStyle());
			sheet.addCell(labe46);

			Label label36 = new Label(27, 2, "Ͷ��Ԥ��", getBodyCellStyle());
			sheet.addCell(label36);
			sheet.mergeCells(27, 2, 27, 3);
			Label labe47 = new Label(27, 4, "Ԫ", getBodyCellStyle());
			sheet.addCell(labe47);

			Label label37 = new Label(28, 2, "��������", getBodyCellStyle());
			sheet.addCell(label37);
			sheet.mergeCells(28, 2, 29, 2);

			Label label38 = new Label(28, 3, "������", getBodyCellStyle());
			sheet.addCell(label38);

			Label label39 = new Label(29, 3, "������", getBodyCellStyle());
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
		 * WritableFont.createFont("����")����������Ϊ���� 10�����������С
		 * WritableFont.BOLD:��������Ӵ֣�BOLD���Ӵ� NO_BOLD�����Ӵ֣� false�����÷�б��
		 * UnderlineStyle.NO_UNDERLINE��û���»���
		 */
		WritableFont font = new WritableFont(WritableFont.createFont("����"), 18, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE);

		WritableCellFormat headerFormat = new WritableCellFormat(NumberFormats.TEXT);
		try {
			// �����������
			headerFormat.setFont(font);
			// ���ñ�ͷ���߿���ʽ
			// ���������Ϊ���ߡ���ɫ
			headerFormat.setBorder(Border.ALL, BorderLineStyle.THICK, Colour.BLACK);
			// ��ͷ����ˮƽ������ʾ
			headerFormat.setAlignment(Alignment.CENTRE);
		} catch (WriteException e) {
			System.out.println("��ͷ��Ԫ����ʽ����ʧ�ܣ�");
		}
		return headerFormat;
	}

	public WritableCellFormat getTitleCellStyle() {

		/*
		 * WritableFont.createFont("����")����������Ϊ���� 10�����������С
		 * WritableFont.BOLD:��������Ӵ֣�BOLD���Ӵ� NO_BOLD�����Ӵ֣� false�����÷�б��
		 * UnderlineStyle.NO_UNDERLINE��û���»���
		 */
		WritableFont font = new WritableFont(WritableFont.createFont("����"), 10, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE);

		WritableCellFormat headerFormat = new WritableCellFormat(NumberFormats.TEXT);
		try {
			// �����������
			headerFormat.setFont(font);
			// ���ñ�ͷ���߿���ʽ
			// ���������Ϊ���ߡ���ɫ
			headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			// ��ͷ����ˮƽ������ʾ
			headerFormat.setAlignment(Alignment.CENTRE);
			headerFormat.setWrap(true);

		} catch (WriteException e) {
			System.out.println("���ݵ�Ԫ����ʽ����ʧ�ܣ�");
		}
		return headerFormat;
	}

	public WritableCellFormat getBodyCellStyle() {

		/*
		 * WritableFont.createFont("����")����������Ϊ���� 10�����������С
		 * WritableFont.BOLD:��������Ӵ֣�BOLD���Ӵ� NO_BOLD�����Ӵ֣� false�����÷�б��
		 * UnderlineStyle.NO_UNDERLINE��û���»���
		 */
		WritableFont font = new WritableFont(WritableFont.createFont("����"), 9, WritableFont.NO_BOLD, false,
				UnderlineStyle.NO_UNDERLINE);

		WritableCellFormat headerFormat = new WritableCellFormat(NumberFormats.TEXT);
		try {
			// �����������
			headerFormat.setFont(font);
			// ���ñ�ͷ���߿���ʽ
			// ���������Ϊ���ߡ���ɫ
			headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			// ��ͷ����ˮƽ������ʾ
			headerFormat.setAlignment(Alignment.CENTRE);
		} catch (WriteException e) {
			System.out.println("���ݵ�Ԫ����ʽ����ʧ�ܣ�");
		}
		return headerFormat;
	}

	private ArrayList<ArrayList<String>> getSheet3Value(v1_Layer layer) {
		ArrayList<HashMap<String, String>> fieldList = new ArrayList<HashMap<String, String>>();
		String[] needField = { "����", "���ƴ�", "С���", "_����", "_����", "_���", "_����", "_����", "D����", "D_�������", "D_�����",
				"D_���߲���", "D_���ֽ��", "D_�Ƿ����" };

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

		String xiaobanField = layer.GetDataFieldNameByFieldName("С���");
		querySql += " from " + layer.GetLayerID() + "_D where SYS_STATUS=0 ";
		if (!xiaobanField.isEmpty()) {
			// TODO:����Ӧ�ü��ϴ�
			querySql += "order by " + xiaobanField;
		}

		SQLiteDataReader DR = PubVar.m_Workspace.GetDatasetById(layer.GetLayerID()).getDataSource().Query(querySql);
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		while (DR.Read()) {
			String xiangzhen = DR.GetString(layer.GetDataFieldNameByFieldName("����"));
			String cun = DR.GetString(layer.GetDataFieldNameByFieldName("���ƴ�"));
			String xiaobanhao = DR.GetString(layer.GetDataFieldNameByFieldName("С���"));
			String humings = DR.GetString(layer.GetDataFieldNameByFieldName("_����"));
			String dileis = DR.GetString(layer.GetDataFieldNameByFieldName("_����"));
			String mianjis = DR.GetString(layer.GetDataFieldNameByFieldName("_���"));
			String linzhongs = DR.GetString(layer.GetDataFieldNameByFieldName("_����"));
			String shuzhongs = DR.GetString(layer.GetDataFieldNameByFieldName("_����"));

			String Dhumings = DR.GetString(layer.GetDataFieldNameByFieldName("D����"));
			String Dniandus = DR.GetString(layer.GetDataFieldNameByFieldName("D_�������"));
			String Dzhongmiaofeis = DR.GetString(layer.GetDataFieldNameByFieldName("D_�����"));
			String Dzcbz = DR.GetString(layer.GetDataFieldNameByFieldName("D_���߲���"));
			String Djine = DR.GetString(layer.GetDataFieldNameByFieldName("D_���ֽ��"));

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
		String[] needField = { "����", "���ƴ�", "С���", "_����", "_����", "_���", "_����", "_����", "_��ľ", "_����" };
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

			if (name.equals("_���") || name.equals("_��ľ") || name.equals("_����")) {
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

		String xiaobanField = layer.GetDataFieldNameByFieldName("С���");
		querySql += " from " + layer.GetLayerID() + "_D where SYS_STATUS=0 ";
		if (!xiaobanField.isEmpty()) {
			// TODO:����Ӧ�ü��ϴ�
			querySql += "order by " + xiaobanField;
		}

		SQLiteDataReader DR = PubVar.m_Workspace.GetDatasetById(layer.GetLayerID()).getDataSource().Query(querySql);

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		while (DR.Read()) {

			String xiangzhen = DR.GetString(layer.GetDataFieldNameByFieldName("����"));
			String cun = DR.GetString(layer.GetDataFieldNameByFieldName("���ƴ�"));
			String xiaobanhao = DR.GetString(layer.GetDataFieldNameByFieldName("С���"));
			String humings = DR.GetString(layer.GetDataFieldNameByFieldName("_����"));
			String dileis = DR.GetString(layer.GetDataFieldNameByFieldName("_����"));
			String mianjis = DR.GetString(layer.GetDataFieldNameByFieldName("_���"));
			String linzhongs = DR.GetString(layer.GetDataFieldNameByFieldName("_����"));
			String shuzhongs = DR.GetString(layer.GetDataFieldNameByFieldName("_����"));
			String miaomus = DR.GetString(layer.GetDataFieldNameByFieldName("_��ľ"));
			String zhongzis = DR.GetString(layer.GetDataFieldNameByFieldName("_����"));
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
		String[] needField = { "����", "���ƴ�", "С���", "_����", "С����", "С�����", "����ҵ���", "����", "��������", "�¶�", "����", "������",
				"��������", "��������", "���ַ�ʽ", "��ֲ�ܶ�", "�콻��", "���ط�ʽ", "����ʱ��", "���ع��", "���ַ�ʽ", "����ʱ��", "��������", "����ʱ��", "������Ҫ��",
				"��ľ���", "�ù����ϼ�", "Ͷ��Ԥ��", "������", "������" };
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

			if (name.equals("С�����") || name.equals("����ҵ���") || name.equals("Ͷ��Ԥ��") || name.equals("�ù����ϼ�")) {
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

		String xianzhenField = layer.GetDataFieldNameByFieldName("����");
		querySql += " from " + layer.GetLayerID() + "_D where SYS_STATUS=0 ";
		if (!xianzhenField.isEmpty()) {
			// TODO:����Ӧ�ü��ϴ�
			querySql += "order by " + xianzhenField;
		}
		SQLiteDataReader DR = PubVar.m_Workspace.GetDatasetById(layer.GetLayerID()).getDataSource().Query(querySql);

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		while (DR.Read()) {
			ArrayList<String> item = new ArrayList<String>();
			for (HashMap<String, String> hm : fieldList) {
				if (hm.get("FieldName").equals("_����")) {
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

	private List<HashMap<String, String>> getSelectLayers() {
		List<HashMap<String, String>> expDatasetNameList = new ArrayList<HashMap<String, String>>();
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
		return expDatasetNameList;
	}

	// ���ݵ���
	private View.OnClickListener ExportEvent = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			// ��ͼ�㵼�����ݣ���ʽ��LayerID,LayerName
			final List<HashMap<String, String>> expDatasetNameList = getSelectLayers();

			if (expDatasetNameList.size() <= 0) {
				Tools.ShowMessageBox("û��ѡ���κ�ͼ�㣡");
			} else {
				lkmap.Tools.Tools.OpenDialog("���ڵ�������...", new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						// ��������·��
						String pathName = Tools.GetTextValueOnID(dialogView, R.id.pn_projectname);
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
									"����ͼ�����ݵ���ʧ�ܣ�\r\n\r\n" + Tools.JoinT("\r\n", ExportErrorList));
						} else {
							Tools.ShowMessageBox(dialogView.getContext(), "���ݳɹ�������\r\n\r\nλ�ڣ���" + ExportPath + "��");
						}
					}
				});
			}

		}
	};

	private ICallback btnCallback = new ICallback() {

		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("��������")) {
				ResortXiaoban();
			}
		}
	};

	private ProgressDialog process = null;

	private void ResortXiaoban() {
		List<HashMap<String, String>> expDatasetNameList = getSelectLayers();
		for (HashMap<String, String> lyr : expDatasetNameList) {
			// ��ʼ����
			final Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.get("LayerID"));
			final Object[] geometryList = pDataset.QueryGeometryFromDB1(null).toArray();
			Arrays.sort(geometryList, new XiaoBanComprator());
			final String layerID = lyr.get("LayerID");

			try {
				final ProgressDialog process = new ProgressDialog(dialogView.getContext(),
						ProgressDialog.THEME_HOLO_LIGHT);
				process.setTitle("����С����Ϣ��" + lyr.get("LayerName"));
				process.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

				process.setIcon(R.drawable.v1_messageinfo);
				process.setCancelable(true);
				process.setCanceledOnTouchOutside(true);

				process.setMax(geometryList.length);

				process.show();
				process.onStart();

				new Thread(new Runnable() {
					@Override
					public void run() {
						v1_Layer layer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID);
						String xbhField = layer.GetDataFieldNameByFieldName("С���");

						for (int i = 1; i <= geometryList.length; i++) {
							if (xbhField.length() > 0) {
								pDataset.UpdateXiaoBanHao(((Geometry) geometryList[i - 1]).getSysId(), xbhField,
										i + "");
							}

							process.setProgress(i);

						}

						process.cancel();

						// try
						// {
						// Thread.sleep(2000);
						// }
						// catch(Exception e)
						// {
						//
						// }
					}
				}).start();

			} catch (Exception e) {
				Tools.ShowMessageBox(e.getMessage());
			}

		}

		// Tools.ShowMessageBox("С����Ϣ������ϣ�");
	}

	class XiaoBanComprator implements Comparator<Object> {
		@Override
		public int compare(Object arg0, Object arg1) {
			Coordinate t1 = ((Geometry) arg0).getCenterPoint();
			Coordinate t2 = ((Geometry) arg1).getCenterPoint();
			if (t1.getX() != t2.getX())
				return t1.getX() > t2.getX() ? 1 : -1;
			else
				return t1.getY() > t2.getY() ? 1 : -1;
		}
	}

	public void ShowDialog() {
		// �˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
		dialogView.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				LoadLayerInfo();
			}
		});
		dialogView.show();
	}

	private List<HashMap<String, Object>> m_HeaderListViewDataItemList = null;

	private void LoadLayerInfo() {

		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		hvf.SetHeaderListView(dialogView.findViewById(R.id.in_result1), "����ͼ���б�");

		this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String, Object>>();
		for (v1_Layer lyr : PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList()) {
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.GetLayerID());
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("LayerID", lyr.GetLayerID());
			hm.put("LayerName", lyr.GetLayerAliasName());
			hm.put("D1", false);
			hm.put("D2", lyr.GetLayerAliasName());
			hm.put("D3", Tools.ToLocale(lyr.GetLayerTypeName()));
			hm.put("D4", pDataset.GetAllObjectCount());
			this.m_HeaderListViewDataItemList.add(hm);
		}
		hvf.BindDataToListView(this.m_HeaderListViewDataItemList);

	}
}
