package lkmap.Layer;

import android.graphics.Canvas;
import lkmap.Cargeometry.Geometry;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.Selection;
import lkmap.Enum.lkDrawType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Enum.lkSelectionType;
import lkmap.Map.Map;
import lkmap.Render.IRender;

public class GeoLayer {
	// Map����
	private Map _Map = null;

	public Map getMap() {
		return _Map;
	}

	// ����\����ͼ������ͣ�lkGeoLayerType����
	private lkGeoLayerType _Type = lkGeoLayerType.enUnknow;

	public lkGeoLayerType getType() {
		return _Type;
	}

	public void setType(lkGeoLayerType value) {
		_Type = value;
	}

	// ͼ�������һ��Ϊ����
	private String _AliasName = "";

	public String GetAliasName() {
		return this._AliasName;
	}

	public void SetAliasName(String _AliasName) {
		this._AliasName = _AliasName;
	}

	// ����ͼ������
	private String _Id = "";

	public String getId() {
		return _Id;
	}

	public void setId(String id) {
		_Id = id;
	}

	// ͼ����С��ʾ����
	private double _VisibleScaleMin = 0;

	public double getVisibleScaleMin() {
		return _VisibleScaleMin;
	}

	public void setVisibleScaleMin(double value) {
		_VisibleScaleMin = value;
	}

	// ͼ�������ʾ����
	private double _VisibleScaleMax = Double.MAX_VALUE;

	public double getVisibleScaleMax() {
		return _VisibleScaleMax;
	}

	public void setVisibleScaleMax(double value) {
		_VisibleScaleMax = value;
	}

	// ͼ���ѡ����
	private boolean _Selectable = true;

	public boolean getSelectable() {
		return _Selectable;
	}

	public void setSelectable(boolean value) {
		_Selectable = value;
	}

	// ͼ��ɱ༭��
	private boolean _Editable = true;

	public boolean getEditable() {
		return _Editable;
	}

	public void setEditable(boolean value) {
		_Editable = value;
	}

	// ͼ��ɲ�׽��
	private boolean _Snapable = true;

	public boolean getSnapable() {
		return _Snapable;
	}

	public void setSnapable(boolean value) {
		_Snapable = value;
	}

	// ͼ��ɼ���
	private boolean _Visible = true;

	public boolean getVisible() {
		return _Visible;
	}

	public void setVisible(boolean value) {
		_Visible = value;
	}

	// �õ�ͼ��ķ��Ż���Ϣ
	private IRender _Render = null;

	public IRender getRender() {
		return _Render;
	}

	public void setRender(IRender value) {
		_Render = value;
	}

	// ѡ��(����)����Ա����
	// ��ʾ����������ͨ���������������β���Ҫ��ʾ��ʵ��
	private String _DisplayFilter = "";

	public String getDispplayFilter() {
		return _DisplayFilter;
	}

	// ��ǰͼ�������ڱ���ʾ��ѡ��
	private Selection _ShowSelection = new Selection();

	public Selection getShowSelection() {
		return _ShowSelection;
	}

	// ��ǰͼ�������ڱ�ѡ���ѡ��
	private Selection _SelSelection = new Selection();

	public Selection getSelSelection() {
		return _SelSelection;
	}

	// ͼ��ʵ�����ݼ�
	private Dataset _Dataset = null;

	public Dataset getDataset() {
		return _Dataset;
	}

	public void setDataset(Dataset value) {
		_Dataset = value;
		_SelSelection.setDataset(_Dataset);
		_ShowSelection.setDataset(_Dataset);
		_Dataset.setBindGeoLayer(this);
	}

	public GeoLayer(Map map) {
		this._Map = map;
		this._ShowSelection.setType(lkSelectionType.enShow);
		this._SelSelection.setType(lkSelectionType.enSelect);
	}

	// ͼ��ˢ��
	public void Refresh() {
		// ��ʼ��ѯ
		// this._Dataset.QueryByExtend(this._Map.getExtend(),
		// this._ShowSelection);
	}

	public void FastRefresh() {
		// ����ѡ��
		this.DrawSelection(this._ShowSelection);
	}

	// ����ѡ��
	public void DrawSelection(Selection pSelection) {
		this.DrawSelection(pSelection, this._Map.getDisplayGraphic(), 0, 0);
	}

