package pl.android.ballsandbars;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//import com.example.maciek.ballsandbars.R;
import pl.android.ballsandbars.gamelogic.MainThread;
import pl.android.ballsandbars.gamelogic.Round;
import pl.android.ballsandbars.managers.ObjectManager;
import pl.android.ballsandbars.managers.PaintManager;
import pl.android.ballsandbars.objects.Area;
import pl.android.ballsandbars.userinterface.MyTextView;

/**
 * Created by Maciek on 2016-09-28.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{
    public static final String TAG = "GAMEPANEL";
    private MainThread mainThread;

    public enum Direction{Poziom, Pion, None};
    public Direction dir = Direction.Poziom;

    ObjectManager objectManager;
    PaintManager paintManager;

    MyTextView levelView;
    MyTextView percentView;

    MainActivity mainActivity;
    Context context;
    DisplayMetrics dm;

    int mode;

    public GamePanel(Context context, MyTextView Level, MyTextView percent, MainActivity mainActiv, DisplayMetrics dm, int mode) {
        super(context);
        this.context = context;
        this.mainActivity = mainActiv;
        this.dm = dm;
        this.mode = mode;

        getHolder().addCallback(this);
        mainThread = new MainThread(getHolder(), this);

        this.levelView = Level;
        this.percentView = percent;


        objectManager = new ObjectManager(context, mainActivity, levelView, percentView);
        paintManager = new PaintManager(objectManager);

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mainThread = new MainThread(getHolder(), this);
        mainThread.setRunning(true);
        mainThread.start();
        this.levelView.show(this.mainActivity, String.valueOf("Poziom " + objectManager.getStage()));
        this.percentView.show(this.mainActivity, String.valueOf(objectManager.getPercent()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Area areaPointer = objectManager.getAreaPointer();

                if(areaPointer==null){
                    int X = (int) event.getX();
                    int Y = (int) event.getY();

                    for (Area area : objectManager.getAreas()) {
                        if (area.getBorder().contains(X, Y)) {

                            objectManager.setAreaPointer(area);
                            Direction dirSlider = objectManager.getDirSlider();

                            if (dir == Direction.Poziom) {

                                if (dirSlider == Direction.None) {
                                    objectManager.setDirSlider(Direction.Poziom);
                                    objectManager.setSlider(new Rect(X, Y, X, Y + Constants.BAR_WIDTH));
                                }


                            } else if (dir == Direction.Pion) {

                                if (dirSlider == Direction.None) {
                                    objectManager.setDirSlider(Direction.Pion);
                                    objectManager.setSlider(new Rect(X, Y, X + Constants.BAR_WIDTH, Y));
                                }

                            }
                        }
                    }
                }

        }

        return true;
        // return super.onTouchEvent(event);
    }




    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry){
            try{
                mainThread.setRunning(false);
                mainThread.join();
                retry = false;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            Log.v(TAG, "SurfaceDestroyed");
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        paintManager.draw(canvas);
    }

    public void update(float time_elapsed_in_sec){

        if(objectManager.isGameOver()) return;

        float translation = ( (time_elapsed_in_sec)) * Constants.SPEED;

        objectManager.update(translation);
    }

}
