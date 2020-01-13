package lkmap.Report;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

public class v1_myScrollView extends ScrollView 
{

	public v1_myScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private GestureDetector mGesture;
	public v1_myScrollView(Context context) 
	{
		super(context);
		this.mGesture = new GestureDetector(context, mOnGesture);
	}
	
	public v1_myScrollView(Context context, AttributeSet attrs) 
	{
        super(context, attrs);
        mGesture = new GestureDetector(context, mOnGesture);
    }

	/** 分发触摸事件 */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) 
    {
        super.dispatchTouchEvent(ev);
        return mGesture.onTouchEvent(ev);
        

    }
    
    @Override  
    protected void onScrollChanged(int x, int y, int oldx, int oldy) 
    {  
        super.onScrollChanged(x, y, oldx, oldy);  
        if (m_bindScrollView!=null)
        m_bindScrollView.scrollTo(0, y);
    } 
    
    private v1_myScrollView m_bindScrollView = null;
    public void SetBindScrollView(v1_myScrollView lv)
    {
    	this.m_bindScrollView = lv;
    }

    private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        


//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//                float velocityY) {
//        	m_bindScrollView.scrollTo(0, getScrollY());
//            return true;
//        }
//
//        /** 滚动 */
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY) 
//        {
//            synchronized (myScrollView.this) 
//            {
//            	m_bindScrollView.scrollTo(0, getScrollY());
//            }
//            return true;
//        }
    };

}
