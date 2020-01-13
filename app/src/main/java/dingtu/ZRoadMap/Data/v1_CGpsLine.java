package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.List;

import com.dingtu.DTGIS.LDBG.LinDiBianGengData;
import com.dingtu.DTGIS.LDBG.XiaoBanXuji;
import com.dingtu.DTGIS.TuiGeng.TuiGengData;
import com.dingtu.DTGIS.WPZFJC.WeiPianZhiFaData;
import com.dingtu.SLDuCha.TuBanYanZheng;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Region.Op;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import dingtu.ZRoadMap.PubVar;
import android.view.MotionEvent;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.Dataset;
import lkmap.Enum.ForestryLayerType;
import lkmap.Enum.lkDataCollectType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGpsReceiveDataStatus;
import lkmap.Enum.lkPartType;
import lkmap.Enum.lkReUndoFlag;
import lkmap.MapControl.IOnPaint;
import lkmap.MapControl.IOnTouchCommand;
import lkmap.MapControl.Pan;
import lkmap.MapControl.ZoomInOutPan;
import lkmap.Tools.Tools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;
import lkmap.ZRoadMap.Project.v1_Layer;;

public class v1_CGpsLine implements IOnTouchCommand, IOnPaint {
	// ������ݼ�
	private Dataset m_Dataset = null;

	// ������
	private Pan m_Pan = null;
	private ZoomInOutPan m_ZoomPan = null;

	/**
	 * ����������ݼ�Dataset
	 * 
	 * @param pDataset
	 */
	public void SetDataset(Dataset pDataset) {
		this.m_Dataset = pDataset;
		this.m_GpsBaseObj.SetDataset(pDataset);
	}

	public Dataset GetDataset() {
		return this.m_Dataset;
	}

