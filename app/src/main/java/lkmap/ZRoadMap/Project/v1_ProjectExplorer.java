package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.R.string;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Envelope;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

public class v1_ProjectExplorer 
{
	private v1_ProjectDB m_ProjectDB = null;
	
	/**
	 * �󶨹��̲�����
	 * @param projectDB
	 */
	public void SetBindProjectDB(v1_ProjectDB projectDB)
	{
		this.m_ProjectDB = projectDB;
	}
	
	//��������
	private String m_ProjectName ="";
	
	/**
	 * ���ù����ļ���
	 * @param prjName
	 */
	public void SetProjectName(String prjName){this.m_ProjectName = prjName;}
	
	
	/**
	 * �õ�����·��,·��+����Ŀ¼��
	 * @return
	 */
	public String GetProjectFullName()
	{
		return PubVar.m_SysAbsolutePath+"/Data/"+this.m_ProjectName;
	}
	
	public String GetProjectShortName()
	{
		return this.m_ProjectName;
	}
	
	/**
	 * �õ��ɼ������ļ�,TAData.dbxȫ·����
	 * @return
	 */
	public String GetProjectDataFileName()
	{
		return this.GetProjectFullName()+"/TAData.dbx";
	}
	
	/**
	 * �õ��ɼ�����Ԥ��ͼ
	 * @return
	 */
	public String GetProjectDataPreviewImageName()
	{
		return this.GetProjectFullName()+"/DataPreview.jpg";
	}
	
	//���� ����ʱ��
	private String m_Project_CreateTime = "";
	/**
	 * �õ����� ����ʱ�� 
	 * @return 2013-07-30 16:43:13
	 */
	public String GetProjectCreateTime(){return this.m_Project_CreateTime;}
	
	//����ϵ����
	private CoorSystem m_CoorSystem = null;
	/**
	 * �õ���������ϵͳ
	 * @return
	 */
	public CoorSystem GetCoorSystem()
	{
		return m_CoorSystem;
	}

	/**
	 * ���ع�����Ϣ
	 */
	public void LoadProjectInfo()
	{
		//����ϵͳ
		if (this.m_CoorSystem ==null) this.m_CoorSystem = new CoorSystem();
		
		//��ȡ�˹��̶�Ӧ��ͼ����Ϣ��ע��SYS_ID=1�ı�ʾģ�幤����Ϣ��Ҳ�����ϴδ����Ĺ�����Ϣ
		SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query("select * from T_Project where id=2");
		if(DR.Read())
		{
			this.m_Project_CreateTime = DR.GetString("CreateTime");
			this.m_CoorSystem.SetName(DR.GetString("CoorSystem"));
			this.m_CoorSystem.SetCenterMeridian(Float.parseFloat(DR.GetString("CenterMeridian")));
			this.m_CoorSystem.SetCoorTransMethodName(DR.GetString("TransMethod"));
			this.m_CoorSystem.SetPMTransMethodName(DR.GetString("PMTransMethod"));
			this.m_CoorSystem.SetTransToP31(DR.GetString("P31"));
			this.m_CoorSystem.SetTransToP32(DR.GetString("P32"));
			this.m_CoorSystem.SetTransToP33(DR.GetString("P33"));
			this.m_CoorSystem.SetTransToP41(DR.GetString("P41"));
			this.m_CoorSystem.SetTransToP42(DR.GetString("P42"));
			this.m_CoorSystem.SetTransToP43(DR.GetString("P43"));
			this.m_CoorSystem.SetTransToP44(DR.GetString("P44"));
			this.m_CoorSystem.SetTransToP71(DR.GetString("P71"));
			this.m_CoorSystem.SetTransToP72(DR.GetString("P72"));
			this.m_CoorSystem.SetTransToP73(DR.GetString("P73"));
			this.m_CoorSystem.SetTransToP74(DR.GetString("P74"));
			this.m_CoorSystem.SetTransToP75(DR.GetString("P75"));
			this.m_CoorSystem.SetTransToP76(DR.GetString("P76"));
			this.m_CoorSystem.SetTransToP77(DR.GetString("P77"));
			this.m_CoorSystem.SetIsAutoCalc(DR.GetString("F1"));
			String dh= DR.GetString("F2");
			if(dh!= null && dh.length()>0)
			{
				this.m_CoorSystem.SetDH(Integer.parseInt(dh));
			}
			String fendai=DR.GetString("F3");
			
			if(fendai!= null&&fendai.length()>0)
			{
				if(fendai.endsWith("��"))
				{
					fendai = fendai.replace("��", "");
				}
				this.m_CoorSystem.setFenDai(Integer.parseInt(fendai));
			}
		}DR.Close();
		
		//��ȡ����ϵͳ����ϸ��Ϣ
		SQLiteDataReader DRT = PubVar.m_DoEvent.m_ConfigDB.GetSQLiteDatabase().Query("select * from T_CoorSystem where name = '"+this.m_CoorSystem.GetName()+"'");
		if (DRT.Read())
		{
			this.m_CoorSystem.SetA(Double.parseDouble(DRT.GetString("a")));
			this.m_CoorSystem.SetB(Double.parseDouble(DRT.GetString("b")));
			this.m_CoorSystem.SetEasting(Double.parseDouble(DRT.GetString("Easting"))+this.m_CoorSystem.GetDH()*1000000);
		}DRT.Close();
		
	}
	
