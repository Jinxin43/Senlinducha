package lkmap.ToolBar;

import java.util.ArrayList;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Cargeometry.Polygon;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Layer.GeoLayer;
import lkmap.MapControl.Tools;
import lkmap.Spatial.PolyTools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;

public class v1_Agent_Poly_PublicBorder implements v1_IToolsBarCommand {

	@Override
	public void OnDispose() {
	}
	
	@Override
	public void OnChange() {

	}
	
	@Override
	public void OnPrepare() {
	}

	
	private View m_View = null;
	public void SetView(View view)
	{
		this.m_View = view;
		this.m_View.findViewById(R.id.bt_drawline).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_snap).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_gps_start).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_clear).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_finish).setOnClickListener(new ViewClick());
	}
	
    public class ViewClick implements OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if (Tag.equals("�ֶ�����"))
    		{
    			//PubVar.m_MapControl.setActiveTools(Tools.MoveObject, v1_Agent_DrawlineEx.GetDrawLineEx(), v1_Agent_DrawlineEx.GetDrawLineEx());
    		}
    		if (Tag.equals("���"))
    		{
    			//v1_Agent_DrawlineEx.GetDrawLineEx().Clear();
    		}
    		
    		if (Tag.equals("ת��"))
    		{
    			//v1_Agent_DrawlineEx.GetDrawLineEx().ChangeEditDirection();
    		}
    		if (Tag.equals("���"))
    		{
    			StartPublicBorder();
    		}

//    		if (Tag.equals("ɾ��"))Delete();
//
//    		if (Tag.equals("����"))
//    		{
//    			PubVar.m_DoEvent.DoCommand(Tag);
//    		}
//    		if (Tag.equals("����"))
//    		{
//    			PubVar.m_DoEvent.DoCommand(Tag);
//    		}
    	}
    }
    
    public static void StartPublicBorder()
    {
    	//�ж��Ƿ��зָ�켣
    	if (v1_Agent_DrawlineEx.GetDrawLine_Poly().GetTrackPointList().size()==0)
    	{
    		lkmap.Tools.Tools.ShowMessageBox("�빴������ߣ�ע�⣺������㡢ֹ������������ٹ����ߵ����ڲ���");
    		return;
    	}
    	
    	//�ж��Ƿ�����㱻ѡ��
		if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
		if (!PubVar.m_DoEvent.m_GPSPoly.getGPSLine().CheckLayerValid())return;
    	
    	//�ж��Ƿ����Ѿ�ѡ�е���
        List<Polygon> SelectPolygonList = new ArrayList<Polygon>();
        
        
        
        for  (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).getList())
        {
        	
        	if (pGeoLayer.getType()!=lkGeoLayerType.enPolygon) continue;
        	List<Integer> emptyPoly = new ArrayList<Integer>();
            if (pGeoLayer.getSelSelection().getCount()>0)
            {
            	
                for(int ObjIndex:pGeoLayer.getSelSelection().getGeometryIndexList())
                {
                	Polygon pPolygon = (Polygon)pGeoLayer.getDataset().GetGeometry(ObjIndex);
                	if(pPolygon== null)
                	{
                		emptyPoly.add(ObjIndex);
                	}
                	else
                	{
                		SelectPolygonList.add(pPolygon);
                	}
                	
                }
                
                if(emptyPoly.size()>0)
                {
                	pGeoLayer.getDataset().AddGeometryOutEnvelope(emptyPoly);
                	for(int id:emptyPoly)
                	{
                		Polygon pPolygon = (Polygon)pGeoLayer.getDataset().GetGeometry(id);
                    	if(pPolygon== null)
                    	{
                    		lkmap.Tools.Tools.ShowMessageBox("ѡ������Ѿ����Ƶ��ǿ�������");
                    	}
                    	else
                    	{
                    		SelectPolygonList.add(pPolygon);
                    	}
                	}
                	
                }
            }
        }
        
        if (SelectPolygonList.size() == 0)
        {
        	lkmap.Tools.Tools.ShowMessageBox("��ѡ�����ٹ����ߵ��棡");
            return;
        } 
        else
        {

        	//��ʼ���㹫�������
        	List<Polygon> ResultSubPolygonList = PolyTools.PublicBorderPoly(SelectPolygonList,v1_Agent_DrawlineEx.GetDrawLine_Poly().GetTrackPointList());
    		if (ResultSubPolygonList.size()>0)
    		{
    			//������
    	    	UnRedoParaStru UnRedoPara = new UnRedoParaStru();
    	        UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;
    	        
				UnRedoDataItem urDataItem = new UnRedoDataItem();
				urDataItem.Type = lkReUndoFlag.enRedo;
				
				IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
				urDataItem.DataList.add(urDelete);
				urDelete.LayerId = PubVar.m_DoEvent.m_GPSPoly.getGPSLine().GetDataset().getId();
				
    			v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
    			gpsDataObj.SetDataset(PubVar.m_DoEvent.m_GPSPoly.getGPSLine().GetDataset());
    			for(Polygon pPoly:ResultSubPolygonList)
    			{
    				gpsDataObj.SetSYS_ID(-1);
    				int newId = gpsDataObj.SaveGeoToDb(pPoly, pPoly.getLength(true), pPoly.getArea(true));
    				if (newId>=0)
    				{
    					urDelete.ObjectIdList.add(newId);
    				}
    			}
    			
				UnRedoPara.DataItemList.add(urDataItem);
				IUnRedo.AddHistory(UnRedoPara);
    		}
    		v1_Agent_DrawlineEx.GetDrawLine_Poly().Cancel();
        	
	        //���ѡ�񼯺�
	        PubVar.m_Map.ClearSelection();    	
        	PubVar.m_Map.Refresh();
        }
        
        
    }
    
    

}
