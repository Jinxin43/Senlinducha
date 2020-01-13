package lkmap.GPS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dingtu.DTGIS.DataService.LogDB;

import lkmap.Enum.lkGpsFixMode;
import lkmap.Enum.lkGpsLocationType;
import lkmap.Tools.Tools;
import android.location.GpsStatus.NmeaListener;
import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;

public class NEMALocate 
{

	/*GPS的NEMA0813报文格式
	
	GPS定位信息GPGGA（Global Positioning SystemFix Data）
	$GPGGA,063740.998,2234.2551,N,11408.0339,E,1,08,00.9,00053.A,M,-2.1,M,,*7B 
	$GPGGA,<1>,<2>,<3>,<4>,<5>,<6>,<7>,<8>,<9>,M,<10>,M,<11>,<12>*hh<CR><LF> 
	<1> UTC时间，hhmmss（时分秒）格式 
	<2> 纬度ddmm.mmmm（度分）格式（前面的0也将被传输） 
	<3> 纬度半球N（北半球）或S（南半球） 
	<4> 经度dddmm.mmmm（度分）格式（前面的0也将被传输） 
	<5> 经度半球E（东经）或W（西经） 
	<6> GPS状态：0=未定位，1=非差分定位，2=差分定位，6=正在估算 
	<7> 正在使用解算位置的卫星数量（00~12）（前面的0也将被传输） 
	<8> HDOP水平精度因子（0.5~99.9） 
	<9> 海拔高度（-9999.9~99999.9） 
	<10> 地球椭球面相对大地水准面的高度 
	<11> 差分时间（从最近一次接收到差分信号开始的秒数，如果不是差分定位将为空） 
	<12> 差分站ID号0000~1023（前面的0也将被传输，如果不是差分定位将为空）

	$GPRMC,012724.000,A,2234.3157,N,11408.0921,E,0.00,,290108,,,A*71 
	$GPRMC,<1>,<2>,<3>,<4>,<5>,<6>,<7>,<8>,<9>,<10>,<11>,<12>*hh<CR><LF> 
	<1> UTC时间，hhmmss（时分秒）格式 
	<2> 定位状态，A=有效定位，V=无效定位 
	<3> 纬度ddmm.mmmm（度分）格式（前面的0也将被传输） 
	<4> 纬度半球N（北半球）或S（南半球） 
	<5> 经度dddmm.mmmm（度分）格式（前面的0也将被传输） 
	<6> 经度半球E（东经）或W（西经） 
	<7> 地面速率（000.0~999.9节，前面的0也将被传输） 
	<8> 地面航向（000.0~359.9度，以真北为参考基准，前面的0也将被传输） 
	<9> UTC日期，ddmmyy（日月年）格式 
	<10> 磁偏角（000.0~180.0度，前面的0也将被传输） 
	<11> 磁偏角方向，E（东）或W（西） 
	<12> 模式指示（仅NMEA0183 3.00版本输出，A=自主定位，D=差分，E=估算，N=数据无效） 

	当前卫星信息GPGSA（GPS DOP and ActiveSatellites）
	$GPGSA,A,3,06,16,14,22,25,01,30,20,,,,,01.6,00.9,01.3*0D 
	$GPGSA,<1>,<2>,<3>,<3>,,,,,<3>,<3>,<3>,<4>,<5>,<6>,<7><CR><LF> 
	<1>模式 ：M = 手动， A = 自动。 
	<2>定位型式 1 = 未定位， 2 = 二维定位， 3 = 三维定位。 
	<3>PRN 数字：01 至 32 表天空使用中的卫星编号，最多可接收12颗卫星信息。 
	<4> PDOP位置精度因子（0.5~99.9） 
	<5> HDOP水平精度因子（0.5~99.9） 
	<6> VDOP垂直精度因子（0.5~99.9） 
	<7> Checksum.(检查位). 
	
	可见卫星信息GPGSV（GPS Satellites in View）  
	$GPGSV,2,1,08,06,26,075,44,16,50,227,47,14,57,097,44,22,17,169,41*70 
	$GPGSV,2,2,08,25,49,352,45,01,64,006,45,30,13,039,39,20,15,312,34*7A 
	$GPGSV,<1>,<2>,<3>,<4>,<5>,<6>,<7>,?<4>,<5>,<6>,<7>,<8><CR><LF> 
	<1> GSV语句的总数 
	<2> 本句GSV的编号 
	<3> 可见卫星的总数，00 至 12。 
	<4> 卫星编号， 01 至 32。 
	<5>卫星仰角， 00 至 90 度。 
	<6>卫星方位角， 000 至 359 度。实际值。 
	<7>讯号噪声比（C/No）， 00 至 99 dB；无表未接收到讯号。 
	<8>Checksum.(检查位). 
	第<4>,<5>,<6>,<7>项个别卫星会重复出现，每行最多有四颗卫星。其余卫星信息会于次一行出现，若未使用，这些字段会空白。

	*/
	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback callback)
	{
		this.m_Callback = callback;
	}
	
	//是否有北斗的卫星
	private boolean m_HaveBD = false;
	public boolean GetHaveBD(){return this.m_HaveBD;}

	
	//卫星列表
	private List<NEMASatellite> m_SatelliteList = new ArrayList<NEMASatellite>();
	
	/**
	 * 返回卫星列表
	 * @return
	 */
	public List<NEMASatellite> GetSatelliteList(){return this.m_SatelliteList;}
	
	//清理无用的卫星信息
	private void PurgeUselessSatellite(String OID)
	{
		int SateCount = this.m_SatelliteList.size();
		for(int i=SateCount-1;i>=0;i--)
		{
			if (!this.m_SatelliteList.get(i).GetOID().equals(OID))this.m_SatelliteList.remove(i);
		}
	}
	
	
	//更新卫星信息
	private void UpdateSatellite(NEMASatellite Sate)
	{
		//判断是否存在此卫星
		boolean Exist = false;
		for(NEMASatellite ST:this.m_SatelliteList)
		{
			if (ST.getPrn().equals(Sate.getPrn()))
			{
				ST.SetOID(Sate.GetOID());
				ST.setAzimuth(Sate.getAzimuth());
				ST.setPrn(Sate.getPrn());
				ST.setSnr(Sate.getSnr());
				ST.setElevation(Sate.getElevation());
				Exist = true;
			}
		}
		if (!Exist)this.m_SatelliteList.add(Sate);
	}
	
	//更新卫星的可用状态
	public void UpdateSatelliteInFix(String PRN,String OID)
	{
		boolean Exist = false;
		for(NEMASatellite ST:this.m_SatelliteList)
		{
			if (ST.getPrn().equals(PRN))
			{
				ST.SetOID(OID);
				ST.setUsedInFix(true);
				Exist = true;
			}
		}
		if (!Exist)
		{
			NEMASatellite ST = new NEMASatellite();
			ST.SetOID(OID);
			ST.setUsedInFix(true);
			ST.setPrn(PRN);
			this.m_SatelliteList.add(ST);
		}
	}
	

    //是否利用NEMA进行位置结算
	private boolean m_UseNMEA = true;
	public boolean GetUseNEMA(){return this.m_UseNMEA;}

	//NEMA原始报文存储器
	private List<String> m_GPSNEMAList = new ArrayList<String>();
	
	private String m_GGAFlag = "";
	
	/**
	 * 设置GPS报文NEMA格式
	 * @param timestamp
	 * @param nmea
	 */
	public void onNmeaReceived(long timestamp, String nmea) 
	{
		Log.d("GPS卫星信息", "时间:"+timestamp+"->"+nmea);
		if (this.m_GGAFlag.equals(""))
		{
			if (nmea.contains("GPGGA"))m_GGAFlag = "GPGGA";
			if (nmea.contains("GNGGA"))m_GGAFlag = "GNGGA";
			return;
		}
		boolean HaveGGA = nmea.contains(m_GGAFlag);
		
		if (HaveGGA)
		{
			if (this.m_GPSNEMAList.size()>0)
			{
				this.m_UseNMEA = true;
				this.CalGPSByNEMA1083(this.m_GPSNEMAList);
				this.m_GPSNEMAList.clear();
				this.m_GPSNEMAList.add(nmea);
				return;
			} 
			else
			{
				this.m_GPSNEMAList.add(nmea);
			}
		}
		if (this.m_GPSNEMAList.size()>0)
		{
			this.m_GPSNEMAList.add(nmea);
		}
	}
		
	//GPS位置
	private LocationEx m_LocationEx = new LocationEx();
	
	//解算GPS卫星信息
	private void CalGPSByNEMA1083(List<String> NEMAStrList)
	{
//		NEMAStrList.clear();
//		NEMAStrList.add("$GPGGA,022830.000,3150.52212,N,11715.73865,E,1,07,,35.6,M,,M,,*5D");
//		NEMAStrList.add("$GPGLL,3150.52212,N,11715.73865,E,022830.000,A,A*5F");
//		NEMAStrList.add("$GPRMC,022830.000,A,3150.52212,N,11715.73865,E,0.480,65,020714,,,A*49");
//		NEMAStrList.add("$GPVTG,65,T,,M,0.480,N,0.933,K,*64");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,23,14,11,147,16,16,64,275,33,20,16,263,14*78");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,33,27,32,181,19,29,16,042,19,31,48,068,20*79");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,32,72,33,231,33,73,32,296,23,75,33,315,28*78");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,072,16*45");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022830.000,3150.52212,N,11715.73865,E,1,07,,35.6,M,,M,,*5D");
//		NEMAStrList.add("$GPGLL,3150.52212,N,11715.73865,E,022830.000,A,A*5F");
//		NEMAStrList.add("$GPRMC,022830.000,A,3150.52212,N,11715.73865,E,0.480,65,020714,,,A*49");
//		NEMAStrList.add("$GPVTG,65,T,,M,0.480,N,0.933,K,*64");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,23,14,11,147,16,16,64,275,33,20,16,263,14*78");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,33,27,32,181,19,29,16,042,19,31,48,068,20*79");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,32,72,33,231,33,73,32,296,23,75,33,315,28*78");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,072,16*45");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022831.000,3150.52220,N,11715.73929,E,1,07,,35.6,M,,M,,*54");
//		NEMAStrList.add("$GPGLL,3150.52220,N,11715.73929,E,022831.000,A,A*56");
//		NEMAStrList.add("$GPRMC,022831.000,A,3150.52220,N,11715.73929,E,0.510,48,020714,,,A*47");
//		NEMAStrList.add("$GPVTG,48,T,,M,0.510,N,0.991,K,*6B");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,18,14,11,147,16,16,64,275,32,20,16,263,16*73");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,33,27,32,181,19,29,16,042,19,31,48,068,20*79");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,32,72,33,231,34,73,32,296,22,75,33,316,28*7D");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,072,16*45");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022831.000,3150.52220,N,11715.73929,E,1,07,,35.6,M,,M,,*54");
//		NEMAStrList.add("$GPGLL,3150.52220,N,11715.73929,E,022831.000,A,A*56");
//		NEMAStrList.add("$GPRMC,022831.000,A,3150.52220,N,11715.73929,E,0.510,48,020714,,,A*47");
//		NEMAStrList.add("$GPVTG,48,T,,M,0.510,N,0.991,K,*6B");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,18,14,11,147,16,16,64,275,32,20,16,263,16*73");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,33,27,32,181,19,29,16,042,19,31,48,068,20*79");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,32,72,33,231,34,73,32,296,22,75,33,316,28*7D");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,072,16*45");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022832.000,3150.52226,N,11715.73945,E,1,07,,35.6,M,,M,,*5B");
//		NEMAStrList.add("$GPGLL,3150.52226,N,11715.73945,E,022832.000,A,A*59");
//		NEMAStrList.add("$GPRMC,022832.000,A,3150.52226,N,11715.73945,E,0.506,63,020714,,,A*46");
//		NEMAStrList.add("$GPVTG,63,T,,M,0.506,N,0.984,K,*61");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,19,14,11,147,16,16,64,275,32,20,16,263,16*72");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,32,27,32,181,19,29,16,042,17,31,48,068,20*76");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,32,72,33,231,35,73,32,296,21,75,33,316,26*71");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,072,14*47");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022833.000,3150.52222,N,11715.73946,E,1,07,,35.6,M,,M,,*5D");
//		NEMAStrList.add("$GPGLL,3150.52222,N,11715.73946,E,022833.000,A,A*5F");
//		NEMAStrList.add("$GPRMC,022833.000,A,3150.52222,N,11715.73946,E,0.136,71,020714,,,A*44");
//		NEMAStrList.add("$GPVTG,71,T,,M,0.136,N,0.265,K,*61");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,20,14,11,147,16,16,64,275,32,20,16,263,18*76");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,32,27,32,181,18,29,16,042,17,31,48,068,17*73");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,32,72,33,231,36,73,32,296,22,75,33,316,27*70");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,072,14*47");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022833.000,3150.52222,N,11715.73946,E,1,07,,35.6,M,,M,,*5D");
//		NEMAStrList.add("$GPGLL,3150.52222,N,11715.73946,E,022833.000,A,A*5F");
//		NEMAStrList.add("$GPRMC,022833.000,A,3150.52222,N,11715.73946,E,0.136,71,020714,,,A*44");
//		NEMAStrList.add("$GPVTG,71,T,,M,0.136,N,0.265,K,*61");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,21,14,11,147,16,16,64,275,33,20,16,263,18*76");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,33,27,32,181,18,29,16,042,17,31,48,068,19*7C");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,32,72,33,231,36,73,32,296,22,75,33,316,28*7F");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,072,14*47");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022835.000,3150.52204,N,11715.73955,E,1,07,,35.4,M,,M,,*5F");
//		NEMAStrList.add("$GPGLL,3150.52204,N,11715.73955,E,022835.000,A,A*5F");
//		NEMAStrList.add("$GPRMC,022835.000,A,3150.52204,N,11715.73955,E,0.542,56,020714,,,A*46");
//		NEMAStrList.add("$GPVTG,56,T,,M,0.542,N,1.053,K,*65");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,20,14,11,147,16,16,64,275,33,20,16,263,18*77");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,33,27,32,181,19,29,16,042,18,31,48,068,19*72");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,32,72,33,231,33,73,32,296,22,75,33,316,29*7B");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,072,15*46");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022836.000,3150.52201,N,11715.73916,E,1,07,,35.3,M,,M,,*59");
//		NEMAStrList.add("$GPGLL,3150.52201,N,11715.73916,E,022836.000,A,A*5E");
//		NEMAStrList.add("$GPRMC,022836.000,A,3150.52201,N,11715.73916,E,0.190,335,020714,,,A*7A");
//		NEMAStrList.add("$GPVTG,335,T,,M,0.190,N,0.369,K,*53");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,20,14,11,147,17,16,64,275,33,20,16,263,17*79");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,34,27,32,181,21,29,16,042,18,31,48,068,18*7F");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,33,72,33,231,34,73,32,296,22,75,33,316,30*75");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,072,15*46");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022837.000,3150.52197,N,11715.73882,E,1,07,,35.3,M,,M,,*58");
//		NEMAStrList.add("$GPGLL,3150.52197,N,11715.73882,E,022837.000,A,A*5F");
//		NEMAStrList.add("$GPRMC,022837.000,A,3150.52197,N,11715.73882,E,0.187,269,020714,,,A*75");
//		NEMAStrList.add("$GPVTG,269,T,,M,0.187,N,0.364,K,*50");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,19,14,11,147,17,16,64,275,34,20,16,263,18*7B");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,35,27,32,181,22,29,16,042,18,31,48,068,18*7D");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,33,72,33,231,32,73,32,296,17,75,33,316,31*74");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,073,16*44");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022838.000,3150.52192,N,11715.73852,E,1,07,,35.3,M,,M,,*5F");
//		NEMAStrList.add("$GPGLL,3150.52192,N,11715.73852,E,022838.000,A,A*58");
//		NEMAStrList.add("$GPRMC,022838.000,A,3150.52192,N,11715.73852,E,0.524,256,020714,,,A*73");
//		NEMAStrList.add("$GPVTG,256,T,,M,0.524,N,1.017,K,*57");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,22,14,11,147,16,16,64,275,34,20,16,263,19*73");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,36,27,32,181,20,29,16,042,15,31,48,068,15*7C");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,33,72,33,231,33,73,32,296,19,75,33,316,32*78");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,073,16*44");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022838.000,3150.52192,N,11715.73852,E,1,07,,35.3,M,,M,,*5F");
//		NEMAStrList.add("$GPGLL,3150.52192,N,11715.73852,E,022838.000,A,A*58");
//		NEMAStrList.add("$GPRMC,022838.000,A,3150.52192,N,11715.73852,E,0.524,256,020714,,,A*73");
//		NEMAStrList.add("$GPVTG,256,T,,M,0.524,N,1.017,K,*57");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,22,14,11,147,16,16,64,275,34,20,16,263,19*73");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,36,27,32,181,20,29,16,042,15,31,48,068,15*7C");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,33,72,33,231,33,73,32,296,19,75,33,316,32*78");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,073,16*44");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022839.000,3150.52183,N,11715.73856,E,1,07,,35.4,M,,M,,*5D");
//		NEMAStrList.add("$GPGLL,3150.52183,N,11715.73856,E,022839.000,A,A*5D");
//		NEMAStrList.add("$GPRMC,022839.000,A,3150.52183,N,11715.73856,E,0.460,242,020714,,,A*72");
//		NEMAStrList.add("$GPVTG,242,T,,M,0.460,N,0.893,K,*56");
//		NEMAStrList.add("$GPGSV,4,1,13,13,07,321,20,14,11,147,16,16,64,275,34,20,16,263,19*71");
//		NEMAStrList.add("$GPGSV,4,2,13,23,34,314,37,27,32,181,20,29,16,042,15,31,48,068,15*7D");
//		NEMAStrList.add("$GPGSV,4,3,13,32,21,224,34,72,33,231,34,73,32,296,21,75,33,316,32*73");
//		NEMAStrList.add("$GPGSV,4,4,13,81,14,073,16*44");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022840.000,3150.52180,N,11715.73936,E,1,07,,35.5,M,,M,,*56");
//		NEMAStrList.add("$GPGLL,3150.52180,N,11715.73936,E,022840.000,A,A*57");
//		NEMAStrList.add("$GPRMC,022840.000,A,3150.52180,N,11715.73936,E,0.395,71,020714,,,A*47");
//		NEMAStrList.add("$GPVTG,71,T,,M,0.395,N,0.768,K,*62");
//		NEMAStrList.add("$GPGSV,3,1,12,13,07,321,21,14,11,147,15,16,64,275,34,20,16,263,18*74");
//		NEMAStrList.add("$GPGSV,3,2,12,23,34,314,36,29,16,042,15,31,48,068,15,32,21,224,31*70");
//		NEMAStrList.add("$GPGSV,3,3,12,72,33,231,34,73,32,296,25,75,33,316,30,81,14,073,17*7C");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022840.000,3150.52180,N,11715.73936,E,1,07,,35.5,M,,M,,*56");
//		NEMAStrList.add("$GPGLL,3150.52180,N,11715.73936,E,022840.000,A,A*57");
//		NEMAStrList.add("$GPRMC,022840.000,A,3150.52180,N,11715.73936,E,0.395,71,020714,,,A*47");
//		NEMAStrList.add("$GPVTG,71,T,,M,0.395,N,0.768,K,*62");
//		NEMAStrList.add("$GPGSV,3,1,12,13,07,321,21,14,11,147,15,16,64,275,34,20,16,263,18*74");
//		NEMAStrList.add("$GPGSV,3,2,12,23,34,314,36,29,16,042,15,31,48,068,15,32,21,224,31*70");
//		NEMAStrList.add("$GPGSV,3,3,12,72,33,231,34,73,32,296,25,75,33,316,30,81,14,073,17*7C");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");
//		NEMAStrList.add("$GPGGA,022841.000,3150.52179,N,11715.73981,E,1,07,,35.6,M,,M,,*5E");
//		NEMAStrList.add("$GPGLL,3150.52179,N,11715.73981,E,022841.000,A,A*5C");
//		NEMAStrList.add("$GPRMC,022841.000,A,3150.52179,N,11715.73981,E,0.057,222,020714,,,A*75");
//		NEMAStrList.add("$GPVTG,222,T,,M,0.057,N,0.110,K,*52");
//		NEMAStrList.add("$GPGSV,3,1,12,13,07,321,21,14,11,147,15,16,64,275,33,20,16,263,18*73");
//		NEMAStrList.add("$GPGSV,3,2,12,23,34,314,35,29,16,042,15,31,48,068,17,32,21,224,32*72");
//		NEMAStrList.add("$GPGSV,3,3,12,72,33,231,35,73,32,296,25,75,33,316,30,81,14,073,18*72");
//		NEMAStrList.add("$GPGSA,,,14,16,20,23,29,31,32,,,,,,,,,,,,,,,,*67");



		
		String NEAMOID = UUID.randomUUID().toString();
		
		for(String NEMAStr:NEMAStrList)
		{
			//*作为一行NEMA结束的标志
			int xPos=NEMAStr.indexOf("*");
			if (xPos<0)continue;
			
			//提取有效的NEMA串
			String NStr = NEMAStr.substring(0, xPos);
			String[] NEMAStrT = NStr.split(",");
			if (NEMAStrT.length==0) continue;
			
			//确定类型
			String NEMAType = NEMAStrT[0].toUpperCase();
			
			if (NEMAType.equals("$GPGGA")||NEMAType.equals("$GNGGA"))  //2,3,4,5,9
			{
				if (NEMAStrT.length>=10)
				{
					this.m_LocationEx.SetGpsLatitudeStr(NEMAStrT[2]);
					this.m_LocationEx.SetGpsLatitudeType(NEMAStrT[3]);
					this.m_LocationEx.SetGpsLongitudeStr(NEMAStrT[4]);
					this.m_LocationEx.SetGpsLongitudeType(NEMAStrT[5]);
					
					String FixMode = "1";
					if (NEMAStrT[6].equals("1") || NEMAStrT[6].equals("2"))  //1=非差分定位，2=差分定位
					{
						if (this.m_LocationEx.GetGpsPDOP()<=3)FixMode="3";
					}
					this.m_LocationEx.SetGpsFixMode(FixMode);   //1-未定位，2-2D定位，3-3D定位
					
					this.m_LocationEx.SetGpsAltitude(Tools.ConvertToDouble(NEMAStrT[9]));
				}
			}
			
			if (NEMAType.equals("$GPRMC")||NEMAType.equals("$GNRMC"))  //1,7,8,9
			{
				if (NEMAStrT.length>=10)
				{
					this.m_LocationEx.SetGpsTime(NEMAStrT[1]);
					this.m_LocationEx.SetGpsSpeed(Tools.ConvertToDouble(NEMAStrT[7])*1.852);
					this.m_LocationEx.SetGpsLandDirection(NEMAStrT[8]);
					this.m_LocationEx.SetGpsDate(NEMAStrT[9]);
				}
			}
			
			if (NEMAType.equals("$GPGSA")||NEMAType.equals("$GNGSA"))  //2,4
			{
				if (NEMAStrT.length>=4)
				{
					if (NEMAStrT[2].equals("3"))this.m_LocationEx.SetGpsFixMode(NEMAStrT[2]);     //3D定位，慢
				}
				if (NEMAStrT.length==18)
				{
					int PdopIndex = NEMAStrT.length-3;
					if (PdopIndex>=0 && PdopIndex<NEMAStrT.length)
					{
						this.m_LocationEx.SetGpsPDOP(Tools.ConvertToDouble(NEMAStrT[PdopIndex]));
					}
				} else
				{
					this.m_LocationEx.SetGpsPDOP(99.99);
				}
			}
			
			
			if (NEMAType.equals("$GPGSA") || NEMAType.equals("$BDGSA")|| NEMAType.equals("$GNGSA")|| NEMAType.equals("$GLGSA"))
			{
				for(int i=3;i<=NEMAStrT.length-4;i++)
				{
					if (NEMAStrT[i].equals(""))continue;

					int PRN =(int) Double.parseDouble(NEMAStrT[i]);  //PRN 码（伪随机噪声码）（01 - 32）（前导位数不足则补0）
					String PRNBeforeStr = "G";
					if (NEMAType.equals("$BDGSA")){this.m_HaveBD = true;PRNBeforeStr = "B";}
					if (NEMAType.equals("$GPGSA"))PRNBeforeStr = "G";
					if (NEMAType.equals("$GNGSA"))PRNBeforeStr = "R";
					if (NEMAType.equals("$GLGSA"))PRNBeforeStr = "R";
					this.UpdateSatelliteInFix(PRNBeforeStr+PRN,NEAMOID);
				}
			}
			
			if (NEMAType.equals("$GPGSV") || NEMAType.equals("$BDGSV")|| NEMAType.equals("$GLGSV")|| NEMAType.equals("$GNGSV"))
			{
				//可见卫星总数
				int SatCount = Integer.parseInt(NEMAStrT[3]);
				
				//报文条数
				int InfoBarCount = Integer.parseInt(NEMAStrT[1]);
				
				//报文序列
				int InfoBarIndex = Integer.parseInt(NEMAStrT[2]);
				
				if (SatCount>0)
				{
					for(int i=4;i<=NEMAStrT.length-1;i+=4)
					{
						if (NEMAStrT[i].equals("")) continue;
						int PRN = Integer.parseInt(NEMAStrT[i]);  //PRN 码（伪随机噪声码）（01 - 32）（前导位数不足则补0）
						
						int Angle1 = 90;
						if ((i+1)>=NEMAStrT.length)continue;
						if (!NEMAStrT[i+1].equals(""))Angle1 = (int)Tools.ConvertToDouble((NEMAStrT[i+1]));   //卫星仰角（00 - 90）度（前导位数不足则补0）
						
						int Angle2 = 0;
						if ((i+2)>=NEMAStrT.length)continue;
						if (!NEMAStrT[i+2].equals(""))Angle2 = (int)Tools.ConvertToDouble((NEMAStrT[i+2]));   //卫星方位角（00 - 359）度（前导位数不足则补0）
						
						int Snr = 0;
						if ((i+3)>=NEMAStrT.length)continue;
						if (!NEMAStrT[i+3].equals(""))Snr = (int)Tools.ConvertToDouble((NEMAStrT[i+3]));   //信噪比（00－99）dbHz
						//if (Snr==0) continue;

						String PRNBeforeStr = "G";
						if (NEMAType.equals("$BDGSV")){this.m_HaveBD = true;PRNBeforeStr = "B";}
						if (NEMAType.equals("$GPGSV"))PRNBeforeStr = "G";
						if (NEMAType.equals("$GLGSV"))PRNBeforeStr = "R";
						if (NEMAType.equals("$GNGSV"))PRNBeforeStr = "R";
						NEMASatellite Sat = new NEMASatellite();
						Sat.SetOID(NEAMOID);
						Sat.setPrn(PRNBeforeStr+PRN);
						Sat.setSnr(Snr+"");
						Sat.setAzimuth(Angle2+"");
						Sat.setElevation(Angle1+"");
						this.UpdateSatellite(Sat);
					}
				}
			}
		}
		
		//清理无用的卫星信息
		this.PurgeUselessSatellite(NEAMOID);
		this.m_LocationEx.SetType(lkGpsLocationType.enNEMA);
		if(PubVar.recordGPS)
		{
			if(m_LocationEx.GetGpsFixMode()==lkGpsFixMode.en3DFix)
			{
				LogDB logDB = new LogDB();
				logDB.logGps(m_LocationEx.GetGpsDate().substring(0,4),m_LocationEx.GetGpsDate().replace("-", ""), m_LocationEx);
			}
		}
		
		
		if (this.m_Callback!=null)m_Callback.OnClick("", this.m_LocationEx);
	}
	
	

}
