package lkmap.Dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ContentShowView;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Envelope;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Enum.lkDatasetSourceType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Enum.lkRenderType;
import lkmap.Index.MapCellIndex;
import lkmap.Index.T4Index;
import lkmap.Layer.GeoLayer;
import lkmap.Render.UniqueValueRender;
import lkmap.Tools.Tools;

public class Dataset {
	public Dataset(DataSource DS) {
		this._DataSource = DS;

		// ͼ���Map������
		this.m_MapCellIndex = new MapCellIndex();
	}

	// ���ڱ����ݼ��Ŀ�������
	private MapCellIndex m_MapCellIndex = null;

	public MapCellIndex GetMapCellIndex() {
		return this.m_MapCellIndex;
	}
	// public void SetMapCellIndex(MapCellIndex
	// mCellIndex){this.m_MapCellIndex=mCellIndex;}

	// ƫ������ ��Ҫ���ڵ�ͼ
	private double _OffsetX = 0, _OffsetY = 0;

	public void SetOffset(double _offsetX, double _offsetY) {
		// �����ݽ���ƫ�ƴ���
		for (Geometry pGeometry : this._GeometryList.values()) {
			if (pGeometry != null) {
				this.OffsetTo(pGeometry, _offsetX, _offsetY, true);
			}
		}
		_OffsetX = _offsetX;
		_OffsetY = _offsetY;
	}

	private void OffsetTo(Geometry pGeometry, double offsetX, double offsetY, boolean AlwaysOffset) {
		if (!AlwaysOffset && offsetX == 0 && offsetY == 0)
			return;
		for (int p = 0; p < pGeometry.getPartCount(); p++) {
			Part part = pGeometry.GetPartAt(p);
			for (Coordinate Coor : part.getVertexList()) {
				Coor.setX(Coor.getX() - (AlwaysOffset ? this._OffsetX : 0) + offsetX);
				Coor.setY(Coor.getY() - (AlwaysOffset ? this._OffsetY : 0) + offsetY);
			}
		}
	}

	private Envelope _Envelope;

	public Envelope getEnvelope() {
		if (this._Envelope == null)
			this.CalEnvelope();
		return _Envelope;
	}

	public void setEnvelope(Envelope env) {
		this._Envelope = env;
	}

	/**
	 * ����ʵ�����Ӿ���
	 */
	public void CalEnvelope() {
		Envelope pEnv = new Envelope(0, 0, 0, 0);
		for (Geometry pGeometry : this.GetGeometryList()) {
			if (pGeometry.getStatus() == lkGeometryStatus.enDelete)
				continue;
			if (pEnv.IsZero())
				pEnv = pGeometry.getEnvelope();
			else
				pEnv = pEnv.Merge(pGeometry.getEnvelope());
		}
		this.setEnvelope(pEnv);
	}

	/**
	 * �����ֶ�
	 * 
	 * @param FieldName
	 * @param FieldSize
	 * @return
	 */
	public boolean AddField(String FieldName, int FieldSize) {
		String SQL = "ALTER TABLE %1$s ADD %2$s varchar(%2$s)";
		return this.getDataSource().ExcuteSQL(SQL);
	}

	// ���Ա�ṹ
	private List<FieldInfo> _TableStruct = null;

	public List<FieldInfo> getTableStruct() {
		// ������Ա�ṹΪNUll��������µ����Խṹ
		if (_TableStruct == null)
			_TableStruct = this._DataSource.GetTableStruct(this.getId());
		return _TableStruct;
	}

	// ���ݼ�Id��ΪGUIDֵ�����ظ�
	private String _Id = "";

	public String getId() {
		return _Id;
	}

	public void setId(String value) {
		this._Id = value;
	}

	private String proType = "";

	public String getProjectType() {
		return proType;
	}

	public void setPorjectType(String projectType) {
		proType = projectType;
	}

	// ���ݼ���Ӧ�����ݱ����ƣ�Ĭ�������ݼ�Id��ͬ
	public String getDataTableName() {
		return this._Id + "_D";
	}

	public String getIndexTableName() {
		return this._Id + "_I";
	}

	// ��ǰͼ���Ƿ񱻱༭��
	private boolean _Edited = false;

	public boolean getEdited() {
		return _Edited;
	}

	public void setEdited(boolean value) {
		_Edited = value;
	}

	// ���ݼ����ͣ��㣬�ߣ��棩
	private lkGeoLayerType _Type = lkGeoLayerType.enUnknow;

	public lkGeoLayerType getType() {
		return this._Type;
	}

	public void setType(lkGeoLayerType value) {
		this._Type = value;
	}

	// ���󶨵�ͼ��
	private GeoLayer _BindGeoLayer = null;

	public GeoLayer getBindGeoLayer() {
		return _BindGeoLayer;
	}

	public void setBindGeoLayer(GeoLayer value) {
		_BindGeoLayer = value;
	}

	// ���ݼ���������Դ���ͣ���Ҫ�����ֲɼ��������Ǳ������ݣ�Ϊͼ��ת����׼��
	private lkDatasetSourceType _SourceType = lkDatasetSourceType.enUnknow;

	public lkDatasetSourceType getSourceType() {
		return _SourceType;
	}

	public void setSourceType(lkDatasetSourceType st) {
		_SourceType = st;
	}

	// ͼ��ʵ���б�
	private HashMap<Integer, Geometry> _GeometryList = new HashMap<Integer, Geometry>();

	/**
	 * �������ݼ���ǰ�����е�ʵ�壬ע��˼��ϲ���ȫ��ʵ��
	 * 
	 * @return
	 */
	public Collection<Geometry> GetGeometryList() {
		return this._GeometryList.values();
	}

	public void Clear() {
		this._GeometryList.clear();
	}

