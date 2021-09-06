package com.example.chesstimekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private long START_TIME_IN_MILLIS = 60000;        //initial time; can take this input from EditText
    private int flag = 0;                             //0 means white to play and 1 means black to play

    //creating objects of the required XML elements
    private TextView TextViewCountDown1;
    private TextView TextViewCountDown2;
    private Button ButtonStartPause;
    private Button ButtonReset;

    private CountDownTimer countDownTimer1;
    private CountDownTimer countDownTimer2;

    //variables for keeping track of the time
    private boolean timerRunning1;     //tells if timer 1 is running or not
    private boolean timerRunning2;     //tells if timer 2 is running or not

    /*
      Time left for both timers initialized with the starting time; since
      game hasn't started yet
     */
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
                   timer would be running in normal course of game.
                 */
                if (timerRunning1)         //pausing timer 1
                {
                    pauseTimer1();
                    flag = 0;              //remember that white to play after resume
                } else if (timerRunning2)  //pausing timer 2
                {
                    pauseTimer2();
                    flag = 1;              //remember that black to play after resume
                } else {
                    if (flag == 0)         //start after being paused
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

    /**
     * Pauses black's timer and starts white's timer
     * @param view the TextView showing black's time
     */
    public void start1(View view) {
        if (timerRunning2) {
            pauseTimer2();
            startTimer1();
        }
    }

    /**
     * Pauses white's timer and starts black's timer
     * @param view the TextView showing white's time
     */
    public void start2(View view) {
        if (timerRunning1) {
            pauseTimer1();
            startTimer2();
        }
    }

    /**
     * method to start the CountDownTimer for player playing white
     */
    private void startTimer1() {

        countDownTimer1 = new CountDownTimer(timeLeftInMillis1, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis1 = millisUntilFinished;
                //update the remaining time left in the textview
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                //set to false since time is up
                timerRunning1 = false;
                updateButtons();
            }
        }.start();

        //set timer 1 as running
        timerRunning1 = true;
        updateButtons();
    }

    /**
     * method to start the CountDownTimer for player playing black
     */
    private void startTimer2() {

        countDownTimer2 = new CountDownTimer(timeLeftInMillis2, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis2 = millisUntilFinished;
                //update the remaining time left in the textview
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                //set to false since time is up
                timerRunning2 = false;
                updateButtons();
            }
        }.start();

        //set timer 2 as running
        timerRunning2 = true;
        updateButtons();
    }

    /**
     * method to pause the CountDownTimer for white by using the cancel method
     */
    private void pauseTimer1() {
        countDownTimer1.cancel();
        timerRunning1 = false;
        updateButtons();
    }

    /**
     * method to pause the CountDownTimer for black by using the cancel method
     */
    private void pauseTimer2() {
        countDownTimer2.cancel();
        timerRunning2 = false;
        updateButtons();
    }

    /**
     * method to reset the time to the previously initialized time for both players
     */
    private void resetTimer() {
        flag = 0;
        timeLeftInMillis1 = timeLeftInMillis2 = START_TIME_IN_MILLIS;
        updateCountDownText();
        updateButtons();
    }

    /**
     * Method to update the time shown in the TextViews
     * The hour field is displayed only if time exceeds 60 minutes.
     */
    private void updateCountDownText() {
        int hours1 = (int) (timeLeftInMillis1 / 1000) / 3600;
        int minutes1 = (int) ((timeLeftInMillis1 / 1000) % 3600) / 60;
        int seconds1 = (int) (timeLeftInMillis1 / 1000) % 60;

        String timeLeftFormatted1;
        if (hours1 > 0) {
            timeLeftFormatted1 = String.format(Locale.getDefault(), "%d:%02d:%02d", hours1, minutes1, seconds1);
        } else {
            timeLeftFormatted1 = String.format(Locale.getDefault(), "%02d:%02d", minutes1, seconds1);
        }
        //display the time left
        TextViewCountDown1.setText(timeLeftFormatted1);

        int hours2 = (int) (timeLeftInMillis2 / 1000) / 3600;
        int minutes2 = (int) ((timeLeftInMillis2 / 1000) % 3600) / 60;
        int seconds2 = (int) (timeLeftInMillis2 / 1000) % 60;

        String timeLeftFormatted2;
        if (hours2 > 0) {
            timeLeftFormatted2 = String.format(Locale.getDefault(), "%d:%02d:%02d", hours2, minutes2, seconds2);
        } else {
            timeLeftFormatted2 = String.format(Locale.getDefault(), "%02d:%02d", minutes2, seconds2);
        }
        //display the time left
        TextViewCountDown2.setText(timeLeftFormatted2);
    }

    /**
     * method to toggle text in Start/Pause button, toggle visibility of Reset button
     * and notify who's the winner to alertWinner() method
     */
    private void updateButtons() {
        if (timerRunning1 || timerRunning2) {                     //if game going on
            //disable reset button visibility while game ensuing
            ButtonReset.setVisibility(View.INVISIBLE);
            //change button text to pause
            ButtonStartPause.setText("Pause");
        } else {                                                  //if paused or game ended
            //Start means resume if the game is paused
            ButtonStartPause.setText("Start");

            /*
               If either or both player's time(s) has started decrementing or simply put - if the game is guaranteed
               to have commenced; then show the reset button. Note that the timers are not running for both players -
               i.e it's in a paused state currently
             */
            if (timeLeftInMillis1 < START_TIME_IN_MILLIS || timeLeftInMillis2 < START_TIME_IN_MILLIS)
                ButtonReset.setVisibility(View.VISIBLE);
            else
                ButtonReset.setVisibility(View.INVISIBLE);

            /*
               If time left in milliseconds go below 1000 for any player - this means he/she has lost by time.
               Accordingly, toggle visibility of Start/Pause button and notify the winner to alertWinner() method.
             */
            if (timeLeftInMillis1 < 1000 || timeLeftInMillis2 < 1000) {
                if (timeLeftInMillis1 < 1000) {
                    ButtonStartPause.setVisibility(View.INVISIBLE);
                    alertWinner("Black");                           //Black is winner
                }
                if (timeLeftInMillis2 < 1000) {
                    ButtonStartPause.setVisibility(View.INVISIBLE);
                    alertWinner("White");                           //White is winner
                }
            } else
                ButtonStartPause.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Invoked when user manually sets time using alertManual() method
     * @param milliseconds time in milliseconds
     */
    private void setTime(long milliseconds) {
        START_TIME_IN_MILLIS = milliseconds;
        resetTimer();
        closeKeyboard();
    }

    /**
     * opens up an AlertDialog box message that notifies who the winner is (by time)
     * @param winner string that notifies whether white or black is the winner
     */
    private void alertWinner(String winner) {
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

    /**
     * Display the available standard presets (taken here as Rapid, Bullet and Blitz) in the form of
     * an AlertDialog that can be checked or unchecked. Accordingly, time for both players will be updated.
     * Works only when the game hasn't started or is in a paused state.
     *
     * @param v corresponding to the Preset button
     */
    public void showChoices(View v) {

        if (timerRunning1 || timerRunning2) {
            Toast.makeText(this, "Can't switch modes while game's going on!!", Toast.LENGTH_SHORT).show();
            return;
        }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Choose Preset")

                .setSingleChoiceItems(R.array.choices, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(MainActivity.this,"Hi",Toast.LENGTH_SHORT).show();
                    }

                })

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int selectedPosition = ((androidx.appcompat.app.AlertDialog) dialog).getListView().getCheckedItemPosition();
                        switch (selectedPosition) {
                            case 1:
                                START_TIME_IN_MILLIS = 180000;
                                resetTimer();
                                Toast.makeText(MainActivity.this, "Blitz mode enabled", Toast.LENGTH_SHORT).show();
                                return;
                            case 2:
                                START_TIME_IN_MILLIS = 600000;
                                resetTimer();
                                Toast.makeText(MainActivity.this, "Rapid mode enabled", Toast.LENGTH_SHORT).show();
                                return;
                            case 0:
                                START_TIME_IN_MILLIS = 60000;
                                resetTimer();
                                Toast.makeText(MainActivity.this, "Bullet mode enabled", Toast.LENGTH_SHORT).show();
                                return;
                            default:
                        }
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the dialog from the screen
                    }
                })

                .show();
    }

    /**
     * Opens up an AlertDialog that contains an EditText where user can enter time manually, in minutes.
     * Accordingly, time for both players will be updated.
     * Works only when the game hasn't started or is in a paused state.
     *
     * @param v corresponding to the Manual button
     */
    public void alertManual(View v) {
        if (timerRunning1 || timerRunning2) {
            Toast.makeText(this, "Can't switch modes while game's going on!!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Manual");

        View viewInflated = getLayoutInflater().inflate(R.layout.manual, null);

        final EditText EditTextManual = (EditText) viewInflated.findViewById(R.id.edit_text_manual);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String input = EditTextManual.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(MainActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(MainActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisInput);
                EditTextManual.setText("");
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Closes the keyboard after manual time entry (if any) is done; since the keyboard
     * doesn't close by itself
     */
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}

