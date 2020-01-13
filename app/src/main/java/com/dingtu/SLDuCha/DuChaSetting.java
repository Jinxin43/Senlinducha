package com.dingtu.SLDuCha;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnShowListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class DuChaSetting {

	public static final String TAG = "duchasetting";

	public static final String JCJB = "duchajb";
	public static final String JCDW = "duchadw";
	public static final String JCRY = "duchary";
	
	private v1_FormTemplate dialogView = null;
	public DuChaSetting()
	{
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.dialog_duchashezhi);
		dialogView.ReSetSize(0.4f, 0.5f);
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+",确定  ,确定", pCallback);

		dialogView.SetCaption("督察信息设置");

		v1_DataBind.SetBindListSpinner(dialogView, "导出格式",
				new String[] { "1-县级",  "2-市级",  "3-省级",  "4-直属院",  "5-专员办" }, R.id.spJCJB);

	
	}
	
	//按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("确定"))
			{
				SharedPreferences preferences = dialogView.getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
				preferences.edit().putString(JCDW, Tools.GetTextValueOnID(dialogView, R.id.etJCDW)).commit();
				preferences.edit().putString(JCRY, Tools.GetTextValueOnID(dialogView, R.id.etJCRY)).commit();
				preferences.edit().putString(JCJB, Tools.GetSpinnerValueOnID(dialogView, R.id.spJCJB)).commit();
				dialogView.dismiss();
			}
		}
    };
    
    public void ShowDialog() {
		// 此处这样做的目的是为了计算控件的尺寸
		dialogView.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				SharedPreferences preferences = dialogView.getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
				Tools.SetTextViewValueOnID(dialogView, R.id.etJCDW, preferences.getString(JCDW, ""));
				Tools.SetTextViewValueOnID(dialogView, R.id.etJCRY, preferences.getString(JCRY, ""));
				String jcjb = preferences.getString(JCJB,null);
				if(jcjb != null && jcjb.length()>0)
				{
					Tools.SetSpinnerValueOnID(dialogView, R.id.spJCJB, jcjb);
				}
			}
		});
		dialogView.show();
	}

}
