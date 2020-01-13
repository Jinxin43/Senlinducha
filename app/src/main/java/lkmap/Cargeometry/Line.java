package lkmap.Cargeometry;

import lkmap.Map.Param;
import lkmap.Tools.Tools;

public class Line 
{
	private Coordinate _StartPoint, _EndPoint;
    public Coordinate getStartPoint()
    {
         return _StartPoint;
    }
    public Coordinate getEndPoint()
    {
        return _EndPoint;
    }
    public Line(Coordinate Pt1, Coordinate Pt2)
    {
        _StartPoint = Pt1; _EndPoint = Pt2;
    }

    //�����Ӿ���
    private Envelope _Envelope;
    public Envelope getEnvelope()
    {
            if (_Envelope == null)
            {
                //����������Ӿ���
                double MinX, MinY, MaxX, MaxY;
                if (_StartPoint.getX() > _EndPoint.getX())
                {
                    MinX = _EndPoint.getX(); MaxX = _StartPoint.getX();
                }
                else
                {
                    MinX = _StartPoint.getX(); MaxX = _EndPoint.getX();
                }

                if (_StartPoint.getY() > _EndPoint.getY())
                {
                    MinY = _EndPoint.getY(); MaxY = _StartPoint.getY();
                }
                else
                {
                    MinY = _StartPoint.getY(); MaxY = _EndPoint.getY();
                }
                _Envelope = new Envelope(MinX, MaxY, MaxX, MinY);
            }
            return _Envelope; 
    }


    //����ѡ����Ƿ���ָ���ľ����ڱ�ѡ�У����ѡ���򷵻�ѡ���Ĵ��㼰��������߶����ľ���

    //����1
    public boolean PointToLineDistance(Coordinate SelPoint, double Tolerance)
    {
        
        Coordinate PerPoint = null; Param LineInnerPoint = new Param();
        double PerDistance = this.PointToLineNearestDistance(SelPoint, LineInnerPoint, PerPoint);
        if (PerDistance <= Tolerance) return true; else return false;
        //return this.PointToLineDistance(SelPoint, Tolerance, out PerDistance, out PerPoint, out LineInnerPoint);
    }

    //����1
    public boolean PointToLineDistance(Coordinate SelPoint, double Tolerance,
                                    Param PerDistanceT/*����������ľ���*/)
    {
    	//double PerDistance = PerDistanceT.getDouble();
        Coordinate PerPoint = null; boolean LineInnerPoint = false;Param LineInnerPointT = new Param();
        double PerDistance = this.PointToLineNearestDistance(SelPoint, LineInnerPointT, PerPoint);
        LineInnerPoint = LineInnerPointT.getBoolean();
        if (PerDistance <= Tolerance)
        {
            if (LineInnerPoint) PerDistance = Tools.GetTwoPointDistance(this._StartPoint, PerPoint);
            PerDistanceT.setValue(PerDistance);
            return true;
        }
        else
        {
        	PerDistanceT.setValue(PerDistance);
            return false;
        }
        //return this.PointToLineDistance(SelPoint, Tolerance, out PerDistance, out PerPoint, out LineInnerPoint);
    }
    //����2
    public boolean PointToLineDistance(Coordinate SelPoint, double Tolerance,
                                    Param PerDistanceT/*����������ľ���*/, 
                                    Coordinate PerPoint /*��ֱ������*/,
                                    boolean LineInnerPoint /*�����Ƿ�Ϊ���ڵ�*/)
    {
    	Param LineInnerPointT = new Param();LineInnerPointT.setValue(LineInnerPoint);
        double PerDistance = this.PointToLineNearestDistance(SelPoint, LineInnerPointT, PerPoint);
        if (PerDistance <= Tolerance)
        {
            if (LineInnerPoint) PerDistance = Tools.GetTwoPointDistance(this._StartPoint, PerPoint);
            PerDistanceT.setValue(PerDistance);
            return true;
        }
        else
        {
        	PerDistanceT.setValue(PerDistance);
            return false;
        }

        //return this.PointToLineDistance(SelPoint.X, SelPoint.Y, Tolerance, out PerDistance, out PerPoint, out LineInnerPoint);
    }
    ////����3
    //public bool PointToLineDistance(double xx, double yy, double Tolerance, 
    //                                out double PerDistance/*����������ľ���*/,
    //                                out Coordinate PerPoint /*��ֱ������*/,
    //                                out bool LineInnerPoint /*�����Ƿ�Ϊ���ڵ�*/)
    //{
    //    PerDistance = double.MaxValue;
    //    PerPoint = null; LineInnerPoint = false;
    //    //x1 , y1 , x2 , y2 : Ϊ�߶����˵�����
    //    //xx , yy : Ϊ����һ�������
    //    //pt1 , pt2 ��б��Ϊ ��k = ( y2 - y1 ) / (x2 - x1 );
    //    //��ֱ�߷���Ϊ�� y = k* ( x - x1) + y1
    //    //�䴹�ߵ�б��Ϊ - 1 / k��
    //    //���߷���Ϊ��  y = (-1/k) * (x - xx) + yy
    //    //������ֱ�߷��̽�ã�
    //    //x  =  ( k^2 * x1 + k * (yy - y1 ) + xx ) / ( k^2 + 1)
    //    //y  =  k * ( x - x1) + y1;

