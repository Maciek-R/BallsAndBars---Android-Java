package pl.android.ballsandbars;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
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
import pl.android.ballsandbars.objects.Ball;
import pl.android.ballsandbars.userinterface.MyTextView;
import pl.android.ballsandbars.util.MyFileReader;
import pl.android.ballsandbars.util.MyFileWriter;

import java.util.Vector;

/**
 * Created by Maciek on 2016-09-28.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{
    public static final String TAG = "GAMEPANEL";

    private MainThread mainThread;

    private Vector<Ball> balls;

    public enum Direction{Poziom, Pion, None};

    public Direction dir = Direction.Poziom;
    Paint paint;

    Vector<Area> areas;
    Area areaPointer;

    MyTextView Level;
    MyTextView Percent;
    Round round;

    Rect Slider;
    Direction DirSlider;

    MainActivity mainActivity;
    Context context;
    DisplayMetrics dm;

    int mode;

    int percent=0;
    int Lifes = 5;
    boolean gameOver = false;

    public GamePanel(Context context, MyTextView Level, MyTextView percent, MainActivity mainActiv, DisplayMetrics dm, int mode) {
        super(context);
        this.context = context;
        this.mainActivity = mainActiv;
        this.dm = dm;
        this.mode = mode;

        getHolder().addCallback(this);
        mainThread = new MainThread(getHolder(), this);

        balls = new Vector<>();

        areas = new Vector<>();
        areas.add(new Area(new Rect(0, Constants.START_Y, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)));
        areaPointer = null;
        paint = new Paint();

        this.Level = Level;
        this.Percent = percent;
        round = new Round(Level, percent, balls, mainActiv, areas.get(0));
        DirSlider = Direction.None;


        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mainThread = new MainThread(getHolder(), this);
        mainThread.setRunning(true);
        mainThread.start();
        this.Level.show(this.mainActivity, String.valueOf("Poziom " + round.getStage()));
        this.Percent.show(this.mainActivity, String.valueOf(percent));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // case MotionEvent.ACTION_MOVE:

                if(areaPointer==null){
                    int X = (int) event.getX();
                    int Y = (int) event.getY();

                    for (Area area : areas) {
                        if (area.border.contains(X, Y)) {

                            areaPointer = area;

                            if (dir == Direction.Poziom) {

                                if (DirSlider == Direction.None) {
                                    DirSlider = Direction.Poziom;
                                    Slider = new Rect(X, Y, X, Y + Constants.BAR_WIDTH);
                                }


                            } else if (dir == Direction.Pion) {

                                if (DirSlider == Direction.None) {
                                    DirSlider = Direction.Pion;
                                    Slider = new Rect(X, Y, X + Constants.BAR_WIDTH, Y);
                                }

                            }
                        }
                    }
                }

        }

        return true;
        // return super.onTouchEvent(event);
    }

    private void checkProgress(){

        Slider = null;
        DirSlider = Direction.None;
        areaPointer = null;

        final double percent = getFilledSurfaceinPercent();
        this.percent = (int)percent;

        Percent.show(mainActivity, String.valueOf(this.percent));

        if(percent > 80){
            Log.v(TAG, "Next Round");

            areas.clear();
            areas.add(new Area(new Rect(0, Constants.START_Y, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)));
            areaPointer = null;

            round.nextRound(balls, areas.get(0));
            this.percent=0;
        }
    }
    private float getFilledSurfaceinPercent(){

        int WholeSurface = Constants.SCREEN_WIDTH * (Constants.SCREEN_HEIGHT- Constants.START_Y);
        int NotUsedSurface=0;

        for(Area area:areas){
            NotUsedSurface += area.getSurface();
        }
        int UsedSurface = WholeSurface - NotUsedSurface;

        return (((float)(UsedSurface)/WholeSurface)*100);
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

        canvas.drawColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawRect(new Rect(0, Constants.START_Y - Constants.BAR_WIDTH, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT), paint);

        paint.setColor(Color.YELLOW);           //painting not filled areas
        for(Area area:areas) {
            canvas.drawRect(area.border, paint);
        }

        paint.setColor(Color.GREEN);
        for(Ball ball:balls) {
            int x = (int) ball.getX();
            int y = (int) ball.getY();
            canvas.drawRect(new Rect(x, y, x + Ball.SizeX, y + Ball.SizeY), paint);
        }

        drawSlider(canvas);
        drawLifes(canvas);

        if(gameOver){
            drawGameOver(canvas);
        }
    }

    public void update(float time_elapsed_in_sec){

        if(gameOver) return;

        float translation = ( (time_elapsed_in_sec)) * Constants.SPEED;

        for(Ball ball:balls) {
            ball.move(translation);
        }

        checkCollisions();
        updateSlider(translation);

    }

    private void updateSlider(float translation){
        if(DirSlider!=Direction.None) {

            if (DirSlider == Direction.Pion) {
                updateSliderVertically(translation);

            } else if (DirSlider == Direction.Poziom) {
                updateSliderHorizontally(translation);
            }
        }
    }

    private void updateSliderVertically(float translation){
        int l = Slider.left;
        int t = Slider.top;
        int b = Slider.bottom;
        int r = Slider.right;
        Slider.set(l, t-(int)translation, r, b+(int)translation);

        if(Slider.top<areaPointer.border.top && Slider.bottom > areaPointer.border.bottom){

            int X = Slider.left;

            if(!isAnyBallInArea(areaPointer.border.left, areaPointer.border.top, X, areaPointer.border.bottom)) {
                areaPointer.border.left = X+ Constants.BAR_WIDTH;
            }

            else if(!isAnyBallInArea(X, areaPointer.border.top, areaPointer.border.right, areaPointer.border.bottom)) {
                areaPointer.border.right = X;

            }
            else{
                areas.add(new Area(new Rect(X+ Constants.BAR_WIDTH, areaPointer.border.top, areaPointer.border.right, areaPointer.border.bottom)));
                areaPointer.set((new Rect(areaPointer.border.left, areaPointer.border.top, X, areaPointer.border.bottom)));

                setBallsToArea(Direction.Pion);
            }

            checkProgress();

        }
        else if(Slider.top<areaPointer.border.top){
            Slider.set(Slider.left, areaPointer.border.top, Slider.right, Slider.bottom);
        }
        else if(Slider.bottom>areaPointer.border.bottom){
            Slider.set(Slider.left, Slider.top, Slider.right, areaPointer.border.bottom);
        }
    }

    private void updateSliderHorizontally(float translation){
        int l = Slider.left;
        int t = Slider.top;
        int b = Slider.bottom;
        int r = Slider.right;
        Slider.set(l-(int)translation, t, r+(int)translation, b);

        if(Slider.left<areaPointer.border.left && Slider.right > areaPointer.border.right){

            int Y = Slider.top;

            if(!isAnyBallInArea(areaPointer.border.left, areaPointer.border.top, areaPointer.border.right, Y)) {

                areaPointer.border.top = Y+ Constants.BAR_WIDTH;
            }

            else if(!isAnyBallInArea(areaPointer.border.left, Y, areaPointer.border.right, areaPointer.border.bottom)) {

                areaPointer.border.bottom = Y;
            }
            else{
                areas.add(new Area(new Rect(areaPointer.border.left, Y+ Constants.BAR_WIDTH, areaPointer.border.right, areaPointer.border.bottom)));
                areaPointer.set((new Rect(areaPointer.border.left, areaPointer.border.top, areaPointer.border.right, Y)));

                setBallsToArea(Direction.Poziom);
            }
            checkProgress();

        }
        else if(Slider.left<areaPointer.border.left){
            Slider.set(areaPointer.border.left, Slider.top, Slider.right, Slider.bottom);
        }
        else if(Slider.right>areaPointer.border.right){
            Slider.set(Slider.left, Slider.top, areaPointer.border.right, Slider.bottom);
        }
    }

    private boolean isAnyBallInArea(int left, int top, int right, int bottom){
        for(Ball ball:balls){
            Rect rect = new Rect((int)ball.getX(), (int)ball.getY(), (int)ball.getX()+Ball.SizeX, (int)ball.getY()+Ball.SizeY);

            if(rect.intersect(new Rect(left, top, right, bottom)))
                return true;
        }
        return false;
    }
    private void setBallsToArea(Direction dir){

        if(dir == Direction.Pion){
            for(Ball ball:balls){
                if(ball.getAreaPoint() == areaPointer){
                    if(ball.getX() >= areaPointer.border.right+ Constants.BAR_WIDTH){
                        ball.setAreaPoint(areas.get(areas.size()-1));
                    }
                }
            }
        }
        else if(dir == Direction.Poziom){

            for(Ball ball:balls){
                if(ball.getAreaPoint() == areaPointer){
                    if(ball.getY() >= areaPointer.border.bottom+ Constants.BAR_WIDTH){
                        ball.setAreaPoint(areas.get(areas.size()-1));
                    }
                }
            }
        }
    }

    private void drawSlider(Canvas canvas){

        if(Slider!=null) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            canvas.drawRect(Slider, paint);
        }
    }
    private void checkCollisions(){

        for(Ball b:balls) {

            float x = b.getX();
            float y = b.getY();

            clamp(b);

            if(Slider!=null){
                if(Slider.intersect((int)x, (int)y, (int)x+Ball.SizeX, (int)y+Ball.SizeY)){
                    areaPointer=null;
                    DirSlider=Direction.None;
                    Slider=null;
                    Lifes--;
                    if(Lifes==0) {
                        gameOver = true;
                        saveResult();
                    }
                }
            }

        }
    }
    private void clamp(Ball b){
        float x = b.getX();
        float y = b.getY();
        float angle = b.getAngle();
        Area ar = b.getAreaPoint();

        if (x < ar.border.left) {
            angle = (180 - angle) % 360;
            b.setAngle(angle);
            b.setX(ar.border.left);
        } else if (x + Ball.SizeX >= ar.border.right) {
            angle = (180 - angle) % 360;
            b.setAngle(angle);
            b.setX(ar.border.right - Ball.SizeX);
        }

        if (y < ar.border.top) {
            angle = (-angle) % 360;
            b.setAngle(angle);
            b.setY(ar.border.top);
        } else if (y + Ball.SizeY >= ar.border.bottom) {
            angle = (-angle) % 360;
            b.setAngle(angle);
            b.setY(ar.border.bottom - Ball.SizeY);
        }
    }

    private void saveResult(){
        MyFileReader Fr = new MyFileReader(context);
        Integer stage = Fr.readNextLine();
        Integer percent = Fr.readNextLine();

        boolean isPreviousResult = !(stage==null && percent==null);
        boolean isBetterNewScore = (round.getStage() > stage.intValue()) ||
                                    ((round.getStage() >= stage.intValue()) && (this.percent > percent.intValue()));

        if((!isPreviousResult) || (isBetterNewScore) ){

            MyFileWriter Fw = new MyFileWriter(context);
            Fw.write(String.valueOf(round.getStage()+"\n"));
            Fw.write(String.valueOf(this.percent));
            Fw.close();

        }
    }

    private void drawLifes(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

        int X = Constants.START_X_LIFES;

        for(int i=0; i<Lifes; ++i){
            canvas.drawRect(new Rect(X+i* Constants.LIFE_SPACE, Constants.START_Y_LIFES, X+ Constants.LIFE_WIDTH+i* Constants.LIFE_SPACE, Constants.START_Y_LIFES+ Constants.LIFE_WIDTH), paint);
        }

    }
    private void drawGameOver(Canvas canvas){

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setTextSize(Constants.GAME_OVER_SIZE);

        canvas.drawText("GAME OVER", Constants.SCREEN_WIDTH/2 - Constants.GAME_OVER_WIDTH/2, Constants.SCREEN_HEIGHT/2, paint);
    }
}
