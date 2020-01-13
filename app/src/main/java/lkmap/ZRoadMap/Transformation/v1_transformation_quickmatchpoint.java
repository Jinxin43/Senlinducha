package lkmap.ZRoadMap.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.UserConfigDB;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_Data_Gps_AveragePoint;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Coordinate;
import lkmap.CoordinateSystem.CoorParamTools;
import lkmap.Tools.Tools;

public class v1_transformation_quickmatchpoint {
	
	private v1_FormTemplate _Dialog = null; 
	
	public v1_transformation_quickmatchpoint()
	{
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_transformation_quickmatchpoint);
    	_Dialog.SetCaption("У׼��");
    	_Dialog.ReSetSize(0.4f,-1f);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+" ,ȷ��", pCallback);

    	_Dialog.findViewById(R.id.btAddGPSPoint).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("����GPS���Ƶ�", "");}});

//    	_Dialog.findViewById(R.id.btAddLoadPoint).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {pCallback.OnClick("��ȡ", "");}});
    	
    	//GPS�ɼ�����
    	int GpsPointCount = Integer.parseInt(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount").Value);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.etGPSPoints, GpsPointCount+"");
    	((CheckBox)_Dialog.findViewById(R.id.cbShowCenterCross)).setChecked(PubVar.CenterCrossShow);
    	
    	((CheckBox)_Dialog.findViewById(R.id.cbShowCenterCross)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PubVar.CenterCrossShow = isChecked;
				PubVar.m_MapControl.invalidate();
				
			}
		});
    	
    	if(PubVar.CenterCrossShow)
		{
			DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
			int heightPixel = dm.heightPixels/2;
			int weightPixel = dm.widthPixels/2;
			
			Coordinate centerCoord = PubVar.m_Map.getViewConvert().ScreenToMap(weightPixel,heightPixel);
			if(centerCoord != null)
			{
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_X1, centerCoord.getX()+"");
				Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y1, centerCoord.getY()+"");
			}else
			{
				Tools.ShowMessageBox("�޷���ȡ��Ļ���ĵ㣡");
			}
		}
		else
		{
			Tools.ShowMessageBox("�뿪ʼ��Ļ���ĵ�ʮ�֣�Ȼ�󽫵�ǰ��վλ���ƶ�����Ļ���ĵ㣡");
		}
    	
	}
	
	//�򿪹��̺�Ļص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		
	//��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("ȷ��"))
			{
				if (m_Callback!=null)
				{
					if (m_MatchPointType.equals("����У��"))
					{
						final String X1 = Tools.GetTextValueOnID(_Dialog, R.id.et_X2);
						final String Y1 = Tools.GetTextValueOnID(_Dialog, R.id.et_Y2);
						final String X2 = Tools.GetTextValueOnID(_Dialog, R.id.et_X1);
						final String Y2 = Tools.GetTextValueOnID(_Dialog, R.id.et_Y1);
						
						//��������Ƿ��������
						if (!(Tools.IsDouble(X1) && Tools.IsDouble(Y1) && Tools.IsDouble(X2) && Tools.IsDouble(Y2))){Tools.ShowMessageBox("�����ʽ����ȷ��");return;}
						
						//�ж����ֵ��Ŀ��-Դ��
						List<Coordinate> matchPointList = new ArrayList<Coordinate>();
						matchPointList.add(new Coordinate(Double.parseDouble(X1),Double.parseDouble(Y1)));
						matchPointList.add(new Coordinate(Double.parseDouble(X2),Double.parseDouble(Y2)));
						HashMap<String,Object> fourParam = CoorParamTools.CalFourPara(matchPointList);
						
						final String dx = Tools.ConvertToDigi(fourParam.get("DX")+"",3);
						final String dy = Tools.ConvertToDigi(fourParam.get("DY")+"",3);
						Tools.ShowYesNoMessage(_Dialog.getContext(),"У׼�����ֵ���£��Ƿ�ȷ��ת��������\nXƽ��(��)��"+dx+"\nYƽ��(��)��"+dy, new ICallback(){
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								if (m_EditCheckPoint==null)
								m_EditCheckPoint = new HashMap<String,Object>();
								
								m_EditCheckPoint.put("DX",dx);
								m_EditCheckPoint.put("DY",dy);
								m_EditCheckPoint.put("R",0);
								m_EditCheckPoint.put("K",1);
								m_EditCheckPoint.put("X1", Tools.ConvertToDouble(X1)+"");
								m_EditCheckPoint.put("Y1", Tools.ConvertToDouble(Y1)+"");
								m_EditCheckPoint.put("X2", Tools.ConvertToDouble(X2)+"");
								m_EditCheckPoint.put("Y2", Tools.ConvertToDouble(Y2)+"");
								m_Callback.OnClick("У׼��", m_EditCheckPoint);
								try
								{
									HashMap<String, Object> calcPoints = new HashMap<String, Object>();
									calcPoints.put("X1", X1);
									calcPoints.put("Y1", Y1);
									calcPoints.put("X2", X2);
									calcPoints.put("Y2", Y2);
									UserConfigDB db = new UserConfigDB();
									db.addTransformPoint(calcPoints);
								}
								catch (Exception e) {
									// TODO: handle exception
								}
								_Dialog.dismiss();
							}});
					}
					
					
				}
				
			}
			
			if (Str.equals("����GPS���Ƶ�"))
			{
				if (!lkmap.Tools.Tools.ReadyGPS(true)) return;
	    		v1_Data_Gps_AveragePoint dsap = new v1_Data_Gps_AveragePoint();
	    		dsap.SetGpsPointCount(Integer.parseInt(Tools.GetTextValueOnID(_Dialog, R.id.etGPSPoints)));
	    		dsap.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						Coordinate Coor = (Coordinate)ExtraStr;
						Tools.SetTextViewValueOnID(_Dialog, R.id.et_X2, Coor.getX()+"");
						Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y2, Coor.getY()+"");
					}});
	    		dsap.ShowDialog();
			}
			
			if (Str.equals("��ȡ"))
			{
				if(PubVar.CenterCrossShow)
				{
					DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
					int heightPixel = dm.heightPixels/2;
					int weightPixel = dm.widthPixels/2;
					
					Coordinate centerCoord = PubVar.m_Map.getViewConvert().ScreenToMap(weightPixel,heightPixel);
					if(centerCoord != null)
					{
						Tools.SetTextViewValueOnID(_Dialog, R.id.et_X1, centerCoord.getX()+"");
						Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y1, centerCoord.getY()+"");
					}else
					{
						Tools.ShowMessageBox("�޷���ȡ��Ļ���ĵ㣡");
					}
				}
				else
				{
					Tools.ShowMessageBox("�뿪ʼ��Ļ���ĵ�ʮ�֣�Ȼ�󽫵�ǰ��վλ���ƶ�����Ļ���ĵ㣡");
				}
				
			}
		}};
		
		//��ȡ�������
		private String m_MatchPointType = "��";
		public void SetMatchPointType(String matchPointType)
		{
			this.m_MatchPointType = matchPointType;
		}
		
		//��ǰ�ɱ༭��У׼��
		private HashMap<String,Object> m_EditCheckPoint = null;
		public void SetEditCheckPoint(HashMap<String,Object> checkPoint)
		{
			this.m_EditCheckPoint = checkPoint;
		}
		
		//�Ƿ����ʹ��GPS��ȡ�����
		private boolean m_UseGPS = true;
		public void SetUseGPS(boolean useGPS)
		{
			this.m_UseGPS = useGPS;
			_Dialog.findViewById(R.id.btAddGPSPoint).setEnabled(this.m_UseGPS);
		}
		
		public void ShowDialog()
	    {
	    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
	    	_Dialog.setOnShowListener(new OnShowListener(){
				@Override
				public void onShow(DialogInterface dialog) 
				{
					if (m_EditCheckPoint!=null)
						
					{
						if (m_MatchPointType.equals("�����Ĳ���"))
						{
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_X1, m_EditCheckPoint.get("X1")+"");
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y1, m_EditCheckPoint.get("Y1")+"");
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_X2, m_EditCheckPoint.get("X2")+"");
							Tools.SetTextViewValueOnID(_Dialog, R.id.et_Y2, m_EditCheckPoint.get("Y2")+"");
							Tools.SetTextViewValueOnID(_Dialog, R.id.tvCaption, "У׼����Ϣ�����="+m_EditCheckPoint.get("DH")+"��");
						}
					}}}
	    	);
	    	_Dialog.show();
	    }


}
