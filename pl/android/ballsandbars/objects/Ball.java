package pl.android.ballsandbars.objects;

import android.graphics.Rect;

import java.util.Random;

import pl.android.ballsandbars.Area;
import pl.android.ballsandbars.Constants;

/**
 * Created by Maciek on 2016-09-08.
 */
public class Ball {

    private double x;
    private double y;
    private double angle;

    public static int SizeX = (4 * Constants.SCREEN_WIDTH)/100;
    public static int SizeY = (4 * Constants.SCREEN_WIDTH)/100;

    private Area areaPoint;

    public Ball(Area area){
        Random rand = new Random();
        x = rand.nextInt(Constants.SCREEN_WIDTH-SizeX);
        y = rand.nextInt(Constants.SCREEN_HEIGHT- Constants.START_Y-SizeY) + Constants.START_Y;
        angle = rand.nextInt(360);
        areaPoint = area;
    }

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double getAngle(){
        return angle;
    }

    public void setX(double x){
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }
    public void setAngle(double angle){
        this.angle = angle;
    }

    public void setAreaPoint(Area areaPoint){
        this.areaPoint = areaPoint;
    }
    public Area getAreaPoint(){
        return areaPoint;
    }
    public Rect getRect(){
        return new Rect((int)x, (int)y, (int)x+SizeX, (int)y+SizeY);
    }
}
