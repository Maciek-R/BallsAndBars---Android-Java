package maciek.ballsandbars.Game;

import android.content.Context;
import android.widget.Toast;

import maciek.ballsandbars.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Maciek on 2016-10-04.
 */
public class MyFileReader {

    private InputStream inputStream;
    private Context context;

    private StartActivity startActivity;
    private BufferedReader bufferedReader;

    public MyFileReader(final Context context){
        this.context = context;

        inputStream = null;
        bufferedReader = null;


        try {
            inputStream = context.openFileInput(context.getString(R.string.scores));
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
            }
        } catch (FileNotFoundException e) {




            // e.printStackTrace();
        }
    }
    public MyFileReader(final Context context, StartActivity startActivity){
        this.context = context;
        this.startActivity = startActivity;
        inputStream = null;
        bufferedReader = null;


        try {
            inputStream = context.openFileInput(context.getString(R.string.scores));
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
            }
        } catch (FileNotFoundException e) {

            startActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "There is no the best score", Toast.LENGTH_SHORT).show();
                }
            });


            // e.printStackTrace();
        }
    }

    public void writeOnScreen(){
        try {
            if(inputStream!=null) {
                String text1 = bufferedReader.readLine();
                //System.out.println(text1);
                String text2 = bufferedReader.readLine();
                // System.out.println(text2);
                Toast.makeText(context, "The Best Score: \n" + "Stage: " + text1 + "\nPercent " + text2, Toast.LENGTH_SHORT).show();

                bufferedReader.close();
                inputStream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer readNextLine(){
        try {
            if(bufferedReader!=null) {
                Integer x = Integer.parseInt(bufferedReader.readLine());
                //System.out.println("WCZYTANO: "+x);
                return x;
            }
            else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
