package lkmap.ZRoadMap.MyControl;

import java.util.ArrayList;
import java.util.List;

import lkmap.ZRoadMap.Config.v1_SymbolObject;
import com.dingtu.senlinducha.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class v1_ImageSpinnerDialogAdpter extends BaseAdapter {

	private Context m_Context = null;
	public v1_ImageSpinnerDialogAdpter(Context context)
	{
		this.m_Context  = context;
	}
	
	private List<v1_SymbolObject> m_DataList = new ArrayList<v1_SymbolObject>();
	public void SetDataList(List<v1_SymbolObject> dataList)
	{
		this.m_DataList = dataList;
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

	private View m_View = null;
	private ImageView m_ImageView = null;
	private TextView m_TextView = null;
	
	public View GetView()
	{
        if (this.m_View==null)
    	{
        	//ImageViewÓëTextViewÈÝÆ÷
        	LinearLayout LL = new LinearLayout(this.m_Context); 
        	LL.setOrientation(LinearLayout.HORIZONTAL);
        	LL.setGravity(Gravity.CENTER);
        	LinearLayout.LayoutParams LPL = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        	LL.setLayoutParams(LPL);
        	
        	//Í¼Æ¬¿ò
        	this.m_ImageView = new ImageView(this.m_Context); 
        	LL.addView(this.m_ImageView);
            Resources resource = this.m_Context.getResources();
            Drawable HippoDrawable =resource.getDrawable(R.drawable.imageview_flat);
            this.m_ImageView.setBackgroundDrawable(HippoDrawable);
            
            this.m_ImageView.setScaleType(ScaleType.CENTER);
            LayoutParams LP = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
            this.m_ImageView.setLayoutParams(LP);
            
            
            //ÎÄ±¾¿ò
            this.m_TextView = new TextView(this.m_Context); 
            LayoutParams LPT = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            this.m_TextView.setVisibility(View.GONE);
            this.m_TextView.setLayoutParams(LPT);
            this.m_TextView.setTextColor(Color.BLACK);
            this.m_TextView.setGravity(Gravity.CENTER);
            LL.addView(this.m_TextView);
            this.m_View = LL;
    	}
        return this.m_View;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		this.GetView();
		if (this.m_DataList.get(position)==null)
		{
			this.m_ImageView.setImageBitmap(null);
			this.m_TextView.setText("");
		} else
		{
			v1_SymbolObject SO = (v1_SymbolObject)this.m_DataList.get(position);
			this.m_ImageView.setImageBitmap(SO.SymbolFigure);
			this.m_TextView.setText(SO.SymbolName);
		}
        return this.GetView(); 
	}

}
