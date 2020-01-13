package lkmap.Report;

import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Report.v1_ReportTemplate.ReportHeader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class v1_myListViewAdpter extends BaseAdapter 
{
	private List<HashMap<String,Object>> m_DataList = null;
	private int m_LayoutId = 0;
	private String[] m_ObjField ;
	private int[] m_ViewId;
	private ReportHeader m_ReportHeader = null;
	public v1_myListViewAdpter(Context context,List<HashMap<String,Object>> list,int layoutid,String[] objField,int[] viewid,ReportHeader reportHeader)
	{
		if (this.mInflater==null)this.mInflater = LayoutInflater.from(context);
		this.m_DataList = list;
		this.m_LayoutId = layoutid;
		this.m_ObjField = objField;
		this.m_ViewId = viewid;
		this.m_ReportHeader = reportHeader;
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

	private LayoutInflater mInflater = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		boolean adjustViewWidth=false;
		if (convertView==null)
		{
			convertView = mInflater.inflate(this.m_LayoutId, null); 
			adjustViewWidth = true;
		}
		    
		HashMap<String,Object> obj = (HashMap<String,Object>)this.getItem(position);
		for(int i=0;i<this.m_ViewId.length;i++)
		{
		    TextView tv = (TextView) convertView.findViewById(this.m_ViewId[i]); 
		    
		    if (adjustViewWidth)
		    {
		    	if (this.m_ReportHeader!=null)
		    	{
		    		ViewGroup.LayoutParams Params = (ViewGroup.LayoutParams)tv.getLayoutParams();
					Params.width = this.m_ReportHeader.m_HeaderWidthList.get(i);
					if (this.m_ReportHeader.m_ItemsRelativeHeightList!=null) 
					{
						Params.height = this.m_ReportHeader.m_ItemsRelativeHeightList.get(position);
					}
					tv.setLayoutParams(Params);
		    	}
		    }
		    
		    tv.setText(obj.get(this.m_ObjField[i]).toString()); 
		}
		return convertView; 
	}

}
