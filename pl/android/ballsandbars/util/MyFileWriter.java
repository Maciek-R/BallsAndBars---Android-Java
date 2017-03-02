package pl.android.ballsandbars.util;

import android.content.Context;

//import maciek.ballsandbars.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import pl.android.ballsandbars.R;

/**
 * Created by Maciek on 2016-10-04.
 */
public class MyFileWriter {

    Context context;
    OutputStreamWriter outputStreamWriter;

    public MyFileWriter(Context context){
        try {
            this.context = context;
            outputStreamWriter = new OutputStreamWriter(context.openFileOutput(context.getString(R.string.scores), Context.MODE_PRIVATE));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String text){
        try {
            outputStreamWriter.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close(){
        try {
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
