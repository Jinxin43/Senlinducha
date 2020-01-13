package com.dingtu.DTGIS.TuiGeng;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DictXZQH;
import com.dingtu.DTGIS.DataService.TuiGengDB;
import com.dingtu.DTGIS.DataService.UserConfigDB;
import com.dingtu.Funtion.PhotoControl;
import com.dingtu.senlinducha.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import dingtu.ZRoadMap.HashValueObject;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.DataBindOfKeyValue;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Polygon;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;

public class TuiGengData 
{
//	private Activity mOwnActivity = null;
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private TuiGengDataObject mBaseObject;
	private v1_Layer mLayer;
	private int mObjId = 0;
	private ViewPager viewPager;
	private String countyCode="";
	private Geometry pGeometry;
	private boolean mFirstEdit = false;
	public static ICallback _Callback = null;
	private PhotoControl mPhotoControl;
	private String coordX;
	private String coordY;
	private String mProjectType = "退耕还林";
	private HashMap<String,View> fieldControls = new HashMap<String,View>();
	private HashMap<String,View> fieldControlsClone = new HashMap<String,View>();
	ArrayList<HashMap<String,Object>> fenhuList = new ArrayList<HashMap<String,Object>>();
	private List<FieldView> mFieldViewList = new ArrayList<FieldView>();
	private String duixianNianDu = "";
	private View mOwnView;
	
	public TuiGengData(String layerID,int dataID,final boolean firstEdit)
	{
		
		mObjId = dataID;
		mLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID);
//		mOwnActivity = (Activity)PubVar.m_DoEvent.m_Context;
		mOwnView = ((Activity)PubVar.m_DoEvent.m_Context).findViewById(R.id.ll_quickshow);
		Button btnQuit = (Button)mOwnView.findViewById(R.id.tuigengdata_quit);
		btnQuit.setOnClickListener(new ViewClick());
		Button btnSave = (Button)mOwnView.findViewById(R.id.tuigengdata_save);
		btnSave.setOnClickListener(new ViewClick());
		initViewPager();
		
