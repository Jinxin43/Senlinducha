package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.List;

import com.dingtu.Funtion.PhotoControl;
import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.Project_Web;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkFieldType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class VectorDataFeature {

	private Activity mOwnActivity = null;
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private PhotoControl mPhotoControl;
	private ViewPager viewPager;
	private int mCurrentSysID = 0;
	private v1_Layer mCurrentLayer = null;
	// 第一次加载第一个页面
	private boolean fristPageOne = true;
	// 第一次加载第二个页面
	private boolean firstPageTwo = true;
	private v1_CGpsDataObject _BaseObject = null;

	// 用于实体内部属性的字段视图列表
	private List<FieldView> _FieldInnerFeauterViewList = new ArrayList<FieldView>();

	// 字段名称与字段视图列表，用于保存数据，格式：字段名称,字段视图
	private List<FieldView> _FieldNameViewList = new ArrayList<FieldView>();

	public VectorDataFeature(String layerID, int dataID) {

		mOwnActivity = (Activity) PubVar.m_DoEvent.m_Context;
		mOwnActivity.findViewById(R.id.btnquit).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.btnsave).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.bt_vp_xb).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.bt_vp_phot).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.ll_generalEdit).setVisibility(View.VISIBLE);
		mCurrentSysID = dataID;
		mCurrentLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
				.GetLayerByID(layerID);
		firstPageTwo = true;
		fristPageOne = true;
		initViewPager();

		Tools.ToLocale(mOwnActivity.findViewById(R.id.tvLocaleText1));
		Tools.ToLocale(mOwnActivity.findViewById(R.id.tvLocaleText2));
		mOwnActivity.findViewById(R.id.ll_status2).setVisibility(View.GONE);
	}

	private void initViewPager() {
		viewContainter
				.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.d_tanhui_datatemplate, null));
		viewContainter
				.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.v1_data_template_photo, null));
		titleContainer.add("基础信息");
		titleContainer.add("照片");

		viewPager = (ViewPager) mOwnActivity.findViewById(R.id.GeneralViewPager);

		viewPager.setAdapter(new PagerAdapter() {
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
					SetEditInfo(mCurrentLayer.GetLayerID(), mCurrentSysID);
					fristPageOne = false;
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

				if (arg0 == 1) {
					if (mPhotoControl == null) {
						initPhotoPager();
					}
					if (pGeometry != null) {
						mPhotoControl.SetXiaoBanInfo("", "", "", "", "", pGeometry.getCenterPoint().getX() + "",
								pGeometry.getCenterPoint().getY() + "");
					}
					// if (mCenterPoint != null) {
					//
					// Log.d("mCenterPoint", mCenterPoint.getX() + " Y:" +
					// mCenterPoint.getY());
					//
					// _BaseObject.RefreshViewValueToData();
					// if (_BaseObject.getWaterMarkValue() == null) {
					// mPhotoControl.SetXiaoBanInfo(mCenterPoint.getX() + "",
					// mCenterPoint.getY() + "", "");
					// } else {
					// mPhotoControl.SetXiaoBanInfo(mCenterPoint.getX() + "",
					// mCenterPoint.getY() + "",
					// _BaseObject.getWaterMarkValue());
					// }
					//
					// } else {
					// Log.d("mCenterPoint", "mCenterPoint is null");
					// }

				}
				changePageViewIndex(arg0);
			}
		});

		changePageViewIndex(0);
	}

	Geometry pGeometry = null;

	public void SetEditInfo(String layerID, int SYS_ID) {
		// this.mCurrentLayer =
		// PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID);
		Dataset pDataset = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).GetLayerById(layerID)
				.getDataset();

		initGeneralForm(layerID, SYS_ID, pDataset);

		// 初始化实体并设置绑定项目
		this._BaseObject = new v1_CGpsDataObject();
		this._BaseObject.SetDataset(pDataset);
		// if (this._Layer.GetIfLabel())
		// this._BaseObject.SetLabelFieldName(this._Layer.GetLabelField());
		// //设置标注字段名称

		this._BaseObject.SetSYS_ID(SYS_ID); // 设置SYS_ID

		for (FieldView FV : this._FieldNameViewList) {
			this._BaseObject.AddDataBindItem(new DataBindOfKeyValue(FV.FieldName, FV.DataFieldName, "", FV.FieldView));
		}

		this._BaseObject.ReadDataAndBindToView("SYS_ID=" + SYS_ID, layerID, mCurrentLayer.GetLayerProjecType()); // 读取数据并更新状态
		// this._DataDialog.SetGpsBasePointObject(this._BaseObject);
		// this._DataDialog.UpdateDialogShowInfo(); //更新界面显示

		pGeometry = pDataset.GetGeometry(SYS_ID);
		if (pGeometry == null) {
			List<String> SYSIDList = new ArrayList<String>();
			SYSIDList.add(SYS_ID + "");
			List<Geometry> pGeometryList = pDataset.QueryGeometryFromDB1(SYSIDList);
			if (pGeometryList.size() != 0) {
				pGeometry = pGeometryList.get(0);
			}
		}

		if (pGeometry != null) {
			Coordinate middle = pGeometry.getCenterPoint();

			// String scale =
			// PubVar.m_HashMap.GetValueObject("Tag_System_MapScale",
			// false).Value;
			// if (etTFH != null) {
			// if (scale.isEmpty()) {
			// this.etTFH.setText(Tools.CalcTuFuHao(middle, "1:1万"));
			// } else {
			// this.etTFH.setText(Tools.CalcTuFuHao(middle, scale));
			// }
			// }

		}

	}

	private void initGeneralForm(String layerID, int SYS_ID, Dataset pDataset) {

		this.CreateForm(this.mCurrentLayer);

		// this.SetShowGpsAveragePointOption();

		// 表示编辑状态
		if (SYS_ID != -1) {
			// 实体内部属性信息
			Geometry pGeometry = pDataset.GetGeometry(SYS_ID);

			if (pGeometry == null) {
				List<String> SYSIDList = new ArrayList<String>();
				SYSIDList.add(SYS_ID + "");
				List<Geometry> pGeometryList = pDataset.QueryGeometryFromDB1(SYSIDList);
				if (pGeometryList.size() != 0)
					pGeometry = pGeometryList.get(0);

			}

			if (pGeometry != null) {
				// mCenterPoint = pGeometry.getCenterPoint();
				if (this.mCurrentLayer.GetLayerType() == lkGeoLayerType.enPoint) {
					TextView TV = (TextView) this._FieldInnerFeauterViewList.get(0).FieldView;
					Coordinate Coor = ((Point) pGeometry).getCoordinate();
					CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
					if (CS.GetName().equals("WGS-84坐标")) {
						Coordinate BLCoor = Project_Web.Web_XYToBL(Coor.getX(), Coor.getY());
						TV.setText(Tools.ConvertToDigi(BLCoor.getX() + "", 6) + ","
								+ Tools.ConvertToDigi(BLCoor.getY() + "", 6) + ","
								+ Tools.ConvertToDigi(Coor.getZ() + "", 2));
					} else {
						TV.setText(Coor.ToString() + "," + Tools.ConvertToDigi(Coor.getZ() + "", 2));
					}
				}
				if (this.mCurrentLayer.GetLayerType() == lkGeoLayerType.enPolyline) {
					TextView TV = (TextView) this._FieldInnerFeauterViewList.get(0).FieldView;
					TV.setText(Tools.ReSetDistance(((Polyline) pGeometry).getLength(true), true));
				}
				if (this.mCurrentLayer.GetLayerType() == lkGeoLayerType.enPolygon) {
					TextView TV = (TextView) this._FieldInnerFeauterViewList.get(0).FieldView;
					String mj = Tools.ReSetArea(((Polygon) pGeometry).getArea(true), true);
					TV.setText(mj);

				}
			}
		}
	}

	private void CreateForm(v1_Layer vLayer) {

		if (vLayer == null)
			return;

		// 计算标签文本的最大长度，为对齐做准备
		int LabelMaxLen = 0;
		for (v1_LayerField LF : vLayer.GetFieldList()) {
			if (Tools.CalStrLength(Tools.ToLocale(LF.GetFieldName())) > LabelMaxLen) {
				LabelMaxLen = Tools.CalStrLength(Tools.ToLocale(LF.GetFieldName()));
			}
		}

		// 根据图层的字段配置信息，动态生成属性表单
		LinearLayout LL = (LinearLayout) mOwnActivity.findViewById(R.id.baselist1);
		TableLayout tableLayout = new TableLayout(LL.getContext());
		LayoutParams LPET1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LPET1.width = LayoutParams.MATCH_PARENT;
		LPET1.height = LayoutParams.WRAP_CONTENT;
		tableLayout.setLayoutParams(LPET1);
		// tableLayout.setBackgroundColor(Color.RED);

		for (v1_LayerField LF : vLayer.GetFieldList()) {
			// 创建标签
			// LinearLayout SubLL = this.CreateFormRowHeader(LL,
			// Tools.ToLocale(LF.GetFieldName()), LabelMaxLen);

			TableRow tableRow = CreateTableRow(tableLayout, Tools.ToLocale(LF.GetFieldName()));
			// LinearLayout linearLayout = new
			// LinearLayout(tableRow.getContext());
			// linearLayout.setMinimumWidth(80);
			// 数据容器
			if (LF.GetFieldType() == lkFieldType.enString) // 文本
			{
				if (LF.GetFieldEnumCode().equals("")) {
					EditText ET = new EditText(LL.getContext());
					LayoutParams LPET = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					// LPET.width = LayoutParams.MATCH_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					// ET.setLayoutParams(LPET);
					// ET.setBackgroundColor(Color.WHITE);
					ET.setMinimumHeight(10);
					// SubLL.addView(ET);
					tableRow.addView(ET);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
				} else {
					v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext(), Spinner.MODE_DROPDOWN, true);
					// LayoutParams LPET = new LayoutParams(es.getWidth(),
					// es.getHeight());
					es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(), LF.GetFieldEnumCode()));
					tableRow.addView(es);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), es));
				}
			}

			if (LF.GetFieldType() == lkFieldType.enFloat) // 浮点数字
			{
				if (LF.GetFieldEnumCode().equals("")) {
					EditText ET = new EditText(LL.getContext());
					ET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
					LayoutParams LPET = new LayoutParams(ET.getWidth(), ET.getHeight());
					LPET.width = LayoutParams.MATCH_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					// ET.setLayoutParams(LPET);
					ET.setMinimumHeight(10);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
					// if (LF.GetFieldName().equals("小班面积")
					// || LF.GetFieldName().equals("面积") |
					// LF.GetFieldName().equals("小班面积(亩)")) {
					// // this.etXBMJ = ET;
					// SubLL.addView(ET, 1);
					//
					// } else if (LF.GetFieldName().equals("小班蓄积量")) {
					// TextView TV = new TextView(LL.getContext());
					// TV.setText("蓄积调查");
					// TV.setTextColor(Color.BLUE);
					// TV.setTextAppearance(LL.getContext(),
					// android.R.attr.textAppearanceMedium);
					//
					// LayoutParams param = new LayoutParams(ET.getWidth(),
					// ET.getHeight());
					// param.width = LayoutParams.WRAP_CONTENT;
					// param.height = LayoutParams.WRAP_CONTENT;
					// TV.setLayoutParams(param);
					//
					// TV.setOnClickListener(new OnClickListener() {
					//
					// @Override
					// public void onClick(View v) {
					// LDBG_Jiaoguiceshu jiaoguiceshu = new LDBG_Jiaoguiceshu();
					// jiaoguiceshu.ShowDialog();
					//
					// }
					// });
					//
					// SubLL.addView(ET, 1);
					// SubLL.addView(TV, 2);
					//
					// } else {
					// SubLL.addView(ET, 1);
					tableRow.addView(ET);
					// }

				} else {
					v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext(), Spinner.MODE_DROPDOWN, true);
					LayoutParams LPET = new LayoutParams(es.getWidth(), es.getHeight());
					LPET.width = LayoutParams.MATCH_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					es.setLayoutParams(LPET);
					es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
					es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(), LF.GetFieldEnumCode()));
					es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
					// SubLL.addView(es);
					tableRow.addView(es);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), es));
				}

			}
			if (LF.GetFieldType() == lkFieldType.enInt) // 整数
			{
				if (LF.GetFieldEnumCode().equals("")) {
					EditText ET = new EditText(LL.getContext());
					ET.setInputType(InputType.TYPE_CLASS_NUMBER);
					LayoutParams LPET = new LayoutParams(ET.getWidth(), ET.getHeight());
					LPET.width = LayoutParams.MATCH_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					// ET.setLayoutParams(LPET);
					// SubLL.addView(ET);
					// ET.setMinimumHeight(10);
					tableRow.addView(ET);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
				} else {
					v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext(), Spinner.MODE_DROPDOWN, true);
					LayoutParams LPET = new LayoutParams(es.getWidth(), es.getHeight());
					LPET.width = LayoutParams.MATCH_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					// es.setLayoutParams(LPET);
					es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
					es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(), LF.GetFieldEnumCode()));
					es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
					// SubLL.addView(es);
					tableRow.addView(es);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), es));
				}
			}
			if (LF.GetFieldType() == lkFieldType.enBoolean) // 布尔型
			{
				Spinner SP = new Spinner(LL.getContext(), Spinner.MODE_DROPDOWN);
				v1_DataBind.SetBindListSpinner(mOwnActivity, "是否", Tools.StrArrayToList(new String[] { "是", "否" }), SP);
				LayoutParams LPET = new LayoutParams(SP.getWidth(), SP.getHeight());
				LPET.width = LayoutParams.MATCH_PARENT;
				LPET.height = LayoutParams.WRAP_CONTENT;
				// SP.setLayoutParams(LPET);
				// SubLL.addView(SP);
				SP.setMinimumHeight(10);
				tableRow.addView(SP);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), SP));
			}

			if (LF.GetFieldType() == lkFieldType.enDateTime) // 日期
			{
				final v1_SpinnerDialog SP = new v1_SpinnerDialog(LL.getContext(), Spinner.MODE_DROPDOWN);
				SP.SetCallback(new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_Data_Template_DateTime dtd = new v1_Data_Template_DateTime();
						dtd.SetCallabck(new ICallback() {
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								v1_DataBind.SetBindListSpinner(mOwnActivity, "日期",
										Tools.StrArrayToList(new String[] { ExtraStr.toString() }), SP);
							}
						});
						dtd.ShowDialog();

					}
				});

				LayoutParams LPET = new LayoutParams(SP.getWidth(), SP.getHeight());
				LPET.width = LayoutParams.MATCH_PARENT;
				LPET.height = LayoutParams.WRAP_CONTENT;
				// SP.setLayoutParams(LPET);
				// SubLL.addView(SP);
				SP.setMinimumHeight(10);
				tableRow.addView(SP);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), SP));
			}

			tableLayout.addView(tableRow);

			// LL.addView(SubLL);

		}
		LL.addView(tableLayout);

		// 在此加入采集数据的状态信息，如点的坐标，线的长度，面的面积等
		List<String> objInnerFeatureList = new ArrayList<String>();
		if (vLayer.GetLayerType() == lkGeoLayerType.enPoint) {
			objInnerFeatureList.add(Tools.ToLocale("坐标"));
		}
		if (vLayer.GetLayerType() == lkGeoLayerType.enPolyline) {
			objInnerFeatureList.add(Tools.ToLocale("长度"));
		}
		if (vLayer.GetLayerType() == lkGeoLayerType.enPolygon) {
			objInnerFeatureList.add(Tools.ToLocale("面积"));
		}
		LinearLayout LO = (LinearLayout) mOwnActivity.findViewById(R.id.otherlist1);
		for (String FL : objInnerFeatureList) {
			LinearLayout SubLL = this.CreateFormRowHeader(LO, FL, LabelMaxLen);
			EditText ET = new EditText(LO.getContext());
			ET.setEnabled(false);
			LayoutParams LPET = new LayoutParams(ET.getWidth(), ET.getHeight());
			LPET.width = LayoutParams.MATCH_PARENT;
			LPET.height = LayoutParams.WRAP_CONTENT;
			ET.setLayoutParams(LPET);
			SubLL.addView(ET);
			LO.addView(SubLL);

			this._FieldInnerFeauterViewList.add(new FieldView(FL, "", ET));
		}

		mOwnActivity.findViewById(R.id.allNomal).setVisibility(View.VISIBLE);
		mOwnActivity.findViewById(R.id.alltanhui).setVisibility(View.GONE);

	}

	private TableRow CreateTableRow(TableLayout tLayout, String Text) {
		TableRow tRow = new TableRow(tLayout.getContext());

		TextView TV = new TextView(tLayout.getContext());
		TV.setText(Text + ":");
		TV.setTextColor(Color.BLACK);
		TV.setTextAppearance(tLayout.getContext(), android.R.attr.textAppearanceMedium);
		TV.setGravity(Gravity.RIGHT);
		tRow.addView(TV);
		// tRow.setBackgroundColor(Color.YELLOW);
		return tRow;
	}

	private LinearLayout CreateFormRowHeader(LinearLayout LL, String Text, int LabelMaxLen) {
		LinearLayout SubLL = new LinearLayout(LL.getContext());
		LayoutParams LP = new LayoutParams(SubLL.getWidth(), SubLL.getHeight());
		LP.width = LayoutParams.MATCH_PARENT;
		LP.height = LayoutParams.WRAP_CONTENT;
		SubLL.setGravity(Gravity.CENTER);
		SubLL.setLayoutParams(LP);

		// 标签文本
		TextView TV = new TextView(LL.getContext());
		TV.setText(Tools.PadLeft(Text, LabelMaxLen) + "：");
		TV.setTextColor(Color.BLACK);
		TV.setTextAppearance(LL.getContext(), android.R.attr.textAppearanceMedium);
		SubLL.addView(TV, 0);
		return SubLL;
	}

	private void changePageViewIndex(int position) {
		Button btnXBXX = (Button) mOwnActivity.findViewById(R.id.bt_vp_xb);
		Button btnPhoto = (Button) mOwnActivity.findViewById(R.id.bt_vp_phot);

		if (position == 0) {
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnPhoto.setTextColor(android.graphics.Color.BLACK);
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnXBXX.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.locator22).setVisibility(View.INVISIBLE);
		}
		if (position == 1) {
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnXBXX.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnPhoto.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.locator22).setVisibility(View.INVISIBLE);
		}
	}

	private void initPhotoPager() {
		List<String> mPhotoNameList = new ArrayList<String>();
		if (this._BaseObject.GetSYS_PHOTO() != null && this._BaseObject.GetSYS_PHOTO().length() > 0) {
			mPhotoNameList = Tools.StrArrayToList(this._BaseObject.GetSYS_PHOTO().split(","));
		}

		mPhotoControl = new PhotoControl(mCurrentSysID, mPhotoNameList, mCurrentLayer.GetShowWaterMark(), false,
				viewContainter.get(1));
		this._BaseObject.setWaterMarkKey(mCurrentLayer.GetWaterMarkDataFieldStr());
		mPhotoControl.setCallback(new ICallback() {

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				Save();
			}
		});
	}

	private void Save() {
		boolean OK = true;
		// 保存属性数据
		this._BaseObject.RefreshViewValueToData();
		if (mPhotoControl != null) {
			this._BaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
		}

		if (!this._BaseObject.SaveVectorFeature())
			OK = false;
		if (!OK) {
			Tools.ShowMessageBox(mOwnActivity, "数据保存失败！");
			return;
		} else {
			// HiddenView();
		}

	}

	class ViewClick implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			DoCommand(Tag);
		}
	}

	private void DoCommand(String StrCommand) {

		if (StrCommand.equals("返回")) {
			HiddenView();
		}

		if (StrCommand.equals("保存")) {
			lkmap.Tools.Tools.OpenDialog("正在保存数据...", new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					Save();
					HiddenView();
					PubVar.m_Map.FastRefresh();
				}
			});

		}

		if (StrCommand.equals("属性记录")) {
			viewPager.setCurrentItem(0);
		}
		if (StrCommand.equals("照片")) {
			viewPager.setCurrentItem(1);
			_BaseObject.RefreshViewValueToData();
		}
	}

	private void HiddenView() {
		try {
			mOwnActivity.findViewById(R.id.ll_generalEdit).setVisibility(View.GONE);
			fristPageOne = true;
			firstPageTwo = true;
		} catch (Exception ex) {

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
