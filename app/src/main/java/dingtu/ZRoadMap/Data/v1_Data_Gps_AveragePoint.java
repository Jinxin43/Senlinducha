package dingtu.ZRoadMap.Data;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Coordinate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;

public class v1_Data_Gps_AveragePoint
{
	private v1_FormTemplate _Dialog = null; 
    public v1_Data_Gps_AveragePoint()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_gps_averagepoint);
    	_Dialog.SetCaption("ƽ��ֵ�ɵ�");
    	_Dialog.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				_MyAveragePoint.Cancel();
			}});
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_gpsaveragepoint+",����  ,����", pCallback);

    	_Dialog.findViewById(R.id.btreset).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				pCallback.OnClick("���¿�ʼ�ɼ�", null);
			}});
    	this._MyAveragePoint.SetCallback(pCallback);
    	
    	//��ʼ���ؼ�
    	v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_setpointcount);
    	esd1.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
    	esd1.SetSelectItemList(Tools.StrArrayToList(new String[]{"3","5","10","15","20","30","60"}));
    	esd1.getEditTextView().setEnabled(true);
    	
    }
    
    //�ɼ�ƽ������
    private v1_CGps_AveragePoint _MyAveragePoint = new v1_CGps_AveragePoint();
    
    //������Ҫ��ȡ�ĵ���
    public void SetGpsPointCount(int pointCount)
    {
		v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_setpointcount);
		esd1.setText(pointCount+"");
    }
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("���¿�ʼ�ɼ�"))
			{
				v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_setpointcount);
				_MyAveragePoint.Start(Integer.parseInt(esd1.getText()));
			}
	    	if (Str.equals("�ɼ�״̬"))
	    	{
	    		String[] gpsStatus = ExtraStr.toString().split(",");
	    		Tools.SetTextViewValueOnID(_Dialog, R.id.et_getpointcount, gpsStatus[0]);
	    		Tools.SetTextViewValueOnID(_Dialog, R.id.et_time, "�����ڲɼ�GPS��λ...����ʱ��"+gpsStatus[1]+" ��");
	    	}
	    	if (Str.equals("�ɼ����"))
	    	{
	    		CheckGPSPoint((Coordinate)ExtraStr);
	    	}	    	
	    	if (Str.equals("����"))
	    	{
	    		_MyAveragePoint.CalGpsPoint();
	    	}
		}};


	//����ȡ��GPS�����Ƿ���Ч
	private void CheckGPSPoint(final Coordinate gpsPoint)
	{
		Tools.SetTextViewValueOnID(_Dialog, R.id.et_time, "���ɼ�GPS��λ��ɡ�");
		
		if (gpsPoint==null)
		{
			Tools.ShowMessageBox(_Dialog.getContext(), "û�л�ȡ��Ч��GPS���꣡");return;
		}
		
		Tools.ShowYesNoMessage(_Dialog.getContext(), "�ѻ�ȡ������Ϣ���£�\nX="+gpsPoint.getX()+"\nY="+gpsPoint.getY()+"\n\n�Ƿ���ô�GPS���ꣿ", new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if (Str.equals("YES"))
				{
					if (m_Callback!=null)m_Callback.OnClick("OK", gpsPoint);
					_Dialog.dismiss();
				}
			}});
	}
	
	//�ɵ��Ļص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	

    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{pCallback.OnClick("���¿�ʼ�ɼ�", null);}}
    	);
    	_Dialog.show();
    }
}