	/**
	 * ����ѡ�񼯺�
	 * 
	 * @param pSelection
	 *            ����
	 * @param g
	 *            ����
	 * @param OffsetX
	 *            ƫ���������أ�
	 * @param OffsetY
	 *            ƫ���������أ�
	 */
	public void DrawSelection(Selection pSelection, Canvas g, int OffsetX, int OffsetY) {
		Geometry pGeometry = null;
		for (int Index : pSelection.getGeometryIndexList()) {
			pGeometry = this._Dataset.GetGeometry(Index);
			if (pGeometry == null)
				continue;
			if (pGeometry.getStatus() == lkGeometryStatus.enDelete)
				continue; // ����ɾ����ʶ�Ĳ���ʾ

			// ������ʾ����ShowSelection)�������ʵ������enSelectѡ���У�����ʾ������enSelect����ʾ
			if (pSelection.getType() == lkSelectionType.enShow) {
				if (this._SelSelection.getGeometryIndexList().indexOf(Index) >= 0)
					continue;
				if (pGeometry.getSymbol() == null) {
					lkmap.Tools.Tools.ShowMessageBox(this.GetAliasName());
					return;
				} else {
					pGeometry.getSymbol().Draw(this._Map, g, pGeometry, OffsetX, OffsetY, lkDrawType.enNormal);
				}
			}

			// ����ǰѡ�еļ���
			if (pSelection.getType() == lkSelectionType.enSelect) {
				// ����ѡ�񼯵���ʾ���֣�����Ļ��ͼ����ʾ
				if (!this.getMap().getExtend().Intersect(pGeometry.getEnvelope()))
					continue;

				if (this.getDataset().getDataSource().getEditing()) {
					pGeometry.getSymbol().Draw(this._Map, g, pGeometry, OffsetX, OffsetY,
							lkDrawType.enSelected_Editing);
				} else {
					pGeometry.getSymbol().Draw(this._Map, g, pGeometry, OffsetX, OffsetY,
							lkDrawType.enSelected_NoEditing);
				}
			}

		}
	}

	// �����ע���ֿ���д��Ŀ���Ƿ�ֹ��ע��Ϣ�������㸲��
	public void DrawSelectionLabel(Selection pSelection, Canvas g, int OffsetX, int OffsetY) {
		Geometry pGeometry = null;
		for (int Index : pSelection.getGeometryIndexList()) {
			pGeometry = this._Dataset.GetGeometry(Index);
			if (pGeometry == null)
				continue;
			if (pGeometry.getStatus() == lkGeometryStatus.enDelete)
				continue;
			// ����ѡ�񼯵���ʾ���֣�Ҳ������ShowSelection�е���ʾ������ShowSelection�о��ǲ���Ҫ����Ļ����ʾ�Ĳ���
			// if (this.ShowSelection.GeometryIndexList.IndexOf(Index) < 0)
			// continue;
			// if (this.SelSelection.Type!= lkSelectionType.enSelect)
			// {
			// if (this.SelSelection.GeometryIndexList.IndexOf(Index) >= 0)
			// continue;
			// }
			if (pGeometry.getSymbol() == null) {
				lkmap.Tools.Tools.ShowMessageBox(pGeometry.getTag());
				return;
			}
			pGeometry.getSymbol().DrawLabel(this._Map, g, pGeometry, OffsetX, OffsetY, pSelection.getType());
		}
	}

	// #region ͼ����Ⱦ

	///// <summary>��ʵ�壬Ҳ����ͼ����Ⱦ�������������Ŀ���Ǳ�ѡ�е�ʵ����������
	///// </summary>
	///// <param name="RenderObjectType">(1-����ʾ��δѡ��ʵ�壬2-ѡ��ʵ��)</param>
	// public void Render(int RenderObjectType)
	// {
	// if (RenderObjectType == 1) //1-����ʾ��δѡ��ʵ��
	// {
	// foreach (int index in this.ShowObjectList)
	// {
	// if (this.SelectObjectList.IndexOf(index) >= 0) continue; //Ŀ�ģ�ѡ�е�ʵ�岻��ʾ
	// if (this.LayerType == 0) this.DrawPoint(index, 1); //����
	// if (this.LayerType == 1) this.DrawPolyline(index, 1); //����
	// if (this.LayerType == 2) this.DrawPolygon(index, 1); //����
	// }
	// }

