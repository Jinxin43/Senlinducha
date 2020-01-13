package lkmap.Cargeometry;

import java.util.ArrayList;
import java.util.List;
import lkmap.CoordinateSystem.ProjectSystem;
import lkmap.CoordinateSystem.Project_GK;
import lkmap.CoordinateSystem.Project_Web;
import lkmap.Enum.lkPartType;
import lkmap.Map.StaticObject;
import lkmap.Tools.Tools;

public class Part 
{
    public Part() { }
    public Part(List<Coordinate> vertexList)
    {
        this._VertexList = vertexList;
    }
    
    

    //��������
    private List<Coordinate> _VertexList = new ArrayList<Coordinate>();
    public List<Coordinate> getVertexList(){return this._VertexList;}
    public void setVertext(List<Coordinate> vertexList){this._VertexList = vertexList;}

    /**
     * ��ȡPart����
     * @return
     */
    public Polyline GetBorder()
    {
    	Polyline PL = new Polyline();
    	Part part = new Part();
    	for(Coordinate Coor:this._VertexList)part.getVertexList().add(Coor);
    	PL.AddPart(part);
    	return PL;
    }

    //��Ӿ���
    private Envelope _Envelope = null;
    public Envelope getEnvelope()
    {
        if (this._Envelope == null)this.UpdateEnvelope(); return this._Envelope;
    }
    //public void setEnvelope(Envelope env){this._Envelope = env;}

    /**
     * ���������Ӿ���
     */
    public void UpdateEnvelope()
    {
        double MinX = -1, MinY = -1, MaxX = -1, MaxY = -1;
        boolean First = true;
        for(Coordinate Pt : this._VertexList)
        {
            if (First) { MinX = Pt.getX(); MinY = Pt.getY(); MaxX = MinX; MaxY = MinY; First = false; }
            if (MinX > Pt.getX()) MinX = Pt.getX();
            if (MaxX < Pt.getX()) MaxX = Pt.getX();
            if (MinY > Pt.getY()) MinY = Pt.getY();
            if (MaxY < Pt.getY()) MaxY = Pt.getY();
        }
        //���������Ӿ���
        this._Envelope = new Envelope(MinX, MaxY, MaxX, MinY);
    }

    /**
     * ���㳤��
     * @return
     */
    public double CalLength()
    {
        int VertexCount = this._VertexList.size();
        double LineLength = 0;
        for (int i = 0; i < VertexCount-1; i++)
        {
            LineLength += Tools.GetTwoPointDistance(this._VertexList.get(i), this._VertexList.get(i+1));
        }
        return LineLength;
    }

    /**
     * �������
     * @return
     */
    public double CalArea()
    {
    	return Math.abs(this.CalAreaContainFlip());
    }
    
    /**
     * �պϴ���
     */
    public void Closed()
    {
    	if(this.getVertexList().size()>0)
    	{
    		Coordinate PStart = this.getVertexList().get(0);
        	Coordinate EStart = this.getVertexList().get(this.getVertexList().size()-1);
        	if (!PStart.Equal(EStart))this.getVertexList().add(PStart);
    	}
    	
    }
    
