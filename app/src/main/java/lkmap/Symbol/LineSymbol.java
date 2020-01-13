package lkmap.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Polyline;
import lkmap.Enum.lkDrawType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Enum.lkSelectionType;
import lkmap.Enum.lkTextPosition;
import lkmap.Map.Map;
import lkmap.Tools.Tools;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.Log;

public class LineSymbol extends ISymbol
{

    public LineSymbol() 
    {
        //Ĭ�Ϸ���
        this.setName("NULL");

        //Ĭ��Ϊ�����ɫ
        Random rd = new Random(); 
        int R = rd.nextInt(255); int G = rd.nextInt(255); int B = rd.nextInt(255);

        Paint pPen = new Paint();
        pPen.setStyle(Style.STROKE);
        pPen.setARGB(255, R, G, B);
        pPen.setStrokeWidth(3);
       // pPen.LineJoin = System.Drawing.Drawing2D.LineJoin.Round;   //�������
        //float[] f = { float.MaxValue };
       // pPen.DashPattern = f; 
        _PenList.add(pPen);

        this._SymbolBase64Str = Tools.ColorToHexStr(pPen.getColor())+","+pPen.getStrokeWidth();
    }
    
    private String _SymbolBase64Str = "";
    /**
     * Base64�ַ���ת���߷���
     * @param base64
     */
    public void CreateByBase64(String value)
    {
    	this._SymbolBase64Str = value;
		String[] symList = value.split("@");
		List<Paint> pPenList = new ArrayList<Paint>();
		
		for(String sym:symList)  //��ʽ����ɫ1,���1,����ʽ1@.....������ʽ1=float[ż����]
		{
	        Paint pPen = new Paint();
	        pPen.setAntiAlias(true);
	        pPen.setStyle(android.graphics.Paint.Style.STROKE);
	        if (!sym.equals(""))
	        {
	        	String[] symStyle = sym.split(",");
		        pPen.setColor(Color.parseColor(symStyle[0]));
		        pPen.setStrokeWidth(Float.valueOf(symStyle[1]));
		        if (symStyle.length==3)
		        {
		        	String[] syList = symStyle[2].split("\\*");
		        	float[] syFList = new float[syList.length];
		        	Log.d("���Ž���", symStyle[2]);
		        	for(int i=0;i<syList.length;i++)syFList[i]=Float.parseFloat(syList[i]);
			        PathEffect effects = new DashPathEffect(syFList,0);
			        pPen.setPathEffect(effects);
		        }
	        }
	        pPenList.add(pPen);
		}
		this.setStyle(pPenList);
    }
    
    /**
     * �������ת����Base64�ַ���
     * @return
     */
    public String ToBase64()
    {
    	return this._SymbolBase64Str;
    }
    
    /**
     * ����ָ����С�ķ���ָʾͼ
     * @param Width
     * @param Height
     * @return
     */
    public Bitmap ToFigureBitmap(int Width,int Height)
    {
		Bitmap bp = Bitmap.createBitmap(Width,Height,Config.ARGB_8888);
		Canvas g = new Canvas(bp);
		for(Paint pPen:this.getStyle())  //��ʽ����ɫ1,���1,����ʽ1
		{
	        g.drawLine(0, Height/2, Width, Height/2, pPen);
		}
		return bp;
    }
    
    //���ʼ�
    private List<Paint> _PenList = new ArrayList<Paint>();           //���ʼ���
    public List<Paint> getStyle()
    {
         return _PenList;
    }
    public void setStyle(List<Paint> value)
    {
    	_PenList = value; 
    }


    //���Ʒ���
	@Override
	public void Draw(Map map, Geometry pGeometry)
    {
        this.Draw(map, map.getDisplayGraphic(), pGeometry, 0, 0, lkDrawType.enNormal);
    }

