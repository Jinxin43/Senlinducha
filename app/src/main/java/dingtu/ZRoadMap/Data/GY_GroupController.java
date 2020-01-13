package dingtu.ZRoadMap.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import android.view.ViewGroup;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;

public class GY_GroupController 
{
	public GY_GroupController()
	{
		
	}
	
	private List<Button> ControlButtonList = new ArrayList<Button>();
	private List<LinearLayout> ControlPannelList = new ArrayList<LinearLayout>();
	
	//绑定控制按钮与控制面板
	private Drawable BeforeBackgroupColor = null;
	private int AfterBackgroupColor = Color.rgb(148, 233, 186);
	public void SetControlButton(int DefaultButtonIndex,List<View> ButtonList,List<View> LinearLayoutList)
	{
		for(int i=0;i<ButtonList.size();i++)
		{
			View v = ButtonList.get(i);v.setTag(i+"");
			LinearLayout l = (LinearLayout)LinearLayoutList.get(i);l.setBackgroundColor(AfterBackgroupColor);
			ControlButtonList.add((Button)v);
			ControlPannelList.add(l);

			BeforeBackgroupColor = ((LinearLayout)v.getParent()).getBackground();
			v.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) 
				{
					ControlButtonClick(v);
				}});
		}
		this.ControlButtonClick(ButtonList.get(DefaultButtonIndex));
	}
	
	private void ControlButtonClick(View v)
	{
		for(View vv:ControlButtonList)
		{
			((LinearLayout)vv.getParent()).setBackgroundDrawable(BeforeBackgroupColor);
		}
		//将控制按钮所在父控件的背景设为指定的颜色
		((LinearLayout)v.getParent()).setBackgroundColor(AfterBackgroupColor);
		
		//将数据面板设为可见
		for(LinearLayout LL :ControlPannelList)
		{
			android.view.ViewGroup.LayoutParams LP = LL.getLayoutParams();LP.height=0;
			LL.setLayoutParams(LP);
		}
		
		int PannelIndex = Integer.valueOf(v.getTag().toString());
		LinearLayout LL = ControlPannelList.get(PannelIndex);
		android.view.ViewGroup.LayoutParams LP = LL.getLayoutParams();LP.height=android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		LL.setLayoutParams(LP);
	}
	
	
}
