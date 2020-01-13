package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Symbol.PointSymbol;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_layer_render_symbolexplorer_pointsymbol_custom
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer_render_symbolexplorer_pointsymbol_custom()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_render_symbolexplorer_pointsymbol_custom);
    	_Dialog.ReSetSize(1f, 0.96f);
    	_Dialog.SetCaption(Tools.ToLocale("点符号"));

    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	
    	this.SetButtonEnable(false);
    }
    
    /**
     * 设置按钮的可用性
     * @param enable
     */
    private void SetButtonEnable(boolean enabled)
    {
    	_Dialog.GetButton("1").setEnabled(enabled);
    }
    
    //上部按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (m_Callback!=null)
    			{
		    		if (m_SelectItem!=null)
		    		{
		    			HashMap<String,Object> OBJ = (HashMap<String,Object>)m_SelectItem;
		    			v1_SymbolObject SO = (v1_SymbolObject)OBJ.get("SymbolObject");
		    			PointSymbol PS = new PointSymbol();
		    			PS.CreateByBase64(SO.SymbolBase64Str);SO.SymbolFigure= PS.ToFigureBitmap(64,48);
		    			m_Callback.OnClick("符号库", SO);
		    		}
    			}
	    		_Dialog.dismiss();
	    	}

	    	//在符号列表中选中一项后回调
	    	if (Str.equals("列表选项"))
	    	{
	    		SetButtonEnable(true);
	    		m_SelectItem = ExtraStr;
	    		pCallback.OnClick("确定", ExtraStr);
	    	}
			
		}};
		
	
	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		
	//符号列表
	private List<v1_SymbolObject> m_SymbolObjectList = new ArrayList<v1_SymbolObject>();
	public void SetSymbolObjectList(List<v1_SymbolObject> SOList){this.m_SymbolObjectList = SOList;}
		
	
	//符号列表绑定的数据项
	private List<HashMap<String,Object>> m_HeaderListViewDataItemList = null;
	//选中后的符号
	private Object m_SelectItem = null;
    /**
     * 加载符号库信息
     */
    private void LoadSymbolLibInfo()
    {
    	//绑定符号列表
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.symbol_list), "简单点符号列表",pCallback);
    	
    	//读取指定符号库中符号列表
    	this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String,Object>>();
    	
    	for(v1_SymbolObject SO:this.m_SymbolObjectList)
    	{
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", SO.SymbolFigure);  		//符号
        	hm.put("SymbolObject", SO);				//整个符号SymbolObject
        	this.m_HeaderListViewDataItemList.add(hm);
    	}
    	hvf.BindDataToListView(this.m_HeaderListViewDataItemList,pCallback);
    	
    	//不可用图层按钮，如编辑之类的
    	this.SetButtonEnable(false);
    	this.m_SelectItem = null;

    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadSymbolLibInfo();}});
    	_Dialog.show();
    }
    

}
