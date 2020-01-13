package com.dingtu.DTGIS.DataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils.TruncateAt;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

@SuppressLint("NewApi")
public class DictXZQH 
{
	String configPath = PubVar.m_SysAbsolutePath+"/SysFile/Config.dbx";
	String mDictTable = "T_DictXZQH";
	
	public DictXZQH()
	{
		//m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
	}
	
	public boolean AddXZQH(String level,String parentCode,String Name,String code)
	{
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
		String sql = "insert into "+ mDictTable +"(L_ID,C_PARCODE,C_DOMAINDES,C_CODE,C_NAME,C_SHORTNAME,C_DOMAINNAME,I_ISUSED) values("
					+(getMaxCode()+1)+",'"+parentCode+"','"+level+"','"+code+"','"+Name+"','"+Name+"','行政区划',1)";
		try
		{
			m_SQLiteDatabase.execSQL(sql);
			m_SQLiteDatabase.close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}
		
		return true;
	}
	
	public boolean updateXZQH(String code,String Name)
	{
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
		String sql = "update "+mDictTable+" set C_NAME='"+Name+"' where C_CODE='"+code+"'";
		try
		{
			m_SQLiteDatabase.execSQL(sql);
			m_SQLiteDatabase.close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}
		
		return true;
	}
	
	public boolean deleteXZQH(String code)
	{
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
		String sql = "delete from  "+mDictTable+" where C_CODE='"+code+"'";
		try
		{
			m_SQLiteDatabase.execSQL(sql);
			m_SQLiteDatabase.close();
			
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}
		
		return true;
	}
	
	private int getMaxCode()
	{
		String sql = "select Max(L_ID) as maxCode from "+mDictTable;
		try
		{
			SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			if(reader.Read())
			{
				int  maxID =  reader.GetInt32("maxCode");
				reader.Close();
				m_SQLiteDatabase.close();
				return maxID;
			}
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}

		return 1;
	}
	
	public boolean isSameCode(String code)
	{
		String sql = "select C_NAME,C_CODE from T_DictXZQH where C_CODE='"+code+"'";
		try
		{
			SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			if(reader.Read())
			{
				if(reader.GetCount()>0)
				{
					reader.Close();
					m_SQLiteDatabase.close();
					return true;
				}
			}
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}

		return false;
	}
	
	public String getCodeByName(String name,String level,String ParCode)
	{
		String sql = "select C_NAME,C_CODE from T_DictXZQH where C_NAME='"+name+"' and C_DOMAINDES='"+level+"' and C_PARCODE ='"+ParCode+"'";
		try
		{
			SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			if(reader.Read())
			{
				if(reader.GetCount()>1)
				{
					Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "行政区划名称有重复，无法根据名称获取代码");
					return  "";
				}
				else
				{
					return reader.GetString("C_CODE");
				}
			}
			reader.Close();
			m_SQLiteDatabase.close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}

		return  "";
	}
	
	//根据代码获取行政区划名称，如果查询不到，返回代码
	public String getNameByCode(String code,String level)
	{
		String sql = "select C_NAME,C_CODE from T_DictXZQH where C_CODE='"+code+"' and C_DOMAINDES='"+level+"'";
		try
		{
			SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			if(reader.Read())
			{
				if(reader.GetCount()>1)
				{
					//Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "行政区划名称有重复，无法根据名称获取代码");
					return  code;
				}
				else
				{
					return reader.GetString("C_NAME");
				}
			}
			reader.Close();
			m_SQLiteDatabase.close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "根据代码匹配行政区划错误："+ex.getMessage());
		}

		return "";
	}
	
	public String getNameByShortCode(String code,String level)
	{
		String sql = "select C_NAME,C_CODE from T_DictXZQH where C_CODE='"+code+"' and C_DOMAINDES='"+level+"'";
		try
		{
			SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			if(reader.Read())
			{
				if(reader.GetCount()>1)
				{
					//Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "行政区划名称有重复，无法根据名称获取代码");
					return  code;
				}
				else
				{
					return reader.GetString("C_NAME");
				}
			}
			reader.Close();
			m_SQLiteDatabase.close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "根据代码匹配行政区划错误："+ex.getMessage());
		}

		return  code;
	}
	
	//查询所有同等级下实体，parentCode为空
	@SuppressLint("NewApi")
	public List<HashMap<String,Object>> getXZQH(String parentCode,String level)
	{
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null); 
		List<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();
		String sql = "select L_ID,C_NAME,C_CODE from T_DictXZQH";
		if(parentCode.isEmpty())
		{
			sql += " where C_DOMAINDES='"+level+"' order by C_CODE ASC";
		}
		else
		{
			sql += " where C_PARCODE ='"+parentCode+"' and C_DOMAINDES='"+level+"' order by C_CODE ASC";
		}
		
		
		try
		{
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			while(reader.Read())
			{
				HashMap<String,Object> hm= new HashMap<String,Object>();
				hm.put("D1", reader.GetString("C_NAME"));
            	hm.put("D2", reader.GetString("C_CODE"));
				//hm.put(reader.GetString("C_NAME"), reader.GetString("C_CODE"));
				result.add(hm);
			}
			reader.Close();
			m_SQLiteDatabase.close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, ex.getMessage());
		}
		
		
		return result;
	}
	
	public boolean importXZQH(HashMap<String,String> listXZQH)
	{
		
			String code = listXZQH.get("Code");
			String sql = "select C_Name from "+mDictTable+" where C_CODE = '"+code+"'";
			boolean isUpdate = false;
			SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
			SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			if(reader.Read())
			{
				if(reader.GetCount()>0)
				{
					isUpdate = true;
				}
				
			}
			reader.Close();
			//TODO：检查父代码是否存在
			if(isUpdate)
			{
				sql = "update "+mDictTable+" set C_NAME ='"+listXZQH.get("Name") 
						+"', C_PARCODE='"+listXZQH.get("ParCode")
						+"', C_DOMAINDES='"+listXZQH.get("Level")
						+"' where C_CODE='"+code+"'";
			}
			else
			{
				sql = "insert into "+mDictTable+" (C_DOMAINDES,C_PARCODE,C_CODE,C_NAME) values('"
							+listXZQH.get("Level")+"','"+listXZQH.get("ParCode")+"','"+listXZQH.get("Code")
							+"','"+listXZQH.get("Name")+"')";
			}
			
			m_SQLiteDatabase.execSQL(sql);
			m_SQLiteDatabase.close();
		
		return true;
	}
	
	public List<HashMap<String,String>> exportXZQH(String parentFix)
	{
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(configPath,null);
		String sql = "select C_PARCODE,C_CODE,C_DOMAINDES,C_NAME from "+mDictTable;
		if(!parentFix.isEmpty())
		{
			sql +=" where C_CODE like '"+parentFix+"%'";
		}
		
		List<HashMap<String,String>> listXZQH = new ArrayList<HashMap<String,String>>();
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));

		while(reader.Read())
		{
			HashMap<String,String> hm =new HashMap<String,String>();
			hm.put("ParCode", reader.GetString("C_PARCODE"));
			hm.put("Code", reader.GetString("C_CODE"));
			hm.put("Level", reader.GetString("C_DOMAINDES"));
			hm.put("Name", reader.GetString("C_NAME"));
			listXZQH.add(hm);
		}
		
		reader.Close();
		m_SQLiteDatabase.close();
		return listXZQH;
	}
	
	
}
