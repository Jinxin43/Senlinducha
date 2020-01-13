package lkmap.Spatial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Line;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Enum.lkPartType;

public class Poly_Intersect 
{

	/**
	 * ���������ཻ����
	 * @param Ply1
	 * @param Ply2
	 * @return 
	 */
	public List<HashMap<String,Object>> Poly_Intersect(Polygon Ply1,Polygon Ply2)
	{
		List<HashMap<String,Object>> interPartList = new ArrayList<HashMap<String,Object>>();
		
		//�Ƿ���Ч�����ֻ�������������ཻ��������Ч���
		boolean voidArea = false;  
		
		for(int i=0;i<Ply1.getPartCount();i++)
		{
			Part Pa1 = Ply1.GetPartAt(i).Clone();
			Pa1.SetPartType(lkPartType.enPoly);
			for(int j=0;j<Ply2.getPartCount();j++)
			{
				Part Pa2 = Ply2.GetPartAt(j).Clone();
				Pa2.SetPartType(lkPartType.enPoly);
				if (i==0 && j==0)voidArea=true;else voidArea=false;
				
				//������Part�ཻ����
				List<List<Coordinate>> subPartVertexList = this.Part_Intersect(Pa1, Pa2);
				if (subPartVertexList!=null)
				{
					for(List<Coordinate> CoorList:subPartVertexList)
					{
						Part part = new Part();
						part.setVertext(CoorList);
						double Area = part.CalArea();
						if (!voidArea)Area *= -1;
						HashMap<String,Object> HMSub = new HashMap<String,Object>();
						HMSub.put("Area", Area);
						HMSub.put("Part", part);
						interPartList.add(HMSub);
					}
				}
			}
		}
		return interPartList;
	}
	
	/**
	 * ���ཻ����
	 * @param Ply1
	 * @param Ply2
	 * @return
	 */
	private List<List<Coordinate>> Part_Intersect(Part part1,Part part2)
	{
		List<Coordinate> CoorList1 = null;
		List<Coordinate> CoorList2 = null;
		
		//�ж������ֵ��ཻ�����������ֱཻ�ӷ���
		if (!part1.getEnvelope().Intersect(part2.getEnvelope()))return null;
		
		//Ԥ������
		HashMap<String,Object> PredoInfo = this.PreDoPolygon(part1,part2);
		if (PredoInfo.get("��ϵ").equals("")) return null;
		if (PredoInfo.get("��ϵ").equals("P2inP1"))  //�ж�Part1�Ƿ���ȫ����Part2��Part2��Part1�ڲ�
		{
			List<List<Coordinate>> SubPolyList1 = new ArrayList<List<Coordinate>>();
			SubPolyList1.add(part2.getVertexList());
			return SubPolyList1;
		}
		if (PredoInfo.get("��ϵ").equals("P1inP2"))  //�ж�Part2�Ƿ���ȫ����Part1��Part1��Part2�ڲ�
		{
			List<List<Coordinate>> SubPolyList1 = new ArrayList<List<Coordinate>>();
			SubPolyList1.add(part1.getVertexList());
			return SubPolyList1;
		}
		
		CoorList1 = (List<Coordinate>)PredoInfo.get("CoorList1");
		CoorList2 = (List<Coordinate>)PredoInfo.get("CoorList2");
		
		//����������α��߽���
		List<InterPoint> ary1 = new ArrayList<InterPoint>();
		List<InterPoint> ary2 = new ArrayList<InterPoint>();
		for(int i=0;i<CoorList1.size()-1;i++)
		{
			Line L1 = new Line(CoorList1.get(i),CoorList1.get(i+1));
			
			for(int j=0;j<CoorList2.size()-1;j++)
			{
				Line L2 = new Line(CoorList2.get(j),CoorList2.get(j+1));
				
				Coordinate jp1=new Coordinate();Coordinate jp2=new Coordinate();
				int IPCount = L1.Intersect(L2, jp1, jp2);
				if (IPCount==1)
				{
					InterPoint ip1 = new InterPoint();
					ip1.DH = ary1.size()+1;
					ip1.mPoint = jp1;
					ip1.beforeVertexIndex = i;
					ip1.ToBeforeVertexDistance = this.CalDistance(CoorList1.get(i), jp1);
					ary1.add(ip1);
					
					InterPoint ip2 = new InterPoint();
					ip2.DH = ary2.size()+1;
					ip2.mPoint = jp1;
					ip2.beforeVertexIndex = j;
					ip2.ToBeforeVertexDistance = this.CalDistance(CoorList2.get(j), jp1);
					ary2.add(ip2);
				}
			}
		}
		
		//�Խ��㼯�Ͻ�����ʱ������
		this.InterPointSort(ary1);this.InterPointSort(ary2);
		
		
		//ȷ������ĳ���״̬��1-����2-��
		this.OutInStatus(CoorList1, CoorList2, ary1, ary2);
		this.OutInStatus(CoorList2, CoorList1, ary2, ary1);
		
		//�����������飬��ȡ�ཻ�����
		List<List<Coordinate>> SubPolyList = new ArrayList<List<Coordinate>>();
		
		
		List<InterPoint> ary = ary1;
		List<Coordinate> coorList = CoorList1;
		int CalListIndex = 1;
		//�ཻ����ε������б�
		List<Coordinate> SubPoly = new ArrayList<Coordinate>();
		
		//��ȡһ�����㽻��
		InterPoint inStartPoint = this.GetUnCheckInPoint(ary);
		InterPoint inCircleStartPoint = this.GetUnCheckInPoint(ary);
		do
		{
			SubPoly.add(inStartPoint.mPoint);inStartPoint.check=true;
			
			//��һ��û�д�����Ľ���
			InterPoint nextInterPoint = this.GetNextPoint(inStartPoint, ary);
			
			if (nextInterPoint!=null)
			{
				nextInterPoint.check=true;
				
				//�������֮��Ľڵ����
				for(int c=inStartPoint.beforeVertexIndex+1;c<=nextInterPoint.beforeVertexIndex;c++)
				{
					SubPoly.add(coorList.get(c));
				}
				
				//�������괮��ʹ�����뵽��һ�������
				if (CalListIndex==1) {ary = ary2;coorList = CoorList2;CalListIndex=2;}
				else if (CalListIndex==2) {ary = ary1;coorList = CoorList1;CalListIndex=1;}
				
				if (inCircleStartPoint.mPoint.Equal(nextInterPoint.mPoint))
				{
					SubPolyList.add(SubPoly);
					SubPoly = new ArrayList<Coordinate>();
					
					//��ȡû�д�������뽻��
					inStartPoint = this.GetUnCheckInPoint(ary1);
					inCircleStartPoint = this.GetUnCheckInPoint(ary1);
					CalListIndex = 1;ary = ary1;coorList = CoorList1;
					continue;
				}
				
				//�õ���һ�б��о�����ͬ����Ľ���
				inStartPoint = this.GetEqualInterPointByCoordinate(nextInterPoint.mPoint, ary);
			} else
			{
				//��������Ժ�û�н�������
				for(int idx=inStartPoint.beforeVertexIndex+1;idx<coorList.size();idx++)
				{
					SubPoly.add(coorList.get(idx));
				}
				SubPolyList.add(SubPoly);
				
				break;
			}
			
		}while(inStartPoint!=null);
		
		return SubPolyList;
//		//��ȡ�ཻ����ε�����㼰���
//		String ResultInfo = "";
//		for(List<Coordinate> CoorList:SubPolyList)
//		{
//			Part part = new Part();
//			part.setVertext(CoorList);
//			double a = part.CalArea();
//			
//			List<String> CoorStrList = new ArrayList<String>();
//			for(Coordinate Coor:CoorList)CoorStrList.add(Coor.ToString());
//			ResultInfo += "�����"+a+"\n���괮��"+CoorStrList.size()+"\n"+Tools.JoinT("\n", CoorStrList)+"\r\n";
//			
//		}
	}

