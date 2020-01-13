package lkmap.ZRoadMap.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dingtu.ZRoadMap.HashValueObject;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkRenderType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class v1_UserConfigDB 
{

	//���ݿ������
	private ASQLiteDatabase m_SQLiteDatabase = null;
	
	//�û��������ñ�
	private v1_UserConfigDB_UserParam m_UserParam = null;
	public v1_UserConfigDB_UserParam GetUserParam()
	{
		if (this.m_UserParam ==null)
		{
			//���T_UserParam���Ƿ����
			if (this.CheckAndCreateTable("T_UserParam"))
			{
				this.m_UserParam = new v1_UserConfigDB_UserParam();
				this.m_UserParam.SetBindDB(this.GetSQLiteDatabase());
			}
		}
		return this.m_UserParam;
	}
	
	//�ҵ�����ϵ���ñ�
	private v1_UserConfigDB_MyCoordinateSystem m_MyCoordinateSystem = null;
	public v1_UserConfigDB_MyCoordinateSystem GetMyCoodinateSystem()
	{
		if (this.m_MyCoordinateSystem ==null)
		{
			//���T_MyCoordinateSystem���Ƿ����
			if (this.CheckAndCreateTable("T_MyCoordinateSystem"))
			{
				this.m_MyCoordinateSystem = new v1_UserConfigDB_MyCoordinateSystem();
				this.m_MyCoordinateSystem.SetBindDB(this.GetSQLiteDatabase());
			}
		}
		return this.m_MyCoordinateSystem;
	}
	
	//ת�������洢��
	private v1_UserConfigDB_TransformationParam m_TransformationParam = null;
	public v1_UserConfigDB_TransformationParam GetTransformationParam()
	{
		if (this.m_TransformationParam ==null)
		{
			//���T_TransformationParam���Ƿ����
			if (this.CheckAndCreateTable("T_TransformationParam"))
			{
				this.m_TransformationParam = new v1_UserConfigDB_TransformationParam();
				this.m_TransformationParam.SetBindDB(this.GetSQLiteDatabase());
			}
		}
		return this.m_TransformationParam;
	}
	
	//ͼ��ģ�����ñ�
	private v1_UserConfigDB_LayerTemplate m_LayerTemplate = null;
	public v1_UserConfigDB_LayerTemplate GetLayerTemplate()
	{
		if (this.m_LayerTemplate ==null)
		{
			//���T_LayerTemplate���Ƿ����
			if (this.CheckAndCreateTable("T_LayerTemplate"))
			{
				this.m_LayerTemplate = new v1_UserConfigDB_LayerTemplate();
				this.m_LayerTemplate.SetBindDB(this.GetSQLiteDatabase());
				
				//����ϵͳͼ��ģ�壬Ҳ�����½�����ʱ���õ�Ĭ��ͼ��ģ��
				HashMap<String,Object> sysLayerTemplate = new HashMap<String,Object>();
				//ģ������
				sysLayerTemplate.put("Name", "ϵͳĬ��ͼ��ģ��");
				//����ʱ��
				sysLayerTemplate.put("CreateTime",Tools.GetSystemDate());
				//�Ƿ񸲸�
				sysLayerTemplate.put("OverWrite","true");
				
				//ͼ���б�
				List<v1_Layer> vLayerList = new ArrayList<v1_Layer>();
				String[] lyrTypeList = new String[]{"��","��","��"};
				for(String lyrType:lyrTypeList)
				{
					v1_Layer vLayer = new v1_Layer();
					vLayer.SetLayerAliasName("Ĭ��"+lyrType+"��");
					vLayer.SetLayerTypeName(lyrType);
					vLayer.SetRenderType(lkRenderType.enSimple);
					
					//Ϊ�ֶ�ʵ�帳ֵ
					v1_LayerField LF1 = new v1_LayerField();
					LF1.SetFieldName("����");
					LF1.SetDataFieldName("F1");
					LF1.SetFieldTypeName("�ַ���");
					LF1.SetFieldSize(254);
					vLayer.GetFieldList().add(LF1);
					
					v1_LayerField LF2 = new v1_LayerField();
					LF2.SetFieldName("��ע");
					LF2.SetDataFieldName("F2");
					LF2.SetFieldTypeName("�ַ���");
					LF2.SetFieldSize(254);
					vLayer.GetFieldList().add(LF2);
					
					vLayerList.add(vLayer);
				}
				sysLayerTemplate.put("LayerList",vLayerList);
				this.m_LayerTemplate.SaveLayerTemplate(sysLayerTemplate);
			}
		}
		return this.m_LayerTemplate;
	}
	
	/**
	 * �õ�ָ�������ݿ������
	 * @return
	 */
	private ASQLiteDatabase GetSQLiteDatabase()
	{
		if (this.m_SQLiteDatabase==null) this.OpenDatabase();
		return this.m_SQLiteDatabase;
	}
	
	//���������ݿ�
	private void OpenDatabase()
	{
		String configFileName = PubVar.m_SysAbsolutePath+"/sysfile/UserConfig.dbx";
		if (Tools.ExistFile(configFileName))
		{
			this.m_SQLiteDatabase = new ASQLiteDatabase();
			this.m_SQLiteDatabase.setDatabaseName(configFileName);
		}
	}
	
	/**
	 * ����ϵͳ���ò���������ϵͳ�����ڣ���Ҫ��PubVar.m_HashMap�ڣ�
	 * ϵͳ������ʶ��Tag_System_***
	 */
	public void LoadSystemConfig()
	{
		//0��ϵͳ����
		HashValueObject HVO_SystemLanguage = PubVar.m_HashMap.GetValueObject("Tag_System_Language", true);
		HashMap<String,String> configItem = this.GetUserParam().GetUserPara("Tag_System_Language");
		 if (configItem==null) 
		 {
			 HVO_SystemLanguage.Value = Tools.ToLocale("ϵͳ����");   //Ĭ��ϵͳ����
		 } else
		 {
			 HVO_SystemLanguage.Value = configItem.get("F2");
		 }
		 
		//1��ȡGPS�ɼ�������Сʱ��������С������
		 HashValueObject HVO_GpsMinTime = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime", true);
		 HashValueObject HVO_GpsMinDis = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance", true);
		 configItem = this.GetUserParam().GetUserPara("Tag_System_GPS");
		 if (configItem==null) 
		 {
		 	HVO_GpsMinTime.Value = "1";   //Ĭ��1��
		 	HVO_GpsMinDis.Value="1";    //Ĭ��1��
		 } else
		 {
			 HVO_GpsMinTime.Value = configItem.get("F2");
			 HVO_GpsMinDis.Value = configItem.get("F3");
		 }
		 
		 //2������������ʾ����ʽ��Code��ʽ��GPS_[0=DD��MM'SS.SSSS"��1=DD��MM.MMMMMM�䣬2=DD.DDDDDD��]_[1=�߳�,0=�޸߳�]
		 //								  PROJECT_[3=XY]_[1=�߳�,0=�޸߳�]
		 HashValueObject HVO_TopXYFormat_Code = PubVar.m_HashMap.GetValueObject("Tag_System_TopXYFormat_Code", true);
		 HashValueObject HVO_TopXYFormat_Label = PubVar.m_HashMap.GetValueObject("Tag_System_TopXYFormat_Label", true);
		 configItem = this.GetUserParam().GetUserPara("Tag_System_TopXYFormat");
		 if (configItem==null) 
		 {
			 HVO_TopXYFormat_Code.Value = "GPS_1_1";   //wgs84��γ��
			 HVO_TopXYFormat_Label.Value="N:000��00��0000�� E:00��00��0000�� H:0.00";    //��γ����߳�
		 } else
		 {
			 HVO_TopXYFormat_Code.Value = configItem.get("F2");
			 HVO_TopXYFormat_Label.Value = configItem.get("F3");
		 }
		 
		 //3���ֶ�GPS�ɼ�ƽ��ֵ����������㼰������
		 HashValueObject HVO_GpsAveragePointEnable = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_AveragePointEnable", true);
		 HashValueObject HVO_GpsPointCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount", true);
		 HashValueObject HVO_GpsVertexCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_VertexCount", true);
		 configItem = this.GetUserParam().GetUserPara("Tag_System_GPS_AveragePoint");
		 if (configItem==null) 
		 {
			 HVO_GpsAveragePointEnable.Value = "true";  //Ĭ�Ͽ���
			 HVO_GpsPointCount.Value = "5";   //Ĭ��5����
			 HVO_GpsVertexCount.Value="3";    //Ĭ��3���㣬����ڵ���
		 } else
		 {
			 HVO_GpsAveragePointEnable.Value = configItem.get("F2");
			 HVO_GpsPointCount.Value = configItem.get("F3");
			 HVO_GpsVertexCount.Value = configItem.get("F4");
		 }
		 
		 //4�������λ
		 HashValueObject HVO_AreaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit", true);
		 configItem = this.GetUserParam().GetUserPara("Tag_System_AreaUnit");
		 if (configItem==null) 
		 {
			 HVO_AreaUnit.Value = "ƽ����";
		 } else
		 {
			 HVO_AreaUnit.Value = configItem.get("F2");
		 }
		 
		 HashValueObject HVO_LengthUnit = PubVar.m_HashMap.GetValueObject("Tag_System_LengthUnit", true);
		 configItem = this.GetUserParam().GetUserPara("Tag_System_LengthUnit");
		 if(configItem == null)
		 {
			 HVO_LengthUnit.Value = "��";
		 } else
		 {
			 HVO_LengthUnit.Value = configItem.get("F2");
		 }
		 
		 HashValueObject HVO_MapScale = PubVar.m_HashMap.GetValueObject("Tag_System_MapScale", true);
		 configItem = this.GetUserParam().GetUserPara("Tag_System_MapScale");
		 if(configItem == null)
		 {
			 HVO_MapScale.Value = "1:1��";
		 } else
		 {
			 HVO_MapScale.Value = configItem.get("F2");
		 }
		 
		 //5���Ŵ󾵣�Ҳ����ȷ������ģʽ
		 HashValueObject HVO_ZoomGlass = PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass", true);
		 configItem = this.GetUserParam().GetUserPara("Tag_System_ZoomGlass");
		 if (configItem==null) 
		 {
			 HVO_ZoomGlass.Value = "true";
			 if(PubVar.m_DoEvent.m_GlassView!= null)
			 {
				 PubVar.m_DoEvent.m_GlassView.SetVisible(true);
			 }
			 
		 } else
		 {
			 HVO_ZoomGlass.Value = configItem.get("F2");
			 if(PubVar.m_DoEvent.m_GlassView != null)
			 {
				 if(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value.equals("true"))
				 {
					 PubVar.m_DoEvent.m_GlassView.SetVisible(true); 
				 }
				 else
				 {
					 PubVar.m_DoEvent.m_GlassView.SetVisible(false); 
				 }
			 }
		 }
		 
		 HashValueObject HVO_ZoomGlass_Scale = PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass_Scale", true);
		 configItem = this.GetUserParam().GetUserPara("Tag_System_ZoomGlass_Scale");
		 if (configItem==null) 
		 {
			 HVO_ZoomGlass_Scale.Value = "��";
			 if(PubVar.m_DoEvent.m_GlassView!= null)
			 {
				 PubVar.m_DoEvent.m_GlassView.setGlassScale(HVO_ZoomGlass_Scale.Value);
			 }
			 
		 } else
		 {
			 HVO_ZoomGlass_Scale.Value = configItem.get("F2");
			 if(PubVar.m_DoEvent.m_GlassView != null)
			 {
				 if(HVO_ZoomGlass_Scale.Value != null)
				 {
					 PubVar.m_DoEvent.m_GlassView.setGlassScale(HVO_ZoomGlass_Scale.Value);
				 }
			 }
		 }
		 
		 //5����Ƭ�Ƿ��ˮӡ
		 HashValueObject hvoWaterMark = PubVar.m_HashMap.GetValueObject("Tag_Photo_WaterMark", true);
		 configItem = this.GetUserParam().GetUserPara("Tag_Photo_WaterMark");
		 if (configItem==null) 
		 {
			 hvoWaterMark.Value = "true";
			 
			 
		 } else
		 {
			 hvoWaterMark.Value = configItem.get("F2");
			 
		 }
		 
		 
		 HashValueObject showCenterCross = PubVar.m_HashMap.GetValueObject("Tag_ShowCenterCross", true);
		 configItem = this.GetUserParam().GetUserPara("Tag_ShowCenterCross");
		 if (configItem==null) 
		 {
			 showCenterCross.Value = "true";
			 PubVar.CenterCrossShow = true;
			 PubVar.m_MapControl.invalidate();
			 
		 } else
		 {
			 showCenterCross.Value = configItem.get("F2");
			 try{
				 PubVar.CenterCrossShow = Boolean.parseBoolean(showCenterCross.Value);
				 PubVar.m_MapControl.invalidate();
			 }
			 catch(Exception ex)
			 {
				 
			 }
			 
		 }
	}
	
	/**
	 * ����ָ�����Ƶı�
	 * @param TableName
	 * @return
	 */
	private boolean CheckAndCreateTable(String TableName)
	{
		if (this.IsExistTable(TableName)) return true;
		else
		{
			//������
			List<String> createSQL = new ArrayList<String>();
	    	createSQL.add("CREATE TABLE "+TableName+" (");
	    	createSQL.add("ID integer primary key autoincrement  not null default (0),");

	    	//�ֲ�ͬ���ƴ�����ṹ
	    	if (TableName.equals("T_LayerTemplate"))
	    	{
	    		createSQL.add("name text,");
	    		createSQL.add("createtime text,");
	    		createSQL.add("layerlist binary");
	    	}
	    	if (TableName.equals("T_MyCoordinateSystem"))
	    	{
	    		createSQL.add("Name text,");
	    		createSQL.add("CreateTime text,");
	    		createSQL.add("Para binary");
	    	}
	    	if (TableName.equals("T_UserParam"))
	    	{
	    		for(int i=1;i<=49;i++)
	    		{
	    			createSQL.add("F"+i+" text,");
	    		}
	    		createSQL.add("F50 text");
	    	}
	    	if (TableName.equals("T_TransformationParam"))
	    	{
	    		for(int i=1;i<=49;i++)
	    		{
	    			createSQL.add("F"+i+" text,");
	    		}
	    		createSQL.add("F50 text");
	    	}
	    	
	    	

	    	createSQL.add(")");
	    	String SQL = Tools.JoinT("\r\n", createSQL);
	    	return this.GetSQLiteDatabase().ExcuteSQL(SQL);
		}
	}
	
	/**
	 * ���ָ���ı��Ƿ��ڴ���
	 * @param TableName
	 * @return
	 */
	private boolean IsExistTable(String TableName)
	{
		String SQL = "SELECT COUNT(*) as count FROM sqlite_master WHERE type='table' and name= '"+TableName+"'";
		SQLiteDataReader DR = this.GetSQLiteDatabase().Query(SQL);
		if (DR==null) return false;
		int Count = 0;
		if(DR.Read())Count = Integer.parseInt(DR.GetString("count"));DR.Close();
		if (Count>0) return true;else return false;
	}
	
	
}

