package lkmap.MapControl;

import android.view.MotionEvent;

public interface ICommand 
{
    void MouseDown(MotionEvent e);
    void MouseMove(MotionEvent e);
    void MouseUp(MotionEvent e);
}
