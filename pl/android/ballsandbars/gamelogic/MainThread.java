package pl.android.ballsandbars.gamelogic;

import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import pl.android.ballsandbars.GamePanel;

/**
 * Created by Maciek on 2016-09-28.
 */
public class MainThread extends Thread{
    public static final String TAG = "MAIN_THREAD";
    public static final int MAX_FPS = 60;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

    private long frameStartTimeMs;
    private int frameCount;
    private long startTimeMs;
    private long expectedFrameTimeMs = 1000/MAX_FPS;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel){
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    public void setRunning(boolean running){
        this.running=running;
    }

    @Override
    public void run() {

        long startTime;

        long lastTimeNano = SystemClock.elapsedRealtime();
        float time_elapsed_in_sec;

        while(running){
            limitFrameRate();
           // logFrameRate();

            startTime = SystemClock.elapsedRealtime();
            time_elapsed_in_sec = (float)(startTime - lastTimeNano)/1000;
            lastTimeNano = startTime;

            canvas = null;
            try{
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    gamePanel.update(time_elapsed_in_sec);
                    if(canvas!=null)
                        gamePanel.draw(canvas);
                }
            }
            catch(Exception e){e.printStackTrace();}
            finally {
                if(canvas!=null){
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch(Exception e){e.printStackTrace();}
                }

            }

        }

    }

    private void limitFrameRate(){
        long elapsedFrameTimeMs = SystemClock.elapsedRealtime() - frameStartTimeMs;
        long timeToSleepMs = expectedFrameTimeMs - elapsedFrameTimeMs;
        if(timeToSleepMs > 0){
            SystemClock.sleep(timeToSleepMs);
        }
        frameStartTimeMs = SystemClock.elapsedRealtime();
    }
    private void logFrameRate() {

            long elapsedRealTimeMs = SystemClock.elapsedRealtime();
            double elapsedSeconds = (elapsedRealTimeMs - startTimeMs) / 1000.0;

            if(elapsedSeconds > 1.0){
                Log.v(TAG, frameCount / elapsedSeconds + "fps");
                startTimeMs = SystemClock.elapsedRealtime();
                frameCount = 0;
            }
            frameCount++;
    }
}
