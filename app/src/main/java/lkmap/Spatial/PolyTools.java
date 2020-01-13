package lkmap.Spatial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Enum.lkPartType;
import lkmap.Tools.Tools;

import com.seisw.util.geom.Clip;
import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;
import com.seisw.util.geom.PolySimple;

import android.R.string;
import android.util.Log;

public class PolyTools 
{
	/**
	 * 用线切割面
	 * @param clipPointList
	 * @param poly
	 * @return
	 */
	public static List<Polygon> ClipPoly(List<Coordinate> clipPointList,Polygon poly)
	{
		List<Polygon> subPolygonList = new ArrayList<Polygon>();
		
		//由线到面的偏移距离
		double OffsetDis = 0.01;
		
		//对切割点进行偏移处理，形成小面状
        Polyline PL = new Polyline();
        Part part = new Part();
        part.setVertext(clipPointList);
        PL.AddPart(part);
        
        List<Coordinate> offsetCoorList = PL.Offset(OffsetDis);
        for(int i=clipPointList.size()-1;i>=0;i--)offsetCoorList.add(clipPointList.get(i));
        PolyDefault p1 = new PolyDefault();
        for(Coordinate coor:offsetCoorList)
        {
        	p1.add(coor.getX(),coor.getY());
        }
        
        //对面进行转换处理
        Poly p2 = PolygonToPoly(poly);

        //进行叉集分析
        Poly result = p2.difference(p1);

        //Log.println(2,"Clip","ploygons"+String.valueOf(result.getNumInnerPoly()));
        
        //消除裂缝处理
       for(int i=0;i<result.getNumInnerPoly();i++)
		{
		  	Poly poly1 = result.getInnerPoly(i);
		  	for(int j=i+1;j<result.getNumInnerPoly();j++)
		  	{
		  		Poly poly2 = result.getInnerPoly(j);
			  	for(int z=0;z<poly1.getNumPoints();z++)
			  	{
				  	for(int c=0;c<poly2.getNumPoints();c++)
				  	{
				  		double delX = Math.abs(poly1.getX(z)-poly2.getX(c));
				  		double delY = Math.abs(poly1.getY(z)-poly2.getY(c));
				  		if (delX<OffsetDis*10 && delY<OffsetDis*10)
				  		{
				  			poly1.setX(z, poly2.getX(c));
				  			poly1.setY(z, poly2.getY(c));
				  		}
				  	}
			  	}
		  	}
		  	
		  	//将poly转换为Polygon
		  	subPolygonList.add(PolyToPolygon(poly1));
		}

       //对结果中的孤岛进行归属处理
       List<Polygon> HoleList = new ArrayList<Polygon>();
       List<Polygon> PolyList = new ArrayList<Polygon>();
       for(Polygon ply:subPolygonList)
       {
    	   if (ply.getPartCount()==1)
    	   {
    		   if (ply.GetPartAt(0).GetPartType()==lkPartType.enHole)
    		   {
    			   HoleList.add(ply);
    			   continue;
    			}
    	   }
    	   PolyList.add(ply);
       }
       
       for(Polygon Poly:PolyList)
       {
    	   for(Polygon Hole:HoleList)
    	   {
    		   if (FullContains(Poly,Hole))
    		   {
    			   Poly.AddPart(Hole.GetPartAt(0));
    		   }
    	   }
       }
       
       return PolyList;
	}
	
	public static ArrayList<Polygon> ClipPoly(Polygon poly1,Polygon poly2)
	{
		ArrayList<Polygon> PolyList = new ArrayList<Polygon>();
		 Poly p1 = PolygonToPoly(poly1);
		 Poly p2 = PolygonToPoly(poly2);
	 
		 Poly result = p2.difference(p1);
		 Log.println(2,"ClipPoly","diffenence polys:"+String.valueOf(result.getNumInnerPoly()));
		 
		 for(int i = 0;i<result.getNumInnerPoly();i++)
		 {
			 PolyList.add(PolyToPolygon(result.getInnerPoly(i)));
		 }
		 
		 return PolyList;
	}
	
