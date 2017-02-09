package maciek.ballsandbars.Game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Maciek on 2016-09-28.
 */
public class MainThread extends Thread{

    public static final int MAX_FPS = 60;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

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
        long timeMillis;
        long waitTime;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 1000/MAX_FPS;

        long lastTimeNano = System.nanoTime();
        double time_elapsed_in_sec;

        while(running){
            startTime = System.nanoTime();

            time_elapsed_in_sec = (double)(startTime - lastTimeNano)/1000000000;
            //   System.out.println("Minelo " + time_elapsed_in_sec);
            // System.out.println("Przes " + time_elapsed_in_sec*500);


            lastTimeNano = startTime;
            canvas = null;

            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    this.gamePanel.update(time_elapsed_in_sec);
                    if(canvas!=null)
                        this.gamePanel.draw(canvas);
                    // System.out.println(this.gamePanel.getBallX());
                }

            }catch(Exception e){e.printStackTrace();}
            finally {
                if(canvas!=null){
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch(Exception e){e.printStackTrace();}
                }

            }

            timeMillis = (System.nanoTime()-startTime)/1000000;
            waitTime = targetTime - timeMillis;

            try{
                if(waitTime>0)
                    this.sleep(waitTime);
            }catch (Exception e){e.printStackTrace();}

            totalTime += System.nanoTime()-startTime;
            frameCount++;
            if(frameCount==MAX_FPS){
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount=0;
                totalTime=0;
                System.out.println(averageFPS);
            }

        }

    }
}
