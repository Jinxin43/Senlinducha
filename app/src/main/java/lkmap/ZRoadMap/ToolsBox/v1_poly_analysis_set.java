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
    	
    	//���ñ���
    	_Dialog.SetCaption(Tools.ToLocale("��������"));
    	
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_data_filter+",����  ,����", pCallback);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+" ,ȷ��", pCallback);
    	
    	CheckBox cb = (CheckBox)_Dialog.findViewById(R.id.cbselectall);
    	cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				for(HashMap<String,Object> hm:m_PolyAnalysisOption)hm.put("Select",arg1);
		    	((v1_Project_Layer_BKMap_MapFileAdapter)(((ListView)_Dialog.findViewById(R.id.listView1))).getAdapter()).notifyDataSetChanged();
			}});
    }
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (SaveOption())
	    		{
	    			_Dialog.dismiss();
			    	if (m_Callback!=null)m_Callback.OnClick("����", "");
	    		}
	    	}
	    	
	    	if (Str.equals("����"))
	    	{
	    		v1_poly_analysis_set_addfilter pasa = new v1_poly_analysis_set_addfilter();
	    		pasa.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						//�����ظ�LayerId����
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
	    	
	    	if(Str.equals("ButtonClick"))  //ɾ������
	    	{
	    		final HashMap<String,Object> hm = (HashMap<String,Object>)ExtraStr;
	    		Tools.ShowYesNoMessage(_Dialog.getContext(),"�Ƿ�ɾ������ѡ�\nͼ�����ƣ�"+hm.get("LayerName")+"\nͳ���ֶΣ�"+hm.get("FieldCaptionListStr"),new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_PolyAnalysisOption.remove(hm);
						((v1_Project_Layer_BKMap_MapFileAdapter)(((ListView)_Dialog.findViewById(R.id.listView1))).getAdapter()).notifyDataSetChanged();
					}});
	    	}
		}};
		
	//�ص�
	private ICallback m_Callback = null;
	
	/**
	 * �ص�
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		
	/**
	 * ��������ѡ��
	 * @return
	 */
	private boolean SaveOption()
	{
		//�ж��Ƿ��й�����
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
    		Tools.ShowMessageBox("�빴ѡͳ��ѡ�");
    		return false;
    	}
	}
	
	private v1_UserConfigDB_PolyAnalysisOption m_PAO = new v1_UserConfigDB_PolyAnalysisOption();
		
    /**
     * ���������ȱʡ����
     */
	private void LoadDefaultSet()
	{
		this.m_PolyAnalysisOption = this.m_PAO.GetPolyAnalysisOption();
		this.RefreshDataToListView();
	}
	
	//ѡ���б���ʽ���v1_UserConfigDB_PolyAnalysisOption
	private List<HashMap<String,Object>> m_PolyAnalysisOption = new ArrayList<HashMap<String,Object>>();
	

	/**
	 * ˢ���б���ʾ
	 */
    private void RefreshDataToListView()
    {
    	//����ѡ���ַ���
    	for(HashMap<String,Object> optItem:m_PolyAnalysisOption)
    	{
    		optItem.put("Select", true);
    		List<String> FieldCaptionList = (List<String>)optItem.get("FieldCaptionList");
    		optItem.put("FieldCaptionListStr", Tools.JoinT(",", FieldCaptionList));
    	}
		//ˢ���б�
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
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadDefaultSet();}}
    	);
    	_Dialog.show();
    }

}
