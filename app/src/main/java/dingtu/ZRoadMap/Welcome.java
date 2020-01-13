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
        //在此加入验证信息，设备码
        //boolean PassCheck =AuthorizeTools.CheckAuthorize(this);
        //String CheckInfo = "通过验证，已授权使用";if (!PassCheck)CheckInfo="未通过验证，无授权";
        String aa = this.getResources().getString(R.string.app_name);
        //lkmap.Tools.Tools.SetTextViewValueOnID(this, R.id.welcome_softnameEx,this.getResources().getString(R.string.app_name));
       // lkmap.Tools.Tools.SetTextViewValueOnID(this, R.id.welcome_softver,"版本："+this.getResources().getString(R.string.app_ver)+"  ");
        lkmap.Tools.Tools.SetTextViewValueOnID(this, R.id.welcome_softunit,"【"+PubVar.m_Version+"】");
        
        //lkmap.Tools.Tools.SetTextViewValueOnID(this, R.id.welcome_author,"授权："+CheckInfo);
        lkmap.Tools.Tools.SetTextViewValueOnID(this, R.id.welcome_text1, "正在加载地图数据，稍候...");
        
        String _SystemPath ="";//lkmap.Tools.Tools.GetSDCardPath();
//    	if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
//    	{
    		//_SystemPath=Environment.getExternalStorageDirectory().getPath()+"/路图数据采集系统";
//    		if (!lkmap.Tools.Tools.ExistFile(_SystemPath))
//    		{
//    			_SystemPath =Environment.getExternalStorageDirectory().getPath()+"/sd/路图数据采集系统";
//    			if (!lkmap.Tools.Tools.ExistFile(_SystemPath))
//    			{
//    				_SystemPath =Environment.getExternalStorageDirectory().getPath()+"/extStorages/SdCard/路图数据采集系统";
//        			if (!lkmap.Tools.Tools.ExistFile(_SystemPath))
//        			{
//        				_SystemPath =Environment.getExternalStorageDirectory().getPath()+"/external_sd/路图数据采集系统";
//        			}
//    			}
//    			
//    		}
//    	}
    	
    	//判断系统目录是否存在
    	if (_SystemPath.equals(""))
    	{
    		lkmap.Tools.Tools.ShowMessageBox(this,"系统目录：【路图数据采集系统】不存在，无法启动程序！",new ICallback(){

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					finish();
					PubVar.m_DoEvent.DoCommand("完全退出");
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
        	PubVar.m_DoEvent.DoCommand("加载");
        	finish(); 
        }
    };
    
    private class LoadMapData extends AsyncTask<Object, Object, Object>
    {
    	protected void onPostExecute(Object result) 
    	{
			//lkmap.Tools.Tools.ShowMessageBox("[系统目录：路图数据采集系统]不存在，无法启动程序！");
    		PubVar.m_DoEvent.DoCommand("加载");
    		//此方法在主线程执行，任务执行的结果作为此方法的参数返回
			//PubVar.m_Map.AyncLoaddata = false;
			//PubVar.m_DoEvent.DoCommand("全屏");
			finish();
    	}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			//lkmap.Tools.Tools.ShowMessageBox("[系统目录：路图数据采集系统]不存在，无法启动程序！");
			//PubVar.m_Map.AyncLoaddata = true;
			//PubVar.m_DoEvent.DoCommand("加载");
			return null;
		}


    }


}