    //    double x1 = this._StartPoint.X;
    //    double y1 = this._StartPoint.Y;

    //    double x2 = this._EndPoint.X;
    //    double y2 = this._EndPoint.Y;


    //    //���߶���һ�㵽�߶εĴ���X,Y����
    //    double XX, YY, LK;
    //    if (y1 == y2)      // ƽ����X��
    //    {
    //        XX = xx; YY = y1;
    //    }
    //    else if (x1 == x2)  //ƽ����Y��
    //    {
    //        XX = x1; YY = yy;
    //    }
    //    else
    //    {
    //        //ע�Ͳ��֣��˷������㲻׼ȷ
    //        //LK = (y2 - y1) / (x2 - x1);  //����֪�߶ε�б��
    //        //double XX1 = (LK * LK * x1 + LK * (yy - y1) + xx) / (LK * LK + 1);
    //        //double YY1 = LK * (XX1 - x1) + y1;

    //        double abx = x2 - x1;
    //        double aby = y2 - y1;
    //        double acx = xx - x1;
    //        double acy = yy - y1;
    //        double ff = (abx * acx + aby * acy) / (abx * abx + aby * aby);     //   ע��ab������ֱ���ϵ�������ͬ��   
    //        XX = x1 + ff * abx;
    //        YY = y1 + ff * aby;
    //    }


    //    double a = Tools.GetTwoPointDistance(x1, y1, xx, yy);  //����һ�����һ�˵�ľ���
    //    double b = Tools.GetTwoPointDistance(x2, y2, xx, yy);  //����һ����ڶ��˵�ľ���
    //    double c = Tools.GetTwoPointDistance(x1, y1, x2, y2);  //�߶����˵����
    //    double d = Tools.GetTwoPointDistance(XX, YY, x1, y1);  //ͶӰ�㵽��һ�˵����
    //    double e = Tools.GetTwoPointDistance(XX, YY, x2, y2);  //ͶӰ�㵽�ڶ��˵����            
    //    double f = Tools.GetTwoPointDistance(XX, YY, xx, yy);  //����һ������ͶӰ��ľ���            

    //    //ͶӰ�������˵������������˵�������,��ͶӰ�����߶�֮��,����ͶӰ�����߶��ӳ�����
    //    //1.��ͶӰ�����߶���,��ô����㵽�߶εľ�������������ͶӰ��ľ���
    //    //2.��ͶӰ�����߶���,��ôѡ������������˵�������Ϊ��������߶εľ���,���ͶӰ����
    //    //  ĳһ���˵����С��0.5��,���������������˵�ľ��������趨��Χ,��ȡ����˵���ΪͶӰ�� 
    //    if (Math.Abs(d + e - c) < 0.00000001 )
    //    {
    //        PerPoint = new Coordinate(XX, YY);
    //        PerDistance = d;
    //        LineInnerPoint = true;
    //        if (f <= Tolerance) return true; else return false;
    //    }
    //    else  //ͶӰ�������ӳ�����
    //    {
    //        if (d <= e)  //ͶӰ�㿿����һ���˵�
    //        {
    //            PerPoint = new Coordinate(x1, y1);
    //            PerDistance = d;
    //            LineInnerPoint = false;
    //            if (a <= Tolerance) return true; else return false;
    //        }
    //        else
    //        {
    //            PerPoint = new Coordinate(x2, y2);
    //            PerDistance = c+e;
    //            LineInnerPoint = false;
    //            if (b <= Tolerance) return true; else return false;
    //        }
    //    }
    //}



