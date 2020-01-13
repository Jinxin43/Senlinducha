package lkmap.ToolBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.ImageView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Polygon;
import lkmap.Layer.GeoLayer;
import lkmap.Spatial.PolyTools;

public class PolyTool_PolySplit 
{
	private v1_FormTemplate dialogView = null;
	ArrayList<Polygon> selectedPoly = new ArrayList<Polygon>();
	ArrayList<Polygon> splitResult1 = new ArrayList<Polygon>();
	ArrayList<Polygon> splitResult2 = new ArrayList<Polygon>();
	ArrayList<Polygon> splitResult3 = new ArrayList<Polygon>();
	
	public PolyTool_PolySplit(List<HashMap<String,Object>> listSelPoly)
	{
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.polysplitresultoption);
		dialogView.ReSetSize(0.6f,0.8f);
		dialogView.SetCaption("面分割");
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+",确定 ,确定", pCallback);
		
		//dialogView.findViewById(R.id.bt_do1).setOnClickListener();
		
		
		for(HashMap<String,Object> hmPoly:listSelPoly)
		{
			GeoLayer layer = (GeoLayer)hmPoly.get("GeoLayer");
			int index = Integer.parseInt(hmPoly.get("ObjIndex")+"");
			Polygon pPolygon = (Polygon)layer.getDataset().GetGeometry(index);
			selectedPoly.add(pPolygon);
			
		}
		
		SplitPoly();
	}
	

	private void SplitPoly()
	{
		splitResult1.clear();
		splitResult2.clear();
		splitResult3.clear();
		
		ArrayList<Polygon> splitResult = new ArrayList<Polygon>();
		if(selectedPoly.size()>1)
		{
			Polygon p1 = selectedPoly.get(0);
			for(int i = 1;i<selectedPoly.size();i++)
			{
				List<Polygon> result = PolyTools.ClipPoly(p1, selectedPoly.get(i));
				splitResult1.addAll(result);
				splitResult2.addAll(result);
				splitResult3.addAll(result);
				
				splitResult2.addAll(PolyTools.RepatePoly(p1, selectedPoly.get(i)));
				splitResult3.addAll(PolyTools.ClipPoly(selectedPoly.get(i),p1));
			}
		}
		
		
		//imagePoly();
		
	}
	
	private void imagePoly()
	{
		ImageView do1 = (ImageView)dialogView.findViewById(R.id.iv_do_1);
		
	}
	
	//按钮事件
    private ICallback pCallback = new ICallback()
    {
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("返回"))
	    	{
	    		dialogView.dismiss();
		    	
	    	}
	    	
	    	if (Str.equals("确定"))
	    	{
	    		
	    		dialogView.dismiss();
	    	}
		}};
		
		
		public void ShowDialog()
	    {
	    	
			dialogView.setOnShowListener(new OnShowListener(){
				@Override
				public void onShow(DialogInterface dialog) 
				{
					
				}}
	    	);
			dialogView.show();
	    }
	
}
