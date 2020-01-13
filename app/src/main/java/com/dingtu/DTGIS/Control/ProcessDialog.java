package com.dingtu.DTGIS.Control;

import com.dingtu.senlinducha.R;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Button_Center;

public class ProcessDialog
{
	private v1_FormTemplate dialogView = null;
	private ProgressBar processbar = null;
	private ICallback callback;
	private String messageTitle = "";
	private int max = 0;
	private int current = 0;
	private TextView messageView = null;
	
	public ProcessDialog(String captionName)
	{
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.d_importprocessbar);
		dialogView.ReSetSize(0.5f, 0.3f,0,0);
		
		dialogView.SetCaption(Tools.ToLocale(captionName));
		
		messageView = (TextView)dialogView.findViewById(R.id.tvprocessinfo);
		processbar = (ProgressBar)dialogView.findViewById(R.id.pbgress2);
		v1_Button_Center btnCancel = (v1_Button_Center)dialogView.findViewById(R.id.btquit);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Tools.ShowYesNoMessage(dialogView.getContext(), "您确定要取消当前任务？", callback);
				
			}
		});
	}
	
	public void setMessage(String message)
	{
		messageTitle = message;
		messageView.setText(messageTitle+current+"/"+max);
	}
	public void setMax(int count)
	{
		processbar.setMax(count);
		max = count;
		messageView.setText(messageTitle+current+"/"+max);
	}
	
	public void setProgress(int currentIndex)
	{
		processbar.setProgress(currentIndex);
		current = currentIndex;
		
		messageView.setText(messageTitle+current+"/"+max);
	}
	
	public void setCancelCallback(ICallback cb)
	{
		callback = cb;
	}
	
	public void  Show() 
	{
		dialogView.show();
	}
	
	public void dismiss() 
	{
		dialogView.dismiss();
	}
}
