package lkmap.Layer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dingtu.DTGIS.DataService.ProjectDB;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import dingtu.ZRoadMap.PubVar;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import lkmap.Tools.Tools;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Envelope;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Map.*;
import lkmap.OverMap.OverMapSQLiteDataBase;
public class GridLayer 
{
	//��̬ɸѡ��ʶ
	public String DynamicFilterStr = "";

	private Map Map = null;
	
	private String m_LayerId = "";
	public String GetLayerID(){return this.m_LayerId;};
	
	/**
	 * ��ʼ��GridLayer����
	 * @param _map
	 */
    public GridLayer(Map _map)
    {
    	this.m_LayerId = UUID.randomUUID().toString();
        this.Map = _map;
    }
    
    /**
     * դ������Դ���ݿ���
     */
    private OverMapSQLiteDataBase _SQLiteDB = null;
    private OverMapSQLiteDataBase getSQLiteDB()
    {
        if (_SQLiteDB == null) _SQLiteDB = new OverMapSQLiteDataBase();
        return _SQLiteDB; 
    }
    
    
    //�Ƿ����դ���ͼ
    private boolean _IfLoadGrid = false;
    /**
     * ж��դ��ͼ
     */
    public void UnloadGird()
    {
    	this._IfLoadGrid =  false;
    	this._CacheList.clear();
    	this._GridDataFileName = "";
    }
    private boolean _ShowGird = true;  //��ʾ
    public boolean GetShowGird(){return this._ShowGird;}
    public void SetShowGrid(boolean visible)
    {
    	this._ShowGird = visible;
    	if (!this._ShowGird)this._CacheList.clear();
    }
    
    /**
     * ��ȡ��դ������Χ
     * @return
     */
    public Envelope GetExtend()
    {
    	if (this._IfLoadGrid)return this._GridPad.Extend;else return null;
    }

    
    private String _GridDataFileName = "";
    public String GetGridDataFile()
    {
    	return this._GridDataFileName;
    }
    /**
     * ����դ������Դ�ļ�����
     * @param gridFileName
     */
    public void SetGridDataFile(String gridFileName,String path)
    {
    	String GridFileMainPath = PubVar.m_SysAbsolutePath+"/Map/";
    	
    	if(path != null && path.length()>0 && (!path.equals("null")))
    	{
    		GridFileMainPath = path;
    		if(!GridFileMainPath.endsWith("/"))
    		{
    			GridFileMainPath+="/";
    		}
    	}
    	
    	if (!Tools.ExistFile(GridFileMainPath+gridFileName))
    	{
        	if(Tools.ExistFile(gridFileName))
        	{
        		int lastIndex = gridFileName.lastIndexOf("/");
        		if(lastIndex>-1)
        		{
        			this._GridDataFileName = gridFileName.substring(lastIndex,gridFileName.length()-1);	
        		}
        		else
        		{
        			this._GridDataFileName = gridFileName;
        		}
        		
            	this.getSQLiteDB().setDatabaseName(gridFileName);
            	this._IfLoadGrid = true;
        	}
        	else
        	{
        		//��ջ���
        		this._IfLoadGrid = false;
            	this._CacheList.clear();return;
        	}
    	}
    	else
    	{
    		this._GridDataFileName = gridFileName;
        	this.getSQLiteDB().setDatabaseName(GridFileMainPath+this._GridDataFileName);
        	this._IfLoadGrid = true;
    	}
    	
    	
    	
    	
    	//��ջ���
    	this._CacheList.clear();
    	
        //դ��ͼ�ķּ���Ϣ
    	this._LevelScale.clear();
        SQLiteDataReader DR = this.getSQLiteDB().Query("select * from MapInfo");
        if (DR == null) return;
        while (DR.Read())
        {
            String Level = DR.GetString("MaxLevel");
            double Scale = DR.GetDouble("Scale");
            for(int i=1;i<=Integer.parseInt(Level);i++)
            {
            	this._LevelScale.add(0,Scale*(Math.pow(2, i-1)));
            }
            
            this._GridPad.TileSize = Integer.parseInt(DR.GetString("TileSize"));
            this._GridPad.SetExtend(DR.GetDouble("Min_X"), DR.GetDouble("Min_Y"),
            						DR.GetDouble("Max_X"), DR.GetDouble("Max_Y"));

        }DR.Close();
    }


    //���ż�������ߣ�Ҳ����ÿ�����ش����ʵ�ʾ���
    private List<Double> _LevelScale = new ArrayList<Double>();
    
    //դ������ƽ�棬��Ҫ���ڼ�����Ҫ��ʾ��դ����Ƭ
    private GridPad _GridPad = new GridPad();
    
