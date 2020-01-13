package dingtu.ZRoadMap.Data;

import lkmap.Tools.Tools;

import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import dingtu.ZRoadMap.AndroidMap;
import dingtu.ZRoadMap.PubVar;

public class DataBindAlertDialog extends Dialog
{
	public Context C = null;
	private ICallback _Callback = null;   //相机回调
	private ICallback _returnCallback = null;   //回调函数
	public void SetCallback(ICallback returnCallback)
	{
		_returnCallback = returnCallback;
	}
	

	public void HideSoftInputMode()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);   //弹出窗体时隐藏输入法
	}
	
	//隐藏按钮
	public void HideButton(String Text)
	{
		if (Text.equals("照片"))
		{
			View v = this.findViewById(R.id.gps_photo);
			((LinearLayout)v.getParent()).removeView(v);
		}
	}
	
	/**
	 * 设置标题文本
	 * @param CaptionText
	 */
	public void SetCaption(String CaptionText)
	{
		TextView tv = (TextView)this.findViewById(R.id.headerbar);
		tv.setText(" "+CaptionText);
	}
	
	//设置按钮的可用性
	public void SetButtonEnable(String Text,boolean enable)
	{
		if (Text.equals("照片"))
		{
			View v = this.findViewById(R.id.gps_photo);
			v.setEnabled(enable);
		}
		if (Text.equals("保存"))
		{
			View v = this.findViewById(R.id.gps_save);
			v.setEnabled(enable);
		}
		if (Text.equals("退出"))
		{
			View v = this.findViewById(R.id.gps_quit);
			v.setEnabled(enable);
		}
	}
	
	public DataBindAlertDialog(Context context)
	{
		super(context);
		this.setOnShowListener(new OnShowListener(){

			@Override
			public void onShow(DialogInterface arg0){}});
		
		this.C = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCancelable(false);   //不响应回退键
        this.HideSoftInputMode();
        this.setContentView(R.layout.databindalertdialogtemplate);
        ViewClick VC = new ViewClick();
        Button bt = (Button)this.findViewById(R.id.gps_photo);bt.setOnClickListener(VC);
        bt.setText(Tools.ToLocale(bt.getText().toString())+" ");
        bt = (Button)this.findViewById(R.id.gps_save);bt.setOnClickListener(VC);
        bt.setText(Tools.ToLocale(bt.getText().toString())+" ");
        bt = (Button)this.findViewById(R.id.gps_quit);bt.setOnClickListener(VC);
        bt.setText(Tools.ToLocale(bt.getText().toString())+" ");
        
//        Window dialogWindow = this.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
	}
	
    class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		DataBindAlertDialog.this.DoCommand(Tag);
    	}
    }
    
 
    private v1_CGpsDataObject _GpsBaseObject = null;
    public void SetGpsBasePointObject(v1_CGpsDataObject O)
    {
    	_GpsBaseObject = O;
    }
    
    //按钮事件
    private void DoCommand(String StrCommand)
    {
    	if (StrCommand.equals("保存"))
    	{
    		//将控件的数据值刷回到数据中
    		//this._GpsBaseObject.RefreshViewValueToData();

    		//保存数据
//    		if (this._GpsBaseObject.Save())
//    		{
    			if (this._returnCallback!=null)this._returnCallback.OnClick("保存", "");
//    			this.dismiss();
//    		} else
//    		{
//    			Tools.ShowMessageBox(this.getContext(), "数据保存败！");
//    		}
            return;
    	}
    	
    	if (StrCommand.equals("退出"))
    	{
    		if (this._returnCallback!=null)this._returnCallback.OnClick("退出", "");
    		this.dismiss();
    		return;
    	}
    	
    	if (StrCommand.equals("照片"))
    	{
    		v1_Photo _PH = new v1_Photo();
    		_PH.SetQuitCallback(new ICallback()
    		{
				@Override
				public void OnClick(String Str, Object ExtraStr)
				{
					try
					{
				    	String PhotoList = Str;
				    	_GpsBaseObject.SetSYS_PHOTO(PhotoList);
				        UpdatePhotoButtonText();
					}
					catch(Error e)
					{
						Tools.ShowMessageBox(e.getMessage());
					}
	
				}});
				
    		_PH.SetPhotoPara(_GpsBaseObject.GetSYS_LABEL(), _GpsBaseObject.GetSYS_OID(), _GpsBaseObject.GetSYS_PHOTO());
    		_PH.ShowDialog();
    		return;
    	}
    }
    

    public void UpdateDialogShowInfo()
    {
    	this.UpdatePhotoButtonText();
    }
    
    //更新拍照按钮的文本显示
    private void UpdatePhotoButtonText()
    {
    	int PhotoCount=this._GpsBaseObject.GetPhotoCount();
    	View v = this.findViewById(R.id.gps_photo);
    	if (v==null) return;
    	Button BT = ((Button)v);
    	BT.setText("【"+PhotoCount+"】"+Tools.ToLocale("照片")+" ");
    }
    
    

    //下面为视图控制部分

	//设置其它尺寸
    public void SetOtherView(int ViewID)
    {
    	LayoutInflater inflater = (LayoutInflater)this.C.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vPopupWindow=inflater.inflate(ViewID, null, false); 
        this.SetOtherView(vPopupWindow);
    }
    
	public void SetOtherView(View view)
	{
		LinearLayout LY = (LinearLayout)this.findViewById(R.id.databindalertdialoglayout);
		LY.addView(view, LY.getLayoutParams());
	}
	//重新设置尺寸
	public void ReSetSize(float WScale,float HScale)
	{
        WindowManager.LayoutParams p = this.getWindow().getAttributes(); 
        WindowManager m = ((Activity)PubVar.m_DoEvent.m_Context).getWindowManager(); 
        
        //获取对话框当前的参数值 ，可根据不同分辨率的设置显示的高与宽
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高 
        p.x = 0; //设置位置 默认为居中
        p.y = 0; //设置位置 默认为居中
        if (AndroidMap.m_SCREEN_ORIENTATION==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
	        if (HScale!=0) p.height = (int) (d.getHeight()*HScale); //高度设置为屏幕的0.6 
	        if (WScale!=0) p.width = (int) (d.getWidth()*WScale); //宽度设置为屏幕的0.95 
        }
        else
        {
        	//p.width = (int) (d.getWidth()*1); //宽度设置为屏幕
        	//p.height = (int) (d.getHeight()*1); //高度设置为屏幕
        	if (HScale!=0) p.height = (int) (d.getHeight()*HScale); //高度设置为屏幕的0.6 
 	        if (WScale!=0) p.width = (int) (d.getWidth()*WScale); //宽度设置为屏幕的0.95 
        }
        //p.height=1000;
        //p.width=1000;
        this.getWindow().setAttributes((android.view.WindowManager.LayoutParams) p);
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
        	DoCommand("退出");
        	return false;
//        	moveTaskToBack(false);  //表示按回退钮后保持现有状态，不回退
//            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

