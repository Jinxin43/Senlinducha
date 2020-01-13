package lkmap.GPS;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.dingtu.DTGIS.DataService.LogDB;
import com.dingtu.DTGIS.WPZFJC.WeiPianZhiFa_GPSMeasure;

import lkmap.Cargeometry.Coordinate;
import lkmap.CoordinateSystem.ProjectSystem;
import lkmap.Enum.lkGpsFixMode;
import lkmap.Enum.lkGpsLocationType;
import lkmap.Map.StaticObject;
import lkmap.MapControl.MapControl;
import lkmap.Tools.Tools;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;

public class GPSLocate
{
	public Context m_Context = null;
	public MapControl m_MapControl = null;
	public GPSMap m_GPSMap = null;
	public LocationManager m_LTManager = null;
	public LocationEx m_LocationEx = null;
	public NEMALocate m_NEMALocate = null;
	
	//GPSSetʱ�õĻص���ֻ����������
	private ICallback m_GPSSetCallback = null;
	public void SetGpsSetCallback(ICallback callback)
	{
		this.m_GPSSetCallback  = callback;
	}
	
	public GPSLocate(MapControl mapControl)
	{
		this.m_MapControl = mapControl;
		this.m_GPSMap = new GPSMap(this.m_MapControl);
		//this.m_GPSMap.SetGpsMeasure(new WeiPianZhiFa_GPSMeasure());
		PubVar.m_GPSMap = this.m_GPSMap;
		this.m_NEMALocate = new NEMALocate();
		this.m_NEMALocate.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				m_LocationEx = (LocationEx)ExtraStr;
		    	m_GPSMap.UpdateGPSStatus(m_LocationEx);
		    	if (m_GPSSetCallback!=null)m_GPSSetCallback.OnClick("", null);
			}});
	}
	
	//��GPS����ʼʱ�н������ݣ�ע���ʱ��GPS�豸Ҫ���ڴ�״̬
	public boolean GPS_OpenClose = false;   //GPS�Ŀ���״̬��true-����close-��
	public boolean OpenGPS()
	{
		//��ȡλ�ù������        
		this.m_LTManager = (LocationManager)this.m_Context.getSystemService(Context.LOCATION_SERVICE);   
		
		if (!this.m_LTManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			//��ͼ�Զ���GPSѡ���û�ɹ�
//			Intent GPSIntent = new Intent(); 
//			GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
//			GPSIntent.addCategory("android.intent.category.ALTERNATIVE"); 
//			GPSIntent.setData(Uri.parse("custom:3")); 
//	        try 
//	        { 
//	            PendingIntent.getBroadcast(m_Context, 0, GPSIntent, 0).send(); 
//
//	        } catch (CanceledException e) 
//	        { 
				// ����AlertDialog        
				AlertDialog.Builder menuDialog = new AlertDialog.Builder(PubVar.m_DoEvent.m_Context); 
				menuDialog.setTitle("ϵͳ��ʾ");
				menuDialog.setMessage("��ȡ��ȷ��λ�÷�������λ�������д�GPS���Ƿ���Ҫ�����ý��棿");
				menuDialog.setPositiveButton("ȷ��", new OnClickListener()
		        {
		            @Override
		             public void onClick(DialogInterface dialog, int which)
		            {
		        		Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		        		PubVar.m_DoEvent.m_Context.startActivity(myIntent);
		                dialog.dismiss();
		            }
		        });
				menuDialog.setNegativeButton("ȡ��", new OnClickListener()
		        {
		            @Override
		             public   void  onClick(DialogInterface dialog,  int  which)
		            {
		                dialog.dismiss();
		            }
		        });
				menuDialog.show();
				this.GPS_OpenClose = false;this.m_GPSMap.UpdateGPSStatus(null);
				return false;

		}
		
		//����λ�õ�֤��
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);   	//���þ�γ�ȵľ�׼�� ��ѡ������ACCURACY_FINE ׼ȷ ACCURACY_COARSE ����
		criteria.setAltitudeRequired(true);    			    //�����Ƿ���Ҫ��ȡ��������  
		//criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH); //�����ٶȾ���
		criteria.setBearingRequired(false);     			//�����Ƿ���Ҫ��÷�����Ϣ
		criteria.setCostAllowed(false);     				//�����Ƿ�����λ�����в����ʷѣ�����������
		criteria.setPowerRequirement(Criteria.POWER_LOW);   //���úĵ����ļ���
		String provider = this.m_LTManager.getBestProvider(criteria, true); 
		
		//��ȡ���һ��GPS��λ��Ϣ
		//this.m_Location = m_LTManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
		
		if (this.m_LocationEx!=null)this.m_GPSMap.UpdateGPSStatus(this.m_LocationEx);
		
		this.m_LTManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,0,this.m_LocateListener);
		
		//NEMA������
		this.m_LTManager.addNmeaListener(this.m_NemaListener);
		
		//GPS״̬������
		this.m_LTManager.addGpsStatusListener(this.m_GpsStatusListener);
		
		GPS_OpenClose = true;this.m_GPSMap.UpdateGPSStatus(null);
		return true;
	}
	
	//NEMA������
	private NmeaListener m_NemaListener = new NmeaListener(){
		@Override
		public void onNmeaReceived(long arg0, String arg1) {
			m_NEMALocate.onNmeaReceived(arg0, arg1);
		}};
	
	
	//λ�øı�������
	private LocationListener m_LocateListener = new LocationListener()
	{
		@Override
		public void onLocationChanged(Location location) 
		{
			if (!m_NEMALocate.GetUseNEMA())
			{
				if (location.hasAccuracy() && location.hasAltitude())
				{
					m_LocationEx.SetType(lkGpsLocationType.enInGps);
			       	m_LocationEx.SetInGpsLocate(location);
			       	m_LocationEx.SetGpsLongitude(location.getLongitude());
			       	m_LocationEx.SetGpsLatitude(location.getLatitude());
			       	m_LocationEx.SetGpsAltitude(location.getAltitude());
			       	m_LocationEx.SetGpsSpeed(location.getSpeed());
			       	m_LocationEx.SetGpsTime(location.getTime());
			    	m_GPSMap.UpdateGPSStatus(m_LocationEx);
			    	Log.d("onLocationChanged", "m_GPSMap�Ѹ���");
			    	try
			    	{
			    		//TODO:��¼�����ݿ�
			    		LogDB logDB = new LogDB();
			    		logDB.logGps(m_LocationEx.GetGpsDate().substring(0,4),m_LocationEx.GetGpsDate().replace("-", ""), m_LocationEx);
			    		Log.d("onLocationChanged", "gps��¼��д�����ݿ�");
			    	}
			    	catch (Exception e) {
						Tools.ShowMessageBox(e.getMessage());
					}
			    	if (m_GPSSetCallback!=null)m_GPSSetCallback.OnClick("", null);
				}
			}
			
			PubVar.m_DoEvent.m_Navigate.refreshNavigationData(location);
		}
	
		@Override
		public void onProviderDisabled(String provider) 
		{
			m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("������");
		}
	
		@Override
		public void onProviderEnabled(String provider) 
		{
			m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("GPS����");
		}

		@Override
		public void onStatusChanged(String provider, int status,Bundle extras) {
		}};

	
	/**
	 * GPS״̬������
	 */
	private boolean m_Sound_HaveSatellite = false;
	private boolean m_Sound_Fix = false;
	private GpsStatus.Listener m_GpsStatusListener = new GpsStatus.Listener() {

		@Override
		public void onGpsStatusChanged(int event) 
		{
			Log.d("GPS״̬", event+"");
			switch(event)
			{
				case GpsStatus.GPS_EVENT_STARTED:
					m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("Music_GPS����");
					break;
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					if (m_LTManager==null) return;
					if (m_LTManager.getGpsStatus(null)==null)return;
					m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("Music_GPS�����¼�");
					int SateCount = 0;
					Iterable<GpsSatellite> GpsSateList = m_LTManager.getGpsStatus(null).getSatellites();
					for(GpsSatellite GpsSate :GpsSateList)SateCount++;
					if (SateCount==0 && m_Sound_HaveSatellite)
					{
						m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("Music_���Ƕ�ʧ");
						m_Sound_HaveSatellite = false;
					}
					if (SateCount>0)m_Sound_HaveSatellite = true;
					
					//��λ����
					if (AlwaysFix())
					{
						if(!m_Sound_Fix)m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("Music_��λ");
						m_Sound_Fix = true;
					} else
					{
						if(m_Sound_Fix)m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("Music_δ��λ");
						m_Sound_Fix=false;
					}
			}
			
			m_GPSMap.UpdateGPSStatus(null);
			PubVar.m_MapControl.invalidate();
		}};
		
	
	//�ر�GPS
	public boolean CloseGPS()
	{
		this.m_LTManager.removeUpdates(this.m_LocateListener);
		this.m_LTManager.removeGpsStatusListener(this.m_GpsStatusListener);
		this.m_LTManager.removeNmeaListener(this.m_NemaListener);
		this.GPS_OpenClose = false;this.m_GPSMap.UpdateGPSStatus(null);
		this.m_LTManager = null;
		return true;
	}
	
	/**
	 * GPS�Ƿ��Ѿ���λ���������ڽ��������������4������λ������15������
	 * @return
	 */
	public boolean AlwaysFix()
	{
		
		if (this.m_LTManager==null || this.m_LocationEx==null) 
		{
			return false;
		}
		
		if (!this.m_NEMALocate.GetUseNEMA())
		{
			//������ڽ����������
			int FixSateCount = 0;
			Iterable<GpsSatellite> GpsSateList = this.m_LTManager.getGpsStatus(null).getSatellites();
			for(GpsSatellite GpsSate :GpsSateList)
			{
				if (GpsSate.usedInFix())FixSateCount++;
			}
			
			if (FixSateCount>=4 && this.m_LocationEx.GetInGpsLocate().hasAccuracy() && this.m_LocationEx.GetInGpsLocate().hasAltitude() &&
				this.m_LocationEx.GetInGpsLocate().getAccuracy()<=15)
			{
				return true;
			}
		} 
		else
		{
			if (this.m_LocationEx.GetGpsFixMode()==lkGpsFixMode.en3DFix) return true;
			
//			int FixSateCount = 0;
//			List<NEMASatellite> nemaSateList = this.m_NEMALocate.GetSatelliteList();
//			for(NEMASatellite GpsSate :nemaSateList)if (GpsSate.getUsedInFix())FixSateCount++;
//			
//			if (this.m_LocationEx.GetGpsFixMode()==lkGpsFixMode.en2DFix && FixSateCount>=5 && this.m_LocationEx.GetGpsPDOP()<=3) return true;
		}
		return false;
	}
	
	/**
	 * ���ݶ�λ���ȼ���ǰ������Ϣǿ�����ֶ�λ����������5��
	 * @return
	 */
	public int GetLevelForAlwaysFix()
	{
		//������ڽ����������
		int FixSateCount = 0;
		if (!this.m_NEMALocate.GetUseNEMA())
		{
			Iterable<GpsSatellite> GpsSateList = this.m_LTManager.getGpsStatus(null).getSatellites();
			for(GpsSatellite GpsSate :GpsSateList)
			{
				if (GpsSate.usedInFix())FixSateCount++;
			}
			
			float jd = this.m_LocationEx.GetInGpsLocate().getAccuracy();

		}
		else
		{
			List<NEMASatellite> nemaSateList = this.m_NEMALocate.GetSatelliteList();
			for(NEMASatellite GpsSate :nemaSateList)
			{
				if (GpsSate.getUsedInFix())FixSateCount++;
			}
		}
		
		if (FixSateCount==0) return 0;
		if (FixSateCount>=1 && FixSateCount<=4) return 1;
		if (FixSateCount>=5 && FixSateCount<=7) return 2;
		if (FixSateCount>=8 && FixSateCount<=11) return 3;
		if (FixSateCount>=12) return 4;
		return 0;
	}
	

	

    /**
     * ��ȡGPSƽ������
     * @return
     */
    public Coordinate getGPSCoordinate()
    {
		return StaticObject.soProjectSystem.WGS84ToXY(this.m_LocationEx.GetGpsLongitude(), this.m_LocationEx.GetGpsLatitude(),this.m_LocationEx.GetGpsAltitude());
    }
    
    /**
     * ��ȡGPS��γ������
     * @return
     */
    public String getJWGPSCoordinate()
    {
    	DecimalFormat df = new DecimalFormat("#.000000");
    	return df.format(this.m_LocationEx.GetGpsLongitude())+","+ df.format(this.m_LocationEx.GetGpsLatitude());
    }
    
    /**
     * ���ظ߳�ֵ
     * @return
     */
    public String getGC()
    {
    	DecimalFormat df = new DecimalFormat("#.0");
    	return df.format(this.m_LocationEx.GetGpsAltitude());
    }
    
    /**
     * ��λ����
     * @return
     */
    public String getAccuracy()
    {
    	if (this.m_NEMALocate.GetUseNEMA())
    	{
    		return this.m_LocationEx.GetGpsPDOP()+"";
    	}
    	else
    	{
    		if (this.m_LocationEx.GetInGpsLocate()==null) return "0";
    		return this.m_LocationEx.GetInGpsLocate().getAccuracy()+"";
    	}
    }
    
    /**
     * �õ�GPS�ٶ�
     * @return
     */
    public String getGPSSpeed()
    {
    	float _SValue = 0;
    	if (!this.m_NEMALocate.GetUseNEMA())
    	{
    		_SValue=((float)this.m_LocationEx.GetGpsSpeed())*3.6f; //meters/s->km/h
    	} 
    	else
    	{
    		_SValue=(float)this.m_LocationEx.GetGpsSpeed();
    	}
		DecimalFormat df = new DecimalFormat("0.0");
		return df.format(_SValue).toString();
    }
    
    //�õ�GPSʱ��
    public String getGPSDate()
    {
    	if (!this.m_NEMALocate.GetUseNEMA())
    	{
	    	Date DT = new Date(this.m_LocationEx.GetInGpsLocate().getTime()+24*60*60);
	    	SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	return SDF.format(DT);
    	}
    	else
    	{
    		return this.m_LocationEx.GetGpsDate()+" "+this.m_LocationEx.GetGpsTime();
    	}
    }
    
    public String[] getGPSDateForPhotoFormat()
    {
    	String[] Dt = this.getGPSDate().split(" ");
    	Dt[0] = Dt[0].replaceAll("-",":");
    	Dt[1] = Dt[1].replaceAll(":","/1,")+"/1";
    	return Dt;
    }

}