	/**
	 * �õ�ָ��Id��ͼ��ʵ��
	 * 
	 * @param SysId
	 * @return
	 */
	public Geometry GetGeometry(int SysId) {
		return _GeometryList.get(SysId);
	}

	/**
	 * ����ʵ��
	 * 
	 * @param pGeometry
	 *            ��ʵ��
	 * @param IfUpdateLayerIndex
	 *            �Ƿ����ͼ�㼶����
	 * @return
	 */
	public boolean AddGeometry(Geometry pGeometry) {
		this._GeometryList.put(pGeometry.getSysId(), pGeometry);
		return true;
	}

	/**
	 * ���ݿ���ʵ�������������ǵ�ǰ�б��ڵ�ʵ������
	 * 
	 * @return
	 */
	public int GetAllObjectCount() {
		int AllCount = 0;
		String SQL = "select count(SYS_ID) as AllCount from " + this.getDataTableName() + " where SYS_STATUS='0'";
		SQLiteDataReader DR = this.getDataSource().Query(SQL);
		if (DR == null)
			return 0;
		if (DR.Read()) {
			AllCount = DR.GetInt32(0);
		}
		DR.Close();
		return AllCount;
	}

	// ��������Ӧ������Դ
	private DataSource _DataSource = null;

	public DataSource getDataSource() {
		return _DataSource;
	}

	// �����ݼ�����ɾ�������ݼ�
	public boolean Remove() {
		return false;
		// this._DataSource.getDatasets().remove(this);
		// return this._DataSource.ExcuteSQL("Drop Table " +
		// this.getTableName());
	}

	/**
	 * �������ݼ���1-�������ɾ����ʶ��ʵ��(SYS_STATUS=1)�� 2-��������������ֹͣ��ʵ��(SYS_STATUS=2)��
	 * 
	 * @return
	 */
	public boolean Purge() {
		// �������ɾ����ʶ��ʵ��(SYS_STATUS=1)
		String DelSQL_I = "delete from " + this.getIndexTableName() + " where SYS_ID in (select SYS_ID from "
				+ this.getDataTableName() + " where SYS_STATUS='1')";
		String DelSQL_D = "delete from " + this.getDataTableName() + " where SYS_STATUS='1'";
		boolean pOK = this.getDataSource().ExcuteSQL(DelSQL_I) && this.getDataSource().ExcuteSQL(DelSQL_D);
		if (!pOK)
			return false;
		
		// ��������������ֹͣ��ʵ��(SYS_STATUS=2)
		String SQL = "select SYS_ID,SYS_GEO from " + this.getDataTableName() + " where SYS_STATUS='2'";
		SQLiteDataReader DR = this.getDataSource().Query(SQL);
		if (DR == null)
			return true;
		List<HashMap<String, Object>> updateItemList = new ArrayList<HashMap<String, Object>>();
		while (DR.Read()) {
			int SYSID = DR.GetInt32(0); // SYS_ID
			byte[] bytes = (byte[]) DR.GetBlob(1); // ͼ��

			// ���ݲ�ͬ��ͼ����ò�ͬ��ByteToGeometry�������ǿ��ܴ��ڶಿ��ʵ������
			Geometry m_Geometry = Tools.ByteToGeometry(bytes, this.getType());
			m_Geometry.CalEnvelope();
			T4Index TIndex = m_Geometry.CalCellIndex(this.GetMapCellIndex());
			
			if(TIndex.GetCol()==70000 && TIndex.GetRow()==70000)
			{
				continue;
			}
			
			double Length = 0, Area = 0;
			if (this.getType() == lkGeoLayerType.enPolyline) {
				Length = ((Polyline) m_Geometry).getLength(true);
			}
			if (this.getType() == lkGeoLayerType.enPolygon) {
				Length = ((Polygon) m_Geometry).getLength(true);
				Area = ((Polygon) m_Geometry).getArea(true);
			}
			HashMap<String, Object> obj = new HashMap<String, Object>();
			obj.put("SYS_ID", SYSID);
			obj.put("Envelope", m_Geometry.getEnvelope());
			obj.put("T4Index", TIndex);
			obj.put("SYS_Length", Length);
			obj.put("SYS_Area", Area);
			updateItemList.add(obj);
		}
		DR.Close();

		// ���²�������Ϣ
		if (updateItemList.size() == 0)
			return true;
		for (HashMap<String, Object> hm : updateItemList) {
			Envelope pEnv = (Envelope) hm.get("Envelope");
			T4Index TIndex = (T4Index) hm.get("T4Index");

			// ��������
			String UpdateSQL_I = "update " + this.getIndexTableName()
					+ " set RIndex=%1$s,CIndex=%2$s,MinX=%3$s,MinY=%4$s,MaxX=%5$s,MaxY=%6$s where SYS_ID="
					+ hm.get("SYS_ID");
			UpdateSQL_I = String.format(UpdateSQL_I, TIndex.GetRow(), TIndex.GetCol(), pEnv.getMinX(), pEnv.getMinY(),
					pEnv.getMaxX(), pEnv.getMaxY());

			// ���³��������
			String UpdateSQL_D = "update " + this.getDataTableName()
					+ " set SYS_STATUS='0',SYS_Length=%1$s,SYS_Area=%2$s where SYS_ID=" + hm.get("SYS_ID");
			UpdateSQL_D = String.format(UpdateSQL_D, hm.get("SYS_Length"), hm.get("SYS_Area"));

			pOK = this.getDataSource().ExcuteSQL(UpdateSQL_I) && this.getDataSource().ExcuteSQL(UpdateSQL_D);
			if (!pOK)
				return false;
		}
		return true;
	}

	public boolean UpdateXiaoBanHao(int id, String filedName, String xiaobanhao) {
		String updateSQL = "update " + this.getDataTableName() + " set " + filedName + "='" + xiaobanhao
				+ "' where SYS_ID=" + id;
		return this.getDataSource().ExcuteSQL(updateSQL);

	}

