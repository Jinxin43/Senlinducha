package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.RadioButton;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class v1_project_layer_bkmap_web
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer_bkmap_web()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_bkmap_web);
    	_Dialog.ReSetSize(0.5f, 0.8f);
    	
    	//��������
    	String _ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
    	
    	//�ϲ����ܰ�ť�¼���
    	_Dialog.SetCaption("��"+_ProjectName+"��"+Tools.ToLocale("��ͼ�ļ�"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_project_clearbkmap+","+Tools.ToLocale("�޵�ͼ")+"  ,�޵�ͼ", pCallback);
    }
    
    //WGS-84������Ӳ��ѡ��
    private int[] m_OverMapOption = new int[]{R.id.rb_google_sat,R.id.rb_google_ter,R.id.rb_google_street,R.id.rb_tdt_sat,R.id.rb_tdt_street};
    
	//ѡ�е�ͼ������ȷ������ص�
	private ICallback m_Callback = null;
	
	/**
	 * ѡ�е�ͼ������ȷ������ص�
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
    //�ϲ���ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("�޵�ͼ"))
			{
				m_Callback.OnClick("��0��", null);
				_Dialog.dismiss();
			}
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (m_Callback==null){ _Dialog.dismiss();return;}
	    		
	    		List<String> MapFileNameList = new ArrayList<String>();
    			List<HashMap<String,Object>> selectWebMap = new ArrayList<HashMap<String,Object>>();
        		for(int op:m_OverMapOption)
        		{
        			RadioButton rb = (RadioButton)_Dialog.findViewById(op);
        			if (rb.isChecked())
        			{
        				HashMap<String,Object> hmObj = new HashMap<String,Object>();
        				hmObj.put("MapFileName", rb.getText().toString());
        				selectWebMap.add(hmObj);
        				MapFileNameList.add(rb.getText().toString());
        			}
        		}
        		m_Callback.OnClick("��"+MapFileNameList.size()+"��"+Tools.JoinT(",", MapFileNameList), selectWebMap);
        		_Dialog.dismiss();
	    	}
		}};
		
		private List<HashMap<String,Object>> m_MapFileList = null;
		
	    //���õ�ͼ�ļ��б�
	    public void SetMapFileList(List<HashMap<String,Object>> mapFileList)
	    {
	    	this.m_MapFileList = mapFileList;
	    }
			
	    /**
	     * ���ص�ͼ�ļ��б���Ϣ
	     */
	    private void LoadBKMapInfo()
	    {
			//���ó�ʼֵ
			for(int op:this.m_OverMapOption)
			{
				RadioButton rb = (RadioButton)_Dialog.findViewById(op);
				
				//Ĭ��ֵ
				if (this.m_MapFileList.size()>=1)
				{
					if (rb.getText().equals(this.m_MapFileList.get(0).get("MapFileName")+""))rb.setChecked(true);
				}
			}
			//((TextView)_Dialog.findViewById(R.id.tvGrid84Caption)).setText(Tools.ToLocale("Ӱ��ͼ")+"��"+PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileName()+"��");
	    }
	    
	    public void ShowDialog()
	    {
	    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
	    	_Dialog.setOnShowListener(new OnShowListener(){
				@Override
				public void onShow(DialogInterface dialog) {LoadBKMapInfo();}});
	    	_Dialog.show();
	    }
    

}
