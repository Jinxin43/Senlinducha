package lkmap.Cargeometry;

import java.util.ArrayList;
import java.util.List;

import dingtu.ZRoadMap.PubVar;

public class Envelope 
{
	public Envelope(Coordinate lefttop, Coordinate rightbottom)
    {
		_LeftTop = lefttop; 
		_RightBottom = rightbottom;
    }
    public Envelope(double LeftTopX, double LeftTopY, double RightBottomX, double RightBottomY)
    {
    	_LeftTop = new Coordinate(LeftTopX, LeftTopY);
        _RightBottom = new Coordinate(RightBottomX, RightBottomY);
    }


    //���Ͻ�����
    private Coordinate _LeftTop = null;
    public Coordinate getLeftTop()
    {
        return _LeftTop;
    }
    public void setLeftTop(Coordinate coor)
    {
    	_LeftTop = coor;
    }


    //���½�����
    private Coordinate _RightBottom = null;
    public Coordinate getRightBottom()
    {
       return _RightBottom;
    }
    public void setRightBottom(Coordinate coor)
    {
    	_RightBottom=coor;
    }

    //Envelope�����ͣ�True������Σ�Ҳ���Ǵ������һ��ġ�False������Σ�Ҳ���Ǵ������󻭵ġ�
    private boolean _Type = true;
    public boolean getType()
    {
         return _Type; 
    }
    public void setType(boolean type)
    {
    	_Type=type;
    }
    
    //��СX����
    public double getMinX()
    {
        return _LeftTop.getX();
    }
    
    //��СY����
    public double getMinY()
    {
        return _RightBottom.getY();
    }
    
    //���X����
    public double getMaxX()
    {
        return _RightBottom.getX();
    }
    
    //���Y����
    public double getMaxY()
    {
        return _LeftTop.getY();
    }

    //���
    public double getHeight()
    {
        return getMaxY() - getMinY();
    }

    //�߶�
    public double getWidth()
    {
         return getMaxX() - getMinX();
    }

    //�������ĵ�����
    public Coordinate getCenter()
    {
       return new Coordinate((_RightBottom.getX() + _LeftTop.getX()) / 2, (_LeftTop.getY() + _RightBottom.getY()) / 2);
    }
    public void setCenter(Coordinate Coor)
    {
        Coordinate cd = this.getCenter();
        Coordinate to = Coor;
        _LeftTop.setX(_LeftTop.getX()+(to.getX()-cd.getX()));
        _LeftTop.setY(_LeftTop.getY()+(to.getY()-cd.getY()));
        
        _RightBottom.setX(_RightBottom.getX()+(to.getX()-cd.getX()));
        _RightBottom.setY(_RightBottom.getY()+(to.getY()-cd.getY()));

    }

    //�ж��Ƿ�Ϊ0�߾���
    public boolean IsZero()
    {
    	double MapDis = 0.00000001;
    	
        if ((this.getWidth() <= MapDis || this.getHeight() <= MapDis) && Math.abs(this.getLeftTop().getX())<=MapDis) return true; else return false;
        
    }

    //�������Σ�ʹ֮��Ϊ�����Σ������߳�Ϊ������
    public Envelope ExtendEnvelope()
    {
        if (this.getWidth() > this.getHeight())
        {
            double OffsetH = (this.getWidth() - this.getHeight()) / 2;
            //this.LeftTop.Y += OffsetH;
            //this.RightBottom.Y -= OffsetH;
            return new Envelope(this.getLeftTop().getX(), this.getLeftTop().getY() + OffsetH, this.getRightBottom().getX(), this.getRightBottom().getY() - OffsetH);
        }
        else
        {
            double OffsetW = (this.getHeight() - this.getWidth()) / 2;
            //this.LeftTop.X -= OffsetW;
            //this.RightBottom.X += OffsetW;
            return new Envelope(this.getLeftTop().getX() - OffsetW, this.getLeftTop().getY(), this.getRightBottom().getX() + OffsetW, this.getRightBottom().getY());
        }
    }

    //�ϲ�������,desEnvelop=���ϲ�����
    public Envelope Merge(Envelope desEnvelop)
    {
    	if (desEnvelop.IsZero()) return this;
        if (this.Contains(desEnvelop)) return new Envelope(this.getLeftTop().Clone(), this.getRightBottom().Clone());
        if (desEnvelop.Contains(this)) return new Envelope(desEnvelop.getLeftTop().Clone(), desEnvelop.getRightBottom().Clone());

        double TwoEnveMinx = Math.min(this.getMinX(), desEnvelop.getMinX());
        double TwoEnveMinY = Math.min(this.getMinY(), desEnvelop.getMinY());
        double TwoEnveMaxX = Math.max(this.getMaxX(), desEnvelop.getMaxX());
        double TwoEnveMaxY = Math.max(this.getMaxY(), desEnvelop.getMaxY());
        return new Envelope(TwoEnveMinx, TwoEnveMaxY, TwoEnveMaxX, TwoEnveMinY);
    }