		new Handler().postDelayed(new Runnable() 
    	{
    	    public void run() 
    	    {
    			initFieldViewControl(mLayer);
    			initBaseObject();
    			
    			initDishi();
    			initXiangzhen();
    			
    			for(FieldView FV:mFieldViewList)
    	        {
    	        	mBaseObject.AddDataBindItem(new DataBindOfKeyValue(FV.FieldName, FV.DataFieldName,"", FV.FieldView));	
    	        }
    			
    			if(firstEdit)
    			{
    				mFirstEdit = true;
    				readPreviewToView();
    			}
    			else
    			{
    				setEditInfo();
    			}
    			calcTFH();
    			
    			mOwnView.findViewById(R.id.bt_viewpager_xbxx).setOnClickListener(new ViewClick());
    			mOwnView.findViewById(R.id.bt_viewpager_sjxx).setOnClickListener(new ViewClick());
    			mOwnView.findViewById(R.id.bt_viewpager_photo).setOnClickListener(new ViewClick());
    			mOwnView.findViewById(R.id.bt_viewpager_duixian).setOnClickListener(new ViewClick());
    			
    			changeKZYMJStatus();
    			initKZYMJEvent();
    			initInputEvent();
    			//放到最后
    			viewPager.setOffscreenPageLimit(3);
    			//changePageViewIndex(0);
    			initFenhu();
    			initDuixianYear();
    			initDishi();
    	    }}
    	  ,10);
		ShowView();
	}
	
	
	private void initDuixianYear()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy"); 
		duixianNianDu =sdf.format(new java.util.Date()); 
		try
		{
			int year = Integer.parseInt(duixianNianDu);
			if(year>1)
			{
				int lastYear = year-1;
				duixianNianDu = lastYear+"";
			}
		}
		catch(Exception ex)
		{
			
		}
	}
	
	public void ShowView()
	{
		try
		{
			mOwnView.setVisibility(View.VISIBLE);
			Animation anim = new TranslateAnimation(1000,0, 0, 0);
			anim.setFillAfter(true);
			anim.setDuration(600);
			mOwnView.setAnimation(anim);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public void HiddenView()
	{
		try
		{
			mOwnView.setVisibility(View.GONE);
			changePageViewIndex(0);
			viewPager.setOffscreenPageLimit(1);
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(ex.getMessage());
		}
	}
	
	public ICallback pCallback = new ICallback()
	{

		@Override
		public void OnClick(String Str, Object ExtraStr) 
		{
			if(Str.equals("分户"))
			{
				Spinner sp = (Spinner)mOwnView.findViewById(R.id.sp_ztsl);
				v1_DataBind.SetBindListSpinner(sp.getContext(), "", Tools.StrArrayToList(new String[]{ExtraStr+""}), sp);
			}
			
			if(Str.equals("新增兑现"))
			{
				duixianList.add((HashMap<String,Object>)ExtraStr);
				duixianNianDu = ((HashMap<String,Object>)ExtraStr).get("niandu")+"";
				refreshDuixianList(duixianList);
			}
			
			if(Str.equals("编辑兑现"))
			{
				editDuixian((HashMap<String,Object>)ExtraStr);
			}
		}

	};
	
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
    		
    		if(Tag.equals("小班信息"))
    		{
    			viewPager.setCurrentItem(0);
    		}
    		if(Tag.equals("设计信息"))
    		{
    			viewPager.setCurrentItem(1);
    		}
    		if(Tag.equals("照片"))
    		{
    			viewPager.setCurrentItem(3);
    		}
    		if(Tag.equals("兑现"))
    		{
    			viewPager.setCurrentItem(2);
    		}
    		
    		if(Tag.equals("新增兑现"))
    		{
    			String cishu = Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_jccs);
    			TuiGengData_AddDuiXian addDuiXian = new TuiGengData_AddDuiXian(true,mLayer.GetLayerID(), mObjId,cishu,duixianNianDu,Tools.GetTextValueOnID(mOwnView, R.id.et_mmdj),Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_zhj));
    			addDuiXian.SetCallback(pCallback);
    		}
    		
    		if(Tag.equals("导入上次兑现"))
    		{
    			try
    			{
    				String cishu = Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_jccs)+"";
    				int curCishu = Integer.parseInt(cishu);
    				if(curCishu>1)
    				{
    					int lastCishu = curCishu-1;
    					initDuiXianList(lastCishu+".");
    				}
    				
        			refreshDuixianList(duixianList);
    			}
    			catch(Exception ex)
    			{
    				
    			}
    			
    		}
    		
    		if(Tag.equals("删除兑现"))
    		{
    			int i = 0;
    			final List<HashMap<String,Object>> deleteIndex = new ArrayList<HashMap<String,Object>>();
    			for(HashMap<String,Object> hm:duixianList)
    			{
    				if((Boolean)hm.get("isSelect"))
    				{
    					deleteIndex.add(hm);
    				}
    				i++;
    			}
    			
    			if(deleteIndex.size()>0)
    			{
    				Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "是否删除选中的"+deleteIndex.size()+"个兑现信息？", new ICallback()
    				{
    					@Override
    					public void OnClick(String Str, Object ExtraStr) {
    						
    						for(HashMap<String,Object> index:deleteIndex)
    						{
    							duixianList.remove(index);
    						}
    						
    						refreshDuixianList(duixianList);
    					}
    				});
    			}
    			else
    			{
    				Tools.ShowMessageBox("请勾分户列表的户名！");
    			}
    		}
    	}
    }
	
	private void changePageViewIndex(int position)
	{
		
		Button btnXBXX = (Button)mOwnView.findViewById(R.id.bt_viewpager_xbxx);
		Button btnSJXX =  (Button)mOwnView.findViewById(R.id.bt_viewpager_sjxx);
		Button btnPhoto =  (Button)mOwnView.findViewById(R.id.bt_viewpager_photo);
		Button btnDuixian =  (Button)mOwnView.findViewById(R.id.bt_viewpager_duixian);
		
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
			mOwnView.findViewById(R.id.locator1).setVisibility(View.INVISIBLE);
			mOwnView.findViewById(R.id.locator2).setVisibility(View.VISIBLE);
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
			mOwnView.findViewById(R.id.locator1).setVisibility(View.INVISIBLE);
			mOwnView.findViewById(R.id.locator2).setVisibility(View.INVISIBLE);
			mOwnView.findViewById(R.id.locator3).setVisibility(View.INVISIBLE);
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
			mOwnView.findViewById(R.id.locator1).setVisibility(View.VISIBLE);
			mOwnView.findViewById(R.id.locator2).setVisibility(View.VISIBLE);
			mOwnView.findViewById(R.id.locator3).setVisibility(View.INVISIBLE);
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
			mOwnView.findViewById(R.id.locator1).setVisibility(View.VISIBLE);
			mOwnView.findViewById(R.id.locator2).setVisibility(View.INVISIBLE);
			mOwnView.findViewById(R.id.locator3).setVisibility(View.INVISIBLE);
		}
	}
	
	private void bindXiaoBanSpinnerAdapter()
	{
		
		String s ="否,是";
		//String[] boolItems = s.split(",");
		ArrayAdapter<String> boolAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																	android.R.layout.simple_spinner_item,
																	s.split(","));
		boolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spnIsBianGen =(Spinner) mOwnView.findViewById(R.id.sp_sfbg);
		spnIsBianGen.setAdapter(boolAdapter);
		
		//权属
		String strqs ="国有,国合,集体,个人";
		ArrayAdapter<String> quanshuAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																	android.R.layout.simple_spinner_item,
																	//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "权属"));
																	strqs.split(","));
		quanshuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_qs)).setAdapter(quanshuAdapter);
		
		//地类
		String strdl ="25以上非基本农田坡地,严重沙化耕地,重要水源地15°-25°破耕地,退耕地,荒山荒地,灌丛地,其他";
		ArrayAdapter<String> dileiAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				strdl.split(","));
		dileiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_dl)).setAdapter(dileiAdapter);
		
		//坡位
		String pw="上,中,下,无";
		ArrayAdapter<String> pwAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "坡位"));
				pw.split(","));
		pwAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_pw)).setAdapter(pwAdapter);
		
		//坡向
		String px="东,南,西,西,北,东北,东南,西北,西南,无坡向";
		ArrayAdapter<String> pxAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "坡向"));
				px.split(","));
		pxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_px)).setAdapter(pxAdapter);
		
		//坡度
		String strpd ="15°以下,15°-25°,25°以上";
		ArrayAdapter<String> pdAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "坡度"));
				strpd.split(","));
		pdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_pd)).setAdapter(pdAdapter);
		
		//土壤名称
		String ss ="棕壤,黄棕壤,黄绵土,黄褐土,褐土,黑垆土";
		ArrayAdapter<String> trmcAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "土壤名称"));
				ss.split(","));
		trmcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_trmc)).setAdapter(trmcAdapter);
		
		//土壤厚度
		String trhd = "厚,中,薄";
		ArrayAdapter<String> trhdAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "土壤厚度"));
				trhd.split(","));
		trhdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_tchd)).setAdapter(trhdAdapter);
		
		//土壤肥力状况
		String sss ="肥沃,中等,贫瘠";
		ArrayAdapter<String> trflzkAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "土壤肥力状况"));
				sss.split(","));
		trflzkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_trflzk)).setAdapter(trflzkAdapter);
		
		//土壤侵蚀程度
		String ssss ="强,中,弱";
		ArrayAdapter<String> trqscdAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "土壤侵蚀程度"));
				ssss.split(","));
		trqscdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_trqscd)).setAdapter(trqscdAdapter);
		
		//植被类型
		String sssss ="乔木,灌木,草";
		ArrayAdapter<String> zblxAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "植被类型"));
				sssss.split(","));
				
		zblxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_zblx)).setAdapter(zblxAdapter);
		
		
	}
	
	
	private void bindXiaoBanShejiSpinnerAdapter()
	{
		//造林方式
		String zz ="植苗造林,播种造林";
		ArrayAdapter<String> zbfsAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "造林方式"));
				zz.split(","));
		zbfsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_zlfs)).setAdapter(zbfsAdapter);
		
		//立地类型
		String zzz ="Ⅰ,Ⅱ,Ⅲ,Ⅳ,Ⅴ,Ⅵ,Ⅶ,Ⅷ,Ⅷ";
		ArrayAdapter<String> ldlxAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "立地类型"));
				zzz.split(","));
		ldlxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_ldlx)).setAdapter(ldlxAdapter);
		
		//抚育时间
		String fysj ="春季,夏季,秋季,冬季";
		ArrayAdapter<String> fysjAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "抚育时间"));
				fysj.split(","));
		fysjAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_fysj)).setAdapter(fysjAdapter);
		
		//整地方式
		String zdfs ="全面,带状整地,反坡梯田,水平沟,水平阶,鱼鳞坑,穴状,雁翅形";
		ArrayAdapter<String> zdfsAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "整地方式"));
				zdfs.split(","));
		zdfsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_zdfs)).setAdapter(zdfsAdapter);
		
		//苗木规格
		String mmgg ="Ⅰ,Ⅱ,Ⅲ";
		ArrayAdapter<String> mmggAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				mmgg.split(","));
		mmggAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_mmgg)).setAdapter(mmggAdapter);
		
		//造林林种
		String zllz ="生态林,经济林";
		ArrayAdapter<String> zllzAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				//PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "造林林种"));
				zllz.split(","));
		zllzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_zllz)).setAdapter(zllzAdapter);

		
		ArrayAdapter<String> zlszAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType, "造林树种"));
		zllzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)mOwnView.findViewById(R.id.sp_zlsz)).setAdapter(zlszAdapter);
		
		
	}
	
	private void initInputEvent()
	{
		((EditText)mOwnView.findViewById(R.id.et_zlmj)).addTextChangedListener(new zlmjChanged());
		((EditText)mOwnView.findViewById(R.id.et_zmxyl)).addTextChangedListener(new TextFilter());
		((EditText)mOwnView.findViewById(R.id.et_grdj)).addTextChangedListener(new TextFilter());
		((EditText)mOwnView.findViewById(R.id.et_mmdj)).addTextChangedListener(new TextFilter());
		((EditText)mOwnView.findViewById(R.id.et_ygl_zd)).addTextChangedListener(new TextFilter());
		((EditText)mOwnView.findViewById(R.id.et_ygl_zl)).addTextChangedListener(new TextFilter());
		((EditText)mOwnView.findViewById(R.id.et_ygl_fy)).addTextChangedListener(new TextFilter());
		((EditText)mOwnView.findViewById(R.id.et_ygl_bz)).addTextChangedListener(new TextFilter());
	}
	
	 class TextFilter implements TextWatcher 
	 {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			calcYuSuan();
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	 
	 }
	 
	 class zlmjChanged implements TextWatcher 
	 {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			calcMiaomu();
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	 
	 }
	 
	 private void calcYuSuan()
	 {
		 double zhengdi = 0;
		 double zaolin = 0;
		 double buzhi = 0;
		 double fuyu = 0;
		 double danjia = 0;
		 double yglsum = 0;
		 double miaomuDj= 0;
		 double miaomuCount = 0;
				 
		
		 try
		 {
			 zhengdi = Double.parseDouble(Tools.GetTextValueOnID(mOwnView, R.id.et_ygl_zd));
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 try
		 {
			 zaolin = Double.parseDouble(Tools.GetTextValueOnID(mOwnView, R.id.et_ygl_zl));
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 try
		 {
			 buzhi = Double.parseDouble(Tools.GetTextValueOnID(mOwnView, R.id.et_ygl_bz));
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 try
		 {
			 danjia = Double.parseDouble(Tools.GetTextValueOnID(mOwnView, R.id.et_grdj));
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 try
		 {
			 fuyu = Double.parseDouble(Tools.GetTextValueOnID(mOwnView, R.id.et_ygl_fy));
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 try
		 {
			 miaomuDj = Double.parseDouble(Tools.GetTextValueOnID(mOwnView, R.id.et_mmdj));
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 try
		 {
			 miaomuCount = Double.parseDouble(Tools.GetTextValueOnID(mOwnView, R.id.et_zmxyl));
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 try
		 {
			 yglsum = zhengdi+zaolin+buzhi+fuyu;
			 Tools.SetTextViewValueOnID(mOwnView, R.id.et_ygl_hj, yglsum+"");
			 
			 double ygys = yglsum*danjia;
			 double miaomu = miaomuDj*miaomuCount;
			 
			 Tools.SetTextViewValueOnID(mOwnView, R.id.et_tzys, (ygys+miaomu)+"");
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 
	 }
	
	private String mPhotoPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/Photo";
	private String tempPhotoName;
	private List<String> mPhotoNameList = new ArrayList<String>();
	ArrayList<HashMap<String,Object>> duixianList = new ArrayList<HashMap<String,Object>>();

	    
	private void initPhotoPager()
	{
//		((Button)mOwnActivity.findViewById(R.id.bt_addPhoto)).setOnClickListener(new ViewClick());
//		((Button)mOwnActivity.findViewById(R.id.bt_deletePhoto)).setOnClickListener(new ViewClick());
		
		initBaseObject();
		if(this.mBaseObject.GetSYS_PHOTO() != null)
		{
			if(!this.mBaseObject.GetSYS_PHOTO().isEmpty())
			{
				mPhotoNameList = Tools.StrArrayToList(this.mBaseObject.GetSYS_PHOTO().split(","));
			}
		}
		
		mPhotoControl = new PhotoControl(mObjId,mPhotoNameList,mLayer.GetShowWaterMark(),false,viewContainter.get(3));
		mBaseObject.setWaterMarkKey(mLayer.GetWaterMarkDataFieldStr());
		mPhotoControl.setCallback(new ICallback() {
			
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				saveToDB();
				
			}
		});
//		HashValueObject hvoWaterMark =  PubVar.m_HashMap.GetValueObject("Tag_Photo_WaterMark", false);
//		if(hvoWaterMark != null && hvoWaterMark.Value.equals("true"))
//		{
//			mPhotoControl = new PhotoControl(mObjId,mPhotoNameList,true,false,viewContainter.get(3));
//		}
//		else
//		{
//			mPhotoControl = new PhotoControl(mObjId,mPhotoNameList,false,false,viewContainter.get(3));
//		}
	}
	
	private void initViewPager()
	{
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.tuigengdata_sheji_xiaoban, null));
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.tuigengdata_sheji_sheji, null));
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.tuigeng_duixianlist, null));
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.v1_data_template_photo, null));
		titleContainer.add("小班信息");
		titleContainer.add("设计信息");
		titleContainer.add("兑现");
		titleContainer.add("照片");
	    
		
		viewPager = (ViewPager)mOwnView.findViewById(R.id.viewPager);
		
		
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
				
				if(position == 0)
				{
					bindXiaoBanSpinnerAdapter();
				}
				if(position == 1)
				{
					bindXiaoBanShejiSpinnerAdapter();
				}
				if(position == 2)
				{
					initDuixian();
				}
				if(position == 3)
				{
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
			public void onPageSelected(int arg0) 
			{
				changePageViewIndex(arg0);
				if(arg0 == 3)
				{
					if(mPhotoControl == null)
					{
						initPhotoPager();
						
					}
					
					mBaseObject.RefreshViewValueToData();
					String watermark="所属单位：";
					if(mBaseObject.getWaterMarkValue() != null)
					{
						watermark+=mBaseObject.getWaterMarkValue();
					}
					mPhotoControl.SetXiaoBanInfo(coordX,coordY, watermark);
				}
			}
		});
		
	}
	
	private int editIndex = -1; 
	private void initDuixian()
	{
		//权属
		String dxcs ="1,2,3,4,5,6,7,8";
		ArrayAdapter<String> quanshuAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																	android.R.layout.simple_spinner_item,
																	dxcs.split(","));
		quanshuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)viewContainter.get(2).findViewById(R.id.sp_jccs)).setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				String cishu = Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_jccs)+".";
				initDuiXianList(cishu);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) 
			{
				
			}
	
		});
		((Spinner)viewContainter.get(2).findViewById(R.id.sp_jccs)).setAdapter(quanshuAdapter);
		
		viewContainter.get(2).findViewById(R.id.bt_addDuiXian).setOnClickListener(new ViewClick());
		viewContainter.get(2).findViewById(R.id.bt_deleteDuiXian).setOnClickListener(new ViewClick());
		viewContainter.get(2).findViewById(R.id.bt_importDuiXian).setOnClickListener(new ViewClick());
		
		((ListView)viewContainter.get(2).findViewById(R.id.lvList)).setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,long arg3) {
				ListView lvList = (ListView)arg0;
				TuiGengFenhuAdapter la = (TuiGengFenhuAdapter)lvList.getAdapter();
				la.SetSelectItemIndex(arg2);
				la.notifyDataSetChanged();
				
	    		TuiGengData_AddDuiXian addDuixian = new TuiGengData_AddDuiXian(false,mLayer.GetLayerID(), mObjId,Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_jccs),duixianNianDu,Tools.GetTextValueOnID(mOwnView, R.id.et_mmdj),Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_zhj));
				addDuixian.SetCallback(pCallback);
				//HashMap<String,Object> seletItem = (HashMap<String,Object>)la.getItem(arg2);
				HashMap<String,Object> seletItem = duixianList.get(arg2);
				editIndex = arg2;
				
				addDuixian.setDuixianEditInfo(arg2,
										Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_xz),
										Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_jzc),
										Tools.GetTextValueOnID(mOwnView, R.id.tv_xbh),
										seletItem.get("huming")+"",
										seletItem.get("dilei")+"",
										seletItem.get("linzhong")+"",
										seletItem.get("shuzhong")+"",
										seletItem.get("miaomu")+"",
										seletItem.get("mianji")+"",
										seletItem.get("niandu")+"",
										seletItem.get("dxje")+"",
										seletItem.get("buzhu")+"",
										seletItem.get("biaozhun")+"",
										seletItem.get("zhongmiao")+"",
										seletItem.get("sfdx")+"",
										seletItem.get("beizhu")+"");
				addDuixian.ShowDialog();
				
			}});
		
	}
	
	private void editDuixian(HashMap<String,Object> editHM)
	{
		try
		{
			int index = Integer.parseInt(editHM.get("dxIndex")+"");
			if(index>0)
			{
				duixianList.set(index, editHM);
				refreshDuixianList(duixianList);
			}
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(ex.getMessage());
		}
	}
	private void initDuiXianList(String cishu)
	{
		TuiGengDB db = new TuiGengDB();
		HashMap<String, String> hMap = db.getDuixianData(mLayer.GetLayerID(), mObjId+"");
		String humings = hMap.get("D户名");
		String dxnd = hMap.get("D兑现年度");
		String dxje = hMap.get("D兑现金额");
		String zcbz = hMap.get("D政策补助");
		String zcbzbz = hMap.get("D补助标准");
		String zmf = hMap.get("D种苗费");
		String sfdx = hMap.get("D是否兑现");
		String bz = hMap.get("D备注");
		
		fenhuList.clear();
		HashMap<String, String> fenhuMap = db.getFenhuData(mLayer.GetLayerID(), mObjId+"");
		String fhHMs = fenhuMap.get("户名s");
		String dileis = fenhuMap.get("地类s");
		String mianjis = fenhuMap.get("面积s");
		String linzhongs = fenhuMap.get("林种s");
		String shuzhus = fenhuMap.get("树种s");
		String miaomus = fenhuMap.get("苗木s");
		String zhongzis = fenhuMap.get("种子s");
		
		if(humings!= null || humings.length()>0)
		{
			String[] arrayHM = fhHMs.split(",");
			String[] arrayDL = dileis.split(",");
			String[] arrayMJ = mianjis.split(",");
			String[] arraySZ = shuzhus.split(",");
			
			String[] arrayLZ = linzhongs.split(",");
			String[] arrayMM = miaomus.split(",");
			String[] arrayZZ = zhongzis.split(",");
			for(int i=0;i<arrayHM.length;i++)
			{
				HashMap<String,Object> hm = new HashMap<String,Object>();
				try
				{
					hm.put("xiangzhen", Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_xz));
					hm.put("jianzhicun", Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_jzc));
					hm.put("xiaoban", Tools.GetTextValueOnID(mOwnView, R.id.et_xbh));
					hm.put("huming", arrayHM[i]);
					hm.put("dilei", arrayDL[i]);
					hm.put("mianji", arrayMJ[i]);
					hm.put("linzhong", arrayLZ[i]);
					hm.put("shuzhong", arraySZ[i]);
					hm.put("miaomu", arrayMM[i]);
					hm.put("zhongzi", arrayZZ[i]);
					fenhuList.add(hm);
					
					
				}
				catch(Exception ex)
				{
					Tools.ShowMessageBox(ex.getMessage());
				}
			}
		}
		
	    duixianList.clear();
	
		if(humings != null || humings.length()>0)
		{
			int j = 0;
			int csIndex = -1;
			for(String hm:humings.split(";"))
			{
				
				if(hm.contains(cishu))
				{
					csIndex = j;
				}
				
				j++;
			}
			
			if(csIndex>-1)
			{
				try
				{
					String[] arrayHM = (humings.split(";"))[csIndex].replace(cishu, "").split(",");
					String[] arrayDXND = (dxnd.split(";"))[csIndex].replace(cishu, "").split(",");
					String[] arrayDXJE = (dxje.split(";"))[csIndex].replace(cishu, "").split(",");
					String[] arrayZCBZ = (zcbz.split(";"))[csIndex].replace(cishu, "").split(",");
					String[] arrayZCBZBZ = (zcbzbz.split(";"))[csIndex].replace(cishu, "").split(",");
					String[] arrayZMF = (zmf.split(";"))[csIndex].replace(cishu, "").split(",");
					String[] arraySFDX = (sfdx.split(";"))[csIndex].replace(cishu, "").split(",");
					String[] arrayBZ = (bz.split(";"))[csIndex].replace(cishu, "").split(",");
					
					for(int i=0;i<arrayHM.length;i++)
					{
						HashMap<String,Object> hm = new HashMap<String,Object>();
						hm.put("isSelect", false);
						hm.put("xiangcun", Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_xz)+"_"+Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_jzc));
						hm.put("xiaoban", Tools.GetTextValueOnID(mOwnView, R.id.et_xbh));
						hm.put("huming", arrayHM[i]);
						hm.put("niandu", arrayDXND[i]);
						hm.put("dxje", arrayDXJE[i]);
						hm.put("zhongmiao", arrayZMF[i]);
						hm.put("buzhu", arrayZCBZ[i]);
						hm.put("biaozhun", arrayZCBZBZ[i]);
						hm.put("beizhu", arrayBZ[i]);
						hm.put("sfdx", arraySFDX[i]);
						
						for(HashMap<String,Object> fenhu:fenhuList)
						{
							if(fenhu.get("huming").equals(arrayHM[i]))
							{
								hm.put("dilei", fenhu.get("dilei"));
								hm.put("shuzhong", fenhu.get("shuzhong"));
								hm.put("linzhong", fenhu.get("linzhong"));
								hm.put("mianji", fenhu.get("mianji"));
							}
						}
						
						duixianList.add(hm);				
					}
				}
				catch(Exception e)
				{
					Tools.ShowMessageBox(e.getMessage());
				}
			}
		}
			
			refreshDuixianList(duixianList);
	}
	
	private void refreshDuixianList(ArrayList<HashMap<String,Object>> list)
	{
		
		TuiGengFenhuAdapter fenhuAdapter = new TuiGengFenhuAdapter(PubVar.m_DoEvent.m_Context,
				list,
				R.layout.tuigeng_duixianlist_item,
				new String[] {"isSelect","huming","xiangcun","xiaoban","dilei","mianji","linzhong","shuzhong","niandu","dxje","zhongmiao","buzhu","beizhu","sfdx"},
				new int[] {R.id.cb_select,R.id.tv_hm,R.id.tv_xzjzc,R.id.tv_xbh,R.id.tv_dl, R.id.tv_mj,R.id.tv_lz,R.id.tv_sz,
							R.id.tv_dxnd,R.id.tv_dxje,R.id.tv_zmf,R.id.tv_zcbz,R.id.tv_bz,R.id.tv_sfdx});

		(((ListView)viewContainter.get(2).findViewById(R.id.lvList))).setAdapter(fenhuAdapter);
	}
	
	@SuppressLint("NewApi")
	private void initXiangzhen()
	{
		DictXZQH xzqh = new DictXZQH();
		String cityCode =  xzqh.getCodeByName(Tools.GetTextValueOnID(mOwnView, R.id.et_ds),"市","61");
    	countyCode = xzqh.getCodeByName(Tools.GetTextValueOnID(mOwnView, R.id.et_qx),"县",cityCode);
    	if(!countyCode.isEmpty())
    	{
    		List<HashMap<String,Object>> town = xzqh.getXZQH(countyCode, "乡");
        	ArrayList<String> countyNames = new ArrayList<String>();
        	for(HashMap<String, Object> hm:town)
        	{
        		countyNames.add(hm.get("D1").toString());
        	}
        	
        	ArrayAdapter<Object> nfAdapter = new ArrayAdapter<Object>(PubVar.m_DoEvent.m_Context,
        			android.R.layout.simple_spinner_item,
        			countyNames.toArray());
        	nfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		Spinner tgds = ((Spinner)mOwnView.findViewById(R.id.sp_xz));
    		tgds.setAdapter(nfAdapter);
    		tgds.setOnItemSelectedListener(new tgdsOnItemSelectedListener());
    		
    		
    	}
    	
	}
	
	private void setJianzhiCun()
	{
		if(mBaseObject != null)
		{
			String cun =  mBaseObject.getCun();
			if(cun != null && cun.length()>0)
			{
				Spinner tv = (Spinner)mOwnView.findViewById(R.id.sp_jzc);
		    	int p = ((ArrayAdapter<CharSequence>)tv.getAdapter()).getPosition(cun);
		    	if(p== -1)
		    	{
		    		if(cun.endsWith("村"))
					{
						cun = cun.replace("村", "");
						int d = ((ArrayAdapter<CharSequence>)tv.getAdapter()).getPosition(cun);
						if(d >-1)
						{
							tv.setSelection(d,true);
						}
//						else
//						{
//							Tools.ShowMessageBox("在行政区划里找不到该建制村!");
//						}
					}
		    	}
		    	else
		    	{
		    		tv.setSelection(p,true);
		    	}
				
			}
			
		}
	}
	
	private void initDishi()
	{
		setCityCountyYear(mLayer.getCity(),
				mLayer.getCounty(), 
				mLayer.getYear());
	}
	
	private void setEditInfo()
	{
        this.mBaseObject.ReadDataAndBindToView("SYS_ID="+mObjId);
	}
	
	private void initBaseObject()
	{
		if(mBaseObject == null)
		{
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
			this.mBaseObject = new TuiGengDataObject();
		    this.mBaseObject.SetDataset(pDataset);
		       
	        this.mBaseObject.SetSYS_ID(mObjId);   //设置SYS_ID
		}
	}
	
	private void initFenhu()
	{
		TuiGengDB db = new TuiGengDB();
		HashMap<String, String> hMap = db.getFenhuData(mLayer.GetLayerID(), mObjId+"");
		String humings = hMap.get("户名s");
		String count = "0";
		if(humings != null && humings.length()>0)
		{
			count = humings.split(",").length+"";
		}
		
        Spinner sp = (Spinner)mOwnView.findViewById(R.id.sp_ztsl);
		v1_DataBind.SetBindListSpinner(sp.getContext(), "", Tools.StrArrayToList(new String[]{count}), sp);
		
//		TuiGengDB tuigengDB = new TuiGengDB();
//		HashMap<String, String> hMap = tuigengDB.getFenhuData(mLayer.GetLayerID(), mObjId+"");
//		if(hMap.get("户名s").isEmpty())
//		{
//			Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_ztsl, "0");
//		}
//		else
//		{
//			Tools.SetSpinnerValueOnID(mOwnActivity, R.id.sp_ztsl, hMap.get("户名s").split(",").length+"");
//		}
		
	}
	
	private void calcTFH()
	{
		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayer.GetLayerID());
		 if(mObjId != -1)
	        {
	        	pGeometry = pDataset.GetGeometry(mObjId);
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
	        		if(mLayer.GetLayerType() == lkGeoLayerType.enPolygon)
	        		{
	        			
	        			Coordinate middle = ((Polygon)pGeometry).getCenterPoint();
	        			coordX = Tools.ConvertToDigi(middle.getX()+"",2);
	        			coordY = Tools.ConvertToDigi(middle.getY()+"",2);
	        			Tools.SetTextViewValueOnID(mOwnView, R.id.tv_coor, 
	        					"X="+coordX+"\n"+"Y="+coordY);
	        			
	        			String xField = mLayer.GetDataFieldNameByFieldName("横坐标");
	        			String yField = mLayer.GetDataFieldNameByFieldName("纵坐标");
	        			if(xField.length()>0 && yField.length()>0)
	        			{
	        				this.mBaseObject.AddDataBindItem(new DataBindOfKeyValue("横坐标", xField,coordX, null));
		        			this.mBaseObject.AddDataBindItem(new DataBindOfKeyValue("纵坐标", yField,coordY, null));
	        			}
	        			
	        			
	        			Tools.SetTextViewValueOnID(mOwnView, R.id.et_xbmj, Tools.ReSetArea(((Polygon)pGeometry).getArea(true), true));
	        			String scale =  PubVar.m_HashMap.GetValueObject("Tag_System_MapScale", false).Value;
	        			if(scale.isEmpty())
	        			{
	        				Tools.SetTextViewValueOnID(mOwnView, R.id.et_tfh, Tools.CalcTuFuHao(middle, "1:1万"));
	        			}
	        			else
	        			{
	        				Tools.SetTextViewValueOnID(mOwnView, R.id.et_tfh, Tools.CalcTuFuHao(middle,scale));
	        			}
	        			
	        		}
	        	}
	        }
	}
	

	
	class tgdsOnItemSelectedListener implements OnItemSelectedListener
	{
    	
	        @SuppressLint("NewApi")
			@Override  
	        public void onItemSelected(AdapterView<?> adapter,View view,int position,long id) 
	        { 
	        	if(!countyCode.isEmpty())
	        	{
	        		DictXZQH xzqh = new DictXZQH();
		        	String xiangCode = xzqh.getCodeByName(Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_xz),"乡",countyCode);
		        	List<HashMap<String,Object>> cun = xzqh.getXZQH(xiangCode, "村");
		        	
		        	ArrayList<String> countyNames = new ArrayList<String>();
		        	for(HashMap<String, Object> hm:cun)
		        	{
		        		countyNames.add(hm.get("D1").toString());
		        	}
		        	
		    		ArrayAdapter<Object> nfAdapter = new ArrayAdapter<Object>(PubVar.m_DoEvent.m_Context,
		    		android.R.layout.simple_spinner_item,
		    		countyNames.toArray());
		    		nfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    		
		    		Spinner tgds = ((Spinner)mOwnView.findViewById(R.id.sp_jzc));
		    		tgds.setAdapter(nfAdapter);
		    		
		    		try
		    		{
		    			if(mFirstEdit)
		    			{
		    				if(preJianzhicun != null && preJianzhicun.length()>0)
	    					{
	    						View viewjzc = mOwnView.findViewById(R.id.sp_jzc);
	    						Tools.SetValueToView(preJianzhicun, viewjzc);
	    					}
		    			}
		    			else
		    			{
		    				setJianzhiCun();
		    			}
		    			
		    		}
		    		catch(Exception ex)
		    		{
		    			
		    		}
	        	}
	        }
        	@Override  
            public void onNothingSelected(AdapterView<?> arg0) 
        	{  
                
            }  
        }
	
	private void setCityCountyYear(String city,String county,String year)
	{
		Tools.SetTextViewValueOnID(mOwnView, R.id.et_ds, city);
		Tools.SetTextViewValueOnID(mOwnView, R.id.et_qx, county);
        Tools.SetTextViewValueOnID(mOwnView, R.id.et_nd, year);
	}
	
	@SuppressLint("NewApi")
	private void saveData()
	{
		EditText kezuoyeMianJi = (EditText)mOwnView.findViewById(R.id.et_kzymj);
		if(kezuoyeMianJi==null || kezuoyeMianJi.getText() == null || kezuoyeMianJi.getText().toString().isEmpty())
		{
			Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "可作业面积没有填写，是否将可作业面积设为于小班面积一致？", new ICallback() {
				
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
					//返回小班信息页面
					viewPager.setCurrentItem(0);
					
					if(Str.equals("YES"))
					{
						if(pGeometry!= null)
						{
							EditText kzymj = (EditText)mOwnView.findViewById(R.id.et_kzymj);
							kzymj.setText(new DecimalFormat("0.0").format(((Polygon)pGeometry).getArea(true)/666.6666666667));
						}
					}
					else
					{
						
					}
				}
			});
		}
		else
		{
			String strKzymj = ((EditText)mOwnView.findViewById(R.id.et_kzymj)).getText().toString();
			String strXBMJ = ((EditText)mOwnView.findViewById(R.id.et_kzymj)).getText().toString();
			if(strXBMJ.indexOf("(")>0)
			{
				strXBMJ = strXBMJ.substring(0, strXBMJ.indexOf("("));
			}
			try
			{
				if(pGeometry != null)
				{
					double kzymj = Double.parseDouble(strKzymj);
					double area = Double.parseDouble(strXBMJ);
					if(kzymj>area)
					{
						Tools.ShowYesContinuMessage(PubVar.m_DoEvent.m_Context, "可作业面积大于小班面积,是否修改？", new ICallback() {
							
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								if(Str.equals("YES"))
								{
									//返回小班信息页面
									viewPager.setCurrentItem(0);
								}
								else
								{
									if(saveToDB())
									{
										HiddenView();
									}
								}
								
							}
						});
					}
					else 
					{
						if(area*0.85>kzymj)
						{
							Tools.ShowYesContinuMessage(PubVar.m_DoEvent.m_Context, "可作业面积小于小班面积的85%，是否修正可作业面积？", new ICallback() 
							{
								
								@Override
								public void OnClick(String Str, Object ExtraStr) {
									if(Str.equals("YES"))
									{
										//返回小班信息页面
										viewPager.setCurrentItem(0);
									}
									else
									{
										if(saveToDB())
										{
											HiddenView();
										}
									}
									
								}
							});
						}
						else
						{
							if(saveToDB())
							{
								HiddenView();
							}
						}
					}
				}
				
			}
			catch(NumberFormatException ex)
			{
				Tools.ShowMessageBox("可作业面积填写不正确！");
			}
			
		}
		
		
	}
	
	private boolean saveToDB()
	{
		boolean result = false;
		saveDuixianData();
		this.mBaseObject.RefreshViewValueToData();
		this.mBaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoControl.getPhotoList()));
		if (!this.mBaseObject.SaveFeatureToDb())
		{
			lkmap.Tools.Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context,"数据保存失败！");
		}
		else 
		{
			savePreviewToDB();
			result = true;
		}
		return result;
	}
	
	int[] saveIDs = {R.id.et_ds,R.id.et_qx,R.id.et_nd,R.id.sp_xz,R.id.et_zrc,R.id.et_xdm,R.id.sp_dl,R.id.sp_qs,R.id.et_hb,R.id.sp_pw,
			 R.id.sp_px,R.id.sp_pd,R.id.sp_trmc,R.id.sp_tchd,R.id.sp_trflzk,R.id.sp_trqscd,R.id.et_ph,
			 R.id.sp_zblx,R.id.et_zbgd,R.id.et_zbhd,R.id.sp_sfbg,R.id.et_bgyy,R.id.sp_msh,R.id.sp_zlfs,
			 R.id.sp_zlsz,R.id.sp_ldlx,R.id.sp_zllz,R.id.sp_zhj,R.id.et_hjb,R.id.et_fync,R.id.sp_fysj,
			 R.id.et_zmxyl,R.id.sp_mmgg,R.id.et_mmdj,R.id.sp_zdfs,R.id.et_zdgg,R.id.et_ygl_hj,
			 R.id.et_grdj,R.id.et_ygl_zd,R.id.et_ygl_zl,R.id.et_ygl_bz,R.id.et_ygl_fy,R.id.et_tzys,R.id.sp_jzc};
	
	private void savePreviewToDB()
	{
		String previewValue = "";
		
		for(int id:saveIDs)
		{
			View view = mOwnView.findViewById(id);
			if(view != null)
			{
				String value = Tools.GetViewValue(view);
				previewValue += id+":"+value+";";
			}
		}
		new UserConfigDB().SavePreviewTGHL(previewValue);
	}
	
	
	
	@SuppressLint("NewApi")
	String preJianzhicun ="";
	private void readPreviewToView()
	{
		String value = new UserConfigDB().readPreviewTGHL();
		String[] valuePairs = value.split(";");
		for(String valuePair:valuePairs)
		{
			if(!valuePair.isEmpty())
			{
				String[] feature = valuePair.split(":");
				if(feature.length == 2)
				{
					String viewId = feature[0];
					if(!viewId.isEmpty())
					{
						int id = Integer.parseInt(viewId);
						if(id == R.id.sp_jzc)
						{
							preJianzhicun = feature[1];
						}
						View view = mOwnView.findViewById(id);
						Tools.SetValueToView(feature[1], view);
					}
				}
			}
			
		}
	}
	
	private void initFieldControl()
	{
		fieldControls.put("地市",mOwnView.findViewById(R.id.et_ds));
		fieldControls.put("区县",mOwnView.findViewById(R.id.et_qx));
		fieldControls.put("设计年度",mOwnView.findViewById(R.id.et_nd));
		fieldControls.put("乡镇",mOwnView.findViewById(R.id.sp_xz));
		fieldControls.put("建制村",mOwnView.findViewById(R.id.sp_jzc));
		fieldControls.put("自然村",mOwnView.findViewById(R.id.et_zrc));
		fieldControls.put("小地名",mOwnView.findViewById(R.id.et_xdm));
		fieldControls.put("小班号",mOwnView.findViewById(R.id.et_xbh));
		fieldControls.put("图幅号",mOwnView.findViewById(R.id.et_tfh));
		fieldControls.put("地类",mOwnView.findViewById(R.id.sp_dl));
		fieldControls.put("小班面积",mOwnView.findViewById(R.id.et_xbmj));
		fieldControls.put("可作业面积",mOwnView.findViewById(R.id.et_kzymj));
		fieldControls.put("权属",mOwnView.findViewById(R.id.sp_qs));
		fieldControls.put("海拔",mOwnView.findViewById(R.id.et_hb));
		fieldControls.put("坡位",mOwnView.findViewById(R.id.sp_pw));
		fieldControls.put("坡向",mOwnView.findViewById(R.id.sp_px));
		fieldControls.put("坡度",mOwnView.findViewById(R.id.sp_pd));
		fieldControls.put("土壤名称",mOwnView.findViewById(R.id.sp_trmc));
		fieldControls.put("土层厚度",mOwnView.findViewById(R.id.sp_tchd));
		fieldControls.put("土壤肥力",mOwnView.findViewById(R.id.sp_trflzk));
		fieldControls.put("侵蚀程度",mOwnView.findViewById(R.id.sp_trqscd));
		fieldControls.put("土壤PH值",mOwnView.findViewById(R.id.et_ph));
		fieldControls.put("植被类型",mOwnView.findViewById(R.id.sp_zblx));
		fieldControls.put("植被盖度",mOwnView.findViewById(R.id.et_zbgd));
		fieldControls.put("植被高度",mOwnView.findViewById(R.id.et_zbhd));
		fieldControls.put("是否变更",mOwnView.findViewById(R.id.sp_sfbg));
		fieldControls.put("变更原因",mOwnView.findViewById(R.id.et_bgyy));
		fieldControls.put("模式号",mOwnView.findViewById(R.id.sp_msh));
		fieldControls.put("造林方式",mOwnView.findViewById(R.id.sp_zlfs));
		
		fieldControls.put("造林树种",mOwnView.findViewById(R.id.sp_zlsz));
		//旧版本书写错误
		fieldControls.put("造林面积",mOwnView.findViewById(R.id.et_zlmj));
		fieldControls.put("立地类型",mOwnView.findViewById(R.id.sp_ldlx));
		fieldControls.put("造林林种",mOwnView.findViewById(R.id.sp_zllz));
		//TODO
		fieldControls.put("株行距",mOwnView.findViewById(R.id.sp_zhj));
		fieldControls.put("混交比",mOwnView.findViewById(R.id.et_hjb));
		fieldControls.put("抚育年次",mOwnView.findViewById(R.id.et_fync));
		fieldControls.put("抚育时间",mOwnView.findViewById(R.id.sp_fysj));
		fieldControls.put("种苗需要量",mOwnView.findViewById(R.id.et_zmxyl));
		fieldControls.put("苗木规格",mOwnView.findViewById(R.id.sp_mmgg));
		fieldControls.put("苗木单价",mOwnView.findViewById(R.id.et_mmdj));
		fieldControls.put("整地方式",mOwnView.findViewById(R.id.sp_zdfs));
		fieldControls.put("整地规格",mOwnView.findViewById(R.id.et_zdgg));
		fieldControls.put("用工量合计",mOwnView.findViewById(R.id.et_ygl_hj));
		fieldControls.put("工日单价",mOwnView.findViewById(R.id.et_grdj));
		fieldControls.put("用工量整地",mOwnView.findViewById(R.id.et_ygl_zd));
		fieldControls.put("用工量补植",mOwnView.findViewById(R.id.et_ygl_bz));
		fieldControls.put("用工量抚育",mOwnView.findViewById(R.id.et_ygl_fy));
		//fieldControls.put("_户名",mOwnActivity.findViewById(R.id.sp_ztsl));
		fieldControls.put("投资预算",mOwnView.findViewById(R.id.et_tzys));
		fieldControlsClone = (HashMap<String,View>)fieldControls.clone();
		
	}
	
	private void initFieldViewControl(v1_Layer layer)
	{
		initFieldControl();
		
		for(v1_LayerField layerField:layer.GetFieldList())
    	{
			View v = fieldControls.get(layerField.GetFieldName());
			if(v != null)
			{
				fieldControlsClone.remove(layerField.GetFieldName());
				mFieldViewList.add(new FieldView(layerField.GetFieldName(),layerField.GetDataFieldName(),v));
			}
			else
			{
			}
			
			if(layerField.GetFieldName().equals("造林树种")||layerField.GetFieldName().equals("造林种树"))
			{
				fieldControlsClone.remove("造林树种");
				final v1_SpinnerDialog sp = (v1_SpinnerDialog)mOwnView.findViewById(R.id.sp_zlsz);
				sp.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						TuiGengData_ZLSZ zhjDialog = new TuiGengData_ZLSZ(Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_zlsz));
						zhjDialog.SetCallback(new ICallback(){
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								v1_DataBind.SetBindListSpinner(PubVar.m_DoEvent.m_Context, 
															  	"造林树种", 
															  	Tools.StrArrayToList(new String[]{ExtraStr.toString()}), 
															  	sp);
								
							}});
						zhjDialog.ShowDialog();
					}});
				
				continue;
			}
			
			if(layerField.GetFieldName().equals("_户名"))
			{
				final v1_SpinnerDialog sp = (v1_SpinnerDialog)mOwnView.findViewById(R.id.sp_ztsl);
				sp.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) 
					{
						if(Tools.GetTextValueOnID(mOwnView, R.id.et_kzymj).equals(""))
						{
							Tools.ShowMessageBox("请填写可作业面积！");
							return;
						}
						TuiGengData_FenHu zhjDialog = new TuiGengData_FenHu(mLayer.GetLayerID(),
																			mObjId,
																			Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_zlsz),
																			Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_zllz),
																			Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_dl),
																			Tools.GetTextValueOnID(mOwnView, R.id.et_kzymj),
																			Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_zhj));
						zhjDialog.SetCallback(pCallback);
						
						zhjDialog.ShowDialog();
					}});
				//initFenhu();
				continue;
			}
			
			if(layerField.GetFieldName().equals("株行距"))
			{
				
				final v1_SpinnerDialog zhj = (v1_SpinnerDialog)mOwnView.findViewById(R.id.sp_zhj);
				zhj.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						TuiGengData_ZHJ zhjDialog = new TuiGengData_ZHJ();
						zhjDialog.SetCallback(new ICallback(){
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								v1_DataBind.SetBindListSpinner(PubVar.m_DoEvent.m_Context, "株行距", Tools.StrArrayToList(new String[]{ExtraStr.toString()}), zhj);
								
								calcMiaomu();
							}});
						zhjDialog.ShowDialog();
					}});
			}
			
    	}
		for(View v:fieldControlsClone.values())
		{
			v.setVisibility(View.INVISIBLE);
		}
	}
	
	private void calcMiaomu()
	{
		try
		{
			
			String mianji = Tools.GetTextValueOnID(mOwnView, R.id.et_zlmj);
			String zhj = Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_zhj);
			
			if(mianji != null && mianji.length()>0)
			{
				double mj = Double.parseDouble(mianji);
				String[] zhjs=(zhj+"").split("\\*");
				if(zhjs.length == 2)
				{
					double h =  Double.parseDouble(zhjs[0]);
					double l = Double.parseDouble(zhjs[1]);
					double fk = h*l;
					int count = (int)((mj*666.7)/fk);
					
					Tools.SetTextViewValueOnID(mOwnView, R.id.et_zmxyl, count+"");
				}
			}
			
		}
		catch(Exception e)
		{
			
		}
	}
	
	
	private boolean saveDuixianData()
	{
		TuiGengDB db = new TuiGengDB();
		HashMap<String, String> hMap = db.getDuixianData(mLayer.GetLayerID(), mObjId+"");
		String humings = hMap.get("D户名");
		String dxnd = hMap.get("D兑现年度");
		String dxje = hMap.get("D兑现金额");
		String zcbz = hMap.get("D政策补助");
		String zcbzbz = hMap.get("D补助标准");
		String zmf = hMap.get("D种苗费");
		String sfdx = hMap.get("D是否兑现");
		String bz = hMap.get("D备注");
		
		ArrayList<String> allDuixianData = new ArrayList<String>();
		String cishu = Tools.GetSpinnerValueOnID(mOwnView, R.id.sp_jccs)+".";
		int csIndex = -1;
		if(humings != null && humings.length() >0)
		{
			int j = 0;
			for(String hm:humings.split(";"))
			{
				if(hm.startsWith(cishu))
				{
					csIndex = j;
					break;
				}
				j++;
			}
		}
		
		String addhm = "";
		String adddxnd = "";
		String adddxje = "";
		String addzcbz = "";
		String addzcbzbz = "";
		String addzmf = "";
		String addsfdx = "";
		String addbz = "";
		
		boolean isFirst = true;
		
		for(HashMap<String,Object> hm:duixianList)
		{
			if(isFirst)
			{
				addhm += cishu+hm.get("huming");
				adddxnd += cishu+hm.get("niandu");
				adddxje += cishu+hm.get("dxje");
				addzcbz += cishu+hm.get("buzhu");
				addzcbzbz += cishu+hm.get("biaozhun");
				addzmf += cishu+hm.get("zhongmiao");
				addsfdx += cishu+hm.get("sfdx");
				addbz += cishu+hm.get("beizhu");
				isFirst = false;
			}
			else
			{
				addhm += ","+hm.get("huming");
				adddxnd += ","+hm.get("niandu");
				adddxje += ","+hm.get("dxje");
				addzcbz += ","+hm.get("buzhu");
				addzcbzbz += ","+hm.get("biaozhun");
				addzmf += ","+hm.get("zhongmiao");
				addsfdx += ","+hm.get("sfdx");
				addbz += ","+hm.get("beizhu");
			}
		}
		
		if(csIndex>-1)
		{
			humings = humings.replaceFirst(humings.split(";")[csIndex],addhm);
			dxnd = dxnd.replaceFirst(dxnd.split(";")[csIndex],adddxnd);
			dxje = dxje.replaceFirst(dxje.split(";")[csIndex],adddxje);
			zcbz = zcbz.replaceFirst(zcbz.split(";")[csIndex],addzcbz);
			zcbzbz = zcbzbz.replaceFirst(zcbzbz.split(";")[csIndex],addzcbzbz);
			zmf = zmf.replaceFirst(zmf.split(";")[csIndex],addzmf);
			sfdx = sfdx.replaceFirst(sfdx.split(";")[csIndex],addsfdx);
			bz = bz.replaceFirst(bz.split(";")[csIndex],addbz);
			allDuixianData.add(humings);
			allDuixianData.add(dxnd);
			allDuixianData.add(dxje);
			allDuixianData.add(zcbz);
			allDuixianData.add(zcbzbz);
			allDuixianData.add(zmf);
			allDuixianData.add(sfdx);
			allDuixianData.add(bz);
		}
		else
		{
			if(humings == null || humings.isEmpty())
			{
				allDuixianData.add(addhm);
				allDuixianData.add(adddxnd);
				allDuixianData.add(adddxje);
				allDuixianData.add(addzcbz);
				allDuixianData.add(addzcbzbz);
				allDuixianData.add(addzmf);
				allDuixianData.add(addsfdx);
				allDuixianData.add(addbz);
			}
			else
			{
				allDuixianData.add(humings+";"+addhm);
				allDuixianData.add(dxnd+";"+adddxnd);
				allDuixianData.add(dxje+";"+adddxje);
				allDuixianData.add(zcbz+";"+addzcbz);
				allDuixianData.add(zcbzbz+";"+addzcbzbz);
				allDuixianData.add(zmf+";"+addzmf);
				allDuixianData.add(sfdx+";"+addsfdx);
				allDuixianData.add(bz+";"+addbz);
			}
		}
		db.saveDuixianData(mLayer.GetLayerID(), mObjId, allDuixianData);
		return true;
	}
	
	private void initKZYMJEvent()
	{
		EditText etKZYMJ = (EditText)mOwnView.findViewById(R.id.et_kzymj);
		etKZYMJ.addTextChangedListener(new TextWatcher() 
		{
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				changeKZYMJStatus();
				
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				
			}
		});
	}
	@SuppressLint("NewApi")
	private void changeKZYMJStatus()
	{
		EditText etKZYMJ = (EditText)mOwnView.findViewById(R.id.et_kzymj);
		if(etKZYMJ.getText().toString().isEmpty())
		{
			etKZYMJ.setBackgroundColor(android.graphics.Color.RED);
		}
		else
		{
			try
			{
				if(pGeometry != null)
				{
					EditText etXBMJ = (EditText)mOwnView.findViewById(R.id.et_xbmj);
					String xbmj = etXBMJ.getText().toString();
					if(xbmj.contains("("))
					{
						xbmj = xbmj.substring(0,xbmj.indexOf("("));
					}
					double kzymj = Double.parseDouble(etKZYMJ.getText().toString());
					double area = Double.parseDouble(xbmj);
					
					if(kzymj>area)
					{
						etKZYMJ.setBackgroundColor(android.graphics.Color.RED);
					}
					else
					{
						if(area*0.85<kzymj)
						{
							etKZYMJ.setBackground(mOwnView.findViewById(R.id.et_hb).getBackground());
						}
						else
						{
							etKZYMJ.setBackgroundColor(android.graphics.Color.RED);
						}
					}
					
					EditText etZLMJ = (EditText)mOwnView.findViewById(R.id.et_zlmj);
					etZLMJ.setText(kzymj+"");
					
				}
			}
			catch(NumberFormatException ex)
			{
				
			}
		}
			
	
	}
	
	
	private class FieldView
    {
    	@SuppressLint("NewApi")
		public FieldView(String fieldName,String dataFieldName,View view)
    	{
    		this.FieldName = fieldName;
    		this.DataFieldName = dataFieldName;
    		this.FieldView = view;
    	}
    	public String FieldName = "";
    	public String DataFieldName = "";
    	public View FieldView = null;
    }
}
