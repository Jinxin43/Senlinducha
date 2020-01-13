package lkmap.Layer;

import java.util.*;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import dingtu.ZRoadMap.PubVar;
import android.graphics.Path;
import android.graphics.Paint.Join;
import android.graphics.PointF;

import lkmap.Cargeometry.Envelope;
import lkmap.Enum.lkSelectionType;
import lkmap.Enum.lkTextPosition;
import lkmap.Map.Map;
import lkmap.Symbol.TextSymbol;

public class GridLayers
{
	private Map m_Map = null;
	/**
	 * ��ʼ��GridLayers����
	 * @param _map
	 */
    public GridLayers(Map _map)
    {
        this.m_Map = _map;
    }
    
	private List<GridLayer> List = new ArrayList<GridLayer>();
	
	public List<GridLayer> GetList()
	{
		return List;
	}

	/**
	 * ��ȡդ��ͼ��������Ӿ��Σ������ж�����
	 * @return
	 */
    public Envelope GetExtend()
    {
    	if (!this._ShowGird) return null;
    	
		//դ��ͼ�������Ӿ���
    	Envelope _ExtendForView = new Envelope(0,0,0,0);
    	for(HashMap<String,Object> hmObj:this.m_MapFileList)
    	{
			double MinX = Double.parseDouble(hmObj.get("MinX")+"");
			double MinY = Double.parseDouble(hmObj.get("MinY")+"");
			double MaxX = Double.parseDouble(hmObj.get("MaxX")+"");
			double MaxY = Double.parseDouble(hmObj.get("MaxY")+"");
			Envelope gridExtend = new Envelope(MinX, MaxY, MaxX, MinY);
			
        	if (_ExtendForView.IsZero())_ExtendForView = gridExtend;
        	else _ExtendForView = _ExtendForView.Merge(gridExtend);
    	}
    	return _ExtendForView;
    }
    
    //��ʾ
    private boolean _ShowGird = false;  
    public void SetShowGrid(boolean visible)
    {
    	this._ShowGird = visible;
		for(GridLayer gLayer:this.List)
		{
			gLayer.SetShowGrid(visible);
		}
    }
    
    
    //������ͼ�ļ��б�
    private List<HashMap<String,Object>> m_MapFileList = null;
    
    /**
     * ������Ҫ��̬���صı�����ͼ�ļ��б���ʽ���v1_BKLayerExplorer.SaveBKLayer()
     */
    public void SetMapFileList(List<HashMap<String,Object>> mapFileList)
    {
    	if (this.m_MapFileList==null)this.m_MapFileList = new ArrayList<HashMap<String,Object>>();
    	this.m_MapFileList.clear();
    	for(int i=mapFileList.size()-1;i>=0;i--)
    	{
    		this.m_MapFileList.add(mapFileList.get(i));
    	}
    }

    //�б����󳤶ȣ���������󳤶Ⱥ�ֻ��ʾդ��ͼ��������Ϣ��Ҳ����դ��ͼ��Χ��ע��
    private int LIST_MAX = 4;
    
