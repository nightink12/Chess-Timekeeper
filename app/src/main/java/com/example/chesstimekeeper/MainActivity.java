package com.example.chesstimekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static long START_TIME_IN_MILLIS = 60000; //can take this input from edittext
    private int flag = 0; //0 means white to play and 1 means black to play

    private TextView TextViewCountDown1;
    private TextView TextViewCountDown2;
    private Button ButtonStartPause;
    private Button ButtonReset;

    private CountDownTimer countDownTimer1;
    private CountDownTimer countDownTimer2;

    private boolean timerRunning1;
    private boolean timerRunning2;
    private long timeLeftInMillis1 = START_TIME_IN_MILLIS;
    private long timeLeftInMillis2 = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextViewCountDown1 = findViewById(R.id.countdown_text_view1);
        TextViewCountDown2 = findViewById(R.id.countdown_text_view2);
        ButtonStartPause = findViewById(R.id.button_start_pause);
        ButtonReset = findViewById(R.id.button_reset);

        ButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                   breaking into individual if else since pausing an already paused timer
                   made the app crash. Also intuitive; since at any instant, only one
                   timer would be running
                 */
                if (timerRunning1)  //pausing
                {
                    pauseTimer1();
                    flag = 0; //remember that white to play after resume
                } else if (timerRunning2)  //pausing
                {
                    pauseTimer2();
                    flag = 1; //remember that black to lay after resume
                } else {
                    if (flag == 0)   //start after being paused
                        startTimer1();
                    else
                        startTimer2();
                }
            }
        });

        ButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        updateCountDownText();
    }

    public void start1(View view) {
        if (timerRunning2) {
            pauseTimer2();
            startTimer1();
        }
    }

    public void start2(View view) {
        if (timerRunning1) {
            pauseTimer1();
            startTimer2();
        }
    }

    private void startTimer1() {
        //endTime = System.currentTimeMillis() + timeLeftInMillis1;

        countDownTimer1 = new CountDownTimer(timeLeftInMillis1, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis1 = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning1 = false;
                updateButtons();
            }
        }.start();

        timerRunning1 = true;
        updateButtons();
    }

    private void startTimer2() {
        //endTime = System.currentTimeMillis() + timeLeftInMillis1;

        countDownTimer2 = new CountDownTimer(timeLeftInMillis2, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis2 = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning2 = false;
                updateButtons();
            }
        }.start();

        timerRunning2 = true;
        updateButtons();
    }

    private void pauseTimer1() {
        countDownTimer1.cancel();
        timerRunning1 = false;
        updateButtons();
    }

    private void pauseTimer2() {
        countDownTimer2.cancel();
        timerRunning2 = false;
        updateButtons();
    }

    private void resetTimer() {
        flag = 0;
        timeLeftInMillis1 = timeLeftInMillis2 = START_TIME_IN_MILLIS;
        updateCountDownText();
        updateButtons();
    }

    private void updateCountDownText() {
        int minutes1 = (int) (timeLeftInMillis1 / 1000) / 60;
        int seconds1 = (int) (timeLeftInMillis1 / 1000) % 60;

        String timeLeftFormatted1 = String.format(Locale.getDefault(), "%02d:%02d", minutes1, seconds1);
        TextViewCountDown1.setText(timeLeftFormatted1);

        int minutes2 = (int) (timeLeftInMillis2 / 1000) / 60;
        int seconds2 = (int) (timeLeftInMillis2 / 1000) % 60;

        String timeLeftFormatted2 = String.format(Locale.getDefault(), "%02d:%02d", minutes2, seconds2);
        TextViewCountDown2.setText(timeLeftFormatted2);
    }

    private void updateButtons() {
        if (timerRunning1 || timerRunning2) {
            ButtonReset.setVisibility(View.INVISIBLE);
            ButtonStartPause.setText("Pause");
        } else {//if paused
            ButtonStartPause.setText("Start");

            if (timeLeftInMillis1 < START_TIME_IN_MILLIS || timeLeftInMillis2 < START_TIME_IN_MILLIS)
                ButtonReset.setVisibility(View.VISIBLE);
            else
                ButtonReset.setVisibility(View.INVISIBLE);

            if (timeLeftInMillis1 < 1000 || timeLeftInMillis2 < 1000) {
                if (timeLeftInMillis1 < 1000) {
                    ButtonStartPause.setVisibility(View.INVISIBLE);
                    alert("Black");
                }
                if (timeLeftInMillis2 < 1000) {
                    ButtonStartPause.setVisibility(View.INVISIBLE);
                    alert("White");
                }
            } else
                ButtonStartPause.setVisibility(View.VISIBLE);
        }
    }

    private void alert(String winner) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("Game Ended");
        dlg.setMessage(winner + " has won by time");
        dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog a = dlg.create();
        a.show();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menugroup);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (timerRunning1 || timerRunning2) {
            Toast.makeText(this, "Can't switch modes while game's going on!!", Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.menu_blitz:
                START_TIME_IN_MILLIS = 180000;
                resetTimer();
                Toast.makeText(this, "Blitz mode enabled", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_rapid:
                START_TIME_IN_MILLIS = 600000;
                resetTimer();
                Toast.makeText(this, "Rapid mode enabled", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_bullet:
                START_TIME_IN_MILLIS = 60000;
                resetTimer();
                Toast.makeText(this, "Bullet mode enabled", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_blitz:
            case R.id.menu_rapid:
            case R.id.menu_bullet:
                if (item.isChecked()) item.setChecked(true);
                else item.setChecked(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */
}

