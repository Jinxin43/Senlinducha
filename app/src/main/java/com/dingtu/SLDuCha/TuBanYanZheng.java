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
		// String dilei = "1-林地,2-非林地";
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
		// "1-建设项目使用林地,2-直接为林业生产服务,3-毁林（湿）开垦,4-土地整理,5-林木采伐,6-造林、抚育及其他森林经营活动,7-森林病虫害、森林火灾,8-地质灾害等自然原因,9-植物季节性变化,10-遥感数据质量,11-前地类是非林地
		// ,12-其他原因";
		// Spinner spBhyy = (Spinner) mView.findViewById(R.id.sp_tbbhyy);
		// ArrayAdapter<String> bhyyAdapter = new
		// ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
		// android.R.layout.simple_spinner_item, bhyy.split(","));
		// bhyyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// // spBhyy.setLayoutMode(Spinner.MODE_DROPDOWN);
		// spBhyy.setAdapter(bhyyAdapter);
		//
		// String jcjb = "1-县级, 2-市级,3-省级,4-直属院,5-专员办";
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
		// Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_jcjgsfyz, "3-县级检查");
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
		// String jcjg = "1-一致,2-不一致,3-县级检查";
		// Spinner spJcjg = (Spinner) mView.findViewById(R.id.sp_jcjgsfyz);
		// ArrayAdapter<String> jcjgAdapter = new
		// ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
		// android.R.layout.simple_spinner_item, jcjg.split(","));
		// jcjgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// // spJcjg.setLayoutMode(Spinner.MODE_DROPDOWN);
		// spJcjg.setAdapter(jcjgAdapter);

		String bhyy = ",411-建设项目使用林地_经审批_永久使用,412-建设项目使用林地_经审批_临时占用,413-建设项目使用林地_经审批_为林业生产服务,421-建设项目使用林地_未审批_永久使用"
				+ ",422-建设项目使用林地_未审批_临时占用,423-建设项目使用林地_未审批_为林业生产服务,51-毁林(湿)开垦-种植,52-毁林(湿)开垦-土地整理,21-林木采伐_经审批"
				+ ",22-林木采伐_未审批,11-人工/飞播造林,12-人工更新,30-规划调整,60-森林抚育,71-火灾,72-地质灾害,73-其他灾害因素,81-封山育林,82-其他自然因素,"
				+ "91-调查原因导致的地类变化,94-标准调整,95-界线变化_数据库整合,96-界线变化_行政界线变化,97-界线变化_国营林场等管理界线变化,98-界线变化_自然保护区森林公园等管理界线变化"
				+ ",99-地类未变化但管理因子变化";

		Spinner spBhyy = (Spinner) mView.findViewById(R.id.sp_bhyy);
		ArrayAdapter<String> bhyyAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, bhyy.split(","));
		bhyyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// spBhyy.setLayoutMode(Spinner.MODE_DROPDOWN);
		spBhyy.setAdapter(bhyyAdapter);

		String syldxz = ",1-未经林业和草原主管部门审核,2-越权审核,3- 超审核使用,4-未按用途使用,5-超期限占用,6-依法依规使用林地";

		Spinner spSyldxz = (Spinner) mView.findViewById(R.id.sp_syldxz);
		ArrayAdapter<String> syldxzAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, syldxz.split(","));
		syldxzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// spBhyy.setLayoutMode(Spinner.MODE_DROPDOWN);
		spSyldxz.setAdapter(syldxzAdapter);

		String jcjb = "国家级,省级,县级,其他";
		Spinner spJcjb = (Spinner) mView.findViewById(R.id.sp_zrbhdjb);
		ArrayAdapter<String> jcjbAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, jcjb.split(","));
		jcjbAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spJcjb.setAdapter(jcjbAdapter);

		String jclx = ",1-自然保护区,2-森林公园 ,3-湿地公园, 4-风景名胜区, 5-地质公园, 6-海洋公园, 7-世界自然遗产地, 8-国际重要湿地, 9-其他, 10-国家公园";
		Spinner spJclx = (Spinner) mView.findViewById(R.id.sp_zrbhdlx);
		ArrayAdapter<String> bxdlxAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item, jclx.split(","));
		bxdlxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spJclx.setAdapter(bxdlxAdapter);

		String xdl = ",0100-耕地,0101-水田,0102-水浇地,0103-旱地,0200-种植园地,0201-果园,0202-茶园,0203-橡胶园,0204-其他园地,0301-乔木林地,0301K-可调整乔木林地,0302-竹林地,"
				+ "0302K-可调整竹林地,0305-灌木林地,0308-疏林地,0309-未成林地,0310-迹地,0311-苗圃地,0400-草地,0401-天然牧草地,0403-人工牧草地,0404-其他草地,0300-湿地,0303-红树林地,0304-森林沼泽,"
				+ "0306-灌丛沼泽,0402-沼泽草地,0603-盐田,1105-沿海滩涂,1106-内陆滩涂,1108-沼泽地,0500-商业服务业用地,05H1-商业服务业用地,0508-物流仓储用地,0600-工矿用地,0601-工业用地,0602-采矿用地,"
				+ "07-住宅用地,0701-城镇住宅用地,0702-农村宅基地,08-公共管理与服务用地,08H1-机关团体新闻出版用地,08H2-科教文卫用地,0809-公共设施用地,"
				+ "0810-公园与绿地,0900-特殊用地,1000-交通运输用地,1001-铁路用地,1002-轨道交通用地,1003-公路用地,1004-城镇村道路用地,1005-交通服务场站用地,"
				+ "1006-农村道路,1007-机场用地,1008-港口码头用地,1009-管道运输用地,1100-水域及水利设施用地,1101-河流水面,1102-湖泊水面,1103-水库水面,1104-水塘水面,"
				+ "1107-沟渠,1109-水工建筑用地,1110-冰川及永久积雪,1200-其他用地,1201-空闲地,1202-设施农用地,1203-田坎,1204-盐碱地,1205-沙地,1206-裸土地,1207-裸岩石砾地,250-建设用地";
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
		titleContainer.add("图斑验证");
		titleContainer.add("照片");

		viewPager = (ViewPager) mView.findViewById(R.id.TBYZviewPager);

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
			if (Tag.equals("返回")) {
				mView.setVisibility(View.GONE);
			}
			if (Tag.equals("保存")) {
				saveData();
				mView.setVisibility(View.GONE);
			}
			if (Tag.equals("图斑验证")) {
				viewPager.setCurrentItem(0);
			}
			if (Tag.equals("照片")) {
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

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("省", mLayer.GetDataFieldNameByFieldName("省"), "",
					mView.findViewById(R.id.et_province)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("县", mLayer.GetDataFieldNameByFieldName("县"), "",
					mView.findViewById(R.id.et_xian)));
			// _BaseObject.AddDataBindItem(new DataBindOfKeyValue("调查年度",
			// mLayer.GetDataFieldNameByFieldName("检查日期"),
			// mView.findViewById(R.id.et_dcnd)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("乡镇", mLayer.GetDataFieldNameByFieldName("乡镇"), "",
					mView.findViewById(R.id.et_xz)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("村", mLayer.GetDataFieldNameByFieldName("村"), "",
					mView.findViewById(R.id.et_cun)));

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("林业局", mLayer.GetDataFieldNameByFieldName("林业局"), "",
					mView.findViewById(R.id.et_lyj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("林场", mLayer.GetDataFieldNameByFieldName("林场"), "",
					mView.findViewById(R.id.et_linchang)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("林班", mLayer.GetDataFieldNameByFieldName("林班"), "",
					mView.findViewById(R.id.et_linban)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违法违规", mLayer.GetDataFieldNameByFieldName("违法违规"), "",
					mView.findViewById(R.id.cb_sfwfwg)));
			mTubanDataField = mLayer.GetDataFieldNameByFieldName("判读图斑编号");
			_BaseObject.AddDataBindItem(
					new DataBindOfKeyValue("判读图斑编号", mTubanDataField, "", mView.findViewById(R.id.et_tubanhao)));

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("林地管理单位", mLayer.GetDataFieldNameByFieldName("林地管理单位"),
					"", mView.findViewById(R.id.et_ldgldw)));

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("核实细斑号", mLayer.GetDataFieldNameByFieldName("核实细斑号"), "",
					mView.findViewById(R.id.et_hsxbh)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("横坐标", mLayer.GetDataFieldNameByFieldName("横坐标"), "",
					mView.findViewById(R.id.et_X)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("纵坐标", mLayer.GetDataFieldNameByFieldName("纵坐标"), "",
					mView.findViewById(R.id.et_Y)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("判读面积", mLayer.GetDataFieldNameByFieldName("判读面积"), "",
					mView.findViewById(R.id.et_tbpdmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("判读地类", mLayer.GetDataFieldNameByFieldName("判读地类"), "",
					mView.findViewById(R.id.et_pddl)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("判读变化原因", mLayer.GetDataFieldNameByFieldName("判读变化原因"),
					"", mView.findViewById(R.id.et_pdbhyy)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("判读备注", mLayer.GetDataFieldNameByFieldName("判读备注"), "",
					mView.findViewById(R.id.et_pdbz)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("前地类", mLayer.GetDataFieldNameByFieldName("前地类"), "",
					mView.findViewById(R.id.et_qdl)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("现状地类", mLayer.GetDataFieldNameByFieldName("现状地类"), "",
					mView.findViewById(R.id.sp_zxdl)));
			
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("变化原因", mLayer.GetDataFieldNameByFieldName("变化原因"), "",
					mView.findViewById(R.id.sp_bhyy)));

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("项目名称", mLayer.GetDataFieldNameByFieldName("项目名称"), "",
					mView.findViewById(R.id.et_xmhfqmc)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("审核文号", mLayer.GetDataFieldNameByFieldName("审核文号"), "",
					mView.findViewById(R.id.et_spwh)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("审核年度", mLayer.GetDataFieldNameByFieldName("审核年度"), "",
					mView.findViewById(R.id.et_spnd)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("审核面积", mLayer.GetDataFieldNameByFieldName("审核面积"), "",
					mView.findViewById(R.id.et_spmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("实际改变林地用途面积",
					mLayer.GetDataFieldNameByFieldName("实际改变林地用途面积"), "", mView.findViewById(R.id.et_sjgbldytmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违规违法改变林地用途面积",
					mLayer.GetDataFieldNameByFieldName("违规违法改变林地用途面积"), "", mView.findViewById(R.id.et_wfwgcflmxj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违规违法中自然保护地面积",
					mLayer.GetDataFieldNameByFieldName("违规违法中自然保护地面积"), "", mView.findViewById(R.id.et_zrbhdmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("自然保护地名称", mLayer.GetDataFieldNameByFieldName("自然保护地名称"),
					"", mView.findViewById(R.id.et_zrbhdmc)));
			// _BaseObject.AddDataBindItem(new DataBindOfKeyValue("自然保护地级别",
			// mLayer.GetDataFieldNameByFieldName("自然保护地级别"),
			// "", mView.findViewById(R.id.sp_zrbhdjbjlx)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违规违法中乔木林地面积",
					mLayer.GetDataFieldNameByFieldName("违规违法中乔木林地面积"), "", mView.findViewById(R.id.et_wfwgzqmlmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违规违法中竹林面积",
					mLayer.GetDataFieldNameByFieldName("违规违法中竹林面积"), "", mView.findViewById(R.id.et_wfwgzzldmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违规违法中红树林面积",
					mLayer.GetDataFieldNameByFieldName("违规违法中红树林面积"), "", mView.findViewById(R.id.et_wfwgzhslmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违规违法中国家特灌林面积",
					mLayer.GetDataFieldNameByFieldName("违规违法中国家特灌林面积"), "", mView.findViewById(R.id.et_wfwgzgjtglmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违规违法中其他灌木林面积",
					mLayer.GetDataFieldNameByFieldName("违规违法中其他灌木林面积"), "", mView.findViewById(R.id.et_wfwgzqtgmlmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违规违法中其他林地面积",
					mLayer.GetDataFieldNameByFieldName("违规违法中其他林地面积"), "", mView.findViewById(R.id.et_wfwgzqtldmj)));
			_BaseObject.AddDataBindItem(
					new DataBindOfKeyValue("违规违法中一级国家公益林面积", mLayer.GetDataFieldNameByFieldName("违规违法中一级国家公益林面积"), "",
							mView.findViewById(R.id.et_wfwgzyjgjgylmj)));
			_BaseObject.AddDataBindItem(
					new DataBindOfKeyValue("违规违法中二级国家公益林面积", mLayer.GetDataFieldNameByFieldName("违规违法中二级国家公益林面积"), "",
							mView.findViewById(R.id.et_wfwgzejgjgylmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违规违法中地方公益林面积",
					mLayer.GetDataFieldNameByFieldName("违规违法中地方公益林面积"), "", mView.findViewById(R.id.et_wfwgzdfgylmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("违规违法中商品林面积",
					mLayer.GetDataFieldNameByFieldName("违规违法中商品林面积"), "", mView.findViewById(R.id.et_wfwgzsplmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("使用林地性质", mLayer.GetDataFieldNameByFieldName("使用林地性质"),
					"", mView.findViewById(R.id.sp_syldxz)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("林木采伐许可证号",
					mLayer.GetDataFieldNameByFieldName("林木采伐许可证号"), "", mView.findViewById(R.id.et_lmcfxkzh)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("发证面积", mLayer.GetDataFieldNameByFieldName("发证面积"), "",
					mView.findViewById(R.id.et_cfzfzmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("发证蓄积", mLayer.GetDataFieldNameByFieldName("发证蓄积"), "",
					mView.findViewById(R.id.et_cfzfzxj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("凭证采伐面积", mLayer.GetDataFieldNameByFieldName("凭证采伐面积"),
					"", mView.findViewById(R.id.et_pzcfmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("凭证采伐蓄积", mLayer.GetDataFieldNameByFieldName("凭证采伐蓄积"),
					"", mView.findViewById(R.id.et_pzcfxj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("超证采伐面积", mLayer.GetDataFieldNameByFieldName("超证采伐面积"),
					"", mView.findViewById(R.id.et_czcfmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("超证采伐蓄积", mLayer.GetDataFieldNameByFieldName("超证采伐蓄积"),
					"", mView.findViewById(R.id.et_czcfxj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("无证采伐面积", mLayer.GetDataFieldNameByFieldName("无证采伐面积"),
					"", mView.findViewById(R.id.et_wzcfmj)));
			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("无证采伐蓄积", mLayer.GetDataFieldNameByFieldName("无证采伐蓄积"),
					"", mView.findViewById(R.id.et_wzcfxj)));

			_BaseObject.AddDataBindItem(new DataBindOfKeyValue("备注", mLayer.GetDataFieldNameByFieldName("备注"), "",
					mView.findViewById(R.id.et_jcbz)));

			_BaseObject.ReadDataAndBindToView("SYS_ID=" + mObjId);
			_BaseObject.RefreshDataToView();

			// mTubanhao = ((TextView)
			// mView.findViewById(R.id.et_tubanhao)).getText().toString();
			// mXian = ((TextView)
			// mView.findViewById(R.id.et_xian)).getText().toString();
			//
			// mXianDataField = mLayer.GetDataFieldNameByFieldName("县");
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
			// Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_jcjb, "1-县级");
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
			// String tubanDF = mLayer.GetDataFieldNameByFieldName("图斑号");
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

		String wfDataField = mLayer.GetDataFieldNameByFieldName("是否违法违规");

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
				// TODO:要判断图斑号是否为空！
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
				Tools.ShowMessageBox("自动计算面积失败：" + ex.getMessage());
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

	// 分析面
	private void StartAnalysisPoly(String layerId, HashMap<Integer, String> hmIds) {

		((EditText) mView.findViewById(R.id.et_wfwgcflmxj)).setText("");
		// ((EditText) mView.findViewById(R.id.et_cflmxj)).setText("");

		v1_UserConfigDB_PolyAnalysisOption m_PAO = new v1_UserConfigDB_PolyAnalysisOption();
		List<HashMap<String, Object>> OptList = m_PAO.GetPolyAnalysisOption();
		if (OptList == null) {
			Tools.ShowYesNoMessage(mOwnActivity, "没有设置森林督查计算基准图层，请到图层->图层设置勾选现状图层为基准图层？", new ICallback() {

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
								// 处理界面

								if (player.GetLayerTypeName().equals("点"))
									PubVar.m_DoEvent.m_GPSPoint.SetDataset(pDataset);
								if (player.GetLayerTypeName().equals("线"))
									PubVar.m_DoEvent.m_GPSLine.SetDataset(pDataset);
								if (player.GetLayerTypeName().equals("面"))
									PubVar.m_DoEvent.m_GPSPoly.SetDataset(pDataset);
								PubVar.m_DoEvent.m_MainBottomToolBar.LoadBottomToolBarByType("全部", false);
								if (Str.equals("Vector")) {
									PubVar.m_DoEvent.m_MainBottomToolBar.LoadBottomToolBarByType("Vector", true);
									PubVar.m_DoEvent.m_EditToolbar.ShowToolsItem(player.GetLayerTypeName() + "工具",
											true);
								} else {
									PubVar.m_DoEvent.m_MainBottomToolBar
											.LoadBottomToolBarByType(player.GetLayerTypeName(), true);
									PubVar.m_DoEvent.m_EditToolbar.ShowToolsItem(player.GetLayerTypeName() + "工具",
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

		// 2、提取需要分析的面层
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

			// 分类整理，主要是根据设置选项中的字段列进行分类汇总
			HashMap<String, Object> STTypeList = new HashMap<String, Object>();
			String SQL = "select (%1$s) as STType,SYS_ID from %2$s where SYS_ID in (%3$s)";
			SQL = String.format(SQL, Tools.JoinT("||','||", (List<String>) hmObj.get("FieldNameList")),
					((Dataset) hmObj.get("Dataset")).getDataTableName(), Tools.JoinT(",", SYSIDList));
			SQLiteDataReader DR = ((Dataset) hmObj.get("Dataset")).getDataSource().Query(SQL);
			if (DR != null)
				while (DR.Read()) {
					String STType = DR.GetString("STType"); // 比如：旱地，水地
					String SYSID = DR.GetString("SYS_ID");
					double B = Double.parseDouble(result.get(SYSID) + ""); // 计算面积

					if (STTypeList.containsKey(STType)) // 累计
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
						Log.e("违法公顷蓄积", gqxjL + "");
						Log.e("违法面积", mj + "");
						wifiXJ += mj * gqxjL / 10000;
						zongXJ += mj * gqxjL / 10000;

						Log.e("违法总蓄积", wifiXJ + "");
						Log.e("总蓄积", zongXJ + "");
						((EditText) mView.findViewById(R.id.et_wfwgcflmxj)).setText(df.format(wifiXJ));
						// ((EditText)
						// mView.findViewById(R.id.et_cflmxj)).setText(df.format(zongXJ));
					}

				} catch (Exception ex) {
					Log.e("计算违法蓄积", ex.getMessage());
				}

			} else {
				try {
					HashMap<String, Object> gqxj = m_AnalysisResultList.get(idx);
					for (String key : gqxj.keySet()) {
						double gqxjL = Double.parseDouble(key);
						double mj = Double.parseDouble(gqxj.get(key) + "");
						zongMJ += mj;
						Log.e("公顷蓄积", gqxjL + "");
						Log.e("面积", mj + "");
						zongXJ += mj * gqxjL / 10000;
						Log.e("总蓄积", zongXJ + "");
						// ((EditText)
						// mView.findViewById(R.id.et_cflmxj)).setText(df.format(zongXJ));
					}

				} catch (Exception ex) {
					Log.e("计算所有蓄积", ex.getMessage());
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
				continue; // 自身不用判断

			// 外接矩形是否相交
			if (!pSelectPoly.getEnvelope().Intersect(Poly2.getEnvelope()))
				continue;

			// 分析面积
			HashMap<String, Object> IntersectResult = SpatialAnalysisTools.Poly_IntersectArea(pSelectPoly, Poly2);

			// 整理相交结果
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
		// 保存属性数据
		this._BaseObject.RefreshViewValueToData();
		if (mPhotoControl != null) {
			this._BaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
		}

		if (!this._BaseObject.SaveFeatureToDb()) {
			Tools.ShowMessageBox(this.mOwnActivity, "数据保存失败！");
			// return;
		} else {
			Toast.makeText(mOwnActivity, "验证图斑数据已保存", Toast.LENGTH_SHORT).show();
			// Spinner spBHYY = (Spinner) mView.findViewById(R.id.sp_tbbhyy);
			// if (spBHYY.getSelectedItemPosition() < 5) {
			// Tools.ShowYesNoMessage(mOwnActivity, "图斑验证信息已保存，是否继续填写检查卡片？", new
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
		// 1、读取面分析的设置信息，格式详见：v1_UserConfigDB_PolyAnalysisOption
		v1_UserConfigDB_PolyAnalysisOption m_PAO = new v1_UserConfigDB_PolyAnalysisOption();
		List<HashMap<String, Object>> OptList = m_PAO.GetPolyAnalysisOption();

		// 2、提取需要分析的面层
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

			// 判断统计字段是否有效
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
			// 没有统计字段，默认用SYS_ID进行统计
			HashMap<String, Object> hmObj = new HashMap<String, Object>();
			hmObj.put("Dataset", pDataset);
			hmObj.put("FieldNameList", FieldList);
			polyDatasetList.add(hmObj);
		}

		// 如果为0，提示需要进行分析设置
		if (polyDatasetList.size() == 0) {
			Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "没有设置森林督查计算基准图层，请到图层->图层设置勾选森林督查计算基准图层？", new ICallback() {
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
				Toast.makeText(PubVar.m_DoEvent.m_Context, "有包含图斑未在图上显示，无法准确计算面积和蓄积", Toast.LENGTH_LONG).show();
				return false;

			}

			for (HashMap<String, Object> pDatasetInfo : polyDatasetList) {
				Dataset pDataset = (Dataset) pDatasetInfo.get("Dataset");
				List<String> FieldList = (List<String>) pDatasetInfo.get("FieldNameList");

				// 分析指定面层，返回格式：result["SYSID"],result["Area"]
				HashMap<String, Object> result = this.CalPolyLayer(pDataset, _SelectPoly);
				if (result.size() == 0)
					continue;
				List<String> SYSIDList = new ArrayList<String>();
				for (String id : result.keySet())
					SYSIDList.add(id);

				// 分类整理，主要是根据设置选项中的字段列进行分类汇总
				HashMap<String, Object> STTypeList = new HashMap<String, Object>();
				String SQL = "select (%1$s) as STType,SYS_ID from %2$s where SYS_ID in (%3$s)";
				SQL = String.format(SQL, Tools.JoinT("||','||", FieldList), pDataset.getDataTableName(),
						Tools.JoinT(",", SYSIDList));
				SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
				if (DR != null)
					while (DR.Read()) {
						String STType = DR.GetString("STType"); // 比如：旱地，水地
						String SYSID = DR.GetString("SYS_ID");
						double B = Double.parseDouble(result.get(SYSID) + ""); // 计算面积

						if (STTypeList.containsKey(STType)) // 累计
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

							Tools.ShowMessageBox("面积分析设置字段有误，请设置DI_LEI,SEN_LIN_LB,SHI_QUAN_D,GJGYL_BHDJ,BH_DJ,HUO_LMGQXJ六个字段.");
							return false;
						}
						
						
						
						
						// 事权等级 10.国家公益林 20.地方公益林
						if (Keys[2] != null && !Keys[2].isEmpty()) {
							if (Keys[2].equals("10")) {
								if (Keys[3] != null && !Keys[3].isEmpty()) {//国家公益林保护等级
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
									} else if (Keys[3].equals("3"))// 原国家公益林等级是3级的并入2级
									{
										GJGYL2 += area;
//										((EditText) mView.findViewById(R.id.a46)).setText(df2.format(GJGYL2 / 10000));
									}
								}
							} else if (Keys[2].equals("20")) {
								DFGYL += area;
								((EditText) mView.findViewById(R.id.et_wfwgzdfgylmj)).setText(df2.format(DFGYL / 10000));
							}
						} else {//森林类别
							if (Keys[1] != null && !Keys[1].isEmpty())// 商品林
							{
								if (Keys[0].startsWith("02") || Keys[0].startsWith("2")) {
									SPL += area;
									((EditText) mView.findViewById(R.id.et_wfwgzsplmj)).setText(df2.format(SPL / 10000));
								}
							}
						}
						
						// 地类
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
									Log.e("蓄积", ex.getMessage());
								}
								
								} 
//							else if (Keys[0].startsWith("12") || Keys[0].startsWith("012")) {
//								dlHSL += area;
//								((EditText) mView.findViewById(R.id.et_wfwgzhslmj)).setText(df2.format(dlHSL / 10000));
////								a36 = Double.parseDouble(df2.format(a36 / 10000));//汇总验证用
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
