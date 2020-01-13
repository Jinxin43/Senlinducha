package lkmap.CoordinateSystem;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import lkmap.Cargeometry.*;
import lkmap.Map.Param;
import lkmap.Map.StaticObject;

public class ViewConvert
{
    //Map�ĵ�ǰ��ͼ��Ӿ��Σ���λ���ף�����Map.Extend
    private Envelope _Extend;
    public Envelope getExtend()
    {
        if (_Center == null) return null;
        return _Extend;
    }
    public void setExtend(Envelope value)
    {
        Envelope pEnv = value;
        if (pEnv == null) return;
        
        _Center = pEnv.getCenter();
        if (pEnv.getHeight()>0)this.setZoom((double)pEnv.getHeight());
        //double ExtendHeight = (_Center.Y - MapHeight * 0.5) - (_Center.Y + MapHeight * 0.5);
        //Zoom *= Math.Abs((double)pEnv.Height / ExtendHeight);
        //���µ�ǰ��ͼ��Ӿ���
        this.CalExtend();
    }
    
    
    public Envelope CalExtend()
    {
        //0.05Ŀ������������
        return _Extend = new Envelope((_Center.getX() - getMapWidth() * 0.5), (_Center.getY() + getMapHeight() * 0.5),
                                      (_Center.getX() + getMapWidth() * 0.5), (_Center.getY() - getMapHeight() * 0.5));
    }

    //Map�������ͼ��Ӿ��Σ�Ҳ����ȫ����Χ����λ���ף�����Map.FullExtend;
    private Envelope _FullExtend;
    public Envelope getFullExtend()
    {
        return _FullExtend; 
    }
    public void setFullExtend(Envelope value)
    {
        _FullExtend = value;
        this.setZoom(_FullExtend.getHeight());
        //Zoom *= (double)_Size.Width / (double)_Size.Height;
        this.setCenter(_FullExtend.getCenter());
    }

    //ʵ�ʵ�λ��ʾ��Map�Ŀ��
    private double getMapWidth()
    {
         return this.getZoomScale() * _Size.getWidth();
    }

    //ʵ�ʵ�λ��ʾ��Map�ĸ߶�
    private double getMapHeight()
    {
            //return (MapWidth / _Size.Width) * _Size.Height;
            return _Zoom;
    }

    //������Ϊ��λ��Map�ߴ磬Ҳ����MapControl�ؼ��ĳߴ�
    private Size _Size;
    public Size getSize()
    {
        return _Size; 
    }
    public void setSize(Size value)
    {
        _Size = value;
        _ZoomScale = _Zoom / (double)_Size.getHeight();
        _ScaleZoom = 1/_ZoomScale;
        if (_Size.getWidth() == 0 || _Size.getHeight() == 0) return;
    }

    //Map.Extend��ʵ�ʵ�λ���ף��������꣬��Map.Center
    private Coordinate _Center;
    public Coordinate getCenter()
    {
         return _Center; 
    }
    public void setCenter(Coordinate value)
    {
    	_Center = value; 
    }

    //Map���ű���Zoom��Ĭ��ֵΪ����Map��ʵ�ʵ�λ(��)����ĸ߶�
    private double _Zoom;
    public double getZoom()
    {
        return _Zoom;
    }
    public void setZoom(double value)
    {
    	_Zoom = value;
    	int d = (_Size.getHeight());
        _ZoomScale = _Zoom / (double)d;
        _ScaleZoom = 1/_ZoomScale;
    }

    //�м����ֵ��Ϊ����ת����׼����ʵ���������ÿ�����ش���ĸ߶�
    private double _ZoomScale;
    private double _ScaleZoom;
    public double getZoomScale()
    {
        return _ZoomScale; 
    }

    //��Map����ת���ɻ�ͼ���꣨MapToScreen��
    public android.graphics.Point MapToScreen(Coordinate MapCoordinate)
    {
        return this.MapToScreen(MapCoordinate.getX(), MapCoordinate.getY());
    }

    public android.graphics.Point MapToScreen(double X, double Y)
    {
        android.graphics.PointF PF = this.MapToScreenF(X,Y);
        return new android.graphics.Point((int)PF.x, (int)PF.y);
    }
    
