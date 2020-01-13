package lkmap.CoordinateSystem;

import lkmap.Cargeometry.Coordinate;

public class Project_GK 
{
    /// <summary>
    /// ���ܣ�Gauss Kruger ƽ��(����)(B,L--->X,Y)
    /// </summary>
    /// <param name="X">80ƽ������ X</param>
    /// <param name="Y">80ƽ������ Y</param>
    /// <param name="L">80�������� L</param>
    /// <param name="B">80γ������ B</param>
    public static Coordinate GK_BLToXY(double L1, double B1,CoorSystem coorSystem)
    {
        double X = 0, Y = 0;
        if ((L1 == 0) || (B1 == 0)) return null;
        double L0 = coorSystem.GetCenterMeridian() * Math.PI / 180;  //���뾭��(����)
        int K = 1;                      //ͶӰ��������
        double FE = coorSystem.GetEasting();              //����ƫ��
        double FN = 0;                    //��γƫ��
        double a = coorSystem.GetA();               //���򳤰���(����003)
        double b = coorSystem.GetB();          //����̰���(����004)                      
        //double f = (a - b) / a;           //����
        double de = Math.sqrt(1 - (b / a) * (b / a));  //��һƫ���� 
        double ee = Math.sqrt((a / b) * (a / b) - 1);  //�ڶ�ƫ����                        
        double B = Math.PI * B1 / 180;    //γ��(��λ:����)
        double L = Math.PI * L1 / 180;    //����(��λ:����)

        double T = Math.tan(B) * Math.tan(B);
        double C = (ee * ee) * (Math.cos(B) * Math.cos(B));
        double A = (L - L0) * Math.cos(B);
        double M = a * ((1 - de * de / 4 - 3 * Math.pow(de, 4) / 64 - 5 * Math.pow(de, 6) / 256) * B
                 - (3 * de * de / 8 + 3 * Math.pow(de, 4) / 32 + 45 * Math.pow(de, 6) / 1024) * Math.sin(2 * B)
                 + (15 * Math.pow(de, 4) / 256 + 45 * Math.pow(de, 6) / 1024) * Math.sin(4 * B)
                 - 35 * Math.pow(de, 6) / 3072 * Math.sin(6 * B));
        double N = a / Math.sqrt(1 - (de * de) * (Math.sin(B) * Math.sin(B))); //î��Ȧ���ʰ뾶   

        double XN = K * (M + N * Math.tan(B) * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * Math.pow(A, 4) / 24)
                  + (61 - 58 * T + T * T + 270 * C - 330 * T * C) * Math.pow(A, 6) / 720);

