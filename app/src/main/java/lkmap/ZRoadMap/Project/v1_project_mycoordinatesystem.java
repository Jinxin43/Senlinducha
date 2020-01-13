package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_mycoordinatesystem
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_mycoordinatesystem()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_mycoordinatesystem);
    	_Dialog.ReSetSize(1f, 0.96f);
    	
    	_Dialog.SetCaption(Tools.ToLocale("我的坐标系"));
    	_Dialog.findViewById(R.id.btsave).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {pCallback.OnClick("保存", "");}});
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_layer_field_delete+","+Tools.ToLocale("删除")+"  ,删除", pCallback);
    	
    	//多语言转换
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText2));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText3));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText4));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText5));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText6));
    }
    
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("删除"))
	    	{
	    		final List<String> idList = new ArrayList<String>();
	    		List<String> nameList = new ArrayList<String>();
	    		for(HashMap<String,Object> hm:m_MyParamList)
	    		{
	    			if (Boolean.parseBoolean(hm.get("D1")+""))
    				{
    					idList.add(hm.get("ID")+"");
    					nameList.add("名称："+hm.get("D2")+"");
    				}
	    		}
	    		
	    		Tools.ShowYesNoMessage(_Dialog.getContext(), "是否删除以下我的坐标系？\n\n"+Tools.JoinT("\n", nameList), new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
			    		if (!PubVar.m_DoEvent.m_UserConfigDB.GetMyCoodinateSystem().DeleteMyCoordinateSystem(idList))
			    		{
			    			Tools.ShowMessageBox("删除我的坐标系失败！");
			    		} else LoadMyCoordinateSystemInfo();
					}});

	    	}
	    	if (Str.equals("保存"))
	    	{
	    		v1_project_mycoordinatesystem_new pmn = new v1_project_mycoordinatesystem_new();
	    		pmn.SetNewCoorSystemPara(m_NewCoorSystemPara);
	    		pmn.SetCallback(new ICallback(){

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (Str.equals("新建我的坐标系"))
						{
							//重新加载
							LoadMyCoordinateSystemInfo(); 
						}
						
					}});
	    		pmn.ShowDialog();
	    	}
		}};
		
    //回调
    private ICallback _selectCallback = null;
    public void SetCallback(ICallback cb){this._selectCallback = cb;}
		
    //设置新坐标系的参数
	private HashMap<String,String> m_NewCoorSystemPara = null;
	public void SetNewCoorSystemPara(HashMap<String,String> _NewCoorSystemPara)
	{
		this.m_NewCoorSystemPara = _NewCoorSystemPara;
		//FillPromptInfo(_Dialog,this.m_NewCoorSystemPara);
	}

	
	private List<HashMap<String,Object>> m_MyParamList = new ArrayList<HashMap<String,Object>>();
    /**
     * 加载我的坐标系列表信息
     */
    private void LoadMyCoordinateSystemInfo()
    {
    	//绑定我的坐标系列表
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "我的坐标系",new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("列表选项"))
				{
					final HashMap<String,String> selCoorPara = (HashMap<String,String>)ExtraStr;
					v1_project_mycoordinatesystem_select pms = new v1_project_mycoordinatesystem_select();
					pms.SetNewCoorSystemPara(selCoorPara);
					pms.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (Str.equals("选择我的坐标系"))
							{
								Tools.ShowYesNoMessage(_Dialog.getContext(), "是否覆盖工程当前的坐标系？", new ICallback(){
									@Override
									public void OnClick(String Str,Object ExtraStr) {
										if (Str.equals("YES"))
										{
											if (_selectCallback!=null)_selectCallback.OnClick("设置坐标系",selCoorPara);
											_Dialog.dismiss();
										}
									}});
							}
						}});
					pms.ShowDialog();
				}
			}});
    	
    	//读取已经创建哪些我的坐标系
    	this.m_MyParamList = PubVar.m_DoEvent.m_UserConfigDB.GetMyCoodinateSystem().GetMyCoordinateSystemList();
    	hvf.BindDataToListView(this.m_MyParamList);
    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadMyCoordinateSystemInfo();}}
    	);
    	_Dialog.show();
    }
    
    /**
     * 填充坐标信息提示文本，用于我的坐标系new,select等
     * @param _Dialog
     * @param MyCoorPara
     */
    public static void FillPromptInfo(Dialog _Dialog,HashMap<String,String> MyCoorPara)
    {
		if (MyCoorPara.containsKey("CoorSystem"))
		{
			_Dialog.findViewById(R.id.ll_coorsystem).setVisibility(View.VISIBLE);
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_coorsystem, MyCoorPara.get("CoorSystem"));
			if (MyCoorPara.get("CoorSystem").equals("WGS-84坐标"))
			{
				MyCoorPara.put("CenterJX", "");MyCoorPara.put("TransMethod","无");
				return;
			}
		}

		_Dialog.findViewById(R.id.ll_centerjx).setVisibility(View.VISIBLE);
		Tools.SetTextViewValueOnID(_Dialog, R.id.tv_centerjx, MyCoorPara.get("CenterJX"));

		_Dialog.findViewById(R.id.ll_transmethod).setVisibility(View.VISIBLE);
		Tools.SetTextViewValueOnID(_Dialog, R.id.tv_transmethod, MyCoorPara.get("TransMethod"));

		_Dialog.findViewById(R.id.ll_pmtransmethod).setVisibility(View.VISIBLE);
		Tools.SetTextViewValueOnID(_Dialog, R.id.tv_pmtransmethod, MyCoorPara.get("PMTransMethod"));
		
		String TransMethod = MyCoorPara.get("TransMethod");
		if (TransMethod.contains("三参"))
		{
			MyCoorPara.put("TransParam", "X平移(米)="+MyCoorPara.get("P31")+"\n"+
										"Y平移(米)="+MyCoorPara.get("P32")+"\n"+
										"Z平移(米)="+MyCoorPara.get("P33")+"\n"+
										"长轴差="+MyCoorPara.get("P34")+"\n"+
										"扁率差="+MyCoorPara.get("P35"));
		}
		if (TransMethod.contains("七参"))
		{
			MyCoorPara.put("TransParam", "X平移(米)="+MyCoorPara.get("P71")+"\n"+
										"Y平移(米)="+MyCoorPara.get("P72")+"\n"+
										"Z平移(米)="+MyCoorPara.get("P73")+"\n"+
										"X旋转(秒)="+MyCoorPara.get("P74")+"\n"+
										"Y旋转(秒)="+MyCoorPara.get("P75")+"\n"+
										"Z旋转(秒)="+MyCoorPara.get("P76")+"\n"+
										"尺度因子(ppm)="+MyCoorPara.get("P77"));
		}
		
		String PMTransMethod = MyCoorPara.get("PMTransMethod");
		if (PMTransMethod.contains("四参"))
		{
			MyCoorPara.put("PMTransParam", "X平移(米)="+MyCoorPara.get("P41")+"\n"+
										"Y平移(米)="+MyCoorPara.get("P42")+"\n"+
										"旋转角度(秒)="+MyCoorPara.get("P43")+"\n"+
										"尺度因子="+MyCoorPara.get("P44"));
		}

			
		if (MyCoorPara.containsKey("TransParam"))
		{
			_Dialog.findViewById(R.id.ll_transparam).setVisibility(View.VISIBLE);
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_transparam, MyCoorPara.get("TransParam"));
		}
		if (MyCoorPara.containsKey("PMTransParam"))
		{
			_Dialog.findViewById(R.id.ll_pmtransparam).setVisibility(View.VISIBLE);
			Tools.SetTextViewValueOnID(_Dialog, R.id.tv_pmtransparam, MyCoorPara.get("PMTransParam"));
		}
    }
}
