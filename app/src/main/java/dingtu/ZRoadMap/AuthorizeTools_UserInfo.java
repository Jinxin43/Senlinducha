package dingtu.ZRoadMap;

public class AuthorizeTools_UserInfo {
	public String SYS_SoftCode = ""; // （必填）软件码
	public String SYS_StopDate = ""; // （必填）到期时间，格式=2012-12-1
	public String SYS_UserType = ""; // （必填）用户类型，格式=临时用户(也就是随意安装，无授权码),试用用户，正式用户
	public String OT_UserName = ""; // 用户名
	public String OT_UserUnit = ""; // 使用单位
	public String OT_UserDepartment = ""; // 所属单位
	public String OT_UserDesc = ""; // 用户备注
	public String HardCode = ""; // 硬件识别码
	public String BU_UserType = "";// 用户类型

	public AuthorizeTools_UserInfo(String SYS_SoftCode, String SYS_UserType, String SYS_StopDate, String OT_UserName,
			String OT_UserUnit, String department, String description, String hardcode) {
		this.SYS_SoftCode = SYS_SoftCode;
		this.SYS_StopDate = SYS_StopDate;
		this.SYS_UserType = SYS_UserType;
		this.OT_UserName = OT_UserName;
		this.OT_UserUnit = OT_UserUnit;
		this.OT_UserDepartment = department;
		this.OT_UserDesc = description;
		this.HardCode = hardcode;
	}

	public AuthorizeTools_UserInfo() {
	}
}
