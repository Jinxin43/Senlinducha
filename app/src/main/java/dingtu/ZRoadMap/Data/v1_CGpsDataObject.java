package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Geometry;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkRenderType;
import lkmap.Index.T4Index;
import lkmap.Render.UniqueValueRender;
import lkmap.Tools.Tools;

//GPS���ݲɼ��Ļ�����ʵ��
public class v1_CGpsDataObject {
	public v1_CGpsDataObject() {
		this.SYS_OID = UUID.randomUUID().toString(); // �Զ�����UUID��
		this.SYS_DATE = PubVar.SaveDataDate; // �Զ����ɲɼ�����
	}

	// �����������ݼ�
	protected Dataset m_Dataset = null;

	/**
	 * ����BaseObject��Ӧ�����ݱ�
	 * 
	 * @param tableName
	 */
	public void SetDataset(Dataset pDataset) {
		this.m_Dataset = pDataset;
	}

	protected String SYS_STATUS = "0"; // 0-����״̬��1-ɾ��״̬��2-����״̬

	/**
	 * ����BaseObject��SYS_STATUS
	 * 
	 * @param type
	 */
	public void SetSYS_STATUS(String status) {
		this.SYS_STATUS = status;
	}

	protected String SYS_TYPE = "";

	/**
	 * ����BaseObject��SYS_TYPE
	 * 
	 * @param type
	 */
	public void SetSYS_TYPE(String type) {
		this.SYS_TYPE = type;
	}

	// ʵ��ID
	protected int SYS_ID = -1;

	/**
	 * ����BaseObject��SYS_ID
	 * 
	 * @param sysid
	 */
	public void SetSYS_ID(int sysid) {
		this.SYS_ID = sysid;
	}

	public int GetSYS_ID() {
		return this.SYS_ID;
	}

	protected String SYS_OID = "";

	/**
	 * UUID��
	 * 
	 * @return
	 */
	public String GetSYS_OID() {
		return this.SYS_OID;
	}

	public String GetSYS_LABEL() {
		lkmap.Cargeometry.Geometry pGeometry = this.m_Dataset.GetGeometry(this.SYS_ID);
		if (pGeometry != null)
			return pGeometry.getTag() + "";
		return "";
	}

	protected String SYS_PHOTO = "";

	/**
	 * ����ͼƬ�б�
	 * 
	 * @param photoList
	 */
	public void SetSYS_PHOTO(String photoList) {
		this.SYS_PHOTO = photoList;
	}

	protected String[] waterMarkFields;

	public void setWaterMarkKey(String waterMarkFields) {
		if (waterMarkFields != null) {
			this.waterMarkFields = waterMarkFields.split(",");
		} else {
			this.waterMarkFields = null;
		}

	}

	protected String waterMarkValue;

	public String getWaterMarkValue() {
		return waterMarkValue;
	}

	/**
	 * ��ȡ��Ƭ�б�
	 * 
	 * @return
	 */
	public String GetSYS_PHOTO() {
		return this.SYS_PHOTO;
	}

	// ������Ƭ����
	public int GetPhotoCount() {
		if (this.SYS_PHOTO == null)
			this.SYS_PHOTO = "";
		if (this.SYS_PHOTO.equals(""))
			return 0;
		else
			return this.SYS_PHOTO.split(",").length;
	}

	protected String SYS_DATE = "";

	/**
	 * ���òɼ�����
	 * 
	 * @param date
	 */
	public void SetSYS_DATE(String date) {
		this.SYS_DATE = date;
	}

	/**
	 * ������������
	 * 
	 * @return
	 */
	public boolean SaveFeatureToDb() {
		if (this.SYS_ID == -1) {
			return this.SaveNewAdd(this.GetFeatureList());
		} else {
			return this.UpdateFeatrue(this.GetFeatureList());
		}
	}

	public boolean SaveVectorFeature() {
		return this.UpdateVectorBackFeatrue(this.GetFeatureList());
	}