    public void Refresh()
    {
    	if (!this._ShowGird)return;  //����ʾդ��ͼ��
    	if (!this._IfLoadGrid)return;  //û�м���դ��ͼ��
    	
    	//��ȡ��ǰ��ͼ��ÿ�����ش���ĸ߶�ֵ��Ҳ����ȷ��դ��ͼ�ļ���
    	double PerPixDistance = this.Map.ToMapDistance(1);
    	int CurrentLevel = this.GetCurrentLevel(PerPixDistance);
    	
    	//��ȡ��ǰ��ͼ�µ������Ӿ��η�Χ
    	Envelope evp = this.Map.getExtend();

    	//�ж��Ƿ��ڵ�ǰ��ͼ��Χ�ڣ������������ջ���
    	if (!this._GridPad.InCurrentView(evp)){this._CacheList.clear();return;}

    	//���������Ӿ��η�Χ����ǰ��ʾ����������Ҫ��ʾ��С��Ƭ
    	String LT = this._GridPad.CalGridPosition(this._LevelScale.get(CurrentLevel),evp.getLeftTop());
    	String RB = this._GridPad.CalGridPosition(this._LevelScale.get(CurrentLevel),evp.getRightBottom());
    	
    	//ˢ��ID
    	String RefreshID = UUID.randomUUID().toString();
    	
    	//������õ�ͼ�����б�
    	int StartX = Integer.parseInt(LT.split(",")[0]);
    	int StartY = Integer.parseInt(LT.split(",")[1]);
    	int EndX = Integer.parseInt(RB.split(",")[0]);
    	int EndY = Integer.parseInt(RB.split(",")[1]);
    	List<String> NameList = new ArrayList<String>();
    	for (int i = StartX; i <= EndX; i++)
    	{
	    	for (int j = StartY; j <= EndY; j++)
	    	{
	    		//�ж��Ƿ��ڻ����У�������򲻶�ȡ
	    		boolean InCache = false;
	    		for(Tile TL:this._CacheList)
	    		{
	    			if (TL.TileName.equals(CurrentLevel+"-"+j + "-" + i)){TL.TileUniqueID=RefreshID;InCache=true;}
	    		}
	    		if (!InCache)NameList.add(j + "-" + i);
	    	}
    	}
    	
    	//�������õĻ���
    	int CacheCount = this._CacheList.size()-1;
		for(int ci = CacheCount;ci>=0;ci--)
		{
			Tile TL = this._CacheList.get(ci);
			if (!TL.TileUniqueID.equals(RefreshID))this._CacheList.remove(TL);
		}

    	//��ѯ���ݿ��Ѿ�������Щ��ͼ
		if (NameList.size()==0) return;
    	String TableName = "L"+(CurrentLevel+1);
    	String SQL = "select * from " + TableName + " where SYS_RC in ('" + lkmap.Tools.Tools.JoinT("','", NameList) + "')";

    	//��ȡ���ݿ��е���ӦͼƬ
    	SQLiteDataReader DR = this.getSQLiteDB().Query(SQL);
    	if (DR == null) return;
    	while (DR.Read())
    	{
    		String Name = DR.GetString("SYS_RC");
	    	byte[] ImageByte = DR.GetBlob("SYS_GEO");
	    	String aa = DR.GetString("LT_X");

	    	double LT_X = DR.GetDouble("LT_X");
	    	double LT_Y = DR.GetDouble("LT_Y");
	    	double RB_X = DR.GetDouble("RB_X");
	    	double RB_Y = DR.GetDouble("RB_Y");
//	    	int isMask = 0;
//	    	try
//	    	{
//	    		isMask = DR.GetInt32("Mask");
//	    	}
//	    	catch(Exception ex)
//	    	{
//	    		isMask = 0;
//	    	}
	    	//������Ƭ������
	    	Tile TL = new Tile();
	    	TL.LT_X = LT_X;TL.LT_Y = LT_Y;TL.RB_X = RB_X;TL.RB_Y = RB_Y;
	    	TL.TileName = CurrentLevel+"-"+Name;
	    	
	    	
//	    	if(isMask == 1)
//	    	{
//	    		TL.TileBitmap = getTransparentBitmap(BitmapFactory.decodeByteArray(ImageByte, 0, ImageByte.length));
//	    	}
//	    	else
//	    	{
//	    		TL.TileBitmap = BitmapFactory.decodeByteArray(ImageByte, 0, ImageByte.length);
//	    	}
	    	
	    	TL.TileBitmap = BitmapFactory.decodeByteArray(ImageByte, 0, ImageByte.length);
	    	this._CacheList.add(TL);

    	}
    	DR.Close();
    	
    }
    
    public  Bitmap getTransparentBitmap(Bitmap sourceImg)
    {
    	
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        Bitmap b = sourceImg.copy(Config.ARGB_8888, true);
        b.setHasAlpha(true);
//        
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                .getWidth(), sourceImg.getHeight());// ���ͼƬ��ARGBֵ
        
//
//        int number =0;

        for (int i = 0; i < argb.length; i++) 
        {            
//        	//argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
////        	if(Color.red(argb[i])<=20)
////        	{
////        		if(Color.green(argb[i]) <= 20)
////        		{
////        			if(Color.blue(argb[i]) <= 20)
////        			{
////        				argb[i] = 0;
////        			}
////        		}
////        	}
        	if(Color.WHITE == argb[i])
        	{
        		argb[i] = 0;
        	}

        }

