package com.dingtu.DTGIS.DataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.ForestryLayerType;
import lkmap.Tools.Tools;

@SuppressLint("NewApi")
public class DictDataDB {
	SQLiteDatabase m_SQLiteDatabase = null;
	String configPath = PubVar.m_SysAbsolutePath + "/SysFile/Config.dbx";
	String mDictTable = "T_DictData";

	private static HashMap<String, List<String>> mLDBGEnumList = new HashMap<String, List<String>>();
	private static HashMap<String, List<String>> mTGHLEnumList = new HashMap<String, List<String>>();
	private static HashMap<String, List<String>> mErDiaoEnumList = new HashMap<String, List<String>>();

	public List<String> getEnumList(String yinzi, String category) {
		List<String> resultList = new ArrayList<String>();
		if (yinzi.equals("林地变更")) {
			resultList = mLDBGEnumList.get(category);
			if (resultList == null) {
				resultList = getEnumValue(yinzi, category);
				mLDBGEnumList.put(category, resultList);
			}
		} else if (yinzi.equals(ForestryLayerType.LinyeErdiao)) {
			resultList = mErDiaoEnumList.get(category);
			if (resultList == null) {
				resultList = getEnumValue(yinzi, category);
				mErDiaoEnumList.put(category, resultList);
			}
		} else if (yinzi.equals("退耕还林")) {
			resultList = mTGHLEnumList.get(category);
			if (resultList == null) {
				resultList = getEnumValue(yinzi, category);
				mTGHLEnumList.put(category, resultList);
			}
		} else {

			resultList = getEnumValue(yinzi, category);
		}

		return resultList;
	}

