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
	 * ���ر༭������
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

		
//		//��ʼ��������������
//		ArrayList<HashMap<String, Object>> ImageItemList = new ArrayList<HashMap<String, Object>>();  
//		
//		HashMap<String, Object> map = new HashMap<String, Object>();  
//		map.put("Image", R.drawable.deleteobject);	
//		map.put("ItemText", "ɾ��");	
//		map.put("ID", "ɾ��ʵ��");
//		ImageItemList.add(map);  
//		
//		HashMap<String, Object> map1 = new HashMap<String, Object>();   
//		map1.put("Image", R.drawable.move);	
//		map1.put("ItemText", "�ƶ�");	
//		map1.put("ID", "�ƶ�");
//		ImageItemList.add(map1);  
//		
//		HashMap<String, Object> map2 = new HashMap<String, Object>();  
//		map2.put("Image", R.drawable.vertex_add);	
//		map2.put("ItemText", "�ӽڵ�");	
//		map2.put("ID", "�ӽڵ�");
//		ImageItemList.add(map2); 
//		
//		HashMap<String, Object> map3 = new HashMap<String, Object>();  
//		map3.put("Image", R.drawable.vertex_del);	
//		map3.put("ItemText", "ɾ�ڵ�");	
//		map3.put("ID", "ɾ�ڵ�");
//		ImageItemList.add(map3); 
//		
//		HashMap<String, Object> map4 = new HashMap<String, Object>();  
//		map4.put("Image", R.drawable.vertex_move);	
//		map4.put("ItemText", "�ƽڵ�");	
//		map4.put("ID", "�ƽڵ�");
//		ImageItemList.add(map4); 
//		
//		HashMap<String, Object> map5 = new HashMap<String, Object>();  
//		map5.put("Image", R.drawable.undo2);	
//		map5.put("ItemText", "����");	
//		map5.put("ID", "����");
//		ImageItemList.add(map5); 
//		
//	      //������������ImageItem <====> ��̬�����Ԫ�أ�����һһ��Ӧ  
//	      SimpleAdapter saImageItems = new SimpleAdapter(gdView.getContext(), //ûʲô����  
//	    		  									ImageItemList,//������Դ   
//	                                                R.layout.v1_toolbaritem,//night_item��XMLʵ��  
//	                                                  
//	                                                //��̬������ImageItem��Ӧ������          
//	                                                new String[] {"Image","ItemText"},   
//	                                                  
//	                                                //ImageItem��XML�ļ������һ��ImageView,����TextView ID  
//	                                                new int[] {R.id.ItemImage,R.id.ItemText});   
//		  //��Ӳ�����ʾ  
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
			
			if (Tag.equals("����GPS"))
			{
				Button bt = (Button)arg0;
				Drawable lock = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.m_zoom_gps_unlock);
				lock.setBounds(0, 0, lock.getMinimumWidth(), lock.getMinimumHeight());
				bt.setCompoundDrawables(lock, null, null, null); //������ͼ��
				bt.setTag("����GPS");
				PubVar.AutoPan=false;
			}
			if (Tag.equals("����GPS"))
			{
				Button bt = (Button)arg0;
				Drawable lock = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.m_zoom_gps_lock);
				lock.setBounds(0, 0, lock.getMinimumWidth(), lock.getMinimumHeight());
				bt.setCompoundDrawables(lock, null, null, null); //������ͼ��
				bt.setTag("����GPS");
				PubVar.AutoPan=true;
			}
			
			//������ͼ������������������Ŵ�
			if (Tag.equals("��ͼ_����"))
			{
				ShowAllViewButton(false);
				arg0.setTag("��ͼ_����");
				Button bt = (Button)arg0;
				Drawable bmp = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_view_left);
				bmp.setBounds(0, 0, bmp.getMinimumWidth(), bmp.getMinimumHeight());
				bt.setCompoundDrawables(bmp, null, null, null); //������ͼ��
			}
			
			if (Tag.equals("��ͼ_����"))
			{
				ShowAllViewButton(true);
				arg0.setTag("��ͼ_����");
				Button bt = (Button)arg0;
				Drawable bmp = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_view_right);
				bmp.setBounds(0, 0, bmp.getMinimumWidth(), bmp.getMinimumHeight());
				bt.setCompoundDrawables(bmp, null, null, null); //������ͼ��				
			}
			
			//�༭��������������չ��
			if (Tag.equals("�༭����_����"))
			{
				ShowEditToolsBar(false);
				arg0.setTag("�༭����_չ��");
				Button bt = (Button)arg0;
				Drawable bmp = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_showeditbar);
				bmp.setBounds(0, 0, bmp.getMinimumWidth(), bmp.getMinimumHeight());
				bt.setCompoundDrawables(bmp, null, null, null); //������ͼ��
			}
			
			if (Tag.equals("�༭����_չ��"))
			{
				ShowEditToolsBar(true);
				arg0.setTag("�༭����_����");
				Button bt = (Button)arg0;
				Drawable bmp = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_hideeditbar);
				bmp.setBounds(0, 0, bmp.getMinimumWidth(), bmp.getMinimumHeight());
				bt.setCompoundDrawables(bmp, null, null, null); //������ͼ��				
			}
    	}
    }
    
    /**
     * �Ƿ�չʾ�༭������
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
     * �Ƿ���ʾ���е���ͼ�������߰�ť
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
	 * ���طŴ���С��������ȫ��������
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
		//��ʼ����������
		ImageView iv = (ImageView)viewBar.findViewById(R.id.gd_scalebar);
		PubVar.m_DoEvent.m_ScaleBar.SetImageView(iv);
		
//		viewBar.findViewById(R.id.gd_zoombar_allbutton).performClick();
	}
	
	/**
	 * ���ؿ���������������ͼ��
	 * @param strechToolbar
	 */
	public void LoadStrechToolBar_View(final View strechToolbar)
	{
		//��ʼ��������������
		ArrayList<HashMap<String, Object>> ImageItemList = new ArrayList<HashMap<String, Object>>();  
		HashMap<String, Object> map1 = new HashMap<String, Object>();  
		HashMap<String, Object> map2 = new HashMap<String, Object>();  
		map2.put("Image", R.drawable.v1_project);	
		map2.put("ItemText", Tools.ToLocale("����"));	
		map2.put("ID", "����_ѡ��");
		ImageItemList.add(map2);
		
		map1.put("Image", R.drawable.v1_layerlist);	 
		map1.put("ItemText",Tools.ToLocale("ͼ��"));		//��ť��ʾ�ı�
		map1.put("ID", "ͼ��");
		ImageItemList.add(map1);  
		
		HashMap<String, Object> map3 = new HashMap<String, Object>();  
		map3.put("Image", R.drawable.m_gps);	
		map3.put("ItemText", "GPS");	 
		map3.put("ID", "����GPS");
		ImageItemList.add(map3);  
		
		
		//������������ImageItem <====> ��̬�����Ԫ�أ�����һһ��Ӧ  
		SimpleAdapter saImageItems = new SimpleAdapter(strechToolbar.getContext(), //ûʲô����  
														ImageItemList,
														R.layout.v1_strechtoolbaritem,
														new String[] {"Image","ItemText"},   
												        new int[] {R.id.ItemImage,R.id.ItemText});  
		//��Ӳ�����ʾ  
		GridView gdView = (GridView)strechToolbar.findViewById(R.id.view_gridview);
		gdView.setAdapter(saImageItems);  

		//���������Ƿ�չ����ر�
		strechToolbar.findViewById(R.id.control_box).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				View vb = strechToolbar.findViewById(R.id.view_box);
				vb.clearAnimation();v.bringToFront();
				
				//����ͼ��
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
		
		//�󶨿�������¼�
		gdView.setOnItemClickListener((OnItemClickListener) new ItemClickListener());
	}
	
	/**
	 * ���ض�̬��������ϵͳ��
	 * @param strechToolbar
	 */
	public void LoadStrechToolBar_System(final View strechToolbar)
	{
		//��ʼ��������������
		ArrayList<HashMap<String, Object>> ImageItemList = new ArrayList<HashMap<String, Object>>();  
		HashMap<String, Object> map1 = new HashMap<String, Object>();  
		map1.put("Image", R.drawable.m_option);	
		map1.put("ItemText", "ϵͳ����");		//��ť��ʾ�ı�
		map1.put("ID", "����");
		ImageItemList.add(map1);  
		
		HashMap<String, Object> map2 = new HashMap<String, Object>();  
		map2.put("Image", R.drawable.m_statistic);	
		map2.put("ItemText", "�ɼ�ͳ��");	
		map2.put("ID", "�ɼ�ͳ��");
		ImageItemList.add(map2);  
		
		HashMap<String, Object> map3 = new HashMap<String, Object>();  
		map3.put("Image", R.drawable.m_ldlc);	
		map3.put("ItemText", "·�����");	
		map3.put("ID", "·�����");
		ImageItemList.add(map3);  
		
		HashMap<String, Object> map4 = new HashMap<String, Object>();  
		map4.put("Image", R.drawable.m_delete);	
		map4.put("ItemText", "ɾ��ʵ��");	
		map4.put("ID", "ɾ��ʵ��");
		ImageItemList.add(map4);  
		
		HashMap<String, Object> map41 = new HashMap<String, Object>();  
		map41.put("Image", R.drawable.m_undo);	
		map41.put("ItemText", "�ָ�ʵ��");	
		map41.put("ID", "�ָ�ʵ��");
		ImageItemList.add(map41);  
		
		HashMap<String, Object> map5 = new HashMap<String, Object>();  
		map5.put("Image", R.drawable.m_save);	
		map5.put("ItemText", "��������");	
		map5.put("ID", "��������");
		ImageItemList.add(map5);  
		
		HashMap<String, Object> map6 = new HashMap<String, Object>();  
		map6.put("Image", R.drawable.m_search);	
		map6.put("ItemText", "��ѯ����");	
		map6.put("ID", "��ѯ����");
		ImageItemList.add(map6);  
		
		HashMap<String, Object> map7 = new HashMap<String, Object>();  
		map7.put("Image", R.drawable.m_paraset);	
		map7.put("ItemText", "��������");	
		map7.put("ID", "��������");
		ImageItemList.add(map7);  
		
		HashMap<String, Object> map8 = new HashMap<String, Object>();  
		map8.put("Image", R.drawable.m_tolayer);	
		map8.put("ItemText", "�л���ͼ");	
		map8.put("ID", "�л���ͼ");
		ImageItemList.add(map8);  
		
//		HashMap<String, Object> map9 = new HashMap<String, Object>();  
//		map9.put("Image", R.drawable.m_lockscreen);	
//		map9.put("ItemText", "��ֹ��Ļ");	
//		map9.put("ID", "����");
//		ImageItemList.add(map9);  
		
		HashMap<String, Object> map10 = new HashMap<String, Object>();  
		map10.put("Image", R.drawable.aboutsystem);	
		map10.put("ItemText", "����ϵͳ");	
		map10.put("ID", "����ϵͳ");
		ImageItemList.add(map10);  
		
		HashMap<String, Object> map11 = new HashMap<String, Object>();  
		map11.put("Image", R.drawable.m_quit);	
		map11.put("ItemText", "�˳�ϵͳ");	
		map11.put("ID", "�˳�ϵͳ");
		ImageItemList.add(map11);  

		//����������
		SimpleAdapter saImageItems = new SimpleAdapter(strechToolbar.getContext(),
														ImageItemList,
														R.layout.v1_toolbarsubitem,
														new String[] {"Image","ItemText"},   
														new int[] {R.id.ItemImage,R.id.ItemText});  
		//��Ӳ�����ʾ  
		final GridView gdView = (GridView)strechToolbar.findViewById(R.id.view_gridview);
		gdView.setAdapter(saImageItems);  

		//���ƹ������չ��������
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
		
		//��������¼�����
		gdView.setOnItemClickListener((OnItemClickListener) new ItemClickListener());

	}
	
	//��ť�¼�������
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