	public static ArrayList<Polygon> RepatePoly(Polygon poly1,Polygon poly2)
	{
		ArrayList<Polygon> PolyList = new ArrayList<Polygon>();
		 Poly p1 = PolygonToPoly(poly1);
		 Poly p2 = PolygonToPoly(poly2);
		 
		 Poly result = p2.intersection(p1);
		 Log.println(2,"RepatePoly","repeate polys:"+String.valueOf(result.getNumInnerPoly()));
		 
		 for(int i = 0;i<result.getNumInnerPoly();i++)
		 {
			 PolyList.add(PolyToPolygon(result.getInnerPoly(i)));
		 }
		 
		 return PolyList;
	}
	
	
	
	/**
	 * 计算公共边
	 * @param poly 相临面
	 * @param clipPointList 外绘面节点
	 * @return
	 */
	public static List<Polygon> PublicBorderPoly(List<Polygon> SubPolygonList,List<Coordinate> clipPointList)
	{
		//被选择的面集合并后的大面
		Polygon MainPolygon = PolyTools.MergePolygon(SubPolygonList);
    	
//    	Polygon PLY = new Polygon();
//    	Part part = new Part();
//    	part.getVertexList().add(new Coordinate(29818102.1650 ,47768957.5530));
//    	part.getVertexList().add(new Coordinate(29785452.8410 ,47708323.0940));
//    	part.getVertexList().add(new Coordinate(28367539.3360 ,43883687.9810));
//    	part.getVertexList().add(new Coordinate(32117547.4220 ,42335177.1790));
//    	part.getVertexList().add(new Coordinate(33726916.4410 ,43947644.0810));
//    	part.getVertexList().add(new Coordinate(33404863.6310 ,47400486.6090));
//    	part.getVertexList().add(new Coordinate(29818102.1650 ,47768957.5530));
//
//		part.SetPartType(lkPartType.enPoly);
//		PLY.AddPart(part);
//		
//		clipPointList = new ArrayList<Coordinate>();
//		clipPointList.add(new Coordinate(30111612.1530 ,43656800.6850));
//		clipPointList.add(new Coordinate(30074632.5690 ,42584234.1760));
//		clipPointList.add(new Coordinate(30423429.3120 ,41217806.6700));
//		clipPointList.add(new Coordinate(31269576.2540 ,40366281.2770));
//		clipPointList.add(new Coordinate(32520627.9930 ,39764254.7050));
//		clipPointList.add(new Coordinate(33305976.2160 ,39996734.2450));
//		clipPointList.add(new Coordinate(33894443.5030 ,40612001.8310));
//		clipPointList.add(new Coordinate(34167040.0800 ,41454962.9240));
//		clipPointList.add(new Coordinate(34104555.2040 ,42428238.9120));
//		clipPointList.add(new Coordinate(33729145.4810 ,43229354.3950));
//		clipPointList.add(new Coordinate(33223961.7560 ,43709195.3810));
//		clipPointList.add(new Coordinate(32718959.0860 ,43910794.0260));
//		clipPointList.add(new Coordinate(32341428.5020 ,43958315.0070));

    	//结果集合面
		List<Polygon> resultSubPolygonList = new ArrayList<Polygon>();
    	Polygon PublicBorderPolygon = CalPublicBorder(MainPolygon,clipPointList);
    	if (PublicBorderPolygon!=null)resultSubPolygonList.add(PublicBorderPolygon);
		
       return resultSubPolygonList;
	}
	
