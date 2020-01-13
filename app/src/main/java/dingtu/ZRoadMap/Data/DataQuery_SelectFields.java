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
		dialogView.SetCaption("字段选择");
		
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+","+"确定"+"  ,确定", pCallback);
		isSelected = selected;
		mAdapter = new DataQuery_SelectFieldsAdapter(dialogView.getContext(),fieldList,isSelected,R.layout.dataquery_selectfields_item,
				new String[] {"tv_value","cb_select"},
				new int[] { R.id.cb_select, R.id.tv_value});
		final List<String> mFieldList = fieldList;
		(((ListView)dialogView.findViewById(R.id.lvList))).setAdapter(mAdapter);
		
//		//选择所有复选框
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
	
	
	
	//按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
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
    				Tools.ShowMessageBox("查询结果最多可以显示30个字段，您选择了"+i+"个字段。");
    			}
    			else
    			{
    				if(mCallback != null)
    	    		{
    	    			mCallback.OnClick("字段选择", adapter.getSelectResult());
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
    	//此处这样做的目的是为了计算控件的尺寸
		dialogView.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				
				
			}}
    	);
		dialogView.show();
    }
}
