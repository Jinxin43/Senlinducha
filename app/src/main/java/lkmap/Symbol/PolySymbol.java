package lkmap.Symbol;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Enum.lkDrawType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Enum.lkSelectionType;
import lkmap.Enum.lkTextPosition;
import lkmap.Map.Map;
import lkmap.Tools.Tools;

public class PolySymbol extends ISymbol {
	public PolySymbol() {
		this.setName("Ĭ��");
		this.setPStyle(new Paint(Color.GREEN));
		this._PBrush.setColor(Color.GREEN);
		this.setLStyle(new Paint(Color.RED));
		this._LBrush.setColor(Color.RED);
		this._LBrush.setStrokeWidth(1);
		this.SetTransparent(125);
	}

	/**
	 * Base64�ַ���ת�������
	 * 
	 * @param base64
	 */
	public void CreateByBase64(String value) {
		if (value.equals(""))
			return;
		// ��ɫ,����ɫ,���߿�
		String[] PSInfo = value.split(",");

		// ��ɫ
		this._PBrush.setColor(Color.parseColor(PSInfo[0]));

		// ����ɫ
		this._LBrush.setColor(Color.parseColor(PSInfo[1]));

		// ���߿�
		this._LBrush.setStrokeWidth(Float.parseFloat(PSInfo[2]));
	}

	/**
	 * �������ת����Base64�ַ�����//��ɫ,����ɫ,���߿�
	 * 
	 * @return
	 */
	public String ToBase64() {
		// String PColor = Tools.ColorToHexStr(this._PBrush.getColor());
		// String LColor = Tools.ColorToHexStr(this._LBrush.getColor());
		String PColor = Tools.ColorToHexStr2(this._PBrush.getColor());
		String LColor = Tools.ColorToHexStr2(this._LBrush.getColor());
		String LW = this._LBrush.getStrokeWidth() + "";
		return PColor + "," + LColor + "," + LW;
	}

	/**
	 * ����ָ����С�ķ���ָʾͼ
	 * 
	 * @param Width
	 * @param Height
	 * @return
	 */
	public Bitmap ToFigureBitmap(int Width, int Height) {
		Bitmap bp = Bitmap.createBitmap(Width, Height, Config.ARGB_8888);
		Canvas g = new Canvas(bp);
		g.drawRect(0, 4, Width, Height - 4, this.getPStyle());
		if (this.getLStyle().getStrokeWidth() > 0)
			g.drawRect(0, 4, Width, Height - 4, this.getLStyle());

		return bp;
	}

	// ����ˢ-����
	private Paint _PBrush = null;

	public Paint getPStyle() {
		return this._PBrush;
	}

	public void setPStyle(Paint value) {
		this._PBrush = value;
		this._PBrush.setStyle(Paint.Style.FILL);
		this._PBrush.setAntiAlias(true);
		this.UpdateTransparent();
	}

	// ����͸����
	private int _Transparent = 0;

	public void SetTransparent(int transparent) {
		this._Transparent = 255 - transparent;
		this.UpdateTransparent();
	}

	private void UpdateTransparent() {
		this._PBrush.setAlpha(this._Transparent);
	}

	// ����ˢ-����
	private Paint _LBrush = null;

	public Paint getLStyle() {
		return this._LBrush;
	}

	public void setLStyle(Paint value) {
		this._LBrush = value;
		this._LBrush.setStyle(Paint.Style.STROKE);
		this._LBrush.setAntiAlias(true);
		this._LBrush.setStrokeJoin(Join.BEVEL);
	}

	// ���Ʒ���
	@Override
	public void Draw(Map map, Geometry pGeometry) {
		this.Draw(map, map.getDisplayGraphic(), pGeometry, 0, 0, lkDrawType.enNormal);
	}

