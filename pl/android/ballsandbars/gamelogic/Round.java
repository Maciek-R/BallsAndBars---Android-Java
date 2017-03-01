package pl.android.ballsandbars.gamelogic;

import android.widget.TextView;

import pl.android.ballsandbars.MainActivity;
import pl.android.ballsandbars.Area;
import pl.android.ballsandbars.objects.Ball;
import pl.android.ballsandbars.userinterface.MyTextView;

import java.util.Vector;

/**
 * Created by Maciek on 2016-09-28.
 */
public class Round {

    private int stage;
    private MyTextView level;
    private TextView percent;
    private int BallsCount;
    private MainActivity mainActivity;

    public Round(MyTextView level, TextView percent, Vector<Ball> balls, MainActivity mainActivity, Area area){

        this.stage = 1;
        this.level = level;
        this.percent = percent;
        this.BallsCount = 1;
        this.mainActivity = mainActivity;

        balls.add(new Ball(area));
    }
    public void nextRound(Vector<Ball> balls, Area area){
        stage++;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                level.setText("Poziom " + String.valueOf(stage));
                percent.setText("0");
            }
        });



        balls.clear();
        for(int i=0; i<stage; ++i){
            balls.add(new Ball(area));
        }
    }
    public int getStage(){
        return stage;
    }
}