	//�õ�ָ���������һ������
	private InterPoint GetNextPoint(InterPoint ip,List<InterPoint> ary)
	{
		for(int i=0;i<ary.size()-1;i++)
		{
			if (ary.get(i).mPoint.Equal(ip.mPoint))
			{
				return ary.get(i+1);
			}
		}
		return null;
	}
	
	//�õ���״̬�Ľ���
	private InterPoint GetUnCheckInPoint(List<InterPoint> ary)
	{
		for(InterPoint ip:ary)
		{
			if (!ip.check && ip.OutInt==2)   //û�д����������״̬
			{
				return ip;
			}
		}
		return null;
	}
	
	//��ȡ������ͬ������InterPoint
	private InterPoint GetEqualInterPointByCoordinate(Coordinate Coor,List<InterPoint> ary)
	{
		for(InterPoint ip :ary)
		{
			if (ip.mPoint.Equal(Coor)) return ip;
		}
		return null;
	}
	
	/**
	 * Ԥ������
	 * @param part1
	 * @param part2
	 * @return 
	 */
	private HashMap<String,Object> PreDoPolygon(Part part1,Part part2)
	{
		HashMap<String,Object> result = new HashMap<String,Object>();
		result.put("��ϵ", "�ཻ");
		
		//�ж�����λ�ù�ϵ������������
		int OutVertexIndex1 = -1;  //��һ��������ڵ�����
		int InnerVertexCount1 = 0;  //�����ڵ�ĸ���
		for(int i=0;i<part2.getVertexList().size();i++)
		{
			if (part1.ContainsPoint(part2.getVertexList().get(i)))InnerVertexCount1++;
			else if (OutVertexIndex1==-1)OutVertexIndex1 = i;
		}
		if (InnerVertexCount1==part2.getVertexList().size())  //�ж�Part1�Ƿ���ȫ����Part2
		{
			result.put("��ϵ", "P2inP1"); return result;
		}
		
		int OutVertexIndex2 = -1;  //��һ��������ڵ�����
		int InnerVertexCount2 = 0;  //�����ڵ�ĸ���
		for(int i=0;i<part1.getVertexList().size();i++)
		{
			if (part2.ContainsPoint(part1.getVertexList().get(i)))InnerVertexCount2++;
			else if (OutVertexIndex2==-1)OutVertexIndex2 = i;
		}
		if (InnerVertexCount2==part1.getVertexList().size())  //�ж�Part2�Ƿ���ȫ����Part1
		{
			result.put("��ϵ", "P1inP2"); return result;
		}
		
		//���ཻ���������
		if (InnerVertexCount1==0 && InnerVertexCount2==0){result.put("��ϵ", ""); return result;}
		
		//����˵������ڵ����
		List<Coordinate> CoorList1 = new ArrayList<Coordinate>();
		List<Coordinate> CoorList2 = new ArrayList<Coordinate>();
		for(Coordinate Coor :part1.getVertexList())CoorList1.add(Coor);CoorList1.add(part1.getVertexList().get(0));
		for(Coordinate Coor :part2.getVertexList())CoorList2.add(Coor);CoorList2.add(part2.getVertexList().get(0));
		
		if (OutVertexIndex1!=0)
		{
			CoorList2.clear();
			for(int i=OutVertexIndex1;i<part2.getVertexList().size();i++)CoorList2.add(part2.getVertexList().get(i));
			for(int i=0;i<=OutVertexIndex1;i++)CoorList2.add(part2.getVertexList().get(i));
		}
		if (OutVertexIndex2!=0)
		{
			CoorList1.clear();
			for(int i=OutVertexIndex2;i<part1.getVertexList().size();i++)CoorList1.add(part1.getVertexList().get(i));
			for(int i=0;i<=OutVertexIndex2;i++)CoorList1.add(part1.getVertexList().get(i));
		}
		
		result.put("CoorList1", CoorList1);
		result.put("CoorList2", CoorList2);
		return result;
		
	}
	
