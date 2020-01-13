package com.dingtu.DTGIS.TuiGeng;

import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.Data.ICallback;

public class TuiGengDataZLSZSelectAdapter extends BaseAdapter 
{
	private List<HashMap<String,Object>> m_DataList = null;
	private int m_LayoutId = 0;
	private String[] m_ObjField ;
	private int[] m_ViewId;
	private LayoutInflater mInflater = null;
	private boolean[] checks;
   	
	public TuiGengDataZLSZSelectAdapter(Context context,List<HashMap<String,Object>> list,boolean[] isSelect,int layoutid,String[] objField,int[] viewid)
	{
		if (this.mInflater==null)
		{
			this.mInflater = LayoutInflater.from(context);
		}
		
		m_DataList = list;
		this.m_LayoutId = layoutid;
		this.m_ObjField = objField;
		this.m_ViewId = viewid;
		checks = isSelect;  
	}

	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb)
	{
		this.m_Callback=cb;
	}
		
	@Override
	public int getCount() {
		return this.m_DataList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.m_DataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
//		Log.d("position", position+"");
//		ViewHolder holder;  
//		HashMap<String,Object> obj = (HashMap<String,Object>)this.getItem(position);
//		if (convertView==null)
//		{
//			 	convertView = mInflater.inflate(this.m_LayoutId, null); 
//			    holder = new ViewHolder();  
//	            holder.cb = (CheckBox) convertView.findViewById(R.id.cb_select);
//	            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
//	            holder.tvCode = (TextView) convertView.findViewById(R.id.tv_code);
//	            
//	            convertView.setTag(holder);
//		}
//		else
//		{
//			holder = (ViewHolder)convertView.getTag();
//			
//		}
//		
//		holder.cb.setChecked(Boolean.getBoolean(((HashMap<String,Object>)m_DataList.get(position)).get("isCheck")+""));
//		holder.tvName.setText(obj.get("Name").toString());
//        holder.tvCode.setText(obj.get("Code").toString());
//        holder.cb.setTag(obj.get("Name"));
//		
//		  holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//	          @Override
//	          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//	        	  UpdateCheckBoxDataListValue(buttonView,position);
//	          }
//	      });
		  
		
		if (convertView==null)
		{
			 	convertView = mInflater.inflate(this.m_LayoutId, null); 
			   
		}
		HashMap<String,Object> obj = (HashMap<String,Object>)this.getItem(position);
		for(int i=0;i<this.m_ViewId.length;i++)
		{
			//显示可用列
			View v = convertView.findViewById(this.m_ViewId[i]);

		    //分情况赋值
	    	String VType = v.getClass().getName();
	    	
	    	if (VType.equals("android.widget.TextView"))
	    	{
			    TextView tv = (TextView) v;
			    tv.setText(obj.get("Code").toString()); 
	    	}
	    	if (VType.equals("android.widget.LinearLayout"))
	    	{
	    		View subView1 = v.findViewById(R.id.cb_select);
	    		View subView2 = v.findViewById(R.id.tv_name);
	    		if(subView2 != null)
	    		{
		    		String value =obj.get("Name").toString();
		    		TextView tv_Name =(TextView)subView2;
		    		tv_Name.setText(value);
		    		if (subView1!=null)
		    		{
		    			CheckBox cb = (CheckBox)subView1;
		    			cb.setTag(value);
		    			final int p = position;
		    			cb.setOnCheckedChangeListener(null);
		    			cb.setChecked(checks[p]);
		    			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
	    				{
			    			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			    			{
			    				checks[p] = isChecked;
			    				String index = p+"";
			    				UpdateCheckBoxDataListValue((CheckBox)buttonView,isChecked,index);
			    			}
	    				});
		    		}
	    		}
	    	}
		}
	    	
	    	return convertView; 
	}
		
	 class ViewHolder {  
		  
	        TextView tvName; 
	        TextView tvCode;
	        CheckBox cb;  
	    }
	 
	
	    
	private boolean UpdateCheckBoxDataListValue(CompoundButton cb,boolean isChecked,String position)
	{
        HashMap<String,Object> obj = new HashMap<String,Object>();
        //Integer idx = Integer.getInteger(position);
 		obj.put("isAdd", isChecked);
 		obj.put("indexN", position);
 		if(m_Callback != null)
 		{
 			m_Callback.OnClick("选择树种", obj);
 		
 		}
		return true;
	}

}