    /**
     * ����ԭʼ�����Ҳ���Ǵ�������
     * @return
     */
    private double CalAreaContainFlip()
    {
        double PolyArea = 0; 
        int NumPoint = this.getVertexList().size();
        
        if (NumPoint >= 3)
        {
        	List<Coordinate> vertextList = null;
        	
        	//���Ϊ��γ�����꣬����ת��ΪͶӰ����������
        	if (StaticObject.soProjectSystem.GetCoorSystem().GetName().equals("WGS-84����"))
        	{
        		//���뾭��
        		Coordinate Coor1 = this.getVertexList().get(this.getVertexList().size()/2);
        		Coordinate MidPoint = Project_Web.Web_XYToBL(Coor1.getX(),Coor1.getY());
        		StaticObject.soProjectSystem.GetCoorSystem().SetCenterMeridian(ProjectSystem.AutoCalCenterJX(MidPoint.getX(),MidPoint.getY()));
        		vertextList = new ArrayList<Coordinate>();
        		
        		//ת��ΪͶӰ����
        		for(Coordinate jwCoor:this.getVertexList())
        		{
        			Coordinate jwd = Project_Web.Web_XYToBL(jwCoor.getX(),jwCoor.getY());
        			Coordinate xyCoordinate = Project_GK.GK_BLToXY(jwd.getX(), jwd.getY(), StaticObject.soProjectSystem.GetCoorSystem());
        			if(xyCoordinate != null)
        			{
        				vertextList.add(xyCoordinate);
        			}
        			
        		}
        	} else
        	{
        		vertextList = this.getVertexList();
        	}
        	
            double sum = 0; int m = 1;int StartPointIndex = 0;
            Coordinate LP0, LPm, LPm1;
            double X1, Y1, X2, Y2;
            for (; ; )
            {
                if (NumPoint < 3) break;
                LP0 = vertextList.get(StartPointIndex);
                LPm = vertextList.get(StartPointIndex + m);
                LPm1 = vertextList.get(StartPointIndex + m + 1);
                //  R1.X = (LPm.X - LP0.X);
                X1 = ((double)(LPm.getX() - LP0.getX()));

                //    R1.Y = (LPm.Y - LP0.Y);
                Y1 = ((double)(LPm.getY() - LP0.getY()));

                //    R2.X = (LPm1.X - LPm.X);
                X2 = ((double)(LPm1.getX() - LPm.getX()));

                //    R2.Y = (LPm1.Y - LPm.Y);
                Y2 = ((double)(LPm1.getY() - LPm.getY()));

                //sum += (R1.X * R2.Y - R2.X * R1.Y);
                sum += (X1 * Y2 - X2 * Y1);

                m++;
                NumPoint--;
            }
            //sum = (sum > 0) ? (sum / 2.0) : (-sum / 2.0);  //ͨ���������ж϶�ߵ�ʱ����Ҳ���������Ǹ�
            PolyArea = sum/2;
        }
        return PolyArea;
    }
    
    //�Զ�����Part�����ͣ���Ҫ���ڶ�ȡ����
    public void AutoSetPartType()
    {
    	double area = this.CalAreaContainFlip();
    	if (area<0) this._PartType=lkPartType.enHole;
    	else this._PartType=lkPartType.enPoly;
    }
    
    private lkPartType _PartType = lkPartType.enPoly;
    /**
     * ����part���ͣ����Ϊ�����Զ�������ڵ㷽����ʱ��
     */
    public void SetPartType(lkPartType partType)
    {
    	double area = this.CalAreaContainFlip();
    	if (partType==lkPartType.enPoly && area<0)Tools.ReverseList(this._VertexList);
    	if (partType==lkPartType.enHole && area>0)Tools.ReverseList(this._VertexList);
    	this._PartType = partType;
    }
    public lkPartType GetPartType(){return this._PartType;}
    
    /**
     * ����Part
     * @return
     */
    public Part Clone()
    {
    	List<Coordinate> newCoorList = new ArrayList<Coordinate>();
    	for(Coordinate Coor:this.getVertexList())newCoorList.add(Coor.Clone());
    	Part newPart = new Part();
    	newPart.setVertext(newCoorList);
    	//newPart.SetPartType(this.GetPartType());
    	newPart._PartType = this.GetPartType();
    	return newPart;
    }

    /**
     * ���ָ����HitPoint����ʵ���ڵ�����������ֵ
     * @param HitPoint ����
     * @param Tolerance ���̾���
     * @return ����������ֵ��-1��ʾָ��������û��ѡ�нڵ�
     */
    public int HitVertexTest(Coordinate HitPoint, double Tolerance)
    {
        int VertexIndex = -1;
        double NearestDist = Double.MAX_VALUE;

        for (int i = 0; i < this.getVertexList().size(); i++)
        {
            double D = Tools.GetTwoPointDistance(HitPoint, this.getVertexList().get(i),false);
            if (D<NearestDist)
            {
                NearestDist = D; VertexIndex = i; 
            }
        }
        if (NearestDist <= Tolerance) return VertexIndex; else {return -1;}
    }
    
