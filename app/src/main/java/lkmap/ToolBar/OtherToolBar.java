package lkmap.ToolBar;

import java.util.ArrayList;
import java.util.HashMap;

import com.dingtu.senlinducha.R;

import lkmap.Tools.Tools;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;

public class OtherToolBar 
{
	/**
	 * 加载编辑工具条
	 * @param gdView
	 */
	public void LoadEditBar(View lnView)
	{
//		View view = lnView.findViewById(R.id.tb_edit_delete);view.setOnClickListener(new ViewClick());Tools.ToLocale(view);
//		view = lnView.findViewById(R.id.tb_edit_move);view.setOnClickListener(new ViewClick());Tools.ToLocale(view);
//		view = lnView.findViewById(R.id.tb_edit_vertex_add);view.setOnClickListener(new ViewClick());Tools.ToLocale(view);
//		view = lnView.findViewById(R.id.tb_edit_vertex_delete);view.setOnClickListener(new ViewClick());Tools.ToLocale(view);
//		view = lnView.findViewById(R.id.tb_edit_vertex_move);view.setOnClickListener(new ViewClick());Tools.ToLocale(view);
//		view = lnView.findViewById(R.id.tb_edit_vertex_reback);view.setOnClickListener(new ViewClick());Tools.ToLocale(view);

		
//		//初始化工具条件数据
//		ArrayList<HashMap<String, Object>> ImageItemList = new ArrayList<HashMap<String, Object>>();  
//		
//		HashMap<String, Object> map = new HashMap<String, Object>();  
//		map.put("Image", R.drawable.deleteobject);	
//		map.put("ItemText", "删除");	
//		map.put("ID", "删除实体");
//		ImageItemList.add(map);  
//		
//		HashMap<String, Object> map1 = new HashMap<String, Object>();   
//		map1.put("Image", R.drawable.move);	
//		map1.put("ItemText", "移动");	
//		map1.put("ID", "移动");
//		ImageItemList.add(map1);  
//		
//		HashMap<String, Object> map2 = new HashMap<String, Object>();  
//		map2.put("Image", R.drawable.vertex_add);	
//		map2.put("ItemText", "加节点");	
//		map2.put("ID", "加节点");
//		ImageItemList.add(map2); 
//		
//		HashMap<String, Object> map3 = new HashMap<String, Object>();  
//		map3.put("Image", R.drawable.vertex_del);	
//		map3.put("ItemText", "删节点");	
//		map3.put("ID", "删节点");
//		ImageItemList.add(map3); 
//		
//		HashMap<String, Object> map4 = new HashMap<String, Object>();  
//		map4.put("Image", R.drawable.vertex_move);	
//		map4.put("ItemText", "移节点");	
//		map4.put("ID", "移节点");
//		ImageItemList.add(map4); 
//		
//		HashMap<String, Object> map5 = new HashMap<String, Object>();  
//		map5.put("Image", R.drawable.undo2);	
//		map5.put("ItemText", "回退");	
//		map5.put("ID", "回退");
//		ImageItemList.add(map5); 
//		
//	      //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应  
//	      SimpleAdapter saImageItems = new SimpleAdapter(gdView.getContext(), //没什么解释  
//	    		  									ImageItemList,//数据来源   
//	                                                R.layout.v1_toolbaritem,//night_item的XML实现  
//	                                                  
//	                                                //动态数组与ImageItem对应的子项          
//	                                                new String[] {"Image","ItemText"},   
//	                                                  
//	                                                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
//	                                                new int[] {R.id.ItemImage,R.id.ItemText});   
//		  //添加并且显示  
//		gdView.setAdapter(saImageItems);  
//		gdView.setOnItemClickListener((OnItemClickListener) new ItemClickListener());
	}
	
	
	
	

    public class ViewClick implements OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
			PubVar.m_DoEvent.DoCommand(Tag);
			
