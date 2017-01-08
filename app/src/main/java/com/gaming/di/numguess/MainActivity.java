package com.gaming.di.numguess;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import com.google.android.gms.games.Games;

import org.w3c.dom.Text;

import java.util.Random;

import static android.R.id.text1;

public class MainActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mcountDown = new CountDownTimer(300000, 1000) {
           TextView gametime = (TextView)findViewById(R.id.gametime);
            @Override
            public void onFinish() {
                timeup(context);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                gametime.setText("Time Left: " + String.valueOf(millisUntilFinished / 1000 ));
            }
        }.start();

    }
    public void timeup(Context context)
    {
        TextView points = (TextView) findViewById(R.id.ttlpoints);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setTitle("Times up!")
                .setMessage("Game over. Play Again?")
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.recreate();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private CountDownTimer mcountDown;


    //Random Number Generator
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
    // Single Guess Button Action
    public void ButtonGuess(View view)
    {
        TextView level = (TextView) findViewById(R.id.textView3);
        TextView tv1 = (TextView) findViewById(R.id.textView);
        TextView pntVal = (TextView) findViewById(R.id.points);
        EditText guess = (EditText) findViewById(R.id.editText2);
        TextView ttl = (TextView) findViewById(R.id.ttlpoints);
        int currentexp = Integer.valueOf(pntVal.getText().toString());
        int currentlevel = Integer.valueOf(level.getText().toString());
        int ExpValue = ((currentlevel * 500) * 2);
        int ttlpoints = Integer.valueOf(ttl.getText().toString());
        String cache = String.valueOf(RandomNum(currentlevel));

        if (String.valueOf(guess.getText()).equals(cache))
        {
            if(currentexp > ExpValue)
            {
                currentlevel += 1;
                Button AutoBtn = (Button) findViewById(R.id.button2);
                AutoBtn.setEnabled(true);
                level.setText(String.valueOf(currentlevel));
                pntVal.setText("0");
            }
            else {
                ttl.setText(String.valueOf(ttlpoints));
                tv1.setText("Your Value Of " + guess.getText().toString() + " Is Correct");
                tv1.setTextColor(Color.rgb(0,200,0));
                int num = Integer.valueOf(pntVal.getText().toString());
                int num2 = num + 100;
                ttlpoints += 100;
                pntVal.setText(String.valueOf(num2));
                ttl.setText(String.valueOf(ttlpoints));
            }
        }
        else
        {
            tv1.setText("Your Guess Is Incorrect The Value Is " + cache.toString());
            tv1.setTextColor(Color.rgb(250,0,0));
            int num = Integer.valueOf(pntVal.getText().toString());
            if (num != 0)
            {
                ttlpoints -= 1;
                int num2 = num - 1;
                pntVal.setText(String.valueOf(num2));
                ttl.setText(String.valueOf(ttlpoints));
            }

        }
    }
    // Quick Guess Button Action (aka Auto Guess 10 Guesses Per Second For 20 Seconds)
    public void AutoGuess(View view)
    {
        boolean timerStarts = false;
        if(!timerStarts)
        {
            Button AutoBtn = (Button) findViewById(R.id.button2);
            //Comment Below Line Out For Quicker Testing / Fast Mode.
            AutoBtn.setEnabled(false);
            timer.start();
            timerStarts = true;
        }
    }

    //Timer Is Used For Auto Guess Function Which Is Available After Every Manual Level Up
    // First Integer Is How Long 20 Seconds, Second Integer Determines Tick Rate aka 10 a second
    CountDownTimer timer = new CountDownTimer(20000, 100)
    {
        public void onTick (long millisUntilFinished)
        {
            TextView ttl = (TextView) findViewById(R.id.ttlpoints);
            int ttlpoints = Integer.valueOf(ttl.getText().toString());
            Button AutoBtn = (Button) findViewById(R.id.button2);
            TextView level = (TextView) findViewById(R.id.textView3);
            TextView tv1 = (TextView) findViewById(R.id.textView);
            TextView pntVal = (TextView) findViewById(R.id.points);
            EditText guess = (EditText) findViewById(R.id.editText2);
            int currentexp = Integer.valueOf(pntVal.getText().toString());
            int currentlevel = Integer.valueOf(level.getText().toString());
            int ExpValue = ((currentlevel * 500) * 2);
            String cache = String.valueOf(RandomNum(currentlevel));

            if (String.valueOf(guess.getText()).equals(cache)) {
                if (currentexp > ExpValue) {
                    currentlevel += 1;
                    AutoBtn.setEnabled(true);
                    level.setText(String.valueOf(currentlevel));
                    pntVal.setText("0");
                } else {
                    tv1.setText("Your Value Of " + guess.getText().toString() + " Is Correct");
                    tv1.setTextColor(Color.rgb(0, 200, 0));
                    int num = Integer.valueOf(pntVal.getText().toString());
                    int num2 = num + 100;
                    ttlpoints += 100;
                    pntVal.setText(String.valueOf(num2));
                    ttl.setText(String.valueOf(ttlpoints));
                }
            } else {
                tv1.setText("Your Guess Is Incorrect The Value Is " + cache.toString());
                tv1.setTextColor(Color.rgb(250, 0, 0));
                int num = Integer.valueOf(pntVal.getText().toString());
                if (num != 0)
                {
                    int num2 = num - 1;
                    ttlpoints -= 1;
                    pntVal.setText(String.valueOf(num2));
                    ttl.setText(String.valueOf(ttlpoints));
                }
            }
        }

        public void onFinish()
        {

        }
    };

    // Game Timer Set To 5 Minuets

}