	/**
	 * 计算公共边
	 * @param polygon
	 * @param clipPointList
	 * @return
	 */
	private static Polygon CalPublicBorder(Polygon polygon,List<Coordinate> clipPointList)
	{
		
		//String MainPolyStr = GetCoorListStr(polygon.GetPartAt(0).getVertexList());
		//String ClipStr = GetCoorListStr(clipPointList);
		
		//外接边线
		Polyline clipPolyline = new Polyline();
		Part part = new Part(clipPointList);
		clipPolyline.AddPart(part);
		
		Part EndPart = null;  //与外边线相交的Part
		Coordinate InterPoint1 = null,InterPoint2 = null;  //相交的两个坐标点
		
		for(int p=0;p<polygon.getPartCount();p++)
		{
			Polyline PL = polygon.GetPartAt(p).GetBorder();
			List<Coordinate> InterPointList = new ArrayList<Coordinate>();
			boolean OK = PL.Intersect(clipPolyline,InterPointList);
			if (OK && InterPointList.size()==2)
			{
				EndPart = polygon.GetPartAt(p).Clone();
				InterPoint1 = InterPointList.get(0);
				InterPoint2 = InterPointList.get(1);
			}
		}
		
		//为Null表示外接边线没有与面相交于两个点
		if (EndPart==null) return null;

		//将相交的坐标点插入到相交的Part内部
		for(int p=1;p<=2;p++)
		{
			Coordinate Coor = (p==1?InterPoint1:InterPoint2);
			for(int i=0;i<EndPart.getVertexList().size()-1;i++)
			{
				Coordinate PT1 = EndPart.getVertexList().get(i);
				Coordinate PT2 = EndPart.getVertexList().get(i+1);
				double JL0 = GetDistance(PT1, PT2);
				double JL1 = GetDistance(PT1, Coor);
				double JL2 = GetDistance(PT2, Coor);
				if (Math.abs(JL0-JL1-JL2)<0.00001)
				{
					EndPart.getVertexList().add(i+1, Coor);break;
				}
			}
		}
		
		//String EndPartStr = GetCoorListStr(EndPart.getVertexList());
		
		//用两个交点，将EndPart切为两部分
		int SplitVertexIndex1=-1,SplitVertexIndex2=-1;
		for(int i=0;i<EndPart.getVertexList().size();i++)
		{
			Coordinate PT = EndPart.getVertexList().get(i);
			if (PT.Equal(InterPoint1))SplitVertexIndex1=i;
			if (PT.Equal(InterPoint2))SplitVertexIndex2=i;
		}
		if (SplitVertexIndex1>SplitVertexIndex2){int L=SplitVertexIndex1;SplitVertexIndex1=SplitVertexIndex2;SplitVertexIndex2=L;}
		
		Part SplitPart1 = new Part();
		Part SplitPart2 = new Part();
		
		for(int i=SplitVertexIndex1;i<=SplitVertexIndex2;i++)
		{
			SplitPart1.getVertexList().add(EndPart.getVertexList().get(i));
		}
		
		for(int i=SplitVertexIndex2;i<EndPart.getVertexList().size();i++)
		{
			SplitPart2.getVertexList().add(EndPart.getVertexList().get(i));
		}
		for(int i=0;i<=SplitVertexIndex1;i++)
		{
			SplitPart2.getVertexList().add(EndPart.getVertexList().get(i));
		}
		
		//将相交坐标点插入外接边线内部
		int VertexIndex1=-1,VertexIndex2 = -1;
		Part partLine = clipPolyline.GetPartAt(0);
		for(int p=1;p<=2;p++)
		{
			Coordinate Coor = (p==1?InterPoint1:InterPoint2);
			for(int i=0;i<part.getVertexList().size()-1;i++)
			{
				Coordinate PT1 = partLine.getVertexList().get(i);
				Coordinate PT2 = partLine.getVertexList().get(i+1);
				double JL0 = GetDistance(PT1, PT2);
				double JL1 = GetDistance(PT1, Coor);
				double JL2 = GetDistance(PT2, Coor);
				if (Math.abs(JL0-JL1-JL2)<0.00001)
				{
					if (p==1)VertexIndex1 = i;
					if (p==2)VertexIndex2 = i;
					break;
				}
			}
		}
		
		boolean ChangePos = false;
		if (VertexIndex1>VertexIndex2){ChangePos=true;int L=VertexIndex1;VertexIndex1=VertexIndex2;VertexIndex2=L;}
		
		//从尾部截去多余节点
		int VertexCount = partLine.getVertexList().size();
		for(int i=VertexIndex2+1;i<VertexCount;i++)partLine.getVertexList().remove(VertexIndex2+1);
		
		for(int i=0;i<=VertexIndex1;i++)partLine.getVertexList().remove(0);
		if (ChangePos){partLine.getVertexList().add(0,InterPoint2);partLine.getVertexList().add(InterPoint1);}
		else {partLine.getVertexList().add(0,InterPoint1);partLine.getVertexList().add(InterPoint2);}
		
		//String SplitPart1Str = GetCoorListStr(SplitPart1.getVertexList());
		//String SplitPart2Str = GetCoorListStr(SplitPart2.getVertexList());
		//String partLineStr = GetCoorListStr(partLine.getVertexList());
		
		//合并外接线与主面分割出来的两个Part
		for(int p=1;p<=2;p++)
		{
			Part part0 = (p==1?SplitPart1:SplitPart2);
			if (part0.getVertexList().get(0).Equal(partLine.getVertexList().get(0)))
			{
				for(int z=partLine.getVertexList().size()-1;z>=0;z--)
				{
					part0.getVertexList().add(partLine.getVertexList().get(z));
				}
			} 
			else
			{
				for(Coordinate Coor:partLine.getVertexList())
				{
					part0.getVertexList().add(Coor);
				}
			}
		}
		
		//比较面的大小 ，最后确定是哪个
		double SplitPartArea1 = Math.abs(SplitPart1.CalArea());
		double SplitPartArea2 = Math.abs(SplitPart2.CalArea());
		Part publicPart = SplitPart1;
		if (SplitPartArea1>SplitPartArea2)publicPart = SplitPart2;
		Polygon publicBorder = new Polygon();
		publicPart.SetPartType(lkPartType.enPoly);
		publicBorder.AddPart(publicPart);
		
		//String CoorStr = GetCoorListStr(publicPart.getVertexList());
		
		RepairPoly(publicBorder);
		return publicBorder;
	}
	
