package lkmap.Spatial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Tools.Tools;

public class LineTools 
{
	/**
	 * ����������
	 * @param MainPolyline
	 * @param reshapePointList
	 * @return
	 */
	public static Polyline StartReshape(Polyline MainPolyline,List<Coordinate> reshapePointList)
	{
		Polyline reshapePolyline = (Polyline)MainPolyline.Clone();
		
		//�����»�����
		Polyline clipPolyline = new Polyline();
		Part clipPart = new Part(reshapePointList);
		clipPolyline.AddPart(clipPart);
		
		//1�������ޱ��������ߵĽ���
		List<Coordinate> m_InterPointList = new ArrayList<Coordinate>();
		Part reshapePart = null;
		for(int p = 0;p<reshapePolyline.getPartCount();p++)
		{
			List<Coordinate> LSInterPointList = new ArrayList<Coordinate>();
			Part part = reshapePolyline.GetPartAt(p);
			Polyline PL = part.GetBorder();
			boolean OK = PL.Intersect(clipPolyline,LSInterPointList);
			if (OK)
			{
				if (reshapePart!=null)return null;
				else
				{
					reshapePart = part;
					for(Coordinate Coor:LSInterPointList)m_InterPointList.add(Coor);
				}
			}
		}
		if (m_InterPointList.size()<=1) return null;

		//2����ȡ�������ޱ����ڵ����ľ��룬Ҳ���ǽ���������
		TreeMap<Double,HashMap<String,Object>> splitPointDisList = new TreeMap<Double,HashMap<String,Object>>();
		for(Coordinate Coor:m_InterPointList)
		{
			for(int i=0;i<clipPart.getVertexList().size()-1;i++)
			{
				Coordinate PT1 = clipPart.getVertexList().get(i);
				Coordinate PT2 = clipPart.getVertexList().get(i+1);
				double JL0 = GetDistance(PT1, PT2);
				double JL1 = GetDistance(PT1, Coor);
				double JL2 = GetDistance(PT2, Coor);
				if (Math.abs(JL0-JL1-JL2)<0.00001)
				{
					//�ۼƵ����ľ���
					double ToStartDis = 0;
					for(int j=0;j<i;j++)ToStartDis+=GetDistance(clipPart.getVertexList().get(j),clipPart.getVertexList().get(j+1));
					ToStartDis+=JL1;
					HashMap<String,Object> ho = new HashMap<String,Object>();
					ho.put("BeforeVertexIndex", i);
					ho.put("Coor", Coor);
					splitPointDisList.put(ToStartDis, ho);
				}
			}
		}
		
		//3����ȡ�ޱ��������Ҫ�����Ľڵ�
		List<Coordinate> SaveClipPointList = new ArrayList<Coordinate>();
		List<HashMap<String,Object>> DisList = new ArrayList<HashMap<String,Object>>();
		for(HashMap<String,Object> ho:splitPointDisList.values())DisList.add(ho);
		
		//�����Ϣ
		int StartVertexIndex = Integer.parseInt(DisList.get(0).get("BeforeVertexIndex")+"");
		Coordinate InterPoint1 = (Coordinate)DisList.get(0).get("Coor");
		
		//ֹ����Ϣ
		int EndVertexIndex = Integer.parseInt(DisList.get(DisList.size()-1).get("BeforeVertexIndex")+"");
		Coordinate InterPoint2 = (Coordinate)DisList.get(DisList.size()-1).get("Coor");
		
		SaveClipPointList.add(InterPoint1);
		for(int i=StartVertexIndex+1;i<=EndVertexIndex;i++)
		{
			SaveClipPointList.add(clipPart.getVertexList().get(i));
		}
		SaveClipPointList.add(InterPoint2);
		
		//4�����㽻���������ϵ�λ��
		int MainPLStartVetexIndex = -1,MainPLEndVertexIndex = -1;
		for(int p=1;p<=2;p++)
		{
			Coordinate Coor = InterPoint1;
			if (p==2)Coor = InterPoint2;
			for(int i=0;i<reshapePart.getVertexList().size()-1;i++)
			{
				Coordinate PT1 = reshapePart.getVertexList().get(i);
				Coordinate PT2 = reshapePart.getVertexList().get(i+1);
				double JL0 = GetDistance(PT1, PT2);
				double JL1 = GetDistance(PT1, Coor);
				double JL2 = GetDistance(PT2, Coor);
				if (Math.abs(JL0-JL1-JL2)<0.00001)
				{
					if (p==1)MainPLStartVetexIndex = i;
					if (p==2)MainPLEndVertexIndex = i;
				}
			}
		}
		
		//5�������������
		List<Coordinate> EndSaveCoorList = new ArrayList<Coordinate>();
		
		//�жϷ�����
		boolean ChangeFlip = false;
		if (MainPLStartVetexIndex>MainPLEndVertexIndex)
		{
			int LS = MainPLStartVetexIndex;MainPLStartVetexIndex=MainPLEndVertexIndex;MainPLEndVertexIndex=LS;
			ChangeFlip=true;
		}

		for(int i=0;i<=MainPLStartVetexIndex;i++)EndSaveCoorList.add(reshapePart.getVertexList().get(i));
		if (!ChangeFlip)for(Coordinate Coor:SaveClipPointList)EndSaveCoorList.add(Coor);
		else
		{
			for(int i=SaveClipPointList.size()-1;i>=0;i--)
			{
				EndSaveCoorList.add(SaveClipPointList.get(i));
			}
		}
		
		for(int i=MainPLEndVertexIndex+1;i<reshapePart.getVertexList().size();i++)EndSaveCoorList.add(reshapePart.getVertexList().get(i));

		//������ʵ��
		reshapePart.getVertexList().clear();
		reshapePart.setVertext(EndSaveCoorList);
		
		String CoorStr = GetCoorListStr(EndSaveCoorList);
		
		return reshapePolyline;
	}
	
