package lkmap.Report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dingtu.senlinducha.R;

import lkmap.Dataset.DataSource;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import dingtu.ZRoadMap.PubVar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class v1_ReportTemplate 
{
	private ReportHeader m_ReportHeader = null;
	public ReportHeader GetReportHeader(){return m_ReportHeader;}
	
	/**
	 * 创建固定列头
	 * @param frozenHeaderView 
	 * @param headerType 表头类型
	 */
	public void CreateReportHeaderForFrozen(RelativeLayout frozenHeaderView,String headerType)
	{
		this.CreateReport(frozenHeaderView,1, headerType);
	}
	
	/**
	 * 创建可滚动列头
	 * @param scrollHeaderView
	 * @param headerType 表头类型
	 */
	public void CreateReportHeaderForScroll(RelativeLayout scrollHeaderView,String headerType)
	{
		this.CreateReport(scrollHeaderView,2, headerType);
	}
	
	/**
	 * 创建列头
	 * @param headerView
	 * @param headerType  1-固定列，2-滚动列
	 */
	private void CreateReport(RelativeLayout headerView,int headerType,String headerTag)
	{
		this.m_ReportHeader = new ReportHeader();
		if (headerType==1)this.m_ReportHeader.m_IsFrozen=true;
		if (headerType==2)this.m_ReportHeader.m_IsFrozen=false;
		
		//标签列的详细定义
		List<ReportHeaderInfo> _ReportStructInfoList = new ArrayList<ReportHeaderInfo>();
		
		//定义每列的宽度
		List<Integer> _ReportHeaderWidthList = null;
		
		switch(headerType)
		{
			case 1:  //固定列
				
				//路线
				if (headerTag.equals("综合查询_路线_技术等级+路面类型"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("路面\\等级",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{100}); //列宽度
				}
				
				if (headerTag.equals("综合查询_路线_里程汇总"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("里程分类",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80}); //列宽度
				}
				
				if (headerTag.equals("综合查询_路线_路段明细"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("路段识别",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{200}); //列宽度
				}
				
				if (headerTag.equals("统计_路线_按技术等级_区划") || headerTag.equals("统计_路线_按路面类型_区划"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("行政区划",1,2,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{100}); //列宽度
				}
				
				
				//桥梁
				if (headerTag.equals("综合查询_桥梁_按跨径统计"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{40}); //列宽度
				}
				
				if (headerTag.equals("综合查询_桥梁_按使用年限统计"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{40}); //列宽度
				}
				
				if (headerTag.equals("综合查询_桥梁_桥梁明细"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("桥梁识别",1,1,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{200}); //列宽度
				}

				if (headerTag.equals("统计_桥梁_按跨径_区划"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("行政区划",1,3,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{100}); //列宽度
				}
				if (headerTag.equals("统计_桥梁_按年限_区划"))
				{
					_ReportStructInfoList.add(new ReportHeaderInfo("行政区划",1,3,1,1,true));
					_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{100}); //列宽度
				}
				

				break;
				
			case 2:	  //滚动列
				if (headerType==2)
				{
					//路线
					if (headerTag.equals("综合查询_路线_技术等级+路面类型"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("高速",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("一级",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("二级",1,1,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("三级",1,1,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("四级",1,1,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("等外",1,1,6,6,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,80,80,80,80,80}); //列宽度
					}
					
					if (headerTag.equals("综合查询_路线_里程汇总"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("里程汇总",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("里程分类",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("里程汇总",1,1,3,3,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,100,80}); //列宽度
					}
					
					if (headerTag.equals("综合查询_路线_路段明细"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("技术等级",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("路面类型",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("里程",1,1,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("重复路",1,1,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("断头路",1,1,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("城管路",1,1,6,6,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,80,80,50,50,50}); //列宽度
					}
					
					if (headerTag.equals("统计_路线_按技术等级_区划"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("公路里程",1,2,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("等级公路",1,1,2,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("高速",2,2,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("一级",2,2,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("二级",2,2,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("三级",2,2,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("四级",2,2,6,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("等外",1,2,7,7,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{70,70,70,70,70,70,70}); //列宽度
					}
					
					if (headerTag.equals("统计_路线_按路面类型_区划"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("公路里程",1,2,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("有铺装路面",1,1,2,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("水泥混凝土",2,2,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("沥青混凝土",2,2,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("沥青碎石",2,2,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("沥青贯入式",2,2,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("简易铺装路面",1,1,6,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("砂石",2,2,6,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("渣石",2,2,7,7,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("砖铺",2,2,8,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("无路面",1,2,9,9,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{70,90,90,90,80,90,70,70,70}); //列宽度
					}
					
					//桥梁
					if (headerTag.equals("综合查询_桥梁_按跨径统计"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("合计",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("特大桥",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("大桥",1,1,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("中桥",1,1,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("小桥",1,1,5,5,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,80,80,80,80}); //列宽度
					}
					
					if (headerTag.equals("综合查询_桥梁_按使用年限统计"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("合计",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("危桥",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("永久性",1,1,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("半永久性",1,1,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("临性",1,1,5,5,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,80,80,80,80}); //列宽度
					}
					
					if (headerTag.equals("综合查询_桥梁_桥梁明细"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("桥长",1,1,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("类型",1,1,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("性质",1,1,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("危桥",1,1,4,4,false));
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{80,80,80,50}); //列宽度
					}
					
					if (headerTag.equals("统计_桥梁_按跨径_区划"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("合计",1,2,1,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("按跨径分",1,1,3,10,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("特大桥",2,2,3,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("大桥",2,2,5,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,6,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("中桥",2,2,7,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,7,7,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,8,8,false));
						
						_ReportStructInfoList.add(new ReportHeaderInfo("小桥",2,2,9,10,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,9,9,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,10,10,false));
						
						_ReportStructInfoList.add(new ReportHeaderInfo("合计中危桥",1,2,11,12,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,11,11,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,12,12,false));
						
						 //列宽度
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{70,70,70,70,70,70,70,70,70,70,70,70});
					}
					
					if (headerTag.equals("统计_桥梁_按年限_区划"))
					{
						_ReportStructInfoList.add(new ReportHeaderInfo("合计",1,2,1,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,1,1,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,2,2,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("按建筑材料和使用年限分",1,1,3,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("永久性",2,2,3,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,3,3,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,4,4,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("半永久性",2,2,5,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,5,5,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,6,6,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("临时性",2,2,7,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,7,7,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,8,8,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("合计中危桥",1,2,9,10,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("米",3,3,9,9,false));
						_ReportStructInfoList.add(new ReportHeaderInfo("座",3,3,10,10,false));
						 //列宽度
						_ReportHeaderWidthList = this.ConverDipToPix(headerView, new int[]{70,70,70,70,70,70,70,70,70,70});
					}
					
				}
				break;
		}
		

		//计算列的总宽度，值给相应视图，防止固定列的宽度不足或超出
		int ColumnWidth = 0;
		for(int w :_ReportHeaderWidthList)ColumnWidth+=w;
		ViewParent vp = headerView.getParent();
		View view = ((LinearLayout)vp).getChildAt(1);
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams)view.getLayoutParams();
		lp.width = ColumnWidth;
		view.setLayoutParams(lp);
		
		
		//赋每个标签列的详细信息
		this.m_ReportHeader.SetHeaderInfoList(_ReportStructInfoList);
		
		//每列的宽度列表
		this.m_ReportHeader.SetHeaderWidthList(_ReportHeaderWidthList);
		
		//表头行高
		int RowHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30, headerView.getResources().getDisplayMetrics());
		
		//动态生成表头
		for(ReportHeaderInfo header:_ReportStructInfoList)
		{
			int StartColLeftMargin = 0;int EndColLeftMargin = 0;
			for(int i=0;i<header.StartCol-1;i++)StartColLeftMargin+=_ReportHeaderWidthList.get(i);
			for(int i=0;i<header.EndCol;i++)EndColLeftMargin+=_ReportHeaderWidthList.get(i);
			
			TextView tv = new TextView(headerView.getContext());
			tv.setText(header.Text);
			tv.setTextColor(Color.BLACK);
			tv.setBackgroundResource(R.layout.v1_bk_table_header);
			tv.setGravity(Gravity.CENTER);
			
			RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams(0, 0);
			Params.leftMargin = StartColLeftMargin;
			Params.topMargin = (header.StartRow-1) * RowHeight;
			Params.width = (EndColLeftMargin - StartColLeftMargin);
			Params.height = (header.EndRow - header.StartRow+1)* RowHeight;
			headerView.addView(tv, Params);
		}

	}
	
	/**
	 * 转换dip到pix
	 * @param headerView
	 * @param dipList
	 * @return
	 */
	private List<Integer> ConverDipToPix(View headerView,int[] dipList)
	{
		List<Integer> _ReportHeaderWidthList = new ArrayList<Integer>();
		for(int w :dipList)//将Dip转换为pix
		{
			_ReportHeaderWidthList.add((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,w, headerView.getResources().getDisplayMetrics()));
		}
		return _ReportHeaderWidthList;
	}
	
	public class ReportHeaderInfo
	{
		public String Text;
		public int StartRow;
		public int EndRow;
		public int StartCol;
		public int EndCol;
		public ReportHeaderInfo(String text,int startRow,int endRow,int startCol,int endCol,boolean frozen)
		{
			this.Text = text;
			this.StartRow = startRow;
			this.EndRow = endRow;
			this.StartCol = startCol;
			this.EndCol = endCol;
			this.Frozen = frozen;
		}
		public boolean Frozen = false;
	}
	
	public class ReportHeader
	{
		public boolean m_IsFrozen = true;
		
		//每列的宽度值
		public List<Integer> m_HeaderWidthList = null;
		
		//每个标签列的详细信息
		private List<ReportHeaderInfo> m_HeaderInfoList= null;
		public void SetHeaderInfoList(List<ReportHeaderInfo> _HeaderInfoList)
		{
			this.m_HeaderInfoList = _HeaderInfoList;
		}
		
		public void SetHeaderWidthList(List<Integer> _HeaderWidthList)
		{
			this.m_HeaderWidthList = _HeaderWidthList;
		}
		
		//报表项的高度列表，主要用于有固定列的情况
		public List<Integer> m_ItemsRelativeHeightList = null;
		public void SetItemsRelativeHeightList(List<Integer> _itemsHeightList)
		{
			this.m_ItemsRelativeHeightList = _itemsHeightList;
		}
	}
	

}
