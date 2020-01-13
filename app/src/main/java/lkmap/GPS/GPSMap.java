package lkmap.GPS;

import com.dingtu.DTGIS.WPZFJC.WeiPianZhiFa_GPSMeasure;
import com.dingtu.senlinducha.R;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsInfoManage;
import dingtu.ZRoadMap.Data.v1_CGpsLine;
import dingtu.ZRoadMap.Data.v1_CGpsPoly;
import lkmap.Cargeometry.Coordinate;
import lkmap.Map.StaticObject;
import lkmap.MapControl.IOnPaint;
import lkmap.MapControl.MapControl;
import lkmap.Tools.Tools;

public class GPSMap implements IOnPaint {

	private MapControl m_MapControl = null;

	public GPSMap(MapControl mapControl) {
		this.m_MapControl = mapControl;
		this.m_MapControl._GPSMapPaint = this;
	}

	// �ɼ�GPS�ߵ�ʵ��
	private v1_CGpsLine m_CGpsLine = null;

	public void SetGpsLine(v1_CGpsLine gpsLine) {
		this.m_CGpsLine = gpsLine;
	}

	// �ɼ�GPS���ʵ��
	private v1_CGpsPoly m_CGpsPoly = null;

	public void SetGpsPoly(v1_CGpsPoly gpsPoly) {
		this.m_CGpsPoly = gpsPoly;
	}

	// GPS�����������
	private WeiPianZhiFa_GPSMeasure m_GpsMeasure = null;

	public void SetGpsMeasure(WeiPianZhiFa_GPSMeasure gpsMeasure) {
		this.m_GpsMeasure = gpsMeasure;
	}

	// GPS��Ϣ������
	private v1_CGpsInfoManage m_CGpsInfoManage = null;

	public void SetGpsInfoManage(v1_CGpsInfoManage gpsinfo) {
		this.m_CGpsInfoManage = gpsinfo;
	}

	public v1_CGpsInfoManage GetGpsInfoManage() {
		return this.m_CGpsInfoManage;
	}

	/**
	 * GPSλ�ö�ʱ����
	 */
	private LocationEx m_LocationEx = null;

	public void UpdateGPSStatus(LocationEx locationEx) {
		if (locationEx != null) {
			this.m_LocationEx = locationEx;
			// �ֲɼ������GPSλ����Ϣ
			if (PubVar.m_GPSLocate.AlwaysFix()) {
				Coordinate newCoor = StaticObject.soProjectSystem.WGS84ToXY(locationEx.GetGpsLongitude(),
						locationEx.GetGpsLatitude(), locationEx.GetGpsAltitude());
				if (newCoor == null) {
					return;
				}

				if (this.m_CGpsLine != null)
					this.m_CGpsLine.UpdateGpsPosition(newCoor);
				if (this.m_CGpsPoly != null)
					this.m_CGpsPoly.getGPSLine().UpdateGpsPosition(newCoor);
				if (this.m_CGpsInfoManage != null)//����״̬��
					this.m_CGpsInfoManage.UpdateGpsPosition(StaticObject.soProjectSystem.XYToWGS84(newCoor));

				if (this.m_GpsMeasure != null) {

					this.m_GpsMeasure.UpdateGpsPosition(newCoor);

				}
			}
		}

		if (this.m_CGpsInfoManage != null) {
			if (!PubVar.m_GPSLocate.GPS_OpenClose)
				this.m_CGpsInfoManage.UpdateGPSStatus("�ر�");
			else {
				if (PubVar.m_GPSLocate.AlwaysFix())
					this.m_CGpsInfoManage.UpdateGPSStatus("�Ѷ�λ");
				else
					this.m_CGpsInfoManage.UpdateGPSStatus("��λ��");
			}
		}

		this.m_MapControl.invalidate();
	}

	// ��Ҫˢ���������ϵ�GPS״̬��Ϣ
	private Paint _TextFont = null; // ���ֵ�����

	private Paint GetTextFont() {
		if (this._TextFont == null) {
			this._TextFont = new Paint();
			this._TextFont.setAntiAlias(true);
			this._TextFont.setTextSize(Tools.SPToPix(20));
			this._TextFont.setColor(Color.BLUE);
			Typeface TF = Typeface.create("����", Typeface.BOLD);
			_TextFont.setTypeface(TF);
			_TextFont.setShadowLayer(20, 0, 0, Color.WHITE);
		}
		return this._TextFont;
	}

	private Bitmap _GpsPointICON = null; // GPS��ǰλ�õ���ʾͼ��

