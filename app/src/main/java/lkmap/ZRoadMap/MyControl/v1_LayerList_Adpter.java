package lkmap.ZRoadMap.MyControl;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Sampler.Value;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import dingtu.ZRoadMap.Data.ICallback;

public class v1_LayerList_Adpter extends BaseAdapter 
{
	//当前选中的项目索引
	private int m_SelectItemIndex = -1;
	public void SetSelectItemIndex(int idx){this.m_SelectItemIndex = idx;}
	
	private List<HashMap<String,Object>> m_DataList = null;
	private int m_LayoutId = 0;
	private String[] m_ObjField ;
	private int[] m_ViewId;
	public v1_LayerList_Adpter(Context context,List<HashMap<String,Object>> list,int layoutid,String[] objField,int[] viewid)
	{
		if (this.mInflater==null)this.mInflater = LayoutInflater.from(context);
		this.m_DataList = list;
		this.m_LayoutId = layoutid;
		this.m_ObjField = objField;
		this.m_ViewId = viewid;
	}
	
	public List<HashMap<String,Object>> GetDataList()
	{
		return this.m_DataList;
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
			    tv.setText(obj.get(this.m_ObjField[i]).toString()); 
	    	}
	    	if (VType.equals("android.widget.CheckBox"))
	    	{
	    		CheckBox cb = (CheckBox)v;
	    		cb.setTag(position+","+i);
	    		if(position==this.m_SelectItemIndex)
	    		{
	    			cb.setChecked(true);
	    			HashMap<String,Object> objvalue = (HashMap<String,Object>)this.getItem(position);
	    			objvalue.put(this.m_ObjField[i], true);
	    		}
	    		else 
	    		{
	    			cb.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View vv) 
						{
							CheckBox ccbb = (CheckBox) vv;
							UpdateCheckBoxDataListValue(ccbb,ccbb.getTag().toString());
						}});
		    		cb.setChecked(Boolean.parseBoolean(obj.get(this.m_ObjField[i]).toString()));
				}
	    	}
	    	if (VType.equals("android.widget.ToggleButton"))
	    	{
	    		ToggleButton cb = (ToggleButton)v;
	    		cb.setTag(position+","+i);
	    		cb.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View vv) 
					{
						ToggleButton ccbb = (ToggleButton)vv;
						ToggleButtonClick(ccbb,ccbb.getTag().toString());
					}});
	    		cb.setChecked(Boolean.parseBoolean(obj.get(this.m_ObjField[i]).toString()));
	    	}
	    	if (VType.equals("android.widget.ImageButton"))
	    	{
	    		ImageButton iv = (ImageButton)v;
	    		if (i<this.m_ObjField.length)
	    		{
	    			iv.setImageBitmap((Bitmap)obj.get(this.m_ObjField[i]));
	    		}
	    	}
	    	if (VType.equals("android.widget.ImageView"))
	    	{
	    		ImageView iv = (ImageView)v;
	    		if (i<this.m_ObjField.length)
	    		{
	    			if (obj.get(this.m_ObjField[i])!=null)
	    				iv.setImageBitmap((Bitmap)obj.get(this.m_ObjField[i]));
	    		}
	    	}
	    	
	    	if (VType.equals("android.widget.Button") || VType.equals("android.widget.ImageButton"))
	    	{
	    		v.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if (m_Callback!=null)m_Callback.OnClick(v.getTag()+"", obj);
					}});
	    	}

		}
		
		
		//选中项目的突出显示
		if (position==this.m_SelectItemIndex)
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
	
	private boolean ToggleButtonClick(ToggleButton tb,String posInfo) 
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
