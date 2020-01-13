package dingtu.ZRoadMap.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import dingtu.ZRoadMap.PubVar;
import lkmap.Tools.Tools;

public class DataQuery_Import 
{
	private v1_FormTemplate DialogView = null;
	private String mLayerName = "默认";
	List<HashMap<String,Object>> mResultFieldList = new ArrayList<HashMap<String,Object>>();
	List<HashMap<String,Object>> mResultList = new ArrayList<HashMap<String,Object>>();
	
	public DataQuery_Import()
	{
		DialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		DialogView.SetOtherView(R.layout.d_dataquery_export);
		DialogView.SetCaption(Tools.ToLocale("数据导出"));
		
		DialogView.ReSetSize(0.6f, -1f);
		DialogView.SetCaption("查询条件");
		
		DialogView.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("导出数据")+"  ,导出数据", new ICallback()
		{
			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				exportData();
			}
		});
		
		String sss ="Excel(CSV)";
		ArrayAdapter<String> trflzkAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
				android.R.layout.simple_spinner_item,
				sss.split(","));
		trflzkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)DialogView.findViewById(R.id.sp_format)).setAdapter(trflzkAdapter);
		
		String pathName = Tools.GetSystemDate().replace("-", "").replace(":", "").replace(" ", "");
		Tools.SetTextViewValueOnID(DialogView, R.id.pn_projectname, pathName);
	}
	
	public void setDataSet(String layerName,List<HashMap<String,Object>> resultFieldList,List<HashMap<String,Object>> resultList)
	{
		mLayerName = layerName;
		mResultFieldList = resultFieldList;
		mResultList = resultList;
		Tools.SetTextViewValueOnID(DialogView, R.id.tv_datacount, "可导出实体数量："+mResultList.size());
	}
	
	public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
		DialogView.setOnShowListener(new OnShowListener()
		{
			@Override
			public void onShow(DialogInterface dialog) 
			{
				
			}}
    	);
		DialogView.show();
    }
	
	
	private void exportData()
	{
		exportCSV();
	}
	
	private void exportPhoto()
	{
		
	}
	
	@SuppressLint("NewApi")
	private void exportCSV()
	{
		String sdCardDir = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/数据导出/";
		String folderName = Tools.GetTextValueOnID(DialogView, R.id.pn_projectname);
		if(!folderName.isEmpty())
		{
			sdCardDir +=folderName;
		}
		
		String fileName = mLayerName+".CSV";
		
		try
		{
			File saveFile = new File(sdCardDir, fileName);
	    	if (!lkmap.Tools.Tools.ExistFile(sdCardDir))
			{
				(new File(sdCardDir)).mkdirs();
			}
	    	
	    	FileOutputStream bcpFileWriter = new FileOutputStream(saveFile); 
	    	
	    	byte[] bom ={(byte) 0xEF,(byte) 0xBB,(byte) 0xBF}; 
	    	bcpFileWriter.write(bom);
	    	String f="";
	    	for(HashMap<String,Object> fieldObj:mResultFieldList)
	    	{
	    		f += fieldObj.get("FieldName")+",";
	    		
	    	}
	    	f +="\n";
	    	bcpFileWriter.write((new String(f.getBytes(), "UTF-8")).getBytes());
	    	
	    	for(HashMap<String,Object> ValueObj:mResultList)
	    	{
	    		String value="";
	    		for(int i = 1;i<ValueObj.keySet().size()-1;i++)
	    		{
	    			value+= ValueObj.get("D"+i)+",";
	    		}
	    		value+="\n";
	    		bcpFileWriter.write((new String(value.getBytes(), "UTF-8")).getBytes());
	    	}
	    	
	    	bcpFileWriter.flush();
    		bcpFileWriter.close();
    		lkmap.Tools.Tools.ShowMessageBox(DialogView.getContext(), "数据成功导出！\r\n\r\n位于：【"+sdCardDir+fileName+"】");
	    	
		}
		catch(Exception ex)
		{
			
		}
	}
	
}