	/**
	 * 面修边
	 * @param reshapePoly
	 * @param clipPointList
	 * @return
	 */
	public static Polygon ReshapePolygon(Polygon selectPoly,List<Coordinate> clipPointList)
	{
		Polygon reshapePoly = (Polygon)selectPoly.Clone();
		
		//处理外接边线
		Polyline clipPolyline = new Polyline();
		Part clipPart = new Part(clipPointList);
		clipPolyline.AddPart(clipPart);
		
		//1、计算修边线与面的交点
		List<Coordinate> m_InterPointList = new ArrayList<Coordinate>();
		Part reshapePart = null;
		for(int p = 0;p<reshapePoly.getPartCount();p++)
		{
			List<Coordinate> LSInterPointList = new ArrayList<Coordinate>();
			Part part = reshapePoly.GetPartAt(p);
			
			String CoorStr = GetCoorListStr(part.getVertexList());
			
			Polyline PL = part.GetBorder();
			boolean OK = PL.Intersect(clipPolyline,LSInterPointList);
			if (OK)
			{
				if (reshapePart!=null)return null;
				else
				{
					reshapePart = reshapePoly.GetPartAt(p);
					for(Coordinate Coor:LSInterPointList)m_InterPointList.add(Coor);
				}
			}
		}
		if (m_InterPointList.size()<=1) return null;
		clipPart.SetPartType(reshapePart.GetPartType());  //修正方向
		
		//2、提取交点在主面内到起点的距离
		TreeMap<Double,HashMap<String,Object>> splitPointDisList = new TreeMap<Double,HashMap<String,Object>>();
		for(Coordinate Coor:m_InterPointList)
		{
			for(int i=0;i<reshapePart.getVertexList().size()-1;i++)
			{
				Coordinate PT1 = reshapePart.getVertexList().get(i);
				Coordinate PT2 = reshapePart.getVertexList().get(i+1);
				double JL0 = GetDistance(PT1, PT2);
				double JL1 = GetDistance(PT1, Coor);
				double JL2 = GetDistance(PT2, Coor);
				if (Math.abs(JL0-JL1-JL2)<0.00001)
				{
					//累计到起点的距离
					double ToStartDis = 0;
					for(int j=0;j<i;j++)
					{
						ToStartDis+=GetDistance(reshapePart.getVertexList().get(j),reshapePart.getVertexList().get(j+1));
					}
					ToStartDis+=JL1;
					HashMap<String,Object> ho = new HashMap<String,Object>();
					ho.put("BeforeVertexIndex", i);
					ho.put("Coor", Coor);
					ho.put("ToStartDis", ToStartDis);
					splitPointDisList.put(ToStartDis, ho);
				}
			}
		}
		//3、对主面边线进行切分，提取其中的最长边
		List<HashMap<String,Object>> KeyPointList= new ArrayList<HashMap<String,Object>>();
		for(HashMap<String,Object> ho:splitPointDisList.values())
		{
			KeyPointList.add(ho);
		}
		
		double NearJL = 0;
		int NearIndex = -1;
		for(int i=1;i<KeyPointList.size();i++)
		{
			double JL1 = Double.parseDouble(KeyPointList.get(i-1).get("ToStartDis")+"");
			double JL2 = Double.parseDouble(KeyPointList.get(i).get("ToStartDis")+"");
			if ((JL2-JL1)>NearJL)
			{
				NearJL = JL2-JL1;
				NearIndex=i;
			}
		}
		
		//特殊计算第一个交点与最后一个交点，通过0点的距离
		double ToEndDis = 0;
		int BeforeVertexIndex = Integer.parseInt(KeyPointList.get(KeyPointList.size()-1).get("BeforeVertexIndex")+"");
		Coordinate EndestVertex = (Coordinate)KeyPointList.get(KeyPointList.size()-1).get("Coor");
		for(int i=BeforeVertexIndex+1;i<reshapePart.getVertexList().size()-1;i++)
		{
			ToEndDis+=GetDistance(reshapePart.getVertexList().get(i),reshapePart.getVertexList().get(i+1));
		}
		ToEndDis+=GetDistance(reshapePart.getVertexList().get(BeforeVertexIndex+1),EndestVertex);
		ToEndDis+=Double.parseDouble(KeyPointList.get(0).get("ToStartDis")+"");
		
		//判断是否为最长距离，提取保留的最长边
		List<Coordinate> SaveReshapePointList = new ArrayList<Coordinate>();
		if (ToEndDis>NearJL)
		{
			SaveReshapePointList.add(EndestVertex);
			for(int i=BeforeVertexIndex+1;i<reshapePart.getVertexList().size();i++)
			{
				SaveReshapePointList.add(reshapePart.getVertexList().get(i));
			}
			int BeforeVertexIndex2 = Integer.parseInt(KeyPointList.get(0).get("BeforeVertexIndex")+"");
			Coordinate VertexCoor = (Coordinate)KeyPointList.get(0).get("Coor");
			for(int i=0;i<=BeforeVertexIndex2;i++)
			{
				SaveReshapePointList.add(reshapePart.getVertexList().get(i));
			}
			SaveReshapePointList.add(VertexCoor);
		} 
		else
		{
			HashMap<String,Object> KeyPoint1 = KeyPointList.get(NearIndex-1);
			HashMap<String,Object> KeyPoint2 = KeyPointList.get(NearIndex);
			
			int BeforeVertexIdx1 = Integer.parseInt(KeyPoint1.get("BeforeVertexIndex")+"");
			int BeforeVertexIdx2 = Integer.parseInt(KeyPoint2.get("BeforeVertexIndex")+"");
			
			Coordinate KeyVertexCoor1 = (Coordinate)KeyPoint1.get("Coor");
			Coordinate KeyVertexCoor2 = (Coordinate)KeyPoint2.get("Coor");
			
			SaveReshapePointList.add(KeyVertexCoor1);
			for(int i=BeforeVertexIdx1+1;i<=BeforeVertexIdx2;i++)
			{
				SaveReshapePointList.add(reshapePart.getVertexList().get(i));
			}
			SaveReshapePointList.add(KeyVertexCoor2);
		}
		
		String CoorStr1 = GetCoorListStr(SaveReshapePointList);
		
		//4、根据主面保留边的两个交点对修边线进行截取
		int VertexIndex1=0,VertexIndex2=0;
		for(int i=1;i<=2;i++)
		{
			Coordinate Coor = (i==1?SaveReshapePointList.get(0):SaveReshapePointList.get(SaveReshapePointList.size()-1));
			for(int j=0;j<clipPart.getVertexList().size()-1;j++)
			{
				Coordinate PT1 = clipPart.getVertexList().get(j);
				Coordinate PT2 = clipPart.getVertexList().get(j+1);
				double JL0 = GetDistance(PT1, PT2);
				double JL1 = GetDistance(PT1, Coor);
				double JL2 = GetDistance(PT2, Coor);
				if (Math.abs(JL0-JL1-JL2)<0.00001)
				{
					if (i==1)VertexIndex1 = j;else VertexIndex2=j;
					break;
				}
			}
		}
		
		if (VertexIndex1>VertexIndex2)
		{
			for(int i=VertexIndex2+1;i<=VertexIndex1;i++)
			{
				SaveReshapePointList.add(clipPart.getVertexList().get(i));
			}
		} 
		else
		{
			for(int i=VertexIndex2;i>=VertexIndex1+1;i--)
			{
				SaveReshapePointList.add(clipPart.getVertexList().get(i));
			}
		}

		//整理最后的图形
		reshapePart.setVertext(SaveReshapePointList);
		reshapePart.SetPartType(reshapePart.GetPartType());
		reshapePart.Closed();
		
		String CoorStr = GetCoorListStr(SaveReshapePointList);
		return reshapePoly;
	}
	
