package lkmap.ZRoadMap.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import android.widget.TextView;
import lkmap.MapControl.v1_MyGlass;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_project_layer_new;

public class v1_SystemSet
{
	private v1_FormTemplate _Dialog = null; 

    public v1_SystemSet()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_systemset);
    	_Dialog.ReSetSize(0.5f,-1f);
    	//���ñ���
    	_Dialog.SetCaption(Tools.ToLocale("ϵͳ����"));
    	
    	//����Ĭ�ϰ�ť
    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("��������")+"  ,��������", _Callback);
    	
    	_Dialog.findViewById(R.id.bt_gps).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.bt_average).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.bt_topxy).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.bt_areaunit).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.bt_Lenthunit).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.cb_zoomglass).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.bt_MapScale).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.cb_watermark).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.cbCenterCross).setOnClickListener(new ViewClick());
    	
		((RadioGroup)_Dialog.findViewById(R.id.rg_glassScale)).setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				HashMap<String,String> value = new HashMap<String,String>();
				value.put("F2", _Dialog.findViewById(checkedId).getTag()+"");
				AddSetItemList("Tag_System_ZoomGlass_Scale",value);
				
				RelativeLayout.LayoutParams npara = new RelativeLayout.LayoutParams(0,0);
				
				if(value.get("F2").equals("С"))
				{
					npara.height = 170;
					npara.width = 170;
				}
				else if(value.get("F2").equals("��"))
				{
					npara.height = 400;
					npara.width =  400;
				}
				else
				{
					npara.height = 300;
					npara.width =  300;
				}

				npara.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				npara.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				PubVar.m_DoEvent.m_GlassView.setLayoutParams(npara);
				
			}});
    	
    	//������֧��
    	int[] ViewID = new int[]{R.id.tvLocaleText1,R.id.tvLocaleText2,R.id.tvLocaleText3,R.id.cb_gpsaverageenable};
    	for(int vid : ViewID)
    	{
    		Tools.ToLocale(_Dialog.findViewById(vid));
    	}
    }
    
    class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		DoCommand(Tag);
    	}
    }
    //��ť�¼�
    private void DoCommand(String StrCommand)
    {
    	if (StrCommand.equals("GPS����"))
    	{
    		v1_SystemSet_GPSSet ssgps = new v1_SystemSet_GPSSet();
    		ssgps.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
		    		HashMap<String,String> value = (HashMap<String,String>)(ExtraStr);
					RefreshShow_GPS(value.get("F2"),value.get("F3"));  //F2=MinTime,F3=MinDistance
					AddSetItemList(Str,value);
				}});
    		ssgps.ShowDialog();
    		return;
    	}
    	
    	if (StrCommand.equals("GPSƽ��ֵ"))
    	{
    		v1_SystemSet_GPSAveragePointSet ssgps = new v1_SystemSet_GPSAveragePointSet();
    		ssgps.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
		    		HashMap<String,String> value = (HashMap<String,String>)(ExtraStr);
		    		RefreshShow_GPS_AveragePoint(Boolean.parseBoolean(value.get("F2")),value.get("F3"),value.get("F4"));  //F2=MinTime,F3=MinDistance
		    		AddSetItemList(Str,value);
				}});
    		ssgps.ShowDialog();
    		return;
    	}
    	
    	if (StrCommand.equals("������������ʽ"))
    	{
    		v1_SystemSet_TopXYFormate ssgps = new v1_SystemSet_TopXYFormate();
    		ssgps.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
		    		HashMap<String,String> value = (HashMap<String,String>)(ExtraStr);
		    		RefreshShow_TopXY(value.get("F3"));  //F3=��ʾʾ��
		    		AddSetItemList(Str,value);
				}});
    		ssgps.ShowDialog();
    		return;
    	}
    	
    	if (StrCommand.equals("�����λ"))
    	{
    		v1_SystemSet_AreaUnit ssgps = new v1_SystemSet_AreaUnit();
    		ssgps.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
		    		HashMap<String,String> value = (HashMap<String,String>)(ExtraStr);
		    		AddSetItemList(Str,value);
		    		((Button)_Dialog.findViewById(R.id.bt_areaunit)).setText(value.get("F2"));
		    		
				}});
    		ssgps.ShowDialog();
    		return;
    	}
    	
    	if (StrCommand.equals("���ȵ�λ"))
    	{
    		SystemSet_LengthUnit sslu = new SystemSet_LengthUnit();
    		sslu.SetCallback(new ICallback()
    				{
		    			@Override
						public void OnClick(String Str, Object ExtraStr) 
						{
				    		HashMap<String,String> value = (HashMap<String,String>)(ExtraStr);
				    		AddSetItemList(Str,value);
				    		((Button)_Dialog.findViewById(R.id.bt_Lenthunit)).setText(value.get("F2"));
				    		
						}
    				});
    		sslu.ShowDialog();
    	}
    	
    	if (StrCommand.equals("ͼ������"))
    	{
    		SystemSet_MapScale mapScale = new SystemSet_MapScale();
    		mapScale.SetCallback(new ICallback()
    				{
		    			@Override
						public void OnClick(String Str, Object ExtraStr) 
						{
				    		HashMap<String,String> value = (HashMap<String,String>)(ExtraStr);
				    		AddSetItemList(Str,value);
				    		((Button)_Dialog.findViewById(R.id.bt_MapScale)).setText(value.get("F2"));
				    		
						}
    				});
    		mapScale.ShowDialog();
    	}
    	
    	
    	if (StrCommand.equals("�Ŵ�"))
    	{
    		HashMap<String,String> value = new HashMap<String,String>();
    		value.put("F2", Tools.GetCheckBoxValueOnID(_Dialog, R.id.cb_zoomglass)+"");
    		AddSetItemList("Tag_System_ZoomGlass",value);
    	}
    	
    	if (StrCommand.equals("��Ƭˮӡ"))
    	{
    		HashMap<String,String> value = new HashMap<String,String>();
    		value.put("F2", Tools.GetCheckBoxValueOnID(_Dialog, R.id.cb_watermark)+"");
    		AddSetItemList("Tag_Photo_WaterMark",value);
    	}
    	
    	if (StrCommand.equals("��Ļ����ʮ��"))
    	{
    		HashMap<String,String> value = new HashMap<String,String>();
    		value.put("F2", Tools.GetCheckBoxValueOnID(_Dialog, R.id.cbCenterCross)+"");
    		AddSetItemList("Tag_ShowCenterCross",value);
    		PubVar.CenterCrossShow = ((CheckBox)_Dialog.findViewById(R.id.cbCenterCross)).isChecked();
    		PubVar.m_MapControl.invalidate();
    	}
    	
    }
    
    //������Ŀ�б�
    private List<SystemSetItem> m_SystemSetItemList = new ArrayList<SystemSetItem>();
    private void AddSetItemList(String ItemName,HashMap<String,String> ItemValueList)
    {
    	boolean haveItem = false;
    	for(SystemSetItem ssi:this.m_SystemSetItemList)
    	{
    		if (ssi.ItemName.equals(ItemName))
    		{
    			ssi.ValueList = ItemValueList;haveItem=true;
    		}
    	}
    	if (!haveItem)
    	{
			SystemSetItem SSI = new SystemSetItem();
			SSI.ItemName = ItemName;SSI.ValueList = ItemValueList;
			this.m_SystemSetItemList.add(SSI);
    	}
    }
    
    private ICallback _Callback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("��������"))
			{
				for(SystemSetItem item:m_SystemSetItemList)
				{
		    		if (!PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().SaveUserPara(item.ItemName, item.ValueList))
		    		{
		    			Tools.ShowMessageBox(_Dialog.getContext(),"���������"+item.ItemName+"��ʧ�ܣ�");
		    		}
				}
				
				//���¶�ȡ���ò���
				PubVar.m_DoEvent.m_UserConfigDB.LoadSystemConfig();
				_Dialog.dismiss();
			}
		}};
    
    //���ز����б���
    private void LoadConfigInfo()
    {
    	//1���ڴ˿��Ʋɼ���ʱ��������С���룬��ϵͳ���� Tag_System_GPSMinTime,Tag_System_GPSMinDistance ����
    	String gpsMinTime = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime").Value+"";
    	String gpsMinDis = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance").Value+"";
    	this.RefreshShow_GPS(gpsMinTime, gpsMinDis);
    	
    	//2������������ʾ��ʽ������ ��Code,Label����
    	String TopXYFormat = PubVar.m_HashMap.GetValueObject("Tag_System_TopXYFormat_Label").Value+"";
    	this.RefreshShow_TopXY(TopXYFormat);
    	
    	//3��GPS���ݵ�ƽ��ֵ
    	boolean avgEnable = Boolean.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_AveragePointEnable").Value+"");
    	String PointCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount").Value+"";
    	String VertexCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_VertexCount").Value+"";
    	this.RefreshShow_GPS_AveragePoint(avgEnable,PointCount,VertexCount);
    	
    	//4�������λ
    	String areaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit").Value+"";
    	((Button)_Dialog.findViewById(R.id.bt_areaunit)).setText(areaUnit);
    	
    	//���ȵ�λ
    	String lenthUnit = PubVar.m_HashMap.GetValueObject("Tag_System_LengthUnit").Value+"";
    	((Button)_Dialog.findViewById(R.id.bt_Lenthunit)).setText(lenthUnit);
    	
    	//5���Ŵ󾵣�Ҳ���Ǿ�ȷ���ݲ���ģʽ
    	String ZoomGlassMode = PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value+"";
    	Tools.SetCheckValueOnID(_Dialog, R.id.cb_zoomglass, Boolean.parseBoolean(ZoomGlassMode));
    	
    	RadioGroup rg = (RadioGroup)_Dialog.findViewById(R.id.rg_glassScale);
    	if(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass_Scale").Value.equals("��"))
    	{
    		rg.check(R.id.rbGlassScaleBig);
    	}
    	else if(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass_Scale").Value.equals("С"))
    	{
    		rg.check(R.id.rbGlassScaleSmall);
    	}
    	else
    	{
    		rg.check(R.id.rbGlassScaleMiddle);
    	}
    	
    	//6��ͼ������
    	String mapScale= PubVar.m_HashMap.GetValueObject("Tag_System_MapScale").Value+"";
    	((Button)_Dialog.findViewById(R.id.bt_MapScale)).setText(mapScale);
    	
    	//5��ͼƬˮӡ
    	String addWaterMark = PubVar.m_HashMap.GetValueObject("Tag_Photo_WaterMark").Value+"";
    	Tools.SetCheckValueOnID(_Dialog, R.id.cb_watermark, Boolean.parseBoolean(ZoomGlassMode));
    	
    	
    	String showCenterCross = PubVar.m_HashMap.GetValueObject("Tag_ShowCenterCross",true).Value;
    	if(showCenterCross.length()<1)
    	{
    		Tools.SetCheckValueOnID(_Dialog, R.id.cbCenterCross, false);
    	}
    	else{
    		Tools.SetCheckValueOnID(_Dialog, R.id.cbCenterCross, Boolean.parseBoolean(showCenterCross));
    	}
    	
    	
    }
    
    /**
     * ˢ��GPS���ݵ�ƽ��ֵ
     * @param avgEnable
     * @param PointCount
     * @param VertexCount
     */
    private void RefreshShow_GPS_AveragePoint(boolean avgEnable,String PointCount,String VertexCount)
    {
    	((CheckBox)_Dialog.findViewById(R.id.cb_gpsaverageenable)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				_Dialog.findViewById(R.id.bt_average).setEnabled(((CheckBox)arg0).isChecked());
				HashMap<String,String> avgPoint = new HashMap<String,String>();
				avgPoint.put("F2", ((CheckBox)arg0).isChecked()+"");
				avgPoint.put("F3", PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount").Value+"");
				avgPoint.put("F4", PubVar.m_HashMap.GetValueObject("Tag_System_GPS_VertexCount").Value+"");
				AddSetItemList("Tag_System_GPS_AveragePoint",avgPoint);
			}});
    	Tools.SetCheckValueOnID(_Dialog, R.id.cb_gpsaverageenable, avgEnable);
    	
    	String gpsText = Tools.ToLocale(" ��")+"��<font color=\"#0000ff\">%1$s"+Tools.ToLocale("��")+"</font>    "+Tools.ToLocale("�ڵ�")+"��<font color=\"#0000ff\">%2$s"+Tools.ToLocale("��")+"</font>";
    	gpsText = String.format(gpsText, PointCount,VertexCount);
    	((TextView)_Dialog.findViewById(R.id.bt_average)).setText(Html.fromHtml(gpsText));
    	_Dialog.findViewById(R.id.bt_average).setEnabled(avgEnable);
    }
    
    /*
     * 
     * ˢ��GPS����ʱ�䣬��������
     */
    private void RefreshShow_GPS(String MinTime,String MinDistance)
    {
    	String gpsText = Tools.ToLocale(" ����ʱ��")+"��<font color=\"#0000ff\">%1$s"+Tools.ToLocale("��")+"</font>    "+Tools.ToLocale("���")+"��<font color=\"#0000ff\">%2$s"+Tools.ToLocale("��")+"</font>";
    	gpsText = String.format(gpsText, MinTime,MinDistance);
    	((TextView)_Dialog.findViewById(R.id.bt_gps)).setText(Html.fromHtml(gpsText));
    }
    /*
     * 
     * ˢ�¶���������ʾ���ĸ�ʽ
     */
    private void RefreshShow_TopXY(String TopXYFormat)
    {
    	((TextView)_Dialog.findViewById(R.id.bt_topxy)).setText(TopXYFormat);
    }
    
    public void ShowDialog()
    {
    	this.LoadConfigInfo();
    	_Dialog.show();
    }
    
    public class SystemSetItem
    {
    	public String ItemName = "";
    	public HashMap<String,String> ValueList = null;
    }

}
