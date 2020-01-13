package lkmap.ZRoadMap.DataDictionary;

import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;

public class v1_DataDictionary 
{
	private v1_FormTemplate _Dialog = null; 
    public v1_DataDictionary()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_dictionary);
    	_Dialog.ReSetSize(0.5f,-1f);
    	_Dialog.SetCaption("数据字典");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",确定  ,确定", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_datadiction_delete+",置空  ,置空", pCallback);
    	
    	//字典类别
    	final v1_SpinnerDialog SD1 = (v1_SpinnerDialog)_Dialog.findViewById(R.id.sd_zdtype);
    	SD1.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				v1_DataDictionary_ZDType zdtype = new v1_DataDictionary_ZDType();
				zdtype.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_DataBind.SetBindListSpinner(_Dialog.getContext(), "", Tools.StrArrayToList(new String[]{ExtraStr.toString()}), SD1);
						pCallback.OnClick("查询字典值列表", "");
					}});
				zdtype.ShowDialog();
			}});
    	
    	//条目大类
    	final v1_SpinnerDialog SD2 = (v1_SpinnerDialog)_Dialog.findViewById(R.id.sd_zdsub);
    	SD2.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				v1_DataDictionary_ZDSub zdsub = new v1_DataDictionary_ZDSub();
				zdsub.SetZDType(Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdtype));
				zdsub.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_DataBind.SetBindListSpinner(_Dialog.getContext(), "", Tools.StrArrayToList(new String[]{ExtraStr.toString()}), SD2);
						pCallback.OnClick("查询字典值列表", "");
					}});
				zdsub.ShowDialog();
			}});
    	
    	//条目细类
    	final v1_SpinnerDialog SD3 = (v1_SpinnerDialog)_Dialog.findViewById(R.id.sd_zdname);
    	SD3.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				v1_DataDictionary_ZDName zdname = new v1_DataDictionary_ZDName();
				zdname.SetZDType(Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdtype));
				zdname.SetZDSub(Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdsub));
				zdname.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_DataBind.SetBindListSpinner(_Dialog.getContext(), "", Tools.StrArrayToList(new String[]{ExtraStr.toString()}), SD3);
						pCallback.OnClick("查询字典值列表", "");
					}});
				zdname.ShowDialog();
			}});    	
    }
    
    //选择后的回调
    private ICallback _SelectCallback = null;
    public void SetCallback(ICallback cb){this._SelectCallback = cb;}
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("置空"))
			{
	    		if (_SelectCallback!=null)
    			{
    				_SelectCallback.OnClick("数据字典", null);
    			}
	    		_Dialog.dismiss();
			}
	    	if (Str.equals("确定"))
	    	{
	    		if (_SelectCallback!=null)
    			{
					String ZDType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdtype);
					String ZDSub = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdsub);
					String ZDName = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdname);
					HashMap<String,Object> dataDicObject = PubVar.m_DoEvent.m_ConfigDB.GetDataDictionaryExplorer().GetZDValueList(ZDType, ZDSub, ZDName);
    				_SelectCallback.OnClick("数据字典", dataDicObject);
    			}
	    		_Dialog.dismiss();
	    	}
	    	
	    	if (Str.equals("查询字典值列表"))
	    	{
				String ZDType = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdtype);
				String ZDSub = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdsub);
				String ZDName = Tools.GetSpinnerValueOnID(_Dialog, R.id.sd_zdname);
				HashMap<String,Object> dataDicObject = PubVar.m_DoEvent.m_ConfigDB.GetDataDictionaryExplorer().GetZDValueList(ZDType, ZDSub, ZDName);
				if (dataDicObject.get("ZDList")==null)Tools.SetTextViewValueOnID(_Dialog, R.id.tv_zdvalue,"");
				else Tools.SetTextViewValueOnID(_Dialog, R.id.tv_zdvalue, Tools.StrListToStr((List<String>)(dataDicObject.get("ZDList"))));
	    	}
		}};


    public void ShowDialog()
    {
    	_Dialog.show();
    }
}
