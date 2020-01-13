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

    
	//ѡ�е�ͼ��ص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	

    //��ͼ�ļ������ͣ�1-ʸ����2-դ��
    private lkMapFileType m_BKMapType = lkMapFileType.enVector;
    public void SetBKMapType(lkMapFileType bkMapType)
    {
    	this.m_BKMapType = bkMapType;
    }
    
    //���õ�ͼ�ļ��б�
    private List<HashMap<String,Object>> m_MapFileList = null;
    public void SetMapFileList(List<HashMap<String,Object>> mapFileList)
    {
    	this.m_MapFileList = mapFileList;
    }
    
    public void ShowDialog()
    {
    	if (this.m_BKMapType== lkMapFileType.enGrid)
    	{
	    	//ȷ��������ͼ�����ͣ�Ҳ�������ΪWGS84����ϵ��դ��ͼ�仯Ϊ����Ӱ��ѡ��
	    	CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
	    	if (CS.GetName().equals("WGS-84����"))
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
