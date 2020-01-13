package lkmap.MapControl;

import java.util.HashMap;

import javax.crypto.spec.GCMParameterSpec;

import com.dingtu.senlinducha.R;

import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Envelope;
import lkmap.Cargeometry.Size;
import lkmap.Map.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.graphics.Region.Op;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import dingtu.ZRoadMap.PubVar;

public class MapControl extends ImageView 
{

	public MapControl(Context context) {
		super(context);
		//��ʼ��MapControl
		this.InitMapControl();
	}

	public Envelope TrackingRectangle;

    private IOnPaint _IOnPaint;                 //��̬ˢ�½ӿ�
    public IOnPaint _GPSMapPaint;				//GPS״̬ˢ�½ӿ�
    private ICommand _ICommand;                 //���������ӿ�
    private IOnTouchCommand _IOnTouchCommand;   //���Ʋ����ӿ�
    private ZoomIn _ZoomIn;                     //�Ŵ������
    private ZoomOut _ZoomOut;                   //��С������
    private ZoomInOutPan _ZoomInOutPan;			//���ƷŴ���С��
    public Pan _Pan;                           //����������
    public Select _Select;                     //ʵ��ѡ����
    private Shutter _Shutter = null;			//��������
    
    
    //private MeasureArea _MeasureArea;           //���������

    //ǰһ�����������ʽ������
    public Tools m_BeforeTool;
    //public Cursor m_BeforeCursor;
    public ICommand m_BeforeCommand;
    public IOnPaint m_BeforeIOnPaint;


    //���캯��

    public void InitMapControl()
    {
    
//        base.MouseMove += new System.Windows.Forms.MouseEventHandler(this.MapControl_MouseMove);
//        base.MouseUp += new System.Windows.Forms.MouseEventHandler(this.MapControl_MouseUp);
//        base.MouseDown += new System.Windows.Forms.MouseEventHandler(this.MapControl_MouseDown);
//#if PC
//        base.MouseDoubleClick += new MouseEventHandler(MapControl_MouseDoubleClick);
//
//        base.MouseWheel += new System.Windows.Forms.MouseEventHandler(this.MapControl_MouseWheel);
//#endif

        _ZoomIn = new ZoomIn(this);
        _ZoomOut = new ZoomOut(this);
        _ZoomInOutPan = new ZoomInOutPan(this);
        _Pan = new Pan(this);
        _Select = new Select(this, true);
        _Shutter = new Shutter(this);
        //_MeasureArea = new MeasureArea(this);

        this.setActiveTool(Tools.None);
        
    }



    //����

    //ȡ��Map����
    private Map _Map;
    public Map getMap()
    {
       return _Map;
    }
    public void setMap(Map value)
    {
    	 _Map = value;
    }


    //��Ա����

    //���ò�������
    public void SetCommand(ICommand _Command)
    {
        _ICommand = _Command;
    }

    //����ˢ�µ�����
    public void SetOnPaint(IOnPaint _OnPaint)
    {
        _IOnPaint = _OnPaint;
    }

	private void SetZoomInOut(float scale)
	{
		int gle_ZoomInAndOut = -1;
		_Map.setExtend(_Map.getExtend().Scale(scale));

		_Map.Refresh();
	}
	
	public void SetZoomIn()
	{
		this.SetZoomInOut(0.5f);
		 _Map.Refresh();
	}
	
	public void SetZoomOut()
	{
		this.SetZoomInOut(2f);
		 _Map.Refresh();
	}
    
    //����ö��
    private Tools _Activetool;
    public Tools getActiveTool()
    {
    	return _Activetool;
    }
    
