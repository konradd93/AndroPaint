package com.konrad.andropaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;


public class MySurface extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    //control and monitor surface
    private SurfaceHolder surfaceHolder;

    //thread - refreshing canva
    private Thread drawingThread;

    //logic flag controled thread work
    private boolean workingThread = false;

    //object for creating critic section
    private Object locker = new Object();

    private Bitmap drawBitmap = null;
    private Canvas mainCanvas = null;

    private Paint paint;

    private Path path = new Path();

    private boolean clear = false;

    //Coordinates X and Y for draw circle
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;

    private static final String EXTRA_LINE_LIST = "line_list";
    private static final String EXTRA_STATE = "instance_state";
    private ArrayList<Line> linesList = new ArrayList<Line>(100);

    public MySurface(Context context){
        super(context);
        //surface container - control and monitor surfa
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    public MySurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        //surface container - control and monitor surfa
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        //Bitmap.Config.ARGB_8888 - Each pixel is stored on 4 bytes.
        drawBitmap = Bitmap.createBitmap(2000, 2000, Bitmap.Config.ARGB_8888);
        mainCanvas = new Canvas(drawBitmap);
        mainCanvas.drawARGB(255, 255, 255, 255);
        paint = new Paint();
        paint.setColor(Color.RED);
        setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //drawing whole canvas to white, when 'X' is clicked
        if (clear) {
            clear = false;
            mainCanvas.drawARGB(255, 255, 255, 255);

        }
        canvas.drawBitmap(drawBitmap, 0, 0, null);
    }

    // start drawing Thread
    public void resumeDrawing() {

        drawingThread = new Thread(this);
        workingThread = true;
        drawingThread.start();
    }

    //onTouch drawing
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        Line line = new Line();
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                path = new Path();
                startX = event.getX();
                startY = event.getY();
                line.setStartX(startX);
                line.setStartY(startY);
                drawCircle(startX, startY);
                path.moveTo(startX,startY);
                invalidate();
                //Log.d("DOWN ", "x: "+startX+" y: "+startY);
                break;
            case MotionEvent.ACTION_MOVE:
                stopX = event.getX();
                stopY = event.getY();
                path.lineTo(stopX,stopY);
                path.moveTo(stopX,stopY);
                invalidate();
                //Log.d("MOVE ", "x: "+stopX+" y: "+stopY);
                break;
            case MotionEvent.ACTION_UP:
                stopX = event.getX();
                stopY = event.getY();
                drawCircle(stopX, stopY);
                line.setStopX(stopX);
                line.setStopY(stopY);
                invalidate();
                //Log.d("UP ", "x: "+stopX+" y: "+stopY);
                break;
        }

        //critic section - draw modification
        synchronized (locker) {
            //modyfication draw.....
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            mainCanvas.drawPath(path,paint);
            line.setPath(path);
            line.setColor(paint.getColor());
            linesList.add(line);
        }
        return true;
    }

    public boolean performClick() {
        return super.performClick();
    }

    void drawCircle(float x, float y){
        paint.setStyle(Paint.Style.FILL);
        mainCanvas.drawCircle(x,y,5,paint);
    }

    @Override
    public void run() {
        while (workingThread) {
            Canvas canvas = null;
            try {
                //critic section - only this thread using container
                synchronized (surfaceHolder) {
                    //whether surface is(correct) valid
                    if (!surfaceHolder.getSurface().isValid()) continue;
                    // return canvas on which can draw, each pixel of canvas
                    // in square forwarded as a parametr must be drawed anew
                    // otherwise: start edit canvas content
                    canvas = surfaceHolder.lockCanvas(null);
                    //critic section - access to draw exclusive
                    synchronized (locker) {
                        if (workingThread) {
                            //drawing on local canvas.....
                            canvas.drawBitmap(drawBitmap,0,0,null);
                            postInvalidate();
                        }
                    }
                }
            } finally {
                // when in the above occurred exception, canvas stay in consistent state
                if (canvas != null) {
                    // end canvas edit and display draw on the screen
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
              Thread.sleep(1000 / 25); //25
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false);
        resumeDrawing();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        drawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mainCanvas = new Canvas(drawBitmap);
        mainCanvas.drawARGB(255, 255, 255, 255);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        if(linesList.size()!=0){
            for(Line line: linesList){
                paint.setFlags(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(line.getColor());
                drawCircle(line.getStartX(),line.getStartY());
                paint.setStrokeWidth(3);
                paint.setStyle(Paint.Style.STROKE);
                mainCanvas.drawPath(line.getPath(),paint);
                drawCircle(line.getStopX(),line.getStopY());
            }
        }

        setFocusable(true);
    }

    //stop drawing
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawing();
    }

    //stop drawing
    public void stopDrawing() {
        workingThread = false;
        while (true) {
            try {
                drawingThread.join();
            } catch (InterruptedException e) {

            }
            break;
        }
        drawingThread = null;
    }

    //change paint color
    public void setPaintColor(String color) {
        paint = new Paint();
        switch (color) {
            case "Red":
            {
                paint.setColor(Color.RED);
                break;
            }
            case "Green":
            {
                paint.setColor(Color.GREEN);
                break;
            }
            case "Blue":
            {
                paint.setColor(Color.BLUE);
                break;
            }
            case "Yellow":
            {
                paint.setColor(Color.BLACK);
                break;
            }
        }
    }
    //clear canvas
    public void clearCanvas() {
        drawBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mainCanvas = new Canvas(drawBitmap);
        mainCanvas.drawARGB(255,255,255,255);
        linesList = new ArrayList<Line>(100);

    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        System.out.println("save instance");
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_STATE, super.onSaveInstanceState());
        bundle.putParcelableArrayList(EXTRA_LINE_LIST, linesList);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle)
        {
            surfaceCreated(surfaceHolder);
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(EXTRA_STATE));
            linesList = bundle.getParcelableArrayList(EXTRA_LINE_LIST);
            if (linesList == null) {
                linesList = new ArrayList<Line>(100);
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public Bitmap getBitmap(){
        return drawBitmap;
    }
}
