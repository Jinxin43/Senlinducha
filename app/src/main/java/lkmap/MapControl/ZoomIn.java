package lkmap.MapControl;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class ZoomIn implements ICommand,IOnPaint
{
    private ICommand _Command;
    private TrackRectangle TR_X;
    private MapControl _MapControl = null;

    public ZoomIn(MapControl MC)
    {
        TR_X = new TrackRectangle(MC);
        _Command = TR_X;
        _MapControl = MC;
    }

	@Override
	public void MouseDown(MotionEvent e)
    {
        _Command.MouseDown(e);
    }

	@Override
	public void MouseMove(MotionEvent e)
    {
        _Command.MouseMove(e);
    }

	@Override
	public void MouseUp(MotionEvent e)
    {
        _Command.MouseUp(e);
        if (TR_X.getTrackEnvelope() == null) 
        {
            TR_X.setTrackEnvelope(this._MapControl.getMap().getExtend().Scale(0.5));
        } else 
        	
        {
        	this._MapControl.getMap().setExtend(TR_X.getTrackEnvelope());
        }
        //�����Ҫ��ʾGoogleMap��ͼ���������ʾ��Χ����
//        if (this._MapControl.getMap().getGMap().getIfLoadGoogleMap() &&
//            this._MapControl.getMap().getGMap().getZoomOnScale())
//        	this._MapControl.getMap().getGMap().SetCurrentLevel();
//        else 
        	this._MapControl.getMap().Refresh();
    }

	@Override
	public void OnPaint(Canvas g)
	{
		TR_X.OnPaint(g);
	}

}
