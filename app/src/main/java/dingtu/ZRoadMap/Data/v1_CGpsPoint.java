package dingtu.ZRoadMap.Data;

import com.dingtu.DTGIS.LDBG.LinDiBianGengData;
import com.dingtu.DTGIS.LDBG.XiaoBanXuji;
import com.dingtu.DTGIS.TuiGeng.TuiGengData;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import dingtu.ZRoadMap.PubVar;
import android.view.MotionEvent;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Point;
import lkmap.Dataset.Dataset;
import lkmap.Enum.ForestryLayerType;
import lkmap.Enum.lkReUndoFlag;
import lkmap.MapControl.IOnPaint;
import lkmap.MapControl.IOnTouchCommand;
import lkmap.MapControl.Pan;
import lkmap.Tools.Tools;
import lkmap.UnRedo.IURDataItem_DeleteAdd;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;

public class v1_CGpsPoint implements IOnTouchCommand, IOnPaint {
	public v1_CGpsPoint() {
		this.m_GestureDetector = new GestureDetector(PubVar.m_MapControl.getContext(), this.m_MyOnGestureListener);
		_Pan = new Pan(PubVar.m_MapControl);
	}

	private Pan _Pan = null;

	// ������ݼ�
	private Dataset m_Dataset = null;

	/**
	 * ����������ݼ�Dataset
	 * 
	 * @param pDataset
	 */
	public void SetDataset(Dataset pDataset) {
		this.m_Dataset = pDataset;
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
		return PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(LayerID);
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
		this.Edit(LayerID, SYS_ID, null);
	}

	public void Edit(String LayerID, int SYS_ID, ICallback cb) {
		// TanhuiDataTemplate _DT = new TanhuiDataTemplate();
		// _DT.SetEditInfo(LayerID, SYS_ID);
		// _DT.SetCallback(cb);
		// _DT.ShowDialog();

		if (!PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(LayerID))
			return;

		String projectType = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(LayerID).GetLayerProjecType();
		if (projectType != null && projectType.equals(ForestryLayerType.TuigengLayer)) {
			TuiGengData tuiGengData = new TuiGengData(LayerID, SYS_ID, false);
		} else if (projectType != null && projectType.equals(ForestryLayerType.XiaoBanXuji)) {
			XiaoBanXuji xbxj = new XiaoBanXuji(LayerID, SYS_ID);
			xbxj.ShowView();
		} else if (projectType != null && projectType.equals(ForestryLayerType.LindibiangengLayer)) {
			LinDiBianGengData ldbg = new LinDiBianGengData(LayerID, SYS_ID);
			ldbg.ShowView();

		} else {
			GeneralDateEditor dataEdit = new GeneralDateEditor(LayerID, SYS_ID);
			dataEdit.SetCallback(cb);
		}

	}

	/**
	 * ����GPS��λ
	 */
	public void AddGPSPoint() {
		if (!lkmap.Tools.Tools.ReadyGPS(true))
			return;

		// �Ƿ�����ƽ��ֵ�ɵ�
		boolean averageEnable = Boolean
				.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_AveragePointEnable").Value);

		Coordinate coordinate = PubVar.m_GPSLocate.getGPSCoordinate();

		if (!averageEnable) {
			if (coordinate != null) {
				this.AddPoint(coordinate, "GPS��λ");
			}

		} else {
			// TanhuiDataTemplate _DT = new TanhuiDataTemplate();
			// _DT.SetEditInfo(this.GetDataset().getId(),-1); //����
			// _DT.SetCalAveragePoint(true);
			// _DT.ShowDialog();

			GeneralDateEditor dataEdit = new GeneralDateEditor(this.GetDataset().getId(), -1);
			dataEdit.SetCalAveragePoint(true);

			// v1_Data_Gps_AveragePoint dsap = new v1_Data_Gps_AveragePoint();
			// dsap.SetDataType(lkGeoLayerType.enPoint);
			// dsap.SetCallback(new ICallback(){
			// @Override
			// public void OnClick(String Str, Object ExtraStr) {
			// AddPoint((Coordinate)ExtraStr,"GPS��λ");
			// }});
			// dsap.ShowDialog();
		}
	}