    public android.graphics.PointF MapToScreenF(double X, double Y)
    {
//        double left = _Center.getX() - this.getMapWidth() * 0.5;
//        double top = _Center.getY() + this.getMapHeight() * 0.5;
        double left = this.getExtend().getMinX();
        double top = this.getExtend().getMaxY();
        
        
        float X1 = (float)((X - left) * (this._ScaleZoom));
        float Y1 = (float)((top - Y) * (this._ScaleZoom));
        return new android.graphics.PointF(X1, Y1);
    }
    
    

    //������Map������ת����Ļ���������飬CompressMPoint:�Ƿ�ѹ����ͬ���ص�,deltX,deltYƫ����
    public android.graphics.Point[] MapPointsToScreePoints(List<Coordinate> _MapCoorList,
    													   boolean CompressMPoint/*�Ƿ�ѹ����ͬ���ص�*/, 
    													   int deltPixX, int deltPixY)
    {
        //System.Drawing.Point[] OPFList = new System.Drawing.Point[_MapCoorList.Count];
        List<android.graphics.Point> OPFList = new ArrayList<android.graphics.Point>();

        int ScreenX = 0, ScreenY = 0;
        for (int i = 0; i < _MapCoorList.size(); i++)
        {
        	android.graphics.Point agp = this.MapToScreen(_MapCoorList.get(i));
            ScreenX = agp.x;ScreenY=agp.y;
            if (CompressMPoint)
            {
                int pCount = OPFList.size();
                if (pCount > 0)
                {
                    if ((OPFList.get(pCount - 1).x == ScreenX && OPFList.get(pCount - 1).y == ScreenY)) continue;
                }
            }

            OPFList.add(new android.graphics.Point(ScreenX + deltPixX, ScreenY + deltPixY));
        }
        return (Point[]) OPFList.toArray(new Point[0]);
    }
    public android.graphics.Point[] MapPointsToScreePoints(List<Coordinate> _MapCoorList)
    {
        return MapPointsToScreePoints(_MapCoorList, true,0, 0);
    }
    public android.graphics.Point[] MapPointsToScreePoints(List<Coordinate> _MapCoorList, boolean CompressMPoint)
    {
        return MapPointsToScreePoints(_MapCoorList, CompressMPoint, 0, 0);
    }

    //����ͼ����ת����Map���� (ScreenToMap)
    //����ͼ����ת����Map����
    public Coordinate ScreenToMap(android.graphics.Point ScreenCoor)
    {
        return this.ScreenToMap(ScreenCoor.x, ScreenCoor.y);
    }
    public Coordinate ScreenToMap(android.graphics.PointF ScreenCoor)
    {
        return this.ScreenToMap(ScreenCoor.x, ScreenCoor.y);
    }
    
    public Coordinate ScreenToMap(int CoorX, int CoorY)
    {
    	return ScreenToMap((float)CoorX,(float)CoorY);
    }
	public Coordinate ScreenToMap(float CoorX, float CoorY) 
	{
        if (this.getExtend()== null) return null;
        double MapX = this.getExtend().getMinX() + CoorX * (this.getZoomScale());
        double MapY = this.getExtend().getMaxY() - CoorY * (this.getZoomScale());
        return new Coordinate(MapX, MapY);
	}
	

    //�����ü� (Clipline[�߶εĿ����ü�]��ClipPolygon[��Ŀ����ü�])
    //1���߶εĿ����ü�
    //    //���ÿ������е��㷨ȥ���Ӵ�֮��ĵ�
    //    //  1001|       1000    |    1010
    //    //______|_______________|________
    //    //      |               |
    //    //  0001|       0000    |    0010
    //    //______|_______________|________
    //    //      |               |
    //    //  0101|       0100    |    0110