    public void setActiveTools(Tools tools,IOnPaint _OnPaint,ICommand _Command)
    {
    	this.setActiveTool(tools);
        _ICommand = _Command;
        _IOnPaint = _OnPaint;
        _Map.Refresh();
    }
    public void setActiveTools(Tools tools,IOnPaint _OnPaint,IOnTouchCommand _Command)
    {
    	this.setActiveTool(tools);
    	_IOnTouchCommand = _Command;
        _IOnPaint = _OnPaint;
        _Map.Refresh();
    }
    
    
    public void setActiveTool(Tools value)
    {
    	//Ĭ�Ͽ�
    	 _IOnTouchCommand = _ZoomInOutPan;
    	if (value!=Tools.FullScreen)
		{
			if (value!=Tools.FullScreenSize)_Activetool = value;
		}
        switch (value)
        {
            case None:
                _ICommand = null;
                _IOnTouchCommand = _ZoomInOutPan;
                _IOnPaint = _ZoomInOutPan;
                break;
            case Shutter:
            	_IOnTouchCommand = _Shutter;
                _IOnPaint = _Shutter;
                _Shutter.StartShutter();
                break;
            case ZoomInOutPan:
            	_IOnTouchCommand = _ZoomInOutPan;
                _IOnPaint = _ZoomInOutPan;
                _Map.Refresh();
            	break;
            //�Ŵ�
            case ZoomIn:
                _ICommand = _ZoomIn;
                _IOnPaint = _ZoomIn;
                break;
            //��С
            case ZoomOut:
                _ICommand = _ZoomOut;
                _IOnPaint = _ZoomIn;
                break;
            //����
            case Pan:
                _IOnPaint = _Pan;
                break;
            //ȫ��
            case FullScreenSize:
            	if (this._Map==null) return;
    	    	int w = this.getWidth();
    	    	int h = this.getHeight();
    	    	if (!(w==this._Map.getSize().getWidth() && h==this._Map.getSize().getHeight()))
    	    	{
    	    		this._Map.setSize(new Size(w,h));
    	    	}
    	        
            	break;
            case FullScreen:
            	setActiveTool(Tools.FullScreenSize);
            	if (this.getMap()==null)return;
            	this._Map.setExtend(this._Map.getFullExtendForView());
    	        this._Map.Refresh();
                break;
            //ѡ��
            case Select:
            	_IOnTouchCommand = _Select;
            	this._Map.Refresh();
                break;
//            case MoveObject:
//                _ICommand = _MoveObject;
//                _IOnPaint = _MoveObject;
//                break;

            	
            //��ѯ
            case Query:
                break;
            //��������
            case MeasureLength:
                break;
            //�������
            case MeasureArea:
                //_ICommand = _MeasureArea;
                break;
            case CallMile:
                _ICommand = null;
                break;
        }
    }
    

    
    @Override
	public boolean onTouchEvent(MotionEvent event) 
    {
    	if(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value == null || 
				PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value.equals("true"))
    	{
    		PubVar.m_DoEvent.m_GlassView.SetGlassPoint(event.getX(),event.getY());
    	}
    	
    	if (this._IOnTouchCommand!=null)
    	{
    		this._IOnTouchCommand.SetOnTouchEvent(event);
    		
    		
    		//����Ŵ����ĵ�
//    		final int x = (int) event.getX();
//    		final int y = (int) event.getY();
//    		// ���λ�ñ�ʾ���ǣ���shader����ʼλ��
//    		drawable = new ShapeDrawable(new OvalShape());
//    		matrix = new Matrix();
//    		//matrix.setTranslate(RADIUS - x * FACTOR, RADIUS - y * FACTOR);
//    		matrix.setTranslate(this.getWidth()-x-30, this.getWidth()-y-30);
    		
    	}
    	
//		  int action = event.getActionMasked();
//          int p = event.getPointerCount();//�Ӵ�����
//          
//		switch(event.getAction() & MotionEvent.ACTION_MASK)
//		{
//			case MotionEvent.ACTION_DOWN:
//				this.MapControl_MouseDown(event);
//				break;
//			case MotionEvent.ACTION_UP:
//				this.MapControl_MouseUp(event);
//				break;
//			case MotionEvent.ACTION_MOVE:
//				this.MapControl_MouseMove(event);
//				break;
//			case MotionEvent.ACTION_POINTER_DOWN:
//		        //if (p>1){this.SetZoomOut();return true;}  //��С
//				if (this._Activetool==Tools.ZoomInOutPan)
//					this.MapControl_MouseDown(event);
//				break;
//			case MotionEvent.ACTION_POINTER_UP:
//				if (this._Activetool==Tools.ZoomInOutPan)
//					this.MapControl_MouseUp(event);
//				break;
//		}
//		int i = event.getPointerCount();
//		i+=1;
//		
//		

		return true;
	}


    private void MapControl_MouseDown(MotionEvent e)
    {
        //m_MouseButtons = e.Button;
        if (_Map != null)
        {
            //�����м�������
//            if (e.Button == MouseButtons.Middle)
//            {
//                //m_BeforeTool = _Activetool;
//                //m_BeforeCursor = this.Cursor;
//                m_BeforeIOnPaint = _IOnPaint;
//                m_BeforeCommand = _ICommand;
//                ActiveTool = Tools.Pan;
//                _IOnPaint = null;
//
//            }
            if (_Activetool == Tools.None) return;
            if (_ICommand == null) return;
            _ICommand.MouseDown(e);
        }
    }

    private void MapControl_MouseMove(MotionEvent e)
    {
        if (_Map != null)
        {
            if (_Activetool == Tools.None) return;
            if (_ICommand == null) return;
            _ICommand.MouseMove(e);
        }
    }

    private void MapControl_MouseUp(MotionEvent e)
    {
        //m_MouseButtons = MouseButtons.None;
        //if (e.Button == MouseButtons.Right) return;
        if (_Map != null)
        {
            if (_Activetool == Tools.None) return;
            if (_ICommand == null) return;
            _ICommand.MouseUp(e);
        }
    }

    private HashMap<String,IOnPaint> m_OnPaintList = new HashMap<String,IOnPaint>();
    public void AddOnPaint(String Id,IOnPaint pOnPaint)
    {
    	if(!this.m_OnPaintList.containsKey(Id))this.m_OnPaintList.put(Id,pOnPaint);
    }
    public void ClearOnPaint(String Id)
    {
    	this.m_OnPaintList.remove(Id);
    }

    //ͨ���ӿڶ�̬ˢ�¿ؼ�
   
    boolean FirstLoad = true;
	@Override
	protected void onDraw(Canvas canvas) 
	{
		if (FirstLoad)FirstLoad=false;

		super.onDraw(canvas);
        if (_IOnPaint != null)  _IOnPaint.OnPaint(canvas);
        
        Log.d("asdf", "X: Y:");
        
        
        if (this._Map==null)return;
        if (this._Map.getInvalidMap()) return;
        if (_GPSMapPaint!=null)_GPSMapPaint.OnPaint(canvas);
        for(IOnPaint pOnPaint:this.m_OnPaintList.values())pOnPaint.OnPaint(canvas);
        
        
        
		if(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value == null || 
				PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value.equals("true"))
    	{
            PubVar.m_DoEvent.m_GlassView.Refresh();

    	}
	}
}