        double YE = FE + K * N * (A + (1 - T + C) * Math.pow(A, 3) / 6
                  + (5 - 18 * T + T * T + 14 * C - 58 * T * C) * Math.pow(A, 5) / 120);
        X = YE;
        Y = XN;
        return new Coordinate(X,Y);
    }
    
    
    public static Coordinate GK_BLToXY2(double L1, double B1,CoorSystem coorSystem)
    {
        double X = 0, Y = 0;
        if ((L1 == 0) || (B1 == 0)) return null;
        double L0 = coorSystem.GetCenterMeridian() * Math.PI / 180;  //���뾭��(����)
        int K = 1;                      //ͶӰ��������
        double FE = coorSystem.GetEasting();              //����ƫ��
        double FN = 0;                    //��γƫ��
        double a = coorSystem.GetA();               //���򳤰���(����003)
        double b = coorSystem.GetB();          //����̰���(����004)                      
        //double f = (a - b) / a;           //����
        double de = Math.sqrt(1 - (b / a) * (b / a));  //��һƫ���� 
        double ee = Math.sqrt((a / b) * (a / b) - 1);  //�ڶ�ƫ����                        
        double B = Math.PI * B1 / 180;    //γ��(��λ:����)
        double L = Math.PI * L1 / 180;    //����(��λ:����)

        double T = Math.tan(B) * Math.tan(B);
        double C = (ee * ee) * (Math.cos(B) * Math.cos(B));
        double A = (L - L0) * Math.cos(B);
        double M = a * ((1 - de * de / 4 - 3 * Math.pow(de, 4) / 64 - 5 * Math.pow(de, 6) / 256) * B
                 - (3 * de * de / 8 + 3 * Math.pow(de, 4) / 32 + 45 * Math.pow(de, 6) / 1024) * Math.sin(2 * B)
                 + (15 * Math.pow(de, 4) / 256 + 45 * Math.pow(de, 6) / 1024) * Math.sin(4 * B)
                 - 35 * Math.pow(de, 6) / 3072 * Math.sin(6 * B));
        double N = a / Math.sqrt(1 - (de * de) * (Math.sin(B) * Math.sin(B))); //î��Ȧ���ʰ뾶   

        double XN = K * (M + N * Math.tan(B) * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * Math.pow(A, 4) / 24)
                  + (61 - 58 * T + T * T + 270 * C - 330 * T * C) * Math.pow(A, 6) / 720);

        double YE = FE + K * N * (A + (1 - T + C) * Math.pow(A, 3) / 6
                  + (5 - 18 * T + T * T + 14 * C - 58 * T * C) * Math.pow(A, 5) / 120);
        X = YE;
        Y = XN;
        return new Coordinate(Y,X);
    }


    /// <summary>
    /// ���ܣ�Gauss Kruger--(����)(X,Y--->B,L)
    /// </summary>
    /// <param name="X">80ƽ������ X</param>
    /// <param name="Y">80ƽ������ Y</param>
    /// <param name="L">80�������� L</param>
    /// <param name="B">80γ������ B</param>
    public static Coordinate GK_XYToBL(double X, double Y,CoorSystem coorSystem)
    {
        double B = 0, L = 0;
        if ((X == 0) || (Y == 0)) return null;
        double L0 = coorSystem.GetCenterMeridian() * Math.PI / 180;  //���뾭�� (����001)  
        int K = 1;                      //ͶӰ��������(����002)         
        double a = coorSystem.GetA();               //���򳤰���(����003)
        double b = coorSystem.GetB();          //����̰���(����004)                      
        //double f = (a - b) / a;           //����(����005)
        double de = Math.sqrt(1 - (b / a) * (b / a));  //��һƫ����(����006)           
        double ee = Math.sqrt((a / b) * (a / b) - 1);  //�ڶ�ƫ����(����007)
        double XN = Y;              //γ��
        double YE = X;              //����                                    
        double FE = coorSystem.GetEasting();        //����ƫ��(����009)  
        double FN = 0;              //��γƫ��(����010)                
        double Mf = (XN - FN) / K;
        double e1 = (1 - b / a) / (1 + b / a);
        double Q = Mf / (a * (1 - de * de / 4 - 3 * Math.pow(de, 4) / 64 - 5 * Math.pow(de, 6) / 256));
        double Bf = Q + (3 * e1 / 2 - 27 * e1 * e1 * e1 / 32) * Math.sin(2 * Q)
                  + (21 * e1 * e1 / 16 - 55 * Math.pow(e1, 4) / 32) * Math.sin(4 * Q)
                  + (151 * e1 * e1 * e1 / 96) * Math.sin(6 * Q)
                  + (1097 * Math.pow(e1, 4) / 512) * Math.sin(8 * Q);
        double Nf = a / Math.sqrt(1 - de * de * Math.sin(Bf) * Math.sin(Bf));
        double D = (YE - FE) / (K * Nf);
        double Tf = Math.tan(Bf) * Math.tan(Bf);
        double Cf = (ee * ee) * (Math.cos(Bf) * Math.cos(Bf));
        double Rf = a * (1 - de * de) / Math.sqrt(Math.pow((1 - de * de * Math.sin(Bf) * Math.sin(Bf)), 3));

        B = Bf - (Nf * Math.tan(Bf) / Rf) * (D * D / 2 - (5 + 3 * Tf + Cf - 9 * Tf * Cf) * Math.pow(D, 4) / 24
              + (61 + 90 * Tf + 45 * Tf * Tf) * Math.pow(D, 6) / 720);

        L = L0 + (1 / Math.cos(Bf)) * (D - (1 + 2 * Tf + Cf) * Math.pow(D, 3) / 6
          + (5 + 28 * Tf + 6 * Cf + 8 * Tf * Cf + 24 * Tf * Tf) * Math.pow(D, 5) / 120);

        B = B * 180 / Math.PI;
        L = L * 180 / Math.PI;
        
        return new Coordinate(L,B);
    }
}