    public android.graphics.Point[] ClipPolyline(List<Coordinate> _MapCoorList, int deltX, int deltY)
    {
        //�ڴ˽��н��в�����Ŀ���ǲ�����Ļ֮��Ĳ��֣�������Ϊ��̬��ע��׼��
        List<android.graphics.Point> m_DrawPoint = new ArrayList<android.graphics.Point>();
        int PointInView = -1;  //-1:״̬δ֪��0:���һ������ͼ�ڲ���1:������ͼ�ⲿ
        int PointCount = _MapCoorList.size();//, ClipType = 0;
        Coordinate Pt11 = new Coordinate();
        Coordinate Pt22 = new Coordinate();
        android.graphics.Point Pt1, Pt2, Pt00 = null;
        for (int i = 0; i < PointCount - 1; i++)
        {
            if (this.Clipline(_MapCoorList.get(i), _MapCoorList.get(i + 1),  Pt11,  Pt22))
            {
                Pt1 = this.MapToScreen(Pt11); Pt1.x += deltX; Pt1.y += deltY;
                Pt2 = this.MapToScreen(Pt22); Pt2.x += deltX; Pt2.y += deltY;
                if (Pt00==null) Pt00 = Pt2;

                if (Pt1.x == Pt00.x && Pt1.y == Pt00.y) m_DrawPoint.add(Pt2);
                else { m_DrawPoint.add(Pt1); m_DrawPoint.add(Pt2); }
                Pt00 = Pt2;
                
                //������ڴ����⣬�����⴩�����ڲ����Ӷ��������۽����

                if (PointInView == 1)   //���ڴ����⣬�����⴩�����ڲ�
                {
                    if (m_DrawPoint.size() >= 3)  
                    {
                        //�ж���������״̬����ȷ���Ƿ���Ҫ����ǵ���Ĩȥ�۽�
                    	android.graphics.Point P1 = m_DrawPoint.get(m_DrawPoint.size()-2);
                    	android.graphics.Point P2 = m_DrawPoint.get(m_DrawPoint.size()-3);
                        if (P1.x == P2.x || P1.y == P2.y) { }  //ƽ�����������Ҫ����
                        else
                        {
                            Coordinate CP1 = this.ScreenToMap(P1);
                            Coordinate CP2 = this.ScreenToMap(P2);
                            Envelope P1P2Enve = new Envelope(Math.min(CP1.getX(),CP2.getX()),Math.max(CP1.getY(),CP2.getY()),Math.max(CP1.getX(),CP2.getX()),Math.min(CP1.getY(),CP2.getY()));

                            double newX = -1, newY = -1; 
                            Coordinate CPt00 = this.ScreenToMap(new android.graphics.Point(0,0));
                            Coordinate CPtW0 = this.ScreenToMap(new android.graphics.Point(this.getSize().getWidth(),0));
                            Coordinate CPtWH = this.ScreenToMap(new android.graphics.Point(this.getSize().getWidth(), this.getSize().getHeight()));
                            Coordinate CPt0H = this.ScreenToMap(new android.graphics.Point(0, this.getSize().getHeight()));
                            if (P1P2Enve.ContainsPoint(CPt00))
                            {
                                newX = this.getClipExtendMinX(); newY = this.getClipExtendMaxY();
                                m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(new Coordinate(newX, newY)));

                            }
                            if (P1P2Enve.ContainsPoint(CPtW0))
                            {
                                newX = this.getClipExtendMaxX(); newY = this.getClipExtendMaxY();
                                m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(new Coordinate(newX, newY)));

                            }
                            if (P1P2Enve.ContainsPoint(CPtWH))
                            {
                                newX = this.getClipExtendMaxX(); newY = this.getClipExtendMinY();
                                m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(new Coordinate(newX, newY)));

                            }
                            if (P1P2Enve.ContainsPoint(CPt0H))
                            {
                                newX = this.getClipExtendMinX(); newY = this.getClipExtendMinY();
                                m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(new Coordinate(newX, newY)));

                            }
                            if (newX == -1 || newY == -1) 
                            {
                                //����������������ǻ�·
                                if (Math.abs(CP1.getY() - CP2.getY()) >= Math.abs(CPt0H.getY() - CPt00.getY()))
                                {
                                    CPt00.setX(CPt00.getX()-this.getZoomScale() * 10); 
                                    CPt00.setY(CPt00.getY()+ this.getZoomScale() * 10);
                                    CPt0H.setX(CPt0H.getX()-this.getZoomScale() * 10); 
                                    CPt0H.setY(CPt0H.getY()-this.getZoomScale() * 10);
                                    if ((CP1.getY() - CP2.getY()) < 0)
                                    {
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt00));
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt0H));
                                    }
                                    else
                                    {
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt0H));
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt00));
                                    }

                                }
                                else
                                {
                                    CPt00.setX(CPt00.getX()- this.getZoomScale() * 10);
                                    CPt00.setY(CPt00.getY()+ this.getZoomScale() * 10);
                                    CPtW0.setX(CPtW0.getX()+ this.getZoomScale() * 10); 
                                    CPtW0.setY(CPtW0.getY()+ this.getZoomScale() * 10);
                                    if ((CP1.getX() - CP2.getX()) > 0)
                                    {
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt00));
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPtW0));
                                    }
                                    else
                                    {
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPtW0));
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt00));
                                    }
                                    
                                    
                                }
                            }
                        }
                    }
                }

                PointInView = 0;
            }
            else
            {
                if (PointInView == 0) PointInView = 1;
            }
        }
        return (Point[]) m_DrawPoint.toArray(new Point[0]);
    }
    /// <summary> ��ָ��ֱ�߶��Ƿ���Ҫ�ڵ�ǰ�Ӵ�����ʾ [Cohen��Sutherland�㷨]
    /// </summary>
    /// <param name="Pt1"></param>
    /// <param name="Pt2"></param>
    /// <param name="Pt11"></param>
    /// <param name="Pt12"></param>
    /// <returns>true ��ʾ�߶� false ����ʾ�߶�</returns>
    private double getClipExtendMinX() { return this.getExtend().getMinX() - this.getZoomScale() * 10; }
    private double getClipExtendMinY() { return this.getExtend().getMinY() - this.getZoomScale() * 10; }
    private double getClipExtendMaxX() { return this.getExtend().getMaxX() + this.getZoomScale() * 10; }
    private double getClipExtendMaxY() { return this.getExtend().getMaxY() + this.getZoomScale() * 10; }

    public boolean Clipline(Coordinate Pt1, Coordinate Pt2,  Coordinate Pt11, Coordinate Pt22)
    {
        double X1 = Pt1.getX(), Y1 = Pt1.getY(), X2 = Pt2.getX(), Y2 = Pt2.getY();
        double X, Y;
        boolean Chp = false;
        while (true)
        {
            int C1 = this.Codec(X1, Y1);
            int C2 = this.Codec(X2, Y2);
            if (C1 == 0 && C2 == 0)          //ȫ�����Ӵ��ڲ�
            {
                if (Chp) { double t; t = X1; X1 = X2; X2 = t; t = Y1; Y1 = Y2; Y2 = t; }
                Pt11.setX(X1);Pt11.setY(Y1);
                Pt22.setX(X2);Pt22.setY(Y2);
                return true;
            }

            if ((C1 & C2) != 0)    //ȫ�����Ӵ��ⲿ
            {
                return false;
            }

            //Ϊ�˹淶�㷨�����߶εĶ˵�Pt1Ϊ��˵㣬�����������������ҪPt1��Pt2�����˵㡣
            //�ж�Pt1��Pt2�Ƿ���Ҫ����λ��
            if (C1 == 0) { Chp = true; double t; t = X1; X1 = X2; X2 = t; t = Y1; Y1 = Y2; Y2 = t; }

            if ((C1 & 0x01) != 0) //��߽�
            {
                X = this.getClipExtendMinX();// this._Extend.MinX;
                Y = ((X - X1) / (X2 - X1)) * (Y2 - Y1) + Y1;
                X1 = X; Y1 = Y; continue;
            }
            if ((C1 & 0x02) != 0) //�ұ߽�
            {
                X = this.getClipExtendMaxX();// this._Extend.MaxX;
                Y = ((X - X1) / (X2 - X1)) * (Y2 - Y1) + Y1;
                X1 = X; Y1 = Y; continue;
            }
            if ((C1 & 0x04) != 0) //�±߽�
            {
                Y = this.getClipExtendMinY();// this._Extend.MinY;
                X = ((Y - Y1) / (Y2 - Y1)) * (X2 - X1) + X1;
                X1 = X; Y1 = Y; continue;
            }
            if ((C1 & 0x08) != 0) //�ϱ߽�
            {
                Y = this.getClipExtendMaxY();// this._Extend.MaxY;
                X = ((Y - Y1) / (Y2 - Y1)) * (X2 - X1) + X1;
                X1 = X; Y1 = Y; continue;
            }
        }
    }

    /// <summary> ��ָ������������
    /// ��λ����������һλ��1����������߽���ࡢ�ڶ�λ��1���������ұ߽����
    ///             ����λ��1���������±߽���ࡢ����λ��1���������ϱ߽����
    /// </summary>
    /// <param name="Pt"></param>
    /// <returns></returns>
    private int Codec(Coordinate Pt)
    {
        return Codec(Pt.getX(), Pt.getY());
    }
    private int Codec(double X, double Y)
    {
        int c = 0;
        //if (Y > Extend.MaxY) ����//�ϱ߽� ��xmin��ymin���ͣ�xmax��ymax��Ϊ�������½ǡ����Ͻ����ꡣ
        //    c = c | 0x08;
        //else if (Y < Extend.MinY)   //�±߽�
        //    c = c | 0x04;
        //if (X > Extend.MaxX)   //�ұ߽�
        //    c = c | 0x02;
        //else if (X < Extend.MinX)  //��߽�
        //    c = c | 0x01;

        if (Y > this.getClipExtendMaxY())  //�ϱ߽� ��xmin��ymin���ͣ�xmax��ymax��Ϊ�������½ǡ����Ͻ����ꡣ
            c = c | 0x08;
        else if (Y < this.getClipExtendMinY())   //�±߽�
            c = c | 0x04;
        if (X > this.getClipExtendMaxX())   //�ұ߽�
            c = c | 0x02;
        else if (X < this.getClipExtendMinX())  //��߽�
            c = c | 0x01;
        return c;
    }


    //2����Ŀ����ü�
    /// <summary>��Ŀ����ü�
    /// </summary>
    /// <param name="_MapCoorList"></param>
    /// <param name="deltX"></param>
    /// <param name="deltY"></param>
    /// <returns></returns>
    public android.graphics.Point[] ClipPolygon(List<Coordinate> _MapCoorList)
    {
        return this.ClipPolygon(_MapCoorList, 0, 0);
    }
    public android.graphics.Point[] ClipPolygon(List<Coordinate> _MapCoorList, int deltX, int deltY)
    {
        //Sutherland-Hodgman�㷨Ҳ����߲ü���
        List<Coordinate> LSide = this.ClipSide(_MapCoorList, 1);  //��� 
        List<Coordinate> TSide = this.ClipSide(LSide, 2);         //�ϱ�
        List<Coordinate> RSide = this.ClipSide(TSide, 3);         //�ұ�
        List<Coordinate> BSide = this.ClipSide(RSide, 4);         //�±�

        LSide.clear(); LSide = null; TSide.clear(); TSide = null; RSide.clear(); RSide = null;

        android.graphics.Point[] OPF = new android.graphics.Point[BSide.size()];
        int j = 0; 

        for (Coordinate Pt : BSide)
        {
        	android.graphics.Point adp = this.MapToScreen(Pt);
            OPF[j++] = new android.graphics.Point(adp.x + deltX, adp.y + deltY);
        }
        BSide.clear(); BSide = null;
        return OPF;
    }

    /// <summary>��ָ���ı���ָ�������괮
    /// </summary>
    /// <param name="CoorList"></param>
    /// <param name="WhichSide"></param>
    /// <returns></returns>
    private List<Coordinate> ClipSide(List<Coordinate> CoorList, int WhichSide)
    {
        int Flag = 0;   //ǰһ���Ƿ��ڴ��бߵ��ڲ�(0-�ڲ࣬1-���)
        List<Coordinate> SideList = new ArrayList<Coordinate>();
        if (CoorList.size() == 0) return SideList;
        
        //���һ�㼴�ǵ�һ�����ǰһ�㣬�������һ�����Ƿ����ڲ�
        Coordinate S = CoorList.get(CoorList.size() - 1);   //ǰһ��
        Flag = this.InnerSide(S, WhichSide);
        for (Coordinate P : CoorList)
        {
            if (this.InnerSide(P, WhichSide) == 0)  //���ڲ�
            {
                if (Flag == 1)   //ǰһ���������
                {
                    Flag = 0;  /*���⵽�ڵ����������־��0,��Ϊ��һ��ѭ����ǰһ���־*/
                    SideList.add(this.GetSideIntersect(P, S, WhichSide));
                }
                SideList.add(P);
            }
            else
            {
                if (Flag == 0)  /*ǰһ�������ڲ���*/
                {
                    Flag = 1;    /*���ڵ�������������־��1,��Ϊ��һ��ѭ����ǰһ���־*/
                    SideList.add(this.GetSideIntersect(P, S, WhichSide));
                }
            }
            S = P;
        }

        return SideList;
    }

    /// <summary>���߶���ָ���ߵ�ֱ�ߵĽ���
    /// </summary>
    /// <param name="S"></param>
    /// <param name="P"></param>
    /// <param name="WhichSide"></param>
    /// <returns></returns>
    private Coordinate GetSideIntersect(Coordinate S, Coordinate P, int WhichSide)
    {
        double X1 = S.getX(), Y1 = S.getY(), X2 = P.getX(), Y2 = P.getY();
        double X = 0, Y = 0;
        switch (WhichSide)
        {
            case 1:   //���
                X = this.getClipExtendMinX();// this.Extend.MinX; 
                Y = ((X - X1) / (X2 - X1)) * (Y2 - Y1) + Y1;
                break;
            case 2:   //�ϱ�
                Y = this.getClipExtendMaxY();  //this.Extend.MaxY;
                X = ((Y - Y1) / (Y2 - Y1)) * (X2 - X1) + X1;
                break;
            case 3:   //�ұ�
                X = this.getClipExtendMaxX();// this.Extend.MaxX;
                Y = ((X - X1) / (X2 - X1)) * (Y2 - Y1) + Y1;
                break;
            case 4:   //�±�
                Y = this.getClipExtendMinY();// this.Extend.MinY;
                X = ((Y - Y1) / (Y2 - Y1)) * (X2 - X1) + X1;
                break;
        }
        return new Coordinate(X, Y);


    }

    /// <summary>�ж�ָ������ָ���ߵ���һ��[��ǰ�ӿ�](0-�ڲ࣬1-���)
    /// </summary>
    /// <param name="Pt"></param>
    /// <param name="WhichSide">1-��ߣ�2-�ϱߣ�3-�ұߣ�4-�±�</param>
    /// <returns></returns>
    private int InnerSide(Coordinate Pt, int WhichSide)
    {
        switch (WhichSide)
        {
            case 1:   //���
                if (Pt.getX() >= this.getExtend().getMinX()) return 0; else return 1;
            case 2:   //�ϱ�
                if (Pt.getY() <= this.getExtend().getMaxY()) return 0; else return 1;
            case 3:   //�ұ�
                if (Pt.getX() <= this.getExtend().getMaxX()) return 0; else return 1;
            case 4:   //�±�
                if (Pt.getY() >= this.getExtend().getMinY()) return 0; else return 1;
        }
        return -1;
    }

    /// <summary>�ж��Ƿ��ڵ�ǰ�Ӵ��ڲ�
    /// </summary>
    /// <param name="ptCoor"></param>
    /// <returns></returns>
    public boolean InViewExtend(Coordinate ptCoor)
    {
        if (ptCoor.getX() >= this.getExtend().getMinX() && ptCoor.getX() <= this.getExtend().getMaxX())
            if (ptCoor.getY() >= this.getExtend().getMinY() && ptCoor.getY() <= this.getExtend().getMaxY())
            {
                return true;
            }
        return false;
    }

}
