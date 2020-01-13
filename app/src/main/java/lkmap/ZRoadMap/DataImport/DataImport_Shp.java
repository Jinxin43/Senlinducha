package lkmap.ZRoadMap.DataImport;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import android.app.ProgressDialog;
import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Index.T4Index;
import lkmap.Map.StaticObject;
import lkmap.Symbol.LineSymbol;
import lkmap.Symbol.PointSymbol;
import lkmap.Symbol.PolySymbol;
import lkmap.Tools.BitConverter;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;
import lkmap.ZRoadMap.Project.v1_project_layer_ex;

public class DataImport_Shp {
	private ProgressDialog process = null;
	private DataImport_Dbf m_DbfReader = null;

	public v1_Layer CreateLayerByShp(String shpFileName) {
		this.m_DbfReader = new DataImport_Dbf();
		this.m_DbfReader.SetDbf(Tools.ChangeExName(shpFileName, "dbf"));

		// ����dbf�ļ��ṹ�����µ�ͼ��

		// try
		{
			// 1����ȡshp�ļ�����
			lkGeoLayerType geoLayerType = this.ReadShpType(shpFileName);

			// 2����ȡdbf�ļ��ֶ��б�
			String dbfFileName = Tools.ChangeExName(shpFileName, "dbf");
			List<HashMap<String, String>> FieldList = this.m_DbfReader.GetFieldList();

			// ����ͼ��
			v1_Layer newLayer = new v1_Layer();
			newLayer.SetLayerType(geoLayerType);
			newLayer.SetEditMode(lkEditMode.enNew);
			newLayer.SetLayerAliasName(Tools.GetFileName_NoEx(shpFileName));

			// ����ͼ����������Ĭ�Ϸ���
			if (newLayer.GetLayerType() == lkGeoLayerType.enPoint) {
				PointSymbol PS = new PointSymbol();
				newLayer.SetSimpleSymbol(PS.ToBase64());
			}
			if (newLayer.GetLayerType() == lkGeoLayerType.enPolyline) {
				LineSymbol LS = new LineSymbol();
				newLayer.SetSimpleSymbol(LS.ToBase64());
			}
			if (newLayer.GetLayerType() == lkGeoLayerType.enPolygon) {
				PolySymbol PS = new PolySymbol();
				newLayer.SetSimpleSymbol(PS.ToBase64());
			}
			for (HashMap<String, String> hm : FieldList) {
				v1_LayerField lf = new v1_LayerField();
				// Ϊ�ֶ�ʵ�帳ֵ
				lf.SetDataFieldName(hm.get("Field"));
				lf.SetFieldName(hm.get("Caption"));
				if (hm.get("Type").equals("C")) {
					lf.SetFieldTypeName("�ַ���");
				} else if (hm.get("Type").equals("F")) {
					lf.SetFieldTypeName("������");
				} else if (hm.get("Type").equals("B")) {
					lf.SetFieldTypeName("������");
				} else if (hm.get("Type").equals("N")) {
					lf.SetFieldTypeName("����");
				} else if (hm.get("Type").equals("I")) {
					lf.SetFieldTypeName("����");
				} else if (hm.get("Type").equals("D")) {
					lf.SetFieldTypeName("������");
				} else if (hm.get("Type").equals("L")) {
					lf.SetFieldTypeName("������");
				} else {
					lf.SetFieldTypeName("�ַ���");
				}

				try {
					lf.SetFieldSize(Integer.parseInt(hm.get("Length")));
				} catch (Exception ex) {
					lf.SetFieldSize(Integer.parseInt(hm.get("255")));
				}

				try {
					lf.SetFieldDecimal(Integer.parseInt(hm.get("FieldDecimal")));
				} catch (Exception e) {
					// Ĭ�ϸ�3
					lf.SetFieldDecimal(3);
				}

				// lf.SetFieldDecimal(Integer.parseInt(FieldDecimal));
				// lf.SetFieldEnumCode(FieldValue);
				// lf.SetFieldEnumEdit(FieldValueInput);
				newLayer.GetFieldList().add(lf);
			}
			// ����Ĭ�ϵ����ɼ�����Ϊ25000
			newLayer.SetVisibleScaleMax(25000);
			if (!v1_project_layer_ex.CreateOrUpdateLayer(newLayer))
				return null;
			PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList().add(newLayer);

			// //�����µ����ݼ���
			// string DatasetName = "T" + Guid.NewGuid().ToString().Replace("-",
			// "").ToUpper();
			// DataSource pDataSource =
			// PubVar.m_Workspace.GetDataSourceByEditing();
			// if (!pDataSource.CreateDataset(DatasetName, geoLayerType,
			// FieldList))
			// {
			// MessageBox.Show("����ͼ�㡾"+System.IO.Path.GetFileNameWithoutExtension(shpFileName)+"ʧ�ܣ�","ϵͳ��ʾ",
			// MessageBoxButtons.OK, MessageBoxIcon.Warning);
			// return "";
			// }
			// Dataset pDataset = pDataSource.GetDatasetByName(DatasetName);
			// pDataset.AliasName =
			// System.IO.Path.GetFileNameWithoutExtension(shpFileName);
			// GeoLayer pGeoLayer = new
			// GeoLayer(PublicVar.PubVar.m_MapControl.Map);
			// pGeoLayer.Dataset = pDataset;
			// pGeoLayer.Name = pDataset.Name;
			// pGeoLayer.Type = pDataset.Type;
			// PublicVar.PubVar.m_MapControl.Map.GeoLayers.AddLayer(pGeoLayer);
			// PublicVar.PubVar.m_Workspace.RenderLayer(pGeoLayer);
			//
			// //dbf���������б�
			// List<string> dbfDataList = this.ReadDbf(dbfFileName);
			// List<string> FieldNameStrList = new List<string>();
			// foreach (string Field in FieldList)
			// FieldNameStrList.Add(Field.Split(',')[0]);
			// string FieldListStr = string.Join(",",
			// FieldNameStrList.ToArray());
			//
			// ����shp����
			final String layerID = newLayer.GetLayerID();
			final String fileName = shpFileName;
			// Dataset pDataset =
			// PubVar.m_Workspace.GetDatasetById(newLayer.GetLayerID());
			// this.ReadShp(shpFileName, pDataset);
			// this.m_DbfReader.Close();

			try {
				process = new ProgressDialog(PubVar.m_DoEvent.m_Context, ProgressDialog.THEME_HOLO_LIGHT);
				process.setTitle("����SHP�ļ�");
				process.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

				process.setIcon(R.drawable.v1_messageinfo);
				process.setCancelable(false);
				process.setCanceledOnTouchOutside(false);

				process.setMax(m_DbfReader.getRowCount());
				process.show();
				process.onStart();

				new Thread() {
					public void run() {
						{
							Dataset pDataset = PubVar.m_Workspace.GetDatasetById(layerID);
							ReadShp(fileName, pDataset);
							m_DbfReader.Close();
							process.cancel();
						}
					}
				}.start();
			} catch (Exception e) {
				Tools.ShowMessageBox(e.getMessage());
			}

			return newLayer;
			//
			// //PublicVar.PubVar.m_MapControl.Map.GeoLayers.GetLayerByName(pDataSource.Identify
			// + "." + DatasetName).Render.UpdateAllSymbol();
			//
			// return pGeoLayer.Name;
		}
		// // catch (Exception e)
		// {
		// //System.Windows.Forms.MessageBox.Show("����shp����ʧ�ܣ�\r\nԭ��" +
		// e.Message, "ϵͳ��ʾ", System.Windows.Forms.MessageBoxButtons.OK,
		// System.Windows.Forms.MessageBoxIcon.Warning);
		// // return "";
		// }
		// //finally
		// {
		//
		// }

	}

