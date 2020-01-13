package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.AdapterView.OnItemClickListener;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.Project_Web;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkFieldType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_LayerList_Adpter;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;
import lkmap.ZRoadMap.Project.v1_project_layer;

public class v1_CGps_Data_InputCoor_Input
{

	private Boolean isCoordinate = true;
	private v1_FormTemplate _Dialog = null; 
    public v1_CGps_Data_InputCoor_Input()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_cgps_data_inputcoor_input);
//    	_Dialog.SetOtherView(R.layout.inputcoor_input);
    	_Dialog.ReSetSize(0.4f, 0.4f);
    	_Dialog.SetCaption("输入坐标");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
//    	((CheckBox)_Dialog.findViewById(R.id.cbIsDuFenMiao)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				
//				isCoordinate = !isChecked;
//				if(isChecked)
//				{
//					_Dialog.findViewById(R.id.layJWD).setVisibility(View.VISIBLE);
//					_Dialog.findViewById(R.id.layCoord).setVisibility(View.GONE);
//				}
//				else
//				{
//					_Dialog.findViewById(R.id.layJWD).setVisibility(View.GONE);
//					_Dialog.findViewById(R.id.layCoord).setVisibility(View.VISIBLE);
//				}
//			}
//		});
    }

    //上部按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		
	    		_MyAveragePoint.Cancel();
	    		if(isCoordinate)
	    		{
	    			if (m_EditCoorItem!=null)
		    		{
		    			m_EditCoorItem.put("XCoor", Tools.GetTextValueOnID(_Dialog, R.id.et_X));
		    			m_EditCoorItem.put("YCoor", Tools.GetTextValueOnID(_Dialog, R.id.et_Y));
		    			if (_returnCallback!=null)_returnCallback.OnClick("", "");
		    			_Dialog.dismiss();
		    		}
		    		if(Tools.GetTextValueOnID(_Dialog, R.id.et_X).isEmpty()||Tools.GetTextValueOnID(_Dialog, R.id.et_Y).isEmpty())
		    		{
		    			_Dialog.dismiss();
		    			return;
		    		}
		    		Coordinate Coor = new Coordinate();
		    		Coor.setX(Double.parseDouble(Tools.GetTextValueOnID(_Dialog, R.id.et_X)));
		    		Coor.setY(Double.parseDouble(Tools.GetTextValueOnID(_Dialog, R.id.et_Y)));
		    		if (_returnCallback!=null)_returnCallback.OnClick("", Coor);
		    		_Dialog.dismiss();
	    		}
	    		else
	    		{
	    			
	    			try
	    			{
	    				int  strJdu =  Integer.parseInt(Tools.GetTextValueOnID(_Dialog, R.id.et_JDu));
	    				int  strJfen = Integer.parseInt(Tools.GetTextValueOnID(_Dialog, R.id.et_JFen));
		    			double  strJmiao = Double.parseDouble(Tools.GetTextValueOnID(_Dialog, R.id.et_JMiao));
		    			int	strWdu = Integer.parseInt(Tools.GetTextValueOnID(_Dialog,R.id.et_WDu));
		    			int  strWfen = Integer.parseInt(Tools.GetTextValueOnID(_Dialog, R.id.et_WFen));
		    			double  strWmiao = Double.parseDouble(Tools.GetTextValueOnID(_Dialog, R.id.et_WMiao));
		    			
		    			double CoorX = Tools.GetCoorFromDDMMSS(strJdu, strJfen, strJmiao);
		    			double CoorY = Tools.GetCoorFromDDMMSS(strWdu, strWfen, strWmiao);
		    			CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
		    			if(CS.GetName().equals("WGS84"))
		    			{
		    				
		    			}
		    			else
		    			{
		    				
		    			}
	    				
	    			}
	    			catch(Exception ex)
	    			{
	    				Tools.ShowMessageBox("请分别填写经纬度的度分秒，秒如果没有请填写0。");
	    			}
	    		}
	    		
	    	}
		}};
		
		
	private HashMap<String,Object> m_EditCoorItem = null;
	public void SetEditItem(HashMap<String,Object> editItem)
	{
		this.m_EditCoorItem = editItem;
		Tools.SetTextViewValueOnID(_Dialog, R.id.et_X, this.m_EditCoorItem.get("XCoor")+"");
		Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y, this.m_EditCoorItem.get("YCoor")+"");
	}
	
    private ICallback _returnCallback = null;
    public void SetCallback(ICallback cb){this._returnCallback=cb;}


    //根据是否采集平均点选择，生成界面
    private v1_CGps_AveragePoint _MyAveragePoint = null;
    private void SetShowGpsAveragePointOption()
    {
    	_Dialog.findViewById(R.id.bt_restart).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_pointcount);
				RadioGroup group = (RadioGroup)_Dialog.findViewById(R.id.rg_countSelect);
				RadioButton rb = (RadioButton)_Dialog.findViewById(group.getCheckedRadioButtonId());
				if(rb != null)
				{
					_MyAveragePoint.Start(Integer.parseInt(rb.getText()+""));
				}
				else
				{
					Tools.ShowMessageBox("没有选择采样次数！");
				}
				
			}});
    	
    	//初始化控件
    	v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_pointcount);
    	esd1.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
    	esd1.SetSelectItemList(Tools.StrArrayToList(new String[]{"3","4","5","10","20","30"}));
    	esd1.getEditTextView().setEnabled(true);
    	
    	//默认值
    	int gpsPointCount = 1;
		String PointCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount").Value+"";
		esd1.setText(PointCount);
		if (Tools.IsInteger(PointCount))gpsPointCount = Integer.parseInt(PointCount);

    	this._MyAveragePoint = new v1_CGps_AveragePoint();
    	//this._MyAveragePoint.Start(gpsPointCount);
    	this._MyAveragePoint.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				if (Str.equals("采集状态"))
				{
					Tools.SetTextViewValueOnID(_Dialog, R.id.et_gpspointcount, (ExtraStr+"").split(",")[0]+"  ");
				}
				if (Str.equals("采集结果"))
				{
					Coordinate Coor = (Coordinate)ExtraStr;
					Tools.SetTextViewValueOnID(_Dialog, R.id.et_X, Coor.getX()+"");
					Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y, Coor.getY()+"");
				}
			}});

    }
    
    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {SetShowGpsAveragePointOption();}});
    	_Dialog.show();
    }
}
