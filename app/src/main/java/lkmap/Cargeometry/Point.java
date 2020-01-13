package lkmap.Cargeometry;

import java.util.ArrayList;
import java.util.List;

import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Index.MapCellIndex;
import lkmap.Index.T4Index;
import lkmap.Map.Param;
import lkmap.Map.StaticObject;
import lkmap.Tools.Tools;

public class Point extends Geometry 
{
    public Point()
    {
    	//��ʼԭʼ״̬
        this.setStatus(lkGeometryStatus.enNormal);
    }

    //�򵥵�
    public Point(double X,double Y)
    {
        Part part = new Part();
        part.getVertexList().add(new Coordinate(X, Y));
        this.AddPart(part);
    }
    public Point(Coordinate Coor)
    {
        Part part = new Part();
        part.getVertexList().add(Coor);
        this.AddPart(part);
    }

    //X����-������ֻ��IsSimple=true��Ч
    public Coordinate getCoordinate()
    {
        return this.GetPartAt(0).getVertexList().get(0);
    }
    
    public Coordinate getCenterPoint()
    {
    	return getCoordinate();
    }

//    //ʵ��ռ����
//    private SpatialOperator _SpatialOperator = null;
//    public SpatialOperator SpatialOperator
//    {
//        get
//        {
//            if (_SpatialOperator == null) _SpatialOperator = new SpatialOperator();
//            return _SpatialOperator;
//        }
//    }
//
//    //ʵ��ռ��ϵ
//    public SpatialRelation _SpatialRelation = null;
//    public SpatialRelation SpatialRelation
//    {
//        get
//        {
//            if (_SpatialRelation == null) _SpatialRelation = new SpatialRelation(this);
//            return _SpatialRelation;
//        }
//    }


    //��������֮��ľ���
    public double DistanceTo(Coordinate desPoint)
    {
    	//��ʵ��
//        if (this.IsSimple())
//        {
            return Tools.GetTwoPointDistance(this.GetPartAt(0).getVertexList().get(0), desPoint);
//        }
//        else   //����ಿ�ֵ���ֱ���㣬�����������
//        {
//            double MinDistance = Double.MAX_VALUE;
//            for (Coordinate Pt : this.getItems())
//            {
//                double D = Tools.GetTwoPointDistance(Pt, desPoint);
//                if (D < MinDistance) MinDistance = D;
//            }
//            return MinDistance;
//        }
    }


    //��¡ʵ��
	@Override
	public Geometry Clone() {
//        List<Coordinate> ptList = new ArrayList<Coordinate>();
//        for(Coordinate Coor : this.GetAllCoordinateList())
//        {
//            ptList.add(Coor.Clone());
//        }
        Point newPoint = new Point(this.GetPartAt(0).getVertexList().get(0).getX(),this.GetPartAt(0).getVertexList().get(0).getY()); 
        return newPoint;
		//return null;
	}

    //��ѡʵ��
	@Override
	public boolean HitTest(Coordinate HitPoint, double Tolerance,Boolean isBGLayer) {
        if (this.DistanceTo(HitPoint) <= Tolerance) return true; else return false;

	}

	@Override
	public boolean Offset(double OffsetX, double OffsetY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public lkGeoLayerType GetType() {
		// TODO Auto-generated method stub
		return lkGeoLayerType.enPoint;
	}

}
