package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;

public class v1_CGps_AveragePoint {

	public v1_CGps_AveragePoint() {
	}

	// ״̬�ص�
	private ICallback m_Callback = null;

	public void SetCallback(ICallback cb) {
		this.m_Callback = cb;
	}

	public void Cancel() {
		if (this.m_Timer != null)
			this.m_Timer.cancel();
	}

	/**
	 * ����ƽ��ֵ
	 */
	public Coordinate CalGpsPoint() {
		this.m_Timer.cancel();
		if (this.m_GpsPointList == null || this.m_GpsPointList.size() == 0) {
			// Tools.ShowMessageBox("��ǰ��ȡ����ЧGPS����Ϊ0���޷�����ƽ��ֵ��");
			return null;
		} else {
			double X = 0, Y = 0, GeoX = 0, GeoY = 0, Z = 0;
			for (Coordinate Pt : this.m_GpsPointList) {
				X += Pt.getX();
				Y += Pt.getY();
				Z += Pt.getZ();
				// GeoX+=Pt.getGeoX();GeoY+=Pt.getGeoY();
			}
			Coordinate newCoor = new Coordinate(X / this.m_GpsPointList.size(), Y / this.m_GpsPointList.size(),
					Z / this.m_GpsPointList.size());
			// newCoor.setGeoX(GeoX/this.m_GpsPointList.size());newCoor.setGeoY(GeoY/this.m_GpsPointList.size());
			if (this.m_Callback != null)
				this.m_Callback.OnClick("�ɼ����", newCoor);
			return newCoor;
		}
	}

	// ��Ҫ������GPS����
	private int m_GpsPointCount = 1;

	// �ɼ��������λ
	private List<Coordinate> m_GpsPointList = new ArrayList<Coordinate>();

	// ��ʱ������
	private Timer m_Timer = null;
	private int m_AllTime = 0; // �ܺ�ʱ

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// ��ʱ�ۼ�
				m_AllTime++;
				if (lkmap.Tools.Tools.ReadyGPS(false)) {
					Coordinate coordinate = PubVar.m_GPSLocate.getGPSCoordinate();
					if (coordinate != null) {
						m_GpsPointList.add(PubVar.m_GPSLocate.getGPSCoordinate());
						PubVar.m_DoEvent.m_SoundTool.PlaySound(5); // �����������
						if (m_GpsPointList.size() >= m_GpsPointCount) {
							// ����ֵ������
							startCallback();
							CalGpsPoint();
							return;
						}
					}

				}
				startCallback();
				break;
			}
			super.handleMessage(msg);
		}
	};

	// ��ʼ�ش��ɼ�״̬
	private void startCallback() {
		if (this.m_Callback != null) {
			this.m_Callback.OnClick("�ɼ�״̬", this.m_GpsPointList.size() + "," + this.m_AllTime);
		}
	}

	/**
	 * ��ʼ��ȡƽ����
	 */
	public void Start(int pointCount) {
		this.m_GpsPointCount = pointCount;

		// ��ʼ����GPS��λ
		if (this.m_Timer != null)
			this.m_Timer.cancel();
		this.m_Timer = new Timer(true);
		this.m_AllTime = 0;
		this.m_GpsPointList.clear();
		this.m_Timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		}, 0, 1000); // ��ʱ1000ms��ִ�У�1000msִ��һ��
	}
}
