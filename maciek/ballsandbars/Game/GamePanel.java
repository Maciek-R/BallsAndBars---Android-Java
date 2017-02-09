package maciek.ballsandbars.Game;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//import com.example.maciek.ballsandbars.R;
import maciek.ballsandbars.UI.MyTextView;

import java.util.Vector;

/**
 * Created by Maciek on 2016-09-28.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    private MainThread mainThread;

    private Vector<Ball> balls;

    public enum Direction{Poziom, Pion, None};

    public Direction dir = Direction.Poziom;


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

    int percent=0;
    int Lifes = 5;
    boolean gameOver = false;

    public GamePanel(Context context, MyTextView Level, MyTextView percent, MainActivity mainActiv, DisplayMetrics dm) {
        super(context);
        this.context = context;
        this.mainActivity = mainActiv;
        this.dm = dm;

        getHolder().addCallback(this);
        mainThread = new MainThread(getHolder(), this);

        balls = new Vector<>();

        areas = new Vector<>();
        areas.add(new Area(new Rect(0, Constans.START_Y, Constans.SCREEN_WIDTH, Constans.SCREEN_HEIGHT)));
        areaPointer = null;


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
        this.Level.show(this.mainActivity, "Poziom 1");
        this.Percent.show(this.mainActivity, "0");
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
                                    Slider = new Rect(X, Y, X, Y + Constans.BAR_WIDTH);
                                }


                            } else if (dir == Direction.Pion) {

                                if (DirSlider == Direction.None) {
                                    DirSlider = Direction.Pion;
                                    Slider = new Rect(X, Y, X + Constans.BAR_WIDTH, Y);
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

        int WholeSurface = Constans.SCREEN_WIDTH * (Constans.SCREEN_HEIGHT-Constans.START_Y);

        int NotUsedSurface=0;

        for(Area area:areas){
            NotUsedSurface += area.getSurface();
        }

        int UsedSurface = WholeSurface - NotUsedSurface;

        final double percent = (((double)(UsedSurface)/WholeSurface)*100);
        this.percent = (int)percent;

       /* mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Percent.setText(String.valueOf(percent));
            }
        });*/
        Percent.show(mainActivity, String.valueOf(this.percent));



        if(percent > 80){
            System.out.println("Next round");


            areas.clear();
            areas.add(new Area(new Rect(0, Constans.START_Y, Constans.SCREEN_WIDTH, Constans.SCREEN_HEIGHT)));
            areaPointer = null;

            round.nextRound(balls, areas.get(0));
            this.percent=0;

        }
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
            System.out.println("koniec");

        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.WHITE);



        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);


        paint.setColor(Color.RED);
        canvas.drawRect(new Rect(0, Constans.START_Y-Constans.BAR_WIDTH, Constans.SCREEN_WIDTH, Constans.SCREEN_HEIGHT), paint);
        paint.setColor(Color.YELLOW);

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

    public void update(double time_elapsed_in_sec){

        if(gameOver) return;

        double przes = ( (time_elapsed_in_sec)) * Constans.SPEED;

        for(Ball ball:balls) {
            double przesX =  (Math.cos(Math.toRadians(ball.getAngle())) * przes);
            double przesY =  (Math.sin(Math.toRadians(ball.getAngle())) * przes);

            ball.setX(ball.getX()+przesX);
            ball.setY(ball.getY()+przesY);
        }

        checkCollisions();

        if(DirSlider!=Direction.None) {

            int l = Slider.left;
            int t = Slider.top;
            int b = Slider.bottom;
            int r = Slider.right;

            if (DirSlider == Direction.Pion) {
                Slider.set(l, t-(int)przes, r, b+(int)przes);

                if(Slider.top<areaPointer.border.top && Slider.bottom > areaPointer.border.bottom){

                    int X = Slider.left;

                    if(!isBallBetween(areaPointer.border.left, areaPointer.border.top, X, areaPointer.border.bottom)) {
                        areaPointer.border.left = X+Constans.BAR_WIDTH;
                        // System.out.println("1");
                    }

                    else if(!isBallBetween(X, areaPointer.border.top, areaPointer.border.right, areaPointer.border.bottom)) {
                        areaPointer.border.right = X;
                        //System.out.println("2");
                    }
                    else{
                        System.out.println("3");
                        System.out.println((X+Constans.BAR_WIDTH)+" "+ areaPointer.border.top+" "+ areaPointer.border.right+" "+ areaPointer.border.bottom);
                        System.out.println(areaPointer.border.left+" "+ areaPointer.border.top+" "+ X+" "+ areaPointer.border.bottom);

                        areas.add(new Area(new Rect(X+Constans.BAR_WIDTH, areaPointer.border.top, areaPointer.border.right, areaPointer.border.bottom)));
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

            } else if (DirSlider == Direction.Poziom) {
                Slider.set(l-(int)przes, t, r+(int)przes, b);

                if(Slider.left<areaPointer.border.left && Slider.right > areaPointer.border.right){

                    int Y = Slider.top;

                    if(!isBallBetween(areaPointer.border.left, areaPointer.border.top, areaPointer.border.right, Y)) {

                        areaPointer.border.top = Y+Constans.BAR_WIDTH;
                        System.out.println("4");
                    }

                    else if(!isBallBetween(areaPointer.border.left, Y, areaPointer.border.right, areaPointer.border.bottom)) {

                        areaPointer.border.bottom = Y;
                        System.out.println("5");

                    }
                    else{
                        System.out.println("6");
                        System.out.println(areaPointer.border.left + " " +  Y+Constans.BAR_WIDTH + " " + areaPointer.border.right + " " + areaPointer.border.bottom);
                        System.out.println(areaPointer.border.left+ " " + areaPointer.border.top+ " " + areaPointer.border.right+ " " + Y);

                        areas.add(new Area(new Rect(areaPointer.border.left, Y+Constans.BAR_WIDTH, areaPointer.border.right, areaPointer.border.bottom)));
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
        }
    }
    private boolean isBallBetween(int x1, int y1, int x2, int y2){
        int SizeX = Ball.SizeX;
        int SizeY = Ball.SizeY;

        for(Ball ball:balls){
            Rect rect = new Rect((int)ball.getX(), (int)ball.getY(), (int)ball.getX()+SizeX, (int)ball.getY()+SizeY);

            if(rect.intersect(new Rect(x1, y1, x2, y2)))
                return true;
        }
        return false;
    }
    private void setBallsToArea(Direction dir){

        if(dir == Direction.Pion){
            for(Ball ball:balls){
                if(ball.getAreaPoint() == areaPointer){
                    if(ball.getX() >= areaPointer.border.right+Constans.BAR_WIDTH){
                        ball.setAreaPoint(areas.get(areas.size()-1));
                    }
                }
            }
        }
        else if(dir == Direction.Poziom){

            for(Ball ball:balls){
                if(ball.getAreaPoint() == areaPointer){
                    if(ball.getY() >= areaPointer.border.bottom+Constans.BAR_WIDTH){
                        ball.setAreaPoint(areas.get(areas.size()-1));
                    }
                }
            }
        }
    }

    private void drawSlider(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.RED);
        if(Slider!=null)
            canvas.drawRect(Slider, paint);
    }
    private void checkCollisions(){

        for(Ball b:balls) {

            double x = b.getX();
            double y = b.getY();
            double angle = b.getAngle();
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

            if(Slider!=null){
                if(Slider.intersect((int)x, (int)y, (int)x+Ball.SizeX, (int)y+Ball.SizeY)){
                    areaPointer=null;
                    DirSlider=Direction.None;
                    Slider=null;
                    Lifes--;
                    if(Lifes==0) {
                        gameOver = true;


                        MyFileReader Fr = new MyFileReader(context);
                        Integer stage = Fr.readNextLine();
                        Integer percent = Fr.readNextLine();


                        // System.out.println(stage.intValue());
                        //System.out.println(percent.intValue());

                        if((stage==null && percent==null) || (round.getStage() > stage.intValue()) ||
                                ((round.getStage() >= stage.intValue()) && (this.percent > percent.intValue()))){

                            MyFileWriter Fw = new MyFileWriter(context);
                            Fw.write(String.valueOf(round.getStage()+"\n"));
                            Fw.write(String.valueOf(this.percent));
                            Fw.close();

                        }


                    }
                }
            }
        }


    }

    private void drawLifes(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

        int X = Constans.START_X_LIFES;

        for(int i=0; i<Lifes; ++i){
            canvas.drawRect(new Rect(X+i*Constans.LIFE_SPACE, Constans.START_Y_LIFES, X+Constans.LIFE_WIDTH+i*Constans.LIFE_SPACE, Constans.START_Y_LIFES+Constans.LIFE_WIDTH), paint);
        }

    }
    private void drawGameOver(Canvas canvas){
        Paint paint = new Paint();
        //paint.setTextSize(paint.getTextSize()*3);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);


        /*int w = (int) paint.measureText("GAME OVER");
        while(w<Constans.SCREEN_WIDTH/2){
            paint.setTextSize((float)(paint.getTextSize()+0.5));
            w = (int) paint.measureText("GAME OVER");
        }*/
        paint.setTextSize(Constans.GAME_OVER_SIZE);


       // System.out.println(paint.getTextSize());
        canvas.drawText("GAME OVER", Constans.SCREEN_WIDTH/2 - Constans.GAME_OVER_WIDTH/2, Constans.SCREEN_HEIGHT/2, paint);

       // paint.setTextSize(Constans.AUTHOR_SIZE);

        //canvas.drawText(Constans.AUTHOR, Constans.SCREEN_WIDTH - Constans.AUTHOR_WIDTH, Constans.SCREEN_HEIGHT, paint);

    }
}