	public boolean saveBZ(String layerId, String objId) {
		String SQL_D = "update %1$s set SYS_BZ1=%2$s,SYS_BZ2=%3$s " + " where SYS_ID = " + this.SYS_ID;
		SQL_D = String.format(SQL_D, this.m_Dataset.getDataTableName(), layerId, objId);

		return this.m_Dataset.getDataSource().ExcuteSQL(SQL_D);
	}

	/**
	 * ����ͼ�ε����ݿ⣬ע��˴���Ҫ����(_I)������
	 * 
	 * @param pGeometry,SYS_Length=-1,SYS_Area=-1��ʾ������
	 * @return
	 */
	public int SaveGeoToDb(Geometry pGeometry, double SYS_Length, double SYS_Area) {
		try {
			// ת��ͼ��ʵ��ΪByte[]
			Object[] value = new Object[] { "" };
			if (pGeometry != null) {
				byte[] GeoByte = Tools.GeometryToByte(pGeometry);
				value = new Object[] { GeoByte };
			}

			// ʵ�����������
			T4Index TIndex = pGeometry.CalCellIndex(this.m_Dataset.GetMapCellIndex());

			// ����״̬
			if (this.SYS_ID != -1) {
				// ����ͼ��ʵ��
				String UpdateLenArea = ",SYS_Length=%1$s,SYS_Area=%2$s";
				if (SYS_Length == -1 && SYS_Area == -1)
					UpdateLenArea = "";
				else
					UpdateLenArea = String.format(UpdateLenArea, SYS_Length, SYS_Area);

				String SQL_D = "update %1$s set SYS_GEO=?,SYS_STATUS=%2$s" + UpdateLenArea + " where SYS_ID = "
						+ this.SYS_ID;
				SQL_D = String.format(SQL_D, this.m_Dataset.getDataTableName(), this.SYS_STATUS);

				// ����������Ϣ������RIndex,CIndex,�����Ӿ���
				String SQL_I = "Update %1$s set RIndex=%2$s,CIndex=%3$s,MinX=%4$s,MinY=%5$s,MaxX=%6$s,MaxY=%7$s where SYS_ID="
						+ this.SYS_ID;
				SQL_I = String.format(SQL_I, this.m_Dataset.getIndexTableName(), TIndex.GetRow(), TIndex.GetCol(),
						pGeometry.getEnvelope().getMinX(), pGeometry.getEnvelope().getMinY(),
						pGeometry.getEnvelope().getMaxX(), pGeometry.getEnvelope().getMaxY());

				Log.d("", "���ڸ���ͼ������[" + SQL_D + "]");
				if (this.m_Dataset.getDataSource().ExcuteSQL(SQL_D, value)
						&& this.m_Dataset.getDataSource().ExcuteSQL(SQL_I)) {
					Log.d("", "����ͼ�����ݳɹ���");
					return this.SYS_ID;
				}
			} else // ����״̬
			{
				// ͼ��ʵ��
				String SQL_D = "insert into " + this.m_Dataset.getDataTableName() + " "
				// +
				// "(SYS_GEO,SYS_STATUS,SYS_TYPE,SYS_OID,SYS_LABEL,SYS_DATE,SYS_PHOTO,SYS_Length,SYS_Area)
				// values "
						+ "(SYS_GEO,SYS_STATUS,SYS_OID,SYS_LABEL,SYS_DATE,SYS_PHOTO,SYS_Length,SYS_Area) values "
						+ "(?,0,'%1$s','%2$s','%3$s','%4$s','%5$s','%6$s')";
				SQL_D = String.format(SQL_D, this.SYS_OID, "", PubVar.SaveDataDate, this.SYS_PHOTO, SYS_Length,
						SYS_Area);

				Log.d("", "��������ͼ������[" + SQL_D + "]");

				if (this.m_Dataset.getDataSource().ExcuteSQL(SQL_D, value)) {
					// ��ȡ�²���ʵ���SYS_ID��
					String SQL = "select max(SYS_ID) as objectid from " + this.m_Dataset.getDataTableName();
					SQLiteDataReader DR = this.m_Dataset.getDataSource().Query(SQL);
					if (DR.Read()) {
						this.SYS_ID = Integer.valueOf(DR.GetString(0));
					}
					DR.Close();

					// ����������Ϣ
					// ����ʵ��
					String SQL_I = "insert into " + this.m_Dataset.getIndexTableName() + " "
							+ "(SYS_ID,RIndex,CIndex,MinX,MinY,MaxX,MaxY) values "
							+ "('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s')";
					SQL_I = String.format(SQL_I, this.SYS_ID, TIndex.GetRow(), TIndex.GetCol(),
							pGeometry.getEnvelope().getMinX(), pGeometry.getEnvelope().getMinY(),
							pGeometry.getEnvelope().getMaxX(), pGeometry.getEnvelope().getMaxY());
					Log.d("", "����������������[" + SQL_I + "]");
					if (this.m_Dataset.getDataSource().ExcuteSQL(SQL_I)) {
						// ����������Ϣ
						if (this.SYS_ID != -1) {
							pGeometry.setSysId(this.SYS_ID);
							// Dataset pDataset =
							// pDataSource.GetDatasetByName(this.TableName);
							// List<Integer> newObjIdx = new
							// ArrayList<Integer>();
							// newObjIdx.add(this.SYS_ID);
							// if (pDataset.QueryGeometryFromDB(newObjIdx,
							// true))
							// pDataset.AddGeometry(pGeometry, true);
							// {
							// pDataset.CalEnvelope();
							// //����Dataset��Envelopeʹ֮��������ʵ��
							// //Geometry pGeometryNew =
							// pDataset.GetGeometryByDIndex(this.SYS_ID);
							// PubVar.m_Map.ClearSelection();
							// PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).GetLayerByName(pDataset.getName()).getSelSelection().Add(pGeometry);
							// PubVar.m_Map.Refresh();

							return this.SYS_ID;
							// }
						}
					}

				}
			}

			return -1;
		} catch (Error e) {
			// Tools.ShowMessageBox(PubVar.m_DoEvent.m_GPSPoint.SubWindow,"["+this.SYS_TYPE
			// + "] ����ʧ�ܣ�\r\nԭ��"+e.getMessage());
			return -1;
		}
	}

