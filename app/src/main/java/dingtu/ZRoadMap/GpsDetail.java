package dingtu.ZRoadMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;
import android.widget.RelativeLayout.LayoutParams;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.GPS.NEMASatellite;
import lkmap.Tools.Tools;

public class GpsDetail {

	private v1_FormTemplate dialogView = null; 
	private String m_Id = "";
	
	public GpsDetail()
	{
		this.m_Id = UUID.randomUUID().toString();
    	dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	dialogView.SetOtherView(R.layout.d_gpsdetail);
    	dialogView.SetCallback(this.m_Callback);
    	dialogView.ReSetSize(0.74f, 0.86f);
    	//dialogView.findViewById(R.id.Close_GPSSET).setOnClickListener(new ViewClick());
        
    	//设置标题
    	dialogView.SetCaption(Tools.ToLocale("返回"));
    	
    	//设置默认按钮
    	dialogView.SetButtonInfo("1,"+R.drawable.icon_title_close+","+Tools.ToLocale("关闭")+" GPS ,关闭GPS", this.m_Callback);
    	
        this.m_ImgView = (ImageView)dialogView.findViewById(R.id.iv_gps);
        this.m_ImgViewBound = (ImageView)dialogView.findViewById(R.id.iv_gps2);
        this.m_ImgViewSNR = (ImageView)dialogView.findViewById(R.id.iv_snr);
        
        
        
        //加入指南针关联
        HashMap<String,Object> _bindCompass = new HashMap<String,Object>();
        _bindCompass.put("ID", this.m_Id);
        _bindCompass.put("View", dialogView.findViewById(R.id.iv_compassarrow));
        _bindCompass.put("ICallback", new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				Tools.SetTextViewValueOnID(dialogView, R.id.tv_compassangle, ExtraStr+"");
			}});
        PubVar.m_DoEvent.m_Compass.AddBindCompass(_bindCompass);
        
        //多语言支持
    	Tools.ToLocale(dialogView.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(dialogView.findViewById(R.id.tvLocaleText2));
    	this.UpdateGPSShowInfo("0.0", "0", "0.0", "未定位", "000:000000", "00:00000");
	}
	   //传感 器类
    private SensorManager m_SensorManager = null;
//    private Sensor m_Sensor1 = null;
//    private Sensor m_Sensor2 = null;
    private Sensor m_Sensor3 = null;
    
	private GpsStatus.Listener statusListener= null;
	
//	//从NEMA1083中解算卫星位置
//	private NEMALocate m_NEMALocate = new NEMALocate();

	//星历绘图控件
	private ImageView m_ImgView= null;
	//星历绘图
	private Canvas m_Graphics = null;
	
	//SNR阶梯图
	private ImageView m_ImgViewSNR= null;
	private Canvas m_SNRGraphics = null;
	
	//柱状图
	private ImageView m_ImgViewBound= null;
	private Canvas m_BoundGraphics = null;
	
	private static Bitmap m_Bitmap1 = null;
	private static Bitmap m_Bitmap2 = null;
	private static Bitmap m_Bitmap3 = null;
    /**
     * 创建布局
     */
    private void CreateLayerOut()
    {
    	//SNR图
    	if (m_Bitmap1==null) m_Bitmap1 = Bitmap.createBitmap(this.m_ImgViewSNR.getMeasuredWidth(),this.m_ImgViewSNR.getMeasuredHeight(),Config.ARGB_8888);
    	this.m_ImgViewSNR.setImageBitmap(m_Bitmap1);
    	this.m_SNRGraphics = new Canvas(m_Bitmap1);
    	
    	//星历图
    	RelativeLayout rl = (RelativeLayout)dialogView.findViewById(R.id.rl_gpscompass);
        if (m_Bitmap2==null)m_Bitmap2= Bitmap.createBitmap(rl.getMeasuredWidth(),rl.getMeasuredHeight(),Config.ARGB_8888);
    	this.m_ImgView.setImageBitmap(m_Bitmap2);
		this.m_Graphics = new Canvas(m_Bitmap2);
    	

		
		//确定指北针箭头的中心定位点
		View compassView = dialogView.findViewById(R.id.iv_compassarrow);
		LayoutParams LP = new LayoutParams(compassView.getWidth(),compassView.getHeight());
		LP.leftMargin = this.m_Graphics.getWidth()/2-compassView.getWidth()/2;
		LP.topMargin = this.m_Graphics.getHeight()/2-compassView.getHeight()/2;
		compassView.setLayoutParams(LP);
		
		//柱状图
		LinearLayout LL = (LinearLayout)dialogView.findViewById(R.id.imageControl);
        if (m_Bitmap3==null) m_Bitmap3= Bitmap.createBitmap(LL.getMeasuredWidth(),LL.getMeasuredHeight(),Config.ARGB_8888);
        this.m_ImgViewBound.setImageBitmap(m_Bitmap3);
		this.m_BoundGraphics = new Canvas(m_Bitmap3);
		
		ToggleButton tbAutoSpan =(ToggleButton)dialogView.findViewById(R.id.tbautoSpan);
		tbAutoSpan.setChecked(PubVar.AutoPan);
		tbAutoSpan.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			 
			 @Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(buttonView.isChecked())
					{
						PubVar.AutoPan= true;
					}
					else
					{
						PubVar.AutoPan= false;
					}
				}
		 });
		