			if (Tag.equals("解锁GPS"))
			{
				Button bt = (Button)arg0;
				Drawable lock = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.m_zoom_gps_unlock);
				lock.setBounds(0, 0, lock.getMinimumWidth(), lock.getMinimumHeight());
				bt.setCompoundDrawables(lock, null, null, null); //设置左图标
				bt.setTag("锁定GPS");
				PubVar.AutoPan=false;
			}
			if (Tag.equals("锁定GPS"))
			{
				Button bt = (Button)arg0;
				Drawable lock = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.m_zoom_gps_lock);
				lock.setBounds(0, 0, lock.getMinimumWidth(), lock.getMinimumHeight());
				bt.setCompoundDrawables(lock, null, null, null); //设置左图标
				bt.setTag("解锁GPS");
				PubVar.AutoPan=true;
			}
			
			//右下视图操作工具条的收缩与放大
			if (Tag.equals("视图_收缩"))
			{
				ShowAllViewButton(false);
				arg0.setTag("视图_扩张");
				Button bt = (Button)arg0;
				Drawable bmp = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_view_left);
				bmp.setBounds(0, 0, bmp.getMinimumWidth(), bmp.getMinimumHeight());
				bt.setCompoundDrawables(bmp, null, null, null); //设置左图标
			}
			
			if (Tag.equals("视图_扩张"))
			{
				ShowAllViewButton(true);
				arg0.setTag("视图_收缩");
				Button bt = (Button)arg0;
				Drawable bmp = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_view_right);
				bmp.setBounds(0, 0, bmp.getMinimumWidth(), bmp.getMinimumHeight());
				bt.setCompoundDrawables(bmp, null, null, null); //设置左图标				
			}
			
			//编辑工具条的收缩与展开
			if (Tag.equals("编辑工具_收缩"))
			{
				ShowEditToolsBar(false);
				arg0.setTag("编辑工具_展开");
				Button bt = (Button)arg0;
				Drawable bmp = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_showeditbar);
				bmp.setBounds(0, 0, bmp.getMinimumWidth(), bmp.getMinimumHeight());
				bt.setCompoundDrawables(bmp, null, null, null); //设置左图标
			}
			
			if (Tag.equals("编辑工具_展开"))
			{
				ShowEditToolsBar(true);
				arg0.setTag("编辑工具_收缩");
				Button bt = (Button)arg0;
				Drawable bmp = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_hideeditbar);
				bmp.setBounds(0, 0, bmp.getMinimumWidth(), bmp.getMinimumHeight());
				bt.setCompoundDrawables(bmp, null, null, null); //设置左图标				
			}
    	}
    }
    
    /**
     * 是否展示编辑工具条
     * @param ifShow
     */
    private void ShowEditToolsBar(boolean ifShow)
    {
    	if (this.m_ViewBar==null)return;
    	
    	int YFrom = 0,YTo = 1;
    	int intVisible = View.GONE;
    	if (ifShow){intVisible=View.VISIBLE;YFrom = 1;YTo = 0;}
    	View view = this.m_ViewBar.findViewById(R.id.submenu_editbar);
    	//view.setVisibility(intVisible);
    	TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,
				  Animation.RELATIVE_TO_SELF, 0,
				  Animation.RELATIVE_TO_PARENT,YFrom,
				  Animation.RELATIVE_TO_SELF,YTo);
    	mHiddenAction.setDuration(300);
		view.startAnimation(mHiddenAction);
		view.setVisibility(intVisible);
		
		
    }
    
    /**
     * 是否显示所有的视图操作工具按钮
     * @param ifall
     */
    private void ShowAllViewButton(boolean ifall)
    {
//    	if (this.m_ViewBar==null) return;
//    	int intVisible = View.VISIBLE;int partIntVisible = View.INVISIBLE;
//    	if (!ifall){intVisible=View.INVISIBLE;partIntVisible = View.VISIBLE;}
//    	
//    	int[] btnIdList = new int[]{R.id.gd_zoombar_zoomout,R.id.gd_zoombar_zoomoin,R.id.gd_zoombar_pan,
//    								R.id.gd_zoombar_fullscreen,R.id.gd_zoombar_gps};
//    	String[] AnimPara1 = new String[]{"0,2,0,0","0,0,0,0","0,1,0,1","0,1,0,-1","0,1,0,0"};
//    	String[] AnimPara2 = new String[]{"2,0,0,0","0,0,0,0","1,0,1,0","1,0,-1,0","1,0,0,0"};
//    	for(int i=0;i<btnIdList.length;i++)
//    	{
//    		String[] aniPara = AnimPara1[i].split(",");
//    		if (intVisible==View.VISIBLE)aniPara = AnimPara2[i].split(",");
//        	Button btn = (Button)this.m_ViewBar.findViewById(btnIdList[i]);
//        	TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,Float.parseFloat(aniPara[0]),
//        															  Animation.RELATIVE_TO_SELF, Float.parseFloat(aniPara[1]),
//        															  Animation.RELATIVE_TO_PARENT, Float.parseFloat(aniPara[2]),
//        															  Animation.RELATIVE_TO_SELF,Float.parseFloat(aniPara[3]));    
//        	mHiddenAction.setDuration(300);
//        	btn.setVisibility(intVisible);
//        	btn.setAnimation(mHiddenAction);
//    	}
//
//    	this.m_ViewBar.findViewById(R.id.gd_zoombar_pan_2).setVisibility(partIntVisible);
    }
    
    private View m_ViewBar = null;
	/**
	 * 加载放大、缩小、移屏、全屏工具条
	 * @param gdView
	 */
	public void LoadZoomBar(View viewBar)
	{
		this.m_ViewBar = viewBar;
//		viewBar.findViewById(R.id.gd_zoombar_zoomout).setOnClickListener(new ViewClick());
//		viewBar.findViewById(R.id.gd_zoombar_zoomoin).setOnClickListener(new ViewClick());
//		viewBar.findViewById(R.id.gd_zoombar_pan).setOnClickListener(new ViewClick());
//		viewBar.findViewById(R.id.gd_zoombar_pan_2).setOnClickListener(new ViewClick());
//		viewBar.findViewById(R.id.gd_zoombar_fullscreen).setOnClickListener(new ViewClick());
//		viewBar.findViewById(R.id.gd_zoombar_gps).setOnClickListener(new ViewClick());
//		viewBar.findViewById(R.id.gd_zoombar_allbutton).setOnClickListener(new ViewClick());
//		viewBar.findViewById(R.id.gd_zoombar_editbar).setOnClickListener(new ViewClick());
//		
//		
		//初始化比例尺条
		ImageView iv = (ImageView)viewBar.findViewById(R.id.gd_scalebar);
		PubVar.m_DoEvent.m_ScaleBar.SetImageView(iv);
		
//		viewBar.findViewById(R.id.gd_zoombar_allbutton).performClick();
	}
	
	/**
	 * 加载可伸缩工具条（视图）
	 * @param strechToolbar
	 */
	public void LoadStrechToolBar_View(final View strechToolbar)
	{
		//初始化工具条件数据
		ArrayList<HashMap<String, Object>> ImageItemList = new ArrayList<HashMap<String, Object>>();  
		HashMap<String, Object> map1 = new HashMap<String, Object>();  
		HashMap<String, Object> map2 = new HashMap<String, Object>();  
		map2.put("Image", R.drawable.v1_project);	
		map2.put("ItemText", Tools.ToLocale("工程"));	
		map2.put("ID", "工程_选择");
		ImageItemList.add(map2);
		
		map1.put("Image", R.drawable.v1_layerlist);	 
		map1.put("ItemText",Tools.ToLocale("图层"));		//按钮显示文本
		map1.put("ID", "图层");
		ImageItemList.add(map1);  
		
		HashMap<String, Object> map3 = new HashMap<String, Object>();  
		map3.put("Image", R.drawable.m_gps);	
		map3.put("ItemText", "GPS");	 
		map3.put("ID", "测试GPS");
		ImageItemList.add(map3);  
		
		
		//生成适配器的ImageItem <====> 动态数组的元素，两者一一对应  
		SimpleAdapter saImageItems = new SimpleAdapter(strechToolbar.getContext(), //没什么解释  
														ImageItemList,
														R.layout.v1_strechtoolbaritem,
														new String[] {"Image","ItemText"},   
												        new int[] {R.id.ItemImage,R.id.ItemText});  
		//添加并且显示  
		GridView gdView = (GridView)strechToolbar.findViewById(R.id.view_gridview);
		gdView.setAdapter(saImageItems);  

		//单击控制是否展开与关闭
		strechToolbar.findViewById(R.id.control_box).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				View vb = strechToolbar.findViewById(R.id.view_box);
				vb.clearAnimation();v.bringToFront();
				
				//更换图标
				ImageView IV = (ImageView)v;
				if (vb.getVisibility()==View.VISIBLE)
					{
						TranslateAnimation animation =new TranslateAnimation(Animation.RELATIVE_TO_SELF,  
				                0.0f, Animation.RELATIVE_TO_SELF, 1.0f,  
				                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,  
				                0.0f); 
								animation.setDuration(500);  
								vb.setAnimation(animation);
								animation.startNow();
						vb.setVisibility(View.GONE);
						v.setBackgroundResource(R.layout.v1_bk_corner);
						IV.setImageResource(R.drawable.v1_view_left);
					}
				else 
					{
					TranslateAnimation animation =new TranslateAnimation(Animation.RELATIVE_TO_SELF,  
			                1.0f, Animation.RELATIVE_TO_SELF, 0.0f,  
			                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,   
			                0.0f); 
							animation.setDuration(500);  
							vb.setAnimation(animation);
							animation.startNow();
							v.setBackgroundResource(R.layout.v1_bk_right_corner);
						vb.setVisibility(View.VISIBLE);
						IV.setImageResource(R.drawable.v1_view_right);
					}
				
				
			}});
		
		//绑定控制项的事件
		gdView.setOnItemClickListener((OnItemClickListener) new ItemClickListener());
	}
	
	/**
	 * 加载动态工具条（系统）
	 * @param strechToolbar
	 */
	public void LoadStrechToolBar_System(final View strechToolbar)
	{
		//初始化工具条件数据
		ArrayList<HashMap<String, Object>> ImageItemList = new ArrayList<HashMap<String, Object>>();  
		HashMap<String, Object> map1 = new HashMap<String, Object>();  
		map1.put("Image", R.drawable.m_option);	
		map1.put("ItemText", "系统设置");		//按钮显示文本
		map1.put("ID", "开关");
		ImageItemList.add(map1);  
		
		HashMap<String, Object> map2 = new HashMap<String, Object>();  
		map2.put("Image", R.drawable.m_statistic);	
		map2.put("ItemText", "采集统计");	
		map2.put("ID", "采集统计");
		ImageItemList.add(map2);  
		
		HashMap<String, Object> map3 = new HashMap<String, Object>();  
		map3.put("Image", R.drawable.m_ldlc);	
		map3.put("ItemText", "路段里程");	
		map3.put("ID", "路段里程");
		ImageItemList.add(map3);  
		
		HashMap<String, Object> map4 = new HashMap<String, Object>();  
		map4.put("Image", R.drawable.m_delete);	
		map4.put("ItemText", "删除实体");	
		map4.put("ID", "删除实体");
		ImageItemList.add(map4);  
		
		HashMap<String, Object> map41 = new HashMap<String, Object>();  
		map41.put("Image", R.drawable.m_undo);	
		map41.put("ItemText", "恢复实体");	
		map41.put("ID", "恢复实体");
		ImageItemList.add(map41);  
		
		HashMap<String, Object> map5 = new HashMap<String, Object>();  
		map5.put("Image", R.drawable.m_save);	
		map5.put("ItemText", "保存数据");	
		map5.put("ID", "保存数据");
		ImageItemList.add(map5);  
		
		HashMap<String, Object> map6 = new HashMap<String, Object>();  
		map6.put("Image", R.drawable.m_search);	
		map6.put("ItemText", "查询数据");	
		map6.put("ID", "查询数据");
		ImageItemList.add(map6);  
		
		HashMap<String, Object> map7 = new HashMap<String, Object>();  
		map7.put("Image", R.drawable.m_paraset);	
		map7.put("ItemText", "所属区划");	
		map7.put("ID", "所属区划");
		ImageItemList.add(map7);  
		
		HashMap<String, Object> map8 = new HashMap<String, Object>();  
		map8.put("Image", R.drawable.m_tolayer);	
		map8.put("ItemText", "切换底图");	
		map8.put("ID", "切换底图");
		ImageItemList.add(map8);  
		
//		HashMap<String, Object> map9 = new HashMap<String, Object>();  
//		map9.put("Image", R.drawable.m_lockscreen);	
//		map9.put("ItemText", "锁止屏幕");	
//		map9.put("ID", "锁屏");
//		ImageItemList.add(map9);  
		
		HashMap<String, Object> map10 = new HashMap<String, Object>();  
		map10.put("Image", R.drawable.aboutsystem);	
		map10.put("ItemText", "关于系统");	
		map10.put("ID", "关于系统");
		ImageItemList.add(map10);  
		
		HashMap<String, Object> map11 = new HashMap<String, Object>();  
		map11.put("Image", R.drawable.m_quit);	
		map11.put("ItemText", "退出系统");	
		map11.put("ID", "退出系统");
		ImageItemList.add(map11);  

		//生成适配器
		SimpleAdapter saImageItems = new SimpleAdapter(strechToolbar.getContext(),
														ImageItemList,
														R.layout.v1_toolbarsubitem,
														new String[] {"Image","ItemText"},   
														new int[] {R.id.ItemImage,R.id.ItemText});  
		//添加并且显示  
		final GridView gdView = (GridView)strechToolbar.findViewById(R.id.view_gridview);
		gdView.setAdapter(saImageItems);  

		//控制工具项的展开与收起
		strechToolbar.findViewById(R.id.control_box).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final View vb = strechToolbar.findViewById(R.id.view_box);
				vb.clearAnimation();v.bringToFront();
							
				if (vb.getVisibility()==View.VISIBLE)
					{
						
						TranslateAnimation animation =new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f, Animation.RELATIVE_TO_SELF, 0.0f,  
																			Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,  -1.0f); 
							animation.setDuration(500);  
							animation.setAnimationListener(new Animation.AnimationListener()
							{
								@Override
								public void onAnimationStart(	Animation animation) {	}

								@Override
								public void onAnimationEnd(Animation animation) {vb.setVisibility(View.GONE);	}

								@Override
								public void onAnimationRepeat(	Animation animation) {	vb.setVisibility(View.GONE);}
							});
							
							vb.setAnimation(animation);
							animation.startNow();
						v.setBackgroundResource(R.layout.v1_bk_corner);
					}
				else 
					{
					vb.setVisibility(View.VISIBLE);
					TranslateAnimation animation =new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f, Animation.RELATIVE_TO_SELF, 0.0f,  
			                											Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,   0.0f); 
							animation.setDuration(500);  
							vb.setAnimation(animation);
							animation.startNow();
						v.setBackgroundResource(R.layout.v1_bk_topleftright_corner);
					}
			}});
		
		//工具项的事件处理
		gdView.setOnItemClickListener((OnItemClickListener) new ItemClickListener());

	}
	
	//按钮事件处理器
	class ItemClickListener implements OnItemClickListener
	{  
		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, long id)
		{
			HashMap<String, Object> item=(HashMap<String, Object>) parent.getItemAtPosition(position);  
			String Tag = item.get("ID").toString();
			PubVar.m_DoEvent.DoCommand(Tag);
		}
	}
	
}
