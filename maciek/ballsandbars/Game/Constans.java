package maciek.ballsandbars.Game;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Pair;

import java.util.Vector;

/**
 * Created by Maciek on 2016-09-28.
 */
public class Constans {



    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static int BUTTON_WIDTH;
    public static int BUTTON_HEIGHT;

    public static int START_Y;
    public static int START_Y_LIFES;
    public static int START_X_LIFES;

    public static int BAR_WIDTH;

    public static int SPEED;

    public static int LIFE_SPACE;
    public static int LIFE_WIDTH;

    public static float GAME_OVER_SIZE;
    public static int GAME_OVER_WIDTH;

   // public static String AUTHOR = "MACIEJ RUSZCZYK";
   // public static float AUTHOR_SIZE;
   // public static int AUTHOR_WIDTH;

    public static void init(DisplayMetrics dm){
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;

        BUTTON_WIDTH = (20 * SCREEN_WIDTH)/100;
        BUTTON_HEIGHT = (10 * SCREEN_HEIGHT)/100;

        START_Y = (12 * SCREEN_HEIGHT)/100;
        START_Y_LIFES = (1 * SCREEN_HEIGHT)/100;
        START_X_LIFES = (21 * SCREEN_WIDTH)/100;

        BAR_WIDTH = (1 * SCREEN_HEIGHT)/100;

        SPEED = (70 * SCREEN_WIDTH)/100;

        LIFE_SPACE = (5*SCREEN_WIDTH)/100;
        LIFE_WIDTH = (4*SCREEN_WIDTH)/100;

       // Pair p = new Pair(Float, Integer);
        Pair<Float, Integer> p;
        p = measureWidthAndSize(SCREEN_WIDTH/2, "GAME OVER");
        GAME_OVER_SIZE = p.first;
        GAME_OVER_WIDTH = p.second;

        //p = measureWidthAndSize(SCREEN_WIDTH/4, AUTHOR);
       // AUTHOR_SIZE = p.first;
       // AUTHOR_WIDTH = p.second;


    }

    private static Pair measureWidthAndSize(int Number, String text){
        Paint p = new Paint();

        int w = (int) p.measureText(text);
        if(w <  Number) {
            while (w < Number) {
                p.setTextSize((float)(p.getTextSize()+0.5));
                w = (int) p.measureText(text);
            }
        }
        else{
            while (w > Number) {
                p.setTextSize((float)(p.getTextSize()-0.5));
                w = (int) p.measureText(text);
            }
        }

        return new Pair(new Float(p.getTextSize()), new Integer(w));
       // GAME_OVER_SIZE = p.getTextSize();
        //GAME_OVER_WIDTH = w;

    }
}