	/**
	 * �������ݵ����ݼ�
	 * 
	 * @param shapfileName
	 * @param pDataset
	 * @param dbfDataList
	 */
	private void ReadShp(String shapfileName, Dataset pDataset) {
		String m_ImportType = "";
		CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
		if (CS.GetName().equals("WGS-84����"))
			m_ImportType = "WGS84";

		pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().beginTransaction();
		try {
			int RecordNumber = 0;
			RandomAccessFile br = new RandomAccessFile(new File(shapfileName), "r");

			// File Code,9994,��λ
			br.read(new byte[4]);

			// Unused����λ
			br.read(new byte[4]);

			// Unused,��λ
			br.read(new byte[4]);

			// Unused,��λ
			br.read(new byte[4]);

			// Unused,��λ
			br.read(new byte[4]);

			// Unused,��λ
			br.read(new byte[4]);

			// File Length,��λ
			br.read(new byte[4]);

			// Versiont 1000
			br.readInt();

			// ShapeType
			br.readInt();

			double Xmin = BitConverter.BigToLittleDouble(br.readDouble()); // XMIN
			double Ymin = (br.readDouble()); // YMIN
			double Xmax = BitConverter.BigToLittleDouble(br.readDouble()); // XMAX
			double Ymax = (br.readDouble()); // XMAX
			double Zmin = (br.readDouble()); // ZMIN
			double Zmax = (br.readDouble()); // ZMAX
			double Mmin = (br.readDouble()); // MMIN
			double Mmax = (br.readDouble()); // MMAX

			// �ж��Ƿ���Ҫȥ������

			int DelDH = 0;
			if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetDH() == 0) {
				String XStr1 = Tools.ConvertToDigi(Xmin + "", 0).replace(".", "");
				String XStr2 = Tools.ConvertToDigi(Xmax + "", 0).replace(".", "");
				if (XStr1.length() == 8 && XStr2.length() == 8) {
					String DHStr = XStr1.substring(0, 2);
					DelDH = Integer.parseInt(DHStr) * 1000000;
				}
			} else {
				// TODO:�Ӵ��Ŵ���ע��Max��Minֵ

			}

			// ���ļ��еĵ�һ����¼��ƫ������50
			int SYS_ID = 1;
			while (br.getFilePointer() < br.length()) {
				// Record Number����λ����1��ʼ
				int count = br.readInt();
				// process.setMax(count);

				// Content Length,��λ
				// byte[] nContendByte = new byte[4];
				br.read(new byte[4]);
				// int ContendLen =
				// BitConverter.BigToLittleInt(BytesToInt(nContendByte));

				// Shape���ͣ�1-Point,11-PointZ(XYZM),21-PointM(XYM)
				// 3-Polyline,13-PolylineZ(XYZM),23-PolylineM(XYM)
				// 5-Polygon,15-PolygonZ(XYZM),25-PolygonM(XYM)
				int ShapeType = BitConverter.BigToLittleInt(br.readInt());

				// ��ʵ��
				if (ShapeType == 1 || ShapeType == 11 || ShapeType == 21) {
					double X = BitConverter.BigToLittleDouble(br.readDouble());
					double Y = BitConverter.BigToLittleDouble(br.readDouble());
					double Z, M;
					if (ShapeType == 11) {
						Z = br.readDouble();
						M = br.readDouble();
					}
					if (ShapeType == 21) {
						Z = br.readDouble();
					}

					double oX = X, oY = Y;
					try {
						if (m_ImportType.equals("WGS84")) {
							Coordinate Coor = StaticObject.soProjectSystem.WGS84ToXY(X, Y, 0);
							oX = Coor.getX();
							oY = Coor.getY();
						}
					} catch (Exception ex)// ��ֹ����WGS84ToXyΪ��
					{
						Log.e("Read point Shp", ex.getMessage());
					}

					Point pt = new Point(oX - DelDH, oY);
					this.ImportGeometry(pDataset, pt, SYS_ID);

				}

				// ������
				if (ShapeType == 3 || ShapeType == 5 || ShapeType == 13 || ShapeType == 15 || ShapeType == 23
						|| ShapeType == 25) {
					// Bounding Box
					double MinX = br.readDouble();
					double MinY = br.readDouble();
					double MaxX = br.readDouble();
					double MaxY = br.readDouble();

					// Number of Parts
					int NumParts = BitConverter.BigToLittleInt(br.readInt());

					// Total Number of Points
					int NumPoints = BitConverter.BigToLittleInt(br.readInt());

					// Index to first Point in Part
					List<Integer> partInxList = new ArrayList<Integer>();
					for (int z = 1; z <= NumParts; z++) {
						partInxList.add(BitConverter.BigToLittleInt(br.readInt()));
					}
					partInxList.add(NumPoints);

					Geometry pGeometry = null;
					for (int i = 0; i < partInxList.size() - 1; i++) {
						// ����
						int pointCount = partInxList.get(i + 1) - partInxList.get(i);
						List<Coordinate> CoorList = new ArrayList<Coordinate>();
						for (int z = 1; z <= pointCount; z++) {
							double X = BitConverter.BigToLittleDouble(br.readDouble());
							double Y = BitConverter.BigToLittleDouble(br.readDouble());
							if (X < 1 || Y < 1) {
								continue;
							}
							double oX = X, oY = Y;

							if (m_ImportType.equals("WGS84")) {
								Coordinate Coor = StaticObject.soProjectSystem.WGS84ToXY(X, Y, 0);
								oX = Coor.getX();
								oY = Coor.getY();
							}

							CoorList.add(new Coordinate(oX - DelDH, oY));
						}

						if (ShapeType == 3 || ShapeType == 13 || ShapeType == 23) {
							if (pGeometry == null)
								pGeometry = new Polyline();
							Polyline PL = (Polyline) pGeometry;
							Part part = new Part();
							part.setVertext(CoorList);
							PL.AddPart(part);
						}

						if (ShapeType == 5 || ShapeType == 15 || ShapeType == 25) {
							if (pGeometry == null)
								pGeometry = new Polygon();
							Polygon PLY = (Polygon) pGeometry;
							Part part = new Part();
							// ������е�������Ϊshp�����뱾ϵͳ����˳���෴
							for (int c = CoorList.size() - 1; c >= 0; c--)
								part.getVertexList().add(CoorList.get(c));
							part.AutoSetPartType();
							PLY.AddPart(part);
							PLY.Closed();
						}
					}
					if(pGeometry != null)
					{
						this.ImportGeometry(pDataset, pGeometry, SYS_ID);
					}
					else
					{
						Log.e("import shp", SYS_ID +"ShapeType is "+ShapeType);
					}
					

					// ����Z,Mֵ
					if (ShapeType == 13 || ShapeType == 15) {
						double MinM = br.readDouble();
						double MaxM = br.readDouble();
						for (int i = 1; i <= NumPoints; i++)
							br.readDouble();

						double MinZ = br.readDouble();
						double MaxZ = br.readDouble();
						for (int i = 1; i <= NumPoints; i++)
							br.readDouble();

					}
					if (ShapeType == 23 || ShapeType == 25) {
						double MinM = br.readDouble();
						double MaxM = br.readDouble();
						for (int i = 1; i <= NumPoints; i++)
							br.readDouble();
					}
				}
				process.setProgress(SYS_ID);
				SYS_ID++;
			}
			br.close();
			pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().setTransactionSuccessful();
			pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
		} catch (Exception e) {
			pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
			// Tools.ShowMessageBox("Shp���ݵ���ʧ�ܣ�\r\nԭ��"+e.getMessage());
			Log.e("����ͼ��", "����ͼ��:" + e.getMessage());
			e.printStackTrace();
		}

	}

	// �����¼��ͼ���ڲ�
	private void ImportGeometry(Dataset pDataset, Geometry pGeometry, int SYS_ID) {
		// ��ȡ��ͼ�ζ�Ӧ�����Լ�¼
		List<String> featureList = this.m_DbfReader.ReadData();

		// ͼ��ת��Ϊbyte
		
		byte[] GeoByte = Tools.GeometryToByte(pGeometry);

		// ͼ��ʵ��
		double SYS_Length = 0, SYS_Area = 0;
		if (pDataset.getType() == lkGeoLayerType.enPolyline) {
			SYS_Length = ((Polyline) pGeometry).getLength(true);
		}
		if (pDataset.getType() == lkGeoLayerType.enPolygon) {
			SYS_Length = ((Polygon) pGeometry).getLength(true);
			SYS_Area = ((Polygon) pGeometry).getArea(true);
		}

		String SQL_D = "insert into " + pDataset.getDataTableName() + " "
				+ "(SYS_GEO,SYS_STATUS,SYS_TYPE,SYS_OID,SYS_Length,SYS_Area,%1$s) values "
				+ "(?,0,'SHP','%2$s','%3$s','%4$s','%5$s')";
		SQL_D = String.format(SQL_D, this.m_DbfReader.GetFieldListStr(), UUID.randomUUID().toString(), SYS_Length,
				SYS_Area, Tools.JoinT("','", featureList));

		Log.d("����ͼ��", "��������ͼ������[" + SQL_D + "]");

		if (pDataset.getDataSource().ExcuteSQL(SQL_D, new Object[] { GeoByte })) {
			// //��ȡ�²���ʵ���SYS_ID��
			// String SYS_ID = "-1";
			// String SQL = "select max(SYS_ID) as objectid from
			// "+pDataset.getDataTableName();
			// SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
			// if (DR.Read())
			// {
			// SYS_ID = Integer.valueOf(DR.GetString(0))+"";
			// }DR.Close();

			// ����������Ϣ
			T4Index TIndex = pGeometry.CalCellIndex(pDataset.GetMapCellIndex());

			String SQL_I = "insert into " + pDataset.getIndexTableName() + " "
					+ "(SYS_ID,RIndex,CIndex,MinX,MinY,MaxX,MaxY) values "
					+ "('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s')";
			SQL_I = String.format(SQL_I, SYS_ID, TIndex.GetRow(), TIndex.GetCol(), pGeometry.getEnvelope().getMinX(),
					pGeometry.getEnvelope().getMinY(), pGeometry.getEnvelope().getMaxX(),
					pGeometry.getEnvelope().getMaxY());
			Log.d("", "����������������[" + SQL_I + "]");
			if (pDataset.getDataSource().ExcuteSQL(SQL_I)) {

			}
		}

	}

	/**
	 * ��ȡshp�ļ�����
	 * 
	 * @param shpFileName
	 * @return
	 */
	private lkGeoLayerType ReadShpType(String shpFileName) {
		try {
			RandomAccessFile br = new RandomAccessFile(new File(shpFileName), "r");
			br.read(new byte[32]);
			// ShapeType
			int ShpType = br.readInt();
			ShpType = BitConverter.BigToLittleInt(ShpType);
			br.close();
			if (ShpType == 1 || ShpType == 11)
				return lkGeoLayerType.enPoint;
			if (ShpType == 3 || ShpType == 13)
				return lkGeoLayerType.enPolyline;
			if (ShpType == 5 || ShpType == 15)
				return lkGeoLayerType.enPolygon;
			return lkGeoLayerType.enUnknow;
		} catch (Exception e) {
			return lkGeoLayerType.enUnknow;
		}
	}

	public static int BytesToInt(byte[] b) {
		int i = (b[0] << 24) & 0xFF000000;
		i |= (b[1] << 16) & 0xFF0000;
		i |= (b[2] << 8) & 0xFF00;
		i |= b[3] & 0xFF;
		return i;
	}

	public static double BytesToDouble(byte[] b) {
		return Double.longBitsToDouble(BytesToLong(b));
	}

	public static long BytesToLong(byte[] b) {
		long l = ((long) b[0] << 56) & 0xFF00000000000000L;
		// �����ǿ��ת��Ϊlong����ôĬ�ϻᵱ��int���������32λ��ʧ
		l |= ((long) b[1] << 48) & 0xFF000000000000L;
		l |= ((long) b[2] << 40) & 0xFF0000000000L;
		l |= ((long) b[3] << 32) & 0xFF00000000L;
		l |= ((long) b[4] << 24) & 0xFF000000L;
		l |= ((long) b[5] << 16) & 0xFF0000L;
		l |= ((long) b[6] << 8) & 0xFF00L;
		l |= (long) b[7] & 0xFFL;
		return l;
	}
}
