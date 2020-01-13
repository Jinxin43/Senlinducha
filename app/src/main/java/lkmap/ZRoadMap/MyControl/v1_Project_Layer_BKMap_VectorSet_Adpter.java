package lkmap.ZRoadMap.MyControl;

import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.ZRoadMap.MyControl.v1_HeaderListViewTemplate.ReportHeader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import dingtu.ZRoadMap.Data.ICallback;
import android.widget.TextView;
import android.widget.ToggleButton;

public class v1_Project_Layer_BKMap_VectorSet_Adpter extends BaseAdapter 
{
	//当前选中的项目索引
	private int m_SelectItemIndex = -1;
	public void SetSelectItemIndex(int idx){this.m_SelectItemIndex = idx;}
	private List<HashMap<String,Object>> m_DataList = null;
	private int m_LayoutId = 0;
	private String[] m_ObjField ;
	private int[] m_ViewId;
	public v1_Project_Layer_BKMap_VectorSet_Adpter(Context context,List<HashMap<String,Object>> list,int layoutid,String[] objField,int[] viewid)
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
						if (m_Callback!=null)m_Callback.OnClick(v.getTag()+"", obj);
					}});
	    	}
	    	
	    	if (VType.equals("android.widget.TextView"))
	    	{
			    TextView tv = (TextView) v;
			    tv.setText(obj.get(this.m_ObjField[i]).toString()); 
	    	}
	    	if (VType.equals("android.widget.LinearLayout"))
	    	{
	    		View subView1 = v.findViewById(R.id.tb_visible);
	    		View subView2 = v.findViewById(R.id.rp_seekBar);
	    		if (subView1!=null)
	    		{
	    			ToggleButton cb = (ToggleButton)subView1;
		    		cb.setTag(position+","+i);
		    		cb.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View vv) 
						{
							ToggleButton ccbb = (ToggleButton) vv;
							clickToggleButton(ccbb,ccbb.getTag().toString());
						}});
		    		cb.setChecked(Boolean.parseBoolean(obj.get(this.m_ObjField[i]).toString()));
	    		}
	    		if (subView2!=null)
	    		{
		    		SeekBar sb = (SeekBar)subView2;
		    		sb.setMax(255);
		    		sb.setTag(position+","+i);
		    		sb.setProgress(Integer.parseInt(obj.get(this.m_ObjField[i]).toString()));
		    		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							// TODO Auto-generated method stub
							UpdateSeekBarDataListValue(seekBar,seekBar.getTag().toString(),progress);
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							
						}});
	    		}

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

	//设置Seekbak值改变事件
	private void UpdateSeekBarDataListValue(SeekBar sb,String posInfo,int value)
	{
		String[] psoInfo = posInfo.split(",");
		int posIdx = Integer.parseInt(psoInfo[0]);
		int objId = Integer.parseInt(psoInfo[1]);
		HashMap<String,Object> obj = (HashMap<String,Object>)this.getItem(posIdx);
		obj.put(this.m_ObjField[objId], value);
	}
	
	private boolean clickToggleButton(ToggleButton tb,String posInfo)
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
