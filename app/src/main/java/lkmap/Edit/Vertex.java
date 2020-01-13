package lkmap.Edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import android.view.MotionEvent;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Line;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Enum.lkDrawType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Enum.lkVertexEditType;
import lkmap.Layer.GeoLayer;
import lkmap.Map.Param;
import lkmap.MapControl.IOnPaint;
import lkmap.MapControl.IOnTouchCommand;
import lkmap.MapControl.MapControl;
import lkmap.MapControl.Pan;
import lkmap.Tools.Tools;
import lkmap.UnRedo.IURDataItem_Vertex;
import lkmap.UnRedo.IUnRedo;
import lkmap.UnRedo.UnRedoDataItem;
import lkmap.UnRedo.UnRedoParaStru;

public class Vertex implements IOnTouchCommand, IOnPaint {
	// 节点操作类型
	private lkVertexEditType m_VertexEditType = lkVertexEditType.enUnkonw;

	private boolean isBGLayer = false;
	private List<HashMap<String, Object>> sharePointGeos = new ArrayList<HashMap<String, Object>>();

	public void SetVertexEditType(lkVertexEditType vet) {
		this.m_VertexEditType = vet;

		// 确定选的实体
		Param GeoLayerName = new Param();
		Param SYSID = new Param();
		if (lkmap.Tools.Tools.GetSelectOneObjectInfo(GeoLayerName, SYSID)) {
			GeoLayer pGeoLayer = this._MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorEditingData)
					.GetLayerById(GeoLayerName.getStringValue());
			if (pGeoLayer != null) {
				if (pGeoLayer.getType() == lkGeoLayerType.enPolyline
						|| pGeoLayer.getType() == lkGeoLayerType.enPolygon) {
					this.m_EditLayerName = GeoLayerName.getStringValue();
					this.m_Geometry = pGeoLayer.getDataset().GetGeometry(SYSID.getInt());
					this.m_GeoLayer = pGeoLayer;
					return;
				}
			} else {
				// 如果不是数据编辑图层，则在矢量底图层查找
				pGeoLayer = this._MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorBackground)
						.GetLayerById(GeoLayerName.getStringValue());
				if (pGeoLayer != null) {
					this.m_EditLayerName = GeoLayerName.getStringValue();
					this.m_Geometry = pGeoLayer.getDataset().GetGeometry(SYSID.getInt());
					this.m_GeoLayer = pGeoLayer;
					isBGLayer = true;
					return;
				}

			}
		}

		lkmap.Tools.Tools.ShowMessageBox("请在可编辑图层中选择需要编辑的实体！");
		this.m_Geometry = null;
	}

	// 正编辑的线，面
	private Geometry m_Geometry = null;

	// 编辑图层名称
	private String m_EditLayerName = "";
	private GeoLayer m_GeoLayer = null;

	// 移动节时的坐标索引值
	private int m_VertexMoveIndex = -1;

	private int m_VertexMovePartIndex = -1;

	// 选择容忍距离
	private double m_TolerancePix = Tools.DPToPix(15); //

	// 节点移动时的老坐标
	Coordinate m_OldCoordinate = null;

	private MapControl _MapControl = null;
	private Pan m_Pan = null;

	public Vertex(MapControl mapControl) {
		this._MapControl = mapControl;
		this.m_GestureDetector = new GestureDetector(PubVar.m_MapControl.getContext(), this.m_MyOnGestureListener);
		this.m_Pan = new Pan(PubVar.m_MapControl);
	}

	public void MouseDown(MotionEvent e) {
		if (this.m_Geometry == null)
			return;

		// 单击点坐标
		Coordinate HitPoint = this._MapControl.getMap().getViewConvert().ScreenToMap(e.getX(), e.getY());

		// 处理节点移动
		if (this.m_VertexEditType == lkVertexEditType.enMove) {
			// 点击处的节点索引
			for (int p = 0; p < this.m_Geometry.getPartCount(); p++) {
				Part part = this.m_Geometry.GetPartAt(p);
				int nearestVertexIndex = part.HitVertexTest(HitPoint,
						this._MapControl.getMap().ToMapDistance(this.m_TolerancePix));
				if (nearestVertexIndex != -1) {
					this.m_VertexMovePartIndex = p;
					this.m_VertexMoveIndex = nearestVertexIndex;
					this.m_OldCoordinate = part.getVertexList().get(this.m_VertexMoveIndex).Clone();
					this.m_VertexMoving = true;
					sharePointGeos = m_GeoLayer.getDataset().getSharePointPoly(m_OldCoordinate, m_Geometry.getSysId(),
							true);
					break;
				}
			}
		}

		// 处理节点的增、删
		if (this.m_VertexEditType == lkVertexEditType.enAdd) {
			for (int p = 0; p < this.m_Geometry.getPartCount(); p++) {
				Part part = this.m_Geometry.GetPartAt(p);
				int SegmentIndex = part.HitSegmentTest(HitPoint,
						this._MapControl.getMap().ToMapDistance(this.m_TolerancePix));
				if (SegmentIndex != -1) {
					this.m_Geometry.SetEdited(true);
					int BeforeVertexIndex = SegmentIndex + 1;

					// 改正，也就是在线上加点
					if ((SegmentIndex) < part.getVertexList().size()) {
						Line l = new Line(part.getVertexList().get(SegmentIndex),
								part.getVertexList().get(SegmentIndex + 1));
						Param LineInnerPoint = new Param();
						Coordinate PerPoint = new Coordinate();
						double d = l.PointToLineNearestDistance(HitPoint, LineInnerPoint, PerPoint);
						if (LineInnerPoint.getBoolean()) {

							HitPoint.setX(PerPoint.getX());
							HitPoint.setY(PerPoint.getY());
							part.getVertexList().add(BeforeVertexIndex, HitPoint);

							// 更新索引
							part.UpdateEnvelope();
							// this.m_GeoLayer.getDataset().UpdateLayerIndex(this.m_Geometry);
							this.m_Geometry.CalEnvelope();
							this.m_Geometry.SetEdited(true);
							// 实时保存
							v1_CGpsDataObject co = new v1_CGpsDataObject();
							co.SetDataset(this.m_GeoLayer.getDataset());
							co.SetSYS_ID(this.m_Geometry.getSysId());
							double Len = 0, Area = 0;
							if (this.m_GeoLayer.getType() == lkGeoLayerType.enPolyline)
								Len = ((Polyline) this.m_Geometry).getLength(true);
							if (this.m_GeoLayer.getType() == lkGeoLayerType.enPolygon)
								Area = ((Polygon) this.m_Geometry).getArea(true);
							co.SaveGeoToDb(this.m_Geometry, Len, Area);

							// 加入回退栈中
							UnRedoParaStru UnRedoPara = new UnRedoParaStru();
							UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enVertexAddDel;

							IURDataItem_Vertex urVertex = new IURDataItem_Vertex();
							urVertex.LayerId = this.m_EditLayerName;
							urVertex.ObjectId = this.m_Geometry.getSysId();
							urVertex.PartIndex = p;
							urVertex.VertexIndex = BeforeVertexIndex;
							urVertex.Coor1 = HitPoint.Clone();

							UnRedoDataItem dataItem = new UnRedoDataItem();
							dataItem.Type = lkReUndoFlag.enUndo;
							dataItem.DataList.add(urVertex);
							UnRedoPara.DataItemList.add(dataItem);

							if (isBGLayer) {
								sharePointGeos = m_GeoLayer.getDataset().getShareTwoPointPoly(
										part.getVertexList().get(SegmentIndex),
										part.getVertexList().get(SegmentIndex + 1), m_Geometry.getSysId(), true);
								for (HashMap<String, Object> hm : sharePointGeos) {

									Geometry geometry = (Geometry) hm.get("geo");
									Part movePart = geometry.GetPartAt((Integer) hm.get("partIndex"));
									int vertexIndex = (Integer) hm.get("vertexIndex") + 1;
									movePart.getVertexList().add(vertexIndex, HitPoint.Clone());

									v1_CGpsDataObject co1 = new v1_CGpsDataObject();
									co1.SetDataset(m_GeoLayer.getDataset());
									co1.SetSYS_ID(geometry.getSysId());
									double Len1 = 0, Area1 = 0;
									Area1 = ((Polygon) this.m_Geometry).getArea(true);
									co1.SaveGeoToDb(this.m_Geometry, Len1, Area1);

									IURDataItem_Vertex urVertex1 = new IURDataItem_Vertex();
									urVertex1.LayerId = this.m_EditLayerName;
									urVertex1.ObjectId = geometry.getSysId();
									urVertex1.PartIndex = (Integer) hm.get("partIndex");
									urVertex1.VertexIndex = vertexIndex;
									urVertex1.Coor1 = HitPoint.Clone();

									UnRedoDataItem dataItem1 = new UnRedoDataItem();
									dataItem.Type = lkReUndoFlag.enUndo;
									dataItem.DataList.add(urVertex1);
									UnRedoPara.DataItemList.add(dataItem1);
								}
							}

							IUnRedo.AddHistory(UnRedoPara);

							this._MapControl.getMap().FastRefresh();
							return;
						}
					}

				}
			}
		}

		if (this.m_VertexEditType == lkVertexEditType.enDelete) {
			// 点击处的节点索引
			for (int p = 0; p < this.m_Geometry.getPartCount(); p++) {
				Part part = this.m_Geometry.GetPartAt(p);
				if (part.getVertexList().size() <= 4)
					continue; // 起止点闭合的情况
				int vertexIndex = part.HitVertexTest(HitPoint,
						this._MapControl.getMap().ToMapDistance(this.m_TolerancePix));
				if (vertexIndex != -1) {
					int VertexType = 0; // 1-首，2-至
					if (vertexIndex == 0)
						VertexType = 1;
					if (vertexIndex == part.getVertexList().size() - 1)
						VertexType = 2;

					this.m_Geometry.SetEdited(true);
					Coordinate deleteVertexCoor = part.getVertexList().get(vertexIndex).Clone();
					part.getVertexList().remove(vertexIndex);

					if (this.m_GeoLayer.getType() == lkGeoLayerType.enPolygon) {
						// 删除首尾节点情况
						if (VertexType == 1) // 起点
						{
							Coordinate endPoint = part.getVertexList().get(part.getVertexList().size() - 1);
							endPoint.setX(part.getVertexList().get(0).getX());
							endPoint.setY(part.getVertexList().get(0).getY());
						}
						if (VertexType == 2) // 止点
						{
							Coordinate endPoint = part.getVertexList().get(part.getVertexList().size() - 1);
							part.getVertexList().get(0).setX(endPoint.getX());
							part.getVertexList().get(0).setY(endPoint.getY());
						}

						if (isBGLayer) {
							sharePointGeos = m_GeoLayer.getDataset().getSharePointPoly(deleteVertexCoor,
									m_Geometry.getSysId(), true);
							deleteBGGeometry();

						}
					}

					// 更新索引
					part.UpdateEnvelope();
					// this.m_GeoLayer.getDataset().UpdateLayerIndex(this.m_Geometry);
					this.m_Geometry.CalEnvelope();
					this.m_Geometry.SetEdited(true);
					// 实时保存
					v1_CGpsDataObject co = new v1_CGpsDataObject();
					co.SetDataset(this.m_GeoLayer.getDataset());
					co.SetSYS_ID(this.m_Geometry.getSysId());
					double Len = 0, Area = 0;
					if (this.m_GeoLayer.getType() == lkGeoLayerType.enPolyline)
						Len = ((Polyline) this.m_Geometry).getLength(true);
					if (this.m_GeoLayer.getType() == lkGeoLayerType.enPolygon)
						Area = ((Polygon) this.m_Geometry).getArea(true);
					co.SaveGeoToDb(this.m_Geometry, Len, Area);

					// 加入回退栈中
					UnRedoParaStru UnRedoPara = new UnRedoParaStru();
					UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enVertexAddDel;

					IURDataItem_Vertex urVertex = new IURDataItem_Vertex();
					urVertex.LayerId = this.m_EditLayerName;
					urVertex.ObjectId = this.m_Geometry.getSysId();
					urVertex.PartIndex = p;
					urVertex.VertexIndex = vertexIndex;
					urVertex.Coor1 = deleteVertexCoor.Clone();

					UnRedoDataItem dataItem = new UnRedoDataItem();
					dataItem.Type = lkReUndoFlag.enRedo;
					dataItem.DataList.add(urVertex);
					UnRedoPara.DataItemList.add(dataItem);

					if (isBGLayer) {
						for (HashMap<String, Object> hm : sharePointGeos) {
							Geometry geometry = (Geometry) hm.get("geo");
							Part movePart = geometry.GetPartAt((Integer) hm.get("partIndex"));
							int pvertexIndex = (Integer) hm.get("vertexIndex");
							Coordinate deleteVertexCoor1 = (Coordinate) hm.get("coor");

							IURDataItem_Vertex urVertex1 = new IURDataItem_Vertex();
							urVertex1.LayerId = this.m_EditLayerName;
							urVertex1.ObjectId = geometry.getSysId();
							urVertex1.PartIndex = (Integer) hm.get("partIndex");
							urVertex1.VertexIndex = pvertexIndex;
							urVertex1.Coor1 = deleteVertexCoor1.Clone();

							UnRedoDataItem dataItem1 = new UnRedoDataItem();
							dataItem1.Type = lkReUndoFlag.enRedo;
							dataItem1.DataList.add(urVertex1);
							UnRedoPara.DataItemList.add(dataItem1);
						}
					}

					IUnRedo.AddHistory(UnRedoPara);
					this._MapControl.getMap().FastRefresh();

					return;
				}
			}
		}

	}

	private boolean m_VertexMoving = false;

	public void MouseMove(MotionEvent e) {
		if (this.m_VertexMoving) {
			Coordinate newPoint = this._MapControl.getMap().getViewConvert().ScreenToMap(e.getX(), e.getY());
			Part movePart = this.m_Geometry.GetPartAt(this.m_VertexMovePartIndex);
			Coordinate movePoint = movePart.getVertexList().get(this.m_VertexMoveIndex);
			movePoint.setX(newPoint.getX());
			movePoint.setY(newPoint.getY());

			if (this.m_GeoLayer.getType() == lkGeoLayerType.enPolygon) {
				// 判读是否为起止端点
				if (this.m_VertexMoveIndex == 0) {
					Coordinate endPoint = movePart.getVertexList().get(movePart.getVertexList().size() - 1);
					endPoint.setX(newPoint.getX());
					endPoint.setY(newPoint.getY());
				}
				if (this.m_VertexMoveIndex == movePart.getVertexList().size() - 1) {
					Coordinate endPoint = movePart.getVertexList().get(0);
					endPoint.setX(newPoint.getX());
					endPoint.setY(newPoint.getY());
				}
			}

			if (isBGLayer && sharePointGeos.size() > 0) {
				moveBGGeometry(newPoint);
			}
			this.m_Geometry.SetEdited(true);
			this._MapControl.invalidate();
		}

	}

	private void AddBGPoint(Part part, Coordinate newPoint) {
		for (HashMap<String, Object> hm : sharePointGeos) {

			Geometry geometry = (Geometry) hm.get("geo");
			Part movePart = geometry.GetPartAt((Integer) hm.get("partIndex"));
			int vertexIndex = (Integer) hm.get("vertexIndex") + 1;

			movePart.getVertexList().add(vertexIndex, newPoint.Clone());

		}
	}

	private void moveBGGeometry(Coordinate newPoint) {
		for (HashMap<String, Object> hm : sharePointGeos) {
			Geometry geometry = (Geometry) hm.get("geo");
			Part movePart = geometry.GetPartAt((Integer) hm.get("partIndex"));
			int vertexIndex = (Integer) hm.get("vertexIndex");
			Coordinate movePoint = movePart.getVertexList().get(vertexIndex);
			movePoint.setX(newPoint.getX());
			movePoint.setY(newPoint.getY());
			geometry.SetEdited(true);

			if (vertexIndex == 0) {
				Coordinate endPoint = movePart.getVertexList().get(movePart.getVertexList().size() - 1);
				endPoint.setX(newPoint.getX());
				endPoint.setY(newPoint.getY());
			}
			if (vertexIndex == movePart.getVertexList().size() - 1) {
				Coordinate endPoint = movePart.getVertexList().get(0);
				endPoint.setX(newPoint.getX());
				endPoint.setY(newPoint.getY());
			}
		}
	}

	private void deleteBGGeometry() {
		for (HashMap<String, Object> hm : sharePointGeos) {
			Geometry geometry = (Geometry) hm.get("geo");
			Part movePart = geometry.GetPartAt((Integer) hm.get("partIndex"));
			int vertexIndex = (Integer) hm.get("vertexIndex");

			int VertexType = 0; // 1-首，2-至
			if (vertexIndex == 0)
				VertexType = 1;
			if (vertexIndex == movePart.getVertexList().size() - 1)
				VertexType = 2;

			movePart.getVertexList().remove(vertexIndex);

			if (VertexType == 1) // 起点
			{
				Coordinate endPoint = movePart.getVertexList().get(movePart.getVertexList().size() - 1);
				endPoint.setX(movePart.getVertexList().get(0).getX());
				endPoint.setY(movePart.getVertexList().get(0).getY());
			}
			if (VertexType == 2) // 止点
			{
				Coordinate endPoint = movePart.getVertexList().get(movePart.getVertexList().size() - 1);
				movePart.getVertexList().get(0).setX(endPoint.getX());
				movePart.getVertexList().get(0).setY(endPoint.getY());
			}

			movePart.UpdateEnvelope();
			// this.m_GeoLayer.getDataset().UpdateLayerIndex(this.m_Geometry);
			geometry.CalEnvelope();
			geometry.SetEdited(true);

			v1_CGpsDataObject co = new v1_CGpsDataObject();
			co.SetDataset(this.m_GeoLayer.getDataset());
			co.SetSYS_ID(geometry.getSysId());
			double Len = 0, Area = 0;
			if (this.m_GeoLayer.getType() == lkGeoLayerType.enPolyline)
				Len = ((Polyline) geometry).getLength(true);
			if (this.m_GeoLayer.getType() == lkGeoLayerType.enPolygon)
				Area = ((Polygon) geometry).getArea(true);
			co.SaveGeoToDb(geometry, Len, Area);

		}
	}

	public void MouseUp(MotionEvent e) {
		if (this.m_VertexMoving) {
			// //加入回退栈中
			// UnRedoParaStru UnRedoPara = new UnRedoParaStru();
			// UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enVertexMove;
			// UnRedoPara.Type = lkmap.Enum.lkReUndoFlag.enRedo;
			// UnRedoPara.ParaList.add(this.m_EditLayerName + "," +
			// this.m_Geometry.getSysId() + "," +this.m_VertexMovePartIndex
			// +","+ this.m_VertexMoveIndex + "," +
			// this.m_OldCoordinate.getX() + ":" + this.m_OldCoordinate.getY() +
			// "," +
			// this.m_Geometry.GetPartAt(this.m_VertexMovePartIndex).getVertexList().get(this.m_VertexMoveIndex).getX()
			// + ":" +
			// this.m_Geometry.GetPartAt(this.m_VertexMovePartIndex).getVertexList().get(this.m_VertexMoveIndex).getY());
			//
			// IUnRedo.AddHistory(UnRedoPara);

			this.m_VertexMoving = false;

			// 更新图层索引
			this.m_Geometry.GetPartAt(this.m_VertexMovePartIndex).UpdateEnvelope();
			this.m_Geometry.CalEnvelope();

			// 实时保存
			v1_CGpsDataObject co = new v1_CGpsDataObject();
			co.SetDataset(this.m_GeoLayer.getDataset());
			co.SetSYS_ID(this.m_Geometry.getSysId());
			double Len = 0, Area = 0;
			if (this.m_GeoLayer.getType() == lkGeoLayerType.enPolyline)
				Len = ((Polyline) this.m_Geometry).getLength(true);
			if (this.m_GeoLayer.getType() == lkGeoLayerType.enPolygon)
				Area = ((Polygon) this.m_Geometry).getArea(true);
			co.SaveGeoToDb(this.m_Geometry, Len, Area);

			// 加入回退栈中
			UnRedoParaStru UnRedoPara = new UnRedoParaStru();
			UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enVertexMove;

			IURDataItem_Vertex urVertex = new IURDataItem_Vertex();
			urVertex.LayerId = this.m_EditLayerName;
			urVertex.ObjectId = this.m_Geometry.getSysId();
			urVertex.PartIndex = this.m_VertexMovePartIndex;
			urVertex.VertexIndex = this.m_VertexMoveIndex;
			urVertex.Coor1 = this.m_OldCoordinate.Clone();
			urVertex.Coor2 = this.m_Geometry.GetPartAt(this.m_VertexMovePartIndex).getVertexList()
					.get(this.m_VertexMoveIndex).Clone();

			UnRedoDataItem dataItem = new UnRedoDataItem();
			dataItem.Type = lkReUndoFlag.enUndo;
			dataItem.DataList.add(urVertex);

			if (isBGLayer && sharePointGeos.size() > 0) {
				for (HashMap<String, Object> hm : sharePointGeos) {
					Geometry geometry = (Geometry) hm.get("geo");
					Part movePart = geometry.GetPartAt((Integer) hm.get("partIndex"));
					Coordinate movePoint = (Coordinate) hm.get("coor");
					int vertIndex = (Integer) hm.get("vertexIndex");
					// 更新图层索引
					movePart.UpdateEnvelope();
					geometry.CalEnvelope();

					// 实时保存
					v1_CGpsDataObject co1 = new v1_CGpsDataObject();
					co1.SetDataset(m_GeoLayer.getDataset());
					co1.SetSYS_ID(geometry.getSysId());
					double Len1 = 0, Area1 = 0;
					if (m_GeoLayer.getType() == lkGeoLayerType.enPolygon)
						Area1 = ((Polygon) geometry).getArea(true);
					co1.SaveGeoToDb(geometry, Len1, Area1);

					IURDataItem_Vertex urVertex1 = new IURDataItem_Vertex();
					urVertex1.LayerId = this.m_EditLayerName;
					urVertex1.ObjectId = geometry.getSysId();
					urVertex1.PartIndex = (Integer) hm.get("partIndex");
					urVertex1.VertexIndex = vertIndex;
					urVertex1.Coor1 = movePoint.Clone();
					urVertex1.Coor2 = movePart.getVertexList().get(vertIndex).Clone();

					dataItem.DataList.add(urVertex1);
				}
			}

			UnRedoPara.DataItemList.add(dataItem);
			IUnRedo.AddHistory(UnRedoPara);
			this._MapControl.getMap().FastRefresh();
		}

	}

	public GestureDetector m_GestureDetector = null;
	private SimpleOnGestureListener m_MyOnGestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			MouseDown(e);
			return super.onDown(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (m_VertexEditType == lkVertexEditType.enMove && m_VertexMoving == true) {
				MouseMove(e2);
			} else {
				m_Pan.MouseDown(e1);
				m_Pan.MouseMove(e2);
			}
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {

			return super.onSingleTapUp(e);
		}

	};

	@Override
	public void SetOnTouchEvent(MotionEvent e) {
		if ((e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
			MouseUp(e);
			this.m_Pan.MouseUp(e);
		}
		this.m_GestureDetector.onTouchEvent(e);
	}

	@Override
	public void OnPaint(Canvas canvas) {
		this.m_Pan.OnPaint(canvas);
		if (PubVar.m_Map.getInvalidMap())
			return;

		if (this.m_VertexMoving) {
			this.m_Geometry.getSymbol().Draw(this._MapControl.getMap(), canvas, this.m_Geometry, 0, 0,
					lkDrawType.enSelected_Editing);
		}
	}

}
