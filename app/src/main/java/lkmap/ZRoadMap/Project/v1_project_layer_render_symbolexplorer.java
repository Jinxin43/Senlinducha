package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_layer_render_symbolexplorer
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer_render_symbolexplorer()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_render_symbolexplorer);
    	_Dialog.ReSetSize(0.8f, 0.90f);
    	_Dialog.SetCaption(Tools.ToLocale("符号库"));

    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	_Dialog.SetButtonInfo("3,"+R.drawable.v1_layer_field_delete+","+Tools.ToLocale("删除 ")+" ,删除", pCallback);
    	_Dialog.findViewById(R.id.btEdit).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				pCallback.OnClick("编辑", null);
			}});
    	//this.SetButtonEnable(false);
    	
    	//多语言支持 
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText));
    }
    
    /**
     * 设置按钮的可用性
     * @param enable
     */
    private void SetButtonEnable(boolean enabled)
    {
    	_Dialog.GetButton("1").setEnabled(enabled);
    	_Dialog.findViewById(R.id.btEdit).setEnabled(enabled);
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
		    		if (m_SelectSymbolObject!=null)
		    		{
		    			m_Callback.OnClick("符号库", m_SelectSymbolObject);
		    		}
    			}
	    		_Dialog.dismiss();
	    	}
	    	if (Str.equals("编辑"))
	    	{
	    		if (m_SelectSymbolObject==null) return;
	    		if (m_GeoLayerType==lkGeoLayerType.enPoint)
	    		{
	    			v1_project_layer_render_symbolexplorer_pointsymbol SP = new v1_project_layer_render_symbolexplorer_pointsymbol();
	    			SP.SetSymbol(m_SelectSymbolObject.SymbolBase64Str);
	    			SP.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							m_SelectSymbolObject = (v1_SymbolObject)ExtraStr;
							CreatePreview();
						}});
	    			SP.ShowDialog();
	    		}
	    		if (m_GeoLayerType==lkGeoLayerType.enPolyline)
	    		{
	    			v1_project_layer_render_symbolexplorer_linesymbol SP = new v1_project_layer_render_symbolexplorer_linesymbol();
	    			SP.SetSymbol(m_SelectSymbolObject.SymbolBase64Str);
	    			SP.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							m_SelectSymbolObject = (v1_SymbolObject)ExtraStr;
							CreatePreview();
						}});	    			
	    			SP.ShowDialog();
	    		}
	    		if (m_GeoLayerType==lkGeoLayerType.enPolygon)
	    		{
	    			v1_project_layer_render_symbolexplorer_polysymbol SP = new v1_project_layer_render_symbolexplorer_polysymbol();
	    			SP.SetSymbol(m_SelectSymbolObject.SymbolBase64Str);
	    			SP.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							m_SelectSymbolObject = (v1_SymbolObject)ExtraStr;
							CreatePreview();
						}});	    			
	    			SP.ShowDialog();
	    		}
	    	}
	    	
	    	if (Str.equals("删除"))
	    	{
	    		
	    	}
	    	
	    	//在符号列表中选中一项后回调
	    	if (Str.equals("列表选项"))
	    	{
	    		SetButtonEnable(true);
	    		m_SelectSymbolObject = (v1_SymbolObject)((HashMap<String,Object>)ExtraStr).get("SymbolObject");
	    		CreatePreview();
	    	}
			
	    	//图层符号选择后的回调，也就是打开符号选取对话框
	    	if (Str.equals("ImageSpinnerCallback"))
	    	{
	    		
	    	}
		}};
		
	
	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		
	//图层类型
	private lkGeoLayerType m_GeoLayerType = lkGeoLayerType.enUnknow;
	public void SetGeoLayerType(lkGeoLayerType geoLayerType){this.m_GeoLayerType = geoLayerType;}
		
	public void SetDefaultSymbolObject(v1_SymbolObject so)
	{
		this.m_SelectSymbolObject = so;
	}
	
	//符号列表绑定的数据项
	private List<HashMap<String,Object>> m_HeaderListViewDataItemList = null;
	
	//选中后的符号
	private v1_SymbolObject m_SelectSymbolObject = null;
    /**
     * 加载符号库信息
     */
    private void LoadSymbolLibInfo()
    {
    	if (this.m_GeoLayerType==lkGeoLayerType.enUnknow) return;
    	
    	//绑定符号列表
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.symbol_list), "符号列表",pCallback);
    	
    	//读取指定符号库中符号列表
    	this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String,Object>>();
    	List<v1_SymbolObject> symbolList = PubVar.m_DoEvent.m_ConfigDB.GetSymbolExplorer().GetSymbolObjectList(new String[]{}, this.m_GeoLayerType);
    	
    	for(v1_SymbolObject SO:symbolList)
    	{
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", false);  					//可选择
        	hm.put("D2", SO.SymbolName);  			//名称
        	hm.put("D3", SO.SymbolFigure);  		//符号
        	hm.put("SymbolObject", SO);				//整个符号SymbolObject
        	this.m_HeaderListViewDataItemList.add(hm);
    	}
    	hvf.BindDataToListView(this.m_HeaderListViewDataItemList,pCallback);
    	
    	if (this.m_SelectSymbolObject == null)
    		if (this.m_HeaderListViewDataItemList.size()>0) hvf.SetSelectItemIndex(0, pCallback);
    	this.CreatePreview();
    }
    
    //创建符号预览
    private void CreatePreview()
    {
    	if (this.m_SelectSymbolObject==null) return;
    	ImageView IV = (ImageView)this._Dialog.findViewById(R.id.ivPreview);
    	IV.setImageBitmap(this.m_SelectSymbolObject.SymbolFigure);
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
