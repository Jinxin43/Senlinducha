package com.dingtu.DTGIS.DataService;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import dingtu.ZRoadMap.PubVar;

public class DuChaDB {

	public static String dbPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()
			+ "/project.dbx";
	SQLiteDatabase m_SQLiteDatabase = null;

	public static boolean CreateTable() {
		try {

			SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE if not exists T_DuChaJCKP (a1 INTEGER PRIMARY KEY AUTOINCREMENT");
			for (int i = 2; i < 178; i++) {
				String field = ",a" + i + " TEXT";
				sql.append(field);
			}
			sql.append(")");
			m_SQLiteDatabase.execSQL(sql.toString());
			m_SQLiteDatabase.close();
		} catch (Exception ex) {
			Log.e("CreateGPSTable", ex.getMessage());
			return false;
		}

		return true;
	}

	/* a159 is layerID,a160 is tuban id */
	public static boolean hasSaved(String layerID, String Ids) {
		try {

			SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
			String sql = "select * from T_DuChaJCKP where a159='" + layerID + "' and a160='" + Ids + "'";

		} catch (Exception ex) {
			Log.e("T_DuChaJCKP hasSaved", ex.getMessage());
			return false;
		}

		return false;
	}

}
