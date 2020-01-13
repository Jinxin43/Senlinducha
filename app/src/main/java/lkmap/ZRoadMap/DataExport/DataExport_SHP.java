package lkmap.ZRoadMap.DataExport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Envelope;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.Project_Web;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkFieldType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Map.StaticObject;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

/**
 * //导出Shape文件，最新改进(2013年8月28日)
 * 
 * @author lmgk
 *
 */
public class DataExport_SHP {

	// 数据导出类型
	private String m_ExportType = "";

	/**
	 * 导出数据集到arcgis(shp)格式
	 * 
	 * @param pDataset
	 */
	public boolean Export(Dataset pDataset, String shpFileName,boolean isZhuiJia) {
		CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
		if (CS.GetName().equals("WGS-84坐标"))
			m_ExportType = "WGS84";
		else
			m_ExportType = "平面";
		

		// 读取全部实体
		List<Geometry> geometryList = new ArrayList<Geometry>();
		String whereSql = null;
		if(isZhuiJia)
		{
			v1_Layer vLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
			if(vLayer != null)
			{
				String dataField = vLayer.GetDataFieldNameByFieldName("调查年度");
				if(dataField.length()>0)
				{
					whereSql = dataField+ " is not null and "+ dataField+" !=''";
					geometryList = pDataset.QueryGeometryWithValue(whereSql);
				}
				else
				{
					Tools.ShowMessageBox("没有调查年度字段，请检查该图层是否是标准森林督查判读图斑！");
					return false;
				}
			}
			else
			{
				Tools.ShowMessageBox("找不到图层！");
				return false;
				
			}
			
		}
		else
		{
			geometryList = pDataset.QueryGeometryFromDB1(null);
		}
		
		boolean prjOK = this.ToPrj(shpFileName + ".prj");
		boolean dbfOK = this.ToDbf(pDataset, shpFileName + ".dbf",whereSql);
		boolean shpOK = this.ToShp(pDataset, geometryList, shpFileName + ".shp");
		boolean shxOK = this.ToShx(pDataset, geometryList, shpFileName + ".shx");
		return prjOK && dbfOK && shpOK && shxOK;
	}

	/**
	 * 导出Prj
	 * 
	 * @param prjFileName
	 * @return
	 */
	private boolean ToPrj(String prjFileName) {
		CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
		String PrjInfo = "PROJCS[\"%1$s\",GEOGCS" + "[\"GCS_%1$s\",DATUM[\"D_%1$s\",SPHEROID[\"%1$s\",%2$s,%3$s]],"
				+ "PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]," + "PROJECTION[\"Gauss_Kruger\"],"
				+ "PARAMETER[\"False_Easting\",%4$s]," + "PARAMETER[\"False_Northing\",0.0],"
				+ "PARAMETER[\"Central_Meridian\",%5$s]," + "PARAMETER[\"Scale_Factor\",1.0],"
				+ "PARAMETER[\"Latitude_Of_Origin\",0.0],UNIT[\"Meter\",1.0]]";

		String PROName = CS.GetName();

		if (PROName.equals("西安80坐标")) {
			PROName = "Xian_1980";
		} else if (PROName.equals("WGS-84坐标")) {
			PROName = "WGS_1984";
		} else if (PROName.equals("北京54坐标")) {
			PROName = "Beijing_1954";
		} else if (PROName.equals("2000国家大地坐标系")) {
			PROName = "China_2000";
			// TODO:
		}

		PrjInfo = String.format(PrjInfo, PROName, CS.GetA(), CS.GetE(), CS.GetEasting(), CS.GetCenterMeridian());

		if (this.m_ExportType.equals("WGS84")) {
			PrjInfo = "GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]";
		}
		try {

			String strUT = new String(PrjInfo.getBytes("GB2312"), "GB2312");

			try {
				// FileWriter fw = new FileWriter(prjFileName);
				// BufferedWriter bw = new BufferedWriter(fw);
				// bw.write(strUT);
				File prjFile = new File(prjFileName);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(prjFile), "GB2312"));
				bw.write(strUT);
				bw.flush();

				bw.close();
				return true;
			} catch (Exception e) {
				return false;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;

	}

	public void copyFile(File fromFile,File toFile) throws IOException{
        FileInputStream ins = new FileInputStream(fromFile);
        FileOutputStream out = new FileOutputStream(toFile);
        byte[] b = new byte[1024];
        int n=0;
        while((n=ins.read(b))!=-1){
            out.write(b, 0, n);
        }
        
        ins.close();
        out.close();
    }
	
