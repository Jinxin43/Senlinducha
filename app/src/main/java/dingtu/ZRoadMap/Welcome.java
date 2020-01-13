package dingtu.ZRoadMap;

import java.util.Timer;
import java.util.TimerTask;

import com.dingtu.senlinducha.R;

import lkmap.MapControl.MapControl;
import lkmap.ZRoadMap.DoEvent.DoEvent;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import dingtu.ZRoadMap.Data.ICallback;

public class Welcome extends Activity 
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(AndroidMap.m_SCREEN_ORIENTATION);
        setContentView(R.layout.welcome);
        //�ڴ˼�����֤��Ϣ���豸��
        //boolean PassCheck =AuthorizeTools.CheckAuthorize(this);
        //String CheckInfo = "ͨ����֤������Ȩʹ��";if (!PassCheck)CheckInfo="δͨ����֤������Ȩ";
        String aa = this.getResources().getString(R.string.app_name);
        //lkmap.Tools.Tools.SetTextViewValueOnID(this, R.id.welcome_softnameEx,this.getResources().getString(R.string.app_name));
       // lkmap.Tools.Tools.SetTextViewValueOnID(this, R.id.welcome_softver,"�汾��"+this.getResources().getString(R.string.app_ver)+"  ");
        lkmap.Tools.Tools.SetTextViewValueOnID(this, R.id.welcome_softunit,"��"+PubVar.m_Version+"��");
        
        //lkmap.Tools.Tools.SetTextViewValueOnID(this, R.id.welcome_author,"��Ȩ��"+CheckInfo);
        lkmap.Tools.Tools.SetTextViewValueOnID(this, R.id.welcome_text1, "���ڼ��ص�ͼ���ݣ��Ժ�...");
        
        String _SystemPath ="";//lkmap.Tools.Tools.GetSDCardPath();
//    	if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
//    	{
    		//_SystemPath=Environment.getExternalStorageDirectory().getPath()+"/·ͼ���ݲɼ�ϵͳ";
//    		if (!lkmap.Tools.Tools.ExistFile(_SystemPath))
//    		{
//    			_SystemPath =Environment.getExternalStorageDirectory().getPath()+"/sd/·ͼ���ݲɼ�ϵͳ";
//    			if (!lkmap.Tools.Tools.ExistFile(_SystemPath))
//    			{
//    				_SystemPath =Environment.getExternalStorageDirectory().getPath()+"/extStorages/SdCard/·ͼ���ݲɼ�ϵͳ";
//        			if (!lkmap.Tools.Tools.ExistFile(_SystemPath))
//        			{
//        				_SystemPath =Environment.getExternalStorageDirectory().getPath()+"/external_sd/·ͼ���ݲɼ�ϵͳ";
//        			}
//    			}
//    			
//    		}
//    	}
    	
    	//�ж�ϵͳĿ¼�Ƿ����
    	if (_SystemPath.equals(""))
    	{
    		lkmap.Tools.Tools.ShowMessageBox(this,"ϵͳĿ¼����·ͼ���ݲɼ�ϵͳ�������ڣ��޷���������",new ICallback(){

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					finish();
					PubVar.m_DoEvent.DoCommand("��ȫ�˳�");
				}});
    		return;
    	}
    	
    	handler.postDelayed(runnable,1000*1);
    } 

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() 
    {
        public void run() 
        {
        	PubVar.m_DoEvent.DoCommand("����");
        	finish(); 
        }
    };
    
    private class LoadMapData extends AsyncTask<Object, Object, Object>
    {
    	protected void onPostExecute(Object result) 
    	{
			//lkmap.Tools.Tools.ShowMessageBox("[ϵͳĿ¼��·ͼ���ݲɼ�ϵͳ]�����ڣ��޷���������");
    		PubVar.m_DoEvent.DoCommand("����");
    		//�˷��������߳�ִ�У�����ִ�еĽ����Ϊ�˷����Ĳ�������
			//PubVar.m_Map.AyncLoaddata = false;
			//PubVar.m_DoEvent.DoCommand("ȫ��");
			finish();
    	}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			//lkmap.Tools.Tools.ShowMessageBox("[ϵͳĿ¼��·ͼ���ݲɼ�ϵͳ]�����ڣ��޷���������");
			//PubVar.m_Map.AyncLoaddata = true;
			//PubVar.m_DoEvent.DoCommand("����");
			return null;
		}


    }


}