package dingtu.ZRoadMap;

import lkmap.Tools.Tools;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class CopyOfv1_GPSSet
{
	private v1_FormTemplate _Dialog = null; 

    public CopyOfv1_GPSSet()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_gpsset);
    	_Dialog.SetCallback(this.m_Callback);
    	_Dialog.ReSetSize(1f, 0.96f);
    	//_Dialog.findViewById(R.id.Close_GPSSET).setOnClickListener(new ViewClick());
        
    	//���ñ���
    	_Dialog.SetCaption("GPS "+Tools.ToLocale("��Ϣ"));
    	
    	//����Ĭ�ϰ�ť
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_closegps+","+Tools.ToLocale("�ر�")+" GPS ,�ر�GPS", this.m_Callback);
    	
        this.m_SD = (TextView)_Dialog.findViewById(R.id.tv_sd);
        this.m_POS_JD = (TextView)_Dialog.findViewById(R.id.tv_pos_jd);
        this.m_POS_ZT = (TextView)_Dialog.findViewById(R.id.tv_pos_zt);
        this.m_JD = (TextView)_Dialog.findViewById(R.id.tv_jd);
        this.m_WD = (TextView)_Dialog.findViewById(R.id.tv_wd);
        this.m_GC = (TextView)_Dialog.findViewById(R.id.tv_gc);
        this.m_ImgView = (ImageView)_Dialog.findViewById(R.id.iv_gps);
        this.m_ImgViewBound = (ImageView)_Dialog.findViewById(R.id.iv_gps2);
        this.m_ImgViewSNR = (ImageView)_Dialog.findViewById(R.id.iv_snr);
        
        this.m_ImgView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				((ViewFlipper)_Dialog.findViewById(R.id.viewFlipper1)).showPrevious();
			}});
        this.m_ImgViewBound.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				((ViewFlipper)_Dialog.findViewById(R.id.viewFlipper1)).showPrevious();
			}});
        
        //�򿪵���������������ٴ�����
        this.m_SensorManager = (SensorManager)PubVar.m_DoEvent.m_Context.getSystemService(Context.SENSOR_SERVICE);
        // this.m_Sensor1 = this.m_SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //this.m_Sensor2 = this.m_SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.m_Sensor3 = this.m_SensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //this.m_SensorManager.registerListener(sensorListener, this.m_Sensor1, SensorManager.SENSOR_DELAY_NORMAL);
        //this.m_SensorManager.registerListener(sensorListener, this.m_Sensor2, SensorManager.SENSOR_DELAY_NORMAL);
        this.m_SensorManager.registerListener(sensorListener, this.m_Sensor3, SensorManager.SENSOR_DELAY_GAME);
        
        //������֧��
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText2));
    	this.UpdateGPSShowInfo("0.0", "0", "0.0", "δ��λ", "000:000000", "00:00000");
    }
    

    //���� ����
    private SensorManager m_SensorManager = null;
