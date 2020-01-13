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
    	
    	//��ȡVMX�����ͼ����Ϣ
    	_dbVMX = new ASQLiteDatabase();
    	_dbVMX.setDatabaseName(vmxFileName);
    	
    	String SQL = "select * from T_Layer order by Id";
    	SQLiteDataReader DR = _dbVMX.Query(SQL);
    	if (DR==null) return vlayerList;
    	while(DR.Read())
    	{
            //����ͼ��
            v1_Layer newLayer = new v1_Layer();
            newLayer.SetEditMode(lkEditMode.enNew);
            
            newLayer.Tag = DR.GetString("LayerId");
    		newLayer.SetLayerTypeName(DR.GetString("Type"));
    		newLayer.SetLayerAliasName(DR.GetString("Name"));
    		newLayer.SetVisible(Boolean.parseBoolean(DR.GetString("Visible")));					//�ɼ���
    		newLayer.SetTransparent(Integer.parseInt(DR.GetString("Transparent")));  			//͸���ȣ���㣩
    		newLayer.SetVisibleScaleMin(Double.parseDouble(DR.GetString("VisibleScaleMin")));  	//��С�ɼ�����
    		newLayer.SetVisibleScaleMax(Double.parseDouble(DR.GetString("VisibleScaleMax")));  	//���ɼ�����    
    		newLayer.SetFieldList(DR.GetString("FieldList"));									//�ֶ��б�
    		newLayer.SetIfLabel(Boolean.parseBoolean(DR.GetString("IfLabel")));					//�Ƿ��ע
    		newLayer.SetLabelDataField(DR.GetString("LabelField"));								//��ע�ֶ�
    		newLayer.SetLabelFont(DR.GetString("LabelFont"));									//��ע��ʽ
    		
    		newLayer.SetMinX(Double.parseDouble(DR.GetString("MinX")));							//��Ӿ���
    		newLayer.SetMinY(Double.parseDouble(DR.GetString("MinY")));
    		newLayer.SetMaxX(Double.parseDouble(DR.GetString("MaxX")));
    		newLayer.SetMaxY(Double.parseDouble(DR.GetString("MaxY")));
    		
			String renderType = DR.GetString("RenderType");               					//��Ⱦ����1-��ֵ��2-��ֵ
			if (renderType.equals("2"))  //��ֵ����
			{
				newLayer.SetRenderType(lkRenderType.enUniqueValue);
				String UVF = DR.GetString("UniqueValueField");
				String UVL = DR.GetString("UniqueValueList");
				String USL = DR.GetString("UniqueSymbolList");
				newLayer.GetUniqueSymbolInfoList().put("UniqueValueField",Tools.JSONStrToList(UVF));  			//Ψһֵ�ֶ�
				newLayer.GetUniqueSymbolInfoList().put("UniqueValueList",Tools.JSONStrToList(UVL));				//Ψһֵ�б�
				newLayer.GetUniqueSymbolInfoList().put("UniqueSymbolList", Tools.JSONStrToList(USL));  		 	//Ψһֵ�����б�
				newLayer.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", DR.GetString("UniqueDefaultSymbol"));    	//Ψһֵȱʡ����
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
    		process.setTitle("����VMX�ļ�");
    		process.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    		
    		process.setIcon(R.drawable.v1_messageinfo);
    		process.setCancelable(false);
    		process.setCanceledOnTouchOutside(false);
    		process.show();
    		process.onStart();
    		
    		 new Thread() {       
                 public void run() { 
    	    	    {
    	    	    	 //����VMX����
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
        			info.add("���ڵ���VMX�ļ���0/"+count);
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
	        			info.add("���ڵ�����������:"+iCount+"/"+count);
	        			cb.OnClick("", info);
	        			 
	        			Log.d("","����������������["+iCount+"]");
	        		}
	        		
	        		drData.Close();
	               
	        	}
        	}
        	
        	iCount = 0;
        	//������������
        	String SQL = "select * from "+vmxLayerId+"_I";
        	SQLiteDataReader DRI = _dbVMX.Query(SQL);
        	if (DRI!=null)
        	{
        		
        		List<String> FieldNameList = null;
        		while(DRI.Read())
        		{
        			
        			//��ȡ�����ֶ�
        			if (FieldNameList==null)
    				{
        				FieldNameList = new ArrayList<String>();
        				String[] FNList = DRI.GetFieldNameList();
        				for(String FN:FNList)FieldNameList.add(FN);
    				}
        			
        			//��ȡֵ��
        			List<String> valueList = new ArrayList<String>();
        			for(String FieldName:FieldNameList)valueList.add(DRI.GetString(FieldName));

        			//��������
                    String SQL_I = "insert into "+ pDataset.getIndexTableName()+" "+ "(%1$s) values ('%2$s')";
                    SQL_I = String.format(SQL_I,Tools.JoinT(",", FieldNameList),Tools.JoinT("','", valueList));
					//pDataset.getDataSource().ExcuteSQL(SQL_I);
					pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().execSQL(SQL_I);
					Log.d("","��������ͼ������["+iCount+"]");
					iCount++;
					
					ArrayList<String> info = new ArrayList<String>();
        			info.add(count+"");
        			info.add(iCount+"");
        			info.add("���ڵ���ͼ������:"+iCount+"/"+count);
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
        	Tools.ShowMessageBox("VMX���ݵ���ʧ�ܣ�\r\nԭ��"+ex.getMessage());
    	}
    	
    }

    /**
     * �������ݵ����ݼ�
     * @param _dbVMX
     * @param vmxLayerId
     * @param pDataset
     */
    private void ReadVMX(ASQLiteDatabase _dbVMX,String vmxLayerId,Dataset pDataset)
    {
        pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().beginTransaction();
        try
        {
        	
//        	//��ȡ�ֶ�
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
//        	//�����µĲ�ѯ����
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
	        				Tools.ShowMessageBox("VMXͼ�����Ե���ʧ�ܣ�\r\nSys_ID��"+drData.GetString("SYS_ID"));
	        			}
	//        			String[] aa = drData.GetFieldNameList();
	//        			String FieldValue = "'"+DR.GetString(1)+"'";
	//        			//��ȡͼ��
	//        			byte[] GeoByte = DR.GetBlob("SYS_GEO");
	        			
	        			iCount++;
	        			Log.d("","��������ͼ������["+iCount+"]"+Offset);
	
	//                   String SQL_D = "insert into "+ pDataset.getDataTableName()+" "+ "(SYS_GEO,%1$s) values (?,%2$s)";
	//					SQL_D = String.format(SQL_D,FieldNameListStr,FieldValue);
	//					pDataset.getDataSource().ExcuteSQL(SQL_D, new Object[]{GeoByte});
	
	        			
	//        			//��������
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
	//						Log.d("","��������ͼ������[�ύһ��]");
	//					}
	        		}drData.Close();
	        		
	            	
	        		
	                pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().setTransactionSuccessful();
	                pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
	                pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().beginTransaction();
	                System.gc();
	        	}
        	}
        	
