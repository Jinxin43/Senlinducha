package com.dingtu.Controls;

import java.text.SimpleDateFormat;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import lkmap.MapControl.Tools;

public class YearPicker extends LinearLayout {

	public YearPicker(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public YearPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.yearpicker, this,true);
		
		findViewById(R.id.yearIncrement).setOnClickListener(new ViewClick());
		findViewById(R.id.yearDecrement).setOnClickListener(new ViewClick());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy"); 
		((TextView)this.findViewById(R.id.numberpicker_input)).setText(simpleDateFormat.format(new java.util.Date()));
	}
	
	public void setYear(String year)
	{
		((TextView)this.findViewById(R.id.numberpicker_input)).setText(year);
	}
	
	public String getYear()
	{
		return String.valueOf(((TextView)findViewById(R.id.numberpicker_input)).getText());
	}
	
	class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String tag = arg0.getTag().toString();
    		if(tag.equals("increment"))
    		{
    			try
    			{
    				String strValue = String.valueOf(((TextView)findViewById(R.id.numberpicker_input)).getText());
    				int value = Integer.parseInt(strValue)+1;
    				((TextView)findViewById(R.id.numberpicker_input)).setText(value+"");
    			}
    			catch(Exception e)
    			{
    				lkmap.Tools.Tools.ShowMessageBox(e.getMessage());
    			}
				
    		}
    		if(tag.equals("decrement"))
    		{
    			try
    			{
    				String strValue = String.valueOf(((TextView)findViewById(R.id.numberpicker_input)).getText());
    				int value = Integer.parseInt(strValue)-1;
    				((TextView)findViewById(R.id.numberpicker_input)).setText(value+"");
    			}
    			catch(Exception e)
    			{
    				lkmap.Tools.Tools.ShowMessageBox(e.getMessage());
    				
    			}
    		}
    	}
    }

}
