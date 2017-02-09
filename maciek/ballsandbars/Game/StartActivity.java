package maciek.ballsandbars.Game;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import maciek.ballsandbars.R;

public class StartActivity extends Activity {

    private int NUMBER;

    private Button NewGameButton;
    private Button ExitButton;
    private Button BestScoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.start_activity);


        NewGameButton = (Button) findViewById(R.id.NewGame);
        ExitButton = (Button) findViewById(R.id.Exit);
        BestScoreButton = (Button) findViewById(R.id.BestScore);

    }

    public void checkButton(View view){

        switch(view.getId()){
            case R.id.NewGame:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.BestScore:

                MyFileReader Fr = new MyFileReader(this, StartActivity.this);
                Fr.writeOnScreen();


                break;
            case R.id.Exit:

                finish();break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("EXIT");
    }

}