	// ���¹���ͼ�������
	public boolean BuildSpatialIndex() {
		// //MapCellIndex�����Χ������֮ǰ
		// // Envelope pEnv = this.m_MapCellIndex.getExtend();
		//
		// Envelope newEnv = new Envelope(0,0,0,0);
		// for (int objIdx = 0; objIdx < this.getRecordCount(); objIdx++)
		// {
		// Geometry pGeometry = this.GetGeometry(objIdx);
		// if (pGeometry.getStatus()==lkGeometryStatus.enDelete)continue;
		// if (newEnv.IsZero())newEnv = pGeometry.getEnvelope();
		// else newEnv = newEnv.Merge(pGeometry.getEnvelope());
		// }
		//
		//// //�ж����Χ�Ƿ���Ҫ����
		//// if
		// (Math.abs(pEnv.getLeftTop().getX()-newEnv.getLeftTop().getX())<10 &&
		//// Math.abs(pEnv.getLeftTop().getY()-newEnv.getLeftTop().getY())<10 &&
		//// Math.abs(pEnv.getWidth()-newEnv.getWidth())<10&&
		//// Math.abs(pEnv.getHeight()-newEnv.getHeight())<10)
		//// {
		//// return true;
		//// }
		// this._CellIndex.ClearAllIndex();
		// this.m_MapCellIndex.setEnvelope(newEnv);
		//
		// //����ͼ��ʵ��������
		// for (int objIdx = 0; objIdx < this.getRecordCount(); objIdx++)
		// {
		// Geometry pGeometry = this.GetGeometry(objIdx);
		// if (pGeometry.getStatus()==lkGeometryStatus.enDelete)continue;
		// pGeometry.setIndex(objIdx);
		// this._CellIndex.SetIndex(pGeometry.CalCellIndex(this.m_MapCellIndex),
		// pGeometry.getIndex());
		// }
		//
		return true;

	}

	// /**
	// * �����ݼ�����Ҫ�Ǽ������ݼ��������Ӿ���
	// * @return
	// */
	// public boolean Open()
	// {
	// //����˾�Ŀ��������SYS_STATUS��״̬��Ҳ���ǿ��ܴ����ϴ�ɾ��û��������
	// String SQL="update "+this.getTableName()+" set SYS_STATUS=0";
	// if (this._DataSource.getEditing())this._DataSource.ExcuteSQL(SQL);
	//
	// try
	// {
	// //��ȡ�����η�Χ
	// String SQLExtend="select MinX,MinY,MaxX,MaxY from T_LayerExtend where
	// LayerID='"+this.getId()+"'";
	// SQLiteDataReader DR=this._DataSource.Query(SQLExtend);
	// if(DR==null)return true;
	// while(DR.Read())
	// {
	// double MinX=Double.valueOf(DR.GetString("MinX"));
	// double MinY=Double.valueOf(DR.GetString("MinY"));
	// double MaxX=Double.valueOf(DR.GetString("MaxX"));
	// double MaxY=Double.valueOf(DR.GetString("MaxY"));
	// this.GetMapCellIndex().setEnvelope(new Envelope(new
	// Coordinate(MinX,MaxY),new Coordinate(MaxX,MinY)));
	// this.setEnvelope(new Envelope(new Coordinate(MinX,MaxY),new
	// Coordinate(MaxX,MinY)));
	// }DR.Close();
	// }
	// catch(Exception e)
	// {
	// return false;
	// }
	//
	// return true;
	// }

	/**
	 * ��������Ļ��ѡ�����ѡ��ʵ��
	 */
	public boolean QueryWithSelEnvelope(Envelope SelEnvelope, Selection SelSelection) {
		for (Geometry StGeometry : this.GetGeometryList()) {
			// ɾ��״̬�Ĳ�ѡ��
			if (StGeometry.getStatus() == lkGeometryStatus.enDelete)
				continue;

			// ��㣬��ָ�����η�Χ����Ϊѡ��
			if (this.getType() == lkGeoLayerType.enPoint) {
				Point StPoint = (Point) StGeometry;
				if (SelEnvelope.ContainsPoint(StPoint.getCoordinate())) {
					// SelSelection.Add(StGeometry);
					this.AddGeometryToSelection(SelSelection, StGeometry);
				}
			}

			// �߲㣬��������� 1��������Σ���ָ�����η�Χ����Ϊѡ�� 2��������Σ�����Ͼ���
			if (this.getType() == lkGeoLayerType.enPolyline) {
				Polyline StPolyline = (Polyline) StGeometry;

				if (SelEnvelope.getType()) // �������
				{
					if (SelEnvelope.Contains(StPolyline.getEnvelope())) {
						// SelSelection.Add(StGeometry);
						this.AddGeometryToSelection(SelSelection, StGeometry);
					}
				} else // ������� ��߾�ѡ��
				{
					if (SelEnvelope.Contains(StPolyline.getEnvelope())) {
						// SelSelection.Add(StGeometry);
						this.AddGeometryToSelection(SelSelection, StGeometry);
					} else {
						if (SelEnvelope.Intersect(StPolyline.getEnvelope())) // ������Ӿ����ཻ
						{
							if (StPolyline.getSpatialRelation().Intersect(SelEnvelope.ConvertToPolyline())) {
								// SelSelection.Add(StGeometry);
								this.AddGeometryToSelection(SelSelection, StGeometry);
							}
						}
					}
				}
			}

			// ��㣬��������� 1��������Σ���ָ�����η�Χ����Ϊѡ�� 2��������Σ�����Ͼ���
			if (this.getType() == lkGeoLayerType.enPolygon) {
				Polygon StPolygon = (Polygon) StGeometry;
				if (SelEnvelope.getType()) // �������
				{
					if (SelEnvelope.Contains(StPolygon.getEnvelope())) {
						// SelSelection.Add(StGeometry);
						this.AddGeometryToSelection(SelSelection, StGeometry);
					}
				} else // ������� ��߾�ѡ��
				{
					if (SelEnvelope.Intersect(StPolygon.getEnvelope())) // ������Ӿ����ཻ
					{
						if (SelEnvelope.Contains(StPolygon.getEnvelope())) {
							// SelSelection.Add(StGeometry);
							this.AddGeometryToSelection(SelSelection, StGeometry);
						} else {
							boolean intersectPoly = false;
							for (int j = 0; j < StPolygon.getPartCount(); j++) {
								Polyline intPL = new Polyline();
								intPL.AddPart(StPolygon.GetPartAt(j));
								intersectPoly = intPL.getSpatialRelation().Intersect(SelEnvelope.ConvertToPolyline());
								if (intersectPoly)
									break;
							}
							if (intersectPoly)
								this.AddGeometryToSelection(SelSelection, StGeometry);// SelSelection.Add(StGeometry);
						}
					}
				}
			}
		}
		return false;
	}

