package lkmap.ZRoadMap.ToolsBox;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import lkmap.Enum.lkEditMode;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_DictionaryList_Adpter;
import lkmap.ZRoadMap.MyControl.v1_LayerList_Adpter;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_MapFileAdapter;
import lkmap.ZRoadMap.Project.v1_Layer;

public class v1_selectdictionary
{
	public v1_FormTemplate _Dialog = null;
	
	public boolean isSelectFolder = false;

    public v1_selectdictionary()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_selectdictionary);
    	_Dialog.ReSetSize(0.5f,0.7f);
    	
    	//设置标题
    	_Dialog.SetCaption(Tools.ToLocale("选择目录"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("确定")+" ,确定", pCallback);
    	
    	_Dialog.findViewById(R.id.bt_back).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				LinearLayout view = (LinearLayout)_Dialog.findViewById(R.id.ll_pathbutton);
				if (view.getChildCount()>1)
				{
					View vv = view.getChildAt(view.getChildCount()-2);
					Button bt = (Button)vv.findViewById(R.id.bt_path);
					bt.performClick();
				}
			}});
    	
    	View view = ((LinearLayout)_Dialog.findViewById(R.id.ll_pathbutton)).getChildAt(0);
    	view.findViewById(R.id.bt_path).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Purge(v);
				LoadDictionary("");
			}});
    }
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("确定"))
			{
				List<String> SelectFileList = new ArrayList<String>();
				for(HashMap<String,Object> ho:m_PathItemList)
				{
					if (Boolean.parseBoolean(ho.get("Select")+""))
					{
						SelectFileList.add(ho.get("PathFullName")+"");
					}
				}
				if (SelectFileList.size()==0)
				{
					Tools.ShowMessageBox(_Dialog.getContext(), "请勾选需要的文件！");
					return;
				}
				
				if (m_Callback!=null)m_Callback.OnClick("", SelectFileList);
				_Dialog.dismiss();

			}
		}};
		
	//回调
	private ICallback m_Callback = null;
	
	/**
	 * 回调
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	
	private List<View> m_PathButtonList = new ArrayList<View>();
	private void AddPathButton(String pathFullName)
	{
		LinearLayout view = (LinearLayout)_Dialog.findViewById(R.id.ll_pathbutton);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( 
	                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); 
		LayoutInflater inflater3 = LayoutInflater.from(_Dialog.getContext());
		View newView = inflater3.inflate(R.layout.v1_selectdictionary_header, null);
		newView.setLayoutParams(lp);
		Button bt = (Button)newView.findViewById(R.id.bt_path);
		bt.setText(GetPathName(pathFullName));
		bt.setTag(pathFullName);
		newView.setTag(pathFullName);
		bt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Purge(v);
				LoadDictionary(v.getTag()+"");
			}});
		view.addView(newView);
	}
	
	private void Purge(View v)
	{
		int Pos = -1;
		LinearLayout viewT = (LinearLayout)_Dialog.findViewById(R.id.ll_pathbutton);
		for(int i=0;i<viewT.getChildCount();i++)
		{
			View v1 = viewT.getChildAt(i);
			if (v1.getTag().toString().equals(v.getTag().toString()))Pos=i;
		}
		int ViewCount = viewT.getChildCount();
		for(int i=ViewCount-1;i>Pos;i--)
		{
			viewT.removeViewAt(i);
		}
	}

	//文件过滤器
	private String[] m_FileFilter = null;
	
	/**
	 * 设置文件过滤器
	 * @param fileFilter
	 */
	public void SetFileFilter(String[] fileFilter)
	{
		this.m_FileFilter = fileFilter;
	}
	
	
	private v1_DictionaryList_Adpter m_LayerList_Adpter = null;
	private List<HashMap<String,Object>> m_PathItemList = new ArrayList<HashMap<String,Object>>();
	
    /**
     * 加载图层列表信息
     */
    private void LoadDictionary(String parentPath)
    {
    	this.m_PathItemList.clear();
    	if (parentPath.equals(""))
    	{
    		List<HashMap<String,Object>> parentPathList = Tools.GetAllSDCardInfoList(PubVar.m_DoEvent.m_Context);
    		int xh = 1;
    		for(HashMap<String,Object> ho:parentPathList)
    		{
    			ho.put("PathName", "存储卡"+xh);xh++;
    			ho.put("PathFullName", ho.get("SDPath"));
    			ho.put("Image", null);
    			ho.put("Type", "目录");
    			ho.put("Select", false);
    			this.m_PathItemList.add(ho);
    		}
    	} else
    	{
	    	File f = new File(parentPath);  
	        File[] files = f.listFiles();// 列出所有文件
	        for(File ff:files)
	        {
	        	if (ff.isDirectory())
	        	{
	        		String FileName = ff.getAbsolutePath();
	        		HashMap<String,Object> ho = new HashMap<String,Object>();
	        		ho.put("PathName",GetPathName(FileName));
	        		ho.put("PathFullName", FileName);
	        		ho.put("Image", null);
	        		ho.put("Type", "目录");
	        		ho.put("Select", false);
	        		this.m_PathItemList.add(ho);
	        	}
	        }
	        for(File ff:files)
	        {
	        	if (this.m_FileFilter==null)break;
	        	if (ff.isFile())
	        	{
	        		String FileName = ff.getAbsolutePath();
	        		
	        		HashMap<String,Object> ho = new HashMap<String,Object>();
	        		ho.put("PathName",GetPathName(FileName));
	        		ho.put("PathFullName", FileName);
	        		ho.put("Image", null);
	        		ho.put("Type", "文件");
	        		ho.put("Select", false);
	        		if (FileName.length()<4)continue;
	        		int dotIndex = FileName.lastIndexOf(".");
	        		if(dotIndex <= 0 ||dotIndex == FileName.length())
	        		{
	        			continue;
	        		}
	        		FileName = FileName.substring(dotIndex+1,FileName.length()).toUpperCase();
	        		for(String filter:this.m_FileFilter)
	        		{
	        			if (filter.toUpperCase().equals(FileName))
	        			{
	    	        		if (FileName.contains("VMX"))ho.put("Image", Tools.GetBitmapByResources(R.drawable.v1_file_vmx));
	    	        		if (FileName.contains("IMX"))ho.put("Image", Tools.GetBitmapByResources(R.drawable.v1_file_imx));
	    	        		if (FileName.contains("SHP"))ho.put("Image", Tools.GetBitmapByResources(R.drawable.v1_file_shp));
	    	        		if (ho.get("Image")==null)ho.put("Image", Tools.GetBitmapByResources(R.drawable.v1_file_bank));
	        			}
	        			
	        		}

	        		if (ho.get("Image")==null)
        			{
        				continue;
        			}
	        		this.m_PathItemList.add(ho);
	        	}
	        }
    	}
        		
        if (this.m_LayerList_Adpter== null)
        {
        	if(isSelectFolder)
        	{
        		this.m_LayerList_Adpter = new v1_DictionaryList_Adpter(_Dialog.getContext(),
						this.m_PathItemList, 
						R.layout.v1_bk_dictionary_item,
						new String[] { "PathName","Image","Select"}, 
						new int[] { R.id.tv_path,R.id.iv_image,R.id.rb_select});
        	}
        	else
        	{
        		this.m_LayerList_Adpter = new v1_DictionaryList_Adpter(_Dialog.getContext(),
						this.m_PathItemList, 
						R.layout.v1_bk_dictionary_item,
						new String[] { "PathName","Image","Select"}, 
						new int[] { R.id.tv_path,R.id.iv_image,R.id.cb_select});
        	}
    		
        }
        
		ListView lvList = (ListView)_Dialog.findViewById(R.id.lvList);
		lvList.setAdapter(this.m_LayerList_Adpter);
		
		lvList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				ListView lvList = (ListView)arg0;
				v1_DictionaryList_Adpter la = (v1_DictionaryList_Adpter)lvList.getAdapter();
				HashMap<String,Object> ho = m_PathItemList.get(arg2);
				if (ho.get("Type").equals("文件"))return;
				String FullName = ho.get("PathFullName")+"";
				LoadDictionary(FullName);
				AddPathButton(FullName);
			}});
    }
    
    private String GetPathName(String fullName)
    {
    	String[] pathInfo = fullName.split("/");
    	return pathInfo[pathInfo.length-1];
    }
    public void ShowDialog(final String currentPath)
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadDictionary(currentPath);
				AddPathButton(currentPath);
				
				}}
    	);
    	_Dialog.show();
    }

}