	/**
	 * �����ݿ�ɾ��ʵ��
	 * 
	 * @return
	 */
	public boolean DeleteFormDb() {
		String SQL = "delete from " + this.m_Dataset.getDataTableName() + " where SYS_ID = " + this.SYS_ID;
		boolean deleteOK = this.m_Dataset.getDataSource().ExcuteSQL(SQL);
		return deleteOK;
	}

	/**
	 * ��ȡ���ݲ����½�����ʾ
	 * 
	 * @param SQL
	 */
	public void ReadDataAndBindToView(String Where) {
		ReadDataAndBindToView(Where, "", "");
	}

	public void ReadDataAndBindToView(String Where, String layerID, String layerType) {
		List<String> FeatureList = new ArrayList<String>();
		SQLiteDataReader DR = this.m_Dataset.getDataSource()
				.Query("select * from " + this.m_Dataset.getDataTableName() + " where " + Where);
		if (DR == null)
			return;
		if (DR.Read()) {
			String[] FieldNameList = DR.GetFieldNameList();
			for (String FieldName : FieldNameList) {
				if (FieldName.equals("SYS_ID"))
					this.SYS_ID = Integer.parseInt(DR.GetString(FieldName));
				if (FieldName.equals("SYS_OID"))
					this.SYS_OID = DR.GetString(FieldName);
				// if (FieldName.equals("SYS_LABEL")) this.SYS_LABEL =
				// DR.GetString(FieldName);
				if (FieldName.equals("SYS_DATE"))
					this.SYS_DATE = DR.GetString(FieldName);
				if (FieldName.equals("SYS_PHOTO")) {
					this.SYS_PHOTO = DR.GetString(FieldName);
				}

				if (FieldName.equals("SYS_GEO"))
					continue; // ��ȡͼ��

				String FValue = DR.GetString(FieldName);

				if (FValue == null)
					FValue = "";
				FeatureList.add(FieldName + "," + FValue);
			}
			this.SetFeatureList(FeatureList, layerID, layerType);
		}
		DR.Close();
		this.RefreshDataToView(); // ������ˢ�µ��ؼ���
	}