	/**
	 * 导出dbf
	 * 
	 * @param pDataset
	 * @param dbfFileName
	 * @throws FileNotFoundException
	 */
	@SuppressLint("NewApi")
	private boolean ToDbf(Dataset pDataset, String dbfFileName,String whereSql) {
		// 字段列表
		int ColumnLength = 0;
		List<String> HeadList = new ArrayList<String>();

		// 读取数据集的字段信息
		List<String> DataFieldList = new ArrayList<String>();
		v1_Layer vLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
		if (vLayer == null) {
			vLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
					.GetLayerByID(pDataset.getId());
		}
		
		if(whereSql != null)
		{
			HeadList.add("SYS_ID,N,10,0"); // 字段名称,数据类型,长度
			DataFieldList.add("SYS_ID"); // TAData.dbx内的F1,F2...
			ColumnLength+=10;
			
			HeadList.add("SYS_STATUS,N,2,0"); // 字段名称,数据类型,长度
			DataFieldList.add("SYS_STATUS"); // TAData.dbx内的F1,F2...
			ColumnLength+=2;
			
			HeadList.add("SYS_TYPE,C,10,0"); // 字段名称,数据类型,长度
			DataFieldList.add("SYS_TYPE"); // TAData.dbx内的F1,F2...
			ColumnLength+=10;
			
			HeadList.add("SYS_OID,C,50,0"); // 字段名称,数据类型,长度
			DataFieldList.add("SYS_OID"); // TAData.dbx内的F1,F2...
			ColumnLength+=50;
			
			HeadList.add("SYS_LABEL,C,50,0"); // 字段名称,数据类型,长度
			DataFieldList.add("SYS_LABEL"); // TAData.dbx内的F1,F2...
			ColumnLength+=50;
			
			HeadList.add("SYS_DATE,C,50,0"); // 字段名称,数据类型,长度
			DataFieldList.add("SYS_DATE"); // TAData.dbx内的F1,F2...
			ColumnLength+=50;
			
			HeadList.add("SYS_PHOTO,C,254,0"); // 字段名称,数据类型,长度
			DataFieldList.add("SYS_PHOTO"); // TAData.dbx内的F1,F2...
			ColumnLength+=254;
			
			HeadList.add("SYS_Length,N,16,4"); // 字段名称,数据类型,长度
			DataFieldList.add("SYS_Length"); // TAData.dbx内的F1,F2...
			ColumnLength+=16;
			
			HeadList.add("SYS_Area,N,16,4"); // 字段名称,数据类型,长度
			DataFieldList.add("SYS_Area"); // TAData.dbx内的F1,F2...
			ColumnLength+=16;
		}
		

		for (v1_LayerField LF : vLayer.GetFieldList()) {
			String DataType = "";
			if (LF.GetFieldType() == lkFieldType.enString) {
				DataType = "C";
				ColumnLength += LF.GetFieldSize();
			}
			if (LF.GetFieldType() == lkFieldType.enFloat) {
				DataType = "N";
				ColumnLength += LF.GetFieldSize();
			}
			if (LF.GetFieldType() == lkFieldType.enDateTime) {
				DataType = "C";
				ColumnLength += LF.GetFieldSize();
			}
			if (LF.GetFieldType() == lkFieldType.enInt) {
				DataType = "N";
				ColumnLength += LF.GetFieldSize();
			}
			if (LF.GetFieldType() == lkFieldType.enBoolean) {
				DataType = "C";
				ColumnLength += 5;
			}
			HeadList.add(LF.GetFieldName() + "," + DataType + "," + LF.GetFieldSize() + "," + LF.GetFieldDecimal()); // 字段名称,数据类型,长度
			DataFieldList.add(LF.GetDataFieldName()); // TAData.dbx内的F1,F2...
		}

		// 字段总数
		int ColumnCount = HeadList.size();

		// 读取属性文本
		List<ArrayList<String>> FeatureList = new ArrayList<ArrayList<String>>();

		String SQL = "select %1$s from %2$s where SYS_STATUS='0' order by SYS_ID";
		if(whereSql != null)
		{
			SQL = "select %1$s from %2$s where SYS_STATUS='0' and "+ whereSql +" order by SYS_ID";
		}
		SQL = String.format(SQL, Tools.JoinT(",", DataFieldList), pDataset.getDataTableName());
		SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
		if (DR == null)
			return true;
		while (DR.Read()) {
			ArrayList<String> dataList = new ArrayList<String>();
			for (String DF : DataFieldList) {
				String FValue = DR.GetString(DF);
				if (FValue == null)
					FValue = "";
				else if(FValue.contains("Photo"))
				{
					String newFValue="";
					for(String value:FValue.split(","))
					{
						int startIndex = value.lastIndexOf("/");
						if(startIndex>0)
						{
							value= value.subSequence(startIndex+1, value.length()).toString();
						}
						if(newFValue.length()>0)
						{
							newFValue += ","+value;
						}
						else
						{
							newFValue += value;
						}
						
					}
					
					FValue = newFValue;
				}
				
				dataList.add(FValue);
			}
			FeatureList.add(dataList);
		}
		DR.Close();

		try {
			// 写入dbf
			RandomAccessFile br = new RandomAccessFile(new File(dbfFileName), "rw");

			/**
			 * 0x02 FoxBASE 0x03 FoxBASE+/Dbase III plus, no memo • 0x30 Visual
			 * FoxPro • 0x31 Visual FoxPro, autoincrement enabled • 0x43 dBASE
			 * IV SQL table files, no memo • 0x63 dBASE IV SQL system files, no
			 * memo • 0x83 FoxBASE+/dBASE III PLUS, with memo • 0x8B dBASE IV
			 * with memo • 0xCB dBASE IV SQL table files, with memo • 0xF5
			 * FoxPro 2.x (or earlier) with memo • 0xFB FoxBASE
			 */

			br.writeByte(3);// 版本

			/**
			 * 表示最近的更新日期，按照YYMMDD格式，以1900年为起始，即第一个字节表示文件最后保存时的年份-1900，
			 * 第二个字节的值为保存时的月， 第三个字节的值为保存时的日
			 */
			br.writeByte(13); // 年+1900
			br.writeByte(7); // 月
			br.writeByte(9); // 日

			// 文件中的记录条数,即行数
			br.write(this.IntToBytes(FeatureList.size())); // 4个字节

			// 文件头中的字节数，在此之后的字节为表格记录数据，通过此数值可以推算出有多少列，每列描述为32个字节
			int headLength = ColumnCount * 32 + 33;
			br.write(this.ShortToBytes(headLength)); // 2个字节

			// 一条记录中的字节长度，即每行数据所占的长度，也就是字段定义长度+1
			br.write(this.ShortToBytes(ColumnLength + 1)); // 2个字节

			// 保留字节，用于以后添加新的说明性信息时使用，这里用0来填写。
			br.write(new byte[2]); // 2个字节

			// 表示未完成的操作
			br.writeByte(0); // 1个字节

			// dBASE IV编密码标记。
			br.writeByte(0);// 1个字节

			// 保留字节，用于多用户处理时使用。
			br.write(new byte[12]); // 12个字节

			/**
			 * DBF文件的MDX标识。在创建一个DBF 表时 ，如果使用了MDX 格式的索引文件， 那么 DBF
			 * 表的表头中的这个字节就自动被设置了一个标志，当你下次试图重新打开这个DBF表的时候，
			 * 数据引擎会自动识别这个标志，如果此标志为真，则数据引擎将试图打开相应的MDX 文件
			 */
			br.writeByte(0);// 1个字节

			// 页码标记
			br.writeByte(0); // 1个字节

			// 保留字节，用于以后添加新的说明性信息时使用，这里用0来填写
			br.write(new byte[2]); // 2个字节

			/**
			 * 32－N （x*32）个字节 这段长度由表格中的列数（即字段数，Field Count）决定，
			 * 每个字段的长度为32，如果有x列，则占用的长度为x*32，
			 * 这每32个字节里面又按其规定包含了每个字段的名称、类型等信息，具体见下面的表。 N＋1 1个字节
			 * 作为字段定义的终止标识，值为0x0D。
			 */

			// 计算字段数目
			for (String HL : HeadList) {
				String[] dbFieldInfo = HL.split(","); // 字段名称,类型,长度

				// 字段的名称，11个字节，是ASCII码值
				String FieldName = dbFieldInfo[0]; // 注意此处最多只能为7个字符
				byte[] bytes = FieldName.getBytes("GB2312");// System.Text.Encoding.GetEncoding("gb2312").GetBytes(FieldName);
				// byte[] bytes = FieldName.getBytes();
				byte[] fbytes = new byte[11];
				for (int i = 0; i < bytes.length; i++)
					if (i < 11)
						fbytes[i] = bytes[i];
				br.write(fbytes); // 11个字节

				/**
				 * 字段的数据类型，为ASCII码值。每个值对应不同的字段数据类型，1个字节 B 二进制型 C 字符型 D 日期型 G
				 * (General or OLE) N 数值型(Numeric) L 逻辑型（Logical） M (Memo)
				 */

				byte b = dbFieldInfo[1].getBytes()[0];
				br.write(b); // 1个字节

				// 保留字节，用于以后添加新的说明性信息时使用，默认为0，4个字节
				br.write(new byte[4]); // 4个字节

				// 字段的长度，表示该字段对应的值在后面的记录中所占的长度，
				br.writeByte(Integer.parseInt(dbFieldInfo[2])); // 1个字节

				// 字段的精度,1个字节
				// if(dbFieldInfo[1].equals("N"))
				// {
				br.writeByte(Integer.parseInt(dbFieldInfo[3]));
				// }
				// else
				// {
				// br.writeByte(0); //1个字节
				// }

				// 保留字节，用于以后添加新的说明性信息时使用，默认为0，2个字节
				br.write(new byte[2]); // 2个字节

				// 工作区ID，1个字节
				br.writeByte(0); // 1个字节

				// 保留字节，用于以后添加新的说明性信息时使用，默认为0，11个字节
				br.write(new byte[11]); // 11个字节

			}

			// 作为字段定义的终止标识，值为0x0D，1个字节
			br.writeByte(13); // 1个字节

			// 写入数据
			for (ArrayList<String> data : FeatureList) {
				// 每行数据中第一个20，跳过
				br.writeByte(20); // 1个字节

				// 写入属性数据
				for (int i = 0; i < HeadList.size(); i++) {
					String[] FieldDefine = HeadList.get(i).split(","); // 名称,类型,长度
					int len = Integer.parseInt(FieldDefine[2]); // 字段长度
					String FieldValue = data.get(i); // 字段值
					byte[] valuebytes = FieldValue.getBytes("GB2312");
					// byte[] valuebytes = FieldValue.getBytes();
					byte[] wrValue = new byte[len];

					for (int z = 0; z < valuebytes.length; z++)
						if (z < len)
							wrValue[z] = valuebytes[z];
					for (int j = valuebytes.length; j < wrValue.length; j++)
						wrValue[j] = 32;
					br.write(wrValue);
				}
			}
			br.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 导出shp
	 * 
	 * @param pDataset
	 * @param shpFileName
	 * @return
	 */
	private boolean ToShp(Dataset pDataset, List<Geometry> pGeometryList, String shpFileName) {
		try {
			RandomAccessFile br = new RandomAccessFile(new File(shpFileName), "rw");

			// 写入shape头
			this.WriteShpHeader(pDataset, pGeometryList, br, "SHP");

			// 写入数据
			for (int i = 0; i < pGeometryList.size(); i++) {
				Geometry pGeometry = pGeometryList.get(i);

				// Record Number，高位，从1开始
				br.writeInt((i + 1)); // 4字节,int,高位

				if (pDataset.getType() == lkGeoLayerType.enPoint) {
					// Content Length,高位
					br.writeInt(10); // 4字节,int,高位

					// Shape类型1-Point,3-Polyline,4-Polygon
					br.write(this.IntToBytes(1)); // 4字节,int

					// 坐标
					Coordinate PT = ((lkmap.Cargeometry.Point) pGeometry).getCoordinate();
					double dX = PT.getX();
					double dY = PT.getY();
					if (this.m_ExportType.equals("WGS84")) {
						Coordinate Coor = Project_Web.Web_XYToBL(dX, dY);
						dX = Coor.getX();
						dY = Coor.getY();
					}

					br.write(this.DoubleToBytes(dX)); // 8字节,double
					br.write(this.DoubleToBytes(dY));
				}

				if (pDataset.getType() == lkGeoLayerType.enPolyline || pDataset.getType() == lkGeoLayerType.enPolygon) {

					int partCount = pGeometry.getPartCount();
					int VertexCount = pGeometry.getVertexCount();

					// Content Length,高位
					br.writeInt((VertexCount * 16 + 44 + 4 * partCount) / 2); // 4字节,int,高位

					// Shape类型1-Point,3-Polyline,5-Polygon
					if (pDataset.getType() == lkGeoLayerType.enPolyline)
						br.write(this.IntToBytes(3)); // 4字节,int
					if (pDataset.getType() == lkGeoLayerType.enPolygon)
						br.write(this.IntToBytes(5)); // 4字节,int

					Envelope envp = pGeometry.getEnvelope();
					double MinX = envp.getMinX();
					double MinY = envp.getMinY();
					double MaxX = envp.getMaxX();
					double MaxY = envp.getMaxY();

					if (this.m_ExportType.equals("WGS84")) {
						Coordinate LT = StaticObject.soProjectSystem.XYToWGS84(envp.getLeftTop().getX(),
								envp.getLeftTop().getY(), 0);
						Coordinate RB = StaticObject.soProjectSystem.XYToWGS84(envp.getRightBottom().getX(),
								envp.getRightBottom().getY(), 0);
						MinX = LT.getX();
						MinY = RB.getY();
						MaxX = RB.getX();
						MaxY = LT.getY();
					}

					// Bounding Box,Xmin,Ymin,Xmax,YMax
					br.write(this.DoubleToBytes(MinX));
					br.write(this.DoubleToBytes(MinY)); // 8字节,double
					br.write(this.DoubleToBytes(MaxX));
					br.write(this.DoubleToBytes(MaxY));

					// Number of Parts
					br.write(this.IntToBytes(partCount)); // 4字节,int

					// Total Number of Points
					if (pDataset.getType() == lkGeoLayerType.enPolyline)
						br.write(this.IntToBytes(VertexCount)); // 4字节,int
					if (pDataset.getType() == lkGeoLayerType.enPolygon)
						br.write(this.IntToBytes(VertexCount)); // 4字节,int

					// Index to first Point in Part
					List<Integer> partIndexList = pGeometry.GetPartIndexList();
					for (int parIndex : partIndexList) {
						br.write(this.IntToBytes(parIndex)); // 4字节,int
					}

					// Points for all parts
					for (int p = 0; p < pGeometry.getPartCount(); p++) {
						Part part = pGeometry.GetPartAt(p);

						if (pDataset.getType() == lkGeoLayerType.enPolyline) {
							for (Coordinate LPT : part.getVertexList()) {
								double dX = LPT.getX();
								double dY = LPT.getY();
								if (this.m_ExportType.equals("WGS84")) {
									Coordinate Coor = Project_Web.Web_XYToBL(dX, dY);
									dX = Coor.getX();
									dY = Coor.getY();
								}
								br.write(this.DoubleToBytes(dX)); // 8字节,double
								br.write(this.DoubleToBytes(dY));
							}
						}

						if (pDataset.getType() == lkGeoLayerType.enPolygon) {
							int CoorCount = part.getVertexList().size();
							for (int c = CoorCount - 1; c >= 0; c--) {
								Coordinate LPT = part.getVertexList().get(c);
								if (c == 0)
									LPT = part.getVertexList().get(CoorCount - 1);
								double dX = LPT.getX();
								double dY = LPT.getY();
								if (this.m_ExportType.equals("WGS84")) {
									Coordinate Coor = Project_Web.Web_XYToBL(dX, dY);
									dX = Coor.getX();
									dY = Coor.getY();
								}
								br.write(this.DoubleToBytes(dX)); // 8字节,double
								br.write(this.DoubleToBytes(dY));
							}
						}
					}

				}
			}

			br.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 导出Shx文件
	 * 
	 * @param pDataset
	 * @param shxFileName
	 */
	private boolean ToShx(Dataset pDataset, List<Geometry> pGeometryList, String shxFileName) {
		try {
			RandomAccessFile br = new RandomAccessFile(new File(shxFileName), "rw");

			// 写入shx头
			this.WriteShpHeader(pDataset, pGeometryList, br, "SHX");

			// 主文件中的第一个记录的偏移量是50
			int PLLen = 0;
			for (int i = 0; i < pGeometryList.size(); i++) {
				Geometry pGeometry = pGeometryList.get(i);
				int VertexCount = pGeometry.getVertexCount();
				int PartCount = pGeometry.getPartCount();

				/**
				 * 对于点实体，第一个，offset=50,contentLenght=10
				 * 第二个，offset=64,contentLenght=10 ...
				 */

				if (pDataset.getType() == lkGeoLayerType.enPoint) {
					// offset=记录长度（点实体的长度，X8+Y8+ShapeType4=20=10word，线=？，面=？）+记录长度（4）+起止地址（50）
					br.writeInt(50 + (10 + 4) * (i)); // offset,4字节，int,高位

					// 记录长度（点=10，线=？，面=？）
					br.writeInt(10); // Content Length,4字节，int,高位
				}

				if (pDataset.getType() == lkGeoLayerType.enPolyline) {
					// offset=记录长度（点实体的长度，X8+Y8+ShapeType4=20=10word，线=？，面=？）+记录长度（4）+起止地址（50）
					br.writeInt(50 + (PLLen + 4 * i)); // offset,4字节，int,高位

					// 记录长度（点=10，线=？，面=？）
					br.writeInt(VertexCount * 8 + 22 + 2); // Content
															// Length,4字节，int,高位

					PLLen += VertexCount * 8 + 22 + 2;
				}

				if (pDataset.getType() == lkGeoLayerType.enPolygon) {
					// VertexCount+; //加1的作用为首尾点闭合，节点多一个
					// offset=记录长度（点实体的长度，X8+Y8+ShapeType4=20=10word，线=？，面=？）+记录长度（4）+起止地址（50）
					br.writeInt(50 + (PLLen + 4 * i)); // offset,4字节，int,高位

					// 记录长度（点=10，线=？，面=？）
					int recordLen = (VertexCount * 16 + 44 + 4 * PartCount) / 2;
					br.writeInt(recordLen); // Content Length,4字节，int,高位
					PLLen += recordLen;
				}

			}
			br.close();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	// Shape文件头操作
	private void WriteShpHeader(Dataset pDataset, List<Geometry> pGeometryList, RandomAccessFile br, String Type) {
		try {
			// File Code,9994,高位
			br.writeInt(9994); // 4字节,int,高位

			// Unused，高位
			br.writeInt(0); // 4字节,int,高位

			// Unused,高位
			br.writeInt(0); // 4字节,int,高位

			// Unused,高位
			br.writeInt(0); // 4字节,int,高位

			// Unused,高位
			br.writeInt(0); // 4字节,int,高位

			// Unused,高位
			br.writeInt(0); // 4字节,int,高位

			// 记录数及坐标点总数
			int RecordCount = 0;
			int CoorCount = 0;
			int PartCount = 0;
			for (int i = 0; i < pGeometryList.size(); i++) {
				PartCount += pGeometryList.get(i).getPartCount();
				RecordCount++;
				CoorCount += pGeometryList.get(i).getVertexCount();
			}

			// 文件头类型
			if (Type.equals("SHP")) {
				// File Length,高位,文件头长度50+实体长度（记录头8+点20,线=？,面=？）* 记录数

				if (pDataset.getType() == lkGeoLayerType.enPoint) {
					br.writeInt(50 + ((8 + 20) * RecordCount) / 2); // 4字节,int,高位

					// Versiont 1000
					br.write(this.IntToBytes(1000)); // 4字节,int

					// ShapeType
					br.write(this.IntToBytes(1)); // 4字节,int
				}
				if (pDataset.getType() == lkGeoLayerType.enPolyline) {
					br.writeInt((50 + ((8 + 4 + 44) * RecordCount + 16 * CoorCount) / 2)); // 4字节,int,高位

					// Versiont 1000
					br.write(this.IntToBytes(1000)); // 4字节,int

					// ShapeType
					br.write(this.IntToBytes(3)); // 4字节,int
				}
				if (pDataset.getType() == lkGeoLayerType.enPolygon) {
					// int AllLen = 50+4*RecordCount+(CoorCount * 16+44 +
					// 4*PartCount)/2;
					// int AllLen = 50+(44+4)*RecordCount+(CoorCount * 16 +
					// 4*PartCount)/2;
					br.writeInt(50 + ((8 + 44) * RecordCount + 4 * PartCount + 16 * CoorCount) / 2); // 4字节,int,高位
					// br.writeInt(AllLen);
					// Versiont 1000
					br.write(this.IntToBytes(1000)); // 4字节,int

					// ShapeType
					br.write(this.IntToBytes(5)); // 4字节,int
				}

			}

			if (Type.equals("SHX")) {
				// File Length,高位
				br.writeInt((50 + RecordCount * 4)); // 以word表示,文件头50word+记录数*4,//4字节,int,高位

				// Versiont 1000
				br.write(this.IntToBytes(1000)); // 4字节,int

				// Shape类型
				if (pDataset.getType() == lkGeoLayerType.enPoint)
					br.write(this.IntToBytes(1)); // 4字节,int
				if (pDataset.getType() == lkGeoLayerType.enPolyline)
					br.write(this.IntToBytes(3)); // 4字节,int
				if (pDataset.getType() == lkGeoLayerType.enPolygon)
					br.write(this.IntToBytes(5)); // 4字节,int
			}

			// 写入最大最小外接矩形
			Envelope envp = PubVar.m_Map.getFullExtend();

			double Xmin = envp.getMinX(); // XMIN
			double Ymin = envp.getMinY(); // YMIN
			double Xmax = envp.getMaxX(); // XMAX
			double Ymax = envp.getMaxY(); // XMAY
			double Zmin = 0; // ZMIN
			double Zmax = 0; // ZMAX
			double Mmin = 0; // MMIN
			double Mmax = 0; // MMAX
			if (this.m_ExportType.equals("WGS84")) {
				Coordinate LT = StaticObject.soProjectSystem.XYToWGS84(envp.getLeftTop().getX(),
						envp.getLeftTop().getY(), 0);
				Coordinate RB = StaticObject.soProjectSystem.XYToWGS84(envp.getRightBottom().getX(),
						envp.getRightBottom().getY(), 0);
				Xmin = LT.getX();
				Ymin = RB.getY();
				Xmax = RB.getX();
				Ymax = LT.getY();
			}

			br.write(this.DoubleToBytes(Xmin));
			br.write(this.DoubleToBytes(Ymin)); // 8字节,double
			br.write(this.DoubleToBytes(Xmax));
			br.write(this.DoubleToBytes(Ymax));
			br.write(this.DoubleToBytes(Zmin));
			br.write(this.DoubleToBytes(Zmax));
			br.write(this.DoubleToBytes(Mmin));
			br.write(this.DoubleToBytes(Mmax));
		} catch (Exception e) {
		}

	}

	/**
	 * 将double转换为double的byte数组，并由高位转低位
	 * 
	 * @param n
	 * @return
	 */

	@SuppressLint("UseValueOf")
	private byte[] DoubleToBytes(double x) {
		byte[] b = new byte[8];
		long l = Double.doubleToLongBits(x);
		for (int i = 0; i < 8; i++) {
			b[i] = new Long(l).byteValue();
			l = l >> 8;
		}
		return b;
	}

	/**
	 * 将int转换为int的byte数组，并由高位转低位
	 * 
	 * @param n
	 * @return
	 */
	private byte[] IntToBytes(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[3 - i] = (byte) (n >> (24 - i * 8));
		}
		return b;
	}

	/**
	 * 将int转换为short的byte数组，并由高位转低位
	 * 
	 * @param n
	 * @return
	 */
	public byte[] ShortToBytes(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (n >> (24 - i * 8));
		}

		byte[] sb = new byte[2];
		sb[0] = b[3];
		sb[1] = b[2];
		return sb;
	}

	/// <summary>
	/// 将Int型值从低位转换为高位
	/// </summary>
	/// <param name="value"></param>
	/// <returns></returns>
	// private static byte[] IntLittleToBigBytes(int value)
	// {
	// Integer.reverse(i)
	// byte[] bytes = BitConverter.GetBytes(value);
	// Array.Reverse(bytes);
	// return bytes;
	// }
	//
	//
	// private static int trueLength(string str)
	// {
	//
	// // str 字符串
	// // return 字符串的字节长度
	// int lenTotal = 0;
	// int n = str.Length;
	// string strWord = "";
	// int asc;
	// for (int i = 0; i < n; i++)
	// {
	// strWord = str.Substring(i, 1);
	// asc = Convert.ToChar(strWord);
	// if (asc < 0 || asc > 127)
	// lenTotal = lenTotal + 2;
	// else
	// lenTotal = lenTotal + 1;
	// }
	//
	// return lenTotal;
	// }
	//
	// #endregion
	//
	// #endregion
}
