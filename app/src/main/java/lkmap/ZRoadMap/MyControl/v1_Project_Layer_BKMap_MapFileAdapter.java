package lkmap.ZRoadMap.MyControl;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import dingtu.ZRoadMap.Data.ICallback;

public class v1_Project_Layer_BKMap_MapFileAdapter extends BaseAdapter 
{
	//当前选中的项目索引
	private int m_SelectItemIndex = -1;
	public void SetSelectItemIndex(int idx){this.m_SelectItemIndex = idx;}
	
	private List<HashMap<String,Object>> m_DataList = null;
	private int m_LayoutId = 0;
	private String[] m_ObjField ;
	private int[] m_ViewId;
	public v1_Project_Layer_BKMap_MapFileAdapter(Context context,List<HashMap<String,Object>> list,int layoutid,String[] objField,int[] viewid)
	{
		if (this.mInflater==null)this.mInflater = LayoutInflater.from(context);
		this.m_DataList = list;
		this.m_LayoutId = layoutid;
		this.m_ObjField = objField;
		this.m_ViewId = viewid;
	}
	

	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback=cb;}

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

	private LayoutInflater mInflater = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if (convertView==null)
		{
			convertView = mInflater.inflate(this.m_LayoutId, null); 
		}
		    
		HashMap<String,Object> obj = (HashMap<String,Object>)this.getItem(position);
		for(int i=0;i<this.m_ViewId.length;i++)
		{
			//显示可用列
			View v = convertView.findViewById(this.m_ViewId[i]);
			v.setTag(obj);
		    //分情况赋值
	    	String VType = v.getClass().getName();
	    	if (VType.equals("android.widget.TextView"))
	    	{
			    TextView tv = (TextView) v;
			    if(obj.get(this.m_ObjField[i]) != null)
			    {
			    	tv.setText(obj.get(this.m_ObjField[i]).toString()); 
			    }
			    
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
							UpdateCheckBoxDataListValue(ccbb,ccbb.getTag().toString());
						}});
		    		cb.setChecked(Boolean.parseBoolean(obj.get(this.m_ObjField[i]).toString()));
	    	}
	    	if (VType.equals("android.widget.ImageButton"))
	    	{
	    		v.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if (m_Callback!=null)m_Callback.OnClick("ButtonClick", v.getTag());
					}});
	    	}
		}
		
		//选中项目的突出显示
		if (position==this.m_SelectItemIndex)
		{
			convertView.setSelected(true);
			convertView.setPressed(true);
			convertView.setBackgroundColor(Color.BLUE);
		} else convertView.setBackgroundColor(Color.TRANSPARENT);
		return convertView; 
	}
	
	//设置CheckBox单击事件值
	private boolean UpdateCheckBoxDataListValue(CheckBox cb,String posInfo)
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
