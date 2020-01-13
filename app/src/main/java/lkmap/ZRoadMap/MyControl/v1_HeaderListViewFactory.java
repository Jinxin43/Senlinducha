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
	//�����б�
	private ListView m_ListView_Scroll = null;
	
	//������ͷ�Ĳ���
	private RelativeLayout m_HeaderView_Scroll = null;
	
	//��ͷģ��
	private v1_HeaderListViewTemplate m_HeaderListViewTemplate = null;
	
	
	/**
	 * ֪ͨ����Դ�����仯�ˣ�����ˢ���б���ʾ
	 */
	public void notifyDataSetInvalidated()
	{
		if (this.m_ListView_Scroll!=null)
		{
			((v1_HeaderListViewAdpter)this.m_ListView_Scroll.getAdapter()).notifyDataSetChanged();
		}
	}
	
	/**
	 * ���õ�ǰ��ѡ��������
	 * @param selectIndex
	 */
	public void SetSelectItemIndex(int selectIndex,ICallback selectItemCallback)
	{
		if (this.m_ListView_Scroll!=null)
		{
			v1_HeaderListViewAdpter hva = (v1_HeaderListViewAdpter)m_ListView_Scroll.getAdapter();
			hva.SetSelectItemIndex(selectIndex);
			hva.notifyDataSetInvalidated();//���������Ѿ��䶯
			if (selectItemCallback!=null)selectItemCallback.OnClick("�б�ѡ��", hva.getItem(selectIndex));
		}

	}
	
	/**
	 * ���ñ�����
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
	 * ���ñ�����
	 * @param headerListView
	 * @param HeaderType �Ǵ���ͷģ���д����ı�ʶ
	 * @param selectItemCallback
	 */
	public void SetHeaderListView(View headerListView,String HeaderType,List<HashMap<String,Object>> FieldHeaderList, final ICallback selectItemCallback)
	{
		//��ͷ����RelativeLayout
		this.m_HeaderView_Scroll = (RelativeLayout)headerListView.findViewById(R.id.rt_header_scroll);
		
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams)this.m_HeaderView_Scroll.getLayoutParams();
		lp.width = headerListView.getMeasuredWidth();
		this.m_HeaderView_Scroll.setLayoutParams(lp);
		
		//�����б�
		this.m_ListView_Scroll = (ListView)headerListView.findViewById(R.id.rt_listview_scroll);
		this.m_ListView_Scroll.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				ListView lv = (ListView)arg0;
				v1_HeaderListViewAdpter hva = (v1_HeaderListViewAdpter)lv.getAdapter();
				hva.SetSelectItemIndex(arg2);
				hva.notifyDataSetInvalidated();//���������Ѿ��䶯
				if (selectItemCallback!=null)selectItemCallback.OnClick("�б�ѡ��", hva.getItem(arg2));
			}});
		
		
		//������ͷģ��
		if (this.m_HeaderListViewTemplate==null) this.m_HeaderListViewTemplate = new v1_HeaderListViewTemplate();
		this.m_HeaderListViewTemplate.CreateReport(this.m_HeaderView_Scroll, HeaderType,FieldHeaderList);
	}
	
	/**
	 * �����ݵ��б�
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
	    //����
		List<ReportHeaderInfo> ColumnList= this.m_HeaderListViewTemplate.GetReportHeader().m_HeaderInfoList;
		
		//��Ҫ�󶨵Ŀؼ��������б�
		String[] bindItemList_Scroll = new String[ColumnList.size()];
		int[] bindIdList_Scroll = new int[ColumnList.size()];
		
		//���˿ؼ�
		int[] AllbindIdList_Scroll = new int[]{R.id.rp_itemtext1,R.id.rp_itemtext2,R.id.rp_itemtext3,R.id.rp_itemtext4,R.id.rp_itemtext5,R.id.rp_itemtext6,
				R.id.rp_itemtext7,R.id.rp_itemtext8,R.id.rp_itemtext9,R.id.rp_itemtext10,R.id.rp_itemtext11,R.id.rp_itemtext12,R.id.rp_itemtext13,
				R.id.rp_itemtext14,R.id.rp_itemtext15,R.id.rp_itemtext16,R.id.rp_itemtext17,R.id.rp_itemtext18,R.id.rp_itemtext19,R.id.rp_itemtext20,
				R.id.rp_itemtext21,R.id.rp_itemtext22,R.id.rp_itemtext23,R.id.rp_itemtext24,R.id.rp_itemtext25,R.id.rp_itemtext26,R.id.rp_itemtext27,
				R.id.rp_itemtext28,R.id.rp_itemtext29,R.id.rp_itemtext30};
		
		//������Ĺؼ���
		for(int i=0;i<ColumnList.size();i++)
		{
			ReportHeaderInfo RHI = this.m_HeaderListViewTemplate.GetReportHeader().m_HeaderInfoList.get(i);
			bindItemList_Scroll[i]="D"+(i+1);
			
			//Ϊ��ͬ���͵�����������
			if (RHI.ItemType==lkHeaderListViewItemType.enImage) bindIdList_Scroll[i] = R.id.rp_itemimage1;
			if (RHI.ItemType==lkHeaderListViewItemType.enText) bindIdList_Scroll[i]=AllbindIdList_Scroll[i];
			if (RHI.ItemType==lkHeaderListViewItemType.enCheckBox) bindIdList_Scroll[i]=R.id.rp_itemlayout;
		}
		
		//��
       items_Scroll = new v1_HeaderListViewAdpter(this.m_ListView_Scroll.getContext(),
    		  										DataItemList,//������Դ   
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
