package lkmap.ToolBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.ListView;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_MapFileAdapter;

public class v1_ChangeTools
{
	private v1_FormTemplate _Dialog = null; 
    public v1_ChangeTools()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetCaption("切换工具条");
    	_Dialog.SetOtherView(R.layout.v1_changetools);
    	_Dialog.ReSetSize(1f,0.96f);
    }
    
	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb)
	{
		this.m_Callback = cb;
	}

	private List<HashMap<String,Object>> m_ToolsItemList = null;
    /**
     * 加载工具条列表
     */
    private void LoadToolsItemList()
    {
    	String[] ToolsItemNameList = {"测量","移动删除","节点编辑","面工具","面分割","面公共边","面孤岛","线打断","线修形","连接圆滑"};
    	String[] ToolsItemMemoList = {"手绘测量线、面，可捕捉","移动、删除实体，回退、重做功能","移动、增加、删除线面节点",
    								  "包括分割、公共边、孤岛、合并等功能",
    								  "以绘线的方式分割面","自动提取公共边，完成面绘制",
    								  "以绘线的方式从面中分割孤岛","在指定位置处打断线","以绘线方式改变线形","多段线连接、线形圆滑"};
    	this.m_ToolsItemList = new ArrayList<HashMap<String,Object>>();
    	
    	for(int i=0;i<ToolsItemNameList.length;i++)
    	{
    		String ToolsItemName = ToolsItemNameList[i];
    		String ToolsItemMemo = ToolsItemMemoList[i];
    		HashMap<String,Object> hm = new HashMap<String,Object>();
    		hm.put("Name", ToolsItemName);
    		hm.put("Memo", ToolsItemMemo);
    		this.m_ToolsItemList.add(hm);
    	}
    	
		//刷新列表
    	v1_Project_Layer_BKMap_MapFileAdapter adapter = new v1_Project_Layer_BKMap_MapFileAdapter(_Dialog.getContext(),this.m_ToolsItemList, 
											       R.layout.v1_bk_changetools, 
											       new String[] {"Name", "Memo"}, new int[] {R.id.tvName, R.id.tvMemo});  
		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
		(((ListView)_Dialog.findViewById(R.id.listView1))).setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String ToolsName = m_ToolsItemList.get(arg2).get("Name")+"";
				if (m_Callback!=null)m_Callback.OnClick("", ToolsName);
				_Dialog.dismiss();
			}});
    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadToolsItemList();}}
    	);
    	_Dialog.show();
    }
}
