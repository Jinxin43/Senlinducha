package lkmap.ZRoadMap.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.UserConfigDB;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_Data_Gps_AveragePoint;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Coordinate;
import lkmap.CoordinateSystem.CoorParamTools;
import lkmap.Tools.Tools;

public class v1_transformation_quickmatchpoint {
	
	private v1_FormTemplate _Dialog = null; 
	
	public v1_transformation_quickmatchpoint()
	{
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_transformation_quickmatchpoint);
    	_Dialog.SetCaption("校准点");
    	_Dialog.ReSetSize(0.4f,-1f);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+" ,确定", pCallback);

    	_Dialog.findViewById(R.id.btAddGPSPoint).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("增加GPS控制点", "");}});

//    	_Dialog.findViewById(R.id.btAddLoadPoint).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {pCallback.OnClick("调取", "");}});
    	
    	//GPS采集点数
    	int GpsPointCount = Integer.parseInt(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount").Value);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.etGPSPoints, GpsPointCount+"");
    	((CheckBox)_Dialog.findViewById(R.id.cbShowCenterCross)).setChecked(PubVar.CenterCrossShow);
    	
    	((CheckBox)_Dialog.findViewById(R.id.cbShowCenterCross)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PubVar.CenterCrossShow = isChecked;
				PubVar.m_MapControl.invalidate();
				
			}
		});
    	
    	if(PubVar.CenterCrossShow)
		{
			DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
			int heightPixel = dm.heightPixels/2;
			int weightPixel = dm.widthPixels/2;
			
			Coordinate centerCoord = PubVar.m_Map.getViewConvert().ScreenToMap(weightPixel,heightPixel);
			if(centerCoord != null)
			{
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_X1, centerCoord.getX()+"");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y1, centerCoord.getY()+"");
			}else
			{
				Tools.ShowMessageBox("无法获取屏幕中心点！");
			}
		}
		else
		{
			Tools.ShowMessageBox("请开始屏幕中心点十字，然后将当前所站位置移动到屏幕中心点！");
		}
    	
	}
	
	//打开工程后的回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		
	//按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("确定"))
			{
				if (m_Callback!=null)
				{
					if (m_MatchPointType.equals("单点校正"))
					{
						final String X1 = Tools.GetTextValueOnID(_Dialog, R.id.et_X2);
						final String Y1 = Tools.GetTextValueOnID(_Dialog, R.id.et_Y2);
						final String X2 = Tools.GetTextValueOnID(_Dialog, R.id.et_X1);
						final String Y2 = Tools.GetTextValueOnID(_Dialog, R.id.et_Y1);
						
						//检查坐标是否符号条件
						if (!(Tools.IsDouble(X1) && Tools.IsDouble(Y1) && Tools.IsDouble(X2) && Tools.IsDouble(Y2))){Tools.ShowMessageBox("坐标格式不正确！");return;}
						
						//判读误差值（目标-源）
						List<Coordinate> matchPointList = new ArrayList<Coordinate>();
						matchPointList.add(new Coordinate(Double.parseDouble(X1),Double.parseDouble(Y1)));
						matchPointList.add(new Coordinate(Double.parseDouble(X2),Double.parseDouble(Y2)));
						HashMap<String,Object> fourParam = CoorParamTools.CalFourPara(matchPointList);
						
						final String dx = Tools.ConvertToDigi(fourParam.get("DX")+"",3);
						final String dy = Tools.ConvertToDigi(fourParam.get("DY")+"",3);
						Tools.ShowYesNoMessage(_Dialog.getContext(),"校准点误差值如下，是否确认转换参数？\nX平移(米)："+dx+"\nY平移(米)："+dy, new ICallback(){
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								if (m_EditCheckPoint==null)
								m_EditCheckPoint = new HashMap<String,Object>();
								
								m_EditCheckPoint.put("DX",dx);
								m_EditCheckPoint.put("DY",dy);
								m_EditCheckPoint.put("R",0);
								m_EditCheckPoint.put("K",1);
								m_EditCheckPoint.put("X1", Tools.ConvertToDouble(X1)+"");
								m_EditCheckPoint.put("Y1", Tools.ConvertToDouble(Y1)+"");
								m_EditCheckPoint.put("X2", Tools.ConvertToDouble(X2)+"");
								m_EditCheckPoint.put("Y2", Tools.ConvertToDouble(Y2)+"");
								m_Callback.OnClick("校准点", m_EditCheckPoint);
								try
								{
									HashMap<String, Object> calcPoints = new HashMap<String, Object>();
									calcPoints.put("X1", X1);
									calcPoints.put("Y1", Y1);
									calcPoints.put("X2", X2);
									calcPoints.put("Y2", Y2);
									UserConfigDB db = new UserConfigDB();
									db.addTransformPoint(calcPoints);
								}
								catch (Exception e) {
									// TODO: handle exception
								}
								_Dialog.dismiss();
							}});
					}
					
					
				}
				
			}
			
			if (Str.equals("增加GPS控制点"))
			{
				if (!lkmap.Tools.Tools.ReadyGPS(true)) return;
	    		v1_Data_Gps_AveragePoint dsap = new v1_Data_Gps_AveragePoint();
	    		dsap.SetGpsPointCount(Integer.parseInt(Tools.GetTextValueOnID(_Dialog, R.id.etGPSPoints)));
	    		dsap.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						Coordinate Coor = (Coordinate)ExtraStr;
						Tools.SetTextViewValueOnID(_Dialog, R.id.et_X2, Coor.getX()+"");
						Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y2, Coor.getY()+"");
					}});
	    		dsap.ShowDialog();
			}
			
			if (Str.equals("调取"))
			{
				if(PubVar.CenterCrossShow)
				{
					DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
					int heightPixel = dm.heightPixels/2;
					int weightPixel = dm.widthPixels/2;
					
					Coordinate centerCoord = PubVar.m_Map.getViewConvert().ScreenToMap(weightPixel,heightPixel);
					if(centerCoord != null)
					{
						Tools.SetTextViewValueOnID(_Dialog, R.id.et_X1, centerCoord.getX()+"");
						Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y1, centerCoord.getY()+"");
					}else
					{
						Tools.ShowMessageBox("无法获取屏幕中心点！");
					}
				}
				else
				{
					Tools.ShowMessageBox("请开始屏幕中心点十字，然后将当前所站位置移动到屏幕中心点！");
				}
				
			}
		}};
		
		//获取点的类型
		private String m_MatchPointType = "无";
		public void SetMatchPointType(String matchPointType)
		{
			this.m_MatchPointType = matchPointType;
		}
		
		//当前可编辑的校准点
		private HashMap<String,Object> m_EditCheckPoint = null;
		public void SetEditCheckPoint(HashMap<String,Object> checkPoint)
		{
			this.m_EditCheckPoint = checkPoint;
		}
		
		//是否可以使用GPS获取坐标点
		private boolean m_UseGPS = true;
		public void SetUseGPS(boolean useGPS)
		{
			this.m_UseGPS = useGPS;
			_Dialog.findViewById(R.id.btAddGPSPoint).setEnabled(this.m_UseGPS);
		}
		
		public void ShowDialog()
	    {
	    	//此处这样做的目的是为了计算控件的尺寸
	    	_Dialog.setOnShowListener(new OnShowListener(){
				@Override
				public void onShow(DialogInterface dialog) 
				{
					if (m_EditCheckPoint!=null)
						
					{
						if (m_MatchPointType.equals("计算四参数"))
						{
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_X1, m_EditCheckPoint.get("X1")+"");
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y1, m_EditCheckPoint.get("Y1")+"");
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_X2, m_EditCheckPoint.get("X2")+"");
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y2, m_EditCheckPoint.get("Y2")+"");
							Tools.SetTextViewValueOnID(_Dialog, R.id.tvCaption, "校准点信息【点号="+m_EditCheckPoint.get("DH")+"】");
						}
					}}}
	    	);
	    	_Dialog.show();
	    }


}
