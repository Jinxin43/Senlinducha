package lkmap.ZRoadMap.DataExport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkDatasetSourceType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Map.StaticObject;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class DataExport_KML 
{
	public List<String> Export(List<HashMap<String,String>> expLayerList,String kmlFileName)
	{
		List<String> ErrorList = new ArrayList<String>();
		try
		{
			FileOutputStream fs = new FileOutputStream(kmlFileName);
			OutputStreamWriter bw = new OutputStreamWriter(fs);  //"GB2312"用此编码保存汉字不乱码
			//bw.write(Tools.JoinT("\r\n", dxfList));
			
			this.WriteKMLHeader(bw);   //写入KML文件头
			
			//写入实体信息,
			for(HashMap<String,String> lyr:expLayerList)
			{
				ErrorList.add(lyr.get("LayerName"));
				Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.get("LayerID"));
				v1_Layer vLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(lyr.get("LayerID"));
				
				//构建查询字段属性语句
				List<String> queryFieldList = new ArrayList<String>();
				for(v1_LayerField LF:vLayer.GetFieldList())queryFieldList.add(LF.GetDataFieldName());
				
				//查询属性数据
				String SQL = "select SYS_GEO,SYS_ID,"+Tools.JoinT(",", queryFieldList)+" from "+pDataset.getDataTableName()+" where SYS_STATUS='0'";
				SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
				if (DR==null) continue;
				while(DR.Read())
				{
					//读取属性
					String FeatureInfo = "";
					for(String field:queryFieldList)
					{
						FeatureInfo+=vLayer.GetFieldNameByDataFieldName(field)+"="+DR.GetString(field)+"\r\n";
					}

		             byte[] bytes = (byte[])DR.GetBlob(0);  	//图形
		             Geometry pGeometry = Tools.ByteToGeometry(bytes, pDataset.getType());

					this.WriteKML(bw, pGeometry, FeatureInfo, pDataset.getType());
				}DR.Close();
			}
			
			this.WriteKMLEnder(bw);
			
	    	bw.close();fs.close();
		}
		catch(Exception e)
		{
			return ErrorList;
		}
		return new ArrayList<String>();
		
	}
	
	private void WriteKMLHeader(OutputStreamWriter sw) throws IOException
	{
		sw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		sw.write("<kml xmlns=\"http://earth.google.com/kml/2.1\">\r\n");
		sw.write("<Document>\r\n");
	}
	
	private void WriteKMLEnder(OutputStreamWriter sw) throws IOException
	{
		sw.write("</Document>\r\n");
		sw.write("</kml>");
	}

	private void WriteKML(OutputStreamWriter sw,Geometry pGeometry,String FeatureInfo,lkGeoLayerType lkGeoType) throws IOException
	{
		if (lkGeoType==lkGeoLayerType.enPoint)
		{
			Coordinate Coor = ((lkmap.Cargeometry.Point)pGeometry).getCoordinate();
			Coordinate CoorX = StaticObject.soProjectSystem.XYToWGS84(Coor);
			
			sw.write("<Placemark>\r\n");
			sw.write("<name>"+pGeometry.getTag()+"</name>\r\n");
			sw.write("<description>"+FeatureInfo+"</description>\r\n");
			sw.write("<Point>\r\n");
			sw.write("<coordinates>"+CoorX.getX()+","+CoorX.getY()+","+Coor.getZ()+"</coordinates>\r\n");
			sw.write("</Point>\r\n");
			sw.write("</Placemark>\r\n");
		}
		if (lkGeoType==lkGeoLayerType.enPolyline)
		{
			sw.write("<Placemark>\r\n");
			sw.write("<name>"+pGeometry.getTag()+"</name>\r\n");
			sw.write("<description>"+FeatureInfo+"</description>\r\n");
			sw.write("<LineString>\r\n");     
			sw.write("<coordinates>\r\n");
			for(int p=0;p<pGeometry.getPartCount();p++)
			{
				Part part = pGeometry.GetPartAt(p);
				for(Coordinate Coor : part.getVertexList())
				{
					Coordinate CoorX = StaticObject.soProjectSystem.XYToWGS84(Coor);
					sw.write(CoorX.getX()+","+CoorX.getY()+","+Coor.getZ()+"\r\n");
				}
			}
			sw.write("</coordinates>\r\n");
			sw.write("</LineString>\r\n");
			sw.write("</Placemark>\r\n");
		}

		if (lkGeoType==lkGeoLayerType.enPolygon)
		{
			sw.write("<Placemark>\r\n");
			sw.write("<name>"+pGeometry.getTag()+"</name>\r\n");
			sw.write("<description>"+FeatureInfo+"</description>\r\n");
			sw.write("<Polygon>\r\n");
			sw.write("<outerBoundaryIs>\r\n");
			sw.write("<LinearRing>\r\n"); 
			sw.write("<coordinates>\r\n");
			for(int p=0;p<pGeometry.getPartCount();p++)
			{
				Part part = pGeometry.GetPartAt(p);
				for(Coordinate Coor : part.getVertexList())
				{
					Coordinate CoorX = StaticObject.soProjectSystem.XYToWGS84(Coor);
					sw.write(CoorX.getX()+","+CoorX.getY()+","+Coor.getZ()+"\r\n");
				}
			}
			sw.write("</coordinates>\r\n");
			sw.write("</LinearRing>\r\n");
			sw.write("</outerBoundaryIs>\r\n");
			sw.write("</Polygon>\r\n");
			sw.write("</Placemark>\r\n");
		}
	}
	
	
}
