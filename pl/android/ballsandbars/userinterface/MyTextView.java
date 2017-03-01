package pl.android.ballsandbars.userinterface;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import pl.android.ballsandbars.Constants;
import pl.android.ballsandbars.MainActivity;

/**
 * Created by Maciek on 2016-10-03.
 */
public class MyTextView extends TextView {

    private int MyWidth;
    private int MyHeight;


    public MyTextView(Context context) {
        super(context);



    }

    public int getMyHeight() {
        return MyHeight;
    }

    public int getMyWidth() {
        return MyWidth;
    }

    public void setAsPercent(){
        setText("00");
        setTextColor(Color.RED);
        measure(0, 0);
//        setText("0");
        MyWidth = getMeasuredWidth();
        MyHeight = getMeasuredHeight();

            setX(Constants.SCREEN_WIDTH-getMyWidth());
            setY(MyHeight);
        //setBackgroundColor(Color.GREEN);
    }

    public void setAsLevel(){
        setText("Poziom 10");
        setTextColor(Color.RED);
        measure(0, 0);
        MyWidth = getMeasuredWidth();
        MyHeight = getMeasuredHeight();


            setX(Constants.SCREEN_WIDTH - MyWidth);


            setY(0);

        //setBackgroundColor(Color.GREEN);
       // setText("Poziom 1");
    }

    public void show(MainActivity mainActivity, final String text){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setText(text);
            }
        });
    }
}
