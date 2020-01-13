package lkmap.Edit;

import java.util.ArrayList;
import java.util.List;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Layer.GeoLayer;
import lkmap.Tools.Tools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;

public class DeleteAddObject
{
    public boolean Delete()
    {
        String ProInfo = "";
        for  (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList())
        {
            if (pGeoLayer.getDataset().getDataSource().getEditing() && pGeoLayer.getSelSelection().getCount()>0)
            {
                ProInfo += pGeoLayer.GetAliasName() + "��"+pGeoLayer.getSelSelection().getCount()+"��\n";
            }
        }
        if (ProInfo == "")
        {
        	lkmap.Tools.Tools.ShowMessageBox("���ڿɱ༭ͼ����ѡ����Ҫɾ����ʵ�壡");
            return true;
        }

        Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context,Tools.ToLocale(ProInfo+"\r\n�Ƿ�ȷ��ɾ�����ϱ�ѡ��ʵ�壿\r\n"), new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if (Str.equals("YES"))
				{
					List<IURDataItem_DeleteAdd> daList = new ArrayList<IURDataItem_DeleteAdd>();
			        for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList())
			        {
			            if (pGeoLayer.getDataset().getDataSource().getEditing() && pGeoLayer.getSelSelection().getCount()>0)
			            {
			            	IURDataItem_DeleteAdd da = new IURDataItem_DeleteAdd();
			            	da.LayerId = pGeoLayer.getDataset().getId();
			            	for(int ObjId:pGeoLayer.getSelSelection().getGeometryIndexList())
			            	{
			            		da.ObjectIdList.add(ObjId);
			            	}
			            	daList.add(da);
			            }
			        }
			        Delete(daList,true);
				}
			}});
		return true;
    }
    
    //ɾ��ʵ�壬�ɻָ�
    public void Delete(List<IURDataItem_DeleteAdd> urDeleteAddList,boolean AddIUnRedo)
    {
        for(IURDataItem_DeleteAdd IurDa : urDeleteAddList)
        {
            Dataset pDataset = PubVar.m_Workspace.GetDatasetById(IurDa.LayerId);
             List<Integer> SYS_IDList = IurDa.ObjectIdList;

            //�����ݿ��д���ɾ����ʶ�������Ժ�ָ�
            String SQL_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in (%2$s)";
            //String SQL_Del = "delete %1$s where SYS_ID in (%2$s)";
            SQL_Del = String.format(SQL_Del, pDataset.getDataTableName(),lkmap.Tools.Tools.Join(",", SYS_IDList));
            if (pDataset.getDataSource().ExcuteSQL(SQL_Del))
            {
                //����ʵ���״̬
                for(int SYS_ID:SYS_IDList)
                {
                	if(pDataset.GetGeometry(SYS_ID) != null)
                	{
                		pDataset.GetGeometry(SYS_ID).setStatus(lkGeometryStatus.enDelete);
                	}
                }
            }
        }
        
    	if (AddIUnRedo)
    	{
	    	UnRedoParaStru UnRedoPara = new UnRedoParaStru();
	        UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;
	        UnRedoDataItem urDataItem = new UnRedoDataItem();
	        urDataItem.Type = lkReUndoFlag.enUndo;
	        for(IURDataItem_DeleteAdd da:urDeleteAddList)urDataItem.DataList.add(da);
	        UnRedoPara.DataItemList.add(urDataItem);
	        IUnRedo.AddHistory(UnRedoPara);
    	}
        //���ѡ�񼯺�
        PubVar.m_Map.ClearSelection();
        PubVar.m_Map.FastRefresh();
    }
}