	/**
	 * 合并面操作
	 * @param subPolyList
	 * @return
	 */
	public static Polygon MergePolygon(List<Polygon> subPolyList)
	{
		ArrayList<Poly> mergePolyList = new ArrayList<Poly>();
		for(Polygon ply:subPolyList)
		{
			mergePolyList.add(PolygonToPoly(ply));
		}
		if (mergePolyList.size()==0) return null;
		if (mergePolyList.size()==1) return PolyToPolygon(mergePolyList.get(0));
		
		//对多个面进行合并操作
		Poly P1 = mergePolyList.get(0);
		for(int i=1;i<mergePolyList.size();i++)
		{
			P1 = P1.union(mergePolyList.get(i));
		}
		Polygon endPoly = PolyToPolygon(P1);
		RepairPoly(endPoly);
		return endPoly;
	}
	
	/**
	 * 切割孤岛
	 * @param MainPoly
	 * @param HolePoly
	 * @return
	 */
	public static Polygon ClipHole(Polygon MainPoly,Polygon HolePoly)
	{
		Poly P1 = PolygonToPoly(MainPoly);
		Poly P2 = PolygonToPoly(HolePoly);
		Poly P3 = P1.difference(P2);
		Polygon result = PolyToPolygon(P3);
		return result;
	}
	