	public DictDataDB() {
		m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath, null);
	}

	public List<HashMap<String, Object>> getDictData(String projectType, String hangye) {
		String sql = "select L_ID,L_PARID,C_CODE,C_NAME from " + mDictTable + " where C_SHORTNAME='" + hangye
				+ "' and C_YINZINAME='" + projectType + "'";

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		try {
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			while (reader.Read()) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("ID", reader.GetInt32("L_ID"));
				hm.put("PID", reader.GetInt32("L_PARID"));
				hm.put("Name", reader.GetString("C_NAME"));
				hm.put("Code", reader.GetString("C_CODE"));
				result.add(hm);
			}
			reader.Close();
		} catch (Exception ex) {
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}

		return result;
	}

	public List<HashMap<String, Object>> getDictData(String hangye, String projectType, String ParName) {
		int pid = getPID(ParName, hangye, projectType);
		String sql = "select L_ID,C_CODE,C_NAME from " + mDictTable + " where C_SHORTNAME='" + hangye
				+ "' and C_YINZINAME='" + projectType + "' and L_PARID=" + pid;

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		try {
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			while (reader.Read()) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("ID", reader.GetInt32("L_ID"));
				hm.put("Name", reader.GetString("C_NAME"));
				hm.put("Code", reader.GetString("C_CODE"));
				result.add(hm);
			}
			reader.Close();
		} catch (Exception ex) {
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}

		return result;
	}

	public boolean updateDict(String name, int id, String code) {
		boolean result = false;
		String sql = "update " + this.mDictTable + " set C_NAME='" + name + "', C_CODE='" + code + "' where L_ID=" + id;
		try {
			m_SQLiteDatabase.execSQL(sql);
			result = true;
		} catch (Exception ex) {
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}

		return result;
	}

	public boolean deleteDict(int id) {
		boolean result = false;
		String sql = "delete from " + this.mDictTable + " where L_ID=" + id;
		try {
			deleteSubKind(id);
			m_SQLiteDatabase.execSQL(sql);
			result = true;
		} catch (Exception ex) {
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}

		return result;
	}

	private boolean deleteSubKind(int id) {
		boolean result = false;
		String sql = "select L_ID from " + this.mDictTable + " where L_PARID =" + id;
		try {
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			while (reader.Read()) {
				int subID = reader.GetInt32("L_ID");
				if (subID > 0) {
					deleteSubKind(subID);
				}
			}
			reader.Close();

			String sqldelete = "delete from " + this.mDictTable + " where L_PARID=" + id;
			m_SQLiteDatabase.execSQL(sqldelete);
			result = true;
		} catch (Exception ex) {
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}

		return result;
	}

	public boolean importXZQH(HashMap<String, String> hm) {
		// int pID = getPID(hm.get("name"), hm.get("hangye"),
		// hm.get("projecttype"),hm.get("yinzi"));
		int id = getID(hm.get("name"), hm.get("pID"), hm.get("hangye"), hm.get("projecttype"));
		if (id > 0) {
			updateDict(hm.get("name"), id, hm.get("code"));
		} else {
			if (!addDict(hm.get("name"), hm.get("code"), hm.get("pID"), hm.get("hangye"), hm.get("projecttype"))) {
				return false;
			}
		}
		return true;
	}

	private boolean isExist(String dictName, String pID, String hangye, String projecttype) {
		int id = getID(dictName, pID, hangye, projecttype);
		if (id > 0) {
			return true;
		} else {
			return false;
		}
	}

	private int getID(String dictName, String pID, String hangye, String projecttype) {
		if (dictName.isEmpty()) {
			return 0;
		}

		String sql = "select L_ID from " + mDictTable + " where C_Name = '" + dictName + "'and C_YINZINAME='"
				+ projecttype + "'" + "and C_SHORTNAME ='" + hangye + "' and L_PARID =" + pID;
		int ID = 0;
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
		if (reader.Read()) {
			if (reader.GetCount() >= 1) {
				ID = reader.GetInt32("L_ID");
			}

		}
		reader.Close();

		return ID;
	}

	@SuppressLint("NewApi")
	public int getPID(String dictName, String hangye, String projecttype) {
		if (dictName.isEmpty()) {
			return 0;
		}

		String sql = "select L_ID from " + mDictTable + " where C_Name = '" + dictName + "'  and C_YINZINAME='"
				+ projecttype + "'" + "and C_SHORTNAME ='" + hangye + "'";
		int pID = 0;
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
		if (reader.Read()) {
			if (reader.GetCount() >= 1) {
				pID = reader.GetInt32("L_ID");
			}

		}
		reader.Close();

		return pID;
	}

	public List<String> getEnumValue(String yinzi, String parentName) {
		ArrayList<String> result = new ArrayList<String>();
		String sql = "select a.C_NAME,a.C_CODE from T_DictData a join T_DictData b on a.L_ParID = b.L_ID where b.C_Name='"
				+ parentName + "' and b.C_YINZINAME='" + yinzi + "'";
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));

		while (reader.Read()) {
			String code = reader.GetString("C_CODE");
			String name = reader.GetString("C_NAME");

			if (code != null && code.length() > 0) {
				result.add(code + "(" + name + ")");
			} else {
				result.add(name);
			}

			if (name != null && name.length() > 0) {
				List<String> list = getEnumValue(yinzi, name);
				result.addAll(list);
			}

		}

		reader.Close();

		return result;
	}

	public List<String> getEnumItem(String hangye, String yinzi, String parentName) {
		ArrayList<String> result = new ArrayList<String>();
		// int pID = getID(parentName,"0",hangye,yinzi);
		String sql = "select a.C_NAME,a.C_CODE from T_DictData a join T_DictData b on a.L_ParID = b.L_ID where b.C_Name='"
				+ parentName + "' and a.C_SHORTNAME='" + hangye + "' and a.C_YINZINAME='" + yinzi + "'";
		// String sql = "C_NAME from "+mDictTable+" where
		// C_SHORTNAME='"+hangye+"' and C_YINZINAME='"+yinzi+"' and
		// L_PARID="+pID;
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));

		while (reader.Read()) {
			String code = reader.GetString("C_CODE");
			String name = reader.GetString("C_NAME");
			if (name != null && name.length() > 0) {
				if (code != null && code.length() > 0) {
					result.add(code + "(" + name + ")");
				} else {
					result.add(name);
				}

				result.addAll(getEnumItem(hangye, yinzi, name));
			}

		}

		reader.Close();

		return result;
	}

	public String getEnumItenWithCode(String hangye, String yinzi, String parentName, String value) {
		String result = "";
		// int pID = getID(parentName,"0",hangye,yinzi);
		String sql = "select a.C_NAME,a.C_CODE from T_DictData a join T_DictData b on a.L_ParID = b.L_ID where b.C_Name='"
				+ parentName + "' and a.C_SHORTNAME='" + hangye + "' and a.C_YINZINAME='" + yinzi + "'";
		// String sql = "C_NAME from "+mDictTable+" where
		// C_SHORTNAME='"+hangye+"' and C_YINZINAME='"+yinzi+"' and
		// L_PARID="+pID;
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));

		while (reader.Read()) {
			boolean isGet = false;
			if (value.equals(reader.GetString("C_CODE"))) {
				result = value + "(" + reader.GetString("C_NAME") + ")";
				isGet = true;
			}

			if (!isGet) {
				result = getEnumItenWithCode(hangye, yinzi, reader.GetString("C_NAME"), value);
				if (!result.equals(value)) {
					break;
				}
			} else {
				break;
			}

		}

		reader.Close();

		if (result.length() == 0) {
			result = value;
		}

		return result;
	}

	public boolean addDict(String name, String code, String pID, String hangye, String projectType) {
		boolean result = false;
		String sql = "insert into " + this.mDictTable + " (C_NAME,C_CODE,L_PARID,C_SHORTNAME,C_YINZINAME) values(" + "'"
				+ name + "','" + code + "'," + pID + ",'" + hangye + "','" + projectType + "')";
		try {
			m_SQLiteDatabase.execSQL(sql);
			result = true;
		} catch (Exception ex) {
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}

		return result;
	}

	public List<HashMap<String, String>> exportXZQH() {

		String sql = "select b.C_NAME as PNAME, a.C_NAME as Name, a.C_CODE as Code,a.C_SHORTNAME"
				+ " as Hangye,a.C_YINZINAME as projectType from T_DictData"
				+ " a left join T_DictData b on a.L_PARID=b.L_ID";

		List<HashMap<String, String>> listXZQH = new ArrayList<HashMap<String, String>>();
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));

		while (reader.Read()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("PName", reader.GetString("PNAME"));
			hm.put("Name", reader.GetString("Name"));
			hm.put("Code", reader.GetString("Code"));
			hm.put("HangYe", reader.GetString("Hangye"));
			hm.put("ProjectType", reader.GetString("projectType"));
			listXZQH.add(hm);
		}

		reader.Close();
		return listXZQH;
	}
}
