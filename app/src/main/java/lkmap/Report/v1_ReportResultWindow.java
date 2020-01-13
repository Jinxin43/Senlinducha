package lkmap.Report;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Report.v1_ReportTemplate;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;



public class v1_ReportResultWindow extends Activity 
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v1_report_listviewex);
        
        //�󶨹̶���
        v1_ReportTemplate rtFrozen = new v1_ReportTemplate();
        rtFrozen.CreateReportHeaderForFrozen((RelativeLayout)this.findViewById(R.id.rt_header_frozen),"ͳ��_����");
        
        //�󶨿ɹ�����
        v1_ReportTemplate rtSroll = new v1_ReportTemplate();
        rtSroll.CreateReportHeaderForScroll((RelativeLayout)this.findViewById(R.id.rt_header_scroll),"ͳ��_����");
        
        v1_ReportQuery reportQuery = new v1_ReportQuery();
        reportQuery.SetReportShowPara(rtFrozen.GetReportHeader(), rtSroll.GetReportHeader(), 
        							 (ListView)this.findViewById(R.id.rt_listview_frozen),
        							 (ListView)this.findViewById(R.id.rt_listview_scroll));
        reportQuery.Query(null);
        
        //rt.BindTestData((ListView)this.findViewById(R.id.listView1));
        

        
//        
//        List<HashMap<String, Object>> DataItemList1 = new ArrayList<HashMap<String, Object>>();  
//        List<HashMap<String, Object>> DataItemList2 = new ArrayList<HashMap<String, Object>>(); 
//        for(int i=1;i<100;i++)
//        {
//            HashMap<String,Object> hm1 = new HashMap<String,Object>();
//            hm1.put("t1", "�̶��̶��̶��̶�"+i);
//            DataItemList1.add(hm1);
//            
//            HashMap<String,Object> hm2 = new HashMap<String,Object>();
//            hm2.put("t1", "����������������"+i);
//            hm2.put("t2", "����"+i);
//            hm2.put("t3", "����"+i);
//            hm2.put("t4", "����"+i);
//            hm2.put("t5", "����"+i);
//            DataItemList2.add(hm2);
//        }
//
//        
//		String[] bindItemList1 = new String[]{"t1"};
//		int[] bindIdList1 = new int[]{R.id.rp_itemtext1};
//		
//		String[] bindItemList2 = new String[]{"t1","t2","t3","t4","t5"};
//		int[] bindIdList2 = new int[]{R.id.rp_itemtext1,R.id.rp_itemtext2,R.id.rp_itemtext3,R.id.rp_itemtext4,R.id.rp_itemtext5};
//		
//		
//		final ListView lv1 = (ListView)this.findViewById(R.id.rt_listview_frozen);
//		final ListView lv2 = (ListView)this.findViewById(R.id.rt_listview_scroll);
//		
//		
//      //������������ImageItem <====> ��̬�����Ԫ�أ�����һһ��Ӧ  
//      myListViewAdpter saImageItems = new myListViewAdpter(lv1.getContext(), //ûʲô����  
//    		  										DataItemList1,//������Դ   
//    		  										R.layout.v1_reporttableitem_2,
//    		  										bindItemList1,   
//    		  										bindIdList1,
//    		  										null);  
//      //��Ӳ�����ʾ  
//      lv1.setAdapter(saImageItems);  
//      lv1.setDivider(null);
//      setPullLvHeight(lv1);
//      
//      myListViewAdpter saImageItems2 = new myListViewAdpter(lv2.getContext(), //ûʲô����  
//				DataItemList2,//������Դ   
//				R.layout.v1_reporttableitem,
//				bindItemList2,   
//				bindIdList2,
//				null);  
//      //��Ӳ�����ʾ  
//      lv2.setAdapter(saImageItems2);  
//      lv2.setDivider(null);
//      
//      setPullLvHeight(lv2);
//      
//      myScrollView sv1 = (myScrollView)this.findViewById(R.id.scrollView1);
//      myScrollView sv2 = (myScrollView)this.findViewById(R.id.scrollView2);
//      sv1.SetBindScrollView(sv2);
//      sv2.SetBindScrollView(sv1);
//      

  
    }
    
    public class ViewClick implements OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{

    	}
    }
    

    
}