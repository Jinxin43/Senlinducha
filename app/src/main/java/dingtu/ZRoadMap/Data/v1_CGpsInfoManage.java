package dingtu.ZRoadMap.Data;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Map.StaticObject;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;

public class v1_CGpsInfoManage 
{
	/**
	 * ����GPSλ��
	 * @param gpsCoor
	 */
	public void UpdateGpsPosition(Coordinate gpsCoor)
	{
		String ShowFormateStr = "";
		HashMap<String,String> CoorStr = ConvertCoordinateFormate(gpsCoor);
		if (CoorStr.containsKey("X"))ShowFormateStr=CoorStr.get("X")+" ";
		if (CoorStr.containsKey("Y"))ShowFormateStr+=CoorStr.get("Y")+" ";
		if (CoorStr.containsKey("Z"))ShowFormateStr+=CoorStr.get("Z")+" ";
		this._mainheader_pos.setText(ShowFormateStr);
		this.UpdateGpsUseTime();
	}
	
	public static HashMap<String,String> ConvertCoordinateFormate(Coordinate gpsCoor)
	{
		HashMap<String,String> ConvertCoordinate = new HashMap<String,String>();
		
		//����ϵͳ����ȷ����ʾ��ʽ�������ʽ��GPS_1_1
		String FormateCode = PubVar.m_HashMap.GetValueObject("Tag_System_TopXYFormat_Code").Value+"";
		String[] Code = FormateCode.split("_");
		
		if (Code[0].equals("GPS"))
		{
			//0=DD��MM'SS.SSSS"��1=DD��MM.MMMMMM'��2=DD.DDDDDD��
			if (Code[1].equals("0"))
			{
				ConvertCoordinate.put("X", "���ȣ�"+GetDDMMSS(gpsCoor.getX()));
				ConvertCoordinate.put("Y", "γ�ȣ�"+ GetDDMMSS(gpsCoor.getY()));
			}
			if (Code[1].equals("1"))
			{
				ConvertCoordinate.put("X","���ȣ�"+GetDDMM(gpsCoor.getX()));
				ConvertCoordinate.put("Y","γ�ȣ�"+GetDDMM(gpsCoor.getY()));
			}
			if (Code[1].equals("2"))
			{
				ConvertCoordinate.put("X", "���ȣ�"+Tools.ConvertToDigi(gpsCoor.getX()+"", 6)+"��");
				ConvertCoordinate.put("Y", "γ�ȣ�"+Tools.ConvertToDigi(gpsCoor.getY()+"", 6)+"��");
			}
		}
		if (Code[0].equals("PROJECT"))
		{
			Coordinate PrjCoor = StaticObject.soProjectSystem.WGS84ToXY(gpsCoor.getX(), gpsCoor.getY(),gpsCoor.getZ());
			if(PrjCoor != null)
			{
				ConvertCoordinate.put("X","X��"+Tools.ConvertToDigi(PrjCoor.getX()+"", 3));
				ConvertCoordinate.put("Y","Y��"+Tools.ConvertToDigi(PrjCoor.getY()+"", 3));
			}
			
		}
		
		if (Code[2].equals("1"))ConvertCoordinate.put("Z", "H��"+Tools.ConvertToDigi(gpsCoor.getZ()+"",2));
		return ConvertCoordinate;
	}
	
	/**
	 * ����ת��Ϊ�ȷ���
	 * @param DDD
	 * @return
	 */
	private static String GetDDMMSS(double DDD)
	{
		//DD��MM'SS.SSSS��
		int dd = (int)Math.floor(DDD);
		double MM = (DDD-dd)*60;
		int mm = (int)Math.floor(MM);
		
		double SS = (MM-mm)*60;
		String ss = Tools.ConvertToDigi(SS+"", 4);
		return dd+"��"+mm+"'"+ss+"��";
		
	}
	/**
	 * ����ת��Ϊ�ȷַ�
	 * @param DDD
	 * @return
	 */
	private static String GetDDMM(double DDD)
	{
		//DD��MM.MMMMMM"
		int dd = (int)Math.floor(DDD);
		double MM = (DDD-dd)*60;
		return dd+"��"+Tools.ConvertToDigi(MM+"",6)+"'";
		
	}
	
