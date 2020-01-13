package lkmap.ZRoadMap.MyControl;

import java.util.ArrayList;
import java.util.List;

import com.dingtu.senlinducha.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class v1_EditSpinnerDialogAdpter extends BaseAdapter {

	private Context m_Context = null;
	public v1_EditSpinnerDialogAdpter(Context context)
	{
		this.m_Context  = context;
	}
	
	private List<String> m_DataList = new ArrayList<String>();
	public void SetDataList(List<String> dataList)
	{
		this.m_DataList = dataList;
		this.GetEditTextView().setText(this.m_DataList.get(0).toString()); 
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_DataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.m_DataList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	private EditText m_EditText = null;
	public EditText GetEditTextView()
	{
        if (this.m_EditText==null)
    	{
        	this.m_EditText= new EditText(this.m_Context); 

        	this.m_EditText.setTextColor(Color.BLACK); 
            Resources resource = this.m_Context.getResources();
            Drawable HippoDrawable =resource.getDrawable(R.drawable.edittextstyle);
            this.m_EditText.setBackgroundDrawable(HippoDrawable);
            LayoutParams LP = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.MATCH_PARENT);
            this.m_EditText.setLayoutParams(LP);
    	}
        return this.m_EditText;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
        //this.GetEditTextView().setText(this.m_DataList.get(0).toString()); 
        return this.GetEditTextView(); 
	}

}