	//���㼯�ϳ���״̬
	private void OutInStatus(List<Coordinate> CoorList1,List<Coordinate> CoorList2, List<InterPoint> ary1,List<InterPoint> ary2)
	{
		//��һ���ཻ��ĳ���״̬
		InterPoint ip1 = ary1.get(0);
		InterPoint ip2 = null;
		for(InterPoint interp:ary2)
		{
			if (ip1.mPoint.Equal(interp.mPoint))ip2 = interp;
		}
		

		Coordinate P1 = CoorList1.get(ip1.beforeVertexIndex);
		Coordinate P2 = CoorList1.get(ip1.beforeVertexIndex+1);
		
		Coordinate Q1 = CoorList2.get(ip2.beforeVertexIndex);
		Coordinate Q2 = CoorList2.get(ip2.beforeVertexIndex+1);
		
		Coordinate P1P2 = new Coordinate(P1.getX()-P2.getX(),P1.getY()-P2.getY());
		
		Coordinate Q1Q2 = new Coordinate(Q1.getX()-Q2.getX(),Q1.getY()-Q2.getY());
				
		double P1P2xQ1Q2 = P1P2.getX() * Q1Q2.getY()-P1P2.getY()*Q1Q2.getX();
		if (P1P2xQ1Q2 > 0) ip1.OutInt=1; else ip1.OutInt=2;
		

		for(int i=1;i<ary1.size();i++)
		{
			if (ary1.get(i-1).OutInt==1)ary1.get(i).OutInt=2;
			else ary1.get(i).OutInt=1;
		}
	}
	
	//�Խ��㼯�Ͻ���������
	private void InterPointSort(List<InterPoint> interPointList)
	{
		for(int i=0;i<interPointList.size();i++)
		{
			InterPoint MinPt = interPointList.get(i);
			for(int j=i;j<interPointList.size();j++)
			{
				InterPoint ip2 = interPointList.get(j);
				if (MinPt.beforeVertexIndex>ip2.beforeVertexIndex)MinPt = ip2;
				if (MinPt.beforeVertexIndex==ip2.beforeVertexIndex)
				{
					if (MinPt.ToBeforeVertexDistance>ip2.ToBeforeVertexDistance)MinPt = ip2;
				}
			}
			interPointList.remove(MinPt);interPointList.add(i, MinPt);
		}
	}
	
	private double CalDistance(Coordinate Coor1,Coordinate Coor2) 
	{
		double d = (Coor1.getX() - Coor2.getX())*(Coor1.getX() - Coor2.getX())+
				   (Coor1.getY() - Coor2.getY())*(Coor1.getY() - Coor2.getY());
		
		return Math.sqrt(d);
	}
	
	
	private class InterPoint
	{
		public Coordinate mPoint = null;   //��������
		public int beforeVertexIndex;    //���������߶εĽ�һ�ڵ�����
		public double ToBeforeVertexDistance=0;   //����һ���߶����ж����������
		
		public int OutInt = 0;    //1-����2-��
		public boolean check = false;   //�Ƿ����
		
		public int DH = -1;
	}
}
