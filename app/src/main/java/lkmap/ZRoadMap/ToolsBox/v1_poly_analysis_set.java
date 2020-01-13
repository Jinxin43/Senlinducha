package lkmap.ZRoadMap.ToolsBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_MapFileAdapter;

public class v1_poly_analysis_set
{
	private v1_FormTemplate _Dialog = null; 

    public v1_poly_analysis_set()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_poly_analysis_set);
    	_Dialog.ReSetSize(0.7f,0.76f);
    	
    	//设置标题
    	_Dialog.SetCaption(Tools.ToLocale("分析设置"));
    	
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_data_filter+",增加  ,增加", pCallback);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+" ,确定", pCallback);
    	
    	CheckBox cb = (CheckBox)_Dialog.findViewById(R.id.cbselectall);
    	cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				for(HashMap<String,Object> hm:m_PolyAnalysisOption)hm.put("Select",arg1);
		    	((v1_Project_Layer_BKMap_MapFileAdapter)(((ListView)_Dialog.findViewById(R.id.listView1))).getAdapter()).notifyDataSetChanged();
			}});
    }
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (SaveOption())
	    		{
	    			_Dialog.dismiss();
			    	if (m_Callback!=null)m_Callback.OnClick("设置", "");
	    		}
	    	}
	    	
	    	if (Str.equals("增加"))
	    	{
	    		v1_poly_analysis_set_addfilter pasa = new v1_poly_analysis_set_addfilter();
	    		pasa.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						//处理重复LayerId现象
						HashMap<String,Object> newOpt = (HashMap<String,Object>)ExtraStr;
						for(HashMap<String,Object> hm:m_PolyAnalysisOption)
						{
							if (hm.get("LayerId").equals(newOpt.get("LayerId"))){m_PolyAnalysisOption.remove(hm);break;}
						}
						m_PolyAnalysisOption.add((HashMap<String,Object>)ExtraStr);
						((v1_Project_Layer_BKMap_MapFileAdapter)(((ListView)_Dialog.findViewById(R.id.listView1))).getAdapter()).notifyDataSetChanged();
					}});
	    		pasa.ShowDialog();
	    	}
	    	
	    	if(Str.equals("ButtonClick"))  //删除条件
	    	{
	    		final HashMap<String,Object> hm = (HashMap<String,Object>)ExtraStr;
	    		Tools.ShowYesNoMessage(_Dialog.getContext(),"是否删除以下选项？\n图层名称："+hm.get("LayerName")+"\n统计字段："+hm.get("FieldCaptionListStr"),new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_PolyAnalysisOption.remove(hm);
						((v1_Project_Layer_BKMap_MapFileAdapter)(((ListView)_Dialog.findViewById(R.id.listView1))).getAdapter()).notifyDataSetChanged();
					}});
	    	}
		}};
		
	//回调
	private ICallback m_Callback = null;
	
	/**
	 * 回调
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		
	/**
	 * 保存配置选项
	 * @return
	 */
	private boolean SaveOption()
	{
		//判断是否有勾引项
		boolean HaveItem = false;
    	for(HashMap<String,Object> optItem:m_PolyAnalysisOption)
    	{
    		if (Boolean.parseBoolean(optItem.get("Select")+""))HaveItem = true;
    	}
    	if (HaveItem)
    	{
    		return this.m_PAO.SavePolyAnalysisOption(this.m_PolyAnalysisOption);
    	}
    	else
    	{
    		Tools.ShowMessageBox("请勾选统计选项！");
    		return false;
    	}
	}
	
	private v1_UserConfigDB_PolyAnalysisOption m_PAO = new v1_UserConfigDB_PolyAnalysisOption();
		
    /**
     * 加载面分析缺省设置
     */
	private void LoadDefaultSet()
	{
		this.m_PolyAnalysisOption = this.m_PAO.GetPolyAnalysisOption();
		this.RefreshDataToListView();
	}
	
	//选项列表，格式详见v1_UserConfigDB_PolyAnalysisOption
	private List<HashMap<String,Object>> m_PolyAnalysisOption = new ArrayList<HashMap<String,Object>>();
	

	/**
	 * 刷新列表显示
	 */
    private void RefreshDataToListView()
    {
    	//整理选项字符串
    	for(HashMap<String,Object> optItem:m_PolyAnalysisOption)
    	{
    		optItem.put("Select", true);
    		List<String> FieldCaptionList = (List<String>)optItem.get("FieldCaptionList");
    		optItem.put("FieldCaptionListStr", Tools.JoinT(",", FieldCaptionList));
    	}
		//刷新列表
    	v1_Project_Layer_BKMap_MapFileAdapter adapter = new v1_Project_Layer_BKMap_MapFileAdapter(_Dialog.getContext(),this.m_PolyAnalysisOption, 
											       R.layout.v1_bk_poly_analysis_set, 
											       new String[] {"Select", "LayerName", "FieldCaptionListStr"}, 
											       new int[] {R.id.cbselect, R.id.tvlayername, R.id.tvfield,R.id.btdelete});  
		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
		adapter.SetCallback(pCallback);
		adapter.notifyDataSetChanged();
    }
    

    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadDefaultSet();}}
    	);
    	_Dialog.show();
    }

}
