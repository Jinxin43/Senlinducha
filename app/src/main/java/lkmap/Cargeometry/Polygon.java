package lkmap.Cargeometry;

import java.util.List;

import dingtu.ZRoadMap.PubVar;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkPartType;

public class Polygon extends Geometry {
	// ��ʼ��
	public Polygon() {
	}

	// ��������

	// �ڵ�
	private Coordinate _InnerPoint = null;

	public Coordinate getCenterPoint() {
		if (_InnerPoint == null) {
			_InnerPoint = this.GetInnerPoint();
		}
		return _InnerPoint;
	}

	/**
	 * �����ڵ�λ��
	 */
	public void UpdateInnerPoint() {
		_InnerPoint = this.GetInnerPoint();
	}

	// ����
	private double _Length = -1; // ����

	public double getLength(boolean reCal) {
		if (reCal)
			this._Length = -1;
		if (this._Length == -1) {
			double AllLen = 0;
			for (int i = 0; i < this.getPartCount(); i++) {
				AllLen += this.GetPartAt(i).CalLength();
			}
			this._Length = AllLen;
		}
		return this._Length;
	}

	// ���
	private double _Area = 0;

	public double getArea(boolean reCal) {
		if (reCal)
			this._Area = -1;
		if (this._Area == -1) {
			double AllArea = 0;
			for (int i = 0; i < this.getPartCount(); i++) {
				Part part = this.GetPartAt(i);
				AllArea += Math.abs(part.CalArea()) * (part.GetPartType() == lkPartType.enHole ? -1 : 1);
			}
			this._Area = AllArea;
		}

		// ��������Ǹ�ֵ������
		if (_Area < 0) {
			_Area = _Area * -1;
		}
		return this._Area;
	}

	private Coordinate CalcInsideCenter(int partIndex) {
		double area = 0.0;// ��������
		double Gx = 0.0, Gy = 0.0;// ���ĵ�x��y
		for (int i = 1; i <= this.GetPartAt(partIndex).getVertexList().size(); i++) {
			double iLat = this.GetPartAt(partIndex).getVertexList()
					.get(i % this.GetPartAt(0).getVertexList().size())._x;
			double iLng = this.GetPartAt(partIndex).getVertexList()
					.get(i % this.GetPartAt(0).getVertexList().size())._y;
			double nextLat = this.GetPartAt(partIndex).getVertexList().get(i - 1)._x;
			double nextLng = this.GetPartAt(partIndex).getVertexList().get(i - 1)._y;
			double temp = (iLat * nextLng - iLng * nextLat) / 2.0;
			area += temp;
			Gx += temp * (iLat + nextLat) / 3.0;
			Gy += temp * (iLng + nextLng) / 3.0;
		}
		Gx = Gx / area;
		Gy = Gy / area;
		return new Coordinate(Gx, Gy);
	}

	// ������ڵ����
	private Coordinate GetInnerPoint() {
		// return CalcInsideCenter();
		// int partIndex = this.getPartCount() - 1;
		int partIndex = 0;
		// if (this.getPartCount() > 1) {
		// for (int i = this.getPartCount() - 2; i >= 0; i--) {
		// if (this.GetPartAt(partIndex).CalArea() <
		// this.GetPartAt(i).CalArea()) {
		// partIndex = i;
		// }
		// }
		// }

		// ȡ����enHole��part
		// if(this.GetPartAt(this.getPartCount()-1).GetPartType() ==
		// lkPartType.enHole)
		// {
		// if(this.getPartCount()> 1&&
		// this.GetPartAt(this.getPartCount()-2).GetPartType() !=
		// lkPartType.enHole)
		// {
		// partIndex = this.getPartCount()-2;
		// }
		// else
		// {
		// partIndex = 0;
		// }
		// }
		// else if(this.getPartCount()>1)
		// {
		// partIndex = this.getPartCount()-1;
		// }
		int i, j;
		int n = this.GetPartAt(partIndex).getVertexList().size();
		double ai, atmp = 0, xtmp = 0, ytmp = 0;

		int DelDH = 0;

		for (i = n - 1, j = 0; j < n; i = j, j++) {
			Coordinate pti = this.GetPartAt(partIndex).getVertexList().get(i);
			Coordinate ptj = this.GetPartAt(partIndex).getVertexList().get(j);

			if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetDH() != 0) {
				DelDH = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetDH() * 1000000;
				ai = (pti.getX() - DelDH) * ptj.getY() - (ptj.getX() - DelDH) * pti.getY();
				atmp += ai;
				xtmp += (ptj.getX() - DelDH + pti.getX() - DelDH) * ai;
				ytmp += (ptj.getY() + pti.getY()) * ai;
			} else {
				ai = pti.getX() * ptj.getY() - ptj.getX() * pti.getY();
				atmp += ai;
				xtmp += (ptj.getX() + pti.getX()) * ai;
				ytmp += (ptj.getY() + pti.getY()) * ai;
			}

		}