//    private Sensor m_Sensor1 = null;
//    private Sensor m_Sensor2 = null;
    private Sensor m_Sensor3 = null;
    
	private GpsStatus.Listener statusListener= null;
    //�ٶ�
	private TextView m_SD = null;
	//��λ����
	private TextView m_POS_JD = null;
	//��λ״̬
	private TextView m_POS_ZT = null;
	//����
	private TextView m_JD = null;
	//γ��
	private TextView m_WD = null;
	//�߳�
	private TextView m_GC = null;
	//������ͼ�ؼ�
	private ImageView m_ImgView= null;
	//������ͼ
	private Canvas m_Graphics = null;
	
	//SNR����ͼ
	private ImageView m_ImgViewSNR= null;
	private Canvas m_SNRGraphics = null;
	
	//��״ͼ
	private ImageView m_ImgViewBound= null;
	private Canvas m_BoundGraphics = null;
	
    /**
     * ��������
     */
    private void CreateLayerOut()
    {
    	//SNRͼ
    	Bitmap bpSNR = Bitmap.createBitmap(this.m_ImgViewSNR.getMeasuredWidth(),this.m_ImgViewSNR.getMeasuredHeight(),Config.ARGB_8888);
    	this.m_ImgViewSNR.setImageBitmap(bpSNR);
    	this.m_SNRGraphics = new Canvas(bpSNR);
    	
    	//����ͼ
    	LinearLayout LL = (LinearLayout)_Dialog.findViewById(R.id.imageControl);
        Bitmap bp = Bitmap.createBitmap(LL.getMeasuredWidth(),LL.getMeasuredHeight(),Config.ARGB_8888);
        this.m_ImgView.setImageBitmap(bp);
		this.m_Graphics = new Canvas(bp);
		
		//ȷ��ָ�����ͷ�����Ķ�λ��
		View compassView = _Dialog.findViewById(R.id.iv_compassarrow);
		LayoutParams LP = new LayoutParams(compassView.getWidth(),compassView.getHeight());
		LP.leftMargin = this.m_Graphics.getWidth()/2-compassView.getWidth()/2;
		LP.topMargin = this.m_Graphics.getHeight()/2-compassView.getHeight()/2;
		compassView.setLayoutParams(LP);
		
		//��״ͼ
        Bitmap bp1 = Bitmap.createBitmap(LL.getMeasuredWidth(),LL.getMeasuredHeight(),Config.ARGB_8888);
        this.m_ImgViewBound.setImageBitmap(bp1);
		this.m_BoundGraphics = new Canvas(bp1);
		
		this.Refresh();

        //����GPS״̬�ı��Listener
        statusListener = new GpsStatus.Listener()
        {
            public void onGpsStatusChanged(int event)
            {
            	m_ImgView.invalidate();
	             switch(event)
	             {
	    	         case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
	    	
	    	            //�õ������յ������ǵ���Ϣ������ ���ǵĸ߶Ƚǡ���λ�ǡ�����ȡ���α����ţ������Ǳ�ţ�
	    	        	Refresh();
	    	          break;
	             }
            }
        };
        if (PubVar.m_GPSLocate==null) return;
        if (PubVar.m_GPSLocate.m_LTManager!=null)   //��ʾ����GPS
        {
        	PubVar.m_GPSLocate.m_LTManager.addGpsStatusListener(statusListener);
        	
        }
    }
    

    
    /**
     * �������¼�
     */
//    private float[] s_accelerometerValues = new float[3]; 
//    private float[] s_magneticFieldValues = new float[3];
    private float s_Angle = 0;
