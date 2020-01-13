package lkmap.ToolBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Edit.Vertex;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkVertexEditType;
import lkmap.Layer.GeoLayer;
import lkmap.MapControl.Tools;
import lkmap.Spatial.LineTools;
import lkmap.Spatial.PolyTools;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;

public class v1_Agent_Line_All implements v1_IToolsBarCommand {

	@Override
	public void OnDispose() {
		//��հ�ť��ѡ��״̬
		this.ClearButtonSelect();
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
		this.m_View.findViewById(R.id.bt_movevertex).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_addvertex).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_delvertex).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_split).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_reshape).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_changeflip).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_merge).setOnClickListener(new ViewClick());
	}
	
    public class ViewClick implements OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if (Tag.equals("�ƶ��ڵ�"))
    		{
    			SetVertexMode(1);
    		}
    		if (Tag.equals("���ӽڵ�"))
    		{
    			SetVertexMode(2);
    		}
    		if (Tag.equals("ɾ���ڵ�"))
    		{
    			SetVertexMode(3);
    		}
    		if (Tag.equals("���"))
    		{
    			v1_Agent_Line_Split.StartSplit();
    		}
    		if (Tag.equals("����"))
    		{
    			v1_Agent_Line_Reshape.StartReshape();
    		}
    		if (Tag.equals("ת��"))
    		{
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
    	        if (selectObjectList.size() == 0)
    	        {
    	        	lkmap.Tools.Tools.ShowMessageBox("���ڿɱ༭ͼ����ѡ����Ҫת����ߣ�");
    	            return;
    	        }
    	        else
    	        {
    	        	for(HashMap<String,Object> selObj:selectObjectList)
    	        	{
    	        		GeoLayer pGeoLayer = (GeoLayer)selObj.get("GeoLayer");
    	        		int ObjIndex = Integer.parseInt(selObj.get("ObjIndex")+"");
    	        		Polyline pPolyline = (Polyline)pGeoLayer.getDataset().GetGeometry(ObjIndex);
    	        		pPolyline.Flip();
    	        	}
    	        	PubVar.m_Map.FastRefresh();
    	        }
    	        
    		}
    		if (Tag.equals("����"))
    		{
    			v1_Agent_Line_Merge.StartMerge();
    		}
    	}
    }
    
    private Vertex m_Vertex = null;				//�ڵ�༭����
    
    //���ýڵ�ģʽ
    private void SetVertexMode(int mode)
    {
    	if (this.m_Vertex==null)this.m_Vertex= new Vertex(PubVar.m_MapControl);
    	PubVar.m_MapControl.setActiveTools(Tools.MoveObject, this.m_Vertex, this.m_Vertex);
    	switch(mode)
    	{
	    	case 1:
	    		this.ClearButtonSelect();
	    		lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_movevertex),true);
	    		 this.m_Vertex.SetVertexEditType(lkVertexEditType.enMove);
	    		 break;
	    	case 2:
	    		this.ClearButtonSelect();
	    		lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_addvertex),true);	    		
	    		 this.m_Vertex.SetVertexEditType(lkVertexEditType.enAdd);
	    		 break;
	    	case 3:
	    		this.ClearButtonSelect();
	    		lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_delvertex),true);	    		
	    		 this.m_Vertex.SetVertexEditType(lkVertexEditType.enDelete);
	    		 break;
    	}
    }
    
    public void ClearButtonSelect()
    {
    	lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_movevertex),false);
    	lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_addvertex),false);
    	lkmap.Tools.Tools.SetToolsBarItemSelect(this.m_View.findViewById(R.id.bt_delvertex),false);
    	PubVar.m_DoEvent.m_MainBottomToolBar.ClearButtonSelect();
    }

}
