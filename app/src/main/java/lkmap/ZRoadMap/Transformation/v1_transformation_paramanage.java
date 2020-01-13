package lkmap.ZRoadMap.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Coordinate;
import lkmap.CoordinateSystem.CoorParamTools;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Project_New_CheckPoint_Adpter;

public class v1_transformation_paramanage
{
	private v1_FormTemplate _Dialog = null; 
    public v1_transformation_paramanage()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_transformation_paramanage);
    	_Dialog.SetCaption("转换参数管理");
    	_Dialog.ReSetSize(0.5f, 0.96f);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+" ,确定", pCallback);

    	_Dialog.findViewById(R.id.bt_add).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("增加参数", "");}});
    	
       	_Dialog.findViewById(R.id.bt_edit).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("编辑参数", "");}});
       	
       	_Dialog.findViewById(R.id.bt_delete).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("删除参数", "");}});
       	
       	_Dialog.findViewById(R.id.bt_cal).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("计算参数", "");}});
       	
    	((ListView)_Dialog.findViewById(R.id.listView1)).setOnItemClickListener(new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{
			ListView lv = (ListView)arg0;
			v1_Project_New_CheckPoint_Adpter hva = (v1_Project_New_CheckPoint_Adpter)lv.getAdapter();
			hva.SetSelectItemIndex(arg2);
			hva.notifyDataSetInvalidated();//提醒数据已经变动
			m_SelectItem = (HashMap<String,Object>)hva.getItem(arg2);
			_Dialog.findViewById(R.id.bt_edit).setEnabled(true);
			_Dialog.findViewById(R.id.bt_delete).setEnabled(true);
			_Dialog.GetButton("1").setEnabled(true);
		}});
    	
    	//绑定参数类型列表
    	String[] paramType = new String[]{"三参数","七参数","四参数"};
    	v1_DataBind.SetBindListSpinner(_Dialog, "选择参数类型", Tools.StrArrayToList(paramType), R.id.spType, new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				RefreshParamList();
			}});
    }
    
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("确定"))
			{
				if (m_SelectItem!=null && m_Callback!=null) 
				{
					m_Callback.OnClick("参数", m_SelectItem);
				}
				_Dialog.dismiss();
			}
			
	    	if (Str.equals("增加参数"))
	    	{
	    		v1_transformation_paramanage_add tpa = new v1_transformation_paramanage_add();
	    		String ParamType = Tools.GetSpinnerValueOnID(_Dialog, R.id.spType);
	    		tpa.SetCallParamType(ParamType);
	    		tpa.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						RefreshParamList();
					}});
	    		tpa.ShowDialog();
	    	}

	    	if (Str.equals("编辑参数"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
		    		v1_transformation_paramanage_add tpa = new v1_transformation_paramanage_add();
		    		String ParamType = Tools.GetSpinnerValueOnID(_Dialog, R.id.spType);
		    		tpa.SetEditParam(m_SelectItem);
		    		tpa.SetCallParamType(ParamType);
		    		tpa.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							RefreshParamList();
						}});
		    		tpa.ShowDialog();
	    		}
	    	}
	    	if (Str.equals("删除参数"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
	    			String MessageStr = "是否删除以下参数？\r\n名称=【"+m_SelectItem.get("DH")+"】";
	    			Tools.ShowYesNoMessage(_Dialog.getContext(), MessageStr, new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							String ID = m_SelectItem.get("ID").toString();
							if (PubVar.m_DoEvent.m_UserConfigDB.GetTransformationParam().DeleteTransformationParam(ID))
							{
								RefreshParamList();
							} else
							{
								Tools.ShowMessageBox("删除参数失败！");
							}
							
						}});
	    		}
	    	}
	    	if (Str.equals("计算参数"))
	    	{
	    		int CalPointCount = 0;
	    		for(HashMap<String,Object> hm:m_ParamList)
	    		{
	    			if (Boolean.parseBoolean(hm.get("Select")+""))CalPointCount++;
	    		}
	    		if (CalPointCount==0 ){Tools.ShowMessageBox("参于解算控制点数量为【0】，无法计算参数！");return;}
	    		
//	    		if (m_CalParamType.equals("计算四参数"))
//	    		{
//	    			HashMap<String,Object> param = CalFourParam();
//	    			String Message = "X平移(米)："+param.get("DX")+"\nY平移(米)："+param.get("DY")+"\n旋转(秒)："+param.get("R")+"\n尺度："+param.get("K");
//	    			lkmap.Tools.Tools.ShowMessageBox("四参数计算完成，参数如下：\n"+Message);
//	    		}
	    	}
		}};
		

	//打开工程后的回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	//参数类型，三参数，四参数，七参数
	public void SetCallParamType(String calParamType)
	{
    	String[] paramType = new String[]{calParamType};
    	v1_DataBind.SetBindListSpinner(_Dialog, "选择参数类型", Tools.StrArrayToList(paramType), R.id.spType, new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				RefreshParamList();
			}});
    	_Dialog.findViewById(R.id.spType).setEnabled(false);
	}
	
	//当前选中的参数项
	private HashMap<String,Object> m_SelectItem = null;
	
	//参数列表
	List<HashMap<String,Object>> m_ParamList = new ArrayList<HashMap<String,Object>>();
	
	//刷新参数列表
	private void RefreshParamList()
	{
		this.m_ParamList.clear();
		
		int LayoutViewId = -1;
		String[] keyItem = null;
		int[] ViewItem = null;
		
		//从配置库中读取参数列表
		String ParamType = Tools.GetSpinnerValueOnID(_Dialog, R.id.spType);
		this.m_ParamList = PubVar.m_DoEvent.m_UserConfigDB.GetTransformationParam().GetTransformationParamList(ParamType);
		
		for(HashMap<String,Object> hm:this.m_ParamList)
		{
			if (ParamType.equals("三参数"))
			{
				hm.put("T1", "X平移(米)："+hm.get("P1").toString());
				hm.put("T2", "Y平移(米)："+hm.get("P2").toString());
				hm.put("T3", "Z平移(米)："+hm.get("P3").toString());
				LayoutViewId = R.layout.v1_bk_transformation_paramanage_threepara_list;
				keyItem = new String[] {"DH", "T1","T2","T3"};
				ViewItem = new int[] {R.id.tvDH, R.id.tvparam1, R.id.tvparam2,R.id.tvparam3};
			}
			if (ParamType.equals("四参数"))
			{
				hm.put("T1", "X平移(米)："+hm.get("P1").toString());
				hm.put("T2", "Y平移(米)："+hm.get("P2").toString());
				hm.put("T3", "旋转(秒)："+hm.get("P3").toString());
				hm.put("T4", "尺度："+hm.get("P4").toString());
				LayoutViewId = R.layout.v1_bk_transformation_paramanage_fourpara_list;
				keyItem = new String[] {"DH", "T1","T2","T3","T4"};
				ViewItem = new int[] {R.id.tvDH, R.id.tvparam1, R.id.tvparam2,R.id.tvparam3,R.id.tvparam4};
			}
			if (ParamType.equals("七参数"))
			{
				hm.put("T1", "X平移(米)："+hm.get("P1").toString());
				hm.put("T2", "Y平移(米)："+hm.get("P2").toString());
				hm.put("T3", "Z平移(米)："+hm.get("P3").toString());
				hm.put("T4", "X旋转(秒)："+hm.get("P4").toString());
				hm.put("T5", "Y旋转(秒)："+hm.get("P5").toString());
				hm.put("T6", "Z旋转(秒)："+hm.get("P6").toString());				
				hm.put("T7", "尺度(ppm)："+hm.get("P7").toString());
				LayoutViewId = R.layout.v1_bk_transformation_paramanage_sevenpara_list;
				keyItem = new String[] {"DH", "T1","T2","T3","T4","T5","T6","T7"};
				ViewItem = new int[] {R.id.tvDH, R.id.tvparam1, R.id.tvparam2,R.id.tvparam3,R.id.tvparam4,R.id.tvparam5,R.id.tvparam6,R.id.tvparam7};
			}
		}
		
		//刷新列表
        v1_Project_New_CheckPoint_Adpter adapter = new v1_Project_New_CheckPoint_Adpter(_Dialog.getContext(),this.m_ParamList, LayoutViewId, keyItem, ViewItem);  
		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
		
		_Dialog.findViewById(R.id.bt_edit).setEnabled(false);
		_Dialog.findViewById(R.id.bt_delete).setEnabled(false);
		_Dialog.GetButton("1").setEnabled(false);
	}

    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				RefreshParamList();
				}}
    	);
    	_Dialog.show();
    }
}
