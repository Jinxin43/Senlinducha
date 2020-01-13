package lkmap.OverMap;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import dingtu.ZRoadMap.PubVar;
import android.graphics.RectF;
import lkmap.Map.*;
import lkmap.Tools.Tools;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Envelope;
import lkmap.CoordinateSystem.*;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkOverMapType;
public class OverMap 
{
    //Map����
    private Map Map = null;
    
	/**
	 * ��ʼ��OverMap����
	 * @param _map
	 */
    public OverMap(Map _map)
    {
        this.Map = _map;
        this.SetOverMapType(lkOverMapType.enUnknow);
    }
    
    //���ӵ�ͼ�����ͣ��ȸ裬���ͼ��
    private lkOverMapType _OverMapType = lkOverMapType.enGoogle_Satellite;
    /**
     * ���õ��ӵ�ͼ������
     * @param overmapType
     */
    public void SetOverMapType(lkOverMapType overmapType)
    {
    	this._OverMapType = overmapType;
    	if (this._OverMapType==lkOverMapType.enGoogle_Satellite || 
    		this._OverMapType==lkOverMapType.enGoogle_Terrain ||
    		this._OverMapType==lkOverMapType.enGoogle_Street)
    	{
            //��ʼ��Google��ͼ�����ż��������  ,ÿ���������ľ���
            for(int i=1;i<=20;i++)
            {
                _LevelScale[i] = ((2 * Math.PI * 6378137) / (Math.pow(2, i)))/256;
            }
            
            if (this._OverMapType==lkOverMapType.enGoogle_Satellite)this._TableName = "g_Sat";
            if (this._OverMapType==lkOverMapType.enGoogle_Terrain)this._TableName = "g_Ter";
            if (this._OverMapType==lkOverMapType.enGoogle_Street)this._TableName = "g_Str";
    	}
    }
    
    /*
     * ���ݵ�ͼ���͵Ĳ�ͬ����Ӧ�����ݱ�����
     * ��ʽ������Ӱ��=Sat����ȸ�Ӱ���Ϊg_Sat
     *       ��ͼ=Ter����ȸ���α�Ϊg_Ter
     */
    private String _TableName = "";
    
    //��ͼ�����ż��������
    private double[] _LevelScale = new double[21];
    private double[] getLevelScale(){return _LevelScale;}

    //�����ͼ���ݿ���
    private OverMapSQLiteDataBase _SQLiteDB = null;
    private OverMapSQLiteDataBase getSQLiteDB()
    {
        if (_SQLiteDB == null) _SQLiteDB = new OverMapSQLiteDataBase();
        return _SQLiteDB; 
    }
    
    //��ͼ������
    private OverMapDownloader _OverMapDownloader = null;
    private OverMapDownloader GetOverMapDownloader()
    {
        if (_OverMapDownloader == null) _OverMapDownloader = new OverMapDownloader();
        _OverMapDownloader.setBindGoogleMap(this);
        return _OverMapDownloader; 
    }

    public boolean _PanRefresh = false;     //�˴�ˢ�²����Ƿ������������
    
    //�Ƿ����դ���ͼ
    private boolean _ShowGird = false;  //��ʾ
    public void SetShowGrid(boolean visible)
    {
    	this._ShowGird = visible;
    }

    private int OffsetX = 0;
    private int OffsetY = 0;

    /**
     * ���ص��ӵ�ͼ��λ�ã�Ҳ���������ʱ�ĵ�ͼ����λ��
     * @return
     */
    public String GetOverMapPath()
    {
    	String CachePath = PubVar.m_SysAbsolutePath+"/Map/OverMap";
    	File file = new File(CachePath);
    	if (!file.exists())file.mkdir();
       return CachePath;
    }


    /// <summary>
    /// ���ݵ�ǰMap��ʾ����ȷ��Google�����ż���
    /// </summary>
    /// <param name="Zoom"></param>
    /// <returns></returns>
//    private int GetGoogleLevel(double Zoom)
//    {
//        return this.GetGoogleLevel(Zoom, !this.getZoomOnScale());
//    }
    
