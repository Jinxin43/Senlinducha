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
	private ICallback _Callback = null;   //����ص�
	private ICallback _returnCallback = null;   //�ص�����
	public void SetCallback(ICallback returnCallback)
	{
		_returnCallback = returnCallback;
	}
	

	public void HideSoftInputMode()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);   //��������ʱ�������뷨
	}
	
	//���ذ�ť
	public void HideButton(String Text)
	{
		if (Text.equals("��Ƭ"))
		{
			View v = this.findViewById(R.id.gps_photo);
			((LinearLayout)v.getParent()).removeView(v);
		}
	}
	
	/**
	 * ���ñ����ı�
	 * @param CaptionText
	 */
	public void SetCaption(String CaptionText)
	{
		TextView tv = (TextView)this.findViewById(R.id.headerbar);
		tv.setText(" "+CaptionText);
	}
	
	//���ð�ť�Ŀ�����
	public void SetButtonEnable(String Text,boolean enable)
	{
		if (Text.equals("��Ƭ"))
		{
			View v = this.findViewById(R.id.gps_photo);
			v.setEnabled(enable);
		}
		if (Text.equals("����"))
		{
			View v = this.findViewById(R.id.gps_save);
			v.setEnabled(enable);
		}
		if (Text.equals("�˳�"))
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
        this.setCancelable(false);   //����Ӧ���˼�
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
    
    //��ť�¼�
    private void DoCommand(String StrCommand)
    {
    	if (StrCommand.equals("����"))
    	{
    		//���ؼ�������ֵˢ�ص�������
    		//this._GpsBaseObject.RefreshViewValueToData();

    		//��������
//    		if (this._GpsBaseObject.Save())
//    		{
    			if (this._returnCallback!=null)this._returnCallback.OnClick("����", "");
//    			this.dismiss();
//    		} else
//    		{
//    			Tools.ShowMessageBox(this.getContext(), "���ݱ���ܣ�");
//    		}
            return;
    	}
    	
    	if (StrCommand.equals("�˳�"))
    	{
    		if (this._returnCallback!=null)this._returnCallback.OnClick("�˳�", "");
    		this.dismiss();
    		return;
    	}
    	
    	if (StrCommand.equals("��Ƭ"))
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
    
    //�������հ�ť���ı���ʾ
    private void UpdatePhotoButtonText()
    {
    	int PhotoCount=this._GpsBaseObject.GetPhotoCount();
    	View v = this.findViewById(R.id.gps_photo);
    	if (v==null) return;
    	Button BT = ((Button)v);
    	BT.setText("��"+PhotoCount+"��"+Tools.ToLocale("��Ƭ")+" ");
    }
    
    

    //����Ϊ��ͼ���Ʋ���

	//���������ߴ�
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
	//�������óߴ�
	public void ReSetSize(float WScale,float HScale)
	{
        WindowManager.LayoutParams p = this.getWindow().getAttributes(); 
        WindowManager m = ((Activity)PubVar.m_DoEvent.m_Context).getWindowManager(); 
        
        //��ȡ�Ի���ǰ�Ĳ���ֵ ���ɸ��ݲ�ͬ�ֱ��ʵ�������ʾ�ĸ����
        Display d = m.getDefaultDisplay(); //Ϊ��ȡ��Ļ���� 
        p.x = 0; //����λ�� Ĭ��Ϊ����
        p.y = 0; //����λ�� Ĭ��Ϊ����
        if (AndroidMap.m_SCREEN_ORIENTATION==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
	        if (HScale!=0) p.height = (int) (d.getHeight()*HScale); //�߶�����Ϊ��Ļ��0.6 
	        if (WScale!=0) p.width = (int) (d.getWidth()*WScale); //�������Ϊ��Ļ��0.95 
        }
        else
        {
        	//p.width = (int) (d.getWidth()*1); //�������Ϊ��Ļ
        	//p.height = (int) (d.getHeight()*1); //�߶�����Ϊ��Ļ
        	if (HScale!=0) p.height = (int) (d.getHeight()*HScale); //�߶�����Ϊ��Ļ��0.6 
 	        if (WScale!=0) p.width = (int) (d.getWidth()*WScale); //�������Ϊ��Ļ��0.95 
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
        	DoCommand("�˳�");
        	return false;
//        	moveTaskToBack(false);  //��ʾ������ť�󱣳�����״̬��������
//            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

