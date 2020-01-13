package com.dingtu.SLDuCha;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DuChaDB;
import com.dingtu.Funtion.PhotoControl;
import com.dingtu.senlinducha.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.DataBindOfKeyValue;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Polygon;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Layer.GeoLayer;
import lkmap.Layer.GeoLayers;
import lkmap.Spatial.SpatialAnalysisTools;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.LayerManger;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;
import lkmap.ZRoadMap.ToolsBox.v1_UserConfigDB_PolyAnalysisOption;

public class TuBanYanZheng {

	public static final String TAG = "duchasetting";

	public static final String JCJB = "duchajb";
	public static final String JCDW = "duchadw";
	public static final String JCRY = "duchary";

	private v1_Layer mLayer;
	private int mObjId = 0;
	private Activity mOwnActivity = null;
	private DuChaDataObject _BaseObject = null;
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private PhotoControl mPhotoControl;
	private ViewPager viewPager;
	private boolean fristPageOne = true;
	private boolean firstPageTwo = true;
	private View mView;
	private String mTubanhao;
	private String mTubanDataField;
	private String mXianDataField;
	private List<Integer> mAllObjIds;
	private String mXian;

	public TuBanYanZheng(String layerId, int dataId, List<Integer> allObjIds) {
		mAllObjIds = allObjIds;
		mObjId = dataId;
		mLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerId);
		mOwnActivity = (Activity) PubVar.m_DoEvent.m_Context;
		mView = ((Activity) PubVar.m_DoEvent.m_Context).findViewById(R.id.ll_duchayanzheng);
		mView.setVisibility(View.VISIBLE);
		initViewPager();
		initButtonEvent();
		initDuChaView();
		
	}

	public TuBanYanZheng(String layerId, int dataId) {
		mObjId = dataId;
		mLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerId);
		mOwnActivity = (Activity) PubVar.m_DoEvent.m_Context;
		mView = ((Activity) PubVar.m_DoEvent.m_Context).findViewById(R.id.ll_duchayanzheng);
		mView.setVisibility(View.VISIBLE);
		mAllObjIds = new ArrayList<Integer>();
		mAllObjIds.add(dataId);
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				initViewPager();
				initButtonEvent();
				initDuChaView();
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
					
					initReleatedView();
					}
				}, 100);
			}
		});
	}

	private void initButtonEvent() {
		mView.findViewById(R.id.dcyz_quit).setOnClickListener(new ViewClick());
		mView.findViewById(R.id.dcyz_save).setOnClickListener(new ViewClick());
		mView.findViewById(R.id.bt_vp_tbyz).setOnClickListener(new ViewClick());
		mView.findViewById(R.id.bt_vp_tbyzphoto).setOnClickListener(new ViewClick());

	}

	@SuppressLint("NewApi")
	private void initTuBanValues() {
		// String dilei = "1-�ֵ�,2-���ֵ�";
		// Spinner spQDL = (Spinner) mView.findViewById(R.id.sp_qiandilei);
		// ArrayAdapter<String> dileiAdapter = new
		// ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
		// android.R.layout.simple_spinner_item, dilei.split(","));
		// dileiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// // spQDL.setLayoutMode(Spinner.MODE_DROPDOWN);
		// spQDL.setAdapter(dileiAdapter);
		//
		// Spinner spXDL = (Spinner) mView.findViewById(R.id.sp_xiandilei);
		// spXDL.setLayoutMode(Spinner.MODE_DROPDOWN);
		// spXDL.setAdapter(dileiAdapter);
		//
		// String bhyy =
		// "1-������Ŀʹ���ֵ�,2-ֱ��Ϊ��ҵ��������,3-���֣�ʪ������,4-��������,5-��ľ�ɷ�,6-���֡�����������ɭ�־�Ӫ�,7-ɭ�ֲ��溦��ɭ�ֻ���,8-�����ֺ�����Ȼԭ��,9-ֲ�＾���Ա仯,10-ң����������,11-ǰ�����Ƿ��ֵ�
		// ,12-����ԭ��";
		// Spinner spBhyy = (Spinner) mView.findViewById(R.id.sp_tbbhyy);
		// ArrayAdapter<String> bhyyAdapter = new
		// ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
		// android.R.layout.simple_spinner_item, bhyy.split(","));
		// bhyyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// // spBhyy.setLayoutMode(Spinner.MODE_DROPDOWN);
		// spBhyy.setAdapter(bhyyAdapter);
		//
		// String jcjb = "1-�ؼ�, 2-�м�,3-ʡ��,4-ֱ��Ժ,5-רԱ��";
		// Spinner spJcjb = (Spinner) mView.findViewById(R.id.sp_jcjb);
		// ArrayAdapter<String> jcjbAdapter = new
		// ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
		// android.R.layout.simple_spinner_item, jcjb.split(","));
		// jcjbAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// // spJcjb.setLayoutMode(Spinner.MODE_DROPDOWN);
		// spJcjb.setAdapter(jcjbAdapter);
		// spJcjb.setOnItemSelectedListener(new OnItemSelectedListener(){
		//
		// @Override
		// public void onItemSelected(AdapterView<?> parent, View view, int
		// position, long id) {
		// if(position == 0)
		// {
		// try
		// {
		// Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_jcjgsfyz, "3-�ؼ����");
		// }
		// catch(Exception ex)
		// {
		//
		// }
		//
		// }
		//
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> parent) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// });
		//
		// String jcjg = "1-һ��,2-��һ��,3-�ؼ����";
		// Spinner spJcjg = (Spinner) mView.findViewById(R.id.sp_jcjgsfyz);
		// ArrayAdapter<String> jcjgAdapter = new
		// ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
		// android.R.layout.simple_spinner_item, jcjg.split(","));
		// jcjgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// // spJcjg.setLayoutMode(Spinner.MODE_DROPDOWN);
		// spJcjg.setAdapter(jcjgAdapter);

		String bhyy = ",411-������Ŀʹ���ֵ�_������_����ʹ��,412-������Ŀʹ���ֵ�_������_��ʱռ��,413-������Ŀʹ���ֵ�_������_Ϊ��ҵ��������,421-������Ŀʹ���ֵ�_δ����_����ʹ��"
				+ ",422-������Ŀʹ���ֵ�_δ����_��ʱռ��,423-������Ŀʹ���ֵ�_δ����_Ϊ��ҵ��������,51-����(ʪ)����-��ֲ,52-����(ʪ)����-��������,21-��ľ�ɷ�_������"
				+ ",22-��ľ�ɷ�_δ����,11-�˹�/�ɲ�����,12-�˹�����,30-�滮����,60-ɭ�ָ���,71-����,72-�����ֺ�,73-�����ֺ�����,81-��ɽ����,82-������Ȼ����,"
				+ "91-����ԭ���µĵ���仯,94-��׼����,95-���߱仯_���ݿ�����,96-���߱仯_�������߱仯,97-���߱仯_��Ӫ�ֳ��ȹ�����߱仯,98-���߱仯_��Ȼ������ɭ�ֹ�԰�ȹ�����߱仯"
				+ ",99-����δ�仯���������ӱ仯";

		Spinner spBhyy = (Spinner) mView.findViewById(R.id.sp_bhyy);
		ArrayAdapter<String> bhyyAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, bhyy.split(","));
		bhyyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// spBhyy.setLayoutMode(Spinner.MODE_DROPDOWN);
		spBhyy.setAdapter(bhyyAdapter);

		String syldxz = ",1-δ����ҵ�Ͳ�ԭ���ܲ������,2-ԽȨ���,3- �����ʹ��,4-δ����;ʹ��,5-������ռ��,6-��������ʹ���ֵ�";

		Spinner spSyldxz = (Spinner) mView.findViewById(R.id.sp_syldxz);
		ArrayAdapter<String> syldxzAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, syldxz.split(","));
		syldxzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// spBhyy.setLayoutMode(Spinner.MODE_DROPDOWN);
		spSyldxz.setAdapter(syldxzAdapter);

		String jcjb = "���Ҽ�,ʡ��,�ؼ�,����";
		Spinner spJcjb = (Spinner) mView.findViewById(R.id.sp_zrbhdjb);
		ArrayAdapter<String> jcjbAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, jcjb.split(","));
		jcjbAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spJcjb.setAdapter(jcjbAdapter);

		String jclx = ",1-��Ȼ������,2-ɭ�ֹ�԰ ,3-ʪ�ع�԰, 4-�羰��ʤ��, 5-���ʹ�԰, 6-����԰, 7-������Ȼ�Ų���, 8-������Ҫʪ��, 9-����, 10-���ҹ�԰";
		Spinner spJclx = (Spinner) mView.findViewById(R.id.sp_zrbhdlx);
		ArrayAdapter<String> bxdlxAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, jclx.split(","));
		bxdlxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spJclx.setAdapter(bxdlxAdapter);

		String xdl = ",0100-����,0101-ˮ��,0102-ˮ����,0103-����,0200-��ֲ԰��,0201-��԰,0202-��԰,0203-��԰,0204-����԰��,0301-��ľ�ֵ�,0301K-�ɵ�����ľ�ֵ�,0302-���ֵ�,"
				+ "0302K-�ɵ������ֵ�,0305-��ľ�ֵ�,0308-���ֵ�,0309-δ���ֵ�,0310-����,0311-���Ե�,0400-�ݵ�,0401-��Ȼ���ݵ�,0403-�˹����ݵ�,0404-�����ݵ�,0300-ʪ��,0303-�����ֵ�,0304-ɭ������,"
				+ "0306-�������,0402-����ݵ�,0603-����,1105-�غ�̲Ϳ,1106-��½̲Ϳ,1108-�����,0500-��ҵ����ҵ�õ�,05H1-��ҵ����ҵ�õ�,0508-�����ִ��õ�,0600-�����õ�,0601-��ҵ�õ�,0602-�ɿ��õ�,"
				+ "07-סլ�õ�,0701-����סլ�õ�,0702-ũ��լ����,08-��������������õ�,08H1-�����������ų����õ�,08H2-�ƽ������õ�,0809-������ʩ�õ�,"
				+ "0810-��԰���̵�,0900-�����õ�,1000-��ͨ�����õ�,1001-��·�õ�,1002-�����ͨ�õ�,1003-��·�õ�,1004-������·�õ�,1005-��ͨ����վ�õ�,"
				+ "1006-ũ���·,1007-�����õ�,1008-�ۿ���ͷ�õ�,1009-�ܵ������õ�,1100-ˮ��ˮ����ʩ�õ�,1101-����ˮ��,1102-����ˮ��,1103-ˮ��ˮ��,1104-ˮ��ˮ��,"
				+ "1107-����,1109-ˮ�������õ�,1110-���������û�ѩ,1200-�����õ�,1201-���е�,1202-��ʩũ�õ�,1203-�￲,1204-�μ��,1205-ɳ��,1206-������,1207-����ʯ����,250-�����õ�";
		Spinner spxzdl = (Spinner) mView.findViewById(R.id.sp_zxdl);
		ArrayAdapter<String> xzdlAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, xdl.split(","));
		xzdlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spxzdl.setAdapter(xzdlAdapter);

	}

	private void initViewPager() {
		viewContainter
				.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.layout_duchatuban_jcyz, null));
		viewContainter
				.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.v1_data_template_photo, null));
		titleContainer.add("ͼ����֤");
		titleContainer.add("��Ƭ");

		viewPager = (ViewPager) mView.findViewById(R.id.TBYZviewPager);

		viewPager.setAdapter(new PagerAdapter() {
			// viewpager�е��������
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

				if (position == 0 && fristPageOne) {
					initTuBanValues();
					fristPageOne = false;
					// calcXY();
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
					if (mPhotoControl == null) {
						initPhotoPager();

					}

					mPhotoControl.setWaterWaterMark(true);
					mPhotoControl.setNeedTuban(true);
					setWaterMarkValue();

					// mPhotoControl.setWaterWaterMark(mLayer.GetShowWaterMark());

				}
			}
		});
	}

	private void setWaterMarkValue() {
		String tubanhao = (((TextView) mView.findViewById(R.id.et_tubanhao))).getText().toString();
		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
		Geometry pGeometry = pDataset.GetGeometry(mObjId);
		DecimalFormat df = new DecimalFormat("0.0");

		mPhotoControl.SetXiaoBanInfo(df.format(pGeometry.getCenterPoint().getX()),
				df.format(pGeometry.getCenterPoint().getY()), tubanhao);
	}

	public class ViewClick implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			if (Tag.equals("����")) {
				mView.setVisibility(View.GONE);
			}
			if (Tag.equals("����")) {
				saveData();
				mView.setVisibility(View.GONE);
			}
			if (Tag.equals("ͼ����֤")) {
				viewPager.setCurrentItem(0);
			}
			if (Tag.equals("��Ƭ")) {
				viewPager.setCurrentItem(1);
				_BaseObject.RefreshViewValueToData();
			}
		}
	}

	private void initDuChaView() {
		EditText spPDBHYY = (EditText) mView.findViewById(R.id.et_pdbhyy);
		Log.e("initDuCha", spPDBHYY.getText().toString());
		if (spPDBHYY.getText().toString().equals("10") || spPDBHYY.getText().toString().equals("20")
				|| spPDBHYY.getText().toString().equals("30")) {
			mView.findViewById(R.id.layoyt_duchaheshi).setVisibility(View.VISIBLE);
		} else {
			mView.findViewById(R.id.layoyt_duchaheshi).setVisibility(View.GONE);
		}
		
	}

	private void initBaseObject() {
		if (_BaseObject == null) {
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
			this._BaseObject = new DuChaDataObject();
			this._BaseObject.SetDataset(pDataset);
			this._BaseObject.SetSYS_ID(mObjId);
			this._BaseObject.setWaterMarkKey(mLayer.GetWaterMarkDataFieldStr());

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("ʡ", mLayer.GetDataFieldNameByFieldName("ʡ"), "",
					mView.findViewById(R.id.et_province)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��", mLayer.GetDataFieldNameByFieldName("��"), "",
					mView.findViewById(R.id.et_xian)));
			// _BaseObject.AddDataBindItem(new DataBindOfKeyValue("�������",
			// mLayer.GetDataFieldNameByFieldName("�������"),
			// mView.findViewById(R.id.et_dcnd)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("����", mLayer.GetDataFieldNameByFieldName("����"), "",
					mView.findViewById(R.id.et_xz)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��", mLayer.GetDataFieldNameByFieldName("��"), "",
					mView.findViewById(R.id.et_cun)));

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��ҵ��", mLayer.GetDataFieldNameByFieldName("��ҵ��"), "",
					mView.findViewById(R.id.et_lyj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("�ֳ�", mLayer.GetDataFieldNameByFieldName("�ֳ�"), "",
					mView.findViewById(R.id.et_linchang)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("�ְ�", mLayer.GetDataFieldNameByFieldName("�ְ�"), "",
					mView.findViewById(R.id.et_linban)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ��", mLayer.GetDataFieldNameByFieldName("Υ��Υ��"), "",
					mView.findViewById(R.id.cb_sfwfwg)));
			mTubanDataField = mLayer.GetDataFieldNameByFieldName("�ж�ͼ�߱��");
			_BaseObject.AddDataBindItem(
					new DataBindOfKeyValue("�ж�ͼ�߱��", mTubanDataField, "", mView.findViewById(R.id.et_tubanhao)));

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("�ֵع���λ", mLayer.GetDataFieldNameByFieldName("�ֵع���λ"),
					"", mView.findViewById(R.id.et_ldgldw)));

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��ʵϸ�ߺ�", mLayer.GetDataFieldNameByFieldName("��ʵϸ�ߺ�"), "",
					mView.findViewById(R.id.et_hsxbh)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("������", mLayer.GetDataFieldNameByFieldName("������"), "",
					mView.findViewById(R.id.et_X)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("������", mLayer.GetDataFieldNameByFieldName("������"), "",
					mView.findViewById(R.id.et_Y)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("�ж����", mLayer.GetDataFieldNameByFieldName("�ж����"), "",
					mView.findViewById(R.id.et_tbpdmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("�ж�����", mLayer.GetDataFieldNameByFieldName("�ж�����"), "",
					mView.findViewById(R.id.et_pddl)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("�ж��仯ԭ��", mLayer.GetDataFieldNameByFieldName("�ж��仯ԭ��"),
					"", mView.findViewById(R.id.et_pdbhyy)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("�ж���ע", mLayer.GetDataFieldNameByFieldName("�ж���ע"), "",
					mView.findViewById(R.id.et_pdbz)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("ǰ����", mLayer.GetDataFieldNameByFieldName("ǰ����"), "",
					mView.findViewById(R.id.et_qdl)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��״����", mLayer.GetDataFieldNameByFieldName("��״����"), "",
					mView.findViewById(R.id.sp_zxdl)));
			
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("�仯ԭ��", mLayer.GetDataFieldNameByFieldName("�仯ԭ��"), "",
					mView.findViewById(R.id.sp_bhyy)));

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��Ŀ����", mLayer.GetDataFieldNameByFieldName("��Ŀ����"), "",
					mView.findViewById(R.id.et_xmhfqmc)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("����ĺ�", mLayer.GetDataFieldNameByFieldName("����ĺ�"), "",
					mView.findViewById(R.id.et_spwh)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("������", mLayer.GetDataFieldNameByFieldName("������"), "",
					mView.findViewById(R.id.et_spnd)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("������", mLayer.GetDataFieldNameByFieldName("������"), "",
					mView.findViewById(R.id.et_spmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("ʵ�ʸı��ֵ���;���",
					mLayer.GetDataFieldNameByFieldName("ʵ�ʸı��ֵ���;���"), "", mView.findViewById(R.id.et_sjgbldytmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ���ı��ֵ���;���",
					mLayer.GetDataFieldNameByFieldName("Υ��Υ���ı��ֵ���;���"), "", mView.findViewById(R.id.et_wfwgcflmxj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ������Ȼ���������",
					mLayer.GetDataFieldNameByFieldName("Υ��Υ������Ȼ���������"), "", mView.findViewById(R.id.et_zrbhdmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��Ȼ����������", mLayer.GetDataFieldNameByFieldName("��Ȼ����������"),
					"", mView.findViewById(R.id.et_zrbhdmc)));
			// _BaseObject.AddDataBindItem(new DataBindOfKeyValue("��Ȼ�����ؼ���",
			// mLayer.GetDataFieldNameByFieldName("��Ȼ�����ؼ���"),
			// "", mView.findViewById(R.id.sp_zrbhdjbjlx)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ������ľ�ֵ����",
					mLayer.GetDataFieldNameByFieldName("Υ��Υ������ľ�ֵ����"), "", mView.findViewById(R.id.et_wfwgzqmlmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ�����������",
					mLayer.GetDataFieldNameByFieldName("Υ��Υ�����������"), "", mView.findViewById(R.id.et_wfwgzzldmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ���к��������",
					mLayer.GetDataFieldNameByFieldName("Υ��Υ���к��������"), "", mView.findViewById(R.id.et_wfwgzhslmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ���й����ع������",
					mLayer.GetDataFieldNameByFieldName("Υ��Υ���й����ع������"), "", mView.findViewById(R.id.et_wfwgzgjtglmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ����������ľ�����",
					mLayer.GetDataFieldNameByFieldName("Υ��Υ����������ľ�����"), "", mView.findViewById(R.id.et_wfwgzqtgmlmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ���������ֵ����",
					mLayer.GetDataFieldNameByFieldName("Υ��Υ���������ֵ����"), "", mView.findViewById(R.id.et_wfwgzqtldmj)));
			_BaseObject.AddDataBindItem(
					new DataBindOfKeyValue("Υ��Υ����һ�����ҹ��������", mLayer.GetDataFieldNameByFieldName("Υ��Υ����һ�����ҹ��������"), "",
							mView.findViewById(R.id.et_wfwgzyjgjgylmj)));
			_BaseObject.AddDataBindItem(
					new DataBindOfKeyValue("Υ��Υ���ж������ҹ��������", mLayer.GetDataFieldNameByFieldName("Υ��Υ���ж������ҹ��������"), "",
							mView.findViewById(R.id.et_wfwgzejgjgylmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ���еط����������",
					mLayer.GetDataFieldNameByFieldName("Υ��Υ���еط����������"), "", mView.findViewById(R.id.et_wfwgzdfgylmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("Υ��Υ������Ʒ�����",
					mLayer.GetDataFieldNameByFieldName("Υ��Υ������Ʒ�����"), "", mView.findViewById(R.id.et_wfwgzsplmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("ʹ���ֵ�����", mLayer.GetDataFieldNameByFieldName("ʹ���ֵ�����"),
					"", mView.findViewById(R.id.sp_syldxz)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��ľ�ɷ����֤��",
					mLayer.GetDataFieldNameByFieldName("��ľ�ɷ����֤��"), "", mView.findViewById(R.id.et_lmcfxkzh)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��֤���", mLayer.GetDataFieldNameByFieldName("��֤���"), "",
					mView.findViewById(R.id.et_cfzfzmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��֤���", mLayer.GetDataFieldNameByFieldName("��֤���"), "",
					mView.findViewById(R.id.et_cfzfzxj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("ƾ֤�ɷ����", mLayer.GetDataFieldNameByFieldName("ƾ֤�ɷ����"),
					"", mView.findViewById(R.id.et_pzcfmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("ƾ֤�ɷ����", mLayer.GetDataFieldNameByFieldName("ƾ֤�ɷ����"),
					"", mView.findViewById(R.id.et_pzcfxj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��֤�ɷ����", mLayer.GetDataFieldNameByFieldName("��֤�ɷ����"),
					"", mView.findViewById(R.id.et_czcfmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��֤�ɷ����", mLayer.GetDataFieldNameByFieldName("��֤�ɷ����"),
					"", mView.findViewById(R.id.et_czcfxj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��֤�ɷ����", mLayer.GetDataFieldNameByFieldName("��֤�ɷ����"),
					"", mView.findViewById(R.id.et_wzcfmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��֤�ɷ����", mLayer.GetDataFieldNameByFieldName("��֤�ɷ����"),
					"", mView.findViewById(R.id.et_wzcfxj)));

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("��ע", mLayer.GetDataFieldNameByFieldName("��ע"), "",
					mView.findViewById(R.id.et_jcbz)));

			_BaseObject.ReadDataAndBindToView("SYS_ID=" + mObjId);
			_BaseObject.RefreshDataToView();

			// mTubanhao = ((TextView)
			// mView.findViewById(R.id.et_tubanhao)).getText().toString();
			// mXian = ((TextView)
			// mView.findViewById(R.id.et_xian)).getText().toString();
			//
			// mXianDataField = mLayer.GetDataFieldNameByFieldName("��");
			//
			SharedPreferences preferences = mOwnActivity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
			String jcry = Tools.GetTextValueOnID(mOwnActivity, R.id.et_jcr);
			if (jcry == null || jcry.length() == 0) {
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jcr, preferences.getString(JCRY, ""));
			}

			String jcdwmc = Tools.GetTextValueOnID(mOwnActivity, R.id.et_jcdwmc);
			if (jcdwmc == null || jcdwmc.length() == 0) {
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jcdwmc, preferences.getString(JCDW, ""));
			}

			StartAnalysisPoly(true);
			
//			calcMianJi();

			// String jcjb = preferences.getString(JCJB,null);
			// if(jcjb != null && jcjb.length()>0)
			// {
			// Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_jcjb, jcjb);
			// }
			// else
			// {
			// Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_jcjb, "1-�ؼ�");
			// }
			//
			// new Handler().post(new Runnable() {
			//
			// @Override
			// public void run() {
			//
			// try
			// {
			// calcXuJi();
			//
			// }
			// catch(Exception ex)
			// {
			// Log.e("autoCalcXuJiMianji", ex.getMessage());
			// }
			//
			// EditText etTuban =(EditText)
			// mView.findViewById(R.id.et_tubanhao);
			// try
			// {
			// int maxTuban = 1;
			// if(((TextView)etTuban).getText()==null ||
			// ((TextView)etTuban).getText().length()==0)
			// {
			// String tubanDF = mLayer.GetDataFieldNameByFieldName("ͼ�ߺ�");
			// SQLiteDataReader reader =
			// PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID()).getDataSource().Query("select
			// "+tubanDF+" from
			// "+PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID()).getDataTableName());//+"
			// order by "+tubanDF+" DESC limit 1");
			// while(reader.Read())
			// {
			// try
			// {
			// int tubanhao = reader.GetInt32(tubanDF);
			// if (tubanhao > maxTuban)
			// {
			// maxTuban = tubanhao;
			// }
			//
			// }
			// catch(Exception ex)
			// {
			//
			// }
			// }
			//
			//// etTuban.setText((maxTuban+1)+"");
			//
			// }
			// }
			// catch(Exception e)
			// {
			// Log.e("autoCalcTuBanhao", e.getMessage());
			//// etTuban.setText("1");
			// }
			// }
			// });
		}

	}

	private void calcMianJi() {
		Geometry pGeometry = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID()).GetGeometry(mObjId);
		DecimalFormat df2 = new DecimalFormat("0.0000");
		String value = df2.format(((Polygon) pGeometry).getArea(true) / 10000);
		Tools.SetTextViewValueOnID(mView, R.id.et_sjgbldytmj, value);
		CheckBox cb = (CheckBox) mView.findViewById(R.id.cb_sfwfwg);
		if (cb.isChecked()) {
			Tools.SetTextViewValueOnID(mView, R.id.et_wfwgcflmxj, value);
		} else {
			Tools.SetTextViewValueOnID(mView, R.id.et_wfwgcflmxj, "");
		}

	}

	private void calcXuJi() {

		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());

		String wfDataField = mLayer.GetDataFieldNameByFieldName("�Ƿ�Υ��Υ��");

		String jcrq = ((TextView) mView.findViewById(R.id.et_jcsj)).getText().toString();
		if (jcrq == null || jcrq.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			((EditText) mView.findViewById(R.id.et_jcsj)).setText(sdf.format(new java.util.Date()));
		}

		String dcnd = ((TextView) mView.findViewById(R.id.et_dcnd)).getText().toString();
		if (dcnd == null || dcnd.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			((EditText) mView.findViewById(R.id.et_dcnd)).setText(sdf.format(new java.util.Date()));
		}

		double totalMJ = 0;
		double weiguiMJ = 0;
		DecimalFormat df = new DecimalFormat("0.0000");

		ArrayList<Integer> idList = new ArrayList<Integer>();
		HashMap<Integer, String> hmIds = new HashMap<Integer, String>();

		if (wfDataField.length() > 0) {
			try {
				// TODO:Ҫ�ж�ͼ�ߺ��Ƿ�Ϊ�գ�
				String sql = "select SYS_ID,SYS_Area," + wfDataField + " from " + pDataset.getDataTableName()
						+ " where " + mTubanDataField + " = '" + mTubanhao + "' and SYS_STATUS='0'";
				if (mXianDataField != null && mXianDataField.length() > 0) {
					sql += " and " + mXianDataField + "= '" + mXian + "' ";
				}
				if (mTubanhao == null || mTubanhao.isEmpty()) {
					sql = "select SYS_ID,SYS_Area," + wfDataField + " from " + pDataset.getDataTableName()
							+ " where SYS_ID  = " + mObjId + " and SYS_STATUS='0' ";
					if (mXianDataField != null && mXianDataField.length() > 0) {
						sql += " and " + mXianDataField + "= '" + mXian + "' ";
					}
				}

				SQLiteDataReader reader = pDataset.getDataSource().Query(sql);
				while (reader.Read()) {
					int wf = reader.GetInt32(wfDataField);
					double mj = reader.GetDouble("SYS_Area");

					int id = reader.GetInt32("SYS_ID");

					if (id != mObjId) {
						idList.add(id);
					}
					if (wf == 1) {
						totalMJ += mj;
						weiguiMJ += mj;
						hmIds.put(id, "1," + df.format(mj / 10000));
					} else {
						totalMJ += mj;
						hmIds.put(id, "0," + df.format(mj / 10000));
					}

				}
			} catch (Exception ex) {
				Tools.ShowMessageBox("�Զ��������ʧ�ܣ�" + ex.getMessage());
			}
			_BaseObject.addRelatedIds(idList);

			// ((EditText)
			// mView.findViewById(R.id.et_gbldythcflmmj)).setText(df.format(totalMJ
			// / 10000));
			// ((EditText)
			// mView.findViewById(R.id.et_wfwggbldythcflmmj)).setText(df.format(weiguiMJ
			// / 10000));

		}

		StartAnalysisPoly(mLayer.GetLayerID(), hmIds);
	}

	// ������
	private void StartAnalysisPoly(String layerId, HashMap<Integer, String> hmIds) {

		((EditText) mView.findViewById(R.id.et_wfwgcflmxj)).setText("");
		// ((EditText) mView.findViewById(R.id.et_cflmxj)).setText("");

		v1_UserConfigDB_PolyAnalysisOption m_PAO = new v1_UserConfigDB_PolyAnalysisOption();
		List<HashMap<String, Object>> OptList = m_PAO.GetPolyAnalysisOption();
		if (OptList == null) {
			Tools.ShowYesNoMessage(mOwnActivity, "û������ɭ�ֶ�������׼ͼ�㣬�뵽ͼ��->ͼ�����ù�ѡ��״ͼ��Ϊ��׼ͼ�㣿", new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("YES")) {
						LayerManger vpl = new LayerManger();
						vpl.SetCallback(new ICallback() {
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								if (ExtraStr == null)
									return;
								v1_Layer player = (v1_Layer) ExtraStr;
								Dataset pDataset = PubVar.m_Workspace.GetDatasetById(player.GetLayerID());
								if (pDataset == null)
									return;
								// PubVar.m_Workspace.SetAllGeoLayerNoSelectable();
								PubVar.m_Map.ClearSelection();
								pDataset.getBindGeoLayer().setSelectable(true);
								// �������

								if (player.GetLayerTypeName().equals("��"))
									PubVar.m_DoEvent.m_GPSPoint.SetDataset(pDataset);
								if (player.GetLayerTypeName().equals("��"))
									PubVar.m_DoEvent.m_GPSLine.SetDataset(pDataset);
								if (player.GetLayerTypeName().equals("��"))
									PubVar.m_DoEvent.m_GPSPoly.SetDataset(pDataset);
								PubVar.m_DoEvent.m_MainBottomToolBar.LoadBottomToolBarByType("ȫ��", false);
								if (Str.equals("Vector")) {
									PubVar.m_DoEvent.m_MainBottomToolBar.LoadBottomToolBarByType("Vector", true);
									PubVar.m_DoEvent.m_EditToolbar.ShowToolsItem(player.GetLayerTypeName() + "����",
											true);
								} else {
									PubVar.m_DoEvent.m_MainBottomToolBar
											.LoadBottomToolBarByType(player.GetLayerTypeName(), true);
									PubVar.m_DoEvent.m_EditToolbar.ShowToolsItem(player.GetLayerTypeName() + "����",
											false);
								}

								// v1_Layer pLayer =
								// PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
								PubVar.m_DoEvent.m_GpsInfoManage.SetCurrentLayerName(player);

							}
						});
						vpl.ShowDialog();
					}
				}
			});
			return;
		}

		HashMap<Integer, HashMap<String, Object>> m_AnalysisResultList = new HashMap<Integer, HashMap<String, Object>>();

		// 2����ȡ��Ҫ���������
		HashMap<String, Object> hmObj = new HashMap<String, Object>();
		List<HashMap<String, Object>> polyDatasetList = new ArrayList<HashMap<String, Object>>();
		for (HashMap<String, Object> Opt : OptList) {
			String LayerId = Opt.get("LayerId") + "";
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(LayerId);

			ArrayList<String> FieldList = new ArrayList<String>();

			v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
					.GetLayerByID(LayerId);
			String dfName = pLayer.GetDataFieldNameByFieldName("HUO_LMGQXJ");
			if (dfName.isEmpty()) {
				return;
			} else {
				FieldList.add(dfName);
			}

			hmObj.put("Dataset", pDataset);
			hmObj.put("FieldNameList", FieldList);
		}

		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(layerId);

		for (int idx : hmIds.keySet()) {

			Polygon _SelectPoly = (Polygon) pDataset.GetGeometry(idx);

			HashMap<String, Object> result = this.CalPolyLayer((Dataset) hmObj.get("Dataset"), _SelectPoly);
			if (result.size() == 0)
				continue;
			List<String> SYSIDList = new ArrayList<String>();
			for (String id : result.keySet())
				SYSIDList.add(id);

			// ����������Ҫ�Ǹ�������ѡ���е��ֶ��н��з������
			HashMap<String, Object> STTypeList = new HashMap<String, Object>();
			String SQL = "select (%1$s) as STType,SYS_ID from %2$s where SYS_ID in (%3$s)";
			SQL = String.format(SQL, Tools.JoinT("||','||", (List<String>) hmObj.get("FieldNameList")),
					((Dataset) hmObj.get("Dataset")).getDataTableName(), Tools.JoinT(",", SYSIDList));
			SQLiteDataReader DR = ((Dataset) hmObj.get("Dataset")).getDataSource().Query(SQL);
			if (DR != null)
				while (DR.Read()) {
					String STType = DR.GetString("STType"); // ���磺���أ�ˮ��
					String SYSID = DR.GetString("SYS_ID");
					double B = Double.parseDouble(result.get(SYSID) + ""); // �������

					if (STTypeList.containsKey(STType)) // �ۼ�
					{
						double A = Double.parseDouble(STTypeList.get(STType) + "");
						STTypeList.put(STType, A + B);
					} else {
						STTypeList.put(STType, B);
					}
				}
			DR.Close();

			m_AnalysisResultList.put(idx, STTypeList);
		}

		DecimalFormat df = new DecimalFormat("0.0");
		double wifiXJ = 0;
		double zongXJ = 0;
		double zongMJ = 0;
		double wfMJ = 0;
		for (Integer idx : m_AnalysisResultList.keySet()) {
			String wfString = hmIds.get(idx);
			if (wfString.split(",")[0].equals("1")) {
				try {
					HashMap<String, Object> gqxj = m_AnalysisResultList.get(idx);
					for (String key : gqxj.keySet()) {
						double gqxjL = Double.parseDouble(key);
						double mj = Double.parseDouble(gqxj.get(key) + "");
						zongMJ += mj;
						wfMJ += mj;
						Log.e("Υ���������", gqxjL + "");
						Log.e("Υ�����", mj + "");
						wifiXJ += mj * gqxjL / 10000;
						zongXJ += mj * gqxjL / 10000;

						Log.e("Υ�������", wifiXJ + "");
						Log.e("�����", zongXJ + "");
						((EditText) mView.findViewById(R.id.et_wfwgcflmxj)).setText(df.format(wifiXJ));
						// ((EditText)
						// mView.findViewById(R.id.et_cflmxj)).setText(df.format(zongXJ));
					}

				} catch (Exception ex) {
					Log.e("����Υ�����", ex.getMessage());
				}

			} else {
				try {
					HashMap<String, Object> gqxj = m_AnalysisResultList.get(idx);
					for (String key : gqxj.keySet()) {
						double gqxjL = Double.parseDouble(key);
						double mj = Double.parseDouble(gqxj.get(key) + "");
						zongMJ += mj;
						Log.e("�������", gqxjL + "");
						Log.e("���", mj + "");
						zongXJ += mj * gqxjL / 10000;
						Log.e("�����", zongXJ + "");
						// ((EditText)
						// mView.findViewById(R.id.et_cflmxj)).setText(df.format(zongXJ));
					}

				} catch (Exception ex) {
					Log.e("�����������", ex.getMessage());
				}
			}
			DecimalFormat df2 = new DecimalFormat("0.0000");
			// ((EditText)
			// mView.findViewById(R.id.et_gbldythcflmmj)).setText(df2.format(zongMJ
			// / 10000));
			// ((EditText)
			// mView.findViewById(R.id.et_wfwggbldythcflmmj)).setText(df2.format(wfMJ
			// / 10000));
		}

	}

	private HashMap<String, Object> CalPolyLayer(Dataset pDataset, Polygon pSelectPoly) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		GeoLayer pGeoLayer = pDataset.getBindGeoLayer();
		int ShowCount = pGeoLayer.getShowSelection().getCount();
		for (int i = 0; i < ShowCount; i++) {
			int idx = pGeoLayer.getShowSelection().getGeometryIndexList().get(i);
			Polygon Poly2 = (Polygon) pGeoLayer.getDataset().GetGeometry(idx);
			if (Poly2.getStatus() == lkGeometryStatus.enDelete)
				continue;
			if (pSelectPoly.equals(Poly2))
				continue; // �������ж�

			// ��Ӿ����Ƿ��ཻ
			if (!pSelectPoly.getEnvelope().Intersect(Poly2.getEnvelope()))
				continue;

			// �������
			HashMap<String, Object> IntersectResult = SpatialAnalysisTools.Poly_IntersectArea(pSelectPoly, Poly2);

			// �����ཻ���
			double Allarea = Double.parseDouble(IntersectResult.get("Area") + "");
			if (Allarea < 0) {
				Allarea = Allarea * -1;
			}

			if (Allarea > 0) {
				result.put(Poly2.getSysId() + "", Allarea);
			}
		}

		return result;
	}

	private void initPhotoPager() {
		try {
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
					_BaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
					_BaseObject.RefreshViewValueToData();
					_BaseObject.SaveFeatureToDb();
				}
			});
		} catch (Exception ex) {
			Log.e("initPhotoPager", ex.getMessage());
		}

	}

	private void changePageViewIndex(int position) {
		Button btnXBXX = (Button) mView.findViewById(R.id.bt_vp_tbyz);
		Button btnPhoto = (Button) mView.findViewById(R.id.bt_vp_tbyzphoto);

		if (position == 0) {
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnPhoto.setTextColor(android.graphics.Color.BLACK);
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnXBXX.setTextColor(android.graphics.Color.WHITE);
			mView.findViewById(R.id.locator22).setVisibility(View.INVISIBLE);
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

	private void saveData() {
		// ������������
		this._BaseObject.RefreshViewValueToData();
		if (mPhotoControl != null) {
			this._BaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
		}

		if (!this._BaseObject.SaveFeatureToDb()) {
			Tools.ShowMessageBox(this.mOwnActivity, "���ݱ���ʧ�ܣ�");
			// return;
		} else {
			Toast.makeText(mOwnActivity, "��֤ͼ�������ѱ���", Toast.LENGTH_SHORT).show();
			// Spinner spBHYY = (Spinner) mView.findViewById(R.id.sp_tbbhyy);
			// if (spBHYY.getSelectedItemPosition() < 5) {
			// Tools.ShowYesNoMessage(mOwnActivity, "ͼ����֤��Ϣ�ѱ��棬�Ƿ������д��鿨Ƭ��", new
			// ICallback() {
			//
			// @Override
			// public void OnClick(String Str, Object ExtraStr) {
			// if (Str.equals("YES")) {
			// String xian =
			// ((TextView)mView.findViewById(R.id.et_xian)).getText().toString();
			//
			//
			// CheckCard checkCard = new CheckCard();
			//// List<Integer> selectIndexs =
			// PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData)
			//// .GetLayerById(mLayer.GetLayerID()).getSelSelection().getGeometryIndexList();
			//
			//
			//// checkCard.setTuBan(mLayer.GetLayerID(), Tools.JoinIntT(",",
			// selectIndexs),xian);
			// checkCard.setTuBan(mLayer.GetLayerID(), mAllObjIds,xian);
			// }
			// }
			// });
			// }

		}

		// HiddenView();

	}
	private boolean StartAnalysisPoly(boolean beIllegal) {
		// 1����ȡ�������������Ϣ����ʽ�����v1_UserConfigDB_PolyAnalysisOption
		v1_UserConfigDB_PolyAnalysisOption m_PAO = new v1_UserConfigDB_PolyAnalysisOption();
		List<HashMap<String, Object>> OptList = m_PAO.GetPolyAnalysisOption();

		// 2����ȡ��Ҫ���������
		List<HashMap<String, Object>> polyDatasetList = new ArrayList<HashMap<String, Object>>();
		for (HashMap<String, Object> Opt : OptList) {
			String LayerId = Opt.get("LayerId") + "";
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(LayerId);
			if (pDataset == null)
				continue;

			v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
			if (pLayer == null)
				pLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
						.GetLayerByID(pDataset.getId());

			List<String> FieldList = (List<String>) Opt.get("FieldNameList");

			// �ж�ͳ���ֶ��Ƿ���Ч
			int FieldCount = FieldList.size();
			for (int i = FieldCount - 1; i >= 0; i--) {
				boolean Have = false;
				for (v1_LayerField LF : pLayer.GetFieldList()) {
					if (LF.GetDataFieldName().equals(FieldList.get(i)))
						Have = true;
				}
				if (!Have)
					FieldList.remove(i);
			}
			if (FieldList.size() == 0)
				FieldList.add("SYS_ID");
			// û��ͳ���ֶΣ�Ĭ����SYS_ID����ͳ��
			HashMap<String, Object> hmObj = new HashMap<String, Object>();
			hmObj.put("Dataset", pDataset);
			hmObj.put("FieldNameList", FieldList);
			polyDatasetList.add(hmObj);
		}

		// ���Ϊ0����ʾ��Ҫ���з�������
		if (polyDatasetList.size() == 0) {
			Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "û������ɭ�ֶ�������׼ͼ�㣬�뵽ͼ��->ͼ�����ù�ѡɭ�ֶ�������׼ͼ�㣿", new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("YES")) {
						
					} else {
					
					}
				}
			});
			
			return false;
		}

		return polyAnalysis(beIllegal,polyDatasetList);
		
	}

		private boolean polyAnalysis(Boolean beIllegal,List<HashMap<String, Object>> polyDatasetList) {
		
			HashMap<String, HashMap<String, Object>> m_AnalysisResultList = new HashMap<String, HashMap<String, Object>>();
			
			Polygon _SelectPoly = (Polygon) PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(mLayer.GetLayerID()).getDataset().GetGeometry(mObjId);
			
			if(_SelectPoly == null)
			{
				Toast.makeText(PubVar.m_DoEvent.m_Context, "�а���ͼ��δ��ͼ����ʾ���޷�׼ȷ������������", Toast.LENGTH_LONG).show();
				return false;

			}

			for (HashMap<String, Object> pDatasetInfo : polyDatasetList) {
				Dataset pDataset = (Dataset) pDatasetInfo.get("Dataset");
				List<String> FieldList = (List<String>) pDatasetInfo.get("FieldNameList");

				// ����ָ����㣬���ظ�ʽ��result["SYSID"],result["Area"]
				HashMap<String, Object> result = this.CalPolyLayer(pDataset, _SelectPoly);
				if (result.size() == 0)
					continue;
				List<String> SYSIDList = new ArrayList<String>();
				for (String id : result.keySet())
					SYSIDList.add(id);

				// ����������Ҫ�Ǹ�������ѡ���е��ֶ��н��з������
				HashMap<String, Object> STTypeList = new HashMap<String, Object>();
				String SQL = "select (%1$s) as STType,SYS_ID from %2$s where SYS_ID in (%3$s)";
				SQL = String.format(SQL, Tools.JoinT("||','||", FieldList), pDataset.getDataTableName(),
						Tools.JoinT(",", SYSIDList));
				SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
				if (DR != null)
					while (DR.Read()) {
						String STType = DR.GetString("STType"); // ���磺���أ�ˮ��
						String SYSID = DR.GetString("SYS_ID");
						double B = Double.parseDouble(result.get(SYSID) + ""); // �������

						if (STTypeList.containsKey(STType)) // �ۼ�
						{
							double A = Double.parseDouble(STTypeList.get(STType) + "");
							STTypeList.put(STType, A + B);
						} else {
							STTypeList.put(STType, B);
						}
					}
				DR.Close();
				m_AnalysisResultList.put(mObjId + "", STTypeList);
					
			}
			
			return calcSumArea(beIllegal,m_AnalysisResultList);

		}
		
		private boolean calcSumArea(Boolean beIllegal, HashMap<String, HashMap<String, Object>> allResultList) {
			Double GJGYL1 =new Double(0),GJGYL2 = new Double(0), DFGYL = new Double(0), SPL = new Double(0), dlQML = new Double(0), cfXuJi = new Double(0), 
					totalMJ = new Double(0), dlZhuLin = new Double(0), dlGuoTG =  new Double(0), dlQiTaGM =  new Double(0), dlQiTa =  new Double(0);
			
			DecimalFormat df2 = new DecimalFormat("0.0000");
			DecimalFormat dfXJ = new DecimalFormat("0.0");
			
			HashMap<String,Double> hmQianDiLei = new HashMap<String,Double>();
			
			for (String sysId : allResultList.keySet()) {
				HashMap<String, Object> resultList = allResultList.get(sysId);

				if (resultList.keySet().size() > 0) {
					for (String key : resultList.keySet()) {
						double area = Double.parseDouble(resultList.get(key) + "");
						
						if (key.endsWith(",")) {
							key += " ";
						}
						String[] Keys = key.split(",");

						if (Keys.length < 6) {

							Tools.ShowMessageBox("������������ֶ�����������DI_LEI,SEN_LIN_LB,SHI_QUAN_D,GJGYL_BHDJ,BH_DJ,HUO_LMGQXJ�����ֶ�.");
							return false;
						}
						
						
						
						
						// ��Ȩ�ȼ� 10.���ҹ����� 20.�ط�������
						if (Keys[2] != null && !Keys[2].isEmpty()) {
							if (Keys[2].equals("10")) {
								if (Keys[3] != null && !Keys[3].isEmpty()) {//���ҹ����ֱ����ȼ�
									if (Keys[3].equals("1")) {
										GJGYL1 += area;
										((EditText) mView.findViewById(R.id.et_wfwgzyjgjgylmj)).setText(df2.format(GJGYL1 / 10000));
//										if(GJGYL1<0.5&&GJGYL1>-2)
//										{
//											GJGYL1=new Double(0);
//											((EditText) mView.findViewById(R.id.a45)).setText("");
//										}
									} else if (Keys[3].equals("2")) {
										GJGYL2 += area;
										((EditText) mView.findViewById(R.id.et_wfwgzejgjgylmj)).setText(df2.format(GJGYL2 / 10000));
									} else if (Keys[3].equals("3"))// ԭ���ҹ����ֵȼ���3���Ĳ���2��
									{
										GJGYL2 += area;
//										((EditText) mView.findViewById(R.id.a46)).setText(df2.format(GJGYL2 / 10000));
									}
								}
							} else if (Keys[2].equals("20")) {
								DFGYL += area;
								((EditText) mView.findViewById(R.id.et_wfwgzdfgylmj)).setText(df2.format(DFGYL / 10000));
							}
						} else {//ɭ�����
							if (Keys[1] != null && !Keys[1].isEmpty())// ��Ʒ��
							{
								if (Keys[0].startsWith("02") || Keys[0].startsWith("2")) {
									SPL += area;
									((EditText) mView.findViewById(R.id.et_wfwgzsplmj)).setText(df2.format(SPL / 10000));
								}
							}
						}
						
						// ����
						if (Keys[0] != null && !Keys[0].isEmpty()) {
							
							
							if(Keys[0].startsWith("1")||Keys[0].startsWith("01"))
							{
								totalMJ+= area;
							if (Keys[0].startsWith("111") || Keys[0].startsWith("0111")) {
								dlQML += area;
								((EditText) mView.findViewById(R.id.et_wfwgzqmlmj)).setText(df2.format(dlQML / 10000));

								
								String huo_LMGQXJ = Keys[5];
								try {
									double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
									cfXuJi += xj;
									((EditText) mView.findViewById(R.id.et_wzcfxj)).setText(dfXJ.format(cfXuJi));
								} catch (Exception ex) {
									Log.e("���", ex.getMessage());
								}
								
								} 
//							else if (Keys[0].startsWith("12") || Keys[0].startsWith("012")) {
//								dlHSL += area;
//								((EditText) mView.findViewById(R.id.et_wfwgzhslmj)).setText(df2.format(dlHSL / 10000));
////								a36 = Double.parseDouble(df2.format(a36 / 10000));//������֤��
//
//
//							} 
							else if (Keys[0].equals("113") || Keys[0].equals("0113")) {
								dlZhuLin += area;
								((EditText) mView.findViewById(R.id.et_wfwgzzldmj)).setText(df2.format(dlZhuLin / 10000));
							} else if (Keys[0].equals("131") || Keys[0].equals("0131")) {
								dlGuoTG += area;
								((EditText) mView.findViewById(R.id.et_wfwgzgjtglmj)).setText(df2.format(dlGuoTG / 10000));
							} 
							else if (Keys[0].equals("132") || Keys[0].equals("0132")) {
								dlQiTaGM += area;
								((EditText) mView.findViewById(R.id.et_wfwgzqtgmlmj)).setText(df2.format(dlQiTaGM / 10000));
							}else {
								dlQiTa += area;
								((EditText) mView.findViewById(R.id.et_wfwgzqtldmj)).setText(df2.format(dlQiTa / 10000));
							} 
						}
						}
						
						
						if(hmQianDiLei.containsKey(Keys[0]))
						{
							hmQianDiLei.put(Keys[0], hmQianDiLei.get(Keys[0])+area);
						}
						else
						{
							hmQianDiLei.put(Keys[0], area);
						}
						
						
					}
				}
			}
			
			Double maxArea = 0d;
			String qianDilei ="";
			for(String key : hmQianDiLei.keySet())
			{
				Double area = hmQianDiLei.get(key);
				if(area>maxArea)
				{
					maxArea = area;
					qianDilei = key;
				}
			}
			((EditText) mView.findViewById(R.id.et_qdl)).setText(qianDilei);
			
			
			
			((EditText) mView.findViewById(R.id.et_sjgbldytmj)).setText(df2.format(totalMJ / 10000));
			if(beIllegal)
			{
				((EditText) mView.findViewById(R.id.et_wfwgcflmxj)).setText(df2.format(totalMJ / 10000));
			}
			else
			{
				((EditText) mView.findViewById(R.id.et_wfwgcflmxj)).setText("");
				((EditText) mView.findViewById(R.id.et_wfwgzsplmj)).setText("");
				((EditText) mView.findViewById(R.id.et_wfwgzqmlmj)).setText("");
				((EditText) mView.findViewById(R.id.et_wfwgzzldmj)).setText("");
				((EditText) mView.findViewById(R.id.et_wfwgzhslmj)).setText("");
				((EditText) mView.findViewById(R.id.et_wfwgzgjtglmj)).setText("");
				((EditText) mView.findViewById(R.id.et_wfwgzqtgmlmj)).setText("");
				((EditText) mView.findViewById(R.id.et_wfwgzqtldmj)).setText("");
				((EditText) mView.findViewById(R.id.et_wfwgzyjgjgylmj)).setText("");
				((EditText) mView.findViewById(R.id.et_wfwgzejgjgylmj)).setText("");
				((EditText) mView.findViewById(R.id.et_wfwgzdfgylmj)).setText("");
				((EditText) mView.findViewById(R.id.et_zrbhdmj)).setText("");
				((EditText) mView.findViewById(R.id.et_zrbhdmc)).setText("");
			}
			
			return false;
		}
		
		private void initReleatedView()
		{
			Spinner spSyldxz = (Spinner) mView.findViewById(R.id.sp_syldxz);
			int arg2 = spSyldxz.getSelectedItemPosition();
			
			if(arg2<5)
			{
				StartAnalysisPoly(true);
			}
			else
			{
				StartAnalysisPoly(false);
			}
			
			
			spSyldxz.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if(arg2<5)
					{
						StartAnalysisPoly(true);
					}
					else
					{
						StartAnalysisPoly(false);
					}
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			Spinner spBhyy = (Spinner) mView.findViewById(R.id.sp_bhyy);
			int arg3 = spBhyy.getSelectedItemPosition();
			
			if(arg3<11)
			{
				mView.findViewById(R.id.layoyt_duchaheshi).setVisibility(View.VISIBLE);
				
					
			} else {
					mView.findViewById(R.id.layoyt_duchaheshi).setVisibility(View.GONE);
					((EditText) mView.findViewById(R.id.et_wfwgcflmxj)).setText("");
					((EditText) mView.findViewById(R.id.et_wfwgzsplmj)).setText("");
					((EditText) mView.findViewById(R.id.et_wfwgzqmlmj)).setText("");
					((EditText) mView.findViewById(R.id.et_wfwgzzldmj)).setText("");
					((EditText) mView.findViewById(R.id.et_wfwgzhslmj)).setText("");
					((EditText) mView.findViewById(R.id.et_wfwgzgjtglmj)).setText("");
					((EditText) mView.findViewById(R.id.et_wfwgzqtgmlmj)).setText("");
					((EditText) mView.findViewById(R.id.et_wfwgzqtldmj)).setText("");
					((EditText) mView.findViewById(R.id.et_wfwgzyjgjgylmj)).setText("");
					((EditText) mView.findViewById(R.id.et_wfwgzejgjgylmj)).setText("");
					((EditText) mView.findViewById(R.id.et_wfwgzdfgylmj)).setText("");
					((EditText) mView.findViewById(R.id.et_zrbhdmj)).setText("");
					((EditText) mView.findViewById(R.id.et_zrbhdmc)).setText("");
					
					((EditText) mView.findViewById(R.id.et_ldgldw)).setText("");
					((EditText) mView.findViewById(R.id.et_hsxbh)).setText("");
					((EditText) mView.findViewById(R.id.et_xmhfqmc)).setText("");
					((EditText) mView.findViewById(R.id.et_spwh)).setText("");
					((EditText) mView.findViewById(R.id.et_spnd)).setText("");
					((EditText) mView.findViewById(R.id.et_spmj)).setText("");
				}
			
			spBhyy.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					
					
					if(arg2>0 && arg2<11)
					{
						mView.findViewById(R.id.layoyt_duchaheshi).setVisibility(View.VISIBLE);
						
							
					} else {
							mView.findViewById(R.id.layoyt_duchaheshi).setVisibility(View.GONE);
							((EditText) mView.findViewById(R.id.et_wfwgcflmxj)).setText("");
							((EditText) mView.findViewById(R.id.et_wfwgzsplmj)).setText("");
							((EditText) mView.findViewById(R.id.et_wfwgzqmlmj)).setText("");
							((EditText) mView.findViewById(R.id.et_wfwgzzldmj)).setText("");
							((EditText) mView.findViewById(R.id.et_wfwgzhslmj)).setText("");
							((EditText) mView.findViewById(R.id.et_wfwgzgjtglmj)).setText("");
							((EditText) mView.findViewById(R.id.et_wfwgzqtgmlmj)).setText("");
							((EditText) mView.findViewById(R.id.et_wfwgzqtldmj)).setText("");
							((EditText) mView.findViewById(R.id.et_wfwgzyjgjgylmj)).setText("");
							((EditText) mView.findViewById(R.id.et_wfwgzejgjgylmj)).setText("");
							((EditText) mView.findViewById(R.id.et_wfwgzdfgylmj)).setText("");
							((EditText) mView.findViewById(R.id.et_zrbhdmj)).setText("");
							((EditText) mView.findViewById(R.id.et_zrbhdmc)).setText("");
							
							((EditText) mView.findViewById(R.id.et_ldgldw)).setText("");
							((EditText) mView.findViewById(R.id.et_hsxbh)).setText("");
							((EditText) mView.findViewById(R.id.et_xmhfqmc)).setText("");
							((EditText) mView.findViewById(R.id.et_spwh)).setText("");
							((EditText) mView.findViewById(R.id.et_spnd)).setText("");
							((EditText) mView.findViewById(R.id.et_spmj)).setText("");
						}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
		}
}
