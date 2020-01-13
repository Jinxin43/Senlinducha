package com.dingtu.DTGIS.Control;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("ClickableViewAccessibility")
public class LinePathView extends View {
	
	private  static final  String TAG=LinePathView.class.getSimpleName();

    private Context mContext;

    /**
     * �ʻ�X�������
     */
    private float mX;
    /**
     * �ʻ�Y�������
     */
    private float mY;
    /**
     * ��д����
     */
    private final Paint mGesturePaint = new Paint();
    /**
     * ·��
     */
    private final Path mPath = new Path();
    /**
     * ��������
     */
    private Canvas cacheCanvas;
    /**
     * ����Bitmap����
     */
    private Bitmap cachebBitmap;
    /**
     * �Ƿ��Ѿ�ǩ��
     */
    private boolean isTouched = false;


    /**
     * ���ʿ�� px��
     */
    private int mPaintWidth = 10;

    /**
     * ǰ��ɫ
     */
    private int mPenColor = Color.BLACK;

    private int mBackColor=Color.TRANSPARENT;
    public LinePathView(Context context) {
        super(context);
        init(context);
    }

    public LinePathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LinePathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Style.STROKE);
        mGesturePaint.setStrokeWidth(mPaintWidth);
        mGesturePaint.setColor(mPenColor);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cachebBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        cacheCanvas = new Canvas(cachebBitmap);
        cacheCanvas.drawColor(mBackColor);
        isTouched=false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                isTouched = true;
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(mPath, mGesturePaint);
                mPath.reset();
                break;
        }
        // ���»���
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(cachebBitmap, 0, 0, mGesturePaint);
        // ͨ���������ƶ���γɵ�ͼ��
        canvas.drawPath(mPath, mGesturePaint);
    }

    // ��ָ������Ļʱ����
    private void touchDown(MotionEvent event) {

        // mPath.rewind();
        // ���û���·�ߣ�������֮ǰ���ƵĹ켣
        mPath.reset();
        float x = event.getX();
        float y = event.getY();

        mX = x;
        mY = y;
        // mPath���ƵĻ������
        mPath.moveTo(x, y);
    }

    // ��ָ����Ļ�ϻ���ʱ����
    private void touchMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        final float previousX = mX;
        final float previousY = mY;

        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);

        // ����֮��ľ�����ڵ���3ʱ�����ɱ�������������
        if (dx >= 3 || dy >= 3) {
            // ���ñ��������ߵĲ�����Ϊ�����յ��һ��
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;

            // ���α�������ʵ��ƽ�����ߣ�previousX, previousYΪ�����㣬cX, cYΪ�յ�
            mPath.quadTo(previousX, previousY, cX, cY);

            // �ڶ���ִ��ʱ����һ�ν������õ�����ֵ����Ϊ�ڶ��ε��õĳ�ʼ����ֵ
            mX = x;
            mY = y;
        }
    }

    /**
     * �������
     */
    public void clear() {
        if (cacheCanvas != null) {
            isTouched = false;
            mGesturePaint.setColor(mPenColor);
            cacheCanvas.drawColor(mBackColor, android.graphics.PorterDuff.Mode.CLEAR);
            mGesturePaint.setColor(mPenColor);
            invalidate();
        }
    }


    /**
     * ���滭��
     *
     * @param path ���浽·��
     */

    public void save(String path) throws IOException {
        save(path, false, 0);
    }

    /**
     * ���滭��
     *
     * @param path       ���浽·��
     * @param clearBlank �Ƿ�����հ�����
     * @param blank  ��Ե�հ�����
     */
    public void save(String path, boolean clearBlank, int blank) throws IOException {

        Bitmap bitmap=cachebBitmap;
        //BitmapUtil.createScaledBitmapByHeight(srcBitmap, 300);//  ѹ��ͼƬ
        if (clearBlank) {
            bitmap = clearBlank(bitmap, blank);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] buffer = bos.toByteArray();
        if (buffer != null) {
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }
                OutputStream outputStream = new FileOutputStream(file);
                outputStream.write(buffer);
                outputStream.close();
        }
    }

    /**
     * ��ȡ�����bitmap
     * @return
     */
    public Bitmap getBitMap()
    {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap=getDrawingCache();
        setDrawingCacheEnabled(false);
        return bitmap;
    }




    /**
     * ����ɨ�� ����߽�հס�
     *
     * @param bp
     * @param blank �߾������ٸ�����
     * @return
     */
    private Bitmap clearBlank(Bitmap bp, int blank) {
        int HEIGHT = bp.getHeight();
        int WIDTH = bp.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[WIDTH];
        boolean isStop;
        for (int y = 0; y < HEIGHT; y++) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int y = HEIGHT - 1; y >= 0; y--) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    bottom = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int x = WIDTH - 1; x > 0; x--) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > WIDTH - 1 ? WIDTH - 1 : right + blank;
        bottom = bottom + blank > HEIGHT - 1 ? HEIGHT - 1 : bottom + blank;
        return Bitmap.createBitmap(bp, left, top, right - left, bottom - top);
    }

    /**
     * ���û��ʿ�� Ĭ�Ͽ��Ϊ10px
     *
     * @param mPaintWidth
     */
    public void setPaintWidth(int mPaintWidth) {
        mPaintWidth = mPaintWidth > 0 ? mPaintWidth : 10;
        this.mPaintWidth = mPaintWidth;
        mGesturePaint.setStrokeWidth(mPaintWidth);

    }


    public void setBackColor(int backColor)
    {
        mBackColor=backColor;
    }


    /**
     * ���û�����ɫ
     *
     * @param mPenColor
     */
    public void setPenColor(int mPenColor) {
        this.mPenColor = mPenColor;
        mGesturePaint.setColor(mPenColor);
    }

    /**
     * �Ƿ���ǩ��
     *
     * @return
     */
    public boolean getTouched() {
        return isTouched;
    }

}