	// public void QueryByExtend(Envelope extend,Selection showSelection)
	// {
	//
	// //ѡ����ο������ڵ�
	// String WhereFilter = this.GetMapCellIndex().CalCellIndexFilter(extend);
	//
	// //���η�Χ����
	// String EnvelopeFilter = "not (max(minx,%1$s)>min(maxx,%3$s) or
	// max(miny,%2$s)>min(maxy,%4$s))";
	// EnvelopeFilter = String.format(EnvelopeFilter, extend.getMinX(),
	// extend.getMinY(), extend.getMaxX(), extend.getMaxY());
	//
	// //�����ѯ�������
	// String SQL = "select SYS_ID from %1$s where (%2$s) and (%3$s)";
	// SQL = String.format(SQL, this.getTableName() + "_I", WhereFilter,
	// EnvelopeFilter);
	//
	//
	// Log.v("��ѯSQL", SQL);
	// List<String> idList = new ArrayList<String>();
	// SQLiteDataReader DR = this.getDataSource().Query(SQL);
	// {
	// if (DR != null) while (DR.Read())
	// {
	// String SYSID = DR.GetString("SYS_ID");
	// } DR.Close();
	// }
	//
	// //�Ա��Ѿ����ڵ�ʵ���б��ų��Ѿ����ڵ�ʵ�壬�ٴβ�ѯ���ݿ��ѯ����ʵ��ʵ������
	// this.QueryGeometryFromDB(idList);
	// }

	/**
	 * �����ݿ��ѯʵ�� QueryIndexList
	 * 
	 * @param QueryIndexList
	 * @return
	 */

	int SYSID = 0;

