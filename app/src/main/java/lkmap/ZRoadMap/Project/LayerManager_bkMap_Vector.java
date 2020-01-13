package lkmap.ZRoadMap.Project;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Enum.lkMapFileType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_MapFileAdapter;

public class LayerManager_bkMap_Vector
{
	private v1_FormTemplate _Dialog = null; 
    public LayerManager_bkMap_Vector()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.layermanager_bkmap_vector);
    	//_Dialog.ReSetSize(1f, 0.96f);
    	_Dialog.ReSetSize(0.5f, 0.8f);
    	//工程名称
    	String _ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
    	
    	//上部功能按钮事件绑定
    	_Dialog.SetCaption("【"+_ProjectName+"】"+Tools.ToLocale("矢量底图文件"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_project_clearbkmap+","+Tools.ToLocale("无底图")+"  ,无底图", pCallback);
    	
    	//选择所有复选框
    	((CheckBox)_Dialog.findViewById(R.id.cbselectall)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (m_MapFileList==null) return;
				for(HashMap<String,Object> hmObj:m_MapFileList)
				{
					hmObj.put("Select", arg1);
				}
				((v1_Project_Layer_BKMap_MapFileAdapter)(((ListView)_Dialog.findViewById(R.id.listView1))).getAdapter()).notifyDataSetChanged();
				//BindMapFileListToView(m_MapFileList);
			}});
    	
    	_Dialog.findViewById(R.id.btnXuanZeMulu).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				lkmap.ZRoadMap.ToolsBox.v1_selectdictionary sd = new lkmap.ZRoadMap.ToolsBox.v1_selectdictionary();
				sd.isSelectFolder = true;
				sd.SetCallback(new ICallback()
				{
					@Override
					public void OnClick(String Str, final Object ExtraStrT) 
					{
						List<String> folderList = (List<String>)ExtraStrT;
						for(String importFile:folderList)
						{
							List<HashMap<String,Object>> mapFileList = Tools.GetBKMapListFromFolder(importFile,lkMapFileType.enVector);
							AddMapFileList(mapFileList);
						}
						
				    	
					}
				});
				sd.ShowDialog(PubVar.m_SysAbsolutePath);
				
			}
    		
    	});
    	
    	//获取Map目录下的所有第一级字目录
    	List<String> ValidSubPathList = new ArrayList<String>();
    	ValidSubPathList.add("当前底图");ValidSubPathList.add("根目录");
    	String MapPath = PubVar.m_SysAbsolutePath+"/Map";
        File f = new File(MapPath);  
        File[] files = f.listFiles();
        for(File ff:files)if (ff.isDirectory())ValidSubPathList.add(ff.getName());
		
    	//绑定底图目录
    	v1_DataBind.SetBindListSpinner(_Dialog, "底图目录", ValidSubPathList, R.id.sp_subpath,new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if (ExtraStr.equals("根目录"))ExtraStr="";
				QueryBKMapFileOnSpecialPath(ExtraStr+"");
			}});

    }
    
    private void AddMapFileList(List<HashMap<String,Object>> mapFileList)
    {
    	if(mapFileList != null)
    	{
    		this.m_MapFileList.addAll(mapFileList);
    		v1_Project_Layer_BKMap_MapFileAdapter adapter = new v1_Project_Layer_BKMap_MapFileAdapter(_Dialog.getContext(),m_MapFileList, 
				       R.layout.v1_bk_project_layer_bkmap_list, 
				       new String[] {"Select", "BKMapFile", "F1"}, 
				       new int[] {R.id.cbselect, R.id.tvname, R.id.tvcoorsystem});  
    		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
    	}
    	
    }
    
	//选中底图并按【确定】后回调
	private ICallback m_Callback = null;
	
	/**
	 * 选中底图并按【确定】后回调
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
    //上部按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("无底图"))
			{
				m_MapFileList.clear();
				pCallback.OnClick("确定", "");
			}
	    	if (Str.equals("确定"))
	    	{
	    		if (m_Callback==null) _Dialog.dismiss();

	    		//选中的背景底图项
	    		if (m_MapFileList!=null)
	    		{
	    			List<HashMap<String,Object>> SelectMapFileList = new ArrayList<HashMap<String,Object>>();
	    			for(HashMap<String,Object> mapFile:m_MapFileList)
	    			{
	    				if (Boolean.parseBoolean(mapFile.get("Select")+""))
						{
	    					SelectMapFileList.add(mapFile);
						}
	    			}
	    	    	if(!PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().SaveBKLayer("矢量",SelectMapFileList))
	    	    	{
	    	    		Tools.ShowMessageBox("矢量底图保存失败！");
	    	    	}
	    	    	if (m_Callback!=null)m_Callback.OnClick("","");
	    			//m_Callback.OnClick("【"+MapFileNameList.size()+"】"+Tools.JoinT(",", MapFileNameList), SelectMapFileList);
	    		}
	    		_Dialog.dismiss();
	    	}
	    	
		}};
		
	
    /**
     * 加载指定目录下底图文件列表信息
     */
	private void QueryBKMapFileOnSpecialPath(String SubPath)
	{
		if (SubPath.equals("当前底图"))
		{
			this.BindMapFileListToView(this.m_CurrentMapFileList);
			return;
		}
    	
    	//读取已经有哪些底图文件
    	List<HashMap<String,Object>> mapFileList = Tools.GetBKMapList(SubPath,lkMapFileType.enVector);
    	this.BindMapFileListToView(mapFileList);
	}
	
	//当前底图列表
	private List<HashMap<String,Object>> m_CurrentMapFileList = null;
	
	//新底图列表
	private List<HashMap<String,Object>> m_MapFileList = null;
	
	/**
	 * 绑定底图列表到listView
	 * @param mapFileList
	 */
    private void BindMapFileListToView(List<HashMap<String,Object>> mapFileList)
    {
		//刷新列表
    	if (mapFileList==null)mapFileList = new ArrayList<HashMap<String,Object>>();
    	v1_Project_Layer_BKMap_MapFileAdapter adapter = new v1_Project_Layer_BKMap_MapFileAdapter(_Dialog.getContext(),mapFileList, 
											       R.layout.v1_bk_project_layer_bkmap_list, 
											       new String[] {"Select", "BKMapFile", "CoorSystem"}, 
											       new int[] {R.id.cbselect, R.id.tvname, R.id.tvcoorsystem});  
		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
		this.m_MapFileList = mapFileList;
    }

    //设置底图文件列表
    public void SetMapFileList(List<HashMap<String,Object>> mapFileList)
    {
    	this.m_CurrentMapFileList = mapFileList;
    	this.m_MapFileList = mapFileList;
    }
    
    /**
     * 加载底图信息
     */
    private void LoadBKMapInfo()
    {
		//坐标系统
		CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
		String CoorSystemInfo = CS.GetName()+"【"+CS.GetCenterMeridian()+"】";
		if (CS.GetName().equals("WGS-84坐标"))CoorSystemInfo = CS.GetName();
    	Tools.SetTextViewValueOnID(_Dialog, R.id.et_prjInfo, CoorSystemInfo);
    	
    	//读取当前工程所绑定的背景图
    	this.BindMapFileListToView(this.m_MapFileList);

    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadBKMapInfo();}});
    	_Dialog.show();
    }
}

