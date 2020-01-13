package lkmap.ZRoadMap.Config;

import java.util.ArrayList;
import java.util.List;

import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

public class v1_ConfigDB {

	// 数据库操作类
	private ASQLiteDatabase m_SQLiteDatabase = null;

	// 符号库
	private v1_SymbolExplorer m_SymbolExplorer = null;

	public v1_SymbolExplorer GetSymbolExplorer() {
		if (this.m_SymbolExplorer == null)
			this.m_SymbolExplorer = new v1_SymbolExplorer();
		this.GetSQLiteDatabase();
		this.m_SymbolExplorer.SetBindSQLiteDatabase(this.GetSQLiteDatabase());
		return this.m_SymbolExplorer;
	}

	private v1_SymbolExplorer m_BKSymbolExplorer = null;

	public v1_SymbolExplorer GetBKSymbolExplorer() {
		if (this.m_BKSymbolExplorer == null)
			this.m_BKSymbolExplorer = new v1_SymbolExplorer();
		return this.m_BKSymbolExplorer;
	}

	// 数据字典
	private v1_DataDictionaryExplorer m_DataDictionary = null;

	public v1_DataDictionaryExplorer GetDataDictionaryExplorer() {
		if (this.m_DataDictionary == null)
			this.m_DataDictionary = new v1_DataDictionaryExplorer();
		this.GetSQLiteDatabase();
		this.m_DataDictionary.SetBindProjectDB(this);
		return this.m_DataDictionary;
	}

	/**
	 * 得到指定的数据库操作类
	 * 
	 * @return
	 */
	public ASQLiteDatabase GetSQLiteDatabase() {
		if (this.m_SQLiteDatabase == null)
			this.OpenDatabase();
		return this.m_SQLiteDatabase;
	}

	// 打开配置数据库
	private void OpenDatabase() {
		String configFileName = PubVar.m_SysAbsolutePath + "/sysfile/config.dbx";
		if (Tools.ExistFile(configFileName)) {
			this.m_SQLiteDatabase = new ASQLiteDatabase();
			this.m_SQLiteDatabase.setDatabaseName(configFileName);
		}
	}

	/**
	 * 读取配置项目
	 * 
	 * @param ItemType
	 * @return
	 */
	public List<String> ReadConfigItem(String ItemType) {
		if (this.m_SQLiteDatabase == null)
			this.OpenDatabase();

		String SQL = "";
		if (ItemType.equals("坐标系统")) {
			SQL = "select * from T_CoorSystem order by code";
		}
		if (ItemType.equals("椭球转换方法")) {
			SQL = "select * from T_CoorSystemTransMethod where Type = '椭球转换' order by code";
		}
		if (ItemType.equals("平面转换方法")) {
			SQL = "select * from T_CoorSystemTransMethod where Type = '平面转换' order by code";
		}

		List<String> resultList = new ArrayList<String>();
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
		if (DR == null)
			return resultList;
		while (DR.Read()) {
			String Code = DR.GetString("Code");
			String Name = DR.GetString("Name");
			resultList.add(Name);
		}
		return resultList;
	}

	public List<String> ReadShuZhong(String cityCode) {
		if (this.m_SQLiteDatabase == null) {
			this.OpenDatabase();
		}
		String sql = "select ShuZhongCode from ShuZhong ";

		if (cityCode != null) {
			sql += "where CityID ='" + cityCode + "'";
		}

		List<String> resultList = new ArrayList<String>();
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(sql);
		if (DR == null) {
			return resultList;
		}

		while (DR.Read()) {
			String sz = DR.GetString("ShuZhongCode");
			resultList.add(sz);
		}

		return resultList;
	}

	public String QueryCaijishiByShuZhongName(String pShuzhong, String pCityName) {
		String strCaijishi = "";
		String sql = "select * from ShuZhong where ShuZhongCode='" + pShuzhong + "' and (CityName='" + pCityName
				+ "'OR CityID='" + pCityName + "')";
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(sql);
		if (DR == null) {
			return strCaijishi;
		}

		while (DR.Read()) {
			strCaijishi = DR.GetString("CaiJiShi");
		}

		return strCaijishi;
	}

	public String QueryCaijishi(String pShuzhongCode, String pCityCode) {
		String strCaijishi = "";
		String sql = "select * from ShuZhong where ShuZhongCode='" + pShuzhongCode + "' and CityID='" + pCityCode + "'";
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(sql);
		if (DR == null) {
			return strCaijishi;
		}

		while (DR.Read()) {
			strCaijishi = DR.GetString("CaiJiShi");
		}

		return strCaijishi;
	}

	public int QuaryCaiji(int pJingjie, int pCaijishi) {
		int intCaiji = 0;
		String sql = "select * from T_CaiJi where JingJie=" + pJingjie;
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(sql);
		if (DR == null) {
			return 0;
		}

		while (DR.Read()) {
			intCaiji = DR.GetInt32(pCaijishi);
		}
		return intCaiji;
	}

	public String QueryShuzhong(String pShuzhongCode, String pCityCode) {
		String strCaijishi = "";
		String sql = "select * from ShuZhong where ShuZhongCode='" + pShuzhongCode + "' and CityID='" + pCityCode + "'";
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(sql);
		if (DR == null) {
			return strCaijishi;
		}

		while (DR.Read()) {
			strCaijishi = DR.GetString("ShuZhong");
		}

		return strCaijishi;
	}

	public String QueryZDList(String pZDName) {
		String zdlist = "";
		String sql = "select * from TDataDictionary where ZDNAME='" + pZDName + "'";
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(sql);
		if (DR == null) {
			if (DR.Read()) {
				zdlist = DR.GetString("ZDList");
			}

		}

		return zdlist;
	}

}