	public boolean QueryGeometryFromDB(List<String> idList) {
		SQLiteDataReader DR = null;
		String SQL = null;
		try {
			// ������
			List<Integer> PurgeIdList = new ArrayList<Integer>();
			Set<Integer> allIdList = this._GeometryList.keySet();
			for (int id : allIdList) {
				if (idList.contains(id + "")) {
					idList.remove(id + "");
					Geometry saveGeometry = this.GetGeometry(id);
					this._BindGeoLayer.getShowSelection().Add(saveGeometry);
				} else
					PurgeIdList.add(id);
			}
			for (int id : PurgeIdList)
				this._GeometryList.remove(id);

			if (idList.size() == 0)
				return true;
			// ��ѯʵ���SYS_ID�б�
			String Where = "SYS_ID in (" + Tools.JoinT(",", idList) + ") and SYS_STATUS='0'";

			// ��Ҫ��ѯ���ֶ�
			String SelectField = "SYS_GEO,SYS_ID";

			// ͬʱ����Ψһֵ�ֶμ���ע�ֶ�
			if (this._BindGeoLayer.getRender().getType() == lkRenderType.enUniqueValue) {
				List<String> UVFList = ((UniqueValueRender) this._BindGeoLayer.getRender()).GetUniqueValueFieldList();
				SelectField += ",(" + Tools.JoinT("||','||", UVFList) + ") as UniqueValueField";
			}
			if (this._BindGeoLayer.getRender().getIfLabel()) {
				SelectField += ",(" + this._BindGeoLayer.getRender().getLabelField().replace(",", "||','||")
						+ ") as LabelField";
			}

			SQL = "select " + SelectField + " from " + this.getDataTableName() + " where " + Where;
			DR = this.getDataSource().Query(SQL);

			// ��ȡʵ��
			if (DR == null)
				return false;
			while (DR.Read()) {
				try
				{
//					Log.e("pre SYSID", SYSID+"");

					SYSID = DR.GetInt32(1); // SYS_ID
//					Log.e("SYSID", SYSID+"");
					
					byte[] bytes = (byte[]) DR.GetBlob(0); // ͼ��
//					Log.e("SYS_GEO", bytes.length+"");

					// ���ݲ�ͬ��ͼ����ò�ͬ��ByteToGeometry�������ǿ��ܴ��ڶಿ��ʵ������
					Geometry m_Geometry = Tools.ByteToGeometry(bytes, this.getType());
					m_Geometry.setSysId(SYSID);

					// ƫ�ƴ�����Ҫ���ڵ�ͼ
					this.OffsetTo(m_Geometry, this._OffsetX, this._OffsetY, false);

					// ��ʵ����뵽�б���
					this.AddGeometry(m_Geometry);

					// ���뵽ѡ�񼯺���
					this._BindGeoLayer.getShowSelection().Add(m_Geometry);

					if (this._BindGeoLayer.getRender().getType() == lkRenderType.enUniqueValue)
						m_Geometry.setTagForUniqueSymbol(DR.GetString("UniqueValueField"));
					if (this._BindGeoLayer.getRender().getIfLabel())
						m_Geometry.setTag(DR.GetString("LabelField"));

					this._BindGeoLayer.getRender().UpdateSymbol(m_Geometry);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				
			}

		} catch (Exception e) {
			lkmap.Tools.Tools
					.ShowMessageBox("ͼ�㣺" + this.getDataTableName() + "����ѯ����ʧ�ܣ�" + SYSID + SQL + " " + e.getMessage());
		} finally {
			if (DR != null) {
				DR.Close();
			}
		}
		return true;

	}

	public boolean AddGeometryOutEnvelope(List<Integer> idList) {
		Set<Integer> allIdList = this._GeometryList.keySet();
		for (int id : allIdList) {
			if (idList.contains(id + "")) {
				idList.remove(id + "");

			}

		}

		if (idList.size() == 0)
			return false;
		// ��ѯʵ���SYS_ID�б�
		String Where = "SYS_ID in (" + Tools.Join(",", idList) + ") and SYS_STATUS='0'";

		// ��Ҫ��ѯ���ֶ�
		String SelectField = "SYS_GEO,SYS_ID";

		SQLiteDataReader DR = null;
		try {

			// ͬʱ����Ψһֵ�ֶμ���ע�ֶ�
			if (this._BindGeoLayer.getRender().getType() == lkRenderType.enUniqueValue) {
				List<String> UVFList = ((UniqueValueRender) this._BindGeoLayer.getRender()).GetUniqueValueFieldList();
				SelectField += ",(" + Tools.JoinT("||','||", UVFList) + ") as UniqueValueField";
			}
			if (this._BindGeoLayer.getRender().getIfLabel()) {
				SelectField += ",(" + this._BindGeoLayer.getRender().getLabelField().replace(",", "||','||")
						+ ") as LabelField";
			}

			String SQL = "select " + SelectField + " from " + this.getDataTableName() + " where " + Where;
			DR = this.getDataSource().Query(SQL);

			// ��ȡʵ��
			if (DR == null)
				return false;

			while (DR.Read()) {
				SYSID = DR.GetInt32(1); // SYS_ID
				byte[] bytes = (byte[]) DR.GetBlob(0); // ͼ��

				// ���ݲ�ͬ��ͼ����ò�ͬ��ByteToGeometry�������ǿ��ܴ��ڶಿ��ʵ������
				Geometry m_Geometry = Tools.ByteToGeometry(bytes, this.getType());
				m_Geometry.setSysId(SYSID);

				// ƫ�ƴ�����Ҫ���ڵ�ͼ
				this.OffsetTo(m_Geometry, this._OffsetX, this._OffsetY, false);

				// ��ʵ����뵽�б���
				this.AddGeometry(m_Geometry);

				// ���뵽ѡ�񼯺���
				this._BindGeoLayer.getShowSelection().Add(m_Geometry);

				if (this._BindGeoLayer.getRender().getType() == lkRenderType.enUniqueValue)
					m_Geometry.setTagForUniqueSymbol(DR.GetString("UniqueValueField"));
				if (this._BindGeoLayer.getRender().getIfLabel())
					m_Geometry.setTag(DR.GetString("LabelField"));

				this._BindGeoLayer.getRender().UpdateSymbol(m_Geometry);
			}

		} catch (Exception ex) {
			lkmap.Tools.Tools.ShowMessageBox("ͼ�㣺" + this.getDataTableName() + "����ѯ����ʧ�ܣ�" + SYSID + ex.getMessage());
		} finally {
			if (DR != null) {
				DR.Close();
			}
		}

		return true;
	}

	/**
	 * �����ݿ��ڲ�ѯʵ��
	 * 
	 * @param SYS_IDList
	 *            ʵ��SYS_ID�б����ΪNull�����ѯȫ��ʵ��
	 * @return
	 */
	public ArrayList<Geometry> QueryGeometryFromDB1(List<String> SYS_IDList) {
		ArrayList<Geometry> pGeometryList = new ArrayList<Geometry>();
		String SQL = "select SYS_GEO,SYS_ID from %1$s where SYS_ID in (%2$s) and SYS_STATUS='0' order by SYS_ID";
		if (SYS_IDList != null)
			SQL = String.format(SQL, this.getDataTableName(), Tools.JoinT(",", SYS_IDList));
		else
			SQL = "select SYS_GEO,SYS_ID from " + this.getDataTableName() + " where SYS_STATUS='0' order by SYS_ID";

		SQLiteDataReader DR = this.getDataSource().Query(SQL);

		// ��ȡʵ��
		if (DR == null)
			return pGeometryList;
		while (DR.Read()) {
			int SYSID = DR.GetInt32(1); // SYS_ID
			byte[] bytes = (byte[]) DR.GetBlob(0); // ͼ��

			// ���ݲ�ͬ��ͼ����ò�ͬ��ByteToGeometry�������ǿ��ܴ��ڶಿ��ʵ������
			Geometry m_Geometry = Tools.ByteToGeometry(bytes, this.getType());
			m_Geometry.setSysId(SYSID);
			pGeometryList.add(m_Geometry);
		}
		DR.Close();
		return pGeometryList;
	}
	
	public ArrayList<Geometry> QueryGeometryWithValue(String whereSql)
	{
		ArrayList<Geometry> pGeometryList = new ArrayList<Geometry>();
		String SQL = "select SYS_GEO,SYS_ID from " + this.getDataTableName() + " where SYS_STATUS='0' order by SYS_ID";
		if(whereSql != null && whereSql.length()>0)
		{
			SQL = "select SYS_GEO,SYS_ID from " + this.getDataTableName() + " where SYS_STATUS='0' and "+ whereSql +" order by SYS_ID";
		}
		
		return QueryGeometryFromSql(SQL);
		
	}
	
	
	private  ArrayList<Geometry> QueryGeometryFromSql(String sql)
	{
		ArrayList<Geometry> pGeometryList = new ArrayList<Geometry>();
		SQLiteDataReader DR = this.getDataSource().Query(sql);

		// ��ȡʵ��
		if (DR == null)
			return pGeometryList;
		while (DR.Read()) {
			int SYSID = DR.GetInt32(1); // SYS_ID
			byte[] bytes = (byte[]) DR.GetBlob(0); // ͼ��

			// ���ݲ�ͬ��ͼ����ò�ͬ��ByteToGeometry�������ǿ��ܴ��ڶಿ��ʵ������
			Geometry m_Geometry = Tools.ByteToGeometry(bytes, this.getType());
			m_Geometry.setSysId(SYSID);
			pGeometryList.add(m_Geometry);
		}
		DR.Close();
		return pGeometryList;
	}
	
	

	/**
	 * ���ݵ�ǰ�ӿڷ�Χѡ��ʵ�壬����ѡ���ʵ�����ѡ�񼯵���
	 * 
	 * @param Extend
	 * @param ShowSelection
	 * @return
	 */
	public boolean QueryWithExtend(Envelope Extend, Selection ShowSelection) {
		// //��������������ȷ����ǰ��ʾ��Χ��������������
		// List<Integer> newExtendCellIndex =
		// this.GetMapCellIndex().CalCellIndex(Extend);
		//
		// //��Ҫ�������ʵ���б�
		// List<Integer> _PurgeObjectIndexList = new ArrayList<Integer>();
		//
		// if (!this._DataSource.getEditing())
		// {
		// //���ݼ�����Ҫ�������������
		// List<Integer> PurgeCellIndex = new ArrayList<Integer>();;
		// for (int cellIndex : this.getCellIndex().getCurrentCellIndex())
		// {
		// if
		// (newExtendCellIndex.indexOf(cellIndex)<0)PurgeCellIndex.add(cellIndex);
		// }
		// //��Ҫ�������ʵ��
		// for (int cellIndex : PurgeCellIndex)
		// {
		// for (int ObjIndex : this._CellIndex.getCellIndex().get(cellIndex))
		// {
		// if (_PurgeObjectIndexList.indexOf(ObjIndex)<0)
		// _PurgeObjectIndexList.add(ObjIndex);
		// }
		// }
		// }
		// this._CellIndex.setCurrentCellIndex(newExtendCellIndex);
		//
		//
		// //���ݴ����ݿ����¶�ȡ��ʵ���б�
		// List<Integer> QueryIndexListByDB = new ArrayList<Integer>();
		//
		// //�Ѿ���ѯ����ʵ�������б���ҪĿ�����ų�����ж������Ҳ���Ǵ���һ��ʵ��������������
		// List<Integer> AlwayQueryedIndexList = new ArrayList<Integer>();
		//
		// for (int cellIndex : this.getCellIndex().getCurrentCellIndex())
		// {
		// List<Integer> ObjIndexList =
		// this._CellIndex.getCellIndex().get(cellIndex);
		// for(int ObjectIdx : ObjIndexList)
		// {
		// //�ڵ�ǰѡ�����Ƿ����
		// if (ShowSelection.InList(ObjectIdx))continue;
		//
		// //�Ƿ��Ѿ������
		// if (AlwayQueryedIndexList.indexOf(ObjectIdx)>=0)continue;else
		// AlwayQueryedIndexList.add(ObjectIdx);
		//
		// //ȡ��ʵ��
		// GeometryEx pGeometryEx = this.GetGeometryEx(ObjectIdx);
		//
		// //�ж�Geometry����Ӿ����Ƿ��ڵ�ǰ��ͼ�����ڲ�
		// boolean InView = Extend.Intersect(pGeometryEx.getEnvelope());
		// if (InView)
		// {
		// if (pGeometryEx.getGeometry()==null)
		// {
		// if (QueryIndexListByDB.indexOf(ObjectIdx) < 0)
		// QueryIndexListByDB.add(ObjectIdx);
		// }
		// else
		// {
		// ShowSelection.Add(pGeometryEx.getGeometry());
		// }
		// }
		// }
		// }
		//
		// //�����ݿ��в�ѯʵ��ͼ����Ϣ,QueryIndexListByDB�ڴ洢Index������Dindex
		// if (QueryIndexListByDB.size() > 0)
		// {
		// if (this.QueryGeometryFromDB(QueryIndexListByDB,false))
		// {
		// for (int idx : QueryIndexListByDB)
		// {
		// Geometry mGeometry = this.GetGeometry(idx);
		// if (mGeometry==null)
		// {
		// lkmap.Tools.Tools.ShowMessageBox("Ϊɶ���ˣ�");
		// continue;
		// }
		// //��ʶΪɾ���Ĳ�ѡ��
		// if (mGeometry.getStatus() == lkGeometryStatus.enDelete) continue;
		// ShowSelection.Add(mGeometry);
		// }
		// }
		// }
		//
		// //���ڴ����Ƴ�����ʵ�壬���ظ�����Ҳ���ǿ���������б����У�ͬʱ��ShowSelectionҲ��
		// this.ClearGeometryFromMemory(_PurgeObjectIndexList,
		// ShowSelection,true);
		return true;

	}

	// ��������ʵ�壬�ͷ��ڴ�
	public void ClearGeometryFromMemory(List<Integer> PurgeObjIndexList, Selection ShowSelection,
			boolean IfCheckShowSelection) {
		// if (!this.getDataSource().getEditing())
		// {
		// for (int OIdx : PurgeObjIndexList)
		// {
		// boolean InShowSelection = false;
		// //�ж���ShowSelection���Ƿ����
		// if (IfCheckShowSelection &&
		// ShowSelection.getGeometryIndexList().indexOf(OIdx) >=
		// 0)InShowSelection=true;
		//
		// //���
		// if (!InShowSelection) this._GeometryList.get(OIdx).setGeometry(null);
		// }
		// }
	}

	public List<HashMap<String, Object>> getShareTwoPointPoly(Coordinate point1, Coordinate point2, int selectedId,
			boolean justBGLayer) {

		List<HashMap<String, Object>> SharedGeos = new ArrayList<HashMap<String, Object>>();
		for (Geometry StGeometry : this.GetGeometryList()) {
			// ɾ��״̬�Ĳ�ѡ��
			if (StGeometry.getStatus() == lkGeometryStatus.enDelete)
				continue;
			if (this.getType() == lkGeoLayerType.enPolygon) {
				if (StGeometry.getSysId() == selectedId) {
					continue;
				}
				if (StGeometry.getEnvelope().ContainsPoint(point1)) {
					Polygon polygon = (Polygon) StGeometry;
					for (int i = 0; i < polygon.getPartCount(); i++) {
						List<Coordinate> vertexList = polygon.GetPartAt(i).getVertexList();
						boolean isOnBorder = false;
						for (int j = 0; j < vertexList.size(); j++) {
							Coordinate coordinate = vertexList.get(j);
							if ((Math.abs(coordinate.getX() - point1.getX()) < 0.001)
									&& (Math.abs(coordinate.getY() - point1.getY()) < 0.001)) {

								if (j < (vertexList.size() - 1)
										&& (Math.abs(vertexList.get(j + 1).getX() - point2.getX()) < 0.001)
										&& (Math.abs(vertexList.get(j + 1).getY() - point2.getY()) < 0.001)) {
									HashMap<String, Object> hashMap = new HashMap<String, Object>();
									hashMap.put("geo", StGeometry);
									hashMap.put("partIndex", i);
									hashMap.put("vertexIndex", j);
									hashMap.put("coor", coordinate);
									SharedGeos.add(hashMap);
									isOnBorder = true;
									break;
								}

							} else if ((Math.abs(coordinate.getX() - point2.getX()) < 0.001)
									&& (Math.abs(coordinate.getY() - point2.getY()) < 0.001)) {
								if ((Math.abs(vertexList.get(j - 1).getX() - point1.getX()) < 0.001)
										&& (Math.abs(vertexList.get(j - 1).getY() - point1.getY()) < 0.001)) {
									HashMap<String, Object> hashMap = new HashMap<String, Object>();
									hashMap.put("geo", StGeometry);
									hashMap.put("partIndex", i);
									hashMap.put("vertexIndex", j - 1);
									hashMap.put("coor", coordinate);
									SharedGeos.add(hashMap);
									isOnBorder = true;
									break;
								}
							}
						}
						if (isOnBorder) {
							break;
						}
					}
				}
			}
		}

		return SharedGeos;
	}

	public List<HashMap<String, Object>> getSharePointPoly(Coordinate movedPoint, int selectedId, boolean justBGLayer) {

		List<HashMap<String, Object>> SharedGeos = new ArrayList<HashMap<String, Object>>();
		for (Geometry StGeometry : this.GetGeometryList()) {
			// ɾ��״̬�Ĳ�ѡ��
			if (StGeometry.getStatus() == lkGeometryStatus.enDelete)
				continue;
			if (this.getType() == lkGeoLayerType.enPolygon) {
				if (StGeometry.getSysId() == selectedId) {
					continue;
				}
				if (StGeometry.getEnvelope().ContainsPoint(movedPoint)) {
					Polygon polygon = (Polygon) StGeometry;
					for (int i = 0; i < polygon.getPartCount(); i++) {
						List<Coordinate> vertexList = polygon.GetPartAt(i).getVertexList();
						boolean isOnBorder = false;
						for (int j = 0; j < vertexList.size(); j++) {
							Coordinate coordinate = vertexList.get(j);
							if ((Math.abs(coordinate.getX() - movedPoint.getX()) < 0.001)
									&& (Math.abs(coordinate.getY() - movedPoint.getY()) < 0.001)) {
								HashMap<String, Object> hashMap = new HashMap<String, Object>();
								hashMap.put("geo", StGeometry);
								hashMap.put("partIndex", i);
								hashMap.put("vertexIndex", j);
								hashMap.put("coor", coordinate.Clone());
								SharedGeos.add(hashMap);
								isOnBorder = true;
								break;
							}
						}
						if (isOnBorder) {
							break;
						}
					}
				}
			}
		}

		return SharedGeos;
	}

	public boolean ShowContent(Coordinate SelPoint, double SelTolerance, Selection SelSelection) {
		// ���¼���ѡ������ڵ���������
		boolean isShow = false;
		for (Geometry StGeometry : this.GetGeometryList()) {
			// ɾ��״̬�Ĳ�ѡ��
			if (StGeometry.getStatus() == lkGeometryStatus.enDelete)
				continue;

			// ��㣬��������֮��ľ��룬��ѡ������ڣ�������ѡ��
			if (this.getType() == lkGeoLayerType.enPoint) {
				Point StPoint = (Point) StGeometry;
				if (StPoint.HitTest(SelPoint, SelTolerance, false)) {

					if (SelSelection.Remove(StGeometry)) {
						SelSelection.Add(StGeometry);
						ContentShowView cShowView = new ContentShowView(this.getBindGeoLayer().getId(),
								StGeometry.getSysId(), StPoint.getCenterPoint());
						return true;
					}

				}

			}

			
		}

		return isShow;

	}

	public boolean ShowContentWithSelEnvelope(Envelope SelEnvelope) {
		for (Geometry StGeometry : this.GetGeometryList()) {
			// ɾ��״̬�Ĳ�ѡ��
			if (StGeometry.getStatus() == lkGeometryStatus.enDelete)
				continue;

			// ��㣬��ָ�����η�Χ����Ϊѡ��
			if (this.getType() == lkGeoLayerType.enPoint) {
				Point StPoint = (Point) StGeometry;
				if (SelEnvelope.ContainsPoint(StPoint.getCoordinate())) {
					ContentShowView cShowView = new ContentShowView(this.getBindGeoLayer().getId(),
							StGeometry.getSysId(), StPoint.getCenterPoint());
					return true;
				}
			}

			// �߲㣬��������� 1��������Σ���ָ�����η�Χ����Ϊѡ�� 2��������Σ�����Ͼ���
			if (this.getType() == lkGeoLayerType.enPolyline) {
				Polyline StPolyline = (Polyline) StGeometry;

				if (SelEnvelope.getType()) // �������
				{
					if (SelEnvelope.Contains(StPolyline.getEnvelope())) {
						ContentShowView cShowView = new ContentShowView(this.getBindGeoLayer().getId(),
								StGeometry.getSysId(), StPolyline.getCenterPoint());
						return true;
					}
				} else // ������� ��߾�ѡ��
				{
					if (SelEnvelope.Contains(StPolyline.getEnvelope())) {
						ContentShowView cShowView = new ContentShowView(this.getBindGeoLayer().getId(),
								StGeometry.getSysId(), StPolyline.getCenterPoint());
					} else {
						if (SelEnvelope.Intersect(StPolyline.getEnvelope())) // ������Ӿ����ཻ
						{
							if (StPolyline.getSpatialRelation().Intersect(SelEnvelope.ConvertToPolyline())) {
								ContentShowView cShowView = new ContentShowView(this.getBindGeoLayer().getId(),
										StGeometry.getSysId(), StPolyline.getCenterPoint());
							}
						}
					}
				}
			}
		}

		return false;
	}

	
	public boolean HitTest(Coordinate SelPoint, double SelTolerance, Selection SelSelection, Boolean isBGLayer) {
		return this.HitTest(SelPoint, SelTolerance, false, SelSelection, isBGLayer);
	}

	/**
	 * ��ѡ
	 * 
	 * @param SelPoint
	 *            ѡ���λ
	 * @param SelTolerance
	 *            ѡ�����
	 * @param MultiSelct
	 *            �Ƿ��ѡ
	 * @param SelSelection
	 *            ѡ��������
	 * @return
	 */
	public boolean HitTest(Coordinate SelPoint, double SelTolerance, boolean MultiSelct, Selection SelSelection,
			Boolean isBGLayer) {
		boolean SelectOK = false;
		Polyline SelectBox = null;

		// ���¼���ѡ������ڵ���������
		for (Geometry StGeometry : this.GetGeometryList()) {
			// ɾ��״̬�Ĳ�ѡ��
			if (StGeometry.getStatus() == lkGeometryStatus.enDelete)
				continue;

			// ��㣬��������֮��ľ��룬��ѡ������ڣ�������ѡ��
			if (this.getType() == lkGeoLayerType.enPoint) {
				Point StPoint = (Point) StGeometry;
				if (StPoint.HitTest(SelPoint, SelTolerance, isBGLayer)) {
					SelectOK = true;
					// SelSelection.Add(StGeometry);
					this.AddGeometryToSelection(SelSelection, StGeometry);
					if (!MultiSelct)
						return true;
				}
			}

			// �߲㣬����ѡ����ο����ߵ��ཻ
			if (this.getType() == lkGeoLayerType.enPolyline) {
				Polyline StPolyline = (Polyline) StGeometry;
				if (SelectBox == null) {
					double R = SelTolerance / 2;

					List<Coordinate> vertextList = new ArrayList<Coordinate>();
					SelectBox = new Polyline();
					vertextList.add(new Coordinate(SelPoint.getX() - R, SelPoint.getY() - R));
					vertextList.add(new Coordinate(SelPoint.getX() + R, SelPoint.getY() - R));
					vertextList.add(new Coordinate(SelPoint.getX() + R, SelPoint.getY() + R));
					vertextList.add(new Coordinate(SelPoint.getX() - R, SelPoint.getY() + R));
					SelectBox.AddPart(new Part(vertextList));
				}
				if (StPolyline.getSpatialRelation().Intersect(SelectBox)) {
					SelectOK = true;
					// SelSelection.Add(StGeometry);
					this.AddGeometryToSelection(SelSelection, StGeometry);
					if (!MultiSelct)
						return true;
				}
			}

			// ��㣬����ѡȡ���Ƿ������ڣ�����ڣ�������ѡ��
			if (this.getType() == lkGeoLayerType.enPolygon) {
				Polygon StPolygon = (Polygon) StGeometry;
				if (StPolygon.HitTest(SelPoint, SelTolerance, isBGLayer)) {
					SelectOK = true;
					// SelSelection.Add(StGeometry);
					this.AddGeometryToSelection(SelSelection, StGeometry);
					if (!MultiSelct)
						return true;
				}
			}
		}
		return SelectOK;
	}

	/**
	 * ����ʵ�嵽ѡ�񼯺ϣ��������ȥ��
	 */
	private void AddGeometryToSelection(Selection SelSelection, Geometry pGeomety) {
		if (!SelSelection.Remove(pGeomety))
		{
			SelSelection.Add(pGeomety);
			if(PubVar.isZhuijiaing && this.getId().equals(PubVar.zhuijiaCheckCard.mLayerId))
			{
				if(PubVar.isRemoveTuban)
				{
					PubVar.zhuijiaCheckCard.ZhuijiaID(pGeomety.getSysId(),false);
				}
				else
				{
					PubVar.zhuijiaCheckCard.ZhuijiaID(pGeomety.getSysId(),true);
				}
				
				
			}
		}
		
			
	}
	
	

	public void Dispose() {
		this._GeometryList.clear();
	}
}