	public HashMap<String, Object> ReadBZ(int sysID) {
		HashMap<String, Object> fieldValue = new HashMap<String, Object>();
		try {
			SQLiteDataReader DR = this.m_Dataset.getDataSource().Query(
					"select SYS_BZ1,SYS_BZ2 from " + this.m_Dataset.getDataTableName() + " where SYS_ID =" + sysID);
			if (DR.Read()) {
				String layerId = DR.GetString("SYS_BZ1");
				fieldValue.put("layerID", layerId);
				String objID = DR.GetString("SYS_BZ2");
				fieldValue.put("objID", objID);
			}
		} catch (Exception ex) {

		}

		return fieldValue;
	}

	public HashMap<String, Object> ReadDataAllFieldsValue(int sysID) {
		List<HashMap<String, Object>> FeatureList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> fieldValue = new HashMap<String, Object>();
		try {
			SQLiteDataReader DR = this.m_Dataset.getDataSource()
					.Query("select * from " + this.m_Dataset.getDataTableName() + " where SYS_ID =" + sysID);
			if (DR == null)
				return fieldValue;
			if (DR.Read()) {
				String[] FieldNameList = DR.GetFieldNameList();
				for (String FieldName : FieldNameList) {
					// if (FieldName.equals("SYS_ID"))
					// {
					// //this.SYS_ID =
					// Integer.parseInt(DR.GetString(FieldName));
					// continue;
					// }
					if (FieldName.equals("SYS_GEO")) {
						continue;
					}

					if (FieldName.equals("SYS_STATUS")) {
						continue;
					}

					String FValue = DR.GetString(FieldName);
					if (FValue == null) {
						FValue = "";
					}
					fieldValue.put(FieldName, FValue);
				}

			}
			DR.Close();
		} catch (Exception ex) {

		}

		return fieldValue;
	}

	/**
	 * ����ɼ�ʵ��,FeatureList��ʽ��ʽ���ֶ�����='XXX'
	 * 
	 * @param FeatureList
	 * @return
	 */
	private boolean SaveNewAdd(List<String> FeatureList) {
		try {
			List<String> FieldNameList = new ArrayList<String>();
			List<String> FieldValueList = new ArrayList<String>();
			for (String FV : FeatureList) {
				String fv[] = FV.split("=");
				String FieldName = fv[0];
				String FieldValue = (fv.length != 2 ? "" : fv[1]);
				FieldNameList.add(FieldName);
				FieldValueList.add(FieldValue);
			}

			String SQL = "insert into %1$s (%2$s) values (%3$s)";
			SQL = String.format(SQL, this.m_Dataset.getDataTableName(), Tools.JoinT(",", FieldNameList),
					Tools.JoinT(",", FieldValueList));
			Log.d("", "���ڱ�������[" + SQL + "]");
			if (this.m_Dataset.getDataSource().ExcuteSQL(SQL))
				return true;

		} catch (Error e) {
			// Tools.ShowMessageBox(PubVar.m_DoEvent.m_GPSPoint.SubWindow,"["+this.SYS_TYPE
			// + "] ����ʧ�ܣ�\r\nԭ��"+e.getMessage());
			return false;
		}
		return false;
	}

