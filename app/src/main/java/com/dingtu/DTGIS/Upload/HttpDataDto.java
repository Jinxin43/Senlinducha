package com.dingtu.DTGIS.Upload;

public class HttpDataDto {

	String layerId;
	String dataJson;
	String srid = "2343";
	
	public String getLayerId()
	{
		return layerId;
	}
	public void setLayerId(String layerId)
	{
		this.layerId = layerId;
	}
	
	public String getDataJson()
	{
		return this.dataJson;
	}
	public void setDataJson(String dataJson)
	{
		this.dataJson = dataJson;
	}
	
	public String getSrid()
	{
		return srid;
	}
	
	public void setSrid(String srid)
	{
		this.srid = srid;
	}
}