    /// <summary>
    /// ����㵽�߶ε���̾���
    /// </summary>
    /// <param name="SelPoint"></param>
    /// <returns></returns>
    public double PointToLineNearestDistance(Coordinate SelPoint)
    {
        Param LineInnerPoint=new Param();LineInnerPoint.setValue(false);Coordinate PerPoint=null;
        return this.PointToLineNearestDistance(SelPoint, LineInnerPoint, PerPoint);
    }

    /// <summary>����㵽�߶ε���̾���
    /// </summary>
    /// <param name="SelPoint">ѡ���</param>
    /// <param name="LineInnerPoint">�Ƿ�Ϊ���ϵ�</param>
    /// <param name="PerPoint">�������꣬����������ϵ㣬�򷵻���ѡ�������Ķ˵�����</param>
    /// <returns></returns>
    //public double PointToLineNearestDistance(Coordinate SelPoint, out bool LineInnerPoint, out Coordinate PerPoint)
    //{
    //    LineInnerPoint = false; PerPoint = null;
    //    Coordinate a = this.StartPoint;
    //    Coordinate b = this.EndPoint;
    //    Coordinate c = SelPoint;

    //    Coordinate ab = this.aDb(b, a);
    //    Coordinate ac = this.aDb(c, a);
    //    double f = aXb(ab, ac);

    //    if (f < 0) { PerPoint = a; return MapTools.Tools.GetTwoPointDistanceHD(c, a); }
    //    double d = this.aXb(ab, ab);
    //    if (f > d) { PerPoint = b; return MapTools.Tools.GetTwoPointDistanceHD(c, b); }
    //    f = f / d;
    //    Coordinate D = this.aAb(a, this.TXb(f, ab));   // c��ab�߶��ϵ�ͶӰ��
    //    PerPoint = D; LineInnerPoint = true;
    //    return MapTools.Tools.GetTwoPointDistanceHD(c, D);
    //}

    /**
     * ����ָ���ĵ��Ƿ����ϣ������ش�������
     */
    public double PointToLineNearestDistance(Coordinate SelPoint, Param LineInnerPoint, Coordinate PerPoint)
    {
        LineInnerPoint.setValue(false); 
        Coordinate PerPt = null;
        Coordinate a = this._StartPoint;
        Coordinate b = this._EndPoint;
        Coordinate c = SelPoint;

        Coordinate ab = this.aDb(b, a);
        Coordinate ac = this.aDb(c, a);
        double f = aXb(ab, ac);

        if (f <= 0) { PerPt = a; return Tools.GetTwoPointDistance(c, a,false); }
        double d = this.aXb(ab, ab);
        if (f > d) { PerPt = b; return Tools.GetTwoPointDistance(c, b,false); }
        f = f / d;
        Coordinate D = this.aAb(a, this.TXb(f, ab));   // c��ab�߶��ϵ�ͶӰ��
        PerPt = D; LineInnerPoint.setValue(true);
        if (PerPoint==null)PerPoint = new Coordinate();
        PerPoint.setX(PerPt.getX());PerPoint.setY(PerPt.getY());
        return Tools.GetTwoPointDistance(c, D,false);
    }

    private Coordinate aDb(Coordinate A, Coordinate B)
    {
        return new Coordinate(A.getX() - B.getX(), A.getY() - B.getY());
    }
    private Coordinate aAb(Coordinate A, Coordinate B)
    {
        return new Coordinate(A.getX() + B.getX(), A.getY() + B.getY());
    }
    private double aXb(Coordinate A, Coordinate B)
    {
        return A.getX() * B.getX() + A.getY() * B.getY();
    }
    private Coordinate TXb(double T, Coordinate A)
    {
        return new Coordinate(T * A.getX(), T * A.getY());
    }