		return new Coordinate(xtmp / (3 * atmp) + DelDH, ytmp / (3 * atmp));
		// return CalcInsideCenter(partIndex);

		// List<Coordinate> IntersectPoint = new ArrayList<Coordinate>();
		// double X1, X2, Y1, Y2; Polyline PL = null;boolean Flip = false;
		// if (this.getEnvelope().getWidth() >= this.getEnvelope().getHeight())
		// {
		// Flip=true;
		// PL = new Polyline();
		// X1 = this.getEnvelope().getMinX() + this.getEnvelope().getWidth() /
		// 2;
		// Y1 = this.getEnvelope().getMinY();
		// Y2 = this.getEnvelope().getMaxY();
		// PL.getVertexList().add(new Coordinate(X1, Y1));
		// PL.getVertexList().add(new Coordinate(X1, Y2));
		// }
		// else
		// {
		// PL = new Polyline();
		// Y1 = this.getEnvelope().getMinY() + this.getEnvelope().getHeight() /
		// 2;
		// X1 = this.getEnvelope().getMinX();
		// X2 = this.getEnvelope().getMaxX();
		// PL.getVertexList().add(new Coordinate(X1, Y1));
		// PL.getVertexList().add(new Coordinate(X2, Y1));
		// }
		//
		// //���㽻��
		// if (this.getBorderLine().Intersect(PL, IntersectPoint))
		// {
		// //�˴�ȱ��������̣�
		// List<Double> DList = new ArrayList<Double>();
		// for (Coordinate Coor : IntersectPoint)
		// {
		// if (Flip)DList.add(Coor.getY()); else DList.add(Coor.getX());
		// }
		// Collections.sort(DList);
		// IntersectPoint.clear();
		// for (double D : DList)
		// {
		// if (Flip) IntersectPoint.add(new Coordinate(X1, D)); else
		// IntersectPoint.add(new Coordinate(D,Y1));
		// }
		//
		// //�������
		// double MaxDist = 0;int Index = -1;
		// for (int i = 0; i < IntersectPoint.size() / 2; i++)
		// {
		// int idx = i * 2;
		// double D = Tools.GetTwoPointDistance(IntersectPoint.get(idx),
		// IntersectPoint.get(idx + 1));
		// if (D > MaxDist)
		// {
		// MaxDist = D; Index = idx;
		// }
		// }
		//
		// if (MaxDist != 0)
		// {
		// return new Coordinate((IntersectPoint.get(Index).getX() +
		// IntersectPoint.get(Index + 1).getX()) / 2,
		// (IntersectPoint.get(Index).getY() + IntersectPoint.get(Index +
		// 1).getY()) / 2);
		// }
		// }
		// return this.getEnvelope().getCenter();
	}

	// ʵ��ѡ��

	///// <summary>�����Ƿ���ָ���ľ��δ��
	///// </summary>
	///// <param name="desEnve"></param>
	///// <returns></returns>
	// public bool IntersectRect(Envelope desRect)
	// {
	// //1���ж�Polygon����Ӿ����Ƿ���ָ�������ཻ�����ཻ�򷵻�false
	// if (desRect.Contain(this.Envelope)) return true;
	// if (!this.Envelope.Intersect(desRect)) return false;

	// //2����Polygon�ֽ�Ϊ����ֱ�߶Σ��ֱ���ָ�������ж��Ƿ��ཻ
	// int CoorCount = this.CoorList.Count;
	// Coordinate StartPoint, EndPoint;
	// for (int i = 0; i <= CoorCount - 1; i++)
	// {
	// StartPoint = (Coordinate)this.CoorList[i];
	// if (i == CoorCount - 1)
	// {
	// EndPoint = (Coordinate)this.CoorList[0];
	// }
	// else
	// {
	// EndPoint = (Coordinate)this.CoorList[i + 1];
	// }
	// if (desRect.IntersectLine(new Line(StartPoint, EndPoint))) return true;
	// }
	// return false;
	// }

	@Override
	public Geometry Clone() {
		Polygon newPolygon = new Polygon();
		for (int p = 0; p < this.getPartCount(); p++) {
			newPolygon.AddPart(this.GetPartAt(p).Clone());
		}
		return newPolygon;
	}

	/*
	 * ������Ƿ������ڣ������������ѡ�� ˼·����ָ���ĵ�������һ�����ߣ���������������������������㣬����ڶ�����ڲ��� Ϊ0��ż�����ڶ�����ⲿ
	 */
	// public boolean isPointInPolygonEx(double px, double py)
	// {
	// if (this.IsSimple()) return
	// isPointInPolygonVertexList(px,py,this.GetAllCoordinateList());
	// else
	// {
	// List<Coordinate> CoorList = this._BorderLine.GetPartAt(0);
	// if (isPointInPolygonVertexList(px,py,CoorList))
	// {
	// int parts = this.getPartCount();
	// for(int i=1;i<parts;i++)
	// {
	// if (isPointInPolygonVertexList(px,py,this.GetPartAt(i))) return false;
	// }
	// return true;
	// }
	// return false;
	// }
	//
	// }
	//
	// private boolean isPointInPolygonVertexList(double px, double
	// py,List<Coordinate> CoorList)
	// {
	// //ArrayList<Double> polygonXA, ArrayList<Double> polygonYA) {
	// boolean isInside = false;
	// double ESP = 1e-9;
	// int count = 0;
	// double linePoint1x;
	// double linePoint1y;
	// double linePoint2x;
	// double linePoint2y;
	// linePoint1x = px;
	// linePoint1y = py;
	//
	// linePoint2x = linePoint1x * 2;
	// linePoint2y = py;
	//
	// for (int i = 0; i < CoorList.size() - 1; i++)
	// {
	// double cx1 = CoorList.get(i).getX();
	// double cy1 = CoorList.get(i).getY();
	// double cx2 = CoorList.get(i+1).getX();
	// double cy2 = CoorList.get(i+1).getY();
	// if (isPointOnLine(px, py, cx1, cy1, cx2, cy2)) return true;
	//
	//
	// if (Math.abs(cy2 - cy1)< ESP) continue;
	//
	// if (isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x,
	// linePoint2y))
	// {
	// if (cy1 > cy2) count++;
	// }
	// else if (isPointOnLine(cx2, cy2, linePoint1x, linePoint1y,linePoint2x,
	// linePoint2y))
	// {
	// if (cy2 > cy1)count++;
	// }
	// else if (isIntersect(cx1, cy1, cx2, cy2, linePoint1x, linePoint1y,
	// linePoint2x, linePoint2y))
	// {
	// count++;
	// }
	// }
	// if (count % 2 == 1) isInside = true;
	// return isInside;
	// }
	//
	// private boolean isPointOnLine(double px0, double py0, double px1,double
	// py1, double px2, double py2)
	// {
	// boolean flag = false;
	// double ESP = 1e-9;
	// if ((Math.abs(Multiply(px0, py0, px1, py1, px2, py2)) < ESP) && ((px0 -
	// px1) * (px0 - px2) <= 0) && ((py0 - py1) * (py0 - py2) <= 0))
	// {
	// flag = true;
	// }
	// return flag;
	// }
	// private double Multiply(double px0, double py0, double px1, double py1,
	// double px2, double py2)
	// {
	// return ((px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0));
	// }

	public boolean isPointOnBorder(double px, double py) {
		for (int i = 0; i < this.getPartCount(); i++) {
			List<Coordinate> vertexList = this.GetPartAt(i).getVertexList();
			for (int j = 0; j < vertexList.size(); j++) {
				Coordinate coordinate = vertexList.get(j);
				if (coordinate.getX() == px && coordinate.getY() == py) {
					return true;
				}
			}

		}
		return false;
	}

	/*
	 * ������Ƿ������ڣ������������ѡ�� ˼·����ָ���ĵ�������һ�����ߣ���������������������������㣬����ڶ�����ڲ��� Ϊ0��ż�����ڶ�����ⲿ
	 */
	@Override
	public boolean HitTest(Coordinate HitPoint, double Tolerance, Boolean isBGLayer) {
		// 1���жϵ��Ƿ��ڶ���ε���Ӿ����ڲ�
		if (!this.getEnvelope().ContainsPoint(HitPoint))
			return false;

		// 2���ֲ����ж�
		boolean InClick = false; // ��enPoly�ڲ�����
		for (int i = 0; i < this.getPartCount(); i++) {
			Part part = this.GetPartAt(i);
			boolean HitOK = part.ContainsPoint(HitPoint);
			if (HitOK) {
				InClick = true;
				if (isBGLayer && InClick) {
					return InClick;
				}
				if (part.GetPartType() == lkPartType.enHole) {
					return false;
				}
			}
		}
		return InClick;
	}

	@Override
	public boolean Offset(double OffsetX, double OffsetY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public lkGeoLayerType GetType() {
		// TODO Auto-generated method stub
		return lkGeoLayerType.enPolygon;
	}

	/**
	 * ��պϴ���
	 */
	public void Closed() {
		for (int p = 0; p < this.getPartCount(); p++) {
			this.GetPartAt(p).Closed();
		}
	}
}
