package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.List;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;
import android.util.Log;

/**
 * 基础数据实体类，主要用于界面与数据库的绑定操作
 * @author lmgk
 *
 */
public class v1_BaseDataObject
{
	public v1_BaseDataObject()
	{
	}
	
	//数据库操作类
	private ASQLiteDatabase m_SQliteDB = null;
	
	/**
	 * 设置数据类操作类
	 * @param sqliteDB
	 */
	public void SetSQLiteDatabase(ASQLiteDatabase sqliteDB)
	{
		this.m_SQliteDB = sqliteDB;
	}
	
	
	/**
	 * 保存属性数据
	 * @return
	 */
	public boolean SaveToDB()
	{
		if (this.SYS_ID==-1)
		{
			return this.SaveNewAdd(this.GetFeatureList());
		}
		else 
		{
			return this.UpdateFeatrue(this.GetFeatureList());
		}
	}
	
	//设置数据表
	private String m_DataTable = "";
	/**
	 * 设置关联数据表
	 * @param dataTable
	 */
	public void SetDataTable(String dataTable){this.m_DataTable = dataTable;}
	
	//实体ID
	private int SYS_ID = -1;								
	/**
	 * 设置BaseObject的SYS_ID
	 * @param sysid
	 */
	public void SetSYS_ID(int sysid)
	{
		this.SYS_ID = sysid;
	}
	//public int GetSYS_ID(){return this.SYS_ID;}


	/**
	 * 读取数据并更新界面显示
	 * @param WhereFilter
	 */
	public void ReadDataAndBindToView(String Where)
	{
        List<String> FeatureList = new ArrayList<String>();
        SQLiteDataReader DR = this.m_SQliteDB.Query("select * from "+this.m_DataTable+" where " + Where);
        if (DR==null) return;
        if (DR.Read()) 
        {
        	String[] FieldNameList = DR.GetFieldNameList();
        	for(String FieldName : FieldNameList)
        	{
        		if (FieldName.equals("SYS_ID")) this.SYS_ID = Integer.parseInt(DR.GetString(FieldName));
        		String FValue = DR.GetString(FieldName);
        		if (FValue==null)FValue="";
        		FeatureList.add(FieldName+","+FValue);
        	}
    		this.SetFeatureList(FeatureList);
        }DR.Close();
        this.RefreshDataToView();    //将数据刷新到控件上
	}
	

    /**
     * 从数据库删除实体
     * @return
     */
    public boolean DeleteFormDb()
    {
        String SQL = "delete from "+this.m_DataTable+" where SYS_ID = "+this.SYS_ID;
        boolean deleteOK = this.m_SQliteDB.ExcuteSQL(SQL);
        return deleteOK;
    }
    
    /**
     * 保存实体,FeatureList格式形式：字段名称='XXX'
     * @param FeatureList
     * @return
     */
    private boolean SaveNewAdd(List<String> FeatureList)
    {
        try
        {
        	List<String> FieldNameList = new ArrayList<String>();
        	List<String> FieldValueList = new ArrayList<String>();
        	for(String FV:FeatureList)
        	{
        		String fv[] = FV.split("=");
        		String FieldName = fv[0];
        		String FieldValue = (fv.length!=2?"":fv[1]);
        		FieldNameList.add(FieldName);FieldValueList.add(FieldValue);
        	}
        	
            String SQL = "insert into %1$s (%2$s) values (%3$s)";
            SQL = String.format(SQL,this.m_DataTable, Tools.JoinT(",", FieldNameList),Tools.JoinT(",", FieldValueList));
            Log.d("","正在保存数据["+SQL+"]");
            if (this.m_SQliteDB.ExcuteSQL(SQL)) return true;

        }
        catch(Error e) 
        {
           // Tools.ShowMessageBox(PubVar.m_DoEvent.m_GPSPoint.SubWindow,"["+this.SYS_TYPE + "] 保存失败！\r\n原因："+e.getMessage());
            return false; 
        }
		return false;
    }

    /**
     * 更新实体,FeatureList格式形式：F1='XXX'
     * @param FeatureList
     * @return
     */
    private boolean UpdateFeatrue(List<String> FeatureList)
    {
        try
        {
            //更新实体的属性信息
            String SQL = "update %1$s set %2$s where SYS_ID=%3$s";
            SQL = String.format(SQL, this.m_DataTable,Tools.JoinT(",", FeatureList),this.SYS_ID);
            return this.m_SQliteDB.ExcuteSQL(SQL);
        }
        catch (Error e)
        {
            Tools.ShowMessageBox("[" + this.m_DataTable + "] 更新失败！\r\n原因："+e.getMessage());
            return false;
        }
    }
    

    
    /**
     * 将列表数据绑定到List<DataBindOfKeyValue>上
     * @param FeatureList，格式：F1='XXX'
     */
    public void SetFeatureList(List<String> FeatureList)
    {
    	for(int i=0;i<FeatureList.size();i++)
    	{
    		String[] fv = FeatureList.get(i).split(",");
    		String FieldName = fv[0];
    		String FieldValue = (fv.length!=2?"":fv[1]);
    		this.SetDataBindItemValue(FieldName,FieldValue);
    		//this.DataBindList.get(i).Value=FeatureList.get(i);
    	}
    }
    
    /**
     * 将List<DataBindOfKeyValue>转化成FeatureList列表，格式：F1='XXX'
     * @return
     */
    public List<String> GetFeatureList()
    {
    	List<String> FeatureList = new ArrayList<String>();
    	for(int i=0;i<this.DataBindList.size();i++)
    	{
    		DataBindOfKeyValue dv = this.DataBindList.get(i);
    		FeatureList.add(dv.DataKey+"='"+dv.Value+"'");
    	}
    	return FeatureList;
    }
    
	//数据与控件集合类
	private List<DataBindOfKeyValue> DataBindList = new ArrayList<DataBindOfKeyValue>();

    /**
     * 增加绑定项目
     * @param dbov = Key数据字段名，Value视图控件Id
     */
	public void AddDataBindItem(DataBindOfKeyValue dbov)
	{
		this.DataBindList.add(dbov);
	}

	
	/**
	 * 将数据刷新到控件上
	 */
	public void RefreshDataToView()
	{
		for(DataBindOfKeyValue KV:DataBindList)
		{
			if (KV.ViewControl!=null)
			{
				Tools.SetValueToView(KV.Value, KV.ViewControl);
			}
		}
	}
	
	/**
	 * 将控件中的值刷新到数据中
	 */
	public void RefreshViewValueToData()
	{
		for(DataBindOfKeyValue KV:DataBindList)
		{
			if (KV.ViewControl!=null)
			{
				KV.Value = Tools.GetViewValue(KV.ViewControl);
			}
		}
	}
	
	/**
	 * 设置指定的KEY的绑定项值
	 * @param key
	 * @param value
	 */
	public void SetDataBindItemValue(String key,String value)
	{
		for(DataBindOfKeyValue DKV:this.DataBindList)
		{
			if (DKV.DataKey.equals(key))
			{
				DKV.Value=value;
			}
		}
	}

}

