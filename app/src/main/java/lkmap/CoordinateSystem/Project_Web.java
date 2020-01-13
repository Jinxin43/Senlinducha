package lkmap.CoordinateSystem;

import lkmap.Cargeometry.Coordinate;

public class Project_Web 
{
    //ͶӰԲ������ϵ��Web Google)
    public static Coordinate Web_BLToXY(double L1, double B1)
    {
        //lon:����,lat��γ��
        double originShift = 2 * Math.PI * 6378137 / 2.0;
        double X = L1 * originShift / 180.0;
        double Y = Math.log(Math.tan((90 + B1) * Math.PI / 360.0)) / (Math.PI / 180.0);
        Y = Y * originShift / 180.0;
        return new Coordinate(X,Y);
    }

    public static Coordinate Web_XYToBL(double X, double Y)
    {
        //lon:����,lat��γ��
        double originShift = 2 * Math.PI * 6378137 / 2.0;
        double L = X * 180.0 / originShift;
        double y = Y * 180 / originShift;
        double B = Math.atan(Math.exp(y * (Math.PI / 180.0))) / (Math.PI / 360.0) - 90;
        return new Coordinate(L,B);
    }
}