	/**
	 * ���Ӷ����
	 * @param MainPolyline
	 * @param SubPolylineList
	 * @return
	 */
	public static Polyline StartConnect(Polyline MainPolyline,List<Polyline> SubPolylineList)
	{
		Polyline newPolyline = (Polyline)MainPolyline.Clone();
		while(SubPolylineList.size()>0)
		{
			//�����뵱ǰ���ߵ������˵�������߶�
			double NearestDis = Double.MAX_VALUE;
			int NearestSegmentIndex = -1;
			boolean IfStart = false;
			int IfNearestNewPolylineIdx = 1;  //�������ĸ������
			for(int i=1;i<=2;i++)
			{
				Coordinate Coor = newPolyline.getStartPoint();
				if (i==2)Coor = newPolyline.getEndPoint();
				for(int l=0;l<SubPolylineList.size();l++)
				{
					Polyline SubPolyline = SubPolylineList.get(l);
					double ToStartDis = GetDistance(Coor,SubPolyline.getStartPoint());
					double ToEndDis = GetDistance(Coor,SubPolyline.getEndPoint());
					if (ToStartDis<NearestDis){NearestDis = ToStartDis;NearestSegmentIndex=l;IfStart=true;IfNearestNewPolylineIdx=i;}
					if (ToEndDis<NearestDis){NearestDis = ToEndDis;NearestSegmentIndex=l;IfStart=false;IfNearestNewPolylineIdx=i;}
				}
			}
			
			//�����߽�������
			if (NearestSegmentIndex!=-1)
			{
				Polyline PL = SubPolylineList.get(NearestSegmentIndex);
				if (IfStart)
				{
					for(int i=0;i<PL.GetPartAt(0).getVertexList().size();i++)
					{
						Coordinate PT = PL.GetPartAt(0).getVertexList().get(i);
						
						//��������
						if (IfNearestNewPolylineIdx==1)
						{
							newPolyline.GetPartAt(0).getVertexList().add(0, PT);
						}
						if (IfNearestNewPolylineIdx==2)
						{
							newPolyline.GetPartAt(0).getVertexList().add(PT);
						}
					}
				}
				else
				{
					for(int i=PL.GetPartAt(0).getVertexList().size()-1;i>=0;i--)
					{
						Coordinate PT = PL.GetPartAt(0).getVertexList().get(i);
						
						//��������
						if (IfNearestNewPolylineIdx==1)
						{
							newPolyline.GetPartAt(0).getVertexList().add(0, PT);
						}
						if (IfNearestNewPolylineIdx==2)
						{
							newPolyline.GetPartAt(0).getVertexList().add(PT);
						}
					}
				}
			}
			SubPolylineList.remove(NearestSegmentIndex);
		}
		
		newPolyline.CalEnvelope();
		newPolyline.getLength(true);
		
		String CoorStr = GetCoorListStr(newPolyline.GetPartAt(0).getVertexList());
		return newPolyline;
	}
	
	
	
	/**
	 * �Ի��ߵķ�ʽ�ָ���
	 * @param PL
	 * @param SplitPointList
	 * @return
	 */
	public static List<Polyline> StartSplit(Polyline PL,List<Coordinate> SplitPointList)
	{
		Polyline clipPolyline = new Polyline();
		Part clipPart = new Part(SplitPointList);
		clipPolyline.AddPart(clipPart);
		
		//1�����������ָ��ߵĽ���
		List<Coordinate> m_InterPointList = new ArrayList<Coordinate>();
		for(int p = 0;p < PL.getPartCount();p++)
		{
			List<Coordinate> LSInterPointList = new ArrayList<Coordinate>();
			boolean OK = clipPolyline.Intersect(PL.GetPartAt(p).GetBorder(),LSInterPointList);
			if (OK)
			{
				for(Coordinate Coor:LSInterPointList)m_InterPointList.add(Coor);
			}
		}
		
		if (m_InterPointList.size()!=1) return null;
		Polyline PL1=new Polyline(),PL2 = new Polyline();
		if (PL.Split(m_InterPointList.get(0), PL1, PL2))
		{
			List<Polyline> plList = new ArrayList<Polyline>();
			plList.add(PL1);plList.add(PL2);
			return plList;
		}
		return null;
	}
	
    private static double GetDistance(Coordinate P1,Coordinate P2)
	{
		return Math.sqrt((P1.getX() - P2.getX()) * (P1.getX() - P2.getX()) + (P1.getY() - P2.getY()) * (P1.getY() - P2.getY()));
	}
	private static String GetCoorListStr(List<Coordinate> CoorList)
	{
		List<String> strList = new ArrayList<String>();
		for(Coordinate Coor:CoorList)
		{
			strList.add(Coor.ToString());
		}
		return Tools.JoinT("\r\n", strList);
	}
}
