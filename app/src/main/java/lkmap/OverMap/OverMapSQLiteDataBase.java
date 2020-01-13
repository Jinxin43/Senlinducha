package lkmap.OverMap;
import java.io.File;

import com.dingtu.senlinducha.R;

import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import dingtu.ZRoadMap.PubVar;

public class OverMapSQLiteDataBase 
{

    public OverMapSQLiteDataBase() { }

    /// <summary>
    /// ���ݿ��ļ�·��
    /// </summary>
    private SQLiteDatabase _SQLiteDatabase = null;
    private String _DatabaseName = "";
    public String getDatabaseName()
    {
    	return _DatabaseName;
    }
    public void setDatabaseName(String value)
    {
    	try
    	{
    		_DatabaseName = value;
    		if (!lkmap.Tools.Tools.ExistFile(_DatabaseName))
    		{
    			//this.CreateDatabase(_DatabaseName);
    			Tools.CopyToFileFromRawID(PubVar.m_DoEvent.m_Context,_DatabaseName,R.raw.mapbase);
    		}
    		if (_SQLiteDatabase!=null)
    		{
    			//if (_SQLiteDatabase.isDbLockedByOtherThreads()){_SQLiteDatabase= null;return;}
    		}
    		if (_SQLiteDatabase!=null)_SQLiteDatabase.close();
    		_SQLiteDatabase = SQLiteDatabase.openDatabase(_DatabaseName, null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    		
    	}
    	catch(SQLiteException e)
    	{
    		String aa = e.getMessage();
    	}
    }

//    /// <summary>
//    /// �������ݿ�
//    /// </summary>
//    /// <param name="DatabaseName"></param>
//    /// <returns></returns>
//    private boolean CreateDatabase(String DatabaseName)
//    {
//        //�����ֶ�ģ���
//        String CreateSatSQL = "CREATE TABLE g_Sat (Name varchar(50) PRIMARY KEY NOT NULL, TGEO Blob)";
//        String CreateTerSQL = "CREATE TABLE g_Ter (Name varchar(50) PRIMARY KEY NOT NULL, TGEO Blob)";
//        try
//        {
//        	this._SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DatabaseName), MODE_PRIVATE,null);
//        	//this._SQLiteDatabase = SQLiteDatabase.openDatabase(_DatabaseName, null,SQLiteDatabase.CREATE_IF_NECESSARY);
//	        this._SQLiteDatabase.execSQL(CreateSatSQL);
//	        this._SQLiteDatabase.execSQL(CreateTerSQL);
//	        return true;
//        }
//		catch(SQLiteException e)
//		{
//			return false;
//		}
//    }

    /// <summary>
    /// ɾ��ָ���ı�
    /// </summary>
    /// <param name="TableName"></param>
    /// <returns></returns>
//    public bool DropTable(string TableName)
//    {
//        bool IfDelete = false;
//        string SQL = "SELECT Count(*) FROM sqlite_master where type='table' and name='" + TableName + "'";
//        using (SQLiteDataReader DR = this.Query(SQL))
//        {
//            if (DR.Read())
//            {
//                if (DR.GetInt32(0) > 0) IfDelete = true;
//            }
//        }
//        if (IfDelete)
//        {
//            this.ExecuteSQL("drop table " + TableName);
//            this.ExecuteSQL("delete from TableStruct where TableName='" + TableName+"'");
//        }
//        return true;
//    }

    /// <summary>
    /// ���ؼ�¼��
    /// </summary>
    /// <param name="SQL"></param>
    /// <returns></returns>
    public SQLiteDataReader Query(String SQL)
    {
    	if (_SQLiteDatabase==null) return null;
    	if (_SQLiteDatabase.isDbLockedByOtherThreads()) return null;
    	try
    	{
	    	Cursor CR =  _SQLiteDatabase.rawQuery(SQL, null);
	    	return new SQLiteDataReader(CR);
    	}
    	catch(SQLiteException e)
    	{
    		return null;
    	}
    }

//    /// <summary>
//    /// ִ��SQL���
//    /// </summary>
//    /// <param name="SQL"></param>
//    /// <returns></returns>
//    public bool ExecuteSQL(string SQL)
//    {
//        try
//        {
//            using (SQLiteCommand CeDC = new SQLiteCommand(SQL, this.SQLiteConnection))
//            {
//                CeDC.ExecuteNonQuery();
//                return true;
//            }
//        }
//        catch (Exception e)
//        {
//            MessageBox.Show("���ݸ���ʧ�ܣ�\rԭ��:" + e.Message, "ϵͳ", MessageBoxButtons.OK, MessageBoxIcon.Exclamation, MessageBoxDefaultButton.Button1);
//            return false;
//        }
//    }
//
//
    //����ͼƬ�����ݿ���
    public boolean InsertImage(String TableName, String Name, byte[] ImageByte)
    {
        try
        {
        	if (_SQLiteDatabase==null) return false;
            //����ͼ��ʵ��
            Object[] value = new Object[]{ImageByte};
//            String SQL = "insert into "+TableName+" (SYS_STATUS,SYS_GEO,SYS_TYPE,SYS_DATE,SYS_OID) values (0,?,'%1$s','%2$s','%3$s')";
//            SQL = String.format(SQL, this.SYS_TYPE,PubVar.SaveDataDate,this.SYS_OID);

            String InsertSQL = "insert into %1$s (Name,TGEO) values ('%2$s',?)";
            InsertSQL = String.format(InsertSQL,TableName,Name);
            _SQLiteDatabase.execSQL(InsertSQL, value);
            return true;
        }
    	catch(SQLiteException e)
    	{
    		return false;
    	}
    }

}