	@Override
	public void Draw(Map map, Canvas g, Geometry pGeometry, int OffsetX, int OffsetY, lkDrawType DrawType) {
		// List<String> strList = new ArrayList<String>();
		// for(Coordinate Coor:pGeometry.GetPartAt(0).getVertexList())
		// {
		// strList.add(Coor.ToString());
		// }
		// String CoorStr = Tools.JoinT("\r\n", strList);
		//
		if (pGeometry.getStatus() == lkGeometryStatus.enDelete)
			return;
		this._VertexList.clear();
		Polygon PLY = (Polygon) pGeometry;
		Path pathAll = new Path();
		for (int i = 0; i < PLY.getPartCount(); i++) {
			Point[] OPF = null;
			Part part = PLY.GetPartAt(i);
			if (map.getExtend().Contains(part.getEnvelope()))
				OPF = map.getViewConvert().MapPointsToScreePoints(part.getVertexList());
			else
				OPF = map.getViewConvert().ClipPolygon(part.getVertexList());
			pathAll.addPath(this.CreatePath(OPF, OffsetX, OffsetY));
		}

		if (DrawType == lkDrawType.enNormal) {
			g.drawPath(pathAll, this.getPStyle());
			if (this.getLStyle().getStrokeWidth() > 0)
				g.drawPath(pathAll, this.getLStyle());
		}

		if (DrawType == lkDrawType.enSelected_NoEditing) {
			g.drawPath(pathAll, this.getPStyle());
			Paint pBrush = new Paint();
			pBrush.setStyle(Style.STROKE);
			pBrush.setColor(Color.rgb(0, 255, 255));
			pBrush.setStrokeWidth(Tools.DPToPix(3));
			pBrush.setAntiAlias(true);
			g.drawPath(pathAll, pBrush);

			// ��ѡ���ߵ��ڽڵ�
			int H = Tools.DPToPix(8), W = H;
			pBrush = new Paint();
			pBrush.setColor(Color.BLACK);
			for (Point pt : this._VertexList) {
				float ox = pt.x + OffsetX;
				float oy = pt.y + OffsetY;
				g.drawRect(ox - W / 2, oy - H / 2, ox + W / 2, oy + H / 2, pBrush);
			}
			this._VertexList.clear();
			pBrush.setColor(Color.BLUE);
		}

		if ((DrawType == lkDrawType.enSelected_Editing)) {
			g.drawPath(pathAll, this.getPStyle());
			g.drawPath(pathAll, this.getLStyle());

			// ��ѡ���ߵ��ڽڵ�
			int H = Tools.DPToPix(8), W = H;
			Paint pBrush = new Paint();
			pBrush.setColor(Color.BLACK);
			for (Point pt : this._VertexList) {
				float ox = pt.x + OffsetX;
				float oy = pt.y + OffsetY;
				g.drawRect(ox - W / 2, oy - H / 2, ox + W / 2, oy + H / 2, pBrush);
			}
			this._VertexList.clear();
			pBrush.setColor(Color.BLUE);
		}

	}

	private List<Point> _VertexList = new ArrayList<Point>();

	private Path CreatePath(Point[] OPF, int OffsetX, int OffsetY) {
		Path p = new Path();
		for (int i = 0; i < OPF.length; i++) {
			if (i == 0)
				p.moveTo(OPF[i].x + OffsetX, OPF[i].y + OffsetY);
			else
				p.lineTo(OPF[i].x + OffsetX, OPF[i].y + OffsetY);
			this._VertexList.add(OPF[i]);
		}
		if (OPF.length > 0)
			p.lineTo(OPF[0].x + OffsetX, OPF[0].y + OffsetY); // ��ıպϴ���
		return p;
	}

	@Override
	public void DrawLabel(Map map, Canvas g, Geometry pGeometry, int OffsetX, int OffsetY,
			lkSelectionType pSelectionType) {
		String LabelText = pGeometry.getTag();
		Point _CenterCoorPix = map.getViewConvert().MapToScreen(((Polygon) pGeometry).getCenterPoint());
		this.getTextSymbol().Draw(g, _CenterCoorPix.x, _CenterCoorPix.y, LabelText, lkTextPosition.enCenter,
				pSelectionType);

	}
}