	private long m_BeforeTime = 0;
	/**
	 * ����GPS״̬
	 * @param status
	 */
	public void UpdateGPSStatus(String status)
	{
		this.SetInVisible();
		this._gps_jd.setText("0.0m");
		
		if (status.equals("Music_GPS����"))
		{
			this.m_GpsBeforeOpenTime = (new java.util.Date()).getTime()+"";
			PubVar.m_DoEvent.m_SoundTool.PlaySound(1);
		}
		
		if (status.equals("Music_GPS�����¼�"))
		{
			if (this.m_GpsBeforeOpenTime=="")
			{
				this.m_GpsBeforeOpenTime = (new java.util.Date()).getTime()+"";
				PubVar.m_DoEvent.m_SoundTool.PlaySound(1);
			}
		}
		
		if (status.equals("Music_���Ƕ�ʧ"))
		{
			PubVar.m_DoEvent.m_SoundTool.PlaySound(2);
		}
		if (status.equals("Music_��λ"))
		{
			PubVar.m_DoEvent.m_SoundTool.PlaySound(3);
		}
		if (status.equals("Music_δ��λ"))
		{
			PubVar.m_DoEvent.m_SoundTool.PlaySound(4);
		}
		
		if (status.equals("�ر�"))
		{
			this.m_GpsBeforeOpenTime="";
			this._gps_close.setVisibility(View.VISIBLE);
			this._gpsxh_close.setVisibility(View.VISIBLE);
		}
		if (status.equals("�Ѷ�λ"))
		{
			this._gps_open.setVisibility(View.VISIBLE);
			//������Ϣǿ�����ü���
			int Level = PubVar.m_GPSLocate.GetLevelForAlwaysFix();
			if (Level==0)this._gpsxh_0.setVisibility(View.VISIBLE);
			if (Level==1)this._gpsxh_1.setVisibility(View.VISIBLE);
			if (Level==2)this._gpsxh_2.setVisibility(View.VISIBLE);
			if (Level==3)this._gpsxh_3.setVisibility(View.VISIBLE);
			if (Level==4)this._gpsxh_4.setVisibility(View.VISIBLE);
			String Unit = "m";
			if (PubVar.m_GPSLocate.m_NEMALocate.GetUseNEMA())Unit="P";
			this._gps_jd.setText(PubVar.m_GPSLocate.getAccuracy()+Unit);
		}
		if (status.equals("��λ��"))
		{
			//GPS״̬����ʱ������л�
			long currentTime = System.currentTimeMillis();
			if ((currentTime-m_BeforeTime)>2000)
			{
				this._gps_open.setVisibility(View.VISIBLE);
				this._gps_close.setVisibility(View.GONE);
				this.m_BeforeTime = currentTime;
			} else
			{
				this._gps_open.setVisibility(View.GONE);
				this._gps_close.setVisibility(View.VISIBLE);
			}
			
			//GPS��ϢΪ��״̬
			this._gpsxh_close.setVisibility(View.VISIBLE);
		}
		this.UpdateGpsUseTime();
	}
	
	/**
	 * ״̬����ͼ
	 */
	private TextView _mainheader_pos = null;  //������Ϣ��
	private TextView _gps_jd = null;  		  //GPS������
	
	private ImageView _gps_open = null;       //GPS��״̬ͼ��
	private ImageView _gps_close = null;       //GPS��״̬ͼ��
	
	private ImageView _gpsxh_close = null;    //GPS�źŹ�״̬ͼ��
	private ImageView _gpsxh_0 = null;       //GPS�ź�0��״̬ͼ��
	private ImageView _gpsxh_1 = null;       //GPS�ź�1��״̬ͼ��
	private ImageView _gpsxh_2 = null;       //GPS�ź�2��״̬ͼ��
	private ImageView _gpsxh_3 = null;       //GPS�ź�3��״̬ͼ��
	private ImageView _gpsxh_4 = null;       //GPS�ź�4��״̬ͼ��
	
