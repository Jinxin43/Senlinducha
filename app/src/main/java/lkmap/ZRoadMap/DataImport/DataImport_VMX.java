package lkmap.ZRoadMap.DataImport;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.DTGIS.Control.ProcessDialog;
import com.dingtu.senlinducha.R;

import android.R.string;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkRenderType;
import lkmap.Index.T4Index;
import lkmap.Map.StaticObject;
import lkmap.MapControl.Select;
import lkmap.Symbol.LineSymbol;
import lkmap.Symbol.PointSymbol;
import lkmap.Symbol.PolySymbol;
import lkmap.Tools.BitConverter;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;
import lkmap.ZRoadMap.Project.v1_project_layer_ex;

public class DataImport_VMX 
{
	private ASQLiteDatabase _dbVMX = null;
	private ProgressDialog process = null;
	
    public List<v1_Layer> CreateLayerByVMX(String vmxFileName)
    {

    	final List<v1_Layer> vlayerList = new ArrayList<v1_Layer>();
    	
    	//提取VMX里面的图层信息
    	_dbVMX = new ASQLiteDatabase();
    	_dbVMX.setDatabaseName(vmxFileName);
    	
    	String SQL = "select * from T_Layer order by Id";
    	SQLiteDataReader DR = _dbVMX.Query(SQL);
    	if (DR==null) return vlayerList;
    	while(DR.Read())
    	{
            //创建图层
            v1_Layer newLayer = new v1_Layer();
            newLayer.SetEditMode(lkEditMode.enNew);
            
            newLayer.Tag = DR.GetString("LayerId");
    		newLayer.SetLayerTypeName(DR.GetString("Type"));
    		newLayer.SetLayerAliasName(DR.GetString("Name"));
    		newLayer.SetVisible(Boolean.parseBoolean(DR.GetString("Visible")));					//可见性
    		newLayer.SetTransparent(Integer.parseInt(DR.GetString("Transparent")));  			//透明度（面层）
    		newLayer.SetVisibleScaleMin(Double.parseDouble(DR.GetString("VisibleScaleMin")));  	//最小可见比例
    		newLayer.SetVisibleScaleMax(Double.parseDouble(DR.GetString("VisibleScaleMax")));  	//最大可见比例    
    		newLayer.SetFieldList(DR.GetString("FieldList"));									//字段列表
    		newLayer.SetIfLabel(Boolean.parseBoolean(DR.GetString("IfLabel")));					//是否标注
    		newLayer.SetLabelDataField(DR.GetString("LabelField"));								//标注字段
    		newLayer.SetLabelFont(DR.GetString("LabelFont"));									//标注样式
    		
    		newLayer.SetMinX(Double.parseDouble(DR.GetString("MinX")));							//外接矩形
    		newLayer.SetMinY(Double.parseDouble(DR.GetString("MinY")));
    		newLayer.SetMaxX(Double.parseDouble(DR.GetString("MaxX")));
    		newLayer.SetMaxY(Double.parseDouble(DR.GetString("MaxY")));
    		
			String renderType = DR.GetString("RenderType");               					//渲染类型1-单值，2-多值
			if (renderType.equals("2"))  //多值符号
			{
				newLayer.SetRenderType(lkRenderType.enUniqueValue);
				String UVF = DR.GetString("UniqueValueField");
				String UVL = DR.GetString("UniqueValueList");
				String USL = DR.GetString("UniqueSymbolList");
				newLayer.GetUniqueSymbolInfoList().put("UniqueValueField",Tools.JSONStrToList(UVF));  			//唯一值字段
				newLayer.GetUniqueSymbolInfoList().put("UniqueValueList",Tools.JSONStrToList(UVL));				//唯一值列表
				newLayer.GetUniqueSymbolInfoList().put("UniqueSymbolList", Tools.JSONStrToList(USL));  		 	//唯一值符号列表
				newLayer.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", DR.GetString("UniqueDefaultSymbol"));    	//唯一值缺省符号
			} else newLayer.SetSimpleSymbol(DR.GetString("SimpleRender"));

            if (v1_project_layer_ex.CreateOrUpdateLayer(newLayer))
            {
            	PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList().add(newLayer);
            	vlayerList.add(newLayer);
            }
    	}
    	
    	DR.Close();
    	
    	
    	try
    	{
    		process = new ProgressDialog(PubVar.m_DoEvent.m_Context,ProgressDialog.THEME_HOLO_LIGHT);
    		process.setTitle("导入VMX文件");
    		process.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    		
    		process.setIcon(R.drawable.v1_messageinfo);
    		process.setCancelable(false);
    		process.setCanceledOnTouchOutside(false);
    		process.show();
    		process.onStart();
    		
    		 new Thread() {       
                 public void run() { 
    	    	    {
    	    	    	 //导入VMX数据
    	    	    	for(v1_Layer pLayer:vlayerList)
    	    	    	{
    	    	    		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(pLayer.GetLayerID());
    	    	    		ImportVMXData(pLayer.Tag, pDataset, pCallback);
    	    	            //process.dismiss();
    	    	    	}
    	    	    	process.cancel();
    	    	    }}}.start();
    	}
    	catch(Exception e)
    	{
    		Tools.ShowMessageBox(e.getMessage());
    	}
		
       
        
        
       
    	return vlayerList;
    }
    
