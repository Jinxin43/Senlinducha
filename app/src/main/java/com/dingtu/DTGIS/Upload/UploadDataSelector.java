package com.dingtu.DTGIS.Upload;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_FormTemplate;

public class UploadDataSelector {
	
	private v1_FormTemplate dialogView = null;
	
	public UploadDataSelector()
	{
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	dialogView.SetOtherView(R.layout.uploaddataselector);
    	dialogView.ReSetSize(0.65f,0.9f);
	}
	
	
	 public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
		 dialogView.setOnShowListener(new OnShowListener(){
				@Override
				public void onShow(DialogInterface dialog) 
				{
					
				}
		});
		dialogView.show();
    }
}
