package dingtu.ZRoadMap;

public class AuthorizeTools_UserInfo {
	public String SYS_SoftCode = ""; // ����������
	public String SYS_StopDate = ""; // ���������ʱ�䣬��ʽ=2012-12-1
	public String SYS_UserType = ""; // ������û����ͣ���ʽ=��ʱ�û�(Ҳ�������ⰲװ������Ȩ��),�����û�����ʽ�û�
	public String OT_UserName = ""; // �û���
	public String OT_UserUnit = ""; // ʹ�õ�λ
	public String OT_UserDepartment = ""; // ������λ
	public String OT_UserDesc = ""; // �û���ע
	public String HardCode = ""; // Ӳ��ʶ����
	public String BU_UserType = "";// �û�����

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
