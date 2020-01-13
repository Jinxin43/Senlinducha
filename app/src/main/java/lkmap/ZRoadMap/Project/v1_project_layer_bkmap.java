package lkmap.ZRoadMap.Project;

import java.util.HashMap;
import java.util.List;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Enum.lkMapFileType;

public class v1_project_layer_bkmap
{
    public v1_project_layer_bkmap()
    {
    }

    
	//选中底图后回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	

    //底图文件的类型，1-矢量，2-栅格
    private lkMapFileType m_BKMapType = lkMapFileType.enVector;
    public void SetBKMapType(lkMapFileType bkMapType)
    {
    	this.m_BKMapType = bkMapType;
    }
    
    //设置底图文件列表
    private List<HashMap<String,Object>> m_MapFileList = null;
    public void SetMapFileList(List<HashMap<String,Object>> mapFileList)
    {
    	this.m_MapFileList = mapFileList;
    }
    
    public void ShowDialog()
    {
    	if (this.m_BKMapType== lkMapFileType.enGrid)
    	{
	    	//确定背景底图的类型，也就是如果为WGS84坐标系，栅格图变化为卫星影像选项
	    	CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
	    	if (CS.GetName().equals("WGS-84坐标"))
	    	{
	    		v1_project_layer_bkmap_web v = new v1_project_layer_bkmap_web();
	    		v.SetMapFileList(this.m_MapFileList);
	    		v.SetCallback(this.m_Callback);
	    		v.ShowDialog();
	    	} 
	    	else
	    	{
//	    		v1_project_layer_bkmap_grid v = new v1_project_layer_bkmap_grid();
	    		
	    		LayerManager_bkMap_Grid v = new LayerManager_bkMap_Grid();
	    		v.SetMapFileList(this.m_MapFileList);
	    		v.SetCallback(this.m_Callback);
	    		v.ShowDialog();
	    	} 
    	}

    	if (this.m_BKMapType==lkMapFileType.enVector)
    	{
//    		v1_project_layer_bkmap_vector v = new v1_project_layer_bkmap_vector();
    		LayerManager_bkMap_Vector v = new LayerManager_bkMap_Vector();
    		v.SetMapFileList(this.m_MapFileList);
    		v.SetCallback(this.m_Callback);
    		v.ShowDialog();
    	}
    }
    

}
