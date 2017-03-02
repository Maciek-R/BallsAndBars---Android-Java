package pl.android.ballsandbars.gamelogic;

import android.widget.TextView;

import pl.android.ballsandbars.MainActivity;
import pl.android.ballsandbars.managers.ObjectManager;
import pl.android.ballsandbars.userinterface.MyTextView;

/**
 * Created by Maciek on 2016-09-28.
 */
public class Round {

    private int stage;
    private MyTextView level;
    private TextView percentView;
    private int BallsCount;
    private MainActivity mainActivity;

    public Round(MyTextView level, TextView percentView, MainActivity mainActivity){

        this.stage = 1;
        this.level = level;
        this.percentView = percentView;
        this.BallsCount = 1;
        this.mainActivity = mainActivity;
    }
    public void nextRound(){
        stage++;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                level.setText("Poziom " + String.valueOf(stage));
                percentView.setText("0");
            }
        });
    }
    public int getStage(){
        return stage;
    }
}
