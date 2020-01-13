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
    	_Dialog.SetCaption("�ֶ����");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    }

    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
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
	    			if (m_Callback!=null)m_Callback.OnClick("�ֶ��б�", SelectFieldList);
	    		}
	    		_Dialog.dismiss();
	    	}
		}};

	//�ص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}

	private v1_Layer m_EditLayer = null;
	/**
	 * ���õ�ǰ���ڱ༭��ͼ��
	 * @param lyr
	 */
	public void SetEditLayer(v1_Layer lyr)
	{
		this.m_EditLayer = lyr;
	}
	
	private List<HashMap<String,Object>> m_BindFieldList = null;
    /**
     * ����ͼ���ֶ������Ϣ
     */
    private void LoadLayerFieldInfo()
    {

    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.in_listview), "��ֵ��Ⱦ_ѡ���ֶ�",pCallback);

		this.m_BindFieldList = new ArrayList<HashMap<String,Object>>();
		
		//ͼ����ֶ��б�
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
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadLayerFieldInfo();}});
    	_Dialog.show();
    }
    

}
