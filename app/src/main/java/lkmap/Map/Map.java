package lkmap.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.ProjectDB;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Envelope;
import lkmap.Cargeometry.Size;
import lkmap.CoordinateSystem.ViewConvert;
import lkmap.Dataset.DataSource;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.Layer.GeoLayers;
import lkmap.Layer.GridLayers;
import lkmap.Layer.OverLayer;
import lkmap.MapControl.MapControl;
import lkmap.MapControl.v1_ScaleBar;
import lkmap.OverMap.OverMap;
import lkmap.Tools.Tools;

public class Map {

	// ϵͳִ���ļ���·��
	private String _SystemPath = "";

	public String getSystemPath() {
		return _SystemPath;
	}

	public void setSystemPath(String value) {
		_SystemPath = value;
	}

	// //Map�Ķ�̬���ٲ�
	// private TrackLayer _TrackLayer = null;
	// public TrackLayer TrackLayer
	// {
	// get { if (_TrackLayer == null) _TrackLayer = new TrackLayer(this); return
	// this._TrackLayer; }
	// }

	// ���ݲɼ�ͼ�㼯��
	private GeoLayers _DAGeoLayers = new GeoLayers();

	// ��������Դͼ�㼯��
	private GeoLayers _BKGeoLayers = new GeoLayers();

	public GeoLayers getGeoLayers(lkGeoLayersType geoLayersType) {
		if (geoLayersType == lkGeoLayersType.enVectorEditingData)
			return _DAGeoLayers;
		if (geoLayersType == lkGeoLayersType.enVectorBackground)
			return _BKGeoLayers;
		if (geoLayersType == lkGeoLayersType.enAll) {
			GeoLayers pGeoLayers = new GeoLayers();
			for (GeoLayer pGeoLayer : this._DAGeoLayers.getList())
				pGeoLayers.AddLayer(pGeoLayer);
			for (GeoLayer pGeoLayer : this._BKGeoLayers.getList())
				pGeoLayers.AddLayer(pGeoLayer);
			return pGeoLayers;
		}
		return null;
	}

	// OverLayer��ͼ�㼯��
	private OverLayer _OverLayer = new OverLayer();

	public OverLayer getOverLayer() {
		return _OverLayer;
	}

	// ���ѡ�񼯺�
	public void ClearSelection() {
		((MapControl) this._DrawPicture)._Select.ClearAllSelection();
	}

	// ����ת���ࣨ��Ļ������ʵ������ת����
	private ViewConvert _ViewConvert = new ViewConvert();

	public ViewConvert getViewConvert() {
		return _ViewConvert;
	}

	// ������Ϊ��λ��Map�ߴ磬Ҳ����MapControl�ؼ��ĳߴ�
	public Size getSize() {
		return this.getViewConvert().getSize();
	}

	public Bitmap MaskBitmap = null;
	public Bitmap bp = null;