	//将PolyDefault转换Polygon
	private static Polygon PolyToPolygon(Poly poly0)
	{
	  	//将poly转换为Polygon
	  	Polygon resultPolygon = new Polygon();
	  	if (poly0.getNumInnerPoly()==1)
	  	{
	  		Part subPart = new Part();
		  	for(int i=0;i<poly0.getNumPoints();i++)
		  	{
		  		subPart.getVertexList().add(new Coordinate(poly0.getX(i),poly0.getY(i)));
		  	}
	  		if (poly0.isHole())subPart.SetPartType(lkPartType.enHole);
	  		else subPart.SetPartType(lkPartType.enPoly);
	  		resultPolygon.AddPart(subPart);
	  	}
	  	
	  	//多部分
	  	if (poly0.getNumInnerPoly()>1)
	  	{
		  	for(int z=0;z<poly0.getNumInnerPoly();z++)
		  	{
		  		Poly subPoly = poly0.getInnerPoly(z);
			  	Part subPart = new Part();
			  	for(int i=0;i<subPoly.getNumPoints();i++)
			  	{
			  		subPart.getVertexList().add(new Coordinate(subPoly.getX(i),subPoly.getY(i)));
			  	}
		  		
		  		if (subPoly.isHole())subPart.SetPartType(lkPartType.enHole);
		  		else subPart.SetPartType(lkPartType.enPoly);
		  		resultPolygon.AddPart(subPart);
		  	}
	  	}
	  	resultPolygon.Closed();
	  	return resultPolygon;
	}
	