    //�ж������߶��Ƿ��ཻ
    ////��� 
    //double mult(Coordinate a, Coordinate b, Coordinate c) 
    //{ 
    //    return (a.X-c.X)*(b.Y-c.Y)-(b.X-c.X)*(a.Y-c.Y); 
    //} 
    public boolean Intersect(Line InLine)
    {
        Coordinate P1 = InLine._StartPoint; Coordinate P2 = InLine._EndPoint;
        Coordinate Q1 = this._StartPoint; Coordinate Q2 = this._EndPoint;

        //���ڼ���������������
        //( P1 - Q1 ) �� ( Q2 - Q1 ) * ( Q2 - Q1 ) �� ( P2 - Q1 ) >= 0
        //( Q1 - P1 )��( P2 - P1) * ( P2 - P1)��(Q2 - P1) >= 0
        double P1Q1_X = (P1.getX() - Q1.getX()); double P1Q1_Y = (P1.getY() - Q1.getY());
        double Q2Q1_X = (Q2.getX() - Q1.getX()); double Q2Q1_Y = (Q2.getY() - Q1.getY());
        double P2Q1_X = (P2.getX() - Q1.getX()); double P2Q1_Y = (P2.getY() - Q1.getY());
        double P1Q1Q2Q1 = (P1Q1_X * Q2Q1_Y - P1Q1_Y * Q2Q1_X);
        double Q2Q1P2Q1 = (Q2Q1_X * P2Q1_Y - Q2Q1_Y * P2Q1_X);
        if (P1Q1Q2Q1 * Q2Q1P2Q1 < 0) return false;

        double Q1P1_X = Q1.getX() - P1.getX(); double Q1P1_Y = Q1.getY() - P1.getY();
        double P2P1_X = P2.getX() - P1.getX(); double P2P1_Y = P2.getY() - P1.getY();
        double Q2P1_X = Q2.getX() - P1.getX(); double Q2P1_Y = Q2.getY() - P1.getY();
        double Q1P1P2P1 = (Q1P1_X * P2P1_Y - Q1P1_Y * P2P1_X);
        double P2P1Q2P1 = (P2P1_X * Q2P1_Y - P2P1_Y * Q2P1_X);
        if (Q1P1P2P1 * P2P1Q2P1 < 0) return false;
        return true;

    }

