package lkmap.CoordinateSystem;

import lkmap.Cargeometry.Coordinate;


public class Project_XYZ 
{
	/**
	 * ��γ��ת�ռ�ֱ������
	 * @param L ����
	 * @param B γ��
	 * @param H ��ظ�
	 * @param coorSystem ����ϵͳ
	 * @return
	 */
	public static Coordinate XYZ_BLHToXYZ(double L,double B,double H,CoorSystem coorSystem)
	{
		double hd = Math.PI / 180;
	    double a = coorSystem.GetA();
	    double b = coorSystem.GetB();
	
	    double e2 = (a * a - b * b) / (a * a);
	    double w = Math.sqrt(1 - e2 * Math.sin(B * hd) * Math.sin(B * hd));
	    double n = a / w;
	
	    double X = (n + H) * Math.cos(B * hd) * Math.cos(L * hd);
	    double Y = (n + H) * Math.cos(B * hd) * Math.sin(L * hd);
	    double Z = (n * (1 - e2) + H) * Math.sin(B * hd);
	    
	    return new Coordinate(X,Y,Z);
	}
	
	
	/**
	 * �ռ�ֱ������ת��γ������
	 * @param X 
	 * @param Y
	 * @param Z
	 * @param coorSystem ����ϵͳ
	 * @return
	 */
	public static Coordinate XYZ_XYZToBLH(double X,double Y,double Z,CoorSystem coorSystem)
	{
	    double jd = 180 / Math.PI;
	    double a = coorSystem.GetA();
	    double b = coorSystem.GetB();
	    double e2 = (a * a - b * b) / (a * a);

        //�ɿռ�ֱ���������ռ������꣨���õ���������
        double bR = a * Math.sqrt(1 - e2);//����̰���
        double n0 = a;
        double h0 = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2) + Math.pow(Z, 2)) - Math.sqrt(a * bR);
        double b0 = Math.atan(Z / Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)) / (1 - e2 * n0 / (n0 + h0)));
        while (true)
        {
            n0 = a / Math.sqrt(1 - e2 * Math.pow(Math.sin(b0), 2));
            double h1 = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)) / Math.cos(b0) - n0;
            double b1 = Math.atan(Z / Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)) / (1 - e2 * n0 / (n0 + h1)));
            if ((b1 - b0) < 1e-10 && (h1 - h0) < 0.0001)
            {
                h0 = h1;
                b0 = b1;
                break;
            }
            else
            {
                h0 = h1;
                b0 = b1;
            }
        }
        
        double H = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)) / Math.cos(b0) - n0;
        double L = Math.atan(Y / X);
        double B = Math.atan(Z / Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)) / (1 - e2 * n0 / (n0 + H)));
        
        return new Coordinate((L * jd)<0?180+L*jd:L*jd,B * jd,H);

	}
}
