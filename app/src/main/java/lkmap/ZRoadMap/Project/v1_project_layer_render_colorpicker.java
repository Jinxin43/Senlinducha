package lkmap.ZRoadMap.Project;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_ColorPicker_B;
import lkmap.ZRoadMap.MyControl.v1_ColorPicker_HS;
import lkmap.ZRoadMap.MyControl.v1_ColorPicker_RGB;
import lkmap.ZRoadMap.MyControl.v1_ColorPicker_Transparent;

public class v1_project_layer_render_colorpicker
{
	private v1_FormTemplate _Dialog = null; 
	
	private v1_ColorPicker_HS _CP_HS = null;
	private v1_ColorPicker_B _CP_B = null;
	private v1_ColorPicker_Transparent _CP_T= null;
	
    public v1_project_layer_render_colorpicker()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_render_colorpicker);
    	_Dialog.ReSetSize(1f, 0.96f);
    	
    	_Dialog.SetCaption("颜色管理器");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+",确定  ,确定", pCallback);
    	
    	this._CP_HS  = (v1_ColorPicker_HS)_Dialog.findViewById(R.id.cp_color);
    	this._CP_B = (v1_ColorPicker_B)_Dialog.findViewById(R.id.cp_color_b);
    	this._CP_T = (v1_ColorPicker_Transparent)_Dialog.findViewById(R.id.cp_color_transparent);
    	
    }
    
    
    //上部按钮事件
    private v1_ColorPicker_RGB m_TempRGB = null;
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		String HValue = Tools.GetTextValueOnID(_Dialog, R.id.et_h);
	    		if (m_Callback!=null)m_Callback.OnClick("颜色", "#"+HValue);
	    		_Dialog.dismiss();
	    	}
	    	if (Str.equals("Color_HS"))
	    	{
	    		v1_ColorPicker_RGB rgb = (v1_ColorPicker_RGB)ExtraStr;
				_CP_B.Set(rgb.R,rgb.G,rgb.B);
	    	}
	    	if (Str.equals("Color_B"))
	    	{
	    		v1_ColorPicker_RGB rgb = (v1_ColorPicker_RGB)ExtraStr;
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_r, rgb.R+"");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_g, rgb.G+"");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_b, rgb.B+"");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_h, rgb.ToHex()+"");
				pCallback.OnClick("Color_Transparent", _Dialog.findViewById(R.id.et_h).getTag().toString());
				
	    	}
	    	
	    	//透明度
	    	if (Str.equals("Color_Transparent"))
	    	{
				String H = Tools.GetTextValueOnID(_Dialog, R.id.et_h);
				if (m_TempRGB==null)m_TempRGB = new v1_ColorPicker_RGB();
				int CInt = Color.parseColor("#"+H);
				m_TempRGB.R = Color.red(CInt);
				m_TempRGB.G = Color.green(CInt);
				m_TempRGB.B = Color.blue(CInt);
				m_TempRGB.A = Integer.parseInt(ExtraStr.toString());
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_h, m_TempRGB.ToHex()+"");
				_Dialog.findViewById(R.id.et_h).setTag(ExtraStr.toString());
				TextView tv = (TextView)_Dialog.findViewById(R.id.tv_color);
				tv.setBackgroundColor(m_TempRGB.ToInt());
	    	}
			
		}};
		
	//选择颜色后的回调
	private ICallback m_Callback = null;
	public void SetICallback(ICallback cb){this.m_Callback = cb;}
		
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				_CP_HS.SetCallback(pCallback);
		    	_CP_HS.LoadHS();
		    	
		    	_CP_B.SetCallback(pCallback);
		    	_CP_B.SetDefaultValue();
		    	
		    	_CP_T.SetCallback(pCallback);
		    	_CP_T.Create();
		    	
				}});
    	_Dialog.show();
    }
    

}
