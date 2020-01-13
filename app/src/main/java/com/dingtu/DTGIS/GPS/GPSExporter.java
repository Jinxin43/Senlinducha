package com.dingtu.DTGIS.GPS;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.dingtu.DTGIS.DataService.GPSPoint;
import com.dingtu.DTGIS.DataService.LogDB;
import com.dingtu.senlinducha.R;

import android.R.bool;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.Cargeometry.Coordinate;
import lkmap.Map.StaticObject;
import lkmap.MapControl.IOnPaint;
import lkmap.MapControl.Pan;

public class GPSExporter implements IOnPaint 
{
	private v1_FormTemplate DialogView = null; 
	private String Id = UUID.randomUUID().toString();
	
	List<GPSPoint> allPoint = new ArrayList<GPSPoint>();
	Date startDate = new Date();
	Date endDate = new Date();
	String logPath = PubVar.m_SysAbsolutePath+"/Log/";
	
	//测量点
	private List<Coordinate> m_MeasurePointList = null;
	private Pan pan = null;	
	
    public GPSExporter()
    {
    	DialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	DialogView.SetOtherView(R.layout.d_gps_manager);
    	EditText etfolder = (EditText)DialogView.findViewById(R.id.exportfolder);
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); 
    	etfolder.setText(simpleDateFormat.format(new java.util.Date()));
    	DialogView.ReSetSize(0.7f,0.96f);
//    	TimePicker startTP = (TimePicker)_Dialog.findViewById(R.id.dp_StartTime);
//    	startTP.setIs24HourView(true);
//    	Calendar calStart = Calendar.getInstance();
//    	startTP.setCurrentHour(calStart.get(Calendar.HOUR_OF_DAY));
    	
//    	TimePicker endTP = (TimePicker)_Dialog.findViewById(R.id.dp_EndTime);
//    	endTP.setIs24HourView(true);
//    	endTP.setCurrentHour(calStart.get(Calendar.HOUR_OF_DAY));
    	//设置标题
    	DialogView.SetCaption(Tools.ToLocale("航迹管理"));
    	
    	Button query = (Button)DialogView.findViewById(R.id.btn_query);
    	query.setOnClickListener(mClickListener);
    	
    	Button btnexport = (Button)DialogView.findViewById(R.id.btn_exportgps);
    	btnexport.setOnClickListener(mClickListener);
    	
    	Button btnPaint = (Button)DialogView.findViewById(R.id.btn_paintgps);
    	btnPaint.setOnClickListener(mClickListener);
    	
    	Button btnClear = (Button)DialogView.findViewById(R.id.btn_clear);
    	btnClear.setOnClickListener(mClickListener);
    	
