package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Envelope;
import lkmap.Dataset.DataSource;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkDatasetSourceType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkRenderType;
import lkmap.Layer.GeoLayer;
import lkmap.Tools.Tools;

public class v1_BKVectorLayerExplorer {

	// ��ͼ����Դ��Ӧ��ͼ���б�
	private List<v1_Layer> m_LayerList = new ArrayList<v1_Layer>();

	public List<v1_Layer> GetLayerList() {
		return this.m_LayerList;
	}

	// ���̶�Ӧ��ʸ����ͼ�ļ��б�
	private List<HashMap<String, Object>> m_BKFileLlist = new ArrayList<HashMap<String, Object>>();

	public List<HashMap<String, Object>> GetBKFileList() {
		return this.m_BKFileLlist;
	}

	public String GetBKFileListStr() {
		List<String> bkFileList = new ArrayList<String>();
		for (HashMap<String, Object> hmObj : m_BKFileLlist) {
			bkFileList.add(hmObj.get("MapFileName") + "");
		}
		return "��" + bkFileList.size() + "��" + Tools.JoinT(",", bkFileList);
	}

	public void SetBKFileList(List<HashMap<String, Object>> bkFileList) {
		this.m_BKFileLlist = bkFileList;
	}

	private boolean m_VectorVisible = true;

	public boolean GetBKVisible() {
		return this.m_VectorVisible;
	}