//        	//������������
//        	SQL = "select * from "+vmxLayerId+"_I";
//        	SQLiteDataReader DRI = _dbVMX.Query(SQL);
//        	if (DRI!=null)
//        	{
//        		int iCount = 1;
//        		List<String> FieldNameList = null;
//        		while(DRI.Read())
//        		{
//        			
//        			//��ȡ�����ֶ�
//        			if (FieldNameList==null)
//    				{
//        				FieldNameList = new ArrayList<String>();
//        				String[] FNList = DRI.GetFieldNameList();
//        				for(String FN:FNList)FieldNameList.add(FN);
//    				}
//        			
//        			//��ȡֵ��
//        			List<String> valueList = new ArrayList<String>();
//        			for(String FieldName:FieldNameList)valueList.add(DRI.GetString(FieldName));
//
//        			//��������
//                    String SQL_I = "insert into "+ pDataset.getIndexTableName()+" "+ "(%1$s) values ('%2$s')";
//                    SQL_I = String.format(SQL_I,Tools.JoinT(",", FieldNameList),Tools.JoinT("','", valueList));
//					//pDataset.getDataSource().ExcuteSQL(SQL_I);
//					pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().execSQL(SQL_I);
//					Log.d("","��������ͼ������["+iCount+"]");iCount++;
//        		}
//        	}
            
            pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().setTransactionSuccessful();
            pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
        }
        catch(Exception e)
        {
        	pDataset.getDataSource().GetSQLiteDatabase().GetSQLiteDatabase().endTransaction();
        	Tools.ShowMessageBox("VMX���ݵ���ʧ�ܣ�\r\nԭ��"+e.getMessage());
        }

    }


}
