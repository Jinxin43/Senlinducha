package lkmap.ZRoadMap.Compass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lkmap.Tools.Tools;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import android.view.WindowManager;

public class v1_Compass 
{

	public v1_Compass()
	{
        //打开地碰场传感器与加速传感器
        this.m_SensorManager = (SensorManager)PubVar.m_DoEvent.m_Context.getSystemService(Context.SENSOR_SERVICE);
        this.m_Sensor3 = this.m_SensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        this.m_SensorManager.registerListener(sensorListener, this.m_Sensor3, SensorManager.SENSOR_DELAY_GAME);
        
//        Sensor magneticSensor=m_SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        Sensor accelerometerSensor=m_SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        m_SensorManager.registerListener(sensorListener2,magneticSensor,SensorManager.SENSOR_DELAY_GAME);
//        m_SensorManager.registerListener(sensorListener2,accelerometerSensor,SensorManager.SENSOR_DELAY_GAME);
	}
	
	//列表，格式：ID=GUID,View,ICallback
	private List<HashMap<String,Object>> m_BindCompassList = new ArrayList<HashMap<String,Object>>();
	public void AddBindCompass(HashMap<String,Object> bindCompass)
	{
		this.m_BindCompassList.add(bindCompass);
	}
	public void RemoveBindCompass(String id)
	{
		for(HashMap<String,Object> hm:this.m_BindCompassList)
		{
			if (hm.get("ID").equals(id)){this.m_BindCompassList.remove(hm);return;}
		}
	}
	
    //传感 器类
    private SensorManager m_SensorManager = null;
    private Sensor m_Sensor3 = null;
    
	
    private SensorEventListener sensorListener2 = new SensorEventListener(){
    	
    	 float[] accelerometerValues=new float[3];
         float[] magneticValues=new float[3];
         
    	@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		@Override
		public void onSensorChanged(SensorEvent event) 
		{
			if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                accelerometerValues=event.values.clone();
            }else if (event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                magneticValues=event.values.clone();
            }
            float[] R=new float[9];
            float[] values=new float[3];
            SensorManager.getRotationMatrix(R,null,accelerometerValues,magneticValues);
            SensorManager.getOrientation(R,values);
            
            float Tang = values[0];
            
            for(HashMap<String,Object> hm:m_BindCompassList)
    		{
     			if (hm.get("View")!=null)
     			{
                    // 创建旋转动画（反向转过degree度）  
                    RotateAnimation ra = new RotateAnimation(s_Angle, -Tang,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);  
                    ra.setDuration(200);  
        			View view = (View)hm.get("View");
        			if (view!=null)view.startAnimation(ra);  
     			}
    		}
             s_Angle = -Tang;
            
		}
    };
    
    private float s_Angle = 0;
    private SensorEventListener sensorListener = new SensorEventListener(){

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		@Override
		public void onSensorChanged(SensorEvent event) 
		{
            if (event.sensor.getType()==Sensor.TYPE_ORIENTATION)
            {
            	float Tang = event.values[0];
            	int naturalOrientation = ((WindowManager)PubVar.m_DoEvent.m_Context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
                if (naturalOrientation == Surface.ROTATION_0)
                {
                   
                }
                else if (naturalOrientation == Surface.ROTATION_90)
                {
                    Tang += 90;
                }
                else if (naturalOrientation == Surface.ROTATION_180)
                {
                	
                    Tang += 180;
                }
                else if (naturalOrientation == Surface.ROTATION_270)
                {
                	
                    Tang += 270;
                }

                if (Tang > 360) // Check if we have gone too far forward with rotation adjustment, keep the result between 0-360
                {
                	Tang -= 360;
                }
                
         		for(HashMap<String,Object> hm:m_BindCompassList)
        		{
         			if (hm.get("View")!=null)
         			{
                        // 创建旋转动画（反向转过degree度）  
                        RotateAnimation ra = new RotateAnimation(s_Angle, -Tang,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);  
                        ra.setDuration(200);  
	        			View view = (View)hm.get("View");
	        			if (view!=null)view.startAnimation(ra);  
         			}
        		}
                 s_Angle = -Tang; 
                 
                 int Angle = (int)Tang;
                 int range = 25; 
                 
                 String ArrowText = "";
                 // 指向正北  
                 if(Angle > 360 - range && Angle < 360 + range)  ArrowText = Tools.ToLocale("北")+" " + Angle + "°"; 
                   
                 // 指向正东  
                 if(Angle > 90 - range && Angle < 90 + range)  ArrowText = Tools.ToLocale("东") +" " + Angle + "°";  
                   
                 // 指向正南  
                 if(Angle > 180 - range && Angle < 180 + range)  ArrowText = Tools.ToLocale("南") +" " + Angle + "°";  
                   
                 // 指向正西  
                 if(Angle > 270 - range && Angle < 270 + range)  ArrowText = Tools.ToLocale("西") +" " + Angle + "°";  
                   
                 // 指向东北  
                 if(Angle > 45 - range && Angle < 45 + range)ArrowText = Tools.ToLocale("东北") +" " + Angle + "°";  
                   
                 // 指向东南  
                 if(Angle > 135 - range && Angle < 135 + range)ArrowText = Tools.ToLocale("东南") +" " + Angle + "°";   
                   
                 // 指向西南  
                 if(Angle > 225 - range && Angle < 225 + range)  ArrowText = Tools.ToLocale("西南") +" " + Angle + "°"; 
                   
                 // 指向西北  
                 if(Angle > 315 - range && Angle < 315 + range)  ArrowText = Tools.ToLocale("西北") +" " + Angle + "°";
                 if (ArrowText=="")return;
          		for(HashMap<String,Object> hm:m_BindCompassList)
        		{
          			if (hm.get("ICallback")!=null)
          			{
	        			ICallback callBack = (ICallback)hm.get("ICallback");
	        			callBack.OnClick("", ArrowText); 
          			}
        		}
            }

		}};
}
