package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Envelope;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Point;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.Project_Web;
import lkmap.Dataset.FieldInfo;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Edit.DeleteAddObject;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.Layer.GeoLayers;
import lkmap.Tools.Tools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.ZRoadMap.MyControl.HashMapEx;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.Project.v1_Layer;

public class v1_Data_Query
{
	private v1_FormTemplate _Dialog = null; 
	private ArrayList<String> allFields= new ArrayList<String>();
	private boolean[] showsFields ;
	private ArrayList<String> allDataFields = new ArrayList<String>();
	
    public v1_Data_Query()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_query);
    	_Dialog.ReSetSize(0.6f,0.96f);
    	_Dialog.SetCaption("数据查询");
    	
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_layer_field_edit+","+Tools.ToLocale("查询全部")+"  ,查询全部", pCallback);
    	//_Dialog.SetButtonInfo("2,"+R.drawable.v1_layer_field_edit+","+Tools.ToLocale("导出每木检尺表")+"  ,导出碳汇", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_ok+","+Tools.ToLocale("导出数据")+"  ,导出数据", pCallback);
    	
    	//_Dialog.findViewById(R.id.ibt_after).setOnClickListener(_ClickEvent);
    	//_Dialog.findViewById(R.id.ibt_before).setOnClickListener(_ClickEvent);
    	_Dialog.findViewById(R.id.ibt_delete).setOnClickListener(_ClickEvent);
    	_Dialog.findViewById(R.id.ibt_edit).setOnClickListener(_ClickEvent);
    	_Dialog.findViewById(R.id.ibt_tomap).setOnClickListener(_ClickEvent);
    	_Dialog.findViewById(R.id.ibt_sql).setOnClickListener(_ClickEvent);
    	_Dialog.findViewById(R.id.ibt_selectField).setOnClickListener(_ClickEvent);
    }

    //按钮事件
    private android.view.View.OnClickListener _ClickEvent = new android.view.View.OnClickListener(){
		@Override
		public void onClick(View arg0) 
		{
			//if (arg0.getTag().toString().equals("下一页")){MoveAfter();}
			//if (arg0.getTag().toString().equals("前一页")){MoveBefore();}
			if (arg0.getTag().toString().equals("删除")){Delete();}
			if (arg0.getTag().toString().equals("编辑")){EditFeature();}
			if (arg0.getTag().toString().equals("字段选择")){selectFields();}
			if (arg0.getTag().toString().equals("图上定位")){LocateToMap();}
			if (arg0.getTag().toString().equals("SQL查询"))
			{
				v1_Data_Query_SQL vdqs = new v1_Data_Query_SQL();
				vdqs.SetICallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						_QueryType = "SQL_Filter";
			    		_CurrentPageIndex=1;
			    		StartQuery(_CurrentPageIndex,_RowCountPerPage);
			    		QuerySum();
					}});
				vdqs.SetQueryParam(_QueryParam);
				vdqs.ShowDialog();
			}
			
			
			
		}};
		

	private void selectFields()
	{
		if(showsFields == null)
		{
			needInitFieldsOnFirstQuery = true;
			_QueryType = "SQL_All";
    		_CurrentPageIndex=0;
    		MoveAfter();
    		QuerySum();
		}
		DataQuery_SelectFields sf = new DataQuery_SelectFields(allFields,showsFields);
		sf.SetCallback(new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				
				showsFields =(boolean[])ExtraStr;
				needInitFieldsOnChangeShow = true;
				StartQuery(_CurrentPageIndex,_RowCountPerPage);
			}
			
		});
		sf.ShowDialog();
		
	}
	
	//图上定位
	private void LocateToMap()
	{
		PubVar.m_Map.ClearSelection();
		if (this.IsSelected())
		{
			int SYS_ID = Integer.parseInt(this._SelectedItem.get("SYS_ID")+"");
			GeoLayer pGeoLayer = (GeoLayer)this._SelectedItem.get("GeoLayer");

			Envelope pEnv = null;
			Geometry pGeometry = pGeoLayer.getDataset().GetGeometry(SYS_ID);
			if (pGeometry==null)
			{
				//表示此实体还没有被加载呢
				List<String> queryDIndex = new ArrayList<String>();
				queryDIndex.add((SYS_ID)+"");
				if (pGeoLayer.getDataset().QueryGeometryFromDB(queryDIndex))
				{
					pGeometry = pGeoLayer.getDataset().GetGeometry(SYS_ID);
					pEnv = pGeometry.getEnvelope();
				}
				
			} else pEnv = pGeometry.getEnvelope();
			
			if (pEnv!=null)
			{
				//if (pEnv.getHeight()<0.5)pEnv.ExtendTo(10);   //向外伸展10米，主要目的是让比例放到最大，显示实体
	        	pGeoLayer.getSelSelection().Add(pGeometry);
	        	PubVar.m_Map.setExtend(PubVar.m_Map.AdjustEnvelopeFitScreen(pEnv));
	        	PubVar.m_Map.Refresh();
	        	Tools.ShowToast(_Dialog.getContext(), "实体图上定位成功！");
	        	return;
			}
			
			Tools.ShowMessageBox(_Dialog.getContext(),"无法定位实体！");
		}
	}
	
	//编辑记录属性
	private void EditFeature()
	{
		if (this.IsSelected())
		{
			int SYS_ID = Integer.parseInt(this._SelectedItem.get("SYS_ID")+"");
			GeoLayer pGeoLayer = (GeoLayer)this._SelectedItem.get("GeoLayer");

			if (pGeoLayer.getDataset().getDataSource().getEditing())  //是否为正在编辑的数据源
			{
                if (pGeoLayer.getType() == lkmap.Enum.lkGeoLayerType.enPoint)
                {
//                    PubVar.m_DoEvent.m_GPSPoint.Edit(pGeoLayer.getId(),SYS_ID,new ICallback(){
//						@Override
//						public void OnClick(String Str, Object ExtraStr) {RefreshList();}});
                	PubVar.m_DoEvent.m_GPSPoint.Edit(pGeoLayer.getId(),SYS_ID);
                	
                }
                if (pGeoLayer.getType() == lkmap.Enum.lkGeoLayerType.enPolyline)
                {
//                	PubVar.m_DoEvent.m_GPSLine.Edit(pGeoLayer.getId(),SYS_ID,new ICallback(){
//						@Override
//						public void OnClick(String Str, Object ExtraStr) {RefreshList();}});
                	PubVar.m_DoEvent.m_GPSLine.Edit(pGeoLayer.getId(),SYS_ID);
                }
                if (pGeoLayer.getType() == lkmap.Enum.lkGeoLayerType.enPolygon)
                {
//                	PubVar.m_DoEvent.m_GPSPoly.Edit(pGeoLayer.getId(),SYS_ID,new ICallback(){
//						@Override
//						public void OnClick(String Str, Object ExtraStr) {RefreshList();}});
                	
                	PubVar.m_DoEvent.m_GPSPoly.Edit(pGeoLayer.getId(),SYS_ID);
                }
                
//                String projectType = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId()).GetLayerProjecType();
//        		if(projectType != null)
//				{
//        			if(projectType.equals("退耕还林")||projectType.equals("林地变更"))
//        			{
//        				_Dialog.dismiss();
//        			}
//        			
//				}
			}
			else
			{
				v1_Data_Back_Feature v1dbf = new v1_Data_Back_Feature();
				HashMap<String,Object> queryObj = new HashMap<String,Object>();
				queryObj.put("LayerId", pGeoLayer.getId());
				queryObj.put("SYS_ID", SYS_ID);
				v1dbf.SetQueryObj(queryObj);
				v1dbf.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {RefreshList();}});

				v1dbf.ShowDialog();
			}
		}
	}
	
	//刷新列表
	private void RefreshList()
	{
        //重新刷新列表
        this.StartQuery(_CurrentPageIndex,_RowCountPerPage);
        QuerySum();
	}

	private DeleteAddObject m_DeleteAddObject = null;
	
	//删除选中的项
	private void Delete()
	{
//		if (this.IsSelected())
//		{
//			final String SYS_ID = this._SelectedItem.get("SYS_ID")+"";
	    final GeoLayer pGeoLayer = (GeoLayer)this._QueryParam.get("GeoLayer");
		//final GeoLayer pGeoLayer = (GeoLayer)this._SelectedItem.get("GeoLayer");
		
	    
		final IURDataItem_DeleteAdd urItem = new IURDataItem_DeleteAdd();
		urItem.LayerId = pGeoLayer.getDataset().getId();
		for(int i=0;i<hvf.getAdapter().getCount();i++)
		{
			try
			{
				HashMap<String,Object> hm = (HashMap<String,Object>)hvf.getAdapter().getItem(i);
				boolean isSelect =Boolean.parseBoolean(hm.get("D1")+"");
				if(isSelect)
				{
					urItem.ObjectIdList.add(Integer.parseInt(hm.get("SYS_ID")+""));
				}
			}
			catch(Exception ex)
			{
				String e = ex.getMessage();
			}
			
		}
		
		if(urItem.ObjectIdList.size()>0)
		{
			//如果为背景图层而不允许删除
			if (!pGeoLayer.getDataset().getDataSource().getEditing())
			{
				Tools.ShowMessageBox(_Dialog.getContext(), "不允许删除背景图层的记录！");return;
			} else
			{
				
				   Tools.ShowYesNoMessage(_Dialog.getContext(), "是否删除所选"+urItem.ObjectIdList.size()+"条记录？\n", new ICallback(){

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (Str.equals("YES"))
						{
							if (m_DeleteAddObject==null)m_DeleteAddObject = new DeleteAddObject();
							List<IURDataItem_DeleteAdd> urList = new ArrayList<IURDataItem_DeleteAdd>();
							urList.add(urItem);
							m_DeleteAddObject.Delete(urList, true);
							
							
//		                    //直接从数据库中删除
//		                    String SQL1 = "delete from " + pGeoLayer.getDataset().getDataTableName() + " where SYS_ID =" + SYS_ID;
//		                    String SQL2 = "delete from " + pGeoLayer.getDataset().getIndexTableName() + " where SYS_ID =" + SYS_ID;
//		                    if (pGeoLayer.getDataset().getDataSource().ExcuteSQL(SQL1) &&
//		                        pGeoLayer.getDataset().getDataSource().ExcuteSQL(SQL2))
//		                    {
//		                        //从数据集中移除实体
//		                    	//int index = pGeoLayer.getDataset().GetGeometryByDIndex(Integer.parseInt(SYS_ID)).getIndex();
//		                        //pGeoLayer.getDataset().RemoveGeometryByDIndex(Integer.parseInt(SYS_ID));
//		                            
//		                        //从当前显示列表中移除实体
//		                        //pGeoLayer.getShowSelection().Remove(index);
//		                            
//		                        //从当前图层索引列表中移除实体
//		                        //pGeoLayer.getDataset().getCellIndex().RemoveIndex(index);
//
//		                        //清空选择集合
//		                        //pGeoLayer.getSelSelection().Remove(index);
//		                        
//		                    }
//		                    PubVar.m_Map.Refresh();
		                    
		                    RefreshList();
						}
						
					}});
			}
		}
		else
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "请勾选要删除的小班！");
		}
	}
	
	private boolean IsSelected()
	{
		if (this._SelectedItem==null)
		{
			Tools.ShowMessageBox(_Dialog.getContext(),"请在列表中选择记录项！");
			return false;
		} else return  true;
	}
	
	//下一页
	private void MoveAfter()
	{
		if (!StartQuery(_CurrentPageIndex+1,_RowCountPerPage))
		{
			StartQuery(_CurrentPageIndex,_RowCountPerPage);
			//Tools.ShowToast(_Dialog.getContext(), "已到最后一页！");
			return;
		} else _CurrentPageIndex++;
	}
	
	//前一页
	private void MoveBefore()
	{
		if (!StartQuery(_CurrentPageIndex-1,_RowCountPerPage))
		{
			StartQuery(_CurrentPageIndex,_RowCountPerPage);
			Tools.ShowToast(_Dialog.getContext(), "已到第一页！");
		} else _CurrentPageIndex--;
	}
	
	//查询类型
	private String _QueryType = "SQL_All";
	
		
	//当前页索引
	private int _CurrentPageIndex = 0;
	
	//每页的行数
	private int _RowCountPerPage = 100;
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("查询全部"))
	    	{
	    		_QueryType = "SQL_All";
	    		_CurrentPageIndex=0;
	    		MoveAfter();
	    		QuerySum();
	    	}
	    	
	    	if (Str.equals("导出数据"))
	    	{
	    		if(_DataResultList.size()>0)
	    		{
	    			DataQuery_Import dataImport = new DataQuery_Import();
		    		dataImport.setDataSet(Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_layerlist),ImportResultFieldList,QueryAll());
		    		dataImport.ShowDialog();
	    		}
	    		else
	    		{
	    			Tools.ShowMessageBox(_Dialog.getContext(), "没有可导出的数据！");
	    		}
	    		
	    	}
		}};

		
	//查询参数，结构：SQL,GeoLayer
	private HashMap<String,Object> _QueryParam = new HashMap<String,Object>();
	
	//查询结果
	private List<HashMap<String,Object>> _DataResultList = new ArrayList<HashMap<String,Object>>();
	private List<HashMap<String,Object>> _DataResultFieldList = new ArrayList<HashMap<String,Object>>();
	private List<HashMap<String,Object>> ImportResultFieldList = new ArrayList<HashMap<String,Object>>();
	boolean SaveFieldList = true;
	boolean SaveFieldListAll = true;
	
	boolean needInitFieldsOnFirstQuery = true;
	boolean needInitFieldsOnChangeShow = false;
	
	
	//当前选中的记录
	private HashMap<String,Object> _SelectedItem = null;
	
	private int dataCount = 0;
	private double totalArea = 0d;
	private double totalLength = 0d;
	
	
	
	private int allCount = 0;
	private double sumArea = 0;
	private double sumLength = 0;
	/**
	 * 开始查询
	 * @param PageIndex 第几页
	 * @param PageCount 每页行数
	 */
	public boolean StartQuery(int PageIndex,int RowCountPerPage)
	{
		
		totalArea = 0;
		totalLength = 0;
		//清空查询结果列表
		this._SelectedItem = null;
		this._DataResultList.clear();
		this.BindResultToLlist();
		
		if (PageIndex<=0) return false;
		
		//如果没查询条件退出
		if (!this._QueryParam.containsKey(this._QueryType)) return false;
		if (this._QueryParam.get(this._QueryType).equals("")) return false;
		
		
		GeoLayer pGeoLayer = (GeoLayer)this._QueryParam.get("GeoLayer");
		
		String SQL = this._QueryParam.get(this._QueryType)+" limit "+RowCountPerPage+" offset "+RowCountPerPage*(PageIndex-1);
		
		SQLiteDataReader DR = pGeoLayer.getDataset().getDataSource().Query(SQL);
		if (DR==null)
		{
			return false;
		}
		
		
		
		try{
			while(DR.Read())
			{
				
				String[] FieldNameList = DR.GetFieldNameList();
				if(needInitFieldsOnFirstQuery)
				{
					initFieldsOnFirstQuery(FieldNameList,pGeoLayer);
				}
				else
				{
					if(needInitFieldsOnChangeShow)
					{
						initFieldsOnChanged(FieldNameList,pGeoLayer);
					}
				}
				
				int Index = 2;
				HashMap<String,Object> hm = new HashMap<String,Object>();
				hm.put("D1", false);
				hm.put("SYS_ID", DR.GetInt32("SYS_ID"));
				hm.put("GeoLayer", pGeoLayer);
				double area = 0.0;
				double length = 0.0;
				for(int i= 0;i<allDataFields.size();i++)
				{
					try
					{
						if(showsFields[i])
						{
							if (allDataFields.get(i).equals("SYS_ID"))
							{
								hm.put("D2", DR.GetInt32(allDataFields.get(i)));
								Index++;
								continue;
							}
							if (pGeoLayer.getType()== lkGeoLayerType.enPolygon && allDataFields.get(i).equals("SYS_Area"))
							{
								area = DR.GetDouble(allDataFields.get(i));
								totalArea +=area;
								hm.put("SYS_Area", totalArea+"");
								continue;
								
							}
							if (pGeoLayer.getType()== lkGeoLayerType.enPolyline && allDataFields.get(i).equals("SYS_Length"))
							{
								length =  DR.GetDouble(allDataFields.get(i));
								totalLength += length;
								hm.put("SYS_Length", totalLength+"");
								continue;
							}
							
							String FValue = DR.GetString(allDataFields.get(i));
							if (FValue==null)
								FValue="";
							hm.put("D"+(Index), FValue);
							Index++;
						}
						
						
					}
					catch(Exception ex)
					{
						Tools.ShowMessageBox(ex.getMessage());
					}
				}
				
				if (pGeoLayer.getType()== lkGeoLayerType.enPolygon)
				{
					hm.put("D"+(Index), Tools.ReSetArea(area, true));
					
				}
				
				if (pGeoLayer.getType()== lkGeoLayerType.enPolyline)
				{
					hm.put("D"+(Index), Tools.ReSetDistance(length, true));
				}
				
			
//				SaveFieldList = false;
//				SaveFieldListAll = false;
				_DataResultList.add(hm);
			}DR.Close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(ex.getMessage());
		}
		
		
		
		this.BindResultToLlist();
		
		if (this._DataResultFieldList.size()==0)return false;
		prepareScrollView();
		return true;
	}
	
	private void initFieldsOnChanged(String[] FieldNameList,GeoLayer pGeoLayer)
	{
		this._DataResultFieldList.clear();
		int i = 0;
		
		v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId());
		for(String FName:FieldNameList)
		{
			if (FName.contains("SYS_"))
			{
				if(FName.equals("SYS_ID"))
				{
					if(showsFields[i])
					{
						HashMap<String,Object> FieldObj = new HashMap<String,Object>();
						FieldObj.put("FieldName", "SYS_ID");
						_DataResultFieldList.add(FieldObj);
					}
					i++;
				}
				//跳过其他系统字段
				continue;
			}
			else
			{
				if(i< allFields.size())
				{
					if(showsFields[i])
					{
						HashMap<String,Object> FieldObj = new HashMap<String,Object>();
						FieldObj.put("FieldName", allFields.get(i));
						_DataResultFieldList.add(FieldObj);
					}
				}
				i++;
			}
		}
		
		needInitFieldsOnChangeShow = false;
	}
	private void initFieldsOnFirstQuery(String[] FieldNameList,GeoLayer pGeoLayer)
	{
		showsFields = new boolean[FieldNameList.length];
		for(int j=0;j<showsFields.length;j++)
		{
			showsFields[j] = false;
		}
		allFields.clear();
		allDataFields.clear();
		ImportResultFieldList.clear();
		this._DataResultFieldList.clear();
		int i = 0;
		
		v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId());
		for(String FName:FieldNameList)
		{
			if (FName.contains("SYS_"))
			{
				if(FName.equals("SYS_ID"))
				{
					HashMap<String,Object> FieldObj = new HashMap<String,Object>();
					FieldObj.put("FieldName", "SYS_ID");
					allFields.add("SYS_ID");
					allDataFields.add("SYS_ID");
					showsFields[i] = true;
					_DataResultFieldList.add(FieldObj);
					//ImportResultFieldList.add(FieldObj);
					i++;
				}
				
				continue;
			}
			else
			{
				//字段英汉转换
				String CHFieldName = FName;
				if (pGeoLayer.getDataset().getDataSource().getEditing())
				{
					CHFieldName = pLayer.GetFieldNameByDataFieldName(FName);
					if (CHFieldName.equals(""))
					{
						continue;
					}	
				} 
				else
				{
					List<FieldInfo> FIList = pGeoLayer.getDataset().getTableStruct();
					for(FieldInfo FI : FIList)
					{
						if (FI.getName().equals(FName))CHFieldName = FI.getCaption();
					}
				}
				
				HashMap<String,Object> FieldObj = new HashMap<String,Object>();
				FieldObj.put("FieldName", CHFieldName);
				allFields.add(CHFieldName);
				allDataFields.add(FName);
				
				ImportResultFieldList.add(FieldObj);
				if(i< 28)
				{
					showsFields[i] = true;
					_DataResultFieldList.add(FieldObj);
				}
				else
				{
					showsFields[i] = false;
				}
				i++;
			}
		}
