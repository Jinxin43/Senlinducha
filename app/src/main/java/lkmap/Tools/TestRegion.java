package lkmap.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Line;
import lkmap.Cargeometry.Part;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.RegionIterator;
import android.os.Parcel;
import android.util.Log;

public class TestRegion 
{
	public void Test()
	{
		List<Coordinate> CoorList1 = new ArrayList<Coordinate>();
		List<Coordinate> CoorList2 = new ArrayList<Coordinate>();
		
		CoorList1.add(new Coordinate(4710.0000,750.0000));
		CoorList1.add(new Coordinate(4260.0000,610.0000));
		CoorList1.add(new Coordinate(4340.0000,270.0000));
		CoorList1.add(new Coordinate(4520.0000,90.0000));
		CoorList1.add(new Coordinate(4720.0000,120.0000));
		CoorList1.add(new Coordinate(4770.0000,440.0000));
		CoorList1.add(new Coordinate(4820.0000,570.0000));
		CoorList1.add(new Coordinate(5180.0000,530.0000));
		CoorList1.add(new Coordinate(5240.0000,220.0000));
		CoorList1.add(new Coordinate(5200.0000,60.0000));
		CoorList1.add(new Coordinate(5480.0000,62.0000));
		CoorList1.add(new Coordinate(5560.0000,390.0000));
		CoorList1.add(new Coordinate(5562.0000,740.0000));
		CoorList1.add(new Coordinate(5350.0000,850.0000));
		CoorList1.add(new Coordinate(4880.0000,850.0000));
		CoorList1.add(new Coordinate(4710.0000,750.0000));

		CoorList2.add(new Coordinate(4160.0000,292.0000));
		CoorList2.add(new Coordinate(4540.0000,290.0000));
		CoorList2.add(new Coordinate(4610.0000,50.0000));
		CoorList2.add(new Coordinate(5110.0000,90.0000));
		CoorList2.add(new Coordinate(5440.0000,500.0000));
		CoorList2.add(new Coordinate(5520.0000,860.0000));
		CoorList2.add(new Coordinate(5060.0000,890.0000));
		CoorList2.add(new Coordinate(4920.0000,660.0000));
		CoorList2.add(new Coordinate(5140.0000,390.0000));
		CoorList2.add(new Coordinate(4890.0000,330.0000));
		CoorList2.add(new Coordinate(4640.0000,390.0000));
		CoorList2.add(new Coordinate(4460.0000,500.0000));
		CoorList2.add(new Coordinate(4160.0000,290.0000));

		
		
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
		
		//��ȡ�ཻ����ε�����㼰���
		String ResultInfo = "";
		for(List<Coordinate> CoorList:SubPolyList)
		{
			Part part = new Part();
			part.setVertext(CoorList);
			double a = part.CalArea();
			
			List<String> CoorStrList = new ArrayList<String>();
			for(Coordinate Coor:CoorList)CoorStrList.add(Coor.ToString());
			ResultInfo += "�����"+a+"\n���괮��"+CoorStrList.size()+"\n"+Tools.JoinT("\n", CoorStrList)+"\r\n";
			
		}
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
        //���ڼ���������������
        //( P1 - Q1 ) �� ( Q2 - Q1 ) * ( Q2 - Q1 ) �� ( P2 - Q1 ) >= 0
        //( Q1 - P1 )��( P2 - P1) * ( P2 - P1)��(Q2 - P1) >= 0
        double P1Q1_X = (P1.getX() - Q1.getX()); double P1Q1_Y = (P1.getY() - Q1.getY());
        double Q2Q1_X = (Q2.getX() - Q1.getX()); double Q2Q1_Y = (Q2.getY() - Q1.getY());
        double P2Q1_X = (P2.getX() - Q1.getX()); double P2Q1_Y = (P2.getY() - Q1.getY());
        double P1Q1Q2Q1 = (P1Q1_X * Q2Q1_Y - P1Q1_Y * Q2Q1_X);
        double Q2Q1P2Q1 = (Q2Q1_X * P2Q1_Y - Q2Q1_Y * P2Q1_X);
        if (P1Q1Q2Q1 * Q2Q1P2Q1 < 0) ip1.OutInt=1; else ip1.OutInt=2;
        
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
