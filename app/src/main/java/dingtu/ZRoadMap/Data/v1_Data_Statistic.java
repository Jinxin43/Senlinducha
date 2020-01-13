package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.DataSource;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.Project.v1_Layer;

public class v1_Data_Statistic
{
	private v1_FormTemplate _Dialog = null; 
    public v1_Data_Statistic()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_statistic);
    	_Dialog.ReSetSize(0.5f,0.96f);
    	
    	_Dialog.SetCaption(Tools.ToLocale("数据统计"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,属性表", pCallback);
    	
    	_Dialog.findViewById(R.id.st_point_list).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.st_line_list).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.st_poly_list).setOnClickListener(new ViewClick());
    	
    	
    	//多语言支持
    	Tools.ToLocale(_Dialog.findViewById(R.id.st_point_list));
    	Tools.ToLocale(_Dialog.findViewById(R.id.st_line_list));
    	Tools.ToLocale(_Dialog.findViewById(R.id.st_poly_list));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText2));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText3));
    }
    
    
    class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if (Tag.contains("收起"))SetArrowImage(arg0,R.drawable.v1_leftarrow);
    		if (Tag.contains("展开"))SetArrowImage(arg0,R.drawable.v1_downarrow);
    		
    		LinearLayout LL = null;
        	if (Tag.contains("点")){LL = (LinearLayout)_Dialog.findViewById(R.id.ll_point);FillData(lkGeoLayerType.enPoint);}
        	if (Tag.contains("线")){LL = (LinearLayout)_Dialog.findViewById(R.id.ll_line);FillData(lkGeoLayerType.enPolyline);}
        	if (Tag.contains("面")){LL = (LinearLayout)_Dialog.findViewById(R.id.ll_poly);FillData(lkGeoLayerType.enPolygon);}
        	if (Tag.contains("收起"))
        	{
        		LayoutParams LP = (LayoutParams) LL.getLayoutParams();
        		LP.width = LayoutParams.FILL_PARENT;
        		LP.height = 0;
        		LL.setLayoutParams(LP);
        		arg0.setTag(Tag.replace("收起", "展开"));
        		return;
        	}
        	if (Tag.contains("展开"))
        	{
        		LayoutParams LP = (LayoutParams) LL.getLayoutParams();
        		LP.width = LayoutParams.FILL_PARENT;
        		LP.height = LayoutParams.WRAP_CONTENT;
        		LL.setLayoutParams(LP);
        		arg0.setTag(Tag.replace("展开", "收起"));
        		return;
        	}
    	}
    }
    
    private void SetArrowImage(View arg0,int imgid)
    {
		Button bt = (Button)arg0;
		Drawable lock = PubVar.m_DoEvent.m_Context.getResources().getDrawable(imgid);
		lock.setBounds(0, 0, lock.getMinimumWidth(), lock.getMinimumHeight());
		bt.setCompoundDrawables(lock, null, null, null); //设置左图标
    }

    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("属性表"))
	    	{
	    		//if (m_SelectItem!=null)
	    		{
	    			_Dialog.dismiss();
	    		}
	    	}
		}};

	/**
	 * 统计数据
	 * @param Type
	 */
	private void FillData(lkGeoLayerType layerType)
	{
		String HeaderType = "统计_%1$s图层";
		int listid = 0;
		if (layerType==lkGeoLayerType.enPoint){HeaderType = String.format(HeaderType,"点");listid=R.id.point_list;}
		if (layerType==lkGeoLayerType.enPolyline){HeaderType = String.format(HeaderType,"线");listid=R.id.line_list;}
		if (layerType==lkGeoLayerType.enPolygon){HeaderType = String.format(HeaderType,"面");listid=R.id.poly_list;}
		
		//绑定统计结果
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(listid), HeaderType,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				_Dialog.GetButton("1").setEnabled(true);
			}});
    	
    	//读取分类统计信息
    	List<HashMap<String,Object>> dataList = this.StatisticData(layerType);
    	hvf.BindDataToListView(dataList);
	}
	
	/**
	 * 统计数据
	 * @param layerType
	 * @return
	 */
	private List<HashMap<String,Object>> StatisticData(lkGeoLayerType layerType)
	{
    	List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
    	
    	List<v1_Layer> vLayerList = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList();
    	for(v1_Layer vLayer : vLayerList)
    	{
    		HashMap<String,Object> layResult = new HashMap<String,Object>();
    		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(vLayer.GetLayerID());
    		if (pDataset.getType()==layerType && layerType==lkGeoLayerType.enPoint)
    		{
    			layResult.put("D1", vLayer.GetLayerAliasName());
    			layResult.put("D2", pDataset.GetAllObjectCount());
    			dataList.add(layResult);
    		}
    		
    		if (pDataset.getType()==layerType && (layerType==lkGeoLayerType.enPolyline || layerType==lkGeoLayerType.enPolygon))
    		{
    			//统计长度
    			double AllLen = 0,AllArea = 0;
    			String SQL = "select sum(SYS_Length) as AllLen,sum(SYS_Area) as AllArea from "+pDataset.getDataTableName()+" where SYS_STATUS='0'";
    			SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
    			if (DR!=null) while(DR.Read())
    			{
    				AllLen = DR.GetDouble("AllLen");
    				AllArea = DR.GetDouble("AllArea");
    			}DR.Close();
    			
    			layResult.put("D1", vLayer.GetLayerAliasName());
    			layResult.put("D2", pDataset.GetAllObjectCount());
    			if (layerType==lkGeoLayerType.enPolyline)layResult.put("D3", Tools.ConvertToDigi(AllLen+"",3));
    			if (layerType==lkGeoLayerType.enPolygon)layResult.put("D3", Tools.ReSetArea(AllArea,false));
    			dataList.add(layResult);
    		}
    	}
    	return dataList;
	}
	
    /**
     * 加载点，线，面的统计信息列表信息，主要是图层数量，数据条数
     */
    private void LoadStatisticInfo()
    {
    	int PointLayerCount=0,LineLayerCount=0,PolyLayerCount=0;
    	int PointObjects=0,LineObjects=0,PolyObjects=0;
    	DataSource pDataSource = PubVar.m_Workspace.GetDataSourceByEditing();
    	for(Dataset pDataset : pDataSource.getDatasets())
    	{
    		if (pDataset.getType()==lkGeoLayerType.enPoint)
    		{
    			PointLayerCount++;
    			PointObjects+=pDataset.GetAllObjectCount();
    		}
    		if (pDataset.getType()==lkGeoLayerType.enPolyline)
    		{
    			LineLayerCount++;
    			LineObjects+=pDataset.GetAllObjectCount();
    		}
    		if (pDataset.getType()==lkGeoLayerType.enPolygon)
    		{
    			PolyLayerCount++;
    			PolyObjects+=pDataset.GetAllObjectCount();
    		}
    	}
    	
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_point_layercount, Tools.ToLocale("图层数")+"="+PointLayerCount);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_line_layercount, Tools.ToLocale("图层数")+"="+LineLayerCount);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_poly_layercount, Tools.ToLocale("图层数")+"="+PolyLayerCount);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_point_datacount, Tools.ToLocale("数据量")+"="+PointObjects);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_line_datacount, Tools.ToLocale("数据量")+"="+LineObjects);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_poly_datacount, Tools.ToLocale("数据量")+"="+PolyObjects);
    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadStatisticInfo();}}
    	);
    	_Dialog.show();
    }
}
