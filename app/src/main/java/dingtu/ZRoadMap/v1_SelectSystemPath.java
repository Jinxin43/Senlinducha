package dingtu.ZRoadMap;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Project_Layer_BKMap_MapFileAdapter;

public class v1_SelectSystemPath
{
	private Context m_Context = null;
    public v1_SelectSystemPath(Context C)
    {
    	this.m_Context = C;
    }
    
	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb)
	{
		this.m_Callback = cb;
	}
	
	private List<HashMap<String,Object>> m_SDCardList = null;
    /**
     * 加载存储卡信息
     */
    private void LoadSDCardInfo()
    {
    	TextView tvName = (TextView)this.m_View.findViewById(R.id.tvSystemName);
    	tvName.setText("请选择【"+PubVar.m_SysDictionaryName+"】工作目录位置");
    	this.m_SDCardList = Tools.GetAllSDCardInfoList(this.m_Context);
    	
		//刷新列表
    	v1_Project_Layer_BKMap_MapFileAdapter adapter = new v1_Project_Layer_BKMap_MapFileAdapter(this.m_Context,this.m_SDCardList, 
											       R.layout.v1_bk_selectsystempath, 
											       new String[] {"SDPath", "AllSize", "FreeSize","Status"}, 
											       new int[] {R.id.tvPath, R.id.tvAllSize, R.id.tvFreeSize,R.id.tvStatus});  
		(((ListView)this.m_View.findViewById(R.id.listView1))).setAdapter(adapter);
	
		(((ListView)this.m_View.findViewById(R.id.listView1))).setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				final String SDPath = m_SDCardList.get(arg2).get("SDPath")+"";
				//验证是否可创建目录
				if (!(new File(SDPath)).canWrite())
				{
					Tools.ShowMessageBox(m_Context, "创建工作目录失败！\n\n原因：目录为只读状态！");return;
				}
				Tools.ShowYesNoMessage(m_View.getContext(),"是否确定在以下位置创建工作目录？\r\n"+SDPath, new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (Str.equals("YES"))
						{
							//创建主目录
							String SystemPath = SDPath+"/"+PubVar.m_SysDictionaryName;

							if (new File(SystemPath).mkdirs())
							{
								if (m_Callback!=null)m_Callback.OnClick("工作目录", SDPath);
								m_AlertDialog.dismiss();
							}
							else
							{
								if (m_Callback!=null)m_Callback.OnClick("退出", "");
							}
							
							m_AlertDialog.dismiss();
						}
					}});
			}});
    }
    
    private AlertDialog m_AlertDialog = null;
    private View m_View = null;
    public void ShowDialog()
    {
    	LayoutInflater inflater = (LayoutInflater)this.m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.m_View=inflater.inflate(R.layout.v1_selectsystempath, null, false); 
        
        AlertDialog.Builder ab = new AlertDialog.Builder(this.m_Context);
        ab.setTitle("工作目录设置");
    	ab.setIcon(R.drawable.v1_messageinfo);
    	ab.setView(this.m_View);
    	ab.setCancelable(false);

    	((Button)this.m_View.findViewById(R.id.btquit)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (m_Callback!=null)m_Callback.OnClick("退出", "");
			}});
		m_AlertDialog = ab.create();
    	m_AlertDialog.show();

		this.LoadSDCardInfo();

    }
}
