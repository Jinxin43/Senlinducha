package lkmap.Edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

public class CopyFeature 
{
	/**
	 * ∏¥÷∆ Ù–‘
	 * @param pDataset
	 * @param ObjectId
	 * @return
	 */
	public static HashMap<String,String> CopyFrom(Dataset pDataset,int ObjectId)
	{
		HashMap<String,String> FeatureList = new HashMap<String,String>();
		String SQL = "select * from %1$s where SYS_ID = %2$s";
		SQL = String.format(SQL,pDataset.getDataTableName(), ObjectId);
		SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
		if (DR!=null)if(DR.Read())
		{
			String[] dataFieldList = DR.GetFieldNameList();
			for(String dataField:dataFieldList)
			{
				if (dataField.contains("SYS_") && !dataField.equals("SYS_PHOTO")) continue;
				FeatureList.put(dataField, DR.getUnNullString(dataField));
				
			}
		}
		return FeatureList;
	}
	
	
	public static int getSameIdTuban(Dataset pDataset, String dataFileName, String id) {
		int count = 0;
		String SQL = "select * from " + pDataset.getDataTableName() + " where " + dataFileName + " = " + id
				+ " and SYS_STATUS=0";
		SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
		if (DR != null) {
			if (DR.Read()) {
				count = DR.GetCount();
			}
		}
		return count;
	}
	
	//’≥Ã˘ Ù–‘
	public static boolean CopyTo(HashMap<String,String> FeatureList,Dataset pDataset,int ObjectId)
	{
		List<String> updateFieldList = new ArrayList<String>();
		for(String key:FeatureList.keySet())updateFieldList.add(key+"='"+FeatureList.get(key)+"'");
		String SQL = "update %1$s set %3$s where SYS_ID=%2$s";
		SQL = String.format(SQL,pDataset.getDataTableName(), ObjectId,Tools.JoinT(",", updateFieldList));
		return pDataset.getDataSource().ExcuteSQL(SQL);
	}
	
}
