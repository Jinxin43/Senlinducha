package lkmap.Dataset;

import java.util.ArrayList;
import java.util.List;

import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Map.StaticObject;

public class Workspace 
{
	//��ʼ�������ռ�
    public Workspace()
    {
        //ȫ��������
        //StaticObject.MapCellIndex = new lkmap.Index.MapCellIndex();
    }
    
    /**
     * ��������ϵͳ��Ϣ
     * @param coorSystem  ����ϵͳ����
     * @param centerMeridian ���뾭��
     */
    public void SetCoorSystemInfo(CoorSystem coorSystem)
    {
    	StaticObject.soProjectSystem.SetCoorSystem(coorSystem);
    }
    
    /**
     * ����Id�������ݼ�
     * @param datasetId
     * @return
     */
    public Dataset GetDatasetById(String datasetId)
    {
        for (DataSource pDataSource : this.GetDataSourceList())
        {
        	Dataset pDataset = pDataSource.GetDatasetById(datasetId);
        	if (pDataset!=null) return pDataset;
        }
        return null;
    }
    
    public void SetAllGeoLayerNoSelectable()
    {
        for (DataSource pDataSource : this.GetDataSourceList())
        {
        	if (pDataSource.getEditing())
        	{
	        	for(Dataset pDataset:pDataSource.getDatasets())
	        	{
	        		pDataset.getBindGeoLayer().setSelectable(false);
	        	}
        	}
        }
    }

    //�ͷŹ����ռ�
    public void FreeWorkSpace()
    {
        for (DataSource pDataSource : this._DataSourceList)
        {
            pDataSource.Dispose();
        }
        this._DataSourceList.clear();
    }

    //�����ռ�����

    //����Դ
    private List<DataSource> _DataSourceList = new ArrayList<DataSource>();
    public List<DataSource> GetDataSourceList()
    {
        return _DataSourceList;
    }
   

    //�ر�����Դ
    public boolean CloseDataSource(DataSource pDataSource,boolean IfSave)
    {
        //if (IfSave) { pDataSource.Commit(true); }
        pDataSource.Dispose(); this.GetDataSourceList().remove(pDataSource);
        return true;
    }

//    //��������Դ�Ŀɱ༭��
//    public boolean SetDataSourceEditing(DataSource pDataSource, boolean Editing)
//    {
//        //1����֤��ǰ�����ռ�����������Դ��״̬��ͬʱֻ���Ա༭һ������Դ
//        for (DataSource dDataSource : this.getDataSourceList())
//        {
//            dDataSource.setEditing(false);
//        }
//
//        pDataSource.setEditing(Editing);
//        return true;
//    }


    //��������Դ�����Ʋ�������Դ
    public DataSource GetDataSourceByName(String DataSourceName)
    {
        for (DataSource pDataSource : this.GetDataSourceList())
        {
            int FileIndex= pDataSource.getName().lastIndexOf("/")+1;
            int PotIndex =  pDataSource.getName().lastIndexOf(".");if (PotIndex==-1)PotIndex = pDataSource.getName().length();
            String ShortName = pDataSource.getName().substring(FileIndex,PotIndex);
            if (ShortName.equals(DataSourceName)) return pDataSource;
        }
        return null;
    }

//    //��������Դ��ʵ�����Ʋ�������Դ
//    public DataSource GetDataSourceByActualName(String DataSourceActualName)
//    {
//        for (DataSource pDataSource : this.getDataSourceList())
//        {
//            if (pDataSource.getActualName() == DataSourceActualName) return pDataSource;
//        }
//        return null;
//    }

    /**
     * ���ص�ǰ���ڱ༭������Դ
     */
    public DataSource GetDataSourceByEditing()
    {
        for (DataSource pDataSource : this.GetDataSourceList())
        {
            if (pDataSource.getEditing()) return pDataSource;
        }
        return null;
    }
    
//    /**
//     * ���ص�ǰ���ɱ༭������Դ��Ҳ���ǵ�ͼ����Դ
//     */
//    public DataSource GetDataSourceByUnEditing()
//    {
//        for (DataSource pDataSource : this.GetDataSourceList())
//        {
//            if (!pDataSource.getEditing()) return pDataSource;
//        }
//        return null;
//    }
}
