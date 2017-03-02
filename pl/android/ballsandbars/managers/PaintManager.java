package pl.android.ballsandbars.managers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import pl.android.ballsandbars.objects.Area;
import pl.android.ballsandbars.Constants;
import pl.android.ballsandbars.objects.Ball;

/**
 * Created by Maciek on 2017-03-02.
 */

public class PaintManager {
    private ObjectManager objectManager;
    private Paint paint;

    public PaintManager(ObjectManager objectManager){
        this.objectManager = objectManager;
        paint = new Paint();
    }

    public void draw(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawRect(new Rect(0, Constants.START_Y - Constants.BAR_WIDTH, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT), paint);

        paint.setColor(Color.YELLOW);           //painting not filled areas
        for(Area area:objectManager.getAreas()) {
            canvas.drawRect(area.getBorder(), paint);
        }

        paint.setColor(Color.GREEN);
        for(Ball ball:objectManager.getBalls()) {
            int x = (int) ball.getX();
            int y = (int) ball.getY();
            canvas.drawRect(new Rect(x, y, x + Ball.SizeX, y + Ball.SizeY), paint);
        }

        drawSlider(canvas);
        drawLifes(canvas, objectManager.getLifes());

        if(objectManager.isGameOver()){
            drawGameOver(canvas);
        }
    }

    private void drawLifes(Canvas canvas, int lifes){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

        int X = Constants.START_X_LIFES;

        for(int i=0; i<lifes; ++i){
            canvas.drawRect(new Rect(X+i* Constants.LIFE_SPACE, Constants.START_Y_LIFES, X+ Constants.LIFE_WIDTH+i* Constants.LIFE_SPACE, Constants.START_Y_LIFES+ Constants.LIFE_WIDTH), paint);
        }

    }
    private void drawGameOver(Canvas canvas){

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setTextSize(Constants.GAME_OVER_SIZE);

        canvas.drawText("GAME OVER", Constants.SCREEN_WIDTH/2 - Constants.GAME_OVER_WIDTH/2, Constants.SCREEN_HEIGHT/2, paint);
    }
    private void drawSlider(Canvas canvas){

        if(objectManager.getSlider() !=null) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            canvas.drawRect(objectManager.getSlider(), paint);
        }
    }
}