	@Override
	public void OnPaint(Canvas canvas) {
		// 1-�˴�Ϊ����ʱ����ʾ����״̬
		if (PubVar.m_Map == null)
			return;
		if (PubVar.m_Map.getInvalidMap())
			return;

		// Ϊ�ɼ���ˢ����ʾ
		if (this.m_CGpsLine != null)
			this.m_CGpsLine.OnPaint(canvas);
		if (this.m_CGpsPoly != null)
			this.m_CGpsPoly.OnPaint(canvas);

		// ���GPS�Ѿ��ر��˳�
		if (!PubVar.m_GPSLocate.GPS_OpenClose)
			return;

		boolean GPSAlwaysFix = PubVar.m_GPSLocate.AlwaysFix(); // GPS�Ƿ��Ѿ���λ
		if (GPSAlwaysFix) {
			// HashValueObject hvObject =
			// PubVar.m_HashMap.GetValueObject("GPS_�ٶ�",true);
			// float _SValue=m_Location.getSpeed()*3.6f; //meters/s->km/h
			// DecimalFormat df = new DecimalFormat("#.0");
			// String _SpeedValue = df.format(_SValue);
			// hvObject.ShowOnMap=true;
			// hvObject.LabelText = "GPSʱ�䣺"+PubVar.m_GPSLocate.getGPSDate();
			PubVar.SaveDataDate = PubVar.m_GPSLocate.getGPSDate();

			// �ڴ��ж������û���ʱ���Ƿ񳬳�������������˳�
			if (!PubVar.m_DoEvent.m_AuthorizeTools.isExpired(PubVar.SaveDataDate, true)) {
				// //����Ļ�м��������������ʾ
				// String[] infoTextList = new
				// String[]{"��ϵͳ��ʾ��","����������ѹ����޷�����ʹ�ã�","����ϵ��������߻�ȡ��ʽ��Ȩ��","���������ϵͳ��"};
				// for(int i=0;i<infoTextList.length;i++)
				// {
				// String infoText = infoTextList[i];
				// float tw = this.GetTextFont().measureText(infoText);
				// canvas.drawText(infoText,canvas.getWidth()/2-tw/2,canvas.getHeight()/2+i*this.GetTextFont().getTextSize(),
				// this.GetTextFont());
				// }
				return;
			}
		}

		// 4-����ǰ�Ķ�λ״̬��Ҳ���ǵ�ǰ��λ��
		Coordinate CurrentGPSCoor = null;
		if (GPSAlwaysFix) {
			CurrentGPSCoor = StaticObject.soProjectSystem.WGS84ToXY(this.m_LocationEx.GetGpsLongitude(),
					this.m_LocationEx.GetGpsLatitude(), this.m_LocationEx.GetGpsAltitude());
			if (CurrentGPSCoor == null) {
				return;
			}
			Point PT = this.m_MapControl.getMap().getViewConvert().MapToScreen(CurrentGPSCoor);

			// GPS״̬ͼƬ��Դ
			if (this._GpsPointICON == null)
				this._GpsPointICON = ((BitmapDrawable) (PubVar.m_DoEvent.m_Context.getResources()
						.getDrawable(R.drawable.v1_gpspointer))).getBitmap();

			float PointX = PT.x - this._GpsPointICON.getWidth() / 2;
			float PointY = PT.y - this._GpsPointICON.getHeight() / 2;
			canvas.drawBitmap(this._GpsPointICON, PointX, PointY, null);
		}

		// //5-��������
		// if (this.ShowTrackline) //����˵�����ڲɼ�·��
		// {
		// //�ռ�GPS�켣����Ϣ�����㳤��ֵ���������Ƿ���뵽�б��У�����ϵͳ�Ĳ�������趨
		// if (GPSAlwaysFix &&
		// CurrentGPSCoor!=null)this.CalMLenght(CurrentGPSCoor);
		//
		// //���ƹ켣����Ϣ���γɹ켣��
		// Point[] PList =
		// PubVar.m_MapControl.getMap().getViewConvert().ClipPolyline(this._GPSTrackPointList,
		// 0, 0);
		// if (PList.length >=2)
		// {
		// Path p = new Path();
		// for(int i=0;i<PList.length;i++)
		// {
		// if (i==0)p.moveTo(PList[i].x, PList[i].y);
		// else p.lineTo(PList[i].x, PList[i].y);
		// }
		//
		// Paint pPen = new Paint();
		// pPen.setStyle(Style.STROKE);
		// pPen.setStrokeWidth(4);
		// pPen.setColor(Color.RED);
		// canvas.drawPath(p, pPen);
		// }
		//
		// //���Ʋɼ�·�߳�����Ϣ
		// String LValue = lkmap.Tools.Tools.ReSetDistance(this._MLength);
		// String LStarting = "";
		// if (PubVar.m_GPSMap.GpsReceiveDataStatus ==
		// lkmap.Enum.lkGpsReceiveDataStatus.enStarting)LStarting="�����ڲɼ���"+PubVar.m_SXX;
		// if (PubVar.m_GPSMap.GpsReceiveDataStatus ==
		// lkmap.Enum.lkGpsReceiveDataStatus.enPause)LStarting="����ͣ�ɼ���"+PubVar.m_SXX;
		//
		// LStarting+=" ����="+LValue;
		// if (PubVar.m_ShowStartZH)
		// {
		// double za = PubVar.m_StartZH+this._MLength;
		// if (za>=0) LStarting+=" ���׮��="+lkmap.Tools.Tools.ReSetDistance(za);
		// }
		// HashValueObject hvObjectLen =
		// PubVar.m_HashMap.GetValueObject("GPS_·����Ϣ",true);
		// hvObjectLen.ShowOnMap=true;hvObjectLen.LabelText = LStarting;
		//
		// //������ڲɼ�·�ߣ������Զ����̹���
		// if
		// (this.GpsReceiveDataStatus!=lkmap.Enum.lkGpsReceiveDataStatus.enStop)
		// {
		// PubVar.m_DoEvent.m_GPSLine.AutoSaveLine();
		// }
		// }

		// List<String> infoList = PubVar.m_HashMap.GetInMapShowMessageList();
		// int HOffsetY = 70;
		// for (String info:infoList)
		// {
		// canvas.drawText(info,0, HOffsetY, _TextFont);
		// HOffsetY+=40;
		// }

		// 6-�Զ�����
		if (PubVar.AutoPan) {
			// �ж��Ƿ��Ѿ������˵�ǰ��ʾ��Χ
			if (GPSAlwaysFix) {
				mIndex++;
				if (!PubVar.m_Map.getExtend().ContainsPoint(CurrentGPSCoor)) {
					Tools.ShowToast(this.m_MapControl.getContext(), mIndex + "");
					this.m_MapControl._Pan.SetNewCenter(CurrentGPSCoor);
				}
			}
		}
	}

	int mIndex = 0;
}
