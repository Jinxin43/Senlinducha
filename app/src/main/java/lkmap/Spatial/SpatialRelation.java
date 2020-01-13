package lkmap.Spatial;

import lkmap.Cargeometry.Envelope;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Line;
import lkmap.Cargeometry.Polyline;

public class SpatialRelation 
{
    private Geometry _Geometry = null;

    public SpatialRelation(Geometry pGeometry)
    {
        _Geometry = pGeometry;
    }

    private int GetGeometryType(Geometry pGeometry)
    {
        //�жϻ�ʵ�������
        switch(pGeometry.GetType())
        {
        	case enPoint:
        		return 0;        //��
        	case enPolyline:
        		return 1;		 //��
        	case enPolygon:
        		return 2;	     //��
        }
        return -1;
    }

    //����ʵ��ת������ʵ��
    private Polyline ConvertToPolyline(Geometry pGeometry)
    {
        switch (this.GetGeometryType(pGeometry))
        {
            case 1:
                return (Polyline)pGeometry;
            case 2:
                //return ((Polygon)(pGeometry)).ConvertToPolyline();
        }
        return null;
    }
    private Polyline ConvertToPolyline()
    {
    	return this.ConvertToPolyline(_Geometry);
    }


    //������ϵ[Contains(Envelope)]

    //�ж��Ƿ���ָ���ľ����ڲ�
    //��Ҫ˼·���ж�ʵ�����Ӿ�������ָ�����ε��ڲ��������ָ�������ڲ�������ȫ�������ϵ
    public boolean Contains(Envelope pEnvelope)
    {
        return (pEnvelope.Contains(_Geometry.getEnvelope()));
    }

    //�ཻ��ϵ[Intersect]

    /*�ж�ʵ����ָ���������ʵ���Ƿ���
     ˼·���ж�ֱ�߶��������ʵ���Ƿ��ཻ���ֿ��۷�Ϊ��ֱ�߶��Ƿ��ཻ
     ���裺1���ж�Polyline����Ӿ����Ƿ���ָ��ʵ�����Ӿ����ཻ�����ཻ�򷵻�false
           2����Polyline�ֽ�Ϊ����ֱ�߶Σ��ֱ���ָ��ʵ���ֵ��߶��ж��Ƿ��ཻ
           3���������ж�һ��ֱ�߶ε���Ӿ����Ƿ���ָ�������ཻ��������ཻ�򷵻�false
           4���ټ����ж�ÿ��ֱ�߶��Ƿ���ָ�������ཻ���ֶ�Ϊֱ�߶���ֱ���ཻ�����
     */
    public boolean Intersect(Geometry pGeometry /*����״ʵ����Ч*/)
    {
        Polyline pPolyline1 = this.ConvertToPolyline();
        Polyline pPolyline2 = this.ConvertToPolyline(pGeometry);

        //1���ж���Polyline����Ӿ����Ƿ���ָ�������ཻ�����ཻ�򷵻�false
        if (!pPolyline2.getEnvelope().Intersect(pPolyline1.getEnvelope())) return false;

        //2��������Polyline�ֽ�Ϊ����ֱ�߶Σ��ֱ��ж��Ƿ��ཻ
        int CoorCount1 = pPolyline1.GetPartAt(0).getVertexList().size(); 
        int CoorCount2 = pPolyline2.GetPartAt(0).getVertexList().size(); 

        Line L1, L2;
        for (int i = 0; i <= CoorCount1 - 2; i++)
        {
            L1 = new Line(pPolyline1.GetPartAt(0).getVertexList().get(i), pPolyline1.GetPartAt(0).getVertexList().get(i + 1));
            for (int j = 0; j <= CoorCount2 - 2; j++)
            {
                L2 = new Line(pPolyline2.GetPartAt(0).getVertexList().get(j), pPolyline2.GetPartAt(0).getVertexList().get(j + 1));
                if (L1.Intersect(L2))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