	// if (RenderObjectType == 2) //2-ѡ��ʵ��
	// {
	// foreach (int index in this.SelectObjectList)
	// {
	// if (this.LayerType == 0) { this.DrawPoint(index, 1);
	// this.DrawPoint(index, 2); } //����
	// if (this.LayerType == 1) { this.DrawPolyline(index, 1);
	// this.DrawPolyline(index, 2); } //����
	// if (this.LayerType == 2) { this.DrawPolygon(index, 1);
	// this.DrawPolygon(index, 2); } //����
	// }
	// }
	// }

	// #region ��Ⱦ���
	///// <summary>����ʵ��
	///// </summary>
	///// <param name="ObjectIndex">ʵ������</param>
	///// <param name="type">���ͣ�1-������2-ѡ��</param>
	// private void DrawPoint(int ObjectIndex, int type)
	// {
	// this.DrawPoint(Map.DisplayGraphic,ObjectIndex, type, 0f, 0f);
	// }
	// public void DrawPoint(Graphics g,int ObjectIndex, int type /*��ʾ����: 1-����
	// 2-ѡ��*/, float OffsetX, float OffsetY)
	// {
	// //��Ҫ�ػ���ʵ��
	// CartoGeometry.IGeometry pGeometry = this.GetObject(ObjectIndex);
	// if (pGeometry.LKStatusMode == lkGeometryStatus.Delete) return;
	// //��ƽ������ת������������
	// float PointX = 0; float PointY = 0;
	// this.Map.MapToScreen(pGeometry.Items[0], out PointX, out PointY);

	// //�õ�ͼ�����Ⱦ��
	// PointRender PR = (PointRender)this.LayerRender;
	// PR.Draw(this, pGeometry, g, PointX, PointY,OffsetX,OffsetY, type);
	// }
	// #endregion

	// #region ��Ⱦ�߲�

	///// <summary>����ʵ��
	///// </summary>
	///// <param name="ObjectIndex">ʵ������</param>
	///// <param name="type">���ͣ�1-������2-ѡ��</param>
	// private void DrawPolyline(int ObjectIndex, int type)
	// {
	// this.DrawPolyline(Map.DisplayGraphic,ObjectIndex, type, 0f, 0f);
	// }
	// public void DrawPolyline(Graphics g,int ObjectIndex, int type, float
	///// OffsetX, float OffsetY)
	// {
	// //�õ���ʵ��
	// CartoGeometry.IGeometry pGeometry = this.GetObject(ObjectIndex);
	// if (pGeometry.LKStatusMode == lkGeometryStatus.Delete) return;

	// //��ȡ��Ⱦ��
	// LineRender LR = (LineRender)this.LayerRender;

	// //ƽ������ת������������
	// System.Drawing.PointF[] OPF =
	// this.Map.MapPointsToScreePoints(pGeometry.Items, OffsetX, OffsetY);

	// //����ͼ����Ⱦ�����л�ͼ
	// LR.Draw(this,pGeometry,g,OPF,type);
	// }

	// #endregion

	// #region ��Ⱦ���
	///// <summary>����ʵ��
	///// </summary>
	///// <param name="ObjectIndex">ʵ������</param>
	///// <param name="type">���ͣ�1-������2-ѡ��</param>
	// private void DrawPolygon(int ObjectIndex, int type)
	// {
	// this.DrawPolygon(Map.DisplayGraphic,ObjectIndex, type, 0f, 0f);
	// }
	// public void DrawPolygon(Graphics g,int ObjectIndex, int type, float
	// OffsetX, float OffsetY)
	// {
	// //�õ�ʵ��
	// CartoGeometry.IGeometry pGeometry = this.GetObject(ObjectIndex);

	// //��ȡ��Ⱦ��
	// PolyRender PRender = (PolyRender)this.LayerRender;
	// //System.Drawing.PointF[] OPF = new System.Drawing.PointF();//
	// this.Map.ClipPolygon(pGeometry.CoorList, OffsetX, OffsetY);
	// PRender.Draw(this, pGeometry, g, type);
	// }
	// #endregion

	// #endregion

	// #region ʵ��ѡ��
	///// <summary>ѡ��ʵ�壨��ѡ��
	///// </summary>
	// public bool SelectAtPoint(Coordinate SelPoint, double SelectDistance)
	// {
	// if (!this.Queryed) return false;
	// //ת��ѡ�����
	// double Tolerance = Map.ToMapDistance(SelectDistance);

