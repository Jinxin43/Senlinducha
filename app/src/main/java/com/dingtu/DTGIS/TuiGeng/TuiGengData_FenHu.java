package com.dingtu.DTGIS.TuiGeng;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DictDataDB;
import com.dingtu.DTGIS.DataService.TuiGengDB;
import com.dingtu.senlinducha.R;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_LayerList_Adpter;

public class TuiGengData_FenHu 
{
	private v1_FormTemplate dialogView = null;
	private String mLayerID;
	private int mObjID;
	private BigDecimal mKZYmianji;
	private BigDecimal mSYWFmianji;
	private int editIndex = -1; 
	private String mShuZHong ="";
	private String mDilei = "";
	private String mZHJ = "";
	private String mLinZhong = "";
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private ViewPager viewPager;
	ArrayList<HashMap<String,Object>> fenhuList = new ArrayList<HashMap<String,Object>>();
	ArrayList<HashMap<String,Object>> duixianList = new ArrayList<HashMap<String,Object>>();
	
	
	public TuiGengData_FenHu(String layerID,int objID,String shuzong,String linzhong,String dilei,String mianji,String zhj)
	{
		mObjID = objID;
		mLayerID =  layerID;
		mShuZHong = shuzong;
		mDilei = dilei;
		
		mZHJ = zhj;
		mLinZhong = linzhong;
		
		try
		{	
			if(mianji.endsWith("(亩)"))
			{
				mianji = mianji.replace("(亩)", "");
			}
			else if(mianji.endsWith("亩"))
			{
				mianji = mianji.replace("亩", "");
			}
			mKZYmianji = new BigDecimal(mianji);
			mSYWFmianji = mKZYmianji;
		}
		catch(Exception ex)
		{
			mKZYmianji = new BigDecimal("0");
			mSYWFmianji = mKZYmianji;
		}
		
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.tuigengdata_sheji_fenhu);
		dialogView.ReSetSize(0.7f,0.7f);
		dialogView.SetCaption("小班分户设计");
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+",确定  ,确定", pCallback);
		
		initViewPager();
	}
	
	private void initViewPager()
	{
		dialogView.findViewById(R.id.bt_viewpager_fenhu).setOnClickListener(new ViewClick());
		//dialogView.findViewById(R.id.bt_viewpager_duixian).setOnClickListener(new ViewClick());
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.tuigengdata_sheji_fenhulist, null));
		//viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.tuigeng_duixianlist, null));
		titleContainer.add("分户信息");
		//titleContainer.add("兑现情况");
		
		viewPager = (ViewPager)dialogView.findViewById(R.id.fenhuViewPager);
		
		
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
			public Object instantiateItem(ViewGroup container, int position) 
			{
				((ViewPager)container).addView(viewContainter.get(position));
				
				if(position == 0)
				{
					viewContainter.get(0).findViewById(R.id.bt_add).setOnClickListener(new ViewClick());
					viewContainter.get(0).findViewById(R.id.bt_delete).setOnClickListener(new ViewClick());
					initFenHuList();
				}
				if(position == 1)
				{
					initDuixian();
				}
				
				return viewContainter.get(position);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) 
			{
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
			}
		});
	}
	
	private void initDuixian()
	{
		//权属
		String dxcs ="1,2,3,4,5,6,7,8";
		ArrayAdapter<String> quanshuAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
																	android.R.layout.simple_spinner_item,
																	dxcs.split(","));
		quanshuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)viewContainter.get(1).findViewById(R.id.sp_jccs)).setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				initDuiXianList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) 
			{
				
			}
	
		});
		((Spinner)viewContainter.get(1).findViewById(R.id.sp_jccs)).setAdapter(quanshuAdapter);
		
		viewContainter.get(1).findViewById(R.id.bt_addDuiXian).setOnClickListener(new ViewClick());
		viewContainter.get(1).findViewById(R.id.bt_deleteDuiXian).setOnClickListener(new ViewClick());
		
		((ListView)viewContainter.get(1).findViewById(R.id.lvList)).setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,long arg3) {
				ListView lvList = (ListView)arg0;
				TuiGengFenhuAdapter la = (TuiGengFenhuAdapter)lvList.getAdapter();
				la.SetSelectItemIndex(arg2);
				la.notifyDataSetChanged();

				
			}});
		
	}
	
	private void initDuiXianList()
	{
		TuiGengDB db = new TuiGengDB();
		HashMap<String, String> hMap = db.getDuixianData(mLayerID, mObjID+"");
		String humings = hMap.get("D户名");
		String dxnd = hMap.get("D兑现年度");
		String dxje = hMap.get("D兑现金额");
		String zcbz = hMap.get("D政策补助");
		String zcbzbz = hMap.get("D补助标准");
		String zmf = hMap.get("D种苗费");
		String sfdx = hMap.get("D是否兑现");
		String bz = hMap.get("D备注");
		
	    duixianList.clear();
	
		if(humings== null || humings.isEmpty())
		{
			
		}
		else
		{
			String cishu = Tools.GetSpinnerValueOnID(dialogView, R.id.sp_jccs)+".";
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
								hm.put("dilei", fenhu.get("huming"));
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
			else
			{
				
			}
		}
			
			refreshDuixianList(duixianList);
	}
	
	private void changePageViewIndex(int position)
	{
		
		Button btnXBXX = (Button)dialogView.findViewById(R.id.bt_viewpager_fenhu);
		Button btnPhoto =  (Button)dialogView.findViewById(R.id.bt_viewpager_duixian);
		
		if(position == 0)
		{
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnPhoto.setTextColor(android.graphics.Color.BLACK);
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnXBXX.setTextColor(android.graphics.Color.WHITE);
		}
		if(position == 1)
		{
			btnXBXX.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnXBXX.setTextColor(android.graphics.Color.BLACK);
			btnPhoto.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnPhoto.setTextColor(android.graphics.Color.WHITE);
			
		}
	}
	
	private void initFenHuList()
	{
		ListView lvList =(((ListView)viewContainter.get(0).findViewById(R.id.lvList)));
		lvList.setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,long arg3) {
			ListView lvList = (ListView)arg0;
			TuiGengFenhuAdapter la = (TuiGengFenhuAdapter)lvList.getAdapter();
			la.SetSelectItemIndex(arg2);
			la.notifyDataSetChanged();
			
    		TuiGengData_AddFenHu addFenhu = new TuiGengData_AddFenHu();
			addFenhu.SetCallback(pCallback);
			HashMap<String,Object> seletItem = (HashMap<String,Object>)la.getItem(arg2);
			editIndex = arg2;
			addFenhu.setEditInfo(arg2,
									seletItem.get("huming")+"",
									seletItem.get("dilei")+"",
									seletItem.get("linzhong")+"",
									seletItem.get("shuzhong")+"",
									seletItem.get("miaomu")+"",
									seletItem.get("zhongzi")+"",
									seletItem.get("mianji")+"",
									mSYWFmianji,
									mZHJ,
									seletItem.get("pinkunhu")+"");
			addFenhu.ShowDialog();
			
		}});
		
		initZLSZList();
	}
	
	class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if(Tag.equals("新增分户"))
    		{
    			TuiGengData_AddFenHu addFenhu = new TuiGengData_AddFenHu();
    			addFenhu.SetCallback(pCallback);
    			addFenhu.setEditInfo(-1,"",mDilei,mLinZhong,mShuZHong,"0","0","",mSYWFmianji,mZHJ,"否");
    			addFenhu.ShowDialog();
    		}
    		if(Tag.equals("删除分户"))
    		{
    			int i = 0;
    			final List<HashMap<String,Object>> delIndex = new ArrayList<HashMap<String,Object>>();
    			for(HashMap<String,Object> hm:fenhuList)
    			{
    				if((Boolean)hm.get("isSelect"))
    				{
    					delIndex.add(hm);
    				}
    				i++;
    			}
    			
    			if(delIndex.size()>0)
    			{
    				Tools.ShowYesNoMessage(dialogView.getContext(), "是否删除选中的"+delIndex.size()+"个分户信息？", new ICallback()
    				{

    					@Override
    					public void OnClick(String Str, Object ExtraStr) {
    						
    						for(HashMap<String,Object> index:delIndex)
    						{
    							fenhuList.remove(index);
    						}
    						
    						TuiGengDB db = new TuiGengDB();
    						if(db.saveFenhuData(mLayerID, mObjID, fenhuList))
    						{
    							refreshList(fenhuList);
    						}
    					}
    				});
    			}
    			else
    			{
    				Tools.ShowMessageBox("请勾分户列表的户名！");
    			}
    			
    		}
    		
    		if(Tag.equals("新增兑现"))
    		{
//    			String cishu = Tools.GetSpinnerValueOnID(dialogView, R.id.sp_jccs);
//    			TuiGengData_AddDuiXian addDuiXian = new TuiGengData_AddDuiXian(true,mLayerID, mObjID,cishu);
//    			addDuiXian.SetCallback(pCallback);
    			//addDuiXian.ShowDialog();
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
    				Tools.ShowYesNoMessage(dialogView.getContext(), "是否删除选中的"+deleteIndex.size()+"个兑现信息？", new ICallback()
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
    		
    		if(Tag.equals("分户列表"))
    		{
    			viewPager.setCurrentItem(0);
    		}
    		
    		if(Tag.equals("兑现列表"))
    		{
    			viewPager.setCurrentItem(1);
    		}
    	}
    }
	
	private ICallback mCallback = null;
	public void SetCallback(ICallback cb)
	{
		this.mCallback = cb;
	}
	 
	private ICallback pCallback = new ICallback()
	{
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if(Str.equals("确定"))
			{
				TuiGengDB db = new TuiGengDB();
				boolean isOkay = true;
//				if(!saveDuixianData())
//				{
//					isOkay = false;
//				}
				
				if(!db.saveFenhuData(mLayerID, mObjID, fenhuList))
				{
					isOkay = false;
				}
				
				if(isOkay)
				{
					dialogView.dismiss();
				}
			}
			
			if(Str.equals("新增分户"))
			{
				fenhuList.add((HashMap<String,Object>)ExtraStr);
				refreshList(fenhuList);
			}
			if(Str.equals("编辑分户"))
			{
				try
				{
					if(editIndex>-1)
					{
						fenhuList.set(editIndex,(HashMap<String,Object>)ExtraStr);
					}
					
				}
				catch(Exception e)
				{
					
				}
				
				refreshList(fenhuList);
			}
			
			if(Str.equals("新增兑现"))
			{
				duixianList.add((HashMap<String,Object>)ExtraStr);
				refreshDuixianList(duixianList);
			}
			
			if(Str.equals("编辑兑现"))
			{
				editDuixian((HashMap<String,Object>)ExtraStr);
			}
		}
	 };
	 
	 
	
	 
	private void initZLSZList()
	{
		//Tools.SetTextViewValueOnID(dialogView,R.id.tv_select, curValue);
		TuiGengDB db = new TuiGengDB();
		HashMap<String, String> hMap = db.getFenhuData(mLayerID, mObjID+"");
		String humings = hMap.get("户名s");
		String dileis = hMap.get("地类s");
		String mianjis = hMap.get("面积s");
		String linzhongs = hMap.get("林种s");
		String shuzhus = hMap.get("树种s");
		String miaomus = hMap.get("苗木s");
		String zhongzis = hMap.get("种子s");
		String pinkunhu = hMap.get("贫困户s");
		
		if(humings== null || humings.isEmpty())
		{
			
		}
		else
		{
			String[] arrayHM = humings.split(",");
			String[] arrayDL = dileis.split(",");
			String[] arrayMJ = mianjis.split(",");
			String[] arraySZ = shuzhus.split(",");
			
			String[] arrayLZ = linzhongs.split(",");
			String[] arrayMM = miaomus.split(",");
			String[] arrayZZ = zhongzis.split(",");
			String[] arrayPKH;
			if(pinkunhu == null||pinkunhu.isEmpty())
			{
				arrayPKH = new String[arrayHM.length];
				for(int i=0;i<arrayPKH.length;i++)
				{
					arrayPKH[i] = "否";
				}
			}
			else
			{
				arrayPKH = pinkunhu.split(",");
			}
			
			for(int i=0;i<arrayHM.length;i++)
			{
				HashMap<String,Object> hm = new HashMap<String,Object>();
				try
				{
					hm.put("isSelect", false);
					hm.put("huming", arrayHM[i]);
					hm.put("dilei", arrayDL[i]);
					hm.put("mianji", arrayMJ[i]);
					hm.put("linzhong", arrayLZ[i]);
					hm.put("shuzhong", arraySZ[i]);
					hm.put("miaomu", arrayMM[i]);
					hm.put("zhongzi", arrayZZ[i]);
					try
					{
						hm.put("pinkunhu", arrayPKH[i]);	
					}
					catch(Exception e)
					{
						hm.put("pinkunhu","否");
					}
					
					fenhuList.add(hm);
					
					mSYWFmianji = mSYWFmianji.subtract(new BigDecimal(arrayMJ[i]));
					if(mSYWFmianji.compareTo(new BigDecimal("0"))<1)
					{
						mSYWFmianji = new BigDecimal("0");
					}
					
				}
				catch(Exception ex)
				{
					Tools.ShowMessageBox(ex.getMessage());
				}
			}
		}
		
		refreshList(fenhuList);
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
	
	private void refreshDuixianList(ArrayList<HashMap<String,Object>> list)
	{
		
		TuiGengFenhuAdapter fenhuAdapter = new TuiGengFenhuAdapter(dialogView.getContext(),
				list,
				R.layout.tuigeng_duixianlist_item,
				new String[] {"isSelect","huming","dilei","mianji","linzhong","shuzhong","niandu","dxje","zhongmiao","buzhu","beizhu","sfdx"},
				new int[] {R.id.cb_select,R.id.tv_hm, R.id.tv_dl, R.id.tv_mj,R.id.tv_lz,R.id.tv_sz,
							R.id.tv_dxnd,R.id.tv_dxje,R.id.tv_zmf,R.id.tv_zcbz,R.id.tv_bz,R.id.tv_sfdx});

		(((ListView)viewContainter.get(1).findViewById(R.id.lvList))).setAdapter(fenhuAdapter);
	}
	
	private void refreshList(ArrayList<HashMap<String,Object>> list)
	{
		TuiGengFenhuAdapter fenhuAdapter = new TuiGengFenhuAdapter(dialogView.getContext(),
				list,
				R.layout.tuigengdata_sheji_fenhuitem,
				new String[] {"isSelect","huming","dilei","mianji","linzhong","shuzhong","miaomu","zhongzi","pinkunhu"},
				new int[] {R.id.cb_select,R.id.tv_hm, R.id.tv_dl, R.id.tv_zlmj,R.id.tv_lz,R.id.tv_sz,R.id.tv_mm,R.id.tv_zz,R.id.tv_pkh});

		(((ListView)viewContainter.get(0).findViewById(R.id.lvList))).setAdapter(fenhuAdapter);
		
			
		if(mCallback != null)
		{
			mCallback.OnClick("分户", fenhuList.size());
		}
		
		mSYWFmianji = mKZYmianji;
		BigDecimal yfhmj = new BigDecimal("0");
		for(HashMap<String,Object> hm:fenhuList)
		{
			try
			{
				BigDecimal fhmj = new BigDecimal(hm.get("mianji")+"");
				mSYWFmianji = mSYWFmianji.subtract(fhmj);
				yfhmj = yfhmj.add(fhmj);
			}
			catch(Exception ex)
			{
				Tools.ShowMessageBox(ex.getMessage());
			}
		}
		
		((TextView)viewContainter.get(0).findViewById( R.id.tv_sum)).setText("造林面积:"+mKZYmianji+"亩，未分户面积:"+mSYWFmianji+"亩，已分户面积:"+yfhmj+"亩");
		//Tools.SetTextViewValueOnID(viewContainter.get(0), R.id.tv_sum,"造林面积:"+mKZYmianji+"亩，未分户面积:"+mSYWFmianji+"亩，已分户面积:"+yfhmj+"亩");
	}
	
	public void ShowDialog()
    {
		dialogView.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				changePageViewIndex(0);
			}}
    	);
		dialogView.show();
    }
	
}
