package lkmap.ZRoadMap.DoEvent;

import lkmap.Cargeometry.Envelope;
import lkmap.Dataset.DataSource;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.Workspace;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Map.Map;
import lkmap.Map.StaticObject;
import lkmap.MapControl.Tools;
import lkmap.Tools.TestRegion;
import lkmap.ZRoadMap.Project.v1_BKLayerExplorer;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerExplorer;
import lkmap.ZRoadMap.Project.v1_LayerRenderExplorer;
import lkmap.ZRoadMap.Project.v1_ProjectExplorer;
import lkmap.ZRoadMap.Project.v1_project_select;

import java.text.SimpleDateFormat;

import android.content.Context;
import dingtu.ZRoadMap.AuthorizeTools_UserInfo;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_GpsData_SelectLayer;

public class DoEvent_Project 
{
	private Context m_Context = null;					//�������Context
	public DoEvent_Project(Context _context)
	{
		this.m_Context = _context;
	}
	
	public void DoCommand(String CommandStr)
	{
		this.DoCommand(CommandStr, null);
	}
	public void DoCommand(String CommandStr,final ICallback callBack)
	{
		if (CommandStr.equals("����_ѡ��"))
		{
//			if (PubVar.currentUserInfo.SYS_UserType.equals("��ʱ�û�"))
//			{
//				lkmap.Tools.Tools.ShowMessageBox("��û�л�������Ȩ������ϵ�����Ӧ�̻�ȡ��Ȩ! \r\n���������->����ϵͳ����");
//			}
//			else {
				//��������
				v1_project_select vps = new v1_project_select();
				vps.ShowDialog();
//			}
			
			
			
//			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
//        	String date = sDateFormat.format(new java.util.Date());  
//        	
//			if(PubVar.m_DoEvent.m_AuthorizeTools.isExpired(date, true))
//			{
//				//��������
//				v1_project_select vps = new v1_project_select();
//				vps.ShowDialog();
//			}
			
		}
		
		if (CommandStr.equals("����_��"))
		{
			//ע���ʱ�Ѿ�ִ�й���v1_ProjectDB.OpenProject()
			
			//��PorjectDB.v1_BKLayerExplorer,��ͼ����ͼ��
			v1_BKLayerExplorer _BKLayerExplorer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer();
			
			//��PorjectDB.PorjectDB.v1_LayerExplorer,�ɼ�����ͼ��
			v1_LayerExplorer _LayerExplorer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer();
			
//			//ͼ����Ⱦ��
//			v1_LayerRenderExplorer _LayerRenderExplorer = PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer();
			
			//���������
			v1_ProjectExplorer _ProjectExplorer = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer();
			
			//�������̿ռ�ʵ��
			if (PubVar.m_Workspace!=null)PubVar.m_Workspace.FreeWorkSpace();
			Workspace pWorkspace = new Workspace();PubVar.m_Workspace = pWorkspace;
			
			//���ù��̿ռ�����ϵͳ����������ϵͳ�洢��StaticObject.soProjectSystem.
			pWorkspace.SetCoorSystemInfo(_ProjectExplorer.GetCoorSystem());

	    	//����MAP�����ڴ˴������Χthis.setFullExtend(StaticObject.soMapCellIndex.GetCellExtend());
			Map map = new Map(PubVar.m_MapControl);PubVar.m_Map = map;
			//����Ҫ������
			//map.SetScaleBar(PubVar.m_DoEvent.m_ScaleBar);
		
			//�򿪲ɼ�����Դ����������Ⱦ��ͼͼ�� ��Ҳ���Ǵ���GeoLayer
			_LayerExplorer.OpenDataSource(_ProjectExplorer.GetProjectDataFileName());
			
			//�򿪵�ͼ����Դ����������Ⱦ��ͼͼ�� ��Ҳ���Ǵ���GeoLayer
			_BKLayerExplorer.OpenBKDataSource();

			//��������ͼ�㲻��ѡ��
			//PubVar.m_Workspace.SetAllGeoLayerNoSelectable();

			
//	        //��ȡ�༭����
//	        for (Dataset pDataset : pWorkspace.GetDataSourceByEditing().getDatasets())
//	        {
//	        	//���ȶ�ȡ���ݵ������������Ӿ�����Ϣ���˲���Ϊ��̬��������
//	        	//pDataset.Open();
//	            //pDataset.QueryGeometryFromDB(null, true);
//	            //pDataset.BuildSpatialIndex();   //���¹���ͼ������
//	            
//	            //�ڴ˴�����Dataset��Envelope���ԣ�Ŀ����Ϊ������Envelope��ʹ����׼ȷ
//	            //pDataset.CalEnvelope();
//	        }
	        
	        //��ȡ���̵��ϴ���ͼ��Χ�����û����ȫͼ��ʾ
	        Envelope pEnv = _ProjectExplorer.ReadShowExtend();
	        if (pEnv!=null)
	        {
	        	PubVar.m_MapControl.setActiveTool(Tools.FullScreenSize);
	        	PubVar.m_Map.setExtend(pEnv);
	        	PubVar.m_Map.Refresh();
	        } else PubVar.m_DoEvent.DoCommand("ȫ��");
		}
		
	}
}
