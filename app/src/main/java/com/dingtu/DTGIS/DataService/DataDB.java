package com.dingtu.DTGIS.DataService;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.database.sqlite.SQLiteDatabase;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.BitConverter;

public class DataDB {

	String dbPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/TAData.dbx";
	SQLiteDatabase m_SQLiteDatabase = null;

	public DataDB() {
		m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
	}

	public List<JSONObject> getAllDataJson(String layerId, String type) {

		String sql = "select * from " + layerId + "_D where SYS_STATUS = 0";
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
		List<JSONObject> datas = new ArrayList<JSONObject>();
		while (reader.Read()) {
			JSONObject fieldJSObject = new JSONObject();
			try {

				fieldJSObject.put("SYS_ID", reader.GetInt32("SYS_ID"));
				byte[] bytes = reader.GetBlob("SYS_GEO");

				// fieldJSObject.put("SYS_GEO", bytes);

				fieldJSObject.put("SYS_GEO", getGeoByte(bytes, type));
				fieldJSObject.put("SYS_STATUS", reader.GetInt32("SYS_STATUS"));
				fieldJSObject.put("SYS_TYPE", reader.getUnNullString("SYS_TYPE"));
				fieldJSObject.put("SYS_OID", reader.GetInt32("SYS_ID") + "_" + reader.getUnNullString("SYS_OID"));
				fieldJSObject.put("SYS_LABEL", reader.getUnNullString("SYS_LABEL"));
				fieldJSObject.put("SYS_DATE", reader.getUnNullString("SYS_DATE"));
				fieldJSObject.put("SYS_PHOTO", reader.getUnNullString("SYS_PHOTO"));
				fieldJSObject.put("SYS_Length", reader.GetDouble("SYS_Length"));
				fieldJSObject.put("SYS_Area", reader.GetDouble("SYS_Area"));
				try {
					for (int i = 1; i < 256; i++) {
						String value = reader.getUnNullString("F" + i);
						if (value != null && !value.isEmpty()) {
							fieldJSObject.put("F" + i, value);
						}
					}
				} catch (Exception ex) {

				}

				datas.add(fieldJSObject);
			} catch (Exception ex) {

			}

		}
		return datas;
	}

	private List<List<String>> getGeoByte(byte[] bytes, String type) {
		int Offset = 0;
		int PartCount = BitConverter.ToInt(bytes, 0); // 多部分数量
		Offset += 4;

		// 读取多部分信息
		List<List<String>> partList = new ArrayList<List<String>>();
		List<Integer> partIndexList = new ArrayList<Integer>();
		for (int i = 1; i <= PartCount; i++) {
			int partIndex = BitConverter.ToInt(bytes, Offset);
			Offset += 4; // 部分的起始索引
			partIndexList.add(partIndex);
		}

		// 读取坐标信息
		int Step = 24;
		List<Coordinate> CoorList = new ArrayList<Coordinate>();
		for (int idx = 0; idx < (bytes.length - Offset) / Step; idx++) {
			double X = BitConverter.ToDouble(bytes, Step * idx + Offset);
			double Y = BitConverter.ToDouble(bytes, Step * idx + Offset + 8);
			double Z = BitConverter.ToDouble(bytes, Step * idx + Offset + 16);
			Coordinate Coor = new Coordinate(X, Y, Z);
			CoorList.add(Coor);
		}

		// 构建图形实体
		if (type.equals("点")) {
			List<String> CoorListP = new ArrayList<String>();
			CoorListP.add(CoorList.get(0).toJson());
			partList.add(CoorListP);
			return partList;
		} else {

			if (type.equals("线")) {
				// 分部分增加
				partIndexList.add(CoorList.size());
				for (int idx = 0; idx < partIndexList.size() - 1; idx++) {
					int startIdx = partIndexList.get(idx);
					int endIdx = partIndexList.get(idx + 1);
					List<String> pPart = new ArrayList<String>();
					for (int pi = startIdx; pi < endIdx; pi++) {
						pPart.add(CoorList.get(pi).toJson());
					}
					partList.add(pPart);
				}
			} else {
				partIndexList.add(CoorList.size());
				for (int idx = 0; idx < partIndexList.size() - 1; idx++) {
					int startIdx = partIndexList.get(idx);
					int endIdx = partIndexList.get(idx + 1);
					List<String> pPart = new ArrayList<String>();
					for (int pi = startIdx; pi < endIdx; pi++) {
						pPart.add(CoorList.get(pi).toJson());
					}

					// for(int pi = startIdx;pi<endIdx;pi++)
					// {
					// pPart.getVertexList().add(CoorList.get(pi));
					// }
					// pPart.AutoSetPartType();
					partList.add(pPart);
				}
			}

		}

		return partList;
	}
}
