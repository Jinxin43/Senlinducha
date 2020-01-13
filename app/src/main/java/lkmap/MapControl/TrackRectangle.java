package lkmap.MapControl;

import java.util.ArrayList;
import java.util.List;

import lkmap.Cargeometry.Envelope;
import lkmap.Tools.Tools;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;


public class TrackRectangle implements ICommand,IOnPaint
{
	private MapControl _MapControl;

    //��̬ˢ����ʽ
    private Paint m_TrackPen = null;
    

    //��Ա����
    private android.graphics.PointF m_StartPoint, m_MovePoint;

    //���ض�̬����
    private Envelope _TrackEnvelope = null;
    private RectF _TrackRectF = null;
    public Envelope getTrackEnvelope()
    {
         return _TrackEnvelope;
    }
    public void setTrackEnvelope(Envelope value)
    {
    	 _TrackEnvelope = value;
    }

    public TrackRectangle(MapControl MC)
    {
        _MapControl = MC;
        
        // ����ɫ50%͸�� 
        m_TrackPen = new Paint();
        m_TrackPen.setStyle(Style.STROKE);
        m_TrackPen.setColor(Color.RED);
        m_TrackPen.setStrokeWidth(2);
        int opacity = 127; 
        m_TrackPen.setAlpha(opacity);
    }

    //��갴���¼�
	private boolean LeftDown = false;
	@Override
	public void MouseDown(MotionEvent e)
    {
        _TrackEnvelope = null;
        _MapControl.SetOnPaint(this);
        m_StartPoint = new android.graphics.PointF(e.getX(),e.getY());
        LeftDown = true;
    }

    //����ƶ��¼�
	@Override
	public void MouseMove(MotionEvent e)
    {
        if (LeftDown)  //�������
        {
            m_MovePoint = new android.graphics.PointF(e.getX(), e.getY());
            _MapControl.invalidate(this.GetRefreshRect(m_StartPoint, m_MovePoint));
        }
    }

    //�õ����㹹�ɵľ���
    private Rect GetRefreshRect(android.graphics.PointF Pt1, android.graphics.PointF Pt2)
    {
        float MinX = Math.min(Pt1.x, Pt2.x);
        float MinY = Math.min(Pt1.y, Pt2.y);
        float MaxX = Math.max(Pt1.x, Pt2.x);
        float MaxY = Math.max(Pt1.y, Pt2.y);
        
        float Offset = 10;
        if (_TrackRectF==null)
    	{
    		_TrackRectF=new RectF(MinX,MinY,MaxX,MaxY);
    	}
        else
        {
        	_TrackRectF.left = MinX;_TrackRectF.top = MinY;
        	_TrackRectF.right = MaxX;_TrackRectF.bottom=MaxY;
        }
        return new Rect((int)(_TrackRectF.left-Offset),(int)(_TrackRectF.top-Offset),
        				(int)(_TrackRectF.right+Offset*2),(int)(_TrackRectF.bottom+Offset*2));
    }

    //����ɿ��¼�
	@Override
	public void MouseUp(MotionEvent e)
    {
		LeftDown=false;
        //�ж���С�����ֵ�����췵�صľ��ο�ʵ�ʵ�λ���ף�
        if (_TrackRectF!=null)
        {
            this._TrackEnvelope = new Envelope(_MapControl.getMap().getViewConvert().ScreenToMap(_TrackRectF.left, _TrackRectF.top),
                                              _MapControl.getMap().getViewConvert().ScreenToMap(_TrackRectF.right, _TrackRectF.bottom));

            //ȷ�����ε����ͣ���������Σ����Ƿ������
            if (e.getX() > m_StartPoint.x) this._TrackEnvelope.setType(true);
            else this._TrackEnvelope.setType(false);
        }
        _TrackRectF=null;
        _MapControl.invalidate();

        //ǿ�ƻ�������
        //GC.Collect();
    }

    //��̬ˢ�¾��ο�
	@Override
	public void OnPaint(Canvas g)
    {
        if (_TrackRectF==null) return;
        g.drawRect(_TrackRectF, m_TrackPen);
        //m_TrackPen.Color = Color.Red; m_TrackPen.DashStyle = System.Drawing.Drawing2D.DashStyle.Dot;
//        pe.Graphics.Clip = new Region(pe.ClipRectangle);
//        System.Drawing.Point[] DrawLine = m_TrackLine.ToArray();
//        pe.Graphics.DrawLines(m_TrackPen, DrawLine);

    }

}
