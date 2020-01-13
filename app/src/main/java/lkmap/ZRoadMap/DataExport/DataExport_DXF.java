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
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.Project_Web;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkGeoLayerType;

public class DataExport_DXF 
{
	//���ݵ�������
	private String m_ExportType = "";
	

	public List<String> Export(List<HashMap<String,String>> expLayerList,String dxfFileName)
	{
		List<String> ErrorList = new ArrayList<String>();
		
		try
		{
			FileOutputStream fs = new FileOutputStream(dxfFileName);
			OutputStreamWriter bw = new OutputStreamWriter(fs,"GB2312");  //�ô˱��뱣�溺�ֲ�����

	    	
			CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
			if (CS.GetName().equals("WGS-84����"))m_ExportType = "WGS84";
			else m_ExportType = "ƽ��";
			
			//����dxf�ļ�ͷ
			this.CreateHeader(bw);
			
			//����ͼ��
			for(HashMap<String,String> lay:expLayerList)this.CreateLayer(bw,lay.get("LayerName"));
			bw.write("0\r\n");
			bw.write("ENDTAB\r\n");
			bw.write("0\r\n");
			bw.write("ENDSEC\r\n");
			bw.write("0\r\n");
			bw.write("SECTION\r\n");
			bw.write("2\r\n");
			bw.write("ENTITIES\r\n");
			
			//д��ʵ����Ϣ,
			for(HashMap<String,String> lyr:expLayerList)
			{
				ErrorList.add(lyr.get("LayerName"));
				Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.get("LayerID"));
				List<Geometry> geometryList = pDataset.QueryGeometryFromDB1(null);
				for(Geometry pGeometry:geometryList)
				{
					int ParCount = pGeometry.getPartCount();
					for(int p=0;p<ParCount;p++)
					{
						if (pDataset.getType()==lkGeoLayerType.enPoint)
							this.CreatePoint(bw, lyr.get("LayerName"), pGeometry.GetPartAt(p));
						if (pDataset.getType()==lkGeoLayerType.enPolyline)
							this.CreatePolyline(bw, lyr.get("LayerName"), pGeometry.GetPartAt(p),false);
						if (pDataset.getType()==lkGeoLayerType.enPolygon)
						 this.CreatePolyline(bw, lyr.get("LayerName"), pGeometry.GetPartAt(p),true);
					}
				}
			}
			
			//������ʶ
			bw.write("0\r\n");
			bw.write("ENDSEC\r\n");
			bw.write("0\r\n");
			bw.write("EOF\r\n");
	    	bw.close();fs.close();
		}
		catch(Exception e)
		{
			return ErrorList;
		}
		return new ArrayList<String>();
		
	}
	
	/**
	 * �����߿�
	 * @param dxfList
	 * @param pGeometry
	 */
	private void CreatePolyline(OutputStreamWriter bw,String LayerName,Part part,boolean Close)
	{
		try {
			bw.write("0\r\n");
			bw.write("POLYLINE\r\n");
			bw.write("5\r\n");
			bw.write("12B\r\n");
			bw.write("8\r\n");
			bw.write(LayerName+"\r\n");
			bw.write("66\r\n");
			bw.write("1\r\n");
			bw.write("10\r\n");
			bw.write("0.0\r\n");
			bw.write("20\r\n");
			bw.write("0.0\r\n");
			bw.write("30\r\n");
			bw.write("0.0\r\n");
			if (Close){bw.write("70\r\n");bw.write("1\r\n");}
			
			//�ڵ�
			for(Coordinate Coor:part.getVertexList())
			{
				bw.write("0\r\n");
				bw.write("VERTEX\r\n");
				bw.write("5\r\n");
				bw.write("14D\r\n");
				bw.write("8\r\n");
				bw.write(LayerName+"\r\n");
				bw.write("10\r\n");
				if (this.m_ExportType.equals("WGS84"))Coor = Project_Web.Web_XYToBL(Coor.getX(), Coor.getY());
				bw.write(Coor.getX()+"\r\n");
				bw.write("20\r\n");
				bw.write(Coor.getY()+"\r\n");
				bw.write("30\r\n");
				bw.write("0.0\r\n");
			}
			bw.write("0\r\n");
			bw.write("SEQEND\r\n");
			bw.write("5\r\n");
			bw.write("153\r\n");
			bw.write("8\r\n");
			bw.write(LayerName+"\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * �������
	 * @param dxfList
	 * @param pGeometry
	 */
	private void CreatePoint(OutputStreamWriter bw,String LayerName,Part part)
	{
		Coordinate Coor = part.getVertexList().get(0);
		try {
			bw.write("0\r\n");
			bw.write("POINT\r\n");
			bw.write("5\r\n");
			bw.write("12A\r\n");
			bw.write("8\r\n");
			bw.write(LayerName+"\r\n");
			bw.write("10\r\n");
			
			if (this.m_ExportType.equals("WGS84"))Coor = Project_Web.Web_XYToBL(Coor.getX(), Coor.getY());
			bw.write(Coor.getX()+"\r\n");
			bw.write("20\r\n");
			bw.write(Coor.getY()+"\r\n");
			bw.write("30\r\n");
			bw.write("0.0\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void CreateHeader(OutputStreamWriter bw)
	{
		try {
			bw.write("0\r\n");
			bw.write("SECTION\r\n");
			bw.write("2\r\n");
			bw.write("HEADER\r\n");
			bw.write("0\r\n");
			bw.write("ENDSEC\r\n");
			bw.write("0\r\n");
			bw.write("SECTION\r\n");
			bw.write("2\r\n");
			bw.write("TABLES\r\n");
			bw.write("0\r\n");
			bw.write("TABLE\r\n");
			bw.write("2\r\n");
			bw.write("LAYER\r\n");
			bw.write("70\r\n");
			bw.write("4\r\n");
			bw.write("0\r\n");
			bw.write("LAYER\r\n");
			bw.write("2\r\n");
			bw.write("0\r\n");
			bw.write("70\r\n");
			bw.write("0\r\n");
			bw.write("62\r\n");
			bw.write("7\r\n");
			bw.write("6\r\n");
			bw.write("CONTINUOUS\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void CreateLayer(OutputStreamWriter bw,String LayerName)
	{
		try {
			bw.write("0\r\n");
			bw.write("LAYER\r\n");
			bw.write("2\r\n");
			bw.write(LayerName+"\r\n");
			bw.write("70\r\n");
			bw.write("0\r\n");
			bw.write("62\r\n");
			bw.write("7\r\n");
			bw.write("6\r\n");
			bw.write("CONTINUOUS\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
