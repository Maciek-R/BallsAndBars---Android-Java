package pl.android.ballsandbars.managers;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import java.util.Vector;

import pl.android.ballsandbars.MainActivity;
import pl.android.ballsandbars.gamelogic.Round;
import pl.android.ballsandbars.objects.Area;
import pl.android.ballsandbars.Constants;
import pl.android.ballsandbars.GamePanel;
import pl.android.ballsandbars.objects.Ball;
import pl.android.ballsandbars.userinterface.MyTextView;
import pl.android.ballsandbars.util.MyFileReader;
import pl.android.ballsandbars.util.MyFileWriter;

/**
 * Created by Maciek on 2017-03-02.
 */

public class ObjectManager {
    public static final String TAG = "OBJECTMANAGER";
    private Vector<Ball> balls;
    private Vector<Area> areas;
    private Area areaPointer;
    private Rect slider;
    private GamePanel.Direction dirSlider;

    Context context;
    MainActivity mainActivity;
    MyTextView levelView;
    MyTextView percentView;
    Round round;
    int percent=0;
    boolean gameOver = false;
    int lifes = 5;
    public ObjectManager(Context context, MainActivity mainActivity, MyTextView levelView, MyTextView percentView){
        this.context = context;
        this.mainActivity = mainActivity;
        this.levelView = levelView;
        this.percentView = percentView;

        balls = new Vector<>();
        areas = new Vector<>();
        areas.add(new Area(new Rect(0, Constants.START_Y, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)));
        areaPointer = null;
        dirSlider = GamePanel.Direction.None;

        round = new Round(levelView, percentView, mainActivity);
        balls.add(new Ball(areas.get(0)));
    }

