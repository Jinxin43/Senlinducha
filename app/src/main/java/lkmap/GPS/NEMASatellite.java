package lkmap.GPS;

import android.location.GpsSatellite;

public class NEMASatellite 
{
	private String _OID = "";
	public void SetOID(String oid){this._OID=oid;}
	public String GetOID(){return this._OID;}
	
	GpsSatellite GpsSate;
	
	//���Ǳ��
	private String _Prn = "";
	public void setPrn(String Prn) {this._Prn = Prn;}
	public String getPrn(){return this._Prn;}
	
	//�����
	private String _Snr = "0";
	public void setSnr(String Snr) {this._Snr = Snr;}
	public String getSnr(){return this._Snr;}
	
	//��λ��
	private String _Azimuth = "0";
	public void setAzimuth(String Azimuth) {this._Azimuth = Azimuth;}
	public String getAzimuth(){return this._Azimuth;}
	
	//�߶Ƚ�
	private String _Elevation = "90";
	public void setElevation(String Elevation) {this._Elevation = Elevation;}
	public String getElevation(){return this._Elevation;}
	
	//�Ƿ���ڽ���
	private boolean _InFix = false;
	public void setUsedInFix(boolean InFix) {this._InFix = InFix;}
	public boolean getUsedInFix(){return this._InFix;}
	
}