	/**
	 * ���ͼ�����Ч��
	 * 
	 * @return
	 */
	public boolean CheckLayerValid() {
		// ���ͼ�����Ч��
		String LayerID = "";
		if (this.m_Dataset != null)
			LayerID = this.m_Dataset.getId();
		// return
		// PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(LayerID);
		boolean hasEditingDataLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(LayerID);
		if (!hasEditingDataLayer) {
			v1_Layer layer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
					.GetLayerByID(LayerID);
			if (layer == null) {
				Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, Tools.ToLocale("��ѡ����Ч������ͼ�㣡"));
				return false;
			} else {
				return true;
			}
		} else {
			return hasEditingDataLayer;
		}
	}

	// ���ڼ������������
	private Polygon m_Polygon = null;

	public v1_CGpsLine() {
		this.m_Polygon = new Polygon();
		this.m_Polygon.AddPart(new Part(this.m_GPSTrackPointList));

		this.m_GestureDetector = new GestureDetector(PubVar.m_MapControl.getContext(), this.m_MyOnGestureListener);
		this.m_Pan = new Pan(PubVar.m_MapControl);
		this.m_ZoomPan = new ZoomInOutPan(PubVar.m_MapControl);

	}

	/**
	 * �༭����
	 * 
	 * @param LayerID
	 *            ͼ��ID
	 * @param SYS_ID
	 *            ʵ��ID
	 */
	public void Edit(String LayerID, int SYS_ID) {

		Edit(LayerID, SYS_ID, false);
	}

	public void Edit(final String LayerID, final int SYS_ID, final boolean firstEdit) {
		if (!PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(LayerID))
			return;

		String projectType = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(LayerID).GetLayerProjecType();
		if (projectType != null && projectType.equals(ForestryLayerType.TuigengLayer)) {
			Tools.OpenDialog(new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					TuiGengData tuiGengData = new TuiGengData(LayerID, SYS_ID, firstEdit);
				}
			});

		} else if (projectType != null && projectType.equals(ForestryLayerType.LindibiangengLayer)) {
			Tools.OpenDialog(new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					LinDiBianGengData ldbg = new LinDiBianGengData(LayerID, SYS_ID);
					ldbg.ShowView();
				}
			});

		} else if (projectType != null && projectType.equals(ForestryLayerType.DuChaYanZheng)) {
			
			TuBanYanZheng tuBanYanZheng = new TuBanYanZheng(LayerID, SYS_ID);
			
		} else if (projectType != null && projectType.equals(ForestryLayerType.XiaoBanXuji)) {
			XiaoBanXuji wpzf = new XiaoBanXuji(LayerID, SYS_ID);
			wpzf.ShowView();
		} else if (projectType != null && projectType.equals(ForestryLayerType.WeipianJianchaLayer)) {
			WeiPianZhiFaData wpzf = new WeiPianZhiFaData(LayerID, SYS_ID);
			wpzf.ShowView();
		} else {
			this.Edit(LayerID, SYS_ID, null);
		}
	}

	public void Edit(String LayerID, int SYS_ID, ICallback cb) {
		// TanhuiDataTemplate _DT = new TanhuiDataTemplate();
		// _DT.SetEditInfo(LayerID, SYS_ID);
		// _DT.SetCallback(cb);
		// _DT.ShowDialog();

		GeneralDateEditor dataEdit = new GeneralDateEditor(LayerID, SYS_ID);
		dataEdit.SetCallback(cb);
	}

	// ����Ƿ����ڲɼ�·��
	public boolean CheckIfStarting() {
		if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop)
			return false;
		else
			return true;
	}

	// ��ʼ�ɼ�
	public void Start(lkDataCollectType dct) {
		this.m_DataCollectType = dct;
		this.m_DataCollectStatus = lkGpsReceiveDataStatus.enStarting;

		if (this.m_DataCollectType == lkDataCollectType.enGps_T) {
			if (!lkmap.Tools.Tools.ReadyGPS(true))
				return;
		}
	}

	// ��ͣ�ɼ�
	public void Pause() {
		// if (PubVar.m_GPSMap==null) return;
		// if (PubVar.m_GPSMap.GpsReceiveDataStatus ==
		// lkmap.Enum.lkGpsReceiveDataStatus.enPause)
		// {
		// PubVar.m_GPSMap.GpsReceiveDataStatus =
		// lkmap.Enum.lkGpsReceiveDataStatus.enStarting;
		// PubVar.m_Map.FastRefresh();
		// return;
		// }
		// if (PubVar.m_GPSMap.GpsReceiveDataStatus ==
		// lkmap.Enum.lkGpsReceiveDataStatus.enStarting)
		// {
		// PubVar.m_GPSMap.GpsReceiveDataStatus =
		// lkmap.Enum.lkGpsReceiveDataStatus.enPause;
		// PubVar.m_Map.FastRefresh();
		// return;
		// }
	}

	/**
	 * �༭��ǰ���ڲɼ��ߵ�����
	 */
	public void Edit() {
		if (!this.CheckLayerValid())
			return;
		if (this.m_DataCollectStatus != lkGpsReceiveDataStatus.enStop) {
			if (this.m_GpsBaseObj.GetSYS_ID() != -1) {
				this.Edit(this.m_Dataset.getId(), this.m_GpsBaseObj.GetSYS_ID());
				return;
			}
		}
		lkmap.Tools.Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("�޷��༭��ǰ���ڲɼ�ʵ�����ԣ�"));
	}

	/**
	 * ȡ����ǰ���ڲɼ�������
	 */
	public void Cancel() {
		if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop) {
			lkmap.Tools.Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("��ǰû�����ڲɼ���ʵ�壡"));
			return;
		}
		this.m_DataCollectStatus = lkmap.Enum.lkGpsReceiveDataStatus.enStop;
		this.m_GPSTrackPointList.clear();
		// ��ջ���ջ
		this.m_UndoCoordinateList.clear();
		this.m_MValueList.clear();
		// this.m_DataCollectType = lkDataCollectType.enUnkonw;
		PubVar.m_MapControl.invalidate();
	}

	// �����ߵĽڵ����ջ��Ϊ������׼��
	private List<Coordinate> m_UndoCoordinateList = new ArrayList<Coordinate>();

	/**
	 * ���˵�ǰ���ڲɼ������ͣ�1����
	 */
	public boolean Undo() {
		if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop) {
			lkmap.Tools.Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("��ǰû�����ڲɼ���ʵ�壡"));
			return false;
		}
		if (this.m_GPSTrackPointList.size() > 0) {
			this.m_UndoCoordinateList.add(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size() - 1));
			this.m_GPSTrackPointList.remove(this.m_GPSTrackPointList.size() - 1);
			if (m_MValueList.size() > 0) {
				this.m_MValueList.remove(this.m_MValueList.size() - 1);
			}

			PubVar.m_MapControl.invalidate();
			return true;
		} else {
			// lkmap.Tools.Tools.ShowToast(PubVar.m_DoEvent.m_Context,
			// Tools.ToLocale("�ڵ�����Ϊ0���޷����ˣ�"));
			return false;
		}
	}

	/**
	 * ����������
	 * 
	 * @return
	 */
	public boolean Redo() {
		if (this.m_UndoCoordinateList.size() > 0) {
			Coordinate ptCoor = this.m_UndoCoordinateList.get(this.m_UndoCoordinateList.size() - 1);
			this.m_UndoCoordinateList.remove(ptCoor);
			this.m_GPSTrackPointList.add(ptCoor);
			if (this.m_GPSTrackPointList.size() > 1) {
				this.AddMValue(Tools
						.GetTwoPointDistance(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size() - 2), ptCoor),
						(this.m_CalArea ? this.m_Polygon.getArea(true) : 0));
			} else {
				this.AddMValue(0, 0);
			}

			PubVar.m_MapControl.invalidate();

			return true;
		}
		return false;
	}

	public void Stop() {
		this.Stop(lkGeoLayerType.enPolyline);
	}

	public void Stop(lkGeoLayerType geoType) {
		// ���ͼ�����Ч��
		if (!PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(this.m_Dataset.getId()))
			return;
		if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop) {
			lkmap.Tools.Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("��ǰû�����ڲɼ���ʵ�壡"));
			return;
		}

		// �ж��Ѿ��ɼ��ߵ������������2�����򲻴�
		if ((this.m_GPSTrackPointList.size() >= 2 && geoType == lkGeoLayerType.enPolyline)
				|| (this.m_GPSTrackPointList.size() >= 3 && geoType == lkGeoLayerType.enPolygon)) {
			// �������һ���ɼ���
			if (lkmap.Tools.Tools.ReadyGPS(false)) {

				if (PubVar.m_DoEvent.m_GPSLocate.getGPSCoordinate() != null) {
					this.AddPoint(PubVar.m_DoEvent.m_GPSLocate.getGPSCoordinate());
				}

			}

			int SYS_ID = this.SaveGeoToDb(true);
			if (SYS_ID != -1) {
				this.Edit(this.m_Dataset.getId(), SYS_ID, true);

				// �������ջ��
				UnRedoParaStru UnRedoPara = new UnRedoParaStru();
				UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;
				UnRedoDataItem urDataItem = new UnRedoDataItem();
				urDataItem.Type = lkReUndoFlag.enRedo;
				IURDataItem_DeleteAdd uiAdd = new IURDataItem_DeleteAdd();
				uiAdd.LayerId = this.m_Dataset.getId();
				uiAdd.ObjectIdList.add(SYS_ID);
				urDataItem.DataList.add(uiAdd);
				UnRedoPara.DataItemList.add(urDataItem);
				IUnRedo.AddHistory(UnRedoPara);
			}
		}

		this.m_DataCollectStatus = lkmap.Enum.lkGpsReceiveDataStatus.enStop;
		this.m_GPSTrackPointList.clear();
		// ��ջ���ջ
		this.m_UndoCoordinateList.clear();
		this.m_MValueList.clear();
		// this.m_DataCollectType = lkDataCollectType.enUnkonw;

		PubVar.m_Map.Refresh();

	}

	// �ɼ�ʵ��
	private v1_CGpsDataObject m_GpsBaseObj = new v1_CGpsDataObject();

	/**
	 * ����ͼ������
	 * 
	 * @param geoType
	 * @param EndSave
	 *            �Ƿ�Ϊ�����ɼ���ı������
	 * @return
	 */
	public int SaveGeoToDb(boolean EndSave) {
		Geometry m_GpsGeometry = null;

		double dataLen = 0, dataArea = 0; // ���������

		String SYS_TYPE = "�ɼ�����"; // ����
		List<Coordinate> newCoorList = new ArrayList<Coordinate>();
		for (Coordinate Coor : this.m_GPSTrackPointList)
			newCoorList.add(Coor.Clone());

		if (this.m_Dataset.getType() == lkGeoLayerType.enPolyline) {
			SYS_TYPE = "�ɼ�����";
			m_GpsGeometry = new Polyline();
			m_GpsGeometry.AddPart(new Part());
			m_GpsGeometry.GetPartAt(0).setVertext(newCoorList);
			dataLen = ((Polyline) m_GpsGeometry).getLength(true);
		}

		if (this.m_Dataset.getType() == lkGeoLayerType.enPolygon) {
			SYS_TYPE = "�ɼ���";
			m_GpsGeometry = new Polygon();
			Part part = new Part();
			m_GpsGeometry.AddPart(part);
			newCoorList.add(this.m_GPSTrackPointList.get(0).Clone()); // ��պ�
			m_GpsGeometry.GetPartAt(0).setVertext(newCoorList);

			// �����ɼ������㳤�������
			// ��������ֻҪ�Ǿ�����ķ�����Ϊ��ʱ�뷽�򣬶�Ϊ˳ʱ��
			m_GpsGeometry.GetPartAt(0).SetPartType(lkPartType.enPoly);
			dataLen = ((Polygon) m_GpsGeometry).getLength(true);
			dataArea = ((Polygon) m_GpsGeometry).getArea(true);
		}

		m_GpsGeometry.CalEnvelope();
		this.m_GpsBaseObj.SetSYS_TYPE(SYS_TYPE);
		this.m_GpsBaseObj.SetSYS_STATUS("0");

		int SYS_ID = this.m_GpsBaseObj.SaveGeoToDb(m_GpsGeometry, dataLen, dataArea);
		this.m_GpsBaseObj.SetSYS_ID(-1);

		return SYS_ID;
	}

	/**
	 * ����GPSλ������
	 * 
	 * @param newCoor
	 */
	private long m_BeforeGpsPosUpateTime = 0;

	public void UpdateGpsPosition(Coordinate newCoor) {
		if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop)
			return;
		if (this.m_DataCollectType != lkDataCollectType.enGps_T)
			return;

		// �ڴ˿��Ʋɼ���ʱ��������С���룬��ϵͳ����
		// Tag_System_GPSMinTime,Tag_System_GPSMinDistance ����

		// ��Ҫ�ڴ˼������������Ҳ���Ǿ���̫�ĵĹ��˵���Ĭ��ֵ��v1_UserConfigDB.LoadSystemConfig
		double MinDistance = 0;
		if (this.m_GPSTrackPointList.size() > 0) {
			Coordinate PT = this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size() - 1);
			MinDistance = Tools.GetTwoPointDistance(newCoor, PT);

			double LimitDistance = 0;
			if (!(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance").Value + "").equals("����")) {
				LimitDistance = Double
						.parseDouble(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance").Value + "");
			}
			if (MinDistance < LimitDistance)
				return;
		}

		// GPS����ʱ�����ȥ����Ĭ��ֵ��v1_UserConfigDB.LoadSystemConfig
		double LimitTime = 0;
		long CurrentTime = System.currentTimeMillis();
		if (!(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime").Value + "").equals("����")) {
			LimitTime = Double.parseDouble(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime").Value + "") * 1000;
		}
		if ((CurrentTime - this.m_BeforeGpsPosUpateTime) >= (LimitTime - 100)) {
			this.m_GPSTrackPointList.add(newCoor);
			this.AddMValue(MinDistance, (this.m_CalArea ? this.m_Polygon.getArea(true) : 0));
			this.m_BeforeGpsPosUpateTime = CurrentTime;
			PubVar.m_DoEvent.m_SoundTool.PlaySound(5); // �����������
			// ��ջ���ջ
			this.m_UndoCoordinateList.clear();
		}
	}

	/**
	 * GPS�߲���
	 */
	private lkDataCollectType m_DataCollectType = lkDataCollectType.enUnkonw; // ���ݲɼ�ģʽ
	private lkGpsReceiveDataStatus m_DataCollectStatus = lkGpsReceiveDataStatus.enStop; // ���ݲɼ�״̬
	private List<Coordinate> m_GPSTrackPointList = new ArrayList<Coordinate>(); // GPS�����ߵĹ켣��

	public List<Coordinate> GetTrackPointList() {
		return this.m_GPSTrackPointList;
	}

	// ���ٲ���ֵ
	private List<MValue> m_MValueList = new ArrayList<MValue>();

	/**
	 * ���²���ֵ
	 * 
	 * @param length
	 * @param area
	 */
	private void AddMValue(double length, double area) {
		MValue mv = new MValue();
		if (this.m_MValueList.size() == 0) {
			mv.Length = length;
			mv.Area = area;
		} else {
			mv.Length = this.m_MValueList.get(this.m_MValueList.size() - 1).Length + length;
			mv.Area = area;
		}
		this.m_MValueList.add(mv);
	}

	// �Ƿ�̬�����������Ҫ�����ڲɼ���
	private boolean m_CalArea = false;

	/**
	 * ��̬���������
	 * 
	 * @param mtype
	 */
	public void SetIfCalArea(boolean calArea) {
		this.m_CalArea = calArea;
	}

	/**
	 * �ı��ͼ����
	 */
	public void ChangeEditDirection() {
		if (this.m_GPSTrackPointList.size() <= 1)
			return;
		List<Coordinate> CoorList = new ArrayList<Coordinate>();
		for (int i = this.m_GPSTrackPointList.size() - 1; i >= 0; i--) {
			CoorList.add(this.m_GPSTrackPointList.get(i));
		}
		this.m_GPSTrackPointList.clear();
		for (Coordinate Coor : CoorList) {
			this.m_GPSTrackPointList.add(Coor);
		}

		// �����������ֵ
		this.m_MValueList.clear();

		if (this.m_GPSTrackPointList.size() > 1) {
			for (int i = 0; i < this.m_GPSTrackPointList.size() - 1; i++) {
				this.AddMValue(
						Tools.GetTwoPointDistance(this.m_GPSTrackPointList.get(i), this.m_GPSTrackPointList.get(i + 1)),
						(this.m_CalArea ? this.m_Polygon.getArea(true) : 0));
			}
		} else {
			if (this.m_GPSTrackPointList.size() == 1)
				this.AddMValue(0, 0);
		}

		this.m_Pan.SetNewCenter(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size() - 1));
		// ��ջ���ջ
		this.m_UndoCoordinateList.clear();
	}

	/**
	 * �ֶ����߽ӿ�
	 */
	@Override
	public void OnPaint(Canvas canvas) {
		this.m_ZoomPan.OnPaint(canvas);
		if (PubVar.m_Map.getInvalidMap())
			return;

		if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop) {
			this.UpdateDataInStatus();
			return;

		}
		// ���ƹ켣����Ϣ���γɹ켣��
		Point[] PList = PubVar.m_MapControl.getMap().getViewConvert().MapPointsToScreePoints(this.m_GPSTrackPointList);
		if (PList.length > 0) {
			Path p = new Path();
			for (int i = 0; i < PList.length; i++) {
				if (i == 0)
					p.moveTo(PList[i].x, PList[i].y);
				else
					p.lineTo(PList[i].x, PList[i].y);
			}

			Paint pPen = new Paint();
			pPen.setStyle(Style.STROKE);
			pPen.setStrokeWidth(Tools.DPToPix(3));
			pPen.setColor(Color.RED);
			canvas.drawPath(p, pPen);
		}

		// ���ƹ켣�ڵ�
		int H = Tools.DPToPix(6);
		Paint pBrush = new Paint();
		for (int i = 0; i < PList.length; i++) {
			pBrush.setStyle(Style.FILL);
			pBrush.setColor(Color.YELLOW);
			canvas.drawCircle(PList[i].x, PList[i].y, H / 2, pBrush);

			pBrush.setStyle(Style.STROKE);
			pBrush.setStrokeWidth(2);
			pBrush.setColor(Color.RED);
			canvas.drawCircle(PList[i].x, PList[i].y, H / 2, pBrush);
		}

		// �����һ���ڵ㴦��һ��ԲȦ����ʾ��ģʽ�¿������ӵķ�Χ
		if (this.m_DataCollectType == lkDataCollectType.enManual) {
			if (PList.length < 1)
				return;
			pBrush.setColor(Color.BLUE);
			pBrush.setStrokeWidth(Tools.DPToPix(3));
			canvas.drawCircle(PList[PList.length - 1].x, PList[PList.length - 1].y, this.m_Tolerance / 2, pBrush);
		}

		this.UpdateDataInStatus();
	}

	int RADIUS = 75;
	int FACTOR = 2;

	private void drawMagnifier3(Canvas canvas, int x, int y) {
		Bitmap bitmap = PubVar.m_Map.bp;

		if (bitmap == null) {
			return;
		}

		// canvas.translate(-FACTOR*RADIUS,-FACTOR*RADIUS);
		Path path = new Path();
		path.addCircle(x, y, FACTOR * RADIUS - 1, Direction.CW);

		PubVar.m_MapControl.getHeight();

		// �Ŵ󾵼����
		Paint pBrush = new Paint();
		pBrush.setStyle(Style.STROKE);
		pBrush.setStrokeWidth(6);
		pBrush.setColor(Color.GRAY);
		canvas.drawCircle(FACTOR * RADIUS, PubVar.m_MapControl.getHeight() - FACTOR * RADIUS, RADIUS * FACTOR, pBrush);

		canvas.translate(FACTOR * RADIUS - x, PubVar.m_MapControl.getHeight() - y - FACTOR * RADIUS);

		Matrix matrix = new Matrix();
		matrix.setScale(FACTOR, FACTOR);
		matrix.postTranslate(-(FACTOR - 1) * x, -(FACTOR - 1) * y);
		canvas.clipPath(path, Op.REPLACE);
		canvas.drawBitmap(bitmap, matrix, null);

		Paint mPaint = new Paint();
		mPaint.setColor(Color.BLUE);
		mPaint.setStrokeWidth(3);
		canvas.drawLine(x - 50, y, x + 50, y, mPaint);
		canvas.drawLine(x, y - 50, x, y + 50, mPaint);
	}

	/**
	 * �������ݲɼ�״̬
	 */
	private void UpdateDataInStatus() {
		// ˢ�²ɼ�״̬
		MValue pMValue = (this.m_MValueList.size() > 0 ? this.m_MValueList.get(this.m_MValueList.size() - 1) : null);
		String mType = "";
		double mValue = 0;
		if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop
				|| this.m_DataCollectType == lkDataCollectType.enUnkonw) {
			mType = "ֹͣ";
			mValue = 0;
		} else {
			if (this.m_DataCollectType == lkDataCollectType.enManual) {
				mType = "�ֻ�";
				mValue = 0;
				if (this.m_CalArea)
					mValue = (pMValue != null ? pMValue.Area : 0);
				else
					mValue = (pMValue != null ? pMValue.Length : 0);
			}
			if (this.m_DataCollectType == lkDataCollectType.enGps_P
					|| this.m_DataCollectType == lkDataCollectType.enGps_T) {
				mType = "GPS";
				mValue = 0;
				if (this.m_CalArea)
					mValue = (pMValue != null ? pMValue.Area : 0);
				else
					mValue = (pMValue != null ? pMValue.Length : 0);
			}
		}

		if (this.m_CalArea) {
			PubVar.m_DoEvent.m_CGpsDataInStatus.UpdatePolyStatus(mType, mValue);
			return;
		} else {
			PubVar.m_DoEvent.m_CGpsDataInStatus.UpdateLineStatus(mType, mValue);
			return;
		}
	}

	/**
	 * ����ƽ����
	 */
	public void AddAveragePoint() {
		v1_Data_Template _DT = new v1_Data_Template();
		_DT.SetCalAveragePoint(true);
		_DT.SetEditInfo(this.m_Dataset.getId(), this.m_GpsBaseObj.GetSYS_ID());
		_DT.ShowDialog();
	}

	/**
	 * ���ӵ�λ��GPS����
	 * 
	 * @param ptCoor
	 */
	public void AddPoint(Coordinate ptCoor) {
		if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop)
			return;
		if (this.m_DataCollectType == lkDataCollectType.enGps_P) {
			this.m_GPSTrackPointList.add(ptCoor);
			if (this.m_GPSTrackPointList.size() > 1) {
				this.AddMValue(Tools
						.GetTwoPointDistance(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size() - 2), ptCoor),
						(this.m_CalArea ? this.m_Polygon.getArea(true) : 0));
			} else {
				this.AddMValue(0, 0);
			}
			// ��ջ���ջ
			this.m_UndoCoordinateList.clear();
			PubVar.m_MapControl.invalidate();
		}
	}

	private void AddPointByHandOnMouseDown(MotionEvent e) {
		// PubVar.m_DoEvent.m_GlassView.SetVisible(false);
		// if (this.m_DataCollectStatus==lkGpsReceiveDataStatus.enStop) return;
		if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop) {
			this.m_DataCollectStatus = lkGpsReceiveDataStatus.enStarting;
		}
		this.m_DataCollectStatus = lkGpsReceiveDataStatus.enStarting;
		if (this.m_DataCollectType == lkDataCollectType.enManual) {
			PointF pt = new PointF(e.getX(), e.getY());

			Coordinate mPoint = PubVar.m_Map.getViewConvert().ScreenToMap(pt);

			if (this.m_GPSTrackPointList.size() >= 1) {

				PointF ptLast = PubVar.m_Map.getViewConvert().MapToScreenF(
						this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size() - 1).getX(),
						this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size() - 1).getY());

				// ��ϡ��px��С
				int buff = 10;

				if (Math.abs((int) ptLast.x - (int) pt.x) >= buff || Math.abs((int) ptLast.y - (int) pt.y) >= buff) {

					this.m_GPSTrackPointList.add(mPoint);
					this.AddMValue(
							Tools.GetTwoPointDistance(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size() - 2),
									mPoint),
							(this.m_CalArea ? this.m_Polygon.getArea(true) : 0));

				} else {

				}

			} else {
				this.m_GPSTrackPointList.add(mPoint);
				this.AddMValue(0, 0);
			}

			// Coordinate mPoint =
			// PubVar.m_Map.getViewConvert().ScreenToMap(pt);
			// //��Map����ת��Ϊ�������ֻ꣬�������ֶ��ӵ�����
			// //Coordinate lb84 =
			// StaticObject.soProjectSystem.XYToWGS84(mPoint);
			// //mPoint.setGeoX(lb84.getX());mPoint.setGeoY(lb84.getY());
			//
			// this.m_GPSTrackPointList.add(mPoint);
			// if (this.m_GPSTrackPointList.size()>1)
			// {
			// this.AddMValue(Tools.GetTwoPointDistance(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-2),mPoint),
			// (this.m_CalArea?this.m_Polygon.getArea(true):0));
			// }else
			// {
			// this.AddMValue(0,0);
			// }
			// //��ջ���ջ
			this.m_UndoCoordinateList.clear();
			PubVar.m_MapControl.invalidate();

		}
	}

	// public void MouseDown(MotionEvent e)
	// {
	// this.AddPointByHandOnMouseDown(e);
	// }

	// �Ƿ�������ģʽ��ͼ
	private boolean m_StreamMode = true;

	/**
	 * �����Ƿ�������ģʽ��ͼ
	 * 
	 * @param streamMode
	 */
	public void SetStreamMode(boolean streamMode) {
		this.m_StreamMode = streamMode;
	}

	// ָʾ��������������ͼ��1-����ͼ��2-�������жϵ�����ΪMouseDown���Ƿ������һ�ڵ㿿��
	private int m_DrawOrPan = 1;
	private boolean isLongPressed = false;
	// ��ʾ��ģʽ�¿������ӵķ�Χ
	private int m_Tolerance = Tools.DPToPix(20);

	private GestureDetector m_GestureDetector = null;
	private SimpleOnGestureListener m_MyOnGestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onDown(MotionEvent e) {

			if (!m_StreamMode) {
				m_DrawOrPan = 2;
			} else {
				m_DrawOrPan = 1;
				if (m_GPSTrackPointList.size() > 0) {
					Coordinate mPoint = m_GPSTrackPointList.get(m_GPSTrackPointList.size() - 1);
					PointF pf = PubVar.m_Map.getViewConvert().MapToScreenF(mPoint.getX(), mPoint.getY());
					if (Math.abs(pf.x - e.getX()) >= m_Tolerance || Math.abs(pf.y - e.getY()) >= m_Tolerance) {
						m_DrawOrPan = 2;
					}
				} else {
					AddPointByHandOnMouseDown(e);
				}
			}

			return super.onDown(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (m_DrawOrPan == 1) {
				AddPointByHandOnMouseDown(e2);
				;
			}

			if (m_DrawOrPan == 2) {
				m_Pan.MouseDown(e1);
				m_Pan.MouseMove(e2);

			}

			// m_ZoomPan.Scroll(e1, e2, distanceX, distanceY);
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// AddPointByHandOnMouseDown(e);
			isLongPressed = true;
			super.onLongPress(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			AddPointByHandOnMouseDown(e);
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			m_ZoomPan.Scroll(e1, e2, velocityX, velocityY);
			return super.onFling(e1, e2, velocityX, velocityY);
		}

	};

	@Override
	public void SetOnTouchEvent(MotionEvent e) {
		this.m_GestureDetector.onTouchEvent(e);

		if ((e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
			this.m_Pan.MouseUp(e);
			if (isLongPressed) {
				AddPointByHandOnMouseDown(e);
				isLongPressed = false;
			}
		}

		if (e.getPointerCount() > 1) {
			m_ZoomPan.SetOnTouchEvent(e);
		}

	}

	private class MValue {
		public double Length = 0;
		public double Area = 0;
	}
}
