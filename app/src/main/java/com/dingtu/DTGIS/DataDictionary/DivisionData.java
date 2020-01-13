package com.dingtu.DTGIS.DataDictionary;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DictXZQH;
import com.dingtu.Funtion.DivisionEdit;
import com.dingtu.senlinducha.R;

import android.app.ProgressDialog;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class DivisionData 
{
	v1_FormTemplate DialogView;
	private DictXZQH dictDB;
	private ProgressDialog process;
	
	private String mSelectedTownCode = "";
	private String mSelectedTownName ="";
	
	public DivisionData(v1_FormTemplate dialog)
	{
		DialogView =dialog;
		((Spinner)DialogView.findViewById(R.id.sp_province)).setOnItemSelectedListener(new ProvinceOnItemSelectedListener());
    	((Spinner)DialogView.findViewById(R.id.sp_city)).setOnItemSelectedListener(new CityOnItemSelectedListener());
    	((Spinner)DialogView.findViewById(R.id.sp_county)).setOnItemSelectedListener(new CountyOnItemSelectedListener());

    	DialogView.findViewById(R.id.butAddXiang).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butEditXiang).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butDelXiang).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butAddCun).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butEditCun).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butDelCun).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butQHImport).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butQHEmport).setOnClickListener(new ViewClick());
    	
    	dictDB = new DictXZQH();
		initData();
	}
	
	private void initData()
	{
		List<HashMap<String,Object>> privinceList = dictDB.getXZQH("", "省");
		ArrayList<String> countyNames = new ArrayList<String>();
    	for(HashMap<String, Object> hm:privinceList)
    	{
    		countyNames.add(hm.get("D1").toString());
    	}
    	
		ArrayAdapter<Object> privinceAdapter = new ArrayAdapter<Object>(DialogView.getContext(),
				android.R.layout.simple_spinner_item,
				countyNames.toArray());
		privinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)DialogView.findViewById(R.id.sp_province)).setAdapter(privinceAdapter);
	}
	
	class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if(Tag.equals("导入"))
    		{
    			lkmap.ZRoadMap.ToolsBox.v1_selectdictionary sd = new lkmap.ZRoadMap.ToolsBox.v1_selectdictionary();
        		
        		sd.SetFileFilter(new String[]{"XLS"});
        		sd.SetCallback(new ICallback()
        		{
    				@Override
    				public void OnClick(String Str, final Object ExtraStrT) 
    				{
    					
    					List<String> importFileList = (List<String>)ExtraStrT;
    					for(String importFile:importFileList)
    					{
    						
    						try
    						{
    							final String fileName = importFile;
    							process = new ProgressDialog(PubVar.m_DoEvent.m_Context,ProgressDialog.THEME_HOLO_LIGHT);
    		    	    		process.setTitle("导入行政区划");
    		    	    		process.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    		    	    		
    		    	    		process.setIcon(R.drawable.v1_messageinfo);
    		    	    		process.setCancelable(false);
    		    	    		process.setCanceledOnTouchOutside(false);
    		    	    		process.show();
    		    	    		process.onStart();
    		    	    		
    		    	    		 new Thread() {       
    		    	                 public void run() { 
    		    	    	    	    {
    		    	    	    	    	Looper.prepare();
    		    	    	    	    	importDivision(fileName);
    		    	    	    	    	process.dismiss();
    		    	    	    	    	Looper.loop();	
    		    	    	    	    }}}.start();
    						}
    						catch(Exception ex)
    						{
    							Tools.ShowMessageBox(ex.getMessage());
    						}
    					}
    					
    				}
        		});
        		sd.ShowDialog("");
    		}
    		
    		if(Tag.equals("导出"))
    		{
    			process = new ProgressDialog(PubVar.m_DoEvent.m_Context,ProgressDialog.THEME_HOLO_LIGHT);
	    		process.setTitle("导出行政区划");
	    		process.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    		
	    		process.setIcon(R.drawable.v1_messageinfo);
	    		process.setCancelable(false);
	    		process.setCanceledOnTouchOutside(false);
	    		
	    		process.show();
	    		process.onStart();
	    		
	    		 new Thread() {       
	                 public void run() { 
	    	    	    {
	    	    	    	exportDivision();
	    	    	    	process.dismiss();;
	    	    	    }}}.start();
    			
    		}
    		if(Tag.equals("修改乡"))
    		{
    			if(mSelectedTownName.isEmpty())
    			{
    				Tools.ShowMessageBox("请选择要修改的乡(林场)！");
    				return;
    			}
    			else
    			{
    				DivisionEdit dEdit = new DivisionEdit();
        			dEdit.setInfo(false, "乡", Tools.GetSpinnerValueOnID(DialogView, R.id.sp_province), 
        									  Tools.GetTextValueOnID(DialogView, R.id.et_provinceCode), 
        									  Tools.GetSpinnerValueOnID(DialogView, R.id.sp_city), 
        									  Tools.GetTextValueOnID(DialogView, R.id.et_cityCode),
        									  Tools.GetSpinnerValueOnID(DialogView, R.id.sp_county), 
        									  Tools.GetTextValueOnID(DialogView, R.id.et_countyCode),
        									  mSelectedTownName,
        									  mSelectedTownCode,
        									  "", 
        									  "");
        			dEdit.setCallback(pCallback2);
        			dEdit.ShowDialog();
    			}
    		}
    		if(Tag.equals("增加乡"))
    		{
    			DivisionEdit dEdit = new DivisionEdit();
    			dEdit.setInfo(true, "乡", Tools.GetSpinnerValueOnID(DialogView, R.id.sp_province), 
    									  Tools.GetTextValueOnID(DialogView, R.id.et_provinceCode), 
    									  Tools.GetSpinnerValueOnID(DialogView, R.id.sp_city), 
    									  Tools.GetTextValueOnID(DialogView, R.id.et_cityCode),
    									  Tools.GetSpinnerValueOnID(DialogView, R.id.sp_county), 
    									  Tools.GetTextValueOnID(DialogView, R.id.et_countyCode),
    									  "", 
    									  "",
    									  "", 
    									  "");
    			dEdit.setCallback(pCallback2);
    			dEdit.ShowDialog();
    		}
    		if(Tag.equals("删除乡"))
    		{
    			if(mSelectedTownCode.isEmpty())
    			{
    				Tools.ShowMessageBox("请选择要删除的的乡(林场)！");
    				return;
    			}
    			else
    			{
    				Tools.ShowYesNoMessage(DialogView.getContext(), "您确定要删除选择的乡信息？", new ICallback() 
    				{
    					@Override
    					public void OnClick(String Str, Object ExtraStr) {
    						
    						if(Str.equals("YES"))
    						{
    							if(dictDB.deleteXZQH(mSelectedTownCode))
        	        			{
    								updateTownList();
        	        				return;
        	        			}
    						}
    					}
    						
    				});
    			}
    			
    		}
    		
    		if(Tag.equals("修改村"))
    		{
    			if(selectedCunName.isEmpty())
    			{
    				Tools.ShowMessageBox("请选择要修改的村(营林区)！");
    				return;
    			}
    			else
    			{
    				DivisionEdit dEdit = new DivisionEdit();
        			dEdit.setInfo(false, "村", Tools.GetSpinnerValueOnID(DialogView, R.id.sp_province), 
        									  Tools.GetTextValueOnID(DialogView, R.id.et_provinceCode), 
        									  Tools.GetSpinnerValueOnID(DialogView, R.id.sp_city), 
        									  Tools.GetTextValueOnID(DialogView, R.id.et_cityCode),
        									  Tools.GetSpinnerValueOnID(DialogView, R.id.sp_county), 
        									  Tools.GetTextValueOnID(DialogView, R.id.et_countyCode),
        									  mSelectedTownName,
        									  mSelectedTownCode,
        									  selectedCunName, 
        									  selectedCunCode);
        			dEdit.setCallback(pCallback2);
        			dEdit.ShowDialog();
    			}
    			
    		}
    		if(Tag.equals("增加村"))
    		{
    			if(mSelectedTownName.isEmpty())
    			{
    				Tools.ShowMessageBox("请先选择要增加村所属的的乡！");
    				return;
    			}
    			
    			DivisionEdit dEdit = new DivisionEdit();
    			dEdit.setInfo(true, "村", Tools.GetSpinnerValueOnID(DialogView, R.id.sp_province), 
    									  Tools.GetTextValueOnID(DialogView, R.id.et_provinceCode), 
    									  Tools.GetSpinnerValueOnID(DialogView, R.id.sp_city), 
    									  Tools.GetTextValueOnID(DialogView, R.id.et_cityCode),
    									  Tools.GetSpinnerValueOnID(DialogView, R.id.sp_county), 
    									  Tools.GetTextValueOnID(DialogView, R.id.et_countyCode),
    									  mSelectedTownName,
    									  mSelectedTownCode,
    									  "", 
    									  "");
    			dEdit.setCallback(pCallback2);
    			dEdit.ShowDialog();
    			
    		}
    		if(Tag.equals("删除村"))
    		{
    			if(selectedCunCode.isEmpty())
    			{
    				Tools.ShowMessageBox("请选择要删除的的村(营林区)！");
    				return;
    			}
    			else
    			{
    				Tools.ShowYesNoMessage(DialogView.getContext(), "您确定要删除选择的村信息？", new ICallback() 
    				{
    					@Override
    					public void OnClick(String Str, Object ExtraStr) {
    						
    						if(Str.equals("YES"))
    						{
    							if(dictDB.deleteXZQH(selectedCunCode))
        	        			{
    								updateCunList();
        	        				return;
        	        			}
    						}
    					}
    						
    				});
    				
    			}
    		}
    	}
    }
	
	ICallback pCallback2 = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr) 
		{
			 if(Str.equals("村"))
			 {
				 updateCunList();
			 }
			 if(Str.equals("乡"))
			 {
				 updateTownList();
			 }
			
		}};
	
	private void updateCunList()
	{
		List<HashMap<String,Object>> cunList = dictDB.getXZQH(mSelectedTownCode, "村");
		
		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(DialogView.findViewById(R.id.in_listview2), "村列表",cunCallback);
    	
    	hvf.BindDataToListView(cunList);
    	
    	clearCunSelected();

	}
	
	private void updateTownList()
	{
		List<HashMap<String,Object>> townList = dictDB.getXZQH(Tools.GetTextValueOnID(DialogView, R.id.et_countyCode), "乡");
		//绑定图层列表
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(DialogView.findViewById(R.id.in_listview1), "乡镇列表",pCallback);
    	clearTownSelected();
    	
    	hvf.BindDataToListView(townList);
    	
    	v1_HeaderListViewFactory hvf2 = new v1_HeaderListViewFactory();
    	hvf2.SetHeaderListView(DialogView.findViewById(R.id.in_listview2), "村列表",null);
   		hvf2.BindDataToListView(new ArrayList<HashMap<String,Object>>());
   		clearCunSelected();
	}
	
	 class  ProvinceOnItemSelectedListener implements OnItemSelectedListener
	 {          
       @Override  
       public void onItemSelected(AdapterView<?> adapter,View view,int position,long id) 
       {
       	
       	String provinceCode = dictDB.getCodeByName(Tools.GetSpinnerValueOnID(DialogView, R.id.sp_province), "省","0");
          	Tools.SetTextViewValueOnID(DialogView, R.id.et_provinceCode, provinceCode);
          	
          	if(!provinceCode.isEmpty())
          	{
          		List<HashMap<String,Object>> cityList = dictDB.getXZQH(provinceCode, "市");
           	
          		ArrayList<String> countyNames = new ArrayList<String>();
           	for(HashMap<String, Object> hm:cityList)
           	{
           		countyNames.add(hm.get("D1").toString());
           	}
           	
           	ArrayAdapter<Object> cityAdapter = new ArrayAdapter<Object>(DialogView.getContext(),
       				android.R.layout.simple_spinner_item,
       				countyNames.toArray());
           	cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       		((Spinner)DialogView.findViewById(R.id.sp_city)).setAdapter(cityAdapter);
          	}
          	else
          	{
          		((Spinner)DialogView.findViewById(R.id.sp_city)).setAdapter(new ArrayAdapter<Object>(DialogView.getContext(),
       				android.R.layout.simple_spinner_item,
       				new ArrayList<Object>().toArray()));
          	}
       	
       }
       
       @Override  
       public void onNothingSelected(AdapterView<?> arg0) 
       {  
           
       }  
	 }
	 
	 class  CityOnItemSelectedListener implements OnItemSelectedListener
	 {          
       @Override  
       public void onItemSelected(AdapterView<?> adapter,View view,int position,long id) 
       {
       	
       		String cityCode = dictDB.getCodeByName(Tools.GetSpinnerValueOnID(DialogView, R.id.sp_city), "市",Tools.GetTextValueOnID(DialogView, R.id.et_provinceCode));
          	Tools.SetTextViewValueOnID(DialogView, R.id.et_cityCode, cityCode);
          	if(!cityCode.isEmpty())
          	{
          		List<HashMap<String,Object>> countyList = dictDB.getXZQH(cityCode, "县");
	           	ArrayList<String> countyNames = new ArrayList<String>();
	           	for(HashMap<String, Object> hm:countyList)
	           	{
	           		countyNames.add(hm.get("D1").toString());
	           	}
	           	ArrayAdapter<Object> countyAdapter = new ArrayAdapter<Object>(DialogView.getContext(),
	       				android.R.layout.simple_spinner_item,
	       				countyNames.toArray());
	           	countyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	       		((Spinner)DialogView.findViewById(R.id.sp_county)).setAdapter(countyAdapter);
          	}
          	else 
          	{
          		((Spinner)DialogView.findViewById(R.id.sp_county)).setAdapter(new ArrayAdapter<Object>(DialogView.getContext(),
       				android.R.layout.simple_spinner_item,
       				new ArrayList<Object>().toArray()));
			}
   		
       }
       
       @Override  
       public void onNothingSelected(AdapterView<?> arg0) 
       {  
           
       }  
	 }
       
       
    class  CountyOnItemSelectedListener implements OnItemSelectedListener
  	 {          
          @Override  
          public void onItemSelected(AdapterView<?> adapter,View view,int position,long id) 
          {
	           	String countyCode = dictDB.getCodeByName(Tools.GetSpinnerValueOnID(DialogView, R.id.sp_county), "县",Tools.GetTextValueOnID(DialogView, R.id.et_cityCode));
	           	Tools.SetTextViewValueOnID(DialogView, R.id.et_countyCode, countyCode);
	           	if(!countyCode.isEmpty())
	           	{
	           		List<HashMap<String,Object>> townList = dictDB.getXZQH(countyCode, "乡");
		           	
		           	
		           //绑定图层列表
		        	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		        	hvf.SetHeaderListView(DialogView.findViewById(R.id.in_listview1), "乡镇列表",pCallback);
		        	clearTownSelected();
		        	hvf.BindDataToListView(townList);
	           	}
       	

       	
       	v1_HeaderListViewFactory hvf2 = new v1_HeaderListViewFactory();
       	hvf2.SetHeaderListView(DialogView.findViewById(R.id.in_listview2), "村列表",null);
      		hvf2.BindDataToListView(new ArrayList<HashMap<String,Object>>());
      		clearCunSelected();
          }
       
       @Override  
       public void onNothingSelected(AdapterView<?> arg0) 
       {  
           
       }  
	 }
    
    private String selectedCunName;
    private String selectedCunCode;
    private ICallback cunCallback = new ICallback(){
   		@Override
   		public void OnClick(String Str, Object ExtraStr)
   		{
   	    	
   	    	//选中字段列表后的回调
   	    	if (Str.equals("列表选项"))
   	    	{
   	    		HashMap<String,String> selectObj = (HashMap<String,String>)ExtraStr;
   	    		selectedCunName = selectObj.get("D1").toString();
   	    		selectedCunCode = selectObj.get("D2").toString();
   	    	}
   		}
    };
    
  //选中事件
   private ICallback pCallback = new ICallback()
   {
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	
	    	//选中字段列表后的回调
	    	if (Str.equals("列表选项"))
	    	{
	    		HashMap<String,Object> selectObj = (HashMap<String,Object>)ExtraStr;
	    		mSelectedTownCode = selectObj.get("D2").toString();
	    		mSelectedTownName = selectObj.get("D1").toString();
	    		
	    		List<HashMap<String,Object>> cunList = dictDB.getXZQH(mSelectedTownCode, "村");
	    		
	    		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
	        	hvf.SetHeaderListView(DialogView.findViewById(R.id.in_listview2), "村列表",cunCallback);
	        	
	        	hvf.BindDataToListView(cunList);

	    	}
		
	}};
	
	private void clearCunSelected()
	{
		selectedCunName ="";
		selectedCunCode = "";
	}
	private void clearTownSelected()
	{
		mSelectedTownCode = "";
		mSelectedTownName = "";
	}
	
	private void importDivision(String importFile)
	{
		try
		{
			ArrayList<HashMap<String,String>> al=new ArrayList<HashMap<String,String>>();
			//AssetManager am=PubVar.m_DoEvent.m_Context.getAssets();
			 	InputStream is= new FileInputStream(importFile);
			 	
				Workbook wb=Workbook.getWorkbook(is);
				Sheet sheet=wb.getSheet(0);
				int row=sheet.getRows();
				HashMap<String,String> hm;
				process.setMax(row);
				
				for(int i=1;i<row;i++)
				{
					process.setProgress(i);
					process.setMessage("正在导入行政区划："+i+"/"+row);
					Cell cellLevel=sheet.getCell(2, i);
					Cell CellParCode = sheet.getCell(0,i);
					Cell CellCode = sheet.getCell(1,i);
					Cell CellName = sheet.getCell(3,i);
					hm=new HashMap<String,String>();
					hm.put("Level", cellLevel.getContents());
					hm.put("ParCode", CellParCode.getContents());
					hm.put("Code", CellCode.getContents());
					hm.put("Name", CellName.getContents());
					if(!CellParCode.getContents().equals("0"))
					{
						if(!CellCode.getContents().contains(CellParCode.getContents()))
						{
							Tools.ShowToast(DialogView.getContext(), "第"+(i+1)+"行的编码和父编码不匹配！");
							is.close();
							return;
						}
					}
					
					dictDB.importXZQH(hm);
				}
			
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(ex.getMessage());
		}
	}
	
	private void exportDivision()
	{
		try
		{
			if (!lkmap.Tools.Tools.ExistFile(PubVar.m_SysAbsolutePath+"/Data/数据字典/"))
			{
				(new File(PubVar.m_SysAbsolutePath+"/Data/数据字典/")).mkdirs();
			}
	    	
			List<HashMap<String,String>> allXZQH = dictDB.exportXZQH("");
			WritableWorkbook  book = Workbook.createWorkbook(new File(PubVar.m_SysAbsolutePath+"/Data/数据字典/行政区划导出.xls"));
	        WritableSheet sheet = book.createSheet("sheet1", 0);
	        Label label1 = new Label(0, 0, "父代码");
	        sheet.addCell(label1);
	        Label label2 = new Label(1, 0, "代码");
	        sheet.addCell(label2); 
	        Label label3 = new Label(2, 0, "级别");
	        sheet.addCell(label3);
	        Label label4 = new Label(3, 0, "名称");
	        sheet.addCell(label4); 
	        process.setMax(allXZQH.size());
	        int i = 1;
	        for(HashMap<String,String> hm:allXZQH)
	        {
	        	process.setProgress(i);
				process.setMessage("正在导出行者行政区划："+i+"/"+allXZQH.size());
	        	Label dlabel1 = new Label(0, i, hm.get("ParCode"));
		        sheet.addCell(dlabel1);
		        Label dlabel2 = new Label(1, i, hm.get("Code"));
		        sheet.addCell(dlabel2); 
		        Label dlabel3 = new Label(2, i, hm.get("Level"));
		        sheet.addCell(dlabel3);
		        Label dlabel4 = new Label(3, i, hm.get("Name"));
		        sheet.addCell(dlabel4);
		        i++;
	        }
	        book.write();
	        book.close();
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox(ex.getMessage());
		}
	}
}