	// ���²ɼ�ʵ��,FeatureList��ʽ��ʽ��F1='XXX'
	private boolean UpdateFeatrue(List<String> FeatureList) {
		try {
			// ����ʵ���������Ϣ
			// String SQL = "update %1$s set
			// SYS_LABEL='%2$s',SYS_PHOTO='%3$s',SYS_TYPE='%6$s',%4$s where
			// SYS_ID=%5$s";
			String SQL = "update %1$s set SYS_LABEL='%2$s',SYS_PHOTO='%3$s',%4$s where SYS_ID=%5$s";
			SQL = String.format(SQL, this.m_Dataset.getDataTableName(), "", this.SYS_PHOTO,
					Tools.JoinT(",", FeatureList), this.SYS_ID);

			if (this.m_Dataset.getDataSource().ExcuteSQL(SQL)) {
				lkmap.Cargeometry.Geometry pGeometry = this.m_Dataset.GetGeometry(this.SYS_ID);
				if (pGeometry != null) {
					// ���±�ע��Ϣ
					if (this.m_Dataset.getBindGeoLayer().getRender().getIfLabel()) {
						String[] labelFieldList = this.m_Dataset.getBindGeoLayer().getRender().getLabelField()
								.split(",");
						if (labelFieldList.length != 0) {
							String ValueStr = "";
							for (String feature : FeatureList) {
								String[] fInfo = feature.split("=");
								for (String UVF : labelFieldList) {
									if (fInfo[0].equals(UVF))
										ValueStr += fInfo[1].replace("'", "") + ",";
								}
							}
							if (ValueStr.length() > 0)
								pGeometry.setTag(ValueStr.substring(0, ValueStr.length() - 1));
						}
					}

					// ����Ψһ��Ⱦ��UniqueValue
					if (this.m_Dataset.getBindGeoLayer().getRender().getType() == lkRenderType.enUniqueValue) {
						List<String> UVFList = ((UniqueValueRender) this.m_Dataset.getBindGeoLayer().getRender())
								.GetUniqueValueFieldList();
						String ValueStr = "";
						for (String feature : FeatureList) {
							String[] fInfo = feature.split("=");
							for (String UVF : UVFList) {
								if (fInfo[0].equals(UVF))
									ValueStr += fInfo[1].replace("'", "") + ",";
							}
						}
						if (ValueStr.length() > 0)
							pGeometry.setTagForUniqueSymbol(ValueStr.substring(0, ValueStr.length() - 1));
					}
					this.m_Dataset.getBindGeoLayer().getRender().UpdateSymbol(pGeometry);
					PubVar.m_Map.Refresh();
				}

				return true;
			}
			return false;
		} catch (Error e) {
			Tools.ShowMessageBox("[" + this.SYS_TYPE + "] ����ʧ�ܣ�\r\nԭ��" + e.getMessage());
			return false;
		}
	}

	// ���²ɼ�ʵ��,FeatureList��ʽ��ʽ��F1='XXX'
	private boolean UpdateVectorBackFeatrue(List<String> FeatureList) {
		try {
			// ����ʵ���������Ϣ
			String SQL = "update %1$s set SYS_LABEL='%2$s',SYS_PHOTO='%3$s',%4$s where SYS_ID=%5$s";
			SQL = String.format(SQL, this.m_Dataset.getDataTableName(), "", this.SYS_PHOTO,
					Tools.JoinT(",", FeatureList), this.SYS_ID);

			if (this.m_Dataset.getDataSource().ExcuteSQL(SQL)) {
				lkmap.Cargeometry.Geometry pGeometry = this.m_Dataset.GetGeometry(this.SYS_ID);
				if (pGeometry != null) {
					// ���±�ע��Ϣ
					if (this.m_Dataset.getBindGeoLayer().getRender().getIfLabel()) {
						String[] labelFieldList = this.m_Dataset.getBindGeoLayer().getRender().getLabelField()
								.split(",");
						if (labelFieldList.length != 0) {
							String ValueStr = "";
							for (String feature : FeatureList) {
								String[] fInfo = feature.split("=");
								for (String UVF : labelFieldList) {
									if (fInfo[0].equals(UVF))
										ValueStr += fInfo[1].replace("'", "") + ",";
								}
							}
							if (ValueStr.length() > 0)
								pGeometry.setTag(ValueStr.substring(0, ValueStr.length() - 1));
						}
					}

					// ����Ψһ��Ⱦ��UniqueValue
					if (this.m_Dataset.getBindGeoLayer().getRender().getType() == lkRenderType.enUniqueValue) {
						List<String> UVFList = ((UniqueValueRender) this.m_Dataset.getBindGeoLayer().getRender())
								.GetUniqueValueFieldList();
						String ValueStr = "";
						for (String feature : FeatureList) {
							String[] fInfo = feature.split("=");
							for (String UVF : UVFList) {
								if (fInfo[0].equals(UVF))
									ValueStr += fInfo[1].replace("'", "") + ",";
							}
						}
						if (ValueStr.length() > 0)
							pGeometry.setTagForUniqueSymbol(ValueStr.substring(0, ValueStr.length() - 1));
					}
					this.m_Dataset.getBindGeoLayer().getRender().UpdateSymbol(pGeometry);
					PubVar.m_Map.Refresh();
				}

				return true;
			}
			return false;
		} catch (Error e) {
			Tools.ShowMessageBox("[" + this.SYS_TYPE + "] ����ʧ�ܣ�\r\nԭ��" + e.getMessage());
			return false;
		}
	}