    /**
     * ���ݵ�ǰ����ͼ������������Ӧ��դ����
     * @param PerPixDistance
     * @return
     */
    private int GetCurrentLevel(double PerPixDistance)
	{
		double MinD = Double.MAX_VALUE;
		int level = -1;
		 for(int Level = 1;Level<this._LevelScale.length;Level++)
		 {
		     double D = Math.abs(this._LevelScale[Level] - PerPixDistance);
		     if (D < MinD){level = Level;MinD = D;}
		 }
		 return level;
	}


////    /// <summary>
////    /// ���ݵ�ǰ��ͼ����ʾ��Χ���Զ�������ʾ����
////    /// </summary>
//    private int _CurrentLevel = -1;
////    public void SetCurrentLevel()
////    {
////        int Level = this.GetGoogleLevel(this.Map.getViewConvert().getZoom(), true);
////        if (Level != -1) this.SetLevel(Level);
////    }
//
//    /// <summary>
//    /// ���õ�ǰ��ͼ����ʾ����
//    /// </summary>
//    /// <param name="Level"></param>
//    public void SetLevel(int Level)
//    {
//    	if (Level>=_LevelScale.length-1)Level = _LevelScale.length-1;
//    	if (Level<=1) Level=1;
//    	_CurrentLevel = Level;
//        this.Map.setExtend(this.GetEnveForLevel(Level));
//        this.Map.Refresh();
//        //System.Windows.Forms.MessageBox.Show(Level.ToString() + "," + this.Map.ViewConvert.ZoomScale.ToString());
//    }
//    
//    //����ָ������������Map����Ӿ���
//    private Envelope GetEnveForLevel(int Level)
//    {
//        double W = this.getLevelScale()[Level] * this.Map.getSize().getHeight();
//        return new Envelope(
//            this.Map.getExtend().getCenter().getX() - W / 2,
//            this.Map.getExtend().getCenter().getY() + W / 2,
//            this.Map.getExtend().getCenter().getX() + W / 2,
//            this.Map.getExtend().getCenter().getY() - W / 2);
//    }