//		 ToggleButton tb =(ToggleButton) dialogView.findViewById(R.id.tb_gpsopen);
//		 
//	     tb.setChecked(true);
//	     
//	     tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(!buttonView.isChecked())
//				{
//					PubVar.m_DoEvent.DoCommand("关闭GPS");
//					if (PubVar.m_GPSLocate.m_LTManager!=null)
//					{
//			    		//PubVar.m_GPSLocate.m_LTManager.removeGpsStatusListener(statusListener);
//			    		//m_SensorManager.unregisterListener(sensorListener);
//						PubVar.m_DoEvent.m_Compass.RemoveBindCompass(m_Id);
//			    		PubVar.m_GPSLocate.SetGpsSetCallback(null);
//					}
//					System.gc();
//		    		dialogView.dismiss();
//				}
//				else
//				{
//					PubVar.m_DoEvent.DoCommand("自动开启GPS");
//				}
//			}
//		});
	        
		this.Refresh();

		PubVar.m_GPSLocate.SetGpsSetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {

	            //得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
//	        	 if (m_GPSInfoType.equals("系统"))
				
				String GpsAllType = "GPS";String JSType = "内置";
				if (PubVar.m_GPSLocate.m_NEMALocate.GetUseNEMA())
				{
					JSType="NMEA0183";
					if (PubVar.m_GPSLocate.m_NEMALocate.GetHaveBD())GpsAllType+="+北斗";
				}
	        	 //dialogView.SetCaption(String.format("GPS 信息:   【%1$s】%2$s", GpsAllType,JSType));
				Tools.SetTextViewValueOnID(dialogView, R.id.tvStatus, String.format("卫星信息：【%1$s】%2$s", GpsAllType,JSType));
	        	 {Refresh();}
			}});
		
        if (PubVar.m_GPSLocate==null) return;

      