    //���α���,"desEnvelop"=С����
    public double Factor(Envelope desEnvelop)
    {
        if (desEnvelop.getWidth() <= 0 || desEnvelop.getHeight() <= 0) return 1;
        double WidthScale = this.getWidth() / desEnvelop.getWidth();
        double HeightScale = this.getHeight() / desEnvelop.getHeight();
        if (WidthScale < HeightScale) return HeightScale;
        else return WidthScale;
    }

    //��ָ�������ű������ž���
    public Envelope Scale(double Factor)
    {
        Coordinate CenterPT = this.getCenter();
        return new Envelope((CenterPT.getX() - (this.getWidth()/2) * Factor), (CenterPT.getY() + (this.getHeight()/2) * Factor),
                            (CenterPT.getX() + (this.getWidth() / 2) * Factor), (CenterPT.getY() - (this.getHeight() / 2) * Factor));
    }
    
    //������չָ������
    public void ExtendTo(double distance)
    {
    	this._LeftTop.setX(this._LeftTop.getX()-distance);
    	this._LeftTop.setY(this._LeftTop.getY()+distance);
    	this._RightBottom.setX(this._RightBottom.getX()+distance);
    	this._RightBottom.setY(this._RightBottom.getY()-distance);
    }

    //�ж����������Ƿ��ཻ����ȫ������ϵҲ�����ཻ���
    public boolean Intersect(Envelope desEnvelop)
    {
        double TwoEnveMinX = Math.max(this.getMinX(), desEnvelop.getMinX());
        double TwoEnveMinY = Math.max(this.getMinY(), desEnvelop.getMinY());
        double TwoEnveMaxX = Math.min(this.getMaxX(), desEnvelop.getMaxX());
        double TwoEnveMaxY = Math.min(this.getMaxY(), desEnvelop.getMaxY());
        if (TwoEnveMinX > TwoEnveMaxX || TwoEnveMinY > TwoEnveMaxY)
        {
            return this.Contains(desEnvelop);
        }
        else
        {
            return true;
        }

    }

    //���������ε��ཻ����������ཻ�򷵻ؿ�
    public Envelope GetIntersectEnvelope(Envelope desEnvelop)
    {
        double TwoEnveMinx = Math.max(this.getMinX(), desEnvelop.getMinX());
        double TwoEnveMinY = Math.max(this.getMinY(), desEnvelop.getMinY());
        double TwoEnveMaxX = Math.min(this.getMaxX(), desEnvelop.getMaxX());
        double TwoEnveMaxY = Math.min(this.getMaxY(), desEnvelop.getMaxY());
        if (TwoEnveMinx > TwoEnveMaxX || TwoEnveMinY > TwoEnveMaxY)
        {
            return null;
        }
        else
        {
            return new Envelope(TwoEnveMinx, TwoEnveMaxY, TwoEnveMaxX, TwoEnveMinY);
        }
    }

    //�������������Ƿ�������ȫ������ϵ,innerEnvelopeΪ���������Σ����ڲ���
    public boolean Contains(Envelope innerEnvelope)
    {
        if (!(innerEnvelope.getMinX() >= this.getMinX() && innerEnvelope.getMinX() <= this.getMaxX())) return false;
        if (!(innerEnvelope.getMaxX() >= this.getMinX() && innerEnvelope.getMaxX() <= this.getMaxX())) return false;
        if (!(innerEnvelope.getMinY() >= this.getMinY() && innerEnvelope.getMinY() <= this.getMaxY())) return false;
        if (!(innerEnvelope.getMaxY() >= this.getMinY() && innerEnvelope.getMaxY() <= this.getMaxY())) return false;
        return true;
    }

    //�ж�ָ�����Ƿ�����ھ����ڲ�
    public boolean ContainsPoint(Coordinate Pt)
    {
        return this.ContainsPoint(Pt.getX(), Pt.getY());
    }
    public boolean ContainsPoint(double X, double Y)
    {
        if (X >= this.getMinX() && X <= this.getMaxX())
        {
            if (Y >= this.getMinY() && Y <= this.getMaxY())
            {
                return true;
            }
        }
        return false;
    }

    //������ת������ʵ��
    public Polyline ConvertToPolyline()
    {
        Polyline PL = new Polyline();
        List<Coordinate> CoorList = new ArrayList<Coordinate>();
        CoorList.add(new Coordinate(this.getMinX(), this.getMinY()));
        CoorList.add(this.getLeftTop());
        CoorList.add(new Coordinate(this.getMaxX(), this.getMaxY()));
        CoorList.add(this.getRightBottom());
        CoorList.add(new Coordinate(this.getMinX(), this.getMinY()));
        PL.AddPart(new Part(CoorList));
        return PL;
    }

    //��¡
    public Envelope Clone()
    {
        return new Envelope(this.getLeftTop().Clone(), this.getRightBottom().Clone());
    }

    //�Ƿ���ͬ
    public boolean Equal(Envelope pEnvelope)
    {
        return (this.getLeftTop().Equal(pEnvelope.getLeftTop()) && this.getRightBottom().Equal(pEnvelope.getRightBottom()));
    }
}
