package lkmap.Spatial;

import java.util.HashMap;
import java.util.List;
import lkmap.Cargeometry.Polygon;

public class SpatialAnalysisTools 
{

	public static HashMap<String,Object> Poly_IntersectArea(Polygon Ply1,Polygon Ply2)
	{
		HashMap<String,Object> result = new HashMap<String,Object>();
		
		Poly_Intersect pi = new Poly_Intersect();
		
		//计算，返回SubPolyList格式:	Key="Area",Key="Part"
		List<HashMap<String,Object>> SubPolyList = pi.Poly_Intersect(Ply1, Ply2);
		
		double Allarea = 0;
		for(HashMap<String,Object> SubPoly:SubPolyList)
		{
			//提取相交多边形的坐标点及面积
			Allarea+=Double.parseDouble(SubPoly.get("Area")+"");
		}
		result.put("Area",Allarea);
		return result;
	}
	
}
