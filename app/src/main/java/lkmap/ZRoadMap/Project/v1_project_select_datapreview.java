package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import dingtu.ZRoadMap.HashValueObject;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Enum.lkCoorTransMethod;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;

public class v1_project_select_datapreview
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_select_datapreview()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_select_datapreview);
    	_Dialog.ReSetSize(1f,0.96f);
    	_Dialog.SetCaption(Tools.ToLocale("数据预览图"));
    	
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_project_open+","+Tools.ToLocale("打开工程")+" ,打开工程", pCallback);
    }
    
    
    //按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("打开工程"))
			{
		    	if (m_Callback!=null)m_Callback.OnClick("打开工程", m_PrjInfo.get("D2"));
		    	_Dialog.dismiss();
			}
		}};


	//打开工程后的回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	
	private HashMap<String,Object> m_PrjInfo = null;
	private String m_DataPreviewImageName = null;
	public void SetDataPreviewInfo(HashMap<String,Object> dataInfo,String dataPreviewImageName)
	{
		this.m_PrjInfo = dataInfo;
		this.m_DataPreviewImageName = dataPreviewImageName;
	}
	
    /**
     * 加载工程数据预览信息
     */
    private void LoadDataPreviewInfo()
    {
    	ImageView iv = (ImageView)_Dialog.findViewById(R.id.iv_datapreview);
    	iv.setImageBitmap(BitmapFactory.decodeFile(this.m_DataPreviewImageName));
    	((TextView)_Dialog.findViewById(R.id.tvLocaleText)).setText("【"+this.m_PrjInfo.get("D2")+"】"+Tools.ToLocale("数据预览图"));
    	iv.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				_Dialog.dismiss();
			}});
    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadDataPreviewInfo();}}
    	);
    	_Dialog.show();
    }
}
