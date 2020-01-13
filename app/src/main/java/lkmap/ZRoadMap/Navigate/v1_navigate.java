package lkmap.ZRoadMap.Navigate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import dingtu.ZRoadMap.HashValueObject;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.view.ViewGroup;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkCoorTransMethod;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.GPS.LocationEx;
import lkmap.Layer.GeoLayer;
import lkmap.Map.StaticObject;
import lkmap.MapControl.IOnPaint;
import lkmap.MapControl.Pan;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Button_Center;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.MyControl.v1_Project_New_CheckPoint_Adpter;
import lkmap.ZRoadMap.MyControl.v1_ViewPager;

public class v1_navigate implements IOnPaint 
{
	private String Id = "navigatePaint";
	private static v1_FormTemplate _Dialog = null;
	private Pan pan = null;	
	
    public v1_navigate()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_navigate);
    	_Dialog.ReSetSize(0.6f,0.8f);
    	_Dialog.SetCaption("导航");
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_clearscreen+",清空 ,清空", pCallback);
    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+",确定 ,确定", pCallback);

    	
    	Button btnCoordPoint = (Button)_Dialog.findViewById(R.id.bt_addpointm);
    	btnCoordPoint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				v1_navigate_addonepoint na = new v1_navigate_addonepoint(true);
				na.SetCallback(pCallback);
                na.ShowDialog();
			}
		});
    	
    	Button btnInputPoint = (Button)_Dialog.findViewById(R.id.bt_inputCoord);
    	btnInputPoint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				v1_navigate_addonepoint na = new v1_navigate_addonepoint(false);
				na.SetCallback(pCallback);
                na.ShowDialog();
			}
		});
    	
    	Button btnSelectedObj = (Button)_Dialog.findViewById(R.id.bt_addpoint_select);
    	btnSelectedObj.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				collectSelectedOjd();
				refreshNavList();
			}
		});
    	
    	pan = new Pan(PubVar.m_MapControl);
    	PubVar.m_MapControl.AddOnPaint(this.Id, this);
    	
    }
    
    private static Coordinate mGpsCoordinate = null;
    public void refreshNavigationData(Location location)
    {
    	if(location == null)
    	{
    		
    		mGpsCoordinate = null;
    	}
    	else
    	{
    		Coordinate coordinate = new Coordinate(location.getLongitude(),location.getLatitude());
    		coordinate.setZ(location.getAltitude());
    		mGpsCoordinate = StaticObject.soProjectSystem.WGS84ToXY(coordinate.getX(),coordinate.getY(),coordinate.getZ());
    		if(mGpsCoordinate == null)
    		{
    			return;
    		}
    		
    		for(HashMap<String,Object> point:navaPoints)
    		{
    			Double navX = (Double)point.get("D2");
    			Double navY = (Double)point.get("D3");
    			int distance = (int)Tools.GetTwoPointDistance(navX,navY,mGpsCoordinate.getX(),mGpsCoordinate.getY(),false);
    			int dis1 = 0;
    			int dis2 = 0;
    			String dirction1 = "";
    			String dirction2 = "";
    			point.put("D4", distance);
    			String direction = "";
    			if(mGpsCoordinate.getX()<navX)
    			{
    				dirction1 = "东";
    				dis1 = (int)(navX-mGpsCoordinate.getX());
    				direction += "东";
    			}
    			else
    			{
    				dirction1 = "西";
    				dis1 = (int)(mGpsCoordinate.getX()-navX);
    				direction += "西";
    			}
    			
    			point.put("D5", dirction1);
				point.put("D6",dis1);
				
    			if(mGpsCoordinate.getY()<navY)
    			{
    				dirction2 = "北";
    				dis2 = (int)(navY - mGpsCoordinate.getY());
    				direction += "北";
    			}
    			else
    			{
    				dirction2 = "南";
    				dis2 = (int)(mGpsCoordinate.getY()-navY);
    				direction += "南";
    			}
    			
    			point.put("D7", dirction2);
				point.put("D8",dis2);
				
    			double tanjiao = 0d;
    			if(direction.equals("东北"))
    			{
    				tanjiao = (navX - mGpsCoordinate.getX())/(navY-mGpsCoordinate.getY());
    				tanjiao = Math.atan(tanjiao)*180/Math.PI;
    				point.put("D9","北偏东"+(int)tanjiao+""+"°");
    			}
    			
    			if(direction.equals("东南"))
    			{
    				tanjiao = (mGpsCoordinate.getY()-navY)/(navX - mGpsCoordinate.getX());
    				tanjiao = Math.atan(tanjiao)*180/Math.PI;
    				point.put("D9","东偏南"+(int)tanjiao+""+"°");
    			}
    			
    			if(direction.equals("西南"))
    			{
    				tanjiao = (mGpsCoordinate.getX()-navX)/(mGpsCoordinate.getY()-navY);
    				tanjiao = Math.atan(tanjiao)*180/Math.PI;
    				point.put("D9","西偏南"+(int)tanjiao+""+"°");
    			}
    			
    			if(direction.equals("西北"))
    			{
    				tanjiao = (navY-mGpsCoordinate.getY())/(mGpsCoordinate.getX()-navX);
    				tanjiao = Math.atan(tanjiao)*180/Math.PI;
    				point.put("D9","西偏北"+(int)tanjiao+""+"°");
    			}
    			
    			CheckBox cbSound= (CheckBox)_Dialog.findViewById(R.id.cb_sound);
    			if(cbSound.isChecked())
    			{
    				EditText etDis= (EditText)_Dialog.findViewById(R.id.et_dis);
    				
    				try
    				{
    					double dis = Double.valueOf(etDis.getText().toString());
    					if(distance <dis)
    					{
    						//TODO:play sounds;
    					}
        				
    				}
    				catch(Exception ex)
    				{
    					
    				}
    				
    			}
    		}
    		
    		if(navaPoints.size()>0)
    		{
    			refreshNavList();
    			
    		}
    	}
    }
    
    @Override
	public void OnPaint(Canvas canvas) 
	{
    	this.pan.OnPaint(canvas);
    	
    	if (PubVar.m_Map.getInvalidMap())
    	{
    		return;
    	}
    	
    	if(mGpsCoordinate == null)
    	{
    		return;
    	}
    	
    	if(navaPoints.size() == 0)
    	{
    		return;
    	}
    	
    	for(HashMap<String,Object> navPoint:navaPoints)
    	{
    		Coordinate coord = new Coordinate((Double)navPoint.get("D2"),(Double)navPoint.get("D3"));
    		List<Coordinate> nav = new ArrayList<Coordinate>();
    		nav.add(mGpsCoordinate);
    		nav.add(coord);
    		Point[] PList = PubVar.m_MapControl.getMap().getViewConvert().MapPointsToScreePoints(nav);
    		
    		CheckBox cbDrawLine = (CheckBox)_Dialog.findViewById(R.id.cb_drawline);
    		if(cbDrawLine.isChecked())
    		{
    			Path p = new Path();
        		p.moveTo(PList[0].x, PList[0].y);
        		p.lineTo(PList[1].x, PList[1].y);
        		
        		Paint pPen = new Paint();
            	pPen.setStrokeWidth(Tools.DPToPix(3));
            	pPen.setColor(Color.BLUE);
            	pPen.setStyle(Style.STROKE);
            	canvas.drawPath(p, pPen);
            	
            	Paint textPaint = new Paint();
            	textPaint.setColor(Color.RED);
            	textPaint.setTextSize(28);
            	
            	canvas.drawTextOnPath(navPoint.get("D4")+"米", p, Float.parseFloat(Tools.GetTwoPointDistance(PList[0].x, PList[0].y,PList[1].x,PList[1].y,true)+"")/2, 26, textPaint);
            	canvas.drawTextOnPath(navPoint.get("D9")+"", p, Float.parseFloat(Tools.GetTwoPointDistance(PList[0].x, PList[0].y,PList[1].x,PList[1].y,true)+"")/2, -20, textPaint);
            	
            	
    		}
    		
    		CheckBox cbMarkPoint = (CheckBox)_Dialog.findViewById(R.id.cb_markPoint);
    		if(cbMarkPoint.isChecked())
    		{
    			Bitmap  flag = ((BitmapDrawable)(PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.flag1))).getBitmap();
//    			canvas.drawBitmap(flag,PList[1].x-(flag.getWidth()/2),PList[1].y-(flag.getHeight()/2),null);
    			canvas.drawBitmap(flag,PList[1].x-(flag.getWidth()/2),PList[1].y-(flag.getHeight()/2)-(flag.getHeight()/4),null);
    			
    			Paint textPaint = new Paint();
            	textPaint.setColor(Color.RED);
            	textPaint.setTextSize(28);
            	
    			canvas.drawText(navPoint.get("D1")+"", PList[1].x,PList[1].y+flag.getHeight(), textPaint);
    			
    		}
        	int H = Tools.DPToPix(8);
//        	for(int i=0;i<PList.length;i++)
//        	{
        	  Paint pBrush = new Paint();
              pBrush.setStrokeWidth(Tools.DPToPix(5));
              pBrush.setColor(Color.YELLOW);
              canvas.drawCircle(PList[0].x, PList[0].y, H/2, pBrush);
              pBrush.setStyle(Style.STROKE);
              pBrush.setColor(Color.BLUE);
              pBrush.setStrokeWidth(Tools.DPToPix(2));
              canvas.drawCircle(PList[0].x, PList[0].y, H/2, pBrush);
//        	}
        	
    	}
    		
	}
    

    private List<HashMap<String,Object>> navaPoints = new ArrayList<HashMap<String,Object>>();
    private void refreshNavList()
    {
    	int LayoutViewId = R.layout.c_dh_navlistitem;
    	String[] keyItem = new String[] {"D1","D4","D5","D6","D7","D8","D9"};
    	int[] ViewItem = new int[] {R.id.tvdh, R.id.tvjl, R.id.tvfx1,R.id.tvjl1,R.id.tvfx2,R.id.tvjl2,R.id.tvjd,R.id.btdelete};
    	
    	//刷新列表
    	NavPointListAdpter adapter = new NavPointListAdpter(_Dialog.getContext(), navaPoints, LayoutViewId, keyItem, ViewItem);
    	adapter.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if(Str.equals("删除"))
				{
					final int index = Integer.valueOf(ExtraStr+"");
					Tools.ShowYesNoMessage(_Dialog.getContext(), "是否删除"+(index+1)+"第个导航点?", new ICallback(){

						@Override
						public void OnClick(String Str, Object obj) {
							if (Str.equals("YES"))
							{
								navaPoints.remove(index);
								refreshNavList();
							}
							
						}});
				}
			}});
		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
		
		adapter.notifyDataSetInvalidated();
    }
    

    
    private void collectSelectedOjd()
    {
    	int pointIndex 	= 0;
    	int lineIndex  	= 0;
    	int polyIndex	= 0;
    	
    	for(int i = 0;i<PubVar.m_MapControl.getMap().getGeoLayers(lkGeoLayersType.enAll).size();i++)
    	{
    		GeoLayer pGeoLayer = PubVar.m_MapControl.getMap().getGeoLayers(lkGeoLayersType.enAll).GetLayerByIndex(i);
    		for(int j = 0;j<pGeoLayer.getSelSelection().getCount();j++)
    		{
    			Geometry geo = pGeoLayer.getSelSelection().getDataset().GetGeometry(pGeoLayer.getSelSelection().getGeometryIndexList().get(j));
    			if(geo.GetType() == lkGeoLayerType.enPoint)
    			{
    				lkmap.Cargeometry.Point point = (lkmap.Cargeometry.Point)geo;
    				HashMap<String,Object> hm = new HashMap<String,Object>();
    				pointIndex++;
	    			hm.put("D1", "点"+pointIndex);//名称
	    			
	    			addPointToNavList("点"+pointIndex,point.getCoordinate());
    			}
    			
    			if(geo.GetType() == lkGeoLayerType.enPolyline)
    			{
    				lkmap.Cargeometry.Polyline line = (lkmap.Cargeometry.Polyline)geo;
    				HashMap<String,Object> hm = new HashMap<String,Object>();
    				lineIndex++;
	    			hm.put("D1", "线"+lineIndex);//名称
	    			if(line.getCenterPoint() != null)
	    			{
	    				addPointToNavList("线"+lineIndex,line.getCenterPoint());
	    			}
    			}
    			
    			if(geo.GetType() == lkGeoLayerType.enPolygon)
    			{
    				lkmap.Cargeometry.Polygon poly = (lkmap.Cargeometry.Polygon)geo;
    				HashMap<String,Object> hm = new HashMap<String,Object>();
    				polyIndex++;
	    			hm.put("D1", "面"+polyIndex);//名称
	    			if(poly.getCenterPoint() != null)
	    			{
	    				addPointToNavList("面"+polyIndex,poly.getCenterPoint());
	    			}
    			}
    		}
    		
    		
    	}
    	
    }
    
    //按钮事件
    private ICallback pCallback = new ICallback()
    {
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		_Dialog.dismiss();
		    	
	    	}
	    	
	    	if(Str.equals("清空"))
	    	{
	    		navaPoints.clear();
	    		refreshNavList();
	    	}
	    	
	    	if (Str.equals("坐标点"))
	    	{
	    		ArrayList<HashMap<String, Object>> points = (ArrayList<HashMap<String, Object>>)ExtraStr;
	    		for(HashMap<String, Object> point:points)
	    		{
	    			HashMap<String,Object> hm = new HashMap<String,Object>();
	    			hm.put("D1", point.get("D2"));//名称
	    	    	hm.put("D2", point.get("D3"));//x坐标
	    	    	hm.put("D3", point.get("D4"));//y坐标
	    	    	hm.put("D4", 0);//直线距离
	    	    	hm.put("D5", " ");//方位1
	    	    	hm.put("D6", 0);//方位1距离
	    	    	hm.put("D7", " ");//方位2
	    	    	hm.put("D8", 0);//方位2距离
	    	    	hm.put("D9", " ");//方位角
	    	    	navaPoints.add(hm);
	    		}
	    		refreshNavList();
	    	}
		}};


	private void addPointToNavList(String pointName,Coordinate coord)
	{
		HashMap<String,Object> hm = new HashMap<String,Object>();
		hm.put("D1", pointName);//名称
    	hm.put("D2", coord.getX());//x坐标
    	hm.put("D3", coord.getY());//y坐标
    	hm.put("D4", 0);//直线距离
    	hm.put("D5", " ");//方位1
    	hm.put("D6", 0);//方位1距离
    	hm.put("D7", " ");//方位2
    	hm.put("D8", 0);//方位2距离
    	hm.put("D9", " ");//方位角
    	navaPoints.add(hm);
	}
	
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
				//InitViewPager();
				refreshNavList();
				}}
    	);
    	_Dialog.show();
    	
    }
}
