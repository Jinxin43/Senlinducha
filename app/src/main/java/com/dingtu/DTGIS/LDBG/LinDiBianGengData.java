package com.dingtu.DTGIS.LDBG;

import java.util.ArrayList;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.DataBindOfKeyValue;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_Data_Template_DateTime;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class LinDiBianGengData {
	private v1_Layer mLayer;
	private int mObjId = 0;
	private Activity mOwnActivity = null;
	private List<FieldView> _FieldInnerFeauterViewList = new ArrayList<FieldView>();
	private List<FieldView> _FieldNameViewList = new ArrayList<FieldView>();
	private LDBGDataObject _BaseObject = null;
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private PhotoControl mPhotoControl;
	private ViewPager viewPager;
	private boolean fristPageOne = true;
	private boolean firstPageTwo = true;
	private View viewBiangenProperty;

	public LinDiBianGengData(String layerID, int dataID) {

		mObjId = dataID;
		mLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID);
		mOwnActivity = (Activity) PubVar.m_DoEvent.m_Context;

		initBaseObject();

		Button btnQuit = (Button) mOwnActivity.findViewById(R.id.ldbg_quit);
		btnQuit.setOnClickListener(new ViewClick());
		Button btnSave = (Button) mOwnActivity.findViewById(R.id.ldbg_save);
		btnSave.setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.bt_viewpager_xb).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.bt_viewpager_phot).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.tv_RecordXuJi).setOnClickListener(new ViewClick());
		initViewPager();

	}

	private void initViewPager() {
		viewBiangenProperty = LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.lindibiangengedit, null);
		viewContainter.add(viewBiangenProperty);
		viewContainter
				.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.v1_data_template_photo, null));
		titleContainer.add("属性记录");
		titleContainer.add("照片");

		viewPager = (ViewPager) mOwnActivity.findViewById(R.id.LDBGviewPager);

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

				if (position == 0 && fristPageOne) {
					CreateForm(mLayer);
					fristPageOne = false;
					calcXY();
				}
				if (position == 1 && firstPageTwo) {
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

		viewPager.setOffscreenPageLimit(1);

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
				if (arg0 == 2) {
					if (mPhotoControl == null) {
						initPhotoPager();
						calcXY();

					}

					// mPhotoControl.SetXiaoBanInfo(_BaseObject.getFeatureValue("县")+_BaseObject.getFeatureValue("林业局").replace("000000",
					// ""),
					// _BaseObject.getFeatureValue("乡")+_BaseObject.getFeatureValue("林场").replace("000",
					// ""),
					// _BaseObject.getFeatureValue("村"),
					// _BaseObject.getFeatureValue("林班号"),
					// _BaseObject.getFeatureValue("小班号"),
					// Tools.GetTextValueOnID(mOwnActivity, R.id.et_lindx),
					// Tools.GetTextValueOnID(mOwnActivity, R.id.et_lindy));
					mPhotoControl.setWaterWaterMark(mLayer.GetShowWaterMark());
					setWaterMarkValue();

				}
			}
		});
	}

	private String setWaterMarkValue() {
		if (mLayer.GetShowWaterMark()) {
			_BaseObject.RefreshViewValueToData();
			String watermark = "所属林班：";
			if (_BaseObject.getWaterMarkValue() != null) {
				watermark += _BaseObject.getWaterMarkValue();
			}
			mPhotoControl.SetXiaoBanInfo(Tools.GetTextValueOnID(mOwnActivity, R.id.et_lindxx),
					Tools.GetTextValueOnID(mOwnActivity, R.id.et_lindyy), watermark);
		} else {
			mPhotoControl.SetXiaoBanInfo(Tools.GetTextValueOnID(mOwnActivity, R.id.et_lindxx),
					Tools.GetTextValueOnID(mOwnActivity, R.id.et_lindyy), "");
		}
		return "";
	}

	private void initPhotoPager() {
		initBaseObject();
		List<String> mPhotoNameList = new ArrayList<String>();
		if (this._BaseObject.GetSYS_PHOTO() != null && this._BaseObject.GetSYS_PHOTO().length() > 0) {
			mPhotoNameList = Tools.StrArrayToList(this._BaseObject.GetSYS_PHOTO().split(","));
		}

		mPhotoControl = new PhotoControl(mObjId, mPhotoNameList, mLayer.GetShowWaterMark(), false,
				viewContainter.get(1));
		mPhotoControl.setCallback(new ICallback() {

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				saveData();
			}
		});

		// HashValueObject hvoWaterMark =
		// PubVar.m_HashMap.GetValueObject("Tag_Photo_WaterMark", false);
		// if(hvoWaterMark != null && hvoWaterMark.Value.equals("true"))
		// {
		// mPhotoControl = new
		// PhotoControl(mObjId,mPhotoNameList,true,viewContainter.get(1));
		// }
		// else
		// {
		// mPhotoControl = new
		// PhotoControl(mObjId,mPhotoNameList,false,viewContainter.get(1));
		// }

	}

	private void initBaseObject() {
		if (_BaseObject == null) {
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
			this._BaseObject = new LDBGDataObject();
			this._BaseObject.SetDataset(pDataset);
			this._BaseObject.SetSYS_ID(mObjId);
			this._BaseObject.setWaterMarkKey(mLayer.GetWaterMarkDataFieldStr());
		}
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
				if (mLayer.GetLayerType() == lkGeoLayerType.enPolygon) {
					Coordinate middle = ((Polygon) pGeometry).getCenterPoint();
					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_lindxx,
							Tools.ConvertToDigi(middle.getX() + "", 2));

					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_lindyy,
							Tools.ConvertToDigi(middle.getY() + "", 2));

				}
				if (mLayer.GetLayerType() == lkGeoLayerType.enPolyline) {
					Coordinate middle = ((Polyline) pGeometry).getCenterPoint();
					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_lindxx,
							Tools.ConvertToDigi(middle.getX() + "", 2));

					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_lindyy,
							Tools.ConvertToDigi(middle.getY() + "", 2));
				}
				if (mLayer.GetLayerType() == lkGeoLayerType.enPoint) {
					Coordinate middle = ((Point) pGeometry).getCoordinate();
					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_lindxx,
							Tools.ConvertToDigi(middle.getX() + "", 2));

					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_lindyy,
							Tools.ConvertToDigi(middle.getY() + "", 2));
				}
			}
		}
	}

	private void changePageViewIndex(int position) {
		Button btnXBXX = (Button) mOwnActivity.findViewById(R.id.bt_viewpager_xb);
		Button btnPhoto = (Button) mOwnActivity.findViewById(R.id.bt_viewpager_phot);

		if (position == 0) {
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnPhoto.setTextColor(android.graphics.Color.BLACK);
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnXBXX.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.locator22).setVisibility(View.INVISIBLE);
		}
		if (position == 1) {
			if (firstPageTwo) {
				initPhotoPager();
				firstPageTwo = false;
			}

			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnXBXX.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnPhoto.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.locator22).setVisibility(View.INVISIBLE);
		}
	}

	// 生成属性字段列表
	private void CreateForm(v1_Layer vLayer) {
		// 根据图层的字段配置信息，动态生成属性表单
		LinearLayout LL = (LinearLayout) viewBiangenProperty.findViewById(R.id.baselist);
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
				if (LF.GetFieldName().equals("省")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_province)));
					continue;
				}
				if (LF.GetFieldName().equals("县")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_xian)));
					continue;
				}

				if (LF.GetFieldName().equals("横坐标")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_lindxx)));
					continue;
				}

				if (LF.GetFieldName().equals("纵坐标")) {
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(),
							mOwnActivity.findViewById(R.id.et_lindyy)));
					continue;
				}

				if (LF.GetFieldName().equals("地市")) {
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

			// //数据容器
			// if (LF.GetFieldType()==lkFieldType.enString) //文本
			// {
			//
			// if (LF.GetFieldEnumCode().equals(""))
			// {
			// EditText ET = new EditText(LL.getContext());
			//// if(LF.GetFieldName().equals("村")||LF.GetFieldName().equals("乡")||LF.GetFieldName().equals("林业局")||LF.GetFieldName().equals("林场"))
			//// {
			//// ET.setEnabled(false);
			//// }
			//
			// LayoutParams LPET = new
			// LayoutParams(ET.getWidth(),ET.getHeight());
			// LPET.width = LayoutParams.MATCH_PARENT;
			// LPET.height = LayoutParams.WRAP_CONTENT;
			// ET.setLayoutParams(LPET);
			// SubLL.addView(ET);
			// this._FieldNameViewList.add(new
			// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
			//
			// } else
			// {
			//// v1_EditSpinnerDialog es = new
			// v1_EditSpinnerDialog(LL.getContext());
			//// LayoutParams LPET = new
			// LayoutParams(es.getWidth(),es.getHeight());
			//// LPET.width = LayoutParams.FILL_PARENT;
			//// LPET.height = LayoutParams.WRAP_CONTENT;
			//// es.setLayoutParams(LPET);
			//// es.SetSelectItemList(LF.getFieldEnumList(vLayer.GetLayerProjecType(),
			// LF.GetFieldEnumCode()));
			//// es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
			//// SubLL.addView(es);
			//// this._FieldNameViewList.add(new
			// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),es));
			//
			// Spinner sp = new Spinner(LL.getContext(),Spinner.MODE_DROPDOWN);
			// LayoutParams LPET = new
			// LayoutParams(sp.getWidth(),sp.getHeight());
			// LPET.width = LayoutParams.FILL_PARENT;
			// LPET.height = LayoutParams.WRAP_CONTENT;
			// sp.setLayoutParams(LPET);
			// ArrayAdapter<String> dileiAdapter = new
			// ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
			// android.R.layout.simple_spinner_item,
			// LF.getFieldEnumList(vLayer.GetLayerProjecType(),
			// LF.GetFieldEnumCode()));
			// dileiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// sp.setAdapter(dileiAdapter);
			// SubLL.addView(sp);
			// this._FieldNameViewList.add(new
			// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),sp));
			// }
			// }
			//
			// if (LF.GetFieldType()==lkFieldType.enFloat) //浮点数字
			// {
			// if (LF.GetFieldEnumCode().equals(""))
			// {
			//
			//
			// //角规绕测蓄积量不在这里展示
			//// if(LF.GetFieldName().equals("小班蓄积量"))
			//// {
			//// EditText ET = new EditText(LL.getContext());
			//// ET.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
			//// LayoutParams LPET = new
			// LayoutParams(ET.getWidth(),ET.getHeight());
			//// LPET.width = 348;
			//// LPET.height = LayoutParams.WRAP_CONTENT;
			////
			////
			//// ET.setLayoutParams(LPET);
			//// SubLL.addView(ET);
			////
			//// Button btnCalcXuji = new Button(SubLL.getContext());
			//// btnCalcXuji.setText("角规绕测");
			//// btnCalcXuji.setTag("蓄积调查");
			//// LayoutParams lpButton = new
			// LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			//// lpButton.width = 140;
			//// lpButton.height = LayoutParams.WRAP_CONTENT;
			//// btnCalcXuji.setLayoutParams(lpButton);
			//// btnCalcXuji.setOnClickListener(new OnClickListener(){
			////
			//// @Override
			//// public void onClick(View v) {
			//// LDBG_Jiaoguiceshu mJiaoguiceshu = new LDBG_Jiaoguiceshu();
			//// mJiaoguiceshu.ShowDialog();
			////
			//// }
			////
			//// });
			////
			//// SubLL.addView(btnCalcXuji);
			////
			//// this._FieldNameViewList.add(new
			// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
			//// }
			//// else
			//// {
			// EditText ET = new EditText(LL.getContext());
			// ET.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
			// LayoutParams LPET = new
			// LayoutParams(ET.getWidth(),ET.getHeight());
			// LPET.width = LayoutParams.FILL_PARENT;
			// LPET.height = LayoutParams.WRAP_CONTENT;
			//
			//
			// ET.setLayoutParams(LPET);
			// SubLL.addView(ET);
			//
			// this._FieldNameViewList.add(new
			// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
			//// }
			//
			//
			//
			// } else
			// {
			// v1_EditSpinnerDialog es = new
			// v1_EditSpinnerDialog(LL.getContext());
			// LayoutParams LPET = new
			// LayoutParams(es.getWidth(),es.getHeight());
			// LPET.width = LayoutParams.FILL_PARENT;
			// LPET.height = LayoutParams.WRAP_CONTENT;
			// es.setLayoutParams(LPET);
			// es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
			// es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType()));
			// es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
			// SubLL.addView(es);
			// this._FieldNameViewList.add(new
			// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),es));
			// }
			// }
			// if (LF.GetFieldType()==lkFieldType.enInt) //整数
			// {
			// if (LF.GetFieldEnumCode().equals(""))
			// {
			// EditText ET = new EditText(LL.getContext());
			// ET.setInputType(InputType.TYPE_CLASS_NUMBER);
			// LayoutParams LPET = new
			// LayoutParams(ET.getWidth(),ET.getHeight());
			// LPET.width = LayoutParams.FILL_PARENT;
			// LPET.height = LayoutParams.WRAP_CONTENT;
			// ET.setLayoutParams(LPET);
			// SubLL.addView(ET);
			// this._FieldNameViewList.add(new
			// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
			// } else
			// {
			// v1_EditSpinnerDialog es = new
			// v1_EditSpinnerDialog(LL.getContext());
			// LayoutParams LPET = new
			// LayoutParams(es.getWidth(),es.getHeight());
			// LPET.width = LayoutParams.FILL_PARENT;
			// LPET.height = LayoutParams.WRAP_CONTENT;
			// es.setLayoutParams(LPET);
			// es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
			// es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType()));
			// es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
			// SubLL.addView(es);
			// this._FieldNameViewList.add(new
			// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),es));
			// }
			// }
			// if (LF.GetFieldType()==lkFieldType.enBoolean) //布尔型
			// {
			// Spinner SP = new Spinner(LL.getContext());
			// v1_DataBind.SetBindListSpinner(mOwnActivity, "是否",
			// Tools.StrArrayToList(new String[]{"是","否"}), SP);
			// LayoutParams LPET = new
			// LayoutParams(SP.getWidth(),SP.getHeight());
			// LPET.width = LayoutParams.FILL_PARENT;
			// LPET.height = LayoutParams.WRAP_CONTENT;
			// SP.setLayoutParams(LPET);
			// SubLL.addView(SP);
			// this._FieldNameViewList.add(new
			// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),SP));
			// }
			//
			// if (LF.GetFieldType()==lkFieldType.enDateTime) //日期
			// {
			// final v1_SpinnerDialog SP = new
			// v1_SpinnerDialog(LL.getContext());
			// SP.SetCallback(new ICallback(){
			// @Override
			// public void OnClick(String Str, Object ExtraStr) {
			// v1_Data_Template_DateTime dtd = new v1_Data_Template_DateTime();
			// dtd.SetCallabck(new ICallback(){
			// @Override
			// public void OnClick(String Str, Object ExtraStr) {
			// v1_DataBind.SetBindListSpinner(mOwnActivity, "日期",
			// Tools.StrArrayToList(new String[]{ExtraStr.toString()}), SP);
			// }});
			// dtd.ShowDialog();
			//
			// }});
			// LayoutParams LPET = new
			// LayoutParams(SP.getWidth(),SP.getHeight());
			// LPET.width = LayoutParams.FILL_PARENT;
			// LPET.height = LayoutParams.WRAP_CONTENT;
			// SP.setLayoutParams(LPET);
			// SubLL.addView(SP);
			// this._FieldNameViewList.add(new
			// FieldView(LF.GetFieldName(),LF.GetDataFieldName(),SP));
			// }

			LL.addView(SubLL);
		}

		// //在此加入采集数据的状态信息，如点的坐标，线的长度，面的面积等
		// List<String> objInnerFeatureList = new ArrayList<String>();
		// if (vLayer.GetLayerType()==lkGeoLayerType.enPoint)
		// {
		// objInnerFeatureList.add(Tools.ToLocale("坐标"));
		// }
		// if (vLayer.GetLayerType()==lkGeoLayerType.enPolyline)
		// {
		// objInnerFeatureList.add(Tools.ToLocale("长度"));
		// }
		// if (vLayer.GetLayerType()==lkGeoLayerType.enPolygon)
		// {
		// objInnerFeatureList.add(Tools.ToLocale("面积"));
		// }
		// LinearLayout LO =
		// (LinearLayout)mOwnActivity.findViewById(R.id.otherlist);
		// for(String FL:objInnerFeatureList)
		// {
		// LinearLayout SubLL = this.CreateFormRowHeader(LO, FL, LabelMaxLen);
		// EditText ET = new EditText(LO.getContext());
		// ET.setEnabled(false);
		// LayoutParams LPET = new LayoutParams(ET.getWidth(),ET.getHeight());
		// LPET.width = LayoutParams.FILL_PARENT;
		// LPET.height = LayoutParams.WRAP_CONTENT;
		// ET.setLayoutParams(LPET);
		// SubLL.addView(ET);
		// LO.addView(SubLL);
		//
		// this._FieldInnerFeauterViewList.add(new FieldView(FL,"",ET));
		// }

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
			if (Tag.equals("属性记录")) {
				viewPager.setCurrentItem(0);
			}
			if (Tag.equals("照片")) {
				viewPager.setCurrentItem(1);
				_BaseObject.RefreshViewValueToData();
			}
			if (Tag.equals("蓄积量")) {
				LDBG_Jiaoguiceshu jiaoguiceshu = new LDBG_Jiaoguiceshu();
				jiaoguiceshu.ShowDialog();
			}
		}
	}

	public void ShowView() {
		try {

			View view = mOwnActivity.findViewById(R.id.ll_LDBG);
			changePageViewIndex(0);
			Animation animation = AnimationUtils.loadAnimation(mOwnActivity, R.anim.view_enter);
			view.setVisibility(View.VISIBLE);
			view.startAnimation(animation);
			calcXY();

		} catch (Exception ex) {

		}

	}

	public void HiddenView() {
		try {
			mOwnActivity.findViewById(R.id.ll_LDBG).setVisibility(View.GONE);
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

		// HiddenView();

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