	/**
	 * ����ʸ��ͼ��ɼ���
	 * 
	 * @param visible
	 */
	public void SetBKVisible(boolean visible) {
		this.m_VectorVisible = visible;
		// ����ɼ�������
		Log.d("LMGK", "���õ�ͼ�ɼ��ԣ�" + this.m_LayerList.size());
		for (int idx = 0; idx < this.m_LayerList.size(); idx++) {
			v1_Layer vLayer = this.m_LayerList.get(idx);
			vLayer.SetVisible(visible);
			PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).GetLayerById(vLayer.GetLayerID())
					.setVisible(visible);
		}
	}

	public void SetVectorSelectable(boolean isSelect) {
		Log.d("LMGK", "����ʸ����ͼ��ѡ���ԣ�" + this.m_LayerList.size());
		for (int idx = 0; idx < this.m_LayerList.size(); idx++) {
			v1_Layer vLayer = this.m_LayerList.get(idx);
			vLayer.SetSelectable(isSelect);
			PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).GetLayerById(vLayer.GetLayerID())
					.setSelectable(isSelect);
		}
	}

	/**
	 * ���ָ����ͼ��ID�Ƿ����
	 * 
	 * @param LayerID
	 * @return
	 */
	public boolean ExistLayerByID(String LayerID) {
		for (v1_Layer vLayer : this.m_LayerList) {
			if (vLayer.GetLayerID().equals(LayerID))
				return true;
		}
		return false;
	}

	/**
	 * ����ͼ��ID����ͼ��
	 * 
	 * @param LayerID
	 * @return
	 */
	public v1_Layer GetLayerByID(String LayerID) {
		for (v1_Layer vLayer : this.m_LayerList) {
			if (vLayer.GetLayerID().toUpperCase().equals(LayerID.toUpperCase()))
				return vLayer;
		}
		return null;
	}

	/**
	 * ��ʸ����ͼ����Դ 1-ʸ������������Dataset
	 */
	public void OpenVectorDataSource() {
		if (this.m_BKFileLlist == null) {
			this.ClearVectorLayer();
			return;
		}
		if (this.m_BKFileLlist.size() == 0) {
			this.ClearVectorLayer();
			return;
		}

		// ���ԭ�еĵ�ͼͼ����Ϣ
		this.ClearVectorLayer();

		// ��ʸ�����ݵ�ͼ
		for (HashMap<String, Object> hmObj : this.m_BKFileLlist) {
			String path = hmObj.get("F1") + "";
			if (path.isEmpty()) {
				path = PubVar.m_SysAbsolutePath + "/Map/";
			}
			String VectorFileFullName = path + "/" + hmObj.get("BKMapFile") + "";
			if (Tools.ExistFile(VectorFileFullName)) {
				Log.d("LMGK", "��ʼ�����µ�ʸ����ͼ��" + VectorFileFullName);
				DataSource pDataSource = new DataSource(VectorFileFullName);
				pDataSource.setEditing(false);
				PubVar.m_Workspace.GetDataSourceList().add(pDataSource);

				// ��ȡ��ͼ����Դ��Ӧ��ͼ����Ϣ���γ���List<v1_Layer>
				List<v1_Layer> vLyrList = this.LoadLayerForVectorBKDataSource(pDataSource);

				// �������ݼ�Dataset
				for (v1_Layer vLayer : vLyrList) {
					Envelope pEnv = new Envelope(vLayer.GetMinX(), vLayer.GetMaxY(), vLayer.GetMaxX(),
							vLayer.GetMinY());
					Dataset pDataset = new Dataset(pDataSource);
					pDataset.setSourceType(lkDatasetSourceType.enBackgroundData);
					pDataset.setId(vLayer.GetLayerID());
					pDataset.setType(vLayer.GetLayerType());
					pDataset.setEnvelope(pEnv);
					pDataset.GetMapCellIndex().setEnvelope(pEnv);
					pDataSource.getDatasets().add(pDataset);

					this.m_LayerList.add(vLayer);
				}

				// ����GeoLayer������Ⱦ
				for (v1_Layer vLayer : vLyrList) {
					PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForAdd(vLayer);
				}

				// ����ʸ������ƫ����
				HashMap<String, Object> OffsetParam = this.ReadVectorOffset(pDataSource);
				this.SetOffset(pDataSource, Double.parseDouble(OffsetParam.get("OffsetX") + ""),
						Double.parseDouble(OffsetParam.get("OffsetY") + ""));
			}
		}
	}

	/**
	 * ��ձ�����ͼͼ��
	 */
	public void ClearVectorLayer() {
		this.m_LayerList.clear();
		PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).Clear();
		List<DataSource> pDataSourceList = this.GetBKDataSourceList();
		for (DataSource pDataSource : pDataSourceList) {
			pDataSource.getDatasets().clear();
			PubVar.m_Workspace.GetDataSourceList().remove(pDataSource);
		}
	}

	/**
	 * �����ͼʸ��ͼ���������Ϣ
	 * 
	 * @param pLayer
	 * @param pLayerConfig
	 */
	public boolean SaveVectorLayerInfo(v1_Layer pLayer) {
		GeoLayer pGeoLayer = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground)
				.GetLayerById(pLayer.GetLayerID());
		DataSource pDataSource = pGeoLayer.getDataset().getDataSource();
		String SQL = "Update T_Layer set Transparent='%2$s',Visible='%3$s' where LayerID='%1$s'";
		SQL = String.format(SQL, pLayer.GetLayerID(), pLayer.GetTransparet(), pLayer.GetVisible());
		if (pDataSource.ExcuteSQL(SQL)) {
			for (v1_Layer layer : this.m_LayerList) {
				if (layer.GetLayerID().equals(pLayer.GetLayerID())) {
					layer = pLayer;
					return true;
				}
			}
		}

		return false;
	}

	public boolean SaveVectorLayerSetting(v1_Layer pLayer) {
		GeoLayer pGeoLayer = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground)
				.GetLayerById(pLayer.GetLayerID());
		DataSource pDataSource = pGeoLayer.getDataset().getDataSource();
		String SQL = "Update T_Layer set F1='%2$s',FieldList='%3$s' where LayerID='%1$s'";
		SQL = String.format(SQL, pLayer.GetLayerID(), pLayer.GetLayerProjecType(), pLayer.GetFieldListJsonStr());
		Log.d("v1_BKVectorLayerExplorer", "����ʸ����ͼͼ�����ã�" + SQL);
		if (pDataSource.ExcuteSQL(SQL)) {
			for (v1_Layer layer : this.m_LayerList) {
				if (layer.GetLayerID().equals(pLayer.GetLayerID())) {
					layer = pLayer;
					return true;
				}
			}
		}
		return false;
	}

	public boolean SaveVectorBKLayer() {
		// �����ͼ�ļ���Ϣ
		String[] FieldList = { "Type", "BKMapFile", "MinX", "MinY", "MaxX", "MaxY", "CoorSystem", "Transparent", "Sort",
				"Visible" };
		String SQL_DEL = "delete from T_BKLayer where Type = '%1$s'";

		// ���沢����
		SQL_DEL = String.format(SQL_DEL, "ʸ��");

		if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_DEL)) {
			for (HashMap<String, Object> ho : this.m_BKFileLlist) {
				String SQL_INS = "insert into T_BKLayer (%1$s) values ('%2$s')";
				List<String> ValueList = new ArrayList<String>();
				for (String field : FieldList)
					ValueList.add(ho.get(field) + "");
				SQL_INS = String.format(SQL_INS, Tools.Joins(",", FieldList), Tools.JoinT("','", ValueList));
				boolean OK = PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_INS);
				if (!OK)
					return false;
			}
		} else
			return false;
		return true;
	}

	/**
	 * ��ȡ��ͼ����Դ�ڵ�ͼ����Ϣ
	 */
	private List<v1_Layer> LoadLayerForVectorBKDataSource(DataSource BKDataSource) {
		List<v1_Layer> vLayerList = new ArrayList<v1_Layer>();

		boolean needAddColumn = false;
		// ��ȡ��ͼ����Դ�ڶ�Ӧ��ͼ����Ϣ
		SQLiteDataReader DR = BKDataSource.Query("select * from T_Layer order by Id");
		if (DR == null)
			return vLayerList;
		while (DR.Read()) {
			v1_Layer lyr = new v1_Layer();
			lyr.SetLayerAliasName(DR.GetString("Name")); // ͼ�����ƣ�����
			lyr.SetLayerID(DR.GetString("LayerId")); // ͼ��Id
			lyr.SetLayerTypeName(DR.GetString("Type")); // ͼ�����ͣ��㣬�ߣ��棩
			lyr.SetVisible(Boolean.parseBoolean(DR.GetString("Visible"))); // �ɼ���
			lyr.SetTransparent(Integer.parseInt(DR.GetString("Transparent"))); // ͸���ȣ���㣩
			lyr.SetVisibleScaleMin(Double.parseDouble(DR.GetString("VisibleScaleMin"))); // ��С�ɼ�����
			lyr.SetVisibleScaleMax(Double.parseDouble(DR.GetString("VisibleScaleMax"))); // ���ɼ�����

			lyr.SetFieldList(DR.GetString("FieldList")); // �ֶ��б�
			lyr.SetIfLabel(Boolean.parseBoolean(DR.GetString("IfLabel"))); // �Ƿ��ע
			lyr.SetLabelDataField(DR.GetString("LabelField")); // ��ע�ֶ�
			lyr.SetLabelFont(DR.GetString("LabelFont")); // ��ע��ʽ

			lyr.SetMinX(Double.parseDouble(DR.GetString("MinX"))); // ��Ӿ���
			lyr.SetMinY(Double.parseDouble(DR.GetString("MinY")));
			lyr.SetMaxX(Double.parseDouble(DR.GetString("MaxX")));
			lyr.SetMaxY(Double.parseDouble(DR.GetString("MaxY")));

			lyr.SetSelectable(Boolean.parseBoolean(DR.GetString("Selectable"))); // �Ƿ��ѡ��
			lyr.SetEditable(Boolean.parseBoolean(DR.GetString("Editable"))); // �Ƿ�ɱ༭
			lyr.SetSnapable(Boolean.parseBoolean(DR.GetString("Snapable"))); // �Ƿ�ɲ�׽

			String renderType = DR.GetString("RenderType"); // ��Ⱦ����1-��ֵ��2-��ֵ
			if (renderType.equals("2")) // ��ֵ����
			{
				lyr.SetRenderType(lkRenderType.enUniqueValue);
				String UVF = DR.GetString("UniqueValueField");
				String UVL = DR.GetString("UniqueValueList");
				String USL = DR.GetString("UniqueSymbolList");
				lyr.GetUniqueSymbolInfoList().put("UniqueValueField", Tools.JSONStrToList(UVF)); // Ψһֵ�ֶ�
				lyr.GetUniqueSymbolInfoList().put("UniqueValueList", Tools.JSONStrToList(UVL)); // Ψһֵ�б�
				lyr.GetUniqueSymbolInfoList().put("UniqueSymbolList", Tools.JSONStrToList(USL)); // Ψһֵ�����б�
				lyr.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", DR.GetString("UniqueDefaultSymbol")); // Ψһֵȱʡ����
			} else
				lyr.SetSimpleSymbol(DR.GetString("SimpleRender"));

			if (DR.getColumnIndex("F1") == -1) {
				lyr.SetLayerProjectType("");
				needAddColumn = true;
			} else {
				lyr.SetLayerProjectType(DR.getUnNullString("F1"));
			}

			vLayerList.add(lyr);
		}
		DR.Close();
		if (needAddColumn) {
			for (int i = 1; i < 11; i++) {
				try {
					String sql = "ALTER TABLE T_Layer ADD F" + i + " TEXT";
					BKDataSource.ExcuteSQL(sql);
				} catch (Exception ex) {

				}
			}

		}
		return vLayerList;
	}

	// ��ͼƫ����
	private double m_OffsetX = 0, m_OffsetY = 0;

	public double GetOffsetX() {
		return this.m_OffsetX;
	}

	public double GetOffsetY() {
		return this.m_OffsetY;
	}

	/**
	 * ����ƫ����
	 * 
	 * @param offsetX
	 *            ʵ��ֵ���ף�
	 * @param offsetY
	 *            ʵ��ֵ���ף�
	 */
	private void SetOffset(DataSource pDataSource, double offsetX, double offsetY) {
		// double H1 = PubVar.m_Map.getViewConvert().getZoom();
		// double H2 =
		// Tools.GetTwoPointDistance(PubVar.m_Map.getExtend().getMinX(),PubVar.m_Map.getExtend().getMaxY(),
		// PubVar.m_Map.getExtend().getMinX(),PubVar.m_Map.getExtend().getMinY());

		double S = 1;
		if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetName().equals("WGS-84����"))
			S = 1.424;

		this.m_OffsetX = offsetX;
		this.m_OffsetY = offsetY;
		for (Dataset pDatset : pDataSource.getDatasets()) {
			if (pDatset != null)
				pDatset.SetOffset(this.m_OffsetX * S, this.m_OffsetY * S);
		}
	}

	/**
	 * ����ʸ�����ݵ�ƫ����
	 * 
	 * @param OffsetX
	 *            ʵ��ֵ���ף�
	 * @param OffsetY
	 *            ʵ��ֵ���ף�
	 * @return
	 */
	public boolean SaveVectorOffset(double OffsetX, double OffsetY) {
		boolean OK = true;
		List<DataSource> pDataSourceList = this.GetBKDataSourceList();
		for (DataSource pDataSource : pDataSourceList) {
			String TableName = "T_MyConfig";
			if (this.CheckAndCreateTable(pDataSource, TableName)) {
				// ����ɾ��
				String SQL = "delete from " + TableName + " where Name='ƫ����'";
				if (pDataSource.ExcuteSQL(SQL)) {
					SQL = "insert into " + TableName + " (Name,F1,F2) values ('ƫ����','%1$s','%2$s')";
					SQL = String.format(SQL, OffsetX + "", OffsetY + "");
					if (pDataSource.ExcuteSQL(SQL)) {
						this.SetOffset(pDataSource, OffsetX, OffsetY);
					} else
						OK = false;
				}
			}
		}
		return OK;
	}

	/**
	 * ��ȡƫ����
	 * 
	 * @return
	 */
	private HashMap<String, Object> ReadVectorOffset(DataSource pDataSource) {
		HashMap<String, Object> offsetParam = new HashMap<String, Object>();
		offsetParam.put("OffsetX", 0);
		offsetParam.put("OffsetY", 0);

		String TableName = "T_MyConfig";

		if (this.CheckAndCreateTable(pDataSource, TableName)) {
			String SQL = "select F1,F2 from " + TableName + " where Name='ƫ����'";
			SQLiteDataReader DR = pDataSource.Query(SQL);
			if (DR != null)
				if (DR.Read()) {
					offsetParam.put("OffsetX", DR.GetString("F1"));
					offsetParam.put("OffsetY", DR.GetString("F2"));
				}
			DR.Close();
		}

		return offsetParam;
	}

	/**
	 * �õ���ǰ��������Դ
	 * 
	 * @return
	 */
	private List<DataSource> GetBKDataSourceList() {
		List<DataSource> BKDataSourceList = new ArrayList<DataSource>();
		for (DataSource pDataSource : PubVar.m_Workspace.GetDataSourceList()) {
			if (!pDataSource.getEditing())
				BKDataSourceList.add(pDataSource);
		}
		return BKDataSourceList;
	}

	/**
	 * �Ƿ��Ѿ���������Դ
	 * 
	 * @return
	 */
	public boolean AlwaysLoadDataSource() {
		if (this.GetBKDataSourceList().size() == 0)
			return false;
		else
			return true;
	}

	/**
	 * ��̬����ָ�����Ƶı�
	 * 
	 * @param TableName
	 * @return
	 */
	private boolean CheckAndCreateTable(DataSource pDataSource, String TableName) {
		boolean CreateTable = false;
		String SQL = "SELECT COUNT(*) as count FROM sqlite_master WHERE type='table' and name= '" + TableName + "'";
		SQLiteDataReader DR = pDataSource.Query(SQL);
		if (DR == null)
			CreateTable = true;
		int Count = 0;
		if (DR.Read())
			Count = Integer.parseInt(DR.GetString("count"));
		DR.Close();
		if (Count <= 0)
			CreateTable = true;
		if (CreateTable) {
			// ������
			List<String> createSQL = new ArrayList<String>();
			createSQL.add("CREATE TABLE " + TableName + " (");
			createSQL.add("ID integer primary key autoincrement not null default (0),");

			// �ֲ�ͬ���ƴ�����ṹ
			if (TableName.equals("T_MyConfig")) {
				createSQL.add("Name text,");
				for (int i = 1; i <= 49; i++)
					createSQL.add("F" + i + " text,");
				createSQL.add("F50 text");
			}
			createSQL.add(")");
			SQL = Tools.JoinT("\r\n", createSQL);
			return pDataSource.ExcuteSQL(SQL);
		} else
			return true;
	}

}
