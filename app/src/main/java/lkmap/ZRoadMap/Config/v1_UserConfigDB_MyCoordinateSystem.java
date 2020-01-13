package lkmap.ZRoadMap.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

//��Ӧ���ݱ�T_MyCoordinateSystem
public class v1_UserConfigDB_MyCoordinateSystem 
{
	//���ݿ������
	private ASQLiteDatabase m_SQLiteDatabase = null;
	
	/**
	 * �����ݿ������
	 * @param _db
	 */
	public void SetBindDB(ASQLiteDatabase _db)
	{
		this.m_SQLiteDatabase = _db;
	}
	
	/**
	 * ��ȡ�ҵ�����ϵ�б�
	 * @return
	 */
	public List<HashMap<String,Object>> GetMyCoordinateSystemList()
	{
		List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
		try
		{
			String SQL = "select * from T_MyCoordinateSystem order by ID DESC";
			SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
			if (DR==null)return dataList;
			while (DR.Read())
			{
				HashMap<String,Object> coorParaObj = new HashMap<String,Object>();
				coorParaObj.put("ID", DR.GetString("ID"));
				coorParaObj.put("D1",false);  //�Ƿ�ѡ��
				coorParaObj.put("D2",DR.GetString("Name"));  //����
				String josnObjectStr = new String(DR.GetBlob("Para"));
			    JSONTokener jsonParser = new JSONTokener(josnObjectStr);  
			    JSONObject ParaJSON = (JSONObject)jsonParser.nextValue();   
			    coorParaObj.put("D3", ParaJSON.getString("CoorSystem"));  //����ϵͳ
			    coorParaObj.put("D4", ParaJSON.getString("CenterJX"));   //���뾭��
			    coorParaObj.put("D5", ParaJSON.getString("TransMethod"));   //����ת������
			    
			    coorParaObj.put("CoorSystem", coorParaObj.get("D3"));  //����ϵͳ
			    coorParaObj.put("CenterJX", coorParaObj.get("D4"));  //���뾭��
			    coorParaObj.put("TransMethod", coorParaObj.get("D5"));  //ת������
			    
			    if (ParaJSON.has("PMTransMethod"))
			    	coorParaObj.put("PMTransMethod", ParaJSON.getString("PMTransMethod"));
			    else coorParaObj.put("PMTransMethod", "��");
			    
			    String[] keys = new String[]{"P31","P32","P33","P34","P35","P41","P42","P43","P44","P71","P72","P73","P74","P75","P76","P77"};
			    for(String key:keys)
			    {
			    	if (ParaJSON.has(key))coorParaObj.put(key, ParaJSON.getString(key));   //ת������
			    	else coorParaObj.put(key, 0);
			    }

			    dataList.add(coorParaObj);
			}DR.Close();
		}
		catch (JSONException ex) 
		{  
		    throw new RuntimeException(ex);  
		} 
		return dataList;
	}
	
	/**
	 * �����µġ��ҵ�����ϵ��
	 * @param Name
	 * @param coorSystem
	 * @return
	 */
	public String SaveMyCoordinateSystem(String Name,HashMap<String,String> coorSystem)
	{
		try
		{
			JSONObject coorParaObj = new JSONObject();  
			coorParaObj.put("CoorSystem", coorSystem.get("CoorSystem"));  
			coorParaObj.put("CenterJX", coorSystem.get("CenterJX"));  
			coorParaObj.put("TransMethod", coorSystem.get("TransMethod"));  
			coorParaObj.put("PMTransMethod", coorSystem.get("PMTransMethod"));  
		    String[] keys = new String[]{"P31","P32","P33","P34","P35","P41","P42","P43","P44","P71","P72","P73","P74","P75","P76","P77"};
		    for(String key:keys){coorParaObj.put(key, coorSystem.get(key));}

			//2���ж��Ƿ���ָ�����Ƶ�ģ��
			String SQL = "insert into T_MyCoordinateSystem (Name,CreateTime,Para) values ('%1$s','%2$s',?)";
	        SQL = String.format(SQL,Name,Tools.GetSystemDate());
	        Object[] value =new Object[]{coorParaObj.toString().getBytes()};
	        if (this.m_SQLiteDatabase.ExcuteSQL(SQL, value)) return "OK";else return "�����ҵ�����ϵʧ�ܣ�";
		}
		catch (JSONException ex) 
		{  
		    throw new RuntimeException(ex);  
		}
	}
	
	/**
	 * ɾ��ָ��ID������ϵ��¼
	 * @param idList
	 * @return
	 */
	public boolean DeleteMyCoordinateSystem(List<String> idList)
	{
		String SQL = "delete from T_MyCoordinateSystem where ID in ("+Tools.JoinT(",", idList)+")";
        return this.m_SQLiteDatabase.ExcuteSQL(SQL);
	}
}