	// ������ؼ�������
	protected List<DataBindOfKeyValue> DataBindList = new ArrayList<DataBindOfKeyValue>();

	/**
	 * ���б����ݰ󶨵�List<DataBindOfKeyValue>��
	 * 
	 * @param FeatureList
	 */
	public void SetFeatureList(List<String> FeatureList, String layerID, String layerType) {
		for (int i = 0; i < FeatureList.size(); i++) {
			String[] fv = FeatureList.get(i).split(",");
			String FieldName = fv[0];
			String FieldValue = (fv.length != 2 ? "" : fv[1]);

			this.SetDataBindItemValue(FieldName, FieldValue);

			// this.DataBindList.get(i).Value=FeatureList.get(i);
		}
	}

	/**
	 * ��List<DataBindOfKeyValue>ת����FeatureList�б���ʽ��F1='XXX'
	 * 
	 * @return
	 */
	public List<String> GetFeatureList() {
		List<String> FeatureList = new ArrayList<String>();
		for (int i = 0; i < this.DataBindList.size(); i++) {
			DataBindOfKeyValue dv = this.DataBindList.get(i);
			FeatureList.add(dv.DataKey + "='" + dv.Value + "'");
		}
		return FeatureList;
	}

	public String getFeatureValue(String key) {
		String value = "";
		for (DataBindOfKeyValue dv : this.DataBindList) {
			if (dv.Key.equals(key)) {
				value = dv.Value;
				break;
			}
		}
		return value;
	}

	/**
	 * ���Ӱ���Ŀ
	 * 
	 * @param dbov
	 */
	public void AddDataBindItem(DataBindOfKeyValue dbov) {
		this.DataBindList.add(dbov);
	}

	/**
	 * ������ˢ�µ��ؼ���
	 */
	public void RefreshDataToView() {
		for (DataBindOfKeyValue KV : DataBindList) {
			if (KV.ViewControl != null) {
				Tools.SetValueToView(KV.Value, KV.ViewControl);
			}
		}
	}

	/**
	 * ���ؼ��е�ֵˢ�µ�������
	 */
	public void RefreshViewValueToData() {
		waterMarkValue = null;

		for (DataBindOfKeyValue KV : DataBindList) {
			if (KV.ViewControl != null) {

				KV.Value = Tools.GetViewValue(KV.ViewControl);
				if (waterMarkFields != null) {
					for (String fieldKey : waterMarkFields) {
						if (KV.DataKey.equals(fieldKey)) {
							if (waterMarkValue == null) {
								waterMarkValue = KV.Value;
							} else {
								waterMarkValue += "_" + KV.Value;
							}
						}
					}
				}

			}
		}
	}

	/**
	 * ����ָ����KEY�İ���ֵ
	 * 
	 * @param key
	 * @param value
	 */

	public void SetDataBindItemValue(String key, String value) {
		for (DataBindOfKeyValue DKV : this.DataBindList) {
			if (DKV.DataKey.equals(key)) {
				DKV.Value = value;
			}
		}
	}

}
