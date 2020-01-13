package lkmap.ZRoadMap.ToolsBox;

import java.util.ArrayList;
import java.util.HashMap;

import com.dingtu.senlinducha.R;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import lkmap.Tools.Tools;

public class v1_moretools
{
	private v1_FormTemplate _Dialog = null; 

    public v1_moretools()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_tools_more);
    	_Dialog.ReSetSize(0.5f, -1f);

    	//设置标题
    	_Dialog.SetCaption(Tools.ToLocale("工具箱"));
    	
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText));
    }

    public void ShowDialog()
    {
    	this.LoadMoreTools();
    	_Dialog.show();
    }
    
	/**
	 * 加载更多工具
	 */
	private void LoadMoreTools()
	{
	    //初始化工具条件数据
	    ArrayList<HashMap<String, Object>> ImageItemList = new ArrayList<HashMap<String, Object>>();  

	        HashMap<String, Object> map1 = new HashMap<String, Object>();  
	        map1.put("Image",R.drawable.m_option);		//添加图像资源的ID  
	        map1.put("ItemText", Tools.ToLocale("系统设置"));		//按钮显示文本
	        map1.put("ID", "系统设置");		//按钮唯一Id
	        ImageItemList.add(map1);   
	        
	        HashMap<String, Object> map3 = new HashMap<String, Object>();  
	        map3.put("Image", R.drawable.v1_checkcontrol);	
	        map3.put("ItemText", Tools.ToLocale("校正参数"));	
	        map3.put("ID", "校正参数");
	        ImageItemList.add(map3); 
	        
//	        HashMap<String, Object> map11 = new HashMap<String, Object>();  
//	        map11.put("Image",R.drawable.v1_paramanage);		//添加图像资源的ID  
//	        map11.put("ItemText", Tools.ToLocale("转换参数管理"));		//按钮显示文本
//	        map11.put("ID", "转换参数管理");		//按钮唯一Id
//	        ImageItemList.add(map11); 
	        
	        HashMap<String, Object> map11 = new HashMap<String, Object>();  
	        map11.put("Image",R.drawable.v1_paramanage);		//添加图像资源的ID  
	        map11.put("ItemText", Tools.ToLocale("数据上传"));		//按钮显示文本
	        map11.put("ID", "转换参数管理");		//按钮唯一Id
	        ImageItemList.add(map11); 
	        
	        HashMap<String, Object> map2 = new HashMap<String, Object>();  
	        map2.put("Image", R.drawable.m_statistic);
	        map2.put("ItemText", Tools.ToLocale("采集统计"));	
	        map2.put("ID", "采集统计");
	        ImageItemList.add(map2);  
	        
	        HashMap<String, Object> map4 = new HashMap<String, Object>();  
	        map4.put("Image", R.drawable.v1_cat);	
	        map4.put("ItemText", Tools.ToLocale("数据导出"));	
	        map4.put("ID", "数据导出");
	        ImageItemList.add(map4); 
	    

	        HashMap<String, Object> map41 = new HashMap<String, Object>();  
	        map41.put("Image", R.drawable.v1_datadic);	
	        map41.put("ItemText", "数据字典");	
	        map41.put("ID", "数据字典");
	        ImageItemList.add(map41);

//	        HashMap<String, Object> map43 = new HashMap<String, Object>();  
//	        map43.put("Image", R.drawable.m_save);	
//	        map43.put("ItemText", Tools.ToLocale("保存数据"));	
//	        map43.put("ID", "保存数据");
//	        ImageItemList.add(map43); 
	        
	        HashMap<String, Object> map43 = new HashMap<String, Object>();  
	        map43.put("Image", R.drawable.m_save);	
	        map43.put("ItemText", Tools.ToLocale("航迹管理"));	
	        map43.put("ID", "航迹管理");
	        ImageItemList.add(map43); 
	        
	        HashMap<String, Object> map8 = new HashMap<String, Object>();  
	        map8.put("Image", R.drawable.icon_update);	
	        map8.put("ItemText", Tools.ToLocale("系统更新"));	
	        map8.put("ID", "系统更新");
	        ImageItemList.add(map8);
	        
//	        HashMap<String, Object> map7 = new HashMap<String, Object>();  
//	        map7.put("Image", R.drawable.v1_poly);	
//	        map7.put("ItemText", Tools.ToLocale("面分析"));	
//	        map7.put("ID", "面分析");
//	        ImageItemList.add(map7);
	        
	        HashMap<String, Object> map7 = new HashMap<String, Object>();  
	        map7.put("Image", R.drawable.m_option);	
	        map7.put("ItemText", Tools.ToLocale("督查设置"));	
	        map7.put("ID", "督查设置");
	        ImageItemList.add(map7);
	        
	        HashMap<String, Object> map5 = new HashMap<String, Object>();  
	        map5.put("Image", R.drawable.v1_about);	
	        map5.put("ItemText", Tools.ToLocale("关于系统"));	
	        map5.put("ID", "关于系统");
	        ImageItemList.add(map5);

	        HashMap<String, Object> map6 = new HashMap<String, Object>();  
	        map6.put("Image", R.drawable.m_quit);	
	        map6.put("ItemText", Tools.ToLocale("退出系统"));	
	        map6.put("ID", "退出系统");
	        ImageItemList.add(map6);

	      //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应  
	      SimpleAdapter saImageItems = new SimpleAdapter(_Dialog.getContext(), //没什么解释  
	    		  										ImageItemList,//数据来源   
	    		  										R.layout.v1_toolbarsubitem,
	    		  										new String[] {"Image","ItemText"},   
	    		  										new int[] {R.id.ItemImage,R.id.ItemText});  
	      //添加并且显示  
	      GridView gridview = (GridView)_Dialog.findViewById(R.id.subgridView_tools);
	      gridview.setAdapter(saImageItems);  
	      gridview.setOnItemClickListener((OnItemClickListener) new ItemClickListener());  
	}

	  class  ItemClickListener implements OnItemClickListener  
	  {  
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id)
			{
				HashMap<String, Object> item=(HashMap<String, Object>) parent.getItemAtPosition(position);  
				String Tag = item.get("ID").toString();
				PubVar.m_DoEvent.DoCommand(Tag);
				_Dialog.dismiss();
			}
	  }
}
