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
    	
    	//工程名称
    	String _ProjectName = PubVar.m_HashMap.GetValueObject("Project").Value;
    	
    	//上部功能按钮事件绑定
    	_Dialog.SetCaption("【"+_ProjectName+"】"+Tools.ToLocale("底图文件"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_project_clearbkmap+","+Tools.ToLocale("无底图")+"  ,无底图", pCallback);
    }
    
    //WGS-84坐标叠加层的选项
    private int[] m_OverMapOption = new int[]{R.id.rb_google_sat,R.id.rb_google_ter,R.id.rb_google_street,R.id.rb_tdt_sat,R.id.rb_tdt_street};
    
	//选中底图并按【确定】后回调
	private ICallback m_Callback = null;
	
	/**
	 * 选中底图并按【确定】后回调
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
    //上部按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("无底图"))
			{
				m_Callback.OnClick("【0】", null);
				_Dialog.dismiss();
			}
	    	if (Str.equals("确定"))
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
        		m_Callback.OnClick("【"+MapFileNameList.size()+"】"+Tools.JoinT(",", MapFileNameList), selectWebMap);
        		_Dialog.dismiss();
	    	}
		}};
		
		private List<HashMap<String,Object>> m_MapFileList = null;
		
	    //设置底图文件列表
	    public void SetMapFileList(List<HashMap<String,Object>> mapFileList)
	    {
	    	this.m_MapFileList = mapFileList;
	    }
			
	    /**
	     * 加载底图文件列表信息
	     */
	    private void LoadBKMapInfo()
	    {
			//设置初始值
			for(int op:this.m_OverMapOption)
			{
				RadioButton rb = (RadioButton)_Dialog.findViewById(op);
				
				//默认值
				if (this.m_MapFileList.size()>=1)
				{
					if (rb.getText().equals(this.m_MapFileList.get(0).get("MapFileName")+""))rb.setChecked(true);
				}
			}
			//((TextView)_Dialog.findViewById(R.id.tvGrid84Caption)).setText(Tools.ToLocale("影像图")+"【"+PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileName()+"】");
	    }
	    
	    public void ShowDialog()
	    {
	    	//此处这样做的目的是为了计算控件的尺寸
	    	_Dialog.setOnShowListener(new OnShowListener(){
				@Override
				public void onShow(DialogInterface dialog) {LoadBKMapInfo();}});
	    	_Dialog.show();
	    }
    

}
