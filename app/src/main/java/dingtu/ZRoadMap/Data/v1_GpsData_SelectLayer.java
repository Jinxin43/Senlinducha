package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_ExpandableBar;
import lkmap.ZRoadMap.Project.v1_Layer;

public class v1_GpsData_SelectLayer
{
	private v1_FormTemplate _Dialog = null; 
    public v1_GpsData_SelectLayer()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_gpsdata_selectlayer);
    	_Dialog.ReSetSize(1f,0.96f);

    	_Dialog.SetCaption(Tools.ToLocale("选择图层"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText));
    	
    	//设置当前正在采集数据图层，快速切换
    	for(int i=1;i<=3;i++)
    	{
    		Dataset pDataset = null;
    		if (i==1)pDataset = PubVar.m_DoEvent.m_GPSPoint.GetDataset();
    		if (i==2)pDataset = PubVar.m_DoEvent.m_GPSLine.GetDataset();
    		if (i==3)pDataset = PubVar.m_DoEvent.m_GPSPoly.GetDataset();
    		String LayerID = "";if (pDataset!=null)LayerID=pDataset.getId();
    		v1_Layer vLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(LayerID);
    		if (vLayer!=null)
    		{
    			_Dialog.findViewById(R.id.ll_datainlayer).setVisibility(View.VISIBLE);
    			
    			Button btButton = null;
    			if(vLayer.GetLayerType()==lkGeoLayerType.enPoint) btButton = (Button)_Dialog.findViewById(R.id.di_point);
    			if(vLayer.GetLayerType()==lkGeoLayerType.enPolyline) btButton = (Button)_Dialog.findViewById(R.id.di_line);  			
    			if(vLayer.GetLayerType()==lkGeoLayerType.enPolygon) btButton= (Button)_Dialog.findViewById(R.id.di_poly);
    			btButton.setVisibility(View.VISIBLE);
    			btButton.setText("["+vLayer.GetLayerAliasName()+"]  ");
    			btButton.setTag(vLayer.GetLayerID());
    			btButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						HashMap<String,Object> item = new HashMap<String,Object>();
						v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(v.getTag()+"");
						item.put("D1", pLayer.GetLayerAliasName());item.put("LayerId", pLayer.GetLayerID());
						m_SelectItem=item;
						pCallback.OnClick("确定", null);
					}});
    		}
    	}
    }
    
    //选择图层后的回调
    private ICallback _Callback = null;
    public void SetCallback(ICallback cb){this._Callback = cb;}
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
	    			HashMap<String,Object> item = (HashMap<String,Object>)m_SelectItem;
	    			String LayerName = item.get("D1").toString();
	    			String LayerID = item.get("LayerId").toString();
	    			v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(LayerID);
	    			if (_Callback!=null) _Callback.OnClick("当前图层", LayerName+","+LayerID+","+pLayer.GetLayerTypeName());
	    			_Dialog.dismiss();
	    		}
	    	}
		}};

	//选中的图层
	private Object m_SelectItem = null;
	
	
    /**
     * 加载图层列表信息
     */
    private void LoadLayerListInfo()
    {
    	_Dialog.GetButton("1").setEnabled(false);

    	//将图层信息绑定到列表中
    	v1_ExpandableBar ebPoint =(v1_ExpandableBar)_Dialog.findViewById(R.id.vExpandableBar_point);
    	v1_ExpandableBar ebLine =(v1_ExpandableBar)_Dialog.findViewById(R.id.vExpandableBar_line);
    	v1_ExpandableBar ebPoly =(v1_ExpandableBar)_Dialog.findViewById(R.id.vExpandableBar_poly);
    	List<HashMap<String,Object>> dataList_point = new ArrayList<HashMap<String,Object>>();
    	List<HashMap<String,Object>> dataList_line = new ArrayList<HashMap<String,Object>>();
    	List<HashMap<String,Object>> dataList_poly = new ArrayList<HashMap<String,Object>>();
    	
    	for(v1_Layer vLayer:PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList())
    	{
    		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(vLayer.GetLayerID());
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", vLayer.GetLayerAliasName());   		//图层别名
        	hm.put("D2", pDataset.GetAllObjectCount());			//图层内实体数量
        	hm.put("LayerId", vLayer.GetLayerID());
    		if (vLayer.GetLayerType()==lkGeoLayerType.enPoint)dataList_point.add(hm);
    		if (vLayer.GetLayerType()==lkGeoLayerType.enPolyline)dataList_line.add(hm);
    		if (vLayer.GetLayerType()==lkGeoLayerType.enPolygon)dataList_poly.add(hm);
    	}
    	
    	if (dataList_point.size()>0)
    	{
    		ebPoint.setVisibility(View.VISIBLE);
    		ebPoint.CreateBarHeader("【"+Tools.ToLocale("点图层")+"】");
    		ebPoint.SetItemList(dataList_point);
    		ebPoint.SetCallback(new ICallback(){
    			@Override
    			public void OnClick(String Str, Object ExtraStr) {
    				// TODO Auto-generated method stub
    				m_SelectItem = ExtraStr;
    				pCallback.OnClick("确定", null);
    			}});
    	}

      	if (dataList_line.size()>0)
    	{
      		if (!PubVar.m_DoEvent.m_GPSLine.CheckIfStarting())
      		{
	      		ebLine.setVisibility(View.VISIBLE);
	      		ebLine.CreateBarHeader("【"+Tools.ToLocale("线图层")+"】");
	      		ebLine.SetItemList(dataList_line);
	      		ebLine.SetCallback(new ICallback(){
	    			@Override
	    			public void OnClick(String Str, Object ExtraStr) {
	    				// TODO Auto-generated method stub
	    				m_SelectItem = ExtraStr;
	    				pCallback.OnClick("确定", null);
	    			}});
      		}
    	}
      	if (dataList_poly.size()>0)
    	{
      		if (!PubVar.m_DoEvent.m_GPSPoly.getGPSLine().CheckIfStarting())
      		{
	      		ebPoly.setVisibility(View.VISIBLE);
	      		ebPoly.CreateBarHeader("【"+Tools.ToLocale("面图层")+"】");
	      		ebPoly.SetItemList(dataList_poly);
	      		ebPoly.SetCallback(new ICallback(){
	    			@Override
	    			public void OnClick(String Str, Object ExtraStr) {
	    				// TODO Auto-generated method stub
	    				m_SelectItem = ExtraStr;
	    				pCallback.OnClick("确定", null);
	    			}});
      		}
    	}

    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadLayerListInfo();}});
    	_Dialog.show();
    }
}