    //�ж���ֱ���Ƿ��ཻ���ཻ�򷵻ؽ������꣬���� 0-���ཻ��1-�ཻ��ֻ��һ������ 2-�ཻ������������
    public int Intersect(Line InLine,  Coordinate IP1, Coordinate IP2)
    {
        //�ж�����ֱ�߶ε���Ӿ����Ƿ��ཻ
        if (!this.getEnvelope().Intersect(InLine.getEnvelope())) return 0;

        //�ж����߶��Ƿ��ཻ
        if (!this.Intersect(InLine)) return 0;
        
        double x1,y1,x2,y2,x3,y3,x4,y4;
        x1 = this._StartPoint.getX();
        y1 = this._StartPoint.getY();
        x2 = this._EndPoint.getX();
        y2 = this._EndPoint.getY();
        x3 = InLine._StartPoint.getX();
        y3 = InLine._StartPoint.getY();
        x4 = InLine._EndPoint.getX();
        y4 = InLine._EndPoint.getY();

        //�����ж�d   =   (y2-y1)(x4-x3)-(y4-y3)(x2-x1)��   
        //��d=0����ֱ��AB��CDƽ�л��غϣ�   
        //��d!=0����ֱ��AB��CD�н��㣬�轻��ΪE(x0,y0)��   

        //1���ж�����ֱ���Ƿ�ƽ�У����ص�
        double D = (y2 - y1)*(x4 - x3) - (y4 - y3)*(x2 - x1);
        if (D == 0) //��ʾƽ�л��غ�
        {
            return 0;
        }

        //2���󽻵�
        double x0 = ((x2 - x1) * (x4 - x3) * (y3 - y1) + (y2 - y1) * (x4 - x3) * x1 - (y4 - y3) * (x2 - x1) * x3) / D;
        double y0 = ((y2 - y1) * (y4 - y3) * (x3 - x1) + (x2 - x1) * (y4 - y3) * y1 - (x4 - x3) * (y2 - y1) * y3) / (-D);

        //3�����жϽ����Ƿ�������
        IP1.setX(x0);IP1.setY(y0);
        
        
        if (this.PointOnLine(IP1)) return 1; else return 0;



        //���㷨��������

        ////�ж�����ֱ�߶ε�λ�����
        //double x1, y1, xx1, yy1, x2, y2, xx2, yy2;
        //double n1, n2, n3, n4, k1, k2;
        //if (this._StartPoint.X>this._EndPoint.X)
        //{
        //    xx1=this._EndPoint.X;
        //    yy1=this._EndPoint.Y;
        //    xx2=this._StartPoint.X;
        //    yy2=this._StartPoint.Y;
        //} else 
        //{
        //    xx1=this._StartPoint.X;
        //    yy1=this._StartPoint.Y;
        //    xx2=this._EndPoint.X;
        //    yy2=this._EndPoint.Y;
        //}

        //if (xx1==xx2 && yy1==yy2) return 0;   //��ֱ��Ϊһ����

        //if (InLine._StartPoint.X>InLine._EndPoint.X)
        //{
        //    x1=InLine._EndPoint.X;
        //    y1=InLine._EndPoint.Y;
        //    x2=InLine._StartPoint.X;
        //    y2=InLine._StartPoint.Y;
        //} else 
        //{
        //    x1=InLine._StartPoint.X;
        //    y1=InLine._StartPoint.Y;
        //    x2=InLine._EndPoint.X;
        //    y2=InLine._EndPoint.Y;
        //}

        //if (x1==x2 && y1==y2) return 0;    //��ֱ��Ϊһ����

        ////1������ֱ�߶ζ�����ֱ�� ||
        //if ((x1 == x2) && (xx1 == xx2))
        //{
        //    if (x1 != xx1) return 0;   //���߶κ����겻ͬ�����ཻ
        //    IP1.X = (int)x1; IP2.X = (int)x1;
        //    n1 = Math.Min(yy1, yy2);
        //    n2 = Math.Max(yy1, yy2);
        //    n3 = Math.Min(y1, y2);
        //    n4 = Math.Max(y1, y2);

        //    if ((n1 > n4) || (n2 < n3)) return 0;//���߶�������û���ཻ�������ز��ཻ
        //    else
        //    {
        //        if (n1 == n4 || n2 == n3)  //ֻ��һ��������ͬ
        //        {
        //            if (n1 == n4)
        //            {
        //                IP1.Y = (int)n1;
        //                return 1;
        //            }
        //            if (n2 == n3)
        //            {
        //                IP1.Y = (int)n2;
        //                return 1;
        //            }
        //        }
        //        else     //���������ص�
        //        {
        //            IP1.Y = (int)Math.Max(n1, n3);
        //            IP2.Y = (int)Math.Min(n2, n4);
        //            return 2;
        //        }
        //    }
        //}
        //else
        //{
        //    if ((x1==x2) && (xx1!=xx2))   //һ��ֱ�ߣ�һ��б��
        //    {
        //        IP1.X = (int)x1;
        //        if (!(x1>=xx1 && x1<=xx2)) return 0;    //���ཻ
        //        IP1.Y = (int)(yy1 + (x1 - xx1) / (xx2 - xx1) * (yy2 - yy1));
        //        if (IP1.Y>=y1 && IP1.Y<=y2 || IP1.Y>=y2 && IP1.Y<=y1)
        //        return 1;else return 0;

        //    }

        //    if ((x1!=x2) && (xx1==xx2))   //һ��ֱ�ߣ�һ��б��
        //    {
        //        IP1.X = (int)xx1;
        //        if (!(xx1>=x1 && xx1<=x2)) return 0;    //���ཻ
        //        IP1.Y = (int)(y1 + (y2 - y1) / (x2 - x1) * (xx1 - x1));
        //        if (!(IP1.Y>=yy1 && IP1.Y<=yy2 || IP1.Y>=yy2 && IP1.Y<=yy1))
        //        return 1;else return 0;
        //    }

        //}

        
        ////��������ֱ�ߵ�б��
        //k1 = (y2 - y1) / (x2 - x1);
        //k2 = (yy2 - yy1) / (xx2 - xx1);
        //if (k1 == k2)   //��ֱ��ƽ��
        //{
        //    if (x1 > xx2 || x2 < xx1)
        //        return 0;

        //    else
        //    {
        //        n1 = y1 + (0 - x1) * k1;
        //        n2 = yy1 + (0 - x1) * k2;
        //        if (n1 != n2)
        //            return 0;
        //        else
        //        {
        //            n3 = Math.Max(xx1, x1);
        //            n4 = Math.Min(xx2, x2);
        //            IP1.X = (int)n3;
        //            IP1.Y = (int)(y1 + (n2 - x1) * k1);
        //            if (n3 == n4)
        //                return 1;
        //            IP2.X = (int)n4;
        //            IP2.Y = (int)(y1 + (n4 - x1) * k1);
        //            return 2;
        //        }
        //    }
        //}
        //else
        //{
        //    IP1.X = (int)((yy1 - y1 + x1 * k1 - xx1 * k2) / (k1 - k2));
        //    IP1.Y = (int)(y1 + (IP1.X - x1) * k1);
        //    if ((IP1.X >= x1 && IP1.X <= x2) && (IP1.X >= xx1 && IP1.X <= xx2))
        //        return 1;
        //    else return 0;
        //}

//        return 0;

    }