//		if(pLayer.GetLayerProjecType() != null && pLayer.GetLayerProjecType().contains("退耕还林"))
//		{
//			
//		}
//		else
//		{
			if (pGeoLayer.getType()== lkGeoLayerType.enPolygon)
			{
				HashMap<String,Object> mj = new HashMap<String,Object>();
				mj.put("FieldName", "面积");
				_DataResultFieldList.add(mj);
				ImportResultFieldList.add(mj);
				allDataFields.add("SYS_Area");
				showsFields[i] = true;
			}
			
			if (pGeoLayer.getType()== lkGeoLayerType.enPolyline)
			{
				HashMap<String,Object> cd = new HashMap<String,Object>();
				cd.put("FieldName", "长度");
				ImportResultFieldList.add(cd);
				allDataFields.add("SYS_Length");
				showsFields[i] = true;
			}
//		}
		
		needInitFieldsOnFirstQuery = false;
	}
	
	private void updateSum()
	{
		GeoLayer pGeoLayer = (GeoLayer)_QueryParam.get("GeoLayer");
    	String result = "总数量:"+allCount;
		if(pGeoLayer.getType()== lkGeoLayerType.enPolygon)
		{
			result +="，"+Tools.ReSetArea(sumArea,true)+"；显示数量:"+_DataResultList.size();//+"，"+Tools.ReSetArea(totalArea, true);
		}
		
		if(pGeoLayer.getType()== lkGeoLayerType.enPolyline)
		{
			result +="，"+Tools.ReSetDistance(sumLength, true)+"；显示数量:"+_DataResultList.size();//+"，"+Tools.ReSetDistance(totalLength, true);
		}
		
		if(pGeoLayer.getType() == lkGeoLayerType.enPoint)
		{
			result += "显示数量:"+_DataResultList.size();
		}
		Tools.SetTextViewValueOnID(_Dialog, R.id.tv_result, result);
	}
	
	private List<HashMap<String,Object>> QueryAll()
	{
		List<HashMap<String,Object>> queryResult = new ArrayList<HashMap<String,Object>>();
		
		//如果没查询条件退出
		if (!this._QueryParam.containsKey(this._QueryType)) return queryResult;
		if (this._QueryParam.get(this._QueryType).equals("")) return queryResult;
		
		//查询图层
		GeoLayer pGeoLayer = (GeoLayer)this._QueryParam.get("GeoLayer");
		
		//查询条件
		String SQL = this._QueryParam.get(this._QueryType)+"";
		
		SQLiteDataReader DR = pGeoLayer.getDataset().getDataSource().Query(SQL);
		if (DR==null) return queryResult;
		v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId());
		
		while(DR.Read())
		{
			HashMap<String,Object> hm = new HashMap<String,Object>();
			String[] FieldNameList = DR.GetFieldNameList();
			
			int FIndex = 1;
			for(String FName:FieldNameList)
			{
				//排除系统字段
				if (FName.contains("SYS_"))
				{
					if (FName.equals("SYS_ID"))
					{
						hm.put("SYS_ID", DR.GetInt32(FName));
						hm.put("GeoLayer", pGeoLayer);
					}
					
					continue;
				}
				
				//字段英汉转换
				String CHFieldName = FName;
				if (pGeoLayer.getDataset().getDataSource().getEditing())
				{
					CHFieldName = pLayer.GetFieldNameByDataFieldName(FName);
					if (CHFieldName.equals("")) continue;
				} 
				else
				{
					List<FieldInfo> FIList = pGeoLayer.getDataset().getTableStruct();
					for(FieldInfo FI : FIList)
					{
						if (FI.getName().equals(FName))CHFieldName = FI.getCaption();
					}
				}

				String FValue = DR.GetString(FName);
				if (FValue==null)
					FValue="";
				hm.put("D"+(FIndex), FValue);
				FIndex++;
			}
			
			if(pGeoLayer.getType() == lkGeoLayerType.enPolygon)
			{
				hm.put("D"+(FIndex), Tools.ReSetArea(DR.GetDouble("SYS_Area"), true));
				FIndex++;
			}
			
			if(pGeoLayer.getType() == lkGeoLayerType.enPolyline)
			{
				hm.put("D"+(FIndex), Tools.ReSetDistance(DR.GetDouble("SYS_Length"), true));
				FIndex++;
			}
			
			if(pGeoLayer.getType() == lkGeoLayerType.enPoint)
			{
//				Geometry pGeometry = pDataset.GetGeometry(SYS_ID);
//		    	if (pGeometry!=null)
//		    	{
//		    		Coordinate Coor = ((Point)pGeometry).getCoordinate();
//		    		CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
//		    		if (CS.GetName().equals("WGS-84坐标"))
//		    		{
//		    			Coordinate BLCoor = Project_Web.Web_XYToBL(Coor.getX(),Coor.getY());
//		    			
//		    		}else
//		    		{
//		    			TV.setText(Coor.ToString()+","+Tools.ConvertToDigi(Coor.getZ()+"",2));
//		    		}
//		    	}
//				
//	    		
//				FIndex++;
			}
			
			queryResult.add(hm);
		}
		
		DR.Close();
		
		return queryResult;
	}
	
	private void QuerySum()
	{
		
		new AsyncTask<Void, String, String >() {
			protected String doInBackground(Void... params) 
			{
				
				try 
				{
					GeoLayer pGeoLayer = (GeoLayer)_QueryParam.get("GeoLayer");
					
					//查询条件
					String sumSQL = _QueryParam.get(_QueryType)+"";
					SQLiteDataReader sumDR = pGeoLayer.getDataset().getDataSource().Query(sumSQL);
					
					if(pGeoLayer.getType() != lkGeoLayerType.enPoint)
					{
						while(sumDR.Read())
						{
							try 
							{
								if (pGeoLayer.getType()== lkGeoLayerType.enPolygon)
								{
									sumArea += sumDR.GetDouble("SYS_Area");
								}
								if (pGeoLayer.getType()== lkGeoLayerType.enPolyline)
								{
									sumLength += sumDR.GetDouble("SYS_Length");
								}
								}
								catch(Exception ex)
								{
									ex.printStackTrace();
								}
						}
					}
					
					allCount = sumDR.GetCount();
				} 
				catch (Exception e) 
				{
					publishProgress("查询汇总数据失败！");
					e.printStackTrace();
				}
				return allCount+"";
			}

			protected void onPreExecute() {
				
				allCount = 0;
				sumArea = 0;
				sumLength = 0;
				super.onPreExecute();
			}

			protected void onPostExecute(String result) 
			{
				
				updateSum();
				
				super.onPostExecute(result);
			}

			protected void onProgressUpdate(String... values) {
				Toast.makeText(_Dialog.getContext(), values[0], Toast.LENGTH_SHORT)
						.show();
				super.onProgressUpdate(values);
			}

		}.execute();
	}
	
	private List<HashMap<String,Object>> nextQuery(int PageIndex,int RowCountPerPage)
	{
		//this._DataResultFieldList.clear();
		
		List<HashMap<String,Object>> queryResult = new ArrayList<HashMap<String,Object>>();
		if (PageIndex<=0) return queryResult;
		
		//如果没查询条件退出
		if (!this._QueryParam.containsKey(this._QueryType)) return queryResult;
		if (this._QueryParam.get(this._QueryType).equals("")) return queryResult;
		
		//查询图层
		GeoLayer pGeoLayer = (GeoLayer)this._QueryParam.get("GeoLayer");
		
		//查询条件
		String SQL = this._QueryParam.get(this._QueryType)+" limit "+RowCountPerPage+" offset "+RowCountPerPage*(PageIndex-1);
		
		SQLiteDataReader DR = pGeoLayer.getDataset().getDataSource().Query(SQL);
		if (DR==null) return queryResult;
		v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId());
		
		
		while(DR.Read())
		{
			HashMap<String,Object> hm = new HashMap<String,Object>();
			String[] FieldNameList = DR.GetFieldNameList();
			
			int FIndex = 3;
			for(String FName:FieldNameList)
			{
				//排除系统字段
				if (FName.contains("SYS_"))
				{
					if (FName.equals("SYS_ID"))
					{
						hm.put("SYS_ID", DR.GetInt32(FName));
						hm.put("D1", false);
						hm.put("D2", DR.GetInt32(FName));
						hm.put("GeoLayer", pGeoLayer);
					}
					if (pGeoLayer.getType()== lkGeoLayerType.enPolygon && FName.equals("SYS_Area"))
					{
						totalArea += DR.GetDouble(FName);
					}
					if (pGeoLayer.getType()== lkGeoLayerType.enPolyline && FName.equals("SYS_Length"))
					{
						totalLength += DR.GetDouble(FName);
					}
					continue;
				}
				
				//字段英汉转换
				String CHFieldName = FName;
				if (pGeoLayer.getDataset().getDataSource().getEditing())
				{
					
					CHFieldName = pLayer.GetFieldNameByDataFieldName(FName);
					if (CHFieldName.equals("")) continue;
				} else
				{
					List<FieldInfo> FIList = pGeoLayer.getDataset().getTableStruct();
					for(FieldInfo FI : FIList)
					{
						if (FI.getName().equals(FName))CHFieldName = FI.getCaption();
					}
				}

				String FValue = DR.GetString(FName);if (FValue==null)FValue="";
				hm.put("D"+(FIndex), FValue);
				FIndex++;
			}
			
			queryResult.add(hm);
		}
		
		DR.Close();
		
		return queryResult;
	}
	
	v1_HeaderListViewFactory hvf = null;
	private void addItems(List<HashMap<String,Object>> items)
	{
		if(hvf != null)
		{
			if(hvf.getAdapter().addItems(items))
			{
				hvf.notifyDataSetInvalidated();
				
				updateSum();
			}
			
		}
	}
	
	//绑定查询结果到列表
	private void BindResultToLlist()
	{
    	//绑定工程列表
    	hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_result1), "查询结果列表1",this._DataResultFieldList,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("列表选项"))
				{
					_SelectedItem = (HashMap<String,Object>)ExtraStr;
				}
			}});
    	
    	hvf.BindDataToListView(this._DataResultList,new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				_SelectedItem = (HashMap<String,Object>)ExtraStr;
			}});
    	
    	updateSum();
	}
	
	private boolean isScrolling = false;//是否正在滚动
	private boolean isloading = false;//判断是否正在加载中
	
	private void prepareScrollView()
	{
		ListView listView = (ListView) _Dialog.findViewById(R.id.rt_listview_scroll);
		listView.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) 
			{
				switch (scrollState) 
				{
				case OnScrollListener.SCROLL_STATE_FLING:
					isScrolling = true;
					break;
				case OnScrollListener.SCROLL_STATE_IDLE:
					isScrolling = false;
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					isScrolling = true;
					break;
				}
			}
			
			//是否已到最底
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) 
			{
				 if (totalItemCount <= 0)
				 {
                     return;
				 }
				if(firstVisibleItem+visibleItemCount ==totalItemCount )
				{
					if(isloading){
						return;
					}
					new AsyncTask<Void, String, List<HashMap<String,Object>>>() {
						protected List<HashMap<String,Object>> doInBackground(Void... params) 
						{
							List<HashMap<String,Object>> listNewBooks = null;
							try 
							{
								if(_DataResultList.size()< allCount)
								{
									listNewBooks = nextQuery(_CurrentPageIndex+1,_RowCountPerPage);
								}

							} catch (Exception e) {
								publishProgress("查询新数据失败！");
								e.printStackTrace();
							}
							return listNewBooks;
						}

						protected void onPreExecute() {
							//ll_loading.setVisibility(View.VISIBLE);
							isloading = true;
							super.onPreExecute();
						}

						protected void onPostExecute(List<HashMap<String,Object>> result) {
							//list.addAll(result);
							//ll_loading.setVisibility(View.GONE);
							//System.out.println("更新adapter");
							//adapter.notifyDataSetChanged();
							//System.out.println("一共有" + list.size() + "本书");
							if(result != null)
							{
								addItems(result);
								if(result.size() >0)
								{
									_CurrentPageIndex++;
								}
								//_DataResultList.addAll(result);
							}
							isloading = false;
							super.onPostExecute(result);
						}

						protected void onProgressUpdate(String... values) {
							Toast.makeText(_Dialog.getContext(), values[0], Toast.LENGTH_SHORT)
									.show();
							super.onProgressUpdate(values);
						}

					}.execute();
				}
				
			}
		});
	}

	
	
	/**
	 * 填充查询图层
	 */
	private void FillQueryLayerList()
	{
		List<HashMapEx> LayerList = new ArrayList<HashMapEx>();
		GeoLayers pGeoLayers = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll);
		for(GeoLayer pGeoLayer:pGeoLayers.getList())
		{
			boolean editing = pGeoLayer.getDataset().getDataSource().getEditing();
			HashMapEx hmx = new HashMapEx();
			hmx.put("D1", pGeoLayer.GetAliasName()+(editing?"【数据】":"【背景】"));
			hmx.put("LayerId", pGeoLayer.getId());
			LayerList.add(hmx);
		}
		v1_DataBind.SetBindListSpinnerByHashMap(_Dialog, "查询图层", LayerList, R.id.sp_layerlist,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				
				HashMapEx hmx = (HashMapEx)ExtraStr;
				GeoLayer pGeoLayer = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(hmx.get("LayerId")+"");
				if (pGeoLayer!=null)
				{
					_QueryParam.put("SQL_All","select * from "+pGeoLayer.getDataset().getDataTableName()+" where SYS_STATUS='0'order by SYS_ID ");
					_QueryParam.put("GeoLayer", pGeoLayer);
				} else _QueryParam.put("SQL_All","");
				
				needInitFieldsOnFirstQuery = true;
				_QueryType = "SQL_All";
	    		_CurrentPageIndex=0;
	    		MoveAfter();
	    		QuerySum();
				
			}});
	}
	
	
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				FillQueryLayerList();
				
				}}
    	);
    	_Dialog.show();
    }
}