    public void Refresh()
    {
    	if (!this._ShowGird) return;
    	//��̬��ʶ��
    	String DynamicFilterStr = UUID.randomUUID().toString();
    	this.m_OnlyShowGridIndex=false;
    	
    	//��̬�ж���Ҫ������Щդ��ͼ������Ϊ��ǰ��ʾ��Χ�ڰ�����դ��ͼ
    	if (this.m_MapFileList==null){for(GridLayer gLayer:List)gLayer.UnloadGird();return;}
    	this.m_NeedLoadGridFileList.clear();
    	List<HashMap<String,Object>> NewNeedLoadGridFileList = new ArrayList<HashMap<String,Object>>();
    	for(HashMap<String,Object> hmObj:this.m_MapFileList)
    	{
    		//դ��ͼ�������Ӿ���
    		double MinX = Double.parseDouble(hmObj.get("MinX")+"");
    		double MinY = Double.parseDouble(hmObj.get("MinY")+"");
    		double MaxX = Double.parseDouble(hmObj.get("MaxX")+"");
    		double MaxY = Double.parseDouble(hmObj.get("MaxY")+"");
    		Envelope gridExtend = new Envelope(MinX, MaxY, MaxX, MinY);
    		
        	//��ȡ��ǰ��ͼ�µ������Ӿ��η�Χ
        	Envelope evp = this.m_Map.getExtend();

        	//�ж��Ƿ��ڵ�ǰ��ͼ��Χ��
        	if (evp.Intersect(gridExtend))
    		{
        		this.m_NeedLoadGridFileList.add(hmObj);
    			
    			//�ڵ�ǰ��List<GridLayer>���ö�̬���ر�ʶ��Ҳ����List<GridLayer>�Ѿ�������Ҫ��ʾ��
    			boolean inList = false;
    			for(GridLayer gLayer:this.List)
    			{
    				if (gLayer.GetGridDataFile().equals(hmObj.get("MapFileName")+""))
    				{
    					gLayer.DynamicFilterStr = DynamicFilterStr;
    					inList = true;
    				}
    			}
    			if (!inList)NewNeedLoadGridFileList.add(hmObj);   //�������Ҫ���¶�ȡ��
    		}
    	}
    	
    	//�ж��Ƿ񳬹������ʾ�б�����������ֻ��ʾͼ��������Ϣ
    	if (this.m_NeedLoadGridFileList.size()>this.LIST_MAX)
    	{
    		//����֮ǰ��ʾ����դ��ͼ
    		for(GridLayer gLayer:List)gLayer.ClearAllCache();
    		
    		//����դ��ͼ��������Ϣ
    		this.m_OnlyShowGridIndex = true;

    	} 
    	else   //û�г��������ʾ�б��������ж�̬����
    	{
    		int KYGridLayerCount = 0;   //���õ�����
			for(GridLayer gLayer:this.List)if (!gLayer.DynamicFilterStr.equals(DynamicFilterStr)){gLayer.UnloadGird();KYGridLayerCount++;}
			
			//�ж��Ƿ���Ҫ��̬����
			for(int i=1;i<=NewNeedLoadGridFileList.size()-KYGridLayerCount;i++)
			{
				this.List.add(new GridLayer(this.m_Map));
			}
			
			//��̬����
    		for(HashMap<String,Object> hmObj:NewNeedLoadGridFileList)
    		{
    			for(GridLayer gLayer:this.List)
    			{
    				if (!gLayer.DynamicFilterStr.equals(DynamicFilterStr))
					{
    					gLayer.DynamicFilterStr = DynamicFilterStr;
						gLayer.SetGridDataFile(hmObj.get("BKMapFile")+"",hmObj.get("F1")+"");
						
						if (hmObj.containsKey("Transparent"))
						{
							gLayer.SetTransparent(Integer.parseInt(hmObj.get("Transparent")+""));
							gLayer.SetShowGrid(Boolean.parseBoolean(hmObj.get("Visible")+""));
						}
						break;
					}
    			}
    		}
    		
    		//ˢ����ʾ
			for(GridLayer gLayer:this.List)
			{
				if (gLayer.DynamicFilterStr.equals(DynamicFilterStr))gLayer.Refresh();
			}
    	}
    }
    
    //�Ƿ�ֻ��ʾͼ��������Ϣ
    private boolean m_OnlyShowGridIndex = true;
    private List<HashMap<String,Object>> m_NeedLoadGridFileList = new ArrayList<HashMap<String,Object>>();
    public void FastRefresh()
    {
    	if (!this._ShowGird) return;
    	if (this.m_OnlyShowGridIndex)
    	{
    		for(HashMap<String,Object> hmObj:this.m_NeedLoadGridFileList)
    		{
    			this.DrawGridFileIndex(hmObj);
    		}
    	}
    	for(GridLayer gLayer:List)gLayer.FastRefresh();
    }
    
    
    private Paint m_Pen = null;
    private TextSymbol m_TextSymbol = null;
    
    /**
     * ����դ��ͼ����������
     * @param mapFile
     */
    private void DrawGridFileIndex(HashMap<String,Object> mapFile)
    {
		//դ��ͼ�������Ӿ���
		double MinX = Double.parseDouble(mapFile.get("MinX")+"");
		double MinY = Double.parseDouble(mapFile.get("MinY")+"");
		double MaxX = Double.parseDouble(mapFile.get("MaxX")+"");
		double MaxY = Double.parseDouble(mapFile.get("MaxY")+"");
		PointF LT = this.m_Map.getViewConvert().MapToScreenF(MinX,MaxY);
		PointF RB = this.m_Map.getViewConvert().MapToScreenF(MaxX,MinY);
		
		//�������Χ��
		if (this.m_Pen==null)
		{
			this.m_Pen = new Paint();
			this.m_Pen.setStrokeWidth(3);
			this.m_Pen.setColor(Color.RED);
			this.m_Pen.setStyle(Style.STROKE);
		}
        if (this.m_TextSymbol == null) 
        {
        	this.m_TextSymbol = new TextSymbol();
        }
        
    	this.m_Map.getDisplayGraphic().drawRect(LT.x, LT.y, RB.x, RB.y, this.m_Pen);
    	
    	//����ͼ������
    	float TextX = (LT.x+RB.x)/2;
    	float TextY = (LT.y+RB.y)/2;
    	this.m_TextSymbol.Draw(this.m_Map.getDisplayGraphic(), TextX, TextY, mapFile.get("BKMapFile")+"", lkTextPosition.enCenter,lkSelectionType.enShow);
    }

}