//    private float remapR[] = new float[16];  
    private SensorEventListener sensorListener = new SensorEventListener(){

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		@Override
		public void onSensorChanged(SensorEvent event) 
		{
//			if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){  
//				s_accelerometerValues=event.values; 
//            } else 
//            if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){  
//            	s_magneticFieldValues=event.values;
//            } else return;
            
            if (event.sensor.getType()==Sensor.TYPE_ORIENTATION)
            {
            	float Tang = event.values[0];
            	
                 // ������ת����������ת��degree�ȣ�  
                 RotateAnimation ra = new RotateAnimation(s_Angle, -Tang,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);  
                 // ���ö����ĳ���ʱ��  
                 ra.setDuration(200);  
                 // ���ж���  
                 _Dialog.findViewById(R.id.iv_compassarrow).startAnimation(ra);  
                 
                 
                 s_Angle = -Tang; 
                 
                 int Angle = (int)Tang;
                 int range = 25; 
                 
                 String ArrowText = "";
                 // ָ������  
                 if(Angle > 360 - range && Angle < 360 + range)  ArrowText = Tools.ToLocale("��")+" " + Angle + "��"; 
                   
                 // ָ������  
                 if(Angle > 90 - range && Angle < 90 + range)  ArrowText = Tools.ToLocale("��") +" " + Angle + "��";  
                   
                 // ָ������  
                 if(Angle > 180 - range && Angle < 180 + range)  ArrowText = Tools.ToLocale("��") +" " + Angle + "��";  
                   
                 // ָ������  
                 if(Angle > 270 - range && Angle < 270 + range)  ArrowText = Tools.ToLocale("��") +" " + Angle + "��";  
                   
                 // ָ�򶫱�  
                 if(Angle > 45 - range && Angle < 45 + range)ArrowText = Tools.ToLocale("����") +" " + Angle + "��";  
                   
                 // ָ����  
                 if(Angle > 135 - range && Angle < 135 + range)ArrowText = Tools.ToLocale("����") +" " + Angle + "��";   
                   
                 // ָ������  
                 if(Angle > 225 - range && Angle < 225 + range)  ArrowText = Tools.ToLocale("����") +" " + Angle + "��"; 
                   
                 // ָ������  
                 if(Angle > 315 - range && Angle < 315 + range)  ArrowText = Tools.ToLocale("����") +" " + Angle + "��";
                 if (ArrowText!="") Tools.SetTextViewValueOnID(_Dialog, R.id.tv_compassangle, ArrowText);
                
            }

		}};
		

	//����GPS��Ϣ��ǩ
    private void UpdateGPSShowInfo(String Speed,String Elev,String Prec,String Status,String JD,String WD)
    {
		this.m_SD.setText(Tools.ToLocale("�ٶ�")+"��"+Speed+ " km/h");
		this.m_GC.setText(Tools.ToLocale("�߳�")+"��"+Elev+ " "+Tools.ToLocale("��"));
    	this.m_POS_JD.setText(Tools.ToLocale("����")+"��"+Prec+" "+Tools.ToLocale("��"));
    	this.m_POS_ZT.setText(Tools.ToLocale("״̬")+"��"+Tools.ToLocale(Status));
    	this.m_JD.setText(Tools.ToLocale("����")+"��"+JD);
    	this.m_WD.setText(Tools.ToLocale("γ��")+"��"+WD);
    }
    
    /**
     * ˢ����ʾ
     */
    private void Refresh()
    {
    	_Dialog.findViewById(R.id.iv_gps2).invalidate();
    	_Dialog.findViewById(R.id.iv_gps).invalidate();
    	
    	this.m_Graphics.drawColor(Color.GRAY);
    	this.m_SNRGraphics.drawColor(Color.GRAY);
    	this.m_BoundGraphics.drawColor(Color.GRAY);
    	
    	if (PubVar.m_GPSLocate==null) return;
    	if (PubVar.m_GPSLocate.m_LTManager==null)return;
    	GpsStatus gpsStatus = PubVar.m_GPSLocate.m_LTManager.getGpsStatus(null);
    	if (gpsStatus==null) return;
    	
    	//�ɼ������������ڽ���������
		int FixSateCount = 0;int AllSateCount = 0;
		Iterable<GpsSatellite> GpsSateList = gpsStatus.getSatellites();
		for(GpsSatellite GpsSate :GpsSateList){AllSateCount++;if (GpsSate.usedInFix())FixSateCount++;}

		//if (PubVar.m_GPSLocate.m_Location!=null)
		{
	    	this.UpdateGPSShowInfo(PubVar.m_GPSLocate.getGPSSpeed(), PubVar.m_GPSLocate.getGC(), PubVar.m_GPSLocate.getAccuracy(), 
	    						  (PubVar.m_GPSLocate.AlwaysFix()?Tools.ToLocale("�Ѷ�λ"):Tools.ToLocale("δ��λ")), 
	    						   PubVar.m_GPSLocate.getJWGPSCoordinate().split(",")[0],PubVar.m_GPSLocate.getJWGPSCoordinate().split(",")[1]);
		}
		
		//����SNR����ͼ
		this.DrawSNR(this.m_ImgViewSNR.getMeasuredWidth(),this.m_ImgViewSNR.getMeasuredHeight());
    	
    	//���Ʊ�������
    	float MinV = Math.min(this.m_ImgView.getMeasuredWidth(), this.m_ImgView.getMeasuredHeight());
    	float R = MinV / 2 - 10;   //�뾶
    	PointF centerPT = new PointF(this.m_ImgView.getMeasuredWidth()/2,this.m_ImgView.getMeasuredHeight()/2);
    	this.DrawCompass(centerPT,R);
    	this.DrawSateNumber(this.m_Graphics, AllSateCount, FixSateCount);
    	
    	//����С����
    	for(GpsSatellite GpsSate : GpsSateList)
    	{
    		this.DrawGPSSatellite(GpsSate, centerPT, R);
    	}
    	
    	//������״ͼ
    	this.DrawBound(GpsSateList,AllSateCount);
    	this.DrawSateNumber(this.m_BoundGraphics, AllSateCount, FixSateCount);
    	
    	
    }
    
    /**
     * ������״ͼ
     * @param GpsSateList
     */
    private void DrawBound(Iterable<GpsSatellite> GpsSateList,int AllSateCount)
    {
    	float H = this.m_BoundGraphics.getHeight();
    	float W = this.m_BoundGraphics.getWidth();
    	
    	//��������ɫ
    	Paint pPen = new Paint();
    	pPen.setStyle(Style.STROKE);
    	pPen.setAntiAlias(true);
    	pPen.setColor(Color.parseColor("#1e1e5a"));
    	
    	//����̶�ֵ��ɫ
		Paint pText = new Paint();
		pText.setColor(Color.WHITE);
		pText.setAntiAlias(true);
    	Typeface TF = Typeface.create("����", Typeface.NORMAL);
    	pText.setTextSize(20);
    	pText.setTypeface(TF);
    	pText.setAntiAlias(true);
    	
    	//��������
    	float KD = H / 8;
    	float WOffset = 30;float HOffset = 40;
    	this.m_BoundGraphics.drawLine(WOffset,H-HOffset, W-WOffset, H-HOffset, pPen);  //����
    	this.m_BoundGraphics.drawLine(WOffset,HOffset+KD, WOffset, H-HOffset, pPen);  //���ᣬΪ�˱�������������������ļ�����ӣ����´�1��KD
    	
		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);  
		pPen.setColor(Color.WHITE);
		pPen.setPathEffect(effects);
    	for(int i = 1;i<=4;i++)
    	{
    		this.m_BoundGraphics.drawLine(WOffset,H - KD*i-HOffset, W-WOffset, H - KD*i-HOffset, pPen);  //����̶���
    		this.m_BoundGraphics.drawText(10*i+"",3,H - KD*i-HOffset+5 ,pText);  //����̶�ֵ
    	}
    	
		Paint p = new Paint();
		p.setStyle(Style.FILL);
		p.setAntiAlias(true);
		
    	//������״�Ŀ��
    	float BarWidth = (W-WOffset*2) / 11f;
    	if (AllSateCount>11)BarWidth = W / AllSateCount;
    	
		int idx = 0;
    	for(GpsSatellite GpsSate : GpsSateList)
    	{
    		float BH = KD*GpsSate.getSnr()/10;
    		if (GpsSate.getSnr()>40)
    		{
    			BH =  KD*40/10 + KD*(GpsSate.getSnr()-40)/25;
    		}
    		
    		float x1 = WOffset+BarWidth*idx+idx;
    		float y2 = this.m_BoundGraphics.getHeight()-HOffset;
    		float x2 = WOffset+BarWidth*idx+idx+BarWidth;
    		float y1 = this.m_BoundGraphics.getHeight()-HOffset-BH;
    		
    		p.setColor(this.GetGpsSatelliteColor(GpsSate));
    		this.m_BoundGraphics.drawRect(x1,y1,x2 ,y2, p);
    		
    		//��SNRֵ
    		String SNRStr = (int)GpsSate.getSnr()+"";
    		this.m_BoundGraphics.drawText(SNRStr,x1+(BarWidth-pText.measureText(SNRStr))/2,y1-2, pText);
    		
    		//�����
        	//1-32ΪGPS,����32ΪR
        	String PrnText = "G"+GpsSate.getPrn();
        	if (GpsSate.getPrn()>32)PrnText = "R"+GpsSate.getPrn();
    		this.m_BoundGraphics.drawText(PrnText,x1+(BarWidth-pText.measureText(PrnText))/2,y2+pText.getTextSize(), pText);

    		idx++;
    	}
    }

    /**
     * �������ϻ�����
     * @param GpsSate
     * @param centerPT
     * @param R
     */
    private void DrawGPSSatellite(GpsSatellite GpsSate,PointF centerPT,float R)
    {
		//GpsSate.getAzimuth() // ��λ��
		//GpsSate.getSnr() // �����
		//GpsSate.getPrn() // α�����
		//GpsSate.getElevation()() // �߶Ƚ�
    	float r = 10;   //С����ԲȦ�뾶
    	
    	float Aang = GpsSate.getAzimuth();
    	Aang=(90-Aang);
		if (Aang<0)Aang=360+Aang;
		
		Aang = (float) (Aang*Math.PI/180);
		float HR = GpsSate.getElevation() * R / 90f;
		float x = (float) (Math.cos(Aang)*HR+centerPT.x);
		float y = (float) (centerPT.y - Math.sin(Aang)*HR);
		Paint p = new Paint();
		p.setStyle(Style.FILL);p.setColor(this.GetGpsSatelliteColor(GpsSate));
		p.setAntiAlias(true);
		this.m_Graphics.drawCircle(x,y,r,p);
		
		//�����Ǳ��
		Paint pText = new Paint();
		pText.setColor(Color.WHITE);
		pText.setAntiAlias(true);
    	Typeface TF = Typeface.create("����", Typeface.NORMAL);
    	pText.setTextSize(20);
    	pText.setTypeface(TF);
    	pText.setAntiAlias(true);
    	
    	//1-32ΪGPS,����32ΪR
    	String PrnText = "G"+GpsSate.getPrn();
    	if (GpsSate.getPrn()>32)PrnText = "R"+GpsSate.getPrn();
    	this.m_Graphics.drawText(PrnText, x-pText.measureText(PrnText)/2, y+r+20, pText);
    }

	/**
	 * ��������
	 * @param centerPoint
	 * @param R
	 */
	private void DrawCompass(PointF centerPoint,float R)
	{
		//��Ȧ��
		Paint p = new Paint();
		p.setStyle(Style.FILL);p.setColor(Color.parseColor("#1e1e5a"));
		p.setAntiAlias(true);
		this.m_Graphics.drawCircle(centerPoint.x,centerPoint.y,R,p);
		
		p.setStyle(Style.STROKE);p.setColor(Color.WHITE);
		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);  
		p.setPathEffect(effects);
		
		this.m_Graphics.drawCircle(centerPoint.x,centerPoint.y,R/3,p);
		this.m_Graphics.drawCircle(centerPoint.x,centerPoint.y,2*R/3,p);
		
		//������
		Paint pText = new Paint();
		
		pText.setAntiAlias(true);
    	Typeface TF = Typeface.create("����", Typeface.NORMAL);
    	
    	pText.setTypeface(TF);
    	pText.setAntiAlias(true);
    	
		for(int ang = 0;ang<=330;ang+=30)
		{
			float Aang = (float) (ang*Math.PI/180);
			float x = (float) (Math.cos(Aang)*R+centerPoint.x);
			float y = (float) (centerPoint.y - Math.sin(Aang)*R);
			
			float Text_X = (float) (Math.cos(Aang)*2.7*R/3+centerPoint.x);
			float Text_Y = (float) (centerPoint.y - Math.sin(Aang)*2.7*R/3);
			
			this.m_Graphics.drawLine(centerPoint.x,centerPoint.y, x, y, p);
			int Tang=(90-ang);
			if (Tang<0)Tang=360+Tang;
			
			String AnText = Tang+"��";
			pText.setTextSize(20);pText.setColor(Color.parseColor("#ded5d5"));
			float TextScale = 1.5f;
			if (Tang==0) {AnText = Tools.ToLocale("��");pText.setColor(Color.WHITE);pText.setTextSize(pText.getTextSize()*TextScale);}
			if (Tang==90) {AnText = Tools.ToLocale("��");pText.setColor(Color.WHITE);pText.setTextSize(pText.getTextSize()*TextScale);}
			if (Tang==180) {AnText = Tools.ToLocale("��");pText.setColor(Color.WHITE);pText.setTextSize(pText.getTextSize()*TextScale);}
			if (Tang==270) {AnText = Tools.ToLocale("��");pText.setColor(Color.WHITE);pText.setTextSize(pText.getTextSize()*TextScale);}
			this.m_Graphics.drawText(AnText, Text_X-pText.measureText(AnText)/2, Text_Y, pText);
		}
		

	}
	
	/**
	 * �����ǵĿɼ�����������ֵ
	 * @param g
	 * @param AllSateCount
	 * @param FixSateCount
	 */
	private void DrawSateNumber(Canvas g,int AllSateCount,int FixSateCount)
	{
		Paint pText = new Paint();
		pText.setColor(Color.parseColor("#ded5d5"));
		pText.setAntiAlias(true);
    	Typeface TF = Typeface.create("����", Typeface.NORMAL);
    	pText.setTextSize(20);
    	pText.setTypeface(TF);
    	pText.setAntiAlias(true);
    	
		//����������
		pText.setColor(Color.WHITE);
		float HOffset = 20;
		g.drawText(Tools.ToLocale("�ɼ�������"), 10,0+HOffset, pText);
		g.drawText(Tools.ToLocale("����������"), g.getWidth() -pText.measureText(Tools.ToLocale("����������"))-10 ,0+HOffset, pText);
		pText.setTextSize(pText.getTextSize()*2);
		g.drawText(AllSateCount+Tools.ToLocale("��"), 10,pText.getTextSize()+HOffset, pText);
		g.drawText(FixSateCount+Tools.ToLocale("��"), g.getWidth() -pText.measureText(FixSateCount+Tools.ToLocale("��"))-10,pText.getTextSize()+HOffset, pText);
	}
	
	/**
	 * ����SNR����ͼ
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
    	Typeface TF = Typeface.create("����", Typeface.NORMAL);
    	pText.setTextSize(20);
    	pText.setTypeface(TF);
    	pText.setAntiAlias(true);
    	
    	float HOffset = 20f;
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
	 * �õ����ǵ���ɫ
	 * @param GpsSate
	 * @return
	 */
	private int GetGpsSatelliteColor(GpsSatellite GpsSate)
	{
		int UnFixColor = Color.argb(255, 197, 194, 197);
		if (!GpsSate.usedInFix())return UnFixColor;
		else return this.GetGpsSatelliteColor(GpsSate.getSnr());
	}
	
	/**
	 * ����SNRֵ��ȡ��ɫ
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
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener()
    	{
			@Override
			public void onShow(DialogInterface dialog) 
			{
				CreateLayerOut();
			}
		});
    	
    	_Dialog.show();
    }
    
    private ICallback m_Callback = new ICallback()
    {
		@Override
		public void OnClick(String Str, Object ExtraStr) 
		{
			if (Str.equals("�˳�"))
			{
				if (PubVar.m_GPSLocate.m_LTManager!=null)
				{
		    		PubVar.m_GPSLocate.m_LTManager.removeGpsStatusListener(statusListener);
		    		m_SensorManager.unregisterListener(sensorListener);
				}
	    		_Dialog.dismiss();
			}
			if (Str.equals("�ر�GPS"))
			{
				PubVar.m_DoEvent.DoCommand(Str);
				m_Callback.OnClick("�˳�", null);
			}
			
		}
	};
}