	// #region ��㣬��������֮��ľ��룬��ѡ������ڣ�������ѡ��
	// if (this.LayerType == 0)
	// {
	// LKMap.CartoGeometry.Point StPoint;
	// for (int i = 0; i <= _ShowObjectList.Count - 1; i++)
	// {
	// int ObjectIndex = (int)_ShowObjectList[i];
	// StPoint = _ObjectList[ObjectIndex] as LKMap.CartoGeometry.Point;
	// if (StPoint.LKStatusMode == lkGeometryStatus.Delete) continue;
	// double ResDistance = StPoint.DistanceToPoint(SelPoint);
	// if (ResDistance <= Tolerance)
	// {
	// _SelectObjectList.Add(ObjectIndex);
	// return true;
	// }
	// }
	// }
	// #endregion

	// #region �߲㣬����ѡȡ�㵽ֱ�ߵľ��룬��ѡ������ڣ�������ѡ��
	// if (this.LayerType == 1)
	// {

	// LKMap.CartoGeometry.Polyline StPolyline;
	// foreach (int ObjIndex in ShowObjectList)
	// {
	// StPolyline = (Polyline)this.GetObject(ObjIndex);
	// if (StPolyline.LKStatusMode == lkGeometryStatus.Delete) continue;
	// if (StPolyline.Select(SelPoint, Tolerance))
	// {
	// this.SelectObjectList.Add(ObjIndex);
	// //ѡ��һ��ʵ��Ϳ��Է���
	// return true;
	// }
	// }
	// }
	// #endregion

	// #region ��㣬����ѡȡ���Ƿ������ڣ�����ڣ�������ѡ��
	// if (this.LayerType == 2)
	// {
	// LKMap.CartoGeometry.Polygon StPolygon;
	// for (int i = 0; i <= _ShowObjectList.Count - 1; i++)
	// {
	// int ObjectIndex = (int)_ShowObjectList[i];
	// StPolygon = _ObjectList[ObjectIndex] as LKMap.CartoGeometry.Polygon;
	// if (StPolygon.LKStatusMode == lkGeometryStatus.Delete) continue;
	// bool PtIn = StPolygon.PointIn(SelPoint);
	// if (PtIn)
	// {
	// _SelectObjectList.Add(ObjectIndex);
	// //ѡ��һ��ʵ��Ϳ��Է���
	// return true;
	// }
	// }
	// }
	// #endregion
	// return false;
	// }

	///// <summary>ѡ��ʵ�壨��ѡ��
	///// </summary>
	// public void SelectAtRect(Envelope SelRect)
	// {
	// LKMap.CartoGeometry.Polygon StPolygon;
	// LKMap.CartoGeometry.Polyline StPolyline;
	// LKMap.CartoGeometry.Point StPoint;
	// if (!this.Queryed) return;
	// for (int i = 0; i <= _ShowObjectList.Count - 1; i++)
	// {
	// int ObjectIndex = (int)_ShowObjectList[i];

	// #region ��㣬��ָ�����η�Χ����Ϊѡ��
	// if (this.LayerType == 0)
	// {
	// StPoint = _ObjectList[ObjectIndex] as LKMap.CartoGeometry.Point;
	// if (StPoint.LKStatusMode == lkGeometryStatus.Delete) continue;
	// if (StPoint.InRect(SelRect))
	// {
	// //if (StPoint.Draw((Layer)this, _Map))
	// _SelectObjectList.Add(ObjectIndex);
	// _SelectObjectList.Add(ObjectIndex);
	// }
	// }
	// #endregion

	// #region �߲㣬��������� 1��������Σ���ָ�����η�Χ����Ϊѡ�� 2��������Σ�����Ͼ���

	// if (this.LayerType == 1)
	// {
	// StPolyline = _ObjectList[ObjectIndex] as LKMap.CartoGeometry.Polyline;
	// if (StPolyline.LKStatusMode == lkGeometryStatus.Delete) continue;
	// if (SelRect.EnvelopeType) //�������
	// {
	// if (StPolyline.InRect(SelRect))
	// {
	// //if (StPolyline.Draw((Layer)this, _Map))
	// _SelectObjectList.Add(ObjectIndex);
	// _SelectObjectList.Add(ObjectIndex);
	// }
	// }
	// else //������� ��߾�ѡ��
	// {

