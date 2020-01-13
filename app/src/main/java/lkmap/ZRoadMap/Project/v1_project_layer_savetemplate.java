package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Enum.lkEditMode;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_layer_savetemplate
{
	private v1_FormTemplate _Dialog = null; 
	

    public v1_project_layer_savetemplate()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_savetemplate);
    	_Dialog.ReSetSize(0.5f,-1f);
    	//初始显示及按钮
    	_Dialog.SetCaption(Tools.ToLocale("图层模板"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);

    	//多语言支持 
    	int[] ViewID = new int[]{R.id.tvLocaleText1,R.id.tvLocaleText2,R.id.tvLocaleText3,R.id.tvLocaleText4,R.id.cb_overwrite};
    	for(int vid : ViewID)
    	{
    		Tools.ToLocale(_Dialog.findViewById(vid));
    	}
    }
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (SaveLayerInfo())_Dialog.dismiss();
	    	}
		}};



	private List<v1_Layer> m_LayerList = null;
	/**
	 * 设置需要保存的图层列表
	 * @param vLayerList
	 */
	public void SetLayerList(List<v1_Layer> vLayerList)
	{
		this.m_LayerList = vLayerList;
		Tools.SetTextViewValueOnID(_Dialog, R.id.lt_layercount, this.m_LayerList.size()+"");
		Tools.SetTextViewValueOnID(_Dialog, R.id.lt_time, Tools.GetSystemDate());
	}
	
	/**
	 * 保存图层模板信息
	 * @return
	 */
	private boolean SaveLayerInfo()
	{
		//获取模板保存信息
		String CreateTime = Tools.GetTextValueOnID(_Dialog, R.id.lt_time);	//创建时间
		String LayerCount = Tools.GetTextValueOnID(_Dialog, R.id.lt_layercount);	//图层数量
		boolean OverWrite = Tools.GetCheckBoxValueOnID(_Dialog, R.id.cb_overwrite);  //覆盖同名模板
		String TempName = Tools.GetTextValueOnID(_Dialog, R.id.lt_name);	//模板名称
		
		//验证模板名称信息
		if (TempName.equals(""))
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "模板名称不允许为空值！");return false;
		}
		if (this.m_LayerList.size()==0)
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "图层列表数量为0，无法保存模板！");return false;
		}
		
		//构造参数
		HashMap<String,Object> tempatePara = new HashMap<String,Object>();
		tempatePara.put("Name", TempName);
		tempatePara.put("CreateTime", CreateTime);
		tempatePara.put("OverWrite", OverWrite);
		tempatePara.put("LayerList", this.m_LayerList);
		
		//保存
		String returnStr = PubVar.m_DoEvent.m_UserConfigDB.GetLayerTemplate().SaveLayerTemplate(tempatePara);
		if (returnStr.equals("OK")) return true;
		else
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "无法保存模板，原因："+returnStr);return false;
		}
	}
	

    public void ShowDialog()
    {
    	_Dialog.show();
    }
    

}
