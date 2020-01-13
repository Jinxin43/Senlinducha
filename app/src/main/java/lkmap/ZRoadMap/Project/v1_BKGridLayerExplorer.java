package lkmap.ZRoadMap.Project;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import lkmap.Enum.lkOverMapType;
import lkmap.Layer.GridLayer;
import lkmap.Tools.Tools;

public class v1_BKGridLayerExplorer 
{

	//���̶�Ӧ��դ���ͼ�ļ�����ʽ���v1_BKLayerExplorer.JSONObjectToList()�����ڲ�
	private List<HashMap<String,Object>> m_BKFileLlist = new ArrayList<HashMap<String,Object>>();
	public List<HashMap<String,Object>> GetBKFileList(){return this.m_BKFileLlist;}
	public String GetBKFileListStr()
	{
		List<String> bkFileList = new ArrayList<String>();
		for(HashMap<String,Object> hmObj:m_BKFileLlist)
		{
			bkFileList.add(hmObj.get("MapFileName")+"");
		}
		return "��"+bkFileList.size()+"��"+Tools.JoinT(",", bkFileList);
	}
	public void SetBKFileList(List<HashMap<String,Object>> bkFileList){this.m_BKFileLlist=bkFileList;}
	
	
	
	/**
	 * ����դ��ͼ������
	 * @return
	 */
	public boolean SaveBKLayer()
	{
		//�����ͼ�ļ���Ϣ
		String[] FieldList = {"Type","BKMapFile","MinX","MinY","MaxX","MaxY","CoorSystem","Transparent","Sort","Visible","F1"};
		String SQL_DEL = "delete from T_BKLayer where Type = '%1$s'";
			
		//���沢����
		SQL_DEL = String.format(SQL_DEL,"դ��");
		if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_DEL))
		{
			for(HashMap<String,Object> ho:this.m_BKFileLlist)
			{
				String SQL_INS = "insert into T_BKLayer (%1$s) values ('%2$s')";
				List<String> ValueList = new ArrayList<String>();
				for(String field:FieldList)
					if(ho.get(field)==null)
					{
						ValueList.add("");
					}
					else
					{
						ValueList.add(ho.get(field)+"");
					}
					
				SQL_INS = String.format(SQL_INS,Tools.Joins(",", FieldList),Tools.JoinT("','", ValueList));
				boolean OK = PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_INS);
				if (!OK) return false;
			}
		} else return false;
		return true;		
	}

	/**
	 * ��դ���ͼ����Դ
	 * դ�񱳾���PubVar.m_Map.GetGridLayers().SetMapFileList(m_BKFileLlist);
	 */
	public void OpenGridDataSource()
	{
		//�ж��Ƿ�ΪWeb��ͼ
		if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetName().equals("WGS-84����"))
		{
			//PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Satellite);
			PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Satellite);return;
//			 HashMap<String, Object> aHashMap = new HashMap<String, Object>();
//			 aHashMap.put("MapFileName", "Google����Ӱ��ͼ");
//			 aHashMap.put("MapFileName", "Google����ͼ");
//			 m_BKFileLlist.add(aHashMap);
			//if (this.m_BKFileLlist.size()==0) return;
//			String MapFileName = this.m_BKFileLlist.get(0).get("MapFileName")+"";
//			if (MapFileName.indexOf("Google����Ӱ��ͼ")>=0){PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Satellite);return;}
//			if (MapFileName.indexOf("Google����ͼ")>=0){PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Terrain);return;}
//			if (MapFileName.indexOf("Google����ͼ")>=0){PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Street);return;}
//			if (MapFileName.indexOf("���ͼ����Ӱ��ͼ")>=0){PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enTianditu_Satellite);return;}
//			if (MapFileName.indexOf("���ͼ����ͼ")>=0){PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enTianditu_Street);return;}
		}
		else
		{
			//��դ�����ݵ�ͼ
			PubVar.m_Map.GetGridLayers().SetMapFileList(this.m_BKFileLlist);
		}

	}


	
	private boolean m_GridVisible = true;
	public boolean GetBKVisible(){return this.m_GridVisible;}
	

	/**
	 * ����դ���ͼ�ɼ���
	 * @param visible
	 */
	public void SetBKVisible(boolean visible)
	{
		this.m_GridVisible = visible;
		
    	//դ���ͼ��ʾ������
		PubVar.m_Map.getOverMapLayer().SetShowGrid(false);
		PubVar.m_Map.GetGridLayers().SetShowGrid(false);
		
		if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetName().equals("WGS-84����"))
		{
			PubVar.m_Map.getOverMapLayer().SetShowGrid(visible);
		} 
		else
		{
			PubVar.m_Map.GetGridLayers().SetShowGrid(visible);
		}
	}
}
