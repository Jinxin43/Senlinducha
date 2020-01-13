package lkmap.ZRoadMap.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.UserConfigDB;
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
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Coordinate;
import lkmap.CoordinateSystem.CoorParamTools;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Project_New_CheckPoint_Adpter;

public class v1_transformation_calparam
{
	private v1_FormTemplate _Dialog = null; 
    public v1_transformation_calparam()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_transformation_calparam);
    	_Dialog.SetCaption("参数计算器");
    	_Dialog.ReSetSize(0.8f, 0.76f);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+" ,确定", pCallback);

    	_Dialog.findViewById(R.id.bt_add).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("增加控制点", "");}});
    	
       	_Dialog.findViewById(R.id.bt_edit).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("编辑控制点", "");}});
       	
       	_Dialog.findViewById(R.id.bt_delete).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("删除控制点", "");}});
       	
       	_Dialog.findViewById(R.id.bt_cal).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("计算控制点", "");}});
       	
       	_Dialog.findViewById(R.id.bt_save).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("保存参数", "");}});

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
		}});
    	
    	
    	UserConfigDB db = new UserConfigDB();
    	UpdateCheckPoint(db.getTransformPoint());
    }
    
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("确定"))
			{
	    		if (m_CalParamType.equals("计算四参数"))
	    		{
	    			if (!CheckControlPointNumber(true))return;
	    			final HashMap<String,Object> param = CalFourParam();
	    			if (param==null){Tools.ShowMessageBox("无法计算参数！");return;}
	    			String Message = GetResultMessage();
	    			lkmap.Tools.Tools.ShowYesNoMessage(_Dialog.getContext(), "四参数计算完成，是否使用该参数？\n\n"+Message,new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (m_Callback!=null)m_Callback.OnClick("四参数", param);
							_Dialog.dismiss();
						}});
	    		}
			}
			
	    	if (Str.equals("增加控制点"))
	    	{
	    		v1_transformation_addmatchpoint pnca = new v1_transformation_addmatchpoint();
	    		pnca.SetMatchPointType(m_CalParamType);
	    		pnca.SetUseGPS(m_UseGPS);
	    		pnca.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						UpdateCheckPoint((HashMap<String,Object>)ExtraStr);
					}});
	    		pnca.ShowDialog();
	    	}

	    	if (Str.equals("编辑控制点"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
		    		v1_transformation_addmatchpoint pnca = new v1_transformation_addmatchpoint();
		    		pnca.SetMatchPointType(m_CalParamType);
		    		pnca.SetEditCheckPoint(m_SelectItem);
		    		pnca.SetUseGPS(m_UseGPS);
		    		pnca.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							UpdateCheckPoint((HashMap<String,Object>)ExtraStr);
						}});
		    		pnca.ShowDialog();
	    		}
	    	}
	    	if (Str.equals("删除控制点"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
	    			String MessageStr = "是否删除以下点对？\r\n点号=【"+m_SelectItem.get("DH")+"】";
	    			Tools.ShowYesNoMessage(_Dialog.getContext(), MessageStr, new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							m_CheckPointList.remove(m_SelectItem);
							RefreshCheckPointList();
						}});
	    		}
	    	}
	    	if (Str.equals("计算控制点"))
	    	{
	    		if (!CheckControlPointNumber(true))return;
	    		if (m_CalParamType.equals("计算四参数"))
	    		{
	    			String Message = GetResultMessage();
	    			lkmap.Tools.Tools.ShowMessageBox("四参数计算完成，参数如下：\n"+Message);
	    		}
	    	}
	    	
	    	if (Str.equals("保存参数"))
	    	{
	    		if (m_CalParamType.equals("计算四参数"))
	    		{
	    			if (!CheckControlPointNumber(true))return;
	    			final HashMap<String,Object> param = CalFourParam();
	    			param.put("DH", "");
	    			param.put("P1", param.get("DX"));param.put("P2", param.get("DY"));
	    			param.put("P3", param.get("R"));param.put("P4", param.get("K"));
	    			
	    			String Message = GetResultMessage();
	    			lkmap.Tools.Tools.ShowYesNoMessage(_Dialog.getContext(), "是否保存以下参数？\n\n"+Message,new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							
							v1_transformation_paramanage_add tpa = new v1_transformation_paramanage_add();
							tpa.SetCallParamType("四参数");
							tpa.SetEditParam(param);
							tpa.ShowDialog();
						}});
	    		}
	    	}
		}};
		
	//检查控制点是否符合要求
	private boolean CheckControlPointNumber(boolean showMessage)
	{
		int CalPointCount = 0;
		for(HashMap<String,Object> hm:this.m_CheckPointList)
		{
			if (Boolean.parseBoolean(hm.get("Select")+""))CalPointCount++;
		}
		if (CalPointCount==0 )
		{
			if (showMessage)Tools.ShowMessageBox("参于解算控制点数量为【0】，无法计算参数！");
			return false;
		}
		return true;
	}
		
	/**
	 * 生成结果信息
	 * @return
	 */
	private String GetResultMessage()
	{
		HashMap<String,Object> param = CalFourParam();
		String Message = "X平移(米)："+param.get("DX")+"\nY平移(米)："+param.get("DY")+"\n旋转(秒)："+param.get("R")+"\n尺度："+param.get("K");
		if (Double.parseDouble(param.get("DX").toString())==0 && 
			Double.parseDouble(param.get("DY").toString())==0 &&
			Double.parseDouble(param.get("R").toString())==0 && 
			Double.parseDouble(param.get("K").toString())==1)
		{
			Message+="\n\n注意：此参数异常，请检查！";
		}
		return Message;
	}
	
	/**
	 * 计算转换参数
	 * @return
	 */
	private HashMap<String,Object> CalFourParam()
	{
		List<Coordinate> CoorList = new ArrayList<Coordinate>();
		for(HashMap<String,Object> cPoint:m_CheckPointList)
		{
			if (!Boolean.parseBoolean(cPoint.get("Select")+""))continue;
			Coordinate Coor1 = new Coordinate(Double.parseDouble(cPoint.get("X1")+""),Double.parseDouble(cPoint.get("Y1")+""));
			Coordinate Coor2 = new Coordinate(Double.parseDouble(cPoint.get("X2")+""),Double.parseDouble(cPoint.get("Y2")+""));
			CoorList.add(Coor1);CoorList.add(Coor2);
		}
		return CoorParamTools.CalFourPara(CoorList);
	}

	//打开工程后的回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	//计算参数类型，计算三参数，计算四参数，计算七参数
	private String m_CalParamType = "";
	public void SetCallParamType(String calParamType)
	{
		this.m_CalParamType = calParamType;
	}
	
	//是否可以使用GPS获取点位
	private boolean m_UseGPS = true;
	public void SetUseGPS(boolean useGPS)
	{
		this.m_UseGPS = useGPS;
	}
	
	//前选中的控制点
	private HashMap<String,Object> m_SelectItem = null;
	
	//校准点列表
	List<HashMap<String,Object>> m_CheckPointList = new ArrayList<HashMap<String,Object>>();
	
	//更新校准点信息，如果checkPointInfo包含DH关键 字，表示为更新，如果不包含表示新增
	private void UpdateCheckPoint(HashMap<String,Object> checkPointInfo)
	{
		if (!checkPointInfo.containsKey("DH"))
		{
			checkPointInfo.put("Select",true);
			this.m_CheckPointList.add(checkPointInfo);
		} 

		this.RefreshCheckPointList();
	}
	
	//刷新校准点列表
	private void RefreshCheckPointList()
	{
//		this.m_CheckPointList.clear();
//		
//		HashMap<String,Object> h1 = new HashMap<String,Object>();
//		h1.put("X1", 3342166.84023232323);h1.put("Y1", 441596.337);
//		h1.put("X2", 3342120.512);h1.put("Y2", 441553.409);h1.put("Select", true);
//
//		HashMap<String,Object> h2 = new HashMap<String,Object>();
//		h2.put("X1", 3316936.647);h2.put("Y1", 424906.947);
//		h2.put("X2", 3316889.969);h2.put("Y2", 424864.015);h2.put("Select", true);
//		
//		HashMap<String,Object> h3 = new HashMap<String,Object>();
//		h3.put("X1", 3338625.302);h3.put("Y1", 415534.526);
//		h3.put("X2", 3338578.777);h3.put("Y2", 415491.366);
//		h3.put("Select", true);
//		
//		HashMap<String,Object> h4 = new HashMap<String,Object>();
//		h4.put("X1", 3356163.501);h4.put("Y1", 392948.358);
//		h4.put("X2", 3356117.009);h4.put("Y2", 392904.880);h4.put("Select", true);
//		
//		HashMap<String,Object> h5 = new HashMap<String,Object>();
		
//		h5.put("X1", 3326868.132);h5.put("Y1", 396444.949);
//		h5.put("X2", 3326821.375);h5.put("Y2", 396401.680);h5.put("Select", true);
//		
//		this.m_CheckPointList.add(h1);this.m_CheckPointList.add(h2);
//		this.m_CheckPointList.add(h3);this.m_CheckPointList.add(h4);
//		this.m_CheckPointList.add(h5);
		
		//更新点号
		for(int i=0;i<this.m_CheckPointList.size();i++)
		{
			this.m_CheckPointList.get(i).put("DH", i+1);
		}
		//刷新列表
        v1_Project_New_CheckPoint_Adpter adapter = new v1_Project_New_CheckPoint_Adpter(_Dialog.getContext(),m_CheckPointList, 
											       R.layout.v1_bk_transformation_calparam_list, new String[] {"DH", "X1",  "Y1","X2","Y2","Select"}, 
											       new int[] {R.id.tvDH, R.id.tvSX1, R.id.tvSY1,R.id.tvRX1, R.id.tvRY1,R.id.cbSelect});  
		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
		
		_Dialog.findViewById(R.id.bt_edit).setEnabled(false);
		_Dialog.findViewById(R.id.bt_delete).setEnabled(false);
	}

    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
		    	if (m_CalParamType.equals("计算四参数"))_Dialog.SetCaption("四参数计算器");
				RefreshCheckPointList();
				}}
    	);
    	_Dialog.show();
    }
}
