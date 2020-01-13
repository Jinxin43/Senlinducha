package lkmap.ZRoadMap.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

public class v1_ProjectDB 
{
	public v1_ProjectDB(){}
	
	//���̹�����
	private v1_ProjectExplorer m_ProjectExplorer = null;
	public v1_ProjectExplorer GetProjectExplorer(){return this.m_ProjectExplorer;}
	
	//ͼ�������
	private v1_LayerExplorer m_LayerExplorer = null;
	public v1_LayerExplorer GetLayerExplorer(){return this.m_LayerExplorer;}
	
	//��ͼ������
	private v1_BKLayerExplorer m_BKLayerExplorer = null;
	public v1_BKLayerExplorer GetBKLayerExplorer(){return this.m_BKLayerExplorer;}
	
	//ͼ����Ⱦ��
	private v1_LayerRenderExplorer m_LayerRenderExplorer = null;
	public v1_LayerRenderExplorer GetLayerRenderExplorer()
	{
		if (this.m_LayerRenderExplorer==null)this.m_LayerRenderExplorer=new v1_LayerRenderExplorer();
		return this.m_LayerRenderExplorer;
	}
	
	/**
	 * �������̣��Թ���������ΪĿ¼����Ŀ¼����project.dbx���������ļ�
	 * @param prjName
	 */
	public boolean CreateProject(String prjName)
	{
		String PrjPath = PubVar.m_SysAbsolutePath+"/Data/"+prjName;
		
	    //��鹤��Ŀ¼�����û���򴴽�����Ŀ¼
	    if (!lkmap.Tools.Tools.ExistFile(PrjPath))
	    {
	    	if ((new File(PrjPath)).mkdirs())
	    	{
	    		//�������������Ŀ¼�������ļ�
	    		Tools.CopyFile(PubVar.m_SysAbsolutePath+"/sysfile/Template.dbx", PrjPath+"/TAData.dbx");
	    		Tools.CopyFile(PubVar.m_SysAbsolutePath+"/sysfile/Project.dbx", PrjPath+"/Project.dbx");
	    		this.OpenDatabase(PrjPath+"/Project.dbx");
	    		return true;
	    	}
	    }
	    return false;
	}
	
	//��ʶ�Ƿ��Ѿ��򿪹���
	private boolean m_AlwaysOpenProject = false;
	public boolean AlwaysOpenProject(){return this.m_AlwaysOpenProject;}
	
	public boolean OpenProject(String prjName)
	{
		return this.OpenProject(prjName, true);
	}
	/**
	 * �򿪹���
	 * @param prjName
	 * @saveInfo �Ƿ񱣴��ϴδ���Ϣ
	 * @return
	 */
	public boolean OpenProject(String prjName,boolean saveInfo)
	{

		String PrjFileFullName = PubVar.m_SysAbsolutePath+"/Data/"+prjName+"/Project.dbx";
		
		//�򿪹��� ���ÿ�
		if (!Tools.ExistFile(PrjFileFullName)) return false;
		this.OpenDatabase(PrjFileFullName);

		//���̹�����
		if (this.m_ProjectExplorer==null) this.m_ProjectExplorer = new v1_ProjectExplorer();
		this.m_ProjectExplorer.SetBindProjectDB(this);
		this.m_ProjectExplorer.SetProjectName(prjName);
		this.m_ProjectExplorer.LoadProjectInfo();
		
		//�򿪱���������Ӧ��ͼ���б�
		if (this.m_LayerExplorer==null)this.m_LayerExplorer = new v1_LayerExplorer();
		this.m_LayerExplorer.SetBindProjectDB(this);
		this.m_LayerExplorer.LoadLayer();
		
		//�򿪱���������Ӧ�ĵ�ͼͼ�������
		if (this.m_BKLayerExplorer==null)this.m_BKLayerExplorer = new v1_BKLayerExplorer();
		this.m_BKLayerExplorer.SetBindProjectDB(this);
		this.m_BKLayerExplorer.LoadBKLayer();
		this.m_AlwaysOpenProject = true;
		
		//�����δ򿪵Ĺ�����Ϣ�����û����ÿ⣬�����´��Կ�ݷ�ʽ��
		if (saveInfo)
		{
			HashMap<String,String> beforeOpenProjectInfo = new HashMap<String,String>();
			beforeOpenProjectInfo.put("F2", prjName);
			beforeOpenProjectInfo.put("F3", Tools.GetSystemDate());
			PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().SaveUserPara("Tag_BeforeOpenProject",beforeOpenProjectInfo);
		}
		return true;
	}
	
	/**
	 * �رչ���
	 * @return
	 */
	public boolean CloseProject()
	{
		if (this.m_SQLiteDatabase!=null)this.m_SQLiteDatabase.Close();
		return true;
	}
	

	
	//���ݿ������
	private ASQLiteDatabase m_SQLiteDatabase = null;
	
	//�����ݿ�
	private void OpenDatabase(String dbFileName)
	{
		if (Tools.ExistFile(dbFileName))
		{
			if (this.m_SQLiteDatabase!=null)this.m_SQLiteDatabase.Close();
			this.m_SQLiteDatabase = new ASQLiteDatabase();
			this.m_SQLiteDatabase.setDatabaseName(dbFileName);
		}
	}
	/**
	 * �õ�ָ�������ݿ������
	 * @return
	 */
	public ASQLiteDatabase GetSQLiteDatabase()
	{
		return this.m_SQLiteDatabase;
	}
	
	

}

