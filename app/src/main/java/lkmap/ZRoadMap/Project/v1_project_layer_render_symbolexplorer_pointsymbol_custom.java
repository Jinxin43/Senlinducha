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
    	_Dialog.SetCaption(Tools.ToLocale("�����"));

    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	
    	this.SetButtonEnable(false);
    }
    
    /**
     * ���ð�ť�Ŀ�����
     * @param enable
     */
    private void SetButtonEnable(boolean enabled)
    {
    	_Dialog.GetButton("1").setEnabled(enabled);
    }
    
    //�ϲ���ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (m_Callback!=null)
    			{
		    		if (m_SelectItem!=null)
		    		{
		    			HashMap<String,Object> OBJ = (HashMap<String,Object>)m_SelectItem;
		    			v1_SymbolObject SO = (v1_SymbolObject)OBJ.get("SymbolObject");
		    			PointSymbol PS = new PointSymbol();
		    			PS.CreateByBase64(SO.SymbolBase64Str);SO.SymbolFigure= PS.ToFigureBitmap(64,48);
		    			m_Callback.OnClick("���ſ�", SO);
		    		}
    			}
	    		_Dialog.dismiss();
	    	}

	    	//�ڷ����б���ѡ��һ���ص�
	    	if (Str.equals("�б�ѡ��"))
	    	{
	    		SetButtonEnable(true);
	    		m_SelectItem = ExtraStr;
	    		pCallback.OnClick("ȷ��", ExtraStr);
	    	}
			
		}};
		
	
	//�ص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		
	//�����б�
	private List<v1_SymbolObject> m_SymbolObjectList = new ArrayList<v1_SymbolObject>();
	public void SetSymbolObjectList(List<v1_SymbolObject> SOList){this.m_SymbolObjectList = SOList;}
		
	
	//�����б�󶨵�������
	private List<HashMap<String,Object>> m_HeaderListViewDataItemList = null;
	//ѡ�к�ķ���
	private Object m_SelectItem = null;
    /**
     * ���ط��ſ���Ϣ
     */
    private void LoadSymbolLibInfo()
    {
    	//�󶨷����б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.symbol_list), "�򵥵�����б�",pCallback);
    	
    	//��ȡָ�����ſ��з����б�
    	this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String,Object>>();
    	
    	for(v1_SymbolObject SO:this.m_SymbolObjectList)
    	{
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", SO.SymbolFigure);  		//����
        	hm.put("SymbolObject", SO);				//��������SymbolObject
        	this.m_HeaderListViewDataItemList.add(hm);
    	}
    	hvf.BindDataToListView(this.m_HeaderListViewDataItemList,pCallback);
    	
    	//������ͼ�㰴ť����༭֮���
    	this.SetButtonEnable(false);
    	this.m_SelectItem = null;

    }
    
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadSymbolLibInfo();}});
    	_Dialog.show();
    }
    

}
