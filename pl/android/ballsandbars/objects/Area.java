package pl.android.ballsandbars.objects;

import android.graphics.Rect;

import pl.android.ballsandbars.Constants;

/**
 * Created by Maciek on 2016-09-29.
 */
public class Area {

    Rect border;

    Rect LeftRect;
    Rect RightRect;
    Rect TopRect;
    Rect BottomRect;

    public Area(){

        border = null;

        LeftRect   = null;
        RightRect  = null;
        TopRect    = null;
        BottomRect = null;
    }

    public Area(Rect rect){

        border = new Rect(rect);

        LeftRect   = null;
        RightRect  = null;
        TopRect    = null;
        BottomRect = null;
    }
    public Area(Area area){

        border = area.border;

        LeftRect = area.LeftRect;
        RightRect = area.RightRect;
        TopRect = area.TopRect;
        BottomRect = area.BottomRect;
    }

    public int getSurface(){
        return ((border.right-border.left) * (border.bottom-border.top));
    }

    public void set(Rect rect){
        border = rect;
    }

    public Rect getBorder(){
        return border;
    }
    public void setLeftBorder(int value){
        border.left = value;
    }
    public void setRightBorder(int value){
        border.right = value;
    }
    public void setTopBorder(int value){
        border.top = value;
    }
    public void setBottomBorder(int value){
        border.bottom = value;
    }

}
