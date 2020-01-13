package lkmap.CoordinateSystem;

import lkmap.Cargeometry.Coordinate;
import lkmap.Enum.lkCoorTransMethod;

public class ProjectSystem {
	// ����ϵͳ����
	private CoorSystem _CoorSystem = null;

	/**
	 * ��������ϵͳ
	 * 
	 * @param coorSystem
	 */
	public void SetCoorSystem(CoorSystem coorSystem) {

		this._CoorSystem = coorSystem;
	}

	/**
	 * ��ȡ����ϵͳ
	 * 
	 * @return
	 */
	public CoorSystem GetCoorSystem() {
		return this._CoorSystem;
	}

	/**
	 * WGS84����ת����ǰ����ϵ��
	 * 
	 * @param JD
	 * @param WD
	 * @return
	 */
	public Coordinate WGS84ToXY(double L1, double B1, double H1) {
		// ֻ��WGS84�����ֱ��ת������������ϵ����Ҫת��
		Coordinate xy = null;
		try {

			if (this._CoorSystem.GetName().equals("WGS-84����")) {
				xy = Project_Web.Web_BLToXY(L1, B1);
				xy.setZ(H1);
				return xy;
			} else {
				// �ռ�ת��
				if (this._CoorSystem.GetCoorTransMethod() == lkCoorTransMethod.enThreePara) {
					// 84��γ��ת84�ռ�����
					CoorSystem CS84 = new CoorSystem();
					CS84.ToWGS84();
					Coordinate XYZ84 = Project_XYZ.XYZ_BLHToXYZ(L1, B1, H1, CS84);

					// ����ת������
					XYZ84.setX(XYZ84.getX() - this._CoorSystem.GetTransToP31());
					XYZ84.setY(XYZ84.getY() - this._CoorSystem.GetTransToP32());
					XYZ84.setZ(XYZ84.getZ() - this._CoorSystem.GetTransToP33());

					// ת��ΪĿ������ϵͳ�Ŀռ�����
					Coordinate BLHTo = Project_XYZ.XYZ_XYZToBLH(XYZ84.getX(), XYZ84.getY(), XYZ84.getZ(),
							this._CoorSystem);
					xy = Project_GK.GK_BLToXY(BLHTo.getX(), BLHTo.getY(), this._CoorSystem);
					if (xy == null) {
						return null;
					}
					xy.setZ(H1);

					if (this._CoorSystem.GetPMTransMethod() == lkCoorTransMethod.enFourPara) {
						xy = this.FourParaChange(xy.getX(), xy.getY(), H1, 2);
					}

					return xy;
				}

				if (this._CoorSystem.GetCoorTransMethod() == lkCoorTransMethod.enServenPara) {
					// 84��γ��ת84�ռ�����
					CoorSystem CS84 = new CoorSystem();
					CS84.ToWGS84();
					Coordinate XYZ84 = Project_XYZ.XYZ_BLHToXYZ(L1, B1, H1, CS84);

					double k = (1 + this._CoorSystem.GetTransToP77() / 1000000); // ��������(ppm->10e-6)
					double a2 = k * this._CoorSystem.GetTransToP74() / 3600 * Math.PI / 180; // X��ת
																								// (��->����)
					double a3 = k * this._CoorSystem.GetTransToP75() / 3600 * Math.PI / 180; // Y��ת
					double a4 = k * this._CoorSystem.GetTransToP76() / 3600 * Math.PI / 180; // Z��ת
					double newX = this._CoorSystem.GetTransToP71() + k * XYZ84.getX() + 0 - a3 * XYZ84.getZ()
							+ a4 * XYZ84.getY();
					double newY = this._CoorSystem.GetTransToP72() + k * XYZ84.getY() + a2 * XYZ84.getZ() + 0
							- a4 * XYZ84.getX();
					double newZ = this._CoorSystem.GetTransToP73() + k * XYZ84.getZ() - a2 * XYZ84.getY()
							+ a3 * XYZ84.getX() + 0;
					XYZ84.setX(newX);
					XYZ84.setY(newY);
					XYZ84.setZ(newZ);

					// ת��ΪĿ������ϵͳ�Ŀռ�����
					Coordinate BLHTo = Project_XYZ.XYZ_XYZToBLH(XYZ84.getX(), XYZ84.getY(), XYZ84.getZ(),
							this._CoorSystem);
					xy = Project_GK.GK_BLToXY(BLHTo.getX(), BLHTo.getY(), this._CoorSystem);
					if (xy == null) {
						return null;
					}
					xy.setZ(H1);

					if (this._CoorSystem.GetPMTransMethod() == lkCoorTransMethod.enFourPara) {
						xy = this.FourParaChange(xy.getX(), xy.getY(), H1, 2);
					}

					return xy;
				}

				// ƽ��ת��
				if (this._CoorSystem.GetPMTransMethod() == lkCoorTransMethod.enFourPara) {
					return this.FourParaChange(L1, B1, H1, 1);
				}

				xy = Project_GK.GK_BLToXY(L1, B1, this._CoorSystem);
				if (xy != null) {
					xy.setZ(H1);
				}

				return xy;
			}
		} catch (Exception ex) {

		}

		return xy;
	}

