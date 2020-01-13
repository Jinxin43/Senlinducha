package lkmap.ToolBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.DTGIS.DataService.DuChaDB;
import com.dingtu.senlinducha.R;

import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Edit.CopyFeature;
import lkmap.Edit.DeleteAddObject;
import lkmap.Edit.DrawlineEx;
import lkmap.Edit.MoveObject;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Layer.GeoLayer;
import lkmap.MapControl.Tools;
import lkmap.Spatial.LineTools;
import lkmap.Spatial.PolyTools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;

public class v1_Agent_Line_Split implements v1_IToolsBarCommand {

	
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
//		this.m_View.findViewById(R.id.bt_drawline).setOnClickListener(new ViewClick());
//		this.m_View.findViewById(R.id.bt_snap).setOnClickListener(new ViewClick());
//		this.m_View.findViewById(R.id.bt_gps_start).setOnClickListener(new ViewClick());
//		this.m_View.findViewById(R.id.bt_clear).setOnClickListener(new ViewClick());
//		this.m_View.findViewById(R.id.bt_changearrow).setOnClickListener(new ViewClick());
//		this.m_View.findViewById(R.id.bt_finish).setOnClickListener(new ViewClick());
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
    			StartSplit();
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
    
    public static void StartSplit()
    {
    	//�ж��Ƿ��зָ�켣
    	if (v1_Agent_DrawlineEx.GetDrawLine_Line().GetTrackPointList().size()==0)
    	{
    		lkmap.Tools.Tools.ShowMessageBox("�빴��ָ��ߣ�");
    		return;
    	}
    	
    	//�ж��Ƿ��ڿɱ༭ͼ�����Ѿ�ѡ�е���
        List<HashMap<String,Object>> selectObjectList = new ArrayList<HashMap<String,Object>>();
        for  (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList())
        {
        	if (pGeoLayer.getType()!=lkGeoLayerType.enPolyline) continue;
            if (pGeoLayer.getDataset().getDataSource().getEditing() && pGeoLayer.getSelSelection().getCount()>0)
            {
                for(int ObjIndex:pGeoLayer.getSelSelection().getGeometryIndexList())
                {
                	HashMap<String,Object> selectObj = new HashMap<String,Object>();
                	selectObj.put("GeoLayer", pGeoLayer);
                	selectObj.put("ObjIndex", ObjIndex);
                	selectObjectList.add(selectObj);
                }
            }
        }
        if (selectObjectList.size() != 1)
        {
        	lkmap.Tools.Tools.ShowMessageBox("���ڿɱ༭ͼ����ѡ����Ҫ��ϵ�һ���ߣ�");
            return;
        } 
        else
        {
        	HashMap<String,Object> selObj=selectObjectList.get(0);
    		GeoLayer pGeoLayer = (GeoLayer)selObj.get("GeoLayer");
    		int ObjIndex = Integer.parseInt(selObj.get("ObjIndex")+"");
    		Polyline pPolyline = (Polyline)pGeoLayer.getDataset().GetGeometry(ObjIndex);
    		List<Polyline> SubLineList = LineTools.StartSplit(pPolyline,v1_Agent_DrawlineEx.GetDrawLine_Line().GetTrackPointList());
    		if (SubLineList==null)
    		{
    			lkmap.Tools.Tools.ShowMessageBox("û���ҵ�Ψһ���㣬�����»��Ʒָ��ߣ�");
    			return;
    		}
    		
    		if (SubLineList.size()>1)
    		{
    			//���Ը���
    			HashMap<String,String> FeatureList = CopyFeature.CopyFrom(pGeoLayer.getDataset(), ObjIndex);
    				
    	    	UnRedoParaStru UnRedoPara = new UnRedoParaStru();
    	        UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;

            	v1_CGpsDataObject gpsDataObj = new v1_CGpsDataObject();
            	
    	        //������
				UnRedoDataItem urDataItem = new UnRedoDataItem();
				urDataItem.Type = lkReUndoFlag.enRedo;
				IURDataItem_DeleteAdd urDelete = new IURDataItem_DeleteAdd();
				urDelete.LayerId = pGeoLayer.getDataset().getId();
				urDataItem.DataList.add(urDelete);
				
				
				//�����·ָ������ʵ��
    			gpsDataObj.SetDataset(pGeoLayer.getDataset());
    			for(Polyline pLine:SubLineList)
    			{
    				gpsDataObj.SetSYS_ID(-1);
    				int newId = gpsDataObj.SaveGeoToDb(pLine, pLine.getLength(true), -1);
    				if (newId>=0)
    				{
    					if (CopyFeature.CopyTo(FeatureList, pGeoLayer.getDataset(), newId)) urDelete.ObjectIdList.add(newId);
    				}
    			}
    			UnRedoPara.DataItemList.add(urDataItem);
    			
    			
    			
    			
    			
    			
                //�����ݿ��д���ɾ����ʶ�������Ժ�ָ�
                String SQL_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in ('%2$s')";
                SQL_Del = String.format(SQL_Del, pGeoLayer.getDataset().getDataTableName(),ObjIndex);
                if (pGeoLayer.getDataset().getDataSource().ExcuteSQL(SQL_Del))
                {
                	pPolyline.setStatus(lkGeometryStatus.enDelete);
                    
                    //������
					UnRedoDataItem urDataItem2 = new UnRedoDataItem();
					urDataItem2.Type = lkReUndoFlag.enUndo;
					IURDataItem_DeleteAdd urDelete2 = new IURDataItem_DeleteAdd();
					urDelete2.LayerId = pGeoLayer.getDataset().getId();
					urDelete2.ObjectIdList.add(ObjIndex);
					urDataItem2.DataList.add(urDelete2);
					UnRedoPara.DataItemList.add(urDataItem2);
                }

    	        //���ѡ�񼯺�
    	        PubVar.m_Map.ClearSelection();
            	IUnRedo.AddHistory(UnRedoPara);
            	v1_Agent_DrawlineEx.GetDrawLine_Line().Cancel();
            	PubVar.m_Map.Refresh();
            	
            	
            	
    		}
        }
    }
    
  
    
    
    

}
