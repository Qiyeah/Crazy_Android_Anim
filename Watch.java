package com.example.qi.anim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.SumPathEffect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * Created by Qi on 2016/9/1.
 */
public class Watch extends SurfaceView implements SurfaceHolder.Callback {
    private DrawingThread mDrawingThread;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private PathEffect[] effects = new PathEffect[7];
    private float phase;
    private float width;
    private float height;
    private float centerX;
    private float centerY;
    private float per;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size;
        int width = getMeasuredWidth();
        int height = getHeight();
        System.out.println(width+"--"+height);
        size = width>height?height:width;
        setMeasuredDimension(size,size);
    }

    public Watch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        /*mPaint = new Paint();
        //mPaint.setColor(0xffffffff);
        mPaint.setStyle(Paint.Style.FILL);*/
        mDrawingThread = new DrawingThread(mSurfaceHolder);


    }
    float angle;
    @Override
    protected void onDraw(Canvas canvas) {
        width = canvas.getWidth();
        height = canvas.getHeight();
        centerX = width/2;
        centerY = height/2;
        per =height>width?height/width:width/height;

        effects[0] = null;
        effects[1] = new CornerPathEffect(10);
        effects[2] = new DiscretePathEffect(3f,5f);
        effects[3] = new DashPathEffect(new float[]{20,10,5,0},phase);


        drawPoint(canvas);
        drawBackGround(canvas);
        invalidate();



//        canvas.translate(0,0);

    }
    private void drawPoint(Canvas canvas){
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(2);
        canvas.drawLine(centerX,centerY,centerX-width/10,centerY-height/10,mPaint);
        canvas.rotate(angle,centerX,centerY);
    }
    private void drawBackGround(Canvas canvas){
        mPaint = new Paint();
        RectF oval=new RectF();                     //RectF对象
        if (height>width){
            oval.left= 50;                              //左边
            oval.top=(height-width)/2+50;                                   //上边
            oval.right=width-50;                             //右边
            oval.bottom=(height-width)/2+width-50;                                //下边
        }else {
            oval.left= (width-height)/2;                                //左边
            oval.top= 0;                                //上边
            oval.right=(width-height)/2+height;                             //右边
            oval.bottom= height;                              //下边
        }
        Path path = new Path();
        path.addRect(0,0,1,10,Path.Direction.CCW);
        effects[0] = new PathDashPathEffect(path,8.88f,phase,PathDashPathEffect.Style.ROTATE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(20);
        // mPaint.setPathEffect(effects[0]);
        canvas.drawArc(oval, 180, 180, false, mPaint);

        oval=new RectF();                     //RectF对象
        if (height>width){
            oval.left= 100;                              //左边
            oval.top=(height-width)/2+100;                                   //上边
            oval.right=width-100;                             //右边
            oval.bottom=(height-width)/2+width-100;                                //下边
        }else {
            oval.left= (width-height)/2;                                //左边
            oval.top= 0;                                //上边
            oval.right=(width-height)/2+height;                             //右边
            oval.bottom= height;                              //下边
        }
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.WHITE);
        canvas.drawArc(oval,180,180,false,mPaint);

        path = new Path();
        path.addRect(0,0,1,17,Path.Direction.CCW);
        effects[0] = new PathDashPathEffect(path,8.88f,phase,PathDashPathEffect.Style.ROTATE);
        mPaint.setColor(Color.WHITE);
        mPaint.setPathEffect(effects[0]);
        canvas.drawArc(oval, 180, 180, false, mPaint);



        path = new Path();
        path.addRect(0,0,2,25,Path.Direction.CCW);
        effects[0] = new PathDashPathEffect(path,44.4f,phase,PathDashPathEffect.Style.ROTATE);
        mPaint.setColor(Color.WHITE);
        mPaint.setPathEffect(effects[0]);
        canvas.drawArc(oval, 180, 180, false, mPaint);
        /**
         * 初始化PathDashPathEffect
         */
        path = new Path();
        path.addRect(0,0,4,55,Path.Direction.CCW);
        effects[4] = new PathDashPathEffect(path,88.8f,phase,PathDashPathEffect.Style.ROTATE);
        /**
         * 初始化PathDashPathEffect
         */
        effects[5] = new ComposePathEffect(effects[2],effects[4]);
        effects[6] = new SumPathEffect(effects[4],effects[3]);
        mPaint.setPathEffect(effects[4]);
        mPaint.setColor(Color.WHITE);
        canvas.drawArc(oval, 180, 180, false, mPaint);    //绘制圆弧

    }

    /**
     *当SurfaceView准备好使用时要调用此方法
     * @param surfaceHolder
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        /**
         * 开始写绘图代码
         */
        mDrawingThread.keepDrawing = true;
        mDrawingThread.start();
    }

    /**
     * 当实力尺寸发生变化时被调用，这里通常发生在设备旋转时。
     * @param surfaceHolder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mDrawingThread.keepDrawing = false;
        boolean retry = true;
        while (retry) {
            try {
                mDrawingThread.join();//等待该线程终止,该线程是指的主线程等待子线程的终止。也就是在子线程调用了join()
                                      // 方法后面的代码，只有等到子线程结束了才能执行。
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * 绘制View的线程
     */
    private class DrawingThread extends Thread {
        private Canvas mCanvas;
        private SurfaceHolder mSurfaceHolder;
        private boolean keepDrawing;

        public DrawingThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {
            mCanvas = null;
            mCanvas = mSurfaceHolder.lockCanvas();
            try {
                synchronized (mSurfaceHolder) {
                    //TODO 设置view变化参数
                    onDraw(mCanvas);
                }
            } finally {
                if (null != mCanvas) {
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }

}
