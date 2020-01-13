package lkmap.ZRoadMap.Project;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import dingtu.ZRoadMap.HashValueObject;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_select
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_select()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_select);
    	_Dialog.ReSetSize(0.5f,0.96f);
    	
    	//当前正在打开的工程
    	_Dialog.findViewById(R.id.ll_currentproject).setVisibility(View.GONE);
    	HashValueObject HO = PubVar.m_HashMap.GetValueObject("Project");
    	if (HO!=null)
		{
    		_Dialog.findViewById(R.id.ll_currentproject).setVisibility(View.VISIBLE);
    		Tools.SetTextViewValueOnID(_Dialog, R.id.bt_currentproject, " "+HO.Value);
    		_Dialog.findViewById(R.id.bt_currentproject).setTag(HO.Value);
    		_Dialog.findViewById(R.id.bt_currentproject).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					//打开工程属性窗体
					HashMap<String,String> prjInfo = new HashMap<String,String>();
					prjInfo.put("Name",arg0.getTag().toString());
					prjInfo.put("CreateTime","");
					v1_project_open po = new v1_project_open();
					po.SetProject(prjInfo);
					po.SetCallback(pCallback);
					po.ShowDialog();
					
				}});
		}
    	
    	_Dialog.SetCaption(Tools.ToLocale("工程管理"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_project_new+","+Tools.ToLocale("新建")+" ,新建工程", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_deleteobject+","+Tools.ToLocale("删除")+" ,删除工程", pCallback);
    	//_Dialog.SetButtonInfo("1,"+R.drawable.v1_project_open+",打开  ,打开工程", pCallback);
    	
    	//进行文字语言转换
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText2));
    	//_Dialog.findViewById(R.id.tvl)
    }
    
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, final Object ExtraStr)
		{
	    	if (Str.equals("新建工程"))
	    	{
	    		v1_project_new vpn = new v1_project_new();
	    		vpn.SetNewProjectCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						LoadProjectListInfo();
					}});
	    		vpn.ShowDialog();
	    	}
	    	if (Str.equals("打开工程"))
	    	{
	    		Tools.OpenDialog(Tools.ToLocale("正在打开工程")+"...", new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr1) {
		    			if (!OpenProject(ExtraStr.toString())) return;
		    			_Dialog.dismiss();
					}});
	    		
	    		_Dialog.dismiss();
	    	}
	    	
	    	if (Str.equals("删除工程"))
	    	{
	    		final List<String> delLayerNameList = new ArrayList<String>();
	       		//提取选中的图层
	    		for(HashMap<String,Object> hashObj:m_HeaderListViewDataItemList)
	    		{
	    			//可选
    				boolean selected = Boolean.parseBoolean(hashObj.get("D1").toString());
    				String PrjName = hashObj.get("D2").toString();
    				if (selected)delLayerNameList.add(PrjName);
	    		}
	    		if (delLayerNameList.size()==0)
	    		{
	    			Tools.ShowMessageBox(_Dialog.getContext(),Tools.ToLocale("请勾选需要删除的工程")+"！");
	    			return;
	    		}
	    		else
	    		{
	    			Tools.ShowYesNoMessage(_Dialog.getContext(), Tools.ToLocale("是否确定要删除以下工程")+"？\r\n"+Tools.ToLocale("工程名称")+"："+Tools.JoinT("\r\n"+Tools.ToLocale("工程名称")+"：", delLayerNameList), new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) 
						{
							if (Str.equals("YES"))
							{
 								String prjPath = PubVar.m_SysAbsolutePath+"/Data";
								for(String prjName:delLayerNameList)
								{
									File f = new File(prjPath+"/"+prjName);
									Tools.DeleteAll(f);
								}
								LoadProjectListInfo();
							}
						}});
	    		}
	    	}
		}
    };
    
	/**
	 * 打开工程
	 * @param ProjectName 工程名称，不含路径
	 * @return
	 */
	private boolean OpenProject(String ProjectName)
	{
		//在此判断是否可以再次打开新的工程，也就是上一次工程是否有正在采集的数据
		if (PubVar.m_DoEvent.CheckHasDataInTask("正在采集数据中，请停止采集任务后再尝试切换工程！")) return false;

		
		//保存起当前工程项目信息
		HashValueObject hvo = new HashValueObject();
		hvo.Value = ProjectName;
		PubVar.m_HashMap.Add("Project",hvo);
		
		//打开工程
		PubVar.m_DoEvent.m_ProjectDB.CloseProject();
		if (PubVar.m_DoEvent.m_ProjectDB.OpenProject(hvo.Value))  //图层列表已经形成
		{
			PubVar.m_DoEvent.DoCommand("工程_打开");
		}
		return true;
	}

	//工程列表绑定的数据项
	private List<HashMap<String,Object>> m_HeaderListViewDataItemList = null;
	
    /**
     * 加载工程列表信息
     */
    private void LoadProjectListInfo()
    {
    	//绑定工程列表
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "工程列表",new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("列表选项"))
				{
					_Dialog.GetButton("1").setEnabled(true);
					
					//调取数据预览图
					ShowDataPreview((HashMap<String,Object>)ExtraStr);
				}
			}});
    	
    	//读取已经创建哪些工程
    	List<String> ProjectList = Tools.GetProjectList();
    	
		//当前正在打开的工程，不允许删除
    	String CurrentProjectName = "";
		HashValueObject HO = PubVar.m_HashMap.GetValueObject("Project");
		if (HO!=null)CurrentProjectName = HO.Value;
		
    	//将工程信息绑定到列表中
    	this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String,Object>>();
    	for(String Project:ProjectList)
    	{
    		if (CurrentProjectName.equals(Project.split(",")[0]))continue;
    		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
    		String date = sDateFormat.format(new java.util.Date(Long.parseLong(Project.split(",")[1]))); 
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", false);  //可选性
        	hm.put("D2", Project.split(",")[0]);  //工程名称
        	hm.put("D3", date);    //创建时间
        	hm.put("D4", BitmapFactory.decodeResource(PubVar.m_DoEvent.m_Context.getResources(),R.drawable.v1_rightmore));    //详细
        	this.m_HeaderListViewDataItemList.add(hm);
    	}
    	hvf.BindDataToListView(this.m_HeaderListViewDataItemList,new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {

				OpenDetailProjectInfo((HashMap<String,Object>)ExtraStr);
				//调取数据预览图
				ShowDataPreview((HashMap<String,Object>)ExtraStr);
			}});
    }
    
    /**
     * 打开详细的工程信息窗口
     * @param prjInfo
     */
    private void OpenDetailProjectInfo(HashMap<String,Object> prjInfo)
    {
		//打开工程详细信息窗体
		HashMap<String,String> prjDetailInfo = new HashMap<String,String>();
		prjDetailInfo.put("Name",prjInfo.get("D2").toString());
		prjDetailInfo.put("CreateTime",prjInfo.get("D3").toString());
		v1_project_open po = new v1_project_open();
		po.SetProject(prjDetailInfo);
		po.SetCallback(pCallback);
		po.ShowDialog();
    }
    
    /**
     * 打开工程数据预览图
     * @param prjInfo
     */
    private void ShowDataPreview(final HashMap<String,Object> prjInfo)
    {
    	//隐藏工具按钮
    	Button ibOpenProject = (Button)_Dialog.findViewById(R.id.bt_openproject);
    	ibOpenProject.setVisibility(View.VISIBLE);
    	ibOpenProject.setText(Tools.ToLocale(ibOpenProject.getText()+""));
    	
    	Button ibProjectDetail = (Button)_Dialog.findViewById(R.id.bt_projectinfo);
    	ibProjectDetail.setVisibility(View.VISIBLE);
    	ibProjectDetail.setText(Tools.ToLocale(ibProjectDetail.getText()+""));
    	
    	Button ibFullScreen = (Button)_Dialog.findViewById(R.id.bt_fullscreen);
    	ibFullScreen.setVisibility(View.INVISIBLE);
    	ibFullScreen.setText(Tools.ToLocale(ibFullScreen.getText()+""));
    	
    	//数据预览标题 
    	((TextView)_Dialog.findViewById(R.id.tvLocaleText2)).setText("【"+prjInfo.get("D2")+"】"+Tools.ToLocale("数据预览图"));
    	
    	//加载数据预览图
    	ImageView iv = (ImageView)_Dialog.findViewById(R.id.iv_datapreview);iv.setEnabled(false);
    	iv.setImageBitmap(null);
    	final String DataPreviewImageName = PubVar.m_SysAbsolutePath+"/Data/"+prjInfo.get("D2")+"/DataPreview.jpg";
    	if (Tools.ExistFile(DataPreviewImageName))
    	{
	    	iv.setImageBitmap(BitmapFactory.decodeFile(DataPreviewImageName));iv.setEnabled(true);
	    	ibFullScreen.setVisibility(View.VISIBLE);
    	}
    	
    	//点击预览图，显示大图
    	iv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				v1_project_select_datapreview psd = new v1_project_select_datapreview();
				psd.SetDataPreviewInfo(prjInfo, DataPreviewImageName);
				psd.SetCallback(pCallback);
				psd.ShowDialog();
			}});
    	
    	//显示大图
    	ibFullScreen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				v1_project_select_datapreview psd = new v1_project_select_datapreview();
				psd.SetDataPreviewInfo(prjInfo, DataPreviewImageName);
				psd.SetCallback(pCallback);
				psd.ShowDialog();
			}});
    	
    	//工程详细信息
    	ibProjectDetail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				OpenDetailProjectInfo(prjInfo);
			}});
    	//打开工程
    	ibOpenProject.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				pCallback.OnClick("打开工程",prjInfo.get("D2"));
			}});    	
    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadProjectListInfo();
				
				//在此加入上次打开的工程提醒功能，仅限于软件启动时提示
				String beforePrjTag = "Tag_BeforeOpenProject";
				HashValueObject HO = PubVar.m_HashMap.GetValueObject(beforePrjTag);
				if (HO==null)
				{
					HO = PubVar.m_HashMap.GetValueObject(beforePrjTag,true);
					
					//读取上次打开的工程信息
					final HashMap<String,String> beforeOpenPrjInfo = PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().GetUserPara(beforePrjTag);
					if (beforeOpenPrjInfo!=null)
					{
						//检查上次打开的工程现在是否有效，可能存在已经删除的情况
						boolean PrjValid = false;
						for(HashMap<String,Object> ho:m_HeaderListViewDataItemList)
						{
							if (ho.get("D2").equals(beforeOpenPrjInfo.get("F2")))PrjValid=true;
						}
						if(PrjValid)
						{
							//提示是否在打开
							String Message = Tools.ToLocale("是否打开上次工程")+"？\r\n\r\n"+Tools.ToLocale("工程名称")+"：%1$s\r\n"+Tools.ToLocale("上次时间")+"：%2$s";
							Message = String.format(Message, beforeOpenPrjInfo.get("F2"),beforeOpenPrjInfo.get("F3"));
							Tools.ShowYesNoMessage(_Dialog.getContext(),Message , new ICallback(){
								@Override
								public void OnClick(String Str, Object ExtraStr) 
								{
									Tools.OpenDialog(Tools.ToLocale("正在打开工程")+"...", new ICallback(){
										@Override
										public void OnClick(String Str,Object ExtraStr) {
											OpenProject(beforeOpenPrjInfo.get("F2"));
											_Dialog.dismiss();
										}});
								}});
						}
					}
				}
				if (m_HeaderListViewDataItemList.size()==0 && !PubVar.m_DoEvent.m_ProjectDB.AlwaysOpenProject())
				{
					Tools.ShowYesNoMessage(_Dialog.getContext(),Tools.ToLocale("当前没有创建任何工程，是否需要创建工程")+"？",new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) 
						{
							if (Str.equals("YES"))pCallback.OnClick("新建工程", "");
						}});
				}
			}}
    	
    	
    	);
    	_Dialog.show();
    }
}