	// if (StPolyline.IntersectRect(SelRect))
	// {
	// //if (StPolyline.Draw((Layer)this, _Map))
	// _SelectObjectList.Add(ObjectIndex);
	// _SelectObjectList.Add(ObjectIndex);
	// }
	// }
	// }
	// #endregion

	// #region ��㣬��������� 1��������Σ���ָ�����η�Χ����Ϊѡ�� 2��������Σ�����Ͼ���

	// if (this.LayerType == 2)
	// {
	// StPolygon = _ObjectList[ObjectIndex] as LKMap.CartoGeometry.Polygon;
	// if (StPolygon.LKStatusMode == lkGeometryStatus.Delete) continue;
	// if (SelRect.EnvelopeType) //�������
	// {
	// if (SelRect.Contain(StPolygon.Envelope))
	// {
	// //if (StPolygon.Draw((Layer)this, _Map))
	// _SelectObjectList.Add(ObjectIndex);
	// _SelectObjectList.Add(ObjectIndex);
	// }
	// }
	// else //������� ��߾�ѡ��
	// {

	// if (StPolygon.IntersectRect(SelRect))
	// {
	// //if (StPolygon.Draw((Layer)this, _Map))
	// _SelectObjectList.Add(ObjectIndex);
	// _SelectObjectList.Add(ObjectIndex);
	// }
	// }
	// }

	// #endregion
	// }
	// }
	// #endregion

	// #region ѡ��ͼ������Ҫ��ʾ��ʵ��

	///// <summary>ѡ��ͼ������Ҫ��ʾ��ʵ��,��Ҫ��������ͼ����
	///// </summary>
	///// <param name="pEnvelope"></param>
	// public void SelectShowObject()
	// {
	// //��������Ѱ��ʵ��
	// foreach (int i in Map.MapCellIndex.CurrentCellIndex)
	// {
	// foreach (int ObjectIdx in this.CellIndex[i])
	// {
	// this.SelectShowObject(ObjectIdx);
	// //if (Map.Extend.Intersect(pGeo.Envelope))
	// //{
	// // //�жϵ�Ŀ���������ظ�
	// // if (this.ShowObjectList.IndexOf(ObjectIdx) < 0)
	///// this.ShowObjectList.Add(ObjectIdx);
	// //}
	// }
	// }
	// }

	// public void SelectShowObject(CartoGeometry.IGeometry pGeometry)
	// {
	// //��ʶΪɾ���Ĳ�ѡ��
	// if (pGeometry.LKStatusMode == lkGeometryStatus.Delete) return;

	// //�Ƿ����ӿ���
	// if (Map.Extend.Intersect(pGeometry.Envelope))
	// {
	// int ObjectIdx = pGeometry.ObjectIndex;
	// //�жϵ�Ŀ���������ظ�
	// if (this.ShowObjectList.IndexOf(ObjectIdx) < 0)
	// this.ShowObjectList.Add(ObjectIdx);
	// }
	// }

	// public void SelectShowObject(int ObjIndex)
	// {
	// this.SelectShowObject(this.GetObject(ObjIndex));
	// }

	///// <summary>���ͼ������Ҫ��ʾ��ʵ��
	///// </summary>
	// public void ClearShowObject()
	// {
	// this.ShowObjectList.Clear();
	// }

	///// <summary>���ͼ���б�ѡ���ʵ�壬Ҳ�������ѡ��
	///// </summary>
	// public bool ClearSelection()
	// {
	// bool HaveObject = false;
	// foreach (int ObjectIdx in SelectObjectList)
	// {
	// //this.GetObject(ObjectIdx).LKStatusMode = LKStatusMode.Normal;
	// HaveObject = true;
	// }
	// SelectObjectList.Clear();
	// return HaveObject;
	// }

	// #endregion

	// ʵ������ά��

	/// <summary> ɾ��ָ��ʵ���ͼ�㼶������Ҳ������ͼ��������ɾ����ʵ�������
	/// </summary>
	/// <param name="pGeometry"></param>
	private void RemoveIndex(Geometry pGeometry) {
		// int ObjIndex = pGeometry.ObjectIndex;
		// foreach (List<int> idxList in this.CellIndex)
		// {
		// idxList.Remove(ObjIndex);
		// }
	}

