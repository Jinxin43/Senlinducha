package com.dingtu.DTGIS.TuiGeng;

import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import dingtu.ZRoadMap.Data.ICallback;

public class TuiGengFenhuAdapter extends BaseAdapter 
{
	private List<HashMap<String,Object>> m_DataList = null;
	private int m_LayoutId = 0;
	private String[] m_ObjField ;
	private int[] m_ViewId;
	private LayoutInflater mInflater = null;
	private int mSelectItemIndex;

	public TuiGengFenhuAdapter(Context context,List<HashMap<String,Object>> list,int layoutid,String[] objField,int[] viewid) 
	{
		this.m_DataList = list;
		this.m_LayoutId = layoutid;
		this.m_ObjField = objField;
		this.m_ViewId = viewid;
		
		// TODO Auto-generated constructor stub
		
		if (this.mInflater==null)
		{
			this.mInflater = LayoutInflater.from(context);
		}
	}
	
	public void SetSelectItemIndex(int index)
	{
		mSelectItemIndex = index;
	}
	
	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb)
	{
		this.m_Callback=cb;
	}
		
	@Override
	public int getCount() 
	{
		return m_DataList.size();
	}

	@Override
	public Object getItem(int position) 
	{
		// TODO Auto-generated method stub		
		return m_DataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
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
	    	
	    	if (VType.equals("android.widget.TextView"))
	    	{
			    TextView tv = (TextView) v;
			    tv.setText(obj.get(this.m_ObjField[i])+""); 
	    	}
	    	if (VType.equals("android.widget.CheckBox"))
	    	{
	    		View subView1 = v.findViewById(R.id.cb_select);
	    		if(subView1 != null)
	    		{
	    			CheckBox cb = (CheckBox)subView1;
	    			cb.setChecked(Boolean.parseBoolean(obj.get("isSelect").toString()));
	    			cb.setTag(position+","+i);
	    			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    				{
		    			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    			{
		    				UpdateCheckBoxDataListValue((CheckBox)buttonView,buttonView.getTag()+"");
		    			}
    				});
	    		}
	    	}
		}
		
		//选中项目的突出显示
		if (position==this.mSelectItemIndex)
		{
			convertView.setSelected(true);
			convertView.setPressed(true);
			convertView.setBackgroundColor(Color.YELLOW);
		} 
		else 
		{
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
	    	
	    	return convertView; 
	}
	
	private boolean UpdateCheckBoxDataListValue(CheckBox cb,String posInfo)
	{
		String[] psoInfo = posInfo.split(",");
		int posIdx = Integer.parseInt(psoInfo[0]);
		int objId = Integer.parseInt(psoInfo[1]);
		HashMap<String,Object> obj = (HashMap<String,Object>)this.getItem(posIdx);
		obj.put(this.m_ObjField[objId], cb.isChecked());
		return true;
	}

}