    /**
     * ˢ�µ�ͼ
     * @return
     */
    public boolean Refresh()
    {
    	if (this._OverMapType==lkOverMapType.enUnknow) return false;
    	boolean PanResult = this._PanRefresh;this._PanRefresh=false;
    	
        if (!this._ShowGird) return false;

        boolean HaveLoadMap = false;

        //����google��ͼ
        try
        {
        	if (_TileScaleCanvas!=null) _TileScaleCanvas.drawColor(Color.WHITE);
        	
            //��ȡ��ǰ��ͼ��ÿ�����ش���ĸ߶�ֵ��Ҳ����ȷ��դ��ͼ�ļ���
            int Level = this.GetCurrentLevel(this.Map.ToMapDistance(1));

            //Envelope _NextEnve = null;
            
            //��ǰ��Ļ�����Ͻǣ����½�����
            Coordinate LeftTop,RightBottom;
            LeftTop = this.Map.getExtend().getLeftTop();
            RightBottom = this.Map.getExtend().getRightBottom();
//            if (PanResult)
//            {
//            	if (this._CurrentLevel==-1){lkmap.Tools.Tools.ShowMessageBox("����-1"); return false;}
//            	Level = this._CurrentLevel;
//            	//_NextEnve = this.GetEnveForLevel(Level);
//                LeftTop = this.Map.getExtend().getLeftTop();
//                RightBottom = this.Map.getExtend().getRightBottom();
//            } 
//            else
//            {
//            	if (Level==-1) {lkmap.Tools.Tools.ShowMessageBox("����-1"); return false;}
//                LeftTop = this.Map.getExtend().getLeftTop();
//                RightBottom = this.Map.getExtend().getRightBottom();
//            }
            	


            double OffsetX = 0, OffsetY = 0;
           // if (this.getGoogleMapType() == 1) { OffsetX = this.OffsetX; OffsetY = this.OffsetY; }
            
            //�������Ͻǣ����½��������ڵ�С��Ƭλ�ã�Ҳ��������ֵ
            double LTX = LeftTop.getX() + OffsetX; 
            double LTY = LeftTop.getY() + OffsetY;
            double RBX = RightBottom.getX() + OffsetX; 
            double RBY = RightBottom.getY() + OffsetY;

            Param StartX=new Param(), StartY=new Param(); 
            Coordinate LT = Project_Web.Web_XYToBL(LTX, LTY);
            GoogleMapAPI.GetTileXY(LT.getX(), LT.getY(), Level, StartX, StartY);

            Param EndX=new Param(), EndY=new Param(); 
            Coordinate RB = Project_Web.Web_XYToBL(RBX, RBY);
            GoogleMapAPI.GetTileXY(RB.getX(), RB.getY(), Level, EndX, EndY);

            //�������С��Ƭ�����б�
            List<String> TileNameList = new ArrayList<String>();
            List<OverMapTile> TileList = new ArrayList<OverMapTile>();
            for (int i = StartX.getInt(); i <= EndX.getInt(); i++)
            {
                for (int j = StartY.getInt(); j <= EndY.getInt(); j++)
                {
                	String TileName = String.valueOf(i) + "@" + String.valueOf(j) + "@" + String.valueOf(Level);
                	OverMapTile omt = new OverMapTile();
                	omt.SetTileName(TileName, this._TableName);
                	TileList.add(omt);TileNameList.add(TileName);
                }
            }
            
            //��ѯ���ݿ��Ѿ�������Щ��ͼ
            
            String SQL = "select * from %1$s where Name in (%2$s)";
            SQL = String.format(SQL, this._TableName,"'"+lkmap.Tools.Tools.JoinT("','", TileNameList)+"'");

            //�ж���Ҫ���ĸ�ͼƬ���ݿ�
            String DatabaseName = this.GetOverMapPath() + "/MapBase" + String.valueOf(Level) + ".dbx";
            if (this.getSQLiteDB().getDatabaseName() != DatabaseName) this.getSQLiteDB().setDatabaseName(DatabaseName);

            //��ȡ���ݿ��е���ӦͼƬ
            SQLiteDataReader DR = this.getSQLiteDB().Query(SQL);
            if (DR != null){while (DR.Read())
            {
                String Name = DR.GetString(0);
                byte[] ImageByte = (byte[])(DR.GetBlob(1));
                for(OverMapTile omt:TileList)
                {
                	if (omt.GetTileName().equals(Name))
                	{
                		if (this.ShowImage(omt, ImageByte)) HaveLoadMap = true;
                		TileList.remove(omt);break;
                	}
                }
            } DR.Close();}
           
            
            //��Щû������
            for(OverMapTile tile:TileList)
            {
            	//�ڶ��������в����Ƿ���ڣ���������Ҳ����PNG�ļ�����ʽ
            	String cheFileName = this.GetOverMapPath()+"/"+tile.GetTileName()+".png";
            	if (lkmap.Tools.Tools.ExistFile(cheFileName))
            	{
            		this.ShowImage(tile, lkmap.Tools.Tools.readStream(cheFileName));
            		TileList.remove(tile);continue;
            	}
            	
            	//���ػ�����û�е�
            	//"x=" + Row + "&y=" + Col + "&z=" + Level;
//            	String s = "Galileo".substring(0, ((3 * col + row) % 8)); 
//            	String url = "http://mt"+(col%4)+".google.cn/vt/lyrs=s&"+"x=" + col + "&y=" + row + "&z=" + level+"&s=" + s; 
            	if (this._OverMapType==lkOverMapType.enGoogle_Satellite || 
            		this._OverMapType==lkOverMapType.enGoogle_Terrain || 
            		this._OverMapType==lkOverMapType.enGoogle_Street)
            		tile.Url = GoogleMapAPI.CreateTileUrl(this._OverMapType, tile.Row, tile.Col, tile.Level);
            	tile.CachePath = this.GetOverMapPath();
            }
            
            //��ʼ������ͼ������
            this.GetOverMapDownloader().setUpLoadFileList(TileList);
            this.GetOverMapDownloader().StartUpLoad();

        }
        finally
        {
            //this.Map.DrawPicture.BackColor = OriColor;
            
        }
        return HaveLoadMap;
    }