	@Override
	public void Draw(Map map, Canvas g, Geometry pGeometry, int OffsetX,
					 int OffsetY, lkDrawType DrawType)
    {
        if (pGeometry.getStatus() == lkGeometryStatus.enDelete) return;

        Polyline PL = (Polyline)pGeometry;
        Point[] OPF = null;

        //�ж��Ƿ���Ҫ���ò���
        if (map.getExtend().Contains(pGeometry.getEnvelope()))
        {
            OPF = map.getViewConvert().MapPointsToScreePoints(PL.GetPartAt(0).getVertexList(),true, OffsetX, OffsetY);
        }
        else
        {
            OPF = map.getViewConvert().ClipPolyline(PL.GetPartAt(0).getVertexList(), OffsetX, OffsetY);
        }
        //

        if (OPF.length == 0) return;
        if (OPF.length >= 2)
        {
         	Path p = new Path();
        	for(int i=0;i<OPF.length;i++)
        	{
        		if (i==0)p.moveTo(OPF[i].x, OPF[i].y);
        		else p.lineTo(OPF[i].x, OPF[i].y);
        	}
        	//p.setFillType(FillType.EVEN_ODD);
        	//g.drawLines(Tools.PointListToFloatList(OPF), this.getStyle());
        	
//        	//����Google��ͼ�����ͼӱ߿�
//        	if (PubVar.m_Map.getGMap().getIfLoadGoogleMap())
//        	{
//	            for (Paint pPen : this.getStyle())
//	            {
//	                float PW = pPen.getStrokeWidth()+2;
//	                this.getBKLinePaint().setStrokeWidth(map.SetDPI(PW));
//	                this.getBKLinePaint().setSubpixelText(true);
//	                this.getBKLinePaint().setAntiAlias(true);
//	                g.drawPath(p, this.getBKLinePaint());
//	            }
//        	}
        	
        	
        	if (DrawType==lkDrawType.enNormal)
        	{
	            for (Paint pPen : this.getStyle())
	            {
	                float PW = pPen.getStrokeWidth();
	                pPen.setStrokeWidth(PW);
	                pPen.setSubpixelText(true);
	                pPen.setAntiAlias(true);
	                pPen.setStrokeJoin(Join.BEVEL);
	                g.drawPath(p, pPen);
	                pPen.setStrokeWidth(PW);
	            }
        	} 
        	else
        	{
        		
                Paint pBrush = new Paint();
                pBrush.setStyle(Style.STROKE);
                if (DrawType==lkDrawType.enSelected_NoEditing)
                {
                	pBrush.setColor(Color.rgb(0, 255, 255));
                    pBrush.setStrokeWidth(Tools.DPToPix(8));
                } else
                {
                	pBrush.setColor(Color.BLUE);
                    pBrush.setStrokeWidth(Tools.DPToPix(5));
                }
                pBrush.setAntiAlias(true);
                pBrush.setStrokeJoin(Join.BEVEL);
                g.drawPath(p, pBrush);
        	}
            

        }
        switch (DrawType)
        {
            case enSelected_Editing:   //���ڱ༭
                //��ѡ���ߵ��ڽڵ�
                int H = Tools.DPToPix(8), W = H;
                int PFCount = OPF.length;
                Paint pBrush = new Paint();
                pBrush.setColor(Color.BLACK);
                for (int i = 1; i < PFCount - 1; i++)
                {
                    //g.FillRectangle(pBrush, );
                	g.drawRect(OPF[i].x - W / 2, OPF[i].y - H / 2, OPF[i].x+W/2, OPF[i].y+H/2, pBrush);
                }
                pBrush.setColor(Color.BLUE);
                
                break;
        }

        if ((DrawType == lkDrawType.enSelected_Editing) || (DrawType == lkDrawType.enSelected_NoEditing))
        {
            int HH = Tools.DPToPix(10), WW = HH;
            //�����
            Paint pBrush = new Paint();
            pBrush.setColor(Color.GREEN);
            //using (Brush pBrush = new SolidBrush(Color.Green))
            {
                g.drawRect(OPF[0].x - WW / 2, OPF[0].y - HH / 2, OPF[0].x + WW / 2, OPF[0].y + HH / 2,pBrush);
            }

            //��ֹ��
            //using (Brush pBrush = new SolidBrush(Color.Red))
            {
            	float x1 = OPF[OPF.length - 1].x;
            	float y1 = OPF[OPF.length - 1].y;
            	pBrush.setColor(Color.RED);
                g.drawRect(x1 - WW / 2, y1 - HH / 2, x1 + WW / 2, y1 + HH / 2,pBrush);
            }
        }


    }


