package dingtu.ZRoadMap.Data;

import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class DataQuery_SelectFieldsAdapter extends BaseAdapter {

	private List<String> m_DataList = null;
	private int m_LayoutId = 0;
	private String[] m_ObjField ;
	private int[] m_ViewId;
	private LayoutInflater mInflater = null;
	private boolean[] checks;
	
	public DataQuery_SelectFieldsAdapter(Context context,List<String> list,boolean[] isSelect,int layoutid,String[] objField,int[] viewid)
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
	
	@Override
	public int getCount() {
		return this.m_DataList.size();
	}

	@Override
	public String getItem(int position) {
		return this.m_DataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView==null)
		{
			 	convertView = mInflater.inflate(this.m_LayoutId, null); 
			   
		}
		String obj = this.getItem(position);
		for(int i=0;i<this.m_ViewId.length;i++)
		{
			//显示可用列
			View v = convertView.findViewById(this.m_ViewId[i]);

		    //分情况赋值
	    	String VType = v.getClass().getName();
	    	
	    	if (VType.equals("android.widget.TextView"))
	    	{
			    TextView tv = (TextView) v;
			    tv.setText(obj); 
	    	}
	    	if (VType.equals("android.widget.CheckBox"))
	    	{
	    		CheckBox cb = (CheckBox)v;
	    		if (cb!=null)
	    		{
	    			cb.setTag(obj);
	    			final int p = position;
	    			cb.setOnCheckedChangeListener(null);
	    			cb.setChecked(checks[p]);
	    			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    				{
		    			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    			{
		    				UpdateCheckBoxDataListValue(p,isChecked);
		    			}
    				});
	    		}
	    	}
		}
	    	
	    	return convertView; 
	}
	
	 class ViewHolder 
	 {  
	    TextView tvName;
	    CheckBox cb;  
	 }
	 
	private boolean UpdateCheckBoxDataListValue(int position,boolean isChecked)
	{
		checks[position] = isChecked;
		return true;
	}
	 
	 public boolean[] getSelectResult()
	 {
		 return checks;
	 }
	 

}