	//将Polygon转换PolyDefault
	private static Poly PolygonToPoly(Polygon polygon0)
	{
	  	//将Polygon转换为Poly
		PolyDefault resultPoly = new PolyDefault();
	  	if (polygon0.getPartCount()==1)
	  	{
	  		Part part = polygon0.GetPartAt(0);
	  		for(Coordinate Coor:part.getVertexList())
	  		{
	  			resultPoly.add(Coor.getX(),Coor.getY());
	  		}
	  	}
	  	if (polygon0.getPartCount()>1)
	  	{
		  	for(int i=0;i<polygon0.getPartCount();i++)
		  	{
		  		Poly subPoly = new PolyDefault();
		  		Part part = polygon0.GetPartAt(i);
		  		for(Coordinate Coor:part.getVertexList())subPoly.add(Coor.getX(),Coor.getY());
		  		if (part.GetPartType()==lkPartType.enHole)subPoly.setIsHole(true);
		  		resultPoly.add(subPoly);
		  	}
	  	}
	  	return resultPoly;
	}
	
	//修正面
	public static void RepairPoly(Polygon endPoly)
	{
		//修正面，主要目的是消除内角为0的小短线
		for(int p=0;p<endPoly.getPartCount();p++)
		{
			Part part = endPoly.GetPartAt(p);
			boolean OK = true;
			while(OK)
			{
				boolean DelOK = false;
				for(int i=0;i<part.getVertexList().size()-2;i++)
				{
					Coordinate Pt1 = part.getVertexList().get(i);
					Coordinate Pt2 = part.getVertexList().get(i+1);
					Coordinate Pt3 = part.getVertexList().get(i+2);
					double A = Math.abs(GetAngle(Pt1,Pt2,Pt3));  //角度
					if (A<0.0001){part.getVertexList().remove(i+1);DelOK=true;}
				}
				OK = DelOK;
			}
		}
		
		int partCount = endPoly.getPartCount();
		for(int p=partCount-1;p>=0;p--)
		{
			Part part = endPoly.GetPartAt(p);
			if (part.getVertexList().size()<=2)endPoly.RemovePart(part);
		}
	}
	
	/**
	 * 判断两个面是否完全包含
	 * @param MainPoly 大面
	 * @param HolePoly 小面
	 * @return
	 */
	public static boolean FullContains(Polygon MainPoly,Polygon HolePoly)
	{
		Poly P1 = PolygonToPoly(MainPoly);
		Poly P2 = PolygonToPoly(HolePoly);
		Poly P3 = P2.difference(P1);
		double A = P3.getArea();
		if (A<0.000000001) return true;else return false;
//		if (A<0.000001) return true;else return false;
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
	public static double GetDistance(Coordinate P1,Coordinate P2)
	{
		return Math.sqrt((P1.getX() - P2.getX()) * (P1.getX() - P2.getX()) + (P1.getY() - P2.getY()) * (P1.getY() - P2.getY()));
	}
	
	//求解三点的内角
	public static double GetAngle(Coordinate Pt1, Coordinate Pt2, Coordinate Pt3) 
	{
		double L12 = GetDistance(Pt1,Pt2);
		double L13 = GetDistance(Pt1,Pt3);
		double L23 = GetDistance(Pt2,Pt3);
		double A = (L12*L12+L23*L23-L13*L13)/(2*L12*L23);
		return Math.acos(A) * 180 /Math.PI;
		
//	    double dx1 = first.getX() - cen.getX(); 
//	    double dy1 = first.getY() - cen.getY(); 
//	    double dx2 = second.getX() - cen.getX(); 
//	    double dy2 = second.getY() - cen.getY(); 
//	    float c = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1) * (float)Math.sqrt(dx2 * dx2 + dy2 * dy2); 
//	    if (c == 0) return -1; 
//	    double angle = (float)Math.acos((dx1 * dx2 + dy1 * dy2) / c); 
//	    return angle;
	}
}