	public void setSize(Size value) {
		if (PubVar.m_DoEvent.m_AuthorizeTools.m_AuthorizePass) {
			bp = null;
		}

		this.Dispose();
		this.getViewConvert().setSize(value);
		// ��Map�ߴ緢���仯ʱҪ���´���Image��������ӦMapControl�ؼ��Ĵ�С
		bp = Bitmap.createBitmap(this._ViewConvert.getSize().getWidth(), this._ViewConvert.getSize().getHeight(),
				Config.ARGB_8888);
		this.MaskBitmap = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(), bp.getHeight());
		this._DrawPicture.setImageBitmap(bp);
		this._Graphics = new Canvas(bp);
	}

	public void setEmpty() {
		this.Dispose();

		int max = 20000000;
		int[] colors = new int[max];
		for (int i = 0; i < max; i++) {
			colors[i] = Color.GRAY;
		}

		Bitmap newbp = Bitmap.createBitmap(colors, this._ViewConvert.getSize().getWidth(),
				this._ViewConvert.getSize().getHeight(), Config.ARGB_8888);
		this.bp = newbp.copy(Config.ARGB_8888, true);
		this.MaskBitmap = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(), bp.getHeight());
		this._DrawPicture.setImageBitmap(bp);
		this._Graphics = new Canvas(bp);
	}

	// Map�ĵ�ǰ��ͼ��Ӿ��Σ���λ���ף�����Map.Extend
	public Envelope getExtend() {
		return this._ViewConvert.getExtend();
	}

	public void setExtend(Envelope value) {
		this._ViewConvert.setExtend(value);
	}

	// Map.Extend��ʵ�ʵ�λ���ף��������꣬��Map.Center
	public Coordinate getCenter() {
		return this._ViewConvert.getCenter();
	}

	public void setCenter(Coordinate value) {
		this._ViewConvert.setCenter(value);
	}

	// ����ָ�����ػ���ʵ�ʾ���
	public double ToMapDistance(double PixDistance) {
		return this.getViewConvert().getZoomScale() * PixDistance;
	}

	// Map�������ͼ��Ӿ��Σ�Ҳ����ȫ����Χ����λ���ף�����Map.FullExtend;
	public Envelope getFullExtend() {
		return this.getViewConvert().getFullExtend();
	}

	public void setFullExtend(Envelope value) {
		this.getViewConvert().setFullExtend(value);
	}

	/**
	 * ��ȡ�ȽϺ������ͼ�����Ӿ���
	 * 
	 * @return
	 */
	public Envelope getFullExtendForView() {
		Envelope _ExtendForView = new Envelope(0, 0, 0, 0);

		// ��ȡ��ǰ���ڲɼ������������Ӿ���
//		_ExtendForView = PubVar.m_Workspace.GetDataSourceByEditing().GetEnvelope();

		// ��ȡ��ǰդ��ͼ�������Ӿ���
		if (_ExtendForView.IsZero()) {
			if (this.GetGridLayers().GetExtend() != null)
				_ExtendForView = this.GetGridLayers().GetExtend();
		}

		// ��ȡ��ǰʸ����ͼ�������Ӿ���
		if (_ExtendForView.IsZero()) {
			for (DataSource pDataSource : PubVar.m_Workspace.GetDataSourceList()) {
				if (!pDataSource.getEditing()) {
					if (_ExtendForView.IsZero())
						_ExtendForView = pDataSource.GetEnvelope();
					else
						_ExtendForView = _ExtendForView.Merge(pDataSource.GetEnvelope());
				}
			}
		}

		// ��ȡϵͳĬ�������Ӿ��Σ���new Map()�ڶ���
		if (_ExtendForView.IsZero())
			_ExtendForView = this.getFullExtend();

		// ������ͼ���ε�������ʹ�������ʾ��Ҫ����Ҫ�Ǹ߿�������������ʾ����
		return this.AdjustEnvelopeFitScreen(_ExtendForView);
		// Coordinate CenterPT = _ExtendForView.getCenter();
		// double W = _ExtendForView.getWidth()*2; //�˴��Ŵ�Ŀ����Ϊ����ʾȫͼ���������¹��������ڵ�
		// double H = _ExtendForView.getHeight()*2;
		// if (W>=H)H = W*this.getViewConvert().getSize().getHeight() /
		// this.getViewConvert().getSize().getWidth(); //���Ÿ߶�
		// else W = H*this.getViewConvert().getSize().getWidth() /
		// this.getViewConvert().getSize().getHeight(); //���ſ��
		// return new
		// Envelope(CenterPT.getX()-W/2,CenterPT.getY()+H/2,CenterPT.getX()+W/2,CenterPT.getY()-H/2);
	}

	/**
	 * �����������ʺ���Ļ��ʾ
	 * 
	 * @param pEnv
	 * @return
	 */
	public Envelope AdjustEnvelopeFitScreen(Envelope pEnv) {
		Coordinate CenterPT = pEnv.getCenter();
		double W = pEnv.getWidth() * 1.2; // �˴��Ŵ�Ŀ����Ϊ����ʾȫͼ���������¹��������ڵ�
		double H = pEnv.getHeight() * 1.2;
		if (W >= H)
			H = W * this.getViewConvert().getSize().getHeight() / this.getViewConvert().getSize().getWidth(); // ���Ÿ߶�
		else
			W = H * this.getViewConvert().getSize().getWidth() / this.getViewConvert().getSize().getHeight(); // ���ſ��
		return new Envelope(CenterPT.getX() - W / 2, CenterPT.getY() + H / 2, CenterPT.getX() + W / 2,
				CenterPT.getY() - H / 2);
	}

	// Map״̬
	private boolean _InvalidMap = false;

	public boolean getInvalidMap() {
		return _InvalidMap;
	}

	public void setInvalidMap(boolean value) {
		_InvalidMap = value;
	}

	// ������Graphics
	private Canvas _Graphics;

	public Canvas getDisplayGraphic() {
		return _Graphics;
	}

	// �滭ƽ�棬���е�ͼ�ξ���������
	private ImageView _DrawPicture;

	public ImageView getDrawPicture() {
		return _DrawPicture;
	}

	// (new Map) ��ʼ��Map����ʵ��
	public Map(MapControl mapControl) {
		this._DrawPicture = mapControl;
		int w = 240;
		int h = 240;
		this.setSize(new Size(w, h));

		try {
			Coordinate LT = StaticObject.soProjectSystem.WGS84ToXY(73, 53, 0);
			Coordinate RB = StaticObject.soProjectSystem.WGS84ToXY(135, 3, 0);
			this.setFullExtend(new Envelope(LT, RB));
		} catch (Exception ex)// ��ֹWGS84ToXYΪ��
		{

		}

		mapControl.setMap(this);
	}

	// ������
	private v1_ScaleBar _ScaleBar = null;

	/**
	 * ���ñ����߲�����
	 * 
	 * @param _scaleBar
	 */
	public void SetScaleBar(v1_ScaleBar _scaleBar) {
		this._ScaleBar = _scaleBar;
	}

	// ��ͼդ��ͼ��
	private GridLayers _GridLayers = new GridLayers(this);

	public GridLayers GetGridLayers() {
		return this._GridLayers;
	}

	// ����Ӱ��ͼ�㣬Ҳ����wgs84����ϵ�µĵ�ͼդ���
	private OverMap _OverMapLayer = new OverMap(this);

	public OverMap getOverMapLayer() {
		return this._OverMapLayer;
	}

	// ͼ��ˢ�� (FastRefresh��Refresh)
	// ˢ��ȫ��ͼ��
	public void Refresh() {
		if (!PubVar.m_DoEvent.m_AuthorizeTools.m_AuthorizePass) {
			return;
		}
		// ˢ�±�����
		if (this._ScaleBar != null)
			this._ScaleBar.RefreshScaleBar(this);

		// ˢ��դ��ͼ��
		this._OverMapLayer.Refresh();
		this._GridLayers.Refresh();

		// ��ȡʵ��
		// StaticObject.StartTime();

		// ��Զ�����Դ������Ҫ����
		if (this._BKGeoLayers.getList().size() > 0) {
			List<DataSource> MBKDataSourceList = new ArrayList<DataSource>();
			for (GeoLayer pGeoLayer : this._BKGeoLayers.getList()) {
				DataSource pDataSource = pGeoLayer.getDataset().getDataSource();
				if (!MBKDataSourceList.contains(pDataSource))
					MBKDataSourceList.add(pDataSource);
			}

			for (DataSource pDataSource : MBKDataSourceList) {
				List<GeoLayer> pGeoLayerList = new ArrayList<GeoLayer>();
				for (Dataset pDataset : pDataSource.getDatasets())
					pGeoLayerList.add(pDataset.getBindGeoLayer());
				this.CalRefresh(pGeoLayerList);
			}
		}

		// �ɼ�����
		this.CalRefresh(this._DAGeoLayers.getList());
		// String T1 = "��ȡͼ�Σ�"+StaticObject.EndTime();

		// ������ʾ
		// StaticObject.StartTime();
		this.FastRefresh();
		// String T2 = "��ʾͼ�Σ�"+StaticObject.EndTime();

		// Paint _Font = new Paint();
		// _Font.setAntiAlias(true);
		// _Font.setTextSize(20);
		// Typeface TF = Typeface.create("����", Typeface.NORMAL);
		// _Font.setTypeface(TF);

		// this._Graphics.drawText(T1, 0, 200, _Font);
		// this._Graphics.drawText(T2, 0, 250, _Font);

	}

	/**
	 * ���ݵ�ǰ��ʾ��Χ����ͼ����Ҫ��ʾ��ʵ��
	 * 
	 * @param geoLayerList
	 */
	private void CalRefresh(List<GeoLayer> geoLayerList) {
		if (geoLayerList.size() == 0)
			return;

		// for (GeoLayer layer : geoLayerList)
		// {
		// layer.getShowSelection().RemoveAll();
		// if (layer.getVisibleScaleMax() >=
		// layer.getMap().getViewConvert().getZoom() &&
		// layer.getVisibleScaleMin() <=
		// layer.getMap().getViewConvert().getZoom() &&
		// layer.getVisible() == true)
		// {
		// //����ÿ��ͼ������Ҫ��ʾ��ʵ���б�����ͼ���ShowSelection��
		// layer.Refresh(); //�����������������Ĳ���
		// }
		// }

		// �������ϲ�ѯ���ݣ��Ĳ�������
		StaticObject.StartTime();
		DataSource pDataSource = null;
		List<String> SQLList = new ArrayList<String>();
		for (GeoLayer layer : geoLayerList) {
			DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
			double D = dm.densityDpi * PubVar.m_Map.getViewConvert().getZoomScale();

			layer.getShowSelection().RemoveAll();
			if (layer.getVisibleScaleMax() >= D / 0.0254 && layer.getVisibleScaleMin() <= D / 0.0254
					&& layer.getVisible() == true) {
				pDataSource = layer.getDataset().getDataSource();
				// ѡ����ο������ڵ�
				String WhereFilter = layer.getDataset().GetMapCellIndex().CalCellIndexFilter(this.getExtend());
				// ���η�Χ����
				String EnvelopeFilter = "not (max(minx,%1$s)>min(maxx,%3$s) or max(miny,%2$s)>min(maxy,%4$s))";
				EnvelopeFilter = String.format(EnvelopeFilter, this.getExtend().getMinX(), this.getExtend().getMinY(),
						this.getExtend().getMaxX(), this.getExtend().getMaxY());
				// EnvelopeFilter = "1=1";
				// �����ѯ�������
				String SQL = "select SYS_ID,'%4$s' as TName  from %1$s where (%2$s) and (%3$s)";
				SQL = String.format(SQL, layer.getDataset().getIndexTableName(), WhereFilter, EnvelopeFilter,
						layer.getDataset().getId());
				SQLList.add(SQL);
			}

		}

		if (SQLList.size() == 0)
			return;

		String QuerySQL = Tools.JoinT("\r\nunion all\r\n", SQLList);

		// Log.v("��ѯSQL", QuerySQL);
		HashMap<String, List<String>> idList = new HashMap<String, List<String>>();
		SQLiteDataReader DR = pDataSource.Query(QuerySQL);
		{
			if (DR != null)
				while (DR.Read()) {
					String SYSID = DR.GetString("SYS_ID");
					String TName = DR.GetString("TName");
					if (!idList.containsKey(TName))
						idList.put(TName, new ArrayList<String>());
					idList.get(TName).add(SYSID);
				}
			DR.Close();
		}

		for (String TName : idList.keySet()) {
			for (GeoLayer pGeoLayer : geoLayerList) {
				if (pGeoLayer.getId().equals(TName))
					pGeoLayer.getDataset().QueryGeometryFromDB(idList.get(TName));
			}
		}

		//
		// //�Ա��Ѿ����ڵ�ʵ���б��ų��Ѿ����ڵ�ʵ�壬�ٴβ�ѯ���ݿ��ѯ����ʵ��ʵ������
		// SQLList.clear();
		// for(String TName : idList.keySet())
		// {
		// GeoLayer pGeoLayer = this._BKGeoLayers.GetLayerByName(TName);
		// Dataset pDataset = pGeoLayer.getDataset();
		//
		// List<String> AllIDList = new ArrayList<String>();
		// AllIDList = idList.get(TName);
		//
		// List<String> queryIDList = new ArrayList<String>();
		// for(String SYSID : AllIDList )
		// {
		// Geometry pGeometry = pDataset.GetGeometry(Integer.parseInt(SYSID));
		// if (pGeometry==null)
		// {
		// queryIDList.add(SYSID);
		// } else
		// {
		// pGeoLayer.getShowSelection().Add(pGeometry);
		// }
		// }
		// String SQL = "select SYS_GEO,SYS_ID,'%3$s' as TName from %1$s where
		// SYS_ID in (%2$s)";
		// SQL = String.format(SQL, TName + "_D", Tools.JoinT(",",
		// queryIDList),TName);
		//
		// SQLList.add(SQL);
		// }
		//
		// int ReadObjectCount = 0;
		// if (SQLList.size() > 0)
		// {
		//
		// QuerySQL = Tools.JoinT("\r\nunion all\r\n", SQLList);
		//
		// //�����ѯ�������
		// SQLiteDataReader DR1 = pDataSource.Query(QuerySQL);
		// {
		// if (DR1 != null) while (DR1.Read())
		// {
		// byte[] bytex = (byte[])DR1.GetBlob("SYS_GEO");
		// GeoLayer pGeoLayer =
		// this._BKGeoLayers.GetLayerByName(DR1.GetString("TName"));
		// Dataset pDataset = pGeoLayer.getDataset();
		// Geometry pGeometry = Tools.ByteToGeometry(bytex,
		// pDataset.getType(),false);
		// pGeometry.setID(DR1.GetString("SYS_ID") + "");
		// pDataset.AddGeometry(pGeometry);
		// pGeoLayer.getShowSelection().Add(pGeometry);
		// ReadObjectCount++;
		// } DR.Close();
		// }
		// }
		//
		//
		//
		// String T2 = "��ȡͼ�Σ�"+StaticObject.EndTime()+","+ReadObjectCount;
		//
		//
		//
		// StaticObject.StartTime();
		//
		// //������ʾ
		// this.FastRefresh();
		//
		// String T3 = "��ʾͼ�Σ�"+StaticObject.EndTime();
		//
		// Paint _Font = new Paint();
		// _Font.setAntiAlias(true);
		// _Font.setTextSize(20);
		// Typeface TF = Typeface.create("����", Typeface.NORMAL);
		// _Font.setTypeface(TF);
		//
		// this._Graphics.drawText(T1, 0, 200, _Font);
		// this._Graphics.drawText(T2, 0, 250, _Font);
		// this._Graphics.drawText(T3, 0, 300, _Font);
	}

	boolean LoadGoogleMap = false; // ��ʾ�Ƿ��Ѿ�������TileͼƬ��Ҳ���Ƿ�ѡ�е�TileͼƬ

	// ����ˢ��ͼ�㣬û��ѡ����˵Ĺ���
	public void FastRefresh() {
		if (!PubVar.m_DoEvent.m_AuthorizeTools.m_AuthorizePass) {
			return;
		}

		_Graphics.drawColor(Color.LTGRAY);

		this._OverMapLayer.FastRefresh();
		this._GridLayers.FastRefresh();

		if (PubVar.imageEffect == null) {
			ProjectDB db = new ProjectDB();
			PubVar.imageEffect = db.getImagetEffect();
			db.Close();
		}

		Boolean isEffect = (Boolean) PubVar.imageEffect.get("isEffect");
		if (isEffect) {
			ColorMatrix bMatrix = new ColorMatrix();
			try {
				int brigthValue = Integer.parseInt(PubVar.imageEffect.get("bright") + "");
				int contrastValue = Integer.parseInt(PubVar.imageEffect.get("contrast") + "");
				int brightness = brigthValue - 127;
				float contrast = (float) ((contrastValue + 128) / 256.0);
				bMatrix.set(new float[] { contrast, 0, 0, 0, brightness, 0, contrast, 0, 0, brightness, 0, 0, contrast,
						0, brightness, 0, 0, 0, 1, 0 });

				Paint paint = new Paint();
				paint.setColorFilter(new ColorMatrixColorFilter(bMatrix));
				PubVar.OriginalMap = Bitmap.createBitmap(this.bp);
				this._Graphics.drawBitmap(this.bp, 0, 0, paint);
			} catch (Exception ex) {
				PubVar.imageEffect.put("isEffect", false);
			}

		} else {
			PubVar.OriginalMap = null;
		}

		this.FastRefreshForGeoLayers(this.getGeoLayers(lkGeoLayersType.enVectorBackground).getList());
		this.FastRefreshForGeoLayers(this.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList());

		// ��OverLayer��
		this.getOverLayer().Refresh();

		new Canvas(this.MaskBitmap).drawBitmap(this.bp, 0, 0, null);
		this._DrawPicture.invalidate();

		Tools.UpdateShowSelectCount();
		Tools.UpdateScaleBar();
	}

	private void FastRefreshForGeoLayers(List<GeoLayer> geoLayerList) {
		// ���»�ͼ��
		for (GeoLayer layer : geoLayerList) {
			// if (layer.getType() == lkGeoLayerType.enPolygon) continue;
			layer.FastRefresh();
		}

		// ���»�ͼ���б�ѡ�е�ʵ��
		for (GeoLayer layer : geoLayerList) {
			layer.DrawSelection(layer.getSelSelection());
		}

		// ���»�ͼ��ı�ע��Ϣ
		for (GeoLayer layer : geoLayerList) {
			if (layer.getRender().getIfLabel()) {
				layer.DrawSelectionLabel(layer.getShowSelection(), _Graphics, 0, 0);
			}
		}
		// ���»�ͼ��ı�ע��Ϣ����ѡ�в��֣�
		for (GeoLayer layer : geoLayerList) {
			if (layer.getRender().getIfLabel()) {
				layer.DrawSelectionLabel(layer.getSelSelection(), _Graphics, 0, 0);
			}
		}
	}

	// Map����
	public void Dispose() {
		if (this.bp != null)
			bp.recycle();
		bp = null;
		if (this.MaskBitmap != null)
			this.MaskBitmap.recycle();
		this.MaskBitmap = null;
		this._Graphics = null;
		System.gc();
		// _Graphics = null;
		// _DrawPicture.Dispose();
		// for (GeoLayer pGeoLayer : this._GeoLayers)
		// if (pGeoLayer is IDisposable) ((IDisposable)pGeoLayer).Dispose();
		// this._GeoLayers.
		// GC.Collect();
	}
}
