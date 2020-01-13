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
		titleContainer.add("��Ƭ�ж�");
		titleContainer.add("�ֵص���");
		titleContainer.add("�������");
		titleContainer.add("��Ƭ");
		
		viewPager = (ViewPager)mOwnActivity.findViewById(R.id.wpViewPager);
		
		viewPager.setAdapter(new PagerAdapter() {
			//viewpager�е��������
			@Override
			public int getCount() {
				return viewContainter.size();
			}
          //�����л���ʱ�����ٵ�ǰ�����
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				((ViewPager) container).removeView(viewContainter.get(position));
			}
          //ÿ�λ�����ʱ�����ɵ����
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
		//ȡXY����
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
    		if (Tag.equals("����"))
    		{
    			HiddenView();
    		}
    		if (Tag.equals("����"))
    		{
    			saveData();
    		}
    		
    		if(Tag.equals("��Ƭ�ж�"))
    		{
    			viewPager.setCurrentItem(0);
    		}
    		if(Tag.equals("�ֵ���֤"))
    		{
    			viewPager.setCurrentItem(1);
    		}
    		if(Tag.equals("��Ƭ"))
    		{
    			viewPager.setCurrentItem(3);
    		}
    		if(Tag.equals("�������"))
    		{
    			viewPager.setCurrentItem(2);
    		}
    		
    		if(Tag.equals("GPS����"))
    		{
    			//StartGPSCalc();
    		}
    		
    		if(Tag.equals("��ȡ�·�����"))
    		{
    			getWeipianXiafaData();
    		}
    		
    	}
    }
	
	private void getWeipianXiafaData()
	{
		if(mLayer.getWeiPianDataLayer().isEmpty())
		{
			Tools.ShowMessageBox("ͼ��û�й��������·�ͼ�㣬���ȹ�����Ӧ����Ƭִ�������·�ͼ�㣡");
			return;
		}
		
		v1_Layer xiafaLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(mLayer.getWeiPianDataLayer());
		if(xiafaLayer == null)
		{
			Tools.ShowMessageBox("��������Ƭִ�������·�ͼ����Ч�������¹�����");
			return;
		}
		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(xiafaLayer.GetLayerID());
		String tubanDataField = mLayer.GetDataFieldNameByFieldName("ͼ�ߺ�");
		String tubanhao = Tools.GetTextValueOnID(mOwnActivity, R.id.et_tfh);
		if(tubanhao == null ||tubanhao.isEmpty())
		{
			Tools.ShowMessageBox("����дͼ�ߺ�");
			return;
		}
		
		try
		{
			String sql = "select * from "+pDataset.getDataTableName()+" where "+tubanDataField+"= '"+tubanhao+"'";
			SQLiteDataReader DR = pDataset.getDataSource().Query(sql);
			if(DR.Read())
			{
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_wpzfds, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("����")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_wpzfqx, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("��")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_tfh, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("ͼ�ߺ�")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_xz, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("����")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_jzc, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("��")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_snhzb, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("������")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_snzzb, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("������")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_pdmj, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("�ж����")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_ldyj, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("�ֵ�����")));
				Tools.SetTextViewValueOnID(mOwnActivity, R.id.et_pdbz, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("��ע")));
				
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_ydl, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("ǰ����")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_xdl, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("�ֵ���")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_bhlx, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("�仯����")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_sfsh, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("�Ƿ�����")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_mxcd, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("���Գ̶�")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_ywcflm, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("���޲ɷ�")));
				Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_sfzw, DR.GetString(xiafaLayer.GetDataFieldNameByFieldName("�Ƿ�����")));
			}
			else
			{
				Tools.ShowMessageBox("û���ڹ����������·�ͼ���ҵ���Ӧ��ͼ�ߺ�");
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
		
		if(!isGpsCalc)//�Ѿ���ʼ����
		{
			if (!lkmap.Tools.Tools.ReadyGPS(true))
			{
				return;
			}
			
			this.mGpsMeasure.m_DataCollectStatus = lkGpsReceiveDataStatus.enStarting;
			PubVar.m_GPSMap.SetGpsMeasure(this.mGpsMeasure);
			//mGpsMeasure.Stop(lkGeoLayerType.enPolygon);
			isGpsCalc = true;
			
			((Button)mOwnActivity.findViewById(R.id.bt_GpsCL)).setText("ֹͣGPS����");
		}
		else//δ��ʼ����
		{
			this.mGpsMeasure.m_DataCollectStatus = lkGpsReceiveDataStatus.enStop;
			PubVar.m_GPSMap.SetGpsMeasure(null);
			isGpsCalc = false;
			((Button)mOwnActivity.findViewById(R.id.bt_GpsCL)).setText("��ʼGPS����");
		}
		
		
	}
	
	private void bindSpinnerAdapter()
	{
		
		String s ="��,��";
		String[] boolItems = "��,��".split(",");
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
		
		//��Ƭ�ж�-���޲ŷ���ľ
		Spinner wpywlmcf =(Spinner) mOwnActivity.findViewById(R.id.sp_ywcflm);
		wpywlmcf.setAdapter(boolAdapter);
		
		
		String[] yuandilei = "��ľ�ֵء����ֵء����ֵء������ľ�ֵء�һ���ľ�ֵء�δ�������ֵء����Եء����ء����ֵء������ֵ�".split("��");
		ArrayAdapter<String> qdlAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																	android.R.layout.simple_spinner_item,
																	yuandilei);
		qdlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner wppdqdl =(Spinner) mOwnActivity.findViewById(R.id.sp_ydl);
		wppdqdl.setAdapter(qdlAdapter);
		
		Spinner xdqdl =(Spinner) mOwnActivity.findViewById(R.id.sp_xdydl);
		xdqdl.setAdapter(qdlAdapter);
		
		
		String[] xiandilei = "��ľ�ֵء����ֵء����ֵء������ľ�ֵء�һ���ľ�ֵء�δ�������ֵء����Եء����ء����ֵء������ֵ� �����ء���ͨ�����õء�ˮ��ˮ����ʩ�õء����罨���õء�����ɿ��õء����������õ�".split("��");
		ArrayAdapter<String> xdlAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																android.R.layout.simple_spinner_item,
																xiandilei);
		xdlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner wppdxdl =(Spinner) mOwnActivity.findViewById(R.id.sp_xdl);
		wppdxdl.setAdapter(xdlAdapter);

		Spinner xdxdl =(Spinner) mOwnActivity.findViewById(R.id.sp_xdxdl);
		xdxdl.setAdapter(xdlAdapter);

		String[] bianhualeixing = "�ֵر���ء��ֵر佻ͨ�����õء��ֵر�ˮ��ˮ����ʩ�õء��ֵر���罨���õء��ֵر俱��ɿ��õء��ֵر����������õ�".split("��");
		ArrayAdapter<String> bhlxAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																	android.R.layout.simple_spinner_item,
																	bianhualeixing);
		bhlxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner bhlx =(Spinner) mOwnActivity.findViewById(R.id.sp_bhlx);
		bhlx.setAdapter(bhlxAdapter);
		
		String[] minanxianchengdu = "��������,����������".split(",");
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
		//������������
    	this.mBaseObject.RefreshViewValueToData();
    	if(mPhotoControl != null)
    	{
    		this.mBaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
    	}
    	
    	//this.mBaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
		if (!this.mBaseObject.SaveFeatureToDb())
		{

			Tools.ShowMessageBox(this.mOwnActivity,"���ݱ���ʧ�ܣ�");
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
		
		//mBaseObject.AddDataBindItem(new DataBindOfKeyValue("", mLayer.GetDataFieldNameByFieldName("ʡ"),"", mOwnActivity.findViewById(R.id.et_sheng)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("����", mLayer.GetDataFieldNameByFieldName("����"),"", mOwnActivity.findViewById(R.id.et_wpzfds)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("��", mLayer.GetDataFieldNameByFieldName("��"),"", mOwnActivity.findViewById(R.id.et_wpzfqx)));
		
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("ͼ�ߺ�", mLayer.GetDataFieldNameByFieldName("ͼ�ߺ�"),"", mOwnActivity.findViewById(R.id.et_tfh)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("����", mLayer.GetDataFieldNameByFieldName("����"),"", mOwnActivity.findViewById(R.id.et_xz)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("��", mLayer.GetDataFieldNameByFieldName("��"),"", mOwnActivity.findViewById(R.id.et_jzc)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("������", mLayer.GetDataFieldNameByFieldName("������"),"", mOwnActivity.findViewById(R.id.et_snhzb)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("������", mLayer.GetDataFieldNameByFieldName("������"),"", mOwnActivity.findViewById(R.id.et_snzzb)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("ǰ����", mLayer.GetDataFieldNameByFieldName("ǰ����"),"", mOwnActivity.findViewById(R.id.sp_ydl)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�ֵ���", mLayer.GetDataFieldNameByFieldName("�ֵ���"),"", mOwnActivity.findViewById(R.id.sp_xdl)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�仯����", mLayer.GetDataFieldNameByFieldName("�仯����"),"", mOwnActivity.findViewById(R.id.sp_bhlx)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("���Գ̶�", mLayer.GetDataFieldNameByFieldName("���Գ̶�"),"", mOwnActivity.findViewById(R.id.sp_mxcd)));
		
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�ж����", mLayer.GetDataFieldNameByFieldName("�ж����"),"", mOwnActivity.findViewById(R.id.et_pdmj)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("���޲ɷ�", mLayer.GetDataFieldNameByFieldName("���޲ɷ�"),"", mOwnActivity.findViewById(R.id.sp_ywcflm)));
		
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�Ƿ�����", mLayer.GetDataFieldNameByFieldName("�Ƿ�����"),"", mOwnActivity.findViewById(R.id.sp_sfsh)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�Ƿ�����", mLayer.GetDataFieldNameByFieldName("�Ƿ�����"),"", mOwnActivity.findViewById(R.id.sp_sfzw)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�ֵ�����", mLayer.GetDataFieldNameByFieldName("�ֵ�����"),"", mOwnActivity.findViewById(R.id.et_ldyj)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("��ע", mLayer.GetDataFieldNameByFieldName("��ע"),"", mOwnActivity.findViewById(R.id.et_pdbz)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�ж���Ա", mLayer.GetDataFieldNameByFieldName("�ж���Ա"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�Ǽ�����", mLayer.GetDataFieldNameByFieldName("�Ǽ�����"),"", mOwnActivity.findViewById(R.id.et_dishi)));
		
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("������3", mLayer.GetDataFieldNameByFieldName("������3"),"", mOwnActivity.findViewById(R.id.et_xdhzb)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("������3", mLayer.GetDataFieldNameByFieldName("������3"),"", mOwnActivity.findViewById(R.id.et_xdzzb)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("ǰ����3", mLayer.GetDataFieldNameByFieldName("ǰ����3"),"", mOwnActivity.findViewById(R.id.sp_xdydl)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�ֵ���3", mLayer.GetDataFieldNameByFieldName("�ֵ���3"),"", mOwnActivity.findViewById(R.id.sp_xdxdl)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("��;�仯", mLayer.GetDataFieldNameByFieldName("��;�仯"),"", mOwnActivity.findViewById(R.id.sp_ldyusfbh)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("���޲ɷ�3", mLayer.GetDataFieldNameByFieldName("���޲ɷ�3"),"", mOwnActivity.findViewById(R.id.sp_xdywlmcf)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�Ƿ�����3", mLayer.GetDataFieldNameByFieldName("�Ƿ�����3"),"", mOwnActivity.findViewById(R.id.sp_xdsfzw)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("��ע3", mLayer.GetDataFieldNameByFieldName("��ע3"),"", mOwnActivity.findViewById(R.id.et_xdbz)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("������", mLayer.GetDataFieldNameByFieldName("������"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("��������", mLayer.GetDataFieldNameByFieldName("��������"),"", mOwnActivity.findViewById(R.id.et_dishi)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�����鵥λ", mLayer.GetDataFieldNameByFieldName("�����鵥λ"),"", mOwnActivity.findViewById(R.id.et_bdcdw)));
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("��������", mLayer.GetDataFieldNameByFieldName("��������"),"", mOwnActivity.findViewById(R.id.et_bdcr)));	
		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("�����¼", mLayer.GetDataFieldNameByFieldName("�����¼"),"", mOwnActivity.findViewById(R.id.et_dcjl)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("������", mLayer.GetDataFieldNameByFieldName("������"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("��¼����", mLayer.GetDataFieldNameByFieldName("��¼����"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("����������", mLayer.GetDataFieldNameByFieldName("����������"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("������", mLayer.GetDataFieldNameByFieldName("������"),"", mOwnActivity.findViewById(R.id.et_dishi)));
//		mBaseObject.AddDataBindItem(new DataBindOfKeyValue("����ʱ��", mLayer.GetDataFieldNameByFieldName("����ʱ��"),"", mOwnActivity.findViewById(R.id.et_dishi)));
		
		
	}
}