//        //解析NEMA报文回调
//        this.m_NEMALocate.Start(new ICallback(){
//
//			@Override
//			public void OnClick(String Str, Object ExtraStr) {
//				
//	        	 String GpsAllType = "GPS";
//	        	 if (Str.equals("北斗")){GpsAllType+="+"+Str;m_GPSInfoType = "NEMA";}
//	        	 dialogView.SetCaption(String.format("GPS 信息【%1$s】", GpsAllType));
//	        	 
//				Refresh();
//			}});
    }
    

	//更新GPS信息标签
    private void UpdateGPSShowInfo(String Speed,String Elev,String Prec,String Status,String JD,String WD)
    {
        Tools.SetTextViewValueOnID(dialogView, R.id.tv_sd, Tools.ToLocale("速度")+"："+Speed+ " km/h");
        Tools.SetTextViewValueOnID(dialogView, R.id.tv_gc, Tools.ToLocale("高程")+"："+Elev+ " "+Tools.ToLocale("米"));
        
        String JDUnit = "精度："+Prec+" 米";
        if (PubVar.m_GPSLocate.m_NEMALocate.GetUseNEMA())JDUnit = "精度："+Prec;

        Tools.SetTextViewValueOnID(dialogView, R.id.tv_pos_jd, JDUnit);
        Tools.SetTextViewValueOnID(dialogView, R.id.tv_pos_zt, Tools.ToLocale("状态")+"："+Tools.ToLocale(Status));
        Tools.SetTextViewValueOnID(dialogView, R.id.tv_jd, Tools.ToLocale("经度")+"："+JD);
        Tools.SetTextViewValueOnID(dialogView, R.id.tv_wd, Tools.ToLocale("纬度")+"："+WD);
    }
    
    /**
     * 刷新显示
     */
    private void Refresh()
    {

    	dialogView.findViewById(R.id.iv_gps2).invalidate();
    	dialogView.findViewById(R.id.iv_gps).invalidate();
    	
    	this.m_Graphics.drawColor(Color.GRAY);
    	this.m_SNRGraphics.drawColor(Color.GRAY);
    	this.m_BoundGraphics.drawColor(Color.GRAY);
    	
    	if (PubVar.m_GPSLocate==null) return;
    	if (PubVar.m_GPSLocate.m_LTManager==null)return;
    	GpsStatus gpsStatus = PubVar.m_GPSLocate.m_LTManager.getGpsStatus(null);
    	if (gpsStatus==null) return;
    	
    	//可见卫星数，参于解算卫星数
    	List<NEMASatellite> StaList = null;
    	if (PubVar.m_GPSLocate.m_NEMALocate.GetUseNEMA()) {StaList = PubVar.m_GPSLocate.m_NEMALocate.GetSatelliteList();}
    	else
    	{
    		StaList = new ArrayList<NEMASatellite>();
    		Iterable<GpsSatellite> GpsSateList = gpsStatus.getSatellites();
    		for(GpsSatellite GpsSate :GpsSateList)
    		{
    			String PrnText = GpsSate.getPrn()+"";
    			if (GpsSate.getPrn()>32)PrnText = "R"+GpsSate.getPrn();
    			NEMASatellite Sta = new NEMASatellite();
    			Sta.setPrn(PrnText);
    			Sta.setSnr(GpsSate.getSnr()+"");
    			Sta.setAzimuth(GpsSate.getAzimuth()+"");
    			Sta.setElevation(GpsSate.getElevation()+"");
    			Sta.setUsedInFix(GpsSate.usedInFix());
    			StaList.add(Sta);
    		}
    	}
    	if (StaList==null) return;
    	
		int FixSateCount = 0;int AllSateCount = 0;
		for(NEMASatellite ST :StaList){AllSateCount++;if (ST.getUsedInFix())FixSateCount++;}

		if (PubVar.m_GPSLocate.m_LocationEx!=null)
		{
	    	this.UpdateGPSShowInfo(PubVar.m_GPSLocate.getGPSSpeed(), PubVar.m_GPSLocate.getGC(), PubVar.m_GPSLocate.getAccuracy(), 
	    						  (PubVar.m_GPSLocate.AlwaysFix()?Tools.ToLocale("已定位"):Tools.ToLocale("未定位")), 
	    						   PubVar.m_GPSLocate.getJWGPSCoordinate().split(",")[0],PubVar.m_GPSLocate.getJWGPSCoordinate().split(",")[1]);
		}
		
		//绘制SNR阶梯图
		this.DrawSNR(this.m_ImgViewSNR.getMeasuredWidth(),this.m_ImgViewSNR.getMeasuredHeight());
    	
    	//绘制背景罗盘
    	float MinV = Math.min(this.m_ImgView.getMeasuredWidth(), this.m_ImgView.getMeasuredHeight());
    	float r = MinV / 2 - 10;   //半径
    	PointF centerPT = new PointF(this.m_ImgView.getMeasuredWidth()/2,this.m_ImgView.getMeasuredHeight()/2);
    	this.DrawCompass(centerPT,r);
    	//this.DrawSateNumber(this.m_Graphics, AllSateCount, FixSateCount);
    	Tools.SetTextViewValueOnID(this.dialogView, R.id.tvstal, "可见卫星数量："+AllSateCount+"");
    	Tools.SetTextViewValueOnID(this.dialogView, R.id.tvfixstal, "解算卫星数量："+FixSateCount+"");
    	
    	//绘制小卫星
    	for(NEMASatellite ST : StaList)
    	{
    		this.DrawGPSSatellite(ST, centerPT, r);
    	}
    	
    	//绘制柱状图
    	this.DrawBound(StaList,AllSateCount);
//    	this.DrawSateNumber(this.m_BoundGraphics, AllSateCount, FixSateCount);
    	
    	
    }
    
    /**
     * 绘制柱状图
     * @param GpsSateList
     */
    private void DrawBound(List<NEMASatellite> GpsSateList,int AllSateCount)
    {
    	float H = this.m_BoundGraphics.getHeight();
    	float W = this.m_BoundGraphics.getWidth();
    	
    	//坐标轴颜色
    	Paint pPen = new Paint();
    	pPen.setStyle(Style.STROKE);
    	pPen.setAntiAlias(true);
    	pPen.setColor(Color.parseColor("#1e1e5a"));
    	
    	//纵轴刻度值颜色
		Paint pText = new Paint();
		pText.setColor(Color.WHITE);
		pText.setAntiAlias(true);
    	Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
    	pText.setTextSize(Tools.SPToPix(13));
    	pText.setTypeface(TF);
    	pText.setAntiAlias(true);
    	
    	//画坐标轴
    	float KD = H / 8;
    	float WOffset = 30;float HOffset = 40;
    	this.m_BoundGraphics.drawLine(WOffset,H-HOffset, W-WOffset, H-HOffset, pPen);  //横轴
    	this.m_BoundGraphics.drawLine(WOffset,HOffset+KD, WOffset, H-HOffset, pPen);  //纵轴，为了避免与上面的卫星数量文件相叠加，向下串1个KD
    	
		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);  
		pPen.setColor(Color.WHITE);
		pPen.setPathEffect(effects);
    	for(int i = 1;i<=4;i++)
    	{
    		this.m_BoundGraphics.drawLine(WOffset,H - KD*i-HOffset, W-WOffset, H - KD*i-HOffset, pPen);  //纵轴刻度线
    		this.m_BoundGraphics.drawText(10*i+"",3,H - KD*i-HOffset+5 ,pText);  //纵轴刻度值
    	}
    	
		Paint p = new Paint();
		p.setStyle(Style.FILL);
		p.setAntiAlias(true);
		
    	//计算柱状的宽度
    	float BarWidth = Tools.DPToPix(30);
    	//if (AllSateCount>11)BarWidth = W / AllSateCount;
    	
		int idx = 0;
    	for(NEMASatellite GpsSate : GpsSateList)
    	{
    		float BH = KD*Float.parseFloat(GpsSate.getSnr())/10;
    		if (Float.parseFloat(GpsSate.getSnr())>40)
    		{
    			BH =  KD*40/10 + KD*(Float.parseFloat(GpsSate.getSnr())-40)/25;
    		}
    		//小于1不画了
    		if (Float.parseFloat(GpsSate.getSnr())<1)continue;
    		
    		float x1 = WOffset+BarWidth*idx+idx;
    		float y2 = this.m_BoundGraphics.getHeight()-HOffset;
    		float x2 = WOffset+BarWidth*idx+idx+BarWidth;
    		float y1 = this.m_BoundGraphics.getHeight()-HOffset-BH;
    		
    		p.setColor(this.GetGpsSatelliteColor(GpsSate));
    		this.m_BoundGraphics.drawRect(x1,y1,x2 ,y2, p);
    		
    		//画SNR值
    		String SNRStr = (int)(Float.parseFloat(GpsSate.getSnr()))+"";
    		this.m_BoundGraphics.drawText(SNRStr,x1+(BarWidth-pText.measureText(SNRStr))/2,y1-2, pText);
    		
    		//画编号
        	String PrnText = GpsSate.getPrn();
    		this.m_BoundGraphics.drawText(PrnText,x1+(BarWidth-pText.measureText(PrnText))/2,y2+pText.getTextSize(), pText);

    		idx++;
    	}
    }

    private Bitmap _BPBitmap = null;
    private Bitmap _GPSBitmap = null;
    private Bitmap _GLNBitmap = null;
    
    /**
     * 在罗盘上画卫星
     * @param GpsSate
     * @param centerPT
     * @param r
     */
    private void DrawGPSSatellite(NEMASatellite GpsSate,PointF centerPT,float r)
    {
		//GpsSate.getAzimuth() // 方位角
		//GpsSate.getSnr() // 信噪比
		//GpsSate.getPrn() // 伪随机号
		//GpsSate.getElevation()() // 高度角
    	
    	//卫星编号，确定用哪个小国旗
    	String PrnText = GpsSate.getPrn();
    	
    	float Aang = Float.parseFloat(GpsSate.getAzimuth());
    	Aang=(90-Aang);
		if (Aang<0)Aang=360+Aang;
		Aang = (float) (Aang*Math.PI/180);
		float HR = Float.parseFloat(GpsSate.getElevation()) * r / 90f;
		float x = (float) (Math.cos(Aang)*HR+centerPT.x);
		float y = (float) (centerPT.y - Math.sin(Aang)*HR);

		Bitmap bitmap = null;
		if (PrnText.contains("B"))
		{
			if (this._BPBitmap==null)this._BPBitmap = ((BitmapDrawable)(PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_china))).getBitmap();  
			bitmap = this._BPBitmap;
		}
		if (PrnText.contains("G"))
		{
			if (this._GPSBitmap==null)this._GPSBitmap = ((BitmapDrawable)(PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_america))).getBitmap();  
			bitmap = this._GPSBitmap;
		}
		if (PrnText.contains("R"))
		{
			if (this._GLNBitmap==null)this._GLNBitmap = ((BitmapDrawable)(PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.v1_russia))).getBitmap();  
			bitmap = this._GLNBitmap;
		}
		if(bitmap==null) return;
		
		int bitmapEndSize = Tools.SPToPix(20);
		this.m_Graphics.drawBitmap(bitmap, new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()), new Rect((int)x-bitmapEndSize/2,(int)y,(int)x+bitmapEndSize/2,(int)y+bitmapEndSize), null);
		//this.m_Graphics.drawBitmap(bitmap,x,y,null);

		
		//画卫星编号
		Paint pText = new Paint();
		pText.setColor(Color.WHITE);
		pText.setAntiAlias(true);
    	Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
    	pText.setTextSize(Tools.SPToPix(10));
    	pText.setTypeface(TF);
    	pText.setAntiAlias(true);
    	
    	this.m_Graphics.drawText(PrnText, x-pText.measureText(PrnText)/2, y+bitmapEndSize+Tools.SPToPix(10), pText);
    }

	/**
	 * 绘制罗盘
	 * @param centerPoint
	 * @param r
	 */
	@SuppressLint("NewApi")
	private void DrawCompass(PointF centerPoint,float r)
	{ 
		//画圈线
		Paint p = new Paint();
		p.setStyle(Style.FILL);
		p.setColor(Color.parseColor("#1e1e5a"));
		p.setAntiAlias(true);
		//this.m_Graphics.drawCircle(centerPoint.x,centerPoint.y,r,p);
		
		BitmapFactory.Options newOptions = new BitmapFactory.Options();
        newOptions.inMutable = true;
		Bitmap newbp = BitmapFactory.decodeResource(PubVar.m_DoEvent.m_Context.getResources(),R.drawable.xingli,newOptions);
		Matrix matrix = new Matrix();
		int minWidth = Math.min(this.m_ImgView.getWidth(),this.m_ImgView.getHeight());
		int maxWidth = Math.max(this.m_ImgView.getWidth(),this.m_ImgView.getHeight());
		float scaleWidth = minWidth/(float)newbp.getWidth();
		matrix.setScale(scaleWidth, scaleWidth);
//		float[] values = {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,2.0f};
//		matrix.setValues(values);
		Bitmap dstbmp = Bitmap.createBitmap(newbp, 0, 0, newbp.getHeight(), newbp.getWidth(),  
                matrix, false);
		this.m_Graphics.drawBitmap(dstbmp, (maxWidth-minWidth)/2,0, null);
		
		p.setStyle(Style.STROKE);p.setColor(Color.WHITE);
		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);  
		p.setPathEffect(effects);
		
		this.m_Graphics.drawCircle(centerPoint.x,centerPoint.y,r/3,p);
		this.m_Graphics.drawCircle(centerPoint.x,centerPoint.y,2*r/3,p);
		
		//画度数
		Paint pText = new Paint();
		
		pText.setAntiAlias(true);
    	Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
    	
    	pText.setTypeface(TF);
    	pText.setAntiAlias(true);
    	
		for(int ang = 0;ang<=330;ang+=30)
		{
			float Aang = (float) (ang*Math.PI/180);
			float x = (float) (Math.cos(Aang)*r+centerPoint.x);
			float y = (float) (centerPoint.y - Math.sin(Aang)*r);
			
			float Text_X = (float) (Math.cos(Aang)*2.7*r/3+centerPoint.x);
			float Text_Y = (float) (centerPoint.y - Math.sin(Aang)*2.7*r/3);
			
			this.m_Graphics.drawLine(centerPoint.x,centerPoint.y, x, y, p);
			int Tang=(90-ang);
			if (Tang<0)Tang=360+Tang;
			
			String AnText = Tang+"°";
			pText.setTextSize(Tools.SPToPix(15));pText.setColor(Color.parseColor("#ded5d5"));
			float TextScale = 1.5f;
			if (Tang==0) {AnText = Tools.ToLocale("北");pText.setColor(Color.WHITE);pText.setTextSize(pText.getTextSize()*TextScale);}
			if (Tang==90) {AnText = Tools.ToLocale("东");pText.setColor(Color.WHITE);pText.setTextSize(pText.getTextSize()*TextScale);}
			if (Tang==180) {AnText = Tools.ToLocale("南");pText.setColor(Color.WHITE);pText.setTextSize(pText.getTextSize()*TextScale);}
			if (Tang==270) {AnText = Tools.ToLocale("西");pText.setColor(Color.WHITE);pText.setTextSize(pText.getTextSize()*TextScale);}
			this.m_Graphics.drawText(AnText, Text_X-pText.measureText(AnText)/2, Text_Y, pText);
		}
		

	}
	
	/**
	 * 画卫星的可见及解算数量值
	 * @param g
	 * @param AllSateCount
	 * @param FixSateCount
	 */
	private void DrawSateNumber(Canvas g,int AllSateCount,int FixSateCount)
	{
		Paint pText = new Paint();
		pText.setColor(Color.parseColor("#ded5d5"));
		pText.setAntiAlias(true);
    	Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
    	pText.setTextSize(Tools.SPToPix(15));
    	pText.setTypeface(TF);
    	pText.setAntiAlias(true);
    	
		//画卫星数量
		pText.setColor(Color.WHITE);
		float HOffset = Tools.SPToPix(15);
		g.drawText(Tools.ToLocale("可见卫星数"), 10,0+HOffset, pText);
		g.drawText(Tools.ToLocale("解算卫星数"), g.getWidth() -pText.measureText(Tools.ToLocale("解算卫星数"))-10 ,0+HOffset, pText);
		pText.setTextSize(pText.getTextSize()*2);
		g.drawText(AllSateCount+Tools.ToLocale("颗"), 10,pText.getTextSize()+HOffset, pText);
		g.drawText(FixSateCount+Tools.ToLocale("颗"), g.getWidth() -pText.measureText(FixSateCount+Tools.ToLocale("颗"))-10,pText.getTextSize()+HOffset, pText);
	}
	
	/**
	 * 绘制SNR阶梯图
	 * @param W
	 * @param H
	 */
	private void DrawSNR(float W,float H)
	{
		Paint p = new Paint();
		p.setStyle(Style.FILL);
		p.setAntiAlias(true);
		
		Paint pText = new Paint();
		pText.setColor(Color.WHITE);
		pText.setAntiAlias(true);
    	Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
    	pText.setTextSize(Tools.SPToPix(15));
    	pText.setTypeface(TF);
    	pText.setAntiAlias(true);
    	
    	float HOffset = Tools.SPToPix(15);
		p.setColor(this.GetGpsSatelliteColor(5));
		this.m_SNRGraphics.drawRect(0, 0, W /10 , H/2, p);
		this.m_SNRGraphics.drawText("00", 0-pText.measureText("00")/2, H/2+HOffset, pText);
		
		p.setColor(this.GetGpsSatelliteColor(15));
		this.m_SNRGraphics.drawRect(W /10, 0, 2*W /10 , H/2, p);
		this.m_SNRGraphics.drawText("10", W /10-pText.measureText("10")/2, H/2+HOffset, pText);
		
		p.setColor(this.GetGpsSatelliteColor(25));
		this.m_SNRGraphics.drawRect(2*W /10, 0, 3*W /10 , H/2, p);
		this.m_SNRGraphics.drawText("20", 2*W /10-pText.measureText("20")/2, H/2+HOffset, pText);
		
		p.setColor(this.GetGpsSatelliteColor(35));
		this.m_SNRGraphics.drawRect(3*W /10, 0, 4*W /10 , H/2, p);
		this.m_SNRGraphics.drawText("30", 3*W /10-pText.measureText("30")/2, H/2+HOffset, pText);
		
		p.setColor(this.GetGpsSatelliteColor(50));
		this.m_SNRGraphics.drawRect(4*W /10, 0, W , H/2, p);
		this.m_SNRGraphics.drawText("40", 4*W /10-pText.measureText("40")/2, H/2+HOffset, pText);
	}
    
	/**
	 * 得到卫星的颜色
	 * @param GpsSate
	 * @return
	 */
	private int GetGpsSatelliteColor(NEMASatellite GpsSate)
	{
		int UnFixColor = Color.argb(255, 197, 194, 197);
		if (!GpsSate.getUsedInFix())return UnFixColor;
		else return this.GetGpsSatelliteColor(Float.parseFloat(GpsSate.getSnr()));
	}
	
	/**
	 * 根据SNR值获取颜色
	 * @param SNR
	 * @return
	 */
	private int GetGpsSatelliteColor(float SNR)
	{
		int Color01 = Color.argb(255, 255, 0, 0);
		int Color12 = Color.argb(255, 255, 129, 0);
		int Color23 = Color.argb(255, 255, 255, 0);
		int Color34 = Color.argb(255, 222, 255, 0);
		int Color49 = Color.argb(255, 0, 255, 0);
		if (SNR>=0 && SNR<=10) return Color01;
		if (SNR>10 && SNR<=20) return Color12;
		if (SNR>20 && SNR<=30) return Color23;
		if (SNR>30 && SNR<=40) return Color34;
		if (SNR>40) return Color49;
		return 0;
	}
	
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	dialogView.setOnShowListener(new OnShowListener()
    	{
			@Override
			public void onShow(DialogInterface dialog) 
			{
				CreateLayerOut();
			}
		});
    	
    	dialogView.show();
    }
    
    private ICallback m_Callback = new ICallback()
    {
		@Override
		public void OnClick(String Str, Object ExtraStr) 
		{
			if (Str.equals("退出"))
			{
				if (PubVar.m_GPSLocate.m_LTManager!=null)
				{
		    		//PubVar.m_GPSLocate.m_LTManager.removeGpsStatusListener(statusListener);
		    		//m_SensorManager.unregisterListener(sensorListener);
					PubVar.m_DoEvent.m_Compass.RemoveBindCompass(m_Id);
		    		PubVar.m_GPSLocate.SetGpsSetCallback(null);
				}
				System.gc();
	    		dialogView.dismiss();
			}
			if (Str.equals("关闭GPS"))
			{
				PubVar.m_DoEvent.DoCommand(Str);
				m_Callback.OnClick("退出", null);
			}
			
		}
	};
}

