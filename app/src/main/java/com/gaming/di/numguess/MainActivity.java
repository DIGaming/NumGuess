package com.gaming.di.numguess;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.media.MediaPlayer;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Context context;
    MediaPlayer background_music;
    private CountDownTimer mcountDown;
    SoundPool mysound;
    int addpoint_sfx;
    int minusepoint_sfx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Typeface newfont = Typeface.createFromAsset(getAssets(), "fonts/LCD-NormalFont.TTF");
        mcountDown = new CountDownTimer(300000, 1000) {
           TextView gametime = (TextView)findViewById(R.id.gametime);

            @Override
            public void onFinish() {
                timeup(context);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                gametime.setText("Time Left: " + String.valueOf(millisUntilFinished / 1000 ));
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
        minusepoint_sfx = mysound.load(this, R.raw.minuspoint, 1);

        ImageButton AutoBtn = (ImageButton) findViewById(R.id.imageButton2);
        AutoBtn.setEnabled(true);

        TextView Point_Label = (TextView)findViewById(R.id.pointlabel);
        TextView Points = (TextView)findViewById(R.id.points);
        TextView TTLPoints_Label = (TextView)findViewById(R.id.ttl_points_lbl);
        TextView TTLPoints = (TextView)findViewById(R.id.ttlpoints);
        TextView Cur_Lev_Label = (TextView)findViewById(R.id.levellabel);
        TextView Cur_Level = (TextView)findViewById(R.id.textView3);
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
                ImageButton AutoBtn = (ImageButton) findViewById(R.id.imageButton2);
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
        boolean timerStarts = false;
        if(!timerStarts)
        {
            ImageButton AutoBtn = (ImageButton) findViewById(R.id.imageButton2);
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
            ImageButton AutoBtn = (ImageButton) findViewById(R.id.imageButton2);
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
                    AddSFX();
                    int num2 = num + 100;
                    ttlpoints += 100;
                    pntVal.setTextColor(Color.GREEN);
                    pntVal.setTextSize(18);
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
                    pntVal.setTextSize(14);
                    pntVal.setText(String.valueOf(num2));
                    ttl.setText(String.valueOf(ttlpoints));
                }
            }
        }

        public void onFinish()
        {
            TextView pntVal = (TextView) findViewById(R.id.points);
            pntVal.setTextColor(Color.LTGRAY);
            pntVal.setTextSize(16);
        }
    };
    //Build SoundPool For SoundEffects Excluding Background Music Which Is Handled Above With Media Player In The OnCreate Function

    public void AddSFX()
    {
        mysound.play(addpoint_sfx, 1, 1, 1, 0, 1);
    }

    public void MinusSFX()
    {
        mysound.play(minusepoint_sfx, 1, 1, 1, 0, 1);
    }
}
