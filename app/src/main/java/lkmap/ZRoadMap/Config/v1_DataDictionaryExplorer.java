package lkmap.ZRoadMap.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.sqlite.SQLiteException;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;


public class v1_DataDictionaryExplorer 
{
	private v1_ConfigDB m_ConfigDB = null;
	
	/**
	 * 绑定配置操作类
	 * @param projectDB
	 */
	public void SetBindProjectDB(v1_ConfigDB configDB)
	{
		this.m_ConfigDB = configDB;
	}
	
	/**
	 * 获取数据字典全部数据
	 * @return
	 */
	public List<HashMap<String,String>> GetZDAllDataList()
	{
		List<HashMap<String,String>> ZDDataList = new ArrayList<HashMap<String,String>>();
		String SQL = "Select * from T_DataDictionary";
		SQLiteDataReader DR = this.m_ConfigDB.GetSQLiteDatabase().Query(SQL);
		if (DR==null)return ZDDataList;
		while(DR.Read())
		{
			String[] FieldList = DR.GetFieldNameList();
			HashMap<String,String> hm = new HashMap<String,String>();
			for(String Field:FieldList)
			{
				hm.put(Field, DR.GetString(Field));
			}
			ZDDataList.add(hm);
		}DR.Close();
		return ZDDataList;
	}
	
	/**
	 * 获取数据字典类型列表
	 * @return
	 */
	public List<String> GetZDTypeList()
	{
		List<String> ZDTypeList = new ArrayList<String>();
		String SQL = "Select distinct ZDType from T_DataDictionary ";
		SQLiteDataReader DR = this.m_ConfigDB.GetSQLiteDatabase().Query(SQL);
		if (DR==null)return ZDTypeList;
		while(DR.Read())
		{
			ZDTypeList.add(DR.GetString("ZDType"));
		}DR.Close();
		return ZDTypeList;
	}
	
	/**
	 * 获取条目大类列表
	 * @return
	 */
	public List<String> GetZDSubList(String ZDType)
	{
		List<String> ZDSubList = new ArrayList<String>();
		String SQL = "Select distinct ZDSub from T_DataDictionary where ZDType='"+ZDType+"'";
		SQLiteDataReader DR = this.m_ConfigDB.GetSQLiteDatabase().Query(SQL);
		if (DR==null)return ZDSubList;
		while(DR.Read())
		{
			ZDSubList.add(DR.GetString("ZDSub"));
		}DR.Close();
		return ZDSubList;
	}

	/**
	 * 获取条目细类列表
	 * @return
	 */
	public List<String> GetZDNameList(String ZDType,String ZDSub)
	{
		List<String> ZDNameList = new ArrayList<String>();
		String SQL = "Select distinct ZDName from T_DataDictionary where ZDType='"+ZDType+"' and ZDSub='"+ZDSub+"'";
		SQLiteDataReader DR = this.m_ConfigDB.GetSQLiteDatabase().Query(SQL);
		if (DR==null)return ZDNameList;
		while(DR.Read())
		{
			ZDNameList.add(DR.GetString("ZDName"));
		}DR.Close();
		return ZDNameList;
	}
	
	/**
	 * 获取字典值列表
	 * @param ZDType
	 * @param ZDSub
	 * @param ZDName
	 * @return HashMap("ZDBM","ZDList")
	 */
	public HashMap<String,Object> GetZDValueList(String ZDType,String ZDSub,String ZDName)
	{
		HashMap<String,Object> resultObj = new HashMap<String,Object>();
		resultObj.put("ZDBM", "");resultObj.put("ZDList", null);
		String SQL = "Select ZDBM,ZDList from T_DataDictionary where ZDType='"+ZDType+"' and ZDSub='"+ZDSub+"' and ZDName='"+ZDName+"'";
		SQLiteDataReader DR = this.m_ConfigDB.GetSQLiteDatabase().Query(SQL);
		if (DR==null)return resultObj;
		if(DR.Read())
		{
			String ZDValue = DR.GetString("ZDList");
			if (ZDValue!=null)
			{
				resultObj.put("ZDBM", DR.GetString("ZDBM"));
				resultObj.put("ZDList", Tools.StrArrayToList(ZDValue.split(",")));
			}
		}DR.Close();
		return resultObj;
	}
	
	/**
	 * 根据字典编码获取字段值列表
	 * @param ZDBM
	 * @return
	 */
	public List<String> GetZDValueList(String ZDBM)
	{
		List<String> ZDValueList = new ArrayList<String>();
		String SQL = "Select ZDList from T_DataDictionary where ZDBM='"+ZDBM+"'";
		SQLiteDataReader DR = this.m_ConfigDB.GetSQLiteDatabase().Query(SQL);
		if (DR==null)return ZDValueList;
		if(DR.Read())
		{
			String value = DR.GetString("ZDList");
			if (value!=null)
			{
				String[] values = value.split(",");
				for(String v:values)
				{
					if (!v.equals(""))ZDValueList.add(v);
				}
			}
		}DR.Close();
		return ZDValueList;
	}
	
	/**
	 * 以事务的形式保存数据字典
	 * @param ZDDataList
	 * @return
	 */
	public boolean Save(List<HashMap<String,String>> ZDDataList)
	{
		try
		{
			this.m_ConfigDB.GetSQLiteDatabase().GetSQLiteDatabase().beginTransaction();

			//1、删除指定类别的数据字典
			String SQL = "delete from T_DataDictionary";
			this.m_ConfigDB.GetSQLiteDatabase().GetSQLiteDatabase().execSQL(SQL);
			
			//2、插入新的数据字典项
			for(HashMap<String,String> hm:ZDDataList)
			{
				SQL = "insert into T_DataDictionary (ZDType,ZDSub,ZDName,ZDList,ZDBM) values ('%1$s','%2$s','%3$s','%4$s','%5$s')";
				SQL = String.format(SQL, hm.get("ZDType"),hm.get("ZDSub"),hm.get("ZDName"),hm.get("ZDList"),hm.get("ZDBM"));
				this.m_ConfigDB.GetSQLiteDatabase().GetSQLiteDatabase().execSQL(SQL);
			}
			
			this.m_ConfigDB.GetSQLiteDatabase().GetSQLiteDatabase().setTransactionSuccessful();
			return true;
		}
		catch(SQLiteException ex)
		{
			return false;
		}
		finally
		{
			this.m_ConfigDB.GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
		}
	}
}
