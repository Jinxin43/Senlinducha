package com.dingtu.DTGIS.DataDictionary;

import java.util.ArrayList;

import com.dingtu.DTGIS.DataService.DictDataDB;
import com.dingtu.Funtion.DivisionManager;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.res.ColorStateList;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class DataDicitonary 
{
	private v1_FormTemplate DialogView = null;
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private DivisionData divisionData;
	private ForestryDict forestryData;
	ViewPager viewPager;
	
	public DataDicitonary()
	{
		DialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	DialogView.SetOtherView(R.layout.datadictionary);
    	DialogView.ReSetSize(0.56f,0.9f);
    	DialogView.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("确定")+"  ,确定", btnCallback);
    	initViewPager();
    	DialogView.findViewById(R.id.bt_viewpager_xzqh).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.bt_viewpager_ly).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.bt_viewpager_gt).setEnabled(false);
    	DialogView.findViewById(R.id.bt_viewpager_hb).setEnabled(false);
	}
	
	class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if(Tag.equals("行政区划"))
    		{
    			viewPager.setCurrentItem(0);
    			changePageViewIndex(0);
    		}
    		if(Tag.equals("林业"))
    		{
    			viewPager.setCurrentItem(1);
    			changePageViewIndex(1);
    		}
    	}
    }
	private ICallback btnCallback = new ICallback() 
	{
			
			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				
	    		if(Str.equals("确定"))
	    		{
	    			DialogView.dismiss();
	    		}
			}
	};
	
	private void initViewPager()
	{
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.divisiondata, null));
		viewContainter.add(LayoutInflater.from(PubVar.m_DoEvent.m_Context).inflate(R.layout.forestrydict, null));
		titleContainer.add("行政区划");
		titleContainer.add("林业");
		
		viewPager = (ViewPager)DialogView.findViewById(R.id.viewPager);
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
				((ViewPager)container).removeView(viewContainter.get(position));
			}
          //每次滑动的时候生成的组件
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				
				
				((ViewPager) container).addView(viewContainter.get(position));
				
				if(position == 0)
				{
					if(divisionData == null)
					{
						divisionData = new DivisionData(DialogView);
					}
				}
				if(position == 1)
				{
					if(forestryData == null)
					{
						forestryData = new ForestryDict(DialogView);
					}
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

		viewPager.setOnPageChangeListener(new OnPageChangeListener() 
		{
			@Override
			public void onPageScrollStateChanged(int arg0) 
			{
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) 
			{
			}

			@Override
			public void onPageSelected(int arg0) 
			{
			}
		});
		
		
	}
	
	private void changePageViewIndex(int position)
	{
		
		Button btnXZQH = (Button)DialogView.findViewById(R.id.bt_viewpager_xzqh);
		Button btnLY =  (Button)DialogView.findViewById(R.id.bt_viewpager_ly);
		if(position == 0)
		{
			btnLY.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnLY.setTextColor(android.graphics.Color.BLACK);
			btnXZQH.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnXZQH.setTextColor(android.graphics.Color.WHITE);
			DialogView.findViewById(R.id.locator1).setVisibility(View.INVISIBLE);
			DialogView.findViewById(R.id.locator2).setVisibility(View.VISIBLE);
		}
		if(position == 1)
		{
			btnXZQH.setBackgroundResource(R.drawable.buttonstyle_transparent_all);
			btnXZQH.setTextColor(android.graphics.Color.BLACK);
			btnLY.setBackgroundResource(R.drawable.buttonstyle_pageview);
			btnLY.setTextColor(android.graphics.Color.WHITE);
			DialogView.findViewById(R.id.locator1).setVisibility(View.INVISIBLE);
			DialogView.findViewById(R.id.locator2).setVisibility(View.INVISIBLE);
		}
		 
	}
	
	public void ShowDialog()
	{
		DialogView.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				
			}
				
    	});
    		
    	DialogView.show();
    	changePageViewIndex(0);
	}
}
