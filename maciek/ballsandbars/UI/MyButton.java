package maciek.ballsandbars.UI;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import maciek.ballsandbars.Game.GamePanel;

/**
 * Created by Maciek on 2016-10-03.
 */
public class MyButton extends Button implements View.OnClickListener{

    public GamePanel gamePanel;

    public MyButton(Context context, GamePanel gamePanel) {
        super(context);

        this.gamePanel = gamePanel;

        setTextSize(10);
        setText("POZIOM");

        setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        System.out.println("ONCLICK");
        if(gamePanel.dir == GamePanel.Direction.Poziom) {

            setText("PION");
            gamePanel.dir = GamePanel.Direction.Pion;
        }
        else {
            setText("POZIOM");
            gamePanel.dir = GamePanel.Direction.Poziom;
        }
    }
}
