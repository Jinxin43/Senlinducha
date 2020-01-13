package lkmap.Dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lkmap.Cargeometry.Envelope;
import lkmap.Tools.Tools;

public class DataSource {
	// ��ʼ������Դ
	public DataSource(String DatabaseName) {
		this.setName(DatabaseName);
		this.Open(); // ������Դ
	}

	// ������Դ
	public boolean Open() {
		_EDatabase = new ASQLiteDatabase();
		_EDatabase.setDatabaseName(this.getName());
		return true;
	}

	// �ر�����Դ
	public boolean Close() {
		_EDatabase.Close();
		return true;
	}

	/**
	 * ��TAData.dbx�д����µ����ݱ�
	 * 
	 * @param DatasetID
	 *            ���ݼ�ID����v1_Layer.GetLayerID��ͬ
	 * @return
	 */
	public boolean CreateDataset(String DatasetID) {
		// ����(_D)���ݱ��Լ�(_I)������
		List<String> createSQL = new ArrayList<String>();
		createSQL.add("CREATE TABLE " + DatasetID + "_D (");
		createSQL.add("SYS_ID integer primary key autoincrement  not null default (0),");
		createSQL.add("SYS_GEO Blob,"); // ͼ��ʵ��
		createSQL.add("SYS_STATUS int,"); // ״̬
		createSQL.add("SYS_TYPE varchar(50),"); // ʵ������
		createSQL.add("SYS_OID varchar(50),"); // ΨһֵGUID
		createSQL.add("SYS_LABEL varchar(50),"); // ��עֵ
		createSQL.add("SYS_DATE varchar(50),"); // �ɼ�ʱ��
		createSQL.add("SYS_PHOTO Text,"); // ��Ƭ�ֶ�
		createSQL.add("SYS_Length double,"); // ����
		createSQL.add("SYS_Area double,"); // ���
		createSQL.add("SYS_BZ1 Text,"); // ��ע�ֶ�1
		createSQL.add("SYS_BZ2 Text,"); // ��ע�ֶ�2
		createSQL.add("SYS_BZ3 Text,"); // ��ע�ֶ�3
		createSQL.add("SYS_BZ4 Text,"); // ��ע�ֶ�4
		createSQL.add("SYS_BZ5 Text,"); // ��ע�ֶ�5

		for (int i = 1; i <= 225; i++) {
			String FName = "F" + i;
			String FType = "varchar(255) default ''";
			createSQL.add(FName + " " + FType + ",");
		}
		String EndStr = createSQL.get(createSQL.size() - 1);
		createSQL.remove(createSQL.size() - 1);
		createSQL.add(EndStr.substring(0, EndStr.length() - 1));
		createSQL.add(")");

		String SQL_D = Tools.JoinT("\r\n", createSQL);
		String SQL_I = "CREATE TABLE " + DatasetID + "_I (" + "SYS_ID INTEGER PRIMARY KEY NOT NULL,"
				+ "RIndex int,CIndex int,MinX double,MinY double,MaxX double,MaxY double)";
		String SQL_Index_D = "CREATE UNIQUE INDEX 'Sys_ID_Index" + UUID.randomUUID().toString() + "' on " + DatasetID
				+ "_D (SYS_ID ASC)";
		String SQL_Index_I = "CREATE INDEX 'Sys_ID_Index" + UUID.randomUUID().toString() + "' on " + DatasetID
				+ "_I (RIndex ASC,CIndex ASC)";
		return this.ExcuteSQL(SQL_D) && this.ExcuteSQL(SQL_I) && this.ExcuteSQL(SQL_Index_D)
				&& this.ExcuteSQL(SQL_Index_I);
	}

	/**
	 * ɾ��ָ�����Ƶ����ݱ�
	 * 
	 * @param datasetID
	 * @return
	 */
	public boolean RemoveDataset(String datasetID) {
//		boolean OK = this.ExcuteSQL("drop table " + datasetID + "_D")
//				&& this.ExcuteSQL("drop table " + datasetID + "_I");
		boolean OK = false;
//		if (OK)
		OK = this._Datasets.remove(GetDatasetById(datasetID));
		return OK;
	}

	// ����Դ���ļ�����
	private String _Name = "";