	private View m_MainHeaderBar = null;
	public void SetHeaderViewBar(View view)
	{
		this.m_MainHeaderBar = view;
		this._mainheader_pos = (TextView)view.findViewById(R.id.ll_coorinfo);
		this._mainheader_pos.setText(PubVar.m_HashMap.GetValueObject("Tag_System_TopXYFormat_Label").Value);
		
		this._gps_jd = (TextView)view.findViewById(R.id.tv_gps_jd);
		
		this._gps_open = (ImageView)view.findViewById(R.id.iv_gpsopen);
		this._gps_close = (ImageView)view.findViewById(R.id.iv_gpsclose);
		this._gpsxh_close = (ImageView)view.findViewById(R.id.iv_gpsxh_close);
		this._gpsxh_0 = (ImageView)view.findViewById(R.id.iv_gpsxh0);
		this._gpsxh_1 = (ImageView)view.findViewById(R.id.iv_gpsxh1);
		this._gpsxh_2 = (ImageView)view.findViewById(R.id.iv_gpsxh2);
		this._gpsxh_3 = (ImageView)view.findViewById(R.id.iv_gpsxh3);
		this._gpsxh_4 = (ImageView)view.findViewById(R.id.iv_gpsxh4);
		this.SetInVisible();
		
		//Ĭ��Ϊ��״̬
		this._gps_close.setVisibility(View.VISIBLE);
		this._gpsxh_close.setVisibility(View.VISIBLE);
		
	}
	
	/**
	 * ���õ�ǰѡ�е�ͼ������
	 * @param LayerName
	 */
	public void SetCurrentLayerName(v1_Layer pLayer)
	{
		TextView tv = (TextView)this.m_MainHeaderBar.findViewById(R.id.ll_layer);
		tv.setText("  ��ǰͼ�㣺"+pLayer.GetLayerAliasName());
		tv.setTag(pLayer.GetLayerID());
	}
	public String GetCurrentLayerId()
	{
		TextView tv = (TextView)this.m_MainHeaderBar.findViewById(R.id.ll_layer);
		if (tv.getTag()!=null)return tv.getTag()+"";
		return "";
	}
	
	/**
	 * ˢ�±�������ʾ
	 */
	public void UpdateScaleBar()
	{
		//1Ӣ�����ľ���
		DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
		double D = dm.densityDpi * PubVar.m_Map.getViewConvert().getZoomScale();
		D = D / 0.0254;
		TextView tv = (TextView)this.m_MainHeaderBar.findViewById(R.id.ll_scalebar);
		tv.setText("������=1��"+Tools.ConvertToDigi(D+"", 0).replace(".", ""));
	}
	
	private String m_GpsBeforeOpenTime = "";
	
	/**
	 * ����GPSʹ��ʱ��
	 */
	public void UpdateGpsUseTime()
	{
		TextView tv = (TextView)this.m_MainHeaderBar.findViewById(R.id.ll_gpstime);
		if (this.m_GpsBeforeOpenTime==""){tv.setText("");return;}
		
		String GpsStr = "GPS�ѿ�����%1$s";
		long JS = (new java.util.Date()).getTime()-Long.parseLong(this.m_GpsBeforeOpenTime);
		int H = 0,M = 0,S = 0;
		if ((JS/1000/60/60)>=1){H = Integer.parseInt((JS/1000/60/60)+"");JS-=H*1000*60*60;}
		if ((JS/1000/60)>=1){M = Integer.parseInt((JS/1000/60)+"");JS-=M*1000*60;}
		if ((JS/1000)>=1)S = Integer.parseInt((JS/1000)+"");
		
		String Result = "";
		if (H!=0)Result+=H+"Сʱ";
		if (M!=0)Result+=M+"����";
		if (S!=0)Result+=S+"��";
		
		GpsStr = String.format(GpsStr, Result);
		tv.setText(GpsStr);
	}
	
	private void SetInVisible()
	{
		_gps_open.setVisibility(View.GONE);
		_gps_close.setVisibility(View.GONE);
		_gpsxh_close.setVisibility(View.GONE);
		_gpsxh_0.setVisibility(View.GONE);
		_gpsxh_1.setVisibility(View.GONE);
		_gpsxh_2.setVisibility(View.GONE);
		_gpsxh_3.setVisibility(View.GONE);
		_gpsxh_4.setVisibility(View.GONE); 
	}
}
