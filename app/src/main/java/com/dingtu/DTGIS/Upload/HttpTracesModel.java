package com.dingtu.DTGIS.Upload;

public class HttpTracesModel {

	private String deviceId;
	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}
	public String getDeviceId()
	{
		return this.deviceId;
	}
	
	public String trackDatasJson;
	public String getTrackDatasJson()
	{
		return this.trackDatasJson;
	}
	
	public void setTrackDatasJson(String datasJson)
	{
		this.trackDatasJson = datasJson;
	}
	
	
}
