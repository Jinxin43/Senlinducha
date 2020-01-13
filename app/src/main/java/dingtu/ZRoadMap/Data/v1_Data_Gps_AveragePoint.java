package dingtu.ZRoadMap.Data;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Coordinate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;

public class v1_Data_Gps_AveragePoint
{
	private v1_FormTemplate _Dialog = null; 
    public v1_Data_Gps_AveragePoint()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_gps_averagepoint);
    	_Dialog.SetCaption("平均值采点");
    	_Dialog.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				_MyAveragePoint.Cancel();
			}});
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_gpsaveragepoint+",计算  ,计算", pCallback);

    	_Dialog.findViewById(R.id.btreset).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				pCallback.OnClick("重新开始采集", null);
			}});
    	this._MyAveragePoint.SetCallback(pCallback);
    	
    	//初始化控件
    	v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_setpointcount);
    	esd1.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
    	esd1.SetSelectItemList(Tools.StrArrayToList(new String[]{"3","5","10","15","20","30","60"}));
    	esd1.getEditTextView().setEnabled(true);
    	
    }
    
    //采集平均点器
    private v1_CGps_AveragePoint _MyAveragePoint = new v1_CGps_AveragePoint();
    
    //设置需要获取的点数
    public void SetGpsPointCount(int pointCount)
    {
		v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_setpointcount);
		esd1.setText(pointCount+"");
    }
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("重新开始采集"))
			{
				v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_setpointcount);
				_MyAveragePoint.Start(Integer.parseInt(esd1.getText()));
			}
	    	if (Str.equals("采集状态"))
	    	{
	    		String[] gpsStatus = ExtraStr.toString().split(",");
	    		Tools.SetTextViewValueOnID(_Dialog, R.id.et_getpointcount, gpsStatus[0]);
	    		Tools.SetTextViewValueOnID(_Dialog, R.id.et_time, "【正在采集GPS点位...】耗时："+gpsStatus[1]+" 秒");
	    	}
	    	if (Str.equals("采集结果"))
	    	{
	    		CheckGPSPoint((Coordinate)ExtraStr);
	    	}	    	
	    	if (Str.equals("计算"))
	    	{
	    		_MyAveragePoint.CalGpsPoint();
	    	}
		}};


	//检查获取的GPS坐标是否有效
	private void CheckGPSPoint(final Coordinate gpsPoint)
	{
		Tools.SetTextViewValueOnID(_Dialog, R.id.et_time, "【采集GPS点位完成】");
		
		if (gpsPoint==null)
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "没有获取有效的GPS坐标！");return;
		}
		
		Tools.ShowYesNoMessage(_Dialog.getContext(), "已获取坐标信息如下：\nX="+gpsPoint.getX()+"\nY="+gpsPoint.getY()+"\n\n是否采用此GPS坐标？", new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if (Str.equals("YES"))
				{
					if (m_Callback!=null)m_Callback.OnClick("OK", gpsPoint);
					_Dialog.dismiss();
				}
			}});
	}
	
	//采点后的回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	

    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{pCallback.OnClick("重新开始采集", null);}}
    	);
    	_Dialog.show();
    }
}
