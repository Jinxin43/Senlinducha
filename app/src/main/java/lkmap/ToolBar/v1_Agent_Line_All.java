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
		//清空按钮的选择状态
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
    		if (Tag.equals("移动节点"))
    		{
    			SetVertexMode(1);
    		}
    		if (Tag.equals("增加节点"))
    		{
    			SetVertexMode(2);
    		}
    		if (Tag.equals("删除节点"))
    		{
    			SetVertexMode(3);
    		}
    		if (Tag.equals("打断"))
    		{
    			v1_Agent_Line_Split.StartSplit();
    		}
    		if (Tag.equals("修形"))
    		{
    			v1_Agent_Line_Reshape.StartReshape();
    		}
    		if (Tag.equals("转向"))
    		{
    	    	//判断是否在可编辑图层有已经选中的面
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
    	        	lkmap.Tools.Tools.ShowMessageBox("请在可编辑图层中选择需要转向的线！");
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
    		if (Tag.equals("连接"))
    		{
    			v1_Agent_Line_Merge.StartMerge();
    		}
    	}
    }
    
    private Vertex m_Vertex = null;				//节点编辑工具
    
    //设置节点模式
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
