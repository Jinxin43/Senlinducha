package lkmap.ZRoadMap.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.ZRoadMap.Project.v1_Layer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class v1_UserConfigDB_LayerTemplate 
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
	 * ����ͼ��ģ����Ϣ
	 * @param LayerTemplateInfo
	 * @return
	 */
	public String SaveLayerTemplate(HashMap<String,Object> LayerTemplateInfo)
	{
		//ģ������
		String Name = LayerTemplateInfo.get("Name").toString();
		//����ʱ��
		String CreateTime = LayerTemplateInfo.get("CreateTime").toString();
		//�Ƿ񸲸�
		boolean OverWrite = Boolean.parseBoolean(LayerTemplateInfo.get("OverWrite").toString());
		//ͼ���б�
		List<v1_Layer> vLayerList = (List<v1_Layer>)LayerTemplateInfo.get("LayerList");
		
		//��ͼ���б�ת��ΪJSON
		JSONObject LYRJSON = this.LayerListToJSONObject(vLayerList);
		String LYRStr = LYRJSON.toString();
		
		//����
		//2���ж��Ƿ���ָ�����Ƶ�ģ��
		String SQL = "select COUNT(*) as count from T_LayerTemplate where name ='"+Name+"'";
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
		int Count = 0;
		if (DR!=null)if(DR.Read())Count = Integer.parseInt(DR.GetString("count"));DR.Close();
		if (Count>0)  //�������
		{
			if (!OverWrite)return "�Ѵ���ͬ��ģ�壡";
			SQL = "delete from T_LayerTemplate where name='"+Name+"'";
			if (!this.m_SQLiteDatabase.ExcuteSQL(SQL)) return "����ͬ��ģ��ʧ�ܣ�";
		}
		
        SQL = "insert into T_LayerTemplate (name,createtime,layerlist) values ('%1$s','%2$s',?)";
        SQL = String.format(SQL,Name,CreateTime);
        Object[] value =new Object[]{LYRStr.getBytes()};
        if (this.m_SQLiteDatabase.ExcuteSQL(SQL, value)) return "OK";
        else return "����ģ��ʧ�ܣ�";
	}
	
	/**
	 * ��ȡָ�����Ƶ�ͼ��ģ��
	 * @param TemplateName
	 * @return
	 */
	public List<v1_Layer> ReadLayerTemplate(String TemplateName)
	{
		String SQL = "select * from T_LayerTemplate where name='"+TemplateName+"'";
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
		if (DR==null)return null;
		String layStr = "";
		if (DR.Read())
		{
			byte[] layerlist = DR.GetBlob("layerlist");
			layStr = new String(layerlist);
		}DR.Close();
		
		if (layStr.equals("")) return null;
		return this.JSONObjectToLayerList(layStr);
	}
	
	/**
	 * ��ȡͼ��ģ�������б�
	 * @param TemplateType ϵͳ���û���ȫ��
	 * @return List<ģ�����ơ�����ʱ�䡿
	 */
	public List<String> ReadTemplateList(String TemplateType)
	{
		String whereTemp = "1=1";
		if (TemplateType.equals("ϵͳ"))whereTemp = "name='ϵͳĬ��ͼ��ģ��'";
		if (TemplateType.equals("�û�"))whereTemp = "name<>'ϵͳĬ��ͼ��ģ��'";
		List<String> nameList = new ArrayList<String>();
		String SQL = "select name,createtime from T_LayerTemplate where %1$s order by id desc";
		SQL = String.format(SQL, whereTemp);
		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
		if (DR==null)return nameList;
		while(DR.Read())
		{
			String name = DR.GetString("name");
			String time = DR.GetString("createtime");
			nameList.add(name+"��"+time+"��");
		}DR.Close();
		return nameList;
	}
	
	/**
	 * ɾ��ָ�����Ƶ�ģ��
	 * @param tempName
	 * @return
	 */
	public boolean DeleteTemplateByName(String tempName)
	{
		String SQL = "delete from T_LayerTemplate where name='"+tempName+"'";
		return this.m_SQLiteDatabase.ExcuteSQL(SQL);
	}
	
	/**
	 * ��JSONObjectת��Ϊͼ���б�
	 * @param josnObject
	 * @return
	 */
	private List<v1_Layer> JSONObjectToLayerList(String josnObjectStr)
	{
		List<v1_Layer> vLayerList = new ArrayList<v1_Layer>();
		try 
		{  
		    JSONTokener jsonParser = new JSONTokener(josnObjectStr);  
		    JSONObject AllLayerJSON = (JSONObject)jsonParser.nextValue();  
		    
		    // �������ľ���JSON����Ĳ�����  
		    JSONArray lyrJSONList = AllLayerJSON.getJSONArray("AllLayer");  
		    for(int i=0;i<lyrJSONList.length();i++)
		    {
		    	v1_Layer vLayer = new v1_Layer();
		    	JSONObject lyrJSON = lyrJSONList.getJSONObject(i);
		    	vLayer.SetLayerAliasName(lyrJSON.getString("Name"));
				vLayer.SetLayerID(lyrJSON.getString("LayerId"));
				vLayer.SetLayerTypeName(lyrJSON.getString("Type"));
				vLayer.SetVisible(lyrJSON.getBoolean("Visible"));
				vLayer.SetTransparent(lyrJSON.getInt("Transparent"));
				
				
//				vLayer.SetShowWaterMark(lyrJSON.getBoolean("F2"));
//				vLayer.SetLabelDataField(lyrJSON.getString("F3"));
				
				vLayer.SetIfLabel(lyrJSON.getBoolean("IfLabel"));
				vLayer.SetLabelDataField(lyrJSON.getString("LabelField"));
				vLayer.SetLabelFont(lyrJSON.getString("LabelFont"));
				vLayer.SetLabelScaleMin(lyrJSON.getDouble("LabelScaleMin"));
				vLayer.SetLabelScaleMax(lyrJSON.getDouble("LabelScaleMax"));
				vLayer.SetFieldList(lyrJSON.getString("FieldList"));
				vLayer.SetVisibleScaleMin(lyrJSON.getDouble("VisibleScaleMin"));
				vLayer.SetVisibleScaleMax(lyrJSON.getDouble("VisibleScaleMax"));
				vLayer.SetSelectable(lyrJSON.getBoolean("Selectable"));
				vLayer.SetEditable(lyrJSON.getBoolean("Editable"));
				vLayer.SetSnapable(lyrJSON.getBoolean("Snapable"));
				vLayer.SetRenderTypeInt(lyrJSON.getInt("RenderType"));
				vLayer.SetSimpleSymbol(lyrJSON.getString("SimpleRender"));
				String projectType = lyrJSON.getString("F1");
				if(projectType == null || projectType.isEmpty())
				{
					projectType ="�Զ��幤��";
				}
				vLayer.SetLayerProjectType(lyrJSON.getString("F1"));

				vLayer.GetUniqueSymbolInfoList().put("UniqueValueField", this.JSONArrayToList(lyrJSON.getJSONArray("UniqueValueField")));
				vLayer.GetUniqueSymbolInfoList().put("UniqueValueList", this.JSONArrayToList(lyrJSON.getJSONArray("UniqueValueList")));
				vLayer.GetUniqueSymbolInfoList().put("UniqueSymbolList", this.JSONArrayToList(lyrJSON.getJSONArray("UniqueSymbolList")));
				vLayer.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", lyrJSON.getString("UniqueDefaultSymbol"));
				vLayerList.add(vLayer);
		    }
		} catch (JSONException ex) {  
		   return null;
		}  
		return vLayerList;
	}
	
	/**
	 * ��ͼ���б�ת��ΪJSONObject
	 * @return
	 */
	private JSONObject LayerListToJSONObject(List<v1_Layer> vLayerList)
	{
		try
		{
			JSONObject LyrAllJSON = new JSONObject();  
			JSONArray LyrJSONList = new JSONArray(); 

			for(v1_Layer vLayer:vLayerList)
			{
				JSONObject LyrJSON = new JSONObject();  
				LyrJSON.put("LayerId", vLayer.GetLayerID());  
				LyrJSON.put("Name", vLayer.GetLayerAliasName());  
				LyrJSON.put("Type", vLayer.GetLayerTypeName());
				LyrJSON.put("Visible", vLayer.GetVisible());  
				LyrJSON.put("Transparent", vLayer.GetTransparet());  
				LyrJSON.put("IfLabel", vLayer.GetIfLabel()); 
				LyrJSON.put("LabelField", vLayer.GetLabelFieldStr());  
				LyrJSON.put("LabelFont", vLayer.GetLabelFont());  
				LyrJSON.put("LabelScaleMin", vLayer.GetLabelScaleMin());  
				LyrJSON.put("LabelScaleMax", vLayer.GetLabelScaleMax());  
				LyrJSON.put("FieldList", vLayer.GetFieldListJsonStr()); 
				LyrJSON.put("VisibleScaleMin", vLayer.GetVisibleScaleMin()); 
				LyrJSON.put("VisibleScaleMax", vLayer.GetVisibleScaleMax()); 
				
				LyrJSON.put("Selectable", vLayer.GetSelectable()); 
				LyrJSON.put("Editable", vLayer.GetEditable()); 
				LyrJSON.put("Snapable", vLayer.GetSnapable()); 
				
				LyrJSON.put("RenderType", vLayer.GetRenderTypeInt());
				LyrJSON.put("SimpleRender", vLayer.GetSimpleSymbol()); 
				LyrJSON.put("F1", vLayer.GetLayerProjecType()); 
				LyrJSON.put("UniqueValueField", this.ListToJSONArray((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueValueField"))); 
				LyrJSON.put("UniqueValueList", this.ListToJSONArray((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueValueList"))); 
				LyrJSON.put("UniqueSymbolList", this.ListToJSONArray((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueSymbolList"))); 
				LyrJSON.put("UniqueDefaultSymbol", vLayer.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol")); 
				
				LyrJSONList = LyrJSONList.put(LyrJSON);
			}
			LyrAllJSON.put("AllLayer", LyrJSONList);
			return LyrAllJSON;
		} catch (JSONException ex) {  
		    throw new RuntimeException(ex);  
		} 
		
	}
	
	private JSONArray ListToJSONArray(List<String> list)
	{
		JSONArray jsList = new JSONArray();
		for(String str:list)jsList.put(str);
		return jsList;
	}
	
	private List<String> JSONArrayToList(JSONArray jsList)
	{
		List<String> list = new ArrayList<String>();
		for(int i=0;i<jsList.length();i++)
		{
			try {
				list.add(jsList.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
}