        b.setPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());
        
        return b;
    }
    
    //��Ƭ����
    private List<Tile> _CacheList = new ArrayList<Tile>();
    public void ClearAllCache()
    {
    	this._CacheList.clear();
    }
    
    //͸����
    private int m_Transparent = 0;
    public int GetTransparet(){return this.m_Transparent;}
    /**
     * ����͸����
     * @param transparent
     */
    public void SetTransparent(int transparent)
    {
    	this.m_Transparent = transparent;
    }
    /**
     * ����ˢ�£�û��ѡ��Ĺ��� ��ֱ�����û���
     */
    public void FastRefresh()
    {
    	if (!this._ShowGird) return;
    	Paint paint = new Paint();   
    	paint.setAlpha(255-this.m_Transparent); //����͸���̶�
    	paint.setAntiAlias(true);

//    	if(PubVar.imageEffect == null)
//    	{
//    		PubVar.imageEffect = new ProjectDB().getImagetEffect();
//    	}
//    	
//    	Boolean isEffect= (Boolean)PubVar.imageEffect.get("isEffect");
//    	if(isEffect)
//    	{
//    		int brigthValue = Integer.parseInt(PubVar.imageEffect.get("bright")+"");
//			int contrastValue = Integer.parseInt(PubVar.imageEffect.get("contrast")+"");
//			int brightness = brigthValue - 127; 
//			float contrast = (float) ((contrastValue+128) / 256.0); 
//	    	 
//            ColorMatrix bMatrix = new ColorMatrix();  
//            bMatrix.set(new float[] {contrast, 0, 0, 0, brightness,
//            		0, contrast,0, 0, brightness,
//            		0, 0, contrast, 0, brightness, 
//            		0, 0, 0, 1, 0 }); 
//            paint.setColorFilter(new ColorMatrixColorFilter(bMatrix));
//    	}
    	
    	for(Tile TL:this._CacheList)
    	{
        	PointF LTP = this.Map.getViewConvert().MapToScreenF(TL.LT_X,TL.LT_Y);
        	PointF RBP = this.Map.getViewConvert().MapToScreenF(TL.RB_X,TL.RB_Y);
    		this.Map.getDisplayGraphic().drawBitmap(TL.TileBitmap,new Rect(0,0,TL.TileBitmap.getWidth(),TL.TileBitmap.getHeight()),
			  			 new RectF(LTP.x,LTP.y,RBP.x,RBP.y),paint);
    	}
    }
    
    /**
     * ���ݵ�ǰ����ͼ������������Ӧ��դ����
     * @param PerPixDistance
     * @return
     */
    private int GetCurrentLevel(double PerPixDistance)
	{
		double MinD = Double.MAX_VALUE;
		int level = -1;
		 for(int Level = 0;Level<_LevelScale.size();Level++)
		 {
		     double D = Math.abs(_LevelScale.get(Level) - PerPixDistance);
		     if (D < MinD){level = Level;MinD = D;}
		 }
		 return level;
	}


    //���ڼ��㵱ǰ��ͼ��Χ����������Ƭ
    private class GridPad
    {
    	//���Χ
    	private double MinX,MinY,MaxX,MaxY;  
    	public Envelope Extend;
    	public void SetExtend(double MinX,double MinY,double MaxX,double MaxY)
    	{
    		this.MinX = MinX;this.MinY = MinY;this.MaxX = MaxX;this.MaxY = MaxY;
    		this.Extend = new Envelope(MinX,MaxY,MaxX,MinY);
    	}
    	//С��Ƭ�Ĵ�С
    	public int TileSize = 256;  
    	
    	/**
    	 * ���ݱ�����������������λ�ã�Ҳ��������ֵ
    	 * @param Scale ÿ�����ش����ʵ�ʾ���ֵ
    	 * @param ViewPT 
    	 * @return
    	 */
    	public String CalGridPosition(double Scale,Coordinate ViewPT)
    	{
    		double TileSizeT = this.TileSize * Scale;
    		
    		int MaxRow = (int)((MaxX - MinX) / TileSizeT)+1;
    		int MaxCol = (int)((MaxY - MinY) / TileSizeT)+1;
    		
    		int Row = (int)((ViewPT.getX() - MinX) / TileSizeT);
    		int Col = (int)((MaxY - ViewPT.getY()) / TileSizeT);
    		
    		if (Row<0)Row=0;if (Col<0)Col=0;
    		if (Row>MaxRow)Row=MaxRow;if (Col>MaxCol)Col = MaxCol;
    		
    		return Row+","+Col;
    	}
    	
    	//�Ƿ��뵱ǰ�ӿ��н���
    	public boolean InCurrentView(Envelope evp)
    	{
    		return evp.Intersect(this.Extend);
    	}
    }
    
    //���ڻ����õ���Ƭ��
    private class Tile
    {
    	//��Ƭ����ʵ����
    	public double LT_X = 0;
    	public double LT_Y = 0;
    	public double RB_X = 0;
    	public double RB_Y = 0;
    	
    	public String TileName = "";  //��ʽ1-Row-Col��Ҳ���Ǽ���-��-��
    	public String TileUniqueID = "";   //ͨ����ID������õ���Ƭ����
    	public Bitmap TileBitmap = null;
    }
}
