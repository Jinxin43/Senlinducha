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

//��Ӧ���ݱ�T_TransformationParam
public class v1_UserConfigDB_TransformationParam 
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
	 * ��ȡת�������б�
	 * @param ParamType �������ͣ����Σ��߲Σ��Ĳ�
	 * @return
	 */
	public List<HashMap<String,Object>> GetTransformationParamList(String ParamType)
	{
		if (ParamType.equals(""))ParamType="1=1";else ParamType = "F1='"+ParamType+"'";
		List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();

		String SQL = "select * from T_TransformationParam where %1$s order by ID DESC";
		SQL = String.format(SQL, ParamType);
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
		if (DR==null)return dataList;
		while (DR.Read())
		{
			HashMap<String,Object> coorParaObj = new HashMap<String,Object>();
			
			coorParaObj.put("ID",DR.GetString("ID"));  //ID
			coorParaObj.put("Type",DR.GetString("F1"));  //���ͣ����Σ��߲Σ��Ĳ�
			coorParaObj.put("DH",DR.GetString("F2"));  //����˵��
			coorParaObj.put("P1",DR.GetString("F3"));  //����1
			coorParaObj.put("P2",DR.GetString("F4"));  //����2
			coorParaObj.put("P3",DR.GetString("F5"));  //����3
			coorParaObj.put("P4",DR.GetString("F6"));  //����4
			coorParaObj.put("P5",DR.GetString("F7"));  //����5
			coorParaObj.put("P6",DR.GetString("F8"));  //����6
			coorParaObj.put("P7",DR.GetString("F9"));  //����7
			
		    dataList.add(coorParaObj);
		}DR.Close();

		return dataList;
	}
	
	/**
	 * ����ת������
	 * @param param [ID],[DH],[P1...pn]
	 * @param OverWrite
	 * @return
	 */
	public String SaveTransformationParam(HashMap<String,Object> param)
	{
		//1���ж��Ƿ���ָ��ID�Ĳ���
		String SQL = "";
		if (param.containsKey("ID"))
		{
			SQL = "select COUNT(*) as count from T_TransformationParam where ID ='"+param.get("ID").toString()+"'";
			SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
			int Count = 0;
			if (DR!=null)if(DR.Read())Count = Integer.parseInt(DR.GetString("count"));DR.Close();
			if (Count>0)  //�������
			{
				SQL = "delete from T_TransformationParam where ID ='"+param.get("ID").toString()+"'";
				if (!this.m_SQLiteDatabase.ExcuteSQL(SQL)) return "���²���ʧ�ܣ�";
			}
		}
		
        SQL = "insert into T_TransformationParam (F1,F2,F3,F4,F5,F6,F7,F8,F9) values ('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s','%8$s','%9$s')";
        SQL = String.format(SQL,param.get("Type").toString(),
				        		param.get("DH").toString(),
				        		param.get("P1").toString(),
				        		param.get("P2").toString(),
				        		param.get("P3").toString(),
				        		param.get("P4").toString(),
				        		param.get("P5").toString(),
				        		param.get("P6").toString(),
				        		param.get("P7").toString());
        if (this.m_SQLiteDatabase.ExcuteSQL(SQL)) return "OK";else return "��������ʧ�ܣ�";
	}
	
	/**
	 * ɾ��ָ��ID��ת������
	 * @param ID
	 * @return
	 */
	public boolean DeleteTransformationParam(String ID)
	{
		//1���ж��Ƿ���ָ��ID�Ĳ���
		String SQL = "delete from T_TransformationParam where ID ='"+ID+"'";
		return this.m_SQLiteDatabase.ExcuteSQL(SQL);
	}
}