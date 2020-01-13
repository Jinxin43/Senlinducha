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
    	_Dialog.SetCaption(Tools.ToLocale("���ſ�"));

    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	_Dialog.SetButtonInfo("3,"+R.drawable.v1_layer_field_delete+","+Tools.ToLocale("ɾ�� ")+" ,ɾ��", pCallback);
    	_Dialog.findViewById(R.id.btEdit).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				pCallback.OnClick("�༭", null);
			}});
    	//this.SetButtonEnable(false);
    	
    	//������֧�� 
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText));
    }
    
    /**
     * ���ð�ť�Ŀ�����
     * @param enable
     */
    private void SetButtonEnable(boolean enabled)
    {
    	_Dialog.GetButton("1").setEnabled(enabled);
    	_Dialog.findViewById(R.id.btEdit).setEnabled(enabled);
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
		    		if (m_SelectSymbolObject!=null)
		    		{
		    			m_Callback.OnClick("���ſ�", m_SelectSymbolObject);
		    		}
    			}
	    		_Dialog.dismiss();
	    	}
	    	if (Str.equals("�༭"))
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
	    	
	    	if (Str.equals("ɾ��"))
	    	{
	    		
	    	}
	    	
	    	//�ڷ����б���ѡ��һ���ص�
	    	if (Str.equals("�б�ѡ��"))
	    	{
	    		SetButtonEnable(true);
	    		m_SelectSymbolObject = (v1_SymbolObject)((HashMap<String,Object>)ExtraStr).get("SymbolObject");
	    		CreatePreview();
	    	}
			
	    	//ͼ�����ѡ���Ļص���Ҳ���Ǵ򿪷���ѡȡ�Ի���
	    	if (Str.equals("ImageSpinnerCallback"))
	    	{
	    		
	    	}
		}};
		
	
	//�ص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		
	//ͼ������
	private lkGeoLayerType m_GeoLayerType = lkGeoLayerType.enUnknow;
	public void SetGeoLayerType(lkGeoLayerType geoLayerType){this.m_GeoLayerType = geoLayerType;}
		
	public void SetDefaultSymbolObject(v1_SymbolObject so)
	{
		this.m_SelectSymbolObject = so;
	}
	
	//�����б�󶨵�������
	private List<HashMap<String,Object>> m_HeaderListViewDataItemList = null;
	
	//ѡ�к�ķ���
	private v1_SymbolObject m_SelectSymbolObject = null;
    /**
     * ���ط��ſ���Ϣ
     */
    private void LoadSymbolLibInfo()
    {
    	if (this.m_GeoLayerType==lkGeoLayerType.enUnknow) return;
    	
    	//�󶨷����б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.symbol_list), "�����б�",pCallback);
    	
    	//��ȡָ�����ſ��з����б�
    	this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String,Object>>();
    	List<v1_SymbolObject> symbolList = PubVar.m_DoEvent.m_ConfigDB.GetSymbolExplorer().GetSymbolObjectList(new String[]{}, this.m_GeoLayerType);
    	
    	for(v1_SymbolObject SO:symbolList)
    	{
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", false);  					//��ѡ��
        	hm.put("D2", SO.SymbolName);  			//����
        	hm.put("D3", SO.SymbolFigure);  		//����
        	hm.put("SymbolObject", SO);				//��������SymbolObject
        	this.m_HeaderListViewDataItemList.add(hm);
    	}
    	hvf.BindDataToListView(this.m_HeaderListViewDataItemList,pCallback);
    	
    	if (this.m_SelectSymbolObject == null)
    		if (this.m_HeaderListViewDataItemList.size()>0) hvf.SetSelectItemIndex(0, pCallback);
    	this.CreatePreview();
    }
    
    //��������Ԥ��
    private void CreatePreview()
    {
    	if (this.m_SelectSymbolObject==null) return;
    	ImageView IV = (ImageView)this._Dialog.findViewById(R.id.ivPreview);
    	IV.setImageBitmap(this.m_SelectSymbolObject.SymbolFigure);
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
