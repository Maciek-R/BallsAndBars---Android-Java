package pl.android.ballsandbars;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import pl.android.ballsandbars.userinterface.MyButton;
import pl.android.ballsandbars.userinterface.MyTextView;

public class MainActivity extends Activity {

    private MyTextView Level;
    private MyTextView Percent;
    private MyButton MyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Constants.init(dm);

        int mode = 0;
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            mode = bundle.getInt("mode");
        }


        Level = new MyTextView(this);
        Level.setAsLevel();

        Percent = new MyTextView(this);
        Percent.setAsPercent();

        GamePanel gamePanel = new GamePanel(this, Level, Percent, MainActivity.this, dm, mode);

        setContentView(gamePanel);

        MyButton = new MyButton(this, gamePanel);

        RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        RelativeLayout.LayoutParams lay2 = new RelativeLayout.LayoutParams(Level.getMyWidth(), Level.getMyHeight());
        RelativeLayout.LayoutParams lay3 = new RelativeLayout.LayoutParams(Percent.getMyWidth(), Percent.getMyHeight());

        addContentView(MyButton, lay);
        addContentView(Level, lay2);
        addContentView(Percent, lay3);
    }


    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

   /* public void newActivity(View view) {
        //setContentView(R.layout.layout);
        Intent intent = new Intent(this, SubActivity.class);
        startActivity(intent);
    }*/
}