	/**
	 * ���湤�̵���ʾ��Χ�������´���ʾ���ٶ�λ
	 * @param pEnv
	 * @return
	 */
	public boolean SaveShowExtend(Envelope pEnv)
	{
		//�Զ��������ñ�
		String TableName = "T_ProjectUserConfig";
		if (this.CheckAndCreateTable(TableName))
		{
			try
			{
				JSONObject ParaObj = new JSONObject();  
				ParaObj.put("LeftTopX", pEnv.getLeftTop().getX());  
				ParaObj.put("LeftTopY", pEnv.getLeftTop().getY());  
				ParaObj.put("RightBottomX", pEnv.getRightBottom().getX());  
				ParaObj.put("RightBottomY", pEnv.getRightBottom().getY());  

				//����ɾ���ϴεķ�Χ��¼
				String SQL = "delete from "+TableName+" where Name = '�ϴ���ͼ��Χ'";
				if (this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL))
				{
					SQL = "insert into "+TableName+" (Name,Para) values ('�ϴ���ͼ��Χ',?)";
			        Object[] value =new Object[]{ParaObj.toString().getBytes()};
			        return this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL, value);
				}
			}
			catch (JSONException ex) 
			{  
			    throw new RuntimeException(ex);  
			}
		}
		return false;
	}
	
	/**
	 * ��ȡ���̵���ͼ��Χ
	 */
	public Envelope ReadShowExtend()
	{
		//�Զ��������ñ�
		String TableName = "T_ProjectUserConfig";
		if (this.CheckAndCreateTable(TableName))
		{
			try
			{
				String SQL = "select * from "+TableName+" where Name='�ϴ���ͼ��Χ'";
				SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query(SQL);
				if (DR==null)return null;
				if (DR.Read())
				{
					String josnObjectStr = new String(DR.GetBlob("Para"));
				    JSONTokener jsonParser = new JSONTokener(josnObjectStr);  
				    JSONObject ParaJSON = (JSONObject)jsonParser.nextValue();   
				    
				    double LeftTopX,LeftTopY,RightBottomX,RightBottomY;
				    String LTX = ParaJSON.getString("LeftTopX");if (Tools.IsDouble(LTX))LeftTopX = Double.parseDouble(LTX); else return null;
				    String LTY = ParaJSON.getString("LeftTopY");if (Tools.IsDouble(LTY))LeftTopY = Double.parseDouble(LTY);else return null;
				    String RBX = ParaJSON.getString("RightBottomX");if (Tools.IsDouble(RBX))RightBottomX = Double.parseDouble(RBX);else return null;
				    String RBY = ParaJSON.getString("RightBottomY");if (Tools.IsDouble(RBY))RightBottomY = Double.parseDouble(RBY);else return null;
				    return new Envelope(LeftTopX,LeftTopY,RightBottomX,RightBottomY);
				}DR.Close();
			}
			catch (JSONException ex) 
			{  
			    return null;
			} 
			return null;
		}
		return null;
	}
	
	/**
	 * ��̬����ָ�����Ƶı�
	 * @param TableName
	 * @return
	 */
	private boolean CheckAndCreateTable(String TableName)
	{
		boolean CreateTable = false;
		String SQL = "SELECT COUNT(*) as count FROM sqlite_master WHERE type='table' and name= '"+TableName+"'";
		SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query(SQL);
		if (DR==null) CreateTable = true;
		int Count = 0;
		if(DR.Read())Count = Integer.parseInt(DR.GetString("count"));DR.Close();
		if (Count<=0) CreateTable = true;
		if (CreateTable)
		{
			//������
			List<String> createSQL = new ArrayList<String>();
	    	createSQL.add("CREATE TABLE "+TableName+" (");
	    	createSQL.add("ID integer primary key autoincrement  not null default (0),");

	    	//�ֲ�ͬ���ƴ�����ṹ
	    	if (TableName.equals("T_ProjectUserConfig"))
	    	{
	    		createSQL.add("Name text,");
	    		createSQL.add("Para binary");
	    		for(int i=1;i<=49;i++)
	    		{
	    			createSQL.add("F"+i+" text,");
	    		}
	    		createSQL.add("F50 text");
	    	}

	    	createSQL.add(")");
	    	SQL = Tools.JoinT("\r\n", createSQL);
	    	return this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL);
		} else return true;
	}
	
	public boolean CheckAndCreateJiaoguiceshuTable()
	{
		String sql = "CREATE TABLE if not exists T_Jiaoguiceshu ("+
				"ID integer primary key autoincrement  not null default (0),"+
				"Shi TEXT,"+
				"Xian Text,"+
				"Xiang Text,"+
			    "Cun Text,"+
				"Linban Text,"+
				"XiaoBan INT,"+
				"PointID INT,"+
				"X TEXT,"+
				"Y TEXT,"+
				"ShuZhong Text,"+
				"TreeID INT,"+
				"D Text,"+
				"H Text,"+
				"G Text)";
				
		
		return this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sql);
	}
	
	public  boolean CheckAndCreateTanhuiTable()
	{
		String sql = "CREATE TABLE if not exists T_MeiMuJianChi ("+
				"JianChiID    TEXT            PRIMARY KEY,"+
				"YangDiHao    TEXT,"+
				"BiaoZhunDiHao Text,"+
			    "XiaoBanHao  Text,"+
				"JianChiCode  INT,"+
				"ShuZhongCode TEXT,"+
				"ShuZhong     TEXT,"+
				"XiongJing    TEXT,"+
				"XuJiLiang    DECIMAL (10, 3))";
		
		return this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sql);
	}
	
	public int  GetMaxJianChiCode(String yangdihao,String xiaobanhao,String biaozhundihao )
	{
		String sql = "Select Max(JianChiCode) as maxCode from T_MeiMuJianChi where YangDiHao='"+yangdihao+
				"' and BiaoZhunDiHao='"+biaozhundihao+"' and xiaobanhao ='"+xiaobanhao+"'";
		SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query(sql);
		if(DR == null)
		{
			return 0 ;
		}
		
		if (DR.Read())
		{
			return DR.GetInt32(0);
		}
		else 
		{
			return 0;
		}
	}
	
	
	

}
