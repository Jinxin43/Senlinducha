package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.TuiGeng.TuiGengDataZLSZSelectAdapter;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_MapFileAdapter;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class DataQuery_SelectFields 
{
	private v1_Layer mLayer;
	private v1_FormTemplate dialogView = null;
	private DataQuery_SelectFieldsAdapter mAdapter;
	private boolean[] isSelected;
	
	public DataQuery_SelectFields(List<String> fieldList,boolean[] selected)
	{
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.dataquery_selectfileds);
		dialogView.ReSetSize(0.6f,0.96f);
		dialogView.SetCaption("�ֶ�ѡ��");
		
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+","+"ȷ��"+"  ,ȷ��", pCallback);
		isSelected = selected;
		mAdapter = new DataQuery_SelectFieldsAdapter(dialogView.getContext(),fieldList,isSelected,R.layout.dataquery_selectfields_item,
				new String[] {"tv_value","cb_select"},
				new int[] { R.id.cb_select, R.id.tv_value});
		final List<String> mFieldList = fieldList;
		(((ListView)dialogView.findViewById(R.id.lvList))).setAdapter(mAdapter);
		
//		//ѡ�����и�ѡ��
//    	((CheckBox)dialogView.findViewById(R.id.cb_selectall)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
//			@Override
//			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//				if (isSelected==null) return;
//				for(int i=0;i<isSelected.length;i++)
//				{
//					isSelected[i] = arg1;
//				}
//				mAdapter = new DataQuery_SelectFieldsAdapter(dialogView.getContext(),mFieldList,isSelected,R.layout.dataquery_selectfields_item,
//						new String[] {"tv_value","cb_select"},
//						new int[] { R.id.cb_select, R.id.tv_value});
//				(((ListView)dialogView.findViewById(R.id.lvList))).setAdapter(mAdapter);
//			}});
	}
	
	
	
	//��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		DataQuery_SelectFieldsAdapter adapter = (DataQuery_SelectFieldsAdapter)((ListView)dialogView.findViewById(R.id.lvList)).getAdapter();
	    		isSelected = adapter.getSelectResult();
	    		int i = 0;
	    		for(boolean b:isSelected)
	    		{
	    			if(b)
	    			{
	    				i++;
	    			}
	    			
	    		}
	    		
	    		if(i>29)
    			{
    				Tools.ShowMessageBox("��ѯ�����������ʾ30���ֶΣ���ѡ����"+i+"���ֶΡ�");
    			}
    			else
    			{
    				if(mCallback != null)
    	    		{
    	    			mCallback.OnClick("�ֶ�ѡ��", adapter.getSelectResult());
    	    			dialogView.dismiss();
    	    		}
    			}
	    	}
	}};
	
	
	private ICallback mCallback = null;
	public void SetCallback(ICallback cb)
	{
		mCallback = cb;
	}
	
	public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
		dialogView.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				
				
			}}
    	);
		dialogView.show();
    }
}