	/**
	 * �Ĳα任
	 * 
	 * @param CoorX
	 * @param Coor
	 * @param ChangeType
	 *            1-��γ�ȣ�2-ƽ��
	 * @return
	 */
	private Coordinate FourParaChange(double L1, double B1, double H1, int ChangeType) {
		double Dx = this._CoorSystem.GetTransToP41();
		double Dy = this._CoorSystem.GetTransToP42();
		double A = this._CoorSystem.GetTransToP43() / 3600 * Math.PI / 180;
		; // ��ת (��->����)
		double K = this._CoorSystem.GetTransToP44();
		Coordinate xy = new Coordinate(L1, B1);
		if (ChangeType == 1)
			xy = Project_GK.GK_BLToXY(L1, B1, this._CoorSystem);

		if (xy == null) {
			return xy;
		}

		double X0 = Dx + xy.getX() * K * Math.cos(A) - xy.getY() * K * Math.sin(A);
		double Y0 = Dy + xy.getX() * K * Math.sin(A) + xy.getY() * K * Math.cos(A);
		xy.setX(X0);
		xy.setY(Y0);
		xy.setZ(H1);
		return xy;
	}

	/**
	 * ��ǰ����ϵ�������ת��WGS84���꣬ע�⣺�����뾭�߲���ȷ��ʱ�򣬿����޷���ȷ���⾭γ������
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Coordinate XYToWGS84(Coordinate xyCoor) {
		return this.XYToWGS84(xyCoor.getX(), xyCoor.getY(), xyCoor.getZ());
	}

	public Coordinate XYToWGS84(double x, double y, double z) {
		// ֻ��WGS84�����ֱ��ת������������ϵ����Ҫת��
		if (this._CoorSystem.GetName().equals("WGS-84����")) {
			Coordinate lb = Project_Web.Web_XYToBL(x, y);
			lb.setZ(z);
			return lb;
		} else {
			if (this._CoorSystem.GetCoorTransMethod() == lkCoorTransMethod.enThreePara) {
				// ƽ�����귴��Ϊ��γ������
				Coordinate XYToBL = Project_GK.GK_XYToBL(x, y, this._CoorSystem);

				// ����γ������ת���ռ�����
				Coordinate XYZ = Project_XYZ.XYZ_BLHToXYZ(XYToBL.getX(), XYToBL.getY(), z, this._CoorSystem);

				// ����ת������ת��ΪWGS84�ռ�����
				XYZ.setX(XYZ.getX() + this._CoorSystem.GetTransToP31());
				XYZ.setY(XYZ.getY() + this._CoorSystem.GetTransToP32());
				XYZ.setZ(XYZ.getZ() + this._CoorSystem.GetTransToP33());

				// 84�ռ�����ת84��γ��
				CoorSystem CS84 = new CoorSystem();
				CS84.ToWGS84();
				Coordinate lb84 = Project_XYZ.XYZ_XYZToBLH(XYZ.getX(), XYZ.getY(), XYZ.getZ(), CS84);
				return lb84;
			}

			if (this._CoorSystem.GetCoorTransMethod() == lkCoorTransMethod.enServenPara) {
				double k = (1 + this._CoorSystem.GetTransToP77() / 1000000); // ��������
				double a2 = k * this._CoorSystem.GetTransToP74() / 3600 * Math.PI / 180; // X��ת
				double a3 = k * this._CoorSystem.GetTransToP75() / 3600 * Math.PI / 180; // Y��ת
				double a4 = k * this._CoorSystem.GetTransToP76() / 3600 * Math.PI / 180; // Z��ת

				// 1����ƽ������ת��Ϊ��γ��
				Coordinate lb = Project_GK.GK_XYToBL(x, y, this._CoorSystem);

				// 2������γ�Ȼ���Ϊ�ռ�����
				Coordinate XZY = Project_XYZ.XYZ_BLHToXYZ(lb.getX(), lb.getY(), z, this._CoorSystem);

				// double k = (1+this._CoorSystem.GetTransToP77()/1000000);
				// //��������(ppm->10e-6)
				// double a2 = k *
				// this._CoorSystem.GetTransToP74()/3600*Math.PI/180; //X��ת
				// (��->����)
				// double a3 = k *
				// this._CoorSystem.GetTransToP75()/3600*Math.PI/180; //Y��ת
				// double a4 = k *
				// this._CoorSystem.GetTransToP76()/3600*Math.PI/180; //Z��ת
				// double newX = this._CoorSystem.GetTransToP71() + k *
				// XYZ84.getX() + 0 - a3 * XYZ84.getZ() + a4 * XYZ84.getY();
				// double newY = this._CoorSystem.GetTransToP72() + k *
				// XYZ84.getY() + a2 * XYZ84.getZ() + 0 - a4 * XYZ84.getX();
				// double newZ = this._CoorSystem.GetTransToP73() + k *
				// XYZ84.getZ() - a2 * XYZ84.getY() + a3 * XYZ84.getX() + 0;
				// XYZ84.setX(newX);XYZ84.setY(newY);XYZ84.setZ(newZ);

				// 3�������߲��������WGS84�ռ����꣬�������ÿ���Ĭ���򣨿���Ĭ�����󷽳̽��
				double A1, B1, C1, D1, A2, B2, C2, D2, A3, B3, C3, D3;
				A1 = k;
				B1 = a4;
				C1 = -a3;
				D1 = XZY.getX() - this._CoorSystem.GetTransToP71();
				A2 = -a4;
				B2 = k;
				C2 = a2;
				D2 = XZY.getY() - this._CoorSystem.GetTransToP72();
				A3 = a3;
				B3 = -a2;
				C3 = k;
				D3 = XZY.getZ() - this._CoorSystem.GetTransToP73();
				double D = A1 * B2 * C3 + B1 * C2 * A3 + C1 * A2 * B3 - C1 * B2 * A3 - B1 * A2 * C3 - A1 * C2 * B3;
				double E = D1 * B2 * C3 + B1 * C2 * D3 + C1 * D2 * B3 - C1 * B2 * D3 - B1 * D2 * C3 - D1 * C2 * B3;
				double F = A1 * D2 * C3 + D1 * C2 * A3 + C1 * A2 * D3 - C1 * D2 * A3 - D1 * A2 * C3 - A1 * C2 * D3;
				double G = A1 * B2 * D3 + B1 * D2 * A3 + D1 * A2 * B3 - D1 * B2 * A3 - B1 * A2 * D3 - A1 * D2 * B3;
				double WGS84_X = E / D;
				double WGS84_Y = F / D;
				double WGS84_Z = G / D;

				// 4����WGS84�ռ�����ת��Ϊ��γ��
				CoorSystem CS84 = new CoorSystem();
				CS84.ToWGS84();
				Coordinate lb84 = Project_XYZ.XYZ_XYZToBLH(WGS84_X, WGS84_Y, WGS84_Z, CS84);
				return lb84;
			}

			// if
			// (this._CoorSystem.GetCoorTransMethod()==lkCoorTransMethod.enFourPara)
			// {
			// double Dx = this._CoorSystem.GetTransToP41();
			// double Dy = this._CoorSystem.GetTransToP42();
			// double A = this._CoorSystem.GetTransToP43();
			// double K = this._CoorSystem.GetTransToP44();
			//
			// //1�������Ĳ��������WGS84ƽ������
			// double a1,b1,c1,a2,b2,c2;
			// a1 =K * Math.cos(A);
			// b1 = -K * Math.sin(A);
			// c1 = x - Dx;
			// a2 = K * Math.sin(A);
			// b2 = K * Math.cos(A);
			// c2 = y - Dy;
			// double X = (c1 - c2 * (b1 / b2)) / (a1 - a2 * (b1 / b2));
			// double Y = (c1 - a1 * X) / b1;
			//
			// //2����WGS84ƽ������ת��Ϊ��γ��
			// Coordinate lb84 = Project_GK.GK_XYToBL(X,Y, this._CoorSystem);
			// lb84.setZ(z);
			// return lb84;
			// }

			// �޸�����
			Coordinate lb = Project_GK.GK_XYToBL(x, y, this._CoorSystem);
			lb.setZ(z);
			return lb;
		}
	}

	/**
	 * ���ݾ�γ������������뾭��3�ȷִ�
	 * 
	 * @param X
	 * @param Y
	 * @return
	 */
	public static int AutoCalCenterJX(double JD, double WD) {
		// 6�ȷִ�
		// int n=Convert.ToInt32(L / 6+1);
		// int L0 = 6 * n - 3;

		// 3�ȷִ�
		int n = (int) (JD / 3 + 0.5);
		int L0 = 3 * n;
		return L0;

		// return Convert.ToInt32(Math.Truncate(L));
	}

