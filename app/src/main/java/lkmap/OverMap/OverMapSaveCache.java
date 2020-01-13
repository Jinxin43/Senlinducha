package lkmap.OverMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OverMapSaveCache  implements Runnable
{
	OverMapSQLiteDataBase _CSQLiteDatabase = null;
	
	//���û����ļ�·��
	private String _CacheFilePath = "";
	public void SetCacheFilePath(String chFilePath)
	{
		this._CacheFilePath = chFilePath;
	}
	
	//���滺���ļ������ݿ�,CacheFileNameȫ·��"/X@Y@Z@����.png
	private boolean Save(String CacheFileName)
	{
		if (this._CSQLiteDatabase==null)this._CSQLiteDatabase=new OverMapSQLiteDataBase();
		//���ݿ�����
		String dbFilePath = this._CacheFilePath;//lkmap.Tools.Tools.GetFilePath(CacheFileName);
		String dbFileName = lkmap.Tools.Tools.GetFileName_NoEx(CacheFileName);
		String[] FileInfo = dbFileName.split("@");
		_CSQLiteDatabase.setDatabaseName(dbFilePath+"/MapBase"+FileInfo[2]+".dbx");
		return _CSQLiteDatabase.InsertImage(FileInfo[3], FileInfo[0]+"@"+FileInfo[1]+"@"+FileInfo[2], lkmap.Tools.Tools.readStream(CacheFileName));
	}
	


	boolean _Saving = false;
	public void StartSave()
	{
		if (this._Saving) return;
		//�õ���Ҫ���Ļ����ļ�
		this._Saving= true;
		List<String> _CahFileList = this.getCacheList(10);
		for(String chFileName :_CahFileList)
		{
			if (this.Save(chFileName))
			{
				File f=new File(chFileName);
				f.delete();
			}
		}
		this._Saving= false;
	}
	
	@Override
	public void run() 
	{
		this.StartSave();
	}
	
   //��ȡ��ǰ�Ļ����ļ��б�  
   private List<String> getCacheList(int MaxFileCount)    
   {    
       /* �趨Ŀǰ����·�� */    
       List<String> it=new ArrayList<String>(); 

       File f=new File(this._CacheFilePath);
       if (!f.exists())f.mkdir();
       File[] files=f.listFiles();
 
       /* �������ļ�����ArrayList�� */    
       for(int i=0;i<files.length;i++)    
       {    
         File file=files[i];
         String fName = file.getPath();
         if (fName.indexOf("@")>=0)
         {
	         String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase(); 
	         if(end.equals("png"))
	         {   
	           it.add(fName);
	           if (MaxFileCount>0)
	           {
	        	   if (it.size()>=MaxFileCount) return it;
	           }
	         }
         }
       }
       return it;    
   }  
}
