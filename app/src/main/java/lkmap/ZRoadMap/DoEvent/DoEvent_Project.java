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
	private Context m_Context = null;					//主界面的Context
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
		if (CommandStr.equals("工程_选择"))
		{
//			if (PubVar.currentUserInfo.SYS_UserType.equals("临时用户"))
//			{
//				lkmap.Tools.Tools.ShowMessageBox("您没有获得软件授权，请联系软件供应商获取授权! \r\n详见【工具->关于系统】！");
//			}
//			else {
				//加载数据
				v1_project_select vps = new v1_project_select();
				vps.ShowDialog();
//			}
			
			
			
//			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
//        	String date = sDateFormat.format(new java.util.Date());  
//        	
//			if(PubVar.m_DoEvent.m_AuthorizeTools.isExpired(date, true))
//			{
//				//加载数据
//				v1_project_select vps = new v1_project_select();
//				vps.ShowDialog();
//			}
			
		}
		
		if (CommandStr.equals("工程_打开"))
		{
			//注意此时已经执行过了v1_ProjectDB.OpenProject()
			
			//在PorjectDB.v1_BKLayerExplorer,底图数据图层
			v1_BKLayerExplorer _BKLayerExplorer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer();
			
			//在PorjectDB.PorjectDB.v1_LayerExplorer,采集数据图层
			v1_LayerExplorer _LayerExplorer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer();
			
//			//图层渲染器
//			v1_LayerRenderExplorer _LayerRenderExplorer = PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer();
			
			//工程浏览器
			v1_ProjectExplorer _ProjectExplorer = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer();
			
			//创建工程空间实例
			if (PubVar.m_Workspace!=null)PubVar.m_Workspace.FreeWorkSpace();
			Workspace pWorkspace = new Workspace();PubVar.m_Workspace = pWorkspace;
			
			//设置工程空间坐标系统，工程坐标系统存储于StaticObject.soProjectSystem.
			pWorkspace.SetCoorSystemInfo(_ProjectExplorer.GetCoorSystem());

	    	//创建MAP对象，在此处赋最大范围this.setFullExtend(StaticObject.soMapCellIndex.GetCellExtend());
			Map map = new Map(PubVar.m_MapControl);PubVar.m_Map = map;
			//不需要比例尺
			//map.SetScaleBar(PubVar.m_DoEvent.m_ScaleBar);
		
			//打开采集数据源，其中有渲染底图图层 ，也就是创建GeoLayer
			_LayerExplorer.OpenDataSource(_ProjectExplorer.GetProjectDataFileName());
			
			//打开底图数据源，其中有渲染底图图层 ，也就是创建GeoLayer
			_BKLayerExplorer.OpenBKDataSource();

			//设置所有图层不可选择
			//PubVar.m_Workspace.SetAllGeoLayerNoSelectable();

			
//	        //读取编辑数据
//	        for (Dataset pDataset : pWorkspace.GetDataSourceByEditing().getDatasets())
//	        {
//	        	//首先读取数据的索引及最大外接矩形信息，此部改为动态计算生成
//	        	//pDataset.Open();
//	            //pDataset.QueryGeometryFromDB(null, true);
//	            //pDataset.BuildSpatialIndex();   //重新构创图层索引
//	            
//	            //在此处更新Dataset的Envelope属性，目的是为了修正Envelope，使其最准确
//	            //pDataset.CalEnvelope();
//	        }
	        
	        //读取工程的上次视图范围，如果没有则全图显示
	        Envelope pEnv = _ProjectExplorer.ReadShowExtend();
	        if (pEnv!=null)
	        {
	        	PubVar.m_MapControl.setActiveTool(Tools.FullScreenSize);
	        	PubVar.m_Map.setExtend(pEnv);
	        	PubVar.m_Map.Refresh();
	        } else PubVar.m_DoEvent.DoCommand("全屏");
		}
		
	}
}
