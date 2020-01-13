package lkmap.Edit;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Polygon;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Layer.GeoLayer;
import lkmap.MapControl.ICommand;
import lkmap.MapControl.IOnPaint;
import lkmap.MapControl.IOnTouchCommand;
import lkmap.MapControl.MapControl;
import lkmap.UnRedo.IURDataItem_Move;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;

public class MoveObject implements IOnTouchCommand,IOnPaint
{

	private MapControl _MapControl = null;
	
	public MoveObject(MapControl mapControl)
	{
		this._MapControl = mapControl;
	}
	

	//移动起点
	private PointF m_StartPoint = null;
	
	//移动标识
	private boolean m_Moveing = false;
	
	//移动偏移量
	private float m_OffsetX = 0;
	private float m_OffsetY = 0;                   
	
	public void MouseDown(MotionEvent e) 
	{
		this.m_StartPoint = new PointF(e.getX(),e.getY());
	}

	public void MouseMove(MotionEvent e) 
	{
		if (this.m_StartPoint==null) return;
		this.m_OffsetX = e.getX() - this.m_StartPoint.x;
		this.m_OffsetY = e.getY() - this.m_StartPoint.y;
		this.m_Moveing = true;
		this._MapControl.invalidate();
	}

	public void MouseUp(MotionEvent e)
	{
		if (this.m_Moveing)
		{
			Coordinate FromPoint = this._MapControl.getMap().getViewConvert().ScreenToMap(this.m_StartPoint.x,this.m_StartPoint.y);
            Coordinate ToPoint = this._MapControl.getMap().getViewConvert().ScreenToMap(e.getX(),e.getY());
            double delX = ToPoint.getX() - FromPoint.getX();
            double delY = ToPoint.getY() - FromPoint.getY();
            
            List<IURDataItem_Move> urMoveList = new ArrayList<IURDataItem_Move>();
	        for (GeoLayer pGeoLayer : _MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorEditingData).getList())
	        {
	        	Dataset pDataset = pGeoLayer.getDataset();
	        	if (pDataset.getDataSource().getEditing())
	        	{
	        		IURDataItem_Move urMove = null;
	                for (int SYS_ID : pGeoLayer.getSelSelection().getGeometryIndexList())
	                {
	                	if (urMove==null)urMove = new IURDataItem_Move();
	                	urMove.LayerId = pDataset.getId();
	                	urMove.ObjectIdList.add(SYS_ID);
	                	urMove.OffsetX = delX;
	                	urMove.OffsetY = delY;
	                }
	                if (urMove!=null)urMoveList.add(urMove);
	        	}
	        }
	        this.MoveObject(urMoveList, true);
		}
		this.m_StartPoint = null;
		this.m_Moveing = false;
		this._MapControl.getMap().Refresh();
	} 
	
	/**
	 * 移动实体
	 * @param urMoveList 需要移动实体参数
	 * @param AddIUndo 是否加入回退栈中
	 */
	public void MoveObject(List<IURDataItem_Move> urMoveList,boolean AddIUndo)
	{
		for(IURDataItem_Move urMove:urMoveList)
		{
			String LayerId = urMove.LayerId;
			List<Integer> ObjIdList = urMove.ObjectIdList;
			double OffsetX = urMove.OffsetX;
			double OffsetY = urMove.OffsetY;
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(LayerId);
			if (pDataset==null)continue;
			
			for(int SYS_ID:ObjIdList)
			{
		    	Geometry pGeometry = pDataset.GetGeometry(SYS_ID);
		    	if (pGeometry==null) continue;
		    	pGeometry.UpdateCoordinate(OffsetX, OffsetY);
		    	pGeometry.CalEnvelope();
		    	
		    	//将结果实时存入数据库内
		    	v1_CGpsDataObject gpsObject = new v1_CGpsDataObject();
		    	gpsObject.SetDataset(pDataset);
		    	gpsObject.SetSYS_ID(SYS_ID);
		    	if (gpsObject.SaveGeoToDb(pGeometry, -1, -1)==SYS_ID)  //-1表示不更新Length，Area
		    	{
			    	//如果是面层而更新中心点
			    	if (pDataset.getType()==lkGeoLayerType.enPolygon) ((Polygon)pGeometry).UpdateInnerPoint();
		    	}
			}
		}

        //加入回退栈中
		if (urMoveList.size()>0)
		{
	        UnRedoParaStru UnRedoPara = new UnRedoParaStru();
	        UnRedoPara.Command =  lkmap.Enum.lkReUndoCommand.enMoveObject;
	        UnRedoDataItem DataItem = new UnRedoDataItem();
	        for(IURDataItem_Move urMove:urMoveList)DataItem.DataList.add(urMove);
	        DataItem.Type = lkReUndoFlag.enUndo;
	        UnRedoPara.DataItemList.add(DataItem);
	        IUnRedo.AddHistory(UnRedoPara);
		}
	}
	
	@Override
	public void OnPaint(Canvas canvas) 
	{
        if (m_Moveing)
        {
        	for (GeoLayer pGeoLayer : _MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorEditingData).getList())
            {
        		if (pGeoLayer.getDataset().getDataSource().getEditing())
                {
        			pGeoLayer.DrawSelection(pGeoLayer.getSelSelection(), canvas, (int)this.m_OffsetX, (int)this.m_OffsetY);
                }
            }
        }
	}

	@Override
	public void SetOnTouchEvent(MotionEvent event) 
	{
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				this.MouseDown(event);
				break;
			case MotionEvent.ACTION_UP:
				this.MouseUp(event);
				break;
			case MotionEvent.ACTION_MOVE:
				this.MouseMove(event);
				break;
		}
	}

}
