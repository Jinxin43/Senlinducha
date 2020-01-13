package dingtu.ZRoadMap.Data;

import java.io.File;

import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import dingtu.ZRoadMap.AndroidMap;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Pad_Graffiti;

public class v1_Data_ClipScreen extends Activity 
{

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState); 
        setRequestedOrientation(AndroidMap.m_SCREEN_ORIENTATION);
        setContentView(R.layout.v1_data_clipscreen);
        
        this.m_PadGraffiti = (v1_Pad_Graffiti)this.findViewById(R.id.iv_datapreview2);
        this.findViewById(R.id.bt_erase).setOnClickListener(m_OnClickListener);
        this.findViewById(R.id.bt_pen).setOnClickListener(m_OnClickListener);
        this.findViewById(R.id.bt_undo).setOnClickListener(m_OnClickListener);
        this.findViewById(R.id.bt_redo).setOnClickListener(m_OnClickListener);
        this.findViewById(R.id.bt_save).setOnClickListener(m_OnClickListener);
        this.findViewById(R.id.bt_quit).setOnClickListener(m_OnClickListener);
        ((Button)this.findViewById(R.id.bt_quit)).setText(Tools.ToLocale("退出")+" ");
        ((Button)this.findViewById(R.id.bt_save)).setText(Tools.ToLocale("保存")+" ");
        Tools.ToLocale(this.findViewById(R.id.tvLocaleText));
    }
    
    private v1_Pad_Graffiti m_PadGraffiti = null;
    
    private OnClickListener m_OnClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) 
		{
			if (v.getTag().equals("相皮擦"))
			{
				m_PadGraffiti.m_DrawMode=2;
			}
			if (v.getTag().equals("画笔"))
			{
				m_PadGraffiti.m_DrawMode=1;
			}
			if (v.getTag().equals("回退"))
			{
				m_PadGraffiti.Undo();
			}
			if (v.getTag().equals("重做"))
			{
				m_PadGraffiti.Redo();
			}
			if (v.getTag().equals("保存"))
			{
				SaveClipScreenToFile();
			}
			if (v.getTag().equals("退出"))
			{
				finish();
			}
		}};
		
		private String m_BeforePngName = "";
		private void SaveClipScreenToFile()
		{
			v1_Data_ClipScreen_Save dss = new v1_Data_ClipScreen_Save(this);
			dss.SetDefaultPngName(m_BeforePngName);
			dss.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
					String pngPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/数据截图";
					String pngName = pngPath+"/"+ExtraStr.toString()+".png";
					if (!Tools.ExistFile(pngPath))(new File(pngPath)).mkdirs();
					if (m_PadGraffiti.SaveTo(pngName))
					{
						m_BeforePngName = ExtraStr.toString();
						Tools.ShowToast(v1_Data_ClipScreen.this, "成功保存截图！\n存储："+pngName);
					} else
					{
						Tools.ShowMessageBox(v1_Data_ClipScreen.this, "无法保存截图，注意检查截图文件名称！");return;
					}
				}});
			dss.ShowDialog();
		}
    
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) 
	    {
	        if(keyCode == KeyEvent.KEYCODE_BACK)
	        {
	        	//lkmap.Tools.Tools.ShowToast(this, "请点击【工具】->【退出】关闭程序！");
	        	return false;
//	        	moveTaskToBack(false);  //表示按回退钮后保持现有状态，不回退
//	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }

}