	/**
	 * ���ݾ��ȣ��������
	 * 
	 * @param JD
	 *            ����
	 * @param FD
	 *            �ִ����ͣ�3��6
	 * @return
	 */
	public static int GetDH(double JD, int FDType) {
		// floor �� ���ز��������������
		int dh = (int) Math.floor(JD / FDType);
		if (FDType == 6)
			dh++;
		return dh;
	}

	/**
	 * ���ݴ��ţ��������뾭��
	 * 
	 * @param DH
	 *            ����
	 * @param FDType
	 *            �ִ����ͣ�3��6
	 * @return
	 */
	public static int GetCenterJX(int DH, int FDType) {
		int centerjx = DH * FDType;
		if (FDType == 6)
			centerjx -= 3;
		return centerjx;
	}

	// //��Բ������ת������������
	// public static Coordinate WebXYToUtmXY(double x1, double y1)
	// {
	// Coordinate LB = WebUTMXYToBL(x1, y1);
	// return UTMBLToXY(LB.getX(), LB.getY());
	// }
	//
	// //84ƽ��ת���ɾ�γ�ȣ�UTM--84��γ��ת84ƽ��(����)��L,B--84��γ������; X1,Y1--84ƽ������
	// public static Coordinate UTMBLToXY(double L1, double B1)
	// {
	// return UTMBLToXY(L1, B1, GlobleCenterJX);
	// }

	//
	// //UTM--84ƽ��ת84��γ��(����)
	// public static Coordinate UTMXYToBL(double X, double Y)
	// {
	// return UTMXYToBL(X, Y,GlobleCenterJX);
	// }

}
