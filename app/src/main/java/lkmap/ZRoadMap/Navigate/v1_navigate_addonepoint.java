package lkmap.ZRoadMap.Navigate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import dingtu.ZRoadMap.HashValueObject;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.view.ViewGroup;
import lkmap.Cargeometry.Coordinate;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkCoorTransMethod;
import lkmap.Map.StaticObject;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Button_Center;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.MyControl.v1_ViewPager;
import lkmap.ZRoadMap.Project.v1_Layer;

public class v1_navigate_addonepoint
{
	private v1_FormTemplate _Dialog = null; 
	//导航点列表绑定的数据项
	private List<HashMap<String,Object>> m_DataItemList = new ArrayList<HashMap<String,Object>>();
		
    public v1_navigate_addonepoint(boolean isCalPoint)
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_navigate_addonepoint);
    	_Dialog.ReSetSize(0.6f,0.8f);
    	_Dialog.SetCaption("增加导航点");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",确定 ,确定", pCallback);
    	
    	v1_Button_Center bc3 = (v1_Button_Center)_Dialog.findViewById(R.id.bt_addpoint_file);
    	v1_Button_Center bc1 = (v1_Button_Center)_Dialog.findViewById(R.id.bt_addpoint_measure);
    	if(isCalPoint)
    	{
    		_Dialog.SetCaption("测量点导航");
    		bc3.setVisibility(View.GONE);
	    	bc1.SetText("测量点");bc1.SetImage(R.drawable.v1_addcheckpoint);
	    	bc1.GetButton().setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) 
				{
					if(PubVar.m_DoEvent.m_Agent_Measure != null)
					{
						Coordinate coor = PubVar.m_DoEvent.m_Agent_Measure.getFirstPoint();
						if(coor != null)
						{
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_x, coor.getX()+"");
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_y, coor.getY()+"");
						}
						else
						{
							Tools.ShowMessageBox("请先用测量工具获取基准点");
						}
					}
					else
					{
						Tools.ShowMessageBox("请先用测量工具获取基准点");
					}
				}});
    	}
    	else
    	{
    		_Dialog.SetCaption("坐标点导航");
    		bc1.setVisibility(View.GONE);
    		
        	bc3.SetText("导入坐标点");
        	bc3.SetImage(R.drawable.v1_editcheckpoint);
        	bc3.GetButton().setOnClickListener(new OnClickListener(){
    			@Override
    			public void onClick(View v) 
    			{
    				
    			}});
    	}
    	
    	v1_Button_Center bc2 = (v1_Button_Center)_Dialog.findViewById(R.id.bt_addpoint_delete);
    	//bc1.SetText("测量点");bc1.SetImage(R.drawable.v1_addcheckpoint);
    	bc2.GetButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				deleteSelectPoint();
			}});
    	
    	
//    	
//    	v1_Button_Center bc2 = (v1_Button_Center)_Dialog.findViewById(R.id.bt_addpoint);
//    	bc2.SetText("增加导航点");bc2.SetImage(R.drawable.v1_addcheckpoint);
//    	bc2.GetButton().setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) 
//			{
//				AddNavigatePoint();
//			}}); 
    	
    	Button btnAddPoint = (Button)_Dialog.findViewById(R.id.bt_addpoint);
    	btnAddPoint.setOnClickListener(new OnClickListener() 
    	{
			
			@Override
			public void onClick(View v) 
			{
				AddNavigatePoint();
			}
		});
    	
    	
    	refreshPointList();
    }
    
    private void deleteSelectPoint()
    {
    	final ArrayList<HashMap<String,Object>> selectID = new ArrayList<HashMap<String,Object>>();
    	for(HashMap<String,Object> coord:m_DataItemList)
		{
			if (Boolean.parseBoolean(coord.get("D1")+""))
			{
				selectID.add(coord);
			}
		}
    	
    	if(selectID.size()>0)
    	{
    		String MessageStr = "是否删除选择的"+ selectID.size() +"个坐标点？";
    		Tools.ShowYesNoMessage(_Dialog.getContext(), MessageStr, new ICallback(){
    			@Override
    			public void OnClick(String Str, Object ExtraStr) 
    			{
    				for(HashMap<String,Object> coord:selectID)
    				{
    					m_DataItemList.remove(coord);
    				}
    				refreshPointList();
    			}});
    	}
    	
    }
    
    private void AddNavigatePoint()
    {
		String CoorStrX = Tools.GetTextValueOnID(_Dialog, R.id.et_x).trim();
		String CoorStrY = Tools.GetTextValueOnID(_Dialog, R.id.et_y).trim();
		
		//校验坐标格式，以空格分割
		if (!(Tools.IsDouble(CoorStrX) && Tools.IsDouble(CoorStrY)))
		{
			Tools.ShowMessageBox(_Dialog.getContext(),"坐标格式不符要求！");
			return;
		}
		
		//判断是经纬度还是平面坐标
		Coordinate newCoor = null;
		double X = Tools.ConvertToDouble(CoorStrX);
		double Y = Tools.ConvertToDouble(CoorStrY);
		if ((X>0 && X<180) && (Y>0 && Y<90))
		{
			newCoor = StaticObject.soProjectSystem.WGS84ToXY(X, Y,0);
		} 
		else
		{
			newCoor = new Coordinate(X,Y);
		}
		
		if(newCoor == null)
		{
			return;
		}
		HashMap<String,Object> hm = new HashMap<String,Object>();
		hm.put("D1", true);
    	hm.put("D2", Tools.GetTextValueOnID(_Dialog,R.id.et_name).trim());
    	hm.put("D3", newCoor.getX());
    	hm.put("D4", newCoor.getY());  	
    	this.m_DataItemList.add(hm);
    	refreshPointList();
    	
    }
    
    private void refreshPointList()
    {
    	//绑定图层列表
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_listview), "导航点列表");
    	
    	hvf.BindDataToListView(this.m_DataItemList);
    	
    	
    }
    
    //按钮事件
    private ICallback pCallback = new ICallback()
    {
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("返回"))
	    	{
	    		_Dialog.dismiss();
		    	
	    	}
	    	
	    	if (Str.equals("确定"))
	    	{
	    		if(m_Callback != null)
	    		{
	    			ArrayList<HashMap<String,Object>> selectCoordList = new ArrayList<HashMap<String,Object>>();
	    			for(HashMap<String,Object> coord:m_DataItemList)
	    			{
	    				if (Boolean.parseBoolean(coord.get("D1")+""))
						{
	    					selectCoordList.add(coord);
						}
	    			}
	    			m_Callback.OnClick("坐标点", selectCoordList);
	    		}
	    		_Dialog.dismiss();
	    	}
		}};

	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb)
	{
		this.m_Callback = cb;
	}
	

    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				
			}}
    	);
    	_Dialog.show();
    }
}