	/**
	 * ͨ���ֶ���������ӵ�
	 */
	public void AddPointByInputCoor() {
		v1_Data_Point_InputCoor dpi = new v1_Data_Point_InputCoor();
		dpi.SetCallback(new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				AddPoint((Coordinate) ExtraStr, "������");
			}
		});
		dpi.ShowDialog();
	}

	/**
	 * ���ӵ�λ
	 * 
	 * @param ptCoor
	 */
	private void AddPoint(Coordinate ptCoor, String SYSTYPE) {
		// Point ptGeo = new Point(ptCoor);
		// v1_BaseDataObject _GpsBaseObj = new v1_BaseDataObject();
		// _GpsBaseObj.SetBaseObjectRelateTable(this.m_TableName);
		// _GpsBaseObj.SetSYS_TYPE(SYSTYPE);
		//
		// int SYS_ID = _GpsBaseObj.SaveGeoToDb(ptGeo);

		int SYS_ID = this.SaveGeoToDb(ptCoor, SYSTYPE);
		if (SYS_ID != -1) {
			this.Edit(this.GetDataset().getId(), SYS_ID);
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
		PubVar.m_Map.Refresh();
	}

	/**
	 * �����ʵ��
	 * 
	 * @param ptCoor
	 * @param SYSTYPE
	 * @return
	 */
	public int SaveGeoToDb(Coordinate ptCoor, String SYSTYPE) {
		Point ptGeo = new Point(ptCoor);
		v1_CGpsDataObject _GpsBaseObj = new v1_CGpsDataObject();
		_GpsBaseObj.SetDataset(this.GetDataset());
		_GpsBaseObj.SetSYS_TYPE(SYSTYPE);
		int SYS_ID = _GpsBaseObj.SaveGeoToDb(ptGeo, 0, 0);

		// List<String> newObjIdx = new
		// ArrayList<String>();newObjIdx.add(SYS_ID+"");
		//
		// if (this.GetDataset().QueryGeometryFromDB(newObjIdx))
		// {
		// Geometry pGeometryNew = this.GetDataset().GetGeometry(SYS_ID);
		// //pDataset.UpdateLayerIndex(pGeometryNew);
		// //pDataset.CalEnvelope(); //����Dataset��Envelopeʹ֮��������ʵ��
		// PubVar.m_Map.ClearSelection();
		// PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(this.GetDataset().getId()).getSelSelection().Add(pGeometryNew);
		// PubVar.m_Map.Refresh();
		// }
		return SYS_ID;
	}

	/**
	 * ����Ϊ�����ӿڣ�Ϊ�ֻ�����׼��
	 * 
	 * @param canvas
	 */
	@Override
	public void OnPaint(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	public void MouseDown(MotionEvent e) {

		// if
		// (Boolean.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value+""))
		// {
		// PubVar.m_DoEvent.m_GlassView.SetVisible(true);
		// PubVar.m_DoEvent.m_GlassView.SetGlassPoint(e.getX(), e.getY());
		// }
		// else
		// {
		this.AddPointByHandOnMouseDown(e);
		// }
	}

	public void MouseMove(MotionEvent e) {
		// if
		// (Boolean.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value+""))
		// {
		// PubVar.m_DoEvent.m_GlassView.SetVisible(true);
		// PubVar.m_DoEvent.m_GlassView.SetGlassPoint(e.getX(), e.getY());
		// }
	}

	public void MouseUp(MotionEvent e) {
		// if
		// (Boolean.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value+""))
		// {
		// this.AddPointByHandOnMouseDown(e);
		// }
	}

	private Coordinate m_HandPoint = null;

	private void AddPointByHandOnMouseDown(final MotionEvent e) {
		// double JD = 102.384812;
		// double WD = 25.292934;
		//
		// //���뾭γ��תƽ������
		// Coordinate XYCoor = StaticObject.soProjectSystem.WGS84ToXY(JD, WD,
		// 0);
		//
		//
		// //ƽ�����귴�⾭γ��
		// Coordinate JWCoor = StaticObject.soProjectSystem.XYToWGS84(XYCoor);
		//
		// Log.d("���귴��", "ԭ���꣺"+JD+","+WD+" -> "+JWCoor.ToString());

		PointF pt = new PointF(e.getX(), e.getY());
		this.m_HandPoint = PubVar.m_Map.getViewConvert().ScreenToMap(pt);
		// PubVar.m_DoEvent.m_GlassView.SetVisible(false);
		Tools.OpenDialog("���ڴ����¼ӵ�λ...", new ICallback() {
			@Override
			public void OnClick(String Str, Object ExtraStr) {

				// ��Map����ת��Ϊ�������ֻ꣬�������ֶ��ӵ�����
				// Coordinate lb84 =
				// StaticObject.soProjectSystem.XYToWGS84(m_HandPoint);
				// m_HandPoint.setGeoX(lb84.getX());m_HandPoint.setGeoY(lb84.getY());
				AddPoint(m_HandPoint, "�ֻ��λ");
			}
		});
	}

	public GestureDetector m_GestureDetector = null;
	private SimpleOnGestureListener m_MyOnGestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			// MouseDown(e);
			return super.onDown(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			// MouseMove(e2);
			// _Pan.MouseDown(e1);
			// _Pan.MouseMove(e2);

			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return super.onSingleTapUp(e);
		}
	};

	@Override
	public void SetOnTouchEvent(MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			MouseDown(event);
			break;
		case MotionEvent.ACTION_UP:
			MouseUp(event);
			break;
		case MotionEvent.ACTION_MOVE:
			MouseMove(event);
			break;
		}
		// this.m_GestureDetector.onTouchEvent(e);
	}

}