    public void update(float translation){
        for(Ball ball:balls) {
            ball.move(translation);
        }

        checkCollisions();
        updateSlider(translation);
    }
    private void checkCollisions(){

        for(Ball b:balls) {

            float x = b.getX();
            float y = b.getY();

            clamp(b);

            if(slider!=null){
                if(slider.intersect((int)x, (int)y, (int)x+Ball.SizeX, (int)y+Ball.SizeY)){

                    areaPointer=null;
                    dirSlider = GamePanel.Direction.None;
                    slider =null;

                    lifes--;
                    if(lifes==0) {
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

        if (x < ar.getBorder().left) {
            angle = (180 - angle) % 360;
            b.setAngle(angle);
            b.setX(ar.getBorder().left);
        } else if (x + Ball.SizeX >= ar.getBorder().right) {
            angle = (180 - angle) % 360;
            b.setAngle(angle);
            b.setX(ar.getBorder().right - Ball.SizeX);
        }

        if (y < ar.getBorder().top) {
            angle = (-angle) % 360;
            b.setAngle(angle);
            b.setY(ar.getBorder().top);
        } else if (y + Ball.SizeY >= ar.getBorder().bottom) {
            angle = (-angle) % 360;
            b.setAngle(angle);
            b.setY(ar.getBorder().bottom - Ball.SizeY);
        }
    }

    private void updateSlider(float translation){
        if(dirSlider != GamePanel.Direction.None) {

            if (dirSlider == GamePanel.Direction.Pion) {
                updateSliderVertically(translation);

            } else if (dirSlider == GamePanel.Direction.Poziom) {
                updateSliderHorizontally(translation);
            }
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
    private void setBallsToArea(GamePanel.Direction dir){
        if(dir == GamePanel.Direction.Pion){
            for(Ball ball:balls){
                if(ball.getAreaPoint() == areaPointer){
                    if(ball.getX() >= areaPointer.getBorder().right+ Constants.BAR_WIDTH){
                        ball.setAreaPoint(getLastArea());
                    }
                }
            }
        }
        else if(dir == GamePanel.Direction.Poziom){

            for(Ball ball:balls){
                if(ball.getAreaPoint() == areaPointer){
                    if(ball.getY() >= areaPointer.getBorder().bottom+ Constants.BAR_WIDTH){
                        ball.setAreaPoint(getLastArea());
                    }
                }
            }
        }
    }

    private void updateSliderVertically(float translation){
        int l = slider.left;
        int t = slider.top;
        int b = slider.bottom;
        int r = slider.right;
        slider.set(l, t-(int)translation, r, b+(int)translation);

        Rect border = areaPointer.getBorder();

        if(slider.top < border.top && slider.bottom > border.bottom){

            int X = slider.left;

            if(!isAnyBallInArea(border.left, border.top, X, border.bottom)) {
                areaPointer.setLeftBorder(X+ Constants.BAR_WIDTH);
            }

            else if(!isAnyBallInArea(X, border.top, border.right, border.bottom)) {
              //  areaPointer.border.right = X;
                areaPointer.setRightBorder(X);
            }
            else{
              //  objectManager.addArea(new Area(new Rect(X+ Constants.BAR_WIDTH, border.top, border.right, border.bottom)));
                areas.add(new Area(new Rect(X+ Constants.BAR_WIDTH, border.top, border.right, border.bottom)));
                areaPointer.set((new Rect(border.left, border.top, X, border.bottom)));

                setBallsToArea(GamePanel.Direction.Pion);
            }

            checkProgress();

        }
        else if(slider.top<border.top){
            slider.set(slider.left, border.top, slider.right, slider.bottom);
        }
        else if(slider.bottom>border.bottom){
            slider.set(slider.left, slider.top, slider.right, border.bottom);
        }
    }

    private void updateSliderHorizontally(float translation){
        int l = slider.left;
        int t = slider.top;
        int b = slider.bottom;
        int r = slider.right;
        slider.set(l-(int)translation, t, r+(int)translation, b);
        Rect border = areaPointer.getBorder();

        if(slider.left<border.left && slider.right > border.right){

            int Y = slider.top;

            if(!isAnyBallInArea(border.left, border.top, border.right, Y)) {

               // areaPointer.border.top = Y+ Constants.BAR_WIDTH;
                areaPointer.setTopBorder(Y+ Constants.BAR_WIDTH);
            }

            else if(!isAnyBallInArea(border.left, Y, border.right, border.bottom)) {

             //   areaPointer.border.bottom = Y;
                areaPointer.setBottomBorder(Y);
            }
            else{
              //  objectManager.addArea(new Area(new Rect(border.left, Y+ Constants.BAR_WIDTH, border.right, border.bottom)));
                areas.add(new Area(new Rect(border.left, Y+ Constants.BAR_WIDTH, border.right, border.bottom)));
                areaPointer.set((new Rect(border.left, border.top, border.right, Y)));

                setBallsToArea(GamePanel.Direction.Poziom);
            }
            checkProgress();

        }
        else if(slider.left<border.left){
            slider.set(border.left, slider.top, slider.right, slider.bottom);
        }
        else if(slider.right>border.right){
            slider.set(slider.left, slider.top, border.right, slider.bottom);
        }
    }
    private void checkProgress(){

        areaPointer=null;
        dirSlider = GamePanel.Direction.None;
        slider =null;

        final double percent = getFilledSurfaceinPercent();
        this.percent = (int)percent;

        percentView.show(mainActivity, String.valueOf(this.percent));

        if(percent > 80){
            Log.v(TAG, "Next Round");

            areas.clear();
            areas.add(new Area(new Rect(0, Constants.START_Y, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)));
            areaPointer = null;

            nextRound();
            this.percent=0;
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

    private float getFilledSurfaceinPercent(){

        int WholeSurface = Constants.SCREEN_WIDTH * (Constants.SCREEN_HEIGHT- Constants.START_Y);
        int NotUsedSurface=0;

        for(Area area:areas){
            NotUsedSurface += area.getSurface();
        }
        int UsedSurface = WholeSurface - NotUsedSurface;

        return (((float)(UsedSurface)/WholeSurface)*100);
    }

    private Area getLastArea(){
        return areas.get(areas.size()-1);
    }

    public void nextRound(){
        round.nextRound();

        balls.clear();
        for(int i=0; i<round.getStage(); ++i){
            balls.add(new Ball(areas.get(0)));
        }
    }

    public Rect getSlider() {
        return slider;
    }

    public void setSlider(Rect slider) {
        this.slider = slider;
    }

    public Vector<Ball> getBalls() {
        return balls;
    }

    public Vector<Area> getAreas() {
        return areas;
    }

    public Area getAreaPointer() {
        return areaPointer;
    }

    public void setAreaPointer(Area areaPointer) {
        this.areaPointer = areaPointer;
    }

    public GamePanel.Direction getDirSlider() {
        return dirSlider;
    }

    public void setDirSlider(GamePanel.Direction dirSlider) {
        this.dirSlider = dirSlider;
    }
    public int getPercent(){
        return percent;
    }
    public boolean isGameOver(){
        return gameOver;
    }
    public int getLifes(){
        return lifes;
    }
    public int getStage(){
        return round.getStage();
    }
}
