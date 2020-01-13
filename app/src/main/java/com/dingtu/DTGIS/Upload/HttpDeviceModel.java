package com.dingtu.DTGIS.Upload;

public class HttpDeviceModel {
	
	  private String fromDeviceId;//softwareCode
	  private String name;//userName
	  private String type;//用户类型
	  private String department;//上级单位
	  private String userunit;//所属单位
	  private String hardcode;//IMEI,
	  private String stopdate;//到期日期
	  
	  public String getFromDeviceId()
	  {
		  return fromDeviceId;
	  }
	  public void setFromDeviceId(String fromDeviceId)
	  {
		  this.fromDeviceId = fromDeviceId;
	  }
	  
	  public String getName()
	  {
		  return this.name;
	  }
	  public void setName(String userName)
	  {
		  this.name = userName;
	  }
	  
	  public String getType()
	  {
		  return this.type;
	  }
	  public void setType(String type)
	  {
		  this.type = type;
	  }
	  
	  public String getDepartment()
	  {
		  return this.department;
	  }
	  public void setDepartment(String department)
	  {
		  this.department = department;
	  }
	  
	  public String getUserunit()
	  {
		  return this.userunit;
	  }
	  public void setUserunit(String userunit)
	  {
		  this.userunit = userunit;
	  }
	  
	  public String getHardcode()
	  {
		  return this.hardcode;
	  }
	  public void setHardcode(String hardcode)
	  {
		  this.hardcode = hardcode;
	  }
	  
	  public String getStopdate()
	  {
		  return this.stopdate;
	  }
	  public void setStopdate(String stopdate)
	  {
		  this.stopdate = stopdate;
	  }

}
