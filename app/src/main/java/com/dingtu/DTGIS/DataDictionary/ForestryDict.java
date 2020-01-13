package com.dingtu.DTGIS.DataDictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DictDataDB;
import com.dingtu.Funtion.SimpleTreeAdapter;
import com.dingtu.senlinducha.R;
import com.zhy.tree.bean.Bean;
import com.zhy.tree.bean.FileBean;
import com.zhy.tree.bean.Node;
import com.zhy.tree.bean.TreeListViewAdapter;
import com.zhy.tree.bean.TreeListViewAdapter.OnTreeNodeClickListener;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class ForestryDict 
{
	private v1_FormTemplate DialogView;
	private List<Bean> mDatas = new ArrayList<Bean>();
	private List<FileBean> mDatas2 = new ArrayList<FileBean>();
	private TreeListViewAdapter mAdapter;
	private DictDataDB mDictDB;
	private boolean isAdd = false;
	private int parentID = 0;
	private int ID = 0;
	private ProgressDialog process;
	
	public ForestryDict(v1_FormTemplate dialog)
	{
		DialogView = dialog;
		
		DialogView.findViewById(R.id.butSave).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butDel).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butAddSameLevel).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butAddNextLevel).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butImport).setOnClickListener(new ViewClick());
    	DialogView.findViewById(R.id.butExport).setOnClickListener(new ViewClick());
    	mDictDB = new DictDataDB();
    	
		
		String strproject ="自定义工程,退耕还林,林地变更,森林资源二类调查";
		ArrayAdapter<String> projectTypeAdapter = new ArrayAdapter<String>(DialogView.getContext(),
																	android.R.layout.simple_spinner_item,
																	strproject.split(","));
		projectTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = ((Spinner)DialogView.findViewById(R.id.sp_projectType));
		spinner.setAdapter(projectTypeAdapter);
		spinner.setOnItemSelectedListener(new YinziOnItemSelectedListener());
		
		
		initTreeView();
		
	}
	
	class  YinziOnItemSelectedListener implements OnItemSelectedListener
	 {          
	        @Override  
	        public void onItemSelected(AdapterView<?> adapter,View view,int position,long id) 
	        {
	        	initTreeView();
	        }
	        
	        @Override  
	        public void onNothingSelected(AdapterView<?> arg0) {  
	            
	        }  
	 }
	 
	 class ViewClick implements View.OnClickListener
	    {
	    	@Override
	    	public void onClick(View arg0)
	    	{
	    		String Tag = arg0.getTag().toString();
	    		if(Tag.equals("保存"))
	    		{
	    			if(isAdd)
	    			{
	    				if(mDictDB.addDict(Tools.GetTextValueOnID(DialogView,  R.id.et_name), 
	    						Tools.GetTextValueOnID(DialogView,  R.id.et_code), 
	    						parentID+"",
	    						"林业", 
	    						Tools.GetSpinnerValueOnID(DialogView, R.id.sp_projectType)))
						{
	    					Tools.ShowMessageBox("新数据字典添加成功！");
	    					initTreeView();
						}
	    					
	    			}
	    			else
	    			{
	    				if(mDictDB.updateDict(Tools.GetTextValueOnID(DialogView,  R.id.et_name),	
	    									  ID, 
	    									  Tools.GetTextValueOnID(DialogView,  R.id.et_code)))
	    				{
	    					Tools.ShowMessageBox("新数据字典更新成功！");
	    					initTreeView();
	    				}
	    			}
	    		}
	    		if(Tag.equals("删除"))
	    		{
	    			if(isAdd)
	    			{
	    				Tools.ShowMessageBox("当前是添加状态，请从左侧选择要删除的字典对象！");
	    			}
	    			else
	    			{
	    				if(ID==0)
	    				{
	    					Tools.ShowMessageBox("请先选中要删除的字典项！");
	    					return;
	    				}
	    				if(mDictDB.deleteDict(ID))
	    				{
	    					Tools.ShowMessageBox("当前字典类别以及子类别删除成功！");
	    					parentID = 0;
	    					ID = 0;
	    					clearEditText();
	    					Tools.SetTextViewValueOnID(DialogView, R.id.et_parname, "");
	    					initTreeView();
	    				}
	    			}
	    			return;
	    		}
	    		if(Tag.equals("同级添加"))
	    		{
	    			if(isAdd)
	    			{
	    				Tools.ShowMessageBox("已处于添加状态！");
	    				return;
	    			}
	    			clearEditText();
	    			isAdd = true;
	    			return;
	    		}
	    		if(Tag.equals("下级添加"))
	    		{
	    			if(isAdd)
	    			{
	    				Tools.ShowMessageBox("已处于添加状态！");
	    				return;
	    			}
	    			if(ID == 0)
	    			{
	    				Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "请现在左侧列表选择当前级别");
	    				return;
	    			}
	    			parentID = ID;
	    			Tools.SetTextViewValueOnID(DialogView, R.id.et_parname, Tools.GetTextValueOnID(DialogView,  R.id.et_name));
	    			clearEditText();
	    			isAdd = true;
	    			return;
	    		}
	    		
	    		if(Tag.equals("导出"))
	    		{
	    			try
	    			{
	    				process = new ProgressDialog(PubVar.m_DoEvent.m_Context,ProgressDialog.THEME_HOLO_LIGHT);
	    	    		process.setTitle("导出数据字典");
	    	    		process.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    	    		
	    	    		process.setIcon(R.drawable.v1_messageinfo);
	    	    		process.setCancelable(false);
	    	    		process.setCanceledOnTouchOutside(false);
	    	    		process.show();
	    	    		process.onStart();
	    	    		
	    	    		 new Thread() {       
	    	                 public void run() { 
	    	    	    	    {
	    	    	    	    	exportDict();
	    	    	    	    	process.dismiss();
	    	    	    	    }}}.start();
	    	    	    	    
	    				
	    		    	
	    				
	    				
	    		        //Tools.ShowMessageBox("所有行政区划数据已导出到【"+PubVar.m_SysAbsolutePath+"/Data/数据字典/数据字典导出.xls】");
	    			}
	    			catch(Exception ex)
	    			{
	    				Tools.ShowMessageBox(ex.getMessage());
	    			}
	    		}
	    		
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
	    		    	    		process.setTitle("导入数据字典");
	    		    	    		process.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    		    	    		
	    		    	    		process.setIcon(R.drawable.v1_messageinfo);
	    		    	    		process.setCancelable(false);
	    		    	    		process.setCanceledOnTouchOutside(false);
	    		    	    		process.show();
	    		    	    		process.onStart();
	    		    	    		
	    		    	    		 new Thread() {       
	    		    	                 public void run() { 
	    		    	    	    	    {
	    		    	    	    	    	importDict(fileName);
	    		    	    	    	    	process.dismiss();
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
	    	}
	    }
	 
	 private void importDict(final String importFile)
		{
			try
			{
				 	InputStream is= new FileInputStream(importFile);
				 	
					Workbook wb=Workbook.getWorkbook(is);
					Sheet sheet=wb.getSheet(0);
					int row=sheet.getRows();
					HashMap<String,String> hm = null;
					process.setMax(row);
					for(int i=1;i<row;i++)
					{
						process.setProgress(i);
						process.setMessage("正在导入数据字典："+i+"/"+row);
						Cell cellParentName=sheet.getCell(0, i);
						Cell CellName= sheet.getCell(1,i);
						Cell CellCode = sheet.getCell(2,i);
						Cell CellHangye = sheet.getCell(3,i);
						Cell CellProjectType = sheet.getCell(4,i);
						int pID = 0;
						pID = mDictDB.getPID(cellParentName.getContents(), CellHangye.getContents(), CellProjectType.getContents());
						hm = new HashMap<String, String>();
						hm.put("pID", pID+"");
						hm.put("name", CellName.getContents());
						hm.put("code", CellCode.getContents());
						hm.put("hangye", CellHangye.getContents());
						hm.put("projecttype", CellProjectType.getContents());
						mDictDB.importXZQH(hm);		
					}
				
			}
			catch(Exception ex)
			{
				Tools.ShowMessageBox(ex.getMessage());
			}
		}
		 
		private void exportDict()
		{
			try
			{
				if (!lkmap.Tools.Tools.ExistFile(PubVar.m_SysAbsolutePath+"/Data/数据字典/"))
				{
					(new File(PubVar.m_SysAbsolutePath+"/Data/数据字典/")).mkdirs();
				}
				
				WritableWorkbook  book = Workbook.createWorkbook(new File(PubVar.m_SysAbsolutePath+"/Data/数据字典/数据字典导出.xls"));
		        WritableSheet sheet = book.createSheet("sheet1", 0);
		        Label label1 = new Label(0, 0, "上级类别");
		        sheet.addCell(label1);
		        Label label2 = new Label(1, 0, "类别");
		        sheet.addCell(label2); 
		        Label label3 = new Label(2, 0, "代码/值");
		        sheet.addCell(label3);
		        Label label4 = new Label(3, 0, "行业");
		        sheet.addCell(label4);
		        Label label5 = new Label(4, 0, "项目类型");
		        sheet.addCell(label5);
		        List<HashMap<String,String>> allXZQH = mDictDB.exportXZQH();
		        process.setMax(allXZQH.size());
		        int i = 1;
		        for(HashMap<String,String> hm:allXZQH)
		        {
		        	process.setProgress(i);
					process.setMessage("正在导出数据字典："+i+"/"+allXZQH.size());
					
		        	Label dlabel1 = new Label(0, i, hm.get("PName"));
			        sheet.addCell(dlabel1);
			        Label dlabel2 = new Label(1, i, hm.get("Name"));
			        sheet.addCell(dlabel2); 
			        Label dlabel3 = new Label(2, i, hm.get("Code"));
			        sheet.addCell(dlabel3);
			        Label dlabel4 = new Label(3, i, hm.get("HangYe"));
			        sheet.addCell(dlabel4);
			        Label dlabel5 = new Label(4, i, hm.get("ProjectType"));
			        sheet.addCell(dlabel5);
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
		private void clearEditText()
		{
			Tools.SetTextViewValueOnID(DialogView, R.id.et_name, "");
			Tools.SetTextViewValueOnID(DialogView, R.id.et_code, "");
		}
		
		private  void initTreeView()
		{
			ListView mTree = (ListView) DialogView.findViewById(R.id.id_tree);
			try
			{
				getTreeViewData();
				
				mAdapter = new SimpleTreeAdapter<FileBean>(mTree, DialogView.getContext(), mDatas2, 0);
				mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener()
				{
					@Override
					public void onClick(Node node, int position)
					{
						ID= node.getId();
						Tools.SetTextViewValueOnID(DialogView, R.id.et_name, node.getName());
						Tools.SetTextViewValueOnID(DialogView, R.id.et_code, node.getCode());
						if(node.getParent() != null)
						{
							parentID = node.getParent().getId();
							Tools.SetTextViewValueOnID(DialogView, R.id.et_parname, node.getParent().getName());
						}
						else
						{
							parentID = 0;
							Tools.SetTextViewValueOnID(DialogView, R.id.et_parname,"");
						}
						isAdd = false;
					}

				});

			} catch (Exception e)
			{
				e.printStackTrace();
			}
			mTree.setAdapter(mAdapter);
		}
		
		private void getTreeViewData()
		{
			mDatas2.clear();
			List<HashMap<String, Object>> source = mDictDB.getDictData(Tools.GetSpinnerValueOnID(DialogView, R.id.sp_projectType),"林业");
			for(HashMap<String, Object> hm:source)
			{
				mDatas2.add(new FileBean(Integer.parseInt(hm.get("ID")+""), Integer.parseInt(hm.get("PID")+""), hm.get("Name")+"", hm.get("Code")+""));
			}
		}
}