	@Override
	public void DrawLabel(Map map, Canvas g, Geometry pGeometry, int OffsetX,int OffsetY, lkSelectionType pSelectionType)
    {
//        String LabelText = pGeometry.getTag();
//        int VertexCount = pGeometry.GetPartAt(0).getVertexList().size();
//        Coordinate midPoint = pGeometry.GetPartAt(0).getVertexList().get(VertexCount/2);
//        if (VertexCount % 2==0)
//        {
//        	Coordinate PT2 = pGeometry.GetPartAt(0).getVertexList().get(VertexCount/2-1);
//        	midPoint = new Coordinate((midPoint.getX()+PT2.getX())/2,(midPoint.getY()+PT2.getY())/2);
//        }
//        
//        Point _CenterCoorPix = map.getViewConvert().MapToScreen(midPoint);
//        this.getTextSymbol().Draw(g, _CenterCoorPix.x, _CenterCoorPix.y, LabelText, lkTextPosition.enCenter, pSelectionType);
        
		
        Point[] OPF = null;

        //�ж��Ƿ���Ҫ���ò���
        if (map.getExtend().Contains(pGeometry.getEnvelope()))
        {
            OPF = map.getViewConvert().MapPointsToScreePoints(pGeometry.GetPartAt(0).getVertexList(),true, OffsetX, OffsetY);
        }
        else
        {
            OPF = map.getViewConvert().ClipPolyline(pGeometry.GetPartAt(0).getVertexList(), OffsetX, OffsetY);
        }
        
        
        try
        {
        	String Text =  pGeometry.getTag();
            if (OPF.length == 0) return;


            //����ע���ı��Ŀ��
            float pWidth = this.getTextSymbol().getTextFont().measureText(Text);
            
            //�������ֿ��
            double pOneTextWidth = this.getTextSymbol().getTextFont().measureText("��");
            
            //����֮��ļ��
            double pTextMargin = pOneTextWidth / 2;
            
            //���м����´��ľ��룬��֤������������ʾ
            double AllLen = this.GetLength(OPF)/2-pWidth / 2 - pTextMargin * Text.length()/2;
            
            //�����߶��е㴦������
            int StartTextPointIndex = 0;
            for (int idx = 0; idx < OPF.length - 1; idx++)
            {
                Point PT1 = OPF[idx];
                Point PT2 = OPF[idx + 1];
                double DIST = this.GetTowPointDistance(PT1, PT2);
                
                if ((AllLen-DIST)<0)
                {
                    Point textPoint = this.GetToStartCoordinate(PT1, PT2, Math.abs(AllLen));
                    OPF[idx] = textPoint;
                   StartTextPointIndex = idx;
                   break;
                } AllLen -= DIST;
                
            }

            //����ÿһ�����ֵ�����λ��
            int textIndex = 0;double lsDist = 0;
            List<Point> textPointList = new ArrayList<Point>();
            for (int idx = StartTextPointIndex; idx < OPF.length - 1; idx++)
            {
                Point PT1 = OPF[idx];
                Point PT2 = OPF[idx + 1];
                double DIST = this.GetTowPointDistance(PT1, PT2);
                if ((lsDist + DIST) > (pOneTextWidth+pTextMargin))
                {
                    Point textPoint = this.GetToStartCoordinate(PT1, PT2, pOneTextWidth+pTextMargin - lsDist);
                    textPointList.add(textPoint);
                    textIndex++;
                    if (textIndex >= Text.length()) break;
                    lsDist = 0; OPF[idx] = textPoint; idx--;
                }
                else lsDist += DIST;
            }

            //�������ֵ����˳��
            boolean ChangeFlip = false;
            Point AP1 = textPointList.get(0);
            Point AP2 = textPointList.get(textPointList.size() - 1);
            if (AP1.x >= AP2.x) ChangeFlip = true;


            for(int i=0;i<textPointList.size();i++)
            {
                Point textPoint = textPointList.get(i);
                int txtIdx = i; if (ChangeFlip) txtIdx = textPointList.size() - 1 - i;
                this.getTextSymbol().Draw(g, textPoint.x, textPoint.y, Text.substring(txtIdx, txtIdx+1), lkTextPosition.enCenter, pSelectionType);
            }

        }
        catch(Exception e)
        {

        }
        
    }
	
	
    //���ȷֵ�
    private double GetLength(Point[] pfList)
    {
        double Len = 0;
        for (int i = 0; i < pfList.length - 1; i++)
        {
            Len += this.GetTowPointDistance(pfList[i], pfList[i + 1]);
        }
        return Len;
    }

    private Point GetToStartCoordinate(Point StartPoint, Point EndPoint, double ToStartDistance)
    {
        //���ȷֵ㹫ʽ��x=��x1+��x2��/��1+�ˣ���y=��y1+��y2��/��1+�ˣ�
        double X1 = StartPoint.x; double Y1 = StartPoint.y;
        double X2 = EndPoint.x; double Y2 = EndPoint.y;
        double S1 = ToStartDistance;
        double S2 = this.GetTowPointDistance(StartPoint,EndPoint) - ToStartDistance;
        if (ToStartDistance == 0) return StartPoint;
        if (S2 == 0) return EndPoint;
        double S = S1 / S2;
        double X = (X1 + S * X2) / (1 + S);
        double Y = (Y1 + S * Y2) / (1 + S);

        //��֤�Ƿ���ȷ
        Point newPointF = new Point();newPointF.set((int)X,(int)Y);

        return newPointF;
    }

    private double GetTowPointDistance(Point P1, Point P2)
    {
        return Math.sqrt((P1.x - P2.x) * (P1.x - P2.x) + (P1.y - P2.y) * (P1.y - P2.y));
    }

}
