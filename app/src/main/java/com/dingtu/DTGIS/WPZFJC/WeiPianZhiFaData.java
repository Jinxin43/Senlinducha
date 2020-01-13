package com.dingtu.DTGIS.WPZFJC;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.TuiGeng.TuiGengDataObject;
import com.dingtu.Funtion.PhotoControl;
import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import dingtu.ZRoadMap.HashValueObject;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.DataBindOfKeyValue;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_BaseDataObject;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Polygon;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGpsReceiveDataStatus;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;

public class WeiPianZhiFaData 
{
	private v1_Layer mLayer;
	private int mObjId = 0;
	private Activity mOwnActivity = null;
    private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private PhotoControl mPhotoControl;
	private ViewPager viewPager;
	private v1_CGpsDataObject mBaseObject;
	private WeiPianZhiFa_GPSMeasure mGpsMeasure= null;
	private boolean isGpsCalc = false;
	
	public WeiPianZhiFaData(String layerID, int dataID)
	{
		mObjId = dataID;
		mLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID);
		mOwnActivity = (Activity)PubVar.m_DoEvent.m_Context;
		
		initBaseObject();
		initViewPager();
		initBindField();
		bindSpinnerAdapter();
		((Button)mOwnActivity.findViewById(R.id.wpzf_quit)).setOnClickListener(new ViewClick());
		((Button)mOwnActivity.findViewById(R.id.wpzf_save)).setOnClickListener(new ViewClick());
		 mOwnActivity.findViewById(R.id.bt_viewpager_wppd).setOnClickListener(new ViewClick());
        mOwnActivity.findViewById(R.id.bt_viewpager_sdyz).setOnClickListener(new ViewClick());
        mOwnActivity.findViewById(R.id.bt_viewpager_mjcl).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.bt_viewpager_wpphoto).setOnClickListener(new ViewClick());
		mOwnActivity.findViewById(R.id.btn_getXiafadata).setOnClickListener(new ViewClick());
		//mOwnActivity.findViewById(R.id.ll_WPZF).setVisibility(View.VISIBLE);
		mOwnActivity.findViewById(R.id.bt_GpsCL).setOnClickListener(new ViewClick());
		setEditInfo();
	}
	
	private void initViewPager()
	{
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.weipianzhifa_snpd, null));
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.weipianzhifa_xdhs, null));
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.weipianzhifa_mjcl, null));
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.v1_data_template_photo, null));
		titleContainer.add("卫片判定");
		titleContainer.add("现地调查");
		titleContainer.add("面积测量");
		titleContainer.add("照片");
		
		viewPager = (ViewPager)mOwnActivity.findViewById(R.id.wpViewPager);
		
		viewPager.setAdapter(new PagerAdapter() {
			//viewpager中的组件数量
			@Override
			public int getCount() {
				return viewContainter.size();
			}
          //滑动切换的时候销毁当前的组件
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				((ViewPager) container).removeView(viewContainter.get(position));
			}
          //每次滑动的时候生成的组件
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				
				
				((ViewPager) container).addView(viewContainter.get(position));
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
			public void onPageSelected(int arg0) 
			{
				changePageViewIndex(arg0);
				if(arg0 == 3)
				{
					if(mPhotoControl == null)
					{
						initPhotoPager();
					}
					
					mPhotoControl.SetXiaoBanInfo(Tools.GetTextValueOnID(mOwnActivity, R.id.et_wpzfqx),
												Tools.GetTextValueOnID(mOwnActivity, R.id.et_xz),
												Tools.GetTextValueOnID(mOwnActivity, R.id.et_jzc), 
												"",
												Tools.GetTextValueOnID(mOwnActivity, R.id.et_tfh),
												Tools.GetTextValueOnID(mOwnActivity, R.id.et_xdhzb),
												Tools.GetTextValueOnID(mOwnActivity, R.id.et_xdzzb));
					
				}
			}
		});
		
		viewPager.setOffscreenPageLimit(3);
	}
	
	
	private void changePageViewIndex(int position)
	{
		
		Button btnXBXX = (Button)mOwnActivity.findViewById(R.id.bt_viewpager_wppd);
		Button btnSJXX =  (Button)mOwnActivity.findViewById(R.id.bt_viewpager_sdyz);
		Button btnPhoto =  (Button)mOwnActivity.findViewById(R.id.bt_viewpager_wpphoto);
		Button btnDuixian =  (Button)mOwnActivity.findViewById(R.id.bt_viewpager_mjcl);
		
		if(position == 0)
		{
			btnSJXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnSJXX.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnPhoto.setTextColor(android.graphics.Color.BLACK);
			btnDuixian.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnDuixian.setTextColor(android.graphics.Color.BLACK);
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnXBXX.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.wplocator1).setVisibility(View.INVISIBLE);
			mOwnActivity.findViewById(R.id.wplocator2).setVisibility(View.VISIBLE);
		}
		if(position == 1)
		{
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnXBXX.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnPhoto.setTextColor(android.graphics.Color.BLACK);
			btnDuixian.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnDuixian.setTextColor(android.graphics.Color.BLACK);
			btnSJXX.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnSJXX.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.wplocator1).setVisibility(View.INVISIBLE);
			mOwnActivity.findViewById(R.id.wplocator2).setVisibility(View.INVISIBLE);
			mOwnActivity.findViewById(R.id.wplocator3).setVisibility(View.INVISIBLE);
		}
		
		if(position == 3)
		{
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnXBXX.setTextColor(android.graphics.Color.BLACK);
			btnSJXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnSJXX.setTextColor(android.graphics.Color.BLACK);
			btnDuixian.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnDuixian.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnPhoto.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.wplocator1).setVisibility(View.VISIBLE);
			mOwnActivity.findViewById(R.id.wplocator2).setVisibility(View.VISIBLE);
			mOwnActivity.findViewById(R.id.wplocator3).setVisibility(View.INVISIBLE);
		}
		
		if(position == 2)
		{
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnXBXX.setTextColor(android.graphics.Color.BLACK);
			btnSJXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnSJXX.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnPhoto.setTextColor(android.graphics.Color.BLACK);
			btnDuixian.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnDuixian.setTextColor(android.graphics.Color.WHITE);
			mOwnActivity.findViewById(R.id.wplocator1).setVisibility(View.VISIBLE);
			mOwnActivity.findViewById(R.id.wplocator2).setVisibility(View.INVISIBLE);
			mOwnActivity.findViewById(R.id.wplocator3).setVisibility(View.INVISIBLE);
		}
	}
	
	private void setEditInfo()
	{
		this.mBaseObject.ReadDataAndBindToView("SYS_ID="+mObjId);
		//取XY坐标
		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
		Geometry pGeometry = pDataset.GetGeometry(mObjId);
	  	if(pGeometry == null)
	  	{
	  		List<String> SYSIDList = new ArrayList<String>();
	  		SYSIDList.add(mObjId+"");
	  		List<Geometry> pGeometryList = pDataset.QueryGeometryFromDB1(SYSIDList);
	  		if (pGeometryList.size()!=0)
				{
					pGeometry = pGeometryList.get(0);
				}
	  	}
	  	if(pGeometry != null)
	  	{
	  		Coordinate middle = pGeometry.getCenterPoint();
	  		Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_xdhzb, Tools.ConvertToDigi(middle.getX()+"",2));
	  		Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_xdzzb, Tools.ConvertToDigi(middle.getY()+"",2));
	  	}
	  	
	  	if(mLayer.GetLayerType() == lkGeoLayerType.enPolygon)
		{
	  		DecimalFormat df2 = new DecimalFormat("0.0000");
			Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_gpscalc, df2.format(((Polygon)pGeometry).getArea(true)/10000));
		}
	  	
	}
	
	public class ViewClick implements OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if (Tag.equals("返回"))
    		{
    			HiddenView();
    		}
    		if (Tag.equals("保存"))
    		{
    			saveData();
    		}
    		
    		if(Tag.equals("卫片判定"))
    		{
    			viewPager.setCurrentItem(0);
    		}
    		if(Tag.equals("现地验证"))
    		{
    			viewPager.setCurrentItem(1);
    		}
    		if(Tag.equals("照片"))
    		{
    			viewPager.setCurrentItem(3);
    		}
    		if(Tag.equals("面积测量"))
    		{
    			viewPager.setCurrentItem(2);
    		}
    		
    		if(Tag.equals("GPS测量"))
    		{
    			//StartGPSCalc();
    		}
    		
    		if(Tag.equals("获取下发数据"))
    		{
    			getWeipianXiafaData();
    		}
    		
    	}
    }
	
	private void getWeipianXiafaData()
	{
		if(mLayer.getWeiPianDataLayer().isEmpty())
		{
			Tools.ShowMessageBox("图层没有关联数据下发图层，请先关联对应的卫片执法数据下发图层！");
			return;
		}
		
		v1_Layer xiafaLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(mLayer.getWeiPianDataLayer());
		if(xiafaLayer == null)
		{
			Tools.ShowMessageBox("关联的卫片执法数据下发图层无效，请重新关联！");
			return;
		}
		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(xiafaLayer.GetLayerID());
		String tubanDataField = mLayer.GetDataFieldNameByFieldName("图斑号");
		String tubanhao = Tools.GetTextValueOnID(mOwnActivity, R.id.et_tfh);
		if(tubanhao == null ||tubanhao.isEmpty())
		{
			Tools.ShowMessageBox("请填写图斑号");
			return;
		}
		
		try
		{
			String sql = "select * from "+pDataset.getDataTableName()+" where "+tubanDataField+"= '"+tubanhao+"'";
			SQLiteDataReader DR = pDataset.getDataSource().Query(sql);
			if(DR.Read())
			{
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_wpzfds, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("地市")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_wpzfqx, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("县")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_tfh, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("图斑号")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_xz, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("乡镇")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jzc, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("村")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_snhzb, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("横坐标")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_snzzb, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("纵坐标")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_pdmj, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("判读面积")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_ldyj, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("林地依据")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_pdbz, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("备注")));
				
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_ydl, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("前地类")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_xdl, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("现地类")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_bhlx, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("变化类型")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_sfsh, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("是否审批")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_mxcd, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("明显程度")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_ywcflm, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("有无采伐")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_sfzw, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("是否掌握")));
			}
			else
			{
				Tools.ShowMessageBox("没有在关联的数据下发图层找到对应的图斑号");
			}
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(ex.getMessage());
		}
		
	}
	
	private void StartGPSCalc()
	{
		
		if(mGpsMeasure == null)
		{	
			this.mGpsMeasure = new WeiPianZhiFa_GPSMeasure();
			mGpsMeasure.SetCallback(new ICallback(){

				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
					HashMap<String,Object> result = (HashMap<String,Object>)ExtraStr;
					double area = Double.parseDouble(result.get("area")+"");
					DecimalFormat df2 = new DecimalFormat("0.000");
					Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_gpscalc, df2.format(area/10000));
					//TODO:add point to list
				}
				
			});
			
		}
		
		if(!isGpsCalc)//已经开始测量
		{
			if (!lkmap.Tools.Tools.ReadyGPS(true))
			{
				return;
			}
			
			this.mGpsMeasure.m_DataCollectStatus = lkGpsReceiveDataStatus.enStarting;
			PubVar.m_GPSMap.SetGpsMeasure(this.mGpsMeasure);
			//mGpsMeasure.Stop(lkGeoLayerType.enPolygon);
			isGpsCalc = true;
			
			((Button)mOwnActivity.findViewById(R.id.bt_GpsCL)).setText("停止GPS测量");
		}
		else//未开始测量
		{
			this.mGpsMeasure.m_DataCollectStatus = lkGpsReceiveDataStatus.enStop;
			PubVar.m_GPSMap.SetGpsMeasure(null);
			isGpsCalc = false;
			((Button)mOwnActivity.findViewById(R.id.bt_GpsCL)).setText("开始GPS测量");
		}
		
		
	}
	
	private void bindSpinnerAdapter()
	{
		
		String s ="否,是";
		String[] boolItems = "否,是".split(",");
		ArrayAdapter<String> boolAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																	android.R.layout.simple_spinner_item,
																	boolItems);
		boolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spnIsBianGen =(Spinner) mOwnActivity.findViewById(R.id.sp_sfsh);
		spnIsBianGen.setAdapter(boolAdapter);
		
		Spinner spnIszhangwo =(Spinner) mOwnActivity.findViewById(R.id.sp_sfzw);
		spnIszhangwo.setAdapter(boolAdapter);
		
		Spinner xdspnIszhangwo =(Spinner) mOwnActivity.findViewById(R.id.sp_xdsfzw);
		xdspnIszhangwo.setAdapter(boolAdapter);
		
		Spinner ytsfbh =(Spinner) mOwnActivity.findViewById(R.id.sp_ldyusfbh);
		ytsfbh.setAdapter(boolAdapter);
		
		Spinner ywlmcf =(Spinner) mOwnActivity.findViewById(R.id.sp_xdywlmcf);
		ywlmcf.setAdapter(boolAdapter);
		
		//卫片判定-有无才发林木
		Spinner wpywlmcf =(Spinner) mOwnActivity.findViewById(R.id.sp_ywcflm);
		wpywlmcf.setAdapter(boolAdapter);
		
		
		String[] yuandilei = "乔木林地、竹林地、疏林地、特殊灌木林地、一般灌木林地、未成林造林地、苗圃地、迹地、宜林地、其他林地".split("、");
		ArrayAdapter<String> qdlAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																	android.R.layout.simple_spinner_item,
																	yuandilei);
		qdlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner wppdqdl =(Spinner) mOwnActivity.findViewById(R.id.sp_ydl);
		wppdqdl.setAdapter(qdlAdapter);
		
		Spinner xdqdl =(Spinner) mOwnActivity.findViewById(R.id.sp_xdydl);
		xdqdl.setAdapter(qdlAdapter);
		
		
		String[] xiandilei = "乔木林地、竹林地、疏林地、特殊灌木林地、一般灌木林地、未成林造林地、苗圃地、迹地、宜林地、其他林地 、耕地、交通运输用地、水域及水利设施用地、城乡建设用地、勘查采矿用地、其它建设用地".split("、");
		ArrayAdapter<String> xdlAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																android.R.layout.simple_spinner_item,
																xiandilei);
		xdlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner wppdxdl =(Spinner) mOwnActivity.findViewById(R.id.sp_xdl);
		wppdxdl.setAdapter(xdlAdapter);

		Spinner xdxdl =(Spinner) mOwnActivity.findViewById(R.id.sp_xdxdl);
		xdxdl.setAdapter(xdlAdapter);

		String[] bianhualeixing = "林地变耕地、林地变交通运输用地、林地变水域及水利设施用地、林地变城乡建设用地、林地变勘查采矿用地、林地变其它建设用地".split("、");
		ArrayAdapter<String> bhlxAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																	android.R.layout.simple_spinner_item,
																	bianhualeixing);
		bhlxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner bhlx =(Spinner) mOwnActivity.findViewById(R.id.sp_bhlx);
		bhlx.setAdapter(bhlxAdapter);
		
		String[] minanxianchengdu = "特征明显,特征不明显".split(",");
		ArrayAdapter<String> mxcdAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
											android.R.layout.simple_spinner_item,
											minanxianchengdu);
		mxcdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner mxcd =(Spinner) mOwnActivity.findViewById(R.id.sp_mxcd);
		mxcd.setAdapter(mxcdAdapter);
	}
	
	public void ShowView()
	{
		try
		{
			try
			{
				View v = mOwnActivity.findViewById(R.id.ll_WPZF);
				v.setVisibility(View.VISIBLE);
				Animation anim = new TranslateAnimation(200,0, 0, 0);
				anim.setFillAfter(true);
				anim.setDuration(200);
				v.setAnimation(anim);
				changePageViewIndex(0);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		catch(Exception ex)
		{
			
		}
		
	}
	
	public void HiddenView()
	{
		try
		{
			mOwnActivity.findViewById(R.id.ll_WPZF).setVisibility(View.GONE);
			PubVar.m_GPSMap.SetGpsMeasure(null);
		}
		catch(Exception ex)
		{
			
		}
	}
	
	private void saveData()
	{
		//保存属性数据
    	this.mBaseObject.RefreshViewValueToData();
    	if(mPhotoControl != null)
    	{
    		this.mBaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
    	}
    	
    	//this.mBaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
		if (!this.mBaseObject.SaveFeatureToDb())
		{

			Tools.ShowMessageBox(this.mOwnActivity,"数据保存失败！");
			return;
		}
		
		HiddenView();
		
	}
	
	private void initPhotoPager()
	{
		initBaseObject();
		List<String> mPhotoNameList = new ArrayList<String>();
		if(this.mBaseObject.GetSYS_PHOTO() != null && this.mBaseObject.GetSYS_PHOTO().length()>0)
		{
			mPhotoNameList = Tools.StrArrayToList(this.mBaseObject.GetSYS_PHOTO().split(","));
		}
		
		HashValueObject hvoWaterMark =  PubVar.m_HashMap.GetValueObject("Tag_Photo_WaterMark", false);
		if(hvoWaterMark != null && hvoWaterMark.Value.equals("true"))
		{
			mPhotoControl = new PhotoControl(mObjId,mPhotoNameList,true,false,viewContainter.get(3));
		}
		else
		{
			mPhotoControl = new PhotoControl(mObjId,mPhotoNameList,false,false,viewContainter.get(3));
		}
		mPhotoControl.setNeedTuban(true);
		
	}
	
	private void initBaseObject()
	{
		if(mBaseObject == null)
		{
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
			this.mBaseObject = new v1_CGpsDataObject();
		    this.mBaseObject.SetDataset(pDataset);
	        this.mBaseObject.SetSYS_ID(mObjId); 
	        this.mBaseObject.ReadDataAndBindToView("SYS_ID="+mObjId); 
		}
	}
	
	private void initBindField()
	{
		if(mBaseObject == null)
		{
			initBaseObject();
		}
		
		//mBaseObject.AddDataBindItem(new DataBindOfKeyValue("", mLayer.GetDataFieldNameByFieldName("省"),"", mOwnActivity.findViewById(R.id.et_sheng)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("地市", mLayer.GetDataFieldNameByFieldName("地市"),"", mOwnActivity.findViewById(R.id.et_wpzfds)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("县", mLayer.GetDataFieldNameByFieldName("县"),"", mOwnActivity.findViewById(R.id.et_wpzfqx)));
		
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("图斑号", mLayer.GetDataFieldNameByFieldName("图斑号"),"", mOwnActivity.findViewById(R.id.et_tfh)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("乡镇", mLayer.GetDataFieldNameByFieldName("乡镇"),"", mOwnActivity.findViewById(R.id.et_xz)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("村", mLayer.GetDataFieldNameByFieldName("村"),"", mOwnActivity.findViewById(R.id.et_jzc)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("横坐标", mLayer.GetDataFieldNameByFieldName("横坐标"),"", mOwnActivity.findViewById(R.id.et_snhzb)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("纵坐标", mLayer.GetDataFieldNameByFieldName("纵坐标"),"", mOwnActivity.findViewById(R.id.et_snzzb)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("前地类", mLayer.GetDataFieldNameByFieldName("前地类"),"", mOwnActivity.findViewById(R.id.sp_ydl)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("现地类", mLayer.GetDataFieldNameByFieldName("现地类"),"", mOwnActivity.findViewById(R.id.sp_xdl)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("变化类型", mLayer.GetDataFieldNameByFieldName("变化类型"),"", mOwnActivity.findViewById(R.id.sp_bhlx)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("明显程度", mLayer.GetDataFieldNameByFieldName("明显程度"),"", mOwnActivity.findViewById(R.id.sp_mxcd)));
		
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("判读面积", mLayer.GetDataFieldNameByFieldName("判读面积"),"", mOwnActivity.findViewById(R.id.et_pdmj)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("有无采伐", mLayer.GetDataFieldNameByFieldName("有无采伐"),"", mOwnActivity.findViewById(R.id.sp_ywcflm)));
		
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("是否审批", mLayer.GetDataFieldNameByFieldName("是否审批"),"", mOwnActivity.findViewById(R.id.sp_sfsh)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("是否掌握", mLayer.GetDataFieldNameByFieldName("是否掌握"),"", mOwnActivity.findViewById(R.id.sp_sfzw)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("林地依据", mLayer.GetDataFieldNameByFieldName("林地依据"),"", mOwnActivity.findViewById(R.id.et_ldyj)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("备注", mLayer.GetDataFieldNameByFieldName("备注"),"", mOwnActivity.findViewById(R.id.et_pdbz)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("判读人员", mLayer.GetDataFieldNameByFieldName("判读人员"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("登记日期", mLayer.GetDataFieldNameByFieldName("登记日期"),"", mOwnActivity.findViewById(R.id.et_dishi)));
		
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("横坐标3", mLayer.GetDataFieldNameByFieldName("横坐标3"),"", mOwnActivity.findViewById(R.id.et_xdhzb)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("纵坐标3", mLayer.GetDataFieldNameByFieldName("纵坐标3"),"", mOwnActivity.findViewById(R.id.et_xdzzb)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("前地类3", mLayer.GetDataFieldNameByFieldName("前地类3"),"", mOwnActivity.findViewById(R.id.sp_xdydl)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("现地类3", mLayer.GetDataFieldNameByFieldName("现地类3"),"", mOwnActivity.findViewById(R.id.sp_xdxdl)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("用途变化", mLayer.GetDataFieldNameByFieldName("用途变化"),"", mOwnActivity.findViewById(R.id.sp_ldyusfbh)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("有无采伐3", mLayer.GetDataFieldNameByFieldName("有无采伐3"),"", mOwnActivity.findViewById(R.id.sp_xdywlmcf)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("是否掌握3", mLayer.GetDataFieldNameByFieldName("是否掌握3"),"", mOwnActivity.findViewById(R.id.sp_xdsfzw)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("备注3", mLayer.GetDataFieldNameByFieldName("备注3"),"", mOwnActivity.findViewById(R.id.et_xdbz)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("调查者", mLayer.GetDataFieldNameByFieldName("调查者"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("调查日期", mLayer.GetDataFieldNameByFieldName("调查日期"),"", mOwnActivity.findViewById(R.id.et_dishi)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("被调查单位", mLayer.GetDataFieldNameByFieldName("被调查单位"),"", mOwnActivity.findViewById(R.id.et_bdcdw)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("被调查人", mLayer.GetDataFieldNameByFieldName("被调查人"),"", mOwnActivity.findViewById(R.id.et_bdcr)));	
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("调查记录", mLayer.GetDataFieldNameByFieldName("调查记录"),"", mOwnActivity.findViewById(R.id.et_dcjl)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("调查人", mLayer.GetDataFieldNameByFieldName("调查人"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("记录日期", mLayer.GetDataFieldNameByFieldName("记录日期"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("其他测量点", mLayer.GetDataFieldNameByFieldName("其他测量点"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("测量人", mLayer.GetDataFieldNameByFieldName("测量人"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("测量时间", mLayer.GetDataFieldNameByFieldName("测量时间"),"", mOwnActivity.findViewById(R.id.et_dishi)));
		
		
	}
}
