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

	// 状态回调
	private ICallback m_Callback = null;

	public void SetCallback(ICallback cb) {
		this.m_Callback = cb;
	}

	public void Cancel() {
		if (this.m_Timer != null)
			this.m_Timer.cancel();
	}

	/**
	 * 计算平均值
	 */
	public Coordinate CalGpsPoint() {
		this.m_Timer.cancel();
		if (this.m_GpsPointList == null || this.m_GpsPointList.size() == 0) {
			// Tools.ShowMessageBox("当前获取的有效GPS点数为0，无法计算平均值！");
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
				this.m_Callback.OnClick("采集结果", newCoor);
			return newCoor;
		}
	}

	// 需要采样的GPS点数
	private int m_GpsPointCount = 1;

	// 采集的坐标点位
	private List<Coordinate> m_GpsPointList = new ArrayList<Coordinate>();

	// 用时计算器
	private Timer m_Timer = null;
	private int m_AllTime = 0; // 总耗时

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 耗时累计
				m_AllTime++;
				if (lkmap.Tools.Tools.ReadyGPS(false)) {
					Coordinate coordinate = PubVar.m_GPSLocate.getGPSCoordinate();
					if (coordinate != null) {
						m_GpsPointList.add(PubVar.m_GPSLocate.getGPSCoordinate());
						PubVar.m_DoEvent.m_SoundTool.PlaySound(5); // 连续打点声音
						if (m_GpsPointList.size() >= m_GpsPointCount) {
							// 计算值并返回
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

	// 开始回传采集状态
	private void startCallback() {
		if (this.m_Callback != null) {
			this.m_Callback.OnClick("采集状态", this.m_GpsPointList.size() + "," + this.m_AllTime);
		}
	}

	/**
	 * 开始获取平均点
	 */
	public void Start(int pointCount) {
		this.m_GpsPointCount = pointCount;

		// 开始捕获GPS点位
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
		}, 0, 1000); // 延时1000ms后执行，1000ms执行一次
	}
}
