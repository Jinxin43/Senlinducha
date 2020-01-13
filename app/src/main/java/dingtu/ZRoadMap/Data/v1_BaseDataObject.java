package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.List;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;
import android.util.Log;

/**
 * ��������ʵ���࣬��Ҫ���ڽ��������ݿ�İ󶨲���
 * @author lmgk
 *
 */
public class v1_BaseDataObject
{
	public v1_BaseDataObject()
	{
	}
	
	//���ݿ������
	private ASQLiteDatabase m_SQliteDB = null;
	
	/**
	 * ���������������
	 * @param sqliteDB
	 */
	public void SetSQLiteDatabase(ASQLiteDatabase sqliteDB)
	{
		this.m_SQliteDB = sqliteDB;
	}
	
	
	/**
	 * ������������
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
	
	//�������ݱ�
	private String m_DataTable = "";
	/**
	 * ���ù������ݱ�
	 * @param dataTable
	 */
	public void SetDataTable(String dataTable){this.m_DataTable = dataTable;}
	
	//ʵ��ID
	private int SYS_ID = -1;								
	/**
	 * ����BaseObject��SYS_ID
	 * @param sysid
	 */
	public void SetSYS_ID(int sysid)
	{
		this.SYS_ID = sysid;
	}
	//public int GetSYS_ID(){return this.SYS_ID;}


	/**
	 * ��ȡ���ݲ����½�����ʾ
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
        this.RefreshDataToView();    //������ˢ�µ��ؼ���
	}
	

    /**
     * �����ݿ�ɾ��ʵ��
     * @return
     */
    public boolean DeleteFormDb()
    {
        String SQL = "delete from "+this.m_DataTable+" where SYS_ID = "+this.SYS_ID;
        boolean deleteOK = this.m_SQliteDB.ExcuteSQL(SQL);
        return deleteOK;
    }
    
    /**
     * ����ʵ��,FeatureList��ʽ��ʽ���ֶ�����='XXX'
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
            Log.d("","���ڱ�������["+SQL+"]");
            if (this.m_SQliteDB.ExcuteSQL(SQL)) return true;

        }
        catch(Error e) 
        {
           // Tools.ShowMessageBox(PubVar.m_DoEvent.m_GPSPoint.SubWindow,"["+this.SYS_TYPE + "] ����ʧ�ܣ�\r\nԭ��"+e.getMessage());
            return false; 
        }
		return false;
    }

    /**
     * ����ʵ��,FeatureList��ʽ��ʽ��F1='XXX'
     * @param FeatureList
     * @return
     */
    private boolean UpdateFeatrue(List<String> FeatureList)
    {
        try
        {
            //����ʵ���������Ϣ
            String SQL = "update %1$s set %2$s where SYS_ID=%3$s";
            SQL = String.format(SQL, this.m_DataTable,Tools.JoinT(",", FeatureList),this.SYS_ID);
            return this.m_SQliteDB.ExcuteSQL(SQL);
        }
        catch (Error e)
        {
            Tools.ShowMessageBox("[" + this.m_DataTable + "] ����ʧ�ܣ�\r\nԭ��"+e.getMessage());
            return false;
        }
    }
    

    
    /**
     * ���б����ݰ󶨵�List<DataBindOfKeyValue>��
     * @param FeatureList����ʽ��F1='XXX'
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
     * ��List<DataBindOfKeyValue>ת����FeatureList�б���ʽ��F1='XXX'
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
    
	//������ؼ�������
	private List<DataBindOfKeyValue> DataBindList = new ArrayList<DataBindOfKeyValue>();

    /**
     * ���Ӱ���Ŀ
     * @param dbov = Key�����ֶ�����Value��ͼ�ؼ�Id
     */
	public void AddDataBindItem(DataBindOfKeyValue dbov)
	{
		this.DataBindList.add(dbov);
	}

	
	/**
	 * ������ˢ�µ��ؼ���
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
	 * ���ؼ��е�ֵˢ�µ�������
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
	 * ����ָ����KEY�İ���ֵ
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

