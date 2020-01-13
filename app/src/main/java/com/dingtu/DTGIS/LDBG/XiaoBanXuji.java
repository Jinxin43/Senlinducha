package com.dingtu.DTGIS.LDBG;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.Funtion.PhotoControl;
import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.DataBindOfKeyValue;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_Data_Template_DateTime;
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
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Edit.CopyFeature;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class XiaoBanXuji {

	private v1_Layer mLayer;
	private int mObjId = 0;
	private Activity mOwnActivity = null;
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private List<FieldView> _FieldNameViewList = new ArrayList<FieldView>();
	private v1_CGpsDataObject _BaseObject = null;
	private ViewPager viewPager;
	private PhotoControl mPhotoControl;
	private boolean fristPageOne = true;
	private boolean firstPageTwo = true;
	private int maxCode = 0;
	private String mShi;
	private String mXian;
	private String mXiang;
	private String mCun;
	private String mLinban;
	private String mXiaoBan;
	private String mX;
	private String mY;
	private View proertyView;
	private HashMap<String, List<HashMap<String, String>>> detailList = new HashMap<String, List<HashMap<String, String>>>();
	HashMap<String, HashMap<String, Object>> allSum = new HashMap<String, HashMap<String, Object>>();
	private List<Double> shuzhongXj = new ArrayList<Double>();

	private double sumG = 0;
	private double zongXJ = 0;

	public XiaoBanXuji(String layerID, int dataID) {
		mObjId = dataID;
		mLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID);
		mOwnActivity = (Activity) PubVar.m_DoEvent.m_Context;

		initBaseObject();

		Button btnQuit = (Button) mOwnActivity.findViewById(R.id.XiaoBanXuji_quit);
		btnQuit.setOnClickListener(new ViewClick());
		Button btnSave = (Button) mOwnActivity.findViewById(R.id.btn_xbxj_save);
		btnSave.setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.bt_xbxj_xbxx).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.bt_xbxj_jgcs).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.bt_xbxj_photo).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.tv_GetXiaoban).setOnClickListener(new ViewClick());
		initViewPager();

	}

	private void initViewPager() {
		proertyView = LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.xiaobanxuji_xiaoban, null);
		viewContainter.add(proertyView);
		viewContainter
				.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.xiaobanxuji_jiaoguiceshu, null));
		viewContainter
				.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.v1_data_template_photo, null));
		titleContainer.add("小班信息");
		titleContainer.add("角规测树");
		titleContainer.add("照片");

		viewPager = (ViewPager) mOwnActivity.findViewById(R.id.XBXJviewPager);

		viewPager.setAdapter(new PagerAdapter() {
			// viewpager中的组件数量
			@Override
			public int getCount() {
				return viewContainter.size();
			}

			// 滑动切换的时候销毁当前的组件
			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				((ViewPager) container).removeView(viewContainter.get(position));
			}

			// 每次滑动的时候生成的组件
			@Override
			public Object instantiateItem(ViewGroup container, int position) {

				((ViewPager) container).addView(viewContainter.get(position));

				if (position == 0) {
					if (fristPageOne) {
						CreateForm(mLayer);
						fristPageOne = false;
						calcXY();
					}
				}
				if (position == 1) {
					if (firstPageTwo) {
						loadJiaoguiceshuList();
						firstPageTwo = false;
					}

				}
				if (position == 2) {
					initPhotoPager();
				}

				return viewContainter.get(position);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getItemPosition(Object object) {
				return super.getItemPosition(object);
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return titleContainer.get(position);
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				changePageViewIndex(arg0);
				if (arg0 == 1) {
					getJgcsFromDB();
				}
				if (arg0 == 2) {
					if (mPhotoControl == null) {
						initPhotoPager();
					}

					// mBaseObject.RefreshViewValueToData();
					// String watermark="所属单位：";
					// if(mBaseObject.getWaterMarkValue() != null)
					// {
					// watermark+=mBaseObject.getWaterMarkValue();
					// }
					// mPhotoControl.SetXiaoBanInfo(coordX,coordY, watermark);
				}
			}
		});

	}

	private void changePageViewIndex(int position) {

		Button btnXBXX = (Button) mOwnActivity.findViewById(R.id.bt_xbxj_xbxx);
		Button btnJgcx = (Button) mOwnActivity.findViewById(R.id.bt_xbxj_jgcs);
		Button btnPhoto = (Button) mOwnActivity.findViewById(R.id.bt_xbxj_photo);

		if (position == 0) {
			btnJgcx.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnJgcx.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnPhoto.setTextColor(android.graphics.Color.BLACK);
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnXBXX.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.locator1).setVisibility(View.INVISIBLE);
			mOwnActivity.findViewById(R.id.locator2).setVisibility(View.VISIBLE);
		}
		if (position == 1) {
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnXBXX.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnPhoto.setTextColor(android.graphics.Color.BLACK);
			btnJgcx.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnJgcx.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.locator1).setVisibility(View.INVISIBLE);
			mOwnActivity.findViewById(R.id.locator2).setVisibility(View.INVISIBLE);
		}

		if (position == 2) {
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnXBXX.setTextColor(android.graphics.Color.BLACK);
			btnJgcx.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnJgcx.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnPhoto.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.locator1).setVisibility(View.VISIBLE);
			mOwnActivity.findViewById(R.id.locator2).setVisibility(View.INVISIBLE);
		}
	}

	private void loadJiaoguiceshuList() {
		// 新增字段，字段属性，删除字段按钮
		mOwnActivity.findViewById(R.id.pln_add).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.pln_edit).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.pln_delete).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.pln_export).setOnClickListener(new ViewClick());
		PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().CheckAndCreateJiaoguiceshuTable();

		getJgcsFromDB();
	}

	private String selectCode = "";
	private HashMap<String, Object> selectObj = null;
	// 按钮事件
	private ICallback jgcsCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			// 选中字段列表后的回调
			if (Str.equals("列表选项")) {
				selectObj = (HashMap<String, Object>) ExtraStr;
				selectCode = selectObj.get("D1").toString();
				SetButtonEnable(true);
			}

		}
	};

	// 设置按钮的状态
	private void SetButtonEnable(boolean enabled) {
		mOwnActivity.findViewById(R.id.pln_edit).setEnabled(enabled);
		mOwnActivity.findViewById(R.id.pln_delete).setEnabled(enabled);
		((TextView) mOwnActivity.findViewById(R.id.tv_edit)).setTextColor((enabled ? Color.BLACK : Color.GRAY));
		((TextView) mOwnActivity.findViewById(R.id.tv_delete)).setTextColor((enabled ? Color.BLACK : Color.GRAY));
	}

	private void initPhotoPager() {
		initBaseObject();
		List<String> mPhotoNameList = new ArrayList<String>();
		if (this._BaseObject.GetSYS_PHOTO() != null && this._BaseObject.GetSYS_PHOTO().length() > 0) {
			mPhotoNameList = Tools.StrArrayToList(this._BaseObject.GetSYS_PHOTO().split(","));
		}

		mPhotoControl = new PhotoControl(mObjId, mPhotoNameList, mLayer.GetShowWaterMark(), false,
				viewContainter.get(2));
		mPhotoControl.setCallback(new ICallback() {

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				// saveData();
			}
		});
	}

	public class ViewClick implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			if (Tag.equals("返回")) {
				HiddenView();
			}
			if (Tag.equals("保存")) {
				saveData();
			}
			if (Tag.equals("小班信息")) {
				viewPager.setCurrentItem(0);
			}
			if (Tag.equals("自动获取")) {
				autoGetXiaoban();
			}
			if (Tag.equals("角规测树")) {
				viewPager.setCurrentItem(1);
			}
			if (Tag.equals("照片")) {
				viewPager.setCurrentItem(2);
				_BaseObject.RefreshViewValueToData();
			}
			if (Tag.equals("增加")) {
				// saveData();

				if (getValueFromView(true)) {
					JiaoguiceshuEdit jiaoguiceshuEdit = new JiaoguiceshuEdit(mShi, mXian, mXiang, mCun, mLinban,
							mXiaoBan, mX, mY);
					jiaoguiceshuEdit.SetCallback(pCallback);
					jiaoguiceshuEdit.SetEditMode("New", null, null, null, null, maxCode + 1);
					jiaoguiceshuEdit.ShowDialog();
				}
			}

			if (Tag.equals("导出数据")) {
				exportXiaoBanXuji();
				return;
			}

			if (Tag.equals("修改")) {
				if (selectCode.isEmpty()) {
					Tools.ShowMessageBox("没有选中数据");
					return;
				}

				JiaoguiceshuEdit jiaoguiceshuEdit = new JiaoguiceshuEdit(mShi, mXian, mXiang, mCun, mLinban, mXiaoBan,
						mX, mY);
				jiaoguiceshuEdit.SetCallback(pCallback);
				jiaoguiceshuEdit.SetEditMode("edit", selectObj.get("D2").toString(), selectObj.get("D3").toString(),
						selectObj.get("D4").toString(), selectObj.get("D5").toString(),
						Integer.valueOf(selectObj.get("D1") + ""));
				jiaoguiceshuEdit.ShowDialog();
			}

			if (Tag.equals("删除字段")) {
				Tools.ShowYesNoMessage(mOwnActivity, Tools.ToLocale("是否删除序号") + "【" + selectCode + "】的角规测树记录？",
						new ICallback() {

							@Override
							public void OnClick(String Str, Object ExtraStr) {

								if (Str.endsWith("YES")) {
									String sql = "delete from T_Jiaoguiceshu where TreeID=" + selectCode + " and Shi='"
											+ mShi + "' and Xiang ='" + mXiang + "' and " + "Xian ='" + mXian
											+ "' and Linban = '" + mLinban + "' and XiaoBan=" + mXiaoBan + " and X='"
											+ mX + "' and Y='" + mY + "'";

									if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sql)) {
										getJgcsFromDB();
									} else {
										// TODO:ExcuteSql已经提示了
									}
								}
							}
						});

			}
		}
	}

	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			getJgcsFromDB();
		}
	};

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

	// 计算小班蓄积量并导出
	private void exportXiaoBanXuji() {
		HashMap<String, HashMap<String, HashMap<String, Object>>> allPoints = getXiaoBanXujiFromDB();
		if (allPoints.keySet().size() == 0) {
			Tools.ShowMessageBox("还没有采集蓄积信息！");
			return;
		}

		ExceportExcel(allPoints);
	}

	private void ExceportExcel(HashMap<String, HashMap<String, HashMap<String, Object>>> allPoint) {
		String exportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/小班蓄积量调查";
		if (!lkmap.Tools.Tools.ExistFile(exportPath)) {
			(new File(exportPath)).mkdirs();
		}

		int index = 0;
		try {
			String fileName = exportPath + "/" + mShi + "_" + mXian + "_" + mXiang + "_" + mCun + "_" + mLinban + "_"
					+ mXiaoBan + "_" + "小班蓄积调查卡片.xls";
			WritableWorkbook book = Workbook.createWorkbook(new File(fileName));
			WritableSheet sheet = book.createSheet("小班蓄积调查", index);

			Label label1 = new Label(0, 0, "角规绕测记录", getHeaderCellStyle());
			sheet.addCell(label1);
			sheet.mergeCells(0, 0, 3 + (allShuZhong.size() * 3), 0);
			Label label2 = new Label(0, 1, "观测点号", getBodyCellStyle());
			sheet.addCell(label2);
			sheet.mergeCells(0, 1, 0, 2);

			Label label3 = new Label(1, 1, "GPS坐标", getBodyCellStyle());
			sheet.addCell(label3);
			sheet.mergeCells(1, 1, 2, 1);

			int indexC = 0;
			for (String sz : allShuZhong) {

				sheet.addCell(new Label(3 + indexC * 3, 1, sz, getBodyCellStyle()));
				sheet.mergeCells(3 + indexC * 3, 1, 3 + indexC * 3 + 2, 1);

				sheet.addCell(new Label(3 + indexC * 3, 2, "平均胸径", getBodyCellStyle()));
				sheet.addCell(new Label(3 + indexC * 3 + 1, 2, "平均树高", getBodyCellStyle()));
				sheet.addCell(new Label(3 + indexC * 3 + 2, 2, "横断面积", getBodyCellStyle()));

				indexC++;

			}
			Label labelG = new Label(3 + indexC * 3, 1, "断面积和", getBodyCellStyle());
			sheet.addCell(labelG);
			sheet.mergeCells(3 + indexC * 3, 1, 3 + indexC * 3, 2);

			sheet.addCell(new Label(1, 2, "X坐标", getBodyCellStyle()));
			sheet.addCell(new Label(2, 2, "Y坐标", getBodyCellStyle()));

			int colSum = 4 + (allShuZhong.size() * 3);
			ArrayList<HashMap<String, String>> allValues = calcXuji(allPoint);
			int rowIndex = 3;
			for (HashMap<String, String> hm : allValues) {
				// sheet.addCell(new
				// Label(0,rowIndex,(rowIndex-2)+"",getBodyCellStyle()));
				for (int i = 0; i < colSum; i++) {
					sheet.addCell(new Label(i, rowIndex, hm.get((i + 1) + ""), getBodyCellStyle()));
				}
				rowIndex++;
			}

			// Calc Sum
			if (rowIndex < 6) {
				exportSum(6, sheet);
			} else {
				exportSum(rowIndex, sheet);
			}

			book.write();
			book.close();
			Tools.ShowMessageBox("导出完成！调查表路径：" + fileName);
		} catch (Exception ex) {
			Tools.ShowMessageBox(ex.getMessage());
		}

	}

	private void exportSum(int rowIndex, WritableSheet sheet) {
		try {
			rowIndex++;
			Label labelSum = new Label(0, rowIndex, "合计", getBodyCellStyle());
			sheet.addCell(labelSum);
			sheet.mergeCells(0, rowIndex, 2, rowIndex);
			Label labelAveraget = new Label(0, rowIndex + 1, "平均", getBodyCellStyle());
			sheet.addCell(labelAveraget);
			sheet.mergeCells(0, rowIndex + 1, 2, rowIndex);
			Label labelXuji = new Label(0, rowIndex + 2, "蓄积量", getBodyCellStyle());
			sheet.addCell(labelXuji);
			sheet.mergeCells(0, rowIndex + 2, 2, rowIndex);
			Label labelAverageXuji = new Label(0, rowIndex + 3, "每公顷蓄积", getBodyCellStyle());
			sheet.addCell(labelAverageXuji);
			sheet.mergeCells(0, rowIndex + 3, 2, rowIndex);
			int pointNum = 1;

			double mgqXuJi = 0d;
			int indexSZ = 0;
			for (String sz : allShuZhong) {
				HashMap<String, Object> szSum = allSum.get(sz);
				if (szSum != null) {
					Integer treeNum = (Integer) szSum.get("TreeNum");
					Double sumD = (Double) szSum.get("DSum");
					Double sumH = (Double) szSum.get("HSum");
					Double sumG = (Double) szSum.get("GSum");

					Label labelSD = new Label(3 + indexSZ * 3, rowIndex, Tools.ConvertToDigi(sumD, 2),
							getBodyCellStyle());
					sheet.addCell(labelSD);
					Label labelAD = new Label(3 + indexSZ * 3, rowIndex + 1, Tools.ConvertToDigi(sumD / treeNum, 2),
							getBodyCellStyle());
					sheet.addCell(labelAD);

					Label labelSH = new Label(4 + indexSZ * 3, rowIndex, Tools.ConvertToDigi(sumH, 2),
							getBodyCellStyle());
					sheet.addCell(labelSH);
					Label labelAH = new Label(4 + indexSZ * 3, rowIndex + 1, Tools.ConvertToDigi(sumH / treeNum, 2),
							getBodyCellStyle());
					sheet.addCell(labelAH);

					Label labelSG = new Label(5 + indexSZ * 3, rowIndex, Tools.ConvertToDigi(sumG, 2),
							getBodyCellStyle());
					sheet.addCell(labelSG);
					Label labelAG = new Label(5 + indexSZ * 3, rowIndex + 1, Tools.ConvertToDigi(sumG / treeNum, 2),
							getBodyCellStyle());
					sheet.addCell(labelAG);

					Double sumXuJi = (Double) szSum.get("XuJiSum");
					pointNum = (Integer) szSum.get("PointNum");

					Label labelXJ = new Label(3 + indexSZ * 3, rowIndex + 2, Tools.ConvertToDigi(sumXuJi, 2),
							getBodyCellStyle());
					sheet.addCell(labelXJ);
					sheet.mergeCells(3 + indexSZ * 3, rowIndex + 2, 3 + indexSZ * 3 + 3, rowIndex + 2);
					mgqXuJi += sumXuJi;

				}
				indexSZ++;
			}
			Label labelXJ = new Label(3, rowIndex + 3, Tools.ConvertToDigi(mgqXuJi / pointNum, 2), getBodyCellStyle());
			sheet.mergeCells(3, rowIndex + 3, 3 + (indexSZ - 1) * 3 + 3, rowIndex + 3);
			sheet.addCell(labelXJ);
		} catch (Exception ex) {
			Tools.ShowMessageBox("汇总蓄积量错误：" + ex.getMessage());
		}

	}

	private ArrayList<HashMap<String, String>> calcXuji(
			HashMap<String, HashMap<String, HashMap<String, Object>>> allPoint) {
		int pointIndex = 1;
		ArrayList<HashMap<String, String>> allValues = new ArrayList<HashMap<String, String>>();
		allSum.clear();

		for (String p : allPoint.keySet()) {
			HashMap<String, String> rowValue = new HashMap<String, String>();
			String[] xy = p.split(";");
			String X = xy[0];
			String Y = "";
			if (xy.length > 1) {
				Y = xy[1];
			}
			rowValue.put("1", pointIndex + "");
			rowValue.put("2", X);
			rowValue.put("3", Y);

			HashMap<String, HashMap<String, Object>> point1 = allPoint.get(p);

			int szIndex = 0;
			for (String sz : allShuZhong) {
				HashMap<String, Object> szSum = allSum.get(sz);
				if (szSum == null) {
					szSum = new HashMap<String, Object>();
					allSum.put(sz, szSum);

				}
				HashMap<String, Object> sz1 = point1.get(sz);
				if (sz1 != null) {
					// Integer treeNum = (Integer)sz1.get("TreeNum");
					if (szSum.get("TreeNum") == null) {
						szSum.put("TreeNum", 1);
					} else {
						szSum.put("TreeNum", 1 + (Integer) szSum.get("TreeNum"));
					}

					szSum.put("TreeNum", sz1.get("TreeNum"));

					Double sumH = (Double) sz1.get("HSum");
					if (szSum.get("HSum") == null) {
						szSum.put("HSum", sumH / (Integer) sz1.get("TreeNum"));
					} else {
						szSum.put("HSum", sumH + (Double) szSum.get("HSum") / (Integer) sz1.get("TreeNum"));
					}

					Double sumD = (Double) sz1.get("DSum");
					if (szSum.get("DSum") == null) {
						szSum.put("DSum", sumD / (Integer) sz1.get("TreeNum"));
					} else {
						szSum.put("DSum", sumD + (Double) szSum.get("DSum") / (Integer) sz1.get("TreeNum"));
					}

					Double sumG = (Double) sz1.get("GSum");
					if (szSum.get("GSum") == null) {
						szSum.put("GSum", sumG);
					} else {
						szSum.put("GSum", sumG + (Double) szSum.get("GSum"));
					}

					double pjH = ((Double) sz1.get("HSum")) / (Integer) sz1.get("TreeNum");
					double pjD = ((Double) sz1.get("DSum")) / (Integer) sz1.get("TreeNum");
					int mmXiongjing = (int) (Math.ceil(pjD) * 10);
					double dCaiji = calcCaijiFromTable(mShi, sz, mmXiongjing);
					String H = Tools.ConvertToDigi(pjH, 2);
					String D = Tools.ConvertToDigi(pjD, 2);
					Double szSumCaiji = ((Double) sz1.get("GSum")) * dCaiji;
					if (szSum.get("XuJiSum") == null) {
						szSum.put("XuJiSum", szSumCaiji);
					} else {
						szSum.put("XuJiSum", szSumCaiji + (Double) szSum.get("XuJiSum"));
					}
					szSum.put("PointNum", pointIndex);
					rowValue.put((3 + szIndex * 3 + 1) + "", D);
					rowValue.put((3 + szIndex * 3 + 2) + "", H);
					rowValue.put((3 + szIndex * 3 + 3) + "", Tools.ConvertToDigi(sz1.get("GSum") + "", 2));

				} else {
					rowValue.put((3 + szIndex * 3 + 1) + "", "");
					rowValue.put((3 + szIndex * 3 + 2) + "", "");
					rowValue.put((3 + szIndex * 3 + 3) + "", "");
				}

				szIndex++;
			}

			pointIndex++;
			allValues.add(rowValue);
		}

		return allValues;
	}

	private List<String> allShuZhong = new ArrayList<String>();

	private HashMap<String, HashMap<String, HashMap<String, Object>>> getXiaoBanXujiFromDB() {
		String sql = "select * from T_Jiaoguiceshu where Shi='" + mShi + "' and Xiang ='" + mXiang + "' and "
				+ "Xian ='" + mXian + "' and Cun='" + mCun + "' and Linban = '" + mLinban + "' and XiaoBan=" + mXiaoBan;
		SQLiteDataReader DR = PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().Query(sql);

		allShuZhong.clear();
		HashMap<String, HashMap<String, HashMap<String, Object>>> allPoint = new HashMap<String, HashMap<String, HashMap<String, Object>>>();
		while (DR.Read()) {
			String point = DR.GetString("X") + ";" + DR.GetString("Y");
			HashMap<String, HashMap<String, Object>> point1 = allPoint.get(point);
			if (point1 == null) {
				point1 = new HashMap<String, HashMap<String, Object>>();

			}

			HashMap<String, Object> shuzhong1 = point1.get(DR.GetString("ShuZhong"));
			if (!allShuZhong.contains(DR.GetString("ShuZhong"))) {
				allShuZhong.add(DR.GetString("ShuZhong"));
			}

			if (shuzhong1 == null) {
				shuzhong1 = new HashMap<String, Object>();
				shuzhong1.put("TreeNum", 1);
				shuzhong1.put("DSum", Double.parseDouble(DR.GetString("D")));
				shuzhong1.put("HSum", Double.parseDouble(DR.GetString("H")));
				shuzhong1.put("GSum", Double.parseDouble(DR.GetString("G")));
			} else {
				shuzhong1.put("TreeNum", (Integer) shuzhong1.get("TreeNum") + 1);
				shuzhong1.put("DSum", (Double) shuzhong1.get("DSum") + Double.parseDouble(DR.GetString("D")));
				shuzhong1.put("HSum", (Double) shuzhong1.get("HSum") + Double.parseDouble(DR.GetString("H")));
				shuzhong1.put("GSum", (Double) shuzhong1.get("HSum") + Double.parseDouble(DR.GetString("G")));
			}
			point1.put(DR.GetString("ShuZhong"), shuzhong1);
			allPoint.put(point, point1);
		}

		return allPoint;
	}

	private void getJgcsFromDB() {
		if (getValueFromView(false)) {
			String sql = "select * from T_Jiaoguiceshu where Shi='" + mShi + "' and Xiang ='" + mXiang + "' and "
					+ "Xian ='" + mXian + "' and Cun='" + mCun + "' and Linban = '" + mLinban + "' and XiaoBan="
					+ mXiaoBan + " and X='" + mX + "' and Y='" + mY + "'";

			SQLiteDataReader DR = PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().Query(sql);

			v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
			hvf.SetHeaderListView(mOwnActivity.findViewById(R.id.jgcs_listview), "角规测树表", jgcsCallback);
			detailList.clear();
			sumG = 0;
			zongXJ = 0;
			maxCode = 0;
			shuzhongXj.clear();
			List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
			while (DR.Read()) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				int treeID = DR.GetInt32("TreeID");
				if (treeID > maxCode) {
					maxCode = treeID;
				}
				hm.put("D1", treeID);
				hm.put("D2", DR.GetString("ShuZhong"));
				hm.put("D3", DR.GetString("D"));
				hm.put("D4", DR.GetDouble("H"));
				hm.put("D5", DR.GetString("G"));
				dataList.add(hm);
				try {
					sumG += Double.parseDouble(DR.GetString("G"));
				} catch (Exception ex) {

				}
				HashMap<String, String> tree = new HashMap<String, String>();
				tree.put("D", DR.GetString("D"));
				tree.put("H", DR.GetString("H"));
				List<HashMap<String, String>> szDetail = detailList.get(DR.GetString("ShuZhong"));
				if (szDetail == null) {
					szDetail = new ArrayList<HashMap<String, String>>();
				}
				szDetail.add(tree);

				detailList.put(DR.GetString("ShuZhong"), szDetail);

			}

			DR.Close();
			Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_sumG, sumG + "");
			calcXuji();
			hvf.BindDataToListView(dataList);

		}
	}

	private void calcXuji() {
		for (String sz : detailList.keySet()) {
			Double sumH = 0d;
			Double sumD = 0d;
			int Di = 0;
			int Hi = 0;
			List<HashMap<String, String>> szDetail = detailList.get(sz);
			for (HashMap<String, String> hm : szDetail) {
				try {
					sumH += Double.parseDouble(hm.get("H"));
					Hi++;
				} catch (Exception ex) {

				}

				try {
					sumD += Double.parseDouble(hm.get("D"));
					Di++;
				} catch (Exception ex) {

				}
			}

			int mmXiongijng = (int) (Math.ceil(sumD / Di) * 10);
			double dCaiji = calcCaijiFromTable(mShi, sz, mmXiongijng);
			zongXJ += dCaiji;
			shuzhongXj.add(dCaiji);
		}

		Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_HaXjl, Tools.ConvertToDigi(zongXJ));
	}

	private double calcCaijiFromTable(String shi, String sz, int xiongjing) {
		double dCaiji = 0;
		if (shi.length() < 3 && (!shi.endsWith("市"))) {
			shi += "市";
		}

		try {
			String pCaijishi = PubVar.m_DoEvent.m_ConfigDB.QueryCaijishiByShuZhongName(sz, shi);
			int caijishi = Integer.parseInt(pCaijishi);
			int Caiji = PubVar.m_DoEvent.m_ConfigDB.QuaryCaiji(xiongjing, caijishi);
			dCaiji = Double.parseDouble(Caiji + "") / 1000;
		} catch (Exception ex) {
			Tools.ShowMessageBox("从一元材积表计算蓄积失败：" + ex.getMessage());
		}

		return dCaiji;
	}

	private boolean getValueFromView(boolean isShowAlert) {
		View title = mOwnActivity.findViewById(R.id.ll_jgcstitle);
		mShi = Tools.GetTextValueOnID(mOwnActivity, R.id.et_Shi);
		mXian = Tools.GetTextValueOnID(mOwnActivity, R.id.et_jgcsxian);
		mXiang = Tools.GetTextValueOnID(mOwnActivity, R.id.et_jgcsxiang);
		mCun = Tools.GetTextValueOnID(mOwnActivity, R.id.et_jgcscun);
		mLinban = Tools.GetTextValueOnID(mOwnActivity, R.id.et_jgcsLinban);
		if (mLinban == null) {
			mLinban = "";
		}
		mXiaoBan = Tools.GetTextValueOnID(mOwnActivity, R.id.et_jgcsXiaoban);
		mX = Tools.GetTextValueOnID(mOwnActivity, R.id.et_jgcsX);
		mY = Tools.GetTextValueOnID(mOwnActivity, R.id.et_jgcsY);

		if (Tools.isEmpty(mShi)) {
			if (isShowAlert)
				Tools.ShowMessageBox("市必须填写");
			return false;
		}
		if (Tools.isEmpty(mXian)) {
			if (isShowAlert)
				Tools.ShowMessageBox("县(局)必须填写");
			return false;
		}

		if (Tools.isEmpty(mXiang)) {
			if (isShowAlert)
				Tools.ShowMessageBox("乡镇必须填写");
			return false;
		}

		if (Tools.isEmpty(mCun)) {
			if (isShowAlert)
				Tools.ShowMessageBox("村必须填写");
			return false;
		}

		if (Tools.isEmpty(mXiaoBan)) {
			if (isShowAlert)
				Tools.ShowMessageBox("小班号必须填写");
			return false;
		}

		if (Tools.isEmpty(mX)) {
			if (isShowAlert)
				Tools.ShowMessageBox("没有X坐标值");
			return false;
		}
		;

		if (Tools.isEmpty(mY)) {
			if (isShowAlert)
				Tools.ShowMessageBox("没有Y坐标值");
			return false;
		}
		;

		return true;
	}

	public void HiddenView() {
		try {
			mOwnActivity.findViewById(R.id.ll_xbxj).setVisibility(View.GONE);
		} catch (Exception ex) {

		}
	}

	private void saveData() {
		// 保存属性数据
		this._BaseObject.RefreshViewValueToData();
		if (mPhotoControl != null) {
			this._BaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
		}

		if (!this._BaseObject.SaveFeatureToDb()) {
			Tools.ShowMessageBox(this.mOwnActivity, "数据保存失败！");
			return;
		}
		{
			Tools.ShowToast(this.mOwnActivity, "数据已保存");
		}

		// HiddenView();
	}

	// 生成属性字段列表
	private void CreateForm(v1_Layer vLayer) {
		// 根据图层的字段配置信息，动态生成属性表单
		LinearLayout LL = (LinearLayout) proertyView.findViewById(R.id.baselist);
		LL.removeAllViews();
		if (vLayer == null)
			return;

		// 计算标签文本的最大长度，为对齐做准备
		int LabelMaxLen = 0;
		for (v1_LayerField LF : vLayer.GetFieldList()) {
			if (Tools.CalStrLength(Tools.ToLocale(LF.GetFieldName())) > LabelMaxLen)
				LabelMaxLen = Tools.CalStrLength(Tools.ToLocale(LF.GetFieldName()));
		}

		for (v1_LayerField LF : vLayer.GetFieldList()) {

			if (!LF.getIsSelect()) {
				continue;
			}

			// 创建标签
			LinearLayout SubLL = this.CreateFormRowHeader(LL, Tools.ToLocale(LF.GetFieldName()), LabelMaxLen);
			LayoutParams LPET;

			switch (LF.GetFieldType()) {
			case enString:

				if (LF.GetFieldName().equals("地市")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_Shi)));
					continue;
				}

				if (LF.GetFieldName().equals("县(局)")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_jgcsxian)));
					continue;
				}

				if (LF.GetFieldName().equals("乡镇(林场)")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_jgcsxiang)));
					continue;
				}

				if (LF.GetFieldName().equals("行政村(营林区)")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_jgcscun)));
					continue;
				}

				if (LF.GetFieldName().equals("小班")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_jgcsXiaoban)));
					continue;
				}

				if (LF.GetFieldName().equals("林班(村民小组)")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_jgcsLinban)));
					continue;
				}

				if (LF.GetFieldName().equals("X坐标")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_jgcsX)));
					continue;
				}

				if (LF.GetFieldName().equals("Y坐标")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_jgcsY)));
					continue;
				}

				if (LF.GetFieldEnumCode().equals("")) {
					EditText ET = new EditText(LL.getContext());
					// if(LF.GetFieldName().equals("村")||LF.GetFieldName().equals("乡")||LF.GetFieldName().equals("林业局")||LF.GetFieldName().equals("林场"))
					// {
					// ET.setEnabled(false);
					// }

					LPET = new LayoutParams(ET.getWidth(), ET.getHeight());
					LPET.width = LayoutParams.MATCH_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					ET.setLayoutParams(LPET);
					SubLL.addView(ET);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));

				} else {
					// v1_EditSpinnerDialog es = new
					// v1_EditSpinnerDialog(LL.getContext());
					// LayoutParams LPET = new
					// LayoutParams(es.getWidth(),es.getHeight());
					// LPET.width = LayoutParams.FILL_PARENT;
					// LPET.height = LayoutParams.WRAP_CONTENT;
					// es.setLayoutParams(LPET);
					// es.SetSelectItemList(LF.getFieldEnumList(vLayer.GetLayerProjecType(),
					// LF.GetFieldEnumCode()));
					// es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
					// SubLL.addView(es);
					// this._FieldNameViewList.add(new
					// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),es));

					Spinner sp = new Spinner(LL.getContext(), Spinner.MODE_DROPDOWN);
					LPET = new LayoutParams(sp.getWidth(), sp.getHeight());
					LPET.width = LayoutParams.FILL_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					sp.setLayoutParams(LPET);
					ArrayAdapter<String> dileiAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
							android.R.layout.simple_spinner_item,
							LF.GetFieldEnumList(vLayer.GetLayerProjecType(), LF.GetFieldEnumCode()));
					dileiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sp.setAdapter(dileiAdapter);
					SubLL.addView(sp);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), sp));
				}
				break;
			case enFloat:
				EditText ET = new EditText(LL.getContext());
				ET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				LPET = new LayoutParams(ET.getWidth(), ET.getHeight());
				LPET.width = LayoutParams.FILL_PARENT;
				LPET.height = LayoutParams.WRAP_CONTENT;

				ET.setLayoutParams(LPET);
				SubLL.addView(ET);

				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
				break;
			case enBoolean:
				Spinner SP = new Spinner(LL.getContext());
				v1_DataBind.SetBindListSpinner(mOwnActivity, "是否", Tools.StrArrayToList(new String[] { "是", "否" }), SP);
				LPET = new LayoutParams(SP.getWidth(), SP.getHeight());
				LPET.width = LayoutParams.FILL_PARENT;
				LPET.height = LayoutParams.WRAP_CONTENT;
				SP.setLayoutParams(LPET);
				SubLL.addView(SP);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), SP));
				break;
			case enInt:
				if (LF.GetFieldEnumCode().equals("")) {
					EditText ET2 = new EditText(LL.getContext());
					ET2.setInputType(InputType.TYPE_CLASS_NUMBER);
					LPET = new LayoutParams(ET2.getWidth(), ET2.getHeight());
					LPET.width = LayoutParams.FILL_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					ET2.setLayoutParams(LPET);
					SubLL.addView(ET2);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET2));
				} else {
					v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext());
					LPET = new LayoutParams(es.getWidth(), es.getHeight());
					LPET.width = LayoutParams.FILL_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					es.setLayoutParams(LPET);
					es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
					es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(), LF.GetFieldEnumCode()));
					es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
					SubLL.addView(es);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), es));
				}
				break;
			case enDateTime:
				final v1_SpinnerDialog SP2 = new v1_SpinnerDialog(LL.getContext());
				SP2.SetCallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_Data_Template_DateTime dtd = new v1_Data_Template_DateTime();
						dtd.SetCallabck(new ICallback() {
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								v1_DataBind.SetBindListSpinner(mOwnActivity, "日期",
										Tools.StrArrayToList(new String[] { ExtraStr.toString() }), SP2);
							}
						});
						dtd.ShowDialog();

					}
				});
				LPET = new LayoutParams(SP2.getWidth(), SP2.getHeight());
				LPET.width = LayoutParams.FILL_PARENT;
				LPET.height = LayoutParams.WRAP_CONTENT;
				SP2.setLayoutParams(LPET);
				SubLL.addView(SP2);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), SP2));
				break;
			default:
				break;
			}
			LL.addView(SubLL);
		}
		for (FieldView FV : this._FieldNameViewList) {
			this._BaseObject.AddDataBindItem(new DataBindOfKeyValue(FV.FieldName, FV.DataFieldName, "", FV.FieldView));
		}

		// this._BaseObject.ReadDataAndBindToView("SYS_ID="+mObjId,mLayer.GetLayerID(),mLayer.GetLayerProjecType());
		// //读取数据并更新状态
		// initPhotoPager();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				_BaseObject.ReadDataAndBindToView("SYS_ID=" + mObjId, mLayer.GetLayerID(), mLayer.GetLayerProjecType());

			}
		}, 1);
	}

	private void initBaseObject() {
		if (_BaseObject == null) {
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
			this._BaseObject = new v1_CGpsDataObject();
			this._BaseObject.SetDataset(pDataset);
			this._BaseObject.SetSYS_ID(mObjId);
			this._BaseObject.setWaterMarkKey(mLayer.GetWaterMarkDataFieldStr());
		}
	}

	private LinearLayout CreateFormRowHeader(LinearLayout LL, String Text, int LabelMaxLen) {
		LinearLayout SubLL = new LinearLayout(LL.getContext());
		LayoutParams LP = new LayoutParams(SubLL.getWidth(), SubLL.getHeight());
		LP.width = LayoutParams.FILL_PARENT;
		LP.height = LayoutParams.WRAP_CONTENT;
		SubLL.setGravity(Gravity.CENTER);
		SubLL.setLayoutParams(LP);

		// 标签文本
		TextView TV = new TextView(LL.getContext());
		TV.setText(Tools.PadLeft(Text, LabelMaxLen) + "：");
		TV.setTextColor(Color.BLACK);
		TV.setTextAppearance(LL.getContext(), android.R.attr.textAppearanceMedium);
		SubLL.addView(TV);
		return SubLL;
	}

	public void ShowView() {
		try {
			View view = mOwnActivity.findViewById(R.id.ll_xbxj);
			changePageViewIndex(0);
			view.setVisibility(View.VISIBLE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void autoGetXiaoban() {
		// 获取XY值
		calcXY();
		int selectedID = 0;
		int selectionCount = 0;
		GeoLayer selectionlayer = null;

		for (GeoLayer layer : PubVar.m_MapControl.getMap().getGeoLayers(lkGeoLayersType.enAll).getList()) {
			if (layer.getType() == lkGeoLayerType.enPolygon) {
				selectionCount += layer.getSelSelection().getCount();
				if (layer.getSelSelection().getCount() == 1) {
					selectedID = layer.getSelSelection().getGeometryIndexList().get(0);
					selectionlayer = layer;
				}
			}
		}
		;

		if (selectionCount == 0) {
			Tools.ShowMessageBox("无法获取小班信息,请选中要测量蓄积的小班");
			return;
		}
		if (selectionCount > 1) {
			Tools.ShowMessageBox("选中小班太多，请只选中要测量蓄积的小班");
			return;
		}

		// 获取选中小班的属性
		if (selectionlayer != null) {
			HashMap<String, String> xbFeature = CopyFeature.CopyFrom(selectionlayer.getDataset(), selectedID);

			v1_Layer selLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
					.GetLayerByID(selectionlayer.getId());
			if (selLayer == null) {
				selLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(selectionlayer.getId());
			}

			if (selLayer == null) {
				return;
			}

			String xian = getValueFromSelction(selLayer, xbFeature, "XIAN", "县");
			if (xian != null) {
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcsxian, xian);
				if (xian.length() == 6) {
					try {
						Integer.parseInt(xian);
						Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_Shi, xian.substring(0, 4));
					} catch (Exception ex) {

					}
				}
			}

			String xiang = getValueFromSelction(selLayer, xbFeature, "XIANG", "乡镇");
			if (xiang != null) {
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcsxiang, xiang);
			}

			String cun = getValueFromSelction(selLayer, xbFeature, "CUN", "村");
			if (cun != null) {
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcscun, cun);
			}

			String linban = getValueFromSelction(selLayer, xbFeature, "LIN_BAN", "林班");
			if (linban != null) {
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcsLinban, linban);
			}

			String xiaoban = getValueFromSelction(selLayer, xbFeature, "XIAO_BAN", "小班");
			if (xiaoban != null) {
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcsXiaoban, xiaoban);
			}

		}

	}

	private String getValueFromSelction(v1_Layer selLayer, HashMap<String, String> xbFeature, String dataFieldName1,
			String dataFieldName2) {
		String value = null;
		String dataFieldName = selLayer.GetDataFieldNameByFieldName(dataFieldName1);
		if (dataFieldName == null || dataFieldName.length() == 0) {
			dataFieldName = selLayer.GetDataFieldNameByFieldName(dataFieldName2);
		}

		if (dataFieldName != null && dataFieldName.length() > 0) {
			value = xbFeature.get(dataFieldName);
		}

		return value;
	}

	private void calcXY() {
		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
		if (mObjId != -1) {
			Geometry pGeometry = pDataset.GetGeometry(mObjId);
			if (pGeometry == null) {
				List<String> SYSIDList = new ArrayList<String>();
				SYSIDList.add(mObjId + "");
				List<Geometry> pGeometryList = pDataset.QueryGeometryFromDB1(SYSIDList);
				if (pGeometryList.size() != 0) {
					pGeometry = pGeometryList.get(0);
				}
			}
			if (pGeometry != null) {

				if (mLayer.GetLayerType() == lkGeoLayerType.enPoint) {
					Coordinate middle = ((Point) pGeometry).getCoordinate();
					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcsX, Tools.ConvertToDigi(middle.getX() + "", 2));

					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcsY, Tools.ConvertToDigi(middle.getY() + "", 2));
					return;
				}
				if (mLayer.GetLayerType() == lkGeoLayerType.enPolygon) {
					Coordinate middle = ((Polygon) pGeometry).getCenterPoint();
					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcsX, Tools.ConvertToDigi(middle.getX() + "", 2));

					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcsY, Tools.ConvertToDigi(middle.getY() + "", 2));
					return;

				}
				if (mLayer.GetLayerType() == lkGeoLayerType.enPolyline) {
					Coordinate middle = ((Polyline) pGeometry).getCenterPoint();
					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcsX, Tools.ConvertToDigi(middle.getX() + "", 2));

					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jgcsY, Tools.ConvertToDigi(middle.getY() + "", 2));
					return;
				}
			}
		}
	}

	private class FieldView {
		public FieldView(String fieldName, String dataFieldName, View view) {
			this.FieldName = fieldName;
			this.DataFieldName = dataFieldName;
			this.FieldView = view;
		}

		public String FieldName = "";
		public String DataFieldName = "";
		public View FieldView = null;
	}
}
