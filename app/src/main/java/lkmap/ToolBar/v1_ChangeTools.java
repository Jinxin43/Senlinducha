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
    	_Dialog.SetCaption("�л�������");
    	_Dialog.SetOtherView(R.layout.v1_changetools);
    	_Dialog.ReSetSize(1f,0.96f);
    }
    
	//�ص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb)
	{
		this.m_Callback = cb;
	}

	private List<HashMap<String,Object>> m_ToolsItemList = null;
    /**
     * ���ع������б�
     */
    private void LoadToolsItemList()
    {
    	String[] ToolsItemNameList = {"����","�ƶ�ɾ��","�ڵ�༭","�湤��","��ָ�","�湫����","��µ�","�ߴ��","������","����Բ��"};
    	String[] ToolsItemMemoList = {"�ֻ�����ߡ��棬�ɲ�׽","�ƶ���ɾ��ʵ�壬���ˡ���������","�ƶ������ӡ�ɾ������ڵ�",
    								  "�����ָ�����ߡ��µ����ϲ��ȹ���",
    								  "�Ի��ߵķ�ʽ�ָ���","�Զ���ȡ�����ߣ���������",
    								  "�Ի��ߵķ�ʽ�����зָ�µ�","��ָ��λ�ô������","�Ի��߷�ʽ�ı�����","��������ӡ�����Բ��"};
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
    	
		//ˢ���б�
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
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadToolsItemList();}}
    	);
    	_Dialog.show();
    }
}
