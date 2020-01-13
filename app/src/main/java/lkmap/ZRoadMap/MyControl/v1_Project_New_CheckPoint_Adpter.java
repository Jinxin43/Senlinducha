package lkmap.ZRoadMap.MyControl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class v1_Project_New_CheckPoint_Adpter extends BaseAdapter 
{
	//��ǰѡ�е���Ŀ����
	private int m_SelectItemIndex = -1;
	public void SetSelectItemIndex(int idx){this.m_SelectItemIndex = idx;}
	
	private List<HashMap<String,Object>> m_DataList = null;
	private int m_LayoutId = 0;
	private String[] m_ObjField ;
	private int[] m_ViewId;
	public v1_Project_New_CheckPoint_Adpter(Context context,List<HashMap<String,Object>> list,int layoutid,String[] objField,int[] viewid)
	{
		if (this.mInflater==null)this.mInflater = LayoutInflater.from(context);
		this.m_DataList = list;
		this.m_LayoutId = layoutid;
		this.m_ObjField = objField;
		this.m_ViewId = viewid;
	}
	

	//�ص�
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
			//��ʾ������
			View v = convertView.findViewById(this.m_ViewId[i]);

		    //�������ֵ
	    	String VType = v.getClass().getName();
	    	if (VType.equals("android.widget.TextView"))
	    	{
			    TextView tv = (TextView) v;
				if(obj.get(this.m_ObjField[i])!=null) {
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
		}
		
		//ѡ����Ŀ��ͻ����ʾ
		if (position==this.m_SelectItemIndex)
		{
			convertView.setSelected(true);
			convertView.setPressed(true);
			convertView.setBackgroundColor(Color.BLUE);
		} else convertView.setBackgroundColor(Color.TRANSPARENT);
		return convertView; 
	}
	
	//����CheckBox�����¼�ֵ
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