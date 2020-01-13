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
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_layer_render_uniquevalue_selectfield
{
	private v1_FormTemplate _Dialog = null; 
	
    public v1_project_layer_render_uniquevalue_selectfield()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_render_uniquevalue_selectfield);
    	//_Dialog.ReSetSize(1f, 0.96f);
    	_Dialog.SetCaption("字段组合");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    }

    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (m_BindFieldList!=null)
	    		{
	    			List<v1_LayerField> SelectFieldList = new ArrayList<v1_LayerField>();
	    			for(HashMap<String,Object> hm:m_BindFieldList)
	    			{
	    				if (Boolean.parseBoolean(hm.get("D1")+""))
	    				{
	    					v1_LayerField field = (v1_LayerField)hm.get("Field");
	    					SelectFieldList.add(field);
	    				}
	    			}
	    			if (m_Callback!=null)m_Callback.OnClick("字段列表", SelectFieldList);
	    		}
	    		_Dialog.dismiss();
	    	}
		}};

	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}

	private v1_Layer m_EditLayer = null;
	/**
	 * 设置当前正在编辑的图层
	 * @param lyr
	 */
	public void SetEditLayer(v1_Layer lyr)
	{
		this.m_EditLayer = lyr;
	}
	
	private List<HashMap<String,Object>> m_BindFieldList = null;
    /**
     * 加载图层字段相关信息
     */
    private void LoadLayerFieldInfo()
    {

    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_listview), "多值渲染_选择字段",pCallback);

		this.m_BindFieldList = new ArrayList<HashMap<String,Object>>();
		
		//图层的字段列表
    	List<v1_LayerField> lyrFieldList = this.m_EditLayer.GetFieldList();
    	for(v1_LayerField Field:lyrFieldList)
    	{
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", false);
        	hm.put("D2", Field.GetFieldName());
        	hm.put("Field", Field);
        	this.m_BindFieldList.add(hm);
    	}
    	hvf.BindDataToListView(this.m_BindFieldList);
    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadLayerFieldInfo();}});
    	_Dialog.show();
    }
    

}
