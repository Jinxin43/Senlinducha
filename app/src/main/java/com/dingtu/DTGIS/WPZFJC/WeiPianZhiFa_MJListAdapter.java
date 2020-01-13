package com.dingtu.DTGIS.WPZFJC;

import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import dingtu.ZRoadMap.Data.ICallback;

public class WeiPianZhiFa_MJListAdapter extends BaseAdapter 
{

	//当前选中的项目索引
		private int m_SelectItemIndex = -1;
		
		private List<HashMap<String,Object>> m_DataList = null;
		private int m_LayoutId = 0;
		private String[] m_ObjField ;
		private int[] m_ViewId;
		private LayoutInflater mInflater = null;
		
		public WeiPianZhiFa_MJListAdapter(Context context,List<HashMap<String,Object>> list,int layoutid,String[] objField,int[] viewid)
		{
			if (this.mInflater==null)
			{
				this.mInflater = LayoutInflater.from(context);
			}
			this.m_DataList = list;
			this.m_LayoutId = layoutid;
			this.m_ObjField = objField;
			this.m_ViewId = viewid;
		}
		
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.m_DataList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return this.m_DataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		//回调
		private ICallback m_Callback = null;
		public void SetCallback(ICallback cb)
		{
			this.m_Callback=cb;
		}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView==null)
		{
			convertView = mInflater.inflate(this.m_LayoutId, null); 
		}
		    
		final HashMap<String,Object> obj = (HashMap<String,Object>)this.getItem(position);
		for(int i=0;i<this.m_ViewId.length;i++)
		{
			//显示可用列
			View v = convertView.findViewById(this.m_ViewId[i]);

		    //分情况赋值
	    	String VType = v.getClass().getName();
	    	if (VType.equals("android.widget.Button"))
	    	{
	    		v.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if (m_Callback!=null)
						{
							m_Callback.OnClick(v.getTag()+"", obj);
						}
					}});
	    	}
	    	
	    	if (VType.equals("android.widget.CheckBox"))
	    	{
	    		CheckBox cb = (CheckBox)v;
	    		cb.setTag(position+","+i);
	    		cb.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View vv) 
					{
						CheckBox ccbb = (CheckBox) vv;
						updateCheckBoxDataListValue(ccbb,ccbb.getTag().toString());
					}});
	    		cb.setChecked(Boolean.parseBoolean(obj.get(this.m_ObjField[i]).toString()));
	    	}
	    	
	    	
	    	if (VType.equals("android.widget.TextView"))
	    	{
			    TextView tv = (TextView) v;
			    tv.setText(String.valueOf(obj.get(this.m_ObjField[i]))); 
	    	}
	    	
		}
		
		//选中项目的突出显示
		if (position==this.m_SelectItemIndex)
		{
			convertView.setSelected(true);
			convertView.setPressed(true);
			convertView.setBackgroundColor(Color.BLUE);
		} 
		
		return convertView; 
	}
	
	//设置CheckBox单击事件值
	private boolean updateCheckBoxDataListValue(CheckBox cb,String posInfo)
	{
		String[] psoInfo = posInfo.split(",");
		int posIdx = Integer.parseInt(psoInfo[0]);
		int objId = Integer.parseInt(psoInfo[1]);
		HashMap<String,Object> obj = (HashMap<String,Object>)this.getItem(posIdx);
		boolean value = Boolean.parseBoolean(obj.get(this.m_ObjField[objId]).toString());
		obj.put(this.m_ObjField[objId], !value);
		return !value;
	}

}
