package com.gaming.di.numguess;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.ImageButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Context context;
    MediaPlayer background_music;
    private CountDownTimer mcountDown;
    SoundPool mysound;
    int addpoint_sfx;
    int minuspoint_sfx;
    boolean isPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Typeface newfont = Typeface.createFromAsset(getAssets(), "fonts/LCD-NormalFont.TTF");
        mcountDown = new CountDownTimer(150000, 1000) {
            TextView gametime = (TextView) findViewById(R.id.GameTimeInt);

            @Override
            public void onFinish() {

                background_music.stop();
                mysound.stop(addpoint_sfx);
                mysound.stop(minuspoint_sfx);
                timer.cancel();
                timeup(context);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                gametime.setText(String.valueOf(millisUntilFinished / 1000));
                gametime.setTypeface(newfont);
            }
        }.start();

        //Background Music Specs and Looping Before Starting.
        background_music = MediaPlayer.create(MainActivity.this, R.raw.backgroundmusic);
        background_music.setLooping(true);
        background_music.start();

        //Create Sound Pool Manager For Sound Effects
        mysound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        addpoint_sfx = mysound.load(this, R.raw.addpoint, 1);
        minuspoint_sfx = mysound.load(this, R.raw.minuspoint, 1);

        ImageButton AutoBtn = (ImageButton) findViewById(R.id.RedButton);
        AutoBtn.setEnabled(true);

        TextView Point_Label = (TextView) findViewById(R.id.PointsLabel);
        TextView Points = (TextView) findViewById(R.id.points);
        TextView TTLPoints_Label = (TextView) findViewById(R.id.TotalPointsLabel);
        TextView TTLPoints = (TextView) findViewById(R.id.ttlPoints);
        TextView Cur_Lev_Label = (TextView) findViewById(R.id.LevelLabel);
        TextView Cur_Level = (TextView) findViewById(R.id.IntLevel);
        Point_Label.setTypeface(newfont);
        Points.setTypeface(newfont);
        TTLPoints_Label.setTypeface(newfont);
        TTLPoints.setTypeface(newfont);
        Cur_Lev_Label.setTypeface(newfont);
        Cur_Level.setTypeface(newfont);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        background_music.release();
        finish();
    }

    public void timeup(Context context)
    {
        TextView points = (TextView) findViewById(R.id.ttlPoints);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setTitle("Times up!")
                .setMessage("Game over. Play Again?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();
    }
    //Random Number Generator
    public int RandomNum(int currentlevel)
    {
        TextView info = (TextView)findViewById(R.id.GameInfo);
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
        ImageButton BlueButton = (ImageButton)findViewById(R.id.BlueButton);
        TextView level = (TextView) findViewById(R.id.IntLevel);
        TextView tv1 = (TextView) findViewById(R.id.GuessStatus);
        TextView pntVal = (TextView) findViewById(R.id.points);
        EditText guess = (EditText) findViewById(R.id.GuessField);
        TextView ttl = (TextView) findViewById(R.id.ttlPoints);
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
                ImageButton AutoBtn = (ImageButton) findViewById(R.id.RedButton);
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
                AddSFX();
                pntVal.setText(String.valueOf(num2));
                ttl.setText(String.valueOf(ttlpoints));
            }
        }
        else if (String.valueOf(guess.getText()).equals(""))
        {
            tv1.setTextColor(Color.RED);
            tv1.setText(R.string.emptyguess);
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
                MinusSFX();
                pntVal.setText(String.valueOf(num2));
                ttl.setText(String.valueOf(ttlpoints));
            }

        }
    }
    // Quick Guess Button Action (aka Auto Guess 10 Guesses Per Second For 20 Seconds)
    public void AutoGuess(View view)
    {
        TextView tv1 = (TextView) findViewById(R.id.GuessStatus);
        EditText guess = (EditText) findViewById(R.id.GuessField);
        boolean timerStarts = false;
        if(!timerStarts)
        {
            ImageButton AutoBtn = (ImageButton) findViewById(R.id.RedButton);
            //Comment Below Line Out For Quicker Testing / Fast Mode.
            if (String.valueOf(guess.getText()).equals(""))
            {
                tv1.setTextColor(Color.RED);
                tv1.setText(R.string.emptyguess);
            }
            else
            {
                AutoBtn.setEnabled(false);
                timer.start();
                timerStarts = true;
            }
        }
    }
    //Timer Is Used For Auto Guess Function Which Is Available After Every Manual Level Up
    // First Integer Is How Long 20 Seconds, Second Integer Determines Tick Rate aka 10 a second
    CountDownTimer timer = new CountDownTimer(20000, 100)
    {
        public void onTick (long millisUntilFinished)
        {
            TextView ttl = (TextView) findViewById(R.id.ttlPoints);
            int ttlpoints = Integer.valueOf(ttl.getText().toString());
            ImageButton AutoBtn = (ImageButton) findViewById(R.id.RedButton);
            TextView level = (TextView) findViewById(R.id.IntLevel);
            TextView tv1 = (TextView) findViewById(R.id.GuessStatus);
            TextView pntVal = (TextView) findViewById(R.id.points);
            EditText guess = (EditText) findViewById(R.id.GuessField);
            int currentexp = Integer.valueOf(pntVal.getText().toString());
            int currentlevel = Integer.valueOf(level.getText().toString());
            int ExpValue = ((currentlevel * 500) * 2);
            String cache = String.valueOf(RandomNum(currentlevel));
            String resolved_guess = String.valueOf(guess.getText());

            if (resolved_guess.equals(cache)) {
                if (currentexp > ExpValue) {
                    currentlevel += 1;
                    AutoBtn.setEnabled(true);
                    level.setText(String.valueOf(currentlevel));
                    pntVal.setText("0");
                } else {
                    tv1.setText("Your Value Of " + guess.getText().toString() + " Is Correct");
                    tv1.setTextColor(Color.rgb(0, 200, 0));
                    int num = Integer.valueOf(pntVal.getText().toString());
                    AddSFX();
                    int num2 = num + 100;
                    ttlpoints += 100;
                    pntVal.setTextColor(Color.GREEN);
                    pntVal.setText(String.valueOf(num2));
                    ttl.setText(String.valueOf(ttlpoints));
                }
            } else {
                tv1.setText("Your Guess Is Incorrect The Value Is " + cache);
                tv1.setTextColor(Color.rgb(250, 0, 0));
                int num = Integer.valueOf(pntVal.getText().toString());
                if (num != 0)
                {
                    MinusSFX();
                    int num2 = num - 1;
                    ttlpoints -= 1;
                    pntVal.setTextColor(Color.RED);
                    pntVal.setText(String.valueOf(num2));
                    ttl.setText(String.valueOf(ttlpoints));
                }
            }
        }

        public void onFinish()
        {
            TextView pntVal = (TextView) findViewById(R.id.points);
            pntVal.setTextColor(Color.LTGRAY);
        }
    };
    //Build SoundPool For SoundEffects Excluding Background Music Which Is Handled Above With Media Player In The OnCreate Function

    public void AddSFX()
    {
        mysound.play(addpoint_sfx, 1, 1, 1, 0, 1);
    }

    public void MinusSFX()
    {
        mysound.play(minuspoint_sfx, 1, 1, 1, 0, 1);
    }
}