	/// <summary>���¼���ʵ�������������뵽ͼ��������
	/// </summary>
	/// <param name="pGeometry"></param>
	private void AddIndex(Geometry pGeometry) {
		// List<int> cellList = pGeometry.CalCellIndex(this.Map);
		// foreach (int idx in cellList)
		// {
		// this.CellIndex[idx].Add(pGeometry.ObjectIndex);
		// }
		// cellList.Clear(); cellList = null;
	}

	/// <summary>������һ����������ʵ����ڱ�������������б��м����ʵ������Խ����������
	/// </summary>
	/// <param name="ObjectIndex"></param>
	/// <param name="CellIndex"></param>
	public void AddIndex(Geometry pGeometry, int CellIndex) {
		// this.CellIndex[CellIndex].Add(pGeometry.ObjectIndex);
	}

	// ʵ��ά�������ӣ�ɾ�������£�

	/// <summary>����ʵ�壬�����Ӿ��Ρ�����ֵ��GUID��
	/// </summary>
	/// <param name="pGeometry"></param>
	public void UpdateGeometry(Geometry pGeometry) {
		//// 1�����������Ӿ���
		// pGeometry.CalEnvelope();

		//// 2������ͼ�������
		// this.RemoveIndex(pGeometry);this.AddIndex(pGeometry);

		//// 3�����������Ҳ���ǽ����е��������ʼ��Ϊ��ֵ
		// if (pGeometry.FeatureTable.Count != this.FeatureTable.Count)
		// {
		// int FeatureItemCount = this.FeatureTable.Count;
		// for (int i = 0; i < FeatureItemCount; i++)
		// {
		// pGeometry.FeatureTable.Add("");
		// }
		// }
		//// 4������GUID��
		// if (pGeometry.ObjectID == null)
		// {
		// Guid newGUID = Guid.NewGuid();//ʵ��������
		// pGeometry.ObjectID = newGUID.ToString();
		// }
	}

	/// <summary>����ʵ�壬��ȫ�������������Ӿ���û�м��㣬��Ҫ���¼������
	/// </summary>
	/// <param name="pGeometry"></param>
	/// <param name="AllUdpate">true ��ʾ������Ӿ��Ρ�������false-������</param>
	public int AddGeometry(Geometry pGeometry, boolean AllUdpate) {
		//// ʵ�����б��е�λ��������Ҳ����Ψһ��ʶ��
		// pGeometry.ObjectIndex = this.ObjectList.Count;

		//// ��ʵ�����ʵ���б�
		// this.ObjectList.Add(pGeometry);

		//// ����ʵ��
		// if (AllUdpate)
		// {
		// this.UpdateGeometry(pGeometry);

		// //�Ƿ���Ҫ��ʾ
		// this.SelectShowObject(pGeometry);
		// }

		return -1;
		// return pGeometry.getIndex();
	}

	/// <summary>����ʵ�壬�ʺ��������������Ӿ��ζ��Ѿ��������
	/// </summary>
	/// <param name="pGeometry"></param>
	public int AddGeometry(Geometry pGeometry) {
		return this.AddGeometry(pGeometry, false);
	}

	/// <summary>����ͼ����ָ����������ʵ��
	/// </summary>
	/// <param name="ObjectIndex"></param>
	/// <returns></returns>
	public Geometry GetObject(int ObjectIndex) {
		// �˴���ΪӰ���ٶȣ�����취����ֻ�����б�ĩβ���Ӽ�¼���������м�����ɾ����
		// ������֤��ͼ�������е�ֵ����ObjectList������ֵ��Ҳ����ʵ�������ֵ�����Ӷ��ɴ��ӿ��ѯ�ٶ�
		// return ObjectList[ObjectIndex];
		return null;
	}

	// ͼ���ֶ�
	public int GetFieldIndex(String FieldName) {
		// int FieldCount = this.FeatureTable.Count;
		// for (int i = 0; i < FieldCount; i++)
		// {
		// if (this.FeatureTable[i] == FieldName) return i;
		// }
		return -1;
	}

	public void Dispose() {
		this._ShowSelection.RemoveAll();
		this._SelSelection.RemoveAll();
		this._Dataset.Dispose();
	}
}
