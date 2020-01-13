package lkmap.ZRoadMap.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

//��Ӧ���ݱ�T_MyCoordinateSystem
public class v1_UserConfigDB_UserParam {
	// ���ݿ������
	private ASQLiteDatabase m_SQLiteDatabase = null;

	/**
	 * �����ݿ������
	 * 
	 * @param _db
	 */
	public void SetBindDB(ASQLiteDatabase _db) {
		this.m_SQLiteDatabase = _db;
	}

	/**
	 * ����ָ��������ȡ����ֵ�б�
	 * 
	 * @param ItemName
	 * @return
	 */
	public HashMap<String, String> GetUserPara(String ItemName) {
		HashMap<String, String> configInfo = null;
		String SQL = "select * from T_UserParam where F1='" + ItemName + "'";
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
		if (DR == null)
			return configInfo;
		while (DR.Read()) {
			if (configInfo == null)
				configInfo = new HashMap<String, String>();
			String[] fieldList = DR.GetFieldNameList();
			for (String field : fieldList) {
				configInfo.put(field, DR.GetString(field));
			}
		}
		DR.Close();
		return configInfo;
	}

	/**
	 * �������ò���
	 * 
	 * @param ItemName
	 * @param Param
	 * @return
	 */
	public boolean SaveUserPara(String ItemName, HashMap<String, String> Param) {
		// ����Ѿ�����ָ��������
		boolean UpdateMode = false;
		String SQL = "Select count(*) as TCount from T_UserParam where F1='" + ItemName + "'";
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
		if (DR.Read()) {
			if (Integer.parseInt(DR.GetString("TCount")) == 1)
				UpdateMode = true;
		}
		DR.Close();

		// ��ȡ������Ŀ��ֵ
		List<String> KeyList = new ArrayList<String>();
		List<String> ValueList = new ArrayList<String>();
		List<String> KeyValueList = new ArrayList<String>();
		Iterator iter = Param.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			KeyList.add(entry.getKey().toString());
			ValueList.add(entry.getValue().toString());
			KeyValueList.add(KeyList.get(KeyList.size() - 1) + "='" + ValueList.get(ValueList.size() - 1) + "'");
		}

		// ����������
		if (UpdateMode) // ����
		{
			SQL = "update T_UserParam set %2$s where F1='%1$s'";
			SQL = String.format(SQL, ItemName, Tools.JoinT(",", KeyValueList));
		} else {
			SQL = "insert into T_UserParam (F1,%2$s) values ('%1$s','%3$s')";
			SQL = String.format(SQL, ItemName, Tools.JoinT(",", KeyList), Tools.JoinT("','", ValueList));
		}

		return this.m_SQLiteDatabase.ExcuteSQL(SQL);
	}

	public boolean deleteUserPara(String ItemName) {
		String SQL = "delete from T_UserParam where F1='" + ItemName + "'";
		return this.m_SQLiteDatabase.ExcuteSQL(SQL);
	}

	// /**
	// * ��ȡ�ϴδ򿪵Ĺ�����Ϣ
	// * @return HashMap<String,String> ��ʽ:Name=�������ƣ�Time=�ϴδ�ʱ��
	// */
	// public HashMap<String,String> ReadBeforeOpenProjectInfo()
	// {
	// HashMap<String,String> prjInfo = null;
	// String SQL = "select * from T_UserParam where F1='�ϴδ򿪹���'";
	// SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
	// if (DR==null)return prjInfo;
	// while (DR.Read())
	// {
	// prjInfo = new HashMap<String,String>();
	// prjInfo.put("Name", DR.GetString("F2"));
	// prjInfo.put("Time", DR.GetString("F3"));
	// }DR.Close();
	//
	// return prjInfo;
	// }
	//
	// /**
	// * �洢�ϴδ򿪵Ĺ�����Ϣ
	// * @param HashMap<String,String> ��ʽ:Name=�������ƣ�Time=�ϴδ�ʱ��
	// * @return
	// */
	// public boolean SaveBeforeOpenProjectInfo(HashMap<String,String> prjInfo)
	// {
	// //ɾ��ԭ�е��ϴδ򿪹�����Ϣ
	// String SQL = "delete from T_UserParam where
	// F1='"+prjInfo.get("Name")+"'";
	// if (!this.m_SQLiteDatabase.ExecuteSQL(SQL)) return false;
	//
	// //�����µ��ϴδ򿪹�����Ϣ
	// SQL = "insert into T_UserParam (F1,F2,F3) values ('%1$s','%2$s','%3$s')";
	// SQL =
	// String.format(SQL,"�ϴδ򿪹���",prjInfo.get("Name"),prjInfo.get("Time"));
	// return this.m_SQLiteDatabase.ExecuteSQL(SQL);
	// }

}