    /**
     * ����Ƿ���ָ���㴦ѡ��ʵ�壬������Segment����ֵ
     * @param SelPoint ����
     * @param Tolerance ����
     * @return ѡ�е�SegmentƬ�ε�������-1��ʾû��ѡ��
     */
    public int HitSegmentTest(Coordinate SelPoint, double Tolerance)
    {
        //�ж��Ƿ��������Ӿ����ڲ���������ⲿ���˳�
        if (!this.getEnvelope().ContainsPoint(SelPoint)) return -1;
        for (int i = 0; i <= this.getVertexList().size() - 2; i++)
        {
            Coordinate LinePoint1 = this.getVertexList().get(i);
            Coordinate LinePoint2 = this.getVertexList().get(i + 1);
            Line SegmentLine = new Line(LinePoint1, LinePoint2);
            if (SegmentLine.PointToLineDistance(SelPoint,Tolerance)) return i;
        }
        return -1;
    }

    /*������Ƿ������ڣ������������ѡ��
     * ˼·����ָ���ĵ�������һ�����ߣ���������������������������㣬����ڶ�����ڲ���
     *       Ϊ0��ż�����ڶ�����ⲿ
     */
 	public boolean ContainsPoint(Coordinate HitPoint)
     {
         //1���жϵ��Ƿ��ڶ���ε���Ӿ����ڲ�
         if (!this.getEnvelope().ContainsPoint(HitPoint)) return false;

         //2��������������
         Coordinate EndPoint = new Coordinate(this.getEnvelope().getMaxX() + 10, HitPoint.getY());
         //Line newLine = new Line(HitPoint, EndPoint);

         //3��ѭ���ж϶���εı��Ƿ����߾��н���
         Coordinate LinePoint1, LinePoint2; int TotalInterPoint = 0;
         //Coordinate Inter1 = new Coordinate(); Coordinate Inter2 = new Coordinate();
         for (int i = 0; i < this.getVertexList().size(); i++)
         {
             LinePoint1 = (Coordinate)this.getVertexList().get(i);

             if (i == this.getVertexList().size() - 1)
             {
                 LinePoint2 = (Coordinate)this.getVertexList().get(0);
             }
             else
             {
                 LinePoint2 = (Coordinate)this.getVertexList().get(i + 1);
             }
             //Line PolyBoundLine = new Line(LinePoint1, LinePoint2);
             
             //������ֱ�߶ν���
             if (this.isIntersect(HitPoint.getX(),HitPoint.getY(),EndPoint.getX(),EndPoint.getY(),
             					LinePoint1.getX(), LinePoint1.getY(), LinePoint2.getX(), LinePoint2.getY()))	
//             int InterPoints = newLine.Intersect(PolyBoundLine, Inter1, Inter2);
//             if (InterPoints == 1) 
             	TotalInterPoint++;
//             //����������������ж��������������Ƿ�λ�ڱ�����ĺ����귶Χ�ڲ�
//             if (InterPoints == 2)
//             {
//                 if (HitPoint.getX() >= PolyBoundLine.getEnvelope().getMinX() && 
//                 	HitPoint.getX() >= PolyBoundLine.getEnvelope().getMaxX())
//                 {
//                     return true;
//                 }
//             }
         }

         if ((TotalInterPoint % 2) == 0) return false; else return true;
     }
	  private boolean isIntersect(double px1, double py1, double px2, double py2,double px3, double py3, double px4, double py4) 
		{
			boolean flag = false; 
			double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3); 
			if (d != 0) 
			{ 
				double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3))/d;  
				double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1))/d; 
				if ((r >= 0) && (r <= 1) && (s >= 0) && (s <= 1)) { flag = true; }
			}
			return flag; 
		}
	  
	  
}
