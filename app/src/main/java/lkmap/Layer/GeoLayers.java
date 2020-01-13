package lkmap.Layer;

import java.util.*;

public class GeoLayers
{

	private List<GeoLayer> List = new ArrayList<GeoLayer>();
	
	public List<GeoLayer> getList()
	{
		return List;
	}
	public int size()
	{
		return List.size();
	}
	
	//����ͼ��
    public void AddLayer(GeoLayer pGeoLayer)
    {
        List.add(pGeoLayer);
    }

    /**
     * �Ƴ�ָ��������ͼ��
     * @param index
     */
    public void RemoveAt(int index)
    {
        List.remove(index);
    }

    /**
     * �Ƴ�ָ����ͼ�㡱��ͼ��
     * @param LayerId
     */
    public void Remove(String LayerId)
    {
        List.remove(this.GetLayerById(LayerId));
    }

    /**
     * �Ƴ�ָ����ͼ�㡱��ͼ��
     * @param pGeoLayer
     */
    public void Remove(GeoLayer pGeoLayer)
    {
        List.remove(pGeoLayer);
    }


    /**
     * ָ����ͼ�������ֵ
     * @param pGeoLayer
     * @return
     */
    public int IndexOf(GeoLayer pGeoLayer)
    {
        return List.indexOf(pGeoLayer);
    }

    /**
     * ָ������ֵ��ͼ��
     * @param index
     * @return
     */
    public GeoLayer GetLayerByIndex(int index)
    {
        return (GeoLayer)List.get(index);
    }

    /**
     * ָ��ͼ��Id��ͼ��
     * @param layerId
     * @return
     */
    public GeoLayer GetLayerById(String layerId)
    {
        for (GeoLayer layer : List)
        {
            if (layer.getId().indexOf(layerId) >= 0) return layer;
        }
        return null;
    }

    /**
     * �ı�ͼ�������˳��
     * @param GeoLayerName
     * @param newIndex
     */
    public void MoveTo(String GeoLayerName,int newIndex)
    {
    	GeoLayer pGeoLayer = this.GetLayerById(GeoLayerName);
    	this.List.remove(pGeoLayer);
    	this.List.add(newIndex,pGeoLayer);
    }
    
//    private void InsertLayer(int index, GeoLayer pLayer)
//    {
//        List.add(index, pLayer);
//    }

//    //����ͼ���˳��
//    public void SetLayerLevel(int FromIndex, int ToIndex)
//    {
//        GeoLayer pLayer = this.GetLayerByIndex(FromIndex);
//        this.RemoveAt(FromIndex);
//        this.InsertLayer(ToIndex, pLayer);
//    }

//    //����ͼ���˳��-����
//    public boolean MoveLayerUp(String LayerName)
//    {
//        int FromIndex = this.IndexOf(this.GetLayerByName(LayerName));
//        this.SetLayerLevel(FromIndex, FromIndex - 1);
//        return true;
//    }
//
//    //����ͼ���˳��-����
//    public boolean MoveLayerDown(String LayerName)
//    {
//        int FromIndex = this.IndexOf(this.GetLayerByName(LayerName));
//        this.SetLayerLevel(FromIndex, FromIndex + 1);
//        return true;
//    }

//    //���µ���ͼ��˳��
//    public boolean ReJustLayerIndex(List<String> newLayerSort)
//    {
//        List<GeoLayer> GeoList = new ArrayList<GeoLayer>(List.size());
//        for (GeoLayer pGeoLayer : List)
//        {
//            GeoList.add(pGeoLayer);
//        }
//        List.clear();
//
//        for (String LayerName : newLayerSort)
//        {
//            for (GeoLayer pGeoLayer : GeoList)
//            {
//                if (pGeoLayer.getName() == LayerName) List.add(pGeoLayer);
//            }
//        }
//        return true;
//    }


    public void Clear()
    {
    	this.List.clear();
    }


}