    private ICallback pCallback = new ICallback() {
		
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			
			List<String> info = (List<String>)ExtraStr;
			process.setMax(Integer.valueOf(info.get(0)));
			process.setProgress(Integer.valueOf(info.get(1)));
			process.setMessage(info.get(2));
		}
	};
    
    public void ImportVMXData(String vmxLayerId,Dataset pDataset,ICallback cb)
    {
    	//final ProgressDialog progressDialog  = process ;
    	pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().beginTransaction();
    	try
        {
    		int count =0;
        	boolean OK = false;
        	int ReadCount = 10000;
        	int Offset = 0;
        	String sumSql = "select * from "+vmxLayerId+"_D";
        	SQLiteDataReader drSum = _dbVMX.Query(sumSql);
        	if(drSum != null)
        	{
        		count = drSum.GetCount();
        		if(count>0)
        		{
        			ArrayList<String> info = new ArrayList<String>();
        			info.add(count+"");
        			info.add("0");
        			info.add("正在导入VMX文件：0/"+count);
        			cb.OnClick("", info);
        		}
        	}
        	
        	
        	int iCount = 0;
        	while(!OK)
        	{
	        	String SQL = "select * from "+vmxLayerId+"_D limit "+ReadCount+" offset "+Offset;
	        	Offset+=10000;
	        	SQLiteDataReader drData = _dbVMX.Query(SQL);
	        	drData.GetCount();
	        	
	        	if (drData==null)
	        	{
	        		OK=true;	
	        	}
	        	else
	        	{
	        		if(drData.GetCount()<10000)
	        		{
	        			OK=true;
	        		}
	        		
	        		List<String> FieldNameList = null;
	        		while(drData.Read())
	        		{
	        			if (FieldNameList==null)
	        			{
	        				FieldNameList = new ArrayList<String>();
		    				String[] FNList = drData.GetFieldNameList();
		    				for(String FN:FNList)
							{
		    					if (FN.equals("SYS_ID") || FN.equals("SYS_GEO"))continue;
		    					FieldNameList.add(FN);
							}
	        			}
	        			
	        			ContentValues cv = new ContentValues();
	        			byte[] GeoByte = drData.GetBlob("SYS_GEO");
	        			cv.put("SYS_GEO", GeoByte);
	        			
	        			for(String FN:FieldNameList)cv.put(FN, drData.GetString(FN));
	
	        			long result = pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().insert(pDataset.getDataTableName(), null, cv);
	        			iCount++ ;
	        			
	        			ArrayList<String> info = new ArrayList<String>();
	        			info.add(count+"");
	        			info.add(iCount+"");
	        			info.add("正在导入属性数据:"+iCount+"/"+count);
	        			cb.OnClick("", info);
	        			 
	        			Log.d("","正在新增属性数据["+iCount+"]");
	        		}
	        		
	        		drData.Close();
	               
	        	}
        	}
        	
        	iCount = 0;
        	//导入索引数据
        	String SQL = "select * from "+vmxLayerId+"_I";
        	SQLiteDataReader DRI = _dbVMX.Query(SQL);
        	if (DRI!=null)
        	{
        		
        		List<String> FieldNameList = null;
        		while(DRI.Read())
        		{
        			
        			//提取数据字段
        			if (FieldNameList==null)
    				{
        				FieldNameList = new ArrayList<String>();
        				String[] FNList = DRI.GetFieldNameList();
        				for(String FN:FNList)FieldNameList.add(FN);
    				}
        			
        			//提取值域
        			List<String> valueList = new ArrayList<String>();
        			for(String FieldName:FieldNameList)valueList.add(DRI.GetString(FieldName));

        			//插入数据
                    String SQL_I = "insert into "+ pDataset.getIndexTableName()+" "+ "(%1$s) values ('%2$s')";
                    SQL_I = String.format(SQL_I,Tools.JoinT(",", FieldNameList),Tools.JoinT("','", valueList));
					//pDataset.getDataSource().ExcuteSQL(SQL_I);
					pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().execSQL(SQL_I);
					Log.d("","正在新增图形数据["+iCount+"]");
					iCount++;
					
					ArrayList<String> info = new ArrayList<String>();
        			info.add(count+"");
        			info.add(iCount+"");
        			info.add("正在导入图形数据:"+iCount+"/"+count);
        			cb.OnClick("", info);
        		
        			
        		}
        		
        		DRI.Close();
        	}
        	
        	//process.cancel();
        	pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().setTransactionSuccessful();
            pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
        }
    	catch(Exception ex)
    	{
    		pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
        	Tools.ShowMessageBox("VMX数据导入失败！\r\n原因："+ex.getMessage());
    	}
    	
    }

    /**
     * 导入数据到数据集
     * @param _dbVMX
     * @param vmxLayerId
     * @param pDataset
     */
    private void ReadVMX(ASQLiteDatabase _dbVMX,String vmxLayerId,Dataset pDataset)
    {
        pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().beginTransaction();
        try
        {
        	
//        	//读取字段
//        	List<String> FieldNameList = new ArrayList<String>();
//        	List<String> FieldNameListSQL = new ArrayList<String>();
//        	String FieldNameListStr = "";
//        	String SQL = "select * from "+vmxLayerId+"_D limit 1";
//        	SQLiteDataReader DR = _dbVMX.Query(SQL);
//        	if (DR!=null)
//        	{
//        		while(DR.Read())
//        		{
//    				String[] FNList = DR.GetFieldNameList();
//    				for(String FN:FNList)
//					{
//    					if (FN.equals("SYS_ID") || FN.equals("SYS_GEO"))continue;
//    					FieldNameList.add(FN);
//    					FieldNameListSQL.add(String.format("case when %1$s is NULL then '' else %1$s end", FN));
//					}
//    				FieldNameListStr = Tools.JoinT(",", FieldNameList);
//        		}DR.Close();
//        	}
//        	
//        	//构造新的查询条件
//        	String SQLT = "select SYS_GEO,(%1$s) as FieldValue from "+vmxLayerId+"_D";
//        	SQLT = String.format(SQLT, Tools.JoinT("||\"','\"||", FieldNameListSQL));
        	
        	boolean OK = false;
        	int ReadCount = 10000;
        	int Offset = 0;
        	while(!OK)
        	{
	        	String SQL = "select * from "+vmxLayerId+"_D limit "+ReadCount+" offset "+Offset;
	        	Offset+=10000;
	        	SQLiteDataReader drData = _dbVMX.Query(SQL);
	        	if (drData==null)OK=true;
	        	else
	        	{
	        		int iCount = 1;
	        		List<String> FieldNameList = null;
	        		while(drData.Read())
	        		{
	        			if (FieldNameList==null)
	        			{
	        				FieldNameList = new ArrayList<String>();
		    				String[] FNList = drData.GetFieldNameList();
		    				for(String FN:FNList)
							{
		    					if (FN.equals("SYS_ID") || FN.equals("SYS_GEO"))continue;
		    					FieldNameList.add(FN);
							}
	        			}
	        			
	        			ContentValues cv = new ContentValues();
	        			byte[] GeoByte = drData.GetBlob("SYS_GEO");
	        			cv.put("SYS_GEO", GeoByte);
	        			
	        			for(String FN:FieldNameList)cv.put(FN, drData.GetString(FN));
	
	        			long result = pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().insert(pDataset.getDataTableName(), null, cv);
	        			if(result<0)
	        			{
	        				Tools.ShowMessageBox("VMX图层属性导入失败！\r\nSys_ID："+drData.GetString("SYS_ID"));
	        			}
	//        			String[] aa = drData.GetFieldNameList();
	//        			String FieldValue = "'"+DR.GetString(1)+"'";
	//        			//提取图形
	//        			byte[] GeoByte = DR.GetBlob("SYS_GEO");
	        			
	        			iCount++;
	        			Log.d("","正在新增图形数据["+iCount+"]"+Offset);
	
	//                   String SQL_D = "insert into "+ pDataset.getDataTableName()+" "+ "(SYS_GEO,%1$s) values (?,%2$s)";
	//					SQL_D = String.format(SQL_D,FieldNameListStr,FieldValue);
	//					pDataset.getDataSource().ExcuteSQL(SQL_D, new Object[]{GeoByte});
	
	        			
	//        			//插入数据
	//                    String SQL_D = "insert into "+ pDataset.getDataTableName()+" "+ "(SYS_GEO,%1$s) values (?,'%2$s')";
	//					SQL_D = String.format(SQL_D,Tools.JoinT(",", FieldNameList),Tools.JoinT("','", valueList));
	//					//pDataset.getDataSource().ExcuteSQL(SQL_D, new Object[]{GeoByte});
	//					pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().execSQL(SQL_D, new Object[]{GeoByte});
	//					iCount++;
	//					if (iCount==5000)
	//					{
	//			            pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().setTransactionSuccessful();
	//			            pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
	//						pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().beginTransaction();
	//						iCount=0;
	//						Log.d("","正在新增图形数据[提交一次]");
	//					}
	        		}drData.Close();
	        		
	            	
	        		
	                pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().setTransactionSuccessful();
	                pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
	                pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().beginTransaction();
	                System.gc();
	        	}
        	}
        	
//        	//导入索引数据
//        	SQL = "select * from "+vmxLayerId+"_I";
//        	SQLiteDataReader DRI = _dbVMX.Query(SQL);
//        	if (DRI!=null)
//        	{
//        		int iCount = 1;
//        		List<String> FieldNameList = null;
//        		while(DRI.Read())
//        		{
//        			
//        			//提取数据字段
//        			if (FieldNameList==null)
//    				{
//        				FieldNameList = new ArrayList<String>();
//        				String[] FNList = DRI.GetFieldNameList();
//        				for(String FN:FNList)FieldNameList.add(FN);
//    				}
//        			
//        			//提取值域
//        			List<String> valueList = new ArrayList<String>();
//        			for(String FieldName:FieldNameList)valueList.add(DRI.GetString(FieldName));
//
//        			//插入数据
//                    String SQL_I = "insert into "+ pDataset.getIndexTableName()+" "+ "(%1$s) values ('%2$s')";
//                    SQL_I = String.format(SQL_I,Tools.JoinT(",", FieldNameList),Tools.JoinT("','", valueList));
//					//pDataset.getDataSource().ExcuteSQL(SQL_I);
//					pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().execSQL(SQL_I);
//					Log.d("","正在新增图形数据["+iCount+"]");iCount++;
//        		}
//        	}
            
            pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().setTransactionSuccessful();
            pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
        }
        catch(Exception e)
        {
        	pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
        	Tools.ShowMessageBox("VMX数据导入失败！\r\n原因："+e.getMessage());
        }

    }


}
