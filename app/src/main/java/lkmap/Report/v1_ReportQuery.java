package lkmap.Report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dingtu.senlinducha.R;

import lkmap.Dataset.DataSource;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Report.v1_ReportTemplate.ReportHeader;
import lkmap.Tools.Tools;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import dingtu.ZRoadMap.PubVar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class v1_ReportQuery 
{
	//固定列头
	private ReportHeader m_ReportHeader_Frozen = null;
	private ListView m_ListView_Frozen = null;
	
	//可滚动列头
	private ReportHeader m_ReportHeader_Scroll = null;
	private ListView m_ListView_Scroll = null;
	
	/**
	 * 设置报表数据的显示参数
	 * @param _headerFrozen
	 * @param _headerScroll
	 * @param _listViewFrozen
	 * @param _listViewScroll
	 */
	public void SetReportShowPara(ReportHeader _headerFrozen,ReportHeader _headerScroll,ListView _listViewFrozen,ListView _listViewScroll)
	{
		this.m_ReportHeader_Frozen = _headerFrozen;
		this.m_ReportHeader_Scroll = _headerScroll;
		this.m_ListView_Frozen = _listViewFrozen;
		this.m_ListView_Scroll = _listViewScroll;
	}
	
	/**
	 * 开始查询数据
	 * @param queryPara
	 */
	public void Query(HashMap<String,String> queryPara)
	{
		ArrayList<HashMap<String, Object>> DataItemList = new ArrayList<HashMap<String, Object>>();  
		
		//查询数据
		DataSource pDataSource = PubVar.m_Workspace.GetDataSourceByName("TP");  //背景图层数据源
		String SQL = "select sum(f14) as QLCD,count(*) as QLZS,F5 as AKJFL,F25 as TBDW from 桥梁0 group by F5,F25 order by TBDW";
		SQLiteDataReader DR = pDataSource.Query(SQL);
		if (DR==null) return;
		
		HashMap<String,HashMap<String,Object>> resultList = new HashMap<String,HashMap<String,Object>>();
		while(DR.Read())
		{
			String TBDW = DR.GetString("TBDW");
			String QLCD = DR.GetString("QLCD");
			String QLZS = DR.GetString("QLZS");
			String AKJFL = DR.GetString("AKJFL");
			
			HashMap<String,Object> data = null;
			if (!resultList.containsKey(TBDW))
			{
				data = new HashMap<String,Object>();
				data.put("TBDW", TBDW);
				data.put("HJ_M", "");data.put("HJ_Z", "");
				data.put("TDQ_M", "");data.put("TDQ_Z", "");
				data.put("DQ_M", "");data.put("DQ_Z", "");
				data.put("ZQ_M", "");data.put("ZQ_Z", "");
				data.put("XQ_M", "");data.put("XQ_Z", "");
				data.put("WQ_M", "");data.put("WQ_Z", "");
				resultList.put(TBDW, data);
			} else data = resultList.get(TBDW);
			
			data.put("HJ_M", Tools.ParseFloat(data.get("HJ_M").toString(), 2)+Tools.ParseFloat(QLCD,2));
			data.put("HJ_Z", Tools.ParseInt(data.get("HJ_Z").toString())+Tools.ParseInt(QLZS));
			if (AKJFL.equals("特大桥"))
			{
				data.put("TDQ_M", Tools.ParseFloat(data.get("TDQ_M").toString(),2)+Tools.ParseFloat(QLCD,2));
				data.put("TDQ_Z", Tools.ParseInt(data.get("TDQ_Z").toString())+Tools.ParseInt(QLZS));
			}
			if (AKJFL.equals("大桥"))
			{
				data.put("DQ_M", Tools.ParseFloat(data.get("DQ_M").toString(),2)+Tools.ParseFloat(QLCD,2));
				data.put("DQ_Z", Tools.ParseInt(data.get("DQ_Z").toString())+Tools.ParseInt(QLZS));
			}
			if (AKJFL.equals("中桥"))
			{
				data.put("ZQ_M", Tools.ParseFloat(data.get("ZQ_M").toString(),2)+Tools.ParseFloat(QLCD,2));
				data.put("ZQ_Z", Tools.ParseInt(data.get("ZQ_Z").toString())+Tools.ParseInt(QLZS));
			}
			if (AKJFL.equals("小桥"))
			{
				data.put("XQ_M", Tools.ParseFloat(data.get("XQ_M").toString(),2)+Tools.ParseFloat(QLCD,2));
				data.put("XQ_Z", Tools.ParseInt(data.get("XQ_Z").toString())+Tools.ParseInt(QLZS));
			}
		}
		
		Iterator iter = resultList.entrySet().iterator();  
		while (iter.hasNext()) {  
		    Map.Entry entry = (Map.Entry) iter.next();  
		    Object val = entry.getValue();  
		    DataItemList.add((HashMap<String, Object>)val);  
		} 

		this.BindDataToListView(DataItemList);

	}
	
	/**
	 * 绑定数据到列表
	 * @param DataItemList  数据项列表
	 * @param Frozen_DataItemKeyList 固定列数据项关键字
	 * @param Frozen_ViewIdList  固定列绑定视图项Id
	 * @param Frozen_LayoutId  固定列绑定布局视图
	 * @param Scroll_DataItemKeyList 滚动列数据项关键字
	 * @param Scroll_ViewIdList  滚动列绑定视图项Id
	 * @param Scroll_LayoutId  滚动列绑定布局视图
	 */
	public void BindDataToListViewEx(List<HashMap<String, Object>> DataItemList,
									String[] Frozen_DataItemKeyList,int[] Frozen_ViewIdList,int Frozen_LayoutId,
									String[] Scroll_DataItemKeyList,int[] Scroll_ViewIdList,int Scroll_LayoutId)
	{
		//绑定固定列部分
       v1_myListViewAdpter items_Frozen = new v1_myListViewAdpter(this.m_ListView_Scroll.getContext(),
    		  										DataItemList,//数据来源   
    		  										Frozen_LayoutId,
    		  										Frozen_DataItemKeyList,   
    		  										Frozen_ViewIdList,
    		  										this.m_ReportHeader_Frozen);  
       this.m_ListView_Frozen.setAdapter(items_Frozen);  
       this.m_ListView_Frozen.setDivider(null);
       
       List<Integer> frozenItemsHeightList = this.CalItemsHeightList(this.m_ListView_Frozen);
       int TotalItemsHeight = this.CalTotalItemsHeight(frozenItemsHeightList);
       this.m_ReportHeader_Frozen.SetItemsRelativeHeightList(frozenItemsHeightList);  //此处的作为当固定列表两个以上时，调整列的高度相一致
       this.SetListViewHeight(this.m_ListView_Frozen,TotalItemsHeight);
		
       
	    //绑定可滚动部分
       v1_myListViewAdpter items_Scroll = new v1_myListViewAdpter(this.m_ListView_Scroll.getContext(),
    		  										DataItemList,//数据来源   
    		  										Scroll_LayoutId,
    		  										Scroll_DataItemKeyList,   
    		  										Scroll_ViewIdList,
    		  										this.m_ReportHeader_Scroll);  
       this.m_ListView_Scroll.setAdapter(items_Scroll);  
       this.m_ListView_Scroll.setDivider(null);
       this.m_ReportHeader_Scroll.SetItemsRelativeHeightList(frozenItemsHeightList);
       this.SetListViewHeight(this.m_ListView_Scroll,TotalItemsHeight );
	}
	
	private void BindDataToListView(List<HashMap<String, Object>> DataItemList)
	{
		//绑定固定列部分
		String[] bindItemList_Frozen = new String[]{"TBDW"};
		int[] bindIdList_Frozen = new int[]{R.id.rp_itemtext1};
       v1_myListViewAdpter items_Frozen = new v1_myListViewAdpter(this.m_ListView_Scroll.getContext(),
    		  										DataItemList,//数据来源   
    		  										R.layout.v1_reporttableitem_2,
    		  										bindItemList_Frozen,   
    		  										bindIdList_Frozen,
    		  										this.m_ReportHeader_Frozen);  
       this.m_ListView_Frozen.setAdapter(items_Frozen);  
       this.m_ListView_Frozen.setDivider(null);
       
       List<Integer> frozenItemsHeightList = this.CalItemsHeightList(this.m_ListView_Frozen);
       int TotalItemsHeight = this.CalTotalItemsHeight(frozenItemsHeightList);
       this.SetListViewHeight(this.m_ListView_Frozen,TotalItemsHeight );
		
		
	    //绑定可滚动部分
		String[] bindItemList_Scroll = new String[]{"HJ_M","HJ_Z","TDQ_M","TDQ_Z","DQ_M","DQ_Z","ZQ_M","ZQ_Z","XQ_M","XQ_Z","WQ_M","WQ_Z"};
		int[] bindIdList_Scroll = new int[]{R.id.rp_itemtext1,R.id.rp_itemtext2,R.id.rp_itemtext3,R.id.rp_itemtext4,R.id.rp_itemtext5,R.id.rp_itemtext6,R.id.rp_itemtext7,R.id.rp_itemtext8,R.id.rp_itemtext9,R.id.rp_itemtext10,R.id.rp_itemtext11,R.id.rp_itemtext12};
       v1_myListViewAdpter items_Scroll = new v1_myListViewAdpter(this.m_ListView_Scroll.getContext(),
    		  										DataItemList,//数据来源   
    		  										R.layout.v1_reporttableitem,
    		  										bindItemList_Scroll,   
    		  										bindIdList_Scroll,
    		  										this.m_ReportHeader_Scroll);  
       this.m_ListView_Scroll.setAdapter(items_Scroll);  
       this.m_ListView_Scroll.setDivider(null);
       this.m_ReportHeader_Scroll.SetItemsRelativeHeightList(frozenItemsHeightList);
       this.SetListViewHeight(this.m_ListView_Scroll,TotalItemsHeight );
	}
	
	//计算指定ListView中每项的高度
    private List<Integer> CalItemsHeightList(ListView listView)
    {  
    	List<Integer> itemsHeightList = new ArrayList<Integer>();
		int totalHeight = 0;    
		ListAdapter adapter= listView.getAdapter();
		for (int i = 0, len = adapter.getCount(); i < len; i++) 
		{ 
			View listItem = adapter.getView(i, null, listView);    
			listItem.measure(0, 0); //计算子项View 的宽高    
			int itemHeight = listItem.getMeasuredHeight();
			totalHeight += itemHeight; //统计所有子项的总高度
			itemsHeightList.add(itemHeight);
		}
		return itemsHeightList;
    }
    
    private int CalTotalItemsHeight(List<Integer> itemsHeightList)
    {
    	int allh = 0;
    	for(int h:itemsHeightList)allh+=h;
    	return allh;
    }
    
    private void SetListViewHeight(ListView listView,int totalItemsHeight)
    {
		ViewGroup.LayoutParams params = listView.getLayoutParams();    
		params.height = totalItemsHeight + (listView.getDividerHeight() * (listView.getCount() - 1)); 
		listView.setLayoutParams(params);   
    }
}
