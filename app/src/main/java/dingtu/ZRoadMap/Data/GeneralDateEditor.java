package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DictXZQH;
import com.dingtu.DTGIS.LDBG.LDBGDataObject;
import com.dingtu.DTGIS.LDBG.LDBG_Jiaoguiceshu;
import com.dingtu.Funtion.PhotoControl;
import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import dingtu.ZRoadMap.PubVar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.Project_Web;
import lkmap.Dataset.DataSource;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkFieldType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class GeneralDateEditor {
	private int mCurrentSysID = 0;
	private int maxCode = 0;
	private boolean isTanhui = false;
	private String xiaobanLayerName = "";
	private String xiaobanField = "TU_BAN";
	private Activity mOwnActivity = null;
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private PhotoControl mPhotoControl;
	private ViewPager viewPager;
	private EditText etTFH;
	private EditText etDiShi;
	private EditText etQuXian;
	private EditText etXBMJ;
	private String mStrXian;
	private String mStrXiang;
	private String mStrCun;
	private String mStrDishi;
	private v1_EditSpinnerDialog mCunSpinner;
	// ��һ�μ��ص�һ��ҳ��
	private boolean fristPageOne = true;
	// ��һ�μ��صڶ���ҳ��
	private boolean firstPageTwo = true;
	// ʵ������
	private LDBGDataObject _BaseObject = null;
	private Coordinate mCenterPoint = null;

	// ��ǰͼ��
	private v1_Layer mCurrentLayer = null;

	public GeneralDateEditor(String layerID, int dataID) {
		mOwnActivity = (Activity) PubVar.m_DoEvent.m_Context;
		mOwnActivity.findViewById(R.id.btnquit).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.btnsave).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.bt_vp_xb).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.bt_vp_phot).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.ll_generalEdit).setVisibility(View.VISIBLE);
		mCurrentSysID = dataID;
		mCurrentLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID);
		mStrDishi = mCurrentLayer.getCity();
		mStrXian = mCurrentLayer.getCounty();
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
		titleContainer.add("������Ϣ");
		titleContainer.add("��Ƭ");

		viewPager = (ViewPager) mOwnActivity.findViewById(R.id.GeneralViewPager);

		viewPager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return viewContainter.size();
			}

			// �����л���ʱ�����ٵ�ǰ�����
			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				((ViewPager) container).removeView(viewContainter.get(position));
			}

			// ÿ�λ�����ʱ�����ɵ����
			@Override
			public Object instantiateItem(ViewGroup container, int position) {

				((ViewPager) container).addView(viewContainter.get(position));

				if (position == 0) {
					SetEditInfo(mCurrentLayer.GetLayerID(), mCurrentSysID);
					fristPageOne = false;
				}
				// if(position == 0 && firstPageTwo)
				// {
				// initPhotoPager();
				// firstPageTwo = false;
				// }
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

					if (mCenterPoint != null) {

						Log.d("mCenterPoint", mCenterPoint.getX() + " Y:" + mCenterPoint.getY());

						_BaseObject.RefreshViewValueToData();
						if (_BaseObject.getWaterMarkValue() == null) {
							mPhotoControl.SetXiaoBanInfo(mCenterPoint.getX() + "", mCenterPoint.getY() + "", "");
						} else {
							mPhotoControl.SetXiaoBanInfo(mCenterPoint.getX() + "", mCenterPoint.getY() + "",
									_BaseObject.getWaterMarkValue());
						}

					} else {
						Log.d("mCenterPoint", "mCenterPoint is null");
					}

				}
				changePageViewIndex(arg0);
			}
		});

		changePageViewIndex(0);
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
			// if(firstPageTwo)
			// {
			// initPhotoPager();
			// firstPageTwo = false;
			// }

			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnXBXX.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnPhoto.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.locator22).setVisibility(View.INVISIBLE);
		}
	}

	private ICallback _returnCallback = null;

	public void SetCallback(ICallback cb) {
		this._returnCallback = cb;
	}

	// �����Ƿ�ɼ�ƽ����ѡ�����ɽ���
	private v1_CGps_AveragePoint _MyAveragePoint = null;

	private void SetShowGpsAveragePointOption() {
		if (this._CalAveragePoint) {
			if (this.mCurrentLayer == null)
				return;
			v1_EditSpinnerDialog esd1;
			if (mCurrentLayer.GetLayerProjecType().contains("̼��")) {
				mOwnActivity.findViewById(R.id.ll_GPSstatus1).setVisibility(View.VISIBLE);
				mOwnActivity.findViewById(R.id.bt_restart1).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog) mOwnActivity
								.findViewById(R.id.sp_pointcount1);
						_MyAveragePoint.Start(Integer.parseInt(esd1.getText()));

					}
				});

				esd1 = (v1_EditSpinnerDialog) mOwnActivity.findViewById(R.id.sp_pointcount1);
			} else {
				mOwnActivity.findViewById(R.id.ll_status2).setVisibility(View.VISIBLE);
				mOwnActivity.findViewById(R.id.bt_restart2).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog) mOwnActivity
								.findViewById(R.id.sp_pointcount2);
						_MyAveragePoint.Start(Integer.parseInt(esd1.getText()));

					}
				});

				esd1 = (v1_EditSpinnerDialog) mOwnActivity.findViewById(R.id.sp_pointcount);
			}

			esd1.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
			esd1.SetSelectItemList(Tools.StrArrayToList(new String[] { "3", "4", "5", "10", "20", "30" }));
			esd1.getEditTextView().setEnabled(true);

			// Ĭ��ֵ
			int gpsPointCount = 1;
			if (this.mCurrentLayer.GetLayerType() == lkGeoLayerType.enPoint) {
				String PointCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount").Value + "";
				esd1.setText(PointCount);
				if (Tools.IsInteger(PointCount))
					gpsPointCount = Integer.parseInt(PointCount);
			} else {
				String VertexCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_VertexCount").Value + "";
				esd1.setText(VertexCount);
				if (Tools.IsInteger(VertexCount))
					gpsPointCount = Integer.parseInt(VertexCount);
			}
			this._MyAveragePoint = new v1_CGps_AveragePoint();
			this._MyAveragePoint.Start(gpsPointCount);
			this._MyAveragePoint.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("�ɼ�״̬")) {

						if (mCurrentLayer.GetLayerProjecType().contains("̼��")) {
							Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_gpspointcount1,
									(ExtraStr + "").split(",")[0] + "  ");
						} else {
							Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_gpspointcount2,
									(ExtraStr + "").split(",")[0] + "  ");
						}
					}

					if (Str.equals("�ɼ����")) {
						Coordinate coordinate = (Coordinate) ExtraStr;
						if (!mCurrentLayer.GetLayerProjecType().contains("̼��")) {
							TextView TV = (TextView) _FieldInnerFeauterViewList.get(0).FieldView;

							TV.setText(coordinate.ToString());
						}
					}
				}
			});

		}
	}

	// �Ƿ���Ҫ�ɼ�ƽ����
	private boolean _CalAveragePoint = false;

	/**
	 * �Ƿ���Ҫ�ɼ�ƽ����
	 * 
	 * @param _calAveragePoint
	 */
	public void SetCalAveragePoint(boolean _calAveragePoint) {
		this._CalAveragePoint = _calAveragePoint;
		this.SetShowGpsAveragePointOption();
	}

	/**
	 * ����ͼ�㶯̬��������Ϣ
	 * 
	 * @param vLayer
	 */

	private void CreateTanhuiForm(v1_Layer vLayer, int sysID) {
		if (vLayer == null)
			return;
		mCurrentLayer = vLayer;
		mCurrentSysID = sysID;

		for (v1_LayerField LF : vLayer.GetFieldList()) {
			if (LF.GetFieldName().equals("���غ�")) {
				EditText ET = (EditText) mOwnActivity.findViewById(R.id.evYangdihao);

				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
			}

			if (LF.GetFieldName().equals("С���")) {
				EditText ET = (EditText) mOwnActivity.findViewById(R.id.evXiaobanhao);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
			}

			if (LF.GetFieldName().equals("��׼�غ�")) {
				EditText ET = (EditText) mOwnActivity.findViewById(R.id.evBiaozhundihao);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
			}

			if (LF.GetFieldName().equals("ƽ����")) {
				EditText ET = (EditText) mOwnActivity.findViewById(R.id.evhight);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
			}

			if (LF.GetFieldName().equals("����")) {
				EditText ET = (EditText) mOwnActivity.findViewById(R.id.tvCount);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
			}

			if (LF.GetFieldName().equals("�������")) {
				EditText ET = (EditText) mOwnActivity.findViewById(R.id.tvXuJiliang);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
			}
		}

		mOwnActivity.findViewById(R.id.allNomal).setVisibility(View.GONE);

		mOwnActivity.findViewById(R.id.alltanhui).setVisibility(View.VISIBLE);

		loadMeiMuJianChiList();

		// �����ֶΣ��ֶ����ԣ�ɾ���ֶΰ�ť
		mOwnActivity.findViewById(R.id.pln_add).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.pln_edit).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.pln_delete).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.pln_export).setOnClickListener(new ViewClick());
	}

	private void initXiangCunControl() {
		if (mCunSpinner != null) {
			if (mStrDishi != null && mStrDishi.length() > 0 && mStrXian != null && mStrXian.length() > 0
					&& mStrXiang != null && mStrXiang.length() > 0) {
				DictXZQH xzqh = new DictXZQH();
				String shiCode = xzqh.getCodeByName(mStrDishi, "��", "61");

				String xianCode = xzqh.getCodeByName(mStrXian, "��", shiCode);
				String XiangCode = xzqh.getCodeByName(mStrXiang, "��", xianCode);

				List<HashMap<String, Object>> cun = xzqh.getXZQH(XiangCode, "��");

				ArrayList<String> cunNames = new ArrayList<String>();
				cunNames.add("");
				for (HashMap<String, Object> hm : cun) {
					cunNames.add(hm.get("D1").toString());
				}

				mCunSpinner.SetSelectItemList(cunNames);
				if (mStrCun != null) {

					if ((ArrayAdapter<String>) mCunSpinner.getAdapter() != null) {
						int p = ((ArrayAdapter<String>) mCunSpinner.getAdapter()).getPosition(mStrCun);
						mCunSpinner.setSelection(p, true);
					}

				}
			}
		}
	}

	private void CreateForm(v1_Layer vLayer) {

		if (vLayer == null)
			return;

		// �����ǩ�ı�����󳤶ȣ�Ϊ������׼��
		int LabelMaxLen = 0;
		for (v1_LayerField LF : vLayer.GetFieldList()) {
			if (Tools.CalStrLength(Tools.ToLocale(LF.GetFieldName())) > LabelMaxLen) {
				LabelMaxLen = Tools.CalStrLength(Tools.ToLocale(LF.GetFieldName()));
			}
		}

		// ����ͼ����ֶ�������Ϣ����̬�������Ա�
		LinearLayout LL = (LinearLayout) mOwnActivity.findViewById(R.id.baselist1);
		for (v1_LayerField LF : vLayer.GetFieldList()) {
			// ������ǩ
			LinearLayout SubLL = this.CreateFormRowHeader(LL, Tools.ToLocale(LF.GetFieldName()), LabelMaxLen);

			// ��������
			if (LF.GetFieldType() == lkFieldType.enString) // �ı�
			{
				if (LF.GetFieldEnumCode().equals("")) {
					if (LF.GetFieldName().equals("����") | LF.GetFieldName().equals("��")) {
						v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext(), Spinner.MODE_DROPDOWN);
						LayoutParams LPET = new LayoutParams(es.getWidth(), es.getHeight());
						LPET.width = LayoutParams.FILL_PARENT;
						LPET.height = LayoutParams.WRAP_CONTENT;
						es.setLayoutParams(LPET);
						es.getEditTextView().setEnabled(true);
						SubLL.addView(es);
						this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), es));

						if (mStrXian != null && mStrXian.length() > 0) {
							DictXZQH xzqh = new DictXZQH();
							if (mStrDishi != null && mStrDishi.length() > 0) {
								String shiCode = xzqh.getCodeByName(mStrDishi, "��", "61");

								String xiangCode = xzqh.getCodeByName(mStrXian, "��", shiCode);
								List<HashMap<String, Object>> Xiang = xzqh.getXZQH(xiangCode, "��");

								ArrayList<String> countyNames = new ArrayList<String>();
								countyNames.add("");
								for (HashMap<String, Object> hm : Xiang) {
									countyNames.add(hm.get("D1").toString());
								}

								es.SetSelectItemList(countyNames);
								es.setOnItemSelectedListener(new OnItemSelectedListener() {

									@Override
									public void onItemSelected(AdapterView<?> parent, View view, int position,
											long id) {
										mStrXiang = (String) parent.getItemAtPosition(position);
										initXiangCunControl();
									}

									@Override
									public void onNothingSelected(AdapterView<?> parent) {
										// TODO Auto-generated method stub

									}
								});
							}
						}
					} else if (LF.GetFieldName().equals("���ƴ�") || LF.GetFieldName().equals("��")
							|| LF.GetFieldName().equals("������")) {
						v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext(), Spinner.MODE_DROPDOWN);
						LayoutParams LPET = new LayoutParams(es.getWidth(), es.getHeight());
						LPET.width = LayoutParams.FILL_PARENT;
						LPET.height = LayoutParams.WRAP_CONTENT;
						es.setLayoutParams(LPET);
						es.getEditTextView().setEnabled(true);
						SubLL.addView(es);
						this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), es));

						mCunSpinner = es;
						initXiangCunControl();
					} else {

						EditText ET = new EditText(LL.getContext());
						LayoutParams LPET = new LayoutParams(ET.getWidth(), ET.getHeight());
						LPET.width = LayoutParams.FILL_PARENT;
						LPET.height = LayoutParams.WRAP_CONTENT;
						ET.setLayoutParams(LPET);
						SubLL.addView(ET);
						this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
						if (LF.GetFieldName().equals("ͼ����")) {
							this.etTFH = ET;
						}

						else if (LF.GetFieldName().equals("����") | LF.GetFieldName().equals("��")) {
							mStrDishi = vLayer.getCity();
							etDiShi = ET;
						} else if (LF.GetFieldName().equals("����") | LF.GetFieldName().equals("��")) {
							mStrXian = vLayer.getCounty();
							etQuXian = ET;
						}
					}
				} else {
					v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext(), Spinner.MODE_DROPDOWN);
					LayoutParams LPET = new LayoutParams(es.getWidth(), es.getHeight());
					LPET.width = LayoutParams.FILL_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					es.setLayoutParams(LPET);
					es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(), LF.GetFieldEnumCode()));
					es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
					SubLL.addView(es);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), es));
				}
			}

			if (LF.GetFieldType() == lkFieldType.enFloat) // ��������
			{
				if (LF.GetFieldEnumCode().equals("")) {
					EditText ET = new EditText(LL.getContext());
					ET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
					LayoutParams LPET = new LayoutParams(ET.getWidth(), ET.getHeight());
					LPET.width = LayoutParams.FILL_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					ET.setLayoutParams(LPET);

					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
					if (LF.GetFieldName().equals("С�����")
							|| LF.GetFieldName().equals("���") | LF.GetFieldName().equals("С�����(Ķ)")) {
						this.etXBMJ = ET;
						SubLL.addView(ET, 1);

					} else if (LF.GetFieldName().equals("С�������")) {
						TextView TV = new TextView(LL.getContext());

						TV.setText("�������");
						TV.setTextColor(Color.BLUE);
						TV.setTextAppearance(LL.getContext(), android.R.attr.textAppearanceMedium);
						TV.setMinimumWidth(30);
						LayoutParams param = new LayoutParams(ET.getWidth(), ET.getHeight());
						param.width = LayoutParams.WRAP_CONTENT;
						param.height = LayoutParams.WRAP_CONTENT;
						TV.setLayoutParams(param);

						TV.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								LDBG_Jiaoguiceshu jiaoguiceshu = new LDBG_Jiaoguiceshu();
								jiaoguiceshu.ShowDialog();

							}
						});

						SubLL.addView(ET, 1);
						SubLL.addView(TV, 2);

					} else {
						SubLL.addView(ET, 1);
					}

				} else {
					v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext(), Spinner.MODE_DROPDOWN);
					LayoutParams LPET = new LayoutParams(es.getWidth(), es.getHeight());
					LPET.width = LayoutParams.FILL_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					es.setLayoutParams(LPET);
					es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
					es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(), LF.GetFieldEnumCode()));
					es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
					SubLL.addView(es);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), es));
				}

			}
			if (LF.GetFieldType() == lkFieldType.enInt) // ����
			{
				if (LF.GetFieldEnumCode().equals("")) {
					EditText ET = new EditText(LL.getContext());
					ET.setInputType(InputType.TYPE_CLASS_NUMBER);
					LayoutParams LPET = new LayoutParams(ET.getWidth(), ET.getHeight());
					LPET.width = LayoutParams.FILL_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					ET.setLayoutParams(LPET);
					SubLL.addView(ET);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), ET));
				} else {
					v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext(), Spinner.MODE_DROPDOWN);
					LayoutParams LPET = new LayoutParams(es.getWidth(), es.getHeight());
					LPET.width = LayoutParams.FILL_PARENT;
					LPET.height = LayoutParams.WRAP_CONTENT;
					es.setLayoutParams(LPET);
					es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
					es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(), LF.GetFieldEnumCode()));
					es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
					SubLL.addView(es);
					this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), es));
				}
			}
			if (LF.GetFieldType() == lkFieldType.enBoolean) // ������
			{
				Spinner SP = new Spinner(LL.getContext(), Spinner.MODE_DROPDOWN);
				v1_DataBind.SetBindListSpinner(mOwnActivity, "�Ƿ�", Tools.StrArrayToList(new String[] { "��", "��" }), SP);
				LayoutParams LPET = new LayoutParams(SP.getWidth(), SP.getHeight());
				LPET.width = LayoutParams.MATCH_PARENT;
				LPET.height = LayoutParams.WRAP_CONTENT;
				SP.setLayoutParams(LPET);
				SubLL.addView(SP);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), SP));
			}

			if (LF.GetFieldType() == lkFieldType.enDateTime) // ����
			{
				final v1_SpinnerDialog SP = new v1_SpinnerDialog(LL.getContext(), Spinner.MODE_DROPDOWN);
				SP.SetCallback(new ICallback() {
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_Data_Template_DateTime dtd = new v1_Data_Template_DateTime();
						dtd.SetCallabck(new ICallback() {
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								v1_DataBind.SetBindListSpinner(mOwnActivity, "����",
										Tools.StrArrayToList(new String[] { ExtraStr.toString() }), SP);
							}
						});
						dtd.ShowDialog();

					}
				});
				LayoutParams LPET = new LayoutParams(SP.getWidth(), SP.getHeight());
				LPET.width = LayoutParams.FILL_PARENT;
				LPET.height = LayoutParams.WRAP_CONTENT;
				SP.setLayoutParams(LPET);
				SubLL.addView(SP);
				this._FieldNameViewList.add(new FieldView(LF.GetFieldName(), LF.GetDataFieldName(), SP));
			}

			LL.addView(SubLL);

		}

		// �ڴ˼���ɼ����ݵ�״̬��Ϣ���������꣬�ߵĳ��ȣ���������
		List<String> objInnerFeatureList = new ArrayList<String>();
		if (vLayer.GetLayerType() == lkGeoLayerType.enPoint) {
			objInnerFeatureList.add(Tools.ToLocale("����"));
		}
		if (vLayer.GetLayerType() == lkGeoLayerType.enPolyline) {
			objInnerFeatureList.add(Tools.ToLocale("����"));
		}
		if (vLayer.GetLayerType() == lkGeoLayerType.enPolygon) {
			objInnerFeatureList.add(Tools.ToLocale("���"));
		}
		LinearLayout LO = (LinearLayout) mOwnActivity.findViewById(R.id.otherlist1);
		for (String FL : objInnerFeatureList) {
			LinearLayout SubLL = this.CreateFormRowHeader(LO, FL, LabelMaxLen);
			EditText ET = new EditText(LO.getContext());
			ET.setEnabled(false);
			LayoutParams LPET = new LayoutParams(ET.getWidth(), ET.getHeight());
			LPET.width = LayoutParams.FILL_PARENT;
			LPET.height = LayoutParams.WRAP_CONTENT;
			ET.setLayoutParams(LPET);
			SubLL.addView(ET);
			LO.addView(SubLL);

			this._FieldInnerFeauterViewList.add(new FieldView(FL, "", ET));
		}

		mOwnActivity.findViewById(R.id.allNomal).setVisibility(View.VISIBLE);
		mOwnActivity.findViewById(R.id.alltanhui).setVisibility(View.GONE);

	}

	/**
	 * ���������е�ͷ�ı���ǩ
	 * 
	 * @param LL
	 * @param Text
	 * @param LabelMaxLen
	 * @return
	 */
	private LinearLayout CreateFormRowHeader(LinearLayout LL, String Text, int LabelMaxLen) {
		LinearLayout SubLL = new LinearLayout(LL.getContext());
		LayoutParams LP = new LayoutParams(SubLL.getWidth(), SubLL.getHeight());
		LP.width = LayoutParams.FILL_PARENT;
		LP.height = LayoutParams.WRAP_CONTENT;
		SubLL.setGravity(Gravity.CENTER);
		SubLL.setLayoutParams(LP);

		// ��ǩ�ı�
		TextView TV = new TextView(LL.getContext());
		TV.setText(Tools.PadLeft(Text, LabelMaxLen) + "��");
		TV.setTextColor(Color.BLACK);
		TV.setTextAppearance(LL.getContext(), android.R.attr.textAppearanceMedium);
		SubLL.addView(TV, 0);
		return SubLL;
	}

	// public void ShowDialog()
	// {
	// _DataDialog.show();
	//
	// }

	// ����ʵ���ڲ����Ե��ֶ���ͼ�б�
	private List<FieldView> _FieldInnerFeauterViewList = new ArrayList<FieldView>();

	// �ֶ��������ֶ���ͼ�б����ڱ������ݣ���ʽ���ֶ�����,�ֶ���ͼ
	private List<FieldView> _FieldNameViewList = new ArrayList<FieldView>();

	/**
	 * �Զ��屣�����
	 */
	private void Save() {
		boolean OK = true;

		// ����ͼ������
		if (this._CalAveragePoint) {
			// ��ȡ��ǰGPS��λ
			Coordinate newGPSCoor = this._MyAveragePoint.CalGpsPoint();
			if (newGPSCoor == null) {
				Tools.ShowMessageBox(mOwnActivity, "û�л�ȡ��Ч��GPS��λ�������ԣ�");
				return;
			}
			if (this._BaseObject.GetSYS_ID() == -1) // ��������������
			{
				// �����ʹ洢
				if (this.mCurrentLayer.GetLayerType() == lkGeoLayerType.enPoint) {
					int SYS_ID = PubVar.m_DoEvent.m_GPSPoint.SaveGeoToDb(newGPSCoor, "GPS��λ");
					this._BaseObject.SetSYS_ID(SYS_ID);
					if (SYS_ID == -1) {
						OK = false;
					} else {
						mCurrentSysID = SYS_ID;
					}
				}
			}

			if (this.mCurrentLayer.GetLayerType() == lkGeoLayerType.enPolyline) {
				PubVar.m_DoEvent.m_GPSLine.AddPoint(newGPSCoor);
				int SYS_ID = PubVar.m_DoEvent.m_GPSLine.SaveGeoToDb(false);
				this._BaseObject.SetSYS_ID(SYS_ID);
				if (SYS_ID == -1)
					OK = false;
				else {
					mCurrentSysID = SYS_ID;
				}
			}
			if (this.mCurrentLayer.GetLayerType() == lkGeoLayerType.enPolygon) {
				PubVar.m_DoEvent.m_GPSPoly.getGPSLine().AddPoint(newGPSCoor);
				int SYS_ID = PubVar.m_DoEvent.m_GPSPoly.getGPSLine().SaveGeoToDb(false);
				this._BaseObject.SetSYS_ID(SYS_ID);
				if (SYS_ID == -1)
					OK = false;
				else {
					mCurrentSysID = SYS_ID;
				}
			}
		}
		if (mPhotoControl != null) {
			this._BaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
		}

		// ������������
		this._BaseObject.RefreshViewValueToData();
		if (Tools.GetTextValueOnID(mOwnActivity, R.id.evYangdihao) != null
				&& !Tools.GetTextValueOnID(mOwnActivity, R.id.evYangdihao).isEmpty()) {
			PubVar.preYangdihao = Tools.GetTextValueOnID(mOwnActivity, R.id.evYangdihao);
		}

		if (!this._BaseObject.SaveFeatureToDb())
			OK = false;

		if (!OK) {
			Tools.ShowMessageBox(mOwnActivity, "���ݱ���ʧ�ܣ�");
			return;
		}
		// this._DataDialog.dismiss();
		if (this._returnCallback != null)
			this._returnCallback.OnClick("OK", null);
	}

	private boolean SaveXiaoBanSum() {
		boolean isOkay = true;

		if (xiaobanLayerName.isEmpty()) {
			return false;
		}

		String fieldBZDSL = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName)
				.GetDataFieldNameByFieldName("��׼������");
		String empty = "";
		if (fieldBZDSL == null || fieldBZDSL.isEmpty()) {
			empty = "��׼������";
			isOkay = false;
		}

		String fieldBZDZS = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName)
				.GetDataFieldNameByFieldName("��׼������");
		if (fieldBZDZS == null || fieldBZDZS.isEmpty()) {
			empty += ",��׼������";
			isOkay = false;
		}

		String fieldBZDXJ = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName)
				.GetDataFieldNameByFieldName("��׼�����");
		if (fieldBZDXJ == null || fieldBZDXJ.isEmpty()) {
			empty += ",��׼�����";
			isOkay = false;
		}

		String fieldBZDBX = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName)
				.GetDataFieldNameByFieldName("��׼�ر��");
		if (fieldBZDBX == null || fieldBZDBX.isEmpty()) {
			empty += ",��׼�ر��";
			isOkay = false;
		}

		if (!isOkay) {
			Tools.ShowMessageBox("��׼������С��û�С�" + empty + "��" + "�ֶΣ����С��ͼ�������ֶ�");
			return isOkay;
		}

		String sql = "Select * from " + mCurrentLayer.GetLayerID() + "_D where F7='" + xiaobanLayerName + "' and F2='"
				+ Tools.GetTextValueOnID(mOwnActivity, R.id.evXiaobanhao) + "'";

		SQLiteDataReader DR = new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName())
				.Query(sql);
		Double sumXuji = 0d;
		int sumZhuShu = 0;
		int sumCount = 0;
		String allBiaozhundi = "";
		while (DR.Read()) {
			sumXuji += DR.GetDouble("F6");
			sumZhuShu += DR.GetInt32("F5");
			allBiaozhundi += DR.GetString("F3") + "��";
			sumCount++;
		}
		DR.Close();

		sql = "update " + xiaobanLayerName + "_D set " + fieldBZDSL + "='" + sumCount + "', " + fieldBZDZS + "='"
				+ sumZhuShu + "', " + fieldBZDXJ + "='" + String.valueOf(sumXuji) + "', " + fieldBZDBX + "='"
				+ allBiaozhundi + "' where "
				+ PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName)
						.GetDataFieldNameByFieldName(xiaobanField)
				+ "= '" + Tools.GetTextValueOnID(mOwnActivity, R.id.evXiaobanhao) + "'";
		return new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName())
				.ExcuteSQL(sql);
	}

	/**
	 * ����ʵ��༭��Ϣ�����SYS_ID=-1��ʾ����������Ϊ�޸�
	 * 
	 * @param layerID
	 * @param SYS_ID
	 */

	private void initGeneralForm(String layerID, int SYS_ID, Dataset pDataset) {

		this.CreateForm(this.mCurrentLayer);

		// this.SetShowGpsAveragePointOption();

		// ��ʾ�༭״̬
		if (SYS_ID != -1) {
			// ʵ���ڲ�������Ϣ
			Geometry pGeometry = pDataset.GetGeometry(SYS_ID);

			if (pGeometry == null) {
				List<String> SYSIDList = new ArrayList<String>();
				SYSIDList.add(SYS_ID + "");
				List<Geometry> pGeometryList = pDataset.QueryGeometryFromDB1(SYSIDList);
				if (pGeometryList.size() != 0)
					pGeometry = pGeometryList.get(0);

			}

			if (pGeometry != null) {
				mCenterPoint = pGeometry.getCenterPoint();
				if (this.mCurrentLayer.GetLayerType() == lkGeoLayerType.enPoint) {
					TextView TV = (TextView) this._FieldInnerFeauterViewList.get(0).FieldView;
					Coordinate Coor = ((Point) pGeometry).getCoordinate();
					CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
					if (CS.GetName().equals("WGS-84����")) {
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

	public void SetEditInfo(String layerID, int SYS_ID) {
		this.mCurrentLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID);
		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(this.mCurrentLayer.GetLayerID());

		if (this.mCurrentLayer.GetLayerProjecType() != null && this.mCurrentLayer.GetLayerProjecType().contains("̼��")) {
			PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().CheckAndCreateTanhuiTable();
			isTanhui = true;
			this.CreateTanhuiForm(this.mCurrentLayer, SYS_ID);
		} else {
			initGeneralForm(layerID, SYS_ID, pDataset);
		}

		// ��ʼ��ʵ�岢���ð���Ŀ
		this._BaseObject = new LDBGDataObject();
		this._BaseObject.SetDataset(pDataset);
		// if (this._Layer.GetIfLabel())
		// this._BaseObject.SetLabelFieldName(this._Layer.GetLabelField());
		// //���ñ�ע�ֶ�����

		this._BaseObject.SetSYS_ID(SYS_ID); // ����SYS_ID

		for (FieldView FV : this._FieldNameViewList) {
			this._BaseObject.AddDataBindItem(new DataBindOfKeyValue(FV.FieldName, FV.DataFieldName, "", FV.FieldView));
		}

		this._BaseObject.ReadDataAndBindToView("SYS_ID=" + SYS_ID, layerID, mCurrentLayer.GetLayerProjecType()); // ��ȡ���ݲ�����״̬
		// this._DataDialog.SetGpsBasePointObject(this._BaseObject);
		// this._DataDialog.UpdateDialogShowInfo(); //���½�����ʾ

		Geometry pGeometry = pDataset.GetGeometry(SYS_ID);
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

			String scale = PubVar.m_HashMap.GetValueObject("Tag_System_MapScale", false).Value;
			if (etTFH != null) {
				if (scale.isEmpty()) {
					this.etTFH.setText(Tools.CalcTuFuHao(middle, "1:1��"));
				} else {
					this.etTFH.setText(Tools.CalcTuFuHao(middle, scale));
				}
			}

			if (etDiShi != null && etDiShi.getText().length() == 0) {
				etDiShi.setText(mCurrentLayer.getCity());
			}
			if (etQuXian != null && etQuXian.getText().length() == 0) {
				etQuXian.setText(mCurrentLayer.getCounty());
			}
			if (etXBMJ != null && mCurrentLayer.GetLayerType() == lkGeoLayerType.enPolygon) {
				etXBMJ.setText(Tools.ReSetArea(((Polygon) pGeometry).getArea(true), true));
			}

		}

		// ��ȡ̼��ѡ�е�С����Ϣ
		if (isTanhui) {
			if (Tools.GetTextValueOnID(mOwnActivity, R.id.evXiaobanhao).isEmpty()) {
				int selectedID = 0;
				for (GeoLayer layer : PubVar.m_MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorEditingData)
						.getList()) {
					if (layer.getType() == lkGeoLayerType.enPolygon) {
						if (layer.getSelSelection().getCount() == 1) {
							selectedID = layer.getSelSelection().getGeometryIndexList().get(0);
							xiaobanLayerName = layer.getId();
						}
					}
				}
				;

				if (!xiaobanLayerName.isEmpty()) {
					DataSource dc = new DataSource(
							PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName());
					String fieldXB = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName)
							.GetDataFieldNameByFieldName(xiaobanField);
					if (fieldXB != null && !fieldXB.isEmpty()) {
						String Xiaobanhao = dc.QueryDataFieldValue(fieldXB, selectedID, xiaobanLayerName + "_D");
						Tools.SetTextViewValueOnID(mOwnActivity, R.id.evXiaobanhao, Xiaobanhao);

						this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("С���", "F7", xiaobanLayerName, null));
					}
				}
			} else {
				DataSource dc = new DataSource(
						PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName());
				String fieldXB = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID)
						.GetDataFieldNameByFieldName("С���");
				if (fieldXB != null && !fieldXB.isEmpty()) {
					xiaobanLayerName = dc.QueryDataFieldValue(fieldXB, mCurrentSysID, layerID + "_D");

					this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("С���", "F7", xiaobanLayerName, null));
				}
			}

			if (Tools.GetTextValueOnID(mOwnActivity, R.id.evYangdihao) == null
					|| Tools.GetTextValueOnID(mOwnActivity, R.id.evYangdihao).isEmpty()) {
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.evYangdihao, PubVar.preYangdihao);
			}
		}

	}

	private void loadMeiMuJianChiList() {
		// ��ͼ���б�
		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		hvf.SetHeaderListView(mOwnActivity.findViewById(R.id.in_listview), "ÿľ��߱�", tanhuiCallback);

		// ��߱���ֶ��б�
		ArrayList<MeiMuJianChi> lyrFieldList = new ArrayList<MeiMuJianChi>();

		List<HashMap<String, Object>> jianChiList = new ArrayList<HashMap<String, Object>>();
		String sql = "select * from " + mCurrentLayer.GetLayerID() + "_D where SYS_ID =" + mCurrentSysID;

		SQLiteDataReader DR = new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName())
				.Query(sql);
		String jianchiValue = "";
		if (DR.Read()) {

			for (int i = PubVar.MinTanhuiIndex; i < PubVar.maxTanhuiIndex; i++) {
				String fi = DR.GetString("F" + i);
				if (!fi.isEmpty()) {
					jianchiValue += fi;
				}

			}

			String[] all = jianchiValue.split(";");
			for (String per : all) {
				try {
					HashMap<String, Object> hm = new HashMap<String, Object>();
					String[] properties = per.split(",");
					if (properties.length > 1) {
						maxCode = maxCode > Integer.valueOf(properties[0]) ? maxCode : Integer.valueOf(properties[0]);
						hm.put("D1", properties[0]);
						hm.put("D2", properties[1]);
						hm.put("D3", properties[2]);
						hm.put("D4", properties[3]);
						hm.put("D5", properties[4]);
						jianChiList.add(hm);
					} else {
						maxCode = 0;
					}

				} catch (Exception e) {
					Tools.ShowToast(mOwnActivity, e.getMessage());
				}
			}
		}
		DR.Close();
		hvf.BindDataToListView(jianChiList);
	}

	private String selectCode = "";
	private HashMap<String, Object> selectObj = null;
	// ��ť�¼�
	private ICallback tanhuiCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			// ѡ���ֶ��б��Ļص�
			if (Str.equals("�б�ѡ��")) {
				selectObj = (HashMap<String, Object>) ExtraStr;
				selectCode = selectObj.get("D1").toString();
				// List<v1_LayerField> lyrFieldList =
				// m_EditLayer.GetFieldList();
				// for(v1_LayerField Field:lyrFieldList)
				// {
				// if
				// (Field.GetFieldName().equals(FieldName))m_SelectField=Field;
				// }
				SetButtonEnable(true);
			}

		}
	};

	// ���ð�ť��״̬
	private void SetButtonEnable(boolean enabled) {
		mOwnActivity.findViewById(R.id.pln_edit).setEnabled(enabled);
		mOwnActivity.findViewById(R.id.pln_delete).setEnabled(enabled);
		((TextView) mOwnActivity.findViewById(R.id.tv_edit)).setTextColor((enabled ? Color.BLACK : Color.GRAY));
		((TextView) mOwnActivity.findViewById(R.id.tv_delete)).setTextColor((enabled ? Color.BLACK : Color.GRAY));
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
		if (StrCommand.equals("���Լ�¼")) {
			viewPager.setCurrentItem(0);
		}

		if (StrCommand.equals("��Ƭ")) {
			viewPager.setCurrentItem(1);
		}

		if (StrCommand.equals("���Լ�¼")) {
			viewPager.setCurrentItem(0);
		}

		if (StrCommand.equals("����")) {
			HiddenView();
		}

		if (StrCommand.equals("����")) {
			lkmap.Tools.Tools.OpenDialog("���ڱ�������...", new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					Save();
					if (isTanhui && xiaobanLayerName != null && !xiaobanLayerName.isEmpty()) {
						if (!SaveXiaoBanSum()) {
							Tools.ShowMessageBox("����С��̼�������Ϣʧ��");
						}

						Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mCurrentLayer.GetLayerID());
						if (pDataset == null)
							return;
						if (mCurrentLayer.GetLayerTypeName().equals("��"))
							PubVar.m_DoEvent.m_GPSPoint.SetDataset(pDataset);
						if (mCurrentLayer.GetLayerTypeName().equals("��"))
							PubVar.m_DoEvent.m_GPSLine.SetDataset(pDataset);
						if (mCurrentLayer.GetLayerTypeName().equals("��"))
							PubVar.m_DoEvent.m_GPSPoly.SetDataset(pDataset);
						PubVar.m_Map.FastRefresh();
					}
				}
			});

		}

		if (StrCommand.equals("����")) {
			Save();

			TanHuiJianChiEdit thEdit = new TanHuiJianChiEdit();
			thEdit.SetCurrentLayer(mCurrentLayer);
			thEdit.SetSysID(mCurrentSysID);

			if (Tools.GetTextValueOnID(mOwnActivity, R.id.evYangdihao).isEmpty()) {
				Tools.ShowMessageBox("���غŲ���Ϊ��");
				return;
			}
			thEdit.SetYangDiHao(Tools.GetTextValueOnID(mOwnActivity, R.id.evYangdihao));
			if (Tools.GetTextValueOnID(mOwnActivity, R.id.evXiaobanhao).isEmpty()) {
				Tools.ShowMessageBox("С��Ų���Ϊ��");
				return;
			}
			thEdit.SetXiaoBanHao(Tools.GetTextValueOnID(mOwnActivity, R.id.evXiaobanhao));
			if (Tools.GetTextValueOnID(mOwnActivity, R.id.evBiaozhundihao).isEmpty()) {
				Tools.ShowMessageBox("��׼�غŲ���Ϊ��");
				return;
			}
			thEdit.SetBiaoZhunDihao(Tools.GetTextValueOnID(mOwnActivity, R.id.evBiaozhundihao));

			// int code =
			// PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetMaxJianChiCode(Tools.GetTextValueOnID(_DataDialog,
			// R.id.evYangdihao),
			// Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao),
			// Tools.GetTextValueOnID(_DataDialog, R.id.evBiaozhundihao));

			thEdit.SetCode(maxCode + 1);

			// plf.SetEditLayer(this.m_EditLayer);
			// plf.SetCallback(pCallback); //�ֶβ�����ɺ�Ļص� ���ص���־���ֶ�
			thEdit.SetCallback(pCallback);
			thEdit.ShowDialog();

		}

		if (StrCommand.equals("��������")) {
			String fileName = Tools.GetTextValueOnID(mOwnActivity, R.id.evYangdihao) + "-"
					+ Tools.GetTextValueOnID(mOwnActivity, R.id.evXiaobanhao) + "-"
					+ Tools.GetTextValueOnID(mOwnActivity, R.id.evBiaozhundihao) + ".CSV";
			// exportOneTanhui(mCurrentLayer,mCurrentSysID,fileName);
			PubVar.m_DoEvent.exportOneTanhui(mCurrentLayer, mCurrentSysID, fileName);
		}

		if (StrCommand.equals("�޸�")) {
			if (!this.selectCode.isEmpty()) {
				TanHuiJianChiEdit thEdit = new TanHuiJianChiEdit();
				thEdit.SetCurrentLayer(mCurrentLayer);
				thEdit.SetSysID(mCurrentSysID);

				thEdit.SetCode(Integer.valueOf(selectObj.get("D1").toString()));
				thEdit.SetEditMode("edit", selectObj.get("D2").toString(), selectObj.get("D4").toString(),
						selectObj.get("D5").toString());

				if (Tools.GetTextValueOnID(mOwnActivity, R.id.evYangdihao).isEmpty()) {
					Tools.ShowMessageBox("���غŲ���Ϊ��");
					return;
				}
				thEdit.SetYangDiHao(Tools.GetTextValueOnID(mOwnActivity, R.id.evYangdihao));
				if (Tools.GetTextValueOnID(mOwnActivity, R.id.evXiaobanhao).isEmpty()) {
					Tools.ShowMessageBox("С��Ų���Ϊ��");
					return;
				}
				thEdit.SetXiaoBanHao(Tools.GetTextValueOnID(mOwnActivity, R.id.evXiaobanhao));
				if (Tools.GetTextValueOnID(mOwnActivity, R.id.evBiaozhundihao).isEmpty()) {
					Tools.ShowMessageBox("��׼�غŲ���Ϊ��");
					return;
				}
				thEdit.SetBiaoZhunDihao(Tools.GetTextValueOnID(mOwnActivity, R.id.evBiaozhundihao));

				// plf.SetEditLayer(this.m_EditLayer);
				// plf.SetCallback(pCallback); //�ֶβ�����ɺ�Ļص� ���ص���־���ֶ�
				thEdit.SetCallback(pCallback);
				thEdit.ShowDialog();
			}
		}

		if (StrCommand.equals("ɾ���ֶ�")) {
			Tools.ShowYesNoMessage(mOwnActivity, Tools.ToLocale("�Ƿ�ɾ�����") + "��" + selectCode + "������ľ������ݣ�",
					new ICallback() {

						@Override
						public void OnClick(String Str, Object ExtraStr) {

							if (Str.endsWith("YES")) {
								String sql = "select * from " + mCurrentLayer.GetDataTableName() + " where SYS_ID ="
										+ mCurrentSysID;
								SQLiteDataReader DR = new DataSource(
										PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName())
												.Query(sql);
								String jianchiValue = "";
								String updateSql = "";
								int zhushu = 0;
								Double xuji = 0d;

								int index = 0;
								boolean isFind = false;
								if (DR.Read()) {
									for (int i = PubVar.MinTanhuiIndex; i < PubVar.maxTanhuiIndex; i++) {
										jianchiValue = DR.GetString("F" + i);
										if (jianchiValue.isEmpty()) {
											continue;
										}

										String[] all = jianchiValue.split(";");

										String newString = "";

										for (String per : all) {
											String[] properties = per.split(",");

											if (properties[0].equals(selectCode)) {
												isFind = true;
												index = i;
											} else {
												zhushu++;
												maxCode = zhushu;
												xuji += Double.valueOf(properties[4]);
												if (!isFind) {
													newString += per + ";";
												} else {
													newString += zhushu + ",";
													newString += properties[1] + "," + properties[2] + ","
															+ properties[3] + "," + properties[4] + ";";

												}

											}
										}

										if (isFind) {
											updateSql += " F" + i + "= '" + newString + "', ";
										}

									}
								}
								DR.Close();
								if (!updateSql.isEmpty()) {
									String updateSumSql = "update " + mCurrentLayer.GetDataTableName() + " set"
											+ updateSql + " F5=" + zhushu + ", F6=" + xuji + " where SYS_ID = "
											+ mCurrentSysID;
									if (new DataSource(
											PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName())
													.ExcuteSQL(updateSumSql)) {
										SetEditInfo(mCurrentLayer.GetLayerID(), mCurrentSysID);
									}
								}

							}
						}
					});

		}

	}

	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			SetEditInfo(mCurrentLayer.GetLayerID(), mCurrentSysID);
		}
	};

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

	class MeiMuJianChi {
		public int code;
		public String shuzhong;
		public float xiongjing;
		public float xujiliang;
		public String yangdihao;

		public MeiMuJianChi() {
		}

		public String getID() {
			return yangdihao + code;
		}

		public float GetXujiliang() {
			return 0f;
		}

	}

}
