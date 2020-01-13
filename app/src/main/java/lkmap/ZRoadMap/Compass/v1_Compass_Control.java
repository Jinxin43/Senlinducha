package lkmap.ZRoadMap.Compass;

import java.util.HashMap;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import dingtu.ZRoadMap.PubVar;

public class v1_Compass_Control 
{
	private View m_CompassView = null;
	public void SetCompassView(View view)
	{
        //指北针
        HashMap<String,Object> bindCompass = new HashMap<String,Object>();
        bindCompass.put("ID", UUID.randomUUID().toString());
        bindCompass.put("View", view);
        PubVar.m_DoEvent.m_Compass.AddBindCompass(bindCompass);
        this.m_CompassView = view;
	}
	
	public void SetCompassControl(View view)
	{
		view.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
	    		String Tag = arg0.getTag().toString();
				//编辑工具条的收缩与展开
				if (Tag.equals("收起"))
				{
					ShowCompass(false);
					arg0.setTag("展开");
					Button bt = (Button)arg0;
					bt.setText("指北针");
					Drawable bmp = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.compass_show);
					bmp.setBounds(0, 0, bmp.getMinimumWidth(), bmp.getMinimumHeight());
					bt.setCompoundDrawables(null, bmp, null, null); //设置上图标
				}
				
				if (Tag.equals("展开"))
				{
					ShowCompass(true);
					arg0.setTag("收起");
					Button bt = (Button)arg0;
					bt.setText("");
					Drawable bmp = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.compass_hide);
					bmp.setBounds(0, 0, bmp.getMinimumWidth(), bmp.getMinimumHeight());
					bt.setCompoundDrawables(bmp, null, null, null); //设置上图标				
				}
			}});
	}
	
    /**
     * 是否展示编辑工具条
     * @param ifShow
     */
    private void ShowCompass(boolean ifShow)
    {
    	if (this.m_CompassView==null)return;
    	int MarginTop = -9999999;
    	if (ifShow)MarginTop=0;
    	int YFrom = 0,YTo = 1;
    	int intVisible = View.GONE;
    	if (ifShow){intVisible=View.VISIBLE;YFrom = 1;YTo = 0;}
//    	TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,
//				  Animation.RELATIVE_TO_SELF, 0,
//				  Animation.RELATIVE_TO_PARENT,YFrom,
//				  Animation.RELATIVE_TO_SELF,YTo);
//    	mHiddenAction.setDuration(300);
//    	this.m_CompassView.startAnimation(mHiddenAction);
    	//this.m_CompassView.setVisibility(intVisible);
    	this.m_CompassView.setPadding(this.m_CompassView.getPaddingLeft(), MarginTop, 
    								  this.m_CompassView.getPaddingRight(),this.m_CompassView.getPaddingBottom());
    }
}
