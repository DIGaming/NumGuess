package com.gaming.di.numguess;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

import static android.R.id.text1;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public int RandomNum(int currentlevel)
    {
        TextView info = (TextView)findViewById(R.id.textView2);
        Random newran = new Random();
        //Modified The Range Tick Per Level To +2 Per Level Instead of + 10
        double maxd = (((currentlevel * 0.2) * 10) + 10);
        int max = (int) maxd;
        info.setText("Guess A Number Between 1 - " + String.valueOf(max) + "!");
        int min = 1;
        int rannum = newran.nextInt(max - min) + 1;
        return rannum;
    }

    public void ButtonGuess(View view)
    {

        TextView level = (TextView) findViewById(R.id.textView3);
        TextView tv1 = (TextView) findViewById(R.id.textView);
        TextView pntVal = (TextView) findViewById(R.id.points);
        EditText guess = (EditText) findViewById(R.id.editText2);
        int currentexp = Integer.valueOf(pntVal.getText().toString());
        int currentlevel = Integer.valueOf(level.getText().toString());
        int ExpValue = ((currentlevel * 500) * 2);
        String cache = String.valueOf(RandomNum(currentlevel));

        if (String.valueOf(guess.getText()).equals(cache))
        {
            if(currentexp == ExpValue)
            {
                currentlevel += 1;
                level.setText(String.valueOf(currentlevel));
                pntVal.setText("0");
            }
            else {
                tv1.setText("Your Value Of " + guess.getText().toString() + " Is Correct");
                tv1.setTextColor(Color.rgb(0,128,0));
                int num = Integer.valueOf(pntVal.getText().toString());
                int num2 = num + 100;
                pntVal.setText(String.valueOf(num2));
            }
        }
        else
        {
            tv1.setText("Your Guess Is Incorrect The Value Is " + cache.toString());
            tv1.setTextColor(Color.rgb(128,0,0));
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