    private Bitmap _TileScaleBitmap = null;
    private Canvas _TileScaleCanvas = null;
    public void FastRefresh()
    {
    	if (this._OverMapType==lkOverMapType.enUnknow) return;
    	if (!this._ShowGird) return;
    	if (_TileScaleBitmap==null) return;
    	//this.Map.getDisplayGraphic().drawBitmap(_TileScaleBitmap, 0, 0, null);
    	float MoveX = _TileScaleBitmap.getWidth()/2f;
    	float MoveY = _TileScaleBitmap.getHeight()/2f;
// 	   	this.Map.getDisplayGraphic().drawBitmap(_TileScaleBitmap,new Rect(0,0,_TileScaleBitmap.getWidth(),_TileScaleBitmap.getHeight()),
// 			   													new RectF(-MoveX,-MoveY,_TileScaleBitmap.getWidth()*2-MoveX,_TileScaleBitmap.getHeight()*2-MoveY),null);
// 	   	
	   	this.Map.getDisplayGraphic().drawBitmap(_TileScaleBitmap,new Rect(0,0,_TileScaleBitmap.getWidth(),_TileScaleBitmap.getHeight()),
					new RectF(0,0,_TileScaleBitmap.getWidth(),_TileScaleBitmap.getHeight()),null);

    }
    //��ʾָ����ͼƬ
    public boolean ShowImage(OverMapTile tile,byte[] ImageByte)
    {
    	if (_TileScaleBitmap==null) 
    	{
            //��Map�ߴ緢���仯ʱҪ���´���Image��������ӦMapControl�ؼ��Ĵ�С
    		_TileScaleBitmap = Bitmap.createBitmap(this.Map.getSize().getWidth(), 
            								this.Map.getSize().getHeight(), 
            								Config.ARGB_8888);
    		_TileScaleCanvas = new Canvas(_TileScaleBitmap);
    	}
        //��ȡ���ڼ��ص�ͼƬ��Ϣ
        int OffsetX = 0, OffsetY = 0;
       // if (this.getGoogleMapType() == 1) { OffsetX = this.OffsetX; OffsetY = this.OffsetY; }

        Param JD1=new Param(), WD1=new Param();
        Param JD2=new Param(), WD2=new Param();;
        GoogleMapAPI.GetTileLL(tile.Col, tile.Row, tile.Level, JD1, WD1);
        GoogleMapAPI.GetTileLL(tile.Col + 1, tile.Row + 1, tile.Level, JD2, WD2);


        Coordinate CoorLT = Project_Web.Web_BLToXY(JD1.getDouble(), WD1.getDouble());
        Coordinate CoorRB = Project_Web.Web_BLToXY(JD2.getDouble(), WD2.getDouble());


        //X1 -= OffsetX; X4 -= OffsetX; Y1 -= OffsetY; Y4 -= OffsetY;
        
        //Coor1.setX(Coor1.getX()-OffsetX);Coor1.setY(Coor1.getY()-OffsetY);
        Point ptLT = this.Map.getViewConvert().MapToScreen(CoorLT);
        Point ptRB = this.Map.getViewConvert().MapToScreen(CoorRB);
        //this.Map.ViewConvert.MapToScreen(X4, Y4, out RBX, out RBY);
        
       try
       {
    	   Bitmap bp = BitmapFactory.decodeByteArray(ImageByte, 0, ImageByte.length);
    	   //this.Map.getDisplayGraphic().drawBitmap(bp, PT.x, PT.y, null);
    	  // _TileScaleCanvas.drawBitmap(bp, PT.x, PT.y, null);
    	   
    	   _TileScaleCanvas.drawBitmap(bp, new Rect(0,0,bp.getWidth(),bp.getHeight()),new Rect(ptLT.x,ptLT.y,ptRB.x,ptRB.y), null);

    	   
    	   //this.Map.getDisplayGraphic().drawBitmap(bp,new Rect(0,0,256,256),new Rect(PT.x,PT.y,PT.x+512,PT.y+512),null);
//           using (MemoryStream ms = new MemoryStream(ImageByte))
//           {
//               using (Image pImage = new Bitmap(ms) as Image)
//               {
//                   Rectangle destRect = new Rectangle(LTX, LTY, RBX - LTX, RBY - LTY);
//                   //Rectangle soucRect = new Rectangle(0,0,256,256);
//                   ImageAttributes IA = new ImageAttributes();
//                   Color pColor = Color.FromArgb(255, 255, 255);
//                   IA.SetColorKey(pColor, pColor);
//                   this.Map.DisplayGraphic.DrawImage(pImage, destRect, 0, 0, 256, 256, GraphicsUnit.Pixel, IA);
//               }
//           }
       }
       catch(NullPointerException e)
       {
    	   if (_TileScaleCanvas==null) _TileScaleBitmap = null;
    	  
           return false;
        }
       return true;

    }
    
    //����
    public void Dispose()
    {
    	if (_TileScaleBitmap!=null)
    		_TileScaleBitmap.recycle();_TileScaleBitmap=null;
    	_TileScaleCanvas = null;System.gc();

    }
}
