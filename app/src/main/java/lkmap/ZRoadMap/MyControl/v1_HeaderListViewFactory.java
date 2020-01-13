package lkmap.ZRoadMap.MyControl;

import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Enum.lkHeaderListViewItemType;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewTemplate.ReportHeaderInfo;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.Data.ICallback;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class v1_HeaderListViewFactory 
{
	//数据列表
	private ListView m_ListView_Scroll = null;
	
	//容纳列头的布局
	private RelativeLayout m_HeaderView_Scroll = null;
	
	//列头模板
	private v1_HeaderListViewTemplate m_HeaderListViewTemplate = null;
	
	
	/**
	 * 通知数据源发生变化了，让其刷新列表显示
	 */
	public void notifyDataSetInvalidated()
	{
		if (this.m_ListView_Scroll!=null)
		{
			((v1_HeaderListViewAdpter)this.m_ListView_Scroll.getAdapter()).notifyDataSetChanged();
		}
	}
	
	/**
	 * 设置当前的选择项索引
	 * @param selectIndex
	 */
	public void SetSelectItemIndex(int selectIndex,ICallback selectItemCallback)
	{
		if (this.m_ListView_Scroll!=null)
		{
			v1_HeaderListViewAdpter hva = (v1_HeaderListViewAdpter)m_ListView_Scroll.getAdapter();
			hva.SetSelectItemIndex(selectIndex);
			hva.notifyDataSetInvalidated();//提醒数据已经变动
			if (selectItemCallback!=null)selectItemCallback.OnClick("列表选项", hva.getItem(selectIndex));
		}

	}
	
	/**
	 * 设置报表布局
	 * @param headerListView
	 * @param HeaderType
	 */
	public void SetHeaderListView(View headerListView,String HeaderType)
	{
		this.SetHeaderListView(headerListView, HeaderType,null, null);
	}
	public void SetHeaderListView(View headerListView,String HeaderType,final ICallback selectItemCallback)
	{
		this.SetHeaderListView(headerListView, HeaderType,null, selectItemCallback);
	}
	
	/**
	 * 设置报表布局
	 * @param headerListView
	 * @param HeaderType 是从列头模板中创建的标识
	 * @param selectItemCallback
	 */
	public void SetHeaderListView(View headerListView,String HeaderType,List<HashMap<String,Object>> FieldHeaderList, final ICallback selectItemCallback)
	{
		//列头容器RelativeLayout
		this.m_HeaderView_Scroll = (RelativeLayout)headerListView.findViewById(R.id.rt_header_scroll);
		
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams)this.m_HeaderView_Scroll.getLayoutParams();
		lp.width = headerListView.getMeasuredWidth();
		this.m_HeaderView_Scroll.setLayoutParams(lp);
		
		//数据列表
		this.m_ListView_Scroll = (ListView)headerListView.findViewById(R.id.rt_listview_scroll);
		this.m_ListView_Scroll.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				ListView lv = (ListView)arg0;
				v1_HeaderListViewAdpter hva = (v1_HeaderListViewAdpter)lv.getAdapter();
				hva.SetSelectItemIndex(arg2);
				hva.notifyDataSetInvalidated();//提醒数据已经变动
				if (selectItemCallback!=null)selectItemCallback.OnClick("列表选项", hva.getItem(arg2));
			}});
		
		
		//创建列头模板
		if (this.m_HeaderListViewTemplate==null) this.m_HeaderListViewTemplate = new v1_HeaderListViewTemplate();
		this.m_HeaderListViewTemplate.CreateReport(this.m_HeaderView_Scroll, HeaderType,FieldHeaderList);
	}
	
	/**
	 * 绑定数据到列表
	 * @param DataItemList
	 */
	public void BindDataToListView(List<HashMap<String, Object>> DataItemList)
	{
		this.BindDataToListView(DataItemList,null);
	}
	
	private v1_HeaderListViewAdpter items_Scroll = null;
	
	public v1_HeaderListViewAdpter getAdapter()
	{
		return items_Scroll;
	}
	public void BindDataToListView(List<HashMap<String, Object>> DataItemList,ICallback pCallback)
	{
	    //列数
		List<ReportHeaderInfo> ColumnList= this.m_HeaderListViewTemplate.GetReportHeader().m_HeaderInfoList;
		
		//需要绑定的控件与数据列表
		String[] bindItemList_Scroll = new String[ColumnList.size()];
		int[] bindIdList_Scroll = new int[ColumnList.size()];
		
		//过滤控件
		int[] AllbindIdList_Scroll = new int[]{R.id.rp_itemtext1,R.id.rp_itemtext2,R.id.rp_itemtext3,R.id.rp_itemtext4,R.id.rp_itemtext5,R.id.rp_itemtext6,
				R.id.rp_itemtext7,R.id.rp_itemtext8,R.id.rp_itemtext9,R.id.rp_itemtext10,R.id.rp_itemtext11,R.id.rp_itemtext12,R.id.rp_itemtext13,
				R.id.rp_itemtext14,R.id.rp_itemtext15,R.id.rp_itemtext16,R.id.rp_itemtext17,R.id.rp_itemtext18,R.id.rp_itemtext19,R.id.rp_itemtext20,
				R.id.rp_itemtext21,R.id.rp_itemtext22,R.id.rp_itemtext23,R.id.rp_itemtext24,R.id.rp_itemtext25,R.id.rp_itemtext26,R.id.rp_itemtext27,
				R.id.rp_itemtext28,R.id.rp_itemtext29,R.id.rp_itemtext30};
		
		//数据项的关键字
		for(int i=0;i<ColumnList.size();i++)
		{
			ReportHeaderInfo RHI = this.m_HeaderListViewTemplate.GetReportHeader().m_HeaderInfoList.get(i);
			bindItemList_Scroll[i]="D"+(i+1);
			
			//为不同类型的列设置类型
			if (RHI.ItemType==lkHeaderListViewItemType.enImage) bindIdList_Scroll[i] = R.id.rp_itemimage1;
			if (RHI.ItemType==lkHeaderListViewItemType.enText) bindIdList_Scroll[i]=AllbindIdList_Scroll[i];
			if (RHI.ItemType==lkHeaderListViewItemType.enCheckBox) bindIdList_Scroll[i]=R.id.rp_itemlayout;
		}
		
		//绑定
       items_Scroll = new v1_HeaderListViewAdpter(this.m_ListView_Scroll.getContext(),
    		  										DataItemList,//数据来源   
    		  										R.layout.v1_reporttableitem,
    		  										bindItemList_Scroll,   
    		  										bindIdList_Scroll,
    		  										this.m_HeaderListViewTemplate.GetReportHeader()); 
       items_Scroll.SetCallback(pCallback);
       this.m_ListView_Scroll.setAdapter(items_Scroll);
       this.m_ListView_Scroll.setDivider(null);
       //this.m_ReportHeader_Scroll.SetItemsRelativeHeightList(frozenItemsHeightList);
       //this.SetListViewHeight(this.m_ListView_Scroll,TotalItemsHeight );
	}
	

}