    	CheckBox ckRecordGPS = (CheckBox)DialogView.findViewById(R.id.cb_recordgps);
    	ckRecordGPS.setChecked(PubVar.recordGPS);
    	ckRecordGPS.setOnCheckedChangeListener(mOnCheckedChangeListener);
    	
    	
    	this.m_MeasurePointList = new ArrayList<Coordinate>();
    	pan = new Pan(PubVar.m_MapControl);
    	
    	
    }
    
    private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
			PubVar.recordGPS = isChecked;
			
		}
	};
    
    private OnClickListener mClickListener = new View.OnClickListener() 
    {
		
		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) 
		{
			if(v.getTag().equals("查询"))
			{
				DatePicker startDP = (DatePicker)DialogView.findViewById(R.id.dp_StartDate);
				//TimePicker startTP = (TimePicker)_Dialog.findViewById(R.id.dp_StartTime);
				//startTP.setIs24HourView(true);
				startDate = new Date(startDP.getYear()-1900,startDP.getMonth(),startDP.getDayOfMonth());
				DatePicker endDP = (DatePicker)DialogView.findViewById(R.id.dp_EndDate);
				//TimePicker endTP = (TimePicker)_Dialog.findViewById(R.id.dp_EndTime);
				//endTP.setIs24HourView(true);
				endDate = new Date(endDP.getYear()-1900,endDP.getMonth(),endDP.getDayOfMonth());
				if(endDate.before(startDate))
				{
					Tools.ShowMessageBox("查询截止时间必须晚于开始时间");
					return;
				}
				
				allPoint = new LogDB().QueryGPSPoint(startDate, endDate);
				SimpleDateFormat allSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Tools.SetTextViewValueOnID(DialogView, R.id.tv_startTime, "起始时间："+allSDF.format(startDate));
				Tools.SetTextViewValueOnID(DialogView, R.id.tv_endTime, "截止时间："+allSDF.format(endDate));
				Tools.SetTextViewValueOnID(DialogView, R.id.tv_gpsCount, "航点总数："+allPoint.size());
			}
			
			if(v.getTag().equals("导出"))
			{
				if(allPoint.size()>0)
				{
					String fileName = ((TextView)DialogView.findViewById(R.id.exportfolder)).getText().toString();
					if(fileName.isEmpty())
					{
						Tools.ShowMessageBox("导出目录不能为空");
						return;
					}
					SimpleDateFormat allSDF = new SimpleDateFormat("yyyyMMdd");
					String logFolder = logPath+fileName+"/";
					File saveFile = new File(logFolder, allSDF.format(startDate)+"-"+allSDF.format(endDate)+".CSV");
			    	
			    	
			    	try
			    	{
			    		if (!lkmap.Tools.Tools.ExistFile(logFolder))
						{
							(new File(logFolder)).mkdirs();
						}
			    		
			    		FileOutputStream bcpFileWriter = new FileOutputStream(saveFile); 
			    		byte[] bom ={(byte) 0xEF,(byte) 0xBB,(byte) 0xBF}; 
				    	bcpFileWriter.write(bom);
			    		bcpFileWriter.write(new String("序号,GPS时间,经度,纬度,高度,速度,定位精度,工程,记录时间\n".getBytes(),"utf-8").getBytes());
			    		int i = 0;
			    		for(GPSPoint point:allPoint)
			    		{
			    			i++;
			    			String value = String.valueOf(i)+","+
    								point.getGPSTime()+","+
    								point.getLongitude()+","+
    								point.getLatitude()+","+
    								point.getAltitude()+","+
    								point.getSpeed()+","+
    								point.getPDOP()+","+
    								point.getproject()+","+
    								point.getRecordTime()+"\n";
			    			
			    			bcpFileWriter.write((new String(value.getBytes(),"utf-8")).getBytes());
			    		}
			    		
			    		bcpFileWriter.flush();
		        		bcpFileWriter.close();
		        		lkmap.Tools.Tools.ShowMessageBox(DialogView.getContext(), "数据成功导出！\r\n\r\n位于：【"+logFolder+allSDF.format(startDate)+"-"+allSDF.format(endDate)+".CSV"+"】");
			    	}
			    	catch(Exception e)
			    	{
			    		Log.e("航迹导出", e.getMessage());
			    	}
				}
			}
			
			if(v.getTag().equals("绘制"))
			{
				addPaint();
				m_MeasurePointList.clear();
				if(allPoint.size()>0)
				{
					for (GPSPoint gpsPoint : allPoint) 
					{
						Coordinate coordinate = new Coordinate(Double.valueOf(gpsPoint.getLongitude()),Double.valueOf(gpsPoint.getLatitude()));
						coordinate.setZ(Double.valueOf(gpsPoint.getAltitude()));
						coordinate = StaticObject.soProjectSystem.WGS84ToXY(coordinate.getX(),coordinate.getY(),coordinate.getZ());
						if(coordinate != null)
						{
							m_MeasurePointList.add(coordinate);
						}
						
					}
				}
				
				PubVar.m_MapControl.invalidate();
			}
			
			if(v.getTag().equals("清除"))
			{
				m_MeasurePointList.clear();
				PubVar.m_MapControl.invalidate();
				clearPaint();
			}
		}
	};
	
	
	private void addPaint()
	{
		PubVar.m_MapControl.AddOnPaint(this.Id, this);
	}
	
	private void clearPaint()
	{
		PubVar.m_MapControl.ClearOnPaint(this.Id);
	}
	
	@Override
	public void OnPaint(Canvas canvas) 
	{
		this.pan.OnPaint(canvas);
    	if (PubVar.m_Map.getInvalidMap()) return;
		if (this.m_MeasurePointList.size()==0) return;
		//绘制轨迹点信息，形成轨迹线
		Point[] PList = PubVar.m_MapControl.getMap().getViewConvert().MapPointsToScreePoints(this.m_MeasurePointList);
		
		Path p = new Path();
		
		//绘制轨迹节点
     	int H = Tools.DPToPix(8);
    	for(int i=0;i<PList.length;i++)
    	{
    		//绘制单个节点
            Paint pBrush = new Paint();
            pBrush.setStrokeWidth(Tools.DPToPix(5));
            if (i==0) pBrush.setColor(Color.GREEN);  //起
            if (i==PList.length-1) pBrush.setColor(Color.YELLOW);  //终
            if (i>0 && i<PList.length-1)pBrush.setColor(Color.RED);
            canvas.drawCircle(PList[i].x, PList[i].y, H/2, pBrush);
            pBrush.setStyle(Style.STROKE);
            pBrush.setColor(Color.BLUE);
            pBrush.setStrokeWidth(Tools.DPToPix(2));
            canvas.drawCircle(PList[i].x, PList[i].y, H/2, pBrush);
            
            if (i==0)p.moveTo(PList[i].x, PList[i].y);
    		else p.lineTo(PList[i].x, PList[i].y);
    	}
    	
    	Paint pPen = new Paint();
    	pPen.setStrokeWidth(Tools.DPToPix(3));
    	pPen.setColor(Color.BLUE);
    	pPen.setStyle(Style.STROKE);
    	canvas.drawPath(p, pPen);
	}
	
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	DialogView.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				
			}});
    	DialogView.show();
    }
}
