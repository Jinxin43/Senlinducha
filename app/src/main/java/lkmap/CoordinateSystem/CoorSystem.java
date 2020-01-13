package lkmap.CoordinateSystem;

import lkmap.Enum.lkCoorTransMethod;

public class CoorSystem 
{
	//����ϵͳ����
	private String _Name = "";
	public void SetName(String name){this._Name = name;}
	public String GetName(){return this._Name;}
	
	//���뾭�ߣ�����ΪС��
	private float _CenterMeridian = 117;
	public void SetCenterMeridian(float cm){this._CenterMeridian = cm;}
	public float GetCenterMeridian(){return this._CenterMeridian;}
	
	//�ִ�����
	private int _DH=0;
	public void SetDH(int fendaihao)
	{
		_DH = fendaihao;
	}
	public int GetDH()
	{
		return _DH;
	}
	
	private int _FenDai=3;
	public void setFenDai(int fendai)
	{
		_FenDai = fendai;
	}
	
	public int getFenDai()
	{
		return _FenDai;
	}
	
	//����
	private double _A = 0;
	public void SetA(double a){this._A = a;}
	public double GetA(){return this._A;}
	
	//����
	private double _B = 0;
	public void SetB(double b){this._B = b;}
	public double GetB(){return this._B;}
	
	//1/E*E
	public double GetE()
	{
		return (this.GetA()) / (this.GetA() - this.GetB());
	}
	
	//��ƫ��
	private double _Easting = 0;
	public void SetEasting(double east){this._Easting = east;}
	public double GetEasting(){return this._Easting;}
	
	//����ת������
	private lkCoorTransMethod _CoorTransMethod = lkCoorTransMethod.enNull;
	private String _CoorTransMethodName = "��";
	public void SetCoorTransMethodName(String MethodName)
	{
		this._CoorTransMethodName = MethodName;
		if (MethodName.equals("")){this._CoorTransMethodName="��"; this.SetCoorTransMethod(lkCoorTransMethod.enNull);}
		if (MethodName.equals("����ת��")) this.SetCoorTransMethod(lkCoorTransMethod.enThreePara);
		if (MethodName.equals("�Ĳ�ת��")) this.SetCoorTransMethod(lkCoorTransMethod.enFourPara);
		if (MethodName.equals("�߲�ת��")) this.SetCoorTransMethod(lkCoorTransMethod.enServenPara);
	}
	public void SetCoorTransMethod(lkCoorTransMethod _coorTransMethod){this._CoorTransMethod = _coorTransMethod;}
	public lkCoorTransMethod GetCoorTransMethod(){return this._CoorTransMethod;}
	public String GetCoorTransMethodName(){return this._CoorTransMethodName;}
	
	//ƽ��ת������
	private lkCoorTransMethod _PMTransMethod = lkCoorTransMethod.enNull;
	private String _PMTransMethodName = "��";
	public void SetPMTransMethodName(String MethodName)
	{
		this._PMTransMethodName = MethodName;
		if (MethodName.equals("")){this._PMTransMethodName="��"; _PMTransMethod = lkCoorTransMethod.enNull;}
		if (MethodName.equals("�Ĳ�ת��")) _PMTransMethod = lkCoorTransMethod.enFourPara;
	}
	public String GetPMTransMethodName(){return this._PMTransMethodName;}
	public lkCoorTransMethod GetPMTransMethod(){return this._PMTransMethod;}
	
	//ת������
	private double _P31 = 0,_P32=0,_P33=0,_P41=0,_P42=0,_P43=0,_P44=0,_P71=0,_P72=0,_P73=0,_P74=0,_P75=0,_P76=0,_P77=0;
	
	//����
	public void SetTransToP31(String _Para){this._P31 = this.ToDouble(_Para);}
	public double GetTransToP31(){return this._P31;}
	public void SetTransToP32(String _Para){this._P32 = this.ToDouble(_Para);}
	public double GetTransToP32(){return this._P32;}
	public void SetTransToP33(String _Para){this._P33 = this.ToDouble(_Para);}
	public double GetTransToP33(){return this._P33;}
	public double GetTransToP34(){return GetThreePara_DA(this.GetName());}
	public double GetTransToP35(){return GetThreePara_DF(this.GetName());}
	
	//�Ĳ�
	public void SetTransToP41(String _Para){this._P41 = this.ToDouble(_Para);}
	public double GetTransToP41(){return this._P41;}
	public void SetTransToP42(String _Para){this._P42 = this.ToDouble(_Para);}
	public double GetTransToP42(){return this._P42;}
	public void SetTransToP43(String _Para){this._P43 = this.ToDouble(_Para);}
	public double GetTransToP43(){return this._P43;}
	public void SetTransToP44(String _Para){this._P44 = this.ToDouble(_Para);}
	public double GetTransToP44(){return this._P44;}
	
	boolean isAutoCalc = false;
	//�Ƿ����Զ������Բ���
	public boolean GetIsAutoCalc()
	{
		return isAutoCalc;
	}
	public void SetIsAutoCalc(String isAuto)
	{
		if(isAuto == null)
		{
			isAutoCalc = false;
		}
		else
		{
			if(isAuto.equals("1"))
			{
				isAutoCalc = true;
			}
			else
			{
				isAutoCalc = false;
			}
		}
		
		
	}
	
	//�߲�
	public void SetTransToP71(String _Para){this._P71 = this.ToDouble(_Para);}
	public double GetTransToP71(){return this._P71;}
	public void SetTransToP72(String _Para){this._P72 = this.ToDouble(_Para);}
	public double GetTransToP72(){return this._P72;}
	public void SetTransToP73(String _Para){this._P73 = this.ToDouble(_Para);}
	public double GetTransToP73(){return this._P73;}
	public void SetTransToP74(String _Para){this._P74 = this.ToDouble(_Para);}
	public double GetTransToP74(){return this._P74;}
	public void SetTransToP75(String _Para){this._P75 = this.ToDouble(_Para);}
	public double GetTransToP75(){return this._P75;}
	public void SetTransToP76(String _Para){this._P76 = this.ToDouble(_Para);}
	public double GetTransToP76(){return this._P76;}
	public void SetTransToP77(String _Para){this._P77 = this.ToDouble(_Para);}
	public double GetTransToP77(){return this._P77;}
	
	/**
	 * ת��ΪWGS�������
	 */
	public void ToWGS84()
	{
		this.SetA(6378137.0);
		this.SetB(6356752.314245179);
	}
	
	private double ToDouble(String str)
	{
		if (str.equals("")) return 0;
		else return Double.parseDouble(str);
	}
	
	/**
	 * ���������еĳ���DA
	 * @param CoorSystemName
	 * @return
	 */
	public static double GetThreePara_DA(String CoorSystemName)
	{
		if (CoorSystemName.indexOf("����54")>=0)return -108.0;
		if (CoorSystemName.indexOf("����80")>=0)return -3;
		return 0;
	}
	/**
	 * ���������еĳ���DF
	 * @param CoorSystemName
	 * @return
	 */
	public static double GetThreePara_DF(String CoorSystemName)
	{
		if (CoorSystemName.indexOf("����54")>=0)return 0.0000005;
		if (CoorSystemName.indexOf("����80")>=0)return 0.00000000;
		return 0;
	}
}