	public String getName() {
		return _Name;
	}

	public void setName(String value) {
		_Name = value;
	}

	// ����Դ�ɱ༭��
	private boolean _Editing = false;

	public boolean getEditing() {
		return this._Editing;
	}

	public void setEditing(boolean value) {
		this._Editing = value;
	}

	// ָʾ����Դ�Ƿ񱻱༭��
	public boolean getEdited() {
		for (Dataset pDataset : this.getDatasets()) {
			if (pDataset.getEdited())
				return true;
		}
		return false;
	}

	// ���ݿ������
	public ASQLiteDatabase _EDatabase = null;

	public ASQLiteDatabase GetSQLiteDatabase() {
		return this._EDatabase;
	};

	// ���ݼ�����
	private List<Dataset> _Datasets = new ArrayList<Dataset>();

	public List<Dataset> getDatasets() {
		return _Datasets;
	}

	/**
	 * �����ݿ����¼�������Դ�������Ӿ���
	 * 
	 * @return
	 */
	public Envelope GetEnvelope() {
		double MinX = 0, MinY = 0, MaxX = 0, MaxY = 0;
		List<String> SQLList = new ArrayList<String>();
		for (Dataset pDataset : this.getDatasets()) {
			String SQL = "select min(MinX) as MinX,min(MinY) as MinY,max(MaxX) as MaxX,max(MaxY) as MaxY from "
					+ pDataset.getIndexTableName();
			SQLList.add(SQL);
		}
		if (SQLList.size() == 0)
			return new Envelope(MinX, MinY, MaxX, MaxY);
		String SQL = "select min(MinX) as MinX,min(MinY) as MinY,max(MaxX) as MaxX,max(MaxY) as MaxY from("
				+ Tools.JoinT("\r\nunion\r\n", SQLList) + ")";
		SQLiteDataReader DR = this.Query(SQL);
		if (DR != null)
			if (DR.Read()) {
				MinX = DR.GetDouble("MinX");
				MinY = DR.GetDouble("MinY");
				MaxX = DR.GetDouble("MaxX");
				MaxY = DR.GetDouble("MaxY");
			}
		DR.Close();
		return new Envelope(MinX, MaxY, MaxX, MinY);
	}

	// �������Ʒ������ݼ�
	public Dataset GetDatasetById(String DatasetId) {
		for (Dataset pDataset : this.getDatasets()) {
			if (pDataset.getId().toUpperCase().equals(DatasetId.toUpperCase()))
				return pDataset;
		}
		return null;
	}

	// ȡ��ָ�����ݱ�Ľṹ��Ϣ
	public List<FieldInfo> GetTableStruct(String TableName) {
		List<FieldInfo> FiList = new ArrayList<FieldInfo>();
		String SQL = "select * from T_TableStruct where LayerId='" + TableName + "'";
		SQLiteDataReader pDR = this.Query(SQL);
		{
			if (pDR != null) {
				while (pDR.Read()) {
					FieldInfo FI = new FieldInfo();
					FI.setName(pDR.GetString("FieldName"));
					FI.setCaption(pDR.GetString("FieldCaption"));
					FiList.add(FI);
				}
			}
		}
		return FiList;
	}

	public String QueryDataFieldValue(String dataFieldName, int sysID, String tableName) {
		String SQL = "select " + dataFieldName + " from " + tableName + " where SYS_ID=" + sysID;
		SQLiteDataReader pDR = this.Query(SQL);
		if (pDR.Read()) {
			return pDR.GetString(dataFieldName);
		}
		return "";
	}

	// ��ѯ��¼
	public SQLiteDataReader Query(String SQL) {
		return _EDatabase.Query(SQL);
	}

	// ִ��SQL���
	public boolean ExcuteSQL(String SQL) {
		return _EDatabase.ExcuteSQL(SQL);
	}

	// ִ��SQL���
	public boolean ExcuteSQL(String SQL, Object[] value) {
		return _EDatabase.ExcuteSQL(SQL, value);
	}

	// �ͷſռ�
	public void Dispose() {
		for (Dataset pDataset : _Datasets)
			pDataset.Dispose();
		_EDatabase.Close();
		_EDatabase = null;
	}
}
