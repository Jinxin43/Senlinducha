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

    	//���ñ���
    	_Dialog.SetCaption(Tools.ToLocale("������"));
    	
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText));
    }

    public void ShowDialog()
    {
    	this.LoadMoreTools();
    	_Dialog.show();
    }
    
	/**
	 * ���ظ��๤��
	 */
	private void LoadMoreTools()
	{
	    //��ʼ��������������
	    ArrayList<HashMap<String, Object>> ImageItemList = new ArrayList<HashMap<String, Object>>();  

	        HashMap<String, Object> map1 = new HashMap<String, Object>();  
	        map1.put("Image",R.drawable.m_option);		//���ͼ����Դ��ID  
	        map1.put("ItemText", Tools.ToLocale("ϵͳ����"));		//��ť��ʾ�ı�
	        map1.put("ID", "ϵͳ����");		//��ťΨһId
	        ImageItemList.add(map1);   
	        
	        HashMap<String, Object> map3 = new HashMap<String, Object>();  
	        map3.put("Image", R.drawable.v1_checkcontrol);	
	        map3.put("ItemText", Tools.ToLocale("У������"));	
	        map3.put("ID", "У������");
	        ImageItemList.add(map3); 
	        
//	        HashMap<String, Object> map11 = new HashMap<String, Object>();  
//	        map11.put("Image",R.drawable.v1_paramanage);		//���ͼ����Դ��ID  
//	        map11.put("ItemText", Tools.ToLocale("ת����������"));		//��ť��ʾ�ı�
//	        map11.put("ID", "ת����������");		//��ťΨһId
//	        ImageItemList.add(map11); 
	        
	        HashMap<String, Object> map11 = new HashMap<String, Object>();  
	        map11.put("Image",R.drawable.v1_paramanage);		//���ͼ����Դ��ID  
	        map11.put("ItemText", Tools.ToLocale("�����ϴ�"));		//��ť��ʾ�ı�
	        map11.put("ID", "ת����������");		//��ťΨһId
	        ImageItemList.add(map11); 
	        
	        HashMap<String, Object> map2 = new HashMap<String, Object>();  
	        map2.put("Image", R.drawable.m_statistic);
	        map2.put("ItemText", Tools.ToLocale("�ɼ�ͳ��"));	
	        map2.put("ID", "�ɼ�ͳ��");
	        ImageItemList.add(map2);  
	        
	        HashMap<String, Object> map4 = new HashMap<String, Object>();  
	        map4.put("Image", R.drawable.v1_cat);	
	        map4.put("ItemText", Tools.ToLocale("���ݵ���"));	
	        map4.put("ID", "���ݵ���");
	        ImageItemList.add(map4); 
	    

	        HashMap<String, Object> map41 = new HashMap<String, Object>();  
	        map41.put("Image", R.drawable.v1_datadic);	
	        map41.put("ItemText", "�����ֵ�");	
	        map41.put("ID", "�����ֵ�");
	        ImageItemList.add(map41);

//	        HashMap<String, Object> map43 = new HashMap<String, Object>();  
//	        map43.put("Image", R.drawable.m_save);	
//	        map43.put("ItemText", Tools.ToLocale("��������"));	
//	        map43.put("ID", "��������");
//	        ImageItemList.add(map43); 
	        
	        HashMap<String, Object> map43 = new HashMap<String, Object>();  
	        map43.put("Image", R.drawable.m_save);	
	        map43.put("ItemText", Tools.ToLocale("��������"));	
	        map43.put("ID", "��������");
	        ImageItemList.add(map43); 
	        
	        HashMap<String, Object> map8 = new HashMap<String, Object>();  
	        map8.put("Image", R.drawable.icon_update);	
	        map8.put("ItemText", Tools.ToLocale("ϵͳ����"));	
	        map8.put("ID", "ϵͳ����");
	        ImageItemList.add(map8);
	        
//	        HashMap<String, Object> map7 = new HashMap<String, Object>();  
//	        map7.put("Image", R.drawable.v1_poly);	
//	        map7.put("ItemText", Tools.ToLocale("�����"));	
//	        map7.put("ID", "�����");
//	        ImageItemList.add(map7);
	        
	        HashMap<String, Object> map7 = new HashMap<String, Object>();  
	        map7.put("Image", R.drawable.m_option);	
	        map7.put("ItemText", Tools.ToLocale("��������"));	
	        map7.put("ID", "��������");
	        ImageItemList.add(map7);
	        
	        HashMap<String, Object> map5 = new HashMap<String, Object>();  
	        map5.put("Image", R.drawable.v1_about);	
	        map5.put("ItemText", Tools.ToLocale("����ϵͳ"));	
	        map5.put("ID", "����ϵͳ");
	        ImageItemList.add(map5);

	        HashMap<String, Object> map6 = new HashMap<String, Object>();  
	        map6.put("Image", R.drawable.m_quit);	
	        map6.put("ItemText", Tools.ToLocale("�˳�ϵͳ"));	
	        map6.put("ID", "�˳�ϵͳ");
	        ImageItemList.add(map6);

	      //������������ImageItem <====> ��̬�����Ԫ�أ�����һһ��Ӧ  
	      SimpleAdapter saImageItems = new SimpleAdapter(_Dialog.getContext(), //ûʲô����  
	    		  										ImageItemList,//������Դ   
	    		  										R.layout.v1_toolbarsubitem,
	    		  										new String[] {"Image","ItemText"},   
	    		  										new int[] {R.id.ItemImage,R.id.ItemText});  
	      //��Ӳ�����ʾ  
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