    //�ж�ָ�����Ƿ�������
    public boolean PointOnLine(Coordinate Pt)
    {
        //if (!this.Envelope.ContainPoint(Pt)) return false;
        boolean InnerPoint = false;Coordinate PerPoint = new Coordinate();
        Param InnerPointT = new Param();
        PointToLineNearestDistance(Pt, InnerPointT, PerPoint);
        InnerPoint = InnerPointT.getBoolean();
        if (InnerPoint && Tools.GetTwoPointDistance(Pt, PerPoint) < 0.5) return true;
        return false;

        //1����һ���㷨������
        //( Q - P1 ) �� ( P2 - P1 ) = 0 �� Q ���� P1��P2Ϊ�ԽǶ���ľ����ڡ�ǰ�߱�֤Q����ֱ��P1P2�ϣ������Ǳ�֤Q�㲻���߶�P1P2���ӳ��߻����ӳ�����


        //double QP1_X = Pt.X - this._StartPoint.X;
        //double QP1_Y = Pt.Y - this._StartPoint.Y;

        //double P2P1_X = this._EndPoint.X - this._StartPoint.X;
        //double P2P1_Y = this._EndPoint.Y - this._StartPoint.Y;

        //double QP1P2P1 = (QP1_X * P2P1_Y - QP1_Y * P2P1_X);
        //if (Math.Abs(QP1P2P1) <= 0.00001)
        //{
        //    //if min(xi,xj) <= xk <= max(xi,xj) and min(yi,yj) <= yk <= max(yi,yj)
        //    //�ж��Ƿ����ӳ�����
        //    if ((Math.Min(this._StartPoint.X, this._EndPoint.X) <= Pt.X && Math.Max(this._StartPoint.X, this._EndPoint.X) >= Pt.X) &&
        //        (Math.Min(this._StartPoint.Y, this._EndPoint.Y) <= Pt.Y && Math.Max(this._StartPoint.Y, this._EndPoint.Y) >= Pt.Y))
        //        return true;
        //    else return false;
        //}
        //else return false;

        //2���ڶ����㷨���жϴ�ֱ����<=����(2)
        //double X0 = Pt.X;
        //double Y0 = Pt.Y;
        //double X1 = this._StartPoint.X;
        //double Y1 = this._StartPoint.Y;
        //double X2 = this._EndPoint.X;
        //double Y2 = this._EndPoint.Y;

        ////S = ((x1-x0)*(y2-y0)-(x2-x0)*(y1-y0))/2 ���������������ʾ
        //double S = ((X1 - X0) * (Y2 - Y0) - (X2 - X0) * (Y1 - Y0));
        //double PerH = S / Tools.GetTwoPointDistance(_StartPoint, _EndPoint);
        //if (PerH <= Math.Sqrt(2)) return true; else return false;


        //�������㷨���ж�б��
        ////�ж��Ƿ��ڶϵ���
        //if (X0 == X1 || X0 == X2) return true;
        //double K1 = (Y1 - Y0) / (X1 - X0);
        //double K2 = (Y2-Y0)/(X2-X0);
        //double K = K1 - K2;
        //if (Math.Abs(K) <= 0.1) return true; else return false;
    }


    //��������ָ������������
    public Coordinate GetToStartCoordinate(double ToStartDistance)
    {
        //���ȷֵ㹫ʽ��x=��x1+��x2��/��1+�ˣ���y=��y1+��y2��/��1+�ˣ�
        double X1 = _StartPoint.getX(); double Y1 = _StartPoint.getY();
        double X2 = _EndPoint.getX(); double Y2 = _EndPoint.getY();
        double S1 = ToStartDistance;
        double S2 = this.Length() - ToStartDistance;
        double S = S1 / S2;
        double X = (X1 + S * X2) / (1 + S);
        double Y = (Y1 + S * Y2) / (1 + S);
        return new Coordinate(X, Y);
    }

    //����ֱ����

    public double Length()
    {
        return Tools.GetTwoPointDistance(_StartPoint, _EndPoint);
    }
}
