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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class v1_myListViewAdpterForCheckBox extends BaseAdapter {

	private List<HashMap<String,Object>> m_DataList = null;
	private int m_LayoutId = 0;
	private String[] m_ObjField ;
	private int[] m_ViewId;
	public v1_myListViewAdpterForCheckBox(Context context,List<HashMap<String,Object>> list,int layoutid,String[] objField,int[] viewid)
	{
		if (this.mInflater==null)this.mInflater = LayoutInflater.from(context);
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

	private LayoutInflater mInflater = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if (convertView==null)convertView = mInflater.inflate(this.m_LayoutId, null); 
		CheckBox cbBox = (CheckBox)convertView.findViewById(R.id.checkBox1);
		HashMap<String,Object> obj = (HashMap<String,Object>)this.getItem(position);
		cbBox.setText(obj.get(this.m_ObjField[0]).toString()); 
		final RadioGroup radiogroup = (RadioGroup)convertView.findViewById(R.id.radiogroup);
		cbBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
			{
				radiogroup.findViewById(R.id.RadioBox1).setEnabled(isChecked);
				radiogroup.findViewById(R.id.RadioBox2).setEnabled(isChecked);
				if (!isChecked){radiogroup.check(-1);}
				else {radiogroup.check(R.id.RadioBox1);}

			}});
		return convertView; 
	}

}
