package lkmap.ZRoadMap.ToolsBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Polygon;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Layer.GeoLayer;
import lkmap.Layer.GeoLayers;
import lkmap.Spatial.SpatialAnalysisTools;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class v1_poly_analysis
{
	private v1_FormTemplate _Dialog = null; 

    public v1_poly_analysis()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_poly_analysis);
    	_Dialog.ReSetSize(0.7f,0.90f);
    	
    	//设置标题
    	_Dialog.SetCaption(Tools.ToLocale("面重叠分析"));
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_cat+",导出  ,导出", pCallback);
    	_Dialog.SetButtonInfo("1,"+R.drawable.m_option+","+Tools.ToLocale("设置")+" ,设置", pCallback);
    	_Dialog.findViewById(R.id.pln_recal).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {pCallback.OnClick("重新计算", "");}});
			
    }
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("设置"))
	    	{
	    		v1_poly_analysis_set pas = new v1_poly_analysis_set();
	    		pas.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						pCallback.OnClick("重新计算", null);
					}});
	    		pas.ShowDialog();
	    	}
	    	
	    	if (Str.equals("导出"))
	    	{
				v1_poly_analysis_saveresult dss = new v1_poly_analysis_saveresult(_Dialog.getContext());
				dss.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) 
					{
						HashMap<String,String> savePara = (HashMap<String,String>)ExtraStr;
						String txtPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/数据导出";
						String txtName = txtPath+"/"+savePara.get("名称")+".txt";
						if (!Tools.ExistFile(txtPath))(new File(txtPath)).mkdirs();
						
						List<String> saveResultList = new ArrayList<String>();
						saveResultList.add("分析时间："+Tools.GetSystemDate());
						saveResultList.add("结果说明："+savePara.get("说明"));
						saveResultList.add("选择面积："+Tools.GetTextValueOnID(_Dialog, R.id.tvresult));
						saveResultList.add("图层名称     统计项目     面积     比例");
						for(HashMap<String,Object> hm:m_AnalysisResultList)
						{
							String resultStr = hm.get("D1")+"     "+hm.get("D2")+"     "+hm.get("D3")+"     "+hm.get("D4");
							saveResultList.add(resultStr);
						}
						try
						{
		    				FileWriter filenew= new FileWriter(txtName);
		    				BufferedWriter bw= new BufferedWriter(filenew);
						    bw.write(Tools.JoinT("\r\n", saveResultList));
						    bw.close();
						    Tools.ShowToast(_Dialog.getContext(), "面分析成功保存！\n存储："+txtName);
						    return;
						}
						catch(Exception e){}

						Tools.ShowMessageBox(_Dialog.getContext(), "无法保存面分析结果，注意检查文件名称！");return;

					}});
				dss.ShowDialog();
	    	}
	    	if (Str.equals("重新计算"))
	    	{
	    		Tools.OpenDialog("正在进行面分析...", new ICallback(){

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						StartAnalysisPoly();
					}});
	    	}
		}};
		
	//面分析配置操作类
	private v1_UserConfigDB_PolyAnalysisOption m_PAO = null;
	
	//最后分析结果
	private List<HashMap<String,Object>> m_AnalysisResultList = new ArrayList<HashMap<String,Object>>();
	
    //分析面
    private void StartAnalysisPoly()
    {
    	//1、读取面分析的设置信息，格式详见：v1_UserConfigDB_PolyAnalysisOption
    	if (this.m_PAO==null) this.m_PAO = new v1_UserConfigDB_PolyAnalysisOption();
    	List<HashMap<String,Object>> OptList = this.m_PAO.GetPolyAnalysisOption();
    	
    	//2、提取需要分析的面层
    	List<HashMap<String,Object>> polyDatasetList = new ArrayList<HashMap<String,Object>>();
    	for(HashMap<String,Object> Opt:OptList)
    	{
    		String LayerId = Opt.get("LayerId")+"";
    		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(LayerId);
    		if (pDataset==null)continue;
    		
    		v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
    		if (pLayer==null)pLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetLayerByID(pDataset.getId());
    		
    		List<String> FieldList = (List<String>)Opt.get("FieldNameList");
    		
    		//判断统计字段是否有效
    		int FieldCount = FieldList.size();
    		for(int i=FieldCount-1;i>=0;i--)
    		{
    			boolean Have = false;
	    		for(v1_LayerField LF :pLayer.GetFieldList())
	    		{
	    			if (LF.GetDataFieldName().equals(FieldList.get(i)))Have = true;
	    		}
	    		if (!Have)FieldList.remove(i);
    		}
    		if (FieldList.size()==0)FieldList.add("SYS_ID");   //没有统计字段，默认用SYS_ID进行统计
    		HashMap<String,Object> hmObj = new HashMap<String,Object>();
    		hmObj.put("Dataset", pDataset);
    		hmObj.put("FieldNameList", FieldList);
    		polyDatasetList.add(hmObj);
    	}
    	
    	//如果为0，提示需要进行分析设置
    	if (polyDatasetList.size()==0)
    	{
    		Tools.ShowYesNoMessage(_Dialog.getContext(), "没有设置统计分析选项，是否需要设置？", new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("YES")){pCallback.OnClick("设置", "");return;}
					else {_Dialog.dismiss();}
				}});
    	}
    	
    	//3、提取当前选中的面
    	Polygon _SelectPoly = null;
    	GeoLayers GeoLayerList = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll);
    	for(GeoLayer pGeoLayer:GeoLayerList.getList())
    	{
    		if (pGeoLayer.getType()==lkGeoLayerType.enPolygon) 
			{
				if (pGeoLayer.getSelSelection().getCount()==1)
				{
					int idx = pGeoLayer.getSelSelection().getGeometryIndexList().get(0);
					_SelectPoly = (Polygon)pGeoLayer.getDataset().GetGeometry(idx);
				}
			}
    	}
    	
    	//4、显示被选择面的面积
    	double SelectPolyArea = _SelectPoly.getArea(true);  
    	Tools.SetTextViewValueOnID(_Dialog, R.id.tvresult, Tools.ReSetArea(SelectPolyArea,true));
    	
    	//整理结果列表
    	this.m_AnalysisResultList = new ArrayList<HashMap<String,Object>>();
    	for(HashMap<String,Object> pDatasetInfo:polyDatasetList)
    	{
    		Dataset pDataset = (Dataset)pDatasetInfo.get("Dataset");
    		List<String> FieldList = (List<String>)pDatasetInfo.get("FieldNameList");
    		
    		//分析指定面层，返回格式：result["SYSID"],result["Area"]
    		HashMap<String,Object> result = this.CalPolyLayer(pDataset, _SelectPoly);
    		if (result.size()==0)continue;
    		List<String> SYSIDList = new ArrayList<String>();
    		for(String id:result.keySet())SYSIDList.add(id);
    		
    		//分类整理，主要是根据设置选项中的字段列进行分类汇总
    		HashMap<String,Object> STTypeList = new HashMap<String,Object>();
    		String SQL = "select (%1$s) as STType,SYS_ID from %2$s where SYS_ID in (%3$s)";
    		SQL = String.format(SQL, Tools.JoinT("||','||", FieldList),pDataset.getDataTableName(),Tools.JoinT(",", SYSIDList));
    		SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
    		if (DR!=null)while(DR.Read())
    		{
    			String STType = DR.GetString("STType");  //比如：旱地，水地
    			String SYSID = DR.GetString("SYS_ID");
    			double B = Double.parseDouble(result.get(SYSID)+"");  //计算面积
    			
    			if (STTypeList.containsKey(STType)) //累计
    			{
					double A = Double.parseDouble(STTypeList.get(STType)+"");
					STTypeList.put(STType, A+B);
    			}
    			else
    			{
    				STTypeList.put(STType,B);
    			}
    		}DR.Close();
    		
    		//整里相交结果
    		if (STTypeList.keySet().size()>0)
    		{
    			for(String key:STTypeList.keySet())
    			{
    				double A = Double.parseDouble(STTypeList.get(key)+"");
        			HashMap<String,Object> hm = new HashMap<String,Object>();
        			hm.put("D1", pDataset.getBindGeoLayer().GetAliasName());  //图层名称
        			hm.put("D2", key);			  							  //统计项目
        			hm.put("D3", Tools.ReSetArea(A, false));			  	  //面积
        			hm.put("D4", Tools.ConvertToDigi(A/SelectPolyArea*100+"",2)+"%");
        			this.m_AnalysisResultList.add(hm);
    			}
    		}
    		
    		
    	}
    	
    	//绑定分析结果
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "面叠加分析结果");
    	hvf.BindDataToListView(this.m_AnalysisResultList);
    }
    
    /**
     * 计算指定面与指定图层的相交情况
     * @param pDataset
     * @param APoly
     * @return
     */
    private HashMap<String,Object> CalPolyLayer(Dataset pDataset,Polygon pSelectPoly)
    {
    	HashMap<String,Object> result = new HashMap<String,Object>();
    	GeoLayer pGeoLayer = pDataset.getBindGeoLayer();
		int ShowCount = pGeoLayer.getShowSelection().getCount();
		for(int i=0;i<ShowCount;i++)
		{
			int idx = pGeoLayer.getShowSelection().getGeometryIndexList().get(i);
			Polygon Poly2 = (Polygon)pGeoLayer.getDataset().GetGeometry(idx);
			if (Poly2.getStatus()==lkGeometryStatus.enDelete) continue;
			if (pSelectPoly.equals(Poly2))continue;   //自身不用判断

			//外接矩形是否相交
			if (!pSelectPoly.getEnvelope().Intersect(Poly2.getEnvelope())) continue;
			
			//分析面积
			HashMap<String,Object> IntersectResult = SpatialAnalysisTools.Poly_IntersectArea(pSelectPoly, Poly2);
			
			
//				//测试用，将坐标串写入临时文件中
//				List<String> TempList = new ArrayList<String>();
//				TempList.add("面积："+Allarea);
//
//				TempList.add("================交点坐标串==============================");
//				Poly_Intersect pi = new Poly_Intersect();
//				List<List<Coordinate>> SubPolyList = pi.Poly_Intersect(_SelectPoly, Poly2);
//				for(int m=0;m<SubPolyList.size();m++)
//				{
//					TempList.add("================子面"+(m+1)+"==============================");
//					for(Coordinate Coor1:SubPolyList.get(m))
//					{
//						TempList.add(Coor1.ToString());
//					}
//				}
//				
//				
//				TempList.add("=================面1 坐标串=============================");
//				for(Coordinate Coor1:_SelectPoly.GetPartAt(0).getVertexList())
//				{
//					TempList.add(Coor1.ToString());
//				}
//				TempList.add("=================面2 坐标串=============================");
//				for(Coordinate Coor1:Poly2.GetPartAt(0).getVertexList())
//				{
//					TempList.add(Coor1.ToString());
//				}
//				
//				try
//				{
//    				FileWriter filenew= new FileWriter(PubVar.m_SysAbsolutePath+"//test.txt");
//
//				   BufferedWriter bw= new BufferedWriter(filenew);
//
//				    bw.write(Tools.JoinT("\r\n", TempList));
//
//				    bw.close();
//				}
//				catch(Exception e)
//				{
//					
//				}

		
			//整理相交结果
			double Allarea = Double.parseDouble(IntersectResult.get("Area")+"");
			if (Allarea>0)
			{
				result.put(Poly2.getSysId()+"",Allarea);
			}
		}
		
		return result;
    }
    

    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				pCallback.OnClick("重新计算","");}}
    	);
    	_Dialog.show();
    }

}
