package lkmap.GPS;

import lkmap.Enum.lkGpsFixMode;
import lkmap.Enum.lkGpsLocationType;
import lkmap.Tools.Tools;
import android.location.Location;

public class LocationEx 
{
	//λ�����ͣ�����GPS��NEMA
	private lkGpsLocationType m_GpsLocationType = lkGpsLocationType.enInGps;
	public void SetType(lkGpsLocationType gpsLocationType)
	{
		this.m_GpsLocationType = gpsLocationType;
	}
	public lkGpsLocationType GetGpsLocationType(){return this.m_GpsLocationType;}
	
	//����GPS�������õ�λ��ʵ��
	private Location m_InGpsLocation = null;
	public void SetInGpsLocate(Location location)
	{
		this.m_InGpsLocation = location;
	}
	public Location GetInGpsLocate(){return this.m_InGpsLocation;}
	
	//GPSʱ��UTC��ʽ
	private String m_GpsTime = "";
	public String GetGpsTime(){return this.m_GpsTime;}
	public void SetGpsTime(String gpsTime)
	{
		//UTCʱ�䣬hhmmss��ʱ���룩��ʽ 
		if (gpsTime.length()>=6)
		{
			String HHStr  = gpsTime.substring(0,2);
			String MMStr  = gpsTime.substring(2,4);
			String SSStr  = gpsTime.substring(4,6);
			if (Tools.IsInteger(HHStr) && Tools.IsInteger(MMStr) && Tools.IsInteger(SSStr))
			{
				this.m_GpsTime = (Integer.parseInt(HHStr)+8)+":"+MMStr+":"+SSStr;
			}
		} 
		else
		{
			this.m_GpsTime="00:00:00";
		}
	}
	public void SetGpsTime(long gpsTime)
	{
		
	}
	
	//GPS����UTC��ʽ
	private String m_GpsDate = "";
	public String GetGpsDate(){return this.m_GpsDate;}
	public void SetGpsDate(String gpsDate)
	{
		//UTC���ڣ�ddmmyy�������꣩��ʽ 
		if (gpsDate.length()==6)
		{
			String DDStr  = gpsDate.substring(0,2);
			String MMStr  = gpsDate.substring(2,4);
			String YYStr  = gpsDate.substring(4,6);
			if (Tools.IsInteger(YYStr) && Tools.IsInteger(MMStr) && Tools.IsInteger(DDStr))
			{
				gpsDate = (Integer.parseInt(YYStr)+2000)+"-"+MMStr+"-"+DDStr;
				this.m_GpsDate = gpsDate;
			}
		}
	}
	
	//GPS����
	private double m_GpsLongitude = 0;
	public double GetGpsLongitude()
	{
		return this.m_GpsLongitude;
	}
	public void SetGpsLongitude(double gpsLongitude)
	{
		this.m_GpsLongitude = gpsLongitude;
	}
	public void SetGpsLongitudeStr(String gpsLongitudeStr)
	{
		this.m_GpsLongitude = this.ToJWD(gpsLongitudeStr, 2);
	}
	
	//GPs�������ͣ����Ȱ���E����������W�������� 
	private String m_GpsLongitudeType = "N";
	public void SetGpsLongitudeType(String gpsLongitudeType){this.m_GpsLongitudeType = gpsLongitudeType;}
	public String GetGpsLongitudeType(){return this.m_GpsLongitudeType;}
	

	//GPSγ ��
	private double m_GpsLatitude = 0;
	public double GetGpsLatitude()
	{
		return this.m_GpsLatitude;
	}
	public void SetGpsLatitude(double gpsLatitude)
	{
		this.m_GpsLatitude = gpsLatitude;
	}
	public void SetGpsLatitudeStr(String gpsLatitudeStr)
	{

		this.m_GpsLatitude = this.ToJWD(gpsLatitudeStr, 1);
	}
	
	//GPsγ �����ͣ�γ�Ȱ���N�������򣩻�S���ϰ��� 
	private String m_GpsLatitudeType = "N";
	public void SetGpsLatitudeType(String gpsLatitudeType){this.m_GpsLatitudeType = m_GpsLatitudeType;}
	public String GetGpsLatitudeType(){return this.m_GpsLatitudeType;}
	
	//GPS���θ߶�
	private double m_GpsAltitude =0;
	public double GetGpsAltitude()
	{
		return this.m_GpsAltitude;
	}
	public void SetGpsAltitude(double gpsAltitude)
	{
		this.m_GpsAltitude = gpsAltitude;
	}
	
	//��λģʽ��1 = δ��λ�� 2 = ��ά��λ�� 3 = ��ά��λ�� 
	private lkGpsFixMode m_GpsFixMode = lkGpsFixMode.enNoFix;
	public lkGpsFixMode GetGpsFixMode()
	{
		return this.m_GpsFixMode;
	}
	public void SetGpsFixMode(String gpsFixMode)
	{
		if (gpsFixMode.trim().equals("1"))this.m_GpsFixMode = lkGpsFixMode.enNoFix;
		if (gpsFixMode.trim().equals("2"))this.m_GpsFixMode = lkGpsFixMode.en2DFix;
		if (gpsFixMode.trim().equals("3"))this.m_GpsFixMode = lkGpsFixMode.en3DFix;
	}
	
	//PDOP
	private double m_GpsPDOP = 99.99;
	public double GetGpsPDOP()
	{
		return this.m_GpsPDOP;
	}
	public void SetGpsPDOP(double gpsPDOP)
	{
		this.m_GpsPDOP = gpsPDOP;
	}
	
	//�����ٶ�
	private double m_GpsSpeed = 0;
	public double GetGpsSpeed()
	{
		return this.m_GpsSpeed;
	}
	public void SetGpsSpeed(double gpsSpeed)
	{
		this.m_GpsSpeed = gpsSpeed;
	}

	//���溽��
	private String m_GpsLandDirection = "";
	public String GetGpsLandDirection()
	{
		return this.m_GpsLandDirection;
	}
	public void SetGpsLandDirection(String gpsLandDirection)
	{
		this.m_GpsLandDirection = gpsLandDirection;
	}
	
	
	//���ַ���ת��Ϊ��γ�ȣ�1-γ�ȣ�2-����
	private double ToJWD(String JWDStr,int Type)
	{
		int BeforeLen = 4,DDLen = 2;
		if (Type==1){BeforeLen = 4;DDLen=2;}
		if (Type==2){BeforeLen = 5;DDLen=3;}
		//γ��ddmm.mmmm���ȷ֣���ʽ��ǰ���0Ҳ�������䣩
		if (Tools.ConvertToDouble(JWDStr)>1)
		{
			String fStr = JWDStr.substring(0,JWDStr.indexOf("."));
			if (fStr.length()==BeforeLen)
			{
				String DD  = JWDStr.substring(0,DDLen);
				String MM = JWDStr.substring(DDLen,JWDStr.length());
				return Tools.ConvertToDouble(DD)+Tools.ConvertToDouble(MM)/60;
			}
		}
		return 0;
	}
}
