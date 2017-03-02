package pl.android.ballsandbars.objects;

import android.graphics.Rect;

import java.util.Random;

import pl.android.ballsandbars.Constants;

/**
 * Created by Maciek on 2016-09-08.
 */
public class Ball {

    private float x;
    private float y;
    private float angle;

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

    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public float getAngle(){
        return angle;
    }

    public void setX(float x){
        this.x = x;
    }
    public void setY(float y){
        this.y = y;
    }
    public void setAngle(float angle){
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

    public void move(float translation){
        float translationX = (float) (Math.cos(Math.toRadians(angle)) * translation);
        float translationY = (float) (Math.sin(Math.toRadians(angle)) * translation);

        x = x + translationX;
        y = y + translationY;
    }